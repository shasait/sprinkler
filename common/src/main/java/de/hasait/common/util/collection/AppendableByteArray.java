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

import java.nio.charset.StandardCharsets;

import de.hasait.common.util.StringUtil;

public class AppendableByteArray {

    private final byte[] data;

    private int dataPos;

    public AppendableByteArray(int maxDataLen) {
        this.data = new byte[maxDataLen];
    }

    public AppendableByteArray(byte[] data) {
        this.data = data;
    }

    public final byte[] getData() {
        return data;
    }

    public final int getDataPos() {
        return dataPos;
    }

    public final void appendUnsignedIntAsByte(int data) {
        this.data[dataPos++] = (byte) data;
    }

    public final void appendByte(byte data) {
        this.data[dataPos++] = data;
    }

    public final void appendStringBytes(String data) {
        appendBytes(data.getBytes(StandardCharsets.ISO_8859_1));
    }

    public final void appendStringBytesAndLen(String data) {
        appendUnsignedIntAsByte(data.length());
        appendBytes(data.getBytes(StandardCharsets.ISO_8859_1));
    }

    public final void appendHexBytes(String hexString) {
        appendBytes(StringUtil.hexToBytes(hexString));
    }

    public final void appendInt16(int data) {
        for (int i = 0; i <= 1; i++) {
            appendByte((byte) ((data >> 8 * i) & 0xFF));
        }
    }

    public final void appendBytes(byte[] data) {
        System.arraycopy(data, 0, this.data, dataPos, data.length);
        dataPos += data.length;
    }

    public final void appendBytes(byte[] data, int offset, int len) {
        System.arraycopy(data, offset, this.data, dataPos, len);
        dataPos += len;
    }

    public final void append(ByteConvertable convertable) {
        convertable.appendTo(this);
    }

}
