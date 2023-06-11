package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class FirstPlayerAction implements OutgoingPacket {

    int nodeId;

    public FirstPlayerAction(int nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(128);
        buf.putShort(nodeId);
        return buf;
    }
}
