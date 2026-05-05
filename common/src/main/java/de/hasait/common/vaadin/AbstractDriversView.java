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

package de.hasait.common.vaadin;


import com.vaadin.flow.component.grid.Grid;

import de.hasait.common.service.driver.AbstractDriverService;
import de.hasait.common.service.driver.Driver;

public class AbstractDriversView<D extends Driver<?>, S extends AbstractDriverService<D, ?, ?>> extends AbstractGridView<D> {

    private final S driverService;

    public AbstractDriversView(S driverService) {
        super(driverService.getDriverClass());

        this.driverService = driverService;
    }

    @Override
    protected void populateBeanGrid() {
        Grid.Column<D> driverIdColumn = beanGrid.addColumn(Driver::getId);
        driverIdColumn.setHeader("Driver");

        Grid.Column<D> descriptionColumn = beanGrid.addColumn(Driver::getDescription);
        descriptionColumn.setHeader("Description");

        Grid.Column<D> disabledReasonColumn = beanGrid.addColumn(Driver::getDisabledReason);
        disabledReasonColumn.setHeader("Disabled Reason");
    }

    @Override
    protected void refreshGridData() {
        beanGrid.setItems(driverService.findAll());
    }

}
