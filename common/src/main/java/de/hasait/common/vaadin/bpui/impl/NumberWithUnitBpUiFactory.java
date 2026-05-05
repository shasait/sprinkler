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

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import de.hasait.common.util.BeanProperty;
import de.hasait.common.util.Unit;
import de.hasait.common.vaadin.bpui.AbstractBpUiFactory;
import de.hasait.common.vaadin.bpui.BpUiWidget;
import de.hasait.common.vaadin.bpui.BpUiWidgets;
import de.hasait.common.vaadin.bpui.SimpleBpUiWidget;

@Service
public class NumberWithUnitBpUiFactory extends AbstractBpUiFactory<Object, Number> {

    public NumberWithUnitBpUiFactory() {
        super(Number.class, 0);
    }

    @Override
    public <CB, CP> boolean canHandle(@Nonnull Class<CB> beanClass, @Nonnull BeanProperty<CB, CP> beanProperty) {
        Class<?> propertyType = beanProperty.getPropertyClass();
        if (true //
                && !Integer.class.isAssignableFrom(propertyType) //
                && !Integer.TYPE.isAssignableFrom(propertyType) //
                && !Long.class.isAssignableFrom(propertyType) //
                && !Long.TYPE.isAssignableFrom(propertyType) //
        ) {
            return false;
        }

        NumberWithUnitBpUi annotation = beanProperty.findAnnotation(NumberWithUnitBpUi.class);
        if (annotation == null) {
            return false;
        }

        return true;
    }

    @Nonnull
    @Override
    public <B, P extends Number> BpUiWidget<B> createWidget(@Nonnull BpUiWidgets<B> bpUiWidgets, @Nonnull BeanProperty<B, P> beanProperty) {
        Class<?> propertyType = beanProperty.getPropertyClass();
        boolean intValueNeeded = Integer.class.isAssignableFrom(propertyType) || Integer.TYPE.isAssignableFrom(propertyType);
        NumberWithUnitBpUi annotation = beanProperty.findAnnotation(NumberWithUnitBpUi.class);
        Unit unit = annotation.unit();
        var converter = new Converter<String, Object>() {
            @Override
            public String convertToPresentation(Object value, ValueContext context) {
                if (value == null) {
                    return null;
                }
                if (value instanceof Integer intValue) {
                    return unit.toHuman(intValue);
                }
                if (value instanceof Long longValue) {
                    return unit.toHuman(longValue);
                }
                throw new RuntimeException("Unsupported value type: " + value.getClass());
            }

            @Override
            public Result<Object> convertToModel(String stringValue, ValueContext context) {
                if (StringUtils.isBlank(stringValue)) {
                    return Result.ok(null);
                }
                long longValue;
                try {
                    longValue = unit.fromHuman(stringValue);
                } catch (Exception e) {
                    return Result.error(e.getMessage());
                }
                if (intValueNeeded) {
                    return Result.ok((int) longValue);
                }
                return Result.ok(longValue);
            }
        };

        return new SimpleBpUiWidget<>(bpUiWidgets, beanProperty, getI18nSupport(), TextField::new) {
            @Override
            public void populateBinder(Binder<B> binder) {
                customizeBindingFv(binder.forField(field)) //
                        .withNullRepresentation(StringUtils.EMPTY) //
                        .withConverter(converter) //
                        .bind(propertyName)
                ;
            }

            @Override
            public void populateGrid(@Nonnull Grid<B> grid) {
                vaadinWidgetFactory.addValueColumn(grid, gb -> converter.convertToPresentation(obtainPropertyValue(gb), null), propertyName);
            }
        };
    }

}
