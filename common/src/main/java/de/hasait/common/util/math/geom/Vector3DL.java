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

import java.util.concurrent.ConcurrentLinkedQueue;

import de.hasait.common.util.AssertUtil;

public final class Vector3DL implements Comparable<Vector3DL> {

	private static final ConcurrentLinkedQueue<Vector3DL> POOL = new ConcurrentLinkedQueue<>();

	public static void free(Vector3DL pVector) {
		POOL.add(pVector);
	}

	public static Vector3DL obtain() {
		return obtain(0L, 0L, 0L);
	}

	public static Vector3DL obtain(long pX, long pY, long pZ) {
		final Vector3DL pooled = POOL.poll();
		if (pooled != null) {
			pooled._x = pX;
			pooled._y = pY;
			pooled._z = pZ;
			return pooled;
		}
		return new Vector3DL(pX, pY, pZ);
	}

	public static Vector3DL obtain(Vector3DL pTemplate) {
		return obtain(pTemplate._x, pTemplate._y, pTemplate._z);
	}

	public long _x, _y, _z;

	private Vector3DL(long pX, long pY, long pZ) {
		super();
		_x = pX;
		_y = pY;
		_z = pZ;
	}

	public Vector3DL absLocal() {
		_x = Math.abs(_x);
		_y = Math.abs(_y);
		_z = Math.abs(_z);
		return this;
	}

	public Vector3DL add(long pX, long pY, long pZ) {
		return obtain(_x + pX, _y + pY, _z + pZ);
	}

	public Vector3DL add(Vector3DI pVector) {
		return add(pVector._x, pVector._y, pVector._z);
	}

	public Vector3DL add(Vector3DL pVector) {
		return add(pVector._x, pVector._y, pVector._z);
	}

	public Vector3DL addLocal(long pX, long pY, long pZ) {
		_x += pX;
		_y += pY;
		_z += pZ;
		return this;
	}

	public Vector3DL addLocal(Vector3DI pVector) {
		return addLocal(pVector._x, pVector._y, pVector._z);
	}

	public Vector3DL addLocal(Vector3DL pVector) {
		return addLocal(pVector._x, pVector._y, pVector._z);
	}

	@Override
	public int compareTo(Vector3DL pOther) {
		int result;
		result = Long.compare(_x, pOther._x);
		if (result != 0) {
			return result;
		}
		result = Long.compare(_y, pOther._y);
		if (result != 0) {
			return result;
		}
		result = Long.compare(_z, pOther._z);
		return result;
	}

	public Vector3DL cpy() {
		return obtain(this);
	}

	public long distance(Vector3DL pOther) {
		return (long) Math.sqrt(distance2(pOther));
	}

	public long distance2(Vector3DL pOther) {
		final long dx = _x - pOther._x;
		final long dy = _y - pOther._y;
		final long dz = _z - pOther._z;
		return dx * dx + dy * dy + dz * dz;
	}

	public Vector3DL div(long pV) {
		return div(pV, pV, pV);
	}

	public Vector3DL div(long pX, long pY, long pZ) {
		return obtain(_x / pX, _y / pY, _z / pZ);
	}

	public Vector3DL div(Vector3DI pVector) {
		return div(pVector._x, pVector._y, pVector._z);
	}

	public Vector3DL div(Vector3DL pVector) {
		return div(pVector._x, pVector._y, pVector._z);
	}

	public long dot(long pX, long pY, long pZ) {
		return _x * pX + _y * pY + _z * pZ;
	}

	public long dot(Vector3DI pVector) {
		return dot(pVector._x, pVector._y, pVector._z);
	}

	public long dot(Vector3DL pVector) {
		return dot(pVector._x, pVector._y, pVector._z);
	}

	@Override
	public boolean equals(Object pOther) {
		if (pOther == this) {
			return true;
		}

		if (pOther == null) {
			return false;
		}

		if (getClass() != pOther.getClass()) {
			return false;
		}

		final Vector3DL other = (Vector3DL) pOther;
		return other._x == _x && other._y == _y && other._z == _z;
	}

