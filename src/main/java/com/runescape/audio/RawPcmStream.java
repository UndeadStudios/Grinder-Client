package com.runescape.audio;

/**
 * @version 1.0
 * @since 13/02/2020
 */
public class RawPcmStream extends PcmStream {

    int __m;
    int __f;
    int __q;
    int __o;
    int __u;
    int __g;
    int __l;
    int __e;
    int start;
    int end;
    boolean __k;
    int __n;
    int __i;
    int __a;
    int __z;

    RawPcmStream(RawSound sound, int var2, int var3, int var4) {
        super.sound = sound;
        this.start = sound.start;
        this.end = sound.end;
        this.__k = sound.__o;
        this.__f = var2;
        this.__q = var3;
        this.__o = var4;
        this.__m = 0;
        this.__o_180();
    }

    RawPcmStream(RawSound sound, int var2, int var3) {
        super.sound = sound;
        this.start = sound.start;
        this.end = sound.end;
        this.__k = sound.__o;
        this.__f = var2;
        this.__q = var3;
        this.__o = 8192;
        this.__m = 0;
        this.__o_180();
    }

    void __o_180() {
        this.__u = this.__q;
        this.__g = method2603(this.__q, this.__o);
        this.__l = method2494(this.__q, this.__o);
    }

    @Override
    public PcmStream firstSubStream() {
        return null;
    }

    @Override
    public PcmStream nextSubStream() {
        return null;
    }

    @Override
    public int __l_171() {
        return this.__q == 0 && this.__n == 0?0:1;
    }

    @Override
    public int unknownLength() {
        int var1 = this.__u * 3 >> 6;
        var1 = (var1 ^ var1 >> 31) + (var1 >>> 31);
        if(this.__e == 0) {
            var1 -= var1 * this.__m / (((RawSound)super.sound).samples.length << 8);
        } else if(this.__e >= 0) {
            var1 -= var1 * this.start / ((RawSound)super.sound).samples.length;
        }

        return Math.min(var1, 255);
    }

    @Override
    public synchronized void playMidiFile(int[] samples, int trackOnset, int fullLength) {
        if(this.__q == 0 && this.__n == 0) {
            this.skipSamples(fullLength);
        } else {
            RawSound sound = (RawSound)super.sound;
            int var5 = this.start << 8;
            int var6 = this.end << 8;
            int var7 = sound.samples.length << 8;
            int var8 = var6 - var5;
            if(var8 <= 0) {
                this.__e = 0;
            }

            int trackOnset1 = trackOnset;
            fullLength += trackOnset;
            if(this.__m < 0) {
                if(this.__f <= 0) {
                    this.__b_189();
                    this.remove();
                    return;
                }

                this.__m = 0;
            }

            if(this.__m >= var7) {
                if(this.__f >= 0) {
                    this.__b_189();
                    this.remove();
                    return;
                }

                this.__m = var7 - 1;
            }

            if(this.__e < 0) {
                if(this.__k) {
                    if(this.__f < 0) {
                        trackOnset1 = this.__ap_203(samples, trackOnset, var5, fullLength, sound.samples[this.start]);
                        if(this.__m >= var5) {
                            return;
                        }

                        this.__m = var5 + var5 - 1 - this.__m;
                        this.__f = -this.__f;
                    }

                    while(true) {
                        trackOnset1 = this.__ad_202(samples, trackOnset1, var6, fullLength, sound.samples[this.end - 1]);
                        if(this.__m < var6) {
                            return;
                        }

                        this.__m = var6 + var6 - 1 - this.__m;
                        this.__f = -this.__f;
                        trackOnset1 = this.__ap_203(samples, trackOnset1, var5, fullLength, sound.samples[this.start]);
                        if(this.__m >= var5) {
                            return;
                        }

                        this.__m = var5 + var5 - 1 - this.__m;
                        this.__f = -this.__f;
                    }
                } else if(this.__f < 0) {
                    while(true) {
                        trackOnset1 = this.__ap_203(samples, trackOnset1, var5, fullLength, sound.samples[this.end - 1]);
                        if(this.__m >= var5) {
                            return;
                        }

                        this.__m = var6 - 1 - (var6 - 1 - this.__m) % var8;
                    }
                } else {
                    while(true) {
                        trackOnset1 = this.__ad_202(samples, trackOnset1, var6, fullLength, sound.samples[this.start]);
                        if(this.__m < var6) {
                            return;
                        }

                        this.__m = var5 + (this.__m - var5) % var8;
                    }
                }
            } else {
                if(this.__e > 0) {
                    if(this.__k) {
                        label140: {
                            if(this.__f < 0) {
                                trackOnset1 = this.__ap_203(samples, trackOnset, var5, fullLength, sound.samples[this.start]);
                                if(this.__m >= var5) {
                                    return;
                                }

                                this.__m = var5 + var5 - 1 - this.__m;
                                this.__f = -this.__f;
                                if(--this.__e == 0) {
                                    break label140;
                                }
                            }

                            do {
                                trackOnset1 = this.__ad_202(samples, trackOnset1, var6, fullLength, sound.samples[this.end - 1]);
                                if(this.__m < var6) {
                                    return;
                                }

                                this.__m = var6 + var6 - 1 - this.__m;
                                this.__f = -this.__f;
                                if(--this.__e == 0) {
                                    break;
                                }

                                trackOnset1 = this.__ap_203(samples, trackOnset1, var5, fullLength, sound.samples[this.start]);
                                if(this.__m >= var5) {
                                    return;
                                }

                                this.__m = var5 + var5 - 1 - this.__m;
                                this.__f = -this.__f;
                            } while(--this.__e != 0);
                        }
                    } else {
                        int var10;
                        if(this.__f < 0) {
                            while(true) {
                                trackOnset1 = this.__ap_203(samples, trackOnset1, var5, fullLength, sound.samples[this.end - 1]);
                                if(this.__m >= var5) {
                                    return;
                                }

                                var10 = (var6 - 1 - this.__m) / var8;
                                if(var10 >= this.__e) {
                                    this.__m += var8 * this.__e;
                                    this.__e = 0;
                                    break;
                                }

                                this.__m += var8 * var10;
                                this.__e -= var10;
                            }
                        } else {
                            while(true) {
                                trackOnset1 = this.__ad_202(samples, trackOnset1, var6, fullLength, sound.samples[this.start]);
                                if(this.__m < var6) {
                                    return;
                                }

                                var10 = (this.__m - var5) / var8;
                                if(var10 >= this.__e) {
                                    this.__m -= var8 * this.__e;
                                    this.__e = 0;
                                    break;
                                }

                                this.__m -= var8 * var10;
                                this.__e -= var10;
                            }
                        }
                    }
                }

                if(this.__f < 0) {
                    this.__ap_203(samples, trackOnset1, 0, fullLength, 0);
                    if(this.__m < 0) {
                        this.__m = -1;
                        this.__b_189();
                        this.remove();
                    }
                } else {
                    this.__ad_202(samples, trackOnset1, var7, fullLength, 0);
                    if(this.__m >= var7) {
                        this.__m = var7;
                        this.__b_189();
                        this.remove();
                    }
                }

            }
        }
    }

