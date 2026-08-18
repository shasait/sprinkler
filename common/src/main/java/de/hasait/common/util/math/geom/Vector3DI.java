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

public final class Vector3DI implements Comparable<Vector3DI> {

	private static final ConcurrentLinkedQueue<Vector3DI> POOL = new ConcurrentLinkedQueue<>();

	public static void free(Vector3DI pVector) {
		POOL.add(pVector);
	}

	public static Vector3DI obtain() {
		return obtain(0, 0, 0);
	}

	public static Vector3DI obtain(int pX, int pY, int pZ) {
		final Vector3DI pooled = POOL.poll();
		if (pooled != null) {
			pooled._x = pX;
			pooled._y = pY;
			pooled._z = pZ;
			return pooled;
		}
		return new Vector3DI(pX, pY, pZ);
	}

	public static Vector3DI obtain(Vector3DI pTemplate) {
		return obtain(pTemplate._x, pTemplate._y, pTemplate._z);
	}

	public int _x, _y, _z;

	private Vector3DI(int pX, int pY, int pZ) {
		super();
		_x = pX;
		_y = pY;
		_z = pZ;
	}

	public Vector3DI absLocal() {
		_x = Math.abs(_x);
		_y = Math.abs(_y);
		_z = Math.abs(_z);
		return this;
	}

	public Vector3DI add(int pX, int pY, int pZ) {
		return obtain(_x + pX, _y + pY, _z + pZ);
	}

	public Vector3DI add(Vector3DI pVector) {
		return add(pVector._x, pVector._y, pVector._z);
	}

	public Vector3DI addLocal(int pX, int pY, int pZ) {
		_x += pX;
		_y += pY;
		_z += pZ;
		return this;
	}

	public Vector3DI addLocal(Vector3DI pVector) {
		return addLocal(pVector._x, pVector._y, pVector._z);
	}

	@Override
	public int compareTo(Vector3DI pOther) {
		int result;
		result = Integer.compare(_x, pOther._x);
		if (result != 0) {
			return result;
		}
		result = Integer.compare(_y, pOther._y);
		if (result != 0) {
			return result;
		}
		result = Integer.compare(_z, pOther._z);
		return result;
	}

	public Vector3DI cpy() {
		return obtain(this);
	}

	public int distance(Vector3DI pOther) {
		return (int) Math.sqrt(distance2(pOther));
	}

	public int distance2(Vector3DI pOther) {
		final int dx = _x - pOther._x;
		final int dy = _y - pOther._y;
		final int dz = _z - pOther._z;
		return dx * dx + dy * dy + dz * dz;
	}

	public Vector3DI div(int pV) {
		return div(pV, pV, pV);
	}

	public Vector3DI div(int pX, int pY, int pZ) {
		return obtain(_x / pX, _y / pY, _z / pZ);
	}

	public Vector3DI div(Vector3DI pVector) {
		return div(pVector._x, pVector._y, pVector._z);
	}

	public int dot(int pX, int pY, int pZ) {
		return _x * pX + _y * pY + _z * pZ;
	}

	public int dot(Vector3DI pVector) {
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

		final Vector3DI other = (Vector3DI) pOther;
		return other._x == _x && other._y == _y && other._z == _z;
	}

	public int getX() {
		return _x;
	}

	public int getY() {
		return _y;
	}

	public int getZ() {
		return _z;
	}

	public boolean hasNegativeCoordinate() {
		return _x < 0 || _y < 0 || _z < 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _x;
		result = prime * result + _y;
		result = prime * result + _z;
		return result;
	}

	public int length() {
		return (int) Math.sqrt(length2());
	}

	public int length2() {
		return _x * _x + _y * _y + _z * _z;
	}

	public Vector3DI maxLocal(Vector3DI pOther) {
		_x = Math.max(_x, pOther._x);
		_y = Math.max(_y, pOther._y);
		_z = Math.max(_z, pOther._z);
		return this;
	}

	public Vector3DI minLocal(Vector3DI pOther) {
		_x = Math.min(_x, pOther._x);
		_y = Math.min(_y, pOther._y);
		_z = Math.min(_z, pOther._z);
		return this;
	}

	public Vector3DI mul(int pV) {
		return mul(pV, pV, pV);
	}

	public Vector3DI mul(int pX, int pY, int pZ) {
		return obtain(_x * pX, _y * pY, _z * pZ);
	}

	public Vector3DI mul(Vector3DI pVector) {
		return mul(pVector._x, pVector._y, pVector._z);
	}

	public Vector3DI mulLocal(int pV) {
		return mulLocal(pV, pV, pV);
	}

	public Vector3DI mulLocal(int pX, int pY, int pZ) {
		_x *= pX;
		_y *= pY;
		_z *= pZ;
		return this;
	}

	public Vector3DI mulLocal(Vector3DI pVector) {
		return mulLocal(pVector._x, pVector._y, pVector._z);
	}

	public Vector3DI negate() {
		return mul(-1);
	}

	public Vector3DI setLocal(int pX, int pY, int pZ) {
		_x = pX;
		_y = pY;
		_z = pZ;
		return this;
	}

	public Vector3DI setLocal(Vector3DI pVector) {
		return setLocal(pVector._x, pVector._y, pVector._z);
	}

	public Vector3DI sub(int pX, int pY, int pZ) {
		return obtain(_x - pX, _y - pY, _z - pZ);
	}

	public Vector3DI sub(Vector3DI pVector) {
		return sub(pVector._x, pVector._y, pVector._z);
	}

	public Vector3DI subLocal(int pX, int pY, int pZ) {
		_x -= pX;
		_y -= pY;
		_z -= pZ;
		return this;
	}

	public Vector3DI subLocal(Vector3DI pVector) {
		return subLocal(pVector._x, pVector._y, pVector._z);
	}

	@Override
	public String toString() {
		return "V3DI[" + _x + "; " + _y + "; " + _z + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	public Vector3DL toVector3DL() {
		return Vector3DL.obtain(_x, _y, _z);
	}

}
