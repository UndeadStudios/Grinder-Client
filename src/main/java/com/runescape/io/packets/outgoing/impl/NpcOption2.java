package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class NpcOption2 implements OutgoingPacket {

    int nodeId;

    public NpcOption2(int nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(17);
        buf.putSignedWordBigEndian(nodeId);
        return buf;
    }
}
