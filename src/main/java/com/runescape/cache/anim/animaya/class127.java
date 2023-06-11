package com.runescape.cache.anim.animaya;

import com.runescape.io.Buffer;

public class class127 {
    public static final float field1459 = Math.ulp(1.0F);
    public static final float field1457 = 2.0F * field1459;
    public static final float[] field1460 = new float[4];
    public static final float[] field1461 = new float[5];
    boolean field1508;
    boolean field1498;
    class125 field1499;
    class125 field1512;
    class122[] field1501;
    boolean field1519;
    float field1505;
    float field1504;
    float field1521;
    float field1506;
    float field1507;
    float field1500;
    float field1509;
    float field1510;
    float field1511;
    float field1520;
    boolean field1513 = true;
    int field1503 = 0;
    float[] field1515;
    int field1516;
    int field1517;
    float field1518;
    float field1514;

    class127() {
    }

    int method2972(Buffer var1, int var2) {
        int var3 = var1.readUnsignedShort();
        var1.readUnsignedByte(); // unknown
        this.field1499 = class125.forId(var1.readUnsignedByte());
        this.field1512 = class125.forId(var1.readUnsignedByte());
        this.field1508 = var1.readUnsignedByte() != 0;
        this.field1501 = new class122[var3];
        class122 var4 = null;

        for(int var5 = 0; var5 < var3; ++var5) {
            class122 var6 = new class122();
            var6.method2892(var1, var2);
            this.field1501[var5] = var6;
            if (var4 != null) {
                var4.field1451 = var6;
            }

            var4 = var6;
        }

        return var3;
    }

    void method2995() {
        this.field1516 = this.field1501[0].field1455;
        this.field1517 = this.field1501[this.method2980() - 1].field1455;
        this.field1515 = new float[this.method2977() + 1];

        for(int var1 = this.method2983(); var1 <= this.method2976(); ++var1) {
            this.field1515[var1 - this.method2983()] = method2646(this, (float)var1);
        }

        this.field1501 = null;
        this.field1518 = method2646(this, (float)(this.method2983() - 1));
        this.field1514 = method2646(this, (float)(this.method2976() + 1));
    }

    public float method2974(int var1) {
        if (var1 < this.method2983()) {
            return this.field1518;
        } else {
            return var1 > this.method2976() ? this.field1514 : this.field1515[var1 - this.method2983()];
        }
    }

    int method2983() {
        return this.field1516;
    }

    int method2976() {
        return this.field1517;
    }

    int method2977() {
        return this.method2976() - this.method2983();
    }

    int method2997(float var1) {
        if (this.field1503 < 0 || !((float)this.field1501[this.field1503].field1455 <= var1) || this.field1501[this.field1503].field1451 != null && !((float)this.field1501[this.field1503].field1451.field1455 > var1)) {
            if (!(var1 < (float)this.method2983()) && !(var1 > (float)this.method2976())) {
                int var2 = this.method2980();
                int var3 = this.field1503;
                if (var2 > 0) {
                    int var4 = 0;
                    int var5 = var2 - 1;

                    do {
                        int var6 = var4 + var5 >> 1;
                        if (var1 < (float)this.field1501[var6].field1455) {
                            if (var1 > (float)this.field1501[var6 - 1].field1455) {
                                var3 = var6 - 1;
                                break;
                            }

                            var5 = var6 - 1;
                        } else {
                            if (!(var1 > (float)this.field1501[var6].field1455)) {
                                var3 = var6;
                                break;
                            }

                            if (var1 < (float)this.field1501[var6 + 1].field1455) {
                                var3 = var6;
                                break;
                            }

                            var4 = var6 + 1;
                        }
                    } while(var4 <= var5);
                }

                if (var3 != this.field1503) {
                    this.field1503 = var3;
                    this.field1513 = true;
                }

                return this.field1503;
            } else {
                return -1;
            }
        } else {
            return this.field1503;
        }
    }

    class122 method2978(float var1) {
        int var2 = this.method2997(var1);
        return var2 >= 0 && var2 < this.field1501.length ? this.field1501[var2] : null;
    }

    int method2980() {
        return this.field1501 == null ? 0 : this.field1501.length;
    }

