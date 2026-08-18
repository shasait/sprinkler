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

package de.hasait.common.util.math.geom;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 *
 */
public final class DynamicArray2DI<T> {

	private static Space2DI createSpace(int pInitialSizeX, int pInitialSizeY) {
		return new Space2DI(0, 0, pInitialSizeX, pInitialSizeY);
	}

	private final AtomicReference<Space2DI> _space;
	private final ConcurrentHashMap<Vector2DI, T> _values = new ConcurrentHashMap<>();

	public DynamicArray2DI() {
		this(0, 0);
	}

	public DynamicArray2DI(int pInitialSizeX, int pInitialSizeY) {
		super();

		_space = new AtomicReference<>(createSpace(pInitialSizeX, pInitialSizeY));
	}

	public void clear(int pInitialSizeX, int pInitialSizeY) {
		_space.set(createSpace(pInitialSizeX, pInitialSizeY));
		_values.clear();
	}

	public T get(int pX, int pY) {
		final Space2DI space = _space.get();
		if (!space.isValid(pX, pY)) {
			return null;
		}
		return _values.get(Vector2DI.obtain(pX, pY));
	}

	public Vector2DI getSize() {
		return _space.get().getSize();
	}

	public Space2DI getSpace() {
		return _space.get();
	}

	public Vector2DI getUpperLeft() {
		return _space.get().getMinInclusive();
	}

	public void set(int pX, int pY, T pValue) {
		expandSpace(pX, pY);
		_values.put(Vector2DI.obtain(pX, pY), pValue);
	}

	public T setIfAbsent(int pX, int pY, Supplier<T> pValueFactory) {
		expandSpace(pX, pY);
		return _values.computeIfAbsent(Vector2DI.obtain(pX, pY), pUnused -> pValueFactory.get());
	}

	private void expandSpace(int pX, int pY) {
		while (true) {
			final Space2DI space = _space.get();
			if (space.isValid(pX, pY)) {
				break;
			}
			final Space2DI newSpace = space.newContaining(pX, pY);
			if (_space.compareAndSet(space, newSpace)) {
				break;
			}
		}
	}

}
