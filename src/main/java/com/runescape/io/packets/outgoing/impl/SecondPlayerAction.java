package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class SecondPlayerAction implements OutgoingPacket {

    int nodeId;

    public SecondPlayerAction(int nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(153);
        buf.putUnsignedWordBigEndian(nodeId);
        return buf;
    }
}
