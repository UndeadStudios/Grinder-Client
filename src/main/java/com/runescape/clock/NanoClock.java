package com.runescape.clock;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 11/12/2019
 */
public class NanoClock extends Clock {

    private long lastTimeNano;

    public NanoClock() {
        lastTimeNano = System.nanoTime();
    }

    @Override
    public void mark() {
        lastTimeNano = System.nanoTime();
    }

    @Override
    public int wait(int max, int min) {
        long minNano = 1000000L * (long)min;
        long timeDifference = this.lastTimeNano - System.nanoTime();
        if(timeDifference < minNano) {
            timeDifference = minNano;
        }

        Time.sleep(timeDifference / 1000000L);
        long currentTime = System.nanoTime();

        int var9;
        for(var9 = 0; var9 < 10 && (var9 < 1 || this.lastTimeNano < currentTime); this.lastTimeNano += 1000000L * (long)max) {
            ++var9;
        }

        if(lastTimeNano < currentTime) {
            lastTimeNano = currentTime;
        }

        return var9;
    }
}
