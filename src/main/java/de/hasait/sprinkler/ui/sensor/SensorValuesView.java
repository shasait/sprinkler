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

package de.hasait.sprinkler.ui.sensor;


import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.hasait.sprinkler.domain.sensor.SensorValuePO;
import de.hasait.sprinkler.domain.sensor.SensorValueRepository;
import de.hasait.sprinkler.ui.AbstractGridView;
import de.hasait.sprinkler.ui.JpaRepositoryDataProvider;
import de.hasait.sprinkler.ui.MainLayout;
import jakarta.annotation.security.PermitAll;

/**
 *
 */
@PermitAll
@Route(value = "sensorvalues", layout = MainLayout.class)
@PageTitle(SensorValuesView.TITLE)
@SpringComponent
@UIScope
public class SensorValuesView extends AbstractGridView<SensorValuePO> {

    public static final String TITLE = "Sensor Values";

    private final SensorValueRepository repository;

    private final JpaRepositoryDataProvider<SensorValuePO, SensorValueRepository> dataProvider;

    public SensorValuesView(SensorValueRepository repository) {
        super(1);

        this.repository = repository;
        this.dataProvider = new JpaRepositoryDataProvider<>(repository);
        beanGrid.setDataProvider(dataProvider);

        Grid.Column<SensorValuePO> scheduleIdColumn = beanGrid.addColumn(po -> po.getSensor().getName());
        scheduleIdColumn.setHeader("Sensor Name");

        Grid.Column<SensorValuePO> dateTimeColumn = beanGrid.addColumn(SensorValuePO::getDateTime);
        dateTimeColumn.setHeader("Date Time");
        dateTimeColumn.setSortable(true);

        Grid.Column<SensorValuePO> intValueColumn = beanGrid.addColumn(SensorValuePO::getIntValue);
        intValueColumn.setHeader("Value");

    }

    @Override
    protected void updateGrid() {
        super.updateGrid();

        dataProvider.refreshAll();
    }

}
