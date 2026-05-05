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

package de.hasait.sprinkler.ui.sensor;


import java.util.Iterator;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import de.hasait.common.service.driver.InvalidDriverIdException;
import de.hasait.common.vaadin.GenericCrudForm;
import de.hasait.sprinkler.domain.sensor.SensorPO;
import de.hasait.sprinkler.domain.sensor.SensorRepository;
import de.hasait.sprinkler.domain.sensor.SensorValuePO;
import de.hasait.sprinkler.service.sensor.SensorService;

/**
 *
 */
@SpringComponent
@UIScope
public class SensorForm extends GenericCrudForm<Long, SensorPO, SensorRepository> {

    private TextField sensorValue1Label;
    private TextField sensorValue2Label;
    private TextField sensorChangeLabel;
    private Button refreshButton = new Button(VaadinIcon.REFRESH.create());

    private final SensorService sensorService;

    public SensorForm(SensorRepository repository, SensorService sensorService) {
        super(SensorPO.class, repository);

        this.sensorService = sensorService;
    }

    @Override
    protected void populateLayoutAfterButtonBar() {
        super.populateLayoutAfterButtonBar();

        vaadinWidgetFactory.addHeader(this, "sensorValues", 2);

        sensorValue1Label = vaadinWidgetFactory.createRoTextField("sensorValue1");
        add(sensorValue1Label);
        sensorValue2Label = vaadinWidgetFactory.createRoTextField("sensorValue2");
        add(sensorValue2Label);
        sensorChangeLabel = vaadinWidgetFactory.createRoTextField("sensorChange");
        add(sensorChangeLabel);
        vaadinWidgetFactory.addSpacer(this);

        refreshButton = vaadinWidgetFactory.createButton("refresh", VaadinIcon.REFRESH, event -> update());
        add(new HorizontalLayout(refreshButton));
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
            } catch (InvalidDriverIdException e) {
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
        return valuePO == null ? "-" : i18nSupport.msgkka("sensorValue.valueAtDateTime", valuePO.getIntValue(), i18nSupport.format("sensorValue.dateTime", valuePO.getDateTime()));
    }

}
