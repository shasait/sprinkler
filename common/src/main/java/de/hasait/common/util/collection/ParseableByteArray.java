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

package de.hasait.common.util.collection;

import java.nio.charset.Charset;

public class ParseableByteArray {

    private final byte[] data;

    private int dataPos;
    private int peekPos;

    public ParseableByteArray(byte[] data) {
        this.data = data;
    }

    public byte takeByte() {
        peekPos = 0;
        return data[dataPos++];
    }

    public Byte takeOptionalByte() {
        if (dataPos >= data.length) {
            return null;
        }
        return takeByte();
    }

    public byte[] takesBytes(int len) {
        peekPos = 0;
        byte[] result = new byte[len];
        System.arraycopy(data, dataPos, result, 0, len);
        dataPos += len;
        return result;
    }

    public int takeInt16() {
        return takeByteAsUnsignedInt() + takeByteAsUnsignedInt() * 256;
    }

    public int takeByteAsUnsignedInt() {
        return Byte.toUnsignedInt(takeByte());
    }

    public String takeString(int len, Charset charset) {
        return new String(takesBytes(len), charset);
    }

    public String takeString(Charset charset) {
        return new String(takesBytes(data.length - dataPos), charset);
    }

    public byte peekByte() {
        return data[dataPos + peekPos++];
    }

    public byte[] peekRemainingBytes() {
        int len = data.length - dataPos - peekPos;
        byte[] result = new byte[len];
        System.arraycopy(data, dataPos + peekPos, result, 0, len);
        return result;
    }

    public int getRemainingBytes() {
        return data.length - dataPos;
    }

}
