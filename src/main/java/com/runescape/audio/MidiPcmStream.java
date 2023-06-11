package com.runescape.audio;

import com.runescape.cache.AbstractIndexCache;
import com.runescape.cache.ByteArrayNode;
import com.runescape.collection.NodeHashTable;

/**
 * @version 1.0
 * @since 13/02/2020
 */
public class MidiPcmStream extends PcmStream {

    NodeHashTable musicPatches;
    int volume;
    int __q;
    int[] __o;
    int[] __u;
    int[] __g;
    int[] __l;
    int[] __e;
    int[] __x;
    int[] __d;
    int[] __k;
    int[] __n;
    int[] channel;
    int[] __h;
    int[] __b;
    int[] __c;
    int[] __r;
    int[] __p;
    MusicPatchNode[][] patchNodes1;
    MusicPatchNode[][] patchNodes2;
    MidiFileReader midiFile;
    boolean loopFile;
    int track;
    int trackLength;
    long __ac;
    long trackPosition;
    MusicPatchPcmStream patchStream;

    public MidiPcmStream() {
        this.volume = 256;
        this.__q = 1000000;
        this.__o = new int[16];
        this.__u = new int[16];
        this.__g = new int[16];
        this.__l = new int[16];
        this.__e = new int[16];
        this.__x = new int[16];
        this.__d = new int[16];
        this.__k = new int[16];
        this.__n = new int[16];
        this.channel = new int[16];
        this.__h = new int[16];
        this.__b = new int[16];
        this.__c = new int[16];
        this.__r = new int[16];
        this.__p = new int[16];
        this.patchNodes1 = new MusicPatchNode[16][128];
        this.patchNodes2 = new MusicPatchNode[16][128];
        this.midiFile = new MidiFileReader();
        this.patchStream = new MusicPatchPcmStream(this);
        this.musicPatches = new NodeHashTable(128);
        this.__at_354();
    }
    public synchronized void setVolume(int var1) {
        this.volume = var1;
    }

    public int getVolume() {
        return this.volume;
    }

    public synchronized boolean loadMusicTrack(MusicTrack var1, AbstractIndexCache var2, SoundCache var3, int var4) {
        var1.load();
        boolean var5 = true;
        int[] var6 = null;
        if(var4 > 0) {
            var6 = new int[]{var4};
        }

        for(ByteArrayNode var7 = (ByteArrayNode)var1.channels.first(); var7 != null; var7 = (ByteArrayNode)var1.channels.next()) {
            int var8 = (int)var7.key;
            MusicPatch var9 = (MusicPatch)this.musicPatches.get((long)var8);
            if(var9 == null) {
                var9 = load(var2, var8);
                if(var9 == null) {
                    var5 = false;
                    continue;
                }

                this.musicPatches.put(var9, (long)var8);
            }

            if(!var9.__f_373(var3, var7.byteArray, var6)) {
                var5 = false;
            }
        }

        if(var5) {
            var1.clear();
        }

        return var5;
    }

    public synchronized void clearAll() {
        for(MusicPatch var1 = (MusicPatch)this.musicPatches.first(); var1 != null; var1 = (MusicPatch)this.musicPatches.next()) {
            var1.clear();
        }

    }
    public synchronized void removeAll() {
        for(MusicPatch var1 = (MusicPatch)this.musicPatches.first(); var1 != null; var1 = (MusicPatch)this.musicPatches.next()) {
            var1.remove();
        }

    }

    @Override
    public synchronized PcmStream firstSubStream() {
        return patchStream;
    }

    @Override
    public synchronized PcmStream nextSubStream() {
        return null;
    }

    @Override
    public synchronized int __l_171() {
        return 0;
    }

    @Override
    public synchronized void playMidiFile(int[] samples, int trackOnset, int fullLength) {
        if(this.midiFile.isReady()) {
            int var4 = this.midiFile.division * this.__q / Audio.PcmPlayer_sampleRate;

            do {
                long var5 = (long)var4 * (long) fullLength + this.__ac;
                if(this.trackPosition - var5 >= 0L) {
                    this.__ac = var5;
                    break;
                }

                int trackLength = (int)(((long)var4 + (this.trackPosition - this.__ac) - 1L) / (long)var4);
                this.__ac += (long)var4 * (long)trackLength;
                this.patchStream.playMidiFile(samples, trackOnset, trackLength);
                trackOnset += trackLength;
                fullLength -= trackLength;
                this.__ai_367();
            } while(this.midiFile.isReady());
        }

        this.patchStream.playMidiFile(samples, trackOnset, fullLength);
    }

