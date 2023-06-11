package com.runescape.cache.anim.animaya;

import java.util.concurrent.ThreadFactory;

final class MayaThreadFactory implements ThreadFactory {

    public Thread newThread(Runnable var1) {
        return new Thread(var1, "OSRS Maya Anim Load");
    }

}
