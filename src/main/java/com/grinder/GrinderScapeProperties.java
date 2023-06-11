package com.grinder;

import com.grinder.client.util.Log;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 11/12/2019
 */

public class GrinderScapeProperties {

    public static String discordAppID = "358664434324865024";
    private static final String GRINDER_SCAPE_TITLE = "grinder_scape.title";
    private static final String GRINDER_SCAPE_VERSION = "grinder_scape.version";
    private static final String DISCORD_APP_ID = "grinder_scape.discord.appid";
    private static final String DISCORD_INVITE = "grinder_scape.discord.invite";
    private static final String LAUNCHER_VERSION_PROPERTY = "grinder.launcher.version";

    private final Properties properties = new Properties();

    public GrinderScapeProperties() {
        try (InputStream in = getClass().getResourceAsStream("grinder_scape.properties")) {
            properties.load(in);
        } catch (IOException ex) {
            Log.error("unable to load properties", ex);
        }
    }

    public String getTitle(){
        return properties.getProperty(GRINDER_SCAPE_TITLE);
    }

    public String getVersion(){
        return properties.getProperty(GRINDER_SCAPE_VERSION);
    }

    public String getDiscordInvite() {
        return properties.getProperty(DISCORD_INVITE);
    }

    @Nullable
    public static String getLauncherVersion()
    {
        return System.getProperty(LAUNCHER_VERSION_PROPERTY);
    }
}
