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

package de.hasait.sprinkler.service.sensor.provider.hww;

import com.google.common.net.UrlEscapers;
import com.google.gson.Gson;
import de.hasait.sprinkler.service.sensor.provider.SensorProvider;
import de.hasait.sprinkler.service.sensor.provider.SensorValue;
import de.hasait.common.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Rain Service of Hamburger Wasserwerke GmbH:
 * <a href="https://gis.hamburgwasser.de/arcgis/sdk/rest/index.html#/Query_Map_Service_Layer/02ss0000000r000000">ArcGIS REST API / Map Service / Query (Map Service Layer)</a></p>
 */
@Service
public class HwwRainProvider implements SensorProvider {

    public static final int ROUND_TO_MINUTES = 5;

    private static final Logger LOG = LoggerFactory.getLogger(HwwRainProvider.class);

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

    private final String disabledReason;

    public HwwRainProvider() {
        this.disabledReason = null;
    }

    @Nonnull
    @Override
    public String getId() {
        return "hww-gis";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Rain Service of Hamburger Wasserwerke GmbH";
    }

    @Nullable
    @Override
    public String getDisabledReason() {
        return disabledReason;
    }

    @Nullable
    @Override
    public String validateConfig(@Nonnull String config) {
        try {
            parseConfig(config);
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        return null;
    }

    private HwwConfiguration parseConfig(@Nonnull String config) {
        String[] split = config.split(";");
        if (split.length != 4) {
            throw new IllegalArgumentException("Expected: <int sriLayer>;<int spatialReference>;<float positionX>;<float positionY>");
        }

        HwwConfiguration configuration = new HwwConfiguration();
        int i = 0;
        Util.parse(split[i++], "int", Integer::parseInt, "sriLayer", configuration::setSriLayer);
        Util.parse(split[i++], "int", Integer::parseInt, "spatialReference", configuration::setSpatialReference);
        Util.parse(split[i++], "float", Float::parseFloat, "positionX", configuration::setPositionX);
        Util.parse(split[i++], "float", Float::parseFloat, "positionY", configuration::setPositionY);
        return configuration;
    }

    @Override
    public SensorValue obtainValue(@Nonnull String config) {
        HwwConfiguration configuration = parseConfig(config);
        String baseUrlString = "https://gis.hamburgwasser.de/sri/rest/services/SRI_Labels/MapServer/" + configuration.getSriLayer() + "/query";

        LocalDateTime dateTimeNow = LocalDateTime.now();
        LocalDateTime dateTimeRounded = dateTimeNow //
                .truncatedTo(ChronoUnit.HOURS)
                .plusMinutes(ROUND_TO_MINUTES * (dateTimeNow.getMinute() / ROUND_TO_MINUTES)) //
                .minusMinutes(2 * ROUND_TO_MINUTES) //
                ;
        Map<String, String> queryParameters = new LinkedHashMap<>();
        queryParameters.put("where", "ende > timestamp '" + formatter.format(dateTimeRounded) + "'");
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
            throw new RuntimeException("Invalid URL: " + urlString, e);
        }

        LOG.debug(urlString);
        String resultJsonString;
        try {
            resultJsonString = IOUtils.toString(url, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read from URL: " + url, e);
        }

        RainResult result;
        try {
            Gson gson = new Gson();
            result = gson.fromJson(resultJsonString, RainResult.class);
        } catch (RuntimeException e) {
            throw new RuntimeException("Cannot parse JSON:\n" + resultJsonString + "\n", e);
        }

        if (!result.features.isEmpty()) {
            long maxEnde = result.features.stream().map(RainFeature::getAttributes).map(RainFeatureAttributes::getEnde)
                    .reduce(Long::max).get();
            List<Integer> regenhoehen = result.features.stream().map(RainFeature::getAttributes).filter(a -> a.getEnde() == maxEnde)
                    .map(RainFeatureAttributes::getRegenhoehe).collect(Collectors.toList());
            int regenhoeheAverage = regenhoehen.stream().reduce(Integer::sum).get() / regenhoehen.size();
            return createValue(maxEnde, regenhoeheAverage);
        } else {
            return createValue(System.currentTimeMillis(), 0);
        }
    }

    private SensorValue createValue(long ende, int regenhoehe) {
        return new SensorValue(LocalDateTime.ofEpochSecond(ende / 1000, 0, ZoneOffset.UTC), regenhoehe);
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
