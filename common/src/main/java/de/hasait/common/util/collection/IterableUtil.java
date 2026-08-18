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

/**
 *
 */
public final class IterableUtil {

	public static Object lazyIterableToStringForLogging(Iterable<?> pIterable, int pMaxElementsToShow) {
		return new LazyIterableToStringForLogging(pIterable, pMaxElementsToShow);
	}

	public static <T> Iterable<T> unmodifiableIterable(Iterable<T> pIterable) {
		if (pIterable == null) {
			return null;
		}

		return () -> IteratorUtil.unmodifiableIterator(pIterable.iterator());
	}

	private IterableUtil() {
		super();
	}

	private record LazyIterableToStringForLogging(Iterable<?> _iterable, int _maxElementsToShow) {

		@Override
			public String toString() {
				if (_iterable == null) {
					return "null";
				}
				final StringBuilder sb = new StringBuilder();
				sb.append(_iterable.getClass().getSimpleName());
				final Iterator<?> iterator = _iterable.iterator();
				if (_maxElementsToShow > 0) {
					sb.append('[');
				}
				int i = 0;
				while (iterator.hasNext()) {
					final Object element = iterator.next();
					if (_maxElementsToShow > 0) {
						if (i < _maxElementsToShow) {
							if (i == 0) {
								sb.append('\n');
							}
							sb.append(i).append('=');
							sb.append(element);
							sb.append('\n');
						} else if (i == _maxElementsToShow) {
							sb.append("...\n");
						}
					}
					i++;
				}
				if (_maxElementsToShow > 0) {
					sb.append(']');
				}
				sb.append(" (size=").append(i).append(")");
				return sb.toString();
			}
		}

}
