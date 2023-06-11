package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class ItemOnGroundItem implements OutgoingPacket {

    int anInt1285;
    int anInt1283;
    int anInt1284;
    int nodeId;
    int val1;
    int val2;

    public ItemOnGroundItem(int anInt1284, int anInt1285, int nodeId, int val1, int anInt1283, int val2) {
        this.nodeId = nodeId;
        this.val1 = val1;
        this.val2 = val2;
        this.anInt1283 = anInt1283;
        this.anInt1284 = anInt1284;
        this.anInt1285 = anInt1285;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(109);
        buf.putUnsignedWordBigEndian(anInt1284);
        buf.putUnsignedWordA(anInt1285);
        buf.putShort(nodeId);
        buf.putUnsignedWordA(val1);
        buf.putSignedWordBigEndian(anInt1283);
        buf.putShort(val2);
        return buf;
    }
}
