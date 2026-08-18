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

package de.hasait.common.util.listener;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hasait.common.util.thread.Async;

/**
 *
 */
public class ChangeListenerSupportBase<S, V> implements ChangeListenerSupport<S, V> {

    private static final Logger LOG = LoggerFactory.getLogger(ChangeListenerSupportBase.class);

    // private final Map<ChangeListener<? super S, ? super V>, Boolean> listeners = new HashMap<>();
    // private final Map<ChangeListener<? super S, ? super V>, Boolean> listeners = new ConcurrentReferenceHashMap<>(0, ConcurrentReferenceHashMap.ReferenceType.WEAK);
    private final Map<ChangeListener<? super S, ? super V>, Boolean> listeners = Collections.synchronizedMap(new WeakHashMap<>());

    @Override
    public final void addChangeListener(@Nonnull ChangeListener<? super S, ? super V> listener) {
        Objects.requireNonNull(listener, "listener");

        listeners.put(listener, true);
    }

    @Override
    public final void removeChangeListener(@Nonnull ChangeListener<? super S, ? super V> listener) {
        Objects.requireNonNull(listener, "listener");

        listeners.remove(listener);
    }

    protected void fireChanged(@Nonnull S source, V value) {
        Objects.requireNonNull(source, "source");

        for (ChangeListener<? super S, ? super V> listener : listeners.keySet()) {
            try {
                listener.onChange(source, value);
            } catch (RuntimeException pE) {
                LOG.error("Listener failed: {}", listener, pE);
            }
        }
    }

    protected void fireChangedAsync(@Nonnull S source, V value) {
        Objects.requireNonNull(source, "source");

        for (ChangeListener<? super S, ? super V> listener : listeners.keySet()) {
            Async.execute(() -> {
                try {
                    listener.onChange(source, value);
                } catch (RuntimeException pE) {
                    LOG.error("Listener failed: {}", listener, pE);
                }
            });
        }
    }

}
