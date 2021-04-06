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

package de.hasait.sprinkler.service.relay;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Service;

import de.hasait.sprinkler.service.base.AbstractListenableService;

/**
 *
 */
@Service
public class RelayService extends AbstractListenableService {

    private final Map<String, RelayProvider> providersById = new TreeMap<>();

    public RelayService(RelayProvider[] providers) {
        super();

        for (RelayProvider provider : providers) {
            String providerId = provider.getProviderId();
            RelayProvider oldProvider = providersById.put(providerId, provider);
            if (oldProvider != null) {
                throw new RuntimeException("Duplicate providerId: " + providerId);
            }
        }
    }

    public RelayDTO getRelay(String providerId, String relayId) {
        return providersById.get(providerId).getRelay(relayId);
    }

    public List<RelayDTO> getRelays() {
        return providersById.entrySet().stream().flatMap(entry -> entry.getValue().getRelays().stream()).collect(Collectors.toList());
    }

    public void setActive(@Nonnull String providerId, @Nonnull String relayId, boolean active) {
        providersById.get(providerId).setActive(relayId, active);
        notifyListeners();
    }

    public void updateRelay(@Nonnull RelayDTO relay) {
        providersById.get(relay.getProviderId()).updateRelay(relay);
        notifyListeners();
    }

}
