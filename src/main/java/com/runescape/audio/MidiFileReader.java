package com.runescape.audio;

import com.runescape.io.Buffer;

/**
 * @version 1.0
 * @since 13/02/2020
 */
public class MidiFileReader {

    static final byte[] __hs_x;
    
    Buffer buffer;
    int division;
    int[] trackStarts;
    int[] trackPositions;
    int[] trackLengths;
    int[] __u;
    int lastPressedKey;
    long __e;

    static {
        __hs_x = new byte[]{(byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)0, (byte)1, (byte)2, (byte)1, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0};
    }

    public MidiFileReader(byte[] var1) {
        this.buffer = new Buffer(null);
        this.parse(var1);
    }

    MidiFileReader() {
        this.buffer = new Buffer(null);
    }
    
    void parse(byte[] data) {
        this.buffer.array = data;
        this.buffer.index = 10;
        int trackCount = this.buffer.getUnsignedLEShort();
        this.division = this.buffer.getUnsignedLEShort();
        this.lastPressedKey = 500000;
        this.trackStarts = new int[trackCount];

        int trackId;
        int length;
        for(trackId = 0; trackId < trackCount; this.buffer.index += length) {
            int type = this.buffer.readInt();
            length = this.buffer.readInt();
            if(type == 1297379947) {
                this.trackStarts[trackId] = this.buffer.index;
                ++trackId;
            }
        }

        this.__e = 0L;
        this.trackPositions = new int[trackCount];

        for(trackId = 0; trackId < trackCount; ++trackId) {
            this.trackPositions[trackId] = this.trackStarts[trackId];
        }

        this.trackLengths = new int[trackCount];
        this.__u = new int[trackCount];
    }

    void clear() {
        this.buffer.array = null;
        this.trackStarts = null;
        this.trackPositions = null;
        this.trackLengths = null;
        this.__u = null;
    }

    boolean isReady() {
        return this.buffer.array != null;
    }


    int trackCount() {
        return this.trackPositions.length;
    }
    void gotoTrack(int track) {
        this.buffer.index = this.trackPositions[track];
    }
    void markTrackPosition(int var1) {
        this.trackPositions[var1] = this.buffer.index;
    }
    void setTrackDone() {
        this.buffer.index = -1;
    }
    void readTrackLength(int var1) {
        int var2 = this.buffer.readVariableLength();
        this.trackLengths[var1] += var2;
    }

    int readMessage(int track) {
        return this.readMessage0(track);
    }

    int readMessage0(int track) {
        byte firstByte = this.buffer.array[this.buffer.index];
        int var5;
        if(firstByte < 0) {
            var5 = firstByte & 255;
            this.__u[track] = var5;
            ++this.buffer.index;
        } else {
            var5 = this.__u[track];
        }

        if(var5 != 240 && var5 != 247) {
            return this.__d_371(track, var5);
        } else {
            int deltaTime = this.buffer.readVariableLength();
            if(var5 == 247 && deltaTime > 0) {
                int var4 = this.buffer.array[this.buffer.index] & 255;
                if(var4 >= 241 && var4 <= 243 || var4 == 246 || var4 == 248 || var4 >= 250 && var4 <= 252 || var4 == 254) {
                    ++this.buffer.index;
                    this.__u[track] = var4;
                    return this.__d_371(track, var4);
                }
            }

            this.buffer.index += deltaTime;
            return 0;
        }
    }
    int __d_371(int trackId, int firstByte) {
        int deltaTime;
        if(firstByte == 255) {
            int statusByte = this.buffer.readUnsignedByte();
            deltaTime = this.buffer.readVariableLength();
            if(statusByte == 47) {
                this.buffer.index += deltaTime;
                return 1;
            } else if(statusByte == 81) {
                int pressedKey = this.buffer.readMedium();
                deltaTime -= 3;
                int trackLength = this.trackLengths[trackId];
                this.__e += (long)trackLength * (long)(this.lastPressedKey - pressedKey);
                this.lastPressedKey = pressedKey;
                this.buffer.index += deltaTime;
                return 2;
            } else {
                this.buffer.index += deltaTime;
                return 3;
            }
        } else {
            byte var3 = __hs_x[firstByte - 128];
            deltaTime = firstByte;
            if(var3 >= 1) {
                deltaTime = firstByte | this.buffer.readUnsignedByte() << 8;
            }

            if(var3 >= 2) {
                deltaTime |= this.buffer.readUnsignedByte() << 16;
            }

            return deltaTime;
        }
    }
    long __a_372(int var1) {
        return this.__e + (long)var1 * (long)this.lastPressedKey;
    }
    int getPrioritizedTrack() {
        int var1 = this.trackPositions.length;
        int var2 = -1;
        int var3 = Integer.MAX_VALUE;

        for(int var4 = 0; var4 < var1; ++var4) {
            if(this.trackPositions[var4] >= 0 && this.trackLengths[var4] < var3) {
                var2 = var4;
                var3 = this.trackLengths[var4];
            }
        }

        return var2;
    }
    boolean isDone() {

        for (int trackPosition : this.trackPositions) {
            if (trackPosition >= 0) {
                return false;
            }
        }

        return true;
    }
    void reset(long var1) {
        this.__e = var1;
        int var3 = this.trackPositions.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            this.trackLengths[var4] = 0;
            this.__u[var4] = 0;
            this.buffer.index = this.trackStarts[var4];
            this.readTrackLength(var4);
            this.trackPositions[var4] = this.buffer.index;
        }

    }

}
