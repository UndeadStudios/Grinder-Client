package com.runescape.cache.graphics.widget.custom.impl.raids;

import com.runescape.cache.graphics.widget.custom.CustomWidget;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class RaidPartiesWidget extends CustomWidget {


    private static final String[] BUTTONS = {"Refresh", "My Party",};

    public RaidPartiesWidget() {
        super(65_400);
    }

    @Override
    public String getName() {
        return "Raiding Parties";
    }

    @Override
    public void init() {
        addBackground(1257);

        int x = 156;
        int y = 289;

        for (String s : BUTTONS) {
            add(addDynamicButton(s, 2, OR1, 100, 30), x, y);
            x += 120;
        }


        add(addScrollbarWithClickText("#", "Select", 0, OR1, 225, 420, 100), 40, 60);
    }
}
