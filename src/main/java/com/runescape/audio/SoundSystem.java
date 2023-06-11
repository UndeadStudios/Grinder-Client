package com.runescape.audio;

import com.grinder.client.util.Log;

/**
 * @version 1.0
 * @since 13/02/2020
 */
public class SoundSystem implements Runnable {

    volatile PcmPlayer[] players;

    SoundSystem() {
        this.players = new PcmPlayer[2];
    }

    @Override
    public void run() {
        try {
            for(int i = 0; i < 2; ++i) {
                PcmPlayer pcmPlayer = this.players[i];
                if(pcmPlayer != null) {
                    pcmPlayer.run();
                }
            }
        } catch (Exception e) {
            Log.error("Failed to run sound system", e);
        }

    }
}
