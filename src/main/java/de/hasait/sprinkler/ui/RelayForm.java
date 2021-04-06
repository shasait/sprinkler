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

import com.vaadin.annotations.PropertyId;
import com.vaadin.data.Binder;
import com.vaadin.data.StatusChangeListener;
import com.vaadin.data.ValidationException;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

import de.hasait.sprinkler.service.relay.RelayDTO;

/**
 *
 */
@SpringComponent
@ViewScope
class RelayForm extends FormLayout {

    @PropertyId("name")
    public final TextField nameField = new TextField(RelaysView.CAPTION_NAME);

    private final Binder<RelayDTO> binder;

    public RelayForm() {
        super();

        setMargin(false);

        addComponent(nameField);

        binder = new Binder<>(RelayDTO.class);

        binder.forMemberField(nameField) //
              .asRequired() //
        ;

        binder.bindInstanceFields(this);
    }

    public void addStatusChangeListener(StatusChangeListener listener) {
        binder.addStatusChangeListener(listener);
    }

    public void readBeanIntoFields(RelayDTO bean) {
        binder.readBean(bean);
        binder.validate();
    }

    public void writeFieldsIntoBean(RelayDTO bean) throws ValidationException {
        binder.writeBean(bean);
    }

}
