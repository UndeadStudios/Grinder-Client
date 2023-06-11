package com.runescape.scene.object;

import com.runescape.audio.PcmStreamMixer;
import com.runescape.audio.RawPcmStream;
import com.runescape.cache.def.ObjectDefinition;
import com.runescape.cache.def.ObjectSoundDefinition;
import com.runescape.cache.definition.OSObjectDefinition;
import com.runescape.collection.NodeDeque;
import com.runescape.collection.Node;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 13/02/2020
 */
public final class ObjectSound extends Node {

    public static NodeDeque objectSounds;

    static {
        objectSounds = new NodeDeque();
    }

    public int objectId;
    public int plane;
    public int west;
    public int south;
    public int east;
    public int north;
    public int minimumDistance;
    public int soundEffectId;
    public RawPcmStream stream1;
    public int frequency1;
    public int frequency2;
    public int[] soundEffectIds;
    public int timer;
    public RawPcmStream stream2;
    public ObjectDefinition OLD_obj;
    public OSObjectDefinition OS_obj;

    public void set() {
        int soundEffectId = this.soundEffectId;
        boolean success = false;

        if(OSObjectDefinition.USE_OSRS){

            final OSObjectDefinition transformed = OS_obj.transform();

            if(transformed != null) {
                this.soundEffectId = transformed.ambientSoundId;
                this.minimumDistance = transformed.int4 * 128;
                this.frequency1 = transformed.int5;
                this.frequency2 = transformed.int6;
                this.soundEffectIds = transformed.someSoundStuff;
            }

        } else {

            final ObjectDefinition transformed = this.OLD_obj.transform();

            if(transformed != null) {
                success = true;
                try {
                    final ObjectSoundDefinition objectSoundDefinition = ObjectSoundDefinition.getSound(transformed);
                    this.soundEffectId = objectSoundDefinition.getAmbientSoundId();
                    this.minimumDistance = objectSoundDefinition.getInt4() * 128;
                    this.frequency1 = objectSoundDefinition.getInt5();
                    this.frequency2 = objectSoundDefinition.getInt6();
                    this.soundEffectIds = objectSoundDefinition.getAmbientSoundIds();
                } catch (Exception e) {
                    System.err.println("Could not load sound for transformed object " + transformed.type);
                }
            }
        }

        if(!success){
            System.out.println("child is null!");
            this.soundEffectId = -1;
            this.minimumDistance = 0;
            this.frequency1 = 0;
            this.frequency2 = 0;
            this.soundEffectIds = null;
        }

        if(soundEffectId != this.soundEffectId && this.stream1 != null) {
            PcmStreamMixer.pcmStreamMixer.removeSubStream(this.stream1);
            this.stream1 = null;
        }
    }

    @Override
    public String toString() {
        return "details[" +
                "" + minimumDistance +
                ", " + frequency1 +
                ", " + frequency2 +
                ", " + timer +
                ']';
    }
}
