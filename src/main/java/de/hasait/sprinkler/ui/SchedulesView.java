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

import java.util.Date;
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
import com.vaadin.ui.renderers.DateRenderer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hasait.sprinkler.service.schedule.ScheduleDTO;
import de.hasait.sprinkler.service.schedule.ScheduleService;

/**
 *
 */
@SpringView(name = "Schedules")
public class SchedulesView implements View, ViewDetach {

    public static final String CAPTION_RELAY = "Relay";
    public static final String CAPTION_DURATION_MIN = "Duration [min]";
    public static final String CAPTION_RAIN_FACTOR_100 = "Rain Factor x 100";
    public static final String CAPTION_CRON_EXPRESSION = "Cron Expression";
    public static final String CAPTION_NEXT = "Next Activation";
    public static final String CAPTION_ACTIVE = "Active";

    private static final Logger LOG = LoggerFactory.getLogger(SchedulesView.class);

    private final ScheduleService scheduleService;
    private final ScheduleForm beanForm;
    private final WeatherForm weatherForm;

    private final GridLayout viewLayout;

    private final Grid<ScheduleDTO> beanGrid;
    private final Button addOrSaveButton;
    private final Button deleteButton;

    private final Runnable scheduleServiceListener = new Runnable() {
        @Override
        public void run() {
            viewLayout.getUI().access(() -> updateGrid());
        }
    };

    public SchedulesView(ScheduleService scheduleService, ScheduleForm beanForm, WeatherForm weatherForm) {
        this.scheduleService = scheduleService;
        this.beanForm = beanForm;
        this.weatherForm = weatherForm;

        viewLayout = new GridLayout(2, 1);
        viewLayout.setMargin(false);
        viewLayout.setSpacing(true);
        viewLayout.setDefaultComponentAlignment(Alignment.TOP_LEFT);
        viewLayout.setSizeFull();

        int row = 0;

        beanGrid = new Grid<>();
        beanGrid.setSizeFull();
        initGrid();

        viewLayout.addComponent(beanGrid, 0, row, 1, row);
        viewLayout.setRowExpandRatio(row, 1);

        row++;
        viewLayout.setRows(row + 1);

        viewLayout.addComponent(beanForm, 0, row, 0, row);
        beanForm.addStatusChangeListener(this::onBinderStatusChanged);

        viewLayout.addComponent(weatherForm, 1, row, 1, row);

        row++;
        viewLayout.setRows(row + 1);

        HorizontalLayout buttonLayout = new HorizontalLayout();

        addOrSaveButton = new Button();
        buttonLayout.addComponent(addOrSaveButton);

        deleteButton = new Button("Delete");
        deleteButton.setIcon(VaadinIcons.TRASH);
        buttonLayout.addComponent(deleteButton);

        viewLayout.addComponent(buttonLayout, 0, row, 0, row);
        viewLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_LEFT);

        row++;
        viewLayout.setRows(row + 1);

        beanGrid.addSelectionListener(this::onGridSelectionChanged);
        addOrSaveButton.addClickListener(this::onAddOrSaveButtonClicked);
        deleteButton.addClickListener(this::onDeleteButtonClicked);
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

        scheduleService.removeListener(scheduleServiceListener);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        LOG.debug("enter");

        scheduleService.addListener(scheduleServiceListener);
        updateGrid();
    }

    @Override
    public Component getViewComponent() {
        return viewLayout;
    }

    private ScheduleDTO getGridSelection() {
        Set<ScheduleDTO> selectedItems = beanGrid.getSelectedItems();
        return selectedItems.isEmpty() ? null : selectedItems.iterator().next();
    }

    private void handleBound(Consumer<ScheduleDTO> ifValid) {
        ScheduleDTO selection = getGridSelection();
        ScheduleDTO cloneOrNew = selection != null ? new ScheduleDTO(selection) : new ScheduleDTO();
        try {
            beanForm.writeFieldsIntoBean(cloneOrNew);
            ifValid.accept(cloneOrNew);
        } catch (ValidationException e) {
            Notification.show("Invalid values");
        }
    }

    private void initGrid() {
        beanGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        Grid.Column<ScheduleDTO, String> relay = beanGrid
                .addColumn(row -> row.getRelay() != null ? row.getRelay().getName() : StringUtils.EMPTY);
        relay.setCaption(CAPTION_RELAY);

        Grid.Column<ScheduleDTO, Integer> durationMinutes = beanGrid.addColumn(ScheduleDTO::getDurationMinutes);
        durationMinutes.setCaption(CAPTION_DURATION_MIN);

        Grid.Column<ScheduleDTO, Integer> rainFactor = beanGrid.addColumn(ScheduleDTO::getRainFactor100);
        rainFactor.setCaption(CAPTION_RAIN_FACTOR_100);

        Grid.Column<ScheduleDTO, String> cronExpression = beanGrid.addColumn(ScheduleDTO::getCronExpression);
        cronExpression.setCaption(CAPTION_CRON_EXPRESSION);

        Grid.Column<ScheduleDTO, Date> next = beanGrid.addColumn(ScheduleDTO::getNext);
        next.setRenderer(new DateRenderer("%1$tF %1$tR %1$ta"));
        next.setCaption(CAPTION_NEXT);

        Grid.Column<ScheduleDTO, Boolean> active = beanGrid.addColumn(row -> row.getRelay() != null && row.getRelay().isActive());
        active.setCaption(CAPTION_ACTIVE);
    }

    private void onAddOrSaveButtonClicked(Button.ClickEvent event) {
        handleBound(scheduleService::addOrUpdateSchedule);
    }

    private void onBinderStatusChanged(StatusChangeEvent event) {
        boolean valid = !event.hasValidationErrors();
        addOrSaveButton.setEnabled(valid);
    }

    private void onDeleteButtonClicked(Button.ClickEvent clickEvent) {
        ScheduleDTO selection = getGridSelection();
        if (selection != null) {
            scheduleService.deleteSchedule(selection);
        }
    }

    private void onGridSelectionChanged(SelectionEvent<ScheduleDTO> event) {
        updateBinder();
        updateFormButtons();
    }

    private void updateBinder() {
        ScheduleDTO selection = getGridSelection();
        beanForm.readBeanIntoFields(selection);
    }

    private void updateFormButtons() {
        boolean existing = getGridSelection() != null;
        addOrSaveButton.setCaption(existing ? "Save" : "Add");
        addOrSaveButton.setIcon(existing ? VaadinIcons.ARCHIVE : VaadinIcons.PLUS);
        deleteButton.setEnabled(existing);
    }

    private void updateGrid() {
        List<ScheduleDTO> schedules = scheduleService.getSchedules();
        beanGrid.setDataProvider(DataProvider.ofCollection(schedules));
        onGridSelectionChanged(null);
    }

}
