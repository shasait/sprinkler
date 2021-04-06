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

package de.hasait.sprinkler.service.relay.gpio;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.springframework.stereotype.Service;

import de.hasait.sprinkler.service.base.AssertUtil;
import de.hasait.sprinkler.service.base.MapDbService;
import de.hasait.sprinkler.service.gpio.GpioService;
import de.hasait.sprinkler.service.relay.RelayDTO;
import de.hasait.sprinkler.service.relay.RelayProvider;

/**
 *
 */
@Service
public class GpioRelayProvider implements RelayProvider {

    private static final String PROVIDER_ID = "GPIO";

    private final MapDbService mapDbService;
    private final GpioService gpioService;
    private final HTreeMap<String, String> relayNames;
    private final Map<String, String> gpioProviders = new TreeMap<>();
    private final Map<String, Integer> gpioAddresses = new TreeMap<>();

    public GpioRelayProvider(GpioRelayConfiguration configuration, MapDbService mapDbService, GpioService gpioService) {
        super();

        this.mapDbService = mapDbService;
        this.gpioService = gpioService;

        this.relayNames = this.mapDbService.getDb().hashMap("RP_" + PROVIDER_ID + "-Names", Serializer.STRING, Serializer.STRING)
                                           .createOrOpen();

        configuration.getRelayToGpios().forEach(relayToGpio -> {
            String relayId = relayToGpio.getRelayId();
            gpioProviders.put(relayId, relayToGpio.getGpioProvider());
            gpioAddresses.put(relayId, relayToGpio.getGpioAddress());
            relayNames.putIfAbsent(relayId, PROVIDER_ID + "-" + relayId);
            createRelay(relayId);
        });
    }

    @Nonnull
    @Override
    public String getProviderId() {
        return PROVIDER_ID;
    }

    @Nullable
    @Override
    public RelayDTO getRelay(@Nonnull String relayId) {
        return gpioAddresses.containsKey(relayId) ? createRelay(relayId) : null;
    }

    @Override
    public List<RelayDTO> getRelays() {
        return gpioAddresses.keySet().stream().map(this::createRelay).collect(Collectors.toList());
    }

    @Override
    public void setActive(@Nonnull String relayId, boolean active) {
        AssertUtil.notNull(relayId);

        String gpioProvider = gpioProviders.get(relayId);
        int gpioAddress = gpioAddresses.get(relayId);
        gpioService.setActive(gpioProvider, gpioAddress, active);
    }

    @Override
    public void updateRelay(@Nonnull RelayDTO relay) {
        AssertUtil.notNull(relay);
        AssertUtil.equals(PROVIDER_ID, relay.getProviderId());

        relayNames.put(relay.getRelayId(), relay.getName());
        mapDbService.commit();
    }

    @NotNull
    private RelayDTO createRelay(@Nonnull String relayId) {
        String gpioProvider = gpioProviders.get(relayId);
        int gpioAddress = gpioAddresses.get(relayId);
        boolean active = gpioService.isActive(gpioProvider, gpioAddress);
        String name = relayNames.get(relayId);
        return new RelayDTO(PROVIDER_ID, relayId, active).withName(name);
    }

}
