package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class ChatSettings implements OutgoingPacket {

    int gameMode;
    int publicMode;
    int privateMode;
    int clanMode;
    int tradeMode;
    int yellMode;

    public ChatSettings(int gameMode, int publicMode, int privateMode, int clanMode, int tradeMode, int yellMode) {
        this.gameMode = gameMode;
        this.publicMode = publicMode;
        this.privateMode = privateMode;
        this.clanMode = clanMode;
        this.tradeMode = tradeMode;
        this.yellMode = yellMode;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(95);
    	buf.putByte(gameMode);
        buf.putByte(publicMode);
        buf.putByte(privateMode);
        buf.putByte(clanMode);
        buf.putByte(tradeMode);
        buf.putByte(yellMode);
        return buf;
    }


}
