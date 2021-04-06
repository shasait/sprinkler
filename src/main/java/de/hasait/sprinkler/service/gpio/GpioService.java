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

package de.hasait.sprinkler.service.gpio;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.hasait.sprinkler.service.base.AbstractListenableService;
import de.hasait.sprinkler.service.gpio.mock.MockGpioProvider;

/**
 *
 */
@Service
public class GpioService extends AbstractListenableService {

    private static final Logger LOG = LoggerFactory.getLogger(GpioService.class);

    private final Map<String, GpioProvider> providersById = new TreeMap<>();

    public GpioService(GpioProvider[] providers) {
        super();

        for (GpioProvider provider : providers) {
            String providerId = provider.getProviderId();
            GpioProvider oldProvider = providersById.put(providerId, provider);
            if (oldProvider != null) {
                throw new RuntimeException("Duplicate providerId: " + providerId);
            }
        }
    }

    public boolean isActive(String providerId, int address) {
        return getGpioProvider(providerId).isActive(address);
    }

    public void setActive(String providerId, int address, boolean active) {
        getGpioProvider(providerId).setActive(address, active);
        notifyListeners();
    }

    @Nonnull
    private GpioProvider getGpioProvider(String providerId) {
        GpioProvider gpioProvider = providersById.get(providerId);
        if (gpioProvider == null) {
            LOG.warn("Unknown provider: {}, using mock.", providerId);
        }
        return gpioProvider != null ? gpioProvider : providersById.get(MockGpioProvider.PROVIDER_ID);
    }

}
