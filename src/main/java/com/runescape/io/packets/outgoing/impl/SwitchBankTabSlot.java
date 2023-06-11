package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class SwitchBankTabSlot implements OutgoingPacket {

    int fromTab;
    int toTab;

    public SwitchBankTabSlot(int fromTab, int toTab) {
        this.fromTab = fromTab;
        this.toTab = toTab;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(212);
        buf.putInt(fromTab);
        buf.putInt(toTab);
        return buf;
    }

}