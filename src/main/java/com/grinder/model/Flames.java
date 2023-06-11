package com.grinder.model;

import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.cache.IndexedSprite;
import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.draw.GraphicsBuffer;
import com.runescape.draw.Rasterizer2D;

public class Flames {

    public static GraphicsBuffer flameBuffer;
    static int[] flames1;
    static int[] flames2;
    static int[] flames3;
    static int[] flames4;
    static int[] flames5;
    static int[] flames6;
    static int[] flames7;
    static int[] flames8;

    static Sprite rightFlameSpritePixels;
    static int[] anIntArray16;
    static Sprite leftFlameSpritePixels;
    static int anInt78;
    static int anInt77;
    static int anInt79;
    static int anInt82;
    public static int tick;
    static int anInt80;


    public static void init() {
        anIntArray16 = new int[256];
        initSmallFlamesArrays();
        flames4 = new int[256];
        flames5 = new int[32768];
        flames6 = new int[32768];
        randomizeBackground(null);
        flames7 = new int[32768];
        flames8 = new int[32768];
    }

    static void initSmallFlamesArrays() {
        flames1 = new int[256];
        populateFlames(flames1, 262144, 1024, 16711680, 4, 16776960);

        flames2 = new int[256];
        populateFlames(flames2, 1024, 4, 65280, 262144, 65535);

        flames3 = new int[256];
        populateFlames(flames3, 4, 262144, 255, 1024, 16711935);
    }

    static void randomizeBackground(IndexedSprite indexedsprite_0) {
        short short_0 = 256;

        int int_0;
        for (int_0 = 0; int_0 < flames5.length; int_0++) {
            flames5[int_0] = 0;
        }

        int int_1;
        for (int_0 = 0; int_0 < 5000; int_0++) {
            int_1 = (int) (Math.random() * 128.0D * (double) short_0);
            flames5[int_1] = (int) (Math.random() * 256.0D);
        }

        int int_2;
        int int_3;
        for (int_0 = 0; int_0 < 20; int_0++) {
            for (int_1 = 1; int_1 < short_0 - 1; int_1++) {
                for (int_2 = 1; int_2 < 127; int_2++) {
                    int_3 = int_2 + (int_1 << 7);
                    flames6[int_3] = (flames5[int_3 - 128] + flames5[int_3 + 1] + flames5[int_3 + 128] + flames5[int_3 - 1]) / 4;
                }
            }

            int[] ints_0 = flames5;
            flames5 = flames6;
            flames6 = ints_0;
        }

        if (indexedsprite_0 != null) {
            int_0 = 0;

            for (int_1 = 0; int_1 < indexedsprite_0.height; int_1++) {
                for (int_2 = 0; int_2 < indexedsprite_0.subWidth; int_2++) {
                    if (indexedsprite_0.pixels[int_0++] != 0) {
                        int_3 = int_2 + indexedsprite_0.xOffset + 16;
                        int int_4 = int_1 + indexedsprite_0.yOffset + 16;
                        int int_5 = int_3 + (int_4 << 7);
                        flames5[int_5] = 0;
                    }
                }
            }
        }
    }

    private static void populateFlames(int[] flames3, int i, int i2, int i3, int i4, int i5) {
        int int_1;
        for (int_1 = 0; int_1 < 64; int_1++) {
            flames3[int_1] = int_1 * i;
        }

        for (int_1 = 0; int_1 < 64; int_1++) {
            flames3[int_1 + 64] = int_1 * i2 + i3;
        }

        for (int_1 = 0; int_1 < 64; int_1++) {
            flames3[int_1 + 128] = int_1 * i4 + i5;
        }

        for (int_1 = 0; int_1 < 64; int_1++) {
            flames3[int_1 + 192] = 16777215;
        }
    }

