package com.grinder.model;

import com.runescape.cache.def.NpcDefinition;
import com.runescape.util.SecondsTimer;
import com.runescape.util.StringUtils;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 07/04/2020
 */
public class MonsterHuntTracker {

    private final SecondsTimer secondsTimer;
    private final int npcId;

    public MonsterHuntTracker(int npcId, int secondsLeft) {
        this.secondsTimer = new SecondsTimer();
        this.npcId = npcId;
        secondsTimer.start(secondsLeft);
    }

    public boolean expired(){
        return secondsTimer.finished();
    }

    public String getTimeLeftAsString(){
        return StringUtils.formatTimeInMinutes(secondsTimer.secondsRemaining());
    }

    public String getNpcName(){
        return NpcDefinition.lookup(npcId).name;
    }

    public int getNpcId() {
        return npcId;
    }
}
