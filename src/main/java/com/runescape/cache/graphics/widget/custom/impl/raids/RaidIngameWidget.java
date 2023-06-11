package com.runescape.cache.graphics.widget.custom.impl.raids;

import com.runescape.cache.graphics.widget.custom.CustomWidget;
/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class RaidIngameWidget extends CustomWidget {

    public RaidIngameWidget() {
        super(23364);
    }

    @Override
    public String getName() {
        return "Raid Ingame Widget";
    }

    @Override
    public void init() {
        add(addTransparentSprite(1285, 126), 15, 50);

        add(addText("#", 0), 20, 55);
        add(addText("#", 0), 20, 75);
    }
}