    @Override
    public synchronized void skipSamples(int var1) {
            if(this.__n > 0) {
                if(var1 >= this.__n) {
                    if(this.__q == Integer.MIN_VALUE) {
                        this.__q = 0;
                        this.__l = 0;
                        this.__g = 0;
                        this.__u = 0;
                        this.remove();
                        var1 = this.__n;
                    }

                    this.__n = 0;
                    this.__o_180();
                } else {
                    this.__u += this.__i * var1;
                    this.__g += this.__a * var1;
                    this.__l += this.__z * var1;
                    this.__n -= var1;
                }
            }

            RawSound var2 = (RawSound)super.sound;
            int var3 = this.start << 8;
            int var4 = this.end << 8;
            int var5 = var2.samples.length << 8;
            int var6 = var4 - var3;
            if(var6 <= 0) {
                this.__e = 0;
            }

            if(this.__m < 0) {
                if(this.__f <= 0) {
                    this.__b_189();
                    this.remove();
                    return;
                }

                this.__m = 0;
            }

            if(this.__m >= var5) {
                if(this.__f >= 0) {
                    this.__b_189();
                    this.remove();
                    return;
                }

                this.__m = var5 - 1;
            }

            this.__m += this.__f * var1;
            if(this.__e < 0) {
                if(!this.__k) {
                    if(this.__f < 0) {
                        if(this.__m >= var3) {
                            return;
                        }

                        this.__m = var4 - 1 - (var4 - 1 - this.__m) % var6;
                    } else {
                        if(this.__m < var4) {
                            return;
                        }

                        this.__m = var3 + (this.__m - var3) % var6;
                    }

                } else {
                    if(this.__f < 0) {
                        if(this.__m >= var3) {
                            return;
                        }

                        this.__m = var3 + var3 - 1 - this.__m;
                        this.__f = -this.__f;
                    }

                    while(this.__m >= var4) {
                        this.__m = var4 + var4 - 1 - this.__m;
                        this.__f = -this.__f;
                        if(this.__m >= var3) {
                            return;
                        }

                        this.__m = var3 + var3 - 1 - this.__m;
                        this.__f = -this.__f;
                    }

                }
            } else {
                if(this.__e > 0) {
                    if(this.__k) {
                        label123: {
                            if(this.__f < 0) {
                                if(this.__m >= var3) {
                                    return;
                                }

                                this.__m = var3 + var3 - 1 - this.__m;
                                this.__f = -this.__f;
                                if(--this.__e == 0) {
                                    break label123;
                                }
                            }

                            do {
                                if(this.__m < var4) {
                                    return;
                                }

                                this.__m = var4 + var4 - 1 - this.__m;
                                this.__f = -this.__f;
                                if(--this.__e == 0) {
                                    break;
                                }

                                if(this.__m >= var3) {
                                    return;
                                }

                                this.__m = var3 + var3 - 1 - this.__m;
                                this.__f = -this.__f;
                            } while(--this.__e != 0);
                        }
                    } else {
                        int var7;
                        if(this.__f < 0) {
                            if(this.__m >= var3) {
                                return;
                            }

                            var7 = (var4 - 1 - this.__m) / var6;
                            if(var7 < this.__e) {
                                this.__m += var6 * var7;
                                this.__e -= var7;
                                return;
                            }

                            this.__m += var6 * this.__e;
                            this.__e = 0;
                        } else {
                            if(this.__m < var4) {
                                return;
                            }

                            var7 = (this.__m - var3) / var6;
                            if(var7 < this.__e) {
                                this.__m -= var6 * var7;
                                this.__e -= var7;
                                return;
                            }

                            this.__m -= var6 * this.__e;
                            this.__e = 0;
                        }
                    }
                }

                if(this.__f < 0) {
                    if(this.__m < 0) {
                        this.__m = -1;
                        this.__b_189();
                        this.remove();
                    }
                } else if(this.__m >= var5) {
                    this.__m = var5;
                    this.__b_189();
                    this.remove();
                }

            }
        }


