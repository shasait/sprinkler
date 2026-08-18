/*
 * Copyright (C) 2025 by Sebastian Hasait (sebastian at hasait dot de)
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

package de.hasait.sprinkler.service.sensor.driver.dummy;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;

import de.hasait.common.util.AssertUtil;
import de.hasait.common.util.Util;
import de.hasait.sprinkler.service.sensor.driver.SensorDriver;
import de.hasait.sprinkler.service.sensor.driver.SensorValue;

/**
 *
 */
@Service
public class DummySensorDriver implements SensorDriver<String> {

    private static final Pattern PATTERN = Pattern.compile("^([0-9]+)([+][-]([0-9]+))?$");

    @Nonnull
    @Override
    public String getId() {
        return "dummy";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Dummy returning configured number";
    }

    @Nullable
    @Override
    public String getDisabledReason() {
        return null;
    }

    @Override
    public @Nonnull String parseDriverConfigText(@Nullable String driverConfigText) {
        AssertUtil.matches(PATTERN, driverConfigText, "driverConfigText");
        return driverConfigText;
    }

    @Override
    public SensorValue obtainValue(@Nonnull String config) {
        Matcher matcher = PATTERN.matcher(config);
        if (!matcher.matches()) {
            throw new RuntimeException("Invalid config: " + config);
        }
        int baseValue = Integer.parseInt(matcher.group(1));
        int value;
        if (matcher.groupCount() == 1) {
            value = baseValue;
        } else {
            int variation = Integer.parseInt(matcher.group(3));
            value = baseValue + Util.RANDOM.nextInt(2 * variation) - variation;
        }
        return new SensorValue(LocalDateTime.now(), value);
    }

}
