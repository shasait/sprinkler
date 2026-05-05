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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Unit {

    B1024(null, 0, "B"),
    KIB(B1024, 1024, "KiB"),
    MIB(KIB, 1024, "MiB"),
    GIB(MIB, 1024, "GiB"),
    TIB(GIB, 1024, "TiB"),
    PIB(TIB, 1024, "PiB"),
    EIB(PIB, 1024, "EiB"),
    ZIB(EIB, 1024, "ZiB"),
    YIB(ZIB, 1024, "YiB"),

    B1000(null, 0, "B"),
    KB(B1000, 1000, "KB"),
    MB(KB, 1000, "MB"),
    GB(MB, 1000, "GB"),
    TB(GB, 1000, "TB"),
    PB(TB, 1000, "PB"),
    EB(PB, 1000, "EB"),
    ZB(EB, 1000, "ZB"),
    YB(ZB, 1000, "YB"),

    NS(null, 0, "ns"),
    US(NS, 1000, "us"),
    MS(US, 1000, "ms"),
    S(MS, 1000, "s"),
    MINUTE(S, 60, "m"),
    HOUR(MINUTE, 60, "h"),
    DAY(HOUR, 24, "d"),
    WEEK(DAY, 7, "w"),
    YEAR(WEEK, 52, "y"),
    CENTURY(YEAR, 100, "cen"),
    EON(CENTURY, 10000000, "eon");

    private final Unit smallerUnit;
    private final int smallerFactor;

    private final String name;
    private final Pattern pattern;

    private Unit biggerUnit;

    Unit(Unit smallerUnit, int smallerFactor, String name) {
        this.smallerUnit = smallerUnit;
        this.smallerFactor = smallerFactor;
        this.name = name;
        this.pattern = Pattern.compile("(\\d+)\\Q" + name + "\\E");

        if (smallerUnit != null) {
            if (smallerUnit.biggerUnit != null) {
                throw new IllegalArgumentException(smallerUnit + " already has a biggerUnit: " + smallerUnit.biggerUnit);
            }
            smallerUnit.biggerUnit = this;
        }
    }

    public Unit getSmallerUnit() {
        return smallerUnit;
    }

    public int getSmallerFactor() {
        return smallerFactor;
    }

    public String getName() {
        return name;
    }

    public Unit getBiggerUnit() {
        return biggerUnit;
    }

    public long convertTo(Unit targetUnit, long amount) {
        if (targetUnit == null) {
            throw new IllegalArgumentException("targetUnit cannot be null");
        }
        if (targetUnit == this) {
            return amount;
        }
        if (smallerUnit == null) {
            throw new IllegalArgumentException("invalid targetUnit");
        }
        return smallerUnit.convertTo(targetUnit, amount * smallerFactor);
    }

    @Override
    public String toString() {
        return name;
    }

    public String toHuman(long longValue) {
        return toHuman(longValue, Integer.MAX_VALUE);
    }

    public String toHuman(long longValue, int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("limit < 1: " + limit);
        }
        if (longValue == 0) {
            return "0" + name;
        }

        if (longValue > 0) {
            return toHumanPositive(longValue, limit);
        } else {
            return "-" + toHumanPositive(-longValue, limit);
        }
    }

    public long fromHuman(String stringValue) {
        if (stringValue.startsWith("-")) {
            return -fromHumanPositive(stringValue.substring(1));
        }
        return fromHumanPositive(stringValue);
    }

    private String toHumanPositive(long longValue, int limit) {
        List<String> result = new ArrayList<>();
        toHuman(longValue, 1, result, limit);
        return String.join(" ", result);
    }

    private long toHuman(long longValue, long unitValue, List<String> result, int limit) {
        long remaining = toHumanBiggerUnit(longValue, unitValue, result, limit);

        if (remaining >= unitValue) {
            long units = remaining / unitValue;
            if (result.size() < limit) {
                result.add(units + name);
            }
            return remaining - units * unitValue;
        }

        return remaining;
    }

    private long toHumanBiggerUnit(long longValue, long unitValue, List<String> result, int limit) {
        if (biggerUnit != null) {
            long biggerUnitValue;
            try {
                biggerUnitValue = Math.multiplyExact(unitValue, biggerUnit.smallerFactor);
            } catch (ArithmeticException e) {
                return longValue;
            }
            if (longValue >= biggerUnitValue) {
                return biggerUnit.toHuman(longValue, biggerUnitValue, result, limit);
            }
        }
        return longValue;
    }

    private long fromHumanPositive(String stringValue) {
        String[] split = stringValue.split(" ");
        List<Long> result = new ArrayList<>();
        int index = fromHuman(split, 0, 1, result);
        if (index != split.length) {
            throw new IllegalArgumentException("Cannot parse: " + stringValue);
        }
        long sum = 0;
        for (long value : result) {
            sum = Math.addExact(sum, value);
        }
        return sum;
    }

    private int fromHuman(String[] split, int currentIndex, long unitValue, List<Long> result) {
        int index0 = fromHumanHere(split, currentIndex, unitValue, result);
        if (index0 != currentIndex) {
            return index0;
        }
        int index1 = fromHumanBiggerUnit(split, index0, unitValue, result);
        if (index1 == split.length) {
            return index1;
        }
        return fromHumanHere(split, index1, unitValue, result);
    }

    private int fromHumanHere(String[] split, int currentIndex, long unitValue, List<Long> result) {
        String s = split[currentIndex];
        Matcher matcher = pattern.matcher(s);
        if (matcher.matches()) {
            long units = Long.parseLong(matcher.group(1));
            result.add(units * unitValue);
            return currentIndex + 1;
        }
        return currentIndex;
    }

    private int fromHumanBiggerUnit(String[] split, int currentIndex, long unitValue, List<Long> result) {
        if (biggerUnit != null) {
            long biggerUnitValue;
            try {
                biggerUnitValue = Math.multiplyExact(unitValue, biggerUnit.smallerFactor);
            } catch (ArithmeticException e) {
                return currentIndex;
            }
            return biggerUnit.fromHuman(split, currentIndex, biggerUnitValue, result);
        }
        return currentIndex;
    }

}
