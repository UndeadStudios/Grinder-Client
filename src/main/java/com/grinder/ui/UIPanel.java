package com.grinder.ui;

import net.runelite.api.Constants;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-17
 */
public class UIPanel extends JPanel {

    public UIPanel(final Applet client) {
        setSize(Constants.GAME_FIXED_SIZE);
        setMinimumSize(Constants.GAME_FIXED_SIZE);
        setPreferredSize(Constants.GAME_FIXED_SIZE);
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        if (client == null)
        {
            return;
        }

        client.setLayout(null);
        client.setSize(Constants.GAME_FIXED_SIZE);
        add(client, BorderLayout.CENTER);

    }


}
