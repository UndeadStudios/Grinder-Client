package com.runescape.io.packets.outgoing.impl;

import com.runescape.Client;
import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class ExamineOrEditNpc implements OutgoingPacket {

    int nodeId;

    public ExamineOrEditNpc(int nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public PacketBuilder create() {
    	return new PacketBuilder(6).putShort(nodeId);
    }
}
