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

import java.util.Objects;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.hasait.sprinkler.service.weather.RainService;

/**
 *
 */
@SpringComponent
@UIScope
class WeatherForm extends VerticalLayout {

    private final RainService rainService;

    private final Label rainLabel;
    private final Label rainingLabel;
    private final Label providerLabel;
    private final Button refreshButton;

    public WeatherForm(RainService rainService) {
        super();

        this.rainService = rainService;

        setMargin(false);
        setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        Label title = new Label("Weather");
        title.addStyleName(ValoTheme.LABEL_H4);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        addComponent(title);

        FormLayout formLayout = new FormLayout();
        formLayout.setMargin(false);
        formLayout.setSpacing(false);

        rainLabel = new Label();
        rainLabel.setCaption("Rain");
        formLayout.addComponent(rainLabel);

        rainingLabel = new Label();
        rainingLabel.setCaption("Raining");
        formLayout.addComponent(rainingLabel);

        providerLabel = new Label();
        providerLabel.setCaption("Provider");
        formLayout.addComponent(providerLabel);

        addComponent(formLayout);

        HorizontalLayout buttonLayout = new HorizontalLayout();

        refreshButton = new Button(VaadinIcons.REFRESH);
        refreshButton.setDescription("Refresh");
        buttonLayout.addComponent(refreshButton);

        addComponent(buttonLayout);

        refreshButton.addClickListener(event -> update());

        update();
    }

    private void update() {
        rainLabel.setValue(Objects.toString(rainService.getLastRainValue(), "-"));
        rainingLabel.setValue(Boolean.toString(rainService.isRaining()));
        providerLabel.setValue(rainService.getProviderId());
    }

}
