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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AngleTest {

    @Test
    public void testCosISin000() {
        assertEquals(10000, Angle.DEG000.cosI(10000));
        assertEquals(0, Angle.DEG000.sinI(10000));
    }

    @Test
    public void testCosISin001() {
        assertEquals(99, Angle.DEG001.cosI(100));
        assertEquals(1, Angle.DEG001.sinI(100));
    }

    @Test
    public void testCosISin045() {
        assertEquals(7071, Angle.DEG045.cosI(10000));
        assertEquals(7071, Angle.DEG045.sinI(10000));
    }

    @Test
    public void testCosISin090() {
        assertEquals(0, Angle.DEG090.cosI(10000));
        assertEquals(10000, Angle.DEG090.sinI(10000));
    }

    @Test
    public void testCosISin180() {
        assertEquals(-10000, Angle.DEG180.cosI(10000));
        assertEquals(0, Angle.DEG180.sinI(10000));
    }

    @Test
    public void testCosISin270() {
        assertEquals(0, Angle.DEG270.cosI(10000));
        assertEquals(-10000, Angle.DEG270.sinI(10000));
    }

}
