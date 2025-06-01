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


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.PropertyId;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.hasait.sprinkler.domain.sensor.SensorPO;
import de.hasait.sprinkler.domain.sensor.SensorRepository;
import de.hasait.sprinkler.domain.sensor.SensorValuePO;
import de.hasait.sprinkler.service.sensor.SensorService;
import de.hasait.common.service.InvalidProviderIdException;
import de.hasait.sprinkler.service.sensor.provider.SensorProviderService;
import de.hasait.common.ui.AbstractCrudForm;
import de.hasait.sprinkler.ui.UiConstants;
import de.hasait.common.ui.widget.CronWidget;

import java.util.Iterator;

/**
 *
 */
@SpringComponent
@UIScope
class SensorForm extends AbstractCrudForm<SensorPO, SensorRepository> {

    @PropertyId("name")
    public final TextField nameField = new TextField(UiConstants.CAPTION_NAME);

    @PropertyId("providerId")
    public final ComboBox<String> providerIdComboBox = new ComboBox<>(UiConstants.CAPTION_PROVIDER_ID);

    @PropertyId("providerConfig")
    public final TextField providerConfigField = new TextField(UiConstants.CAPTION_PROVIDER_CONFIG);

    private final CronWidget cronWidget = new CronWidget();

    @PropertyId("cronExpression")
    public final TextField cronExpressionField = cronWidget.getCronExpressionField();

    private final TextField sensorValue1Label = new TextField("Last Value 1");
    private final TextField sensorValue2Label = new TextField("Last Value 2");
    private final TextField sensorChangeLabel = new TextField("Change");
    private final Button refreshButton = new Button(VaadinIcon.REFRESH.create());

    private final SensorProviderService providerService;
    private final SensorService sensorService;

    public SensorForm(SensorRepository repository, SensorProviderService providerService, SensorService sensorService) {
        super(SensorPO.class, repository);

        this.providerService = providerService;
        this.sensorService = sensorService;
    }

    @Override
    protected void populateLayout() {
        add(nameField);

        providerIdComboBox.setItems(DataProvider.ofCollection(providerService.findAllIds()));
        add(providerIdComboBox);

        add(providerConfigField, 2);

        cronWidget.populateLayout(this);

        closeFormLayout();

        addHeader("Values", 2);

        add(sensorValue1Label);
        add(sensorValue2Label);
        add(sensorChangeLabel);
        addSpacer();

        refreshButton.setTooltipText("Refresh sensor value");
        add(new HorizontalLayout(refreshButton));

        refreshButton.addClickListener(event -> update());
    }

    @Override
    protected void afterBeanSet() {
        super.afterBeanSet();
        update();
    }

    private void update() {
        SensorPO sensorPO = binder.getBean();
        if (sensorPO != null && sensorPO.getId() != null) {
            try {
                Iterator<SensorValuePO> lastValuesI = sensorService.getLastValues(sensorPO).iterator();
                SensorValuePO value1 = lastValuesI.hasNext() ? lastValuesI.next() : null;
                SensorValuePO value2 = lastValuesI.hasNext() ? lastValuesI.next() : null;
                sensorValue1Label.setValue(renderSensorValuePO(value1));
                sensorValue2Label.setValue(renderSensorValuePO(value2));
                sensorChangeLabel.setValue(Integer.toString(sensorService.determineChange(sensorPO)));
            } catch (InvalidProviderIdException e) {
                sensorValue1Label.setValue("!");
                sensorValue2Label.setValue("!");
                sensorChangeLabel.setValue("!");
            }
        } else {
            sensorValue1Label.setValue("-");
            sensorValue2Label.setValue("-");
            sensorChangeLabel.setValue("-");
        }

    }

    private String renderSensorValuePO(SensorValuePO valuePO) {
        return valuePO == null ? "-" : valuePO.getIntValue() + " at " + UiConstants.formatNext(valuePO.getDateTime());
    }

    @Override
    protected void populateBinder() {
        binder.forMemberField(nameField) //
                .asRequired() //
        ;

        binder.forMemberField(providerIdComboBox) //
                .asRequired() //
        ;

        binder.forMemberField(providerConfigField) //
        ;

        cronWidget.populateBinder(binder);

        binder.withValidator((SensorPO value, ValueContext context) -> {
            String result = providerService.validateProviderConfig(value.getProviderId(), value.getProviderConfig());
            return result == null ? ValidationResult.ok() : ValidationResult.error(result);
        });
    }

}
