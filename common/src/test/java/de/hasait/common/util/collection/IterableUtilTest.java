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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import de.hasait.common.util.collection.IterableUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 *
 */
public class IterableUtilTest {

    @Test
    public void lazyIterableToStringForLoggingBoundaryExtreme1() throws Exception {
        assertEquals("HashSet (size=3)", IterableUtil.lazyIterableToStringForLogging(Sets.newHashSet("A", "B", "C"), 0).toString());
    }

    @Test
    public void lazyIterableToStringForLoggingBoundaryExtreme2() throws Exception {
        assertEquals("HashSet (size=3)", IterableUtil.lazyIterableToStringForLogging(Sets.newHashSet("A", "B", "C"), -10).toString());
    }

    @Test
    public void lazyIterableToStringForLoggingEllipsis() throws Exception {
        assertEquals("HashSet[\n" + "0=A\n" + "1=B\n" + "...\n" + "] (size=3)",
                IterableUtil.lazyIterableToStringForLogging(Sets.newHashSet("A", "B", "C"), 2).toString()
        );
    }

    @Test
    public void lazyIterableToStringForLoggingEmpty() throws Exception {
        assertEquals("ArrayList[] (size=0)", IterableUtil.lazyIterableToStringForLogging(Lists.newArrayList(), 10).toString());
    }

    @Test
    public void lazyIterableToStringForLoggingGoodCase() throws Exception {
        assertEquals("ArrayList[\n" + "0=A\n" + "1=B\n" + "] (size=2)",
                IterableUtil.lazyIterableToStringForLogging(Lists.newArrayList("A", "B"), 10).toString()
        );
    }

    @Test
    public void lazyIterableToStringForLoggingNull() throws Exception {
        assertEquals("null", IterableUtil.lazyIterableToStringForLogging(null, 2).toString());
    }

}