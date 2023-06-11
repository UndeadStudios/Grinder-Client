package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class ClickTeleportMenu implements OutgoingPacket {

    private final int type, index;

    public ClickTeleportMenu(int type, int index) {
        this.type = type;
        this.index = index;
    }

    @Override
    public PacketBuilder create() {
    	return new PacketBuilder(183).putByte(type).putByte(index);
    }

}
