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

import org.junit.jupiter.api.Test;

import de.hasait.common.util.math.InterpolationUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class InterpolationUtilTest {

    @Test
    public void testCosine01() throws Exception {
        assertEquals(0, InterpolationUtil.cosine(0, 100, 0, 128));
        assertEquals(50, InterpolationUtil.cosine(0, 100, 64, 128));
        assertEquals(85, InterpolationUtil.cosine(0, 100, 96, 128));
        assertEquals(100, InterpolationUtil.cosine(0, 100, 127, 128));
        assertEquals(100, InterpolationUtil.cosine(0, 100, 128, 128));
    }

    @Test
    public void testCosine02() throws Exception {
        assertEquals(-100, InterpolationUtil.cosine(-100, 100, 0, 128));
        assertEquals(0, InterpolationUtil.cosine(-100, 100, 64, 128));
        assertEquals(71, InterpolationUtil.cosine(-100, 100, 96, 128));
        assertEquals(100, InterpolationUtil.cosine(-100, 100, 128, 128));
    }

    @Test
    public void testLinear01() throws Exception {
        assertEquals(0, InterpolationUtil.linear(0, 100, 0, 256));
        assertEquals(50, InterpolationUtil.linear(0, 100, 128, 256));
        assertEquals(75, InterpolationUtil.linear(0, 100, 192, 256));
        assertEquals(100, InterpolationUtil.linear(0, 100, 255, 256));
        assertEquals(100, InterpolationUtil.linear(0, 100, 256, 256));
    }

    @Test
    public void testLinear02() throws Exception {
        assertEquals(-100, InterpolationUtil.linear(-100, 100, 0, 512));
        assertEquals(-100, InterpolationUtil.linear(-100, 100, 1, 512));
        assertEquals(0, InterpolationUtil.linear(-100, 100, 256, 512));
        assertEquals(50, InterpolationUtil.linear(-100, 100, 384, 512));
        assertEquals(100, InterpolationUtil.linear(-100, 100, 511, 512));
        assertEquals(100, InterpolationUtil.linear(-100, 100, 512, 512));
    }

}
