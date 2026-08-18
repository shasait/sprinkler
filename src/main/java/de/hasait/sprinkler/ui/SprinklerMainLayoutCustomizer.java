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

package de.hasait.sprinkler.ui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.stereotype.Service;

import de.hasait.common.vaadin.AbstractMainLayoutCustomizer;
import de.hasait.sprinkler.domain.relay.RelayPO;
import de.hasait.sprinkler.domain.schedule.ScheduleLogPO;
import de.hasait.sprinkler.domain.schedule.SchedulePO;
import de.hasait.sprinkler.domain.sensor.SensorPO;
import de.hasait.sprinkler.domain.sensor.SensorValuePO;
import de.hasait.sprinkler.service.relay.driver.RelayDriver;
import de.hasait.sprinkler.service.sensor.driver.SensorDriver;
import de.hasait.sprinkler.ui.relay.RelayDriverGridView;
import de.hasait.sprinkler.ui.relay.RelayGrid;
import de.hasait.sprinkler.ui.schedule.ScheduleLogsView;
import de.hasait.sprinkler.ui.schedule.ScheduleGrid;
import de.hasait.sprinkler.ui.sensor.SensorDriverGridView;
import de.hasait.sprinkler.ui.sensor.SensorValuesView;
import de.hasait.sprinkler.ui.sensor.SensorGrid;

/**
 *
 */
@Service
public class SprinklerMainLayoutCustomizer extends AbstractMainLayoutCustomizer {

    @Override
    public void populateDrawer(VerticalLayout verticalLayout) {
        addDataViewRouterLink(verticalLayout, ScheduleGrid.class);
        addDataViewRouterLink(verticalLayout, ScheduleLogsView.class);
        addDataViewRouterLink(verticalLayout, RelayGrid.class);
        addDataViewRouterLink(verticalLayout, RelayDriverGridView.class);
        addDataViewRouterLink(verticalLayout, SensorGrid.class);
        addDataViewRouterLink(verticalLayout, SensorValuesView.class);
        addDataViewRouterLink(verticalLayout, SensorDriverGridView.class);
    }

}
