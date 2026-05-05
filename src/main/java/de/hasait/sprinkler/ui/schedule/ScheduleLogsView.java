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

package de.hasait.sprinkler.ui.schedule;


import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.PermitAll;

import de.hasait.common.vaadin.MainLayout;
import de.hasait.common.vaadin.GenericCrudGrid;
import de.hasait.sprinkler.domain.schedule.ScheduleLogPO;
import de.hasait.sprinkler.domain.schedule.ScheduleLogRepository;

/**
 *
 */
@PermitAll
@Route(value = "schedulelogs", layout = MainLayout.class)
@SpringComponent
@UIScope
public class ScheduleLogsView extends GenericCrudGrid<Long, ScheduleLogPO, ScheduleLogRepository> {

    public ScheduleLogsView(ScheduleLogRepository repository) {
        super(ScheduleLogPO.class, repository, null);
    }

}
