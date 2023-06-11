package com.grinder.ui;

import com.grinder.client.util.Log;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.*;
import java.awt.*;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 12/01/2020
 */
public class Console extends JScrollPane {

    public static Logger logger = Logger.getLogger(Console.class);
    public static ConsoleAppender consoleAppender;

    private JTextArea textPane = new JTextArea();

    public Console(){
        textPane.setEditable(false);
        textPane.setForeground(Color.WHITE);
        textPane.setBackground(Color.DARK_GRAY);
        setViewportView(textPane);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setPreferredSize(new Dimension(250, 250));
        setMinimumSize(new Dimension(10, 10));

        if(consoleAppender != null)
            Log.getLogger().removeAppender(consoleAppender);

        consoleAppender = new ConsoleAppender(this);
        Log.getLogger().addAppender(consoleAppender);
    }

    public void append(LoggingEvent loggingEvent){
        textPane.append(loggingEvent.getMessage().toString()+"\n");
    }
}
