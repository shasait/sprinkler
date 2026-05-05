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

package de.hasait.common.vaadin;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.hasait.common.util.I18nSupport;

public abstract class AbstractGridView<B> extends VerticalLayout implements HasDynamicTitle {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractGridView.class);

    protected final Class<B> beanClass;
    protected final Grid<B> beanGrid;

    private final List<Consumer<B>> selectionConsumers = new ArrayList<>();

    protected I18nSupport i18nSupport;

    public AbstractGridView(Class<B> beanClass) {
        this.beanClass = beanClass;

        addAttachListener(this::attach);
        addDetachListener(this::detach);

        setSizeFull();

        beanGrid = new Grid<>(beanClass, false);
        beanGrid.setSizeFull();
        add(beanGrid);
    }

    @PostConstruct
    final void init() {
        populateBeanGrid();

        beanGrid.addSelectionListener(event -> notifyGridSelectionChanged());
    }

    public final I18nSupport getI18nSupport() {
        return i18nSupport;
    }

    @Autowired
    public final void setI18nSupport(I18nSupport i18nSupport) {
        this.i18nSupport = i18nSupport;
    }

    @Override
    public final String getPageTitle() {
        return i18nSupport.applicationAndPageTitle(beanClass, "grid");
    }

    protected abstract void populateBeanGrid();

    private void attach(AttachEvent attachEvent) {
        LOG.debug("attach {}", getClass());

        updateGrid();
    }

    private void detach(DetachEvent detachEvent) {
        LOG.debug("detach {}", getClass());
    }

    protected final B getGridSelection() {
        Set<B> selectedItems = beanGrid.getSelectedItems();
        return selectedItems.isEmpty() ? createNoSelectionResult() : selectedItems.iterator().next();
    }

    protected B createNoSelectionResult() {
        return null;
    }

    protected final void updateGrid() {
        LOG.debug("updateGrid");

        refreshGridData();
        beanGrid.deselectAll();
        notifyGridSelectionChanged();
    }

    protected abstract void refreshGridData();

    protected final void addSelectionConsumer(Consumer<B> consumer) {
        selectionConsumers.add(consumer);
    }

    private void notifyGridSelectionChanged() {
        LOG.debug("notifyGridSelectionChanged");
        B selection = getGridSelection();

        selectionConsumers.forEach(consumer -> consumer.accept(selection));
    }

}
