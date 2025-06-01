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

package de.hasait.sprinkler.ui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.hasait.common.ui.MainLayoutCustomizer;
import de.hasait.common.ui.VaadinUtil;
import de.hasait.sprinkler.domain.relay.RelayPO;
import de.hasait.sprinkler.domain.schedule.ScheduleLogPO;
import de.hasait.sprinkler.domain.schedule.SchedulePO;
import de.hasait.sprinkler.domain.sensor.SensorPO;
import de.hasait.sprinkler.domain.sensor.SensorValuePO;
import de.hasait.sprinkler.service.relay.provider.RelayProvider;
import de.hasait.sprinkler.service.sensor.provider.SensorProvider;
import de.hasait.sprinkler.ui.relay.RelayProvidersView;
import de.hasait.sprinkler.ui.relay.RelaysView;
import de.hasait.sprinkler.ui.schedule.ScheduleLogsView;
import de.hasait.sprinkler.ui.schedule.SchedulesView;
import de.hasait.sprinkler.ui.sensor.SensorProvidersView;
import de.hasait.sprinkler.ui.sensor.SensorValuesView;
import de.hasait.sprinkler.ui.sensor.SensorsView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class SprinklerMainLayoutCustomizer implements MainLayoutCustomizer {

    private static final Logger LOG = LoggerFactory.getLogger(SprinklerMainLayoutCustomizer.class);

    @Override
    public void populateDrawer(VerticalLayout verticalLayout) {
        VaadinUtil.addDataViewRouterLink(verticalLayout, SchedulePO.class, "grid", SchedulesView.class);
        VaadinUtil.addDataViewRouterLink(verticalLayout, ScheduleLogPO.class, "grid", ScheduleLogsView.class);
        VaadinUtil.addDataViewRouterLink(verticalLayout, RelayPO.class, "grid", RelaysView.class);
        VaadinUtil.addDataViewRouterLink(verticalLayout, RelayProvider.class, "grid", RelayProvidersView.class);
        VaadinUtil.addDataViewRouterLink(verticalLayout, SensorPO.class, "grid", SensorsView.class);
        VaadinUtil.addDataViewRouterLink(verticalLayout, SensorValuePO.class, "grid", SensorValuesView.class);
        VaadinUtil.addDataViewRouterLink(verticalLayout, SensorProvider.class, "grid", SensorProvidersView.class);
    }

}
