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

import de.hasait.common.util.math.geom.DynamicArray2DI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 *
 */
public class DynamicArray2DITest {

    @Test
    public void get() throws Exception {
        final DynamicArray2DI<String> da = new DynamicArray2DI<>();

        assertNull(da.get(0, 0));
        assertNull(da.get(-10, -20));
        assertNull(da.get(10, 20));
    }

    @Test
    public void set1() throws Exception {
        final DynamicArray2DI<String> da = new DynamicArray2DI<>();

        da.set(5, 10, "5_10");
        assertEquals("5_10", da.get(5, 10));
        assertNull(da.get(0, 0));
    }

    @Test
    public void set2() throws Exception {
        final DynamicArray2DI<String> da = new DynamicArray2DI<>();

        da.set(-5, 10, "-5_10");
        assertEquals("-5_10", da.get(-5, 10));
        assertNull(da.get(0, 0));
    }

    @Test
    public void set3() throws Exception {
        final DynamicArray2DI<String> da = new DynamicArray2DI<>();

        da.set(5, 10, "5_10");
        da.set(-5, 10, "-5_10");
        da.set(-5, -10, "-5_-10");
        assertEquals("-5_-10", da.get(-5, -10));
        assertEquals("-5_10", da.get(-5, 10));
        assertEquals("5_10", da.get(5, 10));
        assertNull(da.get(0, 0));
    }

}
