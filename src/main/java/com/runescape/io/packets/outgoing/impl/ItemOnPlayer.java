package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class ItemOnPlayer implements OutgoingPacket {

    int anInt1285;
    int nodeId;
    int anInt1283;
    int anInt1284;

    public ItemOnPlayer(int anInt1284, int nodeId, int anInt1285, int anInt1283) {
        this.anInt1285 = anInt1285;
        this.nodeId = nodeId;
        this.anInt1283 = anInt1283;
        this.anInt1284 = anInt1284;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(14);
        buf.putUnsignedWordA(anInt1284);
        buf.putShort(nodeId);
        buf.putShort(anInt1285);
        buf.putUnsignedWordBigEndian(anInt1283);
        return buf;
    }
}
