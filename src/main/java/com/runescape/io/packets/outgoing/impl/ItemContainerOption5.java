package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class ItemContainerOption5 implements OutgoingPacket {

    int slot;
    int interfaceId;
    int nodeId;

    public ItemContainerOption5(int slot, int interfaceId, int nodeId) {
        this.nodeId = nodeId;
        this.slot = slot;
        this.interfaceId = interfaceId;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(135);
        buf.putInt(interfaceId);
        buf.putUnsignedWordBigEndian(slot);
        buf.putUnsignedWordBigEndian(nodeId);
        return buf;
    }
}
