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

import java.text.MessageFormat;
import java.util.Objects;


public class Ray2DI {

	private final int _startX;
	private final int _startY;
	private final Angle _angle;

	/**
	 * @param pStartX The x coordinate of the start point.
	 * @param pStartY The y coordinate of the start point.
	 * @param pAngle  The angle.
	 */
	public Ray2DI(int pStartX, int pStartY, Angle pAngle) {
		super();
		Objects.requireNonNull(pAngle, "pAngle");

		_startX = pStartX;
		_startY = pStartY;
		_angle = pAngle;
	}

	public Ray2DI addAngle(Angle pAngle) {
		return new Ray2DI(_startX, _startY, _angle.add(pAngle));
	}

	public Ray2DI angle(Angle pAngle) {
		return new Ray2DI(_startX, _startY, pAngle);
	}

	public Ray2DI curveAL(Angle pAngle, int pLength, boolean pClockwise) {
		final double angleAsRAD = pAngle.toRAD();
		return curveAR(pAngle, (int) (pLength / angleAsRAD), pClockwise);
	}

	public Ray2DI curveAR(Angle pAngle, int pRadius, boolean pClockwise) {
		final Angle alpha = _angle.add(pClockwise ? Angle.DEG270 : Angle.DEG090);
		final int cx = _startX + alpha.cosI(pRadius);
		final int cy = _startY + alpha.sinI(pRadius);
		final Angle beta = pClockwise ? alpha.opposite().sub(pAngle) : alpha.opposite().add(pAngle);
		final int startX = cx + beta.cosI(pRadius);
		final int startY = cy + beta.sinI(pRadius);
		final Angle angle = pClockwise ? _angle.sub(pAngle) : _angle.add(pAngle);
		return new Ray2DI(startX, startY, angle);
	}

	public Ray2DI curveARreverse(Angle pAngle, int pRadius, boolean pClockwise) {
		final Ray2DI result = curveAR(pAngle.opposite(), pRadius, !pClockwise);
		return result.opposite();
	}

	/**
	 * @return The angle.
	 */
	public final Angle getAngle() {
		return _angle;
	}

	/**
	 * @return The x coordinate of the start point.
	 */
	public final int getStartX() {
		return _startX;
	}

	/**
	 * @return The y coordinate of the start point.
	 */
	public final int getStartY() {
		return _startY;
	}

	public Ray2DI opposite() {
		return new Ray2DI(_startX, _startY, _angle.opposite());
	}

	public Ray2DI subAngle(Angle pAngle) {
		return new Ray2DI(_startX, _startY, _angle.sub(pAngle));
	}

	@Override
	public String toString() {
		return MessageFormat.format("({0}; {1} -> {2})", _startX, _startY, _angle); //$NON-NLS-1$
	}

	public Ray2DI translate(int pX, int pY) {
		return new Ray2DI(_startX + pX, _startY + pY, _angle);
	}

}
