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

package de.hasait.common.vaadin.wf;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.ValueProvider;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import de.hasait.common.util.I18nSupport;

public interface VaadinWidgetFactory {

    @Nonnull
    I18nSupport getI18nSupport();

    int determineLayoutPriority(@Nonnull String key);

    @Nonnull
    TextField createTextField(@Nonnull String key);

    @Nonnull
    TextField createRoTextField(@Nonnull String key);

    <T> @Nonnull ComboBox<T> createComboBox(@Nonnull String key);

    @Nonnull
    Checkbox createCheckBox(@Nonnull String key);

    default @Nonnull Button createButton(@Nonnull String key, @Nullable ComponentEventListener<ClickEvent<Button>> clickListener) {
        return createButton(key, (Icon) null, clickListener);
    }

    default @Nonnull Button createButton(String key, VaadinIcon vaadinIcon, @Nullable ComponentEventListener<ClickEvent<Button>> clickListener) {
        return createButton(key, vaadinIcon.create(), clickListener);
    }

    @Nonnull
    Button createButton(@Nonnull String key, @Nullable Icon icon, @Nullable ComponentEventListener<ClickEvent<Button>> clickListener);

    <B> @Nonnull Grid.Column<B> addValueColumn(@Nonnull Grid<B> grid, @Nonnull ValueProvider<B, ?> valueProvider, @Nonnull String key);

    <B> @Nonnull Grid.Column<B> addComponentColumn(@Nonnull Grid<B> grid, @Nonnull SerializableFunction<B, ? extends Component> componentFunction, @Nonnull String key);

    void addHeader(@Nonnull FormLayout formLayout, @Nonnull String key, int colspan);

    void addSpacer(@Nonnull FormLayout formLayout);

    <F> @Nonnull F initField(@Nonnull F field, @Nonnull String key);

}
