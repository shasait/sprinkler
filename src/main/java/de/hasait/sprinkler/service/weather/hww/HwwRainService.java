/*
 * Copyright (C) 2021 by Sebastian Hasait (sebastian at hasait dot de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.hasait.sprinkler.service.weather.hww;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.google.common.collect.ImmutableList;
import com.google.common.net.UrlEscapers;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;

import de.hasait.sprinkler.service.weather.RainService;
import de.hasait.sprinkler.service.weather.RainValue;

/**
 * <p>Rain Service of Hamburger Wasserwerke GmbH:
 * <a href="https://gis.hamburgwasser.de/arcgis/sdk/rest/index.html#/Query_Map_Service_Layer/02ss0000000r000000">ArcGIS REST API / Map Service / Query (Map Service Layer)</a></p>
 */
public class HwwRainService implements RainService {

    public static final int ROUND_TO_MINUTES = 5;

    private static final Logger LOG = LoggerFactory.getLogger(HwwRainService.class);

    public static String invalidReason(HwwConfiguration configuration) {
        List<String> reasons = new ArrayList<>();
        if (configuration.getSriLayer() == null) {
            reasons.add("sriLayer is null");
        }
        if (configuration.getSpatialReference() == null) {
            reasons.add("spatialReference is null");
        }
        if (configuration.getPositionX() == null) {
            reasons.add("positionX is null");
        }
        if (configuration.getPositionY() == null) {
            reasons.add("positionY is null");
        }

        return reasons.isEmpty() ? null : StringUtils.join(reasons, ", ");
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final AtomicReference<List<RainValue>> resultHolder = new AtomicReference<>(new ArrayList<>());

    private final HwwConfiguration configuration;

    private final TaskScheduler taskScheduler;

    private ScheduledFuture<?> schedule;

    public HwwRainService(HwwConfiguration configuration, TaskScheduler taskScheduler) {
        this.configuration = configuration;
        this.taskScheduler = taskScheduler;
    }

    @Override
    public RainValue getLastRainValue() {
        List<RainValue> rainValues = resultHolder.get();
        return rainValues.isEmpty() ? null : rainValues.get(0);
    }

    @Override
    public String getProviderId() {
        return "hww";
    }

    @Override
    public boolean isRaining() {
        Iterator<RainValue> rainValuesI = resultHolder.get().iterator();
        if (!rainValuesI.hasNext()) {
            return false;
        }
        RainValue rv0 = rainValuesI.next();
        if (!rainValuesI.hasNext()) {
            return false;
        }
        RainValue rv1 = rainValuesI.next();
        return rv0.getRain() > rv1.getRain();
    }

    private List<RainValue> addRainValue(List<RainValue> oldValues, RainValue value) {
        int expectedSize = Math.min(10, oldValues.size() + 1);
        return ImmutableList.<RainValue>builderWithExpectedSize(expectedSize) //
                            .add(value) //
                            .addAll(oldValues.subList(0, expectedSize - 1)) //
                            .build();
    }

    private void handleResult(long ende, int regenhoehe) {
        RainValue value = new RainValue(LocalDateTime.ofEpochSecond(ende / 1000, 0, ZoneOffset.UTC), regenhoehe);
        List<RainValue> rainValues = resultHolder.updateAndGet(current -> addRainValue(current, value));
        LOG.info("Result: {}", rainValues);
    }

    @PostConstruct
    private void init() {
        schedule = taskScheduler.scheduleWithFixedDelay(this::update, TimeUnit.MINUTES.toMillis(10));
        LOG.info("Started HWW updater");
    }

    @PreDestroy
    private void shutdown() {
        if (schedule != null) {
            LOG.info("Stopping HWW updater...", new RuntimeException("Not thrown"));
            schedule.cancel(false);
            LOG.info("Stopped HWW updater");
        }
    }

    private void update() {
        try {
            String baseUrlString = "https://gis.hamburgwasser.de/sri/rest/services/SRI_Labels/MapServer/"
                    + configuration.getSriLayer()
                    + "/query";

            LocalDateTime dateTimeNow = LocalDateTime.now();
            LocalDateTime dateTimeRounded = dateTimeNow //
                                                        .truncatedTo(ChronoUnit.HOURS)
                                                        .plusMinutes(ROUND_TO_MINUTES * (dateTimeNow.getMinute() / ROUND_TO_MINUTES)) //
                                                        .minusMinutes(2 * ROUND_TO_MINUTES) //
                    ;
            Map<String, String> queryParameters = new LinkedHashMap<>();
            queryParameters.put("where", "ende > '" + formatter.format(dateTimeRounded) + "'");
            queryParameters.put("geometry", configuration.getPositionX() + "," + configuration.getPositionY());
            queryParameters.put("geometryType", "esriGeometryPoint");
            queryParameters.put("inSR", Integer.toString(configuration.getSpatialReference()));
            queryParameters.put("spatialRel", "esriSpatialRelIntersects");
            queryParameters.put("orderByFields", "ende desc");
            queryParameters.put("outFields", "*");
            queryParameters.put("returnGeometry", "true");
            queryParameters.put("f", "json");

            StringBuilder urlStringBuilder = new StringBuilder(baseUrlString);

            boolean[] first = new boolean[]{true};
            queryParameters.forEach((name, value) -> {
                if (first[0]) {
                    first[0] = false;
                    urlStringBuilder.append('?');
                } else {
                    urlStringBuilder.append('&');
                }

                urlStringBuilder.append(name).append('=').append(UrlEscapers.urlFormParameterEscaper().escape(value));
            });

            String urlString = urlStringBuilder.toString();
            URL url;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                LOG.error("Invalid URL", e);
                return;
            }
            LOG.debug(urlString);
            String resultJsonString;
            try {
                resultJsonString = IOUtils.toString(url, StandardCharsets.UTF_8);
            } catch (IOException e) {
                LOG.error("Cannot read from URL: {}", url, e);
                return;
            }

            RainResult result;
            try {
                Gson gson = new Gson();
                result = gson.fromJson(resultJsonString, RainResult.class);
            } catch (RuntimeException e) {
                LOG.error("Cannot parse JSON:\n{}\n", resultJsonString, e);
                return;
            }

            if (!result.features.isEmpty()) {
                long maxEnde = result.features.stream().map(RainFeature::getAttributes).map(RainFeatureAttributes::getEnde)
                                              .reduce(Long::max).get();
                List<Integer> regenhoehen = result.features.stream().map(RainFeature::getAttributes).filter(a -> a.getEnde() == maxEnde)
                                                           .map(RainFeatureAttributes::getRegenhoehe).collect(Collectors.toList());
                int regenhoeheAverage = regenhoehen.stream().reduce(Integer::sum).get() / regenhoehen.size();
                handleResult(maxEnde, regenhoeheAverage);
            } else {
                handleResult(System.currentTimeMillis(), 0);
            }
        } catch (RuntimeException e) {
            LOG.error("Failed", e);
        }
    }

    private static class RainFeature {

        public RainFeatureAttributes attributes;

        public RainFeatureAttributes getAttributes() {
            return attributes;
        }

    }

    private static class RainFeatureAttributes {

        public String ID;
        public long ende;
        public int regenhoehe;

        public long getEnde() {
            return ende;
        }

        public String getID() {
            return ID;
        }

        public int getRegenhoehe() {
            return regenhoehe;
        }

    }

    private static class RainResult {

        public List<RainFeature> features = new ArrayList<>();

        public List<RainFeature> getFeatures() {
            return features;
        }

    }

}
