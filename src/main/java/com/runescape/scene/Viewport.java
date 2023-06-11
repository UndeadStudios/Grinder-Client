package com.runescape.scene;

import com.runescape.Client;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.Rasterizer3D;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 11/12/2019
 */
public class Viewport {

    public static void method1183(int width, int var1) {
        int[] var2 = new int[9];

        for(int var3 = 0; var3 < var2.length; ++var3) {
            int var4 = var3 * 32 + 15 + 128;
            int var5 = var4 * 3 + 600;
            int var7 = Rasterizer3D.Rasterizer3D_sine[var4];
            int var6 = method1176(var5, var1);
            var2[var3] = var6 * var7 >> 16;
        }

        SceneGraph.Scene_buildVisiblityMap(var2, 500, 800, width * 334 / var1, 334);
    }
    public static int method1176(int var0, int var1) {
        int var2 = var1 - 334;
        if(var2 < 0) {
            var2 = 0;
        } else if(var2 > 100) {
            var2 = 100;
        }

        int var3 = (320 - 256) * var2 / 100 + 256;
        return var0 * var3 / 256;
    }
    public static final void setViewportShape(int offsetX, int offsetY, int width, int height, boolean var4) {
        if(width < 1) {
            width = 1;
        }

        if(height < 1) {
            height = 1;
        }

        int var5 = height - 334;
        int var6;
        if(var5 < 0) {
            var6 = 256;
        } else if(var5 >= 100) {
            var6 = 205;
        } else {
            var6 = (205 - 256) * var5 / 100 + 256;
        }

        int var7 = height * var6 * 512 / (width * 334);
        int var8;
        int var9;
        short var10;
        if(var7 < 1) {
            var10 = 1;
            var6 = var10 * width * 334 / (height * 512);
            if(var6 > 32767) {
                var6 = 32767;
                var8 = height * var6 * 512 / (var10 * 334);
                var9 = (width - var8) / 2;
                if(var4) {
                    Rasterizer2D.Rasterizer2D_resetClip();
                    Rasterizer2D.fillRectangle(offsetX, offsetY, width, var9, 0xff000000);
                    Rasterizer2D.fillRectangle(offsetX + width - var9, offsetY, var9, height, 0xff000000);
                }

                offsetX += var9;
                width -= var9 * 2;
            }
        } else if(var7 > 32767) {
            var10 = 32767;
            var6 = var10 * width * 334 / (height * 512);
            if(var6 < 1) {
                var6 = 1;
                var8 = var10 * width * 334 / (var6 * 512);
                var9 = (height - var8) / 2;
                if(var4) {
                    Rasterizer2D.Rasterizer2D_resetClip();
                    Rasterizer2D.fillRectangle(offsetX, offsetY, width, var9, 0xff000000);
                    Rasterizer2D.fillRectangle(offsetX, height + offsetY - var9, width, var9, 0xff000000);
                }

                offsetY += var9;
                height -= var9 * 2;
            }
        }

        Client.viewportZoom = height * var6 / 334;
        if(width != Client.viewportWidth || height != Client.viewportHeight) {
            method1183(width, height);
        }

        Client.viewportOffsetX = offsetX;
        Client.viewportOffsetY = offsetY;
        Client.viewportWidth = width;
        Client.viewportHeight = height;
    }
}
