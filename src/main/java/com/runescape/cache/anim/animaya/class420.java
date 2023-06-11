package com.runescape.cache.anim.animaya;

public final class class420 {
    static class420[] field4606 = new class420[0];
    static int field4601 = 100;
    static int field4600;
    float field4602;
    float field4604;
    float field4605;
    float field4603;

    static {
        field4606 = new class420[100];
        field4600 = 0;
        new class420();
    }

    class420() {
        this.method7818();
    }

    public void method7824() {
        synchronized(field4606) {
            if (field4600 < field4601 - 1) {
                field4606[++field4600 - 1] = this;
            }

        }
    }

    void method7822(float var1, float var2, float var3, float var4) {
        this.field4602 = var1;
        this.field4604 = var2;
        this.field4605 = var3;
        this.field4603 = var4;
    }

    public void method7820(float var1, float var2, float var3, float var4) {
        float var5 = (float)Math.sin((double)(var4 * 0.5F));
        float var6 = (float)Math.cos((double)(0.5F * var4));
        this.field4602 = var1 * var5;
        this.field4604 = var5 * var2;
        this.field4605 = var5 * var3;
        this.field4603 = var6;
    }

    final void method7818() {
        this.field4605 = 0.0F;
        this.field4604 = 0.0F;
        this.field4602 = 0.0F;
        this.field4603 = 1.0F;
    }

    public final void method7819(class420 var1) {
        this.method7822(this.field4605 * var1.field4604 + this.field4603 * var1.field4602 + this.field4602 * var1.field4603 - var1.field4605 * this.field4604, this.field4603 * var1.field4604 + (var1.field4603 * this.field4604 - this.field4605 * var1.field4602) + this.field4602 * var1.field4605, this.field4604 * var1.field4602 + this.field4605 * var1.field4603 - var1.field4604 * this.field4602 + var1.field4605 * this.field4603, this.field4603 * var1.field4603 - var1.field4602 * this.field4602 - this.field4604 * var1.field4604 - this.field4605 * var1.field4605);
    }

    public int hashCode() {
        boolean var1 = true;
        float var2 = 1.0F;
        var2 = var2 * 31.0F + this.field4602;
        var2 = var2 * 31.0F + this.field4604;
        var2 = this.field4605 + var2 * 31.0F;
        var2 = 31.0F * var2 + this.field4603;
        return (int)var2;
    }

    public String toString() {
        return this.field4602 + "," + this.field4604 + "," + this.field4605 + "," + this.field4603;
    }

    public boolean equals(Object var1) {
        if (!(var1 instanceof class420)) {
            return false;
        } else {
            class420 var2 = (class420)var1;
            return this.field4602 == var2.field4602 && this.field4604 == var2.field4604 && this.field4605 == var2.field4605 && this.field4603 == var2.field4603;
        }
    }

}
