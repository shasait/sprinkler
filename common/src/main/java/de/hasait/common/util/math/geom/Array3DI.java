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

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Objects;

import de.hasait.common.util.math.RangeMode;

/**
 *
 */
public class Array3DI<T> implements Iterable<T> {

	private final Class<T> _class;

	private final Space3DI _coord;

	private final T[][][] _data;

	public Array3DI(Class<T> pClass, int pSize) {
		this(pClass, pSize, pSize, pSize);
	}

	public Array3DI(Class<T> pClass, int pSizeX, int pSizeY, int pSizeZ) {
		super();
		Objects.requireNonNull(pClass, "pClass");

		_class = pClass;
		_coord = new Space3DI(0, 0, 0, pSizeX, pSizeY, pSizeZ);
		final Vector3DI size = _coord.getSize();

		_data = (T[][][]) Array.newInstance(_class, size._x, size._y, size._z);
	}

	public Array3DI(Class<T> pClass, Vector3DI pSize) {
		this(pClass, pSize._x, pSize._y, pSize._z);
	}

	public void assertValid(int pX, int pY, int pZ) {
		_coord.assertValid(pX, pY, pZ);
	}

	public Iterator<Vector3DI> coordIterator() {
		return _coord.iterator();
	}

	public Vector3DI getCoord(int pX, int pY, int pZ) {
		return _coord.get(pX, pY, pZ);
	}

	public Vector3DI getCoord(int pX, int pY, int pZ, RangeMode pModeX, RangeMode pModeY, RangeMode pModeZ) {
		return _coord.get(pX, pY, pZ, pModeX, pModeY, pModeZ);
	}

	public Vector3DI getCoord(Vector3DI pCoord) {
		return _coord.get(pCoord);
	}

	public Vector3DI getCoord(Vector3DI pCoord, int pTranslationX, int pTranslationY, int pTranslationZ, RangeMode pModeX, RangeMode pModeY, RangeMode pModeZ) {
		return _coord.get(pCoord, pTranslationX, pTranslationY, pTranslationZ, pModeX, pModeY, pModeZ);
	}

	public Vector3DI getCoord(Vector3DI pCoord, RangeMode pModeX, RangeMode pModeY, RangeMode pModeZ) {
		return _coord.get(pCoord, pModeX, pModeY, pModeZ);
	}

	public Vector3DI getCoord(Vector3DI pCoord, Vector3DI pTranslation) {
		return _coord.get(pCoord, pTranslation);
	}

	public Vector3DI getCoord(Vector3DI pCoord, Vector3DI pTranslation, RangeMode pModeX, RangeMode pModeY, RangeMode pModeZ) {
		return _coord.get(pCoord, pTranslation, pModeX, pModeY, pModeZ);
	}

	public Vector3DI getSize() {
		return _coord.getSize();
	}

	public T getValue(int pX, int pY, int pZ) {
		_coord.assertValid(pX, pY, pZ);
		return getValueInternal(pX, pY, pZ);
	}

	public T getValue(Vector3DI pCoord) {
		return getValue(pCoord._x, pCoord._y, pCoord._z);
	}

	public T getValue(Vector3DI pCoord, Vector3DI pTranslation) {
		return getValue(_coord.get(pCoord, pTranslation));
	}

	public T getWithOutOfBounds(int pX, int pY, int pZ, T pOutOfBoundsResult) {
		if (!_coord.isValid(pX, pY, pZ)) {
			return pOutOfBoundsResult;
		}
		return getValueInternal(pX, pY, pZ);
	}

	public T getWithOutOfBounds(Vector3DI pCoord, T pOutOfBoundsResult) {
		return getWithOutOfBounds(pCoord._x, pCoord._y, pCoord._z, pOutOfBoundsResult);
	}

	public T getWithOutOfBounds(Vector3DI pCoord, Vector3DI pTranslation, T pOutOfBoundsResult) {
		return getWithOutOfBounds(pCoord._x + pTranslation._x, pCoord._y + pTranslation._y, pCoord._z + pTranslation._z,
								  pOutOfBoundsResult
		);
	}

	public boolean isValid(int pX, int pY, int pZ) {
		return _coord.isValid(pX, pY, pZ);
	}

	public boolean isValid(Vector3DI pCoord) {
		return _coord.isValid(pCoord);
	}

	@Override
	public Iterator<T> iterator() {
		return new DataIteratorImpl(_coord.iterator());
	}

	public void setValue(int pX, int pY, int pZ, T pValue) {
		_coord.assertValid(pX, pY, pZ);
		setValueInternal(pX, pY, pZ, pValue);
	}

	public void setValue(Vector3DI pCoord, T pValue) {
		setValue(pCoord._x, pCoord._y, pCoord._z, pValue);
	}

	public void setValue(Vector3DI pCoord, Vector3DI pTranslation, T pValue) {
		setValue(_coord.get(pCoord, pTranslation), pValue);
	}

	public long size() {
		return _coord.size();
	}

	private T getValueInternal(int pX, int pY, int pZ) {
		return _data[pX][pY][pZ];
	}

	private void setValueInternal(int pX, int pY, int pZ, T pValue) {
		_data[pX][pY][pZ] = pValue;
	}

	private class DataIteratorImpl implements Iterator<T> {

		private final Iterator<Vector3DI> _coordI;

		public DataIteratorImpl(Iterator<Vector3DI> pCoordI) {
			super();

			_coordI = pCoordI;
		}

		@Override
		public boolean hasNext() {
			return _coordI.hasNext();
		}

		@Override
		public T next() {
			return getValue(_coordI.next());
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
