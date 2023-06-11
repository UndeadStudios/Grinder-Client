package com.runescape.audio;

/**
 * @version 1.0
 * @since 13/02/2020
 */
public class WorldMapCacheName {

    public static int method634(int var0) {
        int var1 = 0;
        if(var0 < 0 || var0 >= 65536) {
            var0 >>>= 16;
            var1 += 16;
        }

        if(var0 >= 256) {
            var0 >>>= 8;
            var1 += 8;
        }

        if(var0 >= 16) {
            var0 >>>= 4;
            var1 += 4;
        }

        if(var0 >= 4) {
            var0 >>>= 2;
            var1 += 2;
        }

        if(var0 >= 1) {
            var0 >>>= 1;
            ++var1;
        }

        return var0 + var1;
    }
}