    public synchronized void setNumLoops(int var1) {
        this.__e = var1;
    }
    public synchronized void setVolume(int var1) {
        this.__j_184(var1 << 6, this.__t_186());
    }
    public synchronized void __z_183(int var1) {
        this.__j_184(var1, this.__t_186());
    }
    public synchronized void __j_184(int var1, int var2) {
        this.__q = var1;
        this.__o = var2;
        this.__n = 0;
        this.__o_180();
    }
    public synchronized int __s_185() {
        return this.__q == Integer.MIN_VALUE?0:this.__q;
    }
    public synchronized int __t_186() {
        return this.__o < 0?-1:this.__o;
    }
    public synchronized void __y_187(int var1) {
        int var2 = ((RawSound)super.sound).samples.length << 8;
        if(var1 < -1) {
            var1 = -1;
        }

        if(var1 > var2) {
            var1 = var2;
        }

        this.__m = var1;
    }
    public synchronized void __h_188() {
        this.__f = (this.__f ^ this.__f >> 31) + (this.__f >>> 31);
        this.__f = -this.__f;
    }
    public void __b_189() {
        if(this.__n != 0) {
            if(this.__q == Integer.MIN_VALUE) {
                this.__q = 0;
            }

            this.__n = 0;
            this.__o_180();
        }

    }
    public synchronized void __c_190(int var1, int var2) {
        this.__p_191(var1, var2, this.__t_186());
    }
    public synchronized void __p_191(int var1, int var2, int var3) {
        if(var1 == 0) {
            this.__j_184(var2, var3);
        } else {
            int var4 = method2603(var2, var3);
            int var5 = method2494(var2, var3);
            if(var4 == this.__g && var5 == this.__l) {
                this.__n = 0;
            } else {
                int var6 = var2 - this.__u;
                if(this.__u - var2 > var6) {
                    var6 = this.__u - var2;
                }

                if(var4 - this.__g > var6) {
                    var6 = var4 - this.__g;
                }

                if(this.__g - var4 > var6) {
                    var6 = this.__g - var4;
                }

                if(var5 - this.__l > var6) {
                    var6 = var5 - this.__l;
                }

                if(this.__l - var5 > var6) {
                    var6 = this.__l - var5;
                }

                if(var1 > var6) {
                    var1 = var6;
                }

                this.__n = var1;
                this.__q = var2;
                this.__o = var3;
                this.__i = (var2 - this.__u) / var1;
                this.__a = (var4 - this.__g) / var1;
                this.__z = (var5 - this.__l) / var1;
            }
        }
    }

    public synchronized void __v_192(int var1) {
        if(var1 == 0) {
            this.__z_183(0);
            this.remove();
        } else if(this.__g == 0 && this.__l == 0) {
            this.__n = 0;
            this.__q = 0;
            this.__u = 0;
            this.remove();
        } else {
            int var2 = -this.__u;
            if(this.__u > var2) {
                var2 = this.__u;
            }

            if(-this.__g > var2) {
                var2 = -this.__g;
            }

            if(this.__g > var2) {
                var2 = this.__g;
            }

            if(-this.__l > var2) {
                var2 = -this.__l;
            }

            if(this.__l > var2) {
                var2 = this.__l;
            }

            if(var1 > var2) {
                var1 = var2;
            }

            this.__n = var1;
            this.__q = Integer.MIN_VALUE;
            this.__i = -this.__u / var1;
            this.__a = -this.__g / var1;
            this.__z = -this.__l / var1;
        }
    }
    public synchronized void __ah_193(int var1) {
        if(this.__f < 0) {
            this.__f = -var1;
        } else {
            this.__f = var1;
        }

    }
    public synchronized int __ab_194() {
        return this.__f < 0?-this.__f:this.__f;
    }
    public boolean __ae_195() {
        return this.__m < 0 || this.__m >= ((RawSound)super.sound).samples.length << 8;
    }
    public boolean __at_196() {
        return this.__n != 0;
    }
    int __ad_202(int[] var1, int var2, int var3, int var4, int var5) {
        while(true) {
            if(this.__n > 0) {
                int var6 = var2 + this.__n;
                if(var6 > var4) {
                    var6 = var4;
                }

                this.__n += var2;
                if(this.__f == 256 && (this.__m & 255) == 0) {
                    if(PcmPlayer.isStereo) {
                        var2 = method2533(((RawSound)super.sound).samples, var1, this.__m, var2, this.__g, this.__l, this.__a, this.__z, var6, var3, this);
                    } else {
                        var2 = method2532(((RawSound)super.sound).samples, var1, this.__m, var2, this.__u, this.__i, var6, var3, this);
                    }
                } else if(PcmPlayer.isStereo) {
                    var2 = method2504(((RawSound)super.sound).samples, var1, this.__m, var2, this.__g, this.__l, this.__a, this.__z, var6, var3, this, this.__f, var5);
                } else {
                    var2 = method2536(((RawSound)super.sound).samples, var1, this.__m, var2, this.__u, this.__i, var6, var3, this, this.__f, var5);
                }

                this.__n -= var2;
                if(this.__n != 0) {
                    return var2;
                }

                if(this.__au_204()) {
                    continue;
                }

                return var4;
            }

            if(this.__f == 256 && (this.__m & 255) == 0) {
                if(PcmPlayer.isStereo) {
                    return method2525(((RawSound)super.sound).samples, var1, this.__m, var2, this.__g, this.__l, var4, var3, this);
                }

                return method2563(((RawSound)super.sound).samples, var1, this.__m, var2, this.__u, var4, var3, this);
            }

            if(PcmPlayer.isStereo) {
                return method2529(((RawSound)super.sound).samples, var1, this.__m, var2, this.__g, this.__l, var4, var3, this, this.__f, var5);
            }

            return method2528(((RawSound)super.sound).samples, var1, this.__m, var2, this.__u, var4, var3, this, this.__f, var5);
        }
    }
    int __ap_203(int[] var1, int var2, int var3, int var4, int var5) {
        while(true) {
            if(this.__n > 0) {
                int var6 = var2 + this.__n;
                if(var6 > var4) {
                    var6 = var4;
                }

                this.__n += var2;
                if(this.__f == -256 && (this.__m & 255) == 0) {
                    if(PcmPlayer.isStereo) {
                        var2 = method2547(((RawSound)super.sound).samples, var1, this.__m, var2, this.__g, this.__l, this.__a, this.__z, var6, var3, this);
                    } else {
                        var2 = method2534(((RawSound)super.sound).samples, var1, this.__m, var2, this.__u, this.__i, var6, var3, this);
                    }
                } else if(PcmPlayer.isStereo) {
                    var2 = method2624(((RawSound)super.sound).samples, var1, this.__m, var2, this.__g, this.__l, this.__a, this.__z, var6, var3, this, this.__f, var5);
                } else {
                    var2 = method2538(((RawSound)super.sound).samples, var1, this.__m, var2, this.__u, this.__i, var6, var3, this, this.__f, var5);
                }

                this.__n -= var2;
                if(this.__n != 0) {
                    return var2;
                }

                if(this.__au_204()) {
                    continue;
                }

                return var4;
            }

            if(this.__f == -256 && (this.__m & 255) == 0) {
                if(PcmPlayer.isStereo) {
                    return method2496(((RawSound)super.sound).samples, var1, this.__m, var2, this.__g, this.__l, var4, var3, this);
                }

                return method2636(((RawSound)super.sound).samples, var1, this.__m, var2, this.__u, var4, var3, this);
            }

            if(PcmPlayer.isStereo) {
                return method2531(((RawSound)super.sound).samples, var1, this.__m, var2, this.__g, this.__l, var4, var3, this, this.__f, var5);
            }

            return method2509(((RawSound)super.sound).samples, var1, this.__m, var2, this.__u, var4, var3, this, this.__f, var5);
        }
    }

