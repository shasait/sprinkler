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

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.vaadin.data.StatusChangeEvent;
import com.vaadin.data.ValidationException;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hasait.sprinkler.service.relay.RelayDTO;
import de.hasait.sprinkler.service.relay.RelayService;

/**
 *
 */
@SpringView(name = "Relays")
public class RelaysView implements View, ViewDetach {

    public static final String CAPTION_PROVIDER_ID = "Provider ID";
    public static final String CAPTION_RELAY_ID = "Relay ID";
    public static final String CAPTION_NAME = "Name";
    public static final String CAPTION_ACTIVE = SchedulesView.CAPTION_ACTIVE;

    private static final Logger LOG = LoggerFactory.getLogger(RelaysView.class);

    private final RelayService relayService;
    private final RelayForm beanForm;
    private final TestForm testForm;

    private final GridLayout viewLayout;
    private final Grid<RelayDTO> beanGrid;
    private final Button saveButton;

    private final Runnable relayServiceListener = new Runnable() {
        @Override
        public void run() {
            viewLayout.getUI().access(() -> updateGrid());
        }
    };

    public RelaysView(RelayService relayService, RelayForm beanForm, TestForm testForm) {
        this.relayService = relayService;
        this.beanForm = beanForm;
        this.testForm = testForm;

        viewLayout = new GridLayout(3, 1);
        viewLayout.setMargin(false);
        viewLayout.setSpacing(true);
        viewLayout.setDefaultComponentAlignment(Alignment.TOP_LEFT);
        viewLayout.setSizeFull();

        int row = 0;

        beanGrid = new Grid<>();
        beanGrid.setSizeFull();
        initGrid();

        viewLayout.addComponent(beanGrid, 0, row, 2, row);
        viewLayout.setRowExpandRatio(row, 1);

        row++;
        viewLayout.setRows(row + 1);

        viewLayout.addComponent(beanForm, 0, row, 0, row);
        beanForm.addStatusChangeListener(this::onBinderStatusChanged);

        viewLayout.addComponent(testForm, 2, row, 2, row);

        row++;
        viewLayout.setRows(row + 1);

        HorizontalLayout buttonLayout = new HorizontalLayout();

        saveButton = new Button("Save", VaadinIcons.ARCHIVE);
        buttonLayout.addComponent(saveButton);

        viewLayout.addComponent(buttonLayout, 0, row, 1, row);
        viewLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_LEFT);

        beanGrid.addSelectionListener(this::onGridSelectionChanged);
        saveButton.addClickListener(this::onSaveButtonClicked);
    }

    @Override
    public void beforeLeave(ViewBeforeLeaveEvent event) {
        LOG.debug("beforeLeave");

        detach();
        event.navigate();
    }

    @Override
    public void detach() {
        LOG.debug("detach");

        relayService.removeListener(relayServiceListener);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        LOG.debug("enter");

        relayService.addListener(relayServiceListener);
        updateGrid();
    }

    @Override
    public Component getViewComponent() {
        return viewLayout;
    }

    private RelayDTO getGridSelection() {
        Set<RelayDTO> selectedItems = beanGrid.getSelectedItems();
        return selectedItems.isEmpty() ? null : selectedItems.iterator().next();
    }

    private void handleBound(Consumer<RelayDTO> ifValid) {
        RelayDTO selection = getGridSelection();
        if (selection == null) {
            return;
        }
        RelayDTO clone = new RelayDTO(selection);
        try {
            beanForm.writeFieldsIntoBean(clone);
            ifValid.accept(clone);
        } catch (ValidationException e) {
            Notification.show("Invalid values");
        }
    }

    private void initGrid() {
        beanGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        Grid.Column<RelayDTO, String> name = beanGrid.addColumn(RelayDTO::getName);
        name.setCaption(CAPTION_NAME);

        Grid.Column<RelayDTO, String> providerId = beanGrid.addColumn(RelayDTO::getProviderId);
        providerId.setCaption(CAPTION_PROVIDER_ID);

        Grid.Column<RelayDTO, String> relayId = beanGrid.addColumn(RelayDTO::getRelayId);
        relayId.setCaption(CAPTION_RELAY_ID);

        Grid.Column<RelayDTO, Boolean> active = beanGrid.addColumn(RelayDTO::isActive);
        active.setCaption(CAPTION_ACTIVE);
    }

    private void onBinderStatusChanged(StatusChangeEvent event) {
        boolean valid = !event.hasValidationErrors();
        saveButton.setEnabled(valid);
    }

    private void onGridSelectionChanged(SelectionEvent<RelayDTO> event) {
        updateBinder();
        updateFormButtons();
        testForm.setRelay(getGridSelection());
    }

    private void onSaveButtonClicked(Button.ClickEvent event) {
        handleBound(relayService::updateRelay);
    }

    private void updateBinder() {
        RelayDTO selection = getGridSelection();
        beanForm.readBeanIntoFields(selection);
    }

    private void updateFormButtons() {
        boolean existing = getGridSelection() != null;
        saveButton.setEnabled(existing);
    }

    private void updateGrid() {
        List<RelayDTO> relays = relayService.getRelays();
        beanGrid.setDataProvider(DataProvider.ofCollection(relays));
        onGridSelectionChanged(null);
    }

}
