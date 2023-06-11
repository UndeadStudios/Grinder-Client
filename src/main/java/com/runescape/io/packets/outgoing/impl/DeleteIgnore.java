package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class DeleteIgnore implements OutgoingPacket {

	private long ignore;

	public DeleteIgnore(long ignore) {
		this.ignore = ignore;
	}

	@Override
	public PacketBuilder create() {
		return new PacketBuilder(74).putLong(ignore);
	}

}
