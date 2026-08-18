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

import java.util.Iterator;
import java.util.NoSuchElementException;

import jakarta.annotation.Nonnull;

import de.hasait.common.util.math.MathUtil;
import de.hasait.common.util.math.RangeMode;
import de.hasait.common.util.AssertUtil;
import de.hasait.common.util.AssertionException;

/**
 *
 */
public final class Space2DI implements Iterable<Vector2DI> {

    private final Vector2DI minInclusive, maxExclusive, size;

    public Space2DI(int minInclusiveX, int minInclusiveY, int maxExclusiveX, int maxExclusiveY) {
        super();

        AssertUtil.greaterOrEqual(minInclusiveX, maxExclusiveX);
        AssertUtil.greaterOrEqual(minInclusiveY, maxExclusiveY);

        minInclusive = Vector2DI.obtain(minInclusiveX, minInclusiveY);
        maxExclusive = Vector2DI.obtain(maxExclusiveX, maxExclusiveY);
        size = maxExclusive.sub(minInclusive);
    }

    public void assertValid(int x, int y) {
        if (!isValid(x, y)) {
            AssertUtil.fail("Not: {2} <= ({0}, {1}) < {3}", x, y, minInclusive, maxExclusive); //$NON-NLS-1$
        }
    }

    @Nonnull
    public Vector2DI get(int x, int y) {
        assertValid(x, y);
        return Vector2DI.obtain(x, y);
    }

    @Nonnull
    public Vector2DI get(int x, int y, @Nonnull RangeMode modeX, @Nonnull RangeMode modeY) {
        int xInRange = MathUtil.range(minInclusive.x, maxExclusive.x, x, modeX);
        int yInRange = MathUtil.range(minInclusive.y, maxExclusive.y, y, modeY);
        return Vector2DI.obtain(xInRange, yInRange);
    }

    @Nonnull
    public Vector2DI get(@Nonnull Vector2DI v) {
        assertValid(v.x, v.y);
        return v;
    }

    @Nonnull
    public Vector2DI get(Vector2DI v, int translationX, int translationY, RangeMode modeX, RangeMode modeY) {
        return get(v.x + translationX, v.y + translationY, modeX, modeY);
    }

    @Nonnull
    public Vector2DI get(@Nonnull Vector2DI v, @Nonnull RangeMode modeX, @Nonnull RangeMode modeY) {
        return get(v.x, v.y, modeX, modeY);
    }

    @Nonnull
    public Vector2DI get(@Nonnull Vector2DI v, @Nonnull Vector2DI translation) {
        return get(v.x + translation.x, v.y + translation.y);
    }

    @Nonnull
    public Vector2DI get(@Nonnull Vector2DI v, @Nonnull Vector2DI translation, @Nonnull RangeMode modeX, @Nonnull RangeMode modeY) {
        return get(v.x + translation.x, v.y + translation.y, modeX, modeY);
    }

    @Nonnull
    public Vector2DI getMaxExclusive() {
        return maxExclusive;
    }

    @Nonnull
    public Vector2DI getMinInclusive() {
        return minInclusive;
    }

    @Nonnull
    public Vector2DI getSize() {
        return size;
    }

    public boolean isValid(int x, int y) {
        return x >= minInclusive.x && x < maxExclusive.x && y >= minInclusive.y && y < maxExclusive.y;
    }

    public boolean isValid(@Nonnull Vector2DI v) {
        return isValid(v.x, v.y);
    }

    @Override
    @Nonnull
    public Iterator<Vector2DI> iterator() {
        return new CoordIteratorImpl();
    }

    @Nonnull
    public Space2DI newContaining(int x, int y) {
        int minInclusiveX, maxExclusiveX;
        int minInclusiveY, maxExclusiveY;
        minInclusiveX = Math.min(minInclusive.x, x);
        minInclusiveY = Math.min(minInclusive.y, y);
        maxExclusiveX = Math.max(maxExclusive.x, x + 1);
        maxExclusiveY = Math.max(maxExclusive.y, y + 1);
        return new Space2DI(minInclusiveX, minInclusiveY, maxExclusiveX, maxExclusiveY);
    }

    public long size() {
        return (long) size.x * size.y;
    }

    public Space2DI[] splitOnLongAxis() {
        if (size.x <= 1 && size.y <= 1) {
            return null;
        }

        if (size.x >= size.y) {
            int split = size.x / 2;
            return new Space2DI[]{new Space2DI(minInclusive.x, minInclusive.y, minInclusive.x + split, maxExclusive.y), new Space2DI(minInclusive.x + split, minInclusive.y, maxExclusive.x, maxExclusive.y)};
        }

        int split = size.y / 2;
        return new Space2DI[]{new Space2DI(minInclusive.x, minInclusive.y, maxExclusive.x, minInclusive.y + split), new Space2DI(minInclusive.x, minInclusive.y + split, maxExclusive.x, maxExclusive.y)};
    }

    @Override
    public String toString() {
        return "S2DI[" + minInclusive + "; " + maxExclusive + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    private class CoordIteratorImpl implements Iterator<Vector2DI> {

        private int x = minInclusive.x;
        private int y = minInclusive.y;

        @Override
        public boolean hasNext() {
            return isValid(x, y);
        }

        @Override
        public Vector2DI next() {
            Vector2DI result = Vector2DI.obtain(x, y);
            try {
                assertValid(x, y);
            } catch (AssertionException pE) {
                throw new NoSuchElementException(pE.getMessage());
            }
            if (x < maxExclusive.x - 1) {
                x++;
            } else {
                x = minInclusive.x;
                y++;
            }
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
