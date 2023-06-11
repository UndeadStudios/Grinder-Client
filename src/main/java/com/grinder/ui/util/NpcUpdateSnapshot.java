package com.grinder.ui.util;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 25/03/2020
 */
public class NpcUpdateSnapshot extends Snapshot {

    private final int packetSize;

    private final int streamOnset;

    private final int npcListOnset;
    private final int npcSynchronizationOnset;

    private final int currentNpcCount;
    private final int currentRemovedCount;
    private final int currentAwaitingUpdateCount;


    public NpcUpdateSnapshot(int packetSize, int streamOnset, int npcListOnset, int npcSynchronizationOnset, int currentNpcCount, int currentRemovedCount, int currentAwaiwaingUpdateCount) {
        this.packetSize = packetSize;
        this.streamOnset = streamOnset;
        this.npcListOnset = npcListOnset;
        this.npcSynchronizationOnset = npcSynchronizationOnset;
        this.currentNpcCount = currentNpcCount;
        this.currentRemovedCount = currentRemovedCount;
        this.currentAwaitingUpdateCount = currentAwaiwaingUpdateCount;
    }


}
