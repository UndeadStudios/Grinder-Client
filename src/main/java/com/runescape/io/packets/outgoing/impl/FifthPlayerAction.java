package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class FifthPlayerAction implements OutgoingPacket {

    private final int playerIndex;

    public FifthPlayerAction(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    @Override
    public PacketBuilder create() {
        PacketBuilder builder = new PacketBuilder(39);
        builder.putUnsignedWordBigEndian(playerIndex);
        return builder;
    }
}
