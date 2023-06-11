package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class YellEdit implements OutgoingPacket {

    private String title;
    private int bracketColor, bracketShadow, titleColor, titleShadow,
            nameColor, nameShadow, messageColor, messageShadow;

    public YellEdit(String title, int bracketColor, int bracketShadow, int titleColor, int titleShadow,
                    int nameColor, int nameShadow, int messageColor, int messageShadow) {
        this.title = title;
        this.bracketColor = bracketColor;
        this.bracketShadow = bracketShadow;
        this.titleColor = titleColor;
        this.titleShadow = titleShadow;
        this.nameColor = nameColor;
        this.nameShadow = nameShadow;
        this.messageColor = messageColor;
        this.messageShadow = messageShadow;
    }

    @Override
    public PacketBuilder create() {
        PacketBuilder buf = new PacketBuilder(182);
        buf.putString(title);
        buf.putInt(bracketColor);
        buf.putInt(bracketShadow);
        buf.putInt(titleColor);
        buf.putInt(titleShadow);
        buf.putInt(nameColor);
        buf.putInt(nameShadow);
        buf.putInt(messageColor);
        buf.putInt(messageShadow);
        return buf;
    }

}
