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

public final class Vector2DL implements Comparable<Vector2DL> {

	private static final ConcurrentLinkedQueue<Vector2DL> POOL = new ConcurrentLinkedQueue<>();

	public static void free(Vector2DL pVector) {
		POOL.add(pVector);
	}

	public static Vector2DL obtain() {
		return obtain(0L, 0L);
	}

	public static Vector2DL obtain(long pX, long pY) {
		final Vector2DL pooled = POOL.poll();
		if (pooled != null) {
			pooled._x = pX;
			pooled._y = pY;
			return pooled;
		}
		return new Vector2DL(pX, pY);
	}

	public static Vector2DL obtain(Vector2DL pTemplate) {
		return obtain(pTemplate._x, pTemplate._y);
	}

	public long _x, _y;

	private Vector2DL(long pX, long pY) {
		super();
		_x = pX;
		_y = pY;
	}

	public Vector2DL absLocal() {
		_x = Math.abs(_x);
		_y = Math.abs(_y);
		return this;
	}

	public Vector2DL add(long pX, long pY) {
		return obtain(_x + pX, _y + pY);
	}

	public Vector2DL add(Vector2DI pVector) {
		return obtain(_x + pVector.x, _y + pVector.y);
	}

	public Vector2DL add(Vector2DL pVector) {
		return obtain(_x + pVector._x, _y + pVector._y);
	}

	public Vector2DL addLocal(long pX, long pY) {
		_x += pX;
		_y += pY;
		return this;
	}

	public Vector2DL addLocal(Vector2DI pVector) {
		return addLocal(pVector.x, pVector.y);
	}

	public Vector2DL addLocal(Vector2DL pVector) {
		return addLocal(pVector._x, pVector._y);
	}

	@Override
	public int compareTo(Vector2DL pOther) {
		int result;
		result = Long.compare(_x, pOther._x);
		if (result != 0) {
			return result;
		}
		result = Long.compare(_y, pOther._y);
		return result;
	}

	public Vector2DL cpy() {
		return obtain(this);
	}

	public long distance(Vector2DL pOther) {
		return (long) Math.sqrt(distance2(pOther));
	}

	public long distance2(Vector2DL pOther) {
		final long dx = _x - pOther._x;
		final long dy = _y - pOther._y;
		return dx * dx + dy * dy;
	}

	public Vector2DL div(long pV) {
		return div(pV, pV);
	}

	public Vector2DL div(long pX, long pY) {
		return obtain(_x / pX, _y / pY);
	}

	public Vector2DL div(Vector2DI pVector) {
		return div(pVector.x, pVector.y);
	}

	public Vector2DL div(Vector2DL pVector) {
		return div(pVector._x, pVector._y);
	}

	public long dot(long pX, long pY) {
		return _x * pX + _y * pY;
	}

	public long dot(Vector2DI pVector) {
		return dot(pVector.x, pVector.y);
	}

	public long dot(Vector2DL pVector) {
		return dot(pVector._x, pVector._y);
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

		final Vector2DL other = (Vector2DL) pOther;
		return other._x == _x && other._y == _y;
	}

	public long getX() {
		return _x;
	}

	public long getY() {
		return _y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		long result = 1;
		result = prime * result + _x;
		result = prime * result + _y;
		return (int) (result ^ result >>> 32);
	}

	public long length() {
		return (long) Math.sqrt(length2());
	}

	public long length2() {
		return _x * _x + _y * _y;
	}

	public Vector2DL maxLocal(Vector2DL pOther) {
		_x = Math.max(_x, pOther._x);
		_y = Math.max(_y, pOther._y);
		return this;
	}

	public Vector2DL minLocal(Vector2DL pOther) {
		_x = Math.min(_x, pOther._x);
		_y = Math.min(_y, pOther._y);
		return this;
	}

	public Vector2DL mul(long pV) {
		return mul(pV, pV);
	}

	public Vector2DL mul(long pX, long pY) {
		return obtain(_x * pX, _y * pY);
	}

	public Vector2DL mul(Vector2DI pVector) {
		return mul(pVector.x, pVector.y);
	}

	public Vector2DL mul(Vector2DL pVector) {
		return mul(pVector._x, pVector._y);
	}

	public Vector2DL mulLocal(long pV) {
		return mulLocal(pV, pV);
	}

	public Vector2DL mulLocal(long pX, long pY) {
		_x *= pX;
		_y *= pY;
		return this;
	}

	public Vector2DL mulLocal(Vector2DI pVector) {
		return mulLocal(pVector.x, pVector.y);
	}

	public Vector2DL negate() {
		return mul(-1L);
	}

	/**
	 * @param pAngle The angle.
	 * @param pClockwise Clockwise is clockwise if Y is up; if Y is down it is vice-versa.
	 * @return New vector.
	 */
	public Vector2DL rot(Angle pAngle, boolean pClockwise) {
		return cpy().rotLocal(pAngle, pClockwise);
	}

	/**
	 * @param pAngle The angle.
	 * @param pClockwise Clockwise is clockwise if Y is up; if Y is down it is vice-versa.
	 * @return <code>this</code>
	 */
	public Vector2DL rotLocal(Angle pAngle, boolean pClockwise) {
		final Angle angle;
		if (pClockwise) {
			angle = pAngle.negate();
		} else {
			angle = pAngle;
		}
		final long x = angle.cosL(_x) - angle.sinL(_y);
		final long y = angle.sinL(_x) + angle.cosL(_y);
		_x = x;
		_y = y;
		return this;
	}

	public Vector2DL setLocal(long pX, long pY) {
		_x = pX;
		_y = pY;
		return this;
	}

	public Vector2DL setLocal(Vector2DI pVector) {
		return setLocal(pVector.x, pVector.y);
	}

	public Vector2DL setLocal(Vector2DL pVector) {
		return setLocal(pVector._x, pVector._y);
	}

	public Vector2DL sub(long pX, long pY) {
		return obtain(_x - pX, _y - pY);
	}

	public Vector2DL sub(Vector2DI pVector) {
		return sub(pVector.x, pVector.y);
	}

	public Vector2DL sub(Vector2DL pVector) {
		return sub(pVector._x, pVector._y);
	}

	public Vector2DL subLocal(long pX, long pY) {
		_x -= pX;
		_y -= pY;
		return this;
	}

	public Vector2DL subLocal(Vector2DI pVector) {
		return subLocal(pVector.x, pVector.y);
	}

	public Vector2DL subLocal(Vector2DL pVector) {
		return subLocal(pVector._x, pVector._y);
	}

	@Override
	public String toString() {
		return "V2DL[" + _x + "; " + _y + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public Vector2DI toVector2DI() {
		AssertUtil.lessOrEqual(Integer.MAX_VALUE, _x);
		AssertUtil.lessOrEqual(Integer.MAX_VALUE, _y);
		AssertUtil.greaterOrEqual(Integer.MIN_VALUE, _x);
		AssertUtil.greaterOrEqual(Integer.MIN_VALUE, _y);

		return Vector2DI.obtain((int) _x, (int) _y);
	}

}
