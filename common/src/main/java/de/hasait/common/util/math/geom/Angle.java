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

import de.hasait.common.util.math.MathUtil;

/**
 * 0° is at east (1;0); 90° is at north (0;1); 180° is at west (-1;0) and 270° is at south (0;-1).
 */
public final class Angle {

	/**
	 * East (1;0).
	 */
	public static final Angle DEG000 = fromDegree(0);
	/**
	 * North (0;1).
	 */
	public static final Angle DEG090 = fromDegree(90);
	/**
	 * West (-1;0).
	 */
	public static final Angle DEG180 = fromDegree(180);
	/**
	 * South (0;-1).
	 */
	public static final Angle DEG270 = fromDegree(270);
	/**
	 * Northeast.
	 */
	public static final Angle DEG045 = fromDegree(45);

	public static final Angle DEG001 = fromDegree(1);

	private static final int FACTOR = 1024;

	public static Angle fromDegree(double pDegree) {
		return new Angle((int) (pDegree * FACTOR));
	}

	public static Angle fromDegree(int pDegree) {
		return new Angle(pDegree * FACTOR);
	}

	public static Angle fromRawValue(int pRawValue) {
		return new Angle(pRawValue);
	}

	/**
	 * The angle in degree multiplied with FACTOR.
	 */
	private final int _rawValue;

	private Angle(int pRawValue) {
		super();
		_rawValue = MathUtil.circularRange(360 * FACTOR, pRawValue);
	}

	public Angle add(Angle pAngle) {
		return new Angle(_rawValue + pAngle._rawValue);
	}

	public double cos() {
		return Math.cos(toRAD());
	}

	public int cosI(int pRadius) {
		return (int) (cos() * pRadius);
	}

	public long cosL(long pRadius) {
		return (long) (cos() * pRadius);
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

		final Angle other = (Angle) pOther;
		return other._rawValue == _rawValue;
	}

	public int getRawValue() {
		return _rawValue;
	}

	@Override
	public int hashCode() {
		return _rawValue;
	}

	/**
	 * Change sign and normalize, e.g. 45° => 315° (= -45°).
	 *
	 * @return Negated angle.
	 */
	public Angle negate() {
		return DEG000.sub(this);
	}

	/**
	 * The opposite angle, e.g. 0° => 180°, 45° => 225°.
	 *
	 * @return The opposite angle.
	 */
	public Angle opposite() {
		return add(DEG180);
	}

	public double sin() {
		return Math.sin(toRAD());
	}

	public int sinI(int pRadius) {
		return (int) (sin() * pRadius);
	}

	public long sinL(long pRadius) {
		return (long) (sin() * pRadius);
	}

	public Angle sub(Angle pAngle) {
		return new Angle(_rawValue - pAngle._rawValue);
	}

	public double toDEG() {
		return (double) _rawValue / FACTOR;
	}

	public double toRAD() {
		return Math.PI * 2.0 * toDEG() / 360;
	}

	@Override
	public String toString() {
		return toDEG() + "°"; //$NON-NLS-1$
	}

	public Vector2DI toVector2DI(int pLength) {
		return Vector2DI.obtain(cosI(pLength), sinI(pLength));
	}

	public Vector2DL toVector2DL(long pLength) {
		return Vector2DL.obtain(cosL(pLength), sinL(pLength));
	}

}
