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

package de.hasait.sprinkler.service.sensor.provider;

import de.hasait.common.service.AbstractProviderService;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class SensorProviderService extends AbstractProviderService<SensorProvider> {

    public SensorProviderService(SensorProvider[] providers) {
        super(providers);
    }

    public SensorValue obtainValue(String providerId, String providerConfig) {
        SensorProvider provider = getProviderByIdNotNull(providerId);
        return provider.obtainValue(providerConfig);
    }

}
