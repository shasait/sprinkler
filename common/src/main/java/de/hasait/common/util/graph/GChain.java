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

package de.hasait.common.util.graph;

import java.util.Objects;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class GChain<N, E> implements Comparable<GChain<N, E>> {

	private final GChain<N, E> _previous;

	private final int _size;

	private final E _edge;

	private final N _destination;

	private final long _edgeLength;

	private final long _totalLength;

	public GChain(@Nonnull GChain<N, E> pPrevious, E pEdge, N pDestination, long pEdgeLength) {
		super();
		Objects.requireNonNull(pPrevious, "pPrevious");

		_previous = pPrevious;
		_size = _previous._size + 1;
		_edge = pEdge;
		_destination = pDestination;
		_edgeLength = pEdgeLength;
		_totalLength = _previous._totalLength + _edgeLength;
	}

	public GChain(N pOrigin) {
		super();

		_previous = null;
		_size = 1;
		_edge = null;
		_destination = pOrigin;
		_edgeLength = 0;
		_totalLength = 0;
	}

	@Override
	public int compareTo(GChain<N, E> pOther) {
		if (pOther == null) {
			return -1;
		}

		int result;

		result = Long.valueOf(_totalLength).compareTo(Long.valueOf(pOther._totalLength));
		if (result != 0) {
			return result;
		}

		result = Integer.valueOf(_size).compareTo(Integer.valueOf(pOther._size));
		if (result != 0) {
			return result;
		}

		return Integer.valueOf(hashCode()).compareTo(Integer.valueOf(pOther.hashCode()));
	}

	@Override
	public boolean equals(Object pOther) {
		if (pOther == this) {
			return true;
		}
		if (!(pOther instanceof GChain<?, ?> other)) {
			return false;
		}

        if (_size != other._size) {
			return false;
		}
		if (_edgeLength != other._edgeLength) {
			return false;
		}
		if (_totalLength != other._totalLength) {
			return false;
		}
		if (!Objects.equals(_destination, other._destination)) {
			return false;
		}
		if (!Objects.equals(_edge, other._edge)) {
			return false;
		}
        return Objects.equals(_previous, other._previous);
    }

	public N getDestination() {
		return _destination;
	}

	public E getEdge() {
		return _edge;
	}

	public long getEdgeLength() {
		return _edgeLength;
	}

	public GChain<N, E> getPrevious() {
		return _previous;
	}

	public int getSize() {
		return _size;
	}

	public long getTotalLength() {
		return _totalLength;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(_size).append(_totalLength).append(_destination).append(_edge).toHashCode();
	}

	@Override
	public String toString() {
		return (_previous != null ? _previous + "\n -> " : "") + _destination //
				+ " [" + _edge + "]" //
				+ " (length=" + _totalLength + ")";
	}

}
