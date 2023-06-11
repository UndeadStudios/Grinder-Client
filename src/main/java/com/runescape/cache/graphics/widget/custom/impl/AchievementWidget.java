package com.runescape.cache.graphics.widget.custom.impl;

import com.runescape.cache.graphics.widget.custom.CustomWidget;

public class AchievementWidget extends CustomWidget {

    public AchievementWidget(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return "Achievement Diary";
    }

    @Override
    public void init() {

        addBackground(623);

        add(addCenteredText("Total completed: 0/100#", 0), 90, 280);
        add(addCenteredText("Achievement points#", 0), 90, 295);

        add(addCenteredText("Title#", 2), 335, 80);
        add(addCenteredText("Rewards", 2), 335, 140);
        add(addCenteredText("#", 0, 0xFFFFFF), 335, 173);
        add(addCenteredText("#", 0, 0xFFFFFFF), 335, 110);

        add(addItemContainer(6, 1, 0, 0, "rewards"), 243, 210);
        add(addPercentageBar(264, 100, 648, 649, 650, 100,1), 203, 279);
        add(addScrollbarWithClickText("#","Select", 0, OR1, 213, 137, 500), 20, 57);

        add(addCenteredText("View all Grinderscape Achievements and your progression.",0 ,OR1), 256, 39);
    }
}