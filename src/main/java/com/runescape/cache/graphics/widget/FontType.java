package com.runescape.cache.graphics.widget;

import com.runescape.Client;
import com.runescape.cache.graphics.GameFont;

/**
 * Created by Stan van der Bend for Empyrean at 19/06/2018.
 *
 * @author https://www.rune-server.ee/members/StanDev/
 */
public enum FontType {

    SMALL,
    NORMAL;

    public GameFont getRasterizer(){
        switch (this){
            case SMALL:
                return Client.instance.smallText;
            case NORMAL:
                default:
                return Client.instance.regularText;
        }
    }

}
