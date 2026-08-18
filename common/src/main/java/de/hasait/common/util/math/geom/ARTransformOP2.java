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

/**
 * Angle-Radius-Transform.
 */
public class ARTransformOP2 implements TransformOP2 {

	private final Angle _angle;
	private final int _radius;
	private final boolean _clockwise;

	public ARTransformOP2(Angle pAngle, int pRadius, boolean pClockwise) {
		super();
		_angle = pAngle;
		_radius = pRadius;
		_clockwise = pClockwise;
	}

	@Override
	public Ray2DI add(Ray2DI pInput) {
		return pInput.curveAR(_angle, _radius, _clockwise);
	}

	public Angle getAngle() {
		return _angle;
	}

	public int getRadius() {
		return _radius;
	}

	@Override
	public Ray2DI sub(Ray2DI pInput) {
		return pInput.curveARreverse(_angle, _radius, _clockwise);
	}

}
