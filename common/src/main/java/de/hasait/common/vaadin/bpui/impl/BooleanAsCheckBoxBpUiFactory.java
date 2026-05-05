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

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.binder.Binder;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

import de.hasait.common.util.BeanProperty;
import de.hasait.common.vaadin.bpui.AbstractBpUiFactory;
import de.hasait.common.vaadin.bpui.BpUiWidget;
import de.hasait.common.vaadin.bpui.BpUiWidgets;
import de.hasait.common.vaadin.bpui.SimpleBpUiWidget;

@Service
public class BooleanAsCheckBoxBpUiFactory extends AbstractBpUiFactory<Object, Boolean> {

    public BooleanAsCheckBoxBpUiFactory() {
        super(Boolean.class, 1000);
    }

    @Override
    public <CB, CP> boolean canHandle(@Nonnull Class<CB> beanClass, @Nonnull BeanProperty<CB, CP> beanProperty) {
        return super.canHandle(beanClass, beanProperty) || Boolean.TYPE.equals(beanProperty.getPropertyClass());
    }

    @Override
    public @Nonnull <B, P extends Boolean> BpUiWidget<B> createWidget(@Nonnull BpUiWidgets<B> bpUiWidgets, @Nonnull BeanProperty<B, P> beanProperty) {
        return new SimpleBpUiWidget<>(bpUiWidgets, beanProperty, getI18nSupport(), Checkbox::new) {
            @Override
            protected Binder.BindingBuilder<B, Boolean> customizeBindingFv(Binder.BindingBuilder<B, Boolean> bindingBuilder) {
                return bindingBuilder;
            }

            @Override
            public void populateGrid(@Nonnull Grid<B> grid) {
                vaadinWidgetFactory.addComponentColumn(grid, gb -> (Boolean.TRUE.equals(obtainPropertyValue(gb)) ? VaadinIcon.CHECK_SQUARE_O : VaadinIcon.THIN_SQUARE).create(), propertyName);
            }
        };
    }

}
