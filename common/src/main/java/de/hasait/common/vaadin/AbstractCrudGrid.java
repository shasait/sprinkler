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


import java.lang.reflect.InvocationTargetException;

import com.vaadin.flow.data.provider.DataProvider;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import de.hasait.common.service.HasId;
import de.hasait.common.service.Store;

public abstract class AbstractCrudGrid<ID, B extends HasId<ID>, S extends Store<B, ID>, BF extends AbstractCrudForm<ID, B, S>> extends AbstractGridView<B> {

    private final S store;
    private final DataProvider<B, ?> dataProvider;

    protected final BF crudForm;

    public AbstractCrudGrid(@Nonnull Class<B> beanClass, @Nonnull S store, @Nullable BF crudForm) {
        super(beanClass);

        this.crudForm = crudForm;

        this.store = store;
        this.dataProvider = VaadinUtil.createBestDataProvider(store);
        beanGrid.setDataProvider(dataProvider);
    }

    @Override
    protected final void populateBeanGrid() {
        customizeBeanGrid();

        if (crudForm != null) {
            customizeCrudForm();
            add(crudForm);
            addSelectionConsumer(crudForm::setBean);
            addAfterCrudForm();
            crudForm.addChangeListener((s, v) -> updateGrid());
        }
    }

    protected void customizeBeanGrid() {
        // nop
    }

    protected void customizeCrudForm() {
        // nop
    }

    protected void addAfterCrudForm() {
        // nop
    }

    @Override
    protected B createNoSelectionResult() {
        try {
            return beanClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void refreshGridData() {
        dataProvider.refreshAll();
    }

}
