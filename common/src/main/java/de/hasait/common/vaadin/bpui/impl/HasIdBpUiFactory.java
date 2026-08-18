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

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hasait.common.service.HasId;
import de.hasait.common.service.HasName;
import de.hasait.common.service.Store;
import de.hasait.common.service.StoreRegistry;
import de.hasait.common.util.BeanProperty;
import de.hasait.common.vaadin.VaadinUtil;
import de.hasait.common.vaadin.bpui.AbstractBpUiFactory;
import de.hasait.common.vaadin.bpui.BpUiWidget;
import de.hasait.common.vaadin.bpui.BpUiWidgets;
import de.hasait.common.vaadin.bpui.SimpleBpUiWidget;

@Service
public final class HasIdBpUiFactory extends AbstractBpUiFactory<Object, HasId<?>> {

    private StoreRegistry storeRegistry;

    public HasIdBpUiFactory() {
        super((Class) HasId.class, 0);
    }

    public StoreRegistry getStoreRegistry() {
        return storeRegistry;
    }

    @Autowired
    public void setStoreRegistry(StoreRegistry storeRegistry) {
        this.storeRegistry = storeRegistry;
    }

    @Override
    public @Nonnull <B, P extends HasId<?>> BpUiWidget<B> createWidget(@Nonnull BpUiWidgets<B> bpUiWidgets, @Nonnull BeanProperty<B, P> beanProperty) {
        Class<P> objectClass = beanProperty.getPropertyClass();
        Store<P, ?> store = storeRegistry.getNonnull((Class) objectClass);

        ItemLabelGenerator<P> labelFunction;
        if (HasName.class.isAssignableFrom(objectClass)) {
            labelFunction = po -> ((HasName) po).getName();
        } else {
            labelFunction = Object::toString;
        }

        return new SimpleBpUiWidget<>(bpUiWidgets, beanProperty, getI18nSupport(), labelText -> {
            ComboBox<P> field = new ComboBox<>(labelText);
            field.setItems(VaadinUtil.createBestDataProvider(store));
            field.setClearButtonVisible(true);
            field.setItemLabelGenerator(labelFunction);
            return field;
        }) {
            @Override
            public void populateGrid(@Nonnull Grid<B> grid) {
                vaadinWidgetFactory.addValueColumn(grid, gb -> {
                    P object = obtainPropertyValue(gb);
                    return labelFunction.apply(object);
                }, propertyName);
            }
        };
    }

}
