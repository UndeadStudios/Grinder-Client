package com.runescape.draw;

import com.grinder.client.ClientCompanion;
import com.grinder.client.util.Log;
import com.runescape.Client;
import com.runescape.cache.FileArchive;
import com.runescape.cache.TextureLoader;
import com.runescape.cache.graphics.IndexedImage;
import com.runescape.scene.SceneGraph;
import com.grinder.client.ClientUtil;

public final class Rasterizer3D extends Rasterizer2D {

    public final static int MAX_HSL_COLOR_VALUE = 1_234_567_8;
    public final static int MIN_DEPTH = 50;

    private final static int TEXTURE_SMALL_WIDTH = 64;
    private final static int TEXTURE_SMALL_HEIGHT = 64;
    private final static int TEXTURE_BIG_WIDTH = 128;
    private final static int TEXTURE_BIG_HEIGHT = 128;

    private final static int TEXTURE_PIXEL_COUNT_LOW_MEM =  TEXTURE_SMALL_HEIGHT * TEXTURE_SMALL_WIDTH;
    private final static int TEXTURE_PIXEL_R_ONSET_LOW_MEM = TEXTURE_PIXEL_COUNT_LOW_MEM;
    private final static int TEXTURE_PIXEL_G_ONSET_LOW_MEM = 3 * TEXTURE_PIXEL_COUNT_LOW_MEM;
    private final static int TEXTURE_PIXEL_B_ONSET_LOW_MEM = 2 * TEXTURE_PIXEL_COUNT_LOW_MEM;
    private static final int TEXTURE_PIXEL_BUFFER_SIZE_LOW_MEM = 16384;

    private final static int TEXTURE_PIXEL_COUNT_HIGH_MEM =  TEXTURE_BIG_WIDTH * TEXTURE_BIG_HEIGHT;
    private final static int TEXTURE_PIXEL_R_ONSET_HIGH_MEM = TEXTURE_PIXEL_COUNT_HIGH_MEM;
    private final static int TEXTURE_PIXEL_G_ONSET_HIGH_MEM = 3 * TEXTURE_PIXEL_COUNT_HIGH_MEM;
    private final static int TEXTURE_PIXEL_B_ONSET_HIGH_MEM = 2 * TEXTURE_PIXEL_COUNT_HIGH_MEM;
    private static final int TEXTURE_PIXEL_BUFFER_SIZE_HIGH_MEM = 65536;

    public static final int[] __et_p;

    /*public static int Rasterizer3D_clipMidX;
    public static int Rasterizer3D_clipMidY;
    public static int Rasterizer3D_clipWidth;
    public static int Rasterizer3D_alpha;*/
    public static byte[] lastTexturePalettePixels;

    /*static int Rasterizer3D_clipHeight;
    static int Rasterizer3D_clipNegativeMidX;
    static int Rasterizer3D_clipMidX2;
    static int Rasterizer3D_clipNegativeMidY;
    static int Rasterizer3D_clipMidY2;
    static int[] Rasterizer3D_rowOffsets;*/

    public static int[] Rasterizer3D_sine;
    public static int[] Rasterizer3D_cosine;

    /**
     * Contains the pixel index onsets for each row
     * that together comprise the raster.
     */
    public static int[] scanOffsets = new int[1024];

    public static boolean lowMem = true;
    public static boolean triangleIsOutOfBounds;
    public static boolean notTextured = true;
    public static int alpha;

    /**
     * Raster center x
     */
    public static int originViewX;

    /**
     * Raster center y
     */
    public static int originViewY;

    public static int lastTextureRetrievalCount;
    public static int[] hslToRgb = new int[0x10000];
    private static final int textureAmount = 97;
    public static IndexedImage[] textures = new IndexedImage[textureAmount];
    public static int[] textureLastUsed = new int[textureAmount];
    private static boolean opaque;
    private static int[] __et_r;
    private static int textureCount;
    private static boolean[] textureHasTransparency = new boolean[textureAmount];
    private static int[] averageTextureColours = new int[textureAmount];
    private static int textureRequestBufferPointer;
    private static int[][] textureRequestPixelBuffer;
    private static int[][] texturePixelBuffer = new int[textureAmount][];
    private static int[][] texturePalettes = new int[textureAmount][];
    public static int[] Rasterizer3D_colorPalette;
    public static TextureLoader Rasterizer3D_textureLoader;
    public static int Rasterizer3D_zoom;

    static {
        __et_r = new int[512];
        __et_p = new int[2048];
        Rasterizer3D_sine = new int[2048];
        Rasterizer3D_cosine = new int[2048];
        Rasterizer3D_colorPalette = new int[65536];
        Rasterizer3D_zoom = 512;
        alpha = 0;

        for (int i = 1; i < 512; i++) {
            __et_r[i] = 32768 / i;
        }
        for (int j = 1; j < 2048; j++) {
            __et_p[j] = 65536 / j;
        }
        for(int var0 = 0; var0 < 2048; ++var0) {
            Rasterizer3D_sine[var0] = (int)(65536.0D * Math.sin((double)var0 * 0.0030679615D));
            Rasterizer3D_cosine[var0] = (int)(65536.0D * Math.cos((double)var0 * 0.0030679615D));
        }
    }

    /**
     * Nullifies all fields in this class.
     */
    public static void clear() {
        __et_r = null;
        Rasterizer3D_sine = null;
        Rasterizer3D_cosine = null;
        scanOffsets = null;
        textures = null;
        textureHasTransparency = null;
        averageTextureColours = null;
        textureRequestPixelBuffer = null;
        texturePixelBuffer = null;
        textureLastUsed = null;
        hslToRgb = null;
        texturePalettes = null;
    }

    /**
     * Calculates the pixel row onsets based on the raster height and width.
     */
    public static void useViewport() {
        scanOffsets = new int[Rasterizer2D.Rasterizer2D_height];
        for (int y = 0; y < Rasterizer2D.Rasterizer2D_height; y++)
            scanOffsets[y] = Rasterizer2D.Rasterizer2D_width * y;

        originViewX = Rasterizer2D.Rasterizer2D_width / 2;
        originViewY = Rasterizer2D.Rasterizer2D_height / 2;
    }

    /**
     * Calculates the pixel row onsets based on
     * the argued width and length of the raster.
     *
     * @param width     the span of horizontal pixels
     * @param length    the span of vertical pixels
     */
    public static void reposition(int width, int length) {
        scanOffsets = new int[length];
        for (int y = 0; y < length; y++)
            scanOffsets[y] = width * y;

        originViewX = (width / 2) - ClientUtil.getCameraOffsetX();
        originViewY = length / 2;
    }

    /**
     * Creates the {@link #textureRequestPixelBuffer}
     * but only if it is {@code null}.
     */
    public static void tryInitiateTextureCache() {
        if (textureRequestPixelBuffer == null) {
            textureRequestBufferPointer = 20;
            if (lowMem)
                textureRequestPixelBuffer = new int[textureRequestBufferPointer][TEXTURE_PIXEL_BUFFER_SIZE_LOW_MEM];
            else
                textureRequestPixelBuffer = new int[textureRequestBufferPointer][TEXTURE_PIXEL_BUFFER_SIZE_HIGH_MEM];
            for (int i = 0; i < textureAmount; i++)
                texturePixelBuffer[i] = null;
        }
    }

    /**
     * Nullifies {@link #textureRequestPixelBuffer}
     * and clears {@link #texturePixelBuffer}.
     */
    public static void clearTextureCache() {
        textureRequestPixelBuffer = null;
        for (int i = 0; i < textureAmount; i++)
            texturePixelBuffer[i] = null;
    }

    /**
     * Loads the {@link IndexedImage textures} from the argued archive.
     *
     * @param archive the {@link FileArchive} containing the textures.
     */
    public static void loadTextures(FileArchive archive) {
        textureCount = 0;
        for (int index = 0; index < textureAmount; index++) {
            try {
                final IndexedImage texture = new IndexedImage(archive, String.valueOf(index), 0);
                if (lowMem && texture.resizeWidth == 128)
                    texture.downscale();
                else
                    texture.resize();
                textures[index] = texture;
                textureCount++;
            } catch (Exception ex) {
                Log.error("Failed to load texture["+index+"]", ex);
            }
        }
    }

    /**
     * Gets or calculates the average colour of the palette
     * in {@link #texturePalettes} at the argued index.
     *
     * @param index the index of the texture.
     * @return the average colour the texture palette.
     */
    public static int getOrCalculateAverageTextureColour(int index) {
        if (averageTextureColours[index] != 0)
            return averageTextureColours[index];
        int totalRed = 0;
        int totalGreen = 0;
        int totalBlue = 0;
        int colourCount = texturePalettes[index].length;
        for (int ptr = 0; ptr < colourCount; ptr++) {
            totalRed += texturePalettes[index][ptr] >> 16 & 0xff;
            totalGreen += texturePalettes[index][ptr] >> 8 & 0xff;
            totalBlue += texturePalettes[index][ptr] & 0xff;
        }

        int avgPaletteColour = (totalRed / colourCount << 16) + (totalGreen / colourCount << 8) + totalBlue / colourCount;
        avgPaletteColour = Rasterizer3D_brighten(avgPaletteColour, 1.3999999999999999D);
        if (avgPaletteColour == 0)
            avgPaletteColour = 1;
        averageTextureColours[index] = avgPaletteColour;
        return avgPaletteColour;
    }

    /**
     * Sets the request pixel buffer for the texture at the argued index.
     *
     * @see #computeTexturePixelData(int) for usage of the buffer.
     *
     * @param index the index of the texture in the {@link #texturePixelBuffer}.
     */
    public static void requestTextureUpdate(int index) {
        if (texturePixelBuffer[index] == null)
            return;
        textureRequestPixelBuffer[textureRequestBufferPointer++] = texturePixelBuffer[index];
        texturePixelBuffer[index] = null;
    }

    /**
     * Computes an array containing pixel data of the specified texture.
     *
     * This array is structured as follows:
     * [0..PIXEL_COUNT]                 -> hashed color values
     * [PIXEL_COUNT..2*PIXEL_COUNT]     -> red values
     * [2*PIXEL_COUNT..3*PIXEL_COUNT]   -> blue values
     * [3*PIXEL_COUNT..4*PIXEL_COUNT]   -> green values
     *
     * Where the PIXEL_COUNT denotes either {@link #TEXTURE_PIXEL_COUNT_HIGH_MEM}
     * or {@link #TEXTURE_PIXEL_COUNT_LOW_MEM}, which is conditioned upon
     * the value of {@link #lowMem}.
     *
     * @param textureId the id of the texture from which to retrieve the pixels
     *
     * @return a 1D int array containing a hashed color value and rgb values for each pixel
     */
    private static int[] computeTexturePixelData(int textureId) {
        textureLastUsed[textureId] = lastTextureRetrievalCount++;
        if (texturePixelBuffer[textureId] != null)
            return texturePixelBuffer[textureId];
        int[] pixels;
        if (textureRequestBufferPointer > 0) {
            pixels = textureRequestPixelBuffer[--textureRequestBufferPointer];
            textureRequestPixelBuffer[textureRequestBufferPointer] = null;
        } else {
            int lastUsed = 0;
            int target = -1;
            for (int l = 0; l < textureCount; l++)
                if (texturePixelBuffer[l] != null && (textureLastUsed[l] < lastUsed || target == -1)) {
                    lastUsed = textureLastUsed[l];
                    target = l;
                }

            pixels = texturePixelBuffer[target];
            texturePixelBuffer[target] = null;
        }
        texturePixelBuffer[textureId] = pixels;
        IndexedImage background = textures[textureId];
        int[] palette = texturePalettes[textureId];
        if (lowMem) {
            textureHasTransparency[textureId] = false;
            for (int pixel = 0; pixel < TEXTURE_PIXEL_COUNT_LOW_MEM; pixel++) {
                int colour = pixels[pixel] = palette[background.palettePixels[pixel]] & 0xf8f8ff;
                if (colour == 0)
                    textureHasTransparency[textureId] = true;
                /*pixels[TEXTURE_PIXEL_R_ONSET_LOW_MEM + pixel] = colour - (colour >>> 3) & 0xf8f8ff;
                pixels[TEXTURE_PIXEL_B_ONSET_LOW_MEM + pixel] = colour - (colour >>> 2) & 0xf8f8ff;
                pixels[TEXTURE_PIXEL_G_ONSET_LOW_MEM + pixel] = colour - (colour >>> 2) - (colour >>> 3) & 0xf8f8ff;*/
            }

        } else {
            if (background.width == TEXTURE_SMALL_WIDTH) {
                // scaling
                for (int x = 0; x < 128; x++) {
                    for (int y = 0; y < 128; y++)
                        pixels[y + (x << 7)] = palette[background.palettePixels[(y >> 1) + ((x >> 1) << 6)]];
                }
            } else {
                for (int i = 0; i < TEXTURE_PIXEL_COUNT_HIGH_MEM; i++)
                    pixels[i] = palette[background.palettePixels[i]];
            }
            textureHasTransparency[textureId] = false;
            for (int i = 0; i < TEXTURE_PIXEL_COUNT_HIGH_MEM; i++) {
                pixels[i] &= 0xf8f8ff;
                int colour = pixels[i];
                if (colour == 0)
                    textureHasTransparency[textureId] = true;
                /*pixels[TEXTURE_PIXEL_R_ONSET_HIGH_MEM + i] = colour - (colour >>> 3) & 0xf8f8ff;
                pixels[TEXTURE_PIXEL_B_ONSET_HIGH_MEM + i] = colour - (colour >>> 2) & 0xf8f8ff;
                pixels[TEXTURE_PIXEL_G_ONSET_HIGH_MEM + i] = colour - (colour >>> 2) - (colour >>> 3) & 0xf8f8ff;*/
            }

        }
        return pixels;
    }

    /**
     * Recalculates the hsl to rgb mapping and the texture palettes.
     *
     * @param brightness the new brightness value (ranging from 0.0 to 1.0)
     */
    public static void changeBrightness(double brightness) {
        int j = 0;
        for (int k = 0; k < 512; k++) {
            double d1 = (double) (k / 8) / 64D + 0.0078125D;
            double d2 = (double) (k & 7) / 8D + 0.0625D;
            for (int k1 = 0; k1 < 128; k1++) {
                double d3 = (double) k1 / 128D;
                double r = d3;
                double g = d3;
                double b = d3;
                if (d2 != 0.0D) {
                    double d7;
                    if (d3 < 0.5D)
                        d7 = d3 * (1.0D + d2);
                    else
                        d7 = (d3 + d2) - d3 * d2;
                    double d8 = 2D * d3 - d7;
                    double d9 = d1 + 0.33333333333333331D;
                    if (d9 > 1.0D)
                        d9--;
                    double d11 = d1 - 0.33333333333333331D;
                    if (d11 < 0.0D)
                        d11++;
                    if (6D * d9 < 1.0D)
                        r = d8 + (d7 - d8) * 6D * d9;
                    else if (2D * d9 < 1.0D)
                        r = d7;
                    else if (3D * d9 < 2D)
                        r = d8 + (d7 - d8) * (0.66666666666666663D - d9) * 6D;
                    else
                        r = d8;
                    if (6D * d1 < 1.0D)
                        g = d8 + (d7 - d8) * 6D * d1;
                    else if (2D * d1 < 1.0D)
                        g = d7;
                    else if (3D * d1 < 2D)
                        g = d8 + (d7 - d8) * (0.66666666666666663D - d1) * 6D;
                    else
                        g = d8;
                    if (6D * d11 < 1.0D)
                        b = d8 + (d7 - d8) * 6D * d11;
                    else if (2D * d11 < 1.0D)
                        b = d7;
                    else if (3D * d11 < 2D)
                        b = d8 + (d7 - d8) * (0.66666666666666663D - d11) * 6D;
                    else
                        b = d8;
                }
                int byteR = (int) (r * 256D);
                int byteG = (int) (g * 256D);
                int byteB = (int) (b * 256D);
                int rgb = (byteR << 16) + (byteG << 8) + byteB;
                rgb = Rasterizer3D_brighten(rgb, brightness);
                if (rgb == 0)
                    rgb = 1;
                hslToRgb[j++] = rgb;
            }
        }

        for (int textureId = 0; textureId < textureAmount; textureId++) {
            if (textures[textureId] != null) {
                final int[] originalPalette = textures[textureId].palette;
                texturePalettes[textureId] = new int[originalPalette.length];
                for (int colourId = 0; colourId < originalPalette.length; colourId++) {
                    texturePalettes[textureId][colourId] = Rasterizer3D_brighten(originalPalette[colourId], brightness);
                    if ((texturePalettes[textureId][colourId] & 0xf8f8ff) == 0 && colourId != 0)
                        texturePalettes[textureId][colourId] = 1;
                }
            }
        }

        for (int textureId = 0; textureId < textureAmount; textureId++)
            requestTextureUpdate(textureId);
    }

    public static int Rasterizer3D_brighten(int var0, double var1) {
        double var3 = (double)(var0 >> 16) / 256.0D;
        double var5 = (double)(var0 >> 8 & 255) / 256.0D;
        double var7 = (double)(var0 & 255) / 256.0D;
        var3 = Math.pow(var3, var1);
        var5 = Math.pow(var5, var1);
        var7 = Math.pow(var7, var1);
        int var9 = (int)(var3 * 256.0D);
        int var10 = (int)(var5 * 256.0D);
        int var11 = (int)(var7 * 256.0D);
        return var11 + (var10 << 8) + (var9 << 16);
    }

    public static void Rasterizer3D_setTextureLoader(TextureLoader textureLoader) {
        Rasterizer3D_textureLoader = textureLoader;
    }

    public static void Rasterizer3D_setBrightness(double brightness) {
        Rasterizer3D_buildPalette(brightness);
    }

    static void Rasterizer3D_buildPalette(double brightness) {
        int var4 = 0;

        for(int var5 = 0; var5 < 512; ++var5) {
            double var6 = (double)(var5 >> 3) / 64.0D + 0.0078125D;
            double var8 = (double)(var5 & 7) / 8.0D + 0.0625D;

            for(int var10 = 0; var10 < 128; ++var10) {
                double var11 = (double)var10 / 128.0D;
                double var13 = var11;
                double var15 = var11;
                double var17 = var11;
                if(var8 != 0.0D) {
                    double var19;
                    if(var11 < 0.5D) {
                        var19 = var11 * (1.0D + var8);
                    } else {
                        var19 = var11 + var8 - var11 * var8;
                    }

                    double var21 = 2.0D * var11 - var19;
                    double var23 = var6 + 0.3333333333333333D;
                    if(var23 > 1.0D) {
                        --var23;
                    }

                    double var27 = var6 - 0.3333333333333333D;
                    if(var27 < 0.0D) {
                        ++var27;
                    }

                    if(6.0D * var23 < 1.0D) {
                        var13 = var21 + (var19 - var21) * 6.0D * var23;
                    } else if(2.0D * var23 < 1.0D) {
                        var13 = var19;
                    } else if(3.0D * var23 < 2.0D) {
                        var13 = var21 + (var19 - var21) * (0.6666666666666666D - var23) * 6.0D;
                    } else {
                        var13 = var21;
                    }

                    if(6.0D * var6 < 1.0D) {
                        var15 = var21 + (var19 - var21) * 6.0D * var6;
                    } else if(2.0D * var6 < 1.0D) {
                        var15 = var19;
                    } else if(3.0D * var6 < 2.0D) {
                        var15 = var21 + (var19 - var21) * (0.6666666666666666D - var6) * 6.0D;
                    } else {
                        var15 = var21;
                    }

                    if(6.0D * var27 < 1.0D) {
                        var17 = var21 + (var19 - var21) * 6.0D * var27;
                    } else if(2.0D * var27 < 1.0D) {
                        var17 = var19;
                    } else if(3.0D * var27 < 2.0D) {
                        var17 = var21 + (var19 - var21) * (0.6666666666666666D - var27) * 6.0D;
                    } else {
                        var17 = var21;
                    }
                }

                int var29 = (int)(var13 * 256.0D);
                int var20 = (int)(var15 * 256.0D);
                int var30 = (int)(var17 * 256.0D);
                int var22 = var30 + (var20 << 8) + (var29 << 16);
                var22 = Rasterizer3D_brighten(var22, brightness);
                if(var22 == 0) {
                    var22 = 1;
                }

                Rasterizer3D_colorPalette[var4++] = var22;
            }
        }

    }

    public static int method3039(int x, int z, int var2, int var3) {
        return x * var2 + var3 * z >> 16;
    }
    public static int method3004(int var0, int var1, int var2, int var3) {
        return var2 * var1 - var3 * var0 >> 16;
    }
    public static int method3005(int var0, int var1, int var2, int var3) {
        return var0 * var2 - var3 * var1 >> 16;
    }
    public static int method3006(int var0, int var1, int var2, int var3) {
        return var3 * var0 + var2 * var1 >> 16;
    }
    public static int method3007(int var0, int var1, int var2, int var3) {
        return var0 * var2 + var3 * var1 >> 16;
    }
    public static int method3008(int var0, int var1, int var2, int var3) {
        return var2 * var1 - var3 * var0 >> 16;
    }

