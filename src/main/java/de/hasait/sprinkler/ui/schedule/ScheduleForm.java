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

package de.hasait.sprinkler.ui.schedule;


import java.util.concurrent.TimeUnit;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import de.hasait.common.service.driver.InvalidDriverIdException;
import de.hasait.common.util.ValueWithExplanation;
import de.hasait.common.vaadin.GenericCrudForm;
import de.hasait.sprinkler.domain.schedule.SchedulePO;
import de.hasait.sprinkler.domain.schedule.ScheduleRepository;
import de.hasait.sprinkler.service.schedule.ScheduleService;

/**
 *
 */
@SpringComponent
@UIScope
public class ScheduleForm extends GenericCrudForm<Long, SchedulePO, ScheduleRepository> {

    private final TextField durationLabel = new TextField();
    private final TextField effDurationPreviewLabel = new TextField();

    private final ScheduleService scheduleService;

    public ScheduleForm(ScheduleRepository scheduleRepository, ScheduleService scheduleService) {
        super(SchedulePO.class, scheduleRepository);

        this.scheduleService = scheduleService;
    }

    @Override
    protected void populateLayoutBeforeButtonBar() {
        super.populateLayoutBeforeButtonBar();

        getWidgets().addValueChangeListener("durationSeconds", this::onDurationOrRainFactorChange);
        getWidgets().addValueChangeListener("sensorInfluence", this::onDurationOrRainFactorChange);

        durationLabel.setLabel(i18nSupport.labelText("durationLabel"));
        durationLabel.setReadOnly(true);
        add(durationLabel);

        effDurationPreviewLabel.setLabel(i18nSupport.labelText("effDurationPreviewLabel"));
        effDurationPreviewLabel.setReadOnly(true);
        add(effDurationPreviewLabel);
    }

    @Override
    protected void populateBinder() {
        super.populateBinder();

        /* TODO implement
        @Min and @Max
        binder.forMemberField(durationSecondsField) //
                .asRequired() //
                .withConverter(new StringToIntegerConverter("Invalid")) //
                .withValidator(value -> value > 0, "Must be > 0") //
                .withValidator(value -> value < TimeUnit.SECONDS.convert(10, TimeUnit.HOURS), "Must be < 10h") //
        ;

        binder.forMemberField(sensorInfluenceField) //
                .asRequired() //
                .withConverter(new StringToIntegerConverter("Invalid")) //
                .withValidator(value -> value >= 0, "Must be >= 0") //
        ;
        */
    }

    private void onDurationOrRainFactorChange(HasValue.ValueChangeEvent<?> event) {
        SchedulePO tmp = new SchedulePO();
        try {
            binder.writeBean(tmp);
        } catch (ValidationException e) {
            durationLabel.setValue("!");
            effDurationPreviewLabel.setValue("!");
            effDurationPreviewLabel.setTooltipText(e.getMessage());
            return;
        }

        durationLabel.setValue(Integer.toString(tmp.getDurationSeconds()));

        try {
            ValueWithExplanation<Long> durationMillisSensor = scheduleService.determineDurationMillisSensor(tmp);
            effDurationPreviewLabel.setValue(Long.toString(TimeUnit.SECONDS.convert(durationMillisSensor.value(), TimeUnit.MILLISECONDS)));
            effDurationPreviewLabel.setTooltipText(durationMillisSensor.explanation());
        } catch (InvalidDriverIdException e) {
            effDurationPreviewLabel.setValue("!");
            effDurationPreviewLabel.setTooltipText(e.getMessage());
        }
    }

}
