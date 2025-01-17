/*
    Unigrid Hedgehog
    Copyright © 2021-2023 Stiftelsen The Unigrid Foundation, UGD Software AB

    Stiftelsen The Unigrid Foundation (org. nr: 802482-2408)
    UGD Software AB (org. nr: 559339-5824)

    This program is free software: you can redistribute it and/or modify it under the terms of the
    addended GNU Affero General Public License as published by the The Unigrid Foundation and
    the Free Software Foundation, version 3 of the License (see COPYING and COPYING.addendum).

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
    even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU Affero General Public License and the addendum for more details.

    You should have received an addended copy of the GNU Affero General Public License with this program.
    If not, see <http://www.gnu.org/licenses/> and <https://github.com/unigrid-project/hedgehog>.
 */

package org.unigrid.hedgehog.model.network;

import io.netty.util.concurrent.Future;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.apache.commons.configuration2.sync.LockMode;
import org.unigrid.hedgehog.model.cdi.Lock;
import org.unigrid.hedgehog.model.cdi.Protected;
import org.unigrid.hedgehog.model.network.packet.Packet;

@ApplicationScoped
public class Topology {
	private HashSet<Node> nodes;

	@PostConstruct
	private void init() {
		nodes = new HashSet<>();
	}

	@Protected @Lock(LockMode.READ)
	public void forEach(Consumer<Node> consumer) {
		nodes.forEach(consumer);
	}

	public Set<Node> cloneNodes() {
		return new HashSet(nodes);
	}

	@Protected @Lock(LockMode.READ)
	public boolean containsNode(Node node) {
		return nodes.contains(node);
	}

	@Protected @Lock(LockMode.WRITE)
	public void addNode(Node node) {
		nodes.add(node);
	}

	public static void sendAll(Packet packet, Topology topology, Optional<BiConsumer<Node, Future>> consumer) {
		topology.forEach(node -> {
			if (node.getConnection().isPresent()) {
				Node.send(packet, node, consumer);
			}
		});
	}
}
