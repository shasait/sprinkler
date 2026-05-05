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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.binder.Binder;

import de.hasait.common.util.AssertUtil;

public final class BpUiWidgets<B> {

    private final Class<B> beanClass;

    private final List<BpUiWidget<B>> widgets = new ArrayList<>();

    private final Map<String, HasValue<?, ?>> fieldRegistry = new HashMap<>();

    private boolean needsSorting = false;

    public BpUiWidgets(Class<B> beanClass) {
        this.beanClass = beanClass;
    }

    public Class<B> getBeanClass() {
        return beanClass;
    }

    public void register(BpUiWidget<B> puiWidget) {
        widgets.add(puiWidget);
        needsSorting = true;
    }

    public void registerField(String key, HasValue<?, ?> field) {
        HasValue<?, ?> oldField = fieldRegistry.put(key, field);
        AssertUtil.isNull(oldField, "Field {0} already registered", key);
    }

    public void populateForm(FormLayout formLayout) {
        ensureSorted();
        widgets.forEach(widget -> widget.populateForm(formLayout));
    }

    private void ensureSorted() {
        if (needsSorting) {
            widgets.sort(Comparator.comparingInt(BpUiWidget::getLayoutPriority));
            needsSorting = false;
        }
    }

    public void populateBinder(Binder<B> binder) {
        ensureSorted();
        widgets.forEach(widget -> widget.populateBinder(binder));
    }

    public void populateGrid(Grid<B> beanGrid) {
        ensureSorted();
        widgets.forEach(widget -> widget.populateGrid(beanGrid));
    }

    public void addValueChangeListener(String key, HasValue.ValueChangeListener<HasValue.ValueChangeEvent<?>> listener) {
        HasValue<?, ?> field = fieldRegistry.get(key);
        AssertUtil.notNull(field, "Field {0} not registered", key);
        field.addValueChangeListener(listener);
    }

}
