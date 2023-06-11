package com.runescape.io.packets.outgoing.impl;

import com.runescape.util.OSTextInput;
import com.runescape.util.TextInput;
import com.runescape.io.Buffer;
import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

/**
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 20/11/2019
 */
public class PublicChatMessage implements OutgoingPacket {

    public static boolean OSRS = false;

    private Buffer textBuffer;
    private String inputString;
    private final int color;
    private final int effect;

    public PublicChatMessage(Buffer textBuffer, String inputString, int color, int effect) {
        this.textBuffer = textBuffer;
        this.inputString = inputString;
        this.color = color;
        this.effect = effect;
    }

    @Override
    public PacketBuilder create() {
        PacketBuilder buf = new PacketBuilder(4);
        buf.putSignedByteMin(effect);
        buf.putSignedByteMin(color);
        textBuffer.index = 0;
        if(OSRS)
            OSTextInput.writeToStream(textBuffer, inputString);
        else
            TextInput.writeToStream(inputString, textBuffer);
        buf.writeReverseDataA(0, textBuffer.array, textBuffer.index);
        return buf;
    }
}
