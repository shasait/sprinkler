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

package de.hasait.sprinkler.service.sensor.provider;

import java.time.LocalDateTime;

/**
 *
 */
public class SensorValue {

    private final LocalDateTime dateTime;
    private final int value;

    public SensorValue(LocalDateTime dateTime, int value) {
        this.dateTime = dateTime;
        this.value = value;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%1d (%2$tF %2$tR %2$ta)", value, dateTime);
    }

}