    boolean __au_204() {
        int var1 = this.__q;
        int var2;
        int var3;
        if(var1 == Integer.MIN_VALUE) {
            var3 = 0;
            var2 = 0;
            var1 = 0;
        } else {
            var2 = method2603(var1, this.__o);
            var3 = method2494(var1, this.__o);
        }

        if(var1 == this.__u && var2 == this.__g && var3 == this.__l) {
            if(this.__q == Integer.MIN_VALUE) {
                this.__q = 0;
                this.__l = 0;
                this.__g = 0;
                this.__u = 0;
                this.remove();
                return false;
            } else {
                this.__o_180();
                return true;
            }
        } else {
            if(this.__u < var1) {
                this.__i = 1;
                this.__n = var1 - this.__u;
            } else if(this.__u > var1) {
                this.__i = -1;
                this.__n = this.__u - var1;
            } else {
                this.__i = 0;
            }

            if(this.__g < var2) {
                this.__a = 1;
                if(this.__n == 0 || this.__n > var2 - this.__g) {
                    this.__n = var2 - this.__g;
                }
            } else if(this.__g > var2) {
                this.__a = -1;
                if(this.__n == 0 || this.__n > this.__g - var2) {
                    this.__n = this.__g - var2;
                }
            } else {
                this.__a = 0;
            }

            if(this.__l < var3) {
                this.__z = 1;
                if(this.__n == 0 || this.__n > var3 - this.__l) {
                    this.__n = var3 - this.__l;
                }
            } else if(this.__l > var3) {
                this.__z = -1;
                if(this.__n == 0 || this.__n > this.__l - var3) {
                    this.__n = this.__l - var3;
                }
            } else {
                this.__z = 0;
            }

            return true;
        }
    }
    static int method2603(int var0, int var1) {
        return var1 < 0?var0:(int)((double)var0 * Math.sqrt((double)(16384 - var1) * 1.220703125E-4D) + 0.5D);
    }
    static int method2494(int var0, int var1) {
        return var1 < 0?-var0:(int)((double)var0 * Math.sqrt((double)var1 * 1.220703125E-4D) + 0.5D);
    }

    public static RawPcmStream wrap(RawSound sound, int var1, int volume) {
        return sound.samples != null && sound.samples.length != 0
                ?new RawPcmStream(sound, (int)((long)sound.sampleRate * 256L * (long)var1 / (long)(Audio.PcmPlayer_sampleRate * 100)), volume << 6)
                :null;
    }
    public static RawPcmStream wrap(RawSound sound, int var1, int var2, int var3) {
        return sound.samples != null && sound.samples.length != 0
                ?new RawPcmStream(sound, var1, var2, var3)
                :null;
    }
    static int method2563(byte[] var0, int[] var1, int var2, int var3, int var4, int var6, int var7, RawPcmStream var8) {
        var2 >>= 8;
        var7 >>= 8;
        var4 <<= 2;
        int var5;
        if((var5 = var3 + var7 - var2) > var6) {
            var5 = var6;
        }

        int var10001;
        for(var5 -= 3; var3 < var5; var1[var10001] += var0[var2++] * var4) {
            var10001 = var3++;
            var1[var10001] += var0[var2++] * var4;
            var10001 = var3++;
            var1[var10001] += var0[var2++] * var4;
            var10001 = var3++;
            var1[var10001] += var0[var2++] * var4;
            var10001 = var3++;
        }

        for(var5 += 3; var3 < var5; var1[var10001] += var0[var2++] * var4) {
            var10001 = var3++;
        }

        var8.__m = var2 << 8;
        return var3;
    }

