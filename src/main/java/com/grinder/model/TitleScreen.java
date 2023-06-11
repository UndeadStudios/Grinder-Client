package com.grinder.model;

import com.runescape.cache.IndexCache;
import com.runescape.cache.IndexedSprite;
import com.runescape.cache.OsCache;
import com.runescape.util.SpriteUtil;

public class TitleScreen {

    static IndexedSprite[] anIndexedSpriteArray5;
    static boolean isInitialised = false;

    public static void load(){
        final byte[] titleData = OsCache.indexCache10.takeRecordByNames("title.jpg", "");
        Flames.leftFlameSpritePixels = SpriteUtil.load(titleData);
        Flames.rightFlameSpritePixels = Flames.leftFlameSpritePixels.copy();
        anIndexedSpriteArray5 = IndexedSprite.method670(OsCache.indexCache8, "runes", "");
        Flames.init();
        isInitialised = true;
    }

    public static int unpack(IndexCache titleScreenCache, IndexCache spritesCache) {
        int int_0 = 0;
        if (titleScreenCache.tryLoadRecordByNames("title.jpg", ""))
            ++int_0;
        if (spritesCache.tryLoadRecordByNames("logo", ""))
            ++int_0;
        if (spritesCache.tryLoadRecordByNames("logo_deadman_mode", ""))
            ++int_0;
        if (spritesCache.tryLoadRecordByNames("titlebox", ""))
            ++int_0;
        if (spritesCache.tryLoadRecordByNames("titlebutton", ""))
            ++int_0;
        if (spritesCache.tryLoadRecordByNames("runes", ""))
            ++int_0;
        if (spritesCache.tryLoadRecordByNames("title_mute", ""))
            ++int_0;
        if (spritesCache.tryLoadRecordByNames("options_radio_buttons,0", ""))
            ++int_0;
        if (spritesCache.tryLoadRecordByNames("options_radio_buttons,2", ""))
            ++int_0;
        if (spritesCache.tryLoadRecordByNames("options_radio_buttons,4", ""))
            ++int_0;
        if (spritesCache.tryLoadRecordByNames("options_radio_buttons,6", ""))
            ++int_0;
        spritesCache.tryLoadRecordByNames("sl_back", "");
        spritesCache.tryLoadRecordByNames("sl_flags", "");
        spritesCache.tryLoadRecordByNames("sl_arrows", "");
        spritesCache.tryLoadRecordByNames("sl_stars", "");
        spritesCache.tryLoadRecordByNames("sl_button", "");
        return int_0;
    }
}
