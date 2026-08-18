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

/*
 * A speed-improved simplex noise algorithm for 2D, 3D and 4D in Java.
 *
 * Based on example code by Stefan Gustavson (stegu@itn.liu.se).
 * Optimisations by Peter Eastman (peastman@drizzle.stanford.edu).
 * Better rank ordering method by Stefan Gustavson in 2012.
 *
 * This could be speeded up even further, but it's useful as it is.
 *
 * Version 2012-03-09
 *
 * This code was placed in the public domain by its original author,
 * Stefan Gustavson. You may use it as you see fit, but
 * attribution is appreciated.
 *
 */

/**
 * Simplex noise in 2D, 3D and 4D
 */
public class SimplexNoise {
	// Skewing and unskewing factors for 2, 3, and 4 dimensions
	private static final double SKEW_2D = 0.5 * (Math.sqrt(3.0) - 1.0);
	private static final double UNSKEW_2D = (3.0 - Math.sqrt(3.0)) / 6.0;
	private static final double SKEW_3D = 1.0 / 3.0;
	private static final double UNSKEW_3D = 1.0 / 6.0;
	private static final double SKEW_4D = (Math.sqrt(5.0) - 1.0) / 4.0;
	private static final double UNSKEW_4D = (5.0 - Math.sqrt(5.0)) / 20.0;
	private static final Gradient2D[] GRADIENTS_2D = {
			new Gradient2D(1, 1),
			new Gradient2D(-1, 1),
			new Gradient2D(1, -1),
			new Gradient2D(-1, -1),
			new Gradient2D(1, 0),
			new Gradient2D(-1, 0),
			new Gradient2D(0, 1),
			new Gradient2D(0, -1)
	};
	private static final Gradient3D[] GRADIENTS_3D = {
			new Gradient3D(1, 1, 0),
			new Gradient3D(-1, 1, 0),
			new Gradient3D(1, -1, 0),
			new Gradient3D(-1, -1, 0),
			new Gradient3D(1, 0, 1),
			new Gradient3D(-1, 0, 1),
			new Gradient3D(1, 0, -1),
			new Gradient3D(-1, 0, -1),
			new Gradient3D(0, 1, 1),
			new Gradient3D(0, -1, 1),
			new Gradient3D(0, 1, -1),
			new Gradient3D(0, -1, -1)
	};
	private static final Gradient4D[] GRADIENTS_4D = {
			new Gradient4D(0, 1, 1, 1),
			new Gradient4D(0, 1, 1, -1),
			new Gradient4D(0, 1, -1, 1),
			new Gradient4D(0, 1, -1, -1),
			new Gradient4D(0, -1, 1, 1),
			new Gradient4D(0, -1, 1, -1),
			new Gradient4D(0, -1, -1, 1),
			new Gradient4D(0, -1, -1, -1),
			new Gradient4D(1, 0, 1, 1),
			new Gradient4D(1, 0, 1, -1),
			new Gradient4D(1, 0, -1, 1),
			new Gradient4D(1, 0, -1, -1),
			new Gradient4D(-1, 0, 1, 1),
			new Gradient4D(-1, 0, 1, -1),
			new Gradient4D(-1, 0, -1, 1),
			new Gradient4D(-1, 0, -1, -1),
			new Gradient4D(1, 1, 0, 1),
			new Gradient4D(1, 1, 0, -1),
			new Gradient4D(1, -1, 0, 1),
			new Gradient4D(1, -1, 0, -1),
			new Gradient4D(-1, 1, 0, 1),
			new Gradient4D(-1, 1, 0, -1),
			new Gradient4D(-1, -1, 0, 1),
			new Gradient4D(-1, -1, 0, -1),
			new Gradient4D(1, 1, 1, 0),
			new Gradient4D(1, 1, -1, 0),
			new Gradient4D(1, -1, 1, 0),
			new Gradient4D(1, -1, -1, 0),
			new Gradient4D(-1, 1, 1, 0),
			new Gradient4D(-1, 1, -1, 0),
			new Gradient4D(-1, -1, 1, 0),
			new Gradient4D(-1, -1, -1, 0)
	};

	private static double dot(Gradient2D pGradient, double pX, double pY) {
		return pGradient._x * pX + pGradient._y * pY;
	}

	private static double dot(Gradient3D pGradient, double pX, double pY, double pZ) {
		return pGradient._x * pX + pGradient._y * pY + pGradient._z * pZ;
	}

