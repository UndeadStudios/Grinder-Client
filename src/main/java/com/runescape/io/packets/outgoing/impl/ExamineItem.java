package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class ExamineItem implements OutgoingPacket {

	int nodeId;
	int interfaceId;

	public ExamineItem(int nodeId, int interfaceId) {
		this.nodeId = nodeId;
		this.interfaceId = interfaceId;
	}

	@Override
	public PacketBuilder create() {
		return new PacketBuilder(2).putShort(nodeId).putShort(interfaceId);
	}
}
