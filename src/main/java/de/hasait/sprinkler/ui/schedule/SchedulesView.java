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
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.hasait.common.ui.AbstractCrudGrid;
import de.hasait.common.ui.MainLayout;
import de.hasait.common.util.Util;
import de.hasait.sprinkler.domain.schedule.SchedulePO;
import de.hasait.sprinkler.domain.schedule.ScheduleRepository;
import de.hasait.sprinkler.ui.UiConstants;
import jakarta.annotation.security.PermitAll;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@PermitAll
@Route(value = "schedules", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle(SchedulesView.TITLE)
@SpringComponent
@UIScope
public class SchedulesView extends AbstractCrudGrid<SchedulePO, ScheduleRepository, ScheduleForm> {

    public static final String TITLE = "Schedules";

    public SchedulesView(ScheduleRepository repository, ScheduleForm beanForm) {
        super(SchedulePO.class, repository, 2, beanForm);

        Grid.Column<SchedulePO> enabledColumn = beanGrid.addColumn(SchedulePO::isEnabled);
        enabledColumn.setHeader(UiConstants.CAPTION_ENABLED);

        Grid.Column<SchedulePO> relayColumn = beanGrid.addColumn(
                po -> po.getRelay() != null ? po.getRelay().getName() : StringUtils.EMPTY);
        relayColumn.setHeader(UiConstants.CAPTION_RELAY);

        Grid.Column<SchedulePO> durationSecondsColumn = beanGrid.addColumn(SchedulePO::getDurationSeconds);
        durationSecondsColumn.setHeader(UiConstants.CAPTION_DURATION_SECONDS);

        Grid.Column<SchedulePO> durationHumanColumn = beanGrid.addColumn(new TextRenderer<>(po -> Util.millisToHuman(TimeUnit.MILLISECONDS.convert(po.getDurationSeconds(), TimeUnit.SECONDS), Integer.MAX_VALUE)));
        durationHumanColumn.setHeader(UiConstants.CAPTION_DURATION_HUMAN);

        Grid.Column<SchedulePO> sensorColumn = beanGrid.addColumn(
                po -> po.getSensor() != null ? po.getSensor().getName() : StringUtils.EMPTY);
        sensorColumn.setHeader(UiConstants.CAPTION_SENSOR);

        Grid.Column<SchedulePO> sensorInfluenceColumn = beanGrid.addColumn(SchedulePO::getSensorInfluence);
        sensorInfluenceColumn.setHeader(UiConstants.CAPTION_SENSOR_INFLUENCE);

        Grid.Column<SchedulePO> sensorChangeLimitColumn = beanGrid.addColumn(SchedulePO::getSensorChangeLimit);
        sensorChangeLimitColumn.setHeader(UiConstants.CAPTION_SENSOR_CHANGE_LIMIT);

        Grid.Column<SchedulePO> cronExpressionColumn = beanGrid.addColumn(SchedulePO::getCronExpression);
        cronExpressionColumn.setHeader(UiConstants.CAPTION_CRON_EXPRESSION);

        Grid.Column<SchedulePO> nextColumn = beanGrid.addColumn(new TextRenderer<>(po -> UiConstants.formatNext(Util.determineNext(po.getCronExpression(), LocalDateTime.now()))));
        nextColumn.setHeader(UiConstants.CAPTION_CRON_NEXT);

        Grid.Column<SchedulePO> nextRelativeColumn = beanGrid.addColumn(new TextRenderer<>(po -> Util.determineNextRelative(po.getCronExpression(), LocalDateTime.now(), 3)));
        nextRelativeColumn.setHeader(UiConstants.CAPTION_CRON_NEXT_RELATIVE);
    }

    @Override
    protected SchedulePO newPO() {
        return new SchedulePO();
    }

}
