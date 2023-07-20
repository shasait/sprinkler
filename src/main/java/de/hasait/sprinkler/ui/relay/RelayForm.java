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

package de.hasait.sprinkler.ui.relay;


import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.PropertyId;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.hasait.sprinkler.domain.relay.RelayPO;
import de.hasait.sprinkler.domain.relay.RelayRepository;
import de.hasait.sprinkler.service.relay.provider.RelayProviderService;
import de.hasait.sprinkler.ui.AbstractCrudForm;
import de.hasait.sprinkler.ui.UiConstants;

/**
 *
 */
@SpringComponent
@UIScope
class RelayForm extends AbstractCrudForm<RelayPO, RelayRepository> {

    @PropertyId("name")
    public final TextField nameField = new TextField(UiConstants.CAPTION_NAME);

    @PropertyId("providerId")
    public final ComboBox<String> providerIdComboBox = new ComboBox<>(UiConstants.CAPTION_PROVIDER_ID);

    @PropertyId("providerConfig")
    public final TextField providerConfigField = new TextField(UiConstants.CAPTION_PROVIDER_CONFIG);

    private final RelayProviderService relayProviderService;

    public RelayForm(RelayRepository relayRepository, RelayProviderService relayProviderService) {
        super(RelayPO.class, relayRepository);

        this.relayProviderService = relayProviderService;
    }

    @Override
    protected void populateLayout() {
        add(nameField);

        providerIdComboBox.setItems(DataProvider.ofCollection(relayProviderService.findAllIds()));
        add(providerIdComboBox);

        add(providerConfigField);

        addSpacer();
    }

    @Override
    protected void populateBinder() {
        binder.forMemberField(nameField) //
                .asRequired() //
        ;

        binder.forMemberField(providerIdComboBox) //
                .asRequired() //
        ;

        binder.forMemberField(providerConfigField) //
        ;

        binder.withValidator((RelayPO value, ValueContext context) -> {
            String result = relayProviderService.validateProviderConfig(value.getProviderId(), value.getProviderConfig());
            return result == null ? ValidationResult.ok() : ValidationResult.error(result);
        });
    }

}
