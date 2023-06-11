package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class FourthPlayerAction implements OutgoingPacket {

    int nodeId;

    public FourthPlayerAction(int nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(139);
        buf.putUnsignedWordBigEndian(nodeId);
        return buf;
    }
}
