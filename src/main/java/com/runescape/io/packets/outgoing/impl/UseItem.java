package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class UseItem implements OutgoingPacket {

    int interfaceId;
    int slot;
    int nodeId;

    public UseItem(int interfaceId, int slot, int nodeId) {
        this.nodeId = nodeId;
        this.interfaceId = interfaceId;
        this.slot = slot;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(122);
        buf.putShort(interfaceId);
        buf.putShort(slot);
        buf.putShort(nodeId);
        return buf;
    }
}
