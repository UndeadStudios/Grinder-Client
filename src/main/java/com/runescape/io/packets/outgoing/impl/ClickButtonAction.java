package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class ClickButtonAction implements OutgoingPacket {

    private int buttonId, action;

    public ClickButtonAction(int button, int action) {
        this.buttonId = button;
        this.action = action;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(186);
        buf.putInt(buttonId);
        buf.putByte(action);
        return buf;
    }

}
