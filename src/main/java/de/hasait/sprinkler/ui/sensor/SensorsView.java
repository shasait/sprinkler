/*
 * Copyright (C) 2025 by Sebastian Hasait (sebastian at hasait dot de)
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

package de.hasait.sprinkler.ui.sensor;


import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.hasait.common.ui.AbstractCrudGrid;
import de.hasait.common.ui.MainLayout;
import de.hasait.sprinkler.domain.sensor.SensorPO;
import de.hasait.sprinkler.domain.sensor.SensorRepository;
import de.hasait.sprinkler.service.sensor.provider.SensorProviderService;
import de.hasait.sprinkler.ui.UiConstants;
import jakarta.annotation.security.PermitAll;

/**
 *
 */
@PermitAll
@Route(value = "sensors", layout = MainLayout.class)
@SpringComponent
@UIScope
public class SensorsView extends AbstractCrudGrid<SensorPO, SensorRepository, SensorForm> {

    private final SensorProviderService providerService;

    public SensorsView(SensorRepository repository, SensorForm beanForm, SensorProviderService providerService) {
        super(SensorPO.class, repository, 3, beanForm);

        this.providerService = providerService;

        Grid.Column<SensorPO> name = beanGrid.addColumn(SensorPO::getName);
        name.setHeader(UiConstants.CAPTION_NAME);

        Grid.Column<SensorPO> providerId = beanGrid.addColumn(SensorPO::getProviderId);
        providerId.setHeader(UiConstants.CAPTION_PROVIDER_ID);

        Grid.Column<SensorPO> providerConfig = beanGrid.addColumn(SensorPO::getProviderConfig);
        providerConfig.setHeader(UiConstants.CAPTION_PROVIDER_CONFIG);

        Grid.Column<SensorPO> cronExpression = beanGrid.addColumn(SensorPO::getCronExpression);
        cronExpression.setHeader(UiConstants.CAPTION_CRON_EXPRESSION);
    }

    @Override
    protected SensorPO newPO() {
        return new SensorPO();
    }

}