    static int method2525(byte[] var1, int[] var2, int var3, int var4, int var5, int var6, int var8, int var9, RawPcmStream pcmStream) {
        var3 >>= 8;
        var9 >>= 8;
        var5 <<= 2;
        var6 <<= 2;
        int var7;
        if((var7 = var4 + var9 - var3) > var8) {
            var7 = var8;
        }

        var4 <<= 1;
        var7 <<= 1;

        int var10001;
        byte var11;
        for(var7 -= 6; var4 < var7; var2[var10001] += var11 * var6) {
            var11 = var1[var3++];
            var10001 = var4++;
            var2[var10001] += var11 * var5;
            var10001 = var4++;
            var2[var10001] += var11 * var6;
            var11 = var1[var3++];
            var10001 = var4++;
            var2[var10001] += var11 * var5;
            var10001 = var4++;
            var2[var10001] += var11 * var6;
            var11 = var1[var3++];
            var10001 = var4++;
            var2[var10001] += var11 * var5;
            var10001 = var4++;
            var2[var10001] += var11 * var6;
            var11 = var1[var3++];
            var10001 = var4++;
            var2[var10001] += var11 * var5;
            var10001 = var4++;
        }

        for(var7 += 6; var4 < var7; var2[var10001] += var11 * var6) {
            var11 = var1[var3++];
            var10001 = var4++;
            var2[var10001] += var11 * var5;
            var10001 = var4++;
        }

        pcmStream.__m = var3 << 8;
        return var4 >> 1;
    }

    static int method2636(byte[] var0, int[] var1, int var2, int var3, int var4, int var6, int var7, RawPcmStream var8) {
        var2 >>= 8;
        var7 >>= 8;
        var4 <<= 2;
        int var5;
        if((var5 = var3 + var2 - (var7 - 1)) > var6) {
            var5 = var6;
        }

        int var10001;
        for(var5 -= 3; var3 < var5; var1[var10001] += var0[var2--] * var4) {
            var10001 = var3++;
            var1[var10001] += var0[var2--] * var4;
            var10001 = var3++;
            var1[var10001] += var0[var2--] * var4;
            var10001 = var3++;
            var1[var10001] += var0[var2--] * var4;
            var10001 = var3++;
        }

        for(var5 += 3; var3 < var5; var1[var10001] += var0[var2--] * var4) {
            var10001 = var3++;
        }

        var8.__m = var2 << 8;
        return var3;
    }

    static int method2496(byte[] var1, int[] var2, int var3, int var4, int var5, int var6, int var8, int var9, RawPcmStream pcmStream) {
        var3 >>= 8;
        var9 >>= 8;
        var5 <<= 2;
        var6 <<= 2;
        int var7;
        if((var7 = var3 + var4 - (var9 - 1)) > var8) {
            var7 = var8;
        }

        var4 <<= 1;
        var7 <<= 1;

        int var10001;
        byte var11;
        for(var7 -= 6; var4 < var7; var2[var10001] += var11 * var6) {
            var11 = var1[var3--];
            var10001 = var4++;
            var2[var10001] += var11 * var5;
            var10001 = var4++;
            var2[var10001] += var11 * var6;
            var11 = var1[var3--];
            var10001 = var4++;
            var2[var10001] += var11 * var5;
            var10001 = var4++;
            var2[var10001] += var11 * var6;
            var11 = var1[var3--];
            var10001 = var4++;
            var2[var10001] += var11 * var5;
            var10001 = var4++;
            var2[var10001] += var11 * var6;
            var11 = var1[var3--];
            var10001 = var4++;
            var2[var10001] += var11 * var5;
            var10001 = var4++;
        }

        for(var7 += 6; var4 < var7; var2[var10001] += var11 * var6) {
            var11 = var1[var3--];
            var10001 = var4++;
            var2[var10001] += var11 * var5;
            var10001 = var4++;
        }

        pcmStream.__m = var3 << 8;
        return var4 >> 1;
    }

