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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;


/**
 * Assertions.
 */
public final class AssertUtil {

    private AssertUtil() {
        super();
    }

    public static AssertionException createFail(String pattern, Object... args) {
        return new AssertionException(MessageFormatUtil.format(pattern, args));
    }

    public static AssertionException createNotReachable() {
        return createNotReachable("not reachable"); //$NON-NLS-1$
    }

    public static AssertionException createNotReachable(String pattern, Object... args) {
        return createFail(pattern, args);
    }

    public static AssertionException fail(String pattern, Object... args) {
        throw createFail(pattern, args);
    }

    public static void isFalse(boolean condition) {
        isFalse(condition, "value has to be false");
    }

    public static void isFalse(boolean condition, String pattern, Object... args) {
        if (condition) {
            fail(pattern, args);
        }
    }

    public static void isTrue(boolean condition) {
        isTrue(condition, "value has to be true");
    }

    public static void isTrue(boolean condition, String pattern, Object... args) {
        if (!condition) {
            fail(pattern, args);
        }
    }

    public static void isNull(Object value) {
        isNull(value, "value");
    }

    public static void isNull(Object value, String valueDescription) {
        isNullWithPattern(value, "{0} has to be null", valueDescription);
    }

    public static void isNullWithPattern(Object value, String pattern, Object... args) {
        isTrue(value == null, pattern, args);
    }

    public static <T> T notNull(T value) {
        return notNull(value, "value");
    }

    public static <T> T notNull(T value, String valueDescription) {
        notNullWithPattern(value, "{0} cannot be null", valueDescription);
        return value;
    }

    public static <T> T notNullWithPattern(T value, String pattern, Object... args) {
        isTrue(value != null, pattern, args);
        return value;
    }

    public static <T> void equals(T object1, T object2) {
        equals(object1, "object1", object2, "object2");
    }

    public static <T> void equals(T object1, String object1Description, T object2, String object2Description) {
        isTrue(Objects.equals(object1, object2), "{0} ({1}) has to be equal to {1} ({2})", object1, object1Description, object2, object2Description);
    }

    public static <T> void notEquals(T object1, T object2) {
        notEquals(object1, "object1", object2, "object2");
    }

    public static <T> void notEquals(T object1, String object1Description, T object2, String object2Description) {
        isFalse(Objects.equals(object1, object2), "{0} ({1}) cannot be equal to {1} ({2})", object1, object1Description, object2, object2Description);
    }

    public static <T> T same(T object1, T object2) {
        return same(object1, "object1", object2, "object2");
    }

    public static <T> T same(T object1, String object1Description, T object2, String object2Description) {
        isTrue(object1 == object2, "{0} ({1}) has to be the same as {1} ({2})", object1, object1Description, object2, object2Description);
        return object1;
    }

    public static @Nonnull String notBlank(String string) {
        return notBlank(string, "string");
    }

    public static @Nonnull String notBlank(String string, String stringDescription) {
        isTrue(StringUtils.isNotBlank(string), "{0} cannot be blank", stringDescription);
        return string;
    }

    public static @Nonnull String notEmpty(String string) {
        return notEmpty(string, "string");
    }

    public static @Nonnull String notEmpty(String string, String stringDescription) {
        isTrue(StringUtils.isNotEmpty(string), "{0} cannot be empty", stringDescription);
        return string;
    }

    public static @Nonnull <C extends Collection<?>> C notEmpty(C collection) {
        return notEmpty(collection, "collection");
    }

    public static <C extends Collection<?>> C notEmpty(C collection, String collectionDescription) {
        notNull(collection, collectionDescription);
        isFalse(collection.isEmpty(), "{0} cannot be empty", collectionDescription);
        return collection;
    }

    public static Matcher matches(String pattern, String string, String stringDescription) {
        return matches(Pattern.compile(pattern), string, stringDescription);
    }

    public static Matcher matches(Pattern pattern, String string, String stringDescription) {
        notNull(string, stringDescription);
        Matcher matcher = pattern.matcher(string);
        isTrue(matcher.matches(), "{0} ({1}) has to match pattern {2}", string, stringDescription, pattern);
        return matcher;
    }

    /**
     * In range excluding both ends - named "Open interval" in math.
     */
    public static int inRangeEE(int value, int gt, int lt, String valueDescription) {
        isTrue(gt < value && value < lt, "{0} ({1}) has to be in interval ({2}:{3})", value, valueDescription, gt, lt);
        return value;
    }

    /**
     * In range including both ends - named "Closed interval" in math.
     */
    public static int inRangeII(int value, int gte, int lte, String valueDescription) {
        isTrue(gte <= value && value <= lte, "{0} ({1}) has to be in interval [{2}:{3}]", value, valueDescription, gte, lte);
        return value;
    }

    public static String[] splittable(String string, char splitChar, int expectedPartCount, String stringDescription) {
        notNull(string, stringDescription);
        final List<String> strings = Splitter.on(splitChar).splitToList(string);
        final String[] result = strings.toArray(new String[0]);
        isTrue(expectedPartCount == result.length, "Splitting {0} ({1}) on {2} has to return {3} parts and not {4}", string, stringDescription, expectedPartCount, result.length);
        return result;
    }

    // old concept

    public static void greater(int lowerBoundExclusive, int value) {
        greater(lowerBoundExclusive, value, "{0} is not greater than {1}", value, lowerBoundExclusive);
    }

