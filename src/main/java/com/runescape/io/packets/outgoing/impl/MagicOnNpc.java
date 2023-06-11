package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class MagicOnNpc implements OutgoingPacket {

    int nodeId;
    int selectedSpellId;

    public MagicOnNpc(int nodeId, int selectedSpellId) {
        this.nodeId = nodeId;
        this.selectedSpellId = selectedSpellId;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(131);
        buf.putSignedWordBigEndian(nodeId);
        buf.putUnsignedWordA(selectedSpellId);
        return buf;
    }


}