	public long getX() {
		return _x;
	}

	public long getY() {
		return _y;
	}

	public long getZ() {
		return _z;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		long result = 1;
		result = prime * result + _x;
		result = prime * result + _y;
		result = prime * result + _z;
		return (int) (result ^ result >>> 32);
	}

	public long length() {
		return (long) Math.sqrt(length2());
	}

	public long length2() {
		return _x * _x + _y * _y + _z * _z;
	}

	public Vector3DL maxLocal(Vector3DL pOther) {
		_x = Math.max(_x, pOther._x);
		_y = Math.max(_y, pOther._y);
		_z = Math.max(_z, pOther._z);
		return this;
	}

	public Vector3DL minLocal(Vector3DL pOther) {
		_x = Math.min(_x, pOther._x);
		_y = Math.min(_y, pOther._y);
		_z = Math.min(_z, pOther._z);
		return this;
	}

	public Vector3DL mul(long pV) {
		return mul(pV, pV, pV);
	}

	public Vector3DL mul(long pX, long pY, long pZ) {
		return obtain(_x * pX, _y * pY, _z * pZ);
	}

	public Vector3DL mul(Vector3DI pVector) {
		return mul(pVector._x, pVector._y, pVector._z);
	}

	public Vector3DL mul(Vector3DL pVector) {
		return mul(pVector._x, pVector._y, pVector._z);
	}

	public Vector3DL mulLocal(long pV) {
		return mulLocal(pV, pV, pV);
	}

	public Vector3DL mulLocal(long pX, long pY, long pZ) {
		_x *= pX;
		_y *= pY;
		_z *= pZ;
		return this;
	}

	public Vector3DL mulLocal(Vector3DI pVector) {
		return mulLocal(pVector._x, pVector._y, pVector._z);
	}

	public Vector3DL mulLocal(Vector3DL pVector) {
		return mulLocal(pVector._x, pVector._y, pVector._z);
	}

	public Vector3DL negate() {
		return mul(-1L);
	}

	public Vector3DL setLocal(long pX, long pY, long pZ) {
		_x = pX;
		_y = pY;
		_z = pZ;
		return this;
	}

	public Vector3DL setLocal(Vector3DI pVector) {
		return setLocal(pVector._x, pVector._y, pVector._z);
	}

	public Vector3DL setLocal(Vector3DL pVector) {
		return setLocal(pVector._x, pVector._y, pVector._z);
	}

	public Vector3DL sub(long pX, long pY, long pZ) {
		return obtain(_x - pX, _y - pY, _z - pZ);
	}

	public Vector3DL sub(Vector3DI pVector) {
		return sub(pVector._x, pVector._y, pVector._z);
	}

	public Vector3DL sub(Vector3DL pVector) {
		return sub(pVector._x, pVector._y, pVector._z);
	}

	public Vector3DL subLocal(long pX, long pY, long pZ) {
		_x -= pX;
		_y -= pY;
		_z -= pZ;
		return this;
	}

	public Vector3DL subLocal(Vector3DI pVector) {
		return subLocal(pVector._x, pVector._y, pVector._z);
	}

	public Vector3DL subLocal(Vector3DL pVector) {
		return subLocal(pVector._x, pVector._y, pVector._z);
	}

	@Override
	public String toString() {
		return "V3DL[" + _x + "; " + _y + "; " + _z + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	public Vector3DI toVector3DI() {
		AssertUtil.lessOrEqual(Integer.MAX_VALUE, _x);
		AssertUtil.lessOrEqual(Integer.MAX_VALUE, _y);
		AssertUtil.lessOrEqual(Integer.MAX_VALUE, _z);
		AssertUtil.greaterOrEqual(Integer.MIN_VALUE, _x);
		AssertUtil.greaterOrEqual(Integer.MIN_VALUE, _y);
		AssertUtil.greaterOrEqual(Integer.MIN_VALUE, _z);

		return Vector3DI.obtain((int) _x, (int) _y, (int) _z);
	}

}