	private static double dot(Gradient4D pGradient, double pX, double pY, double pZ, double pW) {
		return pGradient._x * pX + pGradient._y * pY + pGradient._z * pZ + pGradient._w * pW;
	}

	// This method is a *lot* faster than using (int)Math.floor(x)
	private static int fastfloor(double pValue) {
		final int intValue = (int) pValue;
		return pValue < intValue ? intValue - 1 : intValue;
	}

	// To remove the need for index wrapping, double the permutation table length
	private final short[] perm = new short[512];

	private final short[] permMod8 = new short[512];

	private final short[] permMod12 = new short[512];

	private final short[] permMod32 = new short[512];

	public SimplexNoise(long pSeed) {
		super();

		final Random random = new Random(pSeed);

		final short[] p = new short[256];
		for (int i = 0; i < 256; i++) {
			p[i] = (short) random.nextInt(256);
		}

		for (int i = 0; i < 512; i++) {
			perm[i] = p[i & 255];
			permMod8[i] = (short) (perm[i] % 8);
			permMod12[i] = (short) (perm[i] % 12);
			permMod32[i] = (short) (perm[i] % 32);
		}
	}

	// 2D simplex noise
	public double get(double pX, double pY) {
		double n0, n1, n2; // Noise contributions from the three corners
		// Skew the input space to determine which simplex cell we're in
		final double s = (pX + pY) * SKEW_2D; // Hairy factor for 2D
		final int i = fastfloor(pX + s);
		final int j = fastfloor(pY + s);
		final double t = (i + j) * UNSKEW_2D;
		final double X0 = i - t; // Unskew the cell origin back to (x,y) space
		final double Y0 = j - t;
		final double x0 = pX - X0; // The x,y distances from the cell origin
		final double y0 = pY - Y0;
		// For the 2D case, the simplex shape is an equilateral triangle.
		// Determine which simplex we are in.
		int i1, j1; // Offsets for second (middle) corner of simplex in (i,j) coords
		if (x0 > y0) {
			i1 = 1;
			j1 = 0;
		} // lower triangle, XY order: (0,0)->(1,0)->(1,1)
		else {
			i1 = 0;
			j1 = 1;
		} // upper triangle, YX order: (0,0)->(0,1)->(1,1)
		// A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
		// a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
		// c = (3-sqrt(3))/6
		final double x1 = x0 - i1 + UNSKEW_2D; // Offsets for middle corner in (x,y) unskewed coords
		final double y1 = y0 - j1 + UNSKEW_2D;
		final double x2 = x0 - 1.0 + 2.0 * UNSKEW_2D; // Offsets for last corner in (x,y) unskewed coords
		final double y2 = y0 - 1.0 + 2.0 * UNSKEW_2D;
		// Work out the hashed gradient indices of the three simplex corners
		final int ii = i & 255;
		final int jj = j & 255;
		final int gi0 = permMod8[ii + perm[jj]];
		final int gi1 = permMod8[ii + i1 + perm[jj + j1]];
		final int gi2 = permMod8[ii + 1 + perm[jj + 1]];
		// Calculate the contribution from the three corners
		double t0 = 0.5 - x0 * x0 - y0 * y0;
		if (t0 < 0) {
			n0 = 0.0;
		} else {
			t0 *= t0;
			n0 = t0 * t0 * dot(GRADIENTS_2D[gi0], x0, y0); // (x,y) of grad3 used for 2D gradient
		}
		double t1 = 0.5 - x1 * x1 - y1 * y1;
		if (t1 < 0) {
			n1 = 0.0;
		} else {
			t1 *= t1;
			n1 = t1 * t1 * dot(GRADIENTS_2D[gi1], x1, y1);
		}
		double t2 = 0.5 - x2 * x2 - y2 * y2;
		if (t2 < 0) {
			n2 = 0.0;
		} else {
			t2 *= t2;
			n2 = t2 * t2 * dot(GRADIENTS_2D[gi2], x2, y2);
		}
		// Add contributions from each corner to get the final noise value.
		// The result is scaled to return values in the interval [-1,1].
		return 70.0 * (n0 + n1 + n2);
	}

