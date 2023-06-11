package com.runescape.net;

import java.io.IOException;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 11/12/2019
 */
public abstract class AbstractSocket {

    public abstract boolean isAvailable(int var1) throws IOException;

    public abstract int available() throws IOException;

    public abstract int readUnsignedByte() throws IOException;

    public abstract int read(byte[] var1, int var2, int var3) throws IOException;

    public abstract void write(byte[] var1, int var2, int var3) throws IOException;

    public abstract void close();
}
