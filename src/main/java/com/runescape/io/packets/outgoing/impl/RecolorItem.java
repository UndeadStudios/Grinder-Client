package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class RecolorItem implements OutgoingPacket {

    private int itemId;
    private int[] colors;

    public RecolorItem(int itemId, int[] colors) {
        this.itemId = itemId;
        this.colors = colors;
    }

    @Override
    public PacketBuilder create() {
        PacketBuilder buf = new PacketBuilder(189);
        buf.putInt(itemId);
        buf.putByte(colors.length);
        for (int i = 0; i < colors.length; i++)
            buf.putInt(colors[i]);
        return buf;
    }

}
