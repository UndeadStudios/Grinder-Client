package com.runescape.audio;

import com.runescape.collection.Node;

/**
 * Represents an abstract sound node, where the position
 * is the position in the pcm stream.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 13/02/2020
 */
public abstract class AbstractSound extends Node {

    int position;
}
