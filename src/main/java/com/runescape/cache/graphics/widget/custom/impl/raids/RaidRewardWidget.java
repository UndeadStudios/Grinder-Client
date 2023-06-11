package com.runescape.cache.graphics.widget.custom.impl.raids;

import com.runescape.cache.graphics.widget.custom.CustomWidget;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class RaidRewardWidget extends CustomWidget {
    public RaidRewardWidget() {
        super(23368);
    }

    @Override
    public String getName() {
        return "Rewards";
    }

    @Override
    public void init() {
        add(addBackground(1286, getName(), 300), 0, 0);

        add(addItemContainer(1,3, 0, 0, WITHDRAW_OPTIONS, "display"), 265, 130);
    }
}
