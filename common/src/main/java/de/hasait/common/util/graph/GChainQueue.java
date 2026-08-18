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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import jakarta.annotation.Nonnull;

import de.hasait.common.util.AssertUtil;


public class GChainQueue<N, E> {

	private final SortedSet<GChain<N, E>> _chains = new TreeSet<>();

	private final Map<N, GChain<N, E>> _chainsByDestinationNode = new HashMap<>();

	public void add(@Nonnull GChain<N, E> pChain) {
		Objects.requireNonNull(pChain, "pChain");

		AssertUtil.isNull(_chainsByDestinationNode.put(pChain.getDestination(), pChain));
		AssertUtil.isTrue(_chains.add(pChain));
	}

	public GChain<N, E> get(N pDestinationNode) {
		return _chainsByDestinationNode.get(pDestinationNode);
	}

	public boolean isEmpty() {
		return _chains.isEmpty();
	}

	public void remove(GChain<N, E> pChain) {
		Objects.requireNonNull(pChain, "pChain");

		_chains.remove(pChain);
		_chainsByDestinationNode.remove(pChain.getDestination());
	}

	public GChain<N, E> removeLongest() {
		final GChain<N, E> chain = _chains.last();
		remove(chain);
		return chain;
	}

	public GChain<N, E> removeShortest() {
		final GChain<N, E> chain = _chains.first();
		remove(chain);
		return chain;
	}

}
