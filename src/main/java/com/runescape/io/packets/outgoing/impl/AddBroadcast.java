package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

/**
 * An {@link OutgoingPacket} that sends a request to add a new broadcast
 * message.
 * 
 * @author Blake
 *
 */
public class AddBroadcast implements OutgoingPacket {

	/**
	 * The duration of the broadcast.
	 */
	private final int duration;

	/**
	 * The text of the broadcast.
	 */
	private final String text;

	/**
	 * The link of the broadcast (optional).
	 */
	private final String link;

	/**
	 * Constructs a new {@link AddBroadcast}.
	 * 
	 * @param duration
	 *            The duration.
	 * @param text
	 *            The text.
	 * @param link
	 *            The link.
	 */
	public AddBroadcast(int duration, String text, String link) {
		this.duration = duration;
		this.text = text;
		this.link = link;
	}

	@Override
	public PacketBuilder create() {
		return new PacketBuilder(250).putInt(duration).putString(text).putString(link);
	}
}
