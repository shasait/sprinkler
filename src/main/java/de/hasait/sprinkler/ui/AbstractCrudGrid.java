/*
 * Copyright (C) 2021 by Sebastian Hasait (sebastian at hasait dot de)
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

package de.hasait.sprinkler.ui;


import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.selection.SelectionEvent;
import de.hasait.sprinkler.domain.SearchableRepository;
import de.hasait.sprinkler.service.IdAndVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public abstract class AbstractCrudGrid<PO extends IdAndVersion, R extends SearchableRepository<PO, Long>, BF extends AbstractCrudForm<PO, R>> extends AbstractGridView<PO> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractCrudGrid.class);

    private final R repository;
    private final BF crudForm;
    private final JpaRepositoryDataProvider<PO, R> dataProvider;

    public AbstractCrudGrid(int columns, R repository, BF crudForm) {
        super(columns);

        this.repository = repository;
        this.crudForm = crudForm;

        dataProvider = new JpaRepositoryDataProvider<>(repository);
        beanGrid.setDataProvider(dataProvider);

        Grid.Column<PO> idColumn = beanGrid.addColumn(IdAndVersion::getId);
        idColumn.setHeader("Id");
        Grid.Column<PO> versionColumn = beanGrid.addColumn(IdAndVersion::getVersion);
        versionColumn.setHeader("Version");

        add(crudForm);

        crudForm.addListener(this::updateGrid);
    }

    protected abstract PO newPO();

    protected void onGridSelectionChanged(SelectionEvent<?, PO> event) {
        super.onGridSelectionChanged(event);

        updateCrudForm();
    }

    protected void updateGrid() {
        super.updateGrid();

        dataProvider.refreshAll();
        beanGrid.deselectAll();
        updateCrudForm();
    }

    private void updateCrudForm() {
        PO selection = getGridSelection();
        LOG.debug("updateBinder: selection={}", selection);
        PO bean = selection != null ? selection : newPO();
        crudForm.setBean(bean);
    }

}
