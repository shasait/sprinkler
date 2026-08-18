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

package de.hasait.common.util.math;

import de.hasait.common.util.math.geom.Angle;
import de.hasait.common.util.AssertUtil;

/**
 *
 */
public final class InterpolationUtil {

	public static int cosine(int pA, int pB, int pPart, int pAll) {
		AssertUtil.isTrue(pAll > 0);
		AssertUtil.isTrue(pPart >= 0);
		AssertUtil.isTrue(pPart <= pAll);

		final int cos = Angle.fromDegree(180 * pPart / pAll).cosI(1000);
		final int f = (1000 - cos) / 2;
		return linear(pA, pB, f, 1000);
	}

	public static int linear(int pA, int pB, int pPart, int pAll) {
		AssertUtil.isTrue(pAll > 0);
		AssertUtil.isTrue(pPart >= 0);
		AssertUtil.isTrue(pPart <= pAll);

		final long v = (long) pA * (pAll - pPart) + (long) pB * pPart;
		final long v16 = v * 16;
		final long r16 = v16 / pAll;
		final long r168;
		if (r16 < 0) {
			r168 = r16 - 8;
		} else {
			r168 = r16 + 8;
		}
		final long result = r168 / 16;
		return (int) result;
	}

	private InterpolationUtil() {
		super();
	}

}
