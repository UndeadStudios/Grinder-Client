package com.runescape.audio;

import com.runescape.collection.Node;

/**
 * @version 1.0
 * @since 13/02/2020
 */
public abstract class PcmStreamMixerListener extends Node {

    int __m;
    abstract void remove2();
    abstract int update();
}
