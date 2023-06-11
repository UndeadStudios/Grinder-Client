package com.runescape.task;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 11/12/2019
 */
public class Task {

    public Task next;

    public volatile Object result;
    public volatile int status;

    public int type;
    int intArgument;
    Object objectArgument;

    Task(){
        status = 0;
    }

}
