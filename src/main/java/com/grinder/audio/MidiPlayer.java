package com.grinder.audio;

import com.grinder.client.GameShell;
import com.runescape.audio.Audio;
import com.runescape.audio.MusicTrack;
import com.runescape.cache.OsCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MidiPlayer {

    public static void main(String[] args) throws IOException {

        OsCache.loadAndWait();

        GameShell.taskHandler.newThreadTask(() -> {
            while(true){
                Audio.methodCycleMidiPlayers();
                Audio.methodDrawMusicTrack();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1);

        final byte[] bytes = Files.readAllBytes(Paths.get("/Users/stanvanderbend/Desktop/grinder-login.mid"));
        MusicTrack musicTrack = new MusicTrack(bytes);

        Audio.requestMusicTrack(musicTrack);
    }

}
