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
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import de.hasait.common.util.BeanProperty;
import de.hasait.common.vaadin.bpui.AbstractBpUiFactory;
import de.hasait.common.vaadin.bpui.BpUiWidget;
import de.hasait.common.vaadin.bpui.BpUiWidgets;
import de.hasait.common.vaadin.bpui.SimpleBpUiWidget;

@Service
public class StringAsTextFieldBpUiFactory extends AbstractBpUiFactory<Object, String> {

    public StringAsTextFieldBpUiFactory() {
        super(String.class, 1000);
    }

    @Nonnull
    @Override
    public <B, P extends String> BpUiWidget<B> createWidget(@Nonnull BpUiWidgets<B> bpUiWidgets, @Nonnull BeanProperty<B, P> beanProperty) {
        return new SimpleBpUiWidget<>(bpUiWidgets, beanProperty, getI18nSupport(), TextField::new) {
            @Override
            protected Binder.BindingBuilder<B, String> customizeBindingFv(Binder.BindingBuilder<B, String> bindingBuilder) {
                return super.customizeBindingFv(bindingBuilder).withNullRepresentation(StringUtils.EMPTY);
            }
        };
    }

}
