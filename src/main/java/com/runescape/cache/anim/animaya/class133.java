package com.runescape.cache.anim.animaya;

import java.util.concurrent.Callable;

class class133 implements Callable {
    final class134 this$0;
    final int start;
    final int end;
    final class126[] jobs;

    class133(class134 var1, int var2, int var3, class126[] var4) {
        this.this$0 = var1;
        this.start = var2;
        this.end = var3;
        this.jobs = var4;
    }

    public Object call() {
        for(int var1 = this.start; var1 < this.end; ++var1) {
            this.jobs[var1].call();
        }

        return null;
    }

}
