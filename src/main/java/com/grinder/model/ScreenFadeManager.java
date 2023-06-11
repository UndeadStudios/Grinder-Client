package com.grinder.model;

import com.google.common.base.Stopwatch;
import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.draw.Rasterizer3D;

import java.util.concurrent.TimeUnit;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 17/12/2019
 */
public class ScreenFadeManager {

    private int state;
    private int duration;
    private String text;
    private int fixedOpacity;

    private Stopwatch stopwatch;

    public ScreenFadeManager(int state, int duration, String text) {
        this.state = state;
        this.duration = state == 2 ? duration / 2 : duration;
        this.text = text;
        stopwatch = Stopwatch.createUnstarted();
    }

    /**
     * Constructor for fixed opacity black screen
     * TODO add color field (blue tint for underwater, brown for skill interfaces, etc)
     */
    public ScreenFadeManager(int opacity) {
        this.state = 3;
        this.fixedOpacity = opacity;
        this.duration = -1;
        this.text = null;
        stopwatch = Stopwatch.createUnstarted();

        System.out.println("INIT BLACK SCREEN");
    }
    public ScreenFadeManager(int state, int duration) {
        this(state, duration, "");
    }

    public void start(){
       stopwatch.start();
    }

    public void complete(){
        if(state == 2){
            stopwatch.reset();
            stopwatch.start();
            state = -1;
        } else {
            stopwatch.stop();
            state = 0;
        }
    }

    public boolean draw(){

        if(state == 0)
            return true;

        final int contentWidth = ClientUI.screenAreaWidth;
        final int contentHeight = ClientUI.screenAreaHeight;

        if(state == 3) {
            // solid black background, must be removed by interface removal or
            // calling fade packet with state 0
            Rasterizer3D.Rasterizer2D_setClip(0, contentHeight, contentWidth, 0);
            Rasterizer3D.drawTransparentBox(0, 0, contentWidth, contentHeight, 0x000000, fixedOpacity);
            return false;
        }

        double increment = (double) stopwatch.elapsed(TimeUnit.MILLISECONDS)
                / TimeUnit.SECONDS.toMillis(duration);

       // System.out.println(increment);

        int opacity = Math.min(255, (int) (increment * 255));

        if(state == -1)
            opacity = Math.max(0, 255-opacity);

        Rasterizer3D.Rasterizer2D_setClip(0, contentHeight, contentWidth, 0);
        Rasterizer3D.drawTransparentBox(0, 0, contentWidth, contentHeight, 0x000000, opacity);

        if (!text.isEmpty() && (increment > .85 && (state == 1 || state == 2) || increment < .5 && state == -1)) {
            final int textWidth = Client.instance.newFancyFont.getTextWidth(text);
            Client.instance.newFancyFont.drawBasicString(text, contentWidth / 2 - (textWidth / 2), contentHeight / 2, 0xFFFFFF, 0);
        }

        if ((state == -1 && opacity == 0) || opacity == 255)
            complete();

        return state == 0;
    }

    public void stop() {
        state = 0;
        stopwatch.stop();
    }

    /**
     * Solid color screen
     */
    public boolean isFixedOpacity() {
        return state == 3;
    }
}
