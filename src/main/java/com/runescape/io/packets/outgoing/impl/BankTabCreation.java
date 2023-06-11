package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class BankTabCreation implements OutgoingPacket {

	int interface_;
	int dragFromSlot;
	int toInterface_;

	public BankTabCreation(int interface_, int dragFromSlot, int toInterface_) {
		this.interface_ = interface_;
		this.dragFromSlot = dragFromSlot;
		this.toInterface_ = toInterface_;
	}

	@Override
	public PacketBuilder create() {
		PacketBuilder buf = new PacketBuilder(216);
		buf.putInt(interface_);
		buf.putShort(dragFromSlot);
		buf.putUnsignedWordBigEndian(toInterface_);
		return buf;
	}

}
