package com.runescape.cache.anim.animaya;

import com.runescape.cache.Js5;
import com.runescape.cache.Skeleton;
import com.runescape.collection.DualNode;
import com.runescape.io.Buffer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class class134 extends DualNode {
    public static int field1573 = Runtime.getRuntime().availableProcessors();
    public static ThreadPoolExecutor field1413 = new ThreadPoolExecutor(0, field1573, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(class134.field1573 * 100 + 100), new MayaThreadFactory());
    int field1574;
    public class127[][] field1568 = null;
    class127[][] field1575 = null;
    public Skeleton field1570;
    int field1567 = 0;
    boolean field1569;
    Future field1572;
    List field1571;

    // var1 = 0, var2 = 1
    public class134(int var3, boolean var4) {
        this.field1574 = var3;
        byte[] var5 = Js5.skins.getRecord(this.field1574 >> 16 & '\uffff', this.field1574 & '\uffff');
        Buffer var6 = new Buffer(var5);
        int var7 = var6.readUnsignedByte();
        int var8 = var6.readUnsignedShort();
        byte[] var9 = Js5.skeletons.getRecord(var8, 0);
        this.field1570 = new Skeleton(var8, var9);
        this.field1571 = new ArrayList();
        this.field1572 = field1413.submit(new class131(this, var6, var7));
        System.out.println("submit class131 version=" + var3);
    }

    void method3049(Buffer var1, int var2) {
        var1.readUnsignedShort();
        var1.readUnsignedShort();
        this.field1567 = var1.readUnsignedByte();
        int var3 = var1.readUnsignedShort();
        this.field1575 = new class127[this.field1570.method4377().method4357()][];
        this.field1568 = new class127[this.field1570.count][];
        class126[] var4 = new class126[var3];

        int var5;
        int var7;
        for(var5 = 0; var5 < var3; ++var5) {
            class128 var12 = class128.forId(var1.readUnsignedByte());
            var7 = var1.readShortSmart();
            class129 var13 = class129.forId(var1.readUnsignedByte());
            class127 var14 = new class127();
            var14.method2972(var1, var2);
            var4[var5] = new class126(this, var14, var12, var13, var7);
            int var10 = var12.method3006();
            class127[][] var15;
            if (var12 == class128.field1534) {
                var15 = this.field1575;
            } else {
                var15 = this.field1568;
            }

            if (var15[var7] == null) {
                var15[var7] = new class127[var10];
            }

            if (var12 == class128.field1526) {
                this.field1569 = true;
            }
        }

        var5 = var3 / field1573;
        int var6 = var3 % field1573;
        int var8 = 0;

        for(int var9 = 0; var9 < field1573; ++var9) {
            var7 = var8;
            var8 += var5;
            if (var6 > 0) {
                ++var8;
                --var6;
            }

            if (var8 == var7) {
                break;
            }

            this.field1571.add(class134.field1413.submit(new class133(this, var7, var8, var4)));
        }

    }

    public boolean method3044() {
        if (this.field1572 == null && this.field1571 == null) {
            return true;
        } else {
            if (this.field1572 != null) {
                if (!this.field1572.isDone()) {
                    return false;
                }

                this.field1572 = null;
            }

            boolean var1 = true;

            for (int var2 = 0; var2 < this.field1571.size(); ++var2) {
                if (!((Future) this.field1571.get(var2)).isDone()) {
                    var1 = false;
                } else {
                    this.field1571.remove(var2);
                    --var2;
                }
            }

            if (!var1) {
                return false;
            } else {
                this.field1571 = null;
                return true;
            }
        }
    }

    public int method3058() {
        return this.field1567;
    }

    public boolean method3043() {
        return this.field1569;
    }

    public void method3047(int var1, class124 var2, int var3, int var4) {
        class421 var5;
        synchronized (class421.field4611) {
            if (class421.field4610 == 0) {
                var5 = new class421();
            } else {
                class421.field4611[--class421.field4610].method7856();
                var5 = class421.field4611[class421.field4610];
            }
        }

        this.method3048(var5, var3, var2, var1);
        this.method3045(var5, var3, var2, var1);
        this.method3054(var5, var3, var2, var1);
        var2.method2944(var5);
        var5.method7888();
    }

    void method3048(class421 var1, int var2, class124 var3, int var4) {
        float[] var5 = var3.method2935(this.field1567);
        float var6 = var5[0];
        float var7 = var5[1];
        float var8 = var5[2];
        if (this.field1575[var2] != null) {
            class127 var9 = this.field1575[var2][0];
            class127 var10 = this.field1575[var2][1];
            class127 var11 = this.field1575[var2][2];
            if (var9 != null) {
                var6 = var9.method2974(var4);
            }

            if (var10 != null) {
                var7 = var10.method2974(var4);
            }

            if (var11 != null) {
                var8 = var11.method2974(var4);
            }
        }

        class420 var17 = method5474();
        var17.method7820(1.0F, 0.0F, 0.0F, var6);
        class420 var18 = method5474();
        var18.method7820(0.0F, 1.0F, 0.0F, var7);
        class420 var19 = method5474();
        var19.method7820(0.0F, 0.0F, 1.0F, var8);
        class420 var12 = method5474();
        var12.method7819(var19);
        var12.method7819(var17);
        var12.method7819(var18);
        class421 var13;
        synchronized (class421.field4611) {
            if (class421.field4610 == 0) {
                var13 = new class421();
            } else {
                class421.field4611[--class421.field4610].method7856();
                var13 = class421.field4611[class421.field4610];
            }
        }

        var13.method7852(var12);
        var1.method7851(var13);
        var17.method7824();
        var18.method7824();
        var19.method7824();
        var12.method7824();
        var13.method7888();
    }

    void method3054(class421 var1, int var2, class124 var3, int var4) {
        float[] var5 = var3.method2936(this.field1567);
        float var6 = var5[0];
        float var7 = var5[1];
        float var8 = var5[2];
        if (this.field1575[var2] != null) {
            class127 var9 = this.field1575[var2][3];
            class127 var10 = this.field1575[var2][4];
            class127 var11 = this.field1575[var2][5];
            if (var9 != null) {
                var6 = var9.method2974(var4);
            }

            if (var10 != null) {
                var7 = var10.method2974(var4);
            }

            if (var11 != null) {
                var8 = var11.method2974(var4);
            }
        }

        var1.field4608[12] = var6;
        var1.field4608[13] = var7;
        var1.field4608[14] = var8;
    }

    void method3045(class421 var1, int var2, class124 var3, int var4) {
        float[] var5 = var3.method2937(this.field1567);
        float var6 = var5[0];
        float var7 = var5[1];
        float var8 = var5[2];
        if (this.field1575[var2] != null) {
            class127 var9 = this.field1575[var2][6];
            class127 var10 = this.field1575[var2][7];
            class127 var11 = this.field1575[var2][8];
            if (var9 != null) {
                var6 = var9.method2974(var4);
            }

            if (var10 != null) {
                var7 = var10.method2974(var4);
            }

            if (var11 != null) {
                var8 = var11.method2974(var4);
            }
        }

        class421 var14;
        synchronized (class421.field4611) {
            if (class421.field4610 == 0) {
                var14 = new class421();
            } else {
                class421.field4611[--class421.field4610].method7856();
                var14 = class421.field4611[class421.field4610];
            }
        }

        var14.method7847(var6, var7, var8);
        var1.method7851(var14);
        var14.method7888();
    }

    public static class420 method5474() {
        synchronized(class420.field4606) {
            if (class420.field4600 == 0) {
                return new class420();
            } else {
                class420.field4606[--class420.field4600].method7818();
                return class420.field4606[class420.field4600];
            }
        }
    }

}
