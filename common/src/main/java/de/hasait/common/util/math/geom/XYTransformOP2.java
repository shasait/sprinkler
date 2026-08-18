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

public class XYTransformOP2 implements TransformOP2 {

	private final int _x;
	private final int _y;

	public XYTransformOP2(int pX, int pY) {
		super();
		_x = pX;
		_y = pY;
	}

	@Override
	public Ray2DI add(Ray2DI pInput) {
		return pInput.translate(_x, _y);
	}

	public int getX() {
		return _x;
	}

	public int getY() {
		return _y;
	}

	@Override
	public Ray2DI sub(Ray2DI pInput) {
		return pInput.translate(-_x, -_y);
	}

}
