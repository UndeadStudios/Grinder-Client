package com.runescape.audio;

import com.runescape.collection.Node;

/**
 * @version 1.0
 * @since 13/02/2020
 */
public class MusicPatchNode extends Node {

    int __m;
    MusicPatch patch;
    RawSound rawSound;
    MusicPatchNode2 patchNode2;
    int __o;
    int __u;
    int __g;
    int __l;
    int __e;
    int __x;
    int __d;
    int __k;
    int __n;
    int __i;
    int __a;
    int __z;
    int __j;
    int __s;
    RawPcmStream stream;
    int length;
    int __b;


    void reset() {
        this.patch = null;
        this.rawSound = null;
        this.patchNode2 = null;
        this.stream = null;
    }
}
