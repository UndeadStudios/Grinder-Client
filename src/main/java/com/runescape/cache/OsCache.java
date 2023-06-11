package com.runescape.cache;

import com.grinder.client.ClientCompanion;
import com.grinder.client.GameShell;
import com.grinder.model.TitleScreen;
import com.runescape.cache.def.ObjectDefinition;
import com.runescape.draw.Rasterizer3D;
import com.runescape.input.KeyRecorder;
import com.runescape.input.MouseRecorder;
import com.grinder.client.util.Log;
import com.grinder.model.LoginScreen;
import com.runescape.Client;
import com.runescape.audio.Audio;
import com.runescape.audio.DevicePcmPlayerProvider;
import com.runescape.cache.definition.OSObjectDefinition;
import com.runescape.cache.definition.VarbitDefinition;
import com.runescape.clock.NanoClock;
import com.runescape.clock.Time;
import com.runescape.io.Buffer;
import com.runescape.sign.SignLink;
import com.runescape.task.TaskHandler;
import com.runescape.util.Huffman;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 13/02/2020
 */
public class OsCache {

    public static final int idxCount = 22;
    public static boolean foundCacheDirectory;

    public static File directory;

    public static BufferedFile randomDat;
    public static BufferedFile dat2File;
    public static BufferedFile idx255File;
    public static BufferedFile[] idxFiles;

    public static Buffer refrence;

    public static IndexCache indexCache0;
    public static IndexCache indexCache1;
    public static IndexCache indexCache2;
    public static IndexCache indexCache3;
    public static IndexCache indexCache4;
    public static IndexCache indexCache5; //todo: implement
    public static IndexCache indexCache6;
    public static IndexCache indexCache7;
    public static IndexCache indexCache8;
    public static IndexCache indexCache9;
    public static IndexCache indexCache10;
    public static IndexCache indexCache11;
    public static IndexCache indexCache12;
    public static IndexCache indexCache13;
    public static IndexCache indexCache14;
    public static IndexCache indexCache15;
    public static IndexCache indexCache17;
    public static IndexCache indexCache18;
    public static IndexCache indexCache19;
    public static IndexCache indexCache20;


    static IndexStore indexStore255;
    static Thread IndexStoreActionHandler_thread;
    static Hashtable __fo_q;
    static ArrayList indexCacheLoaders;
    static int indexCacheLoaderIndex;
    static int totalLoadLength;
    static long loadTime;
    static long longestLoadTime;
    static int titleLoadingStage = 0;
    static TextureProvider textureProvider;
    static Huffman huffman;

    static int loadingPercentage;
    static String loadingText;

    static {
        loadTime = -1L;
        longestLoadTime = -1L;
        foundCacheDirectory = false;
        __fo_q = new Hashtable(16);
        indexCacheLoaders = new ArrayList(10);
    }

    static long lastTrack = 0;

    static boolean isLowDetail = false;

    public static void main(String[] args) {
        loadAndWait();
//        System.out.println(SpotAnimationDefinition.getSpotAnimationDefinition(1111).sequence);
    }

