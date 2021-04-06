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

package de.hasait.sprinkler.service.gpio.base;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;

import org.jetbrains.annotations.NotNull;

import de.hasait.sprinkler.service.gpio.GpioProvider;

/**
 *
 */
public abstract class AbstractGpioProvider implements GpioProvider {

    protected final ConcurrentHashMap<Integer, Boolean> pins = new ConcurrentHashMap<>();
    private final String providerId;

    protected AbstractGpioProvider(String providerId) {
        this.providerId = providerId;
    }

    @NotNull
    @Override
    public final String getProviderId() {
        return providerId;
    }

    @Override
    public final boolean isActive(int address) {
        return pins.computeIfAbsent(address, ignored -> initPin(address));
    }

    @Override
    public final void setActive(int address, boolean active) {
        boolean currentActive = isActive(address);
        if (currentActive != active) {
            if (pins.replace(address, currentActive, active)) {
                changePin(address, active);
            }
        }
    }

    protected abstract void changePin(int address, boolean active);

    protected abstract boolean initPin(int address);

    @PreDestroy
    protected void shutdown() {
        pins.clear();
    }

}
