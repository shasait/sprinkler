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


import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.hasait.common.ui.AbstractCrudGrid;
import de.hasait.common.ui.MainLayout;
import de.hasait.sprinkler.domain.relay.RelayPO;
import de.hasait.sprinkler.domain.relay.RelayRepository;
import de.hasait.sprinkler.service.relay.provider.RelayProvider;
import de.hasait.sprinkler.service.relay.provider.RelayProviderService;
import de.hasait.sprinkler.ui.UiConstants;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@PermitAll
@Route(value = "relays", layout = MainLayout.class)
@PageTitle(RelaysView.TITLE)
@SpringComponent
@UIScope
public class RelaysView extends AbstractCrudGrid<RelayPO, RelayRepository, RelayForm> {

    public static final String TITLE = "Relays";

    private static final Logger LOG = LoggerFactory.getLogger(RelaysView.class);

    private final TestForm testForm;

    private final RelayProviderService relayProviderService;

    public RelaysView(RelayRepository repository, RelayForm beanForm, TestForm testForm, RelayProviderService relayProviderService) {
        super(RelayPO.class, repository, 3, beanForm);

        this.testForm = testForm;
        this.relayProviderService = relayProviderService;

        add(testForm);

        Grid.Column<RelayPO> name = beanGrid.addColumn(RelayPO::getName);
        name.setHeader(UiConstants.CAPTION_NAME);

        Grid.Column<RelayPO> providerId = beanGrid.addColumn(RelayPO::getProviderId);
        providerId.setHeader(UiConstants.CAPTION_PROVIDER_ID);

        Grid.Column<RelayPO> providerConfig = beanGrid.addColumn(RelayPO::getProviderConfig);
        providerConfig.setHeader(UiConstants.CAPTION_PROVIDER_CONFIG);

        Grid.Column<RelayPO> active = beanGrid.addColumn(po -> {
            try {
                return relayProviderService.isActive(po.getProviderId(), po.getProviderConfig());
            } catch (RuntimeException e) {
                LOG.warn("{} failed", RelayProvider.class.getSimpleName(), e);
                // TODO make it somehow visible for the user
                return false;
            }
        });
        active.setHeader(UiConstants.CAPTION_ACTIVE);
    }

    @Override
    protected RelayPO newPO() {
        return new RelayPO();
    }


    @Override
    protected void onGridSelectionChanged(SelectionEvent<?, RelayPO> event) {
        super.onGridSelectionChanged(event);

        testForm.setRelay(getGridSelection());
    }

}
