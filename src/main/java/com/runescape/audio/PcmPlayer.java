package com.runescape.audio;

import com.runescape.clock.Time;

/**
 * @version 1.0
 * @since 13/02/2020
 */
public class PcmPlayer {

    protected static boolean isStereo;
    protected int[] samples;
    PcmStream stream0;
    int maxPositionOffset;
    long timeMs;
    int capacity;
    int frequency;
    int __y;
    long retryTimeMs;
    int __b;
    int __c;
    int nextPosition;
    long __p;
    boolean retrying;
    int remainingSamples;
    PcmStream[] unknownStreams1;
    PcmStream[] unknownStreams2;

    protected PcmPlayer() {
        this.maxPositionOffset = 32;
        this.timeMs = Time.currentTimeMillis();
        this.retryTimeMs = 0L;
        this.__b = 0;
        this.__c = 0;
        this.nextPosition = 0;
        this.__p = 0L;
        this.retrying = true;
        this.remainingSamples = 0;
        this.unknownStreams1 = new PcmStream[8];
        this.unknownStreams2 = new PcmStream[8];
    }

    protected void init() throws Exception {
    }
    protected void open(int var1) throws Exception {
    }
    protected int position() throws Exception {
        return this.capacity;
    }
    protected void write() throws Exception {
    }
    protected void close() {
    }
    protected void discard() throws Exception {
    }
    public final synchronized void setStream(PcmStream var1) {
        this.stream0 = var1;
    }

    public final synchronized void run() {
        if(this.samples != null) {

            long currentTimeMillis = Time.currentTimeMillis();

            try {
                if(0L != this.retryTimeMs) {

                    if(currentTimeMillis < this.retryTimeMs)
                        return;

                    this.open(this.capacity);
                    this.retryTimeMs = 0L;
                    this.retrying = true;
                }

                int var3 = this.position();
                if(this.nextPosition - var3 > this.__b) {
                    this.__b = this.nextPosition - var3;
                }

                int var4 = this.__y + this.frequency;
                if(var4 + 256 > 16384) {
                    var4 = 16128;
                }

                if(var4 + 256 > this.capacity) {
                    this.capacity += 1024;
                    if(this.capacity > 16384) {
                        this.capacity = 16384;
                    }

                    this.close();
                    this.open(this.capacity);
                    var3 = 0;
                    this.retrying = true;
                    if(var4 + 256 > this.capacity) {
                        var4 = this.capacity - 256;
                        this.__y = var4 - this.frequency;
                    }
                }

                while(var3 < var4) {
                    this.fill(this.samples);
                    this.write();
                    var3 += 256;
                }

                if(currentTimeMillis > this.__p) {
                    if(!this.retrying) {
                        if(this.__b == 0 && this.__c == 0) {
                            this.close();
                            this.retryTimeMs = 2000L + currentTimeMillis;
                            return;
                        }

                        this.__y = Math.min(this.__c, this.__b);
                        this.__c = this.__b;
                    } else {
                        this.retrying = false;
                    }

                    this.__b = 0;
                    this.__p = 2000L + currentTimeMillis;
                }

                this.nextPosition = var3;
            } catch (Exception var7) {
                this.close();
                this.retryTimeMs = currentTimeMillis + 2000L;
            }

            try {
                if(currentTimeMillis > this.timeMs + 500000L) {
                    currentTimeMillis = this.timeMs;
                }

                while(currentTimeMillis > 5000L + this.timeMs) {
                    this.skip();
                    this.timeMs += (long)(256000 / Audio.PcmPlayer_sampleRate);
                }
            } catch (Exception var6) {
                this.timeMs = currentTimeMillis;
            }

        }
    }
    public final void __ac_176() {
        this.retrying = true;
    }
    public final synchronized void tryDiscard() {
        this.retrying = true;

        try {
            this.discard();
        } catch (Exception var2) {
            this.close();
            this.retryTimeMs = Time.currentTimeMillis() + 2000L;
        }

    }
    public final synchronized void shutdown() {
        if(Audio.soundSystem != null) {
            boolean var1 = true;

            for(int var2 = 0; var2 < 2; ++var2) {
                if(this == Audio.soundSystem.players[var2]) {
                    Audio.soundSystem.players[var2] = null;
                }

                if(Audio.soundSystem.players[var2] != null) {
                    var1 = false;
                }
            }

            if(var1) {
                Audio.soundSystemExecutor.shutdownNow();
                Audio.soundSystemExecutor = null;
                Audio.soundSystem = null;
            }
        }

        this.close();
        this.samples = null;
    }

