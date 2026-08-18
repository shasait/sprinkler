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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;

public abstract class GAStar<N, E> {

	private final Set<N> _visitedNodes = new HashSet<>();
	private final GChainQueue<N, E> _queuedChains = new GChainQueue<>();

	protected abstract Iterable<Pair<E, N>> determineNextNodes(GChain<N, E> pChain);

	protected final List<GChain<N, E>> determinePath(N pOrigin, Predicate<N> pMatcher, long pMaxLength, int pMaxResults) {
		int resultCount = 0;
		final List<GChain<N, E>> results = new ArrayList<>();

		_queuedChains.add(new GChain<>(pOrigin));

		while (!_queuedChains.isEmpty() && resultCount < pMaxResults) {
			final GChain<N, E> currentChain = _queuedChains.removeShortest();
			final N currentNode = currentChain.getDestination();

			if (pMatcher.test(currentNode)) {
				results.add(currentChain);
				resultCount++;
				continue;
			}

			_visitedNodes.add(currentNode);

			if (currentChain.getTotalLength() < pMaxLength) {
				for (Pair<E, N> nextNodeWithEdge : determineNextNodes(currentChain)) {
					final E usedEdge = nextNodeWithEdge.getLeft();
					final N nextNode = nextNodeWithEdge.getRight();

					if (!_visitedNodes.contains(nextNode) && isPassable(currentNode, usedEdge, nextNode)) {
						final long edgeLength = getLength(usedEdge);

						final GChain<N, E> queuedChain = _queuedChains.get(nextNode);
						if (queuedChain != null) {
							if (currentChain.getTotalLength() + edgeLength < queuedChain.getTotalLength()) {
								_queuedChains.remove(queuedChain);
							} else {
								continue;
							}
						}

						_queuedChains.add(new GChain<>(currentChain, usedEdge, nextNode, edgeLength));
					}
				}
			}
		}

		return results;
	}

	protected abstract long getLength(E pEdge);

	protected abstract boolean isPassable(N pOrigin, E pUsedEdge, N pDestination);

}