    public static void calculate() {
        short short_4;
        int int_7;
        int int_3;
        int tick;
        int int_2;
        if (Flames.tick > 0) {
            tick = Flames.tick;
            short_4 = 256;
            anInt79 += tick * 128;
            if (anInt79 > flames5.length) {
                anInt79 -= flames5.length;
                int_7 = (int) (Math.random() * 12.0D);
                randomizeBackground(TitleScreen.anIndexedSpriteArray5[int_7]);
            }

            int_7 = 0;
            int int_1 = tick * 128;
            int_2 = (short_4 - tick) * 128;

            int int_8;
            for (int_8 = 0; int_8 < int_2; int_8++) {
                int_3 = flames7[int_1 + int_7] - flames5[int_7 + anInt79 & flames5.length - 1] * tick / 6;
                if (int_3 < 0) {
                    int_3 = 0;
                }

                flames7[int_7++] = int_3;
            }

            int int_4;
            int int_9;
            for (int_8 = short_4 - tick; int_8 < short_4; int_8++) {
                int_3 = int_8 * 128;

                for (int_9 = 0; int_9 < 128; int_9++) {
                    int_4 = (int) (Math.random() * 100.0D);
                    if (int_4 < 50 && int_9 > 10 && int_9 < 118) {
                        flames7[int_3 + int_9] = 255;
                    } else {
                        flames7[int_3 + int_9] = 0;
                    }
                }
            }

            if (anInt77 > 0) {
                anInt77 -= tick * 4;
            }

            if (anInt78 > 0) {
                anInt78 -= tick * 4;
            }

            if (anInt77 == 0 && anInt78 == 0) {
                int_8 = (int) (Math.random() * (double) (2000 / tick));
                if (int_8 == 0) {
                    anInt77 = 1024;
                }

                if (int_8 == 1) {
                    anInt78 = 1024;
                }
            }

            for (int_8 = 0; int_8 < short_4 - tick; int_8++) {
                anIntArray16[int_8] = anIntArray16[int_8 + tick];
            }

            for (int_8 = short_4 - tick; int_8 < short_4; int_8++) {
                anIntArray16[int_8] = (int) (Math.sin((double) anInt82 / 14.0D) * 16.0D + Math.sin((double) anInt82 / 15.0D) * 14.0D + Math.sin((double) anInt82 / 16.0D) * 12.0D);
                ++anInt82;
            }

            anInt80 += tick;
            int_8 = (tick + (Client.tick & 0x1)) / 2;
            if (int_8 > 0) {
                for (int_3 = 0; int_3 < anInt80 * 100; int_3++) {
                    int_9 = (int) (Math.random() * 124.0D) + 2;
                    int_4 = (int) (Math.random() * 128.0D) + 128;
                    flames7[int_9 + (int_4 << 7)] = 192;
                }

                anInt80 = 0;
                int_3 = 0;

                label295:
                while (true) {
                    int int_5;
                    if (int_3 >= short_4) {
                        int_3 = 0;

                        while (true) {
                            if (int_3 >= 128) {
                                break label295;
                            }

                            int_9 = 0;

                            for (int_4 = -int_8; int_4 < short_4; int_4++) {
                                int_5 = int_4 * 128;
                                if (int_8 + int_4 < short_4) {
                                    int_9 += flames8[int_5 + int_3 + int_8 * 128];
                                }

                                if (int_4 - (int_8 + 1) >= 0) {
                                    int_9 -= flames8[int_5 + int_3 - (int_8 + 1) * 128];
                                }

                                if (int_4 >= 0) {
                                    flames7[int_5 + int_3] = int_9 / (int_8 * 2 + 1);
                                }
                            }

                            ++int_3;
                        }
                    }

                    int_9 = 0;
                    int_4 = int_3 * 128;

                    for (int_5 = -int_8; int_5 < 128; int_5++) {
                        if (int_5 + int_8 < 128) {
                            int_9 += flames7[int_4 + int_5 + int_8];
                        }

                        if (int_5 - (int_8 + 1) >= 0) {
                            int_9 -= flames7[int_5 + int_4 - (int_8 + 1)];
                        }

                        if (int_5 >= 0) {
                            flames8[int_4 + int_5] = int_9 / (int_8 * 2 + 1);
                        }
                    }

                    ++int_3;
                }
            }

            Flames.tick = 0;
        }
    }

