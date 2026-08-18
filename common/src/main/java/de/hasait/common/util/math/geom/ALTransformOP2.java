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
 * Angle-Length-Transform.
 * <p>
 * Curved line to reach angle within specified length.
 */
public class ALTransformOP2 implements TransformOP2 {

	private final Angle _angle;
	private final int _length;
	private final boolean _clockwise;

	public ALTransformOP2(Angle pAngle, int pLength, boolean pClockwise) {
		super();
		_angle = pAngle;
		_length = pLength;
		_clockwise = pClockwise;
	}

	@Override
	public Ray2DI add(Ray2DI pInput) {
		return pInput.curveAL(_angle, _length, _clockwise);
	}

	public Angle getAngle() {
		return _angle;
	}

	public int getLength() {
		return _length;
	}

	@Override
	public Ray2DI sub(Ray2DI pInput) {
		return pInput.curveAL(_angle.negate(), _length, _clockwise);
	}

}