    @Override
    public synchronized void skipSamples(int sampleCount) {
        if(this.midiFile.isReady()) {
            int var2 = this.midiFile.division * this.__q / Audio.PcmPlayer_sampleRate;

            do {
                long var3 = this.__ac + (long)var2 * (long)sampleCount;
                if(this.trackPosition - var3 >= 0L) {
                    this.__ac = var3;
                    break;
                }

                int var5 = (int)(((long)var2 + (this.trackPosition - this.__ac) - 1L) / (long)var2);
                this.__ac += (long)var5 * (long)var2;
                this.patchStream.skipSamples(var5);
                sampleCount -= var5;
                this.__ai_367();
            } while(this.midiFile.isReady());
        }

        this.patchStream.skipSamples(sampleCount);
    }

    public synchronized void setMusicTrack(MusicTrack track, boolean loopMaybe) {
        this.clear();
        this.midiFile.parse(track.midi);
        this.loopFile = loopMaybe;
        this.__ac = 0L;
        int trackCount = this.midiFile.trackCount();

        for(int trackId = 0; trackId < trackCount; ++trackId) {
            this.midiFile.gotoTrack(trackId);
            this.midiFile.readTrackLength(trackId);
            this.midiFile.markTrackPosition(trackId);
        }

        this.track = this.midiFile.getPrioritizedTrack();
        this.trackLength = this.midiFile.trackLengths[this.track];
        this.trackPosition = this.midiFile.__a_372(this.trackLength);
    }

    public synchronized void clear() {
        this.midiFile.clear();
        this.__at_354();
    }

    public synchronized boolean isReady() {
        return this.midiFile.isReady();
    }
    public synchronized void __j_342(int var1, int var2) {
        this.__s_343(var1, var2);
    }
    void __s_343(int var1, int var2) {
        this.__l[var1] = var2;
        this.__x[var1] = var2 & -128;
        this.__t_344(var1, var2);
    }
    void __t_344(int var1, int var2) {
        if(var2 != this.__e[var1]) {
            this.__e[var1] = var2;

            for(int var3 = 0; var3 < 128; ++var3) {
                this.patchNodes2[var1][var3] = null;
            }
        }
    }

