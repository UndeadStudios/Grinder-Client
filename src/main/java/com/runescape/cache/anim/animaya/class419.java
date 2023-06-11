package com.runescape.cache.anim.animaya;

public class class419 {
    float field4597;
    float field4594;
    float field4598;

    static {
        new class419(0.0F, 0.0F, 0.0F);
        new class419(1.0F, 1.0F, 1.0F);
        new class419(1.0F, 0.0F, 0.0F);
        new class419(0.0F, 1.0F, 0.0F);
        new class419(0.0F, 0.0F, 1.0F);
    }

    class419(float var1, float var2, float var3) {
        this.field4597 = var1;
        this.field4594 = var2;
        this.field4598 = var3;
    }

    final float method7815() {
        return (float)Math.sqrt((double)(this.field4594 * this.field4594 + this.field4597 * this.field4597 + this.field4598 * this.field4598));
    }

    public String toString() {
        return this.field4597 + ", " + this.field4594 + ", " + this.field4598;
    }

}
