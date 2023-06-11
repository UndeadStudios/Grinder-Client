package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-28
 */
public class SendChangePasswordRequest implements OutgoingPacket {

    private final String enteredPassword;
    private final String newPassword;
    private final String confirmationPassword;

    public SendChangePasswordRequest(String enteredPassword, String newPassword, String confirmationPassword) {
        this.enteredPassword = enteredPassword;
        this.newPassword = newPassword;
        this.confirmationPassword = confirmationPassword;
    }

    @Override
    public PacketBuilder create() {
        return new PacketBuilder(247)
                .putString(enteredPassword)
                .putString(newPassword)
                .putString(confirmationPassword);
    }
}
