package com.runescape.audio;

/**
 * @version 1.0
 * @since 13/02/2020
 */
public class RawSound extends AbstractSound {

    public int sampleRate;
    public byte[] samples;
    public int start;
    public int end;
    public boolean __o;

    public RawSound(int var1, byte[] var2, int var3, int var4) {
        this.sampleRate = var1;
        this.samples = var2;
        this.start = var3;
        this.end = var4;
    }

    public RawSound(int var1, byte[] var2, int var3, int var4, boolean var5) {
        this.sampleRate = var1;
        this.samples = var2;
        this.start = var3;
        this.end = var4;
        this.__o = var5;
    }

    public RawSound resample(Decimator decimator) {

        if(decimator == null)
            return this;

        this.samples = decimator.resample(this.samples);
        this.sampleRate = decimator.scaleRate(this.sampleRate);
        if(this.start == this.end) {
            this.start = this.end = decimator.scalePosition(this.start);
        } else {
            this.start = decimator.scalePosition(this.start);
            this.end = decimator.scalePosition(this.end);
            if(this.start == this.end) {
                --this.start;
            }
        }

        return this;
    }
}
