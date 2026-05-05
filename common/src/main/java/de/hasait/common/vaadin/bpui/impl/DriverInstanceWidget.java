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

package de.hasait.common.vaadin.bpui.impl;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.provider.DataProvider;
import jakarta.annotation.Nonnull;

import de.hasait.common.service.driver.DriverInstance;
import de.hasait.common.service.driver.DriverService;
import de.hasait.common.util.BeanProperty;
import de.hasait.common.util.I18nSupport;
import de.hasait.common.vaadin.bpui.AbstractBpUiWidget;
import de.hasait.common.vaadin.bpui.BpUiWidgets;

public class
DriverInstanceWidget<B, P extends DriverInstance> extends AbstractBpUiWidget<B, P> {

    private final DriverService<?> driverService;

    private final ComboBox<String> driverIdComboBox;
    private final TextField driverConfigField;

    public DriverInstanceWidget(BpUiWidgets<B> bpUiWidgets, I18nSupport i18nSupport, BeanProperty<B, P> beanProperty, DriverService<?> driverService) {
        super(bpUiWidgets, beanProperty, i18nSupport);

        this.driverService = driverService;

        String driverKey = beanProperty.getName() + ".driver";
        driverIdComboBox = vaadinWidgetFactory.createComboBox(driverKey);
        String configKey = beanProperty.getName() + ".config";
        driverConfigField = vaadinWidgetFactory.createTextField(configKey);
    }

    @Override
    public void populateForm(FormLayout layout) {
        driverIdComboBox.setItems(DataProvider.ofCollection(driverService.findAllIds()));
        layout.add(driverIdComboBox);
        layout.add(driverConfigField, 2);
    }

    @Override
    public void populateGrid(@Nonnull Grid<B> grid) {
        String driverKey = beanProperty.getName() + ".driver";
        vaadinWidgetFactory.addValueColumn(grid, hdi -> obtainPropertyValue(hdi).getDriverId(), driverKey);
        String configKey = beanProperty.getName() + ".config";
        vaadinWidgetFactory.addValueColumn(grid, hdi -> obtainPropertyValue(hdi).getDriverConfig(), configKey);
    }

    @Override
    public void populateBinder(Binder<B> binder) {
        binder.forField(driverIdComboBox) //
                .asRequired() //
                .bind(hdi -> obtainPropertyValue(hdi).getDriverId(), (hdi, value) -> obtainPropertyValue(hdi).setDriverId(value));

        binder.forField(driverConfigField) //
                .bind(hdi -> obtainPropertyValue(hdi).getDriverConfig(), (hdi, value) -> obtainPropertyValue(hdi).setDriverConfig(value));

        binder.withValidator((value, context) -> {
            String result = driverService.validateDriverConfig(obtainPropertyValue(value));
            return result == null ? ValidationResult.ok() : ValidationResult.error(result);
        });
    }

}
