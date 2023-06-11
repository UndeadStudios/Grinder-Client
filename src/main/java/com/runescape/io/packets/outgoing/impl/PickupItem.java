package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class PickupItem implements OutgoingPacket {

    int nodeId;
    int val1;
    int val2;

    public PickupItem(int val1, int nodeId, int val2) {
        this.val1 = val1;
        this.nodeId = nodeId;
        this.val2 = val2;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(236);
        buf.putUnsignedWordBigEndian(val1);
        buf.putShort(nodeId);
        buf.putUnsignedWordBigEndian(val2);
        return buf;
    }
}