    static int method2528(byte[] var2, int[] var3, int var4, int var5, int var6, int var8, int var9, RawPcmStream pcmStream, int var11, int var12) {
        int var7;
        if(var11 == 0 || (var7 = var5 + (var11 + (var9 - var4) - 257) / var11) > var8) {
            var7 = var8;
        }

        byte var13;
        int var10001;
        int var1;
        while(var5 < var7) {
            var1 = var4 >> 8;
            var13 = var2[var1];
            var10001 = var5++;
            var3[var10001] += ((var13 << 8) + (var2[var1 + 1] - var13) * (var4 & 255)) * var6 >> 6;
            var4 += var11;
        }

        if(var11 == 0 || (var7 = var5 + (var11 + (var9 - var4) - 1) / var11) > var8) {
            var7 = var8;
        }

        for(var1 = var12; var5 < var7; var4 += var11) {
            var13 = var2[var4 >> 8];
            var10001 = var5++;
            var3[var10001] += ((var13 << 8) + (var1 - var13) * (var4 & 255)) * var6 >> 6;
        }

        pcmStream.__m = var4;
        return var5;
    }
    static int method2529(byte[] var2, int[] var3, int var4, int var5, int var6, int var7, int var9, int var10, RawPcmStream var11, int var12, int var13) {
        int var8;
        if(var12 == 0 || (var8 = var5 + (var10 - var4 + var12 - 257) / var12) > var9) {
            var8 = var9;
        }

        var5 <<= 1;

        byte var14;
        int var10001;
        int var1;
        int var0;
        for(var8 <<= 1; var5 < var8; var4 += var12) {
            var1 = var4 >> 8;
            var14 = var2[var1];
            var0 = (var14 << 8) + (var4 & 255) * (var2[var1 + 1] - var14);
            var10001 = var5++;
            var3[var10001] += var0 * var6 >> 6;
            var10001 = var5++;
            var3[var10001] += var0 * var7 >> 6;
        }

        if(var12 == 0 || (var8 = (var5 >> 1) + (var10 - var4 + var12 - 1) / var12) > var9) {
            var8 = var9;
        }

        var8 <<= 1;

        for(var1 = var13; var5 < var8; var4 += var12) {
            var14 = var2[var4 >> 8];
            var0 = (var14 << 8) + (var1 - var14) * (var4 & 255);
            var10001 = var5++;
            var3[var10001] += var0 * var6 >> 6;
            var10001 = var5++;
            var3[var10001] += var0 * var7 >> 6;
        }

        var11.__m = var4;
        return var5 >> 1;
    }

    static int method2509(byte[] var2, int[] var3, int var4, int var5, int var6, int var8, int var9, RawPcmStream pcmStream, int var11, int var12) {
        int var7;
        if(var11 == 0 || (var7 = var5 + (var11 + (var9 + 256 - var4)) / var11) > var8) {
            var7 = var8;
        }

        int var10001;
        int var1;
        while(var5 < var7) {
            var1 = var4 >> 8;
            byte var13 = var2[var1 - 1];
            var10001 = var5++;
            var3[var10001] += ((var13 << 8) + (var2[var1] - var13) * (var4 & 255)) * var6 >> 6;
            var4 += var11;
        }

        if(var11 == 0 || (var7 = var5 + (var11 + (var9 - var4)) / var11) > var8) {
            var7 = var8;
        }

        for(var1 = var11; var5 < var7; var4 += var1) {
            var10001 = var5++;
            var3[var10001] += ((var12 << 8) + (var2[var4 >> 8] - var12) * (var4 & 255)) * var6 >> 6;
        }

        pcmStream.__m = var4;
        return var5;
    }
    static int method2531(byte[] var2, int[] var3, int var4, int var5, int var6, int var7, int var9, int var10, RawPcmStream pcmStream, int var12, int var13) {
        int var8;
        if(var12 == 0 || (var8 = var5 + (var10 + 256 - var4 + var12) / var12) > var9) {
            var8 = var9;
        }

        var5 <<= 1;

        int var10001;
        int var0;
        int var1;
        for(var8 <<= 1; var5 < var8; var4 += var12) {
            var1 = var4 >> 8;
            byte var14 = var2[var1 - 1];
            var0 = (var2[var1] - var14) * (var4 & 255) + (var14 << 8);
            var10001 = var5++;
            var3[var10001] += var0 * var6 >> 6;
            var10001 = var5++;
            var3[var10001] += var0 * var7 >> 6;
        }

        if(var12 == 0 || (var8 = (var5 >> 1) + (var10 - var4 + var12) / var12) > var9) {
            var8 = var9;
        }

        var8 <<= 1;

        for(var1 = var13; var5 < var8; var4 += var12) {
            var0 = (var1 << 8) + (var4 & 255) * (var2[var4 >> 8] - var1);
            var10001 = var5++;
            var3[var10001] += var0 * var6 >> 6;
            var10001 = var5++;
            var3[var10001] += var0 * var7 >> 6;
        }

        pcmStream.__m = var4;
        return var5 >> 1;
    }
    static int method2532(byte[] var0, int[] var1, int var2, int var3, int var4, int var5, int var7, int var8, RawPcmStream pcmStream) {
        var2 >>= 8;
        var8 >>= 8;
        var4 <<= 2;
        var5 <<= 2;
        int var6;
        if((var6 = var3 + var8 - var2) > var7) {
            var6 = var7;
        }

        pcmStream.__g += pcmStream.__a * (var6 - var3);
        pcmStream.__l += pcmStream.__z * (var6 - var3);

        int var10001;
        for(var6 -= 3; var3 < var6; var4 += var5) {
            var10001 = var3++;
            var1[var10001] += var0[var2++] * var4;
            var4 += var5;
            var10001 = var3++;
            var1[var10001] += var0[var2++] * var4;
            var4 += var5;
            var10001 = var3++;
            var1[var10001] += var0[var2++] * var4;
            var4 += var5;
            var10001 = var3++;
            var1[var10001] += var0[var2++] * var4;
        }

        for(var6 += 3; var3 < var6; var4 += var5) {
            var10001 = var3++;
            var1[var10001] += var0[var2++] * var4;
        }

        pcmStream.__u = var4 >> 2;
        pcmStream.__m = var2 << 8;
        return var3;
    }

