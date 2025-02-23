package com.runescape.audio;

import com.runescape.io.Buffer;

/**
 * @version 1.0
 * @since 13/02/2020
 */
public class SoundEnvelope {

    int segments;
    int[] durations;
    int[] phases;
    int start;
    int end;
    int form;
    int ticks;
    int phaseIndex;
    int step;
    int amplitude;
    int max;

    SoundEnvelope() {
        this.segments = 2;
        this.durations = new int[2];
        this.phases = new int[2];
        this.durations[1] = 65535;
        this.phases[1] = 65535;
    }

    final void decode(Buffer var1) {
        this.form = var1.readUnsignedByte();
        this.start = var1.readInt();
        this.end = var1.readInt();
        this.decodeSegments(var1);
    }

    final void decodeSegments(Buffer var1) {
        this.segments = var1.readUnsignedByte();
        this.durations = new int[this.segments];
        this.phases = new int[this.segments];

        for(int var2 = 0; var2 < this.segments; ++var2) {
            this.durations[var2] = var1.getUnsignedLEShort();
            this.phases[var2] = var1.getUnsignedLEShort();
        }
    }

    final void reset() {
        this.ticks = 0;
        this.phaseIndex = 0;
        this.step = 0;
        this.amplitude = 0;
        this.max = 0;
    }

    final int doStep(int var1) {
        if(this.max >= this.ticks) {
            this.amplitude = this.phases[this.phaseIndex++] << 15;
            if(this.phaseIndex >= this.segments) {
                this.phaseIndex = this.segments - 1;
            }

            this.ticks = (int)((double)this.durations[this.phaseIndex] / 65536.0D * (double)var1);
            if(this.ticks > this.max) {
                this.step = ((this.phases[this.phaseIndex] << 15) - this.amplitude) / (this.ticks - this.max);
            }
        }

        this.amplitude += this.step;
        ++this.max;
        return this.amplitude - this.step >> 15;
    }
}