	// 3D simplex noise
	public double get(double pX, double pY, double pZ) {
		double n0, n1, n2, n3; // Noise contributions from the four corners
		// Skew the input space to determine which simplex cell we're in
		final double s = (pX + pY + pZ) * SKEW_3D; // Very nice and simple skew factor for 3D
		final int i = fastfloor(pX + s);
		final int j = fastfloor(pY + s);
		final int k = fastfloor(pZ + s);
		final double t = (i + j + k) * UNSKEW_3D;
		final double X0 = i - t; // Unskew the cell origin back to (x,y,z) space
		final double Y0 = j - t;
		final double Z0 = k - t;
		final double x0 = pX - X0; // The x,y,z distances from the cell origin
		final double y0 = pY - Y0;
		final double z0 = pZ - Z0;
		// For the 3D case, the simplex shape is a slightly irregular tetrahedron.
		// Determine which simplex we are in.
		int i1, j1, k1; // Offsets for second corner of simplex in (i,j,k) coords
		int i2, j2, k2; // Offsets for third corner of simplex in (i,j,k) coords
		if (x0 >= y0) {
			if (y0 >= z0) {
				i1 = 1;
				j1 = 0;
				k1 = 0;
				i2 = 1;
				j2 = 1;
				k2 = 0;
			} // X Y Z order
			else if (x0 >= z0) {
				i1 = 1;
				j1 = 0;
				k1 = 0;
				i2 = 1;
				j2 = 0;
				k2 = 1;
			} // X Z Y order
			else {
				i1 = 0;
				j1 = 0;
				k1 = 1;
				i2 = 1;
				j2 = 0;
				k2 = 1;
			} // Z X Y order
		} else { // x0<y0
			if (y0 < z0) {
				i1 = 0;
				j1 = 0;
				k1 = 1;
				i2 = 0;
				j2 = 1;
				k2 = 1;
			} // Z Y X order
			else if (x0 < z0) {
				i1 = 0;
				j1 = 1;
				k1 = 0;
				i2 = 0;
				j2 = 1;
				k2 = 1;
			} // Y Z X order
			else {
				i1 = 0;
				j1 = 1;
				k1 = 0;
				i2 = 1;
				j2 = 1;
				k2 = 0;
			} // Y X Z order
		}
		// A step of (1,0,0) in (i,j,k) means a step of (1-c,-c,-c) in (x,y,z),
		// a step of (0,1,0) in (i,j,k) means a step of (-c,1-c,-c) in (x,y,z), and
		// a step of (0,0,1) in (i,j,k) means a step of (-c,-c,1-c) in (x,y,z), where
		// c = 1/6.
		final double x1 = x0 - i1 + UNSKEW_3D; // Offsets for second corner in (x,y,z) coords
		final double y1 = y0 - j1 + UNSKEW_3D;
		final double z1 = z0 - k1 + UNSKEW_3D;
		final double x2 = x0 - i2 + 2.0 * UNSKEW_3D; // Offsets for third corner in (x,y,z) coords
		final double y2 = y0 - j2 + 2.0 * UNSKEW_3D;
		final double z2 = z0 - k2 + 2.0 * UNSKEW_3D;
		final double x3 = x0 - 1.0 + 3.0 * UNSKEW_3D; // Offsets for last corner in (x,y,z) coords
		final double y3 = y0 - 1.0 + 3.0 * UNSKEW_3D;
		final double z3 = z0 - 1.0 + 3.0 * UNSKEW_3D;
		// Work out the hashed gradient indices of the four simplex corners
		final int ii = i & 255;
		final int jj = j & 255;
		final int kk = k & 255;
		final int gi0 = permMod12[ii + perm[jj + perm[kk]]];
		final int gi1 = permMod12[ii + i1 + perm[jj + j1 + perm[kk + k1]]];
		final int gi2 = permMod12[ii + i2 + perm[jj + j2 + perm[kk + k2]]];
		final int gi3 = permMod12[ii + 1 + perm[jj + 1 + perm[kk + 1]]];
		// Calculate the contribution from the four corners
		double t0 = 0.6 - x0 * x0 - y0 * y0 - z0 * z0;
		if (t0 < 0) {
			n0 = 0.0;
		} else {
			t0 *= t0;
			n0 = t0 * t0 * dot(GRADIENTS_3D[gi0], x0, y0, z0);
		}
		double t1 = 0.6 - x1 * x1 - y1 * y1 - z1 * z1;
		if (t1 < 0) {
			n1 = 0.0;
		} else {
			t1 *= t1;
			n1 = t1 * t1 * dot(GRADIENTS_3D[gi1], x1, y1, z1);
		}
		double t2 = 0.6 - x2 * x2 - y2 * y2 - z2 * z2;
		if (t2 < 0) {
			n2 = 0.0;
		} else {
			t2 *= t2;
			n2 = t2 * t2 * dot(GRADIENTS_3D[gi2], x2, y2, z2);
		}
		double t3 = 0.6 - x3 * x3 - y3 * y3 - z3 * z3;
		if (t3 < 0) {
			n3 = 0.0;
		} else {
			t3 *= t3;
			n3 = t3 * t3 * dot(GRADIENTS_3D[gi3], x3, y3, z3);
		}
		// Add contributions from each corner to get the final noise value.
		// The result is scaled to stay just inside [-1,1]
		return 32.0 * (n0 + n1 + n2 + n3);
	}

