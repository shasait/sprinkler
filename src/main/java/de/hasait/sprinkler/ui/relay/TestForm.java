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

package de.hasait.sprinkler.ui.relay;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.hasait.sprinkler.domain.relay.RelayPO;
import de.hasait.sprinkler.service.relay.RelayService;
import de.hasait.sprinkler.ui.UiConstants;

import java.util.concurrent.TimeUnit;

/**
 *
 */
@SpringComponent
@UIScope
class TestForm extends VerticalLayout {

    private final TextField nameField = new TextField(UiConstants.CAPTION_NAME);
    private final TextField durationSecondsField = new TextField(UiConstants.CAPTION_DURATION_SECONDS);
    private final Button activateButton;
    private final Button deactivateButton;

    private final RelayService relayService;

    private RelayPO relay;

    public TestForm(RelayService relayService) {
        this.relayService = relayService;

        setMargin(false);

        H4 title = new H4("Test");
        add(title);

        FormLayout formLayout = new FormLayout();

        nameField.setReadOnly(true);
        formLayout.add(nameField);

        formLayout.add(durationSecondsField);

        add(formLayout);

        HorizontalLayout buttonLayout = new HorizontalLayout();

        activateButton = new Button("Activate", VaadinIcon.PLAY.create());
        buttonLayout.add(activateButton);

        deactivateButton = new Button("Deactivate", VaadinIcon.STOP.create());
        buttonLayout.add(deactivateButton);

        add(buttonLayout);

        activateButton.addClickListener(this::onActivateButtonClicked);
        deactivateButton.addClickListener(this::onDeactivateButtonClicked);
    }

    public void setRelay(RelayPO relay) {
        this.relay = relay;

        if (relay != null) {
            nameField.setValue(relay.getName());
            // TODO check disabledReason
            activateButton.setEnabled(true);
            deactivateButton.setEnabled(true);
        } else {
            nameField.setValue("-");
            activateButton.setEnabled(false);
            deactivateButton.setEnabled(false);
        }
    }

    private void onActivateButtonClicked(ClickEvent<?> clickEvent) {
        int durationSeconds = Integer.parseInt(durationSecondsField.getValue());
        relayService.scheduleNow(relay.getId(), (int) TimeUnit.SECONDS.toMillis(durationSeconds), "TestForm");
    }

    private void onDeactivateButtonClicked(ClickEvent<?> clickEvent) {
        relayService.deactivate(relay.getId());
    }

}
