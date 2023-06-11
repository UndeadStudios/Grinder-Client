package com.runescape.cache.graphics.widget;

import com.runescape.cache.graphics.GameFont;
import com.runescape.cache.graphics.sprite.AutomaticSprite;
import com.runescape.util.StringUtils;

public class TitleChooser extends Widget {

    public static final int INTERFACE_ID = 78100;
    public static final int NAV_BUTTON_START_ID = INTERFACE_ID + 13;
    public static final int SCROLL_START_ID = NAV_BUTTON_START_ID + Title.values().length * 2;


    public enum Title {
        PLAYER_KILLING(0xab0000, 33),
        MONSTER_KILLING(0x104ebe, 17),
        SKILLING(0x23d500, 31),
    	MINIGAMES(0xff3301, 7),
    	ACHIEVEMENTS(0x23d500, 12),
    	BUYABLES(0xfc9b06, 44),
    	TASKS(0xff01e8, 12),
        OTHER(0xd3cc12, 36),
        CUSTOM(0xd8bc12, 4)
        ;

        private int color;
        private int amount;

        Title(int color, int amount) {
            this.color = color;
            this.amount = amount;
        }

        public int getColor() {
            return color;
        }

        public int getAmount() {
            return amount;
        }

        public String getFormattedName() {
            return StringUtils.capitalizeEachWord(toString().replace("_", " "));
        }

    }

    public static boolean isComponent(int interfaceId) {
        return interfaceId >= TitleChooser.INTERFACE_ID && interfaceId < TitleChooser.INTERFACE_ID + 1900;
    }

    public static void widget(GameFont[] tda) {
        int id = INTERFACE_ID;

        int titles = Title.values().length;

        Widget w = addInterface(id++);
        w.totalChildren(12 + titles * 2);
        int child = 0;

        int width = 498;
        int height = 314;
        int x = (512 - width) / 2;
        int y = (334 - height) / 2;

        addBasics("Title Chooser", tda, w, child, id, x, y, width, height, AutomaticSprite.BACKGROUND_BROWN);
        interfaceCache[id + 2].textColor = 0xffb000;
        child += 3;
        id += 3;

        int buttonWidth = 128;
        int buttonHeight = 22;

        addVerticalDivider(id, height - 40, AutomaticSprite.DIVIDER_VERTICAL_SMALL_BROWN);
        w.child(child++, id++, x + 6 + buttonWidth, y + 34);

        int subtitleHeight = 17;

        addTransparentRectangle(id, buttonWidth, subtitleHeight, 0, true, 56);
        w.child(child++, id++, x + 6, y + 34);

        addText(id, "Category", tda, 0, 0xffb000, true);
        w.child(child++, id++, x + 6 + (buttonWidth / 2), y + 37);

        addHorizontalDivider(id, buttonWidth, AutomaticSprite.DIVIDER_HORIZONTAL_SMALL_BROWN);
        w.child(child++, id++, x + 6, y + 34 + subtitleHeight);

        addHorizontalDivider(id, buttonWidth, AutomaticSprite.DIVIDER_HORIZONTAL_SMALL_BROWN);
        w.child(child++, id++, x + 6, y + titles * buttonHeight + 35 + subtitleHeight + 2);

        addHorizontalDivider(id, buttonWidth, AutomaticSprite.DIVIDER_HORIZONTAL_SMALL_BROWN);
        w.child(child++, id++, x + 6, y + 286);

        addTransparentRectangle(id, buttonWidth, 19, 0, true, 56);
        w.child(child++, id++, x + 6, y + 288);

        int spriteId = id;
        addAdvancedSprite(id++, 1019); // Load the title panel background

        addText(id, "<col=0xab0000>Killer</col> Xplicit", tda, 0, 0xB8B8B8, true);
        w.child(child++, id++, x + 6 + (buttonWidth / 2), y + 292);

        for (int i = 0; i < titles; i++) {
            addHoverResetSettingButton(id, 1016, 1017, 1018, "View " + Title.values()[i].getFormattedName() + " titles", i, 1153);
            interfaceCache[id].drawsAdvanced = true;
            interfaceCache[id].height = buttonHeight;
            w.child(child++, id++, x + 6, y + subtitleHeight + 2 + 34 + i * buttonHeight);

            addText(id, Title.values()[i].getFormattedName(), tda, 0, 0xffb000, true);
            w.child(child++, id++, x + 6 + (buttonWidth / 2), y + subtitleHeight + 2 + 40 + i * buttonHeight);
        }

        for (int i = 0; i < titles; i++) {
            Title title = Title.values()[i];
            scrollWidget(id, spriteId, tda, title.getFormattedName(), title.getAmount());
            if (i == 0)
                w.child(child++, id, x + 6 + buttonWidth + 3, y + 34);
            id += Title.values()[i].getAmount() * 4 + 3;
        }

    }

    public static void scrollWidget(int id, int spriteId, GameFont[] tda, String name, int titles) {
        Widget scroll = addInterface(id++);
        scroll.totalChildren(titles * 5 + 2);
        scroll.width = 354 - 16;
        scroll.height = 273;
        scroll.scrollMax = 111 * (titles / 3) + 5 + 46;
        int scrollChild = 0;

        addAdvancedSprite(id, 1022);
        scroll.child(scrollChild++, id++, 5, 6);

        addText(id, name + " Titles", tda, 4, 0xffb000, true);
        scroll.child(scrollChild++, id++, scroll.width / 2, 10);

        int titleX = 5;
        int titleY = 51;
        for (int j = 0; j < titles; j++) {

            scroll.child(scrollChild++, spriteId, titleX, titleY);

            addText(id, "" + id, tda, 1, 0xffb000, true);
            scroll.child(scrollChild++, id++, titleX + (106 / 2), titleY + 5);

            addText(id, "" + id, tda, 0, 0xB8B8B8, true);
            scroll.child(scrollChild++, id++, titleX + (106 / 2), titleY + 28);

            int bWidth = 82;
            addHoverButton(id, bWidth, 30, AutomaticSprite.BUTTON_BROWN, AutomaticSprite.BUTTON_BROWN_HOVER, "Select");
            scroll.child(scrollChild++, id++, titleX + ((106 - bWidth) / 2), titleY + 69);

            addText(id, "" + id, tda, 3, 0xffb000, true);
            scroll.child(scrollChild++, id++, titleX + ((106 - bWidth) / 2) + (bWidth / 2), titleY + 74);

            if ((j + 1) % 3 == 0) {
                titleX = 5;
                titleY += 111;
            } else {
                titleX += 111;
            }
        }
    }

    public static void handleButton(int button) {
        if (button >= NAV_BUTTON_START_ID && button <= NAV_BUTTON_START_ID + (Title.values().length * 2)) {
            Widget w = Widget.interfaceCache[INTERFACE_ID];
            int index = (button - NAV_BUTTON_START_ID) / 2;
            int offset = 0;
            for (int i = 0; i < index; i++)
                offset += Title.values()[i].getAmount() * 4 + 3;
            w.children[w.children.length - 1] = SCROLL_START_ID + offset;
        }
    }

}
