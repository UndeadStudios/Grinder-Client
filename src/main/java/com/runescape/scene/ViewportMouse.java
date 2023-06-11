package com.runescape.scene;

/**
 * @version 1.0
 * @since 19/02/2020
 */
public class ViewportMouse {

    public static boolean ViewportMouse_isInViewport;
    public static int ViewportMouse_x;
    public static int ViewportMouse_y;
    public static boolean ViewportMouse_false0;
    public static int ViewportMouse_entityCount;
    public static long[] ViewportMouse_entityTags;

    static {
        ViewportMouse_isInViewport = false;
        ViewportMouse_x = 0;
        ViewportMouse_y = 0;
        ViewportMouse_false0 = false;
        ViewportMouse_entityCount = 0;
        ViewportMouse_entityTags = new long[1000];
    }

    public static boolean method5164(int var0, int var1, int var2, int var3, int var4, int var5, int var6) {
        int var7 = ViewportMouse.ViewportMouse_y + var6;
        if(var7 < var0 && var7 < var1 && var7 < var2) {
            return false;
        } else {
            var7 = ViewportMouse.ViewportMouse_y - var6;
            if(var7 > var0 && var7 > var1 && var7 > var2) {
                return false;
            } else {
                var7 = ViewportMouse.ViewportMouse_x + var6;
                if(var7 < var3 && var7 < var4 && var7 < var5) {
                    return false;
                } else {
                    var7 = ViewportMouse.ViewportMouse_x - var6;
                    return var7 <= var3 || var7 <= var4 || var7 <= var5;
                }
            }
        }
    }
    public static long toTag(int var0, int var1, int var2, boolean var3, int var4) {
        long var5 = (long)((var0 & 127) << 0 | (var1 & 127) << 7 | (var2 & 3) << 14) | ((long)var4 & 4294967295L) << 17;
        if(var3) {
            var5 |= 65536L;
        }

        return var5;
    }

    public static int getX(long tag) {
        return (int)(tag & 127L);
    }
    public static int getY(long tag) {
        return (int)(tag >>> 7 & 127L);
    }
    public static int getOpcode(long tag) {
        return (int)(tag >>> 14 & 3L);
    }
    public static int getID(long tag) {
        return (int)(tag >>> 17 & 0xffffffffL);
    }
    public static void method2081(long var0) {
        ViewportMouse.ViewportMouse_entityTags[++ViewportMouse.ViewportMouse_entityCount - 1] = var0;
    }
}
