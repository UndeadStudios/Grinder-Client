package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class ItemContainerOption6 implements OutgoingPacket {

	private int interfaceId;
	private int slot;
	private int nodeId;

	public ItemContainerOption6(int slot, int interfaceId, int nodeId) {
        this.nodeId = nodeId;
        this.slot = slot;
        this.interfaceId = interfaceId;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(138);
        buf.putInt(interfaceId);
        buf.putUnsignedWordBigEndian(slot);
        buf.putUnsignedWordBigEndian(nodeId);
        return buf;
    }
}
