package com.runescape.cache.anim.animaya;

import com.runescape.io.Buffer;

public class class220 {
    class124[] field2493;
    int field2495;

    public class220(Buffer var1, int var2) {
        this.field2493 = new class124[var2];
        this.field2495 = var1.readUnsignedByte();

        for(int var3 = 0; var3 < this.field2493.length; ++var3) {
            class124 var4 = new class124(this.field2495, var1, false);
            this.field2493[var3] = var4;
        }

        this.method4358();
    }

    void method4358() {
        class124[] var1 = this.field2493;

        for(int var2 = 0; var2 < var1.length; ++var2) {
            class124 var3 = var1[var2];
            if (var3.field1465 >= 0) {
                var3.field1474 = this.field2493[var3.field1465];
            }
        }

    }

    public int method4357() {
        return this.field2493.length;
    }

    public class124 method4360(int var1) {
        return var1 >= this.method4357() ? null : this.field2493[var1];
    }

    class124[] method4372() {
        return this.field2493;
    }

    public void method4362(class134 var1, int var2) {
        this.method4371(var1, var2, (boolean[])null, false);
    }

    public void method4371(class134 var1, int var2, boolean[] var3, boolean var4) {
        int var5 = var1.method3058();
        int var6 = 0;
        class124[] var7 = this.method4372();

        for(int var8 = 0; var8 < var7.length; ++var8) {
            class124 var9 = var7[var8];
            if (var3 == null || var4 == var3[var6]) {
                var1.method3047(var2, var9, var6, var5);
            }

            ++var6;
        }

    }
}
