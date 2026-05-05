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

import java.time.LocalDateTime;
import java.util.function.Function;

import jakarta.annotation.Nonnull;

import de.hasait.common.util.AssertUtil;

public abstract class AbstractRefreshableDriver<O, C> extends AbstractDriver<C> implements RefreshableDriver<O, C> {

    private final Function<O, RefreshableDriverInstance> refreshableDriverInstanceGetter;

    protected AbstractRefreshableDriver(String id, String description, String disabledReason, Function<O, RefreshableDriverInstance> refreshableDriverInstanceGetter) {
        super(id, description, disabledReason);
        this.refreshableDriverInstanceGetter = refreshableDriverInstanceGetter;
    }

    @Override
    public final void refresh(@Nonnull O owner) {
        C driverConfig = determineDriverConfig(owner);
        refresh(owner, driverConfig);
        refreshableDriverInstanceGetter.apply(owner).setDriverLastRefresh(LocalDateTime.now());
    }

    protected final C determineDriverConfig(@Nonnull O owner) {
        var rdi = refreshableDriverInstanceGetter.apply(owner);
        AssertUtil.equals(getId(), rdi.getDriverId(), "Foreign " + RefreshableDriverInstance.class.getSimpleName());
        return parseDriverConfigText(rdi.getDriverConfig());
    }

    protected abstract void refresh(@Nonnull O owner, @Nonnull C driverConfig);

}