    static float method2646(class127 var0, float var1) {
        if (var0 != null && var0.method2980() != 0) {
            if (var1 < (float)var0.field1501[0].field1455) {
                return var0.field1499 == class125.field1490 ? var0.field1501[0].field1446 : method3541(var0, var1, true);
            } else if (var1 > (float)var0.field1501[var0.method2980() - 1].field1455) {
                return var0.field1512 == class125.field1490 ? var0.field1501[var0.method2980() - 1].field1446 : method3541(var0, var1, false);
            } else if (var0.field1498) {
                return var0.field1501[0].field1446;
            } else {
                class122 var2 = var0.method2978(var1);
                boolean var3 = false;
                boolean var4 = false;
                if (var2 == null) {
                    return 0.0F;
                } else {
                    if ((double)var2.field1449 == 0.0 && (double)var2.field1450 == 0.0) {
                        var3 = true;
                    } else if (var2.field1449 == Float.MAX_VALUE && Float.MAX_VALUE == var2.field1450) {
                        var4 = true;
                    } else if (var2.field1451 != null) {
                        if (var0.field1513) {
                            float var5 = (float)var2.field1455;
                            float var9 = var2.field1446;
                            float var6 = var2.field1449 * 0.33333334F + var5;
                            float var10 = var9 + 0.33333334F * var2.field1450;
                            float var8 = (float)var2.field1451.field1455;
                            float var12 = var2.field1451.field1446;
                            float var7 = var8 - var2.field1451.field1447 * 0.33333334F;
                            float var11 = var12 - var2.field1451.field1448 * 0.33333334F;
                            if (var0.field1508) {
                                method3149(var0, var5, var6, var7, var8, var9, var10, var11, var12);
                            } else {
                                method3075(var0, var5, var6, var7, var8, var9, var10, var11, var12);
                            }

                            var0.field1513 = false;
                        }
                    } else {
                        var3 = true;
                    }

                    if (var3) {
                        return var2.field1446;
                    } else if (var4) {
                        return (float)var2.field1455 != var1 && var2.field1451 != null ? var2.field1451.field1446 : var2.field1446;
                    } else {
                        return var0.field1508 ? method2219(var0, var1) : method8462(var0, var1);
                    }
                }
            }
        } else {
            return 0.0F;
        }
    }

    static float method3541(class127 var0, float var1, boolean var2) {
        float var3 = 0.0F;
        if (var0 != null && var0.method2980() != 0) {
            float var4 = (float)var0.field1501[0].field1455;
            float var5 = (float)var0.field1501[var0.method2980() - 1].field1455;
            float var6 = var5 - var4;
            if (0.0 == (double)var6) {
                return var0.field1501[0].field1446;
            } else {
                float var7 = 0.0F;
                if (var1 > var5) {
                    var7 = (var1 - var5) / var6;
                } else {
                    var7 = (var1 - var4) / var6;
                }

                double var8 = (double)((int)var7);
                float var10 = Math.abs((float)((double)var7 - var8));
                float var11 = var10 * var6;
                var8 = Math.abs(var8 + 1.0);
                double var12 = var8 / 2.0;
                double var14 = (double)((int)var12);
                var10 = (float)(var12 - var14);
                float var16;
                float var17;
                if (var2) {
                    if (var0.field1499 == class125.field1486) {
                        if ((double)var10 != 0.0) {
                            var11 += var4;
                        } else {
                            var11 = var5 - var11;
                        }
                    } else if (var0.field1499 != class125.field1484 && var0.field1499 != class125.field1483) {
                        if (var0.field1499 == class125.field1489) {
                            var11 = var4 - var1;
                            var16 = var0.field1501[0].field1447;
                            var17 = var0.field1501[0].field1448;
                            var3 = var0.field1501[0].field1446;
                            if ((double)var16 != 0.0) {
                                var3 -= var11 * var17 / var16;
                            }

                            return var3;
                        }
                    } else {
                        var11 = var5 - var11;
                    }
                } else if (var0.field1512 == class125.field1486) {
                    if (0.0 != (double)var10) {
                        var11 = var5 - var11;
                    } else {
                        var11 += var4;
                    }
                } else if (var0.field1512 != class125.field1484 && var0.field1512 != class125.field1483) {
                    if (var0.field1512 == class125.field1489) {
                        var11 = var1 - var5;
                        var16 = var0.field1501[var0.method2980() - 1].field1449;
                        var17 = var0.field1501[var0.method2980() - 1].field1450;
                        var3 = var0.field1501[var0.method2980() - 1].field1446;
                        if (0.0 != (double)var16) {
                            var3 += var11 * var17 / var16;
                        }

                        return var3;
                    }
                } else {
                    var11 += var4;
                }

                var3 = method2646(var0, var11);
                float var18;
                if (var2 && var0.field1499 == class125.field1483) {
                    var18 = var0.field1501[var0.method2980() - 1].field1446 - var0.field1501[0].field1446;
                    var3 = (float)((double)var3 - var8 * (double)var18);
                } else if (!var2 && var0.field1512 == class125.field1483) {
                    var18 = var0.field1501[var0.method2980() - 1].field1446 - var0.field1501[0].field1446;
                    var3 = (float)((double)var3 + var8 * (double)var18);
                }

                return var3;
            }
        } else {
            return var3;
        }
    }

