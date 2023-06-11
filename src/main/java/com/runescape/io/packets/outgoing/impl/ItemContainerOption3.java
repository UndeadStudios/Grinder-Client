package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class ItemContainerOption3 implements OutgoingPacket {

    int slot;
    int interfaceId;
    int nodeId;

    public ItemContainerOption3(int interfaceId, int nodeId, int slot) {
        this.slot = slot;
        this.interfaceId = interfaceId;
        this.nodeId = nodeId;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(43);
        buf.putInt(interfaceId);
        buf.putUnsignedWordA(nodeId);
        buf.putUnsignedWordA(slot);
        return buf;
    }
}
