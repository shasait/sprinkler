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
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class AbstractDriverService<D extends Driver<?>, O, DI extends DriverInstance> implements DriverService<D> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDriverService.class);

    private final Class<D> driverClass;

    private final Map<String, D> driversById = new HashMap<>();

    private final Class<O> ownerClass;
    private final Function<O, DI> driverInstanceGetter;

    public AbstractDriverService(D[] drivers, Class<O> ownerClass, Function<O, DI> driverInstanceGetter) {
        super();

        this.driverClass = (Class<D>) drivers.getClass().getComponentType();
        this.ownerClass = ownerClass;
        this.driverInstanceGetter = driverInstanceGetter;

        for (D driver : drivers) {
            String driverId = driver.getId();
            D previousDriver = driversById.put(driverId, driver);
            if (previousDriver != null) {
                throw new RuntimeException("Duplicate driverId: " + driverId);
            }
        }
    }

    @Override
    public final Class<D> getDriverClass() {
        return driverClass;
    }

    public final List<String> findAllIds() {
        return driversById.keySet().stream().sorted().collect(Collectors.toList());
    }

    public final List<D> findAll() {
        return driversById.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public final String validateDriverConfig(String driverId, String driverConfigText) {
        try {
            D driver = getDriverByIdNotNull(driverId);
            driver.parseDriverConfigText(driverConfigText);
            return null;
        } catch (InvalidDriverIdException e) {
            return "Driver not found: " + driverId;
        } catch (RuntimeException e) {
            LOG.debug("validateDriverConfig: driverId={}, driverConfigText={}", driverId, driverConfigText, e);
            return e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }

    @Nonnull
    protected final D getDriverByIdNotNull(String driverId) {
        D driver = driversById.get(driverId);
        if (driver == null) {
            throw new InvalidDriverIdException(driverId);
        }
        return driver;
    }

    protected final <R> R driverFunction(@Nonnull O owner, @Nonnull DriverDelegateFunction<O, D, DI, R> delegate) {
        DI di = driverInstanceGetter.apply(owner);
        D driver = getDriverByIdNotNull(di.getDriverId());
        return delegate.delegate(owner, driver, di);
    }

    protected final void driverCallable(@Nonnull O owner, @Nonnull DriverDelegateCallable<O, D, DI> delegate) {
        DI di = driverInstanceGetter.apply(owner);
        D driver = getDriverByIdNotNull(di.getDriverId());
        delegate.delegate(owner, driver, di);
    }

    @FunctionalInterface
    protected interface DriverDelegateFunction<O, D, DI, R> {
        R delegate(@Nonnull O owner, @Nonnull D driver, @Nonnull DI driverInstance);
    }

    @FunctionalInterface
    protected interface DriverDelegateCallable<O, D, DI> {
        void delegate(@Nonnull O owner, @Nonnull D driver, @Nonnull DI driverInstance);
    }

}
