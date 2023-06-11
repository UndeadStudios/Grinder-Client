package com.runescape.scene;


import com.runescape.cache.Js5Cache;
import com.runescape.cache.OsCache;
import com.runescape.io.Buffer;
import com.runescape.util.Xteas;

/**
 * TODO: implement 241 (server constructed region chunks)
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 05/03/2020
 */
public class MapRegionBuilder {

    static byte[][] regionLandArchives;
    static byte[][]  regionMapArchives;
    static int[] regions;
    static int[] regionLandArchiveIds;
    static int[] regionMapArchiveIds;
    public static int map_objects_count;
    static int map_loading_state;

    public static void main(String[] args) {
        OsCache.loadAndWait();
        loadRegion(309, 598);
    }

    public static void loadRegion(int regionX, int regionY){

        int baseX = (regionX - 6) * 8;
        int baseY = (regionY - 6) * 8;

        int regionCount = 0;
        for (int x = (regionX - 6) / 8; x <= (regionX + 6) / 8; x++) {
            for (int y = (regionY - 6) / 8; y <= (regionY + 6) / 8; y++)
                regionCount++;
        }
        regionLandArchives = new byte[regionCount][];
        regionMapArchives = new byte[regionCount][];
        regions = new int[regionCount];
        regionLandArchiveIds = new int[regionCount];
        regionMapArchiveIds = new int[regionCount];

        int region = 0;
        for (int x = (regionX - 6) / 8; x <= (regionX + 6) / 8; x++) {
            for (int y = (regionY - 6) / 8; y <= (regionY + 6) / 8; y++) {
                regions[region] = (x << 8) + y;
                int map = regionLandArchiveIds[region] = OsCache.indexCache5.getArchiveId("l" + regionX + "_" + regionY);

                if (map != -1) {
                    System.out.println("Trying to load region map "+map);
                    OsCache.indexCache5.tryLoadArchive(map);
                } else
                    System.err.println("Coul not load region land archive at "+x+", "+y);

                int landscape = regionMapArchiveIds[region] = OsCache.indexCache5.getArchiveId("m" + regionX + "_" + regionY);

                if (landscape != -1) {
                    OsCache.indexCache5.tryLoadArchive(landscape);
                } else
                    System.err.println("Could not load region map archive at "+x+", "+y);

                region++;
            }
        }

        while(loadingRegion(baseX, baseY)){
            Js5Cache.doCycleJs5();
            OsCache.loadStoreActions();
            System.out.println("Loading region! \t failures = "+__client_fe);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Finished loading!");
    }
    static int __client_fe;
    public static boolean loadingRegion(int baseX, int baseY){
        __client_fe = 0;
        boolean loading = true;
        int regionIndex;
        for(regionIndex = 0; regionIndex < regionLandArchives.length; ++regionIndex) {
            if(regionMapArchiveIds[regionIndex] != -1 && regionLandArchives[regionIndex] == null) {
                regionLandArchives[regionIndex] = OsCache.indexCache5.takeRecord(regionMapArchiveIds[regionIndex], 0);
                if(regionLandArchives[regionIndex] == null) {
                    loading = false;
                    ++__client_fe;
                }
            }

            if(regionLandArchiveIds[regionIndex] != -1 && regionMapArchives[regionIndex] == null) {
               regionMapArchives[regionIndex] = OsCache.indexCache5.takeRecordEncrypted(regionLandArchiveIds[regionIndex], 0, Xteas.getKeys(regionIndex));
                if(regionMapArchives[regionIndex] == null) {
                    loading = false;
                    ++__client_fe;
                }
            }
        }
        if(!loading){
            map_loading_state = 1;
        } else {
            map_loading_state = 0;
            loading = true;

            for (regionIndex = 0; regionIndex < regionLandArchives.length; ++regionIndex) {
                byte[] regionMap = regionMapArchives[regionIndex];
                if (regionMap != null) {
                    int regionX = (regions[regionIndex] >> 8) * 64 - baseX;
                    int regionY = (regions[regionIndex] & 255) * 64 - baseY;
                    loading &= MapRegion.OS_readMapObjects(regionMap, regionX, regionY);
                }
            }

            if(!loading) {
                map_loading_state = 2;
            } else {
                if (map_loading_state != 0) {
                    System.out.println("Loading - please wait." + "<br>" + " (" + 100 + "%" + ")");
                }
                return true;
            }
        }
        return false;
    }
}