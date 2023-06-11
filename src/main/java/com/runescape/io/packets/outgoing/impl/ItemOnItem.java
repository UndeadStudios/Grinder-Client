package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class ItemOnItem implements OutgoingPacket {

    int targetSlot;
    int targetId;
    int usedInterface;
    int usedId;
    int usedSlot;
    int targetInterface;


    public ItemOnItem(int targetSlot, int usedSlot, int targetId, int targetInterface, int usedId, int usedInterface) {
        this.targetId = targetId;
        this.targetSlot = targetSlot;
        this.usedInterface = usedInterface;
        this.usedSlot = usedSlot;
        this.targetInterface = targetInterface;
        this.usedId = usedId;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(53);
        buf.putShort(targetSlot);
        buf.putUnsignedWordA(usedSlot);
        buf.putSignedWordBigEndian(targetId);
        buf.putShort(targetInterface);
        buf.putUnsignedWordBigEndian(usedId);
        buf.putShort(usedInterface);
        return buf;
    }
}
