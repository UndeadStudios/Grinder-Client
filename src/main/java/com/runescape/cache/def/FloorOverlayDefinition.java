package com.runescape.cache.def;

import com.runescape.cache.Js5;
import com.runescape.collection.DualNode;
import com.runescape.collection.EvictingDualNodeHashTable;
import com.runescape.io.Buffer;

public class FloorOverlayDefinition extends DualNode {

    public static EvictingDualNodeHashTable FloorOverlayDefinition_cached = new EvictingDualNodeHashTable(64);

    public int primaryRgb = 0;
    public int texture = -1;
    public boolean hideUnderlay = true;
    public int secondaryRgb = -1;
    public int hue;
    public int saturation;
    public int lightness;
    public int secondaryHue;
    public int secondarySaturation;
    public int secondaryLightness;

    public static FloorOverlayDefinition get(int var0) {
        FloorOverlayDefinition var1 = (FloorOverlayDefinition)FloorOverlayDefinition.FloorOverlayDefinition_cached.get((long)var0);
        if (var1 != null) {
            return var1;
        } else {
            byte[] var2 = Js5.configs.takeRecord(4, var0);
            var1 = new FloorOverlayDefinition();
            if (var2 != null) {
                var1.decode(new Buffer(var2), var0);
            }

            var1.postDecode();
            FloorOverlayDefinition.FloorOverlayDefinition_cached.put(var1, (long)var0);
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

    private void decodeNext(Buffer var1, int var2, int var3) {
        if (var2 == 1) {
            this.primaryRgb = var1.readMedium();
        } else if (var2 == 2) {
            this.texture = var1.readUnsignedByte();
        } else if (var2 == 5) {
            this.hideUnderlay = false;
        } else if (var2 == 7) {
            this.secondaryRgb = var1.readMedium();
        } else if (var2 == 8) {
        }

    }

    private void postDecode() {
        if (this.secondaryRgb != -1) {
            this.setHsl(this.secondaryRgb);
            this.secondaryHue = this.hue;
            this.secondarySaturation = this.saturation;
            this.secondaryLightness = this.lightness;
        }

        this.setHsl(this.primaryRgb);
    }

    private void setHsl(int var1) {
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
        double var16 = (var8 + var10) / 2.0;
        if (var10 != var8) {
            if (var16 < 0.5) {
                var14 = (var10 - var8) / (var10 + var8);
            }

            if (var16 >= 0.5) {
                var14 = (var10 - var8) / (2.0 - var10 - var8);
            }

            if (var2 == var10) {
                var12 = (var4 - var6) / (var10 - var8);
            } else if (var4 == var10) {
                var12 = 2.0 + (var6 - var2) / (var10 - var8);
            } else if (var6 == var10) {
                var12 = (var2 - var4) / (var10 - var8) + 4.0;
            }
        }

        var12 /= 6.0;
        this.hue = (int)(256.0 * var12);
        this.saturation = (int)(var14 * 256.0);
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

    }

}
