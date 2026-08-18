/*
 * Copyright (C) 2026 by Sebastian Hasait (sebastian at hasait dot de)
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

package de.hasait.sprinkler.ui.relay;

import org.springframework.stereotype.Service;

import de.hasait.common.jpa.domain.driver.DriverInstanceEO;
import de.hasait.common.util.BeanProperties;
import de.hasait.common.util.BeanProperty;
import de.hasait.common.vaadin.bpui.BpCustomizer;
import de.hasait.sprinkler.domain.relay.RelayPO;
import de.hasait.sprinkler.service.relay.RelayService;

@Service
public class RelayPOCustomizer implements BpCustomizer<RelayPO> {

    private final RelayService relayService;

    public RelayPOCustomizer(RelayService relayService) {
        this.relayService = relayService;
    }

    @Override
    public Class<RelayPO> getBeanClass() {
        return RelayPO.class;
    }

    @Override
    public void customizeBeanProperties(BeanProperties<RelayPO> beanProperties) {
        BeanProperty<RelayPO, Boolean> activeBeanProperty = new BeanProperty<>(RelayPO.class, Boolean.class, "relayActive", relay -> {
            DriverInstanceEO driverInstance = relay.getDriverInstance();
            try {
                return relayService.isActive(driverInstance);
            } catch (RuntimeException e) {
                return false;
            }
        }, null);
        beanProperties.add(activeBeanProperty);
    }

}