	// 4D simplex noise, better simplex rank ordering method 2012-03-09
	public double get(double pX, double pY, double pZ, double pW) {
		double n0, n1, n2, n3, n4; // Noise contributions from the five corners
		// Skew the (x,y,z,w) space to determine which cell of 24 simplices we're in
		final double s = (pX + pY + pZ + pW) * SKEW_4D; // Factor for 4D skewing
		final int i = fastfloor(pX + s);
		final int j = fastfloor(pY + s);
		final int k = fastfloor(pZ + s);
		final int l = fastfloor(pW + s);
		final double t = (i + j + k + l) * UNSKEW_4D; // Factor for 4D unskewing
		final double X0 = i - t; // Unskew the cell origin back to (x,y,z,w) space
		final double Y0 = j - t;
		final double Z0 = k - t;
		final double W0 = l - t;
		final double x0 = pX - X0; // The x,y,z,w distances from the cell origin
		final double y0 = pY - Y0;
		final double z0 = pZ - Z0;
		final double w0 = pW - W0;
		// For the 4D case, the simplex is a 4D shape I won't even try to describe.
		// To find out which of the 24 possible simplices we're in, we need to
		// determine the magnitude ordering of x0, y0, z0 and w0.
		// Six pair-wise comparisons are performed between each possible pair
		// of the four coordinates, and the results are used to rank the numbers.
		int rankx = 0;
		int ranky = 0;
		int rankz = 0;
		int rankw = 0;
		if (x0 > y0) {
			rankx++;
		} else {
			ranky++;
		}
		if (x0 > z0) {
			rankx++;
		} else {
			rankz++;
		}
		if (x0 > w0) {
			rankx++;
		} else {
			rankw++;
		}
		if (y0 > z0) {
			ranky++;
		} else {
			rankz++;
		}
		if (y0 > w0) {
			ranky++;
		} else {
			rankw++;
		}
		if (z0 > w0) {
			rankz++;
		} else {
			rankw++;
		}
		int i1, j1, k1, l1; // The integer offsets for the second simplex corner
		int i2, j2, k2, l2; // The integer offsets for the third simplex corner
		int i3, j3, k3, l3; // The integer offsets for the fourth simplex corner
		// simplex[c] is a 4-vector with the numbers 0, 1, 2 and 3 in some order.
		// Many values of c will never occur, since e.g. x>y>z>w makes x<z, y<w and x<w
		// impossible. Only the 24 indices which have non-zero entries make any sense.
		// We use a thresholding to set the coordinates in turn from the largest magnitude.
		// Rank 3 denotes the largest coordinate.
		i1 = rankx >= 3 ? 1 : 0;
		j1 = ranky >= 3 ? 1 : 0;
		k1 = rankz >= 3 ? 1 : 0;
		l1 = rankw >= 3 ? 1 : 0;
		// Rank 2 denotes the second largest coordinate.
		i2 = rankx >= 2 ? 1 : 0;
		j2 = ranky >= 2 ? 1 : 0;
		k2 = rankz >= 2 ? 1 : 0;
		l2 = rankw >= 2 ? 1 : 0;
		// Rank 1 denotes the second smallest coordinate.
		i3 = rankx >= 1 ? 1 : 0;
		j3 = ranky >= 1 ? 1 : 0;
		k3 = rankz >= 1 ? 1 : 0;
		l3 = rankw >= 1 ? 1 : 0;
		// The fifth corner has all coordinate offsets = 1, so no need to compute that.
		final double x1 = x0 - i1 + UNSKEW_4D; // Offsets for second corner in (x,y,z,w) coords
		final double y1 = y0 - j1 + UNSKEW_4D;
		final double z1 = z0 - k1 + UNSKEW_4D;
		final double w1 = w0 - l1 + UNSKEW_4D;
		final double x2 = x0 - i2 + 2.0 * UNSKEW_4D; // Offsets for third corner in (x,y,z,w) coords
		final double y2 = y0 - j2 + 2.0 * UNSKEW_4D;
		final double z2 = z0 - k2 + 2.0 * UNSKEW_4D;
		final double w2 = w0 - l2 + 2.0 * UNSKEW_4D;
		final double x3 = x0 - i3 + 3.0 * UNSKEW_4D; // Offsets for fourth corner in (x,y,z,w) coords
		final double y3 = y0 - j3 + 3.0 * UNSKEW_4D;
		final double z3 = z0 - k3 + 3.0 * UNSKEW_4D;
		final double w3 = w0 - l3 + 3.0 * UNSKEW_4D;
		final double x4 = x0 - 1.0 + 4.0 * UNSKEW_4D; // Offsets for last corner in (x,y,z,w) coords
		final double y4 = y0 - 1.0 + 4.0 * UNSKEW_4D;
		final double z4 = z0 - 1.0 + 4.0 * UNSKEW_4D;
		final double w4 = w0 - 1.0 + 4.0 * UNSKEW_4D;
		// Work out the hashed gradient indices of the five simplex corners
		final int ii = i & 255;
		final int jj = j & 255;
		final int kk = k & 255;
		final int ll = l & 255;
		final int gi0 = permMod32[ii + perm[jj + perm[kk + perm[ll]]]];
		final int gi1 = permMod32[ii + i1 + perm[jj + j1 + perm[kk + k1 + perm[ll + l1]]]];
		final int gi2 = permMod32[ii + i2 + perm[jj + j2 + perm[kk + k2 + perm[ll + l2]]]];
		final int gi3 = permMod32[ii + i3 + perm[jj + j3 + perm[kk + k3 + perm[ll + l3]]]];
		final int gi4 = permMod32[ii + 1 + perm[jj + 1 + perm[kk + 1 + perm[ll + 1]]]];
		// Calculate the contribution from the five corners
		double t0 = 0.6 - x0 * x0 - y0 * y0 - z0 * z0 - w0 * w0;
		if (t0 < 0) {
			n0 = 0.0;
		} else {
			t0 *= t0;
			n0 = t0 * t0 * dot(GRADIENTS_4D[gi0], x0, y0, z0, w0);
		}
		double t1 = 0.6 - x1 * x1 - y1 * y1 - z1 * z1 - w1 * w1;
		if (t1 < 0) {
			n1 = 0.0;
		} else {
			t1 *= t1;
			n1 = t1 * t1 * dot(GRADIENTS_4D[gi1], x1, y1, z1, w1);
		}
		double t2 = 0.6 - x2 * x2 - y2 * y2 - z2 * z2 - w2 * w2;
		if (t2 < 0) {
			n2 = 0.0;
		} else {
			t2 *= t2;
			n2 = t2 * t2 * dot(GRADIENTS_4D[gi2], x2, y2, z2, w2);
		}
		double t3 = 0.6 - x3 * x3 - y3 * y3 - z3 * z3 - w3 * w3;
		if (t3 < 0) {
			n3 = 0.0;
		} else {
			t3 *= t3;
			n3 = t3 * t3 * dot(GRADIENTS_4D[gi3], x3, y3, z3, w3);
		}
		double t4 = 0.6 - x4 * x4 - y4 * y4 - z4 * z4 - w4 * w4;
		if (t4 < 0) {
			n4 = 0.0;
		} else {
			t4 *= t4;
			n4 = t4 * t4 * dot(GRADIENTS_4D[gi4], x4, y4, z4, w4);
		}
		// Sum up and scale the result to cover the range [-1,1]
		return 27.0 * (n0 + n1 + n2 + n3 + n4);
	}

	// Inner class to speed upp gradient computations
	// (array access is a lot slower than member access)
	private static class Gradient2D {
		double _x, _y;

		Gradient2D(double pX, double pY) {
			_x = pX;
			_y = pY;
		}
	}

	// Inner class to speed upp gradient computations
	// (array access is a lot slower than member access)
	private static class Gradient3D {
		double _x, _y, _z;

		Gradient3D(double pX, double pY, double pZ) {
			_x = pX;
			_y = pY;
			_z = pZ;
		}
	}

	// Inner class to speed upp gradient computations
	// (array access is a lot slower than member access)
	private static class Gradient4D {
		double _x, _y, _z, _w;

		Gradient4D(double pX, double pY, double pZ, double pW) {
			_x = pX;
			_y = pY;
			_z = pZ;
			_w = pW;
		}
	}
}
