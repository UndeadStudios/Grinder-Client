package com.runescape.cache.def;

import com.runescape.cache.Js5;
import com.runescape.cache.OsCache;
import com.runescape.collection.DualNode;
import com.runescape.collection.EvictingDualNodeHashTable;
import com.runescape.io.Buffer;

public class FloorUnderlayDefinition extends DualNode {

    public static EvictingDualNodeHashTable FloorUnderlayDefinition_cached = new EvictingDualNodeHashTable(64);
    int rgb = 0;
    public int hue;
    public int saturation;
    public int lightness;
    public int hueMultiplier;

    public static FloorUnderlayDefinition get(int var0) {
        FloorUnderlayDefinition var1 = (FloorUnderlayDefinition)FloorUnderlayDefinition.FloorUnderlayDefinition_cached.get((long)var0);
        if (var1 != null) {
            return var1;
        } else {
            byte[] var2 = Js5.configs.takeRecord(1, var0);
            var1 = new FloorUnderlayDefinition();
            if (var2 != null) {
                var1.decode(new Buffer(var2), var0);
            }

            var1.postDecode();
            FloorUnderlayDefinition.FloorUnderlayDefinition_cached.put(var1, (long)var0);
            return var1;
        }
    }

    private void decode(Buffer var1, int var2) {
        while(true) {
            int var3 = var1.readUnsignedByte();
            if (var3 == 0) {
                return;
            }

            this.decodeNext(var1, var3, var2);
        }
    }

    void decodeNext(Buffer var1, int var2, int var3) {
        if (var2 == 1) {
            this.rgb = var1.readMedium();
        }

    }

    private void postDecode() {
        this.setHsl(this.rgb);
    }

    void setHsl(int var1) {
        double var2 = (double)(var1 >> 16 & 255) / 256.0;
        double var4 = (double)(var1 >> 8 & 255) / 256.0;
        double var6 = (double)(var1 & 255) / 256.0;
        double var8 = var2;
        if (var4 < var2) {
            var8 = var4;
        }

        if (var6 < var8) {
            var8 = var6;
        }

        double var10 = var2;
        if (var4 > var2) {
            var10 = var4;
        }

        if (var6 > var10) {
            var10 = var6;
        }

        double var12 = 0.0;
        double var14 = 0.0;
        double var16 = (var10 + var8) / 2.0;
        if (var10 != var8) {
            if (var16 < 0.5) {
                var14 = (var10 - var8) / (var8 + var10);
            }

            if (var16 >= 0.5) {
                var14 = (var10 - var8) / (2.0 - var10 - var8);
            }

            if (var10 == var2) {
                var12 = (var4 - var6) / (var10 - var8);
            } else if (var10 == var4) {
                var12 = 2.0 + (var6 - var2) / (var10 - var8);
            } else if (var6 == var10) {
                var12 = (var2 - var4) / (var10 - var8) + 4.0;
            }
        }

        var12 /= 6.0;
        this.saturation = (int)(256.0 * var14);
        this.lightness = (int)(var16 * 256.0);
        if (this.saturation < 0) {
            this.saturation = 0;
        } else if (this.saturation > 255) {
            this.saturation = 255;
        }

        if (this.lightness < 0) {
            this.lightness = 0;
        } else if (this.lightness > 255) {
            this.lightness = 255;
        }

        if (var16 > 0.5) {
            this.hueMultiplier = (int)((1.0 - var16) * var14 * 512.0);
        } else {
            this.hueMultiplier = (int)(var16 * var14 * 512.0);
        }

        if (this.hueMultiplier < 1) {
            this.hueMultiplier = 1;
        }

        this.hue = (int)((double)this.hueMultiplier * var12);
    }
}
