package com.runescape.io.packets.outgoing.impl;

import com.runescape.util.OSTextInput;
import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 13/02/2020
 */
public class MessagePublic implements OutgoingPacket {

    private final int type;
    private final int color;
    private final int effect;
    private final String text;

    public MessagePublic(int type, int color, int effect, String text) {
        this.type = type;
        this.color = color;
        this.effect = effect;
        this.text = text;
    }

    @Override
    public PacketBuilder create() {
        final PacketBuilder builder = new PacketBuilder(4);
        builder.putByte(type);
        builder.putByte(color);
        builder.putByte(effect);
        OSTextInput.writeToStream(builder, text);
        return builder;
    }
}
