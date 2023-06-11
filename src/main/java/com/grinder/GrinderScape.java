package com.grinder;

import com.grinder.client.ClientCompanion;
import com.grinder.client.config.GrinderScapeConfig;
import com.grinder.client.util.Log;
import com.grinder.javafx.ClientUIFX;
import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.sign.SignLink;
import javafx.application.Platform;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.util.ExecutorServiceExceptionLogger;
import net.runelite.client.util.OSType;

import java.awt.*;
import java.io.File;
import java.lang.management.GarbageCollectorMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 11/12/2019
 */
public class GrinderScape {

    public static final File GRINDER_SCAPE_DIR = new File(System.getProperty("user.home"), ".grinder_scape");

    public static final File PROFILES_DIR = new File(GRINDER_SCAPE_DIR, "profiles");

    public static GarbageCollectorMXBean garbageCollector;

    private final ScheduledExecutorService executorService = new ExecutorServiceExceptionLogger(Executors.newSingleThreadScheduledExecutor());
    private final ConfigManager configManager = new ConfigManager(executorService);
    private final ClientUI clientUI;
    public static Notifier notifier;

    //public static DiscordRP discord = new DiscordRP();

    private GrinderScape(ClientUI clientUI){
        this.clientUI = clientUI;
    }

    public static void main(String[] args) {

        GrinderScape.PROFILES_DIR.mkdirs();

        Log.init();
        //GrinderDiscord.init();

        try {
            Toolkit.getDefaultToolkit().setDynamicLayout(true);
            //System.setProperty("sun.awt.noerasebackground", "true");//OSType.getOSType() == OSType.Windows ? "false" : "true");
            if(OSType.getOSType() == OSType.Windows)
                System.setProperty("sun.awt.erasebackgroundonresize", "false");
            System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

            final boolean canUseFX = System.getProperty("java.version").startsWith("1.8");
            if(canUseFX)
                Platform.setImplicitExit(false);

            Log.info(System.getProperty("java.version"));

            ClientCompanion.nodeID = 10;
            ClientCompanion.portOffset = 0;
            Client.setHighMem();
            ClientCompanion.isMembers = true;

            final ClientUI clientUI = canUseFX ? new ClientUIFX() : new ClientUI();

            final GrinderScape grinderScape = new GrinderScape(clientUI);

            grinderScape.start();

        } catch (Exception e) {
            Log.error("Failed to start up GrinderScape", e);
        }
    }

    public void start() throws UnknownHostException {

        //discord.start();
        //discord.update("In Menu..", "");

        configManager.load();
        GrinderScapeConfig config = configManager.getConfig(GrinderScapeConfig.class);

        notifier = new Notifier(clientUI, config, executorService);

        clientUI.open(this);

        Client.instance = clientUI;
        Client.instance.start();
        Client.instance.init();

        SignLink.storeid = 32;
        SignLink.startpriv(InetAddress.getLocalHost());

        ClientUI.frameMode(Client.ScreenMode.FIXED, false);
    }

    public void shutdown() {
        configManager.sendConfig();
    }
}
