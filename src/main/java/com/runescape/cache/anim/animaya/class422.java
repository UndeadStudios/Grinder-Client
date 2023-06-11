package com.runescape.cache.anim.animaya;

public class class422 {
    float field4619;
    float field4624;
    float field4615;
    float field4616;
    float field4617;
    float field4618;
    float field4613;
    float field4620;
    float field4621;
    float field4622;
    float field4614;
    float field4623;

    static {
        new class422();
    }

    class422() {
        this.method7903();
    }

    void method7903() {
        this.field4623 = 0.0F;
        this.field4614 = 0.0F;
        this.field4622 = 0.0F;
        this.field4620 = 0.0F;
        this.field4613 = 0.0F;
        this.field4618 = 0.0F;
        this.field4616 = 0.0F;
        this.field4615 = 0.0F;
        this.field4624 = 0.0F;
        this.field4621 = 1.0F;
        this.field4617 = 1.0F;
        this.field4619 = 1.0F;
    }

    void method7904(float var1) {
        float var2 = (float) Math.cos((double) var1);
        float var3 = (float) Math.sin((double) var1);
        float var4 = this.field4624;
        float var5 = this.field4617;
        float var6 = this.field4620;
        float var7 = this.field4614;
        this.field4624 = var2 * var4 - this.field4615 * var3;
        this.field4615 = var2 * this.field4615 + var4 * var3;
        this.field4617 = var2 * var5 - var3 * this.field4618;
        this.field4618 = var2 * this.field4618 + var5 * var3;
        this.field4620 = var6 * var2 - this.field4621 * var3;
        this.field4621 = var6 * var3 + this.field4621 * var2;
        this.field4614 = var2 * var7 - var3 * this.field4623;
        this.field4623 = var2 * this.field4623 + var7 * var3;
    }

    void method7905(float var1) {
        float var2 = (float) Math.cos((double) var1);
        float var3 = (float) Math.sin((double) var1);
        float var4 = this.field4619;
        float var5 = this.field4616;
        float var6 = this.field4613;
        float var7 = this.field4622;
        this.field4619 = var2 * var4 + var3 * this.field4615;
        this.field4615 = this.field4615 * var2 - var4 * var3;
        this.field4616 = var3 * this.field4618 + var5 * var2;
        this.field4618 = var2 * this.field4618 - var5 * var3;
        this.field4613 = this.field4621 * var3 + var6 * var2;
        this.field4621 = var2 * this.field4621 - var6 * var3;
        this.field4622 = this.field4623 * var3 + var2 * var7;
        this.field4623 = this.field4623 * var2 - var7 * var3;
    }

    void method7908(float var1) {
        float var2 = (float) Math.cos((double) var1);
        float var3 = (float) Math.sin((double) var1);
        float var4 = this.field4619;
        float var5 = this.field4616;
        float var6 = this.field4613;
        float var7 = this.field4622;
        this.field4619 = var2 * var4 - var3 * this.field4624;
        this.field4624 = var3 * var4 + this.field4624 * var2;
        this.field4616 = var5 * var2 - this.field4617 * var3;
        this.field4617 = var3 * var5 + var2 * this.field4617;
        this.field4613 = var6 * var2 - this.field4620 * var3;
        this.field4620 = this.field4620 * var2 + var6 * var3;
        this.field4622 = var2 * var7 - var3 * this.field4614;
        this.field4614 = var7 * var3 + this.field4614 * var2;
    }

    void method7913(float var1, float var2, float var3) {
        this.field4622 += var1;
        this.field4614 += var2;
        this.field4623 += var3;
    }

    public String toString() {
        return this.field4619 + "," + this.field4616 + "," + this.field4613 + "," + this.field4622 + "\n" + this.field4624 + "," + this.field4617 + "," + this.field4620 + "," + this.field4614 + "\n" + this.field4615 + "," + this.field4618 + "," + this.field4621 + "," + this.field4623;
    }
}
