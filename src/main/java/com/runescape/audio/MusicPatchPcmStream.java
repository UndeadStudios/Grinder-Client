package com.runescape.audio;

import com.runescape.collection.NodeDeque;

/**
 * @version 1.0
 * @since 13/02/2020
 */
public class MusicPatchPcmStream extends PcmStream {

    MidiPcmStream superStream;
    NodeDeque queue;
    PcmStreamMixer mixer;

    MusicPatchPcmStream(MidiPcmStream stream) {
        this.queue = new NodeDeque();
        this.mixer = new PcmStreamMixer();
        this.superStream = stream;
    }

    void __m_379(MusicPatchNode patchNode, int[] samples, int var3, int var4, int var5) {
        if((this.superStream.channel[patchNode.__m] & 4) != 0 && patchNode.__a < 0) {
            int var6 = this.superStream.__p[patchNode.__m] / Audio.PcmPlayer_sampleRate;

            while(true) {
                int var7 = (var6 + 1048575 - patchNode.__b) / var6;
                if(var7 > var4) {
                    patchNode.__b += var4 * var6;
                    break;
                }

                patchNode.stream.playMidiFile(samples, var3, var7);
                var3 += var7;
                var4 -= var7;
                patchNode.__b += var7 * var6 - 1048576;
                int var8 = Audio.PcmPlayer_sampleRate / 100;
                int var9 = 262144 / var6;
                if(var9 < var8) {
                    var8 = var9;
                }

                RawPcmStream var10 = patchNode.stream;
                if(this.superStream.__c[patchNode.__m] == 0) {
                    patchNode.stream = RawPcmStream.wrap(patchNode.rawSound, var10.__ab_194(), var10.__s_185(), var10.__t_186());
                } else {
                    patchNode.stream = RawPcmStream.wrap(patchNode.rawSound, var10.__ab_194(), 0, var10.__t_186());
                    this.superStream.__h_346(patchNode, patchNode.patch.__q[patchNode.__u] < 0);
                    patchNode.stream.__c_190(var8, var10.__s_185());
                }

                if(patchNode.patch.__q[patchNode.__u] < 0) {
                    patchNode.stream.setNumLoops(-1);
                }

                var10.__v_192(var8);
                var10.playMidiFile(samples, var3, var5 - var3);
                if(var10.__at_196()) {
                    this.mixer.addSubStream(var10);
                }
            }
        }

        patchNode.stream.playMidiFile(samples, var3, var4);
    }
    void __f_380(MusicPatchNode var1, int var2) {
        if((this.superStream.channel[var1.__m] & 4) != 0 && var1.__a < 0) {
            int var3 = this.superStream.__p[var1.__m] / Audio.PcmPlayer_sampleRate;
            int var4 = (var3 + 1048575 - var1.__b) / var3;
            var1.__b = var3 * var2 + var1.__b & 1048575;
            if(var4 <= var2) {
                if(this.superStream.__c[var1.__m] == 0) {
                    var1.stream = RawPcmStream.wrap(var1.rawSound, var1.stream.__ab_194(), var1.stream.__s_185(), var1.stream.__t_186());
                } else {
                    var1.stream = RawPcmStream.wrap(var1.rawSound, var1.stream.__ab_194(), 0, var1.stream.__t_186());
                    this.superStream.__h_346(var1, var1.patch.__q[var1.__u] < 0);
                }

                if(var1.patch.__q[var1.__u] < 0) {
                    var1.stream.setNumLoops(-1);
                }

                var2 = var1.__b / var3;
            }
        }

        var1.stream.skipSamples(var2);
    }

    @Override
    public PcmStream firstSubStream() {
        MusicPatchNode var1 = (MusicPatchNode)this.queue.last();
        return var1 == null?null:(var1.stream != null?var1.stream:this.nextSubStream());
    }

    @Override
    public PcmStream nextSubStream() {
        MusicPatchNode var1;
        do {
            var1 = (MusicPatchNode)this.queue.previous();
            if(var1 == null) {
                return null;
            }
        } while(var1.stream == null);

        return var1.stream;
    }

    @Override
    public int __l_171() {
        return 0;
    }

    @Override
    public void playMidiFile(int[] samples, int trackOnset, int fullLength) {
        this.mixer.playMidiFile(samples, trackOnset, fullLength);

        for(MusicPatchNode patchNode = (MusicPatchNode) queue.last(); patchNode != null; patchNode = (MusicPatchNode) queue.previous()) {
            if(!superStream.removeIfNullOnCondition(patchNode)) {
                int trackOnset1 = trackOnset;
                int fullLength1 = fullLength;

                do {
                    if(fullLength1 <= patchNode.length) {
                        this.__m_379(patchNode, samples, trackOnset1, fullLength1, fullLength1 + trackOnset1);
                        patchNode.length -= fullLength1;
                        break;
                    }

                    this.__m_379(patchNode, samples, trackOnset1, patchNode.length, fullLength1 + trackOnset1);
                    trackOnset1 += patchNode.length;
                    fullLength1 -= patchNode.length;
                } while(!superStream.completed(patchNode, samples, trackOnset1, fullLength1));
            }
        }

    }

    @Override
    public void skipSamples(int samples) {
        this.mixer.skipSamples(samples);

        for(MusicPatchNode patchNode = (MusicPatchNode)this.queue.last(); patchNode != null; patchNode = (MusicPatchNode)this.queue.previous()) {
            if(!this.superStream.removeIfNullOnCondition(patchNode)) {
                int var2 = samples;

                do {
                    if(var2 <= patchNode.length) {
                        this.__f_380(patchNode, var2);
                        patchNode.length -= var2;
                        break;
                    }

                    this.__f_380(patchNode, patchNode.length);
                    var2 -= patchNode.length;
                } while(!this.superStream.completed(patchNode, null, 0, var2));
            }
        }

    }
}
