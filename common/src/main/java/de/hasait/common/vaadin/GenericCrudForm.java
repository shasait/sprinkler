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

import org.springframework.beans.factory.annotation.Autowired;

import de.hasait.common.service.HasId;
import de.hasait.common.service.Store;
import de.hasait.common.vaadin.bpui.BpUiService;
import de.hasait.common.vaadin.bpui.BpUiWidgets;

public class GenericCrudForm<ID, B extends HasId<ID>, S extends Store<B, ID>> extends AbstractCrudForm<ID, B, S> {

    private BpUiService bpUiService;

    private BpUiWidgets<B> widgets;

    public GenericCrudForm(Class<B> beanClass, S store) {
        super(beanClass, store);
    }

    public BpUiService getBpUiService() {
        return bpUiService;
    }

    /**
     * Only used if the class is not part of GenericCrudGrid.
     */
    @Autowired
    public void setBpUiService(BpUiService bpUiService) {
        this.bpUiService = bpUiService;
    }

    public BpUiWidgets<B> getWidgets() {
        return widgets;
    }

    /**
     * If owning view created the widgets already they can be passed in, e.g. GenericCrudGrid.
     */
    public void setWidgets(BpUiWidgets<B> widgets) {
        this.widgets = widgets;
    }

    @Override
    protected void populateLayoutBeforeButtonBar() {
        super.populateLayoutBeforeButtonBar();

        if (widgets == null && bpUiService != null) {
            widgets = bpUiService.createWidgets(beanClass);
        }
        widgets.populateForm(this);
    }

    @Override
    protected void populateBinder() {
        widgets.populateBinder(binder);
    }

}
