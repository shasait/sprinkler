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

import java.util.HexFormat;
import java.util.regex.Pattern;

/**
 *
 */
public final class StringUtil {

    private static final HexFormat HEX_FORMAT = HexFormat.ofDelimiter(" ").withUpperCase();

    private static final Pattern TRUE_PATTERN = Pattern.compile("on|yes|true|enable|active", Pattern.CASE_INSENSITIVE);

    /**
     * Parse to <code>boolean</code>.
     *
     * @return <code>true</code> if ignore case "on", "yes", "true", "enable", "active"; else <code>false</code>.
     */
    public static boolean toBoolean(final String pString) {
        return pString != null && TRUE_PATTERN.matcher(pString).matches();
    }

    /**
     * Parse to {@link Integer}.
     *
     * @return The {@link Integer} or <code>null</code> if not parseable.
     */
    public static Integer toInteger(final String pString) {
        try {
            return Integer.parseInt(pString);
        } catch (final NumberFormatException pE) {
            return null;
        }
    }

    public static String unsignedBytesToHex(final String pSplit, final int... pUnsignedBytes) {
        final StringBuilder sb = new StringBuilder();
        if (pUnsignedBytes != null) {
            boolean first = true;
            for (final int unsignedByte : pUnsignedBytes) {
                if (first) {
                    first = false;
                } else if (pSplit != null) {
                    sb.append(pSplit);
                }
                final String hexString = Integer.toHexString(unsignedByte);
                if (hexString.length() < 2) {
                    sb.append('0');
                }
                sb.append(hexString);
            }
        }
        return sb.toString();
    }

    public static String byteToHex(byte data) {
        return HEX_FORMAT.toHexDigits(data);
    }

    public static String bytesToHex(byte[] data) {
        return HEX_FORMAT.formatHex(data);
    }

    public static String bytesToHex(byte[] data, int offset) {
        return HEX_FORMAT.formatHex(data, offset, data.length);
    }

    public static String bytesToHex(byte[] data, int offset, int len) {
        return HEX_FORMAT.formatHex(data, offset, offset + len);
    }

    public static byte[] hexToBytes(String string) {
        return HEX_FORMAT.parseHex(string);
    }

    public static byte hexToByte(String string) {
        byte[] bytes = HEX_FORMAT.parseHex(string);
        if (bytes.length != 1) {
            throw new IllegalArgumentException();
        }
        return bytes[0];
    }

    public static String bytesToAscii(byte[] data) {
        return bytesToAscii(data, 0, data.length);
    }

    public static String bytesToAscii(byte[] data, int offset) {
        return bytesToAscii(data, offset, data.length - offset);
    }

    public static String bytesToAscii(byte[] data, int offset, int len) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            byte rawByte = data[offset + i];
            int unsignedByte = Byte.toUnsignedInt(rawByte);
            if (unsignedByte > 31 && unsignedByte < 127) {
                sb.append((char) unsignedByte);
            } else {
                sb.append('_');
            }
        }
        return sb.toString();
    }

    public static Byte bits07ToByte(String bits0to7as01char) {
        if (bits0to7as01char == null) {
            return null;
        }
        int unsignedInt = 0;
        int v = 1;
        for (int i = 0; i < bits0to7as01char.length(); i++) {
            char c = bits0to7as01char.charAt(i);
            if (c == '1') {
                unsignedInt += v;
            }
            v *= 2;
        }
        return (byte) unsignedInt;
    }

    public static String joinNonNull(String delimiter, String... strings) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String string : strings) {
            if (string != null) {
                if (first) {
                    first = false;
                } else {
                    sb.append(delimiter);
                }
                sb.append(string);
            }
        }
        return sb.toString();
    }

    public static String toStringForByteArray(String what, byte[] data, boolean includeAscii) {
        return toStringForByteArray(what, data, includeAscii, 0);
    }

    public static String toStringForByteArray(String what, byte[] data, boolean includeAscii, int offset) {
        if (data == null) {
            return what + "=null";
        }
        return what + "=[" + bytesToHex(data, offset) + "]" + (includeAscii ? " (" + bytesToAscii(data, offset) + ")" : "");
    }

    public static String toStringForByte(String what, byte data) {
        return what + "=" + Byte.toUnsignedInt(data) + " (" + StringUtil.byteToHex(data) + ")";
    }

    private StringUtil() {
        super();
    }

}
