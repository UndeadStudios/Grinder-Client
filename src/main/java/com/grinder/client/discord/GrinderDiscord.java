package com.grinder.client.discord;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.grinder.client.util.Log;

public class GrinderDiscord {

    public static void init() {
        DiscordRPC lib = DiscordRPC.INSTANCE;
        String applicationId = "699377460612956312";
        String steamId = "";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> Log.info("Discord connection is ready!");
        lib.Discord_Initialize(applicationId, handlers, true, steamId);
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        presence.details = "www.grinderscape.org!";
        presence.largeImageKey = "grinder_logo";
        lib.Discord_UpdatePresence(presence);
        // in a worker thread
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }, "RPC-Callback-Handler").start();
    }
}