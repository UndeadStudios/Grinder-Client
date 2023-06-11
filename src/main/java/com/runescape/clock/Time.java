package com.runescape.clock;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 11/12/2019
 */
public class Time {

    static long currentTimeMillisOffset;
    static long currentTimeMillisLast;

    public static synchronized long currentTimeMillis(){
        long millis = System.currentTimeMillis();
        if(millis < currentTimeMillisLast){
            currentTimeMillisOffset += currentTimeMillisLast - millis;
        }
        currentTimeMillisLast = millis;
        return millis + currentTimeMillisOffset;
    }

    public static void sleep(long sleepMillis) {
        if(sleepMillis > 0L) {
            if(sleepMillis % 10L == 0L) {
                long sleepTime = sleepMillis - 1L;

                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException var8) {
                    var8.printStackTrace();
                }

                try {
                    Thread.sleep(1L);
                } catch (InterruptedException var7) {
                    var7.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(sleepMillis);
                } catch (InterruptedException var6) {
                    var6.printStackTrace();
                }
            }

        }
    }
}
