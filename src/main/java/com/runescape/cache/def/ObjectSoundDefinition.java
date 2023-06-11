package com.runescape.cache.def;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.runescape.scene.object.ObjectSound;
import com.runescape.sign.SignLink;
import net.runelite.cache.io.InputStream;
import net.runelite.cache.io.OutputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 19/02/2020
 */
public class ObjectSoundDefinition {

//    public static void main(String[] args) throws IOException {
//        final OutputStream out = new OutputStream();
//        soundDefinitionMap.forEach((objectId, sound) -> {
//            out.writeByte(0);
//            out.writeShort(objectId);
//            if(sound.ambientSoundId != -1){
//                out.writeByte(1);
//                out.writeShort(sound.ambientSoundId);
//                out.writeByte(sound.int4);
//            }
//            if(sound.ambientSoundIds != null) {
//                out.writeByte(2);
//                out.writeShort(sound.int5);
//                out.writeShort(sound.int6);
//                out.writeByte(sound.int4);
//                out.writeByte(sound.ambientSoundIds.length);
//                for(int soundId: sound.ambientSoundIds){
//                    out.writeShort(soundId);
//                }
//            }
//        });
//
//        final byte[] bytes = out.getArray();
//        try {
//            Files.write(Paths.get(SignLink.findcachedir(), "new_font_pack.dat"), bytes);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        loadDat();
//        System.out.println(soundDefinitionMap);
//
//    }

    static Map<Integer, ObjectSoundDefinition> soundDefinitionMap = new HashMap<>();
    static {
        loadDat();
    }
//    static {
//
////        loadDat();
//        final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
//        final File file = Paths.get(SignLink.findcachedir()+"obj_sounds.json").toFile();
//        try {
//            final Type type = new TypeToken<Map<Integer, ObjectSoundDefinition>>(){}.getType();
//            final FileReader reader = new FileReader(file);
//            soundDefinitionMap = gson.fromJson(reader, type);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    private static void loadDat() {
        soundDefinitionMap.clear();
        try {
            final byte[] bytes = Files.readAllBytes(Paths.get(SignLink.findcachedir(), "new_font_pack.dat"));
            final InputStream buffer = new InputStream(bytes);
            int objectId = -1;
            int ambientSoundId = -1;
            int int4 = 0;
            int int5 = 0;
            int int6 = 0;

            for (;;)
            {
                if(buffer.remaining() <= 0)
                    break;

                int opcode = buffer.readUnsignedByte();

                int[] ambientSoundIds = null;

                if (opcode == 0){

                    if(objectId != -1) {
                        ObjectSoundDefinition soundDefinition = new ObjectSoundDefinition(ambientSoundId, ambientSoundIds, int4, int5, int6);
                        soundDefinitionMap.put(objectId, soundDefinition);
                    }
                    objectId = buffer.readUnsignedShort();
                }

                if (opcode == 1)
                {
                    ambientSoundId = buffer.readUnsignedShort();
                    int4 = buffer.readUnsignedByte();
                }

                if(opcode == 2)
                {
                    int5 = buffer.readUnsignedShort();
                    int6 = buffer.readUnsignedShort();
                    int4 = buffer.readUnsignedByte();
                    int len = buffer.readUnsignedByte();
                    ambientSoundIds = new int[len];
                    for(int i = 0; i < len; i++){
                        ambientSoundIds[i] = buffer.readUnsignedShort();
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    final int ambientSoundId;
    final int[] ambientSoundIds;
    final int int4;
    final int int5;
    final int int6;

    public ObjectSoundDefinition(int ambientSoundId, int[] ambientSoundIds, int int4, int int5, int int6) {
        this.ambientSoundId = ambientSoundId;
        this.ambientSoundIds = ambientSoundIds;
        this.int4 = int4;
        this.int5 = int5;
        this.int6 = int6;
    }

    @Override
    public String toString() {
        return "ObjectSoundDefinition{" +
                "ambientSoundId=" + ambientSoundId +
                ", ambientSoundIds=" + Arrays.toString(ambientSoundIds) +
                ", int4=" + int4 +
                ", int5=" + int5 +
                ", int6=" + int6 +
                '}'+"\n";
    }

    public static ObjectSoundDefinition getSound(ObjectDefinition definition) {
        return soundDefinitionMap.get(definition.type);
    }

    public ObjectSound toSound(int x, int y, int z, int orientation, ObjectDefinition definition){
        final ObjectSound sound = new ObjectSound();
        sound.objectId = definition.type;
        sound.plane = z;
        sound.west = x * 128;
        sound.south = y * 128;
        int var23 = definition.sizeX;
        int var24 = definition.sizeY;
        if(orientation == 1 || orientation == 3) {
            var23 = definition.sizeY;
            var24 = definition.sizeX;
        }
        sound.east = (var23 + x) * 128;
        sound.north = (var24 + y) * 128;
        sound.soundEffectId =ambientSoundId;
        sound.minimumDistance = int4 * 128;
        sound.frequency1 = int5;
        sound.frequency2 = int6;

        sound.soundEffectIds = ambientSoundIds;
        if(definition.transforms != null) {
            sound.OLD_obj = definition;
            sound.set();
        }
        return sound;
    }

    public int getAmbientSoundId() {
        return ambientSoundId;
    }

    public int[] getAmbientSoundIds() {
        return ambientSoundIds;
    }

    public int getInt4() {
        return int4;
    }

    public int getInt5() {
        return int5;
    }

    public int getInt6() {
        return int6;
    }
}