    public static void greater(int lowerBoundExclusive, int value, String pattern, Object... args) {
        isTrue(value > lowerBoundExclusive, pattern, args);
    }

    public static void greater(long lowerBoundExclusive, long value) {
        greater(lowerBoundExclusive, value, "{0} is not greater than {1}", value, lowerBoundExclusive);
    }

    public static void greater(long lowerBoundExclusive, long value, String pattern, Object... args) {
        isTrue(value > lowerBoundExclusive, pattern, args);
    }

    public static void greater(byte lowerBoundExclusive, byte value) {
        greater(lowerBoundExclusive, value, "{0} is not greater than {1}", value, lowerBoundExclusive);
    }

    public static void greater(byte lowerBoundExclusive, byte value, String pattern, Object... args) {
        isTrue(value > lowerBoundExclusive, pattern, args);
    }

    public static void greater(short lowerBoundExclusive, short value) {
        greater(lowerBoundExclusive, value, "{0} is not greater than {1}", value, lowerBoundExclusive);
    }

    public static void greater(short lowerBoundExclusive, short value, String pattern, Object... args) {
        isTrue(value > lowerBoundExclusive, pattern, args);
    }

    public static void greaterOrEqual(int lowerBound, int value) {
        greaterOrEqual(lowerBound, value, "{0} is less than {1}", value, lowerBound);
    }

    public static void greaterOrEqual(int lowerBound, int value, String pattern, Object... args) {
        isTrue(value >= lowerBound, pattern, args);
    }

    public static void greaterOrEqual(long lowerBound, long value) {
        greaterOrEqual(lowerBound, value, "{0} is less than {1}", value, lowerBound);
    }

    public static void greaterOrEqual(long lowerBound, long value, String pattern, Object... args) {
        isTrue(value >= lowerBound, pattern, args);
    }

    public static void greaterOrEqual(byte lowerBound, byte value) {
        greaterOrEqual(lowerBound, value, "{0} is less than {1}", value, lowerBound);
    }

    public static void greaterOrEqual(byte lowerBound, byte value, String pattern, Object... args) {
        isTrue(value >= lowerBound, pattern, args);
    }

    public static void greaterOrEqual(short lowerBound, short value) {
        greaterOrEqual(lowerBound, value, "{0} is less than {1}", value, lowerBound);
    }

    public static void greaterOrEqual(short lowerBound, short value, String pattern, Object... args) {
        isTrue(value >= lowerBound, pattern, args);
    }

    public static void less(int upperBoundExclusive, int value) {
        less(upperBoundExclusive, value, "{0} is not less than {1}", value, upperBoundExclusive);
    }

    public static void less(int upperBoundExclusive, int value, String pattern, Object... args) {
        isTrue(value < upperBoundExclusive, pattern, args);
    }

    public static void less(long upperBoundExclusive, long value) {
        less(upperBoundExclusive, value, "{0} is not less than {1}", value, upperBoundExclusive);
    }

    public static void less(long upperBoundExclusive, long value, String pattern, Object... args) {
        isTrue(value < upperBoundExclusive, pattern, args);
    }

    public static void less(byte upperBoundExclusive, byte value) {
        less(upperBoundExclusive, value, "{0} is not less than {1}", value, upperBoundExclusive);
    }

    public static void less(byte upperBoundExclusive, byte value, String pattern, Object... args) {
        isTrue(value < upperBoundExclusive, pattern, args);
    }

    public static void less(short upperBoundExclusive, short value) {
        less(upperBoundExclusive, value, "{0} is not less than {1}", value, upperBoundExclusive);
    }

    public static void less(short upperBoundExclusive, short value, String pattern, Object... args) {
        isTrue(value < upperBoundExclusive, pattern, args);
    }

    public static void lessOrEqual(int upperBound, int value) {
        lessOrEqual(upperBound, value, "{0} is greater than {1}", value, upperBound);
    }

    public static void lessOrEqual(int upperBound, int value, String pattern, Object... args) {
        isTrue(value <= upperBound, pattern, args);
    }

    public static void lessOrEqual(long upperBound, long value) {
        lessOrEqual(upperBound, value, "{0} is greater than {1}", value, upperBound);
    }

    public static void lessOrEqual(long upperBound, long value, String pattern, Object... args) {
        isTrue(value <= upperBound, pattern, args);
    }

    public static void lessOrEqual(byte upperBound, byte value) {
        lessOrEqual(upperBound, value, "{0} is greater than {1}", value, upperBound);
    }

    public static void lessOrEqual(byte upperBound, byte value, String pattern, Object... args) {
        isTrue(value <= upperBound, pattern, args);
    }

    public static void lessOrEqual(short upperBound, short value) {
        lessOrEqual(upperBound, value, "{0} is greater than {1}", value, upperBound);
    }

    public static void lessOrEqual(short upperBound, short value, String pattern, Object... args) {
        isTrue(value <= upperBound, pattern, args);
    }

    public static AssertionException unhandledEnum(Enum<?> enumValue) {
        throw createFail("unhandled enum value: {0}", enumValue); //$NON-NLS-1$
    }

    public static void instanceOf(Object object, Class<?> clazz) {
        instanceOf(object, clazz, "{0} not instance of {1}", object, clazz);
    }

    public static void instanceOf(Object object, Class<?> clazz, String pattern, Object... args) {
        if (object != null && !clazz.isInstance(object)) {
            AssertUtil.fail(pattern, args);
        }
    }

}
