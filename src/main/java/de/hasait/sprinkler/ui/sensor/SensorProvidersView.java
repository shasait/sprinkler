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

package de.hasait.sprinkler.ui.sensor;


import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.hasait.sprinkler.service.sensor.provider.SensorProvider;
import de.hasait.sprinkler.service.sensor.provider.SensorProviderService;
import de.hasait.sprinkler.ui.AbstractProvidersView;
import de.hasait.sprinkler.ui.MainLayout;
import jakarta.annotation.security.PermitAll;

/**
 *
 */
@PermitAll
@Route(value = "sensorproviders", layout = MainLayout.class)
@PageTitle(SensorProvidersView.TITLE)
@SpringComponent
@UIScope
public class SensorProvidersView extends AbstractProvidersView<SensorProvider, SensorProviderService> {

    public static final String TITLE = "Sensor Providers";

    public SensorProvidersView(SensorProviderService providerService) {
        super(providerService);
    }

}
