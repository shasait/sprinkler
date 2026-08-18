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

import java.util.Random;

import de.hasait.common.util.AssertUtil;


/**
 *
 */
public final class PerlinNoise1D {

	private static final int PERSISTENCE_FACT = 16;

	private final PerlinOctave[] _octaves;

	/**
	 * @param pSeed            Random seed.
	 * @param pNoiseSize       How many random values to use (e.g. <code>1024</code>).
	 * @param pCoordFact       Factor for stretching random values, i.e. size of world is <code>pNoiseSize * pCoordFact</code>
	 *                         (e.g. <code>128</code>).
	 * @param pOctaveCount     How many noise functions to overlay (e.g. <code>3</code>).
	 * @param pPersistencePart Additional noise function amplitude (part of 16, e.g. <code>8</code>):
	 *                         <ul>
	 *                         <li><code>16</code> means all functions have an amplitude of 1</li>
	 *                         <li><code>8</code> means: 1st function has an amplitude of 1, 2nd has 1/2, 3rd has 1/4, 4th has 1/8,
	 *                         ...</li>
	 *                         <li><code>5</code> means: 1st function has an amplitude of 1, 2nd has ~1/3, 3rd has ~1/9, 4th has
	 *                         ~1/27, ...</li>
	 *                         </ul>
	 * @param pRange           Value range (e.g. <code>200</code>).
	 * @param pMin             Minimum value (e.g. <code>-100</code>), i.e. maximum value is <code>pMin + pRange</code>.
	 */
	public PerlinNoise1D(long pSeed, int pNoiseSize, int pCoordFact, int pOctaveCount, int pPersistencePart, int pRange, int pMin) {
		super();
		AssertUtil.isTrue(pNoiseSize > 0);
		AssertUtil.isTrue(pCoordFact > 0);
		AssertUtil.isTrue(pOctaveCount > 0);
		AssertUtil.isTrue(pPersistencePart > 0 && pPersistencePart <= PERSISTENCE_FACT);
		AssertUtil.isTrue(pRange > 0);
		AssertUtil.isTrue((long) pMin + pRange < Integer.MAX_VALUE);

		final Random random = new Random(pSeed);
		_octaves = new PerlinOctave[pOctaveCount];
		for (int i = 0; i < pOctaveCount; i++) {
			int frequency;
			int persistenceFact;
			int persistencePart;
			if (i == 0) {
				frequency = 1;
				persistenceFact = 1;
				persistencePart = 1;
			} else {
				frequency = _octaves[i - 1]._frequency * 2;
				persistenceFact = _octaves[i - 1]._persistenceFact * PERSISTENCE_FACT;
				persistencePart = _octaves[i - 1]._persistencePart * pPersistencePart;
			}
			_octaves[i] = new PerlinOctave(pNoiseSize, pCoordFact, random.nextLong(), pRange, pMin, frequency, persistenceFact,
										   persistencePart
			);
		}
	}

	public int perlinNoise(long pX) {
		int total = 0;

		for (PerlinOctave octave : _octaves) {
			total = total + octave.octaveNoise(pX);
		}

		return total;
	}

	PerlinOctave[] getOctaves() {
		return _octaves;
	}

	static class PerlinOctave {

		private final int _noiseSize;
		private final int _coordFact;
		private final int _frequency;
		private final int _persistenceFact;
		private final int _persistencePart;

		private final int[] _noise;

		public PerlinOctave(int pNoiseSize, int pCoordFact, long pSeed, int pRange, int pMin, int pFrequency, int pPersistenceFact, int pPersistencePart) {
			super();

			_noiseSize = pNoiseSize;
			_coordFact = pCoordFact;

			_noise = new int[_noiseSize * 2];
			_frequency = pFrequency;
			_persistenceFact = pPersistenceFact;
			_persistencePart = pPersistencePart;

			final Random random = new Random(pSeed);
			for (int i = -_noiseSize; i < _noiseSize; i++) {
				_noise[i + _noiseSize] = random.nextInt(pRange) + pMin;
			}
		}

		int octaveNoise(long pX) {
			return (int) ((long) interpolatedNoise(pX * _frequency) * _persistencePart / _persistenceFact);
		}

		private int interpolatedNoise(long pX) {
			final long coordX = pX / _coordFact;
			final int coordXPart = (int) (pX % _coordFact);
			return InterpolationUtil.cosine(noise(coordX), noise(coordX + 1), coordXPart, _coordFact);
		}

		private int noise(long pX) {
			final int i = (int) (pX % _noiseSize);
			return _noise[i + _noiseSize];
		}

	}

}
