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

package de.hasait.sprinkler.service.relay.provider;

import de.hasait.sprinkler.service.AbstractProviderService;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class RelayProviderService extends AbstractProviderService<RelayProvider> {

    public RelayProviderService(RelayProvider[] providers) {
        super(providers);
    }

    public boolean isActive(String providerId, String providerConfig) {
        RelayProvider provider = getProviderByIdNotNull(providerId);
        return provider.isActive(providerConfig);
    }

    public void changeActive(String providerId, String providerConfig, int amount) {
        RelayProvider provider = getProviderByIdNotNull(providerId);
        provider.changeActive(providerConfig, amount);
    }

}
