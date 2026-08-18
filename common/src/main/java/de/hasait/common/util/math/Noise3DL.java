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

public final class Noise3DL {
	private final int[] p;

	private final int[] fade;

	public Noise3DL(long pSeed) {
		super();

		final Random random = new Random(pSeed);

		p = new int[512];
		fade = new int[256];
		for (int i = 0; i < 256; i++) {
			p[i] = (short) random.nextInt(256);
			p[i + 256] = p[i];
			fade[i] = (int) ((1 << 12) * f(i / 256.0));
		}
	}

	/**
	 * @return value in range (-34000; 34000)
	 */
	public int get(long pX, long pY, long pZ) {
		final int X = (int) (pX >> 16 & 255);
		final int Y = (int) (pY >> 16 & 255);
		final int Z = (int) (pZ >> 16 & 255);
		final int N = 1 << 16;
		final int x = (int) (pX & N - 1);
		final int y = (int) (pY & N - 1);
		final int z = (int) (pZ & N - 1);
		final int u = fade(x);
		final int v = fade(y);
		final int w = fade(z);
		final int A = p[X] + Y;
		final int AA = p[A] + Z;
		final int AB = p[A + 1] + Z;
		final int B = p[X + 1] + Y;
		final int BA = p[B] + Z;
		final int BB = p[B + 1] + Z;
		return lerp(w, lerp(v, lerp(u, grad(p[AA], x, y, z), grad(p[BA], x - N, y, z)),
							lerp(u, grad(p[AB], x, y - N, z), grad(p[BB], x - N, y - N, z))
		), lerp(v, lerp(u, grad(p[AA + 1], x, y, z - N), grad(p[BA + 1], x - N, y, z - N)),
				lerp(u, grad(p[AB + 1], x, y - N, z - N), grad(p[BB + 1], x - N, y - N, z - N))
		));
	}

	private double f(double t) {
		return t * t * t * (t * (t * 6 - 15) + 10);
	}

	private int fade(int t) {
		final int t0 = fade[t >> 8];
		final int t1 = fade[Math.min(255, (t >> 8) + 1)];
		return t0 + ((t & 255) * (t1 - t0) >> 8);
	}

	private int grad(int hash, int x, int y, int z) {
		final int h = hash & 15, u = h < 8 ? x : y, v = h < 4 ? y : h == 12 || h == 14 ? x : z;
		return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
	}

	private int lerp(int t, int a, int b) {
		return a + (t * (b - a) >> 12);
	}

}
