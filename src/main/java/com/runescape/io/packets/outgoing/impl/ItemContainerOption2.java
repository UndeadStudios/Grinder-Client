package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class ItemContainerOption2 implements OutgoingPacket {

    int slot;
    int interfaceId;
    int nodeId;

    public ItemContainerOption2(int interfaceId, int nodeId, int slot) {
        this.slot = slot;
        this.interfaceId = interfaceId;
        this.nodeId = nodeId;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(117);
        buf.putInt(interfaceId);
        buf.putSignedWordBigEndian(nodeId);
        buf.putUnsignedWordBigEndian(slot);
        return buf;
    }
}
