package com.runescape.audio;

import com.runescape.cache.AbstractIndexCache;
import com.runescape.io.Buffer;

import java.util.Arrays;

/**
 * @version 1.0
 * @since 13/02/2020
 */
public class SoundEffect {

    Instrument[] instruments;
    int start;
    int end;

    SoundEffect(Buffer var1) {
        this.instruments = new Instrument[10];

        for(int var2 = 0; var2 < 10; ++var2) {
            int var3 = var1.readUnsignedByte();
            if(var3 != 0) {
                --var1.index;
                this.instruments[var2] = new Instrument();
                this.instruments[var2].decode(var1);
            }
        }

        this.start = var1.getUnsignedLEShort();
        this.end = var1.getUnsignedLEShort();
    }

    public RawSound toRawSound() {
        byte[] var1 = this.mix();
        return new RawSound(22050, var1, this.start * 22050 / 1000, this.end * 22050 / 1000);
    }

    public final int __q_174() {
        int var1 = 9999999;

        int var2;
        for(var2 = 0; var2 < 10; ++var2) {
            if(this.instruments[var2] != null && this.instruments[var2].offset / 20 < var1) {
                var1 = this.instruments[var2].offset / 20;
            }
        }

        if(this.start < this.end && this.start / 20 < var1) {
            var1 = this.start / 20;
        }

        if(var1 != 9999999 && var1 != 0) {
            for(var2 = 0; var2 < 10; ++var2) {
                if(this.instruments[var2] != null) {
                    this.instruments[var2].offset -= var1 * 20;
                }
            }

            if(this.start < this.end) {
                this.start -= var1 * 20;
                this.end -= var1 * 20;
            }

            return var1;
        } else {
            return 0;
        }
    }
    final byte[] mix() {
        int var1 = 0;

        int var2;
        for(var2 = 0; var2 < 10; ++var2) {
            if(this.instruments[var2] != null && this.instruments[var2].duration + this.instruments[var2].offset > var1) {
                var1 = this.instruments[var2].duration + this.instruments[var2].offset;
            }
        }

        if(var1 == 0) {
            return new byte[0];
        } else {
            var2 = var1 * 22050 / 1000;
            byte[] var3 = new byte[var2];

            for(int var4 = 0; var4 < 10; ++var4) {
                if(this.instruments[var4] != null) {
                    int var5 = this.instruments[var4].duration * 22050 / 1000;
                    int var6 = this.instruments[var4].offset * 22050 / 1000;
                    int[] var7 = this.instruments[var4].synthesize(var5, this.instruments[var4].duration);

                    for(int var8 = 0; var8 < var5; ++var8) {
                        int var9 = (var7[var8] >> 8) + var3[var8 + var6];
                        if((var9 + 128 & -256) != 0) {
                            var9 = var9 >> 31 ^ 127;
                        }

                        var3[var8 + var6] = (byte)var9;
                    }
                }
            }

            return var3;
        }
    }
    public static SoundEffect get(AbstractIndexCache soundsCache, int fileId, int fileListId) {
        byte[] var3 = soundsCache.takeRecord(fileId, fileListId);
        return var3 == null?null:new SoundEffect(new Buffer(var3));
    }

    @Override
    public String toString() {
        return "SoundEffect{" +
                "start=" + start +
                ", end=" + end +
                ", instruments=" + Arrays.toString(instruments) +
                '}';
    }
}
