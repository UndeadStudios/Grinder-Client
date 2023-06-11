package com.runescape.audio;

import com.grinder.client.util.Log;

import javax.sound.sampled.*;

/**
 * @version 1.0
 * @since 13/02/2020
 */
public class DevicePcmPlayer extends PcmPlayer {

    AudioFormat format;
    SourceDataLine line;
    int capacity2;
    byte[] byteSamples;

    @Override
    protected void init() {
        this.format = new AudioFormat((float) Audio.PcmPlayer_sampleRate, 16, PcmPlayer.isStereo?2:1, true, false);
        this.byteSamples = new byte[256 << (PcmPlayer.isStereo?2:1)];
        Log.info("Initialised");
    }

    @Override
    protected void open(int capacity) throws LineUnavailableException {
        try {
            DataLine.Info var2 = new DataLine.Info(SourceDataLine.class, this.format, capacity << (PcmPlayer.isStereo?2:1));
            this.line = (SourceDataLine) AudioSystem.getLine(var2);
            this.line.open();
            this.line.start();
            this.capacity2 = capacity;
            Log.info("Loaded data line with capacity of "+capacity);
        } catch (LineUnavailableException var5) {
            int var4 = (capacity >>> 1 & 1431655765) + (capacity & 1431655765);
            var4 = (var4 >>> 2 & 858993459) + (var4 & 858993459);
            var4 = (var4 >>> 4) + var4 & 252645135;
            var4 += var4 >>> 8;
            var4 += var4 >>> 16;
            int var3 = var4 & 255;

            if(var3 != 1) {
                this.open(method1759(capacity));
            } else {
                this.line = null;
                Log.error("DataLine could not be loaded ", var5);
                throw var5;
            }
        }
    }

    @Override
    protected int position() {
        return this.capacity2 - (this.line.available() >> (PcmPlayer.isStereo?2:1));
    }

    @Override
    protected void write() {
        int var1 = 256;
        if(PcmPlayer.isStereo) {
            var1 <<= 1;
        }

        for(int var2 = 0; var2 < var1; ++var2) {
            int var3 = super.samples[var2];
            if((var3 + 8388608 & -16777216) != 0) {
                var3 = 8388607 ^ var3 >> 31;
            }

            this.byteSamples[var2 * 2] = (byte)(var3 >> 8);
            this.byteSamples[var2 * 2 + 1] = (byte)(var3 >> 16);
        }

        this.line.write(this.byteSamples, 0, var1 << 1);
    }

    @Override
    protected void close() {
        if(this.line != null) {
            this.line.close();
            this.line = null;
        }
    }

    @Override
    protected void discard() {
        this.line.flush();
    }

    public static int method1759(int var0) {
        --var0;
        var0 |= var0 >>> 1;
        var0 |= var0 >>> 2;
        var0 |= var0 >>> 4;
        var0 |= var0 >>> 8;
        var0 |= var0 >>> 16;
        return var0 + 1;
    }
}
