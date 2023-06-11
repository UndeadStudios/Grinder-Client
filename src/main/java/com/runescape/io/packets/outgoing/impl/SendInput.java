package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 30/11/2019
 */
public class SendInput implements OutgoingPacket {

    public static final int LAST_SENT_DELAY = 300;
    public static int lastSentId = -1;
    public static long lastSentTimeStamp = -1;

    private final String string;
    private final int integer;

    public SendInput(String string, int integer) {
        this.string = string;
        this.integer = integer;
    }

    @Override
    public PacketBuilder create() {
        final PacketBuilder builder = new PacketBuilder(251);
        builder.putString(string);
        builder.putInt(integer);
        return builder;
    }
}