    static void draw() {
        short short_0 = 256;
        int int_0;
        if (anInt77 > 0) {
            for (int_0 = 0; int_0 < 256; int_0++) {
                if (anInt77 > 768) {
                    flames4[int_0] = method328(flames1[int_0], flames2[int_0], 1024 - anInt77);
                } else if (anInt77 > 256) {
                    flames4[int_0] = flames2[int_0];
                } else {
                    flames4[int_0] = method328(flames2[int_0], flames1[int_0], 256 - anInt77);
                }
            }
        } else if (anInt78 > 0) {
            for (int_0 = 0; int_0 < 256; int_0++) {
                if (anInt78 > 768) {
                    flames4[int_0] = method328(flames1[int_0], flames3[int_0], 1024 - anInt78);
                } else if (anInt78 > 256) {
                    flames4[int_0] = flames3[int_0];
                } else {
                    flames4[int_0] = method328(flames3[int_0], flames1[int_0], 256 - anInt78);
                }
            }
        } else {
            for (int_0 = 0; int_0 < 256; int_0++) {
                flames4[int_0] = flames1[int_0];
            }
        }

//        Rasterizer2D.Rasterizer2D_setClip(Client.canvasBottomX, 9, Client.canvasBottomX + 128, short_0 + 7);
//        leftFlameSpritePixels.drawSprite(Client.canvasBottomX, 0);
//        Rasterizer2D.Rasterizer2D_resetClip();
        int_0 = 0;
        int int_1 = flameBuffer.width * 9 + Client.canvasBottomX;

        int int_2;
        int int_3;
        int int_4;
        int int_5;
        int int_6;
        int int_7;
        int int_8;
        int int_9;
        for (int_2 = 1; int_2 < short_0 - 1; int_2++) {
            int_3 = anIntArray16[int_2] * (short_0 - int_2) / short_0;
            int_4 = int_3 + 22;
            if (int_4 < 0) {
                int_4 = 0;
            }

            int_0 += int_4;
                for (int_5 = int_4; int_5 < 128; int_5++) {
                    int_6 = flames7[int_0++];
                    if (int_6 != 0) {
                        int_7 = int_6;
                        int_8 = 256 - int_6;
                        int_6 = flames4[int_6];
                        int_9 = flameBuffer.pixels[int_1];
                        flameBuffer.pixels[int_1++] = (int_7 * (int_6 & 0xFF00) + int_8 * (int_9 & 0xFF00) & 0xFF0000) + ((int_6 & 0xFF00FF) * int_7 + (int_9 & 0xFF00FF) * int_8 & 0xFF00FF00) >> 8;
                    } else {
                        ++int_1;
                    }
                }
            int_1 += int_4 + flameBuffer.width - 128;
        }


//        Rasterizer2D.Rasterizer2D_setClip(Client.canvasBottomX + 765 - 128, 9, Client.canvasBottomX + 765, short_0 + 7);
//        rightFlameSpritePixels.drawSprite(Client.canvasBottomX + 382, 0);
//        Rasterizer2D.Rasterizer2D_resetClip();
        int_0 = 0;
        int_1 = flameBuffer.width * 9 + Client.canvasBottomX + 637 + 24;

        for (int_2 = 1; int_2 < short_0 - 1; int_2++) {
            int_3 = anIntArray16[int_2] * (short_0 - int_2) / short_0;
            int_4 = 103 - int_3;
            int_1 += int_3;

            for (int_5 = 0; int_5 < int_4; int_5++) {
                int_6 = flames7[int_0++];
                if (int_6 != 0) {
                    int_7 = int_6;
                    int_8 = 256 - int_6;
                    int_6 = flames4[int_6];
                    int_9 = flameBuffer.pixels[int_1];
                    flameBuffer.pixels[int_1++] = (int_7 * (int_6 & 0xFF00) + int_8 * (int_9 & 0xFF00) & 0xFF0000) + ((int_9 & 0xFF00FF) * int_8 + (int_6 & 0xFF00FF) * int_7 & 0xFF00FF00) >> 8;
                } else {
                    ++int_1;
                }
            }

            int_0 += 128 - int_4;
            int_1 += flameBuffer.width - int_4 - int_3;
        }

    }

    static int method328(int int_0, int int_1, int int_2) {
        int int_3 = 256 - int_2;
        return ((int_1 & 0xFF00FF) * int_2 + int_3 * (int_0 & 0xFF00FF) & 0xFF00FF00) + ((int_0 & 0xFF00) * int_3 + (int_1 & 0xFF00) * int_2 & 0xFF0000) >> 8;
    }
}
