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
    public void testBits07ToByte_null() {
        assertNull(StringUtil.bits07ToByte(null));
    }

    @Test
    public void testBits07ToByte_zero() {
        assertEquals(0, StringUtil.bits07ToByte("0").byteValue());
        assertEquals(0, StringUtil.bits07ToByte("00").byteValue());
        assertEquals(0, StringUtil.bits07ToByte("000").byteValue());
        assertEquals(0, StringUtil.bits07ToByte("0000").byteValue());
    }

    @Test
    public void testBits07ToByte_one() {
        assertEquals(1, StringUtil.bits07ToByte("1").byteValue());
        assertEquals(1, StringUtil.bits07ToByte("10").byteValue());
        assertEquals(1, StringUtil.bits07ToByte("100").byteValue());
        assertEquals(1, StringUtil.bits07ToByte("1000").byteValue());
    }

    @Test
    public void testBits07ToByte_two() {
        assertEquals(2, StringUtil.bits07ToByte("01").byteValue());
        assertEquals(2, StringUtil.bits07ToByte("010").byteValue());
        assertEquals(2, StringUtil.bits07ToByte("0100").byteValue());
        assertEquals(2, StringUtil.bits07ToByte("01000").byteValue());
    }

    @Test
    public void testBits07ToByte_255() {
        assertEquals(255, Byte.toUnsignedInt((byte) -1));
        assertEquals(-1, StringUtil.bits07ToByte("11111111").byteValue());
    }

}
