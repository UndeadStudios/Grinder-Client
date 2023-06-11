package com.runescape.clock;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 11/12/2019
 */
public abstract class Clock {

    public abstract void mark();

    public abstract int wait(int max, int min);

}