    static float method2219(class127 var0, float var1) {
        if (var0 == null) {
            return 0.0F;
        } else {
            float var2;
            if (var0.field1505 == var1) {
                var2 = 0.0F;
            } else if (var0.field1504 == var1) {
                var2 = 1.0F;
            } else {
                var2 = (var1 - var0.field1505) / (var0.field1504 - var0.field1505);
            }

            float var3;
            if (var0.field1519) {
                var3 = var2;
            } else {
                field1460[3] = var0.field1500;
                field1460[2] = var0.field1507;
                field1460[1] = var0.field1506;
                field1460[0] = var0.field1521 - var2;
                field1461[0] = 0.0F;
                field1461[1] = 0.0F;
                field1461[2] = 0.0F;
                field1461[3] = 0.0F;
                field1461[4] = 0.0F;
                int var4 = method6936(field1460, 3, 0.0F, true, 1.0F, true, field1461);
                if (var4 == 1) {
                    var3 = field1461[0];
                } else {
                    var3 = 0.0F;
                }
            }

            return var0.field1509 + (var0.field1510 + (var3 * var0.field1520 + var0.field1511) * var3) * var3;
        }
    }

    public static int method6936(float[] var0, int var1, float var2, boolean var3, float var4, boolean var5, float[] var6) {
        float var7 = 0.0F;

        for(int var8 = 0; var8 < var1 + 1; ++var8) {
            var7 += Math.abs(var0[var8]);
        }

        float var24 = (Math.abs(var2) + Math.abs(var4)) * (float)(var1 + 1) * field1459;
        if (var7 <= var24) {
            return -1;
        } else {
            float[] var9 = new float[var1 + 1];

            int var10;
            for(var10 = 0; var10 < var1 + 1; ++var10) {
                var9[var10] = var0[var10] * (1.0F / var7);
            }

            while(Math.abs(var9[var1]) < var24) {
                --var1;
            }

            var10 = 0;
            if (var1 == 0) {
                return var10;
            } else if (var1 == 1) {
                var6[0] = -var9[0] / var9[1];
                boolean var22 = var3 ? var2 < var24 + var6[0] : var2 < var6[0] - var24;
                boolean var23 = var5 ? var4 > var6[0] - var24 : var4 > var24 + var6[0];
                var10 = var22 && var23 ? 1 : 0;
                if (var10 > 0) {
                    if (var3 && var6[0] < var2) {
                        var6[0] = var2;
                    } else if (var5 && var6[0] > var4) {
                        var6[0] = var4;
                    }
                }

                return var10;
            } else {
                class423 var11 = new class423(var9, var1);
                float[] var12 = new float[var1 + 1];

                for(int var13 = 1; var13 <= var1; ++var13) {
                    var12[var13 - 1] = (float)var13 * var9[var13];
                }

                float[] var21 = new float[var1 + 1];
                int var14 = method6936(var12, var1 - 1, var2, false, var4, false, var21);
                if (var14 == -1) {
                    return 0;
                } else {
                    boolean var15 = false;
                    float var17 = 0.0F;
                    float var18 = 0.0F;
                    float var19 = 0.0F;

                    for(int var20 = 0; var20 <= var14; ++var20) {
                        if (var10 > var1) {
                            return var10;
                        }

                        float var16;
                        if (var20 == 0) {
                            var16 = var2;
                            var18 = method2665(var9, var1, var2);
                            if (Math.abs(var18) <= var24 && var3) {
                                var6[var10++] = var2;
                            }
                        } else {
                            var16 = var19;
                            var18 = var17;
                        }

                        if (var20 == var14) {
                            var19 = var4;
                            var15 = false;
                        } else {
                            var19 = var21[var20];
                        }

                        var17 = method2665(var9, var1, var19);
                        if (var15) {
                            var15 = false;
                        } else if (Math.abs(var17) < var24) {
                            if (var14 != var20 || var5) {
                                var6[var10++] = var19;
                                var15 = true;
                            }
                        } else if (var18 < 0.0F && var17 > 0.0F || var18 > 0.0F && var17 < 0.0F) {
                            var6[var10++] = method2292(var11, var16, var19, 0.0F);
                            if (var10 > 1 && var6[var10 - 2] >= var6[var10 - 1] - var24) {
                                var6[var10 - 2] = (var6[var10 - 2] + var6[var10 - 1]) * 0.5F;
                                --var10;
                            }
                        }
                    }

                    return var10;
                }
            }
        }
    }

