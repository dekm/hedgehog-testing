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

package org.unigrid.hedgehog.model.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import java.util.Optional;
import org.unigrid.hedgehog.model.network.Node;
import org.unigrid.hedgehog.model.network.codec.api.PacketEncoder;
import org.unigrid.hedgehog.model.network.packet.Packet;
import org.unigrid.hedgehog.model.network.packet.PublishPeers;
import org.unigrid.hedgehog.model.network.util.ByteBufUtils;

@Sharable
public class PublishPeersEncoder extends AbstractMessageToByteEncoder<PublishPeers> implements PacketEncoder<PublishPeers> {
	/*
	    Packet format:
	    0..............................................................63
	    [                << Frame Header (FrameDecoder) >>             ]
	    [ n= num peers ][                   reserved                   ]
	    [ <nodes>                                                  ...n]
	    [     port     ][                   reserved                   ]
	    [    host address                                          ...0]
	*/
	@Override
	public Optional<ByteBuf> encode(ChannelHandlerContext ctx, PublishPeers publishPeers) throws Exception {
		final ByteBuf out = Unpooled.buffer();

		out.writeShort(publishPeers.getNodes().size());
		out.writeZero(6 /* 48 bytes */);

		for (Node n : publishPeers.getNodes()) {
			out.writeShort(n.getAddress().getPort());
			out.writeZero(6 /* 48 bytes */);
			ByteBufUtils.writeNullTerminatedString(n.getAddress().getHostName(), out);
		}

		return Optional.of(out);
	}

	@Override
	public Packet.Type getCodecType() {
		return Packet.Type.PUBLISH_PEERS;
	}
}
