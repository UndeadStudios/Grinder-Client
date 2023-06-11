package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class ItemOption3 implements OutgoingPacket {

    int slot;
    int interfaceId;
    int nodeId;

    public ItemOption3(int nodeId, int slot, int interfaceId) {
        this.slot = slot;
        this.interfaceId = interfaceId;
        this.nodeId = nodeId;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(16);
        buf.putUnsignedWordA(nodeId);
        buf.putSignedWordBigEndian(slot);
        buf.putSignedWordBigEndian(interfaceId);
        return buf;
    }
}