    void __y_345(int channelIndex, int var2, int var3) {
        this.__b_347(channelIndex, var2);
        if((this.channel[channelIndex] & 2) != 0) {
            for(MusicPatchNode node = (MusicPatchNode)this.patchStream.queue.first(); node != null; node = (MusicPatchNode)this.patchStream.queue.next()) {
                if(node.__m == channelIndex && node.__a < 0) {
                    this.patchNodes1[channelIndex][node.__u] = null;
                    this.patchNodes1[channelIndex][var2] = node;
                    int var5 = (node.__d * node.__x >> 12) + node.__e;
                    node.__e += var2 - node.__u << 8;
                    node.__x = var5 - node.__e;
                    node.__d = 4096;
                    node.__u = var2;
                    return;
                }
            }
        }

        MusicPatch var9 = (MusicPatch)this.musicPatches.get((long)this.__e[channelIndex]);
        if(var9 != null) {
            RawSound var8 = var9.rawSounds[var2];
            if(var8 != null) {
                MusicPatchNode patchNode = new MusicPatchNode();
                patchNode.__m = channelIndex;
                patchNode.patch = var9;
                patchNode.rawSound = var8;
                patchNode.patchNode2 = var9.__u[var2];
                patchNode.__o = var9.__g[var2];
                patchNode.__u = var2;
                patchNode.__g = var3 * var3 * var9.__w[var2] * var9.__m + 1024 >> 11;
                patchNode.__l = var9.__o[var2] & 255;
                patchNode.__e = (var2 << 8) - (var9.__q[var2] & 32767);
                patchNode.__k = 0;
                patchNode.__n = 0;
                patchNode.__i = 0;
                patchNode.__a = -1;
                patchNode.__z = 0;
                if(this.__c[channelIndex] == 0) {
                    patchNode.stream = RawPcmStream.wrap(var8, this.__aa_359(patchNode), this.__ax_360(patchNode), this.__af_361(patchNode));
                } else {
                    patchNode.stream = RawPcmStream.wrap(var8, this.__aa_359(patchNode), 0, this.__af_361(patchNode));
                    this.__h_346(patchNode, var9.__q[var2] < 0);
                }

                if(var9.__q[var2] < 0) {
                    patchNode.stream.setNumLoops(-1);
                }

                if(patchNode.__o >= 0) {
                    MusicPatchNode otherPatchNode = this.patchNodes2[channelIndex][patchNode.__o];
                    if(otherPatchNode != null && otherPatchNode.__a < 0) {
                        this.patchNodes1[channelIndex][otherPatchNode.__u] = null;
                        otherPatchNode.__a = 0;
                    }

                    this.patchNodes2[channelIndex][patchNode.__o] = patchNode;
                }

                this.patchStream.queue.addFirst(patchNode);
                this.patchNodes1[channelIndex][var2] = patchNode;
            }
        }
    }
    void __h_346(MusicPatchNode patchNode, boolean var2) {
        int var3 = patchNode.rawSound.samples.length;
        int var4;
        if(var2 && patchNode.rawSound.__o) {
            int var5 = var3 + var3 - patchNode.rawSound.start;
            var4 = (int)((long)var5 * (long)this.__c[patchNode.__m] >> 6);
            var3 <<= 8;
            if(var4 >= var3) {
                var4 = var3 + var3 - 1 - var4;
                patchNode.stream.__h_188();
            }
        } else {
            var4 = (int)((long)this.__c[patchNode.__m] * (long)var3 >> 6);
        }

        patchNode.stream.__y_187(var4);
    }
    void __b_347(int var1, int var2) {
        MusicPatchNode currentPatchNode = this.patchNodes1[var1][var2];
        if(currentPatchNode != null) {
            this.patchNodes1[var1][var2] = null;
            if((this.channel[var1] & 2) != 0) {
                for(MusicPatchNode patchNode = (MusicPatchNode)this.patchStream.queue.last(); patchNode != null; patchNode = (MusicPatchNode)this.patchStream.queue.previous()) {
                    if(patchNode.__m == currentPatchNode.__m && patchNode.__a < 0 && patchNode != currentPatchNode) {
                        currentPatchNode.__a = 0;
                        break;
                    }
                }
            } else {
                currentPatchNode.__a = 0;
            }

        }
    }
    void __c_348(int var1, int var2, int var3) {
    }
    void __p_349(int var1, int var2) {
    }
    void __v_350(int var1, int var2) {
        this.__d[var1] = var2;
    }
    void __ah_351(int var1) {
        for(MusicPatchNode patchNode = (MusicPatchNode)this.patchStream.queue.last(); patchNode != null; patchNode = (MusicPatchNode)this.patchStream.queue.previous()) {
            if(var1 < 0 || patchNode.__m == var1) {
                if(patchNode.stream != null) {
                    patchNode.stream.__v_192(Audio.PcmPlayer_sampleRate / 100);
                    if(patchNode.stream.__at_196()) {
                        this.patchStream.mixer.addSubStream(patchNode.stream);
                    }

                    patchNode.reset();
                }

                if(patchNode.__a < 0) {
                    this.patchNodes1[patchNode.__m][patchNode.__u] = null;
                }

                patchNode.remove();
            }
        }
    }
    void __ab_352(int var1) {
        if(var1 >= 0) {
            this.__o[var1] = 12800;
            this.__u[var1] = 8192;
            this.__g[var1] = 16383;
            this.__d[var1] = 8192;
            this.__k[var1] = 0;
            this.__n[var1] = 8192;
            this.__ad_355(var1);
            this.__ap_356(var1);
            this.channel[var1] = 0;
            this.__h[var1] = 32767;
            this.__b[var1] = 256;
            this.__c[var1] = 0;
            this.__ao_358(var1, 8192);
        } else {
            for(var1 = 0; var1 < 16; ++var1) {
                this.__ab_352(var1);
            }

        }
    }
    void __ae_353(int var1) {
        for(MusicPatchNode var2 = (MusicPatchNode)this.patchStream.queue.last(); var2 != null; var2 = (MusicPatchNode)this.patchStream.queue.previous()) {
            if((var1 < 0 || var2.__m == var1) && var2.__a < 0) {
                this.patchNodes1[var2.__m][var2.__u] = null;
                var2.__a = 0;
            }
        }
    }
    void __at_354() {
        this.__ah_351(-1);
        this.__ab_352(-1);

        int var1;
        for(var1 = 0; var1 < 16; ++var1) {
            this.__e[var1] = this.__l[var1];
        }

        for(var1 = 0; var1 < 16; ++var1) {
            this.__x[var1] = this.__l[var1] & -128;
        }

    }
    void __ad_355(int var1) {
        if((this.channel[var1] & 2) != 0) {
            for(MusicPatchNode var2 = (MusicPatchNode)this.patchStream.queue.last(); var2 != null; var2 = (MusicPatchNode)this.patchStream.queue.previous()) {
                if(var2.__m == var1 && this.patchNodes1[var1][var2.__u] == null && var2.__a < 0) {
                    var2.__a = 0;
                }
            }
        }
    }

