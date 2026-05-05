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

    public static AssertionException createFail(final String pattern, final Object... args) {
        return new AssertionException(MessageFormatUtil.format(pattern, args));
    }

    public static AssertionException createNotReachable() {
        return createNotReachable("not reachable"); //$NON-NLS-1$
    }

    public static AssertionException createNotReachable(final String pattern, final Object... args) {
        return createFail(pattern, args);
    }

    public static <T> void equals(final T object1, final T object2) {
        equals(object1, object2, "{0} not equals to {1}", object1, object2); //$NON-NLS-1$
    }

    public static <T> void equals(final T object1, final T object2, final String pattern, final Object... args) {
        if (!Objects.equals(object1, object2)) {
            fail(pattern, args);
        }
    }

    public static AssertionException fail(final String pattern, final Object... args) {
        throw createFail(pattern, args);
    }

    public static void greater(final int lowerBoundExclusive, final int value) {
        greater(lowerBoundExclusive, value, "{0} is not greater than {1}", value, lowerBoundExclusive);
    }

    public static void greater(final int lowerBoundExclusive, final int value, final String pattern, final Object... args) {
        isTrue(value > lowerBoundExclusive, pattern, args);
    }

    public static void greater(final long lowerBoundExclusive, final long value) {
        greater(lowerBoundExclusive, value, "{0} is not greater than {1}", value, lowerBoundExclusive);
    }

    public static void greater(final long lowerBoundExclusive, final long value, final String pattern, final Object... args) {
        isTrue(value > lowerBoundExclusive, pattern, args);
    }

    public static void greater(final byte lowerBoundExclusive, final byte value) {
        greater(lowerBoundExclusive, value, "{0} is not greater than {1}", value, lowerBoundExclusive);
    }

    public static void greater(final byte lowerBoundExclusive, final byte value, final String pattern, final Object... args) {
        isTrue(value > lowerBoundExclusive, pattern, args);
    }

    public static void greater(final short lowerBoundExclusive, final short value) {
        greater(lowerBoundExclusive, value, "{0} is not greater than {1}", value, lowerBoundExclusive);
    }

    public static void greater(final short lowerBoundExclusive, final short value, final String pattern, final Object... args) {
        isTrue(value > lowerBoundExclusive, pattern, args);
    }

    public static void greaterOrEqual(final int lowerBound, final int value) {
        greaterOrEqual(lowerBound, value, "{0} is less than {1}", value, lowerBound);
    }

    public static void greaterOrEqual(final int lowerBound, final int value, final String pattern, final Object... args) {
        isTrue(value >= lowerBound, pattern, args);
    }

    public static void greaterOrEqual(final long lowerBound, final long value) {
        greaterOrEqual(lowerBound, value, "{0} is less than {1}", value, lowerBound);
    }

    public static void greaterOrEqual(final long lowerBound, final long value, final String pattern, final Object... args) {
        isTrue(value >= lowerBound, pattern, args);
    }

    public static void greaterOrEqual(final byte lowerBound, final byte value) {
        greaterOrEqual(lowerBound, value, "{0} is less than {1}", value, lowerBound);
    }

    public static void greaterOrEqual(final byte lowerBound, final byte value, final String pattern, final Object... args) {
        isTrue(value >= lowerBound, pattern, args);
    }

    public static void greaterOrEqual(final short lowerBound, final short value) {
        greaterOrEqual(lowerBound, value, "{0} is less than {1}", value, lowerBound);
    }

    public static void greaterOrEqual(final short lowerBound, final short value, final String pattern, final Object... args) {
        isTrue(value >= lowerBound, pattern, args);
    }

    public static void isFalse(final boolean condition) {
        isFalse(condition, "value is true");
    }

    public static void isFalse(final boolean condition, final String pattern, final Object... args) {
        if (condition) {
            fail(pattern, args);
        }
    }

    public static void isNull(final Object value) {
        isNull(value, "value is not null");
    }

    public static void isNull(final Object value, final String pattern, final Object... args) {
        isTrue(value == null, pattern, args);
    }

    public static void isTrue(final boolean condition) {
        isTrue(condition, "value is false");
    }

    public static void isTrue(final boolean condition, final String pattern, final Object... args) {
        if (!condition) {
            fail(pattern, args);
        }
    }

    public static void less(final int upperBoundExclusive, final int value) {
        less(upperBoundExclusive, value, "{0} is not less than {1}", value, upperBoundExclusive);
    }

    public static void less(final int upperBoundExclusive, final int value, final String pattern, final Object... args) {
        isTrue(value < upperBoundExclusive, pattern, args);
    }

    public static void less(final long upperBoundExclusive, final long value) {
        less(upperBoundExclusive, value, "{0} is not less than {1}", value, upperBoundExclusive);
    }

    public static void less(final long upperBoundExclusive, final long value, final String pattern, final Object... args) {
        isTrue(value < upperBoundExclusive, pattern, args);
    }

    public static void less(final byte upperBoundExclusive, final byte value) {
        less(upperBoundExclusive, value, "{0} is not less than {1}", value, upperBoundExclusive);
    }

    public static void less(final byte upperBoundExclusive, final byte value, final String pattern, final Object... args) {
        isTrue(value < upperBoundExclusive, pattern, args);
    }

    public static void less(final short upperBoundExclusive, final short value) {
        less(upperBoundExclusive, value, "{0} is not less than {1}", value, upperBoundExclusive);
    }

    public static void less(final short upperBoundExclusive, final short value, final String pattern, final Object... args) {
        isTrue(value < upperBoundExclusive, pattern, args);
    }

    public static void lessOrEqual(final int upperBound, final int value) {
        lessOrEqual(upperBound, value, "{0} is greater than {1}", value, upperBound);
    }

    public static void lessOrEqual(final int upperBound, final int value, final String pattern, final Object... args) {
        isTrue(value <= upperBound, pattern, args);
    }

    public static void lessOrEqual(final long upperBound, final long value) {
        lessOrEqual(upperBound, value, "{0} is greater than {1}", value, upperBound);
    }

    public static void lessOrEqual(final long upperBound, final long value, final String pattern, final Object... args) {
        isTrue(value <= upperBound, pattern, args);
    }

    public static void lessOrEqual(final byte upperBound, final byte value) {
        lessOrEqual(upperBound, value, "{0} is greater than {1}", value, upperBound);
    }

    public static void lessOrEqual(final byte upperBound, final byte value, final String pattern, final Object... args) {
        isTrue(value <= upperBound, pattern, args);
    }

    public static void lessOrEqual(final short upperBound, final short value) {
        lessOrEqual(upperBound, value, "{0} is greater than {1}", value, upperBound);
    }

    public static void lessOrEqual(final short upperBound, final short value, final String pattern, final Object... args) {
        isTrue(value <= upperBound, pattern, args);
    }

    public static @Nonnull String notBlank(final String string) {
        return notBlank(string, "blank");
    }

    public static @Nonnull String notBlank(final String string, final String pattern, final Object... args) {
        isFalse(StringUtils.isBlank(string), pattern, args);
        return string;
    }

    public static @Nonnull String notEmpty(final String string) {
        return notEmpty(string, "empty");
    }

    public static @Nonnull String notEmpty(final String string, final String pattern, final Object... args) {
        isFalse(StringUtils.isEmpty(string), pattern, args);
        return string;
    }

    public static @Nonnull <C extends Collection<?>> C notEmpty(final C collection) {
        return notEmpty(collection, "empty");
    }

    public static <C extends Collection<?>> C notEmpty(final C collection, final String pattern, final Object... args) {
        isFalse(collection.isEmpty(), pattern, args);
        return collection;
    }

    public static <T> void notEquals(final T object1, final T object2) {
        notEquals(object1, object2, "{0} equals to {1}", object1, object2); //$NON-NLS-1$
    }

    public static <T> void notEquals(final T object1, final T object2, final String pattern, final Object... args) {
        isFalse(Objects.equals(object1, object2), pattern, args);
    }

    public static <T> T notNull(final T value) {
        return notNull(value, "value is null");
    }

    public static <T> T notNull(final T value, final String pattern, final Object... args) {
        isTrue(value != null, pattern, args);
        return value;
    }

    public static <T> T same(final T object1, final T object2) {
        return same(object1, object2, "{0} not same to {1}", object1, object2);
    }

    public static <T> T same(final T object1, final T object2, final String pattern, final Object... args) {
        isTrue(object1 == object2, pattern, args);
        return object1;
    }

    public static String[] splittable(final String string, final char splitChar, final int expectedPartCount) {
        notNull(string);
        final List<String> strings = Splitter.on(splitChar).splitToList(string);
        final String[] result = strings.toArray(new String[strings.size()]);
        equals(expectedPartCount, result.length);
        return result;
    }

    public static AssertionException unhandledEnum(final Enum<?> enumValue) {
        throw createFail("unhandled enum value: {0}", enumValue); //$NON-NLS-1$
    }

    public static void instanceOf(final Object object, final Class<?> clazz) {
        instanceOf(object, clazz, "{0} not instance of {1}", object, clazz);
    }

    public static void instanceOf(final Object object, final Class<?> clazz, final String pattern, final Object... args) {
        if (object != null && !clazz.isInstance(object)) {
            AssertUtil.fail(pattern, args);
        }
    }

    public static String[] matches(final String pattern, final String input) {
        return matches(Pattern.compile(pattern), input);
    }

    public static String[] matches(final Pattern pattern, final String input) {
        final Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            throw AssertUtil.createFail("value {0}: not matching pattern {1}", input, pattern);
        }
        final int groupCount = matcher.groupCount();
        final String[] result = new String[groupCount];
        for (int groupIndex = 0; groupIndex < groupCount; groupIndex++) {
            result[groupIndex] = matcher.group(groupIndex + 1);
        }
        return result;
    }

}
