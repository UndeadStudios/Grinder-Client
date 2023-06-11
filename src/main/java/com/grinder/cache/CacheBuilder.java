package com.grinder.cache;

import com.runescape.cache.graphics.sprite.SpriteLoader;
import net.runelite.cache.IndexType;
import net.runelite.cache.fs.*;
import net.runelite.cache.fs.jagex.CompressionType;
import net.runelite.cache.index.FileData;
import net.runelite.cache.util.Djb2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 19/02/2020
 */
public class CacheBuilder {



    public static void main(String[] args) {

        final IndexType[] indicesToCopy = new IndexType[]{
                IndexType.CONFIGS,
                IndexType.SOUNDEFFECTS,
                IndexType.TRACK1,
                IndexType.TRACK2,
                IndexType.INSTRUMENTS,
                IndexType.TEXTURES,
                IndexType.BINARY,
                IndexType.VORBIS
        };
        final File inputCache = Paths.get("/Users/stanbend/Dropbox", "oldschool").toFile();
        final File outputCache = Paths.get("/Users/Sgsrocks/Downloads/Grinderscape Package/Grinderscape Package/Source/data", "oldschool").toFile();


        test(outputCache);
//        copy(indicesToCopy, inputCache, outputCache);
    }

    public static void test(File outputCache){
        try {

            Store outputStore = new Store(outputCache);
            outputStore.load();
            final Index index = outputStore.getIndex(IndexType.SPRITES);
            final List<Archive> archiveList = index.getArchives();

            System.out.println("loaded "+archiveList.size()+" sound effects");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void copy(IndexType[] indicesToCopy, File inputCache, File outputCache) {
        outputCache.mkdir();


        SpriteLoader.loadSprites(() -> { });

        try {
            Store inputStore = new Store(inputCache);
            Store outputStore = new Store(outputCache);

            inputStore.load();

//            for(IndexType type : indicesToCopy) {
//                final Index index = inputStore.getIndex(type);
//                outputStore.getIndexes().add(index);
//                outputStore.getStorage().save(inputStore);
//                for(Archive archive : index.getArchives()){
//                    final byte[] archiveData = inputStore.getStorage().loadArchive(archive);
//                    outputStore.getStorage().saveArchive(archive, archiveData);
//                }
//
//            }



//            packSprites(outputStore);

            outputStore.save();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void packSprites(Store outputStore) throws IOException {
        final SpriteLoader[] sprites = SpriteLoader.cache;

        final Index spriteIndex = outputStore.addIndex(IndexType.SPRITES.getNumber());
        spriteIndex.setNamed(true);
        spriteIndex.setRevision(42);
        spriteIndex.setCompression(CompressionType.NONE);

        final Archive spriteArchive = spriteIndex.addArchive(0);
        final ArchiveFiles spriteFiles = new ArchiveFiles();
        spriteArchive.setRevision(42);
        spriteArchive.setCompression(CompressionType.NONE);
        spriteArchive.setNameHash(Djb2.hash("grinder"));

        final FileData[] fileData = new FileData[sprites.length];
        spriteArchive.setFileData(fileData);

        for (int i = 0; i < sprites.length; i++) {
            final SpriteLoader sprite = sprites[i];
            fileData[i] = new FileData();
            fileData[i].setNameHash(0);
            fileData[i].setId(i);
            final FSFile spriteFile = new FSFile(i);
            spriteFile.setNameHash(0);
            spriteFile.setContents(sprite.write());
            spriteFiles.addFile(spriteFile);
        }

        final byte[] grinderSpriteContents = spriteFiles.saveContents();
        final Container container = new Container(CompressionType.NONE, spriteArchive.getRevision());
        container.compress(grinderSpriteContents, null);

        outputStore.getStorage().saveArchive(spriteArchive, container.data);
    }

}