    static float method2292(class423 var0, float var1, float var2, float var3) {
        float var4 = method2665(var0.field4625, var0.field4626, var1);
        if (Math.abs(var4) < field1459) {
            return var1;
        } else {
            float var5 = method2665(var0.field4625, var0.field4626, var2);
            if (Math.abs(var5) < field1459) {
                return var2;
            } else {
                float var6 = 0.0F;
                float var7 = 0.0F;
                float var8 = 0.0F;
                float var13 = 0.0F;
                boolean var14 = true;
                boolean var15 = false;

                do {
                    var15 = false;
                    if (var14) {
                        var6 = var1;
                        var13 = var4;
                        var7 = var2 - var1;
                        var8 = var7;
                        var14 = false;
                    }

                    if (Math.abs(var13) < Math.abs(var5)) {
                        var1 = var2;
                        var2 = var6;
                        var6 = var1;
                        var4 = var5;
                        var5 = var13;
                        var13 = var4;
                    }

                    float var16 = field1457 * Math.abs(var2) + 0.5F * var3;
                    float var17 = (var6 - var2) * 0.5F;
                    boolean var18 = Math.abs(var17) > var16 && 0.0F != var5;
                    if (var18) {
                        if (!(Math.abs(var8) < var16) && !(Math.abs(var4) <= Math.abs(var5))) {
                            float var12 = var5 / var4;
                            float var9;
                            float var10;
                            if (var1 == var6) {
                                var9 = var17 * 2.0F * var12;
                                var10 = 1.0F - var12;
                            } else {
                                var10 = var4 / var13;
                                float var11 = var5 / var13;
                                var9 = (var17 * 2.0F * var10 * (var10 - var11) - (var2 - var1) * (var11 - 1.0F)) * var12;
                                var10 = (var10 - 1.0F) * (var11 - 1.0F) * (var12 - 1.0F);
                            }

                            if ((double)var9 > 0.0) {
                                var10 = -var10;
                            } else {
                                var9 = -var9;
                            }

                            var12 = var8;
                            var8 = var7;
                            if (var9 * 2.0F < var10 * var17 * 3.0F - Math.abs(var16 * var10) && var9 < Math.abs(var10 * 0.5F * var12)) {
                                var7 = var9 / var10;
                            } else {
                                var7 = var17;
                                var8 = var17;
                            }
                        } else {
                            var7 = var17;
                            var8 = var17;
                        }

                        var1 = var2;
                        var4 = var5;
                        if (Math.abs(var7) > var16) {
                            var2 += var7;
                        } else if ((double)var17 > 0.0) {
                            var2 += var16;
                        } else {
                            var2 -= var16;
                        }

                        var5 = method2665(var0.field4625, var0.field4626, var2);
                        if ((double)(var5 * (var13 / Math.abs(var13))) > 0.0) {
                            var14 = true;
                            var15 = true;
                        } else {
                            var15 = true;
                        }
                    }
                } while(var15);

                return var2;
            }
        }
    }

