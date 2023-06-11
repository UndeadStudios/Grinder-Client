package com.runescape.clock;

import java.util.Arrays;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 11/12/2019
 */
public class MilliClock extends Clock {

    private final long[] measuredAverages;
    private int cycleStep;
    private int sleepTime;
    private int cycleOnset;
    private int measurementIndex;
    private long measurement;

    public MilliClock(){
        measuredAverages = new long[10];
        cycleStep = 256;
        sleepTime = 1;
        cycleOnset = 0;
        measurement = Time.currentTimeMillis();
        Arrays.fill(measuredAverages, measurement);
    }

    @Override
    public void mark() {
        Arrays.fill(measuredAverages, 0L);
    }

    @Override
    public int wait(int var1, int var2) {
        int var3 = this.cycleStep;
        int var4 = sleepTime;
        this.cycleStep = 300;
        sleepTime = 1;
        this.measurement = Time.currentTimeMillis();
        if(0L == measuredAverages[measurementIndex]) {
            this.cycleStep = var3;
            sleepTime = var4;
        } else if(measurement > measuredAverages[this.measurementIndex]) {
            this.cycleStep = (int)((long)(var1 * 2560) / (measurement - measuredAverages[this.measurementIndex]));
        }

        if(this.cycleStep < 25) {
            this.cycleStep = 25;
        }

        if(this.cycleStep > 256) {
            this.cycleStep = 256;
            sleepTime = (int)((long)var1 - (measurement - measuredAverages[this.measurementIndex]) / 10L);
        }

        if(sleepTime > var1) {
            sleepTime = var1;
        }

        measuredAverages[this.measurementIndex] = measurement;
        this.measurementIndex = (this.measurementIndex + 1) % 10;
        int i;
        if(sleepTime > 1) {
            for(i = 0; i < 10; ++i) {
                if(measuredAverages[i] != 0L) {
                    measuredAverages[i] += sleepTime;
                }
            }
        }

        if(sleepTime < var2)
            sleepTime = var2;

        Time.sleep(sleepTime);

        for(i = 0; cycleOnset < 256; cycleOnset += cycleStep) {
            ++i;
        }
        cycleOnset &= 255;
        return i;
    }
}
