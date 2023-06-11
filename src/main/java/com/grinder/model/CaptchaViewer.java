package com.grinder.model;

import com.grinder.Configuration;
import com.grinder.client.ClientCompanion;

import java.awt.*;
import java.net.URI;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 06/12/2019
 */
public class CaptchaViewer {

    public static void openInDefaultBrowser() {
        URI uri = URI.create("http://"+ Configuration.connected_world.getAddress() +":8080");
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
