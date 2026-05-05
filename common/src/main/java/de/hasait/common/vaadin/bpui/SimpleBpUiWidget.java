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

package de.hasait.common.vaadin.bpui;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;

import de.hasait.common.util.BeanProperty;
import de.hasait.common.util.I18nSupport;

public class SimpleBpUiWidget<B, P, FV, F extends Component & HasValue<?, FV>> extends AbstractBpUiWidget<B, P> {

    protected final F field;

    public SimpleBpUiWidget(BpUiWidgets<B> bpUiWidgets, BeanProperty<B, P> beanProperty, I18nSupport i18nSupport, Function<String, F> fieldFactory) {
        super(bpUiWidgets, beanProperty, i18nSupport);

        field = fieldFactory.apply(i18nSupport.labelText(propertyName));
        vaadinWidgetFactory.initField(field, propertyName);
    }

    @Override
    public void populateForm(FormLayout layout) {
        layout.add(field, 1);
    }

    @Override
    public void populateBinder(Binder<B> binder) {
        Binder.BindingBuilder<B, FV> bindingBuilderFv = binder.forField(field);
        bindingBuilderFv = customizeBindingFv(bindingBuilderFv);
        Binder.BindingBuilder<B, P> bindingBuilderP = convertBinding(bindingBuilderFv);
        Function<B, P> getter = Objects.requireNonNull(beanProperty.getGetter());
        BiConsumer<B, P> setter = beanProperty.getSetter();
        if (setter != null) {
            bindingBuilderP.bind(getter::apply, setter::accept);
        } else {
            bindingBuilderP.bindReadOnly(getter::apply);
        }
    }

    protected Binder.BindingBuilder<B, FV> customizeBindingFv(Binder.BindingBuilder<B, FV> bindingBuilder) {
        Binder.BindingBuilder<B, FV> result = bindingBuilder;
        if (beanProperty.isRequired()) {
            result = result.asRequired();
        }
        return result;
    }

    protected Binder.BindingBuilder<B, P> convertBinding(Binder.BindingBuilder<B, FV> bindingBuilder) {
        return (Binder.BindingBuilder<B, P>) bindingBuilder;
    }

}
