package com.runescape.cache;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 14/02/2020
 */
public class HashUtil {

    public static int[] hashXORs;

    static {
        HashUtil.hashXORs = new int[256];

        int var2;
        for(int var1 = 0; var1 < 256; ++var1) {
            int var0 = var1;

            for(var2 = 0; var2 < 8; ++var2) {
                if((var0 & 1) == 1) {
                    var0 = var0 >>> 1 ^ -306674912;
                } else {
                    var0 >>>= 1;
                }
            }

            HashUtil.hashXORs[var1] = var0;
        }
    }

    public static int createHash(byte[] array, int var1) {
        int hash = -1;

        for(int i = 0; i < var1; ++i) {
            hash = hash >>> 8 ^ hashXORs[(hash ^ array[i]) & 255];
        }

        hash = ~hash;
        return hash;
    }

}
