package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class Command implements OutgoingPacket {

	private String cmd;

	public Command(String cmd) {
		this.cmd = cmd;
	}

	@Override
	public PacketBuilder create() {
		return new PacketBuilder(103).writeStringCp1252NullTerminated(cmd);
	}

}