    void __ap_356(int var1) {
        if((this.channel[var1] & 4) != 0) {
            for(MusicPatchNode var2 = (MusicPatchNode)this.patchStream.queue.last(); var2 != null; var2 = (MusicPatchNode)this.patchStream.queue.previous()) {
                if(var2.__m == var1) {
                    var2.__b = 0;
                }
            }
        }
    }

    void __au_357(int var1) {
        int responseType = var1 & 240;
        int leftOrRightChannel;
        int var4;
        int var5;
        if(responseType == 128) {
            leftOrRightChannel = var1 & 15;
            var4 = var1 >> 8 & 127;
            var5 = var1 >> 16 & 127;
            this.__b_347(leftOrRightChannel, var4);
        } else if(responseType == 144) {
            leftOrRightChannel = var1 & 15;
            var4 = var1 >> 8 & 127;
            var5 = var1 >> 16 & 127;
            if(var5 > 0) {
                this.__y_345(leftOrRightChannel, var4, var5);
            } else {
                this.__b_347(leftOrRightChannel, var4);
            }

        } else if(responseType == 160) {
            leftOrRightChannel = var1 & 15;
            var4 = var1 >> 8 & 127;
            var5 = var1 >> 16 & 127;
            this.__c_348(leftOrRightChannel, var4, var5);
        } else if(responseType == 176) {
            leftOrRightChannel = var1 & 15;
            var4 = var1 >> 8 & 127;
            var5 = var1 >> 16 & 127;
            if(var4 == 0) {
                this.__x[leftOrRightChannel] = (var5 << 14) + (this.__x[leftOrRightChannel] & -2080769);
            }

            if(var4 == 32) {
                this.__x[leftOrRightChannel] = (var5 << 7) + (this.__x[leftOrRightChannel] & -16257);
            }

            if(var4 == 1) {
                this.__k[leftOrRightChannel] = (var5 << 7) + (this.__k[leftOrRightChannel] & -16257);
            }

            if(var4 == 33) {
                this.__k[leftOrRightChannel] = var5 + (this.__k[leftOrRightChannel] & -128);
            }

            if(var4 == 5) {
                this.__n[leftOrRightChannel] = (var5 << 7) + (this.__n[leftOrRightChannel] & -16257);
            }

            if(var4 == 37) {
                this.__n[leftOrRightChannel] = var5 + (this.__n[leftOrRightChannel] & -128);
            }

            if(var4 == 7) {
                this.__o[leftOrRightChannel] = (var5 << 7) + (this.__o[leftOrRightChannel] & -16257);
            }

            if(var4 == 39) {
                this.__o[leftOrRightChannel] = var5 + (this.__o[leftOrRightChannel] & -128);
            }

            if(var4 == 10) {
                this.__u[leftOrRightChannel] = (var5 << 7) + (this.__u[leftOrRightChannel] & -16257);
            }

            if(var4 == 42) {
                this.__u[leftOrRightChannel] = var5 + (this.__u[leftOrRightChannel] & -128);
            }

            if(var4 == 11) {
                this.__g[leftOrRightChannel] = (var5 << 7) + (this.__g[leftOrRightChannel] & -16257);
            }

            if(var4 == 43) {
                this.__g[leftOrRightChannel] = var5 + (this.__g[leftOrRightChannel] & -128);
            }

            if(var4 == 64) {
                if(var5 >= 64) {
                    this.channel[leftOrRightChannel] |= 1;
                } else {
                    this.channel[leftOrRightChannel] &= -2;
                }
            }

            if(var4 == 65) {
                if(var5 >= 64) {
                    this.channel[leftOrRightChannel] |= 2;
                } else {
                    this.__ad_355(leftOrRightChannel);
                    this.channel[leftOrRightChannel] &= -3;
                }
            }

            if(var4 == 99) {
                this.__h[leftOrRightChannel] = (var5 << 7) + (this.__h[leftOrRightChannel] & 127);
            }

            if(var4 == 98) {
                this.__h[leftOrRightChannel] = (this.__h[leftOrRightChannel] & 16256) + var5;
            }

            if(var4 == 101) {
                this.__h[leftOrRightChannel] = (var5 << 7) + (this.__h[leftOrRightChannel] & 127) + 16384;
            }

            if(var4 == 100) {
                this.__h[leftOrRightChannel] = (this.__h[leftOrRightChannel] & 16256) + var5 + 16384;
            }

            if(var4 == 120) {
                this.__ah_351(leftOrRightChannel);
            }

            if(var4 == 121) {
                this.__ab_352(leftOrRightChannel);
            }

            if(var4 == 123) {
                this.__ae_353(leftOrRightChannel);
            }

            int var6;
            if(var4 == 6) {
                var6 = this.__h[leftOrRightChannel];
                if(var6 == 16384) {
                    this.__b[leftOrRightChannel] = (var5 << 7) + (this.__b[leftOrRightChannel] & -16257);
                }
            }

            if(var4 == 38) {
                var6 = this.__h[leftOrRightChannel];
                if(var6 == 16384) {
                    this.__b[leftOrRightChannel] = var5 + (this.__b[leftOrRightChannel] & -128);
                }
            }

            if(var4 == 16) {
                this.__c[leftOrRightChannel] = (var5 << 7) + (this.__c[leftOrRightChannel] & -16257);
            }

            if(var4 == 48) {
                this.__c[leftOrRightChannel] = var5 + (this.__c[leftOrRightChannel] & -128);
            }

            if(var4 == 81) {
                if(var5 >= 64) {
                    this.channel[leftOrRightChannel] |= 4;
                } else {
                    this.__ap_356(leftOrRightChannel);
                    this.channel[leftOrRightChannel] &= -5;
                }
            }

            if(var4 == 17) {
                this.__ao_358(leftOrRightChannel, (var5 << 7) + (this.__r[leftOrRightChannel] & -16257));
            }

            if(var4 == 49) {
                this.__ao_358(leftOrRightChannel, var5 + (this.__r[leftOrRightChannel] & -128));
            }

        } else if(responseType == 192) {
            leftOrRightChannel = var1 & 15;
            var4 = var1 >> 8 & 127;
            this.__t_344(leftOrRightChannel, var4 + this.__x[leftOrRightChannel]);
        } else if(responseType == 208) {
            leftOrRightChannel = var1 & 15;
            var4 = var1 >> 8 & 127;
            this.__p_349(leftOrRightChannel, var4);
        } else if(responseType == 224) {
            leftOrRightChannel = var1 & 15;
            var4 = (var1 >> 8 & 127) + (var1 >> 9 & 16256);
            this.__v_350(leftOrRightChannel, var4);
        } else {
            responseType = var1 & 255;
            if(responseType == 255) {
                this.__at_354();
            }
        }
    }
    void __ao_358(int var1, int var2) {
        this.__r[var1] = var2;
        this.__p[var1] = (int)(2097152.0D * Math.pow(2.0D, 5.4931640625E-4D * (double)var2) + 0.5D);
    }
    int __aa_359(MusicPatchNode var1) {
        int var2 = (var1.__d * var1.__x >> 12) + var1.__e;
        var2 += (this.__d[var1.__m] - 8192) * this.__b[var1.__m] >> 12;
        MusicPatchNode2 var3 = var1.patchNode2;
        int var4;
        if(var3.__l > 0 && (var3.__g > 0 || this.__k[var1.__m] > 0)) {
            var4 = var3.__g << 2;
            int var5 = var3.__e << 1;
            if(var1.__j < var5) {
                var4 = var4 * var1.__j / var5;
            }

            var4 += this.__k[var1.__m] >> 7;
            double var6 = Math.sin((double)(var1.__s & 511) * 0.01227184630308513D);
            var2 += (int)(var6 * (double)var4);
        }

        var4 = (int)((double)(var1.rawSound.sampleRate * 256) * Math.pow(2.0D, (double)var2 * 3.255208333333333E-4D) / (double) Audio.PcmPlayer_sampleRate + 0.5D);
        return var4 < 1?1:var4;
    }
    int __ax_360(MusicPatchNode var1) {
        MusicPatchNode2 var2 = var1.patchNode2;
        int var3 = this.__o[var1.__m] * this.__g[var1.__m] + 4096 >> 13;
        var3 = var3 * var3 + 16384 >> 15;
        var3 = var3 * var1.__g + 16384 >> 15;
        var3 = var3 * this.volume + 128 >> 8;
        if(var2.__q > 0) {
            var3 = (int)((double)var3 * Math.pow(0.5D, (double)var2.__q * (double)var1.__k * 1.953125E-5D) + 0.5D);
        }

        int var4;
        int var5;
        int var6;
        int var7;
        if(var2.__m != null) {
            var4 = var1.__n;
            var5 = var2.__m[var1.__i + 1];
            if(var1.__i < var2.__m.length - 2) {
                var6 = (var2.__m[var1.__i] & 255) << 8;
                var7 = (var2.__m[var1.__i + 2] & 255) << 8;
                var5 += (var2.__m[var1.__i + 3] - var5) * (var4 - var6) / (var7 - var6);
            }

            var3 = var5 * var3 + 32 >> 6;
        }

        if(var1.__a > 0 && var2.__f != null) {
            var4 = var1.__a;
            var5 = var2.__f[var1.__z + 1];
            if(var1.__z < var2.__f.length - 2) {
                var6 = (var2.__f[var1.__z] & 255) << 8;
                var7 = (var2.__f[var1.__z + 2] & 255) << 8;
                var5 += (var4 - var6) * (var2.__f[var1.__z + 3] - var5) / (var7 - var6);
            }

            var3 = var3 * var5 + 32 >> 6;
        }

        return var3;
    }
    int __af_361(MusicPatchNode var1) {
        int var2 = this.__u[var1.__m];
        return var2 < 8192?var2 * var1.__l + 32 >> 6:16384 - ((128 - var1.__l) * (16384 - var2) + 32 >> 6);
    }
    void __ai_367() {
        int track = this.track;
        int length = this.trackLength;

        long pos;
        for(pos = this.trackPosition; length == this.trackLength; pos = this.midiFile.__a_372(length)) {
            while(length == this.midiFile.trackLengths[track]) {
                this.midiFile.gotoTrack(track);
                int response = this.midiFile.readMessage(track);
                if(response == 1) {
                    midiFile.setTrackDone();
                    midiFile.markTrackPosition(track);
                    if(midiFile.isDone()) {

                        Audio.MUSIC_TRACK_FINISHED.set(true);

                        if(!loopFile || length == 0) {
                            __at_354();
                            midiFile.clear();
                            return;
                        }
                        this.midiFile.reset(pos);

                    }
                    break;
                }

                if((response & 128) != 0) {
                    this.__au_357(response);
                }

                this.midiFile.readTrackLength(track);
                this.midiFile.markTrackPosition(track);
            }

            track = this.midiFile.getPrioritizedTrack();
            length = this.midiFile.trackLengths[track];
        }

        this.track = track;
        this.trackLength = length;
        this.trackPosition = pos;
    }
    boolean removeIfNullOnCondition(MusicPatchNode patchNode) {
        if(patchNode.stream == null) {
            return removeOnCondition(patchNode);
        } else {
            return false;
        }
    }