    /**
     * This will not render anything.
     *
     * @param x_a x coordinate of vertex a
     * @param x_b x coordinate of vertex b
     * @param x_c x coordinate of vertex c
     * @param y_a y coordinate of vertex a
     * @param y_b y coordinate of vertex b
     * @param y_c y coordinate of vertex c
     * @param z_a z coordinate of vertex a
     * @param z_b z coordinate of vertex b
     * @param z_c z coordinate of vertex c
     */
    @Deprecated
    public static void drawDepthTriangle(int x_a, int x_b, int x_c, int y_a, int y_b, int y_c, float z_a, float z_b, float z_c) {
        int a_to_b = 0, b_to_c = 0, c_to_a = 0;

        if (y_b != y_a) {
            a_to_b = (x_b - x_a << 16) / (y_b - y_a);
        }
        if (y_c != y_b) {
            b_to_c = (x_c - x_b << 16) / (y_c - y_b);
        }
        if (y_c != y_a) {
            c_to_a = (x_a - x_c << 16) / (y_a - y_c);
        }

        float b_aX = x_b - x_a;
        float b_aY = y_b - y_a;
        float c_aX = x_c - x_a;
        float c_aY = y_c - y_a;
        float b_aZ = z_b - z_a;
        float c_aZ = z_c - z_a;

        float div = b_aX * c_aY - c_aX * b_aY;
        float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
        float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;

        //B    /|
        //    / |
        //   /  |
        //  /   |
        //A ----- C
        if (y_a <= y_b && y_a <= y_c) {
            if (y_a < Rasterizer2D_yClipEnd) {
                if (y_b > Rasterizer2D_yClipEnd)
                    y_b = Rasterizer2D_yClipEnd;
                if (y_c > Rasterizer2D_yClipEnd)
                    y_c = Rasterizer2D_yClipEnd;
                z_a = z_a - depth_slope * x_a + depth_slope;
                if (y_b < y_c) {
                    x_c = x_a <<= 16;
                    if (y_a < 0) {
                        x_c -= c_to_a * y_a;
                        x_a -= a_to_b * y_a;
                        z_a -= depth_increment * y_a;
                        y_a = 0;
                    }
                    x_b <<= 16;
                    if (y_b < 0) {
                        x_b -= b_to_c * y_b;
                        y_b = 0;
                    }
                    if (y_a != y_b && c_to_a < a_to_b || y_a == y_b && c_to_a > b_to_c) {
                        y_c -= y_b;
                        y_b -= y_a;
                        y_a = Rasterizer3D.scanOffsets[y_a];
                        while (--y_b >= 0) {
                            drawDepthScanLine(y_a, x_c >> 16, x_a >> 16, z_a, depth_slope);
                            x_c += c_to_a;
                            x_a += a_to_b;
                            z_a += depth_increment;
                            y_a += Rasterizer2D_width;
                        }
                        while (--y_c >= 0) {
                            drawDepthScanLine(y_a, x_c >> 16, x_b >> 16, z_a, depth_slope);
                            x_c += c_to_a;
                            x_b += b_to_c;
                            z_a += depth_increment;
                            y_a += Rasterizer2D_width;
                        }
                    } else {
                        y_c -= y_b;
                        y_b -= y_a;
                        y_a = Rasterizer3D.scanOffsets[y_a];
                        while (--y_b >= 0) {
                            drawDepthScanLine(y_a, x_a >> 16, x_c >> 16, z_a, depth_slope);
                            x_c += c_to_a;
                            x_a += a_to_b;
                            z_a += depth_increment;
                            y_a += Rasterizer2D_width;
                        }
                        while (--y_c >= 0) {
                            drawDepthScanLine(y_a, x_b >> 16, x_c >> 16, z_a, depth_slope);
                            x_c += c_to_a;
                            x_b += b_to_c;
                            z_a += depth_increment;
                            y_a += Rasterizer2D_width;
                        }
                    }
                } else {
                    x_b = x_a <<= 16;
                    if (y_a < 0) {
                        x_b -= c_to_a * y_a;
                        x_a -= a_to_b * y_a;
                        z_a -= depth_increment * y_a;
                        y_a = 0;
                    }
                    x_c <<= 16;
                    if (y_c < 0) {
                        x_c -= b_to_c * y_c;
                        y_c = 0;
                    }
                    if (y_a != y_c && c_to_a < a_to_b || y_a == y_c && b_to_c > a_to_b) {
                        y_b -= y_c;
                        y_c -= y_a;
                        y_a = Rasterizer3D.scanOffsets[y_a];
                        while (--y_c >= 0) {
                            drawDepthScanLine(y_a, x_b >> 16, x_a >> 16, z_a, depth_slope);
                            x_b += c_to_a;
                            x_a += a_to_b;
                            z_a += depth_increment;
                            y_a += Rasterizer2D_width;
                        }
                        while (--y_b >= 0) {
                            drawDepthScanLine(y_a, x_c >> 16, x_a >> 16, z_a, depth_slope);
                            x_c += b_to_c;
                            x_a += a_to_b;
                            z_a += depth_increment;
                            y_a += Rasterizer2D_width;
                        }
                    } else {
                        y_b -= y_c;
                        y_c -= y_a;
                        y_a = Rasterizer3D.scanOffsets[y_a];
                        while (--y_c >= 0) {
                            drawDepthScanLine(y_a, x_a >> 16, x_b >> 16, z_a, depth_slope);
                            x_b += c_to_a;
                            x_a += a_to_b;
                            z_a += depth_increment;
                            y_a += Rasterizer2D_width;
                        }
                        while (--y_b >= 0) {
                            drawDepthScanLine(y_a, x_a >> 16, x_c >> 16, z_a, depth_slope);
                            x_c += b_to_c;
                            x_a += a_to_b;
                            z_a += depth_increment;
                            y_a += Rasterizer2D_width;
                        }
                    }
                }
            }
        } else if (y_b <= y_c) {
            //A |\
            //  | \
            //  |  \
            //  |   \
            //C ----- B
            if (y_b < Rasterizer2D_yClipEnd) {
                if (y_c > Rasterizer2D_yClipEnd)
                    y_c = Rasterizer2D_yClipEnd;
                if (y_a > Rasterizer2D_yClipEnd)
                    y_a = Rasterizer2D_yClipEnd;
                z_b = z_b - depth_slope * x_b + depth_slope;
                if (y_c < y_a) {
                    x_a = x_b <<= 16;
                    if (y_b < 0) {
                        x_a -= a_to_b * y_b;
                        x_b -= b_to_c * y_b;
                        z_b -= depth_increment * y_b;
                        y_b = 0;
                    }
                    x_c <<= 16;
                    if (y_c < 0) {
                        x_c -= c_to_a * y_c;
                        y_c = 0;
                    }
                    if (y_b != y_c && a_to_b < b_to_c || y_b == y_c && a_to_b > c_to_a) {
                        y_a -= y_c;
                        y_c -= y_b;
                        y_b = Rasterizer3D.scanOffsets[y_b];
                        while (--y_c >= 0) {
                            drawDepthScanLine(y_b, x_a >> 16, x_b >> 16, z_b, depth_slope);
                            x_a += a_to_b;
                            x_b += b_to_c;
                            z_b += depth_increment;
                            y_b += Rasterizer2D_width;
                        }
                        while (--y_a >= 0) {
                            drawDepthScanLine(y_b, x_a >> 16, x_c >> 16, z_b, depth_slope);
                            x_a += a_to_b;
                            x_c += c_to_a;
                            z_b += depth_increment;
                            y_b += Rasterizer2D_width;
                        }
                    } else {
                        y_a -= y_c;
                        y_c -= y_b;
                        y_b = Rasterizer3D.scanOffsets[y_b];
                        while (--y_c >= 0) {
                            drawDepthScanLine(y_b, x_b >> 16, x_a >> 16, z_b, depth_slope);
                            x_a += a_to_b;
                            x_b += b_to_c;
                            z_b += depth_increment;
                            y_b += Rasterizer2D_width;
                        }
                        while (--y_a >= 0) {
                            drawDepthScanLine(y_b, x_c >> 16, x_a >> 16, z_b, depth_slope);
                            x_a += a_to_b;
                            x_c += c_to_a;
                            z_b += depth_increment;
                            y_b += Rasterizer2D_width;
                        }
                    }
                } else {
                    x_c = x_b <<= 16;
                    if (y_b < 0) {
                        x_c -= a_to_b * y_b;
                        x_b -= b_to_c * y_b;
                        z_b -= depth_increment * y_b;
                        y_b = 0;
                    }
                    x_a <<= 16;
                    if (y_a < 0) {
                        x_a -= c_to_a * y_a;
                        y_a = 0;
                    }
                    if (a_to_b < b_to_c) {
                        y_c -= y_a;
                        y_a -= y_b;
                        y_b = Rasterizer3D.scanOffsets[y_b];
                        while (--y_a >= 0) {
                            drawDepthScanLine(y_b, x_c >> 16, x_b >> 16, z_b, depth_slope);
                            x_c += a_to_b;
                            x_b += b_to_c;
                            z_b += depth_increment;
                            y_b += Rasterizer2D_width;
                        }
                        while (--y_c >= 0) {
                            drawDepthScanLine(y_b, x_a >> 16, x_b >> 16, z_b, depth_slope);
                            x_a += c_to_a;
                            x_b += b_to_c;
                            z_b += depth_increment;
                            y_b += Rasterizer2D_width;
                        }
                    } else {
                        y_c -= y_a;
                        y_a -= y_b;
                        y_b = Rasterizer3D.scanOffsets[y_b];
                        while (--y_a >= 0) {
                            drawDepthScanLine(y_b, x_b >> 16, x_c >> 16, z_b, depth_slope);
                            x_c += a_to_b;
                            x_b += b_to_c;
                            z_b += depth_increment;
                            y_b += Rasterizer2D_width;
                        }
                        while (--y_c >= 0) {
                            drawDepthScanLine(y_b, x_b >> 16, x_a >> 16, z_b, depth_slope);
                            x_a += c_to_a;
                            x_b += b_to_c;
                            z_b += depth_increment;
                            y_b += Rasterizer2D_width;
                        }
                    }
                }
            }
        } else if (y_c < Rasterizer2D_yClipEnd) {
            if (y_a > Rasterizer2D_yClipEnd)
                y_a = Rasterizer2D_yClipEnd;
            if (y_b > Rasterizer2D_yClipEnd)
                y_b = Rasterizer2D_yClipEnd;
            z_c = z_c - depth_slope * x_c + depth_slope;
            if (y_a < y_b) {
                x_b = x_c <<= 16;
                if (y_c < 0) {
                    x_b -= b_to_c * y_c;
                    x_c -= c_to_a * y_c;
                    z_c -= depth_increment * y_c;
                    y_c = 0;
                }
                x_a <<= 16;
                if (y_a < 0) {
                    x_a -= a_to_b * y_a;
                    y_a = 0;
                }
                if (b_to_c < c_to_a) {
                    y_b -= y_a;
                    y_a -= y_c;
                    y_c = Rasterizer3D.scanOffsets[y_c];
                    while (--y_a >= 0) {
                        drawDepthScanLine(y_c, x_b >> 16, x_c >> 16, z_c, depth_slope);
                        x_b += b_to_c;
                        x_c += c_to_a;
                        z_c += depth_increment;
                        y_c += Rasterizer2D_width;
                    }
                    while (--y_b >= 0) {
                        drawDepthScanLine(y_c, x_b >> 16, x_a >> 16, z_c, depth_slope);
                        x_b += b_to_c;
                        x_a += a_to_b;
                        z_c += depth_increment;
                        y_c += Rasterizer2D_width;
                    }
                } else {
                    y_b -= y_a;
                    y_a -= y_c;
                    y_c = Rasterizer3D.scanOffsets[y_c];
                    while (--y_a >= 0) {
                        drawDepthScanLine(y_c, x_c >> 16, x_b >> 16, z_c, depth_slope);
                        x_b += b_to_c;
                        x_c += c_to_a;
                        z_c += depth_increment;
                        y_c += Rasterizer2D_width;
                    }
                    while (--y_b >= 0) {
                        drawDepthScanLine(y_c, x_a >> 16, x_b >> 16, z_c, depth_slope);
                        x_b += b_to_c;
                        x_a += a_to_b;
                        z_c += depth_increment;
                        y_c += Rasterizer2D_width;
                    }
                }
            } else {
                x_a = x_c <<= 16;
                if (y_c < 0) {
                    x_a -= b_to_c * y_c;
                    x_c -= c_to_a * y_c;
                    z_c -= depth_increment * y_c;
                    y_c = 0;
                }
                x_b <<= 16;
                if (y_b < 0) {
                    x_b -= a_to_b * y_b;
                    y_b = 0;
                }
                if (b_to_c < c_to_a) {
                    y_a -= y_b;
                    y_b -= y_c;
                    y_c = Rasterizer3D.scanOffsets[y_c];
                    while (--y_b >= 0) {
                        drawDepthScanLine(y_c, x_a >> 16, x_c >> 16, z_c, depth_slope);
                        x_a += b_to_c;
                        x_c += c_to_a;
                        z_c += depth_increment;
                        y_c += Rasterizer2D_width;
                    }
                    while (--y_a >= 0) {
                        drawDepthScanLine(y_c, x_b >> 16, x_c >> 16, z_c, depth_slope);
                        x_b += a_to_b;
                        x_c += c_to_a;
                        z_c += depth_increment;
                        y_c += Rasterizer2D_width;
                    }
                } else {
                    y_a -= y_b;
                    y_b -= y_c;
                    y_c = Rasterizer3D.scanOffsets[y_c];
                    while (--y_b >= 0) {
                        drawDepthScanLine(y_c, x_c >> 16, x_a >> 16, z_c, depth_slope);
                        x_a += b_to_c;
                        x_c += c_to_a;
                        z_c += depth_increment;
                        y_c += Rasterizer2D_width;
                    }
                    while (--y_a >= 0) {
                        drawDepthScanLine(y_c, x_c >> 16, x_b >> 16, z_c, depth_slope);
                        x_b += a_to_b;
                        x_c += c_to_a;
                        z_c += depth_increment;
                        y_c += Rasterizer2D_width;
                    }
                }
            }
        }
    }

    private static void drawDepthScanLine(int dest_off, int start_x, int end_x, float depth, float depth_slope) {
        int dbl = depthBuffer.length;
        if (Rasterizer3D.triangleIsOutOfBounds) {
            if (end_x > Rasterizer2D_width)
                end_x = Rasterizer2D_width;
            if (start_x < 0)
                start_x = 0;
        }
        if (start_x >= end_x)
            return;
        dest_off += start_x - 1;
        int loops = end_x - start_x >> 2;
        depth += depth_slope * (float) start_x;

        if (Rasterizer3D.alpha == 0) {
            while (--loops >= 0) {

                dest_off++;
                if (dest_off >= 0 && dest_off < dbl && true) {
                    depthBuffer[dest_off] = depth;
                }

                depth += depth_slope;
                dest_off++;
                if (dest_off >= 0 && dest_off < dbl && true) {
                    depthBuffer[dest_off] = depth;
                }
                depth += depth_slope;
                dest_off++;
                if (dest_off >= 0 && dest_off < dbl && true) {
                    depthBuffer[dest_off] = depth;
                }
                depth += depth_slope;
                dest_off++;
                if (dest_off >= 0 && dest_off < dbl && true) {
                    depthBuffer[dest_off] = depth;
                }
                depth += depth_slope;

            }
            for (loops = end_x - start_x & 3; --loops >= 0;) {
                dest_off++;
                if (dest_off >= 0 && dest_off < dbl && true) {
                    depthBuffer[dest_off] = depth;
                }
                depth += depth_slope;
            }
            return;
        }
        while (--loops >= 0) {
            dest_off++;
            dest_off++;
            dest_off++;
            dest_off++;
        }
        for (loops = end_x - start_x & 3; --loops >= 0;) {
            dest_off++;
        }
    }

    /**
     * @param x1   x coordinate of vertex a
     * @param x2   x coordinate of vertex b
     * @param x3   x coordinate of vertex c
     * @param y1   y coordinate of vertex a
     * @param y2   y coordinate of vertex b
     * @param y3   y coordinate of vertex c
     * @param z1   z coordinate of vertex a
     * @param z2   z coordinate of vertex b
     * @param z3   z coordinate of vertex c
     * @param hsl1 hsl color value at vertex a
     * @param hsl2 hsl color value at vertex b
     * @param hsl3 hsl color value at vertex c
     */
    public static void drawGouraudTriangle(int x1, int x2, int x3, int y1, int y2, int y3, float z1, float z2, float z3, int hsl1, int hsl2, int hsl3) {
        if (z1 < 0 || z2 < 0 || z3 < 0)
            return;
        int distance_v1_v2_x = 0;
        int distance_v1_v2_hsl = 0;
        if (y2 != y1) {
            distance_v1_v2_x = (x2 - x1 << 16) / (y2 - y1);
            distance_v1_v2_hsl = (hsl2 - hsl1 << 15) / (y2 - y1);
        }
        int distance_v2_v3_x = 0;
        int distance_v2_v3_hsl = 0;
        if (y3 != y2) {
            distance_v2_v3_x = (x3 - x2 << 16) / (y3 - y2);
            distance_v2_v3_hsl = (hsl3 - hsl2 << 15) / (y3 - y2);
        }
        int distance_v1_v3_x = 0;
        int distance_v1_v3_hsl = 0;
        if (y3 != y1) {
            distance_v1_v3_x = (x1 - x3 << 16) / (y1 - y3);
            distance_v1_v3_hsl = (hsl1 - hsl3 << 15) / (y1 - y3);
        }
        float dx_21 = x2 - x1;
        float dy_21 = y2 - y1;
        float dx_31 = x3 - x1;
        float dy_31 = y3 - y1;
        float dz_21 = z2 - z1;
        float dz_31 = z3 - z1;

        float div = dx_21 * dy_31 - dx_31 * dy_21;
        float depth_slope = (dz_21 * dy_31 - dz_31 * dy_21) / div;
        float depth_increment = (dz_31 * dx_21 - dz_21 * dx_31) / div;
        if (y1 <= y2 && y1 <= y3) {
            if (y1 >= Rasterizer2D.Rasterizer2D_yClipEnd)
                return;
            if (y2 > Rasterizer2D.Rasterizer2D_yClipEnd)
                y2 = Rasterizer2D.Rasterizer2D_yClipEnd;
            if (y3 > Rasterizer2D.Rasterizer2D_yClipEnd)
                y3 = Rasterizer2D.Rasterizer2D_yClipEnd;
            z1 = z1 - depth_slope * x1 + depth_slope;
            if (y2 < y3) {
                x3 = x1 <<= 16;
                hsl3 = hsl1 <<= 15;
                if (y1 < 0) {
                    x3 -= distance_v1_v3_x * y1;
                    x1 -= distance_v1_v2_x * y1;
                    hsl3 -= distance_v1_v3_hsl * y1;
                    hsl1 -= distance_v1_v2_hsl * y1;
                    z1 -= depth_increment * y1;
                    y1 = 0;
                }
                x2 <<= 16;
                hsl2 <<= 15;
                if (y2 < 0) {
                    x2 -= distance_v2_v3_x * y2;
                    hsl2 -= distance_v2_v3_hsl * y2;
                    y2 = 0;
                }
                if (y1 != y2 && distance_v1_v3_x < distance_v1_v2_x || y1 == y2 && distance_v1_v3_x > distance_v2_v3_x) {
                    y3 -= y2;
                    y2 -= y1;
                    for (y1 = scanOffsets[y1]; --y2 >= 0; y1 += Rasterizer2D.Rasterizer2D_width) {
                        drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y1, x3 >> 16, x1 >> 16, hsl3 >> 7, hsl1 >> 7, z1, depth_slope);
                        x3 += distance_v1_v3_x;
                        x1 += distance_v1_v2_x;
                        hsl3 += distance_v1_v3_hsl;
                        hsl1 += distance_v1_v2_hsl;
                        z1 += depth_increment;
                    }

                    while (--y3 >= 0) {
                        drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y1, x3 >> 16, x2 >> 16, hsl3 >> 7, hsl2 >> 7, z1, depth_slope);
                        x3 += distance_v1_v3_x;
                        x2 += distance_v2_v3_x;
                        hsl3 += distance_v1_v3_hsl;
                        hsl2 += distance_v2_v3_hsl;
                        y1 += Rasterizer2D.Rasterizer2D_width;
                        z1 += depth_increment;
                    }
                    return;
                }
                y3 -= y2;
                y2 -= y1;
                for (y1 = scanOffsets[y1]; --y2 >= 0; y1 += Rasterizer2D.Rasterizer2D_width) {
                    drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y1, x1 >> 16, x3 >> 16, hsl1 >> 7, hsl3 >> 7, z1, depth_slope);
                    x3 += distance_v1_v3_x;
                    x1 += distance_v1_v2_x;
                    hsl3 += distance_v1_v3_hsl;
                    hsl1 += distance_v1_v2_hsl;
                    z1 += depth_increment;
                }

