package com.runescape.util;

import com.runescape.net.AbstractSocket;
import com.runescape.net.BufferedNetSocket;

import java.io.IOException;
import java.net.Socket;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 14/02/2020
 */
public class SocketUtil {

    public static AbstractSocket createBufferedSocket(Socket var0, int var1, int var2) throws IOException {
        return new BufferedNetSocket(var0, var1, var2);
    }

}
