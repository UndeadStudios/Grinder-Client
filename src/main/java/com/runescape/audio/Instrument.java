package com.runescape.audio;

import com.runescape.io.Buffer;

import java.util.Random;

/**
 * @version 1.0
 * @since 13/02/2020
 */
public class Instrument {

    static int[] Instrument_samples;
    static int[] Instrument_noise;
    static int[] Instrument_sine;
    static int[] Instrument_phases;
    static int[] Instrument_delays;
    static int[] Instrument_volumeSteps;
    static int[] Instrument_pitchSteps;
    static int[] Instrument_pitchBaseSteps;

    SoundEnvelope __m;
    SoundEnvelope __f;
    SoundEnvelope __q;
    SoundEnvelope __w;
    SoundEnvelope __o;
    SoundEnvelope __u;
    SoundEnvelope __g;
    SoundEnvelope __l;
    int[] oscillatorVolume;
    int[] oscillatorPitch;
    int[] oscillatorDelays;
    int delayTime;
    int delayDecay;
    AudioFilter filter;
    SoundEnvelope __a;
    int duration;
    int offset;

    static {
        Instrument_noise = new int['耀'];
        Random var0 = new Random(0L);

        int var1;
        for(var1 = 0; var1 < 32768; ++var1) {
            Instrument_noise[var1] = (var0.nextInt() & 2) - 1;
        }

        Instrument_sine = new int['耀'];

        for(var1 = 0; var1 < 32768; ++var1) {
            Instrument_sine[var1] = (int)(Math.sin((double)var1 / 5215.1903D) * 16384.0D);
        }

        Instrument_samples = new int[220500];
        Instrument_phases = new int[5];
        Instrument_delays = new int[5];
        Instrument_volumeSteps = new int[5];
        Instrument_pitchSteps = new int[5];
        Instrument_pitchBaseSteps = new int[5];
    }

    Instrument() {
        this.oscillatorVolume = new int[]{0, 0, 0, 0, 0};
        this.oscillatorPitch = new int[]{0, 0, 0, 0, 0};
        this.oscillatorDelays = new int[]{0, 0, 0, 0, 0};
        this.delayTime = 0;
        this.delayDecay = 100;
        this.duration = 500;
        this.offset = 0;
    }

