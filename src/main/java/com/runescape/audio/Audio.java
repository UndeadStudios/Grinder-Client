package com.runescape.audio;

import com.grinder.Configuration;
import com.grinder.client.ClientCompanion;
import com.grinder.client.util.Log;
import com.grinder.model.PlayerSettings;
import com.runescape.Client;
import com.runescape.cache.*;
import com.runescape.cache.anim.Animation;
import com.runescape.cache.graphics.widget.SettingsWidget;
import com.runescape.io.packets.outgoing.impl.SongFinished;
import com.runescape.scene.object.ObjectSound;
import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 13/02/2020
 */
public final class Audio {

    private final static int MUSIC_TRACK_PLAYING_STATE = 0;
    private final static int MUSIC_TRACK_REQUESTED_STATE = 1;
    private final static int MUSIC_TRACK_FINISHED_STATE = 2;
    private final static Logger LOGGER = Logger.getLogger(Audio.class);

    static final AtomicBoolean MUSIC_TRACK_FINISHED = new AtomicBoolean(false);

    static int pcmPlayerCount;
    static SoundSystem soundSystem;
    static ScheduledExecutorService soundSystemExecutor;

    public static MidiPcmStream midiPcmStream;
    public static PcmPlayerProvider pcmPlayerProvider;
    public static PcmPlayer pcmPlayer0;
    public static PcmPlayer pcmPlayer1;

    public static Decimator decimator;

    public static MusicTrack musicTrack;
    public static SoundCache soundCache;

    public static AbstractIndexCache indexCache15;
    public static AbstractIndexCache indexCache4;
    public static AbstractIndexCache indexCache14;

    public static AbstractIndexCache musicTrackCache;
    public static boolean musicTrackRequestByServer;
    public static boolean musicTrackLoop;
    public static int musicTrackId;
    public static int musicTrackVarpType = 255;
    public static int musicTrackArchiveId;
    public static int musicTrackFileId;
    public static int musicTrackLength;
    public static int musicTrackState;
    public static int musicTrackVolume;
    public static int areaSoundEffectVolume = 127;
    public static int PcmPlayer_sampleRate;

    public static int[] soundEffectIds = new int[50];
    public static int[] areaSoundVolumeModifier = new int[50];
    public static int[] queuedSoundEffectDelays = new int[50];
    public static SoundEffect[] soundEffects = new SoundEffect[50];
    public static int[] soundLocations = new int[50];
    public static int soundEffectCount;
    public static int soundEffectVolume = 127;


    public static boolean musicNotMuted(){
        return !Client.loggedIn && Configuration.loginMusic || musicTrackVarpType > 0;
    }

    public static void load(boolean lowDetail){
        init(22050, !lowDetail, 2);
        final MidiPcmStream midiPcmStream = new MidiPcmStream();
        midiPcmStream.__j_342(9, 128);
        pcmPlayer0 = newPcmPlayer(0, 22050);
        pcmPlayer0.setStream(midiPcmStream);

        configure(
                OsCache.indexCache15,
                OsCache.indexCache14,
                OsCache.indexCache4,
                midiPcmStream);

        pcmPlayer1 = newPcmPlayer(1, 2048);
        PcmStreamMixer.pcmStreamMixer = new PcmStreamMixer();
        pcmPlayer1.setStream(PcmStreamMixer.pcmStreamMixer);

        decimator = new Decimator(22050, PcmPlayer_sampleRate);
    }

