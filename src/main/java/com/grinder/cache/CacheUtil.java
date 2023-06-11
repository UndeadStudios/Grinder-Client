package com.grinder.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.runescape.cache.FileArchive;
import com.runescape.cache.FileStore;
import com.runescape.cache.def.ObjectDefinition;
import com.runescape.sign.SignLink;
import net.runelite.cache.ConfigType;
import net.runelite.cache.IndexType;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.fs.*;
import net.runelite.cache.index.FileData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO: add documentation
 * https://www.rune-server.ee/runescape-development/rs2-server/downloads/671504-oldschool-runescape-funorb-archives.html
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 29/12/2019
 */
public class CacheUtil {

    public static void main(String[] args) throws IOException {
        final Store oldStore = new Store(Paths.get("/Users/Sgsrocks/Downloads/2023-06-07-rev214", "cache").toFile());
        oldStore.load();
        final Store newStore = new Store(Paths.get("/Users/Sgsrocks/Downloads/Grinderscape Package/Grinderscape Package/Source/data", "oldschool").toFile());
        newStore.load();

//        Index to = packModelsFromTO(oldStore, newStore);
//        System.out.println(to.getId()+" has "+to.getArchives().size()+" archives!");

        Archive objects = oldStore.getIndex(IndexType.CONFIGS).getArchive(ConfigType.OBJECT.getId());

        //System.out.println("compress = "+objects.getCompression());

        byte[] archiveData = oldStore.getStorage().loadArchive(objects);
        ArchiveFiles files = objects.getFiles(archiveData);

       // System.out.println("file count "+files.getFiles().size());

        pack(IndexType.CONFIGS, oldStore, newStore);
        pack(IndexType.MAPS, oldStore, newStore);
        newStore.save();
    }
    private static Index pack(IndexType type, Store oldStore, Store newStore) {
        Index from = oldStore.getIndex(type);
        Index to = newStore.getIndex(type);

        to.setNamed(from.isNamed());
        to.setCompression(from.getCompression());
        to.setProtocol(from.getProtocol());
        to.getArchives().clear();

        from.getArchives().forEach(archive -> {
            Archive archive1 = to.addArchive(archive.getArchiveId());
            archive1.setCompression(archive.getCompression());
            archive1.setNameHash(archive.getNameHash());
            final byte[] archiveData;
            try {
                archiveData = oldStore.getStorage().loadArchive(archive);
                ArchiveFiles files = archive.getFiles(archiveData);
                List<FSFile> fileList = files.getFiles();
                FileData[] fileData = new FileData[fileList.size()];
                for (int i = 0; i < fileData.length; i++) {
                    final FSFile file = fileList.get(i);
                    fileData[i] = new FileData();
                    fileData[i].setId(file.getFileId());
                    fileData[i].setNameHash(file.getNameHash());
                }
                archive1.setFileData(fileData);
                newStore.getStorage().saveArchive(archive1, archiveData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return to;
    }

    private static Index packModelsFromTO(Store oldStore, Store newStore) {
        Index from = oldStore.getIndex(IndexType.MODELS);
        Index to = newStore.getIndex(IndexType.MODELS);

        to.setNamed(from.isNamed());
        to.setCompression(from.getCompression());
        to.setProtocol(from.getProtocol());
        to.getArchives().clear();

        from.getArchives().forEach(archive -> {
            Archive archive1 = to.addArchive(archive.getArchiveId());
            archive1.setCompression(archive.getCompression());
            archive1.setNameHash(archive.getNameHash());
            final byte[] archiveData;
            try {
                archiveData = oldStore.getStorage().loadArchive(archive);
                ArchiveFiles files = archive.getFiles(archiveData);
                List<FSFile> fileList = files.getFiles();
                FileData[] fileData = new FileData[fileList.size()];
                for (int i = 0; i < fileData.length; i++) {
                    final FSFile file = fileList.get(i);
                    fileData[i] = new FileData();
                    fileData[i].setId(file.getFileId());
                    fileData[i].setNameHash(file.getNameHash());
                }
                archive1.setFileData(fileData);
                newStore.getStorage().saveArchive(archive1, archiveData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return to;
    }

    public static void pack(String args) throws IOException {

        final File file = Paths.get(SignLink.findcachedir(), "og").toFile();
        file.createNewFile();
        final Store newStore = new Store(file);

        final Store oldStore = new Store(Paths.get(SignLink.findcachedir(), "oldschool").toFile());
        oldStore.load();
        final Index index = oldStore.getIndex(IndexType.SOUNDEFFECTS);
        final Index frameMaps = oldStore.getIndex(IndexType.FRAMEMAPS);


        newStore.addIndex(index.getId());
        newStore.getIndex(IndexType.SOUNDEFFECTS).setCrc(index.getCrc());
        newStore.getIndex(IndexType.SOUNDEFFECTS).setCompression(index.getCompression());
        newStore.getIndex(IndexType.SOUNDEFFECTS).setNamed(index.isNamed());
        newStore.getIndex(IndexType.SOUNDEFFECTS).setProtocol(index.getProtocol());
        newStore.getIndex(IndexType.SOUNDEFFECTS).setRevision(index.getRevision());
        final List<Archive> archiveList = index.getArchives();
        for(Archive archive : archiveList){
            final byte[] data = oldStore.getStorage().loadArchive(archive);
//            newStore.getIndex(IndexType.SOUNDEFFECTS).addArchive(archive.getArchiveId());
//            newStore.getIndex(IndexType.SOUNDEFFECTS).getArchive(archive.getArchiveId())
//                    .set
        }

//        newStore.getStorage().saveArchive();
        newStore.save();
    }

    public static void bla(String[] args) throws IOException {

        new Cache();

        final Store store = new Store(Paths.get("/Users/stanbend/IdeaProjects/rsmod-original/data/cache").toFile());

        store.load();
        ObjectManager manager = new ObjectManager(store);
        manager.load();

        for(int i = 0; i < ObjectDefinition.TOTAL_OBJECTS; i++){
            final ObjectDefinition objectDefinition = ObjectDefinition.lookup(i);
            if(objectDefinition != null){
                final int ambientSoundId = objectDefinition.ambientSoundId;
                final int[] ambientSounds = objectDefinition.someSoundStuff;
                if(ambientSoundId > 0 || ambientSounds != null && ambientSounds.length > 0) {
                    final int int4 = objectDefinition.int4;
                    final int int5 = objectDefinition.int5;
                    final int int6 = objectDefinition.int6;
                    if (int4 == 0 && int5 == 0 && int6 == 0) {
                        System.out.println("["+objectDefinition.type+"]: "+objectDefinition.name+" is missing specs for sounds! "+objectDefinition.ambientSoundId+", "+Arrays.toString(objectDefinition.someSoundStuff));

                    }
                }
            }
        }


        final Map<Integer, ObjectSoundSpecs> objectSoundSpecsMap = new TreeMap<>();
        AtomicInteger count = new AtomicInteger();
        manager.getObjects().forEach(objectDefinition -> {
            final int ambientSoundId = objectDefinition.getAnInt2110();
            final int[] ambientSounds = objectDefinition.getAnIntArray2084();
            if(ambientSoundId > 0 || (ambientSounds != null && ambientSounds.length > 0)) {
                final int int4 = objectDefinition.getAnInt2083();
                final int int5 = objectDefinition.getAnInt2112();
                final int int6 = objectDefinition.getAnInt2113();
                if (int4 <= 0 && int5 <= 0 && int6 <= 0) {
                    System.out.println("[" + objectDefinition.getId() + "] found " + int4 + ", " + int5 + ", " + int6 + " -> " + Arrays.toString(objectDefinition.getAnIntArray2084()));
                    count.incrementAndGet();
                } else {
                    final ObjectSoundSpecs soundSpecs = new ObjectSoundSpecs(ambientSoundId, ambientSounds, int4, int5, int6);
                    objectSoundSpecsMap.put(objectDefinition.getId(), soundSpecs);
                }
            }
        });
        final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        final File file = Paths.get("obj_sounds.json").toFile();
        file.createNewFile();

        final FileWriter writer = new FileWriter(file);
        gson.toJson(objectSoundSpecsMap, writer);
        writer.flush();
        writer.close();

        System.out.println("failed "+count.get()+" times!");
    }

    public static FileArchive createArchive(FileStore[] indices, int file) {
        byte[] buffer = null;

        try {
            if (indices[0] != null)
                buffer = indices[0].decompress(file);
        } catch (Exception _ex) {
            _ex.printStackTrace();
        }

        return new FileArchive(buffer);
    }

    static class ObjectSoundSpecs {

        final int ambientSoundId;
        final int[] ambientSoundIds;
        final int int4;
        final int int5;
        final int int6;

        public ObjectSoundSpecs(int ambientSoundId, int[] ambientSoundIds, int int4, int int5, int int6) {
            this.ambientSoundId = ambientSoundId;
            this.ambientSoundIds = ambientSoundIds;
            this.int4 = int4;
            this.int5 = int5;
            this.int6 = int6;
        }
    }
}
