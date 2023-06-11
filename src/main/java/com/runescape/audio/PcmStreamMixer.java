package com.runescape.audio;

import com.runescape.collection.NodeDeque;
import com.runescape.collection.Node;

/**
 * Class used ot mix two {@link PcmStream}s together to form one.
 *
 * (Blends audio)
 *
 * @version 1.0
 * @since 13/02/2020
 */
public class PcmStreamMixer extends PcmStream {

    public static PcmStreamMixer pcmStreamMixer;

    NodeDeque subStreams;
    NodeDeque streamMixerListeners;
    int __q;
    int streamMixerValue;

    public PcmStreamMixer() {
        this.subStreams = new NodeDeque();
        this.streamMixerListeners = new NodeDeque();
        this.__q = 0;
        this.streamMixerValue = -1;
    }
    public final synchronized void addSubStream(PcmStream stream) {
        this.subStreams.addLast(stream);
    }
    public final synchronized void removeSubStream(PcmStream stream) {
        stream.remove();
    }
    void __q_168() {
        if(this.__q > 0) {
            for(PcmStreamMixerListener listener = (PcmStreamMixerListener)this.streamMixerListeners.last(); listener != null; listener = (PcmStreamMixerListener)this.streamMixerListeners.previous()) {
                listener.__m -= this.__q;
            }

            this.streamMixerValue -= this.__q;
            this.__q = 0;
        }

    }
    void __w_169(Node node, PcmStreamMixerListener listener) {
        while(this.streamMixerListeners.sentinel != node && ((PcmStreamMixerListener)node).__m <= listener.__m) {
            node = node.previous;
        }

        NodeDeque.method5270(listener, node);
        this.streamMixerValue = ((PcmStreamMixerListener)this.streamMixerListeners.sentinel.previous).__m;
    }
    void __o_170(PcmStreamMixerListener listener) {
        listener.remove();
        listener.remove2();
        Node previous = this.streamMixerListeners.sentinel.previous;
        if(previous == this.streamMixerListeners.sentinel) {
            this.streamMixerValue = -1;
        } else {
            this.streamMixerValue = ((PcmStreamMixerListener)previous).__m;
        }

    }
    @Override
    public PcmStream firstSubStream() {
        return (PcmStream)this.subStreams.last();
    }

    @Override
    public PcmStream nextSubStream() {
        return (PcmStream)this.subStreams.previous();
    }

    @Override
    public int __l_171() {
        return 0;
    }

    @Override
    public final synchronized void playMidiFile(int[] samples, int var2, int fullLength) {
        do {
            if(this.streamMixerValue < 0) {
                this.updateSubStreams(samples, var2, fullLength);
                return;
            }

            if(fullLength + this.__q < this.streamMixerValue) {
                this.__q += fullLength;
                this.updateSubStreams(samples, var2, fullLength);
                return;
            }

            int var4 = this.streamMixerValue - this.__q;
            this.updateSubStreams(samples, var2, var4);
            var2 += var4;
            fullLength -= var4;
            this.__q += var4;
            this.__q_168();
            final PcmStreamMixerListener var5 = (PcmStreamMixerListener)this.streamMixerListeners.last();
            synchronized(var5) {
                int var7 = var5.update();
                if(var7 < 0) {
                    var5.__m = 0;
                    this.__o_170(var5);
                } else {
                    var5.__m = var7;
                    this.__w_169(var5.previous, var5);
                }
            }
        } while(fullLength != 0);

    }

    @Override
    public final synchronized void skipSamples(int samples) {
        do {
            if(this.streamMixerValue < 0) {
                this.skipSubStreams(samples);
                return;
            }

            if(this.__q + samples < this.streamMixerValue) {
                this.__q += samples;
                this.skipSubStreams(samples);
                return;
            }

            int var2 = this.streamMixerValue - this.__q;
            this.skipSubStreams(var2);
            samples -= var2;
            this.__q += var2;
            this.__q_168();
            PcmStreamMixerListener var3 = (PcmStreamMixerListener)this.streamMixerListeners.last();
            synchronized(var3) {
                int var5 = var3.update();
                if(var5 < 0) {
                    var3.__m = 0;
                    this.__o_170(var3);
                } else {
                    var3.__m = var5;
                    this.__w_169(var3.previous, var3);
                }
            }
        } while(samples != 0);

    }

    void updateSubStreams(int[] samples, int var2, int var3) {
        for(PcmStream stream = (PcmStream)this.subStreams.last(); stream != null; stream = (PcmStream)this.subStreams.previous()) {
            stream.update(samples, var2, var3);
        }
    }

    void skipSubStreams(int var1) {
        for(PcmStream stream = (PcmStream)this.subStreams.last(); stream != null; stream = (PcmStream)this.subStreams.previous()) {
            stream.skipSamples(var1);
        }
    }
}
