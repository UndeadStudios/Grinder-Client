package com.grinder.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.grinder.client.util.Log;
import com.grinder.ui.util.PlayerUpdateSnapshot;
import com.grinder.ui.util.Snapshot;
import com.runescape.sign.SignLink;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 25/03/2020
 */
public final class SnapshotCache {

    private final static int MAX = 20;
    private final static DateFormat FOLDER_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final static DateFormat FORMAT = new SimpleDateFormat("HH-mm-ss.SSS");
    private final static Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private static long timeSinceLastSerialiseCall = 0L;

    private static final LinkedHashMap<Date, Snapshot> CACHE_MAP = new LinkedHashMap<Date, Snapshot>(){
        protected boolean removeEldestEntry(Map.Entry<Date, Snapshot> eldest)
        {
            return size() > MAX;
        }
    };

    public static void store(Snapshot snapshot){
        CACHE_MAP.put(new Date(), snapshot);
    }

    public static void serialise(){

        if(System.currentTimeMillis() - timeSinceLastSerialiseCall > 10_000)
            return;

        timeSinceLastSerialiseCall = System.currentTimeMillis();

        final Date now = Calendar.getInstance().getTime();
        final Path dir = Paths.get(SignLink.findcachedir(), "snapshots");

        dir.toFile().mkdir();

        final Path finalDir = dir.resolve(FOLDER_FORMAT.format(now));

        finalDir.toFile().mkdir();

        final File playUpSnap = createSnapShotFile(finalDir, "play_up_snap_");
        final File npcUpSnap = createSnapShotFile(finalDir, "npc_up_snap_");

        final JsonArray playerSnapshots = new JsonArray();
        final JsonArray npcSnapshots = new JsonArray();
        CACHE_MAP.forEach((date, snapshot) -> {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("time", FORMAT.format(date));
            jsonObject.add("details", GSON.toJsonTree(snapshot));
            if(snapshot instanceof PlayerUpdateSnapshot)
                playerSnapshots.add(jsonObject);
            else
                npcSnapshots.add(jsonObject);
        });
        CACHE_MAP.clear();

        writeSnaps(playUpSnap, playerSnapshots);
        writeSnaps(npcUpSnap, npcSnapshots);
    }

    public static void writeSnaps(File file, JsonArray snaps) {
        try {
            final FileWriter writer = new FileWriter(file);
            GSON.toJson(snaps, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File createSnapShotFile(Path finalDir, String filePrefix) {
        final File file = finalDir.resolve(filePrefix+FORMAT.format(new Date())+".json").toFile();

        try {
            if(file.createNewFile()){
                Log.info("Created a new snapshot file at "+file.getPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
