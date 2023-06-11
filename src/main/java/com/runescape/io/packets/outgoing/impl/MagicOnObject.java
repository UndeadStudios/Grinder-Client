package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class MagicOnObject implements OutgoingPacket {

    int objectId;
    int objectX;
    int objectY;
    int selectedSpellId;

    public MagicOnObject(int objectId, int objectX, int objectY, int selectedSpellId) {
        this.objectId = objectId;
        this.objectX = objectX;
        this.objectY = objectY;
        this.selectedSpellId = selectedSpellId;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(35);
        buf.putUnsignedWordBigEndian(objectId);
        buf.putInt(objectX);
        buf.putInt(objectY);
        buf.putUnsignedWordBigEndian(selectedSpellId);
        return buf;
    }


}
