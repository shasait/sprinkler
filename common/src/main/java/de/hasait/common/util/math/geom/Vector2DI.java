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

import java.util.concurrent.ConcurrentLinkedQueue;

public final class Vector2DI implements Comparable<Vector2DI> {

    private static final ConcurrentLinkedQueue<Vector2DI> POOL = new ConcurrentLinkedQueue<>();

    public static void free(Vector2DI vector) {
        POOL.add(vector);
    }

    public static Vector2DI obtain(int x, int y) {
        final Vector2DI pooled = POOL.poll();
        if (pooled != null) {
            pooled.x = x;
            pooled.y = y;
            return pooled;
        }
        return new Vector2DI(x, y);
    }

    public static Vector2DI obtain() {
        return obtain(0, 0);
    }

    public static Vector2DI obtain(Vector2DI v) {
        return obtain(v.x, v.y);
    }

    public int x, y;

    private Vector2DI() {
        super();
    }

    private Vector2DI(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (other == null) {
            return false;
        }

        if (getClass() != other.getClass()) {
            return false;
        }

        Vector2DI v = (Vector2DI) other;
        return v.x == x && v.y == y;
    }

    @Override
    public int compareTo(Vector2DI other) {
        int result;
        result = Integer.compare(x, other.x);
        if (result != 0) {
            return result;
        }
        result = Integer.compare(y, other.y);
        return result;
    }

    public Vector2DI cpy() {
        return obtain(this);
    }

    public Vector2DI setLocal(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2DI setLocal(Vector2DI v) {
        return setLocal(v.x, v.y);
    }

    public Vector2DI absLocal() {
        x = Math.abs(x);
        y = Math.abs(y);
        return this;
    }

    public int dot(int x, int y) {
        return this.x * x + this.y * y;
    }

    public int dot(Vector2DI v) {
        return dot(v.x, v.y);
    }

    public int length() {
        return (int) Math.sqrt(length2());
    }

    public int length2() {
        return x * x + y * y;
    }

    public int distance(Vector2DI v) {
        return (int) Math.sqrt(distance2(v));
    }

    public int distance2(Vector2DI v) {
        int dx = x - v.x;
        int dy = y - v.y;
        return dx * dx + dy * dy;
    }

    public Vector2DI add(int x, int y) {
        return obtain(this.x + x, this.y + y);
    }

    public Vector2DI add(Vector2DI v) {
        return add(v.x, v.y);
    }

    public Vector2DI addLocal(int x, int y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2DI addLocal(Vector2DI v) {
        return addLocal(v.x, v.y);
    }

    public Vector2DI sub(int x, int y) {
        return obtain(this.x - x, this.y - y);
    }

    public Vector2DI sub(Vector2DI v) {
        return sub(v.x, v.y);
    }

    public Vector2DI subLocal(int x, int y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2DI subLocal(Vector2DI v) {
        return subLocal(v.x, v.y);
    }

    public Vector2DI mul(int x, int y) {
        return obtain(this.x * x, this.y * y);
    }

    public Vector2DI mul(int f) {
        return mul(f, f);
    }

    public Vector2DI mul(Vector2DI v) {
        return mul(v.x, v.y);
    }

    public Vector2DI mulLocal(int x, int y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public Vector2DI mulLocal(int f) {
        return mulLocal(f, f);
    }

    public Vector2DI mulLocal(Vector2DI v) {
        return mulLocal(v.x, v.y);
    }

    public Vector2DI negate() {
        return mul(-1);
    }

    public Vector2DI div(int x, int y) {
        return obtain(this.x / x, this.y / y);
    }

    public Vector2DI div(int f) {
        return div(f, f);
    }

    public Vector2DI div(Vector2DI v) {
        return div(v.x, v.y);
    }

    public Vector2DI divLocal(int x, int y) {
        this.x /= x;
        this.y /= y;
        return this;
    }

    public Vector2DI divLocal(int f) {
        return divLocal(f, f);
    }

    public Vector2DI divLocal(Vector2DI v) {
        return divLocal(v.x, v.y);
    }

    /**
     * @param a         The angle.
     * @param clockwise Clockwise is clockwise if Y is up; if Y is down it is vice-versa.
     * @return New vector.
     */
    public Vector2DI rot(Angle a, boolean clockwise) {
        return cpy().rotLocal(a, clockwise);
    }

    /**
     * @param a         The angle.
     * @param clockwise Clockwise is clockwise if Y is up; if Y is down it is vice-versa.
     * @return <code>this</code>
     */
    public Vector2DI rotLocal(Angle a, boolean clockwise) {
        Angle angle;
        if (clockwise) {
            angle = a.negate();
        } else {
            angle = a;
        }
        int x = angle.cosI(this.x) - angle.sinI(y);
        int y = angle.sinI(this.x) + angle.cosI(this.y);
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2DI maxLocal(Vector2DI v) {
        x = Math.max(x, v.x);
        y = Math.max(y, v.y);
        return this;
    }

    public Vector2DI minLocal(Vector2DI v) {
        x = Math.min(x, v.x);
        y = Math.min(y, v.y);
        return this;
    }

    @Override
    public String toString() {
        return "V2DI[" + x + "; " + y + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public Vector2DL toVector2DL() {
        return Vector2DL.obtain(x, y);
    }

}
