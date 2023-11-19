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

package de.hasait.sprinkler.ui.schedule;


import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.PropertyId;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.hasait.sprinkler.domain.relay.RelayPO;
import de.hasait.sprinkler.domain.relay.RelayRepository;
import de.hasait.sprinkler.domain.schedule.SchedulePO;
import de.hasait.sprinkler.domain.schedule.ScheduleRepository;
import de.hasait.sprinkler.domain.sensor.SensorPO;
import de.hasait.sprinkler.domain.sensor.SensorRepository;
import de.hasait.sprinkler.service.schedule.ScheduleService;
import de.hasait.sprinkler.service.InvalidProviderIdException;
import de.hasait.sprinkler.ui.AbstractCrudForm;
import de.hasait.sprinkler.ui.JpaRepositoryDataProvider;
import de.hasait.sprinkler.ui.UiConstants;
import de.hasait.sprinkler.ui.widget.CronWidget;
import de.hasait.sprinkler.util.Util;

import java.util.concurrent.TimeUnit;

/**
 *
 */
@SpringComponent
@UIScope
class ScheduleForm extends AbstractCrudForm<SchedulePO, ScheduleRepository> {

    @PropertyId("enabled")
    public final Checkbox enabledField = new Checkbox(UiConstants.CAPTION_ENABLED);

    @PropertyId("relay")
    public final ComboBox<RelayPO> relayComboBox = new ComboBox<>(UiConstants.CAPTION_RELAY);

    @PropertyId("durationSeconds")
    public final TextField durationSecondsField = new TextField(UiConstants.CAPTION_DURATION_SECONDS);

    @PropertyId("sensor")
    public final ComboBox<SensorPO> sensorComboBox = new ComboBox<>(UiConstants.CAPTION_SENSOR);

    @PropertyId("sensorInfluence")
    public final TextField sensorInfluenceField = new TextField(UiConstants.CAPTION_SENSOR_INFLUENCE);

    @PropertyId("sensorChangeLimit")
    public final TextField sensorChangeLimitField = new TextField(UiConstants.CAPTION_SENSOR_CHANGE_LIMIT);

    private final CronWidget cronWidget = new CronWidget();

    @PropertyId("cronExpression")
    public final TextField cronExpressionField = cronWidget.getCronExpressionField();

    private final TextField durationHumanLabel = new TextField(UiConstants.CAPTION_DURATION_HUMAN);
    private final TextField effDurationPreviewLabel = new TextField("Effective " + UiConstants.CAPTION_DURATION_SECONDS);

    private final RelayRepository relayRepository;
    private final SensorRepository sensorRepository;
    private final ScheduleService scheduleService;

    public ScheduleForm(ScheduleRepository scheduleRepository, RelayRepository relayRepository, SensorRepository sensorRepository, ScheduleService scheduleService) {
        super(SchedulePO.class, scheduleRepository);

        this.relayRepository = relayRepository;
        this.sensorRepository = sensorRepository;
        this.scheduleService = scheduleService;
    }

    @Override
    protected void populateLayout() {
        add(enabledField);

        relayComboBox.setItems(new JpaRepositoryDataProvider<>(relayRepository));
        relayComboBox.setItemLabelGenerator(RelayPO::getName);
        add(relayComboBox);

        add(durationSecondsField);
        durationSecondsField.addValueChangeListener(this::onDurationOrRainFactorChange);

        durationHumanLabel.setReadOnly(true);
        add(durationHumanLabel);

        sensorComboBox.setItems(new JpaRepositoryDataProvider<>(sensorRepository));
        sensorComboBox.setItemLabelGenerator(SensorPO::getName);
        add(sensorComboBox);

        add(sensorInfluenceField);
        sensorInfluenceField.setTooltipText(
                UiConstants.CAPTION_DURATION_SECONDS + " is reduced by sensor value multiplied by this value and then divided by 100");
        sensorInfluenceField.addValueChangeListener(this::onDurationOrRainFactorChange);

        add(sensorChangeLimitField);

        effDurationPreviewLabel.setReadOnly(true);
        effDurationPreviewLabel.setTooltipText(
                UiConstants.CAPTION_DURATION_SECONDS + " including " + UiConstants.CAPTION_SENSOR_INFLUENCE);
        add(effDurationPreviewLabel);

        cronWidget.populateLayout(this);
    }

    @Override
    protected void populateBinder() {
        binder.forMemberField(relayComboBox) //
                .asRequired() //
        ;

        binder.forMemberField(durationSecondsField) //
                .asRequired() //
                .withConverter(new StringToIntegerConverter("Invalid")) //
                .withValidator(value -> value > 0, "Must be > 0") //
                .withValidator(value -> value < TimeUnit.SECONDS.convert(10, TimeUnit.HOURS), "Must be < 10h") //
        ;

        binder.forMemberField(sensorComboBox) //
                .asRequired() //
        ;

        binder.forMemberField(sensorInfluenceField) //
                .asRequired() //
                .withConverter(new StringToIntegerConverter("Invalid")) //
                .withValidator(value -> value >= 0, "Must be >= 0") //
        ;

        cronWidget.populateBinder(binder);
    }

    private void onDurationOrRainFactorChange(HasValue.ValueChangeEvent<String> event) {
        SchedulePO tmp = new SchedulePO();
        try {
            binder.writeBean(tmp);
        } catch (ValidationException e) {
            durationHumanLabel.setValue("!");
            effDurationPreviewLabel.setValue("!");
            return;
        }

        durationHumanLabel.setValue(Util.millisToHuman(tmp.determineDurationMillis(), Integer.MAX_VALUE));

        try {
            long durationMillisRain = scheduleService.determineDurationMillisSensor(tmp);
            effDurationPreviewLabel.setValue(Long.toString(TimeUnit.SECONDS.convert(durationMillisRain, TimeUnit.MILLISECONDS)));
        } catch (InvalidProviderIdException e) {
            effDurationPreviewLabel.setValue("!");
        }
    }

}
