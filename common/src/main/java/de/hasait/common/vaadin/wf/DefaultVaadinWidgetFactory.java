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
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.ValueProvider;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import de.hasait.common.util.I18nSupport;

public class DefaultVaadinWidgetFactory implements VaadinWidgetFactory {

    private final I18nSupport i18nSupport;

    public DefaultVaadinWidgetFactory(I18nSupport i18nSupport) {
        this.i18nSupport = i18nSupport;
    }

    @Nonnull
    @Override
    public I18nSupport getI18nSupport() {
        return i18nSupport;
    }

    @Override
    public int determineLayoutPriority(@Nonnull String baseKey) {
        String layoutPriorityString = i18nSupport.msgdka("1000", baseKey + ".layoutPriority");
        return Integer.parseInt(layoutPriorityString);
    }

    @Nonnull
    @Override
    public TextField createTextField(@Nonnull String key) {
        var field = new TextField(i18nSupport.labelText(key));
        return initField(field, key);
    }

    @Nonnull
    @Override
    public TextField createRoTextField(@Nonnull String key) {
        var field = createTextField(key);
        field.setReadOnly(true);
        return field;
    }

    @Nonnull
    @Override
    public <T> ComboBox<T> createComboBox(@Nonnull String key) {
        var field = new ComboBox<T>(i18nSupport.labelText(key));
        return initField(field, key);
    }

    @Nonnull
    @Override
    public Checkbox createCheckBox(@Nonnull String key) {
        var field = new Checkbox(i18nSupport.labelText(key));
        return initField(field, key);
    }

    @Nonnull
    @Override
    public Button createButton(@Nonnull String key, @Nullable Icon icon, @Nullable ComponentEventListener<ClickEvent<Button>> clickListener) {
        var field = new Button(i18nSupport.labelText(key));
        if (icon != null) {
            field.setIcon(icon);
        }
        if (clickListener != null) {
            field.addClickListener(clickListener);
        }
        return initField(field, key);
    }

    @Override
    public <B> Grid.Column<B> addValueColumn(@Nonnull Grid<B> grid, @Nonnull ValueProvider<B, ?> valueProvider, String key) {
        Grid.Column<B> column = grid.addColumn(valueProvider);
        column.setHeader(i18nSupport.headerText(key));
        return column;
    }

    @Override
    public <B> Grid.Column<B> addComponentColumn(@Nonnull Grid<B> grid, @Nonnull SerializableFunction<B, ? extends Component> componentFunction, String key) {
        Grid.Column<B> column = grid.addColumn(new ComponentRenderer<>(componentFunction));
        column.setHeader(i18nSupport.headerText(key));
        return column;
    }


    @Override
    public void addSpacer(@Nonnull HasComponents layout) {
        layout.add(new Span(""));
    }

    @Override
    public void addHeader(@Nonnull HasComponents layout, @Nonnull String baseKey) {
        addHeaderInternal(layout, baseKey, null);
    }

    @Override
    public void addHeader(@Nonnull HasComponents layout, @Nonnull String baseKey, int colspan) {
        addHeaderInternal(layout, baseKey, colspan);
    }

    private void addHeaderInternal(@Nonnull HasComponents layout, @Nonnull String baseKey, Integer colspan) {
        H4 heading = new H4(i18nSupport.msgkka(baseKey + ".title"));
        heading.getStyle().set("margin-top", "1em");
        layout.add(heading);
        if (colspan != null) {
            if (layout instanceof FormLayout formLayout) {
                formLayout.setColspan(heading, colspan);
            }
        }
    }

    @Nonnull
    @Override
    public <F> F initField(@Nonnull F field, @Nonnull String baseKey) {
        if (field instanceof HasTooltip hasTooltip) {
            String tooltipMarkdown = i18nSupport.tooltipMarkdown(baseKey);
            if (tooltipMarkdown != null) {
                hasTooltip.setTooltipMarkdown(tooltipMarkdown);
            } else {
                String tooltipText = i18nSupport.tooltipText(baseKey);
                if (tooltipText != null) {
                    hasTooltip.setTooltipText(tooltipText);
                }
            }
        }
        return field;
    }

}
