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

package de.hasait.sprinkler.service.sensor.provider.dummy;

import de.hasait.sprinkler.service.sensor.provider.SensorProvider;
import de.hasait.sprinkler.service.sensor.provider.SensorValue;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;

/**
 *
 */
@Service
public class DummySensorProvider implements SensorProvider {

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

    @Nullable
    @Override
    public String validateConfig(@Nonnull String config) {
        try {
            Integer.parseInt(config);
        } catch (NumberFormatException e) {
            return "Not a number";
        }
        return null;
    }

    @Override
    public SensorValue obtainValue(@Nonnull String config) {
        return new SensorValue(LocalDateTime.now(), Integer.parseInt(config));
    }

}