    static void method3149(class127 var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
        if (var0 != null) {
            float var9 = var4 - var1;
            if (0.0 != (double)var9) {
                float var10 = var2 - var1;
                float var11 = var3 - var1;
                float[] var12 = new float[]{var10 / var9, var11 / var9};
                var0.field1519 = var12[0] == 0.33333334F && 0.6666667F == var12[1];
                float var13 = var12[0];
                float var14 = var12[1];
                if ((double)var12[0] < 0.0) {
                    var12[0] = 0.0F;
                }

                if ((double)var12[1] > 1.0) {
                    var12[1] = 1.0F;
                }

                float var15;
                if ((double)var12[0] > 1.0 || var12[1] < -1.0F) {
                    var12[1] = 1.0F - var12[1];
                    if (var12[0] < 0.0F) {
                        var12[0] = 0.0F;
                    }

                    if (var12[1] < 0.0F) {
                        var12[1] = 0.0F;
                    }

                    if (var12[0] > 1.0F || var12[1] > 1.0F) {
                        var15 = (float)(1.0 + (double)var12[1] * ((double)var12[1] - 2.0) + (double)(var12[0] * (var12[1] + (var12[0] - 2.0F))));
                        if (var15 + field1459 > 0.0F) {
                            method1778(var12);
                        }
                    }

                    var12[1] = 1.0F - var12[1];
                }

                float var10000;
                if (var13 != var12[0]) {
                    var10000 = var1 + var9 * var12[0];
                    if (0.0 != (double)var13) {
                        var6 = var5 + var12[0] * (var6 - var5) / var13;
                    }
                }

                if (var12[1] != var14) {
                    var10000 = var1 + var12[1] * var9;
                    if (1.0 != (double)var14) {
                        var7 = (float)((double)var8 - (double)(var8 - var7) * (1.0 - (double)var12[1]) / (1.0 - (double)var14));
                    }
                }

                var0.field1505 = var1;
                var0.field1504 = var4;
                var15 = var12[0];
                float var16 = var12[1];
                float var17 = var15 - 0.0F;
                float var18 = var16 - var15;
                float var19 = 1.0F - var16;
                float var20 = var18 - var17;
                var0.field1500 = var19 - var18 - var20;
                var0.field1507 = var20 + var20 + var20;
                var0.field1506 = var17 + var17 + var17;
                var0.field1521 = 0.0F;
                method8039(var5, var6, var7, var8, var0);
            }
        }
    }

    static void method1778(float[] var0) {
        if (field1459 + var0[0] < 1.3333334F) {
            float var1 = var0[0] - 2.0F;
            float var2 = var0[0] - 1.0F;
            float var3 = (float)Math.sqrt((double)(var1 * var1 - var2 * var2 * 4.0F));
            float var4 = 0.5F * (var3 + -var1);
            if (var0[1] + field1459 > var4) {
                var0[1] = var4 - field1459;
            } else {
                var4 = (-var1 - var3) * 0.5F;
                if (var0[1] < var4 + field1459) {
                    var0[1] = var4 + field1459;
                }
            }
        } else {
            var0[0] = 1.3333334F - field1459;
            var0[1] = 0.33333334F - field1459;
        }

    }

    static float method2665(float[] var0, int var1, float var2) {
        float var3 = var0[var1];

        for(int var4 = var1 - 1; var4 >= 0; --var4) {
            var3 = var0[var4] + var3 * var2;
        }

        return var3;
    }

    static float method8462(class127 var0, float var1) {
        if (var0 == null) {
            return 0.0F;
        } else {
            float var2 = var1 - var0.field1505;
            return var0.field1500 + var2 * (var2 * (var2 * var0.field1521 + var0.field1506) + var0.field1507);
        }
    }

    static void method8039(float var0, float var1, float var2, float var3, class127 var4) {
        float var5 = var1 - var0;
        float var6 = var2 - var1;
        float var7 = var3 - var2;
        float var8 = var6 - var5;
        var4.field1520 = var7 - var6 - var8;
        var4.field1511 = var8 + var8 + var8;
        var4.field1510 = var5 + var5 + var5;
        var4.field1509 = var0;
    }

    static void method3075(class127 var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
        if (var0 != null) {
            var0.field1505 = var1;
            float var9 = var4 - var1;
            float var10 = var8 - var5;
            float var11 = var2 - var1;
            float var12 = 0.0F;
            float var13 = 0.0F;
            if ((double)var11 != 0.0) {
                var12 = (var6 - var5) / var11;
            }

            var11 = var4 - var3;
            if (0.0 != (double)var11) {
                var13 = (var8 - var7) / var11;
            }

            float var14 = 1.0F / (var9 * var9);
            float var15 = var9 * var12;
            float var16 = var13 * var9;
            var0.field1521 = (var16 + var15 - var10 - var10) * var14 / var9;
            var0.field1506 = (var10 + var10 + var10 - var15 - var15 - var16) * var14;
            var0.field1507 = var12;
            var0.field1500 = var5;
        }
    }

}
