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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.hasait.sprinkler.service.relay.RelayDTO;
import de.hasait.sprinkler.service.schedule.ScheduleService;

/**
 *
 */
@SpringComponent
@ViewScope
class TestForm extends VerticalLayout {

    private final TextField durationSecondsField = new TextField("Duration [s]");
    private final Button activateButton;
    private final Button deactivateButton;

    private final ScheduleService scheduleService;

    private RelayDTO relay;

    public TestForm(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;

        setMargin(false);

        Label title = new Label("Test");
        title.addStyleName(ValoTheme.LABEL_H4);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        addComponent(title);

        FormLayout formLayout = new FormLayout();
        formLayout.setMargin(false);
        formLayout.addComponent(durationSecondsField);
        addComponent(formLayout);

        HorizontalLayout buttonLayout = new HorizontalLayout();

        activateButton = new Button("Activate", VaadinIcons.PLAY);
        buttonLayout.addComponent(activateButton);

        deactivateButton = new Button("Deactivate", VaadinIcons.STOP);
        buttonLayout.addComponent(deactivateButton);

        addComponent(buttonLayout);

        activateButton.addClickListener(this::onActivateButtonClicked);
        deactivateButton.addClickListener(this::onDeactivateButtonClicked);
    }

    public void setRelay(RelayDTO relay) {
        this.relay = relay;

        activateButton.setEnabled(relay != null);
        deactivateButton.setEnabled(relay != null);
    }

    private void onActivateButtonClicked(Button.ClickEvent clickEvent) {
        int durationSeconds = Integer.parseInt(durationSecondsField.getValue());
        scheduleService.pulse(relay, (int) TimeUnit.SECONDS.toMillis(durationSeconds));
    }

    private void onDeactivateButtonClicked(Button.ClickEvent clickEvent) {
        scheduleService.stop(relay);
    }

}
