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

package de.hasait.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class StringUtilTest {

    @Test
    public void testBitsToByte_null() {
        assertNull(StringUtil.bitsToByte(null));
    }

    @Test
    public void testBitsToByte_zero() {
        assertEquals(0, StringUtil.bitsToByte("0").byteValue());
        assertEquals(0, StringUtil.bitsToByte("00").byteValue());
        assertEquals(0, StringUtil.bitsToByte("000").byteValue());
        assertEquals(0, StringUtil.bitsToByte("0000").byteValue());
    }

    @Test
    public void testBitsToByte_one() {
        assertEquals(1, StringUtil.bitsToByte("1").byteValue());
        assertEquals(1, StringUtil.bitsToByte("01").byteValue());
        assertEquals(1, StringUtil.bitsToByte("001").byteValue());
        assertEquals(1, StringUtil.bitsToByte("0001").byteValue());
    }

    @Test
    public void testBitsToByte_two() {
        assertEquals(2, StringUtil.bitsToByte("10").byteValue());
        assertEquals(2, StringUtil.bitsToByte("010").byteValue());
        assertEquals(2, StringUtil.bitsToByte("0010").byteValue());
        assertEquals(2, StringUtil.bitsToByte("00010").byteValue());
    }

    @Test
    public void testBitsToByte_255() {
        assertEquals(255, Byte.toUnsignedInt((byte) -1));
        assertEquals(-1, StringUtil.bitsToByte("11111111").byteValue());
    }

}
