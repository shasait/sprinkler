/*
 * Copyright (C) 2022 by Sebastian Hasait (sebastian at hasait dot de)
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

package de.hasait.sprinkler.service.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;

public class Util {

    private static final List<Pair<Long, String>> UNITS;

    static {
        List<Pair<Long, String>> units = new ArrayList<>();
        units.add(Pair.of(TimeUnit.DAYS.toMillis(365), "y"));
        units.add(Pair.of(TimeUnit.DAYS.toMillis(1), "d"));
        units.add(Pair.of(TimeUnit.HOURS.toMillis(1), "h"));
        units.add(Pair.of(TimeUnit.MINUTES.toMillis(1), "m"));
        units.add(Pair.of(TimeUnit.SECONDS.toMillis(1), "s"));
        units.add(Pair.of(1L, "ms"));
        UNITS = Collections.unmodifiableList(units);
    }

    public static String millisToHuman(Date seed, Date next, int limit) {
        long millis = next.getTime() - seed.getTime();
        return millisToHuman(millis, limit);
    }

    public static String millisToHuman(long millis, int limit) {
        List<String> result = new ArrayList<>();

        Iterator<Pair<Long, String>> i = UNITS.iterator();
        while (i.hasNext() && millis > 0 && result.size() < limit) {
            Pair<Long, String> pair = i.next();
            millis = appendUnit(millis, pair.getLeft(), pair.getRight(), result);
        }

        return String.join(" ", result);
    }

    private static long appendUnit(long millis, long unitMillis, String unit, List<String> result) {
        if (millis >= unitMillis) {
            long units = millis / unitMillis;
            result.add(units + unit);
            return millis - units * unitMillis;
        }
        return millis;
    }

}
