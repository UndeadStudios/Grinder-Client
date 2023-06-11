package com.grinder.ui.util;

import com.runescape.Client;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 25/03/2020
 */
public class PlayerUpdateSnapshot extends Snapshot{

    private final int packetSize;

    private final int streamOnset;

    private final int playerMovementOnset;
    private final int playerListOnset;
    private final int playerSynchronizationOnset;

    private final int currentPlayerCount;
    private final int currentRemovedCount;
    private final int currentAwaitingUpdateCount;


    public PlayerUpdateSnapshot(int packetSize, int streamOnset, int playerMovementOnset, int playerListOnset, int playerSynchronizationOnset, int currentPlayerCount, int currentRemovedCount, int currentAwaiwaingUpdateCount) {
        this.packetSize = packetSize;
        this.streamOnset = streamOnset;
        this.playerMovementOnset = playerMovementOnset;
        this.playerListOnset = playerListOnset;
        this.playerSynchronizationOnset = playerSynchronizationOnset;
        this.currentPlayerCount = currentPlayerCount;
        this.currentRemovedCount = currentRemovedCount;
        this.currentAwaitingUpdateCount = currentAwaiwaingUpdateCount;
    }


}
