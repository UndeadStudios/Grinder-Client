package com.grinder.ui;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 12/01/2020
 */
public class ConsoleAppender extends AppenderSkeleton {

    private final Console console;

    public ConsoleAppender(Console console) {
        this.console = console;
    }

    @Override
    protected void append(LoggingEvent event) {
        console.append(event);
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
