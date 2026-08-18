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

import java.util.Objects;

import de.hasait.common.util.AssertUtil;

/**
 *
 */
public final class MathUtil {

	public static int chunkify(int pValue, int pStepSize) {
		AssertUtil.greater(0, pStepSize);

		final int negFix = pValue < 0 ? 1 : 0;
		return (pValue + negFix) / pStepSize - negFix;
	}

	public static long chunkify(long pValue, long pStepSize) {
		AssertUtil.greater(0, pStepSize);

		final long negFix = pValue < 0 ? 1 : 0;
		return (pValue + negFix) / pStepSize - negFix;
	}

	public static int circularRange(int pMaxExclusive, int pRawValue) {
		return circularRange(0, pMaxExclusive, pRawValue);
	}

	public static int circularRange(int pMinInclusive, int pMaxExclusive, int pRawValue) {
		AssertUtil.isTrue(pMaxExclusive > pMinInclusive);

		final int range = pMaxExclusive - pMinInclusive;

		// TODO write test and refactor to (pRawValue - pMinInclusive) % range + pMinInclusive

		int rawValue = pRawValue;
		while (rawValue >= pMaxExclusive) {
			rawValue -= range;
		}
		while (rawValue < pMinInclusive) {
			rawValue += range;
		}
		return rawValue;
	}

	public static long circularRange(long pMaxExclusive, long pRawValue) {
		return circularRange(0, pMaxExclusive, pRawValue);
	}

	public static long circularRange(long pMinInclusive, long pMaxExclusive, long pRawValue) {
		AssertUtil.isTrue(pMaxExclusive > pMinInclusive);

		final long range = pMaxExclusive - pMinInclusive;

		// TODO write test and refactor to (pRawValue - pMinInclusive) % range + pMinInclusive

		long rawValue = pRawValue;
		while (rawValue >= pMaxExclusive) {
			rawValue -= range;
		}
		while (rawValue < pMinInclusive) {
			rawValue += range;
		}
		return rawValue;
	}

	public static float hardRange(float pMinInclusive, float pMaxInclusive, float pRawValue) {
		AssertUtil.isTrue(pMaxInclusive >= pMinInclusive);

		if (pRawValue > pMaxInclusive) {
			return pMaxInclusive;
		}

		if (pRawValue < pMinInclusive) {
			return pMinInclusive;
		}

		return pRawValue;
	}

	public static int hardRange(int pMaxInclusive, int pRawValue) {
		return hardRange(0, pMaxInclusive, pRawValue);
	}

	public static int hardRange(int pMinInclusive, int pMaxInclusive, int pRawValue) {
		AssertUtil.isTrue(pMaxInclusive >= pMinInclusive);

		if (pRawValue > pMaxInclusive) {
			return pMaxInclusive;
		}

		if (pRawValue < pMinInclusive) {
			return pMinInclusive;
		}

		return pRawValue;
	}

	public static long hardRange(long pMaxInclusive, long pRawValue) {
		return hardRange(0, pMaxInclusive, pRawValue);
	}

	public static long hardRange(long pMinInclusive, long pMaxInclusive, long pRawValue) {
		AssertUtil.isTrue(pMaxInclusive >= pMinInclusive);

		if (pRawValue > pMaxInclusive) {
			return pMaxInclusive;
		}

		if (pRawValue < pMinInclusive) {
			return pMinInclusive;
		}

		return pRawValue;
	}

	public static int pow(int pBase, int pExponent) {
		AssertUtil.isTrue(pExponent >= 0);

		int result = 1;
		for (int i = 0; i < pExponent; i++) {
			result = result * pBase;
		}

		return result;
	}

	public static int range(int pMinInclusive, int pMaxExclusive, int pRawValue, RangeMode pRangeMode) {
		Objects.requireNonNull(pRangeMode, "pRangeMode");
		AssertUtil.isTrue(pMaxExclusive > pMinInclusive);

		switch (pRangeMode) {
			case CIRCULAR:
				return circularRange(pMinInclusive, pMaxExclusive, pRawValue);
			case HARD:
				return hardRange(pMinInclusive, pMaxExclusive - 1, pRawValue);
			case EXCEPTION:
				AssertUtil.greaterOrEqual(pMinInclusive, pRawValue);
				AssertUtil.less(pMaxExclusive, pRawValue);
				return pRawValue;
			default:
				throw AssertUtil.createNotReachable();
		}
	}

	public static long range(long pMinInclusive, long pMaxExclusive, long pRawValue, RangeMode pRangeMode) {
		Objects.requireNonNull(pRangeMode, "pRangeMode");
		AssertUtil.isTrue(pMaxExclusive > pMinInclusive);

		switch (pRangeMode) {
			case CIRCULAR:
				return circularRange(pMinInclusive, pMaxExclusive, pRawValue);
			case HARD:
				return hardRange(pMinInclusive, pMaxExclusive - 1, pRawValue);
			case EXCEPTION:
				AssertUtil.greaterOrEqual(pMinInclusive, pRawValue);
				AssertUtil.less(pMaxExclusive, pRawValue);
				return pRawValue;
			default:
				throw AssertUtil.createNotReachable();
		}
	}

	public static int stepify(long pValue, int pDivisor) {
		if (pValue < 0) {
			return (int) ((pValue + 1) % pDivisor + pDivisor - 1);
		}
		return (int) (pValue % pDivisor);
	}

	private MathUtil() {
		super();
	}

}
