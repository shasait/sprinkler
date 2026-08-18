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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class ParserTree<K, S, R> {

    private final Map<K, Function<S, R>> parsers = new HashMap<>();
    private final Map<K, ParserTree<K, S, R>> subparsers = new HashMap<>();

    private final Function<S, K> keyExtractor;

    public ParserTree(Function<S, K> keyExtractor) {
        this.keyExtractor = keyExtractor;
    }

    public void leaf(K key, Function<S, R> parser) {
        if (subparsers.containsKey(key)) {
            throw new IllegalStateException("Parser tree already contains subparser for key " + key);
        }
        parsers.put(key, parser);
    }

    public ParserTree<K, S, R> node(K key) {
        if (parsers.containsKey(key)) {
            throw new IllegalStateException("Parser tree already contains parser for key " + key);
        }
        return subparsers.computeIfAbsent(key, ignored -> new ParserTree<>(keyExtractor));
    }

    public R parse(S source) {
        K key = keyExtractor.apply(source);
        Function<S, R> parser = parsers.get(key);
        if (parser != null) {
            return parser.apply(source);
        }
        ParserTree<K, S, R> subparser = subparsers.get(key);
        if (subparser != null) {
            return subparser.parse(source);
        }
        return null;
    }

}
