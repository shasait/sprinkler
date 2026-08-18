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

import org.junit.jupiter.api.Test;

import de.hasait.common.util.math.geom.Angle;
import de.hasait.common.util.math.geom.Vector2DI;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Vector2DITest {

    @Test
    public void testRotLocalN90() {
        final Vector2DI v40 = Vector2DI.obtain(4, 0);

        v40.rotLocal(Angle.DEG090.negate(), true);

        assertEquals(0, v40.x);
        assertEquals(4, v40.y);

        v40.rotLocal(Angle.DEG090.negate(), true);

        assertEquals(-4, v40.x);
        assertEquals(0, v40.y);

        v40.rotLocal(Angle.DEG090.negate(), true);

        assertEquals(0, v40.x);
        assertEquals(-4, v40.y);

        v40.rotLocal(Angle.DEG090.negate(), true);

        assertEquals(4, v40.x);
        assertEquals(0, v40.y);
    }

    @Test
    public void testRotLocalP18() {
        final Vector2DI v40 = Vector2DI.obtain(1000, 0);

        v40.rotLocal(Angle.fromDegree(18), true);

        assertEquals(951, v40.x);
        assertEquals(-309, v40.y);

        v40.rotLocal(Angle.fromDegree(18), true);

        assertEquals(809, v40.x);
        assertEquals(-586, v40.y);

        v40.rotLocal(Angle.fromDegree(18), true);

        assertEquals(588, v40.x);
        assertEquals(-806, v40.y);

        v40.rotLocal(Angle.fromDegree(18), true);

        assertEquals(310, v40.x);
        assertEquals(-947, v40.y);

        v40.rotLocal(Angle.fromDegree(18), true);

        assertEquals(2, v40.x);
        assertEquals(-995, v40.y);
    }

    @Test
    public void testRotLocalP90() {
        final Vector2DI v40 = Vector2DI.obtain(4, 0);

        v40.rotLocal(Angle.DEG090, true);

        assertEquals(0, v40.x);
        assertEquals(-4, v40.y);

        v40.rotLocal(Angle.DEG090, true);

        assertEquals(-4, v40.x);
        assertEquals(0, v40.y);

        v40.rotLocal(Angle.DEG090, true);

        assertEquals(0, v40.x);
        assertEquals(4, v40.y);

        v40.rotLocal(Angle.DEG090, true);

        assertEquals(4, v40.x);
        assertEquals(0, v40.y);
    }

}
