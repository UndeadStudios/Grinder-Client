package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class ObjectOption1 implements OutgoingPacket {

    int id;
    int val1;
    int val2;

    public ObjectOption1(int val1, int id, int val2) {
        this.id = id;
        this.val1 = val1;
        this.val2 = val2;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(132);
        buf.putSignedWordBigEndian(val1);
        buf.putInt(id);
        buf.putUnsignedWordA(val2);
        return buf;
    }
}
