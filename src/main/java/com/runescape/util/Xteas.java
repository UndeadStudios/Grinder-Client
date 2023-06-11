package com.runescape.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.runescape.Client;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 27/02/2020
 */
public class Xteas {

    public static HashMap<Integer, int[]> regionKeys = new HashMap<>();

    static {
        final Type type = new TypeToken<XteaEntry[]>(){}.getType();
        final InputStreamReader reader = new InputStreamReader(Client.class.getResourceAsStream("xteas.json"));
        final XteaEntry[] entries = new Gson().fromJson(reader, type);

        for(XteaEntry entry : entries){
            regionKeys.put(entry.mapsquare, entry.key);
        }
    }

    public static void main(String[] args) {
        System.out.println();
    }

    public static int[] getKeys(int regionId){
        return regionKeys.get(regionId);
    }

    class XteaEntry {
        private final int mapsquare;
        private final int[] key;

        public XteaEntry(int mapsquare, int[] key) {
            this.mapsquare = mapsquare;
            this.key = key;
        }
    }

}
