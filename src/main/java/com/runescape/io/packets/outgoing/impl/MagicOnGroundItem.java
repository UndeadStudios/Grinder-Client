package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class MagicOnGroundItem implements OutgoingPacket {

    int itemY;
    int itemId;
    int itemX;
    int selectedSpellId;

    public MagicOnGroundItem(int itemY, int itemId, int itemX, int selectedSpellId) {
        this.itemY = itemY;
        this.itemId = itemId;
        this.itemX = itemX;
        this.selectedSpellId = selectedSpellId;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(181);
        buf.putShort(itemY);
        buf.putShort(itemId);
        buf.putShort(itemX);
        buf.putShort(selectedSpellId);
        return buf;
    }


}
