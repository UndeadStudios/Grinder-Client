package com.runescape.cache.anim.animaya;

import com.runescape.io.Buffer;

public class class124 {
    static int field1471;
    static int field1479;
    public final int field1465;
    public class124 field1474;
    float[][] field1481;
    final class421[] field1467;
    class421[] field1468;
    class421[] field1469;
    class421 field1470 = new class421();
    boolean field1464 = true;
    class421 field1472 = new class421();
    boolean field1473 = true;
    class421 field1466 = new class421();
    float[][] field1475;
    float[][] field1476;
    float[][] field1477;

    public class124(int var1, Buffer var2, boolean var3) {
        this.field1465 = var2.readShort();
        this.field1467 = new class421[var1];
        this.field1468 = new class421[this.field1467.length];
        this.field1469 = new class421[this.field1467.length];
        this.field1481 = new float[this.field1467.length][3];

        for(int var4 = 0; var4 < this.field1467.length; ++var4) {
            this.field1467[var4] = new class421(var2, var3);
            this.field1481[var4][0] = var2.method8626();
            this.field1481[var4][1] = var2.method8626();
            this.field1481[var4][2] = var2.method8626();
        }

        this.method2927();
    }

    void method2927() {
        this.field1475 = new float[this.field1467.length][3];
        this.field1476 = new float[this.field1467.length][3];
        this.field1477 = new float[this.field1467.length][3];
        class421 var1;
        synchronized(class421.field4611) {
            if (class421.field4610 == 0) {
                var1 = new class421();
            } else {
                class421.field4611[--class421.field4610].method7856();
                var1 = class421.field4611[class421.field4610];
            }
        }

        class421 var2 = var1;

        for(int var5 = 0; var5 < this.field1467.length; ++var5) {
            class421 var4 = this.method2928(var5);
            var2.method7850(var4);
            var2.method7855();
            this.field1475[var5] = var2.method7858();
            this.field1476[var5][0] = var4.field4608[12];
            this.field1476[var5][1] = var4.field4608[13];
            this.field1476[var5][2] = var4.field4608[14];
            this.field1477[var5] = var4.method7857();
        }

        var2.method7888();
    }

    class421 method2928(int var1) {
        return this.field1467[var1];
    }

    class421 method2929(int var1) {
        if (this.field1468[var1] == null) {
            this.field1468[var1] = new class421(this.method2928(var1));
            if (this.field1474 != null) {
                this.field1468[var1].method7851(this.field1474.method2929(var1));
            } else {
                this.field1468[var1].method7851(class421.field4612);
            }
        }

        return this.field1468[var1];
    }

    class421 method2930(int var1) {
        if (this.field1469[var1] == null) {
            this.field1469[var1] = new class421(this.method2929(var1));
            this.field1469[var1].method7855();
        }

        return this.field1469[var1];
    }

    void method2944(class421 var1) {
        this.field1470.method7850(var1);
        this.field1464 = true;
        this.field1473 = true;
    }

    class421 method2926() {
        return this.field1470;
    }

    class421 method2933() {
        if (this.field1464) {
            this.field1472.method7850(this.method2926());
            if (this.field1474 != null) {
                this.field1472.method7851(this.field1474.method2933());
            }

            this.field1464 = false;
        }

        return this.field1472;
    }

    public class421 method2934(int var1) {
        if (this.field1473) {
            this.field1466.method7850(this.method2930(var1));
            this.field1466.method7851(this.method2933());
            this.field1473 = false;
        }

        return this.field1466;
    }

    float[] method2935(int var1) {
        return this.field1475[var1];
    }

    float[] method2936(int var1) {
        return this.field1476[var1];
    }

    float[] method2937(int var1) {
        return this.field1477[var1];
    }
}
