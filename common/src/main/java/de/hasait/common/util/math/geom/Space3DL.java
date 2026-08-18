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

import java.util.Iterator;
import java.util.NoSuchElementException;

import de.hasait.common.util.math.MathUtil;
import de.hasait.common.util.math.RangeMode;
import de.hasait.common.util.AssertUtil;
import de.hasait.common.util.AssertionException;

/**
 *
 */
public class Space3DL implements Iterable<Vector3DL> {

	private final Vector3DL _minInclusive, _maxExclusive, _size;

	public Space3DL(long pMinInclusiveX, long pMinInclusiveY, long pMinInclusiveZ, long pMaxExclusiveX, long pMaxExclusiveY, long pMaxExclusiveZ) {
		super();

		AssertUtil.greater(pMinInclusiveX, pMaxExclusiveX);
		AssertUtil.greater(pMinInclusiveY, pMaxExclusiveY);
		AssertUtil.greater(pMinInclusiveZ, pMaxExclusiveZ);

		_minInclusive = Vector3DL.obtain(pMinInclusiveX, pMinInclusiveY, pMinInclusiveZ);
		_maxExclusive = Vector3DL.obtain(pMaxExclusiveX, pMaxExclusiveY, pMaxExclusiveZ);
		_size = _maxExclusive.sub(_minInclusive);
	}

	public final Vector3DL get(long pX, long pY, long pZ) {
		assertValid(pX, pY, pZ);
		return Vector3DL.obtain(pX, pY, pZ);
	}

	public final Vector3DL get(long pX, long pY, long pZ, RangeMode pModeX, RangeMode pModeY, RangeMode pModeZ) {
		final long x = MathUtil.range(_minInclusive._x, _maxExclusive._x, pX, pModeX);
		final long y = MathUtil.range(_minInclusive._y, _maxExclusive._y, pY, pModeY);
		final long z = MathUtil.range(_minInclusive._z, _maxExclusive._z, pZ, pModeZ);
		return Vector3DL.obtain(x, y, z);
	}

	public final Vector3DL get(Vector3DL pCoord) {
		assertValid(pCoord._x, pCoord._y, pCoord._z);
		return pCoord;
	}

	public final Vector3DL get(Vector3DL pCoord, long pTranslationX, long pTranslationY, long pTranslationZ, RangeMode pModeX, RangeMode pModeY, RangeMode pModeZ) {
		return get(pCoord._x + pTranslationX, pCoord._y + pTranslationY, pCoord._z + pTranslationZ, pModeX, pModeY, pModeZ);
	}

	public final Vector3DL get(Vector3DL pCoord, RangeMode pModeX, RangeMode pModeY, RangeMode pModeZ) {
		return get(pCoord._x, pCoord._y, pCoord._z, pModeX, pModeY, pModeZ);
	}

	public final Vector3DL get(Vector3DL pCoord, Vector3DL pTranslation) {
		return get(pCoord._x + pTranslation._x, pCoord._y + pTranslation._y, pCoord._z + pTranslation._z);
	}

	public final Vector3DL get(Vector3DL pCoord, Vector3DL pTranslation, RangeMode pModeX, RangeMode pModeY, RangeMode pModeZ) {
		return get(pCoord._x + pTranslation._x, pCoord._y + pTranslation._y, pCoord._z + pTranslation._z, pModeX, pModeY, pModeZ);
	}

	public final Vector3DL getMaxExclusive() {
		return _maxExclusive;
	}

	public final Vector3DL getMinInclusive() {
		return _minInclusive;
	}

	public final Vector3DL getSize() {
		return _size;
	}

	public final boolean isValid(long pX, long pY, long pZ) {
		return pX >= _minInclusive._x
				&& pX < _maxExclusive._x
				&& pY >= _minInclusive._y
				&& pY < _maxExclusive._y
				&& pZ >= _minInclusive._z
				&& pZ < _maxExclusive._z;
	}

	public final boolean isValid(Vector3DL pCoord) {
		return isValid(pCoord._x, pCoord._y, pCoord._z);
	}

	@Override
	public final Iterator<Vector3DL> iterator() {
		return new CoordIteratorImpl();
	}

	public final long size() {
		return _size._x * _size._y * _size._z;
	}

	public final Space3DL[] splitOnLongAxis() {
		if (_size._x <= 1 && _size._y <= 1 && _size._z <= 1) {
			return null;
		}

		if (_size._x >= _size._y && _size._x >= _size._z) {
			final long split = _size._x / 2;
			return new Space3DL[]{
					new Space3DL(_minInclusive._x, _minInclusive._y, _minInclusive._z, _minInclusive._x + split, _maxExclusive._y,
								 _maxExclusive._z
					),
					new Space3DL(_minInclusive._x + split, _minInclusive._y, _minInclusive._z, _maxExclusive._x, _maxExclusive._y,
								 _maxExclusive._z
					)
			};
		}

		if (_size._y >= _size._z) {
			final long split = _size._y / 2;
			return new Space3DL[]{
					new Space3DL(_minInclusive._x, _minInclusive._y, _minInclusive._z, _maxExclusive._x, _minInclusive._y + split,
								 _maxExclusive._z
					),
					new Space3DL(_minInclusive._x, _minInclusive._y + split, _minInclusive._z, _maxExclusive._x, _maxExclusive._y,
								 _maxExclusive._z
					)
			};
		}

		final long split = _size._z / 2;
		return new Space3DL[]{
				new Space3DL(_minInclusive._x, _minInclusive._y, _minInclusive._z, _maxExclusive._x, _maxExclusive._y,
							 _minInclusive._z + split
				),
				new Space3DL(_minInclusive._x, _minInclusive._y, _minInclusive._z + split, _maxExclusive._x, _maxExclusive._y,
							 _maxExclusive._z
				)
		};
	}

	@Override
	public String toString() {
		return "S3DL[" + _minInclusive + "; " + _maxExclusive + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
	}

	private void assertValid(long pX, long pY, long pZ) {
		if (!isValid(pX, pY, pZ)) {
			AssertUtil.fail("Not: {3} <= ({0}, {1}, {2}) < {4}", pX, pY, pZ, _minInclusive, _maxExclusive); //$NON-NLS-1$
		}
	}

	private class CoordIteratorImpl implements Iterator<Vector3DL> {

		private long _x = _minInclusive._x;
		private long _y = _minInclusive._y;
		private long _z = _minInclusive._z;

		@Override
		public boolean hasNext() {
			return isValid(_x, _y, _z);
		}

		@Override
		public Vector3DL next() {
			final Vector3DL result = Vector3DL.obtain(_x, _y, _z);
			try {
				assertValid(_x, _y, _z);
			} catch (AssertionException pE) {
				throw new NoSuchElementException(pE.getMessage());
			}
			if (_x < _maxExclusive._x - 1) {
				_x++;
			} else if (_y < _maxExclusive._y - 1) {
				_x = _minInclusive._x;
				_y++;
			} else {
				_x = _minInclusive._x;
				_y = _minInclusive._y;
				_z++;
			}
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