    static int method2533(byte[] var1, int[] var2, int var3, int var4, int var5, int var6, int var7, int var8, int var10, int var11, RawPcmStream pcmStream) {
        var3 >>= 8;
        var11 >>= 8;
        var5 <<= 2;
        var6 <<= 2;
        var7 <<= 2;
        var8 <<= 2;
        int var9;
        if((var9 = var11 + var4 - var3) > var10) {
            var9 = var10;
        }

        pcmStream.__u += pcmStream.__i * (var9 - var4);
        var4 <<= 1;
        var9 <<= 1;

        byte var13;
        int var10001;
        for(var9 -= 6; var4 < var9; var6 += var8) {
            var13 = var1[var3++];
            var10001 = var4++;
            var2[var10001] += var13 * var5;
            var5 += var7;
            var10001 = var4++;
            var2[var10001] += var13 * var6;
            var6 += var8;
            var13 = var1[var3++];
            var10001 = var4++;
            var2[var10001] += var13 * var5;
            var5 += var7;
            var10001 = var4++;
            var2[var10001] += var13 * var6;
            var6 += var8;
            var13 = var1[var3++];
            var10001 = var4++;
            var2[var10001] += var13 * var5;
            var5 += var7;
            var10001 = var4++;
            var2[var10001] += var13 * var6;
            var6 += var8;
            var13 = var1[var3++];
            var10001 = var4++;
            var2[var10001] += var13 * var5;
            var5 += var7;
            var10001 = var4++;
            var2[var10001] += var13 * var6;
        }

        for(var9 += 6; var4 < var9; var6 += var8) {
            var13 = var1[var3++];
            var10001 = var4++;
            var2[var10001] += var13 * var5;
            var5 += var7;
            var10001 = var4++;
            var2[var10001] += var13 * var6;
        }

        pcmStream.__g = var5 >> 2;
        pcmStream.__l = var6 >> 2;
        pcmStream.__m = var3 << 8;
        return var4 >> 1;
    }
    static int method2534(byte[] var0, int[] var1, int var2, int var3, int var4, int var5, int var7, int var8, RawPcmStream pcmStream) {
        var2 >>= 8;
        var8 >>= 8;
        var4 <<= 2;
        var5 <<= 2;
        int var6;
        if((var6 = var3 + var2 - (var8 - 1)) > var7) {
            var6 = var7;
        }

        pcmStream.__g += pcmStream.__a * (var6 - var3);
        pcmStream.__l += pcmStream.__z * (var6 - var3);

        int var10001;
        for(var6 -= 3; var3 < var6; var4 += var5) {
            var10001 = var3++;
            var1[var10001] += var0[var2--] * var4;
            var4 += var5;
            var10001 = var3++;
            var1[var10001] += var0[var2--] * var4;
            var4 += var5;
            var10001 = var3++;
            var1[var10001] += var0[var2--] * var4;
            var4 += var5;
            var10001 = var3++;
            var1[var10001] += var0[var2--] * var4;
        }

        for(var6 += 3; var3 < var6; var4 += var5) {
            var10001 = var3++;
            var1[var10001] += var0[var2--] * var4;
        }

        pcmStream.__u = var4 >> 2;
        pcmStream.__m = var2 << 8;
        return var3;
    }

    static int method2547(byte[] var1, int[] var2, int var3, int var4, int var5, int var6, int var7, int var8, int var10, int var11, RawPcmStream pcmStream) {
        var3 >>= 8;
        var11 >>= 8;
        var5 <<= 2;
        var6 <<= 2;
        var7 <<= 2;
        var8 <<= 2;
        int var9;
        if((var9 = var3 + var4 - (var11 - 1)) > var10) {
            var9 = var10;
        }

        pcmStream.__u += pcmStream.__i * (var9 - var4);
        var4 <<= 1;
        var9 <<= 1;

        byte var13;
        int var10001;
        for(var9 -= 6; var4 < var9; var6 += var8) {
            var13 = var1[var3--];
            var10001 = var4++;
            var2[var10001] += var13 * var5;
            var5 += var7;
            var10001 = var4++;
            var2[var10001] += var13 * var6;
            var6 += var8;
            var13 = var1[var3--];
            var10001 = var4++;
            var2[var10001] += var13 * var5;
            var5 += var7;
            var10001 = var4++;
            var2[var10001] += var13 * var6;
            var6 += var8;
            var13 = var1[var3--];
            var10001 = var4++;
            var2[var10001] += var13 * var5;
            var5 += var7;
            var10001 = var4++;
            var2[var10001] += var13 * var6;
            var6 += var8;
            var13 = var1[var3--];
            var10001 = var4++;
            var2[var10001] += var13 * var5;
            var5 += var7;
            var10001 = var4++;
            var2[var10001] += var13 * var6;
        }

        for(var9 += 6; var4 < var9; var6 += var8) {
            var13 = var1[var3--];
            var10001 = var4++;
            var2[var10001] += var13 * var5;
            var5 += var7;
            var10001 = var4++;
            var2[var10001] += var13 * var6;
        }

        pcmStream.__g = var5 >> 2;
        pcmStream.__l = var6 >> 2;
        pcmStream.__m = var3 << 8;
        return var4 >> 1;
    }
    static int method2536(byte[] var2, int[] var3, int var4, int var5, int var6, int var7, int var9, int var10, RawPcmStream pcmStream, int var12, int var13) {
        pcmStream.__g -= pcmStream.__a * var5;
        pcmStream.__l -= pcmStream.__z * var5;
        int var8;
        if(var12 == 0 || (var8 = var5 + (var10 - var4 + var12 - 257) / var12) > var9) {
            var8 = var9;
        }

        byte var14;
        int var10001;
        int var1;
        while(var5 < var8) {
            var1 = var4 >> 8;
            var14 = var2[var1];
            var10001 = var5++;
            var3[var10001] += ((var14 << 8) + (var2[var1 + 1] - var14) * (var4 & 255)) * var6 >> 6;
            var6 += var7;
            var4 += var12;
        }

        if(var12 == 0 || (var8 = var5 + (var10 - var4 + var12 - 1) / var12) > var9) {
            var8 = var9;
        }

        for(var1 = var13; var5 < var8; var4 += var12) {
            var14 = var2[var4 >> 8];
            var10001 = var5++;
            var3[var10001] += ((var14 << 8) + (var1 - var14) * (var4 & 255)) * var6 >> 6;
            var6 += var7;
        }

        pcmStream.__g += pcmStream.__a * var5;
        pcmStream.__l += pcmStream.__z * var5;
        pcmStream.__u = var6;
        pcmStream.__m = var4;
        return var5;
    }

