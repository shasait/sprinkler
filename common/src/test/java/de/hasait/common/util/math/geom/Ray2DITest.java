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
import de.hasait.common.util.math.geom.Ray2DI;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class Ray2DITest {

    @Test
    public void test_addAngle() throws Exception {
        final Ray2DI ray = new Ray2DI(3, 7, Angle.DEG001);

        final Ray2DI result = ray.addAngle(Angle.DEG045);

        assertEquals(3, result.getStartX());
        assertEquals(7, result.getStartY());
        assertEquals(Angle.fromDegree(46), result.getAngle());
    }

    @Test
    public void test_angle() throws Exception {
        final Ray2DI ray = new Ray2DI(3, 7, Angle.DEG001);

        final Ray2DI result = ray.angle(Angle.DEG045);

        assertEquals(3, result.getStartX());
        assertEquals(7, result.getStartY());
        assertEquals(Angle.DEG045, result.getAngle());
    }

    @Test
    public void test_curveAR_0_180_100() throws Exception {
        final Ray2DI ray = new Ray2DI(0, 0, Angle.DEG000);

        final Ray2DI result = ray.curveAR(Angle.DEG180, 100, false);

        assertEquals(0, result.getStartX());
        assertEquals(200, result.getStartY());
        assertEquals(Angle.DEG180, result.getAngle());
    }

    @Test
    public void test_curveAR_0_18_1000() throws Exception {
        final Ray2DI ray = new Ray2DI(0, 0, Angle.DEG000);

        final Ray2DI result = ray.curveAR(Angle.fromDegree(18), 1000, false);

        assertEquals(309, result.getStartX());
        assertEquals(49, result.getStartY());
        assertEquals(Angle.fromDegree(18), result.getAngle());
    }

    @Test
    public void test_curveAR_0_18_1000_cw() throws Exception {
        final Ray2DI ray = new Ray2DI(0, 0, Angle.DEG000);

        final Ray2DI result = ray.curveAR(Angle.fromDegree(18), 1000, true);

        assertEquals(309, result.getStartX());
        assertEquals(-49, result.getStartY());
        assertEquals(Angle.fromDegree(342), result.getAngle());
    }

    @Test
    public void test_curveAR_0_90_100() throws Exception {
        final Ray2DI ray = new Ray2DI(0, 0, Angle.DEG000);

        final Ray2DI result = ray.curveAR(Angle.DEG090, 100, false);

        assertEquals(100, result.getStartX());
        assertEquals(100, result.getStartY());
        assertEquals(Angle.DEG090, result.getAngle());
    }

    @Test
    public void test_curveAR_90_90_100() throws Exception {
        final Ray2DI ray = new Ray2DI(0, 0, Angle.DEG090);

        final Ray2DI result = ray.curveAR(Angle.DEG090, 100, false);

        assertEquals(-100, result.getStartX());
        assertEquals(100, result.getStartY());
        assertEquals(Angle.DEG180, result.getAngle());
    }

}
