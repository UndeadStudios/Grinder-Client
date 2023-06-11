package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class ItemContainerOption4 implements OutgoingPacket {

    int slot;
    int interfaceId;
    int nodeId;

    public ItemContainerOption4(int slot, int interfaceId, int nodeId) {
        this.nodeId = nodeId;
        this.slot = slot;
        this.interfaceId = interfaceId;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(129);
        buf.putUnsignedWordA(slot);
        buf.putInt(interfaceId);
        buf.putUnsignedWordA(nodeId);
        return buf;
    }
}
