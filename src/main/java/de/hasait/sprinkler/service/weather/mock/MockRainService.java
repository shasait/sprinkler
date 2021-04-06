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

package de.hasait.sprinkler.service.weather.mock;

import java.time.LocalDateTime;

import de.hasait.sprinkler.service.weather.RainService;
import de.hasait.sprinkler.service.weather.RainValue;

/**
 *
 */
public class MockRainService implements RainService {

    @Override
    public RainValue getLastRainValue() {
        return new RainValue(LocalDateTime.now(), 100);
    }

    @Override
    public String getProviderId() {
        return "mock";
    }

    @Override
    public boolean isRaining() {
        return false;
    }

}
