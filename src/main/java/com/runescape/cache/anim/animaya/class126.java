package com.runescape.cache.anim.animaya;

import java.util.concurrent.Callable;

public class class126 implements Callable {
    public static short[][] field1495;
    final class127 field1494;
    final class128 field1491;
    final class129 field1493;
    final int field1492;
    final class134 this$0;

    class126(class134 var1, class127 var2, class128 var3, class129 var4, int var5) {
        this.this$0 = var1;
        this.field1494 = var2;
        this.field1491 = var3;
        this.field1493 = var4;
        this.field1492 = var5;
    }

    public Object call() {
        this.field1494.method2995();
        class127[][] var1;
        if (this.field1491 == class128.field1534) {
            var1 = this.this$0.field1575;
        } else {
            var1 = this.this$0.field1568;
        }

        var1[this.field1492][this.field1493.method3017()] = this.field1494;
        return null;
    }
}
