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

import org.slf4j.Logger;
import org.slf4j.event.Level;

/**
 *
 */
public final class StringUtil {

    private static final HexFormat HEX_FORMAT_SPACE = HexFormat.ofDelimiter(" ").withUpperCase();
    private static final HexFormat HEX_FORMAT_NOSPACE = HexFormat.ofDelimiter("").withUpperCase();

    private static final Pattern TRUE_PATTERN = Pattern.compile("on|yes|true|enable|active", Pattern.CASE_INSENSITIVE);

    /**
     * Parse to <code>boolean</code>.
     *
     * @return <code>true</code> if ignore case "on", "yes", "true", "enable", "active"; else <code>false</code>.
     */
    public static boolean toBoolean(String string) {
        return string != null && TRUE_PATTERN.matcher(string).matches();
    }

    /**
     * Parse to {@link Integer}.
     *
     * @return The {@link Integer} or <code>null</code> if not parseable.
     */
    public static Integer toInteger(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String unsignedBytesToHex(String pSplit, int... pUnsignedBytes) {
        final StringBuilder sb = new StringBuilder();
        if (pUnsignedBytes != null) {
            boolean first = true;
            for (int unsignedByte : pUnsignedBytes) {
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

    public static String booleansToString(boolean... bits) {
        return booleansToString('1', '0', bits);
    }

    public static String booleansToString(char trueChar, char falseChar, boolean... bits) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bits.length; i++) {
            sb.append(bits[i] ? trueChar : falseChar);
        }
        return sb.toString();
    }

    public static String booleansWithNullToString(char nullChar, Boolean... bits) {
        return booleansWithNullToString('1', '0', nullChar, bits);
    }

    public static String booleansWithNullToString(char trueChar, char falseChar, char nullChar, Boolean... bits) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bits.length; i++) {
            Boolean bit = bits[i];
            if (bit != null) {
                sb.append(bit ? trueChar : falseChar);
            } else {
                sb.append(nullChar);
            }
        }
        return sb.toString();
    }

    public static String byteToHex(byte data) {
        return HEX_FORMAT_SPACE.toHexDigits(data);
    }

    public static String bytesToHex(byte[] data) {
        if (data == null) {
            return null;
        }
        return HEX_FORMAT_SPACE.formatHex(data);
    }

    public static String bytesToHexNoSpace(byte[] data) {
        if (data == null) {
            return null;
        }
        return HEX_FORMAT_NOSPACE.formatHex(data);
    }

    public static String bytesToHex(byte[] data, int offset) {
        if (data == null) {
            return null;
        }
        return HEX_FORMAT_SPACE.formatHex(data, offset, data.length);
    }

    public static String bytesToHex(byte[] data, int offset, int len) {
        if (data == null) {
            return null;
        }
        return HEX_FORMAT_SPACE.formatHex(data, offset, offset + len);
    }

    public static byte[] hexToBytes(String string) {
        return HEX_FORMAT_SPACE.parseHex(string);
    }

    public static byte[] hexToBytesNoSpace(String string) {
        return HEX_FORMAT_NOSPACE.parseHex(string);
    }

    public static byte hexToByte(String string) {
        byte[] bytes = HEX_FORMAT_SPACE.parseHex(string);
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

    public static Byte bitsToByte(String bitsAs01WithMsbLeft) {
        if (bitsAs01WithMsbLeft == null) {
            return null;
        }
        int unsignedInt = 0;
        int bitValue = 1;
        int l = bitsAs01WithMsbLeft.length() - 1;
        for (int i = 0; i <= l; i++) {
            char c = bitsAs01WithMsbLeft.charAt(l - i);
            if (c == '1') {
                unsignedInt += bitValue;
            }
            bitValue <<= 1;
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

    public static void logBytes(Logger logger, Level level, String msgPrefix, byte[] bytes) {
        if (logger.isEnabledForLevel(level)) {
            logger.atLevel(level).log(msgPrefix + StringUtil.bytesToHex(bytes) + " " + StringUtil.bytesToAscii(bytes));
        }
    }

    private StringUtil() {
        super();
    }

}
