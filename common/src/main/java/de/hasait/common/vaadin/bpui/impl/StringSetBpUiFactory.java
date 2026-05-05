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

import java.util.Set;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

import de.hasait.common.util.BeanProperty;
import de.hasait.common.vaadin.bpui.AbstractBpUiFactory;
import de.hasait.common.vaadin.bpui.BpUiWidget;
import de.hasait.common.vaadin.bpui.BpUiWidgets;
import de.hasait.common.vaadin.bpui.SimpleBpUiWidget;
import de.hasait.common.vaadin.widget.StringSetWidget;

@Service
public class StringSetBpUiFactory extends AbstractBpUiFactory<Object, Set<String>> {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public StringSetBpUiFactory() {
        super((Class<Set<String>>) ((Class) Set.class), 0);
    }

    @Override
    public <CB, CP> boolean canHandle(@Nonnull Class<CB> beanClass, @Nonnull BeanProperty<CB, CP> beanProperty) {
        if (!super.canHandle(beanClass, beanProperty)) {
            return false;
        }

        StringSetBpUi annotation = beanProperty.findAnnotation(StringSetBpUi.class);
        if (annotation == null) {
            return false;
        }

        return true;
    }

    @Override
    public @Nonnull <B, P extends Set<String>> BpUiWidget<B> createWidget(@Nonnull BpUiWidgets<B> bpUiWidgets, @Nonnull BeanProperty<B, P> beanProperty) {
        return new SimpleBpUiWidget<>(bpUiWidgets, beanProperty, getI18nSupport(), labelText -> {
            StringSetWidget field = new StringSetWidget(getI18nSupport());
            field.setLabel(labelText);
            StringSetBpUi annotation = beanProperty.findAnnotation(StringSetBpUi.class);
            field.setHeight(annotation.height());
            return field;
        });
    }

}
