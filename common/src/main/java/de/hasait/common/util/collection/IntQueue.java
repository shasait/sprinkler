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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.ArrayUtils;

import de.hasait.common.util.AssertUtil;

public class IntQueue {

    private final LinkedList<int[]> _intArrayLinkedList = new LinkedList<>();

    private int _size = 0;

    private int _index = 0;

    public void append(@Nullable int... pData) {
        if (ArrayUtils.isEmpty(pData)) {
            return;
        }
        _intArrayLinkedList.add(pData);
        _size += pData.length;
    }

    public boolean canTake(int pCount) {
        return pCount <= _size;
    }

    public void discard(int pCount) {
        AssertUtil.greaterOrEqual(0, pCount);
        AssertUtil.lessOrEqual(_size, pCount);

        for (int i = 0; i < pCount; i++) {
            take();
        }
    }

    public boolean isEmpty() {
        return _size == 0;
    }

    public Iterator<Integer> iterator() {
        return new Iterator<>() {
            private final Iterator<int[]> _intArrayI = _intArrayLinkedList.iterator();
            private int _i = _index;
            private int[] _intArray = _intArrayI.hasNext() ? _intArrayI.next() : null;

            @Override
            public boolean hasNext() {
                return _intArray != null || _intArrayI.hasNext();
            }

            @Override
            public Integer next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                if (_intArray == null) {
                    _intArray = _intArrayI.next();
                    _i = 0;
                }

                final int result = _intArray[_i++];

                if (_i >= _intArray.length) {
                    _intArray = null;
                }

                return result;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public int[][] peek(int... counts) {
        final Iterator<Integer> iterator = iterator();
        final int[][] result = new int[counts.length][];
        for (int c = 0; c < counts.length; c++) {
            result[c] = new int[counts[c]];
            for (int i = 0; i < counts[c]; i++) {
                result[c][i] = iterator.next();
            }
        }
        return result;
    }

    public int[] peekAll() {
        return peek(_size)[0];
    }

    public int size() {
        return _size;
    }

    public boolean startsWith(int... pPrefix) {
        if (ArrayUtils.isEmpty(pPrefix)) {
            return true;
        }
        if (_size < pPrefix.length) {
            return false;
        }

        final Iterator<Integer> iterator = iterator();
        for (int prefix : pPrefix) {
            if (!iterator.hasNext()) {
                return false;
            }
            if (iterator.next() != prefix) {
                return false;
            }
        }
        return true;
    }

    public boolean startsWithOrLess(int[]... prefixes) {
        if (ArrayUtils.isEmpty(prefixes)) {
            return true;
        }
        final Iterator<Integer> iterator = iterator();
        for (int[] prefix : prefixes) {
            for (int value : prefix) {
                if (!iterator.hasNext()) {
                    return true;
                }
                if (iterator.next() != value) {
                    return false;
                }
            }
        }
        return true;
    }

    public int take() {
        AssertUtil.greater(0, _size);

        final int[] first = _intArrayLinkedList.getFirst();
        final int result = first[_index];

        if (_index + 1 < first.length) {
            _index++;
        } else {
            _intArrayLinkedList.removeFirst();
            _index = 0;
        }

        _size--;
        return result;
    }

    public @Nonnull
    int[] take(int pCount) {
        AssertUtil.greaterOrEqual(0, pCount);
        AssertUtil.lessOrEqual(_size, pCount);

        final int[] result = new int[pCount];
        for (int i = 0; i < pCount; i++) {
            result[i] = take();
        }
        return result;
    }

    public @Nonnull
    int[] takeAll() {
        return take(_size);
    }

}