                while (--y3 >= 0) {
                    drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y1, x2 >> 16, x3 >> 16, hsl2 >> 7, hsl3 >> 7, z1, depth_slope);
                    x3 += distance_v1_v3_x;
                    x2 += distance_v2_v3_x;
                    hsl3 += distance_v1_v3_hsl;
                    hsl2 += distance_v2_v3_hsl;
                    y1 += Rasterizer2D.Rasterizer2D_width;
                    z1 += depth_increment;
                }
                return;
            }
            x2 = x1 <<= 16;
            hsl2 = hsl1 <<= 15;
            if (y1 < 0) {
                x2 -= distance_v1_v3_x * y1;
                x1 -= distance_v1_v2_x * y1;
                hsl2 -= distance_v1_v3_hsl * y1;
                hsl1 -= distance_v1_v2_hsl * y1;
                z1 -= depth_increment * y1;
                y1 = 0;
            }
            x3 <<= 16;
            hsl3 <<= 15;
            if (y3 < 0) {
                x3 -= distance_v2_v3_x * y3;
                hsl3 -= distance_v2_v3_hsl * y3;
                y3 = 0;
            }
            if (y1 != y3 && distance_v1_v3_x < distance_v1_v2_x || y1 == y3 && distance_v2_v3_x > distance_v1_v2_x) {
                y2 -= y3;
                y3 -= y1;
                for (y1 = scanOffsets[y1]; --y3 >= 0; y1 += Rasterizer2D.Rasterizer2D_width) {
                    drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y1, x2 >> 16, x1 >> 16, hsl2 >> 7, hsl1 >> 7, z1, depth_slope);
                    x2 += distance_v1_v3_x;
                    x1 += distance_v1_v2_x;
                    hsl2 += distance_v1_v3_hsl;
                    hsl1 += distance_v1_v2_hsl;
                    z1 += depth_increment;
                }

                while (--y2 >= 0) {
                    drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y1, x3 >> 16, x1 >> 16, hsl3 >> 7, hsl1 >> 7, z1, depth_slope);
                    x3 += distance_v2_v3_x;
                    x1 += distance_v1_v2_x;
                    hsl3 += distance_v2_v3_hsl;
                    hsl1 += distance_v1_v2_hsl;
                    y1 += Rasterizer2D.Rasterizer2D_width;
                    z1 += depth_increment;
                }
                return;
            }
            y2 -= y3;
            y3 -= y1;
            for (y1 = scanOffsets[y1]; --y3 >= 0; y1 += Rasterizer2D.Rasterizer2D_width) {
                drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y1, x1 >> 16, x2 >> 16, hsl1 >> 7, hsl2 >> 7, z1, depth_slope);
                x2 += distance_v1_v3_x;
                x1 += distance_v1_v2_x;
                hsl2 += distance_v1_v3_hsl;
                hsl1 += distance_v1_v2_hsl;
                z1 += depth_increment;
            }

            while (--y2 >= 0) {
                drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y1, x1 >> 16, x3 >> 16, hsl1 >> 7, hsl3 >> 7, z1, depth_slope);
                x3 += distance_v2_v3_x;
                x1 += distance_v1_v2_x;
                hsl3 += distance_v2_v3_hsl;
                hsl1 += distance_v1_v2_hsl;
                y1 += Rasterizer2D.Rasterizer2D_width;
                z1 += depth_increment;
            }
            return;
        }
        if (y2 <= y3) {
            if (y2 >= Rasterizer2D.Rasterizer2D_yClipEnd)
                return;
            if (y3 > Rasterizer2D.Rasterizer2D_yClipEnd)
                y3 = Rasterizer2D.Rasterizer2D_yClipEnd;
            if (y1 > Rasterizer2D.Rasterizer2D_yClipEnd)
                y1 = Rasterizer2D.Rasterizer2D_yClipEnd;
            z2 = z2 - depth_slope * x2 + depth_slope;
            if (y3 < y1) {
                x1 = x2 <<= 16;
                hsl1 = hsl2 <<= 15;
                if (y2 < 0) {
                    x1 -= distance_v1_v2_x * y2;
                    x2 -= distance_v2_v3_x * y2;
                    hsl1 -= distance_v1_v2_hsl * y2;
                    hsl2 -= distance_v2_v3_hsl * y2;
                    z2 -= depth_increment * y2;
                    y2 = 0;
                }
                x3 <<= 16;
                hsl3 <<= 15;
                if (y3 < 0) {
                    x3 -= distance_v1_v3_x * y3;
                    hsl3 -= distance_v1_v3_hsl * y3;
                    y3 = 0;
                }
                if (y2 != y3 && distance_v1_v2_x < distance_v2_v3_x || y2 == y3 && distance_v1_v2_x > distance_v1_v3_x) {
                    y1 -= y3;
                    y3 -= y2;
                    for (y2 = scanOffsets[y2]; --y3 >= 0; y2 += Rasterizer2D.Rasterizer2D_width) {
                        drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y2, x1 >> 16, x2 >> 16, hsl1 >> 7, hsl2 >> 7, z2, depth_slope);
                        x1 += distance_v1_v2_x;
                        x2 += distance_v2_v3_x;
                        hsl1 += distance_v1_v2_hsl;
                        hsl2 += distance_v2_v3_hsl;
                        z2 += depth_increment;
                    }

                    while (--y1 >= 0) {
                        drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y2, x1 >> 16, x3 >> 16, hsl1 >> 7, hsl3 >> 7, z2, depth_slope);
                        x1 += distance_v1_v2_x;
                        x3 += distance_v1_v3_x;
                        hsl1 += distance_v1_v2_hsl;
                        hsl3 += distance_v1_v3_hsl;
                        y2 += Rasterizer2D.Rasterizer2D_width;
                        z2 += depth_increment;
                    }
                    return;
                }
                y1 -= y3;
                y3 -= y2;
                for (y2 = scanOffsets[y2]; --y3 >= 0; y2 += Rasterizer2D.Rasterizer2D_width) {
                    drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y2, x2 >> 16, x1 >> 16, hsl2 >> 7, hsl1 >> 7, z2, depth_slope);
                    x1 += distance_v1_v2_x;
                    x2 += distance_v2_v3_x;
                    hsl1 += distance_v1_v2_hsl;
                    hsl2 += distance_v2_v3_hsl;
                    z2 += depth_increment;
                }

                while (--y1 >= 0) {
                    drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y2, x3 >> 16, x1 >> 16, hsl3 >> 7, hsl1 >> 7, z2, depth_slope);
                    x1 += distance_v1_v2_x;
                    x3 += distance_v1_v3_x;
                    hsl1 += distance_v1_v2_hsl;
                    hsl3 += distance_v1_v3_hsl;
                    y2 += Rasterizer2D.Rasterizer2D_width;
                    z2 += depth_increment;
                }
                return;
            }
            x3 = x2 <<= 16;
            hsl3 = hsl2 <<= 15;
            if (y2 < 0) {
                x3 -= distance_v1_v2_x * y2;
                x2 -= distance_v2_v3_x * y2;
                hsl3 -= distance_v1_v2_hsl * y2;
                hsl2 -= distance_v2_v3_hsl * y2;
                z2 -= depth_increment * y2;
                y2 = 0;
            }
            x1 <<= 16;
            hsl1 <<= 15;
            if (y1 < 0) {
                x1 -= distance_v1_v3_x * y1;
                hsl1 -= distance_v1_v3_hsl * y1;
                y1 = 0;
            }
            if (distance_v1_v2_x < distance_v2_v3_x) {
                y3 -= y1;
                y1 -= y2;
                for (y2 = scanOffsets[y2]; --y1 >= 0; y2 += Rasterizer2D.Rasterizer2D_width) {
                    drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y2, x3 >> 16, x2 >> 16, hsl3 >> 7, hsl2 >> 7, z2, depth_slope);
                    x3 += distance_v1_v2_x;
                    x2 += distance_v2_v3_x;
                    hsl3 += distance_v1_v2_hsl;
                    hsl2 += distance_v2_v3_hsl;
                    z2 += depth_increment;
                }

                while (--y3 >= 0) {
                    drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y2, x1 >> 16, x2 >> 16, hsl1 >> 7, hsl2 >> 7, z2, depth_slope);
                    x1 += distance_v1_v3_x;
                    x2 += distance_v2_v3_x;
                    hsl1 += distance_v1_v3_hsl;
                    hsl2 += distance_v2_v3_hsl;
                    y2 += Rasterizer2D.Rasterizer2D_width;
                    z2 += depth_increment;
                }
                return;
            }
            y3 -= y1;
            y1 -= y2;
            for (y2 = scanOffsets[y2]; --y1 >= 0; y2 += Rasterizer2D.Rasterizer2D_width) {
                drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y2, x2 >> 16, x3 >> 16, hsl2 >> 7, hsl3 >> 7, z2, depth_slope);
                x3 += distance_v1_v2_x;
                x2 += distance_v2_v3_x;
                hsl3 += distance_v1_v2_hsl;
                hsl2 += distance_v2_v3_hsl;
                z2 += depth_increment;
            }

            while (--y3 >= 0) {
                drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y2, x2 >> 16, x1 >> 16, hsl2 >> 7, hsl1 >> 7, z2, depth_slope);
                x1 += distance_v1_v3_x;
                x2 += distance_v2_v3_x;
                hsl1 += distance_v1_v3_hsl;
                hsl2 += distance_v2_v3_hsl;
                y2 += Rasterizer2D.Rasterizer2D_width;
                z2 += depth_increment;
            }
            return;
        }
        if (y3 >= Rasterizer2D.Rasterizer2D_yClipEnd)
            return;
        if (y1 > Rasterizer2D.Rasterizer2D_yClipEnd)
            y1 = Rasterizer2D.Rasterizer2D_yClipEnd;
        if (y2 > Rasterizer2D.Rasterizer2D_yClipEnd)
            y2 = Rasterizer2D.Rasterizer2D_yClipEnd;
        z3 = z3 - depth_slope * x3 + depth_slope;
        if (y1 < y2) {
            x2 = x3 <<= 16;
            hsl2 = hsl3 <<= 15;
            if (y3 < 0) {
                x2 -= distance_v2_v3_x * y3;
                x3 -= distance_v1_v3_x * y3;
                hsl2 -= distance_v2_v3_hsl * y3;
                hsl3 -= distance_v1_v3_hsl * y3;
                z3 -= depth_increment * y3;
                y3 = 0;
            }
            x1 <<= 16;
            hsl1 <<= 15;
            if (y1 < 0) {
                x1 -= distance_v1_v2_x * y1;
                hsl1 -= distance_v1_v2_hsl * y1;
                y1 = 0;
            }
            if (distance_v2_v3_x < distance_v1_v3_x) {
                y2 -= y1;
                y1 -= y3;
                for (y3 = scanOffsets[y3]; --y1 >= 0; y3 += Rasterizer2D.Rasterizer2D_width) {
                    drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y3, x2 >> 16, x3 >> 16, hsl2 >> 7, hsl3 >> 7, z3, depth_slope);
                    x2 += distance_v2_v3_x;
                    x3 += distance_v1_v3_x;
                    hsl2 += distance_v2_v3_hsl;
                    hsl3 += distance_v1_v3_hsl;
                    z3 += depth_increment;
                }

                while (--y2 >= 0) {
                    drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y3, x2 >> 16, x1 >> 16, hsl2 >> 7, hsl1 >> 7, z3, depth_slope);
                    x2 += distance_v2_v3_x;
                    x1 += distance_v1_v2_x;
                    hsl2 += distance_v2_v3_hsl;
                    hsl1 += distance_v1_v2_hsl;
                    y3 += Rasterizer2D.Rasterizer2D_width;
                    z3 += depth_increment;
                }
                return;
            }
            y2 -= y1;
            y1 -= y3;
            for (y3 = scanOffsets[y3]; --y1 >= 0; y3 += Rasterizer2D.Rasterizer2D_width) {
                drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y3, x3 >> 16, x2 >> 16, hsl3 >> 7, hsl2 >> 7, z3, depth_slope);
                x2 += distance_v2_v3_x;
                x3 += distance_v1_v3_x;
                hsl2 += distance_v2_v3_hsl;
                hsl3 += distance_v1_v3_hsl;
                z3 += depth_increment;
            }

            while (--y2 >= 0) {
                drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y3, x1 >> 16, x2 >> 16, hsl1 >> 7, hsl2 >> 7, z3, depth_slope);
                x2 += distance_v2_v3_x;
                x1 += distance_v1_v2_x;
                hsl2 += distance_v2_v3_hsl;
                hsl1 += distance_v1_v2_hsl;
                y3 += Rasterizer2D.Rasterizer2D_width;
                z3 += depth_increment;
            }
            return;
        }
        x1 = x3 <<= 16;
        hsl1 = hsl3 <<= 15;
        if (y3 < 0) {
            x1 -= distance_v2_v3_x * y3;
            x3 -= distance_v1_v3_x * y3;
            hsl1 -= distance_v2_v3_hsl * y3;
            hsl3 -= distance_v1_v3_hsl * y3;
            z3 -= depth_increment * y3;
            y3 = 0;
        }
        x2 <<= 16;
        hsl2 <<= 15;
        if (y2 < 0) {
            x2 -= distance_v1_v2_x * y2;
            hsl2 -= distance_v1_v2_hsl * y2;
            y2 = 0;
        }
        if (distance_v2_v3_x < distance_v1_v3_x) {
            y1 -= y2;
            y2 -= y3;
            for (y3 = scanOffsets[y3]; --y2 >= 0; y3 += Rasterizer2D.Rasterizer2D_width) {
                drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y3, x1 >> 16, x3 >> 16, hsl1 >> 7, hsl3 >> 7, z3, depth_slope);
                x1 += distance_v2_v3_x;
                x3 += distance_v1_v3_x;
                hsl1 += distance_v2_v3_hsl;
                hsl3 += distance_v1_v3_hsl;
                z3 += depth_increment;
            }

            while (--y1 >= 0) {
                drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y3, x2 >> 16, x3 >> 16, hsl2 >> 7, hsl3 >> 7, z3, depth_slope);
                x2 += distance_v1_v2_x;
                x3 += distance_v1_v3_x;
                hsl2 += distance_v1_v2_hsl;
                hsl3 += distance_v1_v3_hsl;
                y3 += Rasterizer2D.Rasterizer2D_width;
                z3 += depth_increment;
            }
            return;
        }
        y1 -= y2;
        y2 -= y3;
        for (y3 = scanOffsets[y3]; --y2 >= 0; y3 += Rasterizer2D.Rasterizer2D_width) {
            drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y3, x3 >> 16, x1 >> 16, hsl3 >> 7, hsl1 >> 7, z3, depth_slope);
            x1 += distance_v2_v3_x;
            x3 += distance_v1_v3_x;
            hsl1 += distance_v2_v3_hsl;
            hsl3 += distance_v1_v3_hsl;
            z3 += depth_increment;
        }

        while (--y1 >= 0) {
            drawGouraudScanline(Rasterizer2D.Rasterizer2D_pixels, y3, x3 >> 16, x2 >> 16, hsl3 >> 7, hsl2 >> 7, z3, depth_slope);
            x2 += distance_v1_v2_x;
            x3 += distance_v1_v3_x;
            hsl2 += distance_v1_v2_hsl;
            hsl3 += distance_v1_v3_hsl;
            y3 += Rasterizer2D.Rasterizer2D_width;
            z3 += depth_increment;
        }
    }

    public static void method4270(float z_a, float z_b, float z_c, int y_a, int y_b, int y_c, int x_a, int x_b, int x_c, int var6, int var7, int var8) {
        if (z_a < 0 || z_b < 0 || z_c < 0) return;
        int var9 = x_b - x_a;
        int var10 = y_b - y_a;
        int var11 = x_c - x_a;
        int var12 = y_c - y_a;
        int var13 = var7 - var6;
        int var14 = var8 - var6;
        int var15;
        if (y_c != y_b) {
            var15 = (x_c - x_b << 14) / (y_c - y_b);
        } else {
            var15 = 0;
        }

        int var16;
        if (y_a != y_b) {
            var16 = (var9 << 14) / var10;
        } else {
            var16 = 0;
        }

        int var17;
        if (y_a != y_c) {
            var17 = (var11 << 14) / var12;
        } else {
            var17 = 0;
        }

        float b_aX = x_b - x_a;
        float b_aY = y_b - y_a;
        float c_aX = x_c - x_a;
        float c_aY = y_c - y_a;
        float b_aZ = z_b - z_a;
        float c_aZ = z_c - z_a;

        float div = b_aX * c_aY - c_aX * b_aY;
        float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
        float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;

        int var18 = var9 * var12 - var11 * var10;
        if (var18 != 0) {
            int var19 = (var13 * var12 - var14 * var10 << 8) / var18;
            int var20 = (var14 * var9 - var13 * var11 << 8) / var18;
            if (y_a <= y_b && y_a <= y_c) {
                if (y_a < Rasterizer2D_yClipEnd) {
                    if (y_b > Rasterizer2D_yClipEnd) {
                        y_b = Rasterizer2D_yClipEnd;
                    }

                    if (y_c > Rasterizer2D_yClipEnd) {
                        y_c = Rasterizer2D_yClipEnd;
                    }

                    var6 = var19 + ((var6 << 8) - x_a * var19);
                    z_a = z_a - depth_slope * x_a + depth_slope;
                    if (y_b < y_c) {
                        x_c = x_a <<= 14;
                        if (y_a < 0) {
                            x_c -= y_a * var17;
                            x_a -= y_a * var16;
                            var6 -= y_a * var20;
                            z_a -= depth_increment * y_a;
                            y_a = 0;
                        }

                        x_b <<= 14;
                        if (y_b < 0) {
                            x_b -= var15 * y_b;
                            y_b = 0;
                        }

                        if (y_a != y_b && var17 < var16 || y_a == y_b && var17 > var15) {
                            y_c -= y_b;
                            y_b -= y_a;
                            y_a = scanOffsets[y_a];

                            while(true) {
                                --y_b;
                                if (y_b < 0) {
                                    while(true) {
                                        --y_c;
                                        if (y_c < 0) {
                                            return;
                                        }

                                        Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_a, 0, 0, x_c >> 14, x_b >> 14, var6, var19, z_a, depth_slope);
                                        x_c += var17;
                                        x_b += var15;
                                        var6 += var20;
                                        y_a += Rasterizer2D.Rasterizer2D_width;
                                        z_a += depth_increment;
                                    }
                                }

                                Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_a, 0, 0, x_c >> 14, x_a >> 14, var6, var19, z_a, depth_slope);
                                x_c += var17;
                                x_a += var16;
                                var6 += var20;
                                y_a += Rasterizer2D.Rasterizer2D_width;
                                z_a += depth_increment;
                            }
                        } else {
                            y_c -= y_b;
                            y_b -= y_a;
                            y_a = scanOffsets[y_a];

                            while(true) {
                                --y_b;
                                if (y_b < 0) {
                                    while(true) {
                                        --y_c;
                                        if (y_c < 0) {
                                            return;
                                        }

                                        Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_a, 0, 0, x_b >> 14, x_c >> 14, var6, var19, z_a, depth_slope);
                                        x_c += var17;
                                        x_b += var15;
                                        var6 += var20;
                                        y_a += Rasterizer2D.Rasterizer2D_width;
                                        z_a += depth_increment;
                                    }
                                }

                                Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_a, 0, 0, x_a >> 14, x_c >> 14, var6, var19, z_a, depth_slope);
                                x_c += var17;
                                x_a += var16;
                                var6 += var20;
                                y_a += Rasterizer2D.Rasterizer2D_width;
                                z_a += depth_increment;
                            }
                        }
                    } else {
                        x_b = x_a <<= 14;
                        if (y_a < 0) {
                            x_b -= y_a * var17;
                            x_a -= y_a * var16;
                            var6 -= y_a * var20;
                            z_a -= depth_increment * y_a;
                            y_a = 0;
                        }

                        x_c <<= 14;
                        if (y_c < 0) {
                            x_c -= var15 * y_c;
                            y_c = 0;
                        }

                        if (y_a != y_c && var17 < var16 || y_a == y_c && var15 > var16) {
                            y_b -= y_c;
                            y_c -= y_a;
                            y_a = scanOffsets[y_a];

                            while(true) {
                                --y_c;
                                if (y_c < 0) {
                                    while(true) {
                                        --y_b;
                                        if (y_b < 0) {
                                            return;
                                        }

                                        Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_a, 0, 0, x_c >> 14, x_a >> 14, var6, var19, z_a, depth_slope);
                                        x_c += var15;
                                        x_a += var16;
                                        var6 += var20;
                                        y_a += Rasterizer2D.Rasterizer2D_width;
                                        z_a += depth_increment;
                                    }
                                }

                                Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_a, 0, 0, x_b >> 14, x_a >> 14, var6, var19, z_a, depth_slope);
                                x_b += var17;
                                x_a += var16;
                                var6 += var20;
                                y_a += Rasterizer2D.Rasterizer2D_width;
                                z_a += depth_increment;
                            }
                        } else {
                            y_b -= y_c;
                            y_c -= y_a;
                            y_a = scanOffsets[y_a];

                            while(true) {
                                --y_c;
                                if (y_c < 0) {
                                    while(true) {
                                        --y_b;
                                        if (y_b < 0) {
                                            return;
                                        }

                                        Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_a, 0, 0, x_a >> 14, x_c >> 14, var6, var19, z_a, depth_slope);
                                        x_c += var15;
                                        x_a += var16;
                                        var6 += var20;
                                        y_a += Rasterizer2D.Rasterizer2D_width;
                                        z_a += depth_increment;
                                    }
                                }

                                Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_a, 0, 0, x_a >> 14, x_b >> 14, var6, var19, z_a, depth_slope);
                                x_b += var17;
                                x_a += var16;
                                var6 += var20;
                                y_a += Rasterizer2D.Rasterizer2D_width;
                                z_a += depth_increment;
                            }
                        }
                    }
                }
            } else if (y_b <= y_c) {
                if (y_b < Rasterizer2D_yClipEnd) {
                    if (y_c > Rasterizer2D_yClipEnd) {
                        y_c = Rasterizer2D_yClipEnd;
                    }

                    if (y_a > Rasterizer2D_yClipEnd) {
                        y_a = Rasterizer2D_yClipEnd;
                    }

                    z_b = z_b - depth_slope * x_b + depth_slope;
                    var7 = var19 + ((var7 << 8) - var19 * x_b);
                    if (y_c < y_a) {
                        x_a = x_b <<= 14;
                        if (y_b < 0) {
                            x_a -= var16 * y_b;
                            x_b -= var15 * y_b;
                            var7 -= var20 * y_b;
                            z_b -= depth_increment * y_b;
                            y_b = 0;
                        }

                        x_c <<= 14;
                        if (y_c < 0) {
                            x_c -= var17 * y_c;
                            y_c = 0;
                        }

                        if (y_c != y_b && var16 < var15 || y_c == y_b && var16 > var17) {
                            y_a -= y_c;
                            y_c -= y_b;
                            y_b = scanOffsets[y_b];

                            while(true) {
                                --y_c;
                                if (y_c < 0) {
                                    while(true) {
                                        --y_a;
                                        if (y_a < 0) {
                                            return;
                                        }

                                        Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_b, 0, 0, x_a >> 14, x_c >> 14, var7, var19, z_b, depth_slope);
                                        x_a += var16;
                                        x_c += var17;
                                        var7 += var20;
                                        y_b += Rasterizer2D.Rasterizer2D_width;
                                        z_b += depth_increment;
                                    }
                                }

                                Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_b, 0, 0, x_a >> 14, x_b >> 14, var7, var19, z_b, depth_slope);
                                x_a += var16;
                                x_b += var15;
                                var7 += var20;
                                y_b += Rasterizer2D.Rasterizer2D_width;
                                z_b += depth_increment;
                            }
                        } else {
                            y_a -= y_c;
                            y_c -= y_b;
                            y_b = scanOffsets[y_b];

                            while(true) {
                                --y_c;
                                if (y_c < 0) {
                                    while(true) {
                                        --y_a;
                                        if (y_a < 0) {
                                            return;
                                        }

                                        Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_b, 0, 0, x_c >> 14, x_a >> 14, var7, var19, z_b, depth_slope);
                                        x_a += var16;
                                        x_c += var17;
                                        var7 += var20;
                                        y_b += Rasterizer2D.Rasterizer2D_width;
                                        z_b += depth_increment;
                                    }
                                }

                                Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_b, 0, 0, x_b >> 14, x_a >> 14, var7, var19, z_b, depth_slope);
                                x_a += var16;
                                x_b += var15;
                                var7 += var20;
                                y_b += Rasterizer2D.Rasterizer2D_width;
                                z_b += depth_increment;
                            }
                        }
                    } else {
                        x_c = x_b <<= 14;
                        if (y_b < 0) {
                            x_c -= var16 * y_b;
                            x_b -= var15 * y_b;
                            var7 -= var20 * y_b;
                            z_b -= depth_increment * y_b;
                            y_b = 0;
                        }

                        x_a <<= 14;
                        if (y_a < 0) {
                            x_a -= y_a * var17;
                            y_a = 0;
                        }

                        if (var16 < var15) {
                            y_c -= y_a;
                            y_a -= y_b;
                            y_b = scanOffsets[y_b];

                            while(true) {
                                --y_a;
                                if (y_a < 0) {
                                    while(true) {
                                        --y_c;
                                        if (y_c < 0) {
                                            return;
                                        }

                                        Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_b, 0, 0, x_a >> 14, x_b >> 14, var7, var19, z_b, depth_slope);
                                        x_a += var17;
                                        x_b += var15;
                                        var7 += var20;
                                        y_b += Rasterizer2D.Rasterizer2D_width;
                                        z_b += depth_increment;
                                    }
                                }

                                Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_b, 0, 0, x_c >> 14, x_b >> 14, var7, var19, z_b, depth_slope);
                                x_c += var16;
                                x_b += var15;
                                var7 += var20;
                                y_b += Rasterizer2D.Rasterizer2D_width;
                                z_b += depth_increment;
                            }
                        } else {
                            y_c -= y_a;
                            y_a -= y_b;
                            y_b = scanOffsets[y_b];

                            while(true) {
                                --y_a;
                                if (y_a < 0) {
                                    while(true) {
                                        --y_c;
                                        if (y_c < 0) {
                                            return;
                                        }

                                        Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_b, 0, 0, x_b >> 14, x_a >> 14, var7, var19, z_b, depth_slope);
                                        x_a += var17;
                                        x_b += var15;
                                        var7 += var20;
                                        y_b += Rasterizer2D.Rasterizer2D_width;
                                        z_b += depth_increment;
                                    }
                                }

                                Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_b, 0, 0, x_b >> 14, x_c >> 14, var7, var19, z_b, depth_slope);
                                x_c += var16;
                                x_b += var15;
                                var7 += var20;
                                y_b += Rasterizer2D.Rasterizer2D_width;
                                z_b += depth_increment;
                            }
                        }
                    }
                }
            } else if (y_c < Rasterizer2D_yClipEnd) {
                if (y_a > Rasterizer2D_yClipEnd) {
                    y_a = Rasterizer2D_yClipEnd;
                }

                if (y_b > Rasterizer2D_yClipEnd) {
                    y_b = Rasterizer2D_yClipEnd;
                }

                var8 = var19 + ((var8 << 8) - x_c * var19);
                z_c = z_c - depth_slope * x_c + depth_slope;
                if (y_a < y_b) {
                    x_b = x_c <<= 14;
                    if (y_c < 0) {
                        x_b -= var15 * y_c;
                        x_c -= var17 * y_c;
                        var8 -= var20 * y_c;
                        z_c -= depth_increment * y_c;
                        y_c = 0;
                    }

                    x_a <<= 14;
                    if (y_a < 0) {
                        x_a -= y_a * var16;
                        y_a = 0;
                    }

                    if (var15 < var17) {
                        y_b -= y_a;
                        y_a -= y_c;
                        y_c = scanOffsets[y_c];

                        while(true) {
                            --y_a;
                            if (y_a < 0) {
                                while(true) {
                                    --y_b;
                                    if (y_b < 0) {
                                        return;
                                    }

                                    Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_c, 0, 0, x_b >> 14, x_a >> 14, var8, var19, z_c, depth_slope);
                                    x_b += var15;
                                    x_a += var16;
                                    var8 += var20;
                                    y_c += Rasterizer2D.Rasterizer2D_width;
                                    z_c += depth_increment;
                                }
                            }

                            Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_c, 0, 0, x_b >> 14, x_c >> 14, var8, var19, z_c, depth_slope);
                            x_b += var15;
                            x_c += var17;
                            var8 += var20;
                            y_c += Rasterizer2D.Rasterizer2D_width;
                            z_c += depth_increment;
                        }
                    } else {
                        y_b -= y_a;
                        y_a -= y_c;
                        y_c = scanOffsets[y_c];

                        while(true) {
                            --y_a;
                            if (y_a < 0) {
                                while(true) {
                                    --y_b;
                                    if (y_b < 0) {
                                        return;
                                    }

                                    Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_c, 0, 0, x_a >> 14, x_b >> 14, var8, var19, z_c, depth_slope);
                                    x_b += var15;
                                    x_a += var16;
                                    var8 += var20;
                                    y_c += Rasterizer2D.Rasterizer2D_width;
                                    z_c += depth_increment;
                                }
                            }

                            Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_c, 0, 0, x_c >> 14, x_b >> 14, var8, var19, z_c, depth_slope);
                            x_b += var15;
                            x_c += var17;
                            var8 += var20;
                            y_c += Rasterizer2D.Rasterizer2D_width;
                            z_c += depth_increment;
                        }
                    }
                } else {
                    x_a = x_c <<= 14;
                    if (y_c < 0) {
                        x_a -= var15 * y_c;
                        x_c -= var17 * y_c;
                        var8 -= var20 * y_c;
                        z_c -= depth_increment * y_c;
                        y_c = 0;
                    }

                    x_b <<= 14;
                    if (y_b < 0) {
                        x_b -= var16 * y_b;
                        y_b = 0;
                    }

                    if (var15 < var17) {
                        y_a -= y_b;
                        y_b -= y_c;
                        y_c = scanOffsets[y_c];

                        while(true) {
                            --y_b;
                            if (y_b < 0) {
                                while(true) {
                                    --y_a;
                                    if (y_a < 0) {
                                        return;
                                    }

                                    Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_c, 0, 0, x_b >> 14, x_c >> 14, var8, var19, z_c, depth_slope);
                                    x_b += var16;
                                    x_c += var17;
                                    var8 += var20;
                                    y_c += Rasterizer2D.Rasterizer2D_width;
                                    z_c += depth_increment;
                                }
                            }

                            Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_c, 0, 0, x_a >> 14, x_c >> 14, var8, var19, z_c, depth_slope);
                            x_a += var15;
                            x_c += var17;
                            var8 += var20;
                            y_c += Rasterizer2D.Rasterizer2D_width;
                            z_c += depth_increment;
                        }
                    } else {
                        y_a -= y_b;
                        y_b -= y_c;
                        y_c = scanOffsets[y_c];

                        while(true) {
                            --y_b;
                            if (y_b < 0) {
                                while(true) {
                                    --y_a;
                                    if (y_a < 0) {
                                        return;
                                    }

                                    Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_c, 0, 0, x_c >> 14, x_b >> 14, var8, var19, z_c, depth_slope);
                                    x_b += var16;
                                    x_c += var17;
                                    var8 += var20;
                                    y_c += Rasterizer2D.Rasterizer2D_width;
                                    z_c += depth_increment;
                                }
                            }

                            Rasterizer3D_vertAlpha(Rasterizer2D.Rasterizer2D_pixels, y_c, 0, 0, x_c >> 14, x_a >> 14, var8, var19, z_c, depth_slope);
                            x_a += var15;
                            x_c += var17;
                            var8 += var20;
                            y_c += Rasterizer2D.Rasterizer2D_width;
                            z_c += depth_increment;
                        }
                    }
                }
            }
        }
    }

    static final void Rasterizer3D_vertAlpha(int[] var0, int offset, int rgb, int var3, int x1, int x2, int var6, int var7, float depth, float depth_slope) {
        if (triangleIsOutOfBounds) {
            if (x2 > Rasterizer2D.lastX) {
                x2 = Rasterizer2D.lastX;
            }

            if (x1 < 0) {
                x1 = 0;
            }
        }

        if (x1 < x2) {
            offset += x1;
            var6 += x1 * var7;
            depth += depth_slope * (float) x1;
            int var8;
            int var9;
            int var10;
            int var11;
            if (notTextured) {
                var3 = x2 - x1 >> 2;
                var7 <<= 2;
                if (alpha == 0) {
                    if (var3 > 0) {
                        do {
                            var8 = (var6 & (var6 >> 31 & 1) - 1) >> 8;
                            rgb = hslToRgb[var8];
                            var6 += var7;
                            depthBuffer[offset] = depth;
                            depth += depth_slope;
                            var0[offset++] = rgb;
                            depthBuffer[offset] = depth;
                            depth += depth_slope;
                            var0[offset++] = rgb;
                            depthBuffer[offset] = depth;
                            depth += depth_slope;
                            var0[offset++] = rgb;
                            depthBuffer[offset] = depth;
                            depth += depth_slope;
                            var0[offset++] = rgb;
                            --var3;
                        } while(var3 > 0);
                    }

                    var3 = x2 - x1 & 3;
                    if (var3 > 0) {
                        var8 = (var6 & (var6 >> 31 & 1) - 1) >> 8;
                        rgb = hslToRgb[var8];

                        do {
                            depthBuffer[offset] = depth;
                            depth += depth_slope;
                            var0[offset++] = rgb;
                            --var3;
                        } while(var3 > 0);
                    }
                } else {
                    var8 = alpha;
                    var9 = 256 - alpha;
                    if (var3 > 0) {
                        do {
                            var10 = (var6 & (var6 >> 31 & 1) - 1) >> 8;
                            rgb = hslToRgb[var10];
                            var6 += var7;
                            rgb = (var9 * (rgb & '\uff00') >> 8 & '\uff00') + (var9 * (rgb & 16711935) >> 8 & 16711935);
                            var11 = var0[offset];
                            var0[offset++] = ((var11 & 16711935) * var8 >> 8 & 16711935) + rgb + (var8 * (var11 & '\uff00') >> 8 & '\uff00');
                            var11 = var0[offset];
                            var0[offset++] = ((var11 & 16711935) * var8 >> 8 & 16711935) + rgb + (var8 * (var11 & '\uff00') >> 8 & '\uff00');
                            var11 = var0[offset];
                            var0[offset++] = ((var11 & 16711935) * var8 >> 8 & 16711935) + rgb + (var8 * (var11 & '\uff00') >> 8 & '\uff00');
                            var11 = var0[offset];
                            var0[offset++] = ((var11 & 16711935) * var8 >> 8 & 16711935) + rgb + (var8 * (var11 & '\uff00') >> 8 & '\uff00');
                            --var3;
                        } while(var3 > 0);
                    }

                    var3 = x2 - x1 & 3;
                    if (var3 > 0) {
                        var10 = (var6 & (var6 >> 31 & 1) - 1) >> 8;
                        rgb = hslToRgb[var10];
                        rgb = (var9 * (rgb & '\uff00') >> 8 & '\uff00') + (var9 * (rgb & 16711935) >> 8 & 16711935);

                        do {
                            var11 = var0[offset];
                            var0[offset++] = ((var11 & 16711935) * var8 >> 8 & 16711935) + rgb + (var8 * (var11 & '\uff00') >> 8 & '\uff00');
                            --var3;
                        } while(var3 > 0);
                    }
                }

            } else {
                var3 = x2 - x1;
                if (alpha == 0) {
                    do {
                        var8 = (var6 & (var6 >> 31 & 1) - 1) >> 8;
                        depthBuffer[offset] = depth;
                        depth += depth_slope;
                        var0[offset++] = hslToRgb[var8];
                        var6 += var7;
                        --var3;
                    } while(var3 > 0);
                } else {
                    var8 = alpha;
                    var9 = 256 - alpha;

                    do {
                        var10 = (var6 & (var6 >> 31 & 1) - 1) >> 8;
                        rgb = hslToRgb[var10];
                        var6 += var7;
                        rgb = (var9 * (rgb & '\uff00') >> 8 & '\uff00') + (var9 * (rgb & 16711935) >> 8 & 16711935);
                        var11 = var0[offset];
                        var0[offset++] = ((var11 & 16711935) * var8 >> 8 & 16711935) + rgb + (var8 * (var11 & '\uff00') >> 8 & '\uff00');
                        --var3;
                    } while(var3 > 0);
                }

            }
        }
    }

    private static void drawGouraudScanline(int[] pixelBuffer, int offset, int x1, int x2, int hsl1, int hsl2, float depth, float depth_slope) {
        int rgb;
        int k;
        if (notTextured) {
            int l1;
            if (triangleIsOutOfBounds) {
                if (x2 - x1 > 3)
                    l1 = (hsl2 - hsl1) / (x2 - x1);
                else
                    l1 = 0;
                if (x2 > Rasterizer2D.lastX)
                    x2 = Rasterizer2D.lastX;
                if (x1 < 0) {
                    hsl1 -= x1 * l1;
                    x1 = 0;
                }
                if (x1 >= x2)
                    return;
                offset += x1;
                depth += depth_slope * (float) x1;
                k = x2 - x1 >> 2;
                l1 <<= 2;
            } else {
                if (x1 >= x2)
                    return;
                offset += x1;
                depth += depth_slope * (float) x1;
                k = x2 - x1 >> 2;
                if (k > 0)
                    l1 = (hsl2 - hsl1) * __et_r[k] >> 15;
                else
                    l1 = 0;
            }
            if (alpha == 0) {
                while (--k >= 0) {
                    rgb = hslToRgb[hsl1 >> 8];
                    hsl1 += l1;
                    pixelBuffer[offset] = rgb;
                    depthBuffer[offset] = depth;
                    depth += depth_slope;
                    offset++;
                    pixelBuffer[offset] = rgb;
                    depthBuffer[offset] = depth;
                    depth += depth_slope;
                    offset++;
                    pixelBuffer[offset] = rgb;
                    depthBuffer[offset] = depth;
                    depth += depth_slope;
                    offset++;
                    pixelBuffer[offset] = rgb;
                    depthBuffer[offset] = depth;
                    depth += depth_slope;
                    offset++;
                }
                k = x2 - x1 & 3;
                if (k > 0) {
                    rgb = hslToRgb[hsl1 >> 8];
                    do {
                        pixelBuffer[offset] = rgb;
                        depthBuffer[offset] = depth;
                        depth += depth_slope;
                        offset++;
                    }
                    while (--k > 0);
                    return;
                }
            } else {
                int a1 = alpha;
                int a2 = 256 - alpha;
                while (--k >= 0) {
                    rgb = hslToRgb[hsl1 >> 8];
                    hsl1 += l1;
                    rgb = ((rgb & 0xff00ff) * a2 >> 8 & 0xff00ff) + ((rgb & 0xff00) * a2 >> 8 & 0xff00);
                    pixelBuffer[offset] = rgb + ((pixelBuffer[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((pixelBuffer[offset] & 0xff00) * a1 >> 8 & 0xff00);
                    offset++;
                    pixelBuffer[offset] = rgb + ((pixelBuffer[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((pixelBuffer[offset] & 0xff00) * a1 >> 8 & 0xff00);
                    offset++;
                    pixelBuffer[offset] = rgb + ((pixelBuffer[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((pixelBuffer[offset] & 0xff00) * a1 >> 8 & 0xff00);
                    offset++;
                    pixelBuffer[offset] = rgb + ((pixelBuffer[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((pixelBuffer[offset] & 0xff00) * a1 >> 8 & 0xff00);
                    offset++;
                }
                k = x2 - x1 & 3;
                if (k > 0) {
                    rgb = hslToRgb[hsl1 >> 8];
                    rgb = ((rgb & 0xff00ff) * a2 >> 8 & 0xff00ff) + ((rgb & 0xff00) * a2 >> 8 & 0xff00);
                    do {
                        pixelBuffer[offset] = rgb + ((pixelBuffer[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((pixelBuffer[offset] & 0xff00) * a1 >> 8 & 0xff00);
                        offset++;
                    }
                    while (--k > 0);
                }
            }
            return;
        }
        if (x1 >= x2)
            return;
        int i2 = (hsl2 - hsl1) / (x2 - x1);
        if (triangleIsOutOfBounds) {
            if (x2 > Rasterizer2D.lastX)
                x2 = Rasterizer2D.lastX;
            if (x1 < 0) {
                hsl1 -= x1 * i2;
                x1 = 0;
            }
            if (x1 >= x2)
                return;
        }
        offset += x1;
        depth += depth_slope * (float) x1;
        k = x2 - x1;
        if (alpha == 0) {
            do {
                pixelBuffer[offset] = hslToRgb[hsl1 >> 8];
                depthBuffer[offset] = depth;
                depth += depth_slope;
                offset++;
                hsl1 += i2;
            } while (--k > 0);
            return;
        }
        int a1 = alpha;
        int a2 = 256 - alpha;
        do {
            rgb = hslToRgb[hsl1 >> 8];
            hsl1 += i2;
            rgb = ((rgb & 0xff00ff) * a2 >> 8 & 0xff00ff) + ((rgb & 0xff00) * a2 >> 8 & 0xff00);
            pixelBuffer[offset] = rgb + ((pixelBuffer[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((pixelBuffer[offset] & 0xff00) * a1 >> 8 & 0xff00);
            offset++;
        } while (--k > 0);
    }

    /**
     * @param x_a x coordinate of vertex a
     * @param x_b x coordinate of vertex b
     * @param x_c x coordinate of vertex c
     * @param y_a y coordinate of vertex a
     * @param y_b y coordinate of vertex b
     * @param y_c y coordinate of vertex c
     * @param z_a z coordinate of vertex a
     * @param z_b z coordinate of vertex b
     * @param z_c z coordinate of vertex c
     * @param rgb the rgb color value of the triangle
     */
    public static void drawFlatTriangle(int x_a, int x_b, int x_c, int y_a, int y_b, int y_c, float z_a, float z_b, float z_c, int rgb) {
        if (z_a < 0 || z_b < 0 || z_c < 0) return;
        int a_to_b = 0;
        if (y_b != y_a) {
            a_to_b = (x_b - x_a << 16) / (y_b - y_a);
        }
        int b_to_c = 0;
        if (y_c != y_b) {
            b_to_c = (x_c - x_b << 16) / (y_c - y_b);
        }
        int c_to_a = 0;
        if (y_c != y_a) {
            c_to_a = (x_a - x_c << 16) / (y_a - y_c);
        }
        float b_aX = x_b - x_a;
        float b_aY = y_b - y_a;
        float c_aX = x_c - x_a;
        float c_aY = y_c - y_a;
        float b_aZ = z_b - z_a;
        float c_aZ = z_c - z_a;

        float div = b_aX * c_aY - c_aX * b_aY;
        float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
        float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;
        if (y_a <= y_b && y_a <= y_c) {
            if (y_a >= Rasterizer2D.Rasterizer2D_yClipEnd)
                return;
            if (y_b > Rasterizer2D.Rasterizer2D_yClipEnd)
                y_b = Rasterizer2D.Rasterizer2D_yClipEnd;
            if (y_c > Rasterizer2D.Rasterizer2D_yClipEnd)
                y_c = Rasterizer2D.Rasterizer2D_yClipEnd;
            z_a = z_a - depth_slope * x_a + depth_slope;
            if (y_b < y_c) {
                x_c = x_a <<= 16;
                if (y_a < 0) {
                    x_c -= c_to_a * y_a;
                    x_a -= a_to_b * y_a;
                    z_a -= depth_increment * y_a;
                    y_a = 0;
                }
                x_b <<= 16;
                if (y_b < 0) {
                    x_b -= b_to_c * y_b;
                    y_b = 0;
                }
                if (y_a != y_b && c_to_a < a_to_b || y_a == y_b && c_to_a > b_to_c) {
                    y_c -= y_b;
                    y_b -= y_a;
                    for (y_a = scanOffsets[y_a]; --y_b >= 0; y_a += Rasterizer2D.Rasterizer2D_width) {
                        drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_a, rgb, x_c >> 16, x_a >> 16, z_a, depth_slope);
                        x_c += c_to_a;
                        x_a += a_to_b;
                        z_a += depth_increment;
                    }

                    while (--y_c >= 0) {
                        drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_a, rgb, x_c >> 16, x_b >> 16, z_a, depth_slope);
                        x_c += c_to_a;
                        x_b += b_to_c;
                        y_a += Rasterizer2D.Rasterizer2D_width;
                        z_a += depth_increment;
                    }
                    return;
                }
                y_c -= y_b;
                y_b -= y_a;
                for (y_a = scanOffsets[y_a]; --y_b >= 0; y_a += Rasterizer2D.Rasterizer2D_width) {
                    drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_a, rgb, x_a >> 16, x_c >> 16, z_a, depth_slope);
                    x_c += c_to_a;
                    x_a += a_to_b;
                    z_a += depth_increment;
                }

                while (--y_c >= 0) {
                    drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_a, rgb, x_b >> 16, x_c >> 16, z_a, depth_slope);
                    x_c += c_to_a;
                    x_b += b_to_c;
                    y_a += Rasterizer2D.Rasterizer2D_width;
                    z_a += depth_increment;
                }
                return;
            }
            x_b = x_a <<= 16;
            if (y_a < 0) {
                x_b -= c_to_a * y_a;
                x_a -= a_to_b * y_a;
                z_a -= depth_increment * y_a;
                y_a = 0;

            }
            x_c <<= 16;
            if (y_c < 0) {
                x_c -= b_to_c * y_c;
                y_c = 0;
            }
            if (y_a != y_c && c_to_a < a_to_b || y_a == y_c && b_to_c > a_to_b) {
                y_b -= y_c;
                y_c -= y_a;
                for (y_a = scanOffsets[y_a]; --y_c >= 0; y_a += Rasterizer2D.Rasterizer2D_width) {
                    drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_a, rgb, x_b >> 16, x_a >> 16, z_a, depth_slope);
                    x_b += c_to_a;
                    x_a += a_to_b;
                    z_a += depth_increment;
                }

                while (--y_b >= 0) {
                    drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_a, rgb, x_c >> 16, x_a >> 16, z_a, depth_slope);
                    x_c += b_to_c;
                    x_a += a_to_b;
                    y_a += Rasterizer2D.Rasterizer2D_width;
                    z_a += depth_increment;
                }
                return;
            }
            y_b -= y_c;
            y_c -= y_a;
            for (y_a = scanOffsets[y_a]; --y_c >= 0; y_a += Rasterizer2D.Rasterizer2D_width) {
                drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_a, rgb, x_a >> 16, x_b >> 16, z_a, depth_slope);
                x_b += c_to_a;
                x_a += a_to_b;
                z_a += depth_increment;
            }

            while (--y_b >= 0) {
                drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_a, rgb, x_a >> 16, x_c >> 16, z_a, depth_slope);
                x_c += b_to_c;
                x_a += a_to_b;
                y_a += Rasterizer2D.Rasterizer2D_width;
                z_a += depth_increment;
            }
            return;
        }
        if (y_b <= y_c) {
            if (y_b >= Rasterizer2D.Rasterizer2D_yClipEnd)
                return;
            if (y_c > Rasterizer2D.Rasterizer2D_yClipEnd)
                y_c = Rasterizer2D.Rasterizer2D_yClipEnd;
            if (y_a > Rasterizer2D.Rasterizer2D_yClipEnd)
                y_a = Rasterizer2D.Rasterizer2D_yClipEnd;
            z_b = z_b - depth_slope * x_b + depth_slope;
            if (y_c < y_a) {
                x_a = x_b <<= 16;
                if (y_b < 0) {
                    x_a -= a_to_b * y_b;
                    x_b -= b_to_c * y_b;
                    z_b -= depth_increment * y_b;
                    y_b = 0;
                }
                x_c <<= 16;
                if (y_c < 0) {
                    x_c -= c_to_a * y_c;
                    y_c = 0;
                }
                if (y_b != y_c && a_to_b < b_to_c || y_b == y_c && a_to_b > c_to_a) {
                    y_a -= y_c;
                    y_c -= y_b;
                    for (y_b = scanOffsets[y_b]; --y_c >= 0; y_b += Rasterizer2D.Rasterizer2D_width) {
                        drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_b, rgb, x_a >> 16, x_b >> 16, z_b, depth_slope);
                        x_a += a_to_b;
                        x_b += b_to_c;
                        z_b += depth_increment;
                    }

                    while (--y_a >= 0) {
                        drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_b, rgb, x_a >> 16, x_c >> 16, z_b, depth_slope);
                        x_a += a_to_b;
                        x_c += c_to_a;
                        y_b += Rasterizer2D.Rasterizer2D_width;
                        z_b += depth_increment;
                    }
                    return;
                }
                y_a -= y_c;
                y_c -= y_b;
                for (y_b = scanOffsets[y_b]; --y_c >= 0; y_b += Rasterizer2D.Rasterizer2D_width) {
                    drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_b, rgb, x_b >> 16, x_a >> 16, z_b, depth_slope);
                    x_a += a_to_b;
                    x_b += b_to_c;
                    z_b += depth_increment;
                }

                while (--y_a >= 0) {
                    drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_b, rgb, x_c >> 16, x_a >> 16, z_b, depth_slope);
                    x_a += a_to_b;
                    x_c += c_to_a;
                    y_b += Rasterizer2D.Rasterizer2D_width;
                    z_b += depth_increment;
                }
                return;
            }
            x_c = x_b <<= 16;
            if (y_b < 0) {
                x_c -= a_to_b * y_b;
                x_b -= b_to_c * y_b;
                z_b -= depth_increment * y_b;
                y_b = 0;
            }
            x_a <<= 16;
            if (y_a < 0) {
                x_a -= c_to_a * y_a;
                y_a = 0;
            }
            if (a_to_b < b_to_c) {
                y_c -= y_a;
                y_a -= y_b;
                for (y_b = scanOffsets[y_b]; --y_a >= 0; y_b += Rasterizer2D.Rasterizer2D_width) {
                    drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_b, rgb, x_c >> 16, x_b >> 16, z_b, depth_slope);
                    x_c += a_to_b;
                    x_b += b_to_c;
                    z_b += depth_increment;
                }

                while (--y_c >= 0) {
                    drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_b, rgb, x_a >> 16, x_b >> 16, z_b, depth_slope);
                    x_a += c_to_a;
                    x_b += b_to_c;
                    y_b += Rasterizer2D.Rasterizer2D_width;
                    z_b += depth_increment;
                }
                return;
            }
            y_c -= y_a;
            y_a -= y_b;
            for (y_b = scanOffsets[y_b]; --y_a >= 0; y_b += Rasterizer2D.Rasterizer2D_width) {
                drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_b, rgb, x_b >> 16, x_c >> 16, z_b, depth_slope);
                x_c += a_to_b;
                x_b += b_to_c;
                z_b += depth_increment;
            }

            while (--y_c >= 0) {
                drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_b, rgb, x_b >> 16, x_a >> 16, z_b, depth_slope);
                x_a += c_to_a;
                x_b += b_to_c;
                y_b += Rasterizer2D.Rasterizer2D_width;
                z_b += depth_increment;
            }
            return;
        }
        if (y_c >= Rasterizer2D.Rasterizer2D_yClipEnd)
            return;
        if (y_a > Rasterizer2D.Rasterizer2D_yClipEnd)
            y_a = Rasterizer2D.Rasterizer2D_yClipEnd;
        if (y_b > Rasterizer2D.Rasterizer2D_yClipEnd)
            y_b = Rasterizer2D.Rasterizer2D_yClipEnd;
        z_c = z_c - depth_slope * x_c + depth_slope;
        if (y_a < y_b) {
            x_b = x_c <<= 16;
            if (y_c < 0) {
                x_b -= b_to_c * y_c;
                x_c -= c_to_a * y_c;
                z_c -= depth_increment * y_c;
                y_c = 0;
            }
            x_a <<= 16;
            if (y_a < 0) {
                x_a -= a_to_b * y_a;
                y_a = 0;
            }
            if (b_to_c < c_to_a) {
                y_b -= y_a;
                y_a -= y_c;
                for (y_c = scanOffsets[y_c]; --y_a >= 0; y_c += Rasterizer2D.Rasterizer2D_width) {
                    drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_c, rgb, x_b >> 16, x_c >> 16, z_c, depth_slope);
                    x_b += b_to_c;
                    x_c += c_to_a;
                    z_c += depth_increment;
                }

                while (--y_b >= 0) {
                    drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_c, rgb, x_b >> 16, x_a >> 16, z_c, depth_slope);
                    x_b += b_to_c;
                    x_a += a_to_b;
                    y_c += Rasterizer2D.Rasterizer2D_width;
                    z_c += depth_increment;
                }
                return;
            }
            y_b -= y_a;
            y_a -= y_c;
            for (y_c = scanOffsets[y_c]; --y_a >= 0; y_c += Rasterizer2D.Rasterizer2D_width) {
                drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_c, rgb, x_c >> 16, x_b >> 16, z_c, depth_slope);
                x_b += b_to_c;
                x_c += c_to_a;
                z_c += depth_increment;
            }

            while (--y_b >= 0) {
                drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_c, rgb, x_a >> 16, x_b >> 16, z_c, depth_slope);
                x_b += b_to_c;
                x_a += a_to_b;
                y_c += Rasterizer2D.Rasterizer2D_width;
                z_c += depth_increment;
            }
            return;
        }
        x_a = x_c <<= 16;
        if (y_c < 0) {
            x_a -= b_to_c * y_c;
            x_c -= c_to_a * y_c;
            z_c -= depth_increment * y_c;
            y_c = 0;
        }
        x_b <<= 16;
        if (y_b < 0) {
            x_b -= a_to_b * y_b;
            y_b = 0;
        }
        if (b_to_c < c_to_a) {
            y_a -= y_b;
            y_b -= y_c;
            for (y_c = scanOffsets[y_c]; --y_b >= 0; y_c += Rasterizer2D.Rasterizer2D_width) {
                drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_c, rgb, x_a >> 16, x_c >> 16, z_c, depth_slope);
                x_a += b_to_c;
                x_c += c_to_a;
                z_c += depth_increment;
            }

            while (--y_a >= 0) {
                drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_c, rgb, x_b >> 16, x_c >> 16, z_c, depth_slope);
                x_b += a_to_b;
                x_c += c_to_a;
                y_c += Rasterizer2D.Rasterizer2D_width;
                z_c += depth_increment;
            }
            return;
        }
        y_a -= y_b;
        y_b -= y_c;
        for (y_c = scanOffsets[y_c]; --y_b >= 0; y_c += Rasterizer2D.Rasterizer2D_width) {
            drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_c, rgb, x_c >> 16, x_a >> 16, z_c, depth_slope);
            x_a += b_to_c;
            x_c += c_to_a;
            z_c += depth_increment;
        }

        while (--y_a >= 0) {
            drawFlatScanline(Rasterizer2D.Rasterizer2D_pixels, y_c, rgb, x_c >> 16, x_b >> 16, z_c, depth_slope);
            x_b += a_to_b;
            x_c += c_to_a;
            y_c += Rasterizer2D.Rasterizer2D_width;
            z_c += depth_increment;
        }
    }

    private static void drawFlatScanline(int[] dest, int offset, int rgb, int x1, int x2, float depth, float depth_slope) {
        if (triangleIsOutOfBounds) {
            if (x2 > Rasterizer2D.lastX) {
                x2 = Rasterizer2D.lastX;
            }
            if (x1 < 0) {
                x1 = 0;
            }
        }
        if (x1 >= x2) {
            return;
        }
        offset += x1;
        int pos = x2 - x1 >> 2;
        depth += depth_slope * (float) x1;
        if (alpha == 0) {
            while (--pos >= 0) {
                for (int i = 0; i < 4; i++) {
                    dest[offset] = rgb;
                    depthBuffer[offset] = depth;
                    depth += depth_slope;
                    offset++;
                }
            }
            for (pos = x2 - x1 & 3; --pos >= 0; ) {
                dest[offset] = rgb;
                depthBuffer[offset] = depth;
                depth += depth_slope;
                offset++;
            }
            return;
        }
        int a1 = alpha;
        int a2 = 256 - alpha;
        rgb = ((rgb & 0xff00ff) * a2 >> 8 & 0xff00ff) + ((rgb & 0xff00) * a2 >> 8 & 0xff00);
        while (--pos >= 0) {
            for (int i = 0; i < 4; i++) {
                dest[offset] = rgb + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
                offset++;
            }
        }
        for (pos = x2 - x1 & 3; --pos >= 0; ) {
            dest[offset] = rgb + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
            offset++;
        }
    }

    /**
     * @param x_a   x coordinate of vertex a
     * @param x_b   x coordinate of vertex b
     * @param x_c   x coordinate of vertex c
     * @param y_a   y coordinate of vertex a
     * @param y_b   y coordinate of vertex b
     * @param y_c   y coordinate of vertex c
     * @param z_a   z coordinate of vertex a
     * @param z_b   z coordinate of vertex b
     * @param z_c   z coordinate of vertex c
     * @param hsl_a hsl color value at vertex a
     * @param hsl_b hsl color value at vertex b
     * @param hsl_c hsl color value at vertex c
     * @param t_x_a x coordinate of texture in vertex a
     * @param t_x_b x coordinate of texture in vertex b
     * @param t_x_c x coordinate of texture in vertex c
     * @param t_y_a y coordinate of texture in vertex a
     * @param t_y_b y coordinate of texture in vertex b
     * @param t_y_c y coordinate of texture in vertex c
     * @param t_z_a z coordinate of texture in vertex a
     * @param t_z_b z coordinate of texture in vertex b
     * @param t_z_c z coordinate of texture in vertex c
     * @param textureIndex the index of the texture
     */
    public static void drawTexturedTriangle(
            int x_a, int x_b, int x_c,
            int y_a, int y_b, int y_c,
            float z_a, float z_b, float z_c,
            int hsl_a, int hsl_b, int hsl_c,
            int t_x_a, int t_x_b, int t_x_c,
            int t_y_a, int t_y_b, int t_y_c,
            int t_z_a, int t_z_b, int t_z_c,
            int textureIndex
    ) {
        if (z_a < 0 || z_b < 0 || z_c < 0) return;
        hsl_a = 0x7f - hsl_a << 1;
        hsl_b = 0x7f - hsl_b << 1;
        hsl_c = 0x7f - hsl_c << 1;
        int[] texture = computeTexturePixelData(textureIndex);
        opaque = !textureHasTransparency[textureIndex];
        t_x_b = t_x_a - t_x_b;
        t_y_b = t_y_a - t_y_b;
        t_z_b = t_z_a - t_z_b;
        t_x_c -= t_x_a;
        t_y_c -= t_y_a;
        t_z_c -= t_z_a;
        int l4 = t_x_c * t_y_a - t_y_c * t_x_a << (SceneGraph.viewDistance == 9 ? 14 : 15);
        int i5 = t_y_c * t_z_a - t_z_c * t_y_a << 8;
        int j5 = t_z_c * t_x_a - t_x_c * t_z_a << 5;
        int k5 = t_x_b * t_y_a - t_y_b * t_x_a << (SceneGraph.viewDistance == 9 ? 14 : 15);
        int l5 = t_y_b * t_z_a - t_z_b * t_y_a << 8;
        int i6 = t_z_b * t_x_a - t_x_b * t_z_a << 5;
        int j6 = t_y_b * t_x_c - t_x_b * t_y_c << (SceneGraph.viewDistance == 9 ? 14 : 15);
        int k6 = t_z_b * t_y_c - t_y_b * t_z_c << 8;
        int l6 = t_x_b * t_z_c - t_z_b * t_x_c << 5;
        int i7 = 0;
        int j7 = 0;
        if (y_b != y_a) {
            i7 = (x_b - x_a << 16) / (y_b - y_a);
            j7 = (hsl_b - hsl_a << 16) / (y_b - y_a);
        }
        int k7 = 0;
        int l7 = 0;
        if (y_c != y_b) {
            k7 = (x_c - x_b << 16) / (y_c - y_b);
            l7 = (hsl_c - hsl_b << 16) / (y_c - y_b);
        }
        int i8 = 0;
        int j8 = 0;
        if (y_c != y_a) {
            i8 = (x_a - x_c << 16) / (y_a - y_c);
            j8 = (hsl_a - hsl_c << 16) / (y_a - y_c);
        }
        float b_aX = x_b - x_a;
        float b_aY = y_b - y_a;
        float c_aX = x_c - x_a;
        float c_aY = y_c - y_a;
        float b_aZ = z_b - z_a;
        float c_aZ = z_c - z_a;

        float div = b_aX * c_aY - c_aX * b_aY;
        float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
        float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;
        if (y_a <= y_b && y_a <= y_c) {
            if (y_a >= Rasterizer2D.Rasterizer2D_yClipEnd)
                return;
            if (y_b > Rasterizer2D.Rasterizer2D_yClipEnd)
                y_b = Rasterizer2D.Rasterizer2D_yClipEnd;
            if (y_c > Rasterizer2D.Rasterizer2D_yClipEnd)
                y_c = Rasterizer2D.Rasterizer2D_yClipEnd;
            z_a = z_a - depth_slope * x_a + depth_slope;
            if (y_b < y_c) {
                x_c = x_a <<= 16;
                hsl_c = hsl_a <<= 16;
                if (y_a < 0) {
                    x_c -= i8 * y_a;
                    x_a -= i7 * y_a;
                    z_a -= depth_increment * y_a;
                    hsl_c -= j8 * y_a;
                    hsl_a -= j7 * y_a;
                    y_a = 0;
                }
                x_b <<= 16;
                hsl_b <<= 16;
                if (y_b < 0) {
                    x_b -= k7 * y_b;
                    hsl_b -= l7 * y_b;
                    y_b = 0;
                }
                int k8 = y_a - originViewY;
                l4 += j5 * k8;
                k5 += i6 * k8;
                j6 += l6 * k8;
                if (y_a != y_b && i8 < i7 || y_a == y_b && i8 > k7) {
                    y_c -= y_b;
                    y_b -= y_a;
                    y_a = scanOffsets[y_a];
                    while (--y_b >= 0) {
                        drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_a, x_c >> 16, x_a >> 16, hsl_c, hsl_a, l4, k5, j6, i5, l5, k6, z_a, depth_slope);
                        x_c += i8;
                        x_a += i7;
                        hsl_c += j8;
                        hsl_a += j7;
                        z_a += depth_increment;
                        y_a += Rasterizer2D.Rasterizer2D_width;
                        l4 += j5;
                        k5 += i6;
                        j6 += l6;
                    }
                    while (--y_c >= 0) {
                        drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_a, x_c >> 16, x_b >> 16, hsl_c, hsl_b, l4, k5, j6, i5, l5, k6, z_a, depth_slope);
                        x_c += i8;
                        x_b += k7;
                        hsl_c += j8;
                        hsl_b += l7;
                        z_a += depth_increment;
                        y_a += Rasterizer2D.Rasterizer2D_width;
                        l4 += j5;
                        k5 += i6;
                        j6 += l6;
                    }
                    return;
                }
                y_c -= y_b;
                y_b -= y_a;
                y_a = scanOffsets[y_a];
                while (--y_b >= 0) {
                    drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_a, x_a >> 16, x_c >> 16, hsl_a, hsl_c, l4, k5, j6, i5, l5, k6, z_a, depth_slope);
                    x_c += i8;
                    x_a += i7;
                    hsl_c += j8;
                    hsl_a += j7;
                    z_a += depth_increment;
                    y_a += Rasterizer2D.Rasterizer2D_width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--y_c >= 0) {
                    drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_a, x_b >> 16, x_c >> 16, hsl_b, hsl_c, l4, k5, j6, i5, l5, k6, z_a, depth_slope);
                    x_c += i8;
                    x_b += k7;
                    hsl_c += j8;
                    hsl_b += l7;
                    z_a += depth_increment;
                    y_a += Rasterizer2D.Rasterizer2D_width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            x_b = x_a <<= 16;
            hsl_b = hsl_a <<= 16;
            if (y_a < 0) {
                x_b -= i8 * y_a;
                x_a -= i7 * y_a;
                z_a -= depth_increment * y_a;
                hsl_b -= j8 * y_a;
                hsl_a -= j7 * y_a;
                y_a = 0;
            }
            x_c <<= 16;
            hsl_c <<= 16;
            if (y_c < 0) {
                x_c -= k7 * y_c;
                hsl_c -= l7 * y_c;
                y_c = 0;
            }
            int l8 = y_a - originViewY;
            l4 += j5 * l8;
            k5 += i6 * l8;
            j6 += l6 * l8;
            if (y_a != y_c && i8 < i7 || y_a == y_c && k7 > i7) {
                y_b -= y_c;
                y_c -= y_a;
                y_a = scanOffsets[y_a];
                while (--y_c >= 0) {
                    drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_a, x_b >> 16, x_a >> 16, hsl_b, hsl_a, l4, k5, j6, i5, l5, k6, z_a, depth_slope);
                    x_b += i8;
                    x_a += i7;
                    hsl_b += j8;
                    hsl_a += j7;
                    z_a += depth_increment;
                    y_a += Rasterizer2D.Rasterizer2D_width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--y_b >= 0) {
                    drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_a, x_c >> 16, x_a >> 16, hsl_c, hsl_a, l4, k5, j6, i5, l5, k6, z_a, depth_slope);
                    x_c += k7;
                    x_a += i7;
                    hsl_c += l7;
                    hsl_a += j7;
                    z_a += depth_increment;
                    y_a += Rasterizer2D.Rasterizer2D_width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            y_b -= y_c;
            y_c -= y_a;
            y_a = scanOffsets[y_a];
            while (--y_c >= 0) {
                drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_a, x_a >> 16, x_b >> 16, hsl_a, hsl_b, l4, k5, j6, i5, l5, k6, z_a, depth_slope);
                x_b += i8;
                x_a += i7;
                hsl_b += j8;
                hsl_a += j7;
                z_a += depth_increment;
                y_a += Rasterizer2D.Rasterizer2D_width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            while (--y_b >= 0) {
                drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_a, x_a >> 16, x_c >> 16, hsl_a, hsl_c, l4, k5, j6, i5, l5, k6, z_a, depth_slope);
                x_c += k7;
                x_a += i7;
                hsl_c += l7;
                hsl_a += j7;
                z_a += depth_increment;
                y_a += Rasterizer2D.Rasterizer2D_width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            return;
        }
        if (y_b <= y_c) {
            if (y_b >= Rasterizer2D.Rasterizer2D_yClipEnd)
                return;
            if (y_c > Rasterizer2D.Rasterizer2D_yClipEnd)
                y_c = Rasterizer2D.Rasterizer2D_yClipEnd;
            if (y_a > Rasterizer2D.Rasterizer2D_yClipEnd)
                y_a = Rasterizer2D.Rasterizer2D_yClipEnd;
            z_b = z_b - depth_slope * x_b + depth_slope;
            if (y_c < y_a) {
                x_a = x_b <<= 16;
                hsl_a = hsl_b <<= 16;
                if (y_b < 0) {
                    x_a -= i7 * y_b;
                    x_b -= k7 * y_b;
                    z_b -= depth_increment * y_b;
                    hsl_a -= j7 * y_b;
                    hsl_b -= l7 * y_b;
                    y_b = 0;
                }
                x_c <<= 16;
                hsl_c <<= 16;
                if (y_c < 0) {
                    x_c -= i8 * y_c;
                    hsl_c -= j8 * y_c;
                    y_c = 0;
                }
                int i9 = y_b - originViewY;
                l4 += j5 * i9;
                k5 += i6 * i9;
                j6 += l6 * i9;
                if (y_b != y_c && i7 < k7 || y_b == y_c && i7 > i8) {
                    y_a -= y_c;
                    y_c -= y_b;
                    y_b = scanOffsets[y_b];
                    while (--y_c >= 0) {
                        drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_b, x_a >> 16, x_b >> 16, hsl_a, hsl_b, l4, k5, j6, i5, l5, k6, z_b, depth_slope);
                        x_a += i7;
                        x_b += k7;
                        hsl_a += j7;
                        hsl_b += l7;
                        z_b += depth_increment;
                        y_b += Rasterizer2D.Rasterizer2D_width;
                        l4 += j5;
                        k5 += i6;
                        j6 += l6;
                    }
                    while (--y_a >= 0) {
                        drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_b, x_a >> 16, x_c >> 16, hsl_a, hsl_c, l4, k5, j6, i5, l5, k6, z_b, depth_slope);
                        x_a += i7;
                        x_c += i8;
                        hsl_a += j7;
                        hsl_c += j8;
                        z_b += depth_increment;
                        y_b += Rasterizer2D.Rasterizer2D_width;
                        l4 += j5;
                        k5 += i6;
                        j6 += l6;
                    }
                    return;
                }
                y_a -= y_c;
                y_c -= y_b;
                y_b = scanOffsets[y_b];
                while (--y_c >= 0) {
                    drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_b, x_b >> 16, x_a >> 16, hsl_b, hsl_a, l4, k5, j6, i5, l5, k6, z_b, depth_slope);
                    x_a += i7;
                    x_b += k7;
                    hsl_a += j7;
                    hsl_b += l7;
                    z_b += depth_increment;
                    y_b += Rasterizer2D.Rasterizer2D_width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--y_a >= 0) {
                    drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_b, x_c >> 16, x_a >> 16, hsl_c, hsl_a, l4, k5, j6, i5, l5, k6, z_b, depth_slope);
                    x_a += i7;
                    x_c += i8;
                    hsl_a += j7;
                    hsl_c += j8;
                    z_b += depth_increment;
                    y_b += Rasterizer2D.Rasterizer2D_width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            x_c = x_b <<= 16;
            hsl_c = hsl_b <<= 16;
            if (y_b < 0) {
                x_c -= i7 * y_b;
                x_b -= k7 * y_b;
                z_b -= depth_increment * y_b;
                hsl_c -= j7 * y_b;
                hsl_b -= l7 * y_b;
                y_b = 0;
            }
            x_a <<= 16;
            hsl_a <<= 16;
            if (y_a < 0) {
                x_a -= i8 * y_a;
                hsl_a -= j8 * y_a;
                y_a = 0;
            }
            int j9 = y_b - originViewY;
            l4 += j5 * j9;
            k5 += i6 * j9;
            j6 += l6 * j9;
            if (i7 < k7) {
                y_c -= y_a;
                y_a -= y_b;
                y_b = scanOffsets[y_b];
                while (--y_a >= 0) {
                    drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_b, x_c >> 16, x_b >> 16, hsl_c, hsl_b, l4, k5, j6, i5, l5, k6, z_b, depth_slope);
                    x_c += i7;
                    x_b += k7;
                    hsl_c += j7;
                    hsl_b += l7;
                    z_b += depth_increment;
                    y_b += Rasterizer2D.Rasterizer2D_width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--y_c >= 0) {
                    drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_b, x_a >> 16, x_b >> 16, hsl_a, hsl_b, l4, k5, j6, i5, l5, k6, z_b, depth_slope);
                    x_a += i8;
                    x_b += k7;
                    hsl_a += j8;
                    hsl_b += l7;
                    z_b += depth_increment;
                    y_b += Rasterizer2D.Rasterizer2D_width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            y_c -= y_a;
            y_a -= y_b;
            y_b = scanOffsets[y_b];
            while (--y_a >= 0) {
                drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_b, x_b >> 16, x_c >> 16, hsl_b, hsl_c, l4, k5, j6, i5, l5, k6, z_b, depth_slope);
                x_c += i7;
                x_b += k7;
                hsl_c += j7;
                hsl_b += l7;
                z_b += depth_increment;
                y_b += Rasterizer2D.Rasterizer2D_width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            while (--y_c >= 0) {
                drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_b, x_b >> 16, x_a >> 16, hsl_b, hsl_a, l4, k5, j6, i5, l5, k6, z_b, depth_slope);
                x_a += i8;
                x_b += k7;
                hsl_a += j8;
                hsl_b += l7;
                z_b += depth_increment;
                y_b += Rasterizer2D.Rasterizer2D_width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            return;
        }
        if (y_c >= Rasterizer2D.Rasterizer2D_yClipEnd)
            return;
        if (y_a > Rasterizer2D.Rasterizer2D_yClipEnd)
            y_a = Rasterizer2D.Rasterizer2D_yClipEnd;
        if (y_b > Rasterizer2D.Rasterizer2D_yClipEnd)
            y_b = Rasterizer2D.Rasterizer2D_yClipEnd;
        z_c = z_c - depth_slope * x_c + depth_slope;
        if (y_a < y_b) {
            x_b = x_c <<= 16;
            hsl_b = hsl_c <<= 16;
            if (y_c < 0) {
                x_b -= k7 * y_c;
                x_c -= i8 * y_c;
                z_c -= depth_increment * y_c;
                hsl_b -= l7 * y_c;
                hsl_c -= j8 * y_c;
                y_c = 0;
            }
            x_a <<= 16;
            hsl_a <<= 16;
            if (y_a < 0) {
                x_a -= i7 * y_a;
                hsl_a -= j7 * y_a;
                y_a = 0;
            }
            int k9 = y_c - originViewY;
            l4 += j5 * k9;
            k5 += i6 * k9;
            j6 += l6 * k9;
            if (k7 < i8) {
                y_b -= y_a;
                y_a -= y_c;
                y_c = scanOffsets[y_c];
                while (--y_a >= 0) {
                    drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_c, x_b >> 16, x_c >> 16, hsl_b, hsl_c, l4, k5, j6, i5, l5, k6, z_c, depth_slope);
                    x_b += k7;
                    x_c += i8;
                    hsl_b += l7;
                    hsl_c += j8;
                    z_c += depth_increment;
                    y_c += Rasterizer2D.Rasterizer2D_width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--y_b >= 0) {
                    drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_c, x_b >> 16, x_a >> 16, hsl_b, hsl_a, l4, k5, j6, i5, l5, k6, z_c, depth_slope);
                    x_b += k7;
                    x_a += i7;
                    hsl_b += l7;
                    hsl_a += j7;
                    z_c += depth_increment;
                    y_c += Rasterizer2D.Rasterizer2D_width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            y_b -= y_a;
            y_a -= y_c;
            y_c = scanOffsets[y_c];
            while (--y_a >= 0) {
                drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_c, x_c >> 16, x_b >> 16, hsl_c, hsl_b, l4, k5, j6, i5, l5, k6, z_c, depth_slope);
                x_b += k7;
                x_c += i8;
                hsl_b += l7;
                hsl_c += j8;
                z_c += depth_increment;
                y_c += Rasterizer2D.Rasterizer2D_width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            while (--y_b >= 0) {
                drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_c, x_a >> 16, x_b >> 16, hsl_a, hsl_b, l4, k5, j6, i5, l5, k6, z_c, depth_slope);
                x_b += k7;
                x_a += i7;
                hsl_b += l7;
                hsl_a += j7;
                z_c += depth_increment;
                y_c += Rasterizer2D.Rasterizer2D_width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            return;
        }
        x_a = x_c <<= 16;
        hsl_a = hsl_c <<= 16;
        if (y_c < 0) {
            x_a -= k7 * y_c;
            x_c -= i8 * y_c;
            z_c -= depth_increment * y_c;
            hsl_a -= l7 * y_c;
            hsl_c -= j8 * y_c;
            y_c = 0;
        }
        x_b <<= 16;
        hsl_b <<= 16;
        if (y_b < 0) {
            x_b -= i7 * y_b;
            hsl_b -= j7 * y_b;
            y_b = 0;
        }
        int l9 = y_c - originViewY;
        l4 += j5 * l9;
        k5 += i6 * l9;
        j6 += l6 * l9;
        if (k7 < i8) {
            y_a -= y_b;
            y_b -= y_c;
            y_c = scanOffsets[y_c];
            while (--y_b >= 0) {
                drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_c, x_a >> 16, x_c >> 16, hsl_a, hsl_c, l4, k5, j6, i5, l5, k6, z_c, depth_slope);
                x_a += k7;
                x_c += i8;
                hsl_a += l7;
                hsl_c += j8;
                z_c += depth_increment;
                y_c += Rasterizer2D.Rasterizer2D_width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            while (--y_a >= 0) {
                drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_c, x_b >> 16, x_c >> 16, hsl_b, hsl_c, l4, k5, j6, i5, l5, k6, z_c, depth_slope);
                x_b += i7;
                x_c += i8;
                hsl_b += j7;
                hsl_c += j8;
                z_c += depth_increment;
                y_c += Rasterizer2D.Rasterizer2D_width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            return;
        }
        y_a -= y_b;
        y_b -= y_c;
        y_c = scanOffsets[y_c];
        while (--y_b >= 0) {
            drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_c, x_c >> 16, x_a >> 16, hsl_c, hsl_a, l4, k5, j6, i5, l5, k6, z_c, depth_slope);
            x_a += k7;
            x_c += i8;
            hsl_a += l7;
            hsl_c += j8;
            z_c += depth_increment;
            y_c += Rasterizer2D.Rasterizer2D_width;
            l4 += j5;
            k5 += i6;
            j6 += l6;
        }
        while (--y_a >= 0) {
            drawTexturedScanline(Rasterizer2D.Rasterizer2D_pixels, texture, y_c, x_c >> 16, x_b >> 16, hsl_c, hsl_b, l4, k5, j6, i5, l5, k6, z_c, depth_slope);
            x_b += i7;
            x_c += i8;
            hsl_b += j7;
            hsl_c += j8;
            z_c += depth_increment;
            y_c += Rasterizer2D.Rasterizer2D_width;
            l4 += j5;
            k5 += i6;
            j6 += l6;
        }
    }

    private static void drawTexturedScanline(int[] dest, int[] texture, int dest_off, int x1, int x2, int l1, int l2, int a1, int i2, int j2, int k2, int a2, int i3, float depth, float depth_slope) {
        int i = 0;
        int loops = 0;
        if (x1 >= x2)
            return;
        int dl = (l2 - l1) / (x2 - x1);
        int n;
        if (triangleIsOutOfBounds) {
            if (x2 > Rasterizer2D.lastX)
                x2 = Rasterizer2D.lastX;
            if (x1 < 0) {
                l1 -= x1 * dl;
                x1 = 0;
            }
        }
        if (x1 >= x2)
            return;
        n = x2 - x1 >> 3;
        dest_off += x1;
        depth += depth_slope * (float) x1;
        if (lowMem) {
            int i4 = 0;
            int k4 = 0;
            int k6 = x1 - originViewX;
            a1 += (k2 >> 3) * k6;
            i2 += (a2 >> 3) * k6;
            j2 += (i3 >> 3) * k6;
            int i5 = j2 >> 12;
            if (i5 != 0) {
                i = a1 / i5;
                loops = i2 / i5;
                if (i < 0)
                    i = 0;
                else if (i > 4032)
                    i = 4032;
            }
            a1 += k2;
            i2 += a2;
            j2 += i3;
            i5 = j2 >> 12;
            if (i5 != 0) {
                i4 = a1 / i5;
                k4 = i2 / i5;
                if (i4 < 7)
                    i4 = 7;
                else if (i4 > 4032)
                    i4 = 4032;
            }
            int i7 = i4 - i >> 3;
            int k7 = k4 - loops >> 3;
            if (opaque) {
                int rgb;
                int l;
                while (n-- > 0) {
                    for (int q = 0; q < 8; q++) {
                        rgb = texture[(loops & 0xfc0) + (i >> 6)];
                        l = l1 >> 16;
                        if (dest_off >= 0) {
                            depthBuffer[dest_off] = depth;
                            dest[dest_off] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                        }
                        depth += depth_slope;
                        dest_off++;
                        i += i7;
                        loops += k7;
                        l1 += dl;
                    }
                    a1 += k2;
                    i2 += a2;
                    j2 += i3;
                    int j5 = j2 >> 12;
                    if (j5 != 0) {
                        i4 = a1 / j5;
                        k4 = i2 / j5;
                        if (i4 < 7)
                            i4 = 7;
                        else if (i4 > 4032)
                            i4 = 4032;
                    }
                    i7 = i4 - i >> 3;
                    k7 = k4 - loops >> 3;
                    l1 += dl;
                }
                for (n = x2 - x1 & 7; n-- > 0; ) {
                    rgb = texture[(loops & 0xfc0) + (i >> 6)];
                    l = l1 >> 16;
                    if (dest_off >= 0) {
                        depthBuffer[dest_off] = depth;
                        dest[dest_off] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                    }
                    depth += depth_slope;
                    dest_off++;
                    i += i7;
                    loops += k7;
                    l1 += dl;
                }
                return;
            }
            while (n-- > 0) {
                int k8;
                int l;
                if ((k8 = texture[(loops & 0xfc0) + (i >> 6)]) != 0) {
                    l = l1 >> 16;
                    dest[dest_off] = ((k8 & 0xff00ff) * l & ~0xff00ff) + ((k8 & 0xff00) * l & 0xff0000) >> 8;
                    depthBuffer[dest_off] = depth;
                }
                depth += depth_slope;
                dest_off++;
                i += i7;
                loops += k7;
                l1 += dl;
                if ((k8 = texture[(loops & 0xfc0) + (i >> 6)]) != 0) {
                    l = l1 >> 16;
                    dest[dest_off] = ((k8 & 0xff00ff) * l & ~0xff00ff) + ((k8 & 0xff00) * l & 0xff0000) >> 8;
                    depthBuffer[dest_off] = depth;
                }
                depth += depth_slope;
                dest_off++;
                i += i7;
                loops += k7;
                l1 += dl;
                if ((k8 = texture[(loops & 0xfc0) + (i >> 6)]) != 0) {
                    l = l1 >> 16;
                    dest[dest_off] = ((k8 & 0xff00ff) * l & ~0xff00ff) + ((k8 & 0xff00) * l & 0xff0000) >> 8;
                    depthBuffer[dest_off] = depth;
                }
                depth += depth_slope;
                dest_off++;
                i += i7;
                loops += k7;
                l1 += dl;
                if ((k8 = texture[(loops & 0xfc0) + (i >> 6)]) != 0) {
                    l = l1 >> 16;
                    dest[dest_off] = ((k8 & 0xff00ff) * l & ~0xff00ff) + ((k8 & 0xff00) * l & 0xff0000) >> 8;
                    depthBuffer[dest_off] = depth;
                }
                depth += depth_slope;
                dest_off++;
                i += i7;
                loops += k7;
                l1 += dl;
                if ((k8 = texture[(loops & 0xfc0) + (i >> 6)]) != 0) {
                    l = l1 >> 16;
                    dest[dest_off] = ((k8 & 0xff00ff) * l & ~0xff00ff) + ((k8 & 0xff00) * l & 0xff0000) >> 8;
                    depthBuffer[dest_off] = depth;
                }
                depth += depth_slope;
                dest_off++;
                i += i7;
                loops += k7;
                l1 += dl;
                if ((k8 = texture[(loops & 0xfc0) + (i >> 6)]) != 0) {
                    l = l1 >> 16;
                    dest[dest_off] = ((k8 & 0xff00ff) * l & ~0xff00ff) + ((k8 & 0xff00) * l & 0xff0000) >> 8;
                    depthBuffer[dest_off] = depth;
                }
                depth += depth_slope;
                dest_off++;
                i += i7;
                loops += k7;
                l1 += dl;
                if ((k8 = texture[(loops & 0xfc0) + (i >> 6)]) != 0) {
                    l = l1 >> 16;
                    dest[dest_off] = ((k8 & 0xff00ff) * l & ~0xff00ff) + ((k8 & 0xff00) * l & 0xff0000) >> 8;
                    depthBuffer[dest_off] = depth;
                }
                depth += depth_slope;
                dest_off++;
                i += i7;
                loops += k7;
                l1 += dl;
                if ((k8 = texture[(loops & 0xfc0) + (i >> 6)]) != 0) {
                    l = l1 >> 16;
                    dest[dest_off] = ((k8 & 0xff00ff) * l & ~0xff00ff) + ((k8 & 0xff00) * l & 0xff0000) >> 8;
                    depthBuffer[dest_off] = depth;
                }
                depth += depth_slope;
                dest_off++;
                i += i7;
                loops += k7;
                l1 += dl;
                a1 += k2;
                i2 += a2;
                j2 += i3;
                int k5 = j2 >> 12;
                if (k5 != 0) {
                    i4 = a1 / k5;
                    k4 = i2 / k5;
                    if (i4 < 7)
                        i4 = 7;
                    else if (i4 > 4032)
                        i4 = 4032;
                }
                i7 = i4 - i >> 3;
                k7 = k4 - loops >> 3;
                l1 += dl;
            }
            for (n = x2 - x1 & 7; n-- > 0; ) {
                int l8;
                int l;
                if ((l8 = texture[(loops & 0xfc0) + (i >> 6)]) != 0) {
                    l = l1 >> 16;
                    dest[dest_off] = ((l8 & 0xff00ff) * l & ~0xff00ff) + ((l8 & 0xff00) * l & 0xff0000) >> 8;
                    depthBuffer[dest_off] = depth;
                }
                depth += depth_slope;
                dest_off++;
                i += i7;
                loops += k7;
                l1 += dl;
            }

            return;
        }
        int j4 = 0;
        int l4 = 0;
        int l6 = x1 - originViewX;
        a1 += (k2 >> 3) * l6;
        i2 += (a2 >> 3) * l6;
        j2 += (i3 >> 3) * l6;
        int l5 = j2 >> 14;
        if (l5 != 0) {
            i = a1 / l5;
            loops = i2 / l5;
            if (i < 0)
                i = 0;
            else if (i > 16256)
                i = 16256;
        }
        a1 += k2;
        i2 += a2;
        j2 += i3;
        l5 = j2 >> 14;
        if (l5 != 0) {
            j4 = a1 / l5;
            l4 = i2 / l5;
            if (j4 < 7)
                j4 = 7;
            else if (j4 > 16256)
                j4 = 16256;
        }
        int j7 = j4 - i >> 3;
        int l7 = l4 - loops >> 3;
        if (opaque) {
            while (n-- > 0) {
                int rgb;
                int l;
                rgb = texture[(loops & 0x3f80) + (i >> 7)];
                l = l1 >> 16;
                dest[dest_off] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                depthBuffer[dest_off] = depth;
                depth += depth_slope;
                dest_off++;
                i += j7;
                loops += l7;
                l1 += dl;
                rgb = texture[(loops & 0x3f80) + (i >> 7)];
                l = l1 >> 16;
                dest[dest_off] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                depthBuffer[dest_off] = depth;
                depth += depth_slope;
                dest_off++;
                i += j7;
                loops += l7;
                l1 += dl;
                rgb = texture[(loops & 0x3f80) + (i >> 7)];
                l = l1 >> 16;
                dest[dest_off] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                depthBuffer[dest_off] = depth;
                depth += depth_slope;
                dest_off++;
                i += j7;
                loops += l7;
                l1 += dl;
                rgb = texture[(loops & 0x3f80) + (i >> 7)];
                l = l1 >> 16;
                dest[dest_off] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                depthBuffer[dest_off] = depth;
                depth += depth_slope;
                dest_off++;
                i += j7;
                loops += l7;
                l1 += dl;
                rgb = texture[(loops & 0x3f80) + (i >> 7)];
                l = l1 >> 16;
                dest[dest_off] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                depthBuffer[dest_off] = depth;
                depth += depth_slope;
                dest_off++;
                i += j7;
                loops += l7;
                l1 += dl;
                rgb = texture[(loops & 0x3f80) + (i >> 7)];
                l = l1 >> 16;
                dest[dest_off] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                depthBuffer[dest_off] = depth;
                depth += depth_slope;
                dest_off++;
                i += j7;
                loops += l7;
                l1 += dl;
                rgb = texture[(loops & 0x3f80) + (i >> 7)];
                l = l1 >> 16;
                dest[dest_off] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                depthBuffer[dest_off] = depth;
                depth += depth_slope;
                dest_off++;
                i += j7;
                loops += l7;
                l1 += dl;
                rgb = texture[(loops & 0b11111110000000) + (i >> 7)];
                l = l1 >> 16;
                dest[dest_off] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                depthBuffer[dest_off] = depth;
                depth += depth_slope;
                dest_off++;
                i += j7;
                loops += l7;
                l1 += dl;
                a1 += k2;
                i2 += a2;
                j2 += i3;
                int i6 = j2 >> 14;
                if (i6 != 0) {
                    j4 = a1 / i6;
                    l4 = i2 / i6;
                    if (j4 < 7)
                        j4 = 7;
                    else if (j4 > 16256)
                        j4 = 16256;
                }
                j7 = j4 - i >> 3;
                l7 = l4 - loops >> 3;
                l1 += dl;
            }
            for (n = x2 - x1 & 7; n-- > 0; ) {
                int rgb;
                int l;
                rgb = texture[(loops & 0x3f80) + (i >> 7)];
                l = l1 >> 16;
                dest[dest_off] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                depthBuffer[dest_off] = depth;
                depth += depth_slope;
                dest_off++;
                i += j7;
                loops += l7;
                l1 += dl;
            }

            return;
        }
        while (n-- > 0) {
            int i9;
            int l;
            if ((i9 = texture[(loops & 0b11111110000000) + (i >> 7)]) != 0) {
                l = l1 >> 16;
                dest[dest_off] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 8;
                depthBuffer[dest_off] = depth;
            }
            depth += depth_slope;
            dest_off++;
            i += j7;
            loops += l7;
            l1 += dl;
            if ((i9 = texture[(loops & 0b11111110000000) + (i >> 7)]) != 0) {
                l = l1 >> 16;
                dest[dest_off] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 8;
                depthBuffer[dest_off] = depth;
            }
            depth += depth_slope;
            dest_off++;
            i += j7;
            loops += l7;
            l1 += dl;
            if ((i9 = texture[(loops & 0b11111110000000) + (i >> 7)]) != 0) {
                l = l1 >> 16;
                dest[dest_off] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 8;
                depthBuffer[dest_off] = depth;
            }
            depth += depth_slope;
            dest_off++;
            i += j7;
            loops += l7;
            l1 += dl;
            if ((i9 = texture[(loops & 0b11111110000000) + (i >> 7)]) != 0) {
                l = l1 >> 16;
                dest[dest_off] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 8;
                depthBuffer[dest_off] = depth;
            }
            depth += depth_slope;
            dest_off++;
            i += j7;
            loops += l7;
            l1 += dl;
            if ((i9 = texture[(loops & 0b11111110000000) + (i >> 7)]) != 0) {
                l = l1 >> 16;
                dest[dest_off] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 8;
                depthBuffer[dest_off] = depth;
            }
            depth += depth_slope;
            dest_off++;
            i += j7;
            loops += l7;
            l1 += dl;
            if ((i9 = texture[(loops & 0b11111110000000) + (i >> 7)]) != 0) {
                l = l1 >> 16;
                dest[dest_off] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 8;
                depthBuffer[dest_off] = depth;
            }
            depth += depth_slope;
            dest_off++;
            i += j7;
            loops += l7;
            l1 += dl;
            if ((i9 = texture[(loops & 0x3f80) + (i >> 7)]) != 0) {
                l = l1 >> 16;
                dest[dest_off] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 8;
                depthBuffer[dest_off] = depth;
            }
            depth += depth_slope;
            dest_off++;
            i += j7;
            loops += l7;
            l1 += dl;
            if ((i9 = texture[(loops & 0x3f80) + (i >> 7)]) != 0) {
                l = l1 >> 16;
                dest[dest_off] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 8;
                depthBuffer[dest_off] = depth;
            }
            depth += depth_slope;
            dest_off++;
            i += j7;
            loops += l7;
            l1 += dl;
            a1 += k2;
            i2 += a2;
            j2 += i3;
            int j6 = j2 >> 14;
            if (j6 != 0) {
                j4 = a1 / j6;
                l4 = i2 / j6;
                if (j4 < 7)
                    j4 = 7;
                else if (j4 > 16256)
                    j4 = 16256;
            }
            j7 = j4 - i >> 3;
            l7 = l4 - loops >> 3;
            l1 += dl;
        }
        for (int l3 = x2 - x1 & 7; l3-- > 0; ) {
            int j9;
            int l;
            if ((j9 = texture[(loops & 0x3f80) + (i >> 7)]) != 0) {
                l = l1 >> 16;
                dest[dest_off] = ((j9 & 0xff00ff) * l & ~0xff00ff) + ((j9 & 0xff00) * l & 0xff0000) >> 8;
                depthBuffer[dest_off] = depth;
            }
            depth += depth_slope;
            dest_off++;
            i += j7;
            loops += l7;
            l1 += dl;
        }
    }

    public static void method4352(float z_a, float z_b, float z_c, int y_a, int y_b, int y_c, int x_a, int x_b, int x_c, int hsl_a, int hsl_b, int hsl_c, int t_x_a, int t_x_b, int t_x_c, int t_y_a, int t_y_b, int t_y_c, int t_z_a, int t_z_b, int t_z_c, int textureIndex) {
        if (z_a < 0 || z_b < 0 || z_c < 0) return;
        //int[] var19 = computeTexturePixelData(var18);
        int[] var19 = Rasterizer3D_textureLoader.getTexturePixels(textureIndex);
        int var20;
        if (var19 == null) {
            var20 = Rasterizer3D_textureLoader.getAverageTextureRGB(textureIndex);
            method4270(z_a, z_b, z_c, y_a, y_b, y_c, x_a, x_b, x_c, method4281(var20, hsl_a), method4281(var20, hsl_b), method4281(var20, hsl_c));
        } else {
            // Rasterizer3D_isLowDetailTexture = isLowDetail(var18); // TODO:
            opaque = Rasterizer3D_textureLoader.vmethod4757(textureIndex);
            var20 = x_b - x_a;
            int var21 = y_b - y_a;
            int var22 = x_c - x_a;
            int var23 = y_c - y_a;
            int var24 = hsl_b - hsl_a;
            int var25 = hsl_c - hsl_a;
            int var26 = 0;
            if (y_a != y_b) {
                var26 = (x_b - x_a << 14) / (y_b - y_a);
            }

            int var27 = 0;
            if (y_c != y_b) {
                var27 = (x_c - x_b << 14) / (y_c - y_b);
            }

            int var28 = 0;
            if (y_a != y_c) {
                var28 = (x_a - x_c << 14) / (y_a - y_c);
            }

            int var29 = var20 * var23 - var22 * var21;
            if (var29 != 0) {
                int var30 = (var24 * var23 - var25 * var21 << 9) / var29;
                int var31 = (var25 * var20 - var24 * var22 << 9) / var29;
                t_x_b = t_x_a - t_x_b;
                t_y_b = t_y_a - t_y_b;
                t_z_b = t_z_a - t_z_b;
                t_x_c -= t_x_a;
                t_y_c -= t_y_a;
                t_z_c -= t_z_a;

                int var32 = t_x_c * t_y_a - t_x_a * t_y_c << 14;
                int var33 = (int)(((long)(t_z_a * t_y_c - t_z_c * t_y_a) << 3 << 14) / (long)512);
                int var34 = (int)(((long)(t_z_c * t_x_a - t_x_c * t_z_a) << 14) / (long)512);
                int var35 = t_x_b * t_y_a - t_y_b * t_x_a << 14;
                int var36 = (int)(((long)(t_y_b * t_z_a - t_z_b * t_y_a) << 3 << 14) / (long)512);
                int var37 = (int)(((long)(t_z_b * t_x_a - t_x_b * t_z_a) << 14) / (long)512);
                int var38 = t_y_b * t_x_c - t_x_b * t_y_c << 14;
                int var39 = (int)(((long)(t_z_b * t_y_c - t_y_b * t_z_c) << 3 << 14) / (long)512);
                int var40 = (int)(((long)(t_z_c * t_x_b - t_x_c * t_z_b) << 14) / (long)512);
                int var41;
                float b_aX = x_b - x_a;
                float b_aY = y_b - y_a;
                float c_aX = x_c - x_a;
                float c_aY = y_c - y_a;
                float b_aZ = z_b - z_a;
                float c_aZ = z_c - z_a;

                float div = b_aX * c_aY - c_aX * b_aY;
                float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
                float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;
                if (y_a <= y_b && y_a <= y_c) {
                    if (y_a < Rasterizer2D_yClipEnd) {
                        if (y_b > Rasterizer2D_yClipEnd) {
                            y_b = Rasterizer2D_yClipEnd;
                        }

                        if (y_c > Rasterizer2D_yClipEnd) {
                            y_c = Rasterizer2D_yClipEnd;
                        }

                        hsl_a = var30 + ((hsl_a << 9) - x_a * var30);
                        z_a = z_a - depth_slope * x_a + depth_slope;
                        if (y_b < y_c) {
                            x_c = x_a <<= 14;
                            if (y_a < 0) {
                                x_c -= y_a * var28;
                                x_a -= y_a * var26;
                                z_a -= depth_increment * y_a;
                                hsl_a -= y_a * var31;
                                y_a = 0;
                            }

                            x_b <<= 14;
                            if (y_b < 0) {
                                x_b -= var27 * y_b;
                                y_b = 0;
                            }

                            var41 = y_a - originViewY;
                            var32 += var34 * var41;
                            var35 += var37 * var41;
                            var38 += var40 * var41;
                            if ((y_a == y_b || var28 >= var26) && (y_a != y_b || var28 <= var27)) {
                                y_c -= y_b;
                                y_b -= y_a;
                                y_a = scanOffsets[y_a];

                                while(true) {
                                    --y_b;
                                    if (y_b < 0) {
                                        while(true) {
                                            --y_c;
                                            if (y_c < 0) {
                                                return;
                                            }

                                            Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_a, x_b >> 14, x_c >> 14, hsl_a, var30, var32, var35, var38, var33, var36, var39, z_a, depth_slope);
                                            x_c += var28;
                                            x_b += var27;
                                            hsl_a += var31;
                                            z_a += depth_increment;
                                            y_a += Rasterizer2D.Rasterizer2D_width;
                                            var32 += var34;
                                            var35 += var37;
                                            var38 += var40;
                                        }
                                    }

                                    Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_a, x_a >> 14, x_c >> 14, hsl_a, var30, var32, var35, var38, var33, var36, var39, z_a, depth_slope);
                                    x_c += var28;
                                    x_a += var26;
                                    hsl_a += var31;
                                    z_a += depth_increment;
                                    y_a += Rasterizer2D.Rasterizer2D_width;
                                    var32 += var34;
                                    var35 += var37;
                                    var38 += var40;
                                }
                            } else {
                                y_c -= y_b;
                                y_b -= y_a;
                                y_a = scanOffsets[y_a];

                                while(true) {
                                    --y_b;
                                    if (y_b < 0) {
                                        while(true) {
                                            --y_c;
                                            if (y_c < 0) {
                                                return;
                                            }

                                            Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_a, x_c >> 14, x_b >> 14, hsl_a, var30, var32, var35, var38, var33, var36, var39, z_a, depth_slope);
                                            x_c += var28;
                                            x_b += var27;
                                            hsl_a += var31;
                                            z_a += depth_increment;
                                            y_a += Rasterizer2D.Rasterizer2D_width;
                                            var32 += var34;
                                            var35 += var37;
                                            var38 += var40;
                                        }
                                    }

                                    Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_a, x_c >> 14, x_a >> 14, hsl_a, var30, var32, var35, var38, var33, var36, var39, z_a, depth_slope);
                                    x_c += var28;
                                    x_a += var26;
                                    hsl_a += var31;
                                    z_a += depth_increment;
                                    y_a += Rasterizer2D.Rasterizer2D_width;
                                    var32 += var34;
                                    var35 += var37;
                                    var38 += var40;
                                }
                            }
                        } else {
                            x_b = x_a <<= 14;
                            if (y_a < 0) {
                                x_b -= y_a * var28;
                                x_a -= y_a * var26;
                                z_a -= depth_increment * y_a;
                                hsl_a -= y_a * var31;
                                y_a = 0;
                            }

                            x_c <<= 14;
                            if (y_c < 0) {
                                x_c -= var27 * y_c;
                                y_c = 0;
                            }

                            var41 = y_a - originViewY;
                            var32 += var34 * var41;
                            var35 += var37 * var41;
                            var38 += var40 * var41;
                            if ((y_a == y_c || var28 >= var26) && (y_a != y_c || var27 <= var26)) {
                                y_b -= y_c;
                                y_c -= y_a;
                                y_a = scanOffsets[y_a];

                                while(true) {
                                    --y_c;
                                    if (y_c < 0) {
                                        while(true) {
                                            --y_b;
                                            if (y_b < 0) {
                                                return;
                                            }

                                            Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_a, x_a >> 14, x_c >> 14, hsl_a, var30, var32, var35, var38, var33, var36, var39, z_a, depth_slope);
                                            x_c += var27;
                                            x_a += var26;
                                            hsl_a += var31;
                                            z_a += depth_increment;
                                            y_a += Rasterizer2D.Rasterizer2D_width;
                                            var32 += var34;
                                            var35 += var37;
                                            var38 += var40;
                                        }
                                    }

                                    Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_a, x_a >> 14, x_b >> 14, hsl_a, var30, var32, var35, var38, var33, var36, var39, z_a, depth_slope);
                                    x_b += var28;
                                    x_a += var26;
                                    hsl_a += var31;
                                    z_a += depth_increment;
                                    y_a += Rasterizer2D.Rasterizer2D_width;
                                    var32 += var34;
                                    var35 += var37;
                                    var38 += var40;
                                }
                            } else {
                                y_b -= y_c;
                                y_c -= y_a;
                                y_a = scanOffsets[y_a];

                                while(true) {
                                    --y_c;
                                    if (y_c < 0) {
                                        while(true) {
                                            --y_b;
                                            if (y_b < 0) {
                                                return;
                                            }

                                            Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_a, x_c >> 14, x_a >> 14, hsl_a, var30, var32, var35, var38, var33, var36, var39, z_a, depth_slope);
                                            x_c += var27;
                                            x_a += var26;
                                            hsl_a += var31;
                                            z_a += depth_increment;
                                            y_a += Rasterizer2D.Rasterizer2D_width;
                                            var32 += var34;
                                            var35 += var37;
                                            var38 += var40;
                                        }
                                    }

                                    Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_a, x_b >> 14, x_a >> 14, hsl_a, var30, var32, var35, var38, var33, var36, var39, z_a, depth_slope);
                                    x_b += var28;
                                    x_a += var26;
                                    hsl_a += var31;
                                    z_a += depth_increment;
                                    y_a += Rasterizer2D.Rasterizer2D_width;
                                    var32 += var34;
                                    var35 += var37;
                                    var38 += var40;
                                }
                            }
                        }
                    }
                } else if (y_b <= y_c) {
                    if (y_b < Rasterizer2D_yClipEnd) {
                        if (y_c > Rasterizer2D_yClipEnd) {
                            y_c = Rasterizer2D_yClipEnd;
                        }

                        if (y_a > Rasterizer2D_yClipEnd) {
                            y_a = Rasterizer2D_yClipEnd;
                        }

                        hsl_b = var30 + ((hsl_b << 9) - var30 * x_b);
                        z_b = z_b - depth_slope * x_b + depth_slope;
                        if (y_c < y_a) {
                            x_a = x_b <<= 14;
                            if (y_b < 0) {
                                x_a -= var26 * y_b;
                                x_b -= var27 * y_b;
                                hsl_b -= var31 * y_b;
                                z_b -= depth_increment * y_b;
                                y_b = 0;
                            }

                            x_c <<= 14;
                            if (y_c < 0) {
                                x_c -= var28 * y_c;
                                y_c = 0;
                            }

                            var41 = y_b - originViewY;
                            var32 += var34 * var41;
                            var35 += var37 * var41;
                            var38 += var40 * var41;
                            if (y_c != y_b && var26 < var27 || y_c == y_b && var26 > var28) {
                                y_a -= y_c;
                                y_c -= y_b;
                                y_b = scanOffsets[y_b];

                                while(true) {
                                    --y_c;
                                    if (y_c < 0) {
                                        while(true) {
                                            --y_a;
                                            if (y_a < 0) {
                                                return;
                                            }

                                            Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_b, x_a >> 14, x_c >> 14, hsl_b, var30, var32, var35, var38, var33, var36, var39, z_b, depth_slope);
                                            x_a += var26;
                                            x_c += var28;
                                            hsl_b += var31;
                                            z_b += depth_increment;
                                            y_b += Rasterizer2D.Rasterizer2D_width;
                                            var32 += var34;
                                            var35 += var37;
                                            var38 += var40;
                                        }
                                    }

                                    Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_b, x_a >> 14, x_b >> 14, hsl_b, var30, var32, var35, var38, var33, var36, var39, z_b, depth_slope);
                                    x_a += var26;
                                    x_b += var27;
                                    hsl_b += var31;
                                    z_b += depth_increment;
                                    y_b += Rasterizer2D.Rasterizer2D_width;
                                    var32 += var34;
                                    var35 += var37;
                                    var38 += var40;
                                }
                            } else {
                                y_a -= y_c;
                                y_c -= y_b;
                                y_b = scanOffsets[y_b];

                                while(true) {
                                    --y_c;
                                    if (y_c < 0) {
                                        while(true) {
                                            --y_a;
                                            if (y_a < 0) {
                                                return;
                                            }

                                            Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_b, x_c >> 14, x_a >> 14, hsl_b, var30, var32, var35, var38, var33, var36, var39, z_b, depth_slope);
                                            x_a += var26;
                                            x_c += var28;
                                            hsl_b += var31;
                                            z_b += depth_increment;
                                            y_b += Rasterizer2D.Rasterizer2D_width;
                                            var32 += var34;
                                            var35 += var37;
                                            var38 += var40;
                                        }
                                    }

                                    Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_b, x_b >> 14, x_a >> 14, hsl_b, var30, var32, var35, var38, var33, var36, var39, z_b, depth_slope);
                                    x_a += var26;
                                    x_b += var27;
                                    hsl_b += var31;
                                    z_b += depth_increment;
                                    y_b += Rasterizer2D.Rasterizer2D_width;
                                    var32 += var34;
                                    var35 += var37;
                                    var38 += var40;
                                }
                            }
                        } else {
                            x_c = x_b <<= 14;
                            if (y_b < 0) {
                                x_c -= var26 * y_b;
                                x_b -= var27 * y_b;
                                hsl_b -= var31 * y_b;
                                z_b -= depth_increment * y_b;
                                y_b = 0;
                            }

                            x_a <<= 14;
                            if (y_a < 0) {
                                x_a -= y_a * var28;
                                y_a = 0;
                            }

                            var41 = y_b - originViewY;
                            var32 += var34 * var41;
                            var35 += var37 * var41;
                            var38 += var40 * var41;
                            if (var26 < var27) {
                                y_c -= y_a;
                                y_a -= y_b;
                                y_b = scanOffsets[y_b];

                                while(true) {
                                    --y_a;
                                    if (y_a < 0) {
                                        while(true) {
                                            --y_c;
                                            if (y_c < 0) {
                                                return;
                                            }

                                            Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_b, x_a >> 14, x_b >> 14, hsl_b, var30, var32, var35, var38, var33, var36, var39, z_b, depth_slope);
                                            x_a += var28;
                                            x_b += var27;
                                            hsl_b += var31;
                                            z_b += depth_increment;
                                            y_b += Rasterizer2D.Rasterizer2D_width;
                                            var32 += var34;
                                            var35 += var37;
                                            var38 += var40;
                                        }
                                    }

                                    Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_b, x_c >> 14, x_b >> 14, hsl_b, var30, var32, var35, var38, var33, var36, var39, z_b, depth_slope);
                                    x_c += var26;
                                    x_b += var27;
                                    hsl_b += var31;
                                    z_b += depth_increment;
                                    y_b += Rasterizer2D.Rasterizer2D_width;
                                    var32 += var34;
                                    var35 += var37;
                                    var38 += var40;
                                }
                            } else {
                                y_c -= y_a;
                                y_a -= y_b;
                                y_b = scanOffsets[y_b];

                                while(true) {
                                    --y_a;
                                    if (y_a < 0) {
                                        while(true) {
                                            --y_c;
                                            if (y_c < 0) {
                                                return;
                                            }

                                            Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_b, x_b >> 14, x_a >> 14, hsl_b, var30, var32, var35, var38, var33, var36, var39, z_b, depth_slope);
                                            x_a += var28;
                                            x_b += var27;
                                            hsl_b += var31;
                                            z_b += depth_increment;
                                            y_b += Rasterizer2D.Rasterizer2D_width;
                                            var32 += var34;
                                            var35 += var37;
                                            var38 += var40;
                                        }
                                    }

                                    Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_b, x_b >> 14, x_c >> 14, hsl_b, var30, var32, var35, var38, var33, var36, var39, z_b, depth_slope);
                                    x_c += var26;
                                    x_b += var27;
                                    hsl_b += var31;
                                    z_b += depth_increment;
                                    y_b += Rasterizer2D.Rasterizer2D_width;
                                    var32 += var34;
                                    var35 += var37;
                                    var38 += var40;
                                }
                            }
                        }
                    }
                } else if (y_c < Rasterizer2D_yClipEnd) {
                    if (y_a > Rasterizer2D_yClipEnd) {
                        y_a = Rasterizer2D_yClipEnd;
                    }

                    if (y_b > Rasterizer2D_yClipEnd) {
                        y_b = Rasterizer2D_yClipEnd;
                    }

                    hsl_c = (hsl_c << 9) - x_c * var30 + var30;
                    z_c = z_c - depth_slope * x_c + depth_slope;
                    if (y_a < y_b) {
                        x_b = x_c <<= 14;
                        if (y_c < 0) {
                            x_b -= var27 * y_c;
                            x_c -= var28 * y_c;
                            hsl_c -= var31 * y_c;
                            z_c -= depth_increment * y_c;
                            y_c = 0;
                        }

                        x_a <<= 14;
                        if (y_a < 0) {
                            x_a -= y_a * var26;
                            y_a = 0;
                        }

                        var41 = y_c - originViewY;
                        var32 += var34 * var41;
                        var35 += var37 * var41;
                        var38 += var40 * var41;
                        if (var27 < var28) {
                            y_b -= y_a;
                            y_a -= y_c;
                            y_c = scanOffsets[y_c];

                            while(true) {
                                --y_a;
                                if (y_a < 0) {
                                    while(true) {
                                        --y_b;
                                        if (y_b < 0) {
                                            return;
                                        }

                                        Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_c, x_b >> 14, x_a >> 14, hsl_c, var30, var32, var35, var38, var33, var36, var39, z_c, depth_slope);
                                        x_b += var27;
                                        x_a += var26;
                                        hsl_c += var31;
                                        z_c += depth_increment;
                                        y_c += Rasterizer2D.Rasterizer2D_width;
                                        var32 += var34;
                                        var35 += var37;
                                        var38 += var40;
                                    }
                                }

                                Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_c, x_b >> 14, x_c >> 14, hsl_c, var30, var32, var35, var38, var33, var36, var39, z_c, depth_slope);
                                x_b += var27;
                                x_c += var28;
                                hsl_c += var31;
                                z_c += depth_increment;
                                y_c += Rasterizer2D.Rasterizer2D_width;
                                var32 += var34;
                                var35 += var37;
                                var38 += var40;
                            }
                        } else {
                            y_b -= y_a;
                            y_a -= y_c;
                            y_c = scanOffsets[y_c];

                            while(true) {
                                --y_a;
                                if (y_a < 0) {
                                    while(true) {
                                        --y_b;
                                        if (y_b < 0) {
                                            return;
                                        }

                                        Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_c, x_a >> 14, x_b >> 14, hsl_c, var30, var32, var35, var38, var33, var36, var39, z_c, depth_slope);
                                        x_b += var27;
                                        x_a += var26;
                                        hsl_c += var31;
                                        z_c += depth_increment;
                                        y_c += Rasterizer2D.Rasterizer2D_width;
                                        var32 += var34;
                                        var35 += var37;
                                        var38 += var40;
                                    }
                                }

                                Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_c, x_c >> 14, x_b >> 14, hsl_c, var30, var32, var35, var38, var33, var36, var39, z_c, depth_slope);
                                x_b += var27;
                                x_c += var28;
                                hsl_c += var31;
                                z_c += depth_increment;
                                y_c += Rasterizer2D.Rasterizer2D_width;
                                var32 += var34;
                                var35 += var37;
                                var38 += var40;
                            }
                        }
                    } else {
                        x_a = x_c <<= 14;
                        if (y_c < 0) {
                            x_a -= var27 * y_c;
                            x_c -= var28 * y_c;
                            hsl_c -= var31 * y_c;
                            y_c = 0;
                        }

                        x_b <<= 14;
                        if (y_b < 0) {
                            x_b -= var26 * y_b;
                            y_b = 0;
                        }

                        var41 = y_c - originViewY;
                        var32 += var34 * var41;
                        var35 += var37 * var41;
                        var38 += var40 * var41;
                        if (var27 < var28) {
                            y_a -= y_b;
                            y_b -= y_c;
                            y_c = scanOffsets[y_c];

                            while(true) {
                                --y_b;
                                if (y_b < 0) {
                                    while(true) {
                                        --y_a;
                                        if (y_a < 0) {
                                            return;
                                        }

                                        Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_c, x_b >> 14, x_c >> 14, hsl_c, var30, var32, var35, var38, var33, var36, var39, z_c, depth_slope);
                                        x_b += var26;
                                        x_c += var28;
                                        hsl_c += var31;
                                        z_c += depth_increment;
                                        y_c += Rasterizer2D.Rasterizer2D_width;
                                        var32 += var34;
                                        var35 += var37;
                                        var38 += var40;
                                    }
                                }

                                Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_c, x_a >> 14, x_c >> 14, hsl_c, var30, var32, var35, var38, var33, var36, var39, z_c, depth_slope);
                                x_a += var27;
                                x_c += var28;
                                hsl_c += var31;
                                z_c += depth_increment;
                                y_c += Rasterizer2D.Rasterizer2D_width;
                                var32 += var34;
                                var35 += var37;
                                var38 += var40;
                            }
                        } else {
                            y_a -= y_b;
                            y_b -= y_c;
                            y_c = scanOffsets[y_c];

                            while(true) {
                                --y_b;
                                if (y_b < 0) {
                                    while(true) {
                                        --y_a;
                                        if (y_a < 0) {
                                            return;
                                        }

                                        Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_c, x_c >> 14, x_b >> 14, hsl_c, var30, var32, var35, var38, var33, var36, var39, z_c, depth_slope);
                                        x_b += var26;
                                        x_c += var28;
                                        hsl_c += var31;
                                        z_c += depth_increment;
                                        y_c += Rasterizer2D.Rasterizer2D_width;
                                        var32 += var34;
                                        var35 += var37;
                                        var38 += var40;
                                    }
                                }

                                Rasterizer3D_iDontKnow(Rasterizer2D.Rasterizer2D_pixels, var19, 0, 0, y_c, x_c >> 14, x_a >> 14, hsl_c, var30, var32, var35, var38, var33, var36, var39, z_c, depth_slope);
                                x_a += var27;
                                x_c += var28;
                                hsl_c += var31;
                                z_c += depth_increment;
                                y_c += Rasterizer2D.Rasterizer2D_width;
                                var32 += var34;
                                var35 += var37;
                                var38 += var40;
                            }
                        }
                    }
                }
            }
        }
    }

    private static int method4281(int var0, int var1) {
        var1 = (var0 & 127) * var1 >> 7;
        if (var1 < 2) {
            var1 = 2;
        } else if (var1 > 126) {
            var1 = 126;
        }

        return (var0 & 'ﾀ') + var1;
    }

    static final void Rasterizer3D_iDontKnow(int[] dest, int[] texture, int var2, int rgb, int dest_off, int x1, int x2, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, float depth, float depth_slope) {
        if (triangleIsOutOfBounds) {
            if (x2 > Rasterizer2D.lastX) {
                x2 = Rasterizer2D.lastX;
            }

            if (x1 < 0) {
                x1 = 0;
            }
        }

        if (x1 < x2) {
            dest_off += x1;
            var7 += x1 * var8;
            int var17 = x2 - x1;
            int var15;
            int var16;
            int var10000;
            int var18;
            int var19;
            int var20;
            int var21;
            int var22;
            int var23;
            depth += depth_slope * (float) x1;
            if (lowMem) {
                var23 = x1 - originViewX;
                var9 += var23 * (var12 >> 3);
                var10 += (var13 >> 3) * var23;
                var11 += var23 * (var14 >> 3);
                var22 = var11 >> 12;
                if (var22 != 0) {
                    var18 = var9 / var22;
                    var19 = var10 / var22;
                    if (var18 < 0) {
                        var18 = 0;
                    } else if (var18 > 4032) {
                        var18 = 4032;
                    }
                } else {
                    var18 = 0;
                    var19 = 0;
                }

                var9 += var12;
                var10 += var13;
                var11 += var14;
                var22 = var11 >> 12;
                if (var22 != 0) {
                    var20 = var9 / var22;
                    var21 = var10 / var22;
                    if (var20 < 0) {
                        var20 = 0;
                    } else if (var20 > 4032) {
                        var20 = 4032;
                    }
                } else {
                    var20 = 0;
                    var21 = 0;
                }

                var2 = (var18 << 20) + var19;
                var16 = (var21 - var19 >> 3) + (var20 - var18 >> 3 << 20);
                var17 >>= 3;
                var8 <<= 3;
                var15 = var7 >> 8;
                if (opaque) {
                    if (var17 > 0) {
                        do {
                            rgb = texture[(var2 >>> 26) + (var2 & 4032)];
                            depthBuffer[dest_off] = depth;
                            depth += depth_slope;
                            dest[dest_off++] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                            var2 += var16;
                            rgb = texture[(var2 >>> 26) + (var2 & 4032)];
                            depthBuffer[dest_off] = depth;
                            depth += depth_slope;
                            dest[dest_off++] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                            var2 += var16;
                            rgb = texture[(var2 >>> 26) + (var2 & 4032)];
                            depthBuffer[dest_off] = depth;
                            depth += depth_slope;
                            dest[dest_off++] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                            var2 += var16;
                            rgb = texture[(var2 >>> 26) + (var2 & 4032)];
                            depthBuffer[dest_off] = depth;
                            depth += depth_slope;
                            dest[dest_off++] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                            var2 += var16;
                            rgb = texture[(var2 >>> 26) + (var2 & 4032)];
                            depthBuffer[dest_off] = depth;
                            depth += depth_slope;
                            dest[dest_off++] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                            var2 += var16;
                            rgb = texture[(var2 >>> 26) + (var2 & 4032)];
                            depthBuffer[dest_off] = depth;
                            depth += depth_slope;
                            dest[dest_off++] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                            var2 += var16;
                            rgb = texture[(var2 >>> 26) + (var2 & 4032)];
                            depthBuffer[dest_off] = depth;
                            depth += depth_slope;
                            dest[dest_off++] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                            var2 += var16;
                            rgb = texture[(var2 >>> 26) + (var2 & 4032)];
                            depthBuffer[dest_off] = depth;
                            depth += depth_slope;
                            dest[dest_off++] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                            var10000 = var16 + var2;
                            var18 = var20;
                            var19 = var21;
                            var9 += var12;
                            var10 += var13;
                            var11 += var14;
                            var22 = var11 >> 12;
                            if (var22 != 0) {
                                var20 = var9 / var22;
                                var21 = var10 / var22;
                                if (var20 < 0) {
                                    var20 = 0;
                                } else if (var20 > 4032) {
                                    var20 = 4032;
                                }
                            } else {
                                var20 = 0;
                                var21 = 0;
                            }

                            var2 = (var18 << 20) + var19;
                            var16 = (var21 - var19 >> 3) + (var20 - var18 >> 3 << 20);
                            var7 += var8;
                            var15 = var7 >> 8;
                            --var17;
                        } while(var17 > 0);
                    }

                    var17 = x2 - x1 & 7;
                    if (var17 > 0) {
                        do {
                            rgb = texture[(var2 >>> 26) + (var2 & 4032)];
                            depthBuffer[dest_off] = depth;
                            depth += depth_slope;
                            dest[dest_off++] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                            var2 += var16;
                            --var17;
                        } while(var17 > 0);
                    }
                } else {
                    if (var17 > 0) {
                        do {
                            if ((rgb = texture[(var2 >>> 26) + (var2 & 4032)]) != 0) {
                                dest[dest_off] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                                depthBuffer[dest_off] = depth;
                            }

                            depth += depth_slope;
                            ++dest_off;
                            var2 += var16;
                            if ((rgb = texture[(var2 >>> 26) + (var2 & 4032)]) != 0) {
                                dest[dest_off] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                                depthBuffer[dest_off] = depth;
                            }

                            depth += depth_slope;
                            ++dest_off;
                            var2 += var16;
                            if ((rgb = texture[(var2 >>> 26) + (var2 & 4032)]) != 0) {
                                dest[dest_off] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                                depthBuffer[dest_off] = depth;
                            }

                            depth += depth_slope;
                            ++dest_off;
                            var2 += var16;
                            if ((rgb = texture[(var2 >>> 26) + (var2 & 4032)]) != 0) {
                                dest[dest_off] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                                depthBuffer[dest_off] = depth;
                            }

                            depth += depth_slope;
                            ++dest_off;
                            var2 += var16;
                            if ((rgb = texture[(var2 >>> 26) + (var2 & 4032)]) != 0) {
                                dest[dest_off] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                                depthBuffer[dest_off] = depth;
                            }

                            depth += depth_slope;
                            ++dest_off;
                            var2 += var16;
                            if ((rgb = texture[(var2 >>> 26) + (var2 & 4032)]) != 0) {
                                dest[dest_off] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                                depthBuffer[dest_off] = depth;
                            }

                            depth += depth_slope;
                            ++dest_off;
                            var2 += var16;
                            if ((rgb = texture[(var2 >>> 26) + (var2 & 4032)]) != 0) {
                                dest[dest_off] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                                depthBuffer[dest_off] = depth;
                            }

                            depth += depth_slope;
                            ++dest_off;
                            var2 += var16;
                            if ((rgb = texture[(var2 >>> 26) + (var2 & 4032)]) != 0) {
                                dest[dest_off] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                                depthBuffer[dest_off] = depth;
                            }

                            depth += depth_slope;
                            ++dest_off;
                            var10000 = var16 + var2;
                            var18 = var20;
                            var19 = var21;
                            var9 += var12;
                            var10 += var13;
                            var11 += var14;
                            var22 = var11 >> 12;
                            if (var22 != 0) {
                                var20 = var9 / var22;
                                var21 = var10 / var22;
                                if (var20 < 0) {
                                    var20 = 0;
                                } else if (var20 > 4032) {
                                    var20 = 4032;
                                }
                            } else {
                                var20 = 0;
                                var21 = 0;
                            }

                            var2 = (var18 << 20) + var19;
                            var16 = (var21 - var19 >> 3) + (var20 - var18 >> 3 << 20);
                            var7 += var8;
                            var15 = var7 >> 8;
                            --var17;
                        } while(var17 > 0);
                    }

                    var17 = x2 - x1 & 7;
                    if (var17 > 0) {
                        do {
                            if ((rgb = texture[(var2 >>> 26) + (var2 & 4032)]) != 0) {
                                dest[dest_off] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                                depthBuffer[dest_off] = depth;
                            }

                            depth += depth_slope;
                            ++dest_off;
                            var2 += var16;
                            --var17;
                        } while(var17 > 0);
                    }
                }
            } else {
                var23 = x1 - originViewX;
                var9 += var23 * (var12 >> 3);
                var10 += (var13 >> 3) * var23;
                var11 += var23 * (var14 >> 3);
                var22 = var11 >> 14;
                if (var22 != 0) {
                    var18 = var9 / var22;
                    var19 = var10 / var22;
                    if (var18 < 0) {
                        var18 = 0;
                    } else if (var18 > 16256) {
                        var18 = 16256;
                    }
                } else {
                    var18 = 0;
                    var19 = 0;
                }

                var9 += var12;
                var10 += var13;
                var11 += var14;
                var22 = var11 >> 14;
                if (var22 != 0) {
                    var20 = var9 / var22;
                    var21 = var10 / var22;
                    if (var20 < 0) {
                        var20 = 0;
                    } else if (var20 > 16256) {
                        var20 = 16256;
                    }
                } else {
                    var20 = 0;
                    var21 = 0;
                }

                var2 = (var18 << 18) + var19;
                var16 = (var21 - var19 >> 3) + (var20 - var18 >> 3 << 18);
                var17 >>= 3;
                var8 <<= 3;
                var15 = var7 >> 8;
                if (opaque) {
                    if (var17 > 0) {
                        do {
                            rgb = texture[(var2 & 16256) + (var2 >>> 25)];
                            depthBuffer[dest_off] = depth;
                            depth += depth_slope;
                            dest[dest_off++] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                            var2 += var16;
                            rgb = texture[(var2 & 16256) + (var2 >>> 25)];
                            depthBuffer[dest_off] = depth;
                            depth += depth_slope;
                            dest[dest_off++] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                            var2 += var16;
                            rgb = texture[(var2 & 16256) + (var2 >>> 25)];
                            depthBuffer[dest_off] = depth;
                            depth += depth_slope;
                            dest[dest_off++] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                            var2 += var16;
                            rgb = texture[(var2 & 16256) + (var2 >>> 25)];
                            depthBuffer[dest_off] = depth;
                            depth += depth_slope;
                            dest[dest_off++] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                            var2 += var16;
                            rgb = texture[(var2 & 16256) + (var2 >>> 25)];
                            depthBuffer[dest_off] = depth;
                            depth += depth_slope;
                            dest[dest_off++] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                            var2 += var16;
                            rgb = texture[(var2 & 16256) + (var2 >>> 25)];
                            depthBuffer[dest_off] = depth;
                            depth += depth_slope;
                            dest[dest_off++] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                            var2 += var16;
                            rgb = texture[(var2 & 16256) + (var2 >>> 25)];
                            depthBuffer[dest_off] = depth;
                            depth += depth_slope;
                            dest[dest_off++] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                            var2 += var16;
                            rgb = texture[(var2 & 16256) + (var2 >>> 25)];
                            depthBuffer[dest_off] = depth;
                            depth += depth_slope;
                            dest[dest_off++] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                            var10000 = var16 + var2;
                            var18 = var20;
                            var19 = var21;
                            var9 += var12;
                            var10 += var13;
                            var11 += var14;
                            var22 = var11 >> 14;
                            if (var22 != 0) {
                                var20 = var9 / var22;
                                var21 = var10 / var22;
                                if (var20 < 0) {
                                    var20 = 0;
                                } else if (var20 > 16256) {
                                    var20 = 16256;
                                }
                            } else {
                                var20 = 0;
                                var21 = 0;
                            }

                            var2 = (var18 << 18) + var19;
                            var16 = (var21 - var19 >> 3) + (var20 - var18 >> 3 << 18);
                            var7 += var8;
                            var15 = var7 >> 8;
                            --var17;
                        } while(var17 > 0);
                    }

                    var17 = x2 - x1 & 7;
                    if (var17 > 0) {
                        do {
                            rgb = texture[(var2 & 16256) + (var2 >>> 25)];
                            depthBuffer[dest_off] = depth;
                            depth += depth_slope;
                            dest[dest_off++] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                            var2 += var16;
                            --var17;
                        } while(var17 > 0);
                    }
                } else {
                    if (var17 > 0) {
                        do {
                            if ((rgb = texture[(var2 & 16256) + (var2 >>> 25)]) != 0) {
                                dest[dest_off] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                                depthBuffer[dest_off] = depth;
                            }

                            depth += depth_slope;
                            ++dest_off;
                            var2 += var16;
                            if ((rgb = texture[(var2 & 16256) + (var2 >>> 25)]) != 0) {
                                dest[dest_off] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                                depthBuffer[dest_off] = depth;
                            }

                            depth += depth_slope;
                            ++dest_off;
                            var2 += var16;
                            if ((rgb = texture[(var2 & 16256) + (var2 >>> 25)]) != 0) {
                                dest[dest_off] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                                depthBuffer[dest_off] = depth;
                            }

                            depth += depth_slope;
                            ++dest_off;
                            var2 += var16;
                            if ((rgb = texture[(var2 & 16256) + (var2 >>> 25)]) != 0) {
                                dest[dest_off] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                                depthBuffer[dest_off] = depth;
                            }

                            depth += depth_slope;
                            ++dest_off;
                            var2 += var16;
                            if ((rgb = texture[(var2 & 16256) + (var2 >>> 25)]) != 0) {
                                dest[dest_off] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                                depthBuffer[dest_off] = depth;
                            }

                            depth += depth_slope;
                            ++dest_off;
                            var2 += var16;
                            if ((rgb = texture[(var2 & 16256) + (var2 >>> 25)]) != 0) {
                                dest[dest_off] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                                depthBuffer[dest_off] = depth;
                            }

                            depth += depth_slope;
                            ++dest_off;
                            var2 += var16;
                            if ((rgb = texture[(var2 & 16256) + (var2 >>> 25)]) != 0) {
                                dest[dest_off] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                                depthBuffer[dest_off] = depth;
                            }

                            depth += depth_slope;
                            ++dest_off;
                            var2 += var16;
                            if ((rgb = texture[(var2 & 16256) + (var2 >>> 25)]) != 0) {
                                dest[dest_off] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                                depthBuffer[dest_off] = depth;
                            }

                            depth += depth_slope;
                            ++dest_off;
                            var10000 = var16 + var2;
                            var18 = var20;
                            var19 = var21;
                            var9 += var12;
                            var10 += var13;
                            var11 += var14;
                            var22 = var11 >> 14;
                            if (var22 != 0) {
                                var20 = var9 / var22;
                                var21 = var10 / var22;
                                if (var20 < 0) {
                                    var20 = 0;
                                } else if (var20 > 16256) {
                                    var20 = 16256;
                                }
                            } else {
                                var20 = 0;
                                var21 = 0;
                            }

                            var2 = (var18 << 18) + var19;
                            var16 = (var21 - var19 >> 3) + (var20 - var18 >> 3 << 18);
                            var7 += var8;
                            var15 = var7 >> 8;
                            --var17;
                        } while(var17 > 0);
                    }

                    var17 = x2 - x1 & 7;
                    if (var17 > 0) {
                        do {
                            if ((rgb = texture[(var2 & 16256) + (var2 >>> 25)]) != 0) {
                                dest[dest_off] = (var15 * (rgb & '\uff00') & 16711680) + ((rgb & 16711935) * var15 & -16711936) >> 8;
                                depthBuffer[dest_off] = depth;
                            }

                            depth += depth_slope;
                            ++dest_off;
                            var2 += var16;
                            --var17;
                        } while(var17 > 0);
                    }
                }
            }

        }
    }

    public static void updateTextures(Client client, int j) {
        if (!ClientCompanion.lowMemory) {
            if (textureLastUsed[17] >= j) { // fountain water
                IndexedImage background = textures[17];
                int k = background.width * background.height - 1;
                int j1 = background.width * client.tickDelta * 2;
                byte[] raster = background.palettePixels;
                byte[] abyte3 = lastTexturePalettePixels;
                for (int i2 = 0; i2 <= k; i2++)
                    abyte3[i2] = raster[i2 - j1 & k];

                background.palettePixels = abyte3;
                lastTexturePalettePixels = raster;
                requestTextureUpdate(17);
                ClientCompanion.anInt854++;
                if (ClientCompanion.anInt854 > 1235) {
                    ClientCompanion.anInt854 = 0;
                }
            }
            /* Moving textures */
            if (textureLastUsed[24] >= j) { //water texture
                IndexedImage background_1 = textures[24];
                int l = background_1.width * background_1.height - 1;
                int k1 = background_1.width * client.tickDelta * 2;
                byte[] abyte1 = background_1.palettePixels;
                byte[] abyte4 = lastTexturePalettePixels;
                for (int j2 = 0; j2 <= l; j2++)
                    abyte4[j2] = abyte1[j2 - k1 & l];

                background_1.palettePixels = abyte4;
                lastTexturePalettePixels = abyte1;
                requestTextureUpdate(24);
            }
            if (textureLastUsed[34] >= j) { // stars
                IndexedImage background_2 = textures[34];
                int i1 = background_2.width * background_2.height - 1;
                int l1 = background_2.width * client.tickDelta * 2;
                byte[] abyte2 = background_2.palettePixels;
                byte[] abyte5 = lastTexturePalettePixels;
                for (int k2 = 0; k2 <= i1; k2++)
                    abyte5[k2] = abyte2[k2 - l1 & i1];

                background_2.palettePixels = abyte5;
                lastTexturePalettePixels = abyte2;
                requestTextureUpdate(34);
            }
            if (textureLastUsed[40] >= j) { // lava texture
                IndexedImage background_2 = textures[40];
                int i1 = background_2.width * background_2.height - 1;
                int l1 = background_2.width * client.tickDelta * 2;
                byte[] abyte2 = background_2.palettePixels;
                byte[] abyte5 = lastTexturePalettePixels;
                for (int k2 = 0; k2 <= i1; k2++)
                    abyte5[k2] = abyte2[k2 - l1 & i1];

                background_2.palettePixels = abyte5;
                lastTexturePalettePixels = abyte2;
                requestTextureUpdate(40);
            }
            if (textureLastUsed[59] >= j) {
                IndexedImage background_1 = textures[59];
                int l = background_1.width * background_1.height - 1;
                int k1 = background_1.width * client.tickDelta * 2;
                byte[] abyte1 = background_1.palettePixels;
                byte[] abyte4 = lastTexturePalettePixels;
                for (int j2 = 0; j2 <= l; j2++)
                    abyte4[j2] = abyte1[j2 - k1 & l];

                background_1.palettePixels = abyte4;
                lastTexturePalettePixels = abyte1;
                requestTextureUpdate(59);
            }
        }
    }

    public static final void method4269(float z1, float z2, float z3, int y1, int y2, int y3, int x1, int x2, int x3, int var6) {
        if (z1 < 0 || z2 < 0 || z3 < 0)
            return;
        int var7 = 0;
        if (y1 != y2) {
            var7 = (x2 - x1 << 14) / (y2 - y1);
        }

        int var8 = 0;
        if (y3 != y2) {
            var8 = (x3 - x2 << 14) / (y3 - y2);
        }

        int var9 = 0;
        if (y1 != y3) {
            var9 = (x1 - x3 << 14) / (y1 - y3);
        }

        float dx_21 = x2 - x1;
        float dy_21 = y2 - y1;
        float dx_31 = x3 - x1;
        float dy_31 = y3 - y1;
        float dz_21 = z2 - z1;
        float dz_31 = z3 - z1;

        float div = dx_21 * dy_31 - dx_31 * dy_21;
        float depth_slope = (dz_21 * dy_31 - dz_31 * dy_21) / div;
        float depth_increment = (dz_31 * dx_21 - dz_21 * dx_31) / div;

        if (y1 <= y2 && y1 <= y3) {
            if (y1 < Rasterizer2D_yClipEnd) {
                if (y2 > Rasterizer2D_yClipEnd) {
                    y2 = Rasterizer2D_yClipEnd;
                }

                if (y3 > Rasterizer2D_yClipEnd) {
                    y3 = Rasterizer2D_yClipEnd;
                }
                z1 = z1 - depth_slope * x1 + depth_slope;
                if (y2 < y3) {
                    x3 = x1 <<= 14;
                    if (y1 < 0) {
                        x3 -= y1 * var9;
                        x1 -= y1 * var7;
                        z1 -= depth_increment * y1;
                        y1 = 0;
                    }

                    x2 <<= 14;
                    if (y2 < 0) {
                        x2 -= var8 * y2;
                        y2 = 0;
                    }

                    if (y1 != y2 && var9 < var7 || y1 == y2 && var9 > var8) {
                        y3 -= y2;
                        y2 -= y1;
                        y1 = scanOffsets[y1];

                        while(true) {
                            --y2;
                            if (y2 < 0) {
                                while(true) {
                                    --y3;
                                    if (y3 < 0) {
                                        return;
                                    }

                                    Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y1, var6, 0, x3 >> 14, x2 >> 14, z1, depth_slope);
                                    x3 += var9;
                                    x2 += var8;
                                    y1 += Rasterizer2D.Rasterizer2D_width;
                                    z1 += depth_increment;
                                }
                            }

                            Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y1, var6, 0, x3 >> 14, x1 >> 14, z1, depth_slope);
                            x3 += var9;
                            x1 += var7;
                            y1 += Rasterizer2D.Rasterizer2D_width;
                            z1 += depth_increment;
                        }
                    } else {
                        y3 -= y2;
                        y2 -= y1;
                        y1 = scanOffsets[y1];

                        while(true) {
                            --y2;
                            if (y2 < 0) {
                                while(true) {
                                    --y3;
                                    if (y3 < 0) {
                                        return;
                                    }

                                    Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y1, var6, 0, x2 >> 14, x3 >> 14, z1, depth_slope);
                                    x3 += var9;
                                    x2 += var8;
                                    y1 += Rasterizer2D.Rasterizer2D_width;
                                    z1 += depth_increment;
                                }
                            }

                            Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y1, var6, 0, x1 >> 14, x3 >> 14, z1, depth_slope);
                            x3 += var9;
                            x1 += var7;
                            y1 += Rasterizer2D.Rasterizer2D_width;
                            z1 += depth_increment;
                        }
                    }
                } else {
                    x2 = x1 <<= 14;
                    if (y1 < 0) {
                        x2 -= y1 * var9;
                        x1 -= y1 * var7;
                        z1 -= depth_increment * y1;
                        y1 = 0;
                    }

                    x3 <<= 14;
                    if (y3 < 0) {
                        x3 -= var8 * y3;
                        y3 = 0;
                    }

                    if (y1 != y3 && var9 < var7 || y1 == y3 && var8 > var7) {
                        y2 -= y3;
                        y3 -= y1;
                        y1 = scanOffsets[y1];

                        while(true) {
                            --y3;
                            if (y3 < 0) {
                                while(true) {
                                    --y2;
                                    if (y2 < 0) {
                                        return;
                                    }

                                    Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y1, var6, 0, x3 >> 14, x1 >> 14, z1, depth_slope);
                                    x3 += var8;
                                    x1 += var7;
                                    y1 += Rasterizer2D.Rasterizer2D_width;
                                    z1 += depth_increment;
                                }
                            }

                            Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y1, var6, 0, x2 >> 14, x1 >> 14, z1, depth_slope);
                            x2 += var9;
                            x1 += var7;
                            y1 += Rasterizer2D.Rasterizer2D_width;
                            z1 += depth_increment;
                        }
                    } else {
                        y2 -= y3;
                        y3 -= y1;
                        y1 = scanOffsets[y1];

                        while(true) {
                            --y3;
                            if (y3 < 0) {
                                while(true) {
                                    --y2;
                                    if (y2 < 0) {
                                        return;
                                    }

                                    Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y1, var6, 0, x1 >> 14, x3 >> 14, z1, depth_slope);
                                    x3 += var8;
                                    x1 += var7;
                                    y1 += Rasterizer2D.Rasterizer2D_width;
                                    z1 += depth_increment;
                                }
                            }

                            Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y1, var6, 0, x1 >> 14, x2 >> 14, z1, depth_slope);
                            x2 += var9;
                            x1 += var7;
                            y1 += Rasterizer2D.Rasterizer2D_width;
                            z1 += depth_increment;
                        }
                    }
                }
            }
        } else if (y2 <= y3) {
            if (y2 < Rasterizer2D_yClipEnd) {
                if (y3 > Rasterizer2D_yClipEnd) {
                    y3 = Rasterizer2D_yClipEnd;
                }

                if (y1 > Rasterizer2D_yClipEnd) {
                    y1 = Rasterizer2D_yClipEnd;
                }

                if (y3 < y1) {
                    x1 = x2 <<= 14;
                    if (y2 < 0) {
                        x1 -= var7 * y2;
                        x2 -= var8 * y2;
                        z2 -= depth_increment * y2;
                        y2 = 0;
                    }

                    x3 <<= 14;
                    if (y3 < 0) {
                        x3 -= var9 * y3;
                        y3 = 0;
                    }

                    if (y3 != y2 && var7 < var8 || y3 == y2 && var7 > var9) {
                        y1 -= y3;
                        y3 -= y2;
                        y2 = scanOffsets[y2];

                        while(true) {
                            --y3;
                            if (y3 < 0) {
                                while(true) {
                                    --y1;
                                    if (y1 < 0) {
                                        return;
                                    }

                                    Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y2, var6, 0, x1 >> 14, x3 >> 14, z2, depth_slope);
                                    x1 += var7;
                                    x3 += var9;
                                    y2 += Rasterizer2D.Rasterizer2D_width;
                                    z2 += depth_increment;
                                }
                            }

                            Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y2, var6, 0, x1 >> 14, x2 >> 14, z2, depth_slope);
                            x1 += var7;
                            x2 += var8;
                            y2 += Rasterizer2D.Rasterizer2D_width;
                            z2 += depth_increment;
                        }
                    } else {
                        y1 -= y3;
                        y3 -= y2;
                        y2 = scanOffsets[y2];

                        while(true) {
                            --y3;
                            if (y3 < 0) {
                                while(true) {
                                    --y1;
                                    if (y1 < 0) {
                                        return;
                                    }

                                    Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y2, var6, 0, x3 >> 14, x1 >> 14, z2, depth_slope);
                                    x1 += var7;
                                    x3 += var9;
                                    y2 += Rasterizer2D.Rasterizer2D_width;
                                    z2 += depth_increment;
                                }
                            }

                            Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y2, var6, 0, x2 >> 14, x1 >> 14, z2, depth_slope);
                            x1 += var7;
                            x2 += var8;
                            y2 += Rasterizer2D.Rasterizer2D_width;
                            z2 += depth_increment;
                        }
                    }
                } else {
                    x3 = x2 <<= 14;
                    if (y2 < 0) {
                        x3 -= var7 * y2;
                        x2 -= var8 * y2;
                        z2 -= depth_increment * y2;
                        y2 = 0;
                    }

                    x1 <<= 14;
                    if (y1 < 0) {
                        x1 -= y1 * var9;
                        y1 = 0;
                    }

                    if (var7 < var8) {
                        y3 -= y1;
                        y1 -= y2;
                        y2 = scanOffsets[y2];

                        while(true) {
                            --y1;
                            if (y1 < 0) {
                                while(true) {
                                    --y3;
                                    if (y3 < 0) {
                                        return;
                                    }

                                    Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y2, var6, 0, x1 >> 14, x2 >> 14, z2, depth_slope);
                                    x1 += var9;
                                    x2 += var8;
                                    y2 += Rasterizer2D.Rasterizer2D_width;
                                    z2 += depth_increment;
                                }
                            }

                            Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y2, var6, 0, x3 >> 14, x2 >> 14, z2, depth_slope);
                            x3 += var7;
                            x2 += var8;
                            y2 += Rasterizer2D.Rasterizer2D_width;
                            z2 += depth_increment;
                        }
                    } else {
                        y3 -= y1;
                        y1 -= y2;
                        y2 = scanOffsets[y2];

                        while(true) {
                            --y1;
                            if (y1 < 0) {
                                while(true) {
                                    --y3;
                                    if (y3 < 0) {
                                        return;
                                    }

                                    Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y2, var6, 0, x2 >> 14, x1 >> 14, z2, depth_slope);
                                    x1 += var9;
                                    x2 += var8;
                                    y2 += Rasterizer2D.Rasterizer2D_width;
                                    z2 += depth_increment;
                                }
                            }

                            Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y2, var6, 0, x2 >> 14, x3 >> 14, z2, depth_slope);
                            x3 += var7;
                            x2 += var8;
                            y2 += Rasterizer2D.Rasterizer2D_width;
                            z2 += depth_increment;
                        }
                    }
                }
            }
        } else if (y3 < Rasterizer2D_yClipEnd) {
            if (y1 > Rasterizer2D_yClipEnd) {
                y1 = Rasterizer2D_yClipEnd;
            }

            if (y2 > Rasterizer2D_yClipEnd) {
                y2 = Rasterizer2D_yClipEnd;
            }

            if (y1 < y2) {
                x2 = x3 <<= 14;
                if (y3 < 0) {
                    x2 -= var8 * y3;
                    x3 -= var9 * y3;
                    z3 -= depth_increment * y3;
                    y3 = 0;
                }

                x1 <<= 14;
                if (y1 < 0) {
                    x1 -= y1 * var7;
                    y1 = 0;
                }

                if (var8 < var9) {
                    y2 -= y1;
                    y1 -= y3;
                    y3 = scanOffsets[y3];

                    while(true) {
                        --y1;
                        if (y1 < 0) {
                            while(true) {
                                --y2;
                                if (y2 < 0) {
                                    return;
                                }

                                Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y3, var6, 0, x2 >> 14, x1 >> 14, z3, depth_slope);
                                x2 += var8;
                                x1 += var7;
                                y3 += Rasterizer2D.Rasterizer2D_width;
                                z3 += depth_increment;
                            }
                        }

                        Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y3, var6, 0, x2 >> 14, x3 >> 14, z3, depth_slope);
                        x2 += var8;
                        x3 += var9;
                        y3 += Rasterizer2D.Rasterizer2D_width;
                        z3 += depth_increment;
                    }
                } else {
                    y2 -= y1;
                    y1 -= y3;
                    y3 = scanOffsets[y3];

                    while(true) {
                        --y1;
                        if (y1 < 0) {
                            while(true) {
                                --y2;
                                if (y2 < 0) {
                                    return;
                                }

                                Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y3, var6, 0, x1 >> 14, x2 >> 14, z3, depth_slope);
                                x2 += var8;
                                x1 += var7;
                                y3 += Rasterizer2D.Rasterizer2D_width;
                                z3 += depth_increment;
                            }
                        }

                        Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y3, var6, 0, x3 >> 14, x2 >> 14, z3, depth_slope);
                        x2 += var8;
                        x3 += var9;
                        y3 += Rasterizer2D.Rasterizer2D_width;
                        z3 += depth_increment;
                    }
                }
            } else {
                x1 = x3 <<= 14;
                if (y3 < 0) {
                    x1 -= var8 * y3;
                    x3 -= var9 * y3;
                    z3 -= depth_increment * y3;
                    y3 = 0;
                }

                x2 <<= 14;
                if (y2 < 0) {
                    x2 -= var7 * y2;
                    y2 = 0;
                }

                if (var8 < var9) {
                    y1 -= y2;
                    y2 -= y3;
                    y3 = scanOffsets[y3];

                    while(true) {
                        --y2;
                        if (y2 < 0) {
                            while(true) {
                                --y1;
                                if (y1 < 0) {
                                    return;
                                }

                                Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y3, var6, 0, x2 >> 14, x3 >> 14, z3, depth_slope);
                                x2 += var7;
                                x3 += var9;
                                y3 += Rasterizer2D.Rasterizer2D_width;
                                z3 += depth_increment;
                            }
                        }

                        Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y3, var6, 0, x1 >> 14, x3 >> 14, z3, depth_slope);
                        x1 += var8;
                        x3 += var9;
                        y3 += Rasterizer2D.Rasterizer2D_width;
                        z3 += depth_increment;
                    }
                } else {
                    y1 -= y2;
                    y2 -= y3;
                    y3 = scanOffsets[y3];

                    while(true) {
                        --y2;
                        if (y2 < 0) {
                            while(true) {
                                --y1;
                                if (y1 < 0) {
                                    return;
                                }

                                Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y3, var6, 0, x3 >> 14, x2 >> 14, z3, depth_slope);
                                x2 += var7;
                                x3 += var9;
                                y3 += Rasterizer2D.Rasterizer2D_width;
                                z3 += depth_increment;
                            }
                        }

                        Rasterizer3D_horizAlpha(Rasterizer2D.Rasterizer2D_pixels, y3, var6, 0, x3 >> 14, x1 >> 14, z3, depth_slope);
                        x1 += var8;
                        x3 += var9;
                        y3 += Rasterizer2D.Rasterizer2D_width;
                        z3 += depth_increment;
                    }
                }
            }
        }
    }

    static final void Rasterizer3D_horizAlpha(int[] dest, int offset, int var2, int var3, int x1, int x2, float depth, float depth_slope) {
        if (triangleIsOutOfBounds) {
            if (x2 > Rasterizer2D.lastX) {
                x2 = Rasterizer2D.lastX;
            }

            if (x1 < 0) {
                x1 = 0;
            }
        }

        if (x1 < x2) {
            offset += x1;
            var3 = x2 - x1 >> 2;
            depth += depth_slope * (float) x1;
            if (alpha != 0) {
                if (alpha == 254) {
                    while(true) {
                        --var3;
                        if (var3 < 0) {
                            var3 = x2 - x1 & 3;

                            while(true) {
                                --var3;
                                if (var3 < 0) {
                                    return;
                                }

                                dest[offset++] = dest[offset];
                            }
                        }

                        dest[offset++] = dest[offset];
                        dest[offset++] = dest[offset];
                        dest[offset++] = dest[offset];
                        dest[offset++] = dest[offset];
                    }
                } else {
                    int var6 = alpha;
                    int var7 = 256 - alpha;
                    var2 = (var7 * (var2 & '\uff00') >> 8 & '\uff00') + (var7 * (var2 & 16711935) >> 8 & 16711935);

                    while(true) {
                        --var3;
                        int var8;
                        if (var3 < 0) {
                            var3 = x2 - x1 & 3;

                            while(true) {
                                --var3;
                                if (var3 < 0) {
                                    return;
                                }

                                var8 = dest[offset];
                                dest[offset++] = ((var8 & 16711935) * var6 >> 8 & 16711935) + var2 + (var6 * (var8 & '\uff00') >> 8 & '\uff00');
                            }
                        }

                        var8 = dest[offset];
                        dest[offset++] = ((var8 & 16711935) * var6 >> 8 & 16711935) + var2 + (var6 * (var8 & '\uff00') >> 8 & '\uff00');
                        var8 = dest[offset];
                        dest[offset++] = ((var8 & 16711935) * var6 >> 8 & 16711935) + var2 + (var6 * (var8 & '\uff00') >> 8 & '\uff00');
                        var8 = dest[offset];
                        dest[offset++] = ((var8 & 16711935) * var6 >> 8 & 16711935) + var2 + (var6 * (var8 & '\uff00') >> 8 & '\uff00');
                        var8 = dest[offset];
                        dest[offset++] = ((var8 & 16711935) * var6 >> 8 & 16711935) + var2 + (var6 * (var8 & '\uff00') >> 8 & '\uff00');
                    }
                }
            } else {
                while(true) {
                    --var3;
                    if (var3 < 0) {
                        var3 = x2 - x1 & 3;

                        while(true) {
                            --var3;
                            if (var3 < 0) {
                                return;
                            }

                            dest[offset++] = var2;
                            depthBuffer[offset] = depth;
                            depth += depth_slope;
                        }
                    }

                    depthBuffer[offset] = depth;
                    depth += depth_slope;
                    dest[offset++] = var2;
                    depthBuffer[offset] = depth;
                    depth += depth_slope;
                    dest[offset++] = var2;
                    depthBuffer[offset] = depth;
                    depth += depth_slope;
                    dest[offset++] = var2;
                    depthBuffer[offset] = depth;
                    depth += depth_slope;
                    dest[offset++] = var2;
                }
            }
        }
    }

    public static final int method4282(int var0, int var1, int var2, int var3) {
        return var0 * var2 + var3 * var1 >> 16;
    }

    public static final int method4283(int var0, int var1, int var2, int var3) {
        return var2 * var1 - var3 * var0 >> 16;
    }

    public static final int method4284(int var0, int var1, int var2, int var3) {
        return var0 * var2 - var3 * var1 >> 16;
    }

    public static final int method4346(int var0, int var1, int var2, int var3) {
        return var3 * var0 + var2 * var1 >> 16;
    }

    public static final int method4286(int var0, int var1, int var2, int var3) {
        return var0 * var2 + var3 * var1 >> 16;
    }

    public static final int method4287(int var0, int var1, int var2, int var3) {
        return var2 * var1 - var3 * var0 >> 16;
    }

}