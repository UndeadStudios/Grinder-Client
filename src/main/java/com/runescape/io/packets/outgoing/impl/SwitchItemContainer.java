package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class SwitchItemContainer implements OutgoingPacket {

    int fromContainerId;
    int toContainerId;
    int fromSlot;
    int toSlot;

    public SwitchItemContainer(int fromContainerId, int toContainerId, int fromSlot, int toSlot) {
        this.fromContainerId = fromContainerId;
        this.toContainerId = toContainerId;
        this.fromSlot = fromSlot;
        this.toSlot = toSlot;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(213);
        buf.putInt(fromContainerId);
        buf.putInt(toContainerId);
        buf.putSignedWordBigEndian(fromSlot);
        buf.putUnsignedWordBigEndian(toSlot);
        return buf;
    }

}