    final void skip() {
        this.remainingSamples -= 256;
        if(this.remainingSamples < 0) {
            this.remainingSamples = 0;
        }

        if(this.stream0 != null) {
            this.stream0.skipSamples(256);
        }
    }

    final void fill(int[] samples) {

        int length = 256;

        if(isStereo)
            length = 256 << 1;

        class212.clearIntArray(samples, 0, length);

        this.remainingSamples -= 256;

        if(this.stream0 != null && this.remainingSamples <= 0) {

            this.remainingSamples += Audio.PcmPlayer_sampleRate >> 4;

            MidiPcmStream.PcmStream_disable(this.stream0);

            this.setUnknownStreams12(this.stream0, this.stream0.unknownLength());

            int totalPositionOffset = 0;
            int var5 = 255;

            int var6;
            PcmStream unknownStream2;
            mainLoop:
            for(var6 = 7; var5 != 0; --var6) {
                int var7;
                int var8;
                if(var6 < 0) {
                    var7 = var6 & 3;
                    var8 = -(var6 >> 2);
                } else {
                    var7 = var6;
                    var8 = 0;
                }

                for(int var9 = var5 >>> var7 & 286331153; var9 != 0; var9 >>>= 4) {
                    if((var9 & 1) != 0) {
                        var5 &= ~(1 << var7);
                        unknownStream2 = null;
                        PcmStream pcmStream = this.unknownStreams1[var7];

                        streamLoop:
                        while(true) {
                            while(true) {

                                if(pcmStream == null)
                                    break streamLoop;

                                final AbstractSound sound = pcmStream.sound;

                                if(sound != null && sound.position > var8) {
                                    var5 |= 1 << var7;
                                    unknownStream2 = pcmStream;
                                    pcmStream = pcmStream.after;
                                } else {
                                    pcmStream.active = true;
                                    int positionOffset = pcmStream.__l_171();
                                    totalPositionOffset += positionOffset;

                                    if(sound != null)
                                        sound.position += positionOffset;

                                    if(totalPositionOffset >= this.maxPositionOffset)
                                        break mainLoop;

                                    PcmStream subStream = pcmStream.firstSubStream();
                                    if(subStream != null) {
                                        for(int index = pcmStream.unknownPackedStreamIndex; subStream != null; subStream = pcmStream.nextSubStream()) {
                                            this.setUnknownStreams12(subStream, index * subStream.unknownLength() >> 8);
                                        }
                                    }

                                    final PcmStream unknownStream1 = pcmStream.after;

                                    pcmStream.after = null;

                                    if(unknownStream2 == null)
                                        this.unknownStreams1[var7] = unknownStream1;
                                    else
                                        unknownStream2.after = unknownStream1;

                                    if(unknownStream1 == null) {
                                        this.unknownStreams2[var7] = unknownStream2;
                                    }

                                    pcmStream = unknownStream1;
                                }
                            }
                        }
                    }

                    var7 += 4;
                    ++var8;
                }
            }

            for(int i = 0; i < 8; ++i) {
                PcmStream pcmStream = this.unknownStreams1[i];
                PcmStream[] unknownStreams1 = this.unknownStreams1;
                this.unknownStreams2[i] = null;

                for(unknownStreams1[i] = null; pcmStream != null; pcmStream = unknownStream2) {
                    unknownStream2 = pcmStream.after;
                    pcmStream.after = null;
                }
            }
        }

        if(this.remainingSamples < 0) {
            this.remainingSamples = 0;
        }

        if(this.stream0 != null) {
            this.stream0.playMidiFile(samples, 0, 256);
        }

        this.timeMs = Time.currentTimeMillis();
    }

    final void setUnknownStreams12(PcmStream stream, int packed) {
        int streamIndex = packed >> 5;
        PcmStream unknownStream2 = this.unknownStreams2[streamIndex];
        if(unknownStream2 == null) {
            this.unknownStreams1[streamIndex] = stream;
        } else {
            unknownStream2.after = stream;
        }

        this.unknownStreams2[streamIndex] = stream;
        stream.unknownPackedStreamIndex = packed;
    }
}