    static int method2504(byte[] var2, int[] var3, int var4, int var5, int var6, int var7, int var8, int var9, int var11, int var12, RawPcmStream var13, int var14, int var15) {
        var13.__u -= var5 * var13.__i;
        int var10;
        if(var14 == 0 || (var10 = var5 + (var12 - var4 + var14 - 257) / var14) > var11) {
            var10 = var11;
        }

        var5 <<= 1;

        byte var16;
        int var10001;
        int var0;
        int var1;
        for(var10 <<= 1; var5 < var10; var4 += var14) {
            var1 = var4 >> 8;
            var16 = var2[var1];
            var0 = (var16 << 8) + (var4 & 255) * (var2[var1 + 1] - var16);
            var10001 = var5++;
            var3[var10001] += var0 * var6 >> 6;
            var6 += var8;
            var10001 = var5++;
            var3[var10001] += var0 * var7 >> 6;
            var7 += var9;
        }

        if(var14 == 0 || (var10 = (var5 >> 1) + (var12 - var4 + var14 - 1) / var14) > var11) {
            var10 = var11;
        }

        var10 <<= 1;

        for(var1 = var15; var5 < var10; var4 += var14) {
            var16 = var2[var4 >> 8];
            var0 = (var16 << 8) + (var1 - var16) * (var4 & 255);
            var10001 = var5++;
            var3[var10001] += var0 * var6 >> 6;
            var6 += var8;
            var10001 = var5++;
            var3[var10001] += var0 * var7 >> 6;
            var7 += var9;
        }

        var5 >>= 1;
        var13.__u += var13.__i * var5;
        var13.__g = var6;
        var13.__l = var7;
        var13.__m = var4;
        return var5;
    }
    static int method2538(byte[] var2, int[] var3, int var4, int var5, int var6, int var7, int var9, int var10, RawPcmStream pcmStream, int var12, int var13) {
        pcmStream.__g -= pcmStream.__a * var5;
        pcmStream.__l -= pcmStream.__z * var5;
        int var8;
        if(var12 == 0 || (var8 = var5 + (var10 + 256 - var4 + var12) / var12) > var9) {
            var8 = var9;
        }

        int var10001;
        int var1;
        while(var5 < var8) {
            var1 = var4 >> 8;
            byte var14 = var2[var1 - 1];
            var10001 = var5++;
            var3[var10001] += ((var14 << 8) + (var2[var1] - var14) * (var4 & 255)) * var6 >> 6;
            var6 += var7;
            var4 += var12;
        }

        if(var12 == 0 || (var8 = var5 + (var10 - var4 + var12) / var12) > var9) {
            var8 = var9;
        }

        for(var1 = var12; var5 < var8; var4 += var1) {
            var10001 = var5++;
            var3[var10001] += ((var13 << 8) + (var2[var4 >> 8] - var13) * (var4 & 255)) * var6 >> 6;
            var6 += var7;
        }

        pcmStream.__g += pcmStream.__a * var5;
        pcmStream.__l += pcmStream.__z * var5;
        pcmStream.__u = var6;
        pcmStream.__m = var4;
        return var5;
    }

    static int method2624(byte[] var2, int[] var3, int var4, int var5, int var6, int var7, int var8, int var9, int var11, int var12, RawPcmStream pcmStream, int var14, int var15) {
        pcmStream.__u -= var5 * pcmStream.__i;
        int var10;
        if(var14 == 0 || (var10 = var5 + (var12 + 256 - var4 + var14) / var14) > var11) {
            var10 = var11;
        }

        var5 <<= 1;

        int var10001;
        int var0;
        int var1;
        for(var10 <<= 1; var5 < var10; var4 += var14) {
            var1 = var4 >> 8;
            byte var16 = var2[var1 - 1];
            var0 = (var2[var1] - var16) * (var4 & 255) + (var16 << 8);
            var10001 = var5++;
            var3[var10001] += var0 * var6 >> 6;
            var6 += var8;
            var10001 = var5++;
            var3[var10001] += var0 * var7 >> 6;
            var7 += var9;
        }

        if(var14 == 0 || (var10 = (var5 >> 1) + (var12 - var4 + var14) / var14) > var11) {
            var10 = var11;
        }

        var10 <<= 1;

        for(var1 = var15; var5 < var10; var4 += var14) {
            var0 = (var1 << 8) + (var4 & 255) * (var2[var4 >> 8] - var1);
            var10001 = var5++;
            var3[var10001] += var0 * var6 >> 6;
            var6 += var8;
            var10001 = var5++;
            var3[var10001] += var0 * var7 >> 6;
            var7 += var9;
        }

        var5 >>= 1;
        pcmStream.__u += pcmStream.__i * var5;
        pcmStream.__g = var6;
        pcmStream.__l = var7;
        pcmStream.__m = var4;
        return var5;
    }
}