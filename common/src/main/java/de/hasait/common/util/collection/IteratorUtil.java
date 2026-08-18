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
public final class IteratorUtil {

	public static <T> Iterator<T> unmodifiableIterator(Iterator<T> pIterator) {
		if (pIterator == null) {
			return null;
		}

		return new Iterator<T>() {
			@Override
			public boolean hasNext() {
				return pIterator.hasNext();
			}

			@Override
			public T next() {
				return pIterator.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Unmodifiable");
			}
		};
	}

	private IteratorUtil() {
		super();
	}

}
