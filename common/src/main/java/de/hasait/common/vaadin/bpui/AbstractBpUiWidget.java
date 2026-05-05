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

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.Grid;
import jakarta.annotation.Nonnull;

import de.hasait.common.util.BeanProperty;
import de.hasait.common.util.I18nSupport;
import de.hasait.common.vaadin.wf.DefaultVaadinWidgetFactory;
import de.hasait.common.vaadin.wf.VaadinWidgetFactory;

public abstract class AbstractBpUiWidget<B, P> implements BpUiWidget<B> {

    private final BpUiWidgets<B> bpUiWidgets;
    protected final Class<B> beanClass;

    protected final VaadinWidgetFactory vaadinWidgetFactory;
    protected final I18nSupport i18nSupport;
    private final int layoutPriority;

    protected final BeanProperty<B, P> beanProperty;
    protected final String propertyName;

    protected AbstractBpUiWidget(BpUiWidgets<B> bpUiWidgets, BeanProperty<B, P> beanProperty, I18nSupport i18nSupport) {
        this.bpUiWidgets = Objects.requireNonNull(bpUiWidgets, "puiWidgets");
        bpUiWidgets.register(this);

        this.beanProperty = Objects.requireNonNull(beanProperty, "beanProperty");
        Objects.requireNonNull(beanProperty.getGetter(), "beanProperty.getter");
        this.beanClass = bpUiWidgets.getBeanClass();
        this.propertyName = beanProperty.getName();

        this.i18nSupport = Objects.requireNonNull(i18nSupport, "i18nSupport");
        this.vaadinWidgetFactory = new DefaultVaadinWidgetFactory(i18nSupport) {
            @Nonnull
            @Override
            public <F> F initField(@Nonnull F field, @Nonnull String baseKey) {
                if (field instanceof HasValue<?, ?> hasValue) {
                    bpUiWidgets.registerField(baseKey, hasValue);
                }
                return super.initField(field, baseKey);
            }
        };
        this.layoutPriority = vaadinWidgetFactory.determineLayoutPriority(propertyName);
    }

    @Override
    public int getLayoutPriority() {
        return layoutPriority;
    }

    @Override
    public void populateGrid(@Nonnull Grid<B> grid) {
        addDefaultColumn(grid);
    }

    protected Grid.Column<B> addDefaultColumn(Grid<B> grid) {
        return vaadinWidgetFactory.addValueColumn(grid, this::obtainPropertyValue, propertyName);
    }

    protected final P obtainPropertyValue(B bean) {
        return beanProperty.getGetter().apply(bean);
    }

}
