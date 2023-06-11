package com.runescape.cache.anim.animaya;

import com.runescape.io.Buffer;

import java.util.concurrent.Callable;

class class131 implements Callable {
    final class134 this$0;
    final Buffer buffer;
    final int version;

    class131(class134 var1, Buffer var2, int var3) {
        this.this$0 = var1;
        this.buffer = var2;
        this.version = var3;
    }

    public Object call() {
        this.this$0.method3049(this.buffer, this.version);
        return null;
    }

}
