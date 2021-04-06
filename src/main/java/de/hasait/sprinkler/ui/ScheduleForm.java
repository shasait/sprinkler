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

import java.util.concurrent.TimeUnit;

import com.vaadin.annotations.PropertyId;
import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.data.StatusChangeListener;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import de.hasait.sprinkler.service.relay.RelayDTO;
import de.hasait.sprinkler.service.relay.RelayService;
import de.hasait.sprinkler.service.schedule.ScheduleDTO;
import de.hasait.sprinkler.service.schedule.ScheduleService;

/**
 *
 */
@SpringComponent
@ViewScope
class ScheduleForm extends FormLayout {

    @PropertyId("relay")
    public final ComboBox<RelayDTO> relayComboBox = new ComboBox<>(SchedulesView.CAPTION_RELAY);

    @PropertyId("durationMinutes")
    public final TextField durationMinutesField = new TextField();

    @PropertyId("rainFactor100")
    public final TextField rainFactor100Field = new TextField(SchedulesView.CAPTION_RAIN_FACTOR_100);

    @PropertyId("cronExpression")
    public final TextField cronExpressionField = new TextField(SchedulesView.CAPTION_CRON_EXPRESSION);

    private final ScheduleService scheduleService;
    private final Label durationPreviewLabel;
    private final Binder<ScheduleDTO> binder;

    public ScheduleForm(RelayService relayService, ScheduleService scheduleService) {
        super();

        this.scheduleService = scheduleService;

        relayComboBox.setItemCaptionGenerator(RelayDTO::getName);
        relayComboBox.setDataProvider(DataProvider.ofCollection(relayService.getRelays()));

        setMargin(false);

        addComponent(relayComboBox);

        durationPreviewLabel = new Label();
        HorizontalLayout durationLayout = new HorizontalLayout();
        durationLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        durationLayout.addComponent(durationMinutesField);
        durationLayout.addComponent(durationPreviewLabel);
        durationLayout.setCaption(SchedulesView.CAPTION_DURATION_MIN);
        addComponent(durationLayout);

        addComponent(rainFactor100Field);
        rainFactor100Field.setDescription("Duration is reduced by rain multiplied with this value and then divided by 100");

        rainFactor100Field.addValueChangeListener(this::onRainFactorChanged);

        addComponent(cronExpressionField);
        cronExpressionField.setDescription("Minute Hour Day Month Weekday");

        binder = new Binder<>(ScheduleDTO.class);

        binder.forMemberField(relayComboBox) //
              .asRequired() //
        ;

        binder.forMemberField(durationMinutesField) //
              .asRequired() //
              .withConverter(new StringToIntegerConverter("Invalid")) //
              .withValidator(value -> value > 0, "Must be > 0") //
              .withValidator(value -> value < 600, "Must be < 10h") //
        ;

        binder.forMemberField(rainFactor100Field) //
              .asRequired() //
              .withConverter(new StringToIntegerConverter("Invalid")) //
              .withValidator(value -> value >= 0, "Must be >= 0") //
        ;

        binder.bindInstanceFields(this);
    }

    public void addStatusChangeListener(StatusChangeListener listener) {
        binder.addStatusChangeListener(listener);
    }

    public void readBeanIntoFields(ScheduleDTO bean) {
        binder.readBean(bean);
        binder.validate();
    }

    public void writeFieldsIntoBean(ScheduleDTO bean) throws ValidationException {
        binder.writeBean(bean);
    }

    private void onRainFactorChanged(HasValue.ValueChangeEvent<String> event) {
        ScheduleDTO tmp = new ScheduleDTO();
        try {
            binder.writeBean(tmp);
            int durationMinutes = tmp.getDurationMinutes();
            int rainFactor100 = tmp.getRainFactor100();
            long durationMillis = scheduleService.determineDurationMillis(TimeUnit.MINUTES.toMillis(durationMinutes), rainFactor100);
            durationPreviewLabel.setValue(TimeUnit.MILLISECONDS.toSeconds(durationMillis) + "s");
        } catch (ValidationException e) {
            durationPreviewLabel.setValue("invalid");
        }
    }

}
