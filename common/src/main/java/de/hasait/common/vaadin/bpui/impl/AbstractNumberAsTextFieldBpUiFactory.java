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

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.Converter;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

import de.hasait.common.util.BeanProperty;
import de.hasait.common.vaadin.bpui.AbstractBpUiFactory;
import de.hasait.common.vaadin.bpui.BpUiWidget;
import de.hasait.common.vaadin.bpui.BpUiWidgets;
import de.hasait.common.vaadin.bpui.SimpleBpUiWidget;

public abstract class AbstractNumberAsTextFieldBpUiFactory<N extends Number> extends AbstractBpUiFactory<Object, N> {

    protected final Class<?> primitiveClass;

    public AbstractNumberAsTextFieldBpUiFactory(Class<N> propertyClass, int priority, Class<?> primitiveClass) {
        super(propertyClass, priority);

        this.primitiveClass = primitiveClass;
    }

    public AbstractNumberAsTextFieldBpUiFactory(Class<N> propertyClass, Class<?> primitiveClass) {
        this(propertyClass, 1000, primitiveClass);
    }

    @Override
    public <CB, CP> boolean canHandle(@Nonnull Class<CB> beanClass, @Nonnull BeanProperty<CB, CP> beanProperty) {
        return super.canHandle(beanClass, beanProperty) || primitiveClass.equals(beanProperty.getPropertyClass());
    }

    @Nonnull
    @Override
    public <B, P extends N> BpUiWidget<B> createWidget(@Nonnull BpUiWidgets<B> bpUiWidgets, @Nonnull BeanProperty<B, P> beanProperty) {
        Converter<String, N> converter = createConverter(bpUiWidgets.getBeanClass(), beanProperty);
        return new SimpleBpUiWidget<>(bpUiWidgets, beanProperty, getI18nSupport(), TextField::new) {
            @Override
            public void populateBinder(Binder<B> binder) {
                customizeBindingFv(binder.forField(field)) //
                        .withNullRepresentation(StringUtils.EMPTY) //
                        .withConverter(converter) //
                        .bind(propertyName)
                ;
            }
        };
    }

    protected abstract <B, P extends N> @Nonnull Converter<String, N> createConverter(@Nonnull Class<B> beanClass, @Nonnull BeanProperty<B, P> beanProperty);

}
