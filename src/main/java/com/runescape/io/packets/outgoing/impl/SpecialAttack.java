package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class SpecialAttack implements OutgoingPacket {

    private int barId;

    public SpecialAttack(int barId) {
        this.barId = barId;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(184);
        buf.putInt(barId);
        return buf;
    }

}
