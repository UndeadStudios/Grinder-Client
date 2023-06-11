package com.grinder.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.runescape.api.Frame;
import com.runescape.cache.*;
import com.runescape.cache.anim.Animation;
import com.runescape.cache.anim.Graphic;
import com.runescape.cache.anim.RSFrame317;
import com.runescape.cache.config.VariableBits;
import com.runescape.cache.config.VariablePlayer;
import com.runescape.cache.def.ItemDefinition;
import com.runescape.cache.def.NpcDefinition;
import com.runescape.entity.model.IdentityKit;
import com.runescape.io.jaggrab.JagGrab;
import com.runescape.sign.SignLink;

import java.io.*;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 29/12/2019
 */
public class Cache {

    public final FileStore[] indices= new FileStore[SignLink.CACHE_INDEX_LENGTH];
    public final RandomAccessFile[] cache_idx_files = new RandomAccessFile[SignLink.CACHE_INDEX_LENGTH];
    public RandomAccessFile cache_dat = null;
    FileArchive configArchive;
    FileArchive interfaceArchive;
    FileArchive mediaArchive;
    FileArchive streamLoader_6;
    FileArchive textureArchive;
    FileArchive wordencArchive;
    FileArchive soundArchive;

    public Cache() throws IOException {

        String directory = SignLink.findcachedir();

        try {

            cache_dat = new RandomAccessFile(directory + "main_file_cache.dat", "rw");
            for (int index = 0; index < SignLink.CACHE_INDEX_LENGTH; index++) {
                cache_idx_files[index] = new RandomAccessFile(directory + "main_file_cache.idx" + index, "rw");
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        for (int i = 0; i < SignLink.CACHE_INDEX_LENGTH; i++)
            indices[i] = new FileStore(cache_dat, cache_idx_files[i], i + 1);

        configArchive = CacheUtil.createArchive(indices, JagGrab.CONFIG_CRC
        );
        interfaceArchive = CacheUtil.createArchive(indices, JagGrab.INTERFACE_CRC
        );
        mediaArchive = CacheUtil.createArchive(indices, JagGrab.MEDIA_CRC
        );
        streamLoader_6 = CacheUtil.createArchive(indices, JagGrab.UPDATE_CRC
        );
        textureArchive = CacheUtil.createArchive(indices, JagGrab.TEXTURES_CRC
        );
        wordencArchive = CacheUtil.createArchive(indices, JagGrab.CHAT_CRC
        );
        soundArchive = CacheUtil.createArchive(indices, JagGrab.SOUNDS_CRC
        );

        RSFrame317.animationlist = new RSFrame317[3000][0];

        IdentityKit.init(configArchive);
        VariablePlayer.init(configArchive);
        VariableBits.init(configArchive);


        final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        dumpNames(gson, dumpNpcDefs(), "npcNames.json");
        dumpNames(gson, dumpIemDefs(), "itemNames.json");

        OsCache.loadAndWait();

        final byte[] gzipInputBuffer = new byte[0x71868];
        for(int frameId: Animation.getSequenceDefinition(4103).frameIds){
            int file = frameId >> 16;
            int frameIndex = frameId & 0xffff;
            byte[] buffer = indices[2].decompress(file);
            int read = 0;
            try {
                GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(buffer));
                do {
                    if (read == gzipInputBuffer.length)
                        throw new RuntimeException("buffer overflow!");
                    int in = gis.read(gzipInputBuffer, read, gzipInputBuffer.length - read);
                    if (in == -1)
                        break;
                    read += in;
                } while (true);
            } catch (IOException _ex) {
                _ex.printStackTrace();
                continue;
            }
            buffer = new byte[read];
            System.arraycopy(gzipInputBuffer, 0, buffer, 0, read);
            RSFrame317.load(file, buffer);
            Frame frame = RSFrame317.getFrames(frameId);
            System.out.println("["+file+"]["+frameIndex+"]: "+frame);
        }
        System.out.println();
        System.out.println(SequenceDefinition.getSequenceDefinition(4103));
        for(int frameId: SequenceDefinition.getSequenceDefinition(4103).frameIds){
            int file = frameId >> 16;
            Frames frames = Frames.getFrames(file);
            int index = frameId &= 65535;
            com.runescape.cache.Animation animation = frames.frames[index];
            System.out.println("["+file+"]["+frameId+"]: "+animation);
        }
    }

    public JsonArray dumpNpcDefs() {
        final JsonArray npcs = new JsonArray();
        for (int npcId = 0; npcId < NpcDefinition.getTotalNpcs(); npcId++) {
            final NpcDefinition definition = NpcDefinition.lookup(npcId);
            if (definition != null && definition.name != null && !definition.name.isEmpty()) {
                final JsonObject object = new JsonObject();
                object.addProperty("id", definition.id);
                object.addProperty("name", definition.name);
                object.addProperty("combatLevel", definition.combatLevel);
                npcs.add(object);
            }
        }
        return npcs;
    }

    public JsonArray dumpIemDefs() {
        final JsonArray items = new JsonArray();
        for (int itemId = 0; itemId < ItemDefinition.getTotalItems(); itemId++) {
            final ItemDefinition definition = ItemDefinition.lookup(itemId);
            if (definition != null && definition.name != null && definition.noted_item_id == -1) {
                final JsonObject object = new JsonObject();
                object.addProperty("id", definition.id);
                object.addProperty("name", definition.name);
                items.add(object);
            }
        }
        return items;
    }

    public void dumpNames(Gson gson, JsonArray items, String s) throws IOException {
        final File itemNamesFile = Paths.get(SignLink.findcachedir(), s).toFile();
        itemNamesFile.createNewFile();
        final FileWriter itemNamesFileWriter = new FileWriter(itemNamesFile);
        gson.toJson(items, itemNamesFileWriter);
        itemNamesFileWriter.flush();
        itemNamesFileWriter.close();
    }

    public static void main(String[] args) {
        try {
            new Cache();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
