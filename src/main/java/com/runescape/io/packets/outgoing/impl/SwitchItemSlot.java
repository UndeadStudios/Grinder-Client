package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class SwitchItemSlot implements OutgoingPacket {

    int interface_;
    int param2;
    int dragFromSlot;
    int toInterface_;

    public SwitchItemSlot(int interface_, int param2, int dragFromSlot, int toInterface_) {
        this.interface_ = interface_;
        this.param2 = param2;
        this.dragFromSlot = dragFromSlot;
        this.toInterface_ = toInterface_;
    }

    @Override
    public PacketBuilder create() {
    	PacketBuilder buf = new PacketBuilder(214);
        buf.putInt(interface_);
        buf.putByteC(param2);
        buf.putSignedWordBigEndian(dragFromSlot);
        buf.putUnsignedWordBigEndian(toInterface_);
        return buf;
    }


}
