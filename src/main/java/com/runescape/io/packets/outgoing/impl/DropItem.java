package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class DropItem implements OutgoingPacket {

    int nodeId;
    int interfaceId;
    int slot;

    public DropItem(int nodeId, int interfaceId, int slot) {
        this.nodeId = nodeId;
        this.interfaceId = interfaceId;
        this.slot = slot;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(87);
        buf.putUnsignedWordA(nodeId);
        buf.putShort(interfaceId);
        buf.putUnsignedWordA(slot);
        return buf;
    }


}
