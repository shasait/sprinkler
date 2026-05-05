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

package de.hasait.common.service.driver;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

@Service
public class DriverServiceRegistry {

    private final Map<Class<?>, DriverService<?>> driverServicesByClass = new HashMap<>();

    public DriverServiceRegistry(DriverService<?>[] driverServices) {
        super();

        for (var driverService : driverServices) {
            driverServicesByClass.put(driverService.getDriverClass(), driverService);
        }
    }

    @SuppressWarnings("unchecked")
    public <D extends Driver<?>> DriverService<D> get(Class<D> driverClass) {
        return (DriverService<D>) driverServicesByClass.get(driverClass);
    }

    public <D extends Driver<?>> @Nonnull DriverService<D> getNonnull(Class<D> driverClass) {
        var result = get(driverClass);
        if (result == null) {
            throw new RuntimeException("No " + DriverService.class.getSimpleName() + " found for class '" + driverClass);
        }
        return result;
    }

}