    public static void init(int sampleRate, boolean stereoSound, int audioPlayerCount) {
        if(sampleRate >= 8000 && sampleRate <= 48000) {
            PcmPlayer_sampleRate = sampleRate;
            PcmPlayer.isStereo = stereoSound;
            pcmPlayerCount = audioPlayerCount;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static void configure(AbstractIndexCache cache15, AbstractIndexCache cache14, AbstractIndexCache cache4, MidiPcmStream pcmStream) {
        indexCache15 = cache15;
        indexCache14 = cache14;
        indexCache4 = cache4;
        midiPcmStream = pcmStream;
    }

    public static void playPcmPlayers() {
        if(pcmPlayer1 != null)
            pcmPlayer1.run();
        if(pcmPlayer0 != null)
            pcmPlayer0.run();
    }

    public static void sequenceVarp(int varpId){
        updateObjectSoundTransforms();

        final VarpDefinition varpDefinition = OsCache.method1140(varpId);
        final int varpType = varpDefinition.type;
        if(varpType != 0){
            int varp = Varps.Varps_main[varpType];
            if(varpType == 3) {
                updateMusicTrackVolume(varp);
            }
            if(varpType == 4) {
                updateSoundEffectVolume(varp);
            }
            if(varpType == 10) {
                updateObjectSoundVolume(varp);
            }
        }
    }

    public static void updateSoundEffectVolume(int varp){
        if(varp == 0) updateSoundEffectDirect(127);
        if(varp == 1) updateSoundEffectDirect(96);
        if(varp == 2) updateSoundEffectDirect(64);
        if(varp == 3) updateSoundEffectDirect(32);
        if(varp == 4) updateSoundEffectDirect(0);
    }

    public static void updateSoundEffectDirect(int volume) {
        soundEffectVolume = volume;
    }

    public static void updateMusicTrackVolumeDirect(int volume) {
        if(volume != musicTrackVarpType) {
            if(musicTrackVarpType == 0 && musicTrackId != -1) {
                requestMusicTrack(OsCache.indexCache6, musicTrackId, 0, volume, true);
                musicTrackRequestByServer = false;
            } else if(volume == 0) {
                midiPcmStream.clear();
                musicTrackState = MUSIC_TRACK_REQUESTED_STATE;
                musicTrackCache = null;
                musicTrackRequestByServer = false;
            } else {
                setMusicTrackOrPcmStreamType(volume);
            }

            musicTrackVarpType = volume;
        }
    }
    public static void updateMusicTrackVolume(int varp) {
        short volume = 0;
        if(varp == 0) volume = 255;
        if(varp == 1) volume = 192;
        if(varp == 2) volume = 128;
        if(varp == 3) volume = 64;
        updateMusicTrackVolumeDirect(volume);
    }

    public static void updateObjectSoundVolume(int varp) {
        if(varp == 0) updateObjectSoundVolumeDirect(127);
        if(varp == 1) updateObjectSoundVolumeDirect(96);
        if(varp == 2) updateObjectSoundVolumeDirect(64);
        if(varp == 3) updateObjectSoundVolumeDirect(32);
        if(varp == 4) updateObjectSoundVolumeDirect(0);
    }
    public static void updateObjectSoundVolumeDirect(int volume) {
        areaSoundEffectVolume = volume;
    }
    public static void updateObjectSoundTransforms(){
        for(ObjectSound objectSound = (ObjectSound)ObjectSound.objectSounds.last();
            objectSound != null;
            objectSound = (ObjectSound)ObjectSound.objectSounds.previous())
        {
            if(objectSound.OLD_obj != null)
                objectSound.set();
        }
    }

    public static void setMusicTrackOrPcmStreamType(int type) {
        if(musicTrackState != MUSIC_TRACK_PLAYING_STATE)
            musicTrackVolume = type;
        else
            midiPcmStream.setVolume(type);
    }

    public static void clearObjectSounds() {
        for(ObjectSound var0 = (ObjectSound)ObjectSound.objectSounds.last(); var0 != null; var0 = (ObjectSound)ObjectSound.objectSounds.previous()) {
            if(var0.stream1 != null) {
                PcmStreamMixer.pcmStreamMixer.removeSubStream(var0.stream1);
                var0.stream1 = null;
            }

            if(var0.stream2 != null) {
                PcmStreamMixer.pcmStreamMixer.removeSubStream(var0.stream2);
                var0.stream2 = null;
            }
        }

        ObjectSound.objectSounds.clear();
    }

    public static void queueSoundEffect(int id, int unknown, int delay) {
        if(soundEffectVolume != 0 && unknown != 0 && soundEffectCount < 50) {
            soundEffectIds[soundEffectCount] = id;
            areaSoundVolumeModifier[soundEffectCount] = unknown;
            queuedSoundEffectDelays[soundEffectCount] = delay;
            soundEffects[soundEffectCount] = null;
            soundLocations[soundEffectCount] = 0;
            ++soundEffectCount;
        }
    }

    public static void resetMusicTrack(int var0) {
        musicTrackState = MUSIC_TRACK_REQUESTED_STATE;
        musicTrackCache = null;
        musicTrackArchiveId = -1;
        musicTrackFileId = -1;
        musicTrackVolume = 0;
        musicTrackLoop = false;
        musicTrackLength = var0;
        LOGGER.info("Reset music track length="+var0+"");
    }

    public static void requestMusicTrack(AbstractIndexCache cache, int archiveId, int fileId, int volume, boolean loopTrack) {
        musicTrackState = MUSIC_TRACK_REQUESTED_STATE;
        musicTrackCache = cache;
        musicTrackArchiveId = archiveId;
        musicTrackFileId = fileId;
        musicTrackVolume = volume;
        musicTrackLoop = loopTrack;
        musicTrackLength = 10000;
        LOGGER.info("Requested music track archive="+archiveId+", file="+fileId+", vol="+volume+", someBool="+loopTrack);
    }

    public static void requestMusicTrack(MusicTrack track){
        musicTrack = track;
        musicTrackState = MUSIC_TRACK_FINISHED_STATE;
        musicTrackVolume = 255;
        musicTrackLoop = true;
    }

    public static void requestMusicTrack(int archiveId) {
        if(archiveId == -1 && !musicTrackRequestByServer) {
            midiPcmStream.clear();
            musicTrackState = MUSIC_TRACK_REQUESTED_STATE;
            musicTrackCache = null;
            LOGGER.info("Reset music track, clearing midi stream");
        } else if(archiveId != -1 && archiveId != musicTrackId && musicTrackVarpType != 0 && !musicTrackRequestByServer) {
            IndexCache trackCache = OsCache.indexCache6;
            int volume = musicTrackVarpType;
            musicTrackState = MUSIC_TRACK_REQUESTED_STATE;
            musicTrackCache = trackCache;
            musicTrackArchiveId = archiveId;
            musicTrackFileId = 0;
            musicTrackVolume = volume;
            musicTrackLoop = false;
            musicTrackLength = 2;
            LOGGER.info("Requested music track archive="+archiveId+", file="+0+", vol="+volume+", someBool="+false);
        }
        musicTrackId = archiveId;
    }

    public static void requestMusicTrack2(int trackId) {
        if(musicTrackVarpType != 0 && trackId != -1) {
            requestMusicTrack(OsCache.indexCache11, trackId, 0, musicTrackVarpType, false);
            musicTrackRequestByServer = true;
            LOGGER.info("Requested music track archive="+trackId+", file="+0+", vol="+musicTrackVarpType+", someBool=false");
        }
    }

    public static void requestMusicTrack3(int length, AbstractIndexCache trackCache, String archiveName, String trackName, int volume, boolean loopTrack) {
        int archiveId = trackCache.getArchiveId(archiveName);
        int fileId = trackCache.getRecordId(archiveId, trackName);
        musicTrackState = MUSIC_TRACK_REQUESTED_STATE;
        musicTrackCache = trackCache;
        musicTrackArchiveId = archiveId;
        musicTrackFileId = fileId;
        musicTrackVolume = volume;
        musicTrackLoop = loopTrack;
        musicTrackLength = length;
        LOGGER.info("Requested music track archive="+archiveId+", file="+fileId+", vol="+volume+", length="+length+", someBool="+false);
    }

    public static void requestMusicTrack4(AbstractIndexCache var0, String var1, String var2, int var3, boolean var4) {
        int var5 = var0.getArchiveId(var1);
        int var6 = var0.getRecordId(var5, var2);
        requestMusicTrack(var0, var5, var6, var3, var4);
    }

    public static void methodCycleMidiPlayers(){
        int volume;
        try {

            if(MUSIC_TRACK_FINISHED.get()){
                Log.info("Finished song!");
                Client.instance.sendPacket(new SongFinished().create());
                MUSIC_TRACK_FINISHED.set(false);
            }

            if(musicTrackState == MUSIC_TRACK_REQUESTED_STATE) {

                if(midiPcmStream == null)
                    return;

                volume = midiPcmStream.getVolume();
                final boolean ready = midiPcmStream.isReady();

                //LOGGER.info("Parsing music track request, duration="+volume+", length="+musicTrackLength+", midiReady="+ready);
                if(volume > 0 && ready) {
                    volume -= musicTrackLength;
                    if(volume < 0)
                        volume = 0;
                    midiPcmStream.setVolume(volume);
                } else {
                    midiPcmStream.clear();
                    midiPcmStream.removeAll();
                    musicTrackState = musicTrackCache != null
                            ? MUSIC_TRACK_FINISHED_STATE
                            : MUSIC_TRACK_PLAYING_STATE;
                    musicTrack = null;
                    soundCache = null;
                }
            }

        } catch (Exception e) {
            Log.error("Failed to process midi players", e);
            if(midiPcmStream != null)
                midiPcmStream.clear();
            musicTrackState = MUSIC_TRACK_PLAYING_STATE;
            musicTrack = null;
            soundCache = null;
            musicTrackCache = null;
        }

        playPcmPlayers();
    }

    public static void methodDrawMusicTrack(){
        boolean ready;
        midiPlayerSequenceBlock: {
            try {
                if(musicTrackState == MUSIC_TRACK_FINISHED_STATE) {
//                    LOGGER.info("Music track finished track="+musicTrack);
                    if(musicTrack == null) {
                        musicTrack = MusicTrack.readTrack(musicTrackCache, musicTrackArchiveId, musicTrackFileId);
                        if(musicTrack == null) {
                            ready = false;
                            break midiPlayerSequenceBlock;
                        }
                    }

//                    LOGGER.info("Music track, new track = "+musicTrack);

                    if(soundCache == null)
                        soundCache = new SoundCache(indexCache4, indexCache14);

                    if(midiPcmStream.loadMusicTrack(musicTrack, indexCache15, soundCache, 22050)) {
//                        LOGGER.info("Music track, loaded new track");
                        midiPcmStream.clearAll();
                        midiPcmStream.setVolume(musicTrackVolume);
                        midiPcmStream.setMusicTrack(musicTrack, musicTrackLoop);
                        musicTrackState = MUSIC_TRACK_PLAYING_STATE;
                        musicTrack = null;
                        soundCache = null;
                        musicTrackCache = null;
                        ready = true;
                        break midiPlayerSequenceBlock;
                    }
                }
            } catch (Exception e) {
                Log.error("Failed to process midi players", e);
                midiPcmStream.clear();
                musicTrackState = MUSIC_TRACK_PLAYING_STATE;
                musicTrack = null;
                soundCache = null;
                musicTrackCache = null;
            }
            ready = false;
        }

        if(ready && musicTrackRequestByServer && pcmPlayer0 != null) {
            pcmPlayer0.tryDiscard();
            System.out.println("trying to discard pcmPlayer0");
        }
    }

    public static void handleSoundEffects(Animation sequence, int soundEffectIndex, int x, int y) {
        if(soundEffectCount < 50 && areaSoundEffectVolume != 0) {
            if(sequence.soundsEffects != null && soundEffectIndex < sequence.soundsEffects.length) {
                int soundEffect = sequence.soundsEffects[soundEffectIndex];
                if(soundEffect != 0) {
                    int soundEffectId = soundEffect >> 8;
                    int unknownSoundValue1 = soundEffect >> 4 & 7;
                    int soundEffectHeight = soundEffect & 15;
                    soundEffectIds[soundEffectCount] = soundEffectId;
                    areaSoundVolumeModifier[soundEffectCount] = unknownSoundValue1;
                    queuedSoundEffectDelays[soundEffectCount] = 0;
                    soundEffects[soundEffectCount] = null;
                    int localX = (x - 64) / 128;
                    int localY = (y - 64) / 128;
                    soundLocations[soundEffectCount] = soundEffectHeight + (localY << 8) + (localX << 16);
                    ++soundEffectCount;
                }
            }
        }
    }

    public static void processSoundEffects() {
        for(int i = 0; i < soundEffectCount; ++i) {
            --queuedSoundEffectDelays[i];
            if(queuedSoundEffectDelays[i] >= -10) {
                SoundEffect soundEffect = soundEffects[i];
                if(soundEffect == null) {
                    soundEffect = SoundEffect.get(OsCache.indexCache4, soundEffectIds[i], 0);
                    if(soundEffect == null) {
                        continue;
                    }

                    queuedSoundEffectDelays[i] += soundEffect.__q_174();
                    soundEffects[i] = soundEffect;
                }

                if(queuedSoundEffectDelays[i] < 0) {
                    int volume;
                    if(soundLocations[i] != 0) {
                        final int soundRadius = (soundLocations[i] & 255) * 128;
                        final int soundX = soundLocations[i] >> 16 & 255;
                        final int soundY = soundLocations[i] >> 8 & 255;
                        final int deltaX = Math.abs(soundX * 128 + 64 - Client.localPlayer.x);
                        final int deltaY = Math.abs(soundY * 128 + 64 - Client.localPlayer.y);
                        final int distance = deltaX + deltaY - 128;
                        if(distance > soundRadius) {
                            queuedSoundEffectDelays[i] = -100;
                            continue;
                        }
                        if(soundRadius == 0)
                            continue;
                        volume = (soundRadius - distance) * areaSoundEffectVolume / soundRadius;
                        //System.out.println("volume = "+volume+", distance = "+distance+" radius = "+soundRadius);
                    } else {
                        volume = soundEffectVolume;
                    }

                    if(volume > 0) {

                        final RawSound rawSound = soundEffect.toRawSound().resample(decimator);
                        final RawPcmStream pcmStream = RawPcmStream.wrap(rawSound, 100, volume);
                        if(pcmStream == null){
                            Log.error("Failed to create RawPcmStream for sound "+soundEffectIds[i]);
                            continue;
                        }
                        pcmStream.setNumLoops(areaSoundVolumeModifier[i] - 1);
                        PcmStreamMixer.pcmStreamMixer.addSubStream(pcmStream);
                    }

                    queuedSoundEffectDelays[i] = -100;
                }
            } else {
                --soundEffectCount;

                for(int j = i; j < soundEffectCount; ++j) {
                    soundEffectIds[j] = soundEffectIds[j + 1];
                    soundEffects[j] = soundEffects[j + 1];
                    areaSoundVolumeModifier[j] = areaSoundVolumeModifier[j + 1];
                    queuedSoundEffectDelays[j] = queuedSoundEffectDelays[j + 1];
                    soundLocations[j] = soundLocations[j + 1];
                }

                --i;
            }
        }

        if(musicTrackRequestByServer) {

            final boolean ready = musicTrackState != MUSIC_TRACK_PLAYING_STATE || midiPcmStream.isReady();

            if(!ready) {

                if(musicTrackVarpType != 0 && musicTrackId != -1)
                    requestMusicTrack(OsCache.indexCache6, musicTrackId, 0, musicTrackVarpType, true);

                musicTrackRequestByServer = false;
            }
        }

    }

    public static void processObjectSounds(int tickDelta) {
        int plane = Client.plane;
        int x = Client.localPlayer.x;
        int y = Client.localPlayer.y;
        int distance;
        int adjustedVolume;

        for(ObjectSound sound = (ObjectSound)ObjectSound.objectSounds.last(); sound != null; sound = (ObjectSound)ObjectSound.objectSounds.previous()) {
            if(sound.soundEffectId != -1 || sound.soundEffectIds != null) {
                distance = 0;
                if(x > sound.east) distance += x - sound.east;
                else if(x < sound.west) distance += sound.west - x;

                if(y > sound.north) distance += y - sound.north;
                else if(y < sound.south) distance += sound.south - y;

                if(distance - 64 <= sound.minimumDistance && areaSoundEffectVolume != 0 && plane == sound.plane) {
                    distance -= 64;
                    if(distance < 0) {
                        distance = 0;
                    }


                    adjustedVolume = (sound.minimumDistance - distance) * areaSoundEffectVolume / Math.max(1, sound.minimumDistance);
                    if(sound.stream1 == null) {
                        if(sound.soundEffectId >= 0) {
                            final SoundEffect soundEffect = SoundEffect.get(OsCache.indexCache4, sound.soundEffectId, 0);
                            if(soundEffect != null) {
                                final RawSound rawSound = soundEffect.toRawSound().resample(decimator);
                                final RawPcmStream pcmStream = RawPcmStream.wrap(rawSound, 100, adjustedVolume);
                                if(pcmStream == null){
                                    Log.error("Failed to create RawPcmStream for sound "+sound.soundEffectId);
                                    continue;
                                }
                                pcmStream.setNumLoops(-1);
                                PcmStreamMixer.pcmStreamMixer.addSubStream(pcmStream);
                                sound.stream1 = pcmStream;
                            }
                        }
                    } else {
                        sound.stream1.setVolume(adjustedVolume);
                    }

                    if(sound.stream2 == null) {
                        if(sound.soundEffectIds != null && (sound.timer -= tickDelta) <= 0) {
                            final int randomSoundIndex = (int)(Math.random() * (double)sound.soundEffectIds.length);
                            final int randomSoundId = sound.soundEffectIds[randomSoundIndex];
                            final SoundEffect soundEffect = SoundEffect.get(OsCache.indexCache4, randomSoundId, 0);

                            if(soundEffect != null) {
                                final RawSound rawSound = soundEffect.toRawSound().resample(decimator);
                                final RawPcmStream pcmStream = RawPcmStream.wrap(rawSound, 100, adjustedVolume);
                                if(pcmStream == null){
                                    Log.error("Failed to create RawPcmStream for sound "+randomSoundId);
                                    continue;
                                }
                                pcmStream.setNumLoops(0);
                                PcmStreamMixer.pcmStreamMixer.addSubStream(pcmStream);
                                sound.stream2 = pcmStream;
                                sound.timer = (sound.frequency1 + (int)(Math.random() * (double)(sound.frequency2 - sound.frequency1)));
                            }
                        }
                    } else {
                        sound.stream2.setVolume(adjustedVolume);
                        if(!sound.stream2.hasNext())
                            sound.stream2 = null;
                    }
                } else {
                    if(sound.stream1 != null) {
                        PcmStreamMixer.pcmStreamMixer.removeSubStream(sound.stream1);
                        sound.stream1 = null;
                    }

                    if(sound.stream2 != null) {
                        PcmStreamMixer.pcmStreamMixer.removeSubStream(sound.stream2);
                        sound.stream2 = null;
                    }
                }
            }
        }
    }

    public static void renderAreaSoundLabels(Client client) {
        int playerX = Client.localPlayer.x;
        int playerY = Client.localPlayer.y;

        for(ObjectSound sound = (ObjectSound)ObjectSound.objectSounds.last(); sound != null; sound = (ObjectSound)ObjectSound.objectSounds.previous()) {
            if(sound.soundEffectId != -1 || sound.soundEffectIds != null) {
                int distance = 0;
                if (playerX > sound.east) distance += playerX - sound.east;
                else if (playerX < sound.west) distance += sound.west - playerX;

                if (playerY > sound.north) distance += playerY - sound.north;
                else if (playerY < sound.south) distance += sound.south - playerY;
                if (distance - 64 <= sound.minimumDistance && Client.plane == sound.plane) {
                    distance -= 64;
                    if (distance < 0) {
                        distance = 0;
                    }

                    int centerX = (sound.west + sound.east) / 2;
                    int centerY = (sound.north + sound.south) / 2;

                    /*client.calcEntityScreenPos(centerX, 64, centerY);
                    int offset = 12;

                    client.newSmallFont.drawCenteredString("distance = " + distance+", "+sound.minimumDistance,
                            client.spriteDrawX, client.spriteDrawY - offset, 0xffffff, 1);
                    client.newSmallFont.drawCenteredString("timer = "+sound.timer+", "+sound.frequency1+", "+sound.frequency2,
                            client.spriteDrawX, client.spriteDrawY - 2 * offset, 0xffffff, 1);
                    client.newSmallFont.drawCenteredString("objectId = "+sound.objectId,
                            client.spriteDrawX, client.spriteDrawY - 3 * offset, 0xffffff, 1);
                    client.newSmallFont.drawCenteredString("id(s) = "+sound.soundEffectId+", "+ Arrays.toString(sound.soundEffectIds),
                            client.spriteDrawX, client.spriteDrawY - 4 * offset, 0xffffff, 1);*/
                }
            }
        }
        for(int i = 0; i < soundEffectCount; ++i) {

            if(soundLocations[i] != 0) {
                final int soundRadius = (soundLocations[i] & 255) * 128;
                final int soundLocalX = (soundLocations[i] >> 16 & 255);
                final int soundLocalY = (soundLocations[i] >>  8 & 255);
                final int soundWorldX = soundLocalX * 128 + 64;
                final int soundWorldY = soundLocalY * 128 + 64;
                final int deltaX = Math.abs(soundWorldX - Client.localPlayer.x);
                final int deltaY = Math.abs(soundWorldY - Client.localPlayer.y);
                final int distance = Math.min(0, deltaX + deltaY - 128);

                if(soundRadius == 0)
                    continue;

                final int volume = (soundRadius - distance) * areaSoundEffectVolume / soundRadius;

                //System.out.println("processing area sound "+soundRadius+", "+distance+", "+volume+" , "+deltaX+", "+deltaY);
                if(distance > soundRadius)
                    continue;

                /*client.calcEntityScreenPos(soundWorldX, 64, soundWorldY);
                int offset = 12;

                client.newSmallFont.drawCenteredString("distance = " + distance+", "+soundRadius,
                        client.spriteDrawX, client.spriteDrawY - offset, 0xffffff, 1);
                client.newSmallFont.drawCenteredString("timer = "+queuedSoundEffectDelays[i],
                        client.spriteDrawX, client.spriteDrawY - 2 * offset, 0xffffff, 1);
                client.newSmallFont.drawCenteredString("id = "+soundEffectIds[i],
                        client.spriteDrawX, client.spriteDrawY - 4 * offset, 0xffffff, 1);*/
            }
        }
    }

    public static PcmPlayer newPcmPlayer(int index, int sampleRate) {
        if(index >= 0 && index < 2) {
            if(sampleRate < 256) {
                sampleRate = 256;
            }

            try {
                PcmPlayer pcmPlayer = pcmPlayerProvider.player();
                pcmPlayer.samples = new int[(PcmPlayer.isStereo?2:1) * 256];
                pcmPlayer.frequency = sampleRate;
                pcmPlayer.init();
                pcmPlayer.capacity = (sampleRate & -1024) + 1024;
                if(pcmPlayer.capacity > 16384) {
                    pcmPlayer.capacity = 16384;
                }

                pcmPlayer.open(pcmPlayer.capacity);
                if(pcmPlayerCount > 0 && soundSystem == null) {
                    soundSystem = new SoundSystem();
                    soundSystemExecutor = Executors.newScheduledThreadPool(1);
                    soundSystemExecutor.scheduleAtFixedRate(soundSystem, 0L, 10L, TimeUnit.MILLISECONDS);
                }

                if(soundSystem != null) {
                    if(soundSystem.players[index] != null) {
                        throw new IllegalArgumentException();
                    }

                    soundSystem.players[index] = pcmPlayer;
                }

                Log.info("Created new pcm player {index="+index+", sampleRate="+sampleRate+"}");
                return pcmPlayer;
            } catch (Throwable throwable) {
                Log.error("Failed to load pcm player", throwable);
                return new PcmPlayer();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static void changeMusicVolume(Client client, int newVolume) {
        boolean wasPlayingMusic = musicNotMuted();
        updateMusicTrackVolumeDirect(newVolume);

        if (musicNotMuted()!= wasPlayingMusic && !ClientCompanion.lowMemory) {
            if (musicNotMuted()) {
                if (!Client.loggedIn) {
                    Audio.requestMusicTrack3(2, OsCache.indexCache6, "autumn voyage", "", 255, true);
                } else {
                    client.sendPacket(new SongFinished().create());
                }
            } else
                resetMusicTrack(2);
        }

        SettingsWidget.updateSettings();
        PlayerSettings.savePlayerData(client);
    }
}
