package com.runescape.cache;

/**
 * @version 1.0
 * @since 13/02/2020
 */
public class Strings {

    public static int hashString(CharSequence var0) {
        int var1 = var0.length();
        int var2 = 0;

        for(int var3 = 0; var3 < var1; ++var3)
            var2 = (var2 << 5) - var2 + charToByteCp1252(var0.charAt(var3));

        return var2;
    }

    public static byte charToByteCp1252(char var0) {
        byte var1;
        if(var0 > 0 && var0 < 128 || var0 >= 160 && var0 <= 255) var1 = (byte) var0;
        else if(var0 == 8364) var1 = -128;
        else if(var0 == 8218) var1 = -126;
        else if(var0 == 402) var1 = -125;
        else if(var0 == 8222) var1 = -124;
        else if(var0 == 8230) var1 = -123;
        else if(var0 == 8224) var1 = -122;
        else if(var0 == 8225) var1 = -121;
        else if(var0 == 710) var1 = -120;
        else if(var0 == 8240) var1 = -119;
        else if(var0 == 352) var1 = -118;
        else if(var0 == 8249) var1 = -117;
        else if(var0 == 338) var1 = -116;
        else if(var0 == 381) var1 = -114;
        else if(var0 == 8216) var1 = -111;
        else if(var0 == 8217) var1 = -110;
        else if(var0 == 8220) var1 = -109;
        else if(var0 == 8221) var1 = -108;
        else if(var0 == 8226) var1 = -107;
        else if(var0 == 8211) var1 = -106;
        else if(var0 == 8212) var1 = -105;
        else if(var0 == 732) var1 = -104;
        else if(var0 == 8482) var1 = -103;
        else if(var0 == 353) var1 = -102;
        else if(var0 == 8250) var1 = -101;
        else if(var0 == 339) var1 = -100;
        else if(var0 == 382) var1 = -98;
        else if(var0 == 376) var1 = -97;
        else var1 = 63;

        return var1;
    }
}
