package com.runescape.cache.anim.animaya;

public class class129 {
    static final class129 field1557 = new class129(0, 0, (String)null, -1, -1);
    static final class129 field1536 = new class129(1, 1, (String)null, 0, 2);
    static final class129 field1539 = new class129(2, 2, (String)null, 1, 2);
    static final class129 field1538 = new class129(3, 3, (String)null, 2, 2);
    static final class129 field1550 = new class129(4, 4, (String)null, 3, 1);
    static final class129 field1540 = new class129(5, 5, (String)null, 4, 1);
    static final class129 field1541 = new class129(6, 6, (String)null, 5, 1);
    static final class129 field1542 = new class129(7, 7, (String)null, 6, 3);
    static final class129 field1543 = new class129(8, 8, (String)null, 7, 3);
    static final class129 field1544 = new class129(9, 9, (String)null, 8, 3);
    static final class129 field1545 = new class129(10, 10, (String)null, 0, 7);
    static final class129 field1546 = new class129(11, 11, (String)null, 1, 7);
    static final class129 field1547 = new class129(12, 12, (String)null, 2, 7);
    static final class129 field1548 = new class129(13, 13, (String)null, 3, 7);
    static final class129 field1549 = new class129(14, 14, (String)null, 4, 7);
    static final class129 field1535 = new class129(15, 15, (String)null, 5, 7);
    static final class129 field1551 = new class129(16, 16, (String)null, 0, 5);
    final int field1555;
    final int field1553;
    final int field1554;

    class129(int var1, int var2, String var3, int var4, int var5) {
        this.field1555 = var1;
        this.field1553 = var2;
        this.field1554 = var4;
    }

    public int rsOrdinal() {
        return this.field1553;
    }

    int method3017() {
        return this.field1554;
    }

    static class129[] method4080() {
        return new class129[]{class129.field1557, class129.field1536, class129.field1539, class129.field1538, class129.field1550, class129.field1540, class129.field1541, class129.field1542, class129.field1543, class129.field1544, class129.field1545, class129.field1546, class129.field1547, class129.field1548, class129.field1549, class129.field1535, class129.field1551};
    }

    static class129 forId(int var0) {
        class129[] values = method4080();
        for (class129 c : values) {
            if (c.rsOrdinal() == var0) {
                return c;
            }
        }
        return field1557;
    }

}
