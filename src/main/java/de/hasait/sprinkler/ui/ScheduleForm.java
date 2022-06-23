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

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.vaadin.annotations.PropertyId;
import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.data.StatusChangeListener;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
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

    @PropertyId("enabled")
    public final CheckBox enabledField = new CheckBox(SchedulesView.CAPTION_ENABLED);

    @PropertyId("relay")
    public final ComboBox<RelayDTO> relayComboBox = new ComboBox<>(SchedulesView.CAPTION_RELAY);

    @PropertyId("duration")
    public final TextField durationField = new TextField(SchedulesView.CAPTION_DURATION);

    @PropertyId("rainFactor100")
    public final TextField rainFactor100Field = new TextField(SchedulesView.CAPTION_RAIN_FACTOR_100);

    @PropertyId("cronExpression")
    public final TextField cronExpressionField = new TextField(SchedulesView.CAPTION_CRON_EXPRESSION);

    private final ScheduleService scheduleService;
    private final Label duration2ndLabel;
    private final Label effDurationPreviewLabel;
    private final Label next1PreviewLabel;
    private final Label nextRelativePreviewLabel;
    private final Label next2PreviewLabel;
    private final Binder<ScheduleDTO> binder;

    public ScheduleForm(RelayService relayService, ScheduleService scheduleService) {
        super();

        this.scheduleService = scheduleService;

        relayComboBox.setItemCaptionGenerator(RelayDTO::getName);
        relayComboBox.setDataProvider(DataProvider.ofCollection(relayService.getRelays()));

        setMargin(false);

        addComponent(enabledField);

        addComponent(relayComboBox);

        addComponent(durationField);
        durationField.addValueChangeListener(this::onDurationOrRainFactorChange);

        duration2ndLabel = new Label();
        duration2ndLabel.setCaption(SchedulesView.CAPTION_DURATION_2ND);
        addComponent(duration2ndLabel);

        addComponent(rainFactor100Field);
        rainFactor100Field.setDescription(
                SchedulesView.CAPTION_DURATION + " is reduced by rain multiplied with this value and then divided by 100");
        rainFactor100Field.addValueChangeListener(this::onDurationOrRainFactorChange);

        effDurationPreviewLabel = new Label();
        effDurationPreviewLabel.setCaption("Effective " + SchedulesView.CAPTION_DURATION);
        effDurationPreviewLabel.setDescription(
                SchedulesView.CAPTION_DURATION + " for current weather including " + SchedulesView.CAPTION_RAIN_FACTOR_100);
        addComponent(effDurationPreviewLabel);

        addComponent(cronExpressionField);
        cronExpressionField.setDescription("Second[0-59] Minute[0-59] Hour[0-23] Day[1-31] Month[1-12] Weekday[0-7]"
                                                   + "<br/>Example 1: <b>0 0 6 * * 1</b> = Every Monday at 06:00:00"
                                                   + "<br/>Example 2: <b>0 0 5,21 * * *</b> = Every day at 06:00:00 and 21:00:00",
                                           ContentMode.HTML
        );

        next1PreviewLabel = new Label();
        next1PreviewLabel.setCaption(SchedulesView.CAPTION_NEXT);
        addComponent(next1PreviewLabel);

        nextRelativePreviewLabel = new Label();
        nextRelativePreviewLabel.setCaption(SchedulesView.CAPTION_NEXT_RELATIVE);
        addComponent(nextRelativePreviewLabel);

        next2PreviewLabel = new Label();
        next2PreviewLabel.setCaption(SchedulesView.CAPTION_NEXT + " + 1");
        addComponent(next2PreviewLabel);

        binder = new Binder<>(ScheduleDTO.class);

        binder.forMemberField(relayComboBox) //
              .asRequired() //
        ;

        binder.forMemberField(durationField) //
              .asRequired() //
              .withConverter(new StringToLongConverter("Invalid")) //
              .withValidator(value -> value > 0, "Must be > 0") //
              .withValidator(value -> value < ScheduleDTO.DURATION_TIME_UNIT.convert(10, TimeUnit.HOURS), "Must be < 10h") //
        ;

        binder.forMemberField(rainFactor100Field) //
              .asRequired() //
              .withConverter(new StringToIntegerConverter("Invalid")) //
              .withValidator(value -> value >= 0, "Must be >= 0") //
        ;

        binder.forMemberField(cronExpressionField) //
              .withValidator(value -> {
                  Date now = new Date();
                  Date next1 = scheduleService.determineNext(value, now);
                  next1PreviewLabel.setValue(next1 == null ? "" : String.format(SchedulesView.NEXT_FORMAT, next1));
                  nextRelativePreviewLabel.setValue(
                          next1 == null ? "" : scheduleService.determineNextRelative(now, next1, Integer.MAX_VALUE));
                  Date next2 = scheduleService.determineNext(value, next1);
                  next2PreviewLabel.setValue(next2 == null ? "" : String.format(SchedulesView.NEXT_FORMAT, next2));
                  return true;
              }, "Invalid");

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

    private void onDurationOrRainFactorChange(HasValue.ValueChangeEvent<String> event) {
        try {
            ScheduleDTO tmp = new ScheduleDTO();
            binder.writeBean(tmp);
            long duration = tmp.getDuration();
            duration2ndLabel.setValue(ScheduleDTO.getDuration2nd(duration));

            int rainFactor100 = tmp.getRainFactor100();
            long durationMillis = scheduleService.determineDurationMillis(
                    TimeUnit.MILLISECONDS.convert(duration, ScheduleDTO.DURATION_TIME_UNIT), rainFactor100);
            effDurationPreviewLabel.setValue(Long.toString(ScheduleDTO.DURATION_TIME_UNIT.convert(durationMillis, TimeUnit.MILLISECONDS)));
        } catch (ValidationException e) {
            duration2ndLabel.setValue("");
            effDurationPreviewLabel.setValue("");
        }
    }

}
