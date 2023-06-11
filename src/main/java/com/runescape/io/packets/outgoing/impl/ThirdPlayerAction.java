package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class ThirdPlayerAction implements OutgoingPacket {

    int nodeId;

    public ThirdPlayerAction(int nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public PacketBuilder create() {
    	return new PacketBuilder(73).putUnsignedWordBigEndian(nodeId);
    }
}