    final int[] synthesize(int var1, int var2) {
        class212.clearIntArray(Instrument_samples, 0, var1);
        if (var2 >= 10) {
            double var3 = (double) var1 / ((double) var2 + 0.0D);
            this.__m.reset();
            this.__f.reset();
            int var5 = 0;
            int var6 = 0;
            int var7 = 0;
            if (this.__q != null) {
                this.__q.reset();
                this.__w.reset();
                var5 = (int) ((double) (this.__q.end - this.__q.start) * 32.768D / var3);
                var6 = (int) ((double) this.__q.start * 32.768D / var3);
            }

            int var8 = 0;
            int var9 = 0;
            int var10 = 0;
            if (this.__o != null) {
                this.__o.reset();
                this.__u.reset();
                var8 = (int) ((double) (this.__o.end - this.__o.start) * 32.768D / var3);
                var9 = (int) ((double) this.__o.start * 32.768D / var3);
            }

            int var11;
            for (var11 = 0; var11 < 5; ++var11) {
                if (this.oscillatorVolume[var11] != 0) {
                    Instrument_phases[var11] = 0;
                    Instrument_delays[var11] = (int) ((double) this.oscillatorDelays[var11] * var3);
                    Instrument_volumeSteps[var11] = (this.oscillatorVolume[var11] << 14) / 100;
                    Instrument_pitchSteps[var11] = (int) ((double) (this.__m.end - this.__m.start) * 32.768D * Math.pow(1.0057929410678534D, (double) this.oscillatorPitch[var11]) / var3);
                    Instrument_pitchBaseSteps[var11] = (int) ((double) this.__m.start * 32.768D / var3);
                }
            }

            int var12;
            int var13;
            int var14;
            int var15;
            for (var11 = 0; var11 < var1; ++var11) {
                var12 = this.__m.doStep(var1);
                var13 = this.__f.doStep(var1);
                if (this.__q != null) {
                    var14 = this.__q.doStep(var1);
                    var15 = this.__w.doStep(var1);
                    var12 += this.evaluateWave(var7, var15, this.__q.form) >> 1;
                    var7 = var7 + var6 + (var14 * var5 >> 16);
                }

                if (this.__o != null) {
                    var14 = this.__o.doStep(var1);
                    var15 = this.__u.doStep(var1);
                    var13 = var13 * ((this.evaluateWave(var10, var15, this.__o.form) >> 1) + 32768) >> 15;
                    var10 = var10 + var9 + (var14 * var8 >> 16);
                }

                for (var14 = 0; var14 < 5; ++var14) {
                    if (this.oscillatorVolume[var14] != 0) {
                        var15 = Instrument_delays[var14] + var11;
                        if (var15 < var1) {
                            Instrument_samples[var15] += this.evaluateWave(Instrument_phases[var14], var13 * Instrument_volumeSteps[var14] >> 15, this.__m.form);
                            Instrument_phases[var14] += (var12 * Instrument_pitchSteps[var14] >> 16) + Instrument_pitchBaseSteps[var14];
                        }
                    }
                }
            }

            int var16;
            if (this.__g != null) {
                this.__g.reset();
                this.__l.reset();
                var11 = 0;
                boolean var19 = false;
                boolean var20 = true;

                for (var14 = 0; var14 < var1; ++var14) {
                    var15 = this.__g.doStep(var1);
                    var16 = this.__l.doStep(var1);
                    if (var20) {
                        var12 = (var15 * (this.__g.end - this.__g.start) >> 8) + this.__g.start;
                    } else {
                        var12 = (var16 * (this.__g.end - this.__g.start) >> 8) + this.__g.start;
                    }

                    var11 += 256;
                    if (var11 >= var12) {
                        var11 = 0;
                        var20 = !var20;
                    }

                    if (var20) {
                        Instrument_samples[var14] = 0;
                    }
                }
            }

            if (this.delayTime > 0 && this.delayDecay > 0) {
                var11 = (int) ((double) this.delayTime * var3);

                for (var12 = var11; var12 < var1; ++var12) {
                    Instrument_samples[var12] += Instrument_samples[var12 - var11] * this.delayDecay / 100;
                }
            }

            if (this.filter.pairCounts[0] > 0 || this.filter.pairCounts[1] > 0) {
                this.__a.reset();
                var11 = this.__a.doStep(var1 + 1);
                var12 = this.filter.compute(0, (float) var11 / 65536.0F);
                var13 = this.filter.compute(1, (float) var11 / 65536.0F);
                if (var1 >= var12 + var13) {
                    var14 = 0;
                    var15 = Math.min(var13, var1 - var12);

                    int var17;
                    while (var14 < var15) {
                        var16 = (int) ((long) Instrument_samples[var14 + var12] * (long) AudioFilter.audioFrequencyRange >> 16);

                        for (var17 = 0; var17 < var12; ++var17) {
                            var16 += (int) ((long) Instrument_samples[var14 + var12 - 1 - var17] * (long) AudioFilter.coefficientRanges[0][var17] >> 16);
                        }

                        for (var17 = 0; var17 < var14; ++var17) {
                            var16 -= (int) ((long) Instrument_samples[var14 - 1 - var17] * (long) AudioFilter.coefficientRanges[1][var17] >> 16);
                        }

                        Instrument_samples[var14] = var16;
                        var11 = this.__a.doStep(var1 + 1);
                        ++var14;
                    }

                    var15 = 128;

                    while (true) {
                        if (var15 > var1 - var12) {
                            var15 = var1 - var12;
                        }

                        int var18;
                        while (var14 < var15) {
                            var17 = (int) ((long) Instrument_samples[var14 + var12] * (long) AudioFilter.audioFrequencyRange >> 16);

                            for (var18 = 0; var18 < var12; ++var18) {
                                var17 += (int) ((long) Instrument_samples[var14 + var12 - 1 - var18] * (long) AudioFilter.coefficientRanges[0][var18] >> 16);
                            }

                            for (var18 = 0; var18 < var13; ++var18) {
                                var17 -= (int) ((long) Instrument_samples[var14 - 1 - var18] * (long) AudioFilter.coefficientRanges[1][var18] >> 16);
                            }

                            Instrument_samples[var14] = var17;
                            var11 = this.__a.doStep(var1 + 1);
                            ++var14;
                        }

                        if (var14 >= var1 - var12) {
                            while (var14 < var1) {
                                var17 = 0;

                                for (var18 = var14 + var12 - var1; var18 < var12; ++var18) {
                                    var17 += (int) ((long) Instrument_samples[var14 + var12 - 1 - var18] * (long) AudioFilter.coefficientRanges[0][var18] >> 16);
                                }

                                for (var18 = 0; var18 < var13; ++var18) {
                                    var17 -= (int) ((long) Instrument_samples[var14 - 1 - var18] * (long) AudioFilter.coefficientRanges[1][var18] >> 16);
                                }

                                Instrument_samples[var14] = var17;
                                this.__a.doStep(var1 + 1);
                                ++var14;
                            }
                            break;
                        }

                        var12 = this.filter.compute(0, (float) var11 / 65536.0F);
                        var13 = this.filter.compute(1, (float) var11 / 65536.0F);
                        var15 += 128;
                    }
                }
            }

            for (var11 = 0; var11 < var1; ++var11) {
                if (Instrument_samples[var11] < -32768) {
                    Instrument_samples[var11] = -32768;
                }

                if (Instrument_samples[var11] > 32767) {
                    Instrument_samples[var11] = 32767;
                }
            }

        }
        return Instrument_samples;
    }

