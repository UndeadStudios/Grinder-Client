package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class ItemOnNpc implements OutgoingPacket {

    int itemId;
    int npcIndex;
    int itemSlot;
    int interfaceId;

    public ItemOnNpc(int itemId, int npcIndex, int itemSlot, int interfaceId) {
        this.itemId = itemId;
        this.npcIndex = npcIndex;
        this.itemSlot = itemSlot;
        this.interfaceId = interfaceId;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(57);
        buf.putUnsignedWordA(itemId);
        buf.putUnsignedWordA(npcIndex);
        buf.putUnsignedWordBigEndian(itemSlot);
        buf.putUnsignedWordA(interfaceId);
        return buf;
    }
}
