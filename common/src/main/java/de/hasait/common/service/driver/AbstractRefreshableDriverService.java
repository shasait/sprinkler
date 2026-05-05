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

import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hasait.common.service.RefreshableService;

public class AbstractRefreshableDriverService<D extends RefreshableDriver<O, ?>, O> extends AbstractDriverService<D, O, RefreshableDriverInstance> implements RefreshableService<O> {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final Supplier<Iterable<O>> findAll;

    public AbstractRefreshableDriverService(D[] drivers, Class<O> ownerClass, Function<O, RefreshableDriverInstance> refreshableDriverInstanceGetter, Supplier<Iterable<O>> findAll) {
        super(drivers, ownerClass, refreshableDriverInstanceGetter);

        this.findAll = findAll;
    }

    @Override
    public final void refreshAll() {
        Iterable<O> hdiList = findAll.get();
        for (O o : hdiList) {
            try {
                refresh(o);
            } catch (RuntimeException e) {
                log.warn("Refresh failed for {}", o, e);
            }
        }
    }

    @Override
    public final void refresh(O owner) {
        driverCallable(owner, (ignored, driver, di) -> driver.refresh(owner));
    }

}
