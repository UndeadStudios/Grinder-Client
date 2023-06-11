package com.runescape.audio;

import com.runescape.collection.Node;

/**
 * @version 1.0
 * @since 13/02/2020
 */
public abstract class PcmStream extends Node {

    public volatile boolean active;
    public PcmStream after;
    public int unknownPackedStreamIndex;
    public AbstractSound sound;

    public PcmStream(){
        active = true;
    }
    public abstract PcmStream firstSubStream();
    public abstract PcmStream nextSubStream();
    public abstract int __l_171();
    public abstract void playMidiFile(int[] samples, int var2, int fullLength);
    public abstract void skipSamples(int var1);

    public int unknownLength() {
        return 255;
    }

    public final void update(int[] samples, int var2, int var3) {
        if(active) {
            playMidiFile(samples, var2, var3);
        } else {
            skipSamples(var3);
        }
    }
}
