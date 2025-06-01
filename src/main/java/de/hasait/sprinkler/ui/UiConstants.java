/*
 * Copyright (C) 2024 by Sebastian Hasait (sebastian at hasait dot de)
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

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UiConstants {

    public static final String CAPTION_ACTIVE = "Active";
    public static final String CAPTION_CRON_EXPRESSION = "Cron Expression";
    public static final String TOOLTOP_CRON_EXPRESSION = "Second[0-59] Minute[0-59] Hour[0-23] Day[1-31] Month[1-12] Weekday[0-7]"
            + "<br/>Example 1: <b>0 0 6 * * 1</b> = Every Monday at 06:00:00"
            + "<br/>Example 2: <b>0 0 5,21 * * *</b> = Every day at 06:00:00 and 21:00:00";
    public static final String CAPTION_CRON_NEXT = "Cron Next";
    public static final String CAPTION_CRON_NEXT_RELATIVE = "Cron Next (relative)";
    public static final String CAPTION_CRON_NEXT_NEXT = "Cron Next Next";
    public static final String CAPTION_DURATION_SECONDS = "Duration [s]";
    public static final String CAPTION_DURATION_HUMAN = "Duration";
    public static final String CAPTION_ENABLED = "Enabled";
    public static final String CAPTION_NAME = "Name";
    public static final String CAPTION_PROVIDER_ID = "Provider";
    public static final String CAPTION_PROVIDER_CONFIG = "Provider Config";
    public static final String CAPTION_RELAY = "Relay";
    public static final String CAPTION_SENSOR = "Sensor";
    public static final String CAPTION_SENSOR_INFLUENCE = "Sensor Influence";
    public static final String CAPTION_SENSOR_CHANGE_LIMIT = "Sensor Change Limit";
    public static final String NEXT_FORMAT_PATTERN = "E, yyyy-MM-dd HH:mm";
    public static final DateTimeFormatter NEXT_FORMATTER = DateTimeFormatter.ofPattern(NEXT_FORMAT_PATTERN);

    public static String formatNext(LocalDateTime next) {
        return next == null ? StringUtils.EMPTY : UiConstants.NEXT_FORMATTER.format(next);
    }

}
