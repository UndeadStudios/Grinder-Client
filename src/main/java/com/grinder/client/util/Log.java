package com.grinder.client.util;

import com.runescape.sign.SignLink;
import org.apache.log4j.*;
import org.apache.log4j.varia.LevelRangeFilter;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 09/11/2019
 */
public class Log {

    private static final AtomicBoolean loaded = new AtomicBoolean();

    private static String previousInfoMessage;
    private static String previousErrorMessage;

    public static void init(){

        final PatternLayout errorConsoleLayout = new PatternLayout();
        errorConsoleLayout.setConversionPattern("\u001b[31;1m%d{HH:mm:ss} %-5p - %-27c - %m\n");
        final ConsoleAppender errorConsoleAppender = new ConsoleAppender(errorConsoleLayout);
        errorConsoleAppender.setThreshold(Level.ERROR);
        errorConsoleAppender.activateOptions();

        final PatternLayout warnConsoleLayout = new PatternLayout();
        warnConsoleLayout.setConversionPattern("\u001b[33;1m%d{HH:mm:ss} %-5p - %-27c - %m\n");
        final ConsoleAppender warnConsoleAppender = new ConsoleAppender(warnConsoleLayout);
        warnConsoleAppender.setThreshold(Level.WARN);
        final LevelRangeFilter warnConsoleFilter = new LevelRangeFilter();
        warnConsoleFilter.setLevelMin(Level.WARN);
        warnConsoleFilter.setLevelMax(Level.WARN);
        warnConsoleAppender.addFilter(warnConsoleFilter);
        warnConsoleAppender.activateOptions();

        final PatternLayout infoConsoleLayout = new PatternLayout();
        infoConsoleLayout.setConversionPattern("\u001b[0m%d{HH:mm:ss} %-5p - %-27c - %m\n");
        final ConsoleAppender infoConsoleAppender = new ConsoleAppender(infoConsoleLayout);
        infoConsoleAppender.setThreshold(Level.INFO);
        final LevelRangeFilter infoConsoleFilter = new LevelRangeFilter();
        infoConsoleFilter.setLevelMin(Level.INFO);
        infoConsoleFilter.setLevelMax(Level.INFO);
        infoConsoleAppender.addFilter(infoConsoleFilter);
        infoConsoleAppender.activateOptions();

        final PatternLayout debugConsoleLayout = new PatternLayout();
        debugConsoleLayout.setConversionPattern("\u001b[0;36m%d{HH:mm:ss} %-5p - %-27c - %m\n");
        final ConsoleAppender debugConsoleAppender = new ConsoleAppender(debugConsoleLayout);
        debugConsoleAppender.setThreshold(Level.DEBUG);
        final LevelRangeFilter debugConsoleFilter = new LevelRangeFilter();
        debugConsoleFilter.setLevelMin(Level.DEBUG);
        debugConsoleFilter.setLevelMax(Level.DEBUG);
        debugConsoleAppender.addFilter(debugConsoleFilter);
        debugConsoleAppender.activateOptions();

        final PatternLayout traceConsoleLayout = new PatternLayout();
        traceConsoleLayout.setConversionPattern("\u001b[0;36m%d{HH:mm:ss} %-5p - %-27c - %m\n");
        final ConsoleAppender traceConsoleAppender = new ConsoleAppender(traceConsoleLayout);
        traceConsoleAppender.setThreshold(Level.TRACE);
        final LevelRangeFilter traceConsoleFilter = new LevelRangeFilter();
        traceConsoleFilter.setLevelMin(Level.TRACE);
        traceConsoleFilter.setLevelMax(Level.TRACE);
        traceConsoleAppender.addFilter(traceConsoleFilter);
        traceConsoleAppender.activateOptions();

        final PatternLayout fileLayout = new PatternLayout();
        fileLayout.setConversionPattern("%d{dd MMM yyyy HH:mm:ss,SSS} %-5p - %-27c - %m\n");
        final RollingFileAppender rollingFileAppender = new RollingFileAppender();
        rollingFileAppender.setLayout(fileLayout);
        rollingFileAppender.setFile(SignLink.findcachedir()+"console.log");
        rollingFileAppender.setImmediateFlush(true);
        rollingFileAppender.setThreshold(Level.DEBUG);
        rollingFileAppender.setAppend(true);
        rollingFileAppender.setMaxFileSize("10MB");
        rollingFileAppender.setMaxBackupIndex(10);
        rollingFileAppender.activateOptions();


        final Logger logger = getLogger();
        logger.addAppender(errorConsoleAppender);
        logger.addAppender(warnConsoleAppender);
        logger.addAppender(infoConsoleAppender);
        logger.addAppender(debugConsoleAppender);
        logger.addAppender(traceConsoleAppender);
        logger.addAppender(rollingFileAppender);

        loaded.set(true);
    }

    public static void info(String message){

        if(!loaded.get())
            return;

        if(message.equals(previousInfoMessage))
            return;

        previousInfoMessage = message;

        getLogger().info(message);
    }

    public static void error(String message){
        error(message, null);
    }

    public static void error(String message, Throwable throwable){

        if(!loaded.get())
            return;

        if(message.equals(previousErrorMessage))
            return;

        previousErrorMessage = message;

        System.out.println(message);

        if(throwable == null)
            getLogger().error(message);
        else
            getLogger().error(message, throwable);
    }

    public static Logger getLogger() {
        return LogManager.getRootLogger();
    }
}