    private boolean removeOnCondition(MusicPatchNode patchNode) {
        if(patchNode.__a >= 0) {
            patchNode.remove();
            if(patchNode.__o > 0 && patchNode == this.patchNodes2[patchNode.__m][patchNode.__o]) {
                this.patchNodes2[patchNode.__m][patchNode.__o] = null;
            }
        }
        return true;
    }

    boolean completed(MusicPatchNode musicPatchNode, int[] var2, int var3, int var4) {
        musicPatchNode.length = Audio.PcmPlayer_sampleRate / 100;
        if(musicPatchNode.__a < 0 || musicPatchNode.stream != null && !musicPatchNode.stream.__ae_195()) {
            int var5 = musicPatchNode.__d;
            if(var5 > 0) {
                var5 -= (int)(16.0D * Math.pow(2.0D, (double)this.__n[musicPatchNode.__m] * 4.921259842519685E-4D) + 0.5D);
                if(var5 < 0) {
                    var5 = 0;
                }

                musicPatchNode.__d = var5;
            }

            musicPatchNode.stream.__ah_193(this.__aa_359(musicPatchNode));
            MusicPatchNode2 nextPatchNote = musicPatchNode.patchNode2;
            boolean var7 = false;
            ++musicPatchNode.__j;
            musicPatchNode.__s += nextPatchNote.__l;
            double var8 = 5.086263020833333E-6D * (double)((musicPatchNode.__u - 60 << 8) + (musicPatchNode.__x * musicPatchNode.__d >> 12));
            if(nextPatchNote.__q > 0) {
                if(nextPatchNote.__u > 0) {
                    musicPatchNode.__k += (int)(128.0D * Math.pow(2.0D, var8 * (double)nextPatchNote.__u) + 0.5D);
                } else {
                    musicPatchNode.__k += 128;
                }
            }

            if(nextPatchNote.__m != null) {
                if(nextPatchNote.__w > 0) {
                    musicPatchNode.__n += (int)(128.0D * Math.pow(2.0D, var8 * (double)nextPatchNote.__w) + 0.5D);
                } else {
                    musicPatchNode.__n += 128;
                }

                while(musicPatchNode.__i < nextPatchNote.__m.length - 2 && musicPatchNode.__n > (nextPatchNote.__m[musicPatchNode.__i + 2] & 255) << 8) {
                    musicPatchNode.__i += 2;
                }

                if(nextPatchNote.__m.length - 2 == musicPatchNode.__i && nextPatchNote.__m[musicPatchNode.__i + 1] == 0) {
                    var7 = true;
                }
            }

            if(musicPatchNode.__a >= 0 && nextPatchNote.__f != null && (this.channel[musicPatchNode.__m] & 1) == 0 && (musicPatchNode.__o < 0 || musicPatchNode != this.patchNodes2[musicPatchNode.__m][musicPatchNode.__o])) {
                if(nextPatchNote.__o > 0) {
                    musicPatchNode.__a += (int)(128.0D * Math.pow(2.0D, var8 * (double)nextPatchNote.__o) + 0.5D);
                } else {
                    musicPatchNode.__a += 128;
                }

                while(musicPatchNode.__z < nextPatchNote.__f.length - 2 && musicPatchNode.__a > (nextPatchNote.__f[musicPatchNode.__z + 2] & 255) << 8) {
                    musicPatchNode.__z += 2;
                }

                if(nextPatchNote.__f.length - 2 == musicPatchNode.__z) {
                    var7 = true;
                }
            }

            if(var7) {
                musicPatchNode.stream.__v_192(musicPatchNode.length);
                if(var2 != null) {
                    musicPatchNode.stream.playMidiFile(var2, var3, var4);
                } else {
                    musicPatchNode.stream.skipSamples(var4);
                }

                if(musicPatchNode.stream.__at_196()) {
                    this.patchStream.mixer.addSubStream(musicPatchNode.stream);
                }

                musicPatchNode.reset();
                return removeOnCondition(musicPatchNode);
            } else {
                musicPatchNode.stream.__p_191(musicPatchNode.length, this.__ax_360(musicPatchNode), this.__af_361(musicPatchNode));
                return false;
            }
        } else {
            musicPatchNode.reset();
            musicPatchNode.remove();
            if(musicPatchNode.__o > 0 && musicPatchNode == this.patchNodes2[musicPatchNode.__m][musicPatchNode.__o]) {
                this.patchNodes2[musicPatchNode.__m][musicPatchNode.__o] = null;
            }

            return true;
        }
    }

    static void PcmStream_disable(PcmStream stream) {
        stream.active = false;
        if(stream.sound != null) {
            stream.sound.position = 0;
        }

        for(PcmStream subStream = stream.firstSubStream(); subStream != null; subStream = stream.nextSubStream()) {
            PcmStream_disable(subStream);
        }
    }

    static MusicPatch load(AbstractIndexCache cache, int file) {
        final byte[] data = cache.takeRecordFlat(file);
        return data == null?null:new MusicPatch(data);
    }

}
