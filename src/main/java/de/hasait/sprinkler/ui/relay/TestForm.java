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

package de.hasait.sprinkler.ui.relay;

import java.util.concurrent.TimeUnit;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import de.hasait.common.util.Unit;
import de.hasait.common.vaadin.AbstractForm;
import de.hasait.sprinkler.domain.relay.RelayPO;
import de.hasait.sprinkler.service.relay.RelayService;

/**
 *
 */
@SpringComponent
@UIScope
public class TestForm extends AbstractForm {

    private final RelayService relayService;

    private TextField nameField;
    private TextField durationSecondsField;
    private Button activateButton;
    private Button deactivateButton;

    private RelayPO relay;

    public TestForm(RelayService relayService) {
        super();

        this.relayService = relayService;
    }

    @Override
    protected void populateLayoutBeforeButtonBar() {
        super.populateLayoutBeforeButtonBar();

        vaadinWidgetFactory.addHeader(this, "test", 2);

        nameField = vaadinWidgetFactory.createRoTextField("name");
        add(nameField);

        durationSecondsField = vaadinWidgetFactory.createTextField("durationSeconds");
        add(durationSecondsField);

        activateButton = vaadinWidgetFactory.createButton("activate", VaadinIcon.PLAY, this::onActivateButtonClicked);
        addToButtonBar(activateButton);

        deactivateButton = vaadinWidgetFactory.createButton("deactivate", VaadinIcon.STOP.create(), this::onDeactivateButtonClicked);
        addToButtonBar(deactivateButton);
    }

    public void setRelay(RelayPO relay) {
        this.relay = relay;

        if (relay != null && relay.getId() != null) {
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
        int durationSeconds = (int) Unit.S.fromHuman(durationSecondsField.getValue());
        relayService.scheduleNow(relay.getId(), (int) TimeUnit.SECONDS.toMillis(durationSeconds), "TestForm");
    }

    private void onDeactivateButtonClicked(ClickEvent<?> clickEvent) {
        relayService.deactivate(relay.getId());
    }

}
