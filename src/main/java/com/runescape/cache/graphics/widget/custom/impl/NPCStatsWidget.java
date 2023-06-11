package com.runescape.cache.graphics.widget.custom.impl;

import com.runescape.cache.graphics.widget.custom.CustomWidget;

public class NPCStatsWidget extends CustomWidget {

    private static final String[] TYPE = {
            "Stab#", "Slash#", "Stab#", "Slash#", "Range#", "Magic#",
    };

    private static final int[] ATTACK_ICONS = {
           1088, 1071, 1079, 1072, 1074, 1077,
    };

    private static final int[] DEFENCE_ICONS = {
            1081, 1082, 1083, 1074, 1077,
    };
    private static final int[] OTHER_ICONS = {
            1087, 1075, 1076
    };
    public NPCStatsWidget(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return "NPC Stats Viewer";
    }

    @Override
    public void init() {
        addBackground(1070);

        add(addCenteredText("View the monsters stats levels and other information.", 0, OR1), 256, 40);

        add(addCenteredText("NPC NAME#", 4), 256, 60);

        add(addCenteredText("Combat Stats", 2), 150, 100);
        add(addCenteredText("Defence Stats", 2), 350, 100);

        add(addTextList(TYPE, 0, OR1, false, 16, true), 67, 124);
        add(addTextList(TYPE, 0, OR1, false, 16, true), 259, 131);

        add(addCenteredText("Other stats:", 2), 256, 220);

        add(addTextList(new String[]{"Weakness#", "Immunity#", "Protection Prayer#"}, 0, OR1, false, 13, true), 155, 236);

        add(addText("Note:#", 0, 0xFFFFFFF), 60, 284);

        add(addSprite(1084), 82, 92);
        add(addSprite(1085), 283, 96);
        add(addSprite(1086), 204, 221);

        add(addSpriteList(ATTACK_ICONS, 10, true), 100, 123);
        add(addSpriteList(DEFENCE_ICONS, 10, true), 290, 130);
        add(addSpriteList(OTHER_ICONS, 8, true), 186, 234);
    }
}
