package com.grinder.model;

import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.cache.graphics.sprite.SpriteLoader;
import com.runescape.util.StringUtils;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @version 1.0
 * @since 12/12/2019
 */
public class EffectTimerManager {
    // Timers
    public static java.util.List<EffectTimer> effects_list = new CopyOnWriteArrayList<>();

    public static void addEffectTimer(EffectTimer et) {

        // Check if exists.. If so, update delay.
        for (EffectTimer timer : effects_list) {
            if (timer.getSprite() == et.getSprite()) {
                timer.setSeconds(et.getSecondsTimer().secondsRemaining());
                return;
            }
        }

        effects_list.add(et);
    }

    public static void drawEffectTimers(Client client) {
        int yDraw = ClientUI.frameHeight - 195 - client.resizeChatOffset();
        int xDraw = 435;
        for (EffectTimer timer : effects_list) {
            if (timer.getSecondsTimer().finished()) {
                effects_list.remove(timer);
                continue;
            }

            Sprite sprite = SpriteLoader.getSprite(timer.getSprite());

            if (sprite != null) {
                sprite.drawAdvancedSprite(xDraw + 12, yDraw);
                client.newSmallFont.drawBasicString(StringUtils.formatTimeInMinutes(timer.getSecondsTimer().secondsRemaining()) + "",
                        xDraw + 40, yDraw + 13, 0xFF8C00);
                yDraw -= 25;
            }
        }
    }
}
