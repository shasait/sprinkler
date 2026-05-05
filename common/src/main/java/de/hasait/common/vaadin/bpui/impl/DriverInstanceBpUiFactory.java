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

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

import de.hasait.common.service.driver.DriverInstance;
import de.hasait.common.service.driver.DriverServiceRegistry;
import de.hasait.common.util.BeanProperty;
import de.hasait.common.vaadin.bpui.AbstractBpUiFactory;
import de.hasait.common.vaadin.bpui.BpUiWidget;
import de.hasait.common.vaadin.bpui.BpUiWidgets;

@Service
public final class DriverInstanceBpUiFactory extends AbstractBpUiFactory<Object, DriverInstance> {

    private final DriverServiceRegistry driverServiceRegistry;

    public DriverInstanceBpUiFactory(DriverServiceRegistry driverServiceRegistry) {
        super(DriverInstance.class, 1000);

        this.driverServiceRegistry = driverServiceRegistry;
    }

    @Override
    public <CB, CP> boolean canHandle(@Nonnull Class<CB> beanClass, @Nonnull BeanProperty<CB, CP> beanProperty) {
        if (!super.canHandle(beanClass, beanProperty)) {
            return false;
        }

        var annotation = beanProperty.findAnnotation(DriverInstanceBpUi.class);
        if (annotation == null) {
            return false;
        }

        return true;
    }

    @Override
    public @Nonnull <B, P extends DriverInstance> BpUiWidget<B> createWidget(@Nonnull BpUiWidgets<B> bpUiWidgets, @Nonnull BeanProperty<B, P> beanProperty) {
        var annotation = beanProperty.findAnnotation(DriverInstanceBpUi.class);
        var driverClass = annotation.driverClass();
        var driverService = driverServiceRegistry.getNonnull(driverClass);
        return new DriverInstanceWidget<>(bpUiWidgets, getI18nSupport(), beanProperty, driverService);
    }

}