    final int evaluateWave(int var1, int var2, int var3) {
        return var3 == 1?((var1 & 32767) < 16384?var2:-var2):(var3 == 2?Instrument_sine[var1 & 32767] * var2 >> 14:(var3 == 3?(var2 * (var1 & 32767) >> 14) - var2:(var3 == 4?var2 * Instrument_noise[var1 / 2607 & 32767]:0)));
    }

    final void decode(Buffer var1) {
        this.__m = new SoundEnvelope();
        this.__m.decode(var1);
        this.__f = new SoundEnvelope();
        this.__f.decode(var1);
        int var2 = var1.readUnsignedByte();
        if(var2 != 0) {
            --var1.index;
            this.__q = new SoundEnvelope();
            this.__q.decode(var1);
            this.__w = new SoundEnvelope();
            this.__w.decode(var1);
        }

        var2 = var1.readUnsignedByte();
        if(var2 != 0) {
            --var1.index;
            this.__o = new SoundEnvelope();
            this.__o.decode(var1);
            this.__u = new SoundEnvelope();
            this.__u.decode(var1);
        }

        var2 = var1.readUnsignedByte();
        if(var2 != 0) {
            --var1.index;
            this.__g = new SoundEnvelope();
            this.__g.decode(var1);
            this.__l = new SoundEnvelope();
            this.__l.decode(var1);
        }

        for(int var3 = 0; var3 < 10; ++var3) {
            int var4 = var1.readUnsignedShortSmart();
            if(var4 == 0) {
                break;
            }

            this.oscillatorVolume[var3] = var4;
            this.oscillatorPitch[var3] = var1.readByteOrShort1();
            this.oscillatorDelays[var3] = var1.readUnsignedShortSmart();
        }

        this.delayTime = var1.readUnsignedShortSmart();
        this.delayDecay = var1.readUnsignedShortSmart();
        this.duration = var1.getUnsignedLEShort();
        this.offset = var1.getUnsignedLEShort();
        this.filter = new AudioFilter();
        this.__a = new SoundEnvelope();
        this.filter.decode(var1, this.__a);
    }

    @Override
    public String toString() {
        return "Instrument{" +
                "duration=" + duration +
                '}';
    }
}
