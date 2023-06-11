package com.runescape.cache.graphics.widget;

import com.runescape.Client;

/**
 * Created by Stan van der Bend for Empyrean at 07/08/2018.
 *
 * @author https://www.rune-server.ee/members/StanDev/
 */
public interface GameScreenAdapter {

    void onSwitch(Client.ScreenMode newMode, Client.ScreenMode oldMode);
    void onResize(int newWidth, int newHeight);
}
