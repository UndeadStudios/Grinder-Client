package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class ItemOption2 implements OutgoingPacket {

    int slot;
    int interfaceId;
    int nodeId;

    public ItemOption2(int interfaceId, int slot, int nodeId) {
        this.slot = slot;
        this.interfaceId = interfaceId;
        this.nodeId = nodeId;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(75);
        buf.putSignedWordBigEndian(interfaceId);
        buf.putUnsignedWordBigEndian(slot);
        buf.putUnsignedWordA(nodeId);
        return buf;
    }
}
