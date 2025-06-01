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

package de.hasait.sprinkler.ui.schedule;


import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.hasait.common.ui.AbstractGridView;
import de.hasait.common.ui.JpaRepositoryDataProvider;
import de.hasait.common.ui.MainLayout;
import de.hasait.sprinkler.domain.schedule.ScheduleLogPO;
import de.hasait.sprinkler.domain.schedule.ScheduleLogRepository;
import de.hasait.sprinkler.ui.UiConstants;
import jakarta.annotation.security.PermitAll;

/**
 *
 */
@PermitAll
@Route(value = "schedulelogs", layout = MainLayout.class)
@PageTitle(ScheduleLogsView.TITLE)
@SpringComponent
@UIScope
public class ScheduleLogsView extends AbstractGridView<ScheduleLogPO> {

    public static final String TITLE = "Schedule Logs";

    private final ScheduleLogRepository repository;

    private final JpaRepositoryDataProvider<ScheduleLogPO, ScheduleLogRepository> dataProvider;

    public ScheduleLogsView(ScheduleLogRepository repository) {
        super(ScheduleLogPO.class, 1);

        this.repository = repository;
        this.dataProvider = new JpaRepositoryDataProvider<>(repository);
        beanGrid.setDataProvider(dataProvider);

        Grid.Column<ScheduleLogPO> startColumn = beanGrid.addColumn(ScheduleLogPO::getStart);
        startColumn.setHeader("Start");

        Grid.Column<ScheduleLogPO> scheduleIdColumn = beanGrid.addColumn(po -> po.getSchedule().getId());
        scheduleIdColumn.setHeader("Schedule Id");

        Grid.Column<ScheduleLogPO> relayNameColumn = beanGrid.addColumn(ScheduleLogPO::getRelayName);
        relayNameColumn.setHeader("Relay Name");

        Grid.Column<ScheduleLogPO> durationMillisColumn = beanGrid.addColumn(ScheduleLogPO::determineDurationHuman);
        durationMillisColumn.setHeader(UiConstants.CAPTION_DURATION_HUMAN);
    }

    @Override
    protected void updateGrid() {
        super.updateGrid();

        dataProvider.refreshAll();
    }

}