    public static void loadAndWait(){
        init();
        Log.init();

        GameShell.clock = new NanoClock();
        GameShell.taskHandler = new TaskHandler();
        Audio.pcmPlayerProvider = new DevicePcmPlayerProvider();
        titleLoadingStage = 0;

        final Semaphore semaphore = new Semaphore(0);

        GameShell.taskHandler.newThreadTask(() -> {
            try {
                while (true) {

                    Js5Cache.doCycleJs5();
                    loadStoreActions();

                    processLoading();

                    if(Client.gameState > 5){
                        processCacheLoaders();
                        if(loadersCompleted()) {
                            System.out.println("Releasing");
                            semaphore.release();
                            return;
                        }
                    } else {
                        GameShell.resetClock();
                    }

                    Thread.sleep(10);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 1);
        try {
            semaphore.tryAcquire(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Log.error("Failed to load cache!", e);
        }
        System.out.println("Acquired");
    }

    public static void processLoading() {
        if(titleLoadingStage == 0){

          updateLoadingBar("Starting game engine...", 5);
          titleLoadingStage = 20;
        } else if(titleLoadingStage == 20) {
            updateLoadingBar("Prepared visibility map", 10);
            titleLoadingStage = 30;
        } else if (titleLoadingStage == 30) {
            indexCache0 = newIndexCache(0, false, true, true);
            indexCache1 = newIndexCache(1, false, true, true);
            indexCache2 = newIndexCache(2, true, false, true);
            indexCache3 = newIndexCache(3, false, true, true);
            indexCache4 = newIndexCache(4, false, true, true);
            indexCache5 = newIndexCache(5, true, true, true);
            indexCache6 = newIndexCache(6, true, true, true);
            indexCache7 = newIndexCache(7, false, true, true);
            indexCache8 = newIndexCache(8, false, true, true);
            indexCache9 = newIndexCache(9, false, true, true);
            indexCache10 = newIndexCache(10, false, true, true);
            indexCache11 = newIndexCache(11, false, true, true);
            indexCache12 = newIndexCache(12, false, true, true);
            indexCache13 = newIndexCache(13, true, false, true);
            indexCache14 = newIndexCache(14, false, true, true);
            indexCache15 = newIndexCache(15, false, true, true);
            indexCache17 = newIndexCache(17, true, true, true);
            indexCache18 = newIndexCache(18, false, true, true);
            indexCache19 = newIndexCache(19, false, true, true);
            indexCache20 = newIndexCache(20, false, true, true);
            titleLoadingStage = 40;
            updateLoadingBar("Connecting to update server", 20);
        } else if (titleLoadingStage == 40) {
            int i = 0;
            i += indexCache0.indexLoadPercent() * 4 / 100;
            i += indexCache1.indexLoadPercent() * 4 / 100;
            i += indexCache2.indexLoadPercent() * 2 / 100;
            i += indexCache3.indexLoadPercent() * 2 / 100;
            i += indexCache4.indexLoadPercent() * 6 / 100;
            i += indexCache5.indexLoadPercent() * 4 / 100;
            i += indexCache6.indexLoadPercent() * 2 / 100;
            i += indexCache7.indexLoadPercent() * 56 / 100;
            i += indexCache8.indexLoadPercent() * 2 / 100;
            i += indexCache9.indexLoadPercent() * 2 / 100;
            i += indexCache10.indexLoadPercent() * 2 / 100;
            i += indexCache11.indexLoadPercent() * 2 / 100;
            i += indexCache12.indexLoadPercent() * 2 / 100;
            i += indexCache13.indexLoadPercent() * 2 / 100;
            i += indexCache14.indexLoadPercent() * 2 / 100;
            i += indexCache15.indexLoadPercent() * 2 / 100;
            i += indexCache19.indexLoadPercent() / 100;
            i += indexCache18.indexLoadPercent() / 100;
            i += indexCache20.indexLoadPercent() / 100;
            i += indexCache17.failed() && indexCache17.__l_394()?1:0;
            if (i != 100) {
                if(i != 0)
                    updateLoadingBar("Checking for updates - " + i + "%");
                updateLoadingBar(30);
//                System.out.println(""+i);
            } else {
                createLoader(indexCache0, "Animations");
                createLoader(indexCache1, "Skeletons");
                createLoader(indexCache4, "Sound FX");
                createLoader(indexCache5, "Maps");
                createLoader(indexCache6, "Music Tracks");
                createLoader(indexCache8, "Sprites");
                createLoader(indexCache10, "Title Screen");
                createLoader(indexCache11, "Music Jingles");
                createLoader(indexCache14, "Music Samples");
                createLoader(indexCache15, "Music Patches");
                updateLoadingBar("Loaded update list", 30);
                titleLoadingStage = 45;
            }
        } else if (titleLoadingStage == 45) {
            Audio.load(isLowDetail);
            updateLoadingBar("Prepared sound engine", 35);
            titleLoadingStage = 50;
        } else if(titleLoadingStage == 50){
            updateLoadingBar("Loaded fonts", 40);
            titleLoadingStage = 60;
        } else if(titleLoadingStage == 60) {
            final int progress = TitleScreen.unpack(indexCache10, indexCache8);
            if (progress < 11) {
                updateLoadingBar("Loading title screen - " + progress * 100 / 11 + "%", 50);
            } else {
                updateLoadingBar("Loaded title screen", 50);
                GameShell.updateGameState(5);
                titleLoadingStage = 70;
            }
        }  else if(titleLoadingStage == 70){

            if(!indexCache2.__l_394()){
                updateLoadingBar("Loading config - "+indexCache2.loadPercent()+"%", 60);
            } else {
                Js5.skins = indexCache0;
                Js5.skeletons = indexCache1;
                Js5.configs = indexCache2;
                Js5.models = indexCache7;
                ObjectDefinition.ObjectDefinition_isLowDetail = isLowDetail;
                OSObjectDefinition.setCaches(indexCache2, indexCache7, isLowDetail);
                Js5.executeLoad();
                updateLoadingBar("Loaded config", 60);
                titleLoadingStage = 80;
            }
        } else if(titleLoadingStage == 80){
            updateLoadingBar("Loaded sprites", 70);
            titleLoadingStage = 90;
        }  else if(titleLoadingStage == 90){
            if(!indexCache9.__l_394()) {
                updateLoadingBar("Loading textures - " + "0%", 90);
            } else {
                Rasterizer3D.Rasterizer3D_setTextureLoader(new TextureProvider(OsCache.indexCache9, OsCache.indexCache8, 20, 0.8, ClientCompanion.lowMemory ? 64 : 128));
                titleLoadingStage = 100;
            }
        } else if(titleLoadingStage == 100){
            updateLoadingBar("Loaded textures",90);
            titleLoadingStage = 110;
        } else if(titleLoadingStage == 110){
            updateLoadingBar("Loaded input handler", 92);
            KeyRecorder.instance = new KeyRecorder();
            MouseRecorder.instance = new MouseRecorder();
            GameShell.taskHandler.newThreadTask(MouseRecorder.instance, 10);
            titleLoadingStage = 120;
        } else if(titleLoadingStage == 120) {
            if(!indexCache10.tryLoadRecordByNames("huffman", "")) {
                updateLoadingBar("Loading wordpack - " + 0 + "%", 94);
            } else {
                huffman = new Huffman(indexCache10.takeRecordByNames("huffman", ""));
                updateLoadingBar("Loaded wordpack", 94);
                titleLoadingStage = 130;
            }
        } else if(titleLoadingStage == 130){
            updateLoadingBar("Loaded interfaces", 98);
            titleLoadingStage = 140;
        } else if(titleLoadingStage == 140){
            updateLoadingBar("Loaded world map");
            titleLoadingStage = 150;
        } else if(titleLoadingStage == 150){
            GameShell.updateGameState(10);
            TitleScreen.load();
        }
    }

    public static void renderLoadingBar(){
        LoginScreen.drawLoadingText(Client.instance, loadingPercentage, loadingText);
    }

    static void updateLoadingBar(String message, int percentage){
        updateLoadingBar(message);
        updateLoadingBar(percentage);
        System.out.println(message+" - "+percentage+"%");
    }
    static void updateLoadingBar(String message){
        loadingText = message;
    }
    static void updateLoadingBar(int percentage){
        loadingPercentage = percentage;
//        System.out.println(percentage+"%");
    }

    public static void init() {

//        OsCheck.method1976();

        directory = Paths.get(SignLink.findcachedir(), "oldschool").toFile();
        if(!directory.exists()) {
            directory.mkdirs();
            Log.info("Created new cache directory at "+OsCache.directory);
        }

        if (!directory.exists()) {
            Log.error("Could not find cache dir at " + directory);
            throw new RuntimeException("");
        }

        Log.info("Set cache dir at "+directory.getPath());

        foundCacheDirectory = true;

        try {
            dat2File = new BufferedFile(new AccessFile(readFile("main_file_cache.dat2"), "rw", 1048576000L), 5200, 0);
            idx255File = new BufferedFile(new AccessFile(readFile("main_file_cache.idx255"), "rw", 1048576L), 6000, 0);
            idxFiles = new BufferedFile[idxCount];
            for (int i = 0; i < idxCount; ++i) {
                idxFiles[i] = new BufferedFile(new AccessFile(readFile("main_file_cache.idx" + i), "rw", 1048576L), 6000, 0);
            }

        } catch (IOException e) {
            Log.error("Failed to init cache", e);
        }
    }

    public static void loadStoreActions() {
        while (true) {
            IndexStoreAction storeAction;
            synchronized (IndexStoreActionHandler.IndexStoreActionHandler_requestQueue) {
                storeAction = (IndexStoreAction) IndexStoreActionHandler.IndexStoreActionHandler_responseQueue.removeLast();
            }
            if (storeAction == null)
                return;
            storeAction.indexCache.load(storeAction.indexStore, (int) storeAction.key, storeAction.data, false);
        }
    }

    static IndexCache newIndexCache(int index, boolean releaseArchives, boolean shallowRecords, boolean var3) {
        IndexStore store = null;

        if (dat2File != null)
            store = new IndexStore(index, dat2File, idxFiles[index], 1000000);

        return new IndexCache(store, indexStore255, index, releaseArchives, shallowRecords, var3);
    }

    static void createLoader(IndexCache cache, String name) {
        IndexCacheLoader loader = new IndexCacheLoader(cache, name);
        indexCacheLoaders.add(loader);
        totalLoadLength += loader.loadLength;
    }

    public static void processCacheLoaders() {

        if (-1L == longestLoadTime) {
            longestLoadTime = Time.currentTimeMillis() + 1000L;
        }

        long timeStamp = Time.currentTimeMillis();

        if (loadersCompleted() && -1L == loadTime) {
            loadTime = timeStamp;
            if (loadTime > longestLoadTime) {
                longestLoadTime = loadTime;
            }
        }
    }

    public static File readFile(String file) {
        if (!foundCacheDirectory) {
            throw new RuntimeException("");
        } else {
            File var1 = (File) __fo_q.get(file);
            if (var1 != null) {
                return var1;
            } else {
                File var2 = new File(directory, file);
                RandomAccessFile var3 = null;

                try {
                    File var4 = new File(var2.getParent());
                    if (!var4.exists()) {
                        throw new RuntimeException("");
                    } else {
                        var3 = new RandomAccessFile(var2, "rw");
                        int var5 = var3.read();
                        var3.seek(0L);
                        var3.write(var5);
                        var3.seek(0L);
                        var3.close();
                        __fo_q.put(file, var2);
                        return var2;
                    }
                } catch (Exception var8) {
                    try {
                        var8.printStackTrace();
                        System.err.println("Attempting to close file "+var3);
                        if (var3 != null) {
                            var3.close();
                        }
                    } catch (Exception var7) {
                        var7.printStackTrace();
                    }

                    throw new RuntimeException();
                }
            }
        }
    }

    static boolean loadersCompleted() {

        if (indexCacheLoaders != null && indexCacheLoaderIndex < indexCacheLoaders.size()) {

            while (indexCacheLoaderIndex < indexCacheLoaders.size()) {

                IndexCacheLoader loader = (IndexCacheLoader) indexCacheLoaders.get(indexCacheLoaderIndex);

                if (!loader.completed()) {
                    System.out.println("loading"+loader);
                    return false;
                }

                ++indexCacheLoaderIndex;
            }

        }
        return true;
    }

    public static void clear() {
        indexCache0.clearAllRecords();
        indexCache1.clearAllRecords();
        indexCache2.clearAllRecords();
        indexCache4.clearAllRecords();
        indexCache5.clearAllRecords();
        indexCache6.clearAllRecords();
        indexCache7.clearAllRecords();
        indexCache8.clearAllRecords();
        indexCache11.clearAllRecords();
        indexCache14.clearAllRecords();
        indexCache15.clearAllRecords();
        OSObjectDefinition.clearCaches();
    }


    public static VarpDefinition method1140(int varpId) {
        VarpDefinition var1 = (VarpDefinition) VarpDefinition.VarpDefinition_cached.get((long) varpId);
        if (var1 != null) {
            return var1;
        } else {
            byte[] length = VarpDefinition.varpDefinitionCacheIndex.takeRecord(16, varpId);
            var1 = new VarpDefinition();
            if (length != null) {
                var1.read(new Buffer(length));
            }

            VarpDefinition.VarpDefinition_cached.put(var1, (long) varpId);
            return var1;
        }
    }

    static void requestNetFile(IndexCache var0, int var1, int var2, int var3, byte var4, boolean var5) {
        long var6 = (var1 << 16) + var2;
        NetFileRequest var8 = (NetFileRequest) NetCache.pendingPriorityWrites.get(var6);
        if (var8 == null) {
            var8 = (NetFileRequest) NetCache.pendingPriorityResponses.get(var6);
            if (var8 == null) {
                var8 = (NetFileRequest) NetCache.pendingWrites.get(var6);
                if (var8 != null) {
                    if (var5) {
                        var8.removeDual();
                        NetCache.pendingPriorityWrites.put(var8, var6);
                        --NetCache.pendingWritesCount;
                        ++NetCache.pendingPriorityWritesCount;
                    }

                } else {
                    if (!var5) {
                        var8 = (NetFileRequest) NetCache.pendingResponses.get(var6);
                        if (var8 != null) {
                            return;
                        }
                    }

                    var8 = new NetFileRequest();
                    var8.indexCache = var0;
                    var8.crc = var3;
                    var8.padding = var4;
                    if (var5) {
                        NetCache.pendingPriorityWrites.put(var8, var6);
                        ++NetCache.pendingPriorityWritesCount;
                    } else {
                        NetCache.pendingWritesQueue.addFirst(var8);
                        NetCache.pendingWrites.put(var8, var6);
                        ++NetCache.pendingWritesCount;
                    }

                }
            }
        }
    }
    public static int getVarbit(int var0) {
        VarbitDefinition var1 = OsCache.getVarbitDefinition(var0);
        int var2 = var1.varp;
        int var3 = var1.lowBit;
        int var4 = var1.highBit;
        int var5 = Varps.Varps_masks[var4 - var3];
        return Varps.Varps_main[var2] >> var3 & var5;
    }
    public static VarbitDefinition getVarbitDefinition(int var0) {
        VarbitDefinition var1 = (VarbitDefinition)VarbitDefinition.VarbitDefinition_cached.get((long)var0);
        if(var1 != null) {
            return var1;
        } else {
            byte[] var2 = Js5.configs.takeRecord(14, var0);
            var1 = new VarbitDefinition();
            if(var2 != null) {
                var1.read(new Buffer(var2));
            }

            VarbitDefinition.VarbitDefinition_cached.put(var1, (long)var0);
            return var1;
        }
    }

}
