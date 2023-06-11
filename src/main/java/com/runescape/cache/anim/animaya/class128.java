package com.runescape.cache.anim.animaya;

public class class128 {
    static final class128 field1523 = new class128(0, 0, (String)null, 0);
    static final class128 field1534 = new class128(1, 1, (String)null, 9);
    static final class128 field1524 = new class128(2, 2, (String)null, 3);
    static final class128 field1525 = new class128(3, 3, (String)null, 6);
    static final class128 field1526 = new class128(4, 4, (String)null, 1);
    static final class128 field1527 = new class128(5, 5, (String)null, 3);
    public static short[] field1529;
    static int field1533;
    final int field1528;
    final int field1522;
    final int field1530;

    class128(int var1, int var2, String var3, int var4) {
        this.field1528 = var1;
        this.field1522 = var2;
        this.field1530 = var4;
    }

    public int rsOrdinal() {
        return this.field1522;
    }

    int method3006() {
        return this.field1530;
    }

    static class128[] method3040() {
        return new class128[]{class128.field1523, class128.field1534, class128.field1524, class128.field1525, class128.field1526, class128.field1527};
    }

    static class128 forId(int var0) {
        class128[] values = method3040();
        for (class128 c : values) {
            if (c.rsOrdinal() == var0) {
                return c;
            }
        }
        return field1523;
    }

}
