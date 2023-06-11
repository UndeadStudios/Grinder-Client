package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class RandomSoundArea implements OutgoingPacket {


	@Override
    public PacketBuilder create() {
    	return new PacketBuilder(220);
    }


}
