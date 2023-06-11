package com.grinder.security;

import com.grinder.client.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Calendar;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since 08/09/2020
 */
public final class UID {

    public final static File UID_FILE = Paths.get(System.getProperty("user.home"), ".yab", ".config").toFile();

    public static boolean hasUID(){
        return UID_FILE.exists();
    }

    public static long read(){
        try {
            final byte[] data = Files.readAllBytes(UID_FILE.toPath());
            return bytesToLong(data);
        } catch (IOException ignored) {
        }
        return 0L;
    }

    public static void create(long uid){

        try {
            if(!UID_FILE.createNewFile())
                Log.error("Failed to create config file!");
        } catch (IOException e) {
            return;
        }

        try {
            Files.write(UID_FILE.toPath(), longToBytes(uid));
        } catch (IOException e) {
            Log.error("Failed to write bytes to file", e);
        }

        try {

            final Calendar calendar = Calendar.getInstance();

            calendar.set(2010, Calendar.MARCH, 20);

            Files.setAttribute(UID_FILE.toPath(), "creationTime", FileTime.fromMillis(calendar.getTimeInMillis()));

        } catch (IOException ignored) {
        }
    }

    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();
        return buffer.getLong();
    }

}
