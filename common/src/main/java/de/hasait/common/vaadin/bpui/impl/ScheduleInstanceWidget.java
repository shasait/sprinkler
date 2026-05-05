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

import java.time.LocalDateTime;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

import de.hasait.common.service.schedule.ScheduleInstance;
import de.hasait.common.util.BeanProperty;
import de.hasait.common.util.I18nSupport;
import de.hasait.common.util.Util;
import de.hasait.common.vaadin.bpui.AbstractBpUiWidget;
import de.hasait.common.vaadin.bpui.BpUiWidgets;

public class
ScheduleInstanceWidget<B, P extends ScheduleInstance> extends AbstractBpUiWidget<B, P> {

    private final Checkbox enabledCheckBox;
    private final TextField cronField;
    private final TextField next1PreviewLabel;
    private final TextField nextRelativePreviewLabel;
    private final TextField next2PreviewLabel;

    public ScheduleInstanceWidget(BpUiWidgets<B> bpUiWidgets, I18nSupport i18nSupport, BeanProperty<B, P> beanProperty) {
        super(bpUiWidgets, beanProperty, i18nSupport);

        String enabledKey = beanProperty.getName() + ".enabled";
        enabledCheckBox = vaadinWidgetFactory.createCheckBox(enabledKey);
        cronField = vaadinWidgetFactory.createTextField(beanProperty.getName() + ".cron");
        next1PreviewLabel = vaadinWidgetFactory.createTextField(beanProperty.getName() + ".cronNext");
        nextRelativePreviewLabel = vaadinWidgetFactory.createTextField(beanProperty.getName() + ".cronNextRelative");
        next2PreviewLabel = vaadinWidgetFactory.createTextField(beanProperty.getName() + ".cronNextNext");
    }

    @Override
    public void populateForm(FormLayout layout) {
        layout.add(enabledCheckBox, 1);

        layout.add(cronField, 1);

        next1PreviewLabel.setReadOnly(true);
        layout.add(next1PreviewLabel, 1);

        nextRelativePreviewLabel.setReadOnly(true);
        layout.add(nextRelativePreviewLabel, 1);

        next2PreviewLabel.setReadOnly(true);
        layout.add(next2PreviewLabel, 1);
    }

    @Override
    public void populateGrid(@Nonnull Grid<B> grid) {
        String enabledKey = beanProperty.getName() + ".enabled";
        vaadinWidgetFactory.addValueColumn(grid, hdi -> obtainPropertyValue(hdi).isScheduleEnabled(), enabledKey);
        String cronKey = beanProperty.getName() + ".cron";
        vaadinWidgetFactory.addValueColumn(grid, hdi -> obtainPropertyValue(hdi).getScheduleCron(), cronKey);
    }

    @Override
    public void populateBinder(Binder<B> binder) {
        binder.forField(enabledCheckBox) //
                .bind(bean -> obtainPropertyValue(bean).isScheduleEnabled(), (hdi, value) -> obtainPropertyValue(hdi).setScheduleEnabled(value));

        binder.forField(cronField) //
                .withValidator(value -> {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime next1 = Util.determineNext(value, now);
                    next1PreviewLabel.setValue(formatNext(next1));
                    nextRelativePreviewLabel.setValue(Util.determineNextRelative(now, next1, Integer.MAX_VALUE));
                    LocalDateTime next2 = Util.determineNext(value, next1);
                    next2PreviewLabel.setValue(formatNext(next2));
                    return true;
                }, "Invalid") //
                .bind(bean -> obtainPropertyValue(bean).getScheduleCron(), (hdi, value) -> obtainPropertyValue(hdi).setScheduleCron(value));
    }

    private String formatNext(LocalDateTime next) {
        return next == null ? StringUtils.EMPTY : i18nSupport.format(beanProperty.getName() + ".cronNext", next);
    }

}
