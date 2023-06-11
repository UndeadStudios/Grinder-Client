package com.runescape.io.packets.outgoing.impl;

import com.runescape.util.OSTextInput;
import com.runescape.util.TextInput;
import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class PrivateChatMessage implements OutgoingPacket {

    private long privateMessageTarget;
    private String promptInput;

    public PrivateChatMessage(long privateMessageTarget, String promptInput) {
        this.privateMessageTarget = privateMessageTarget;
        this.promptInput = promptInput;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(126);
        buf.putLong(privateMessageTarget);
        if(PublicChatMessage.OSRS)
            OSTextInput.writeToStream(buf, promptInput);
        else
            TextInput.writeToStream(promptInput, buf);
        return buf;
    }
}
