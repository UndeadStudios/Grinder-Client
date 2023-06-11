package com.runescape.cache.graphics.widget;

import com.grinder.Configuration;
import com.runescape.cache.graphics.GameFont;
import com.runescape.cache.graphics.sprite.AutomaticSprite;

public class ExpCounterSetup extends Widget {

    public static final int POSITION_DROPDOWN = 55239;
    public static final int SIZE_DROPDOWN = 55237;
    public static final int SPEED_DROPDOWN = 55235;
    public static final int MULTIPLY_DROPDOWN = 55233;
    public static final int TYPE_DROPDOWN = 55231;
    public static final int PROGRESS_DROPDOWN = 55229;
    public static final int COLOUR_DROPDOWN = 55227;
    public static final int GROUP_DROPDOWN = 55225;

    public static final int MULTIPLY_QUEST_TAB = 31659;

    public static void widget(GameFont[] tda) {
        int id = 55200;
        Widget setup = addInterface(id++);
        setup.totalChildren(19);
        int child = 0;

        addBackground(id, 426, 153, true, AutomaticSprite.BACKGROUND_BLACK);
        setup.child(child++, id++, 43, 80);

        addCloseButton(id, true);
        setup.child(child++, id++, 441, 87);

        addText(id, "Configure XP Drops", tda, 2, 0xff981f, true);
        setup.child(child++, id++, 43 + (426 / 2), 90);

        id++; // reserved id for sprite background to possibly add in the future

        id += 20; // reserved id space because additional drop downs are added at the beginning

        /* Second Row */

        String[] options = new String[] { "Off", "On" };
        dropdownMenu(GROUP_DROPDOWN, 70, 0, options, Dropdown.EXP_DROPS_GROUP);
        setup.child(child++, id++, 381, 194);
        addText(id, "Group", tda, 2, 0xff981f, true);
        setup.child(child++, id++, 381 + (70 / 2), 174);

        options = new String[] { "<col=ffffff>White", "<col=c8c8ff>Lilac", "<col=65535>Cyan", "<col=c8ffc8>Jade", "<col=64ff64>Lime", "<col=ffff40>Yellow", "<col=ff981f>Orange", "<col=ffc8c8>Pink" };
        dropdownMenu(COLOUR_DROPDOWN, 85, 0, options, Dropdown.EXP_DROPS_COLOUR);
        setup.child(child++, id++, 291, 194);
        addText(id, "Colour", tda, 2, 0xff981f, true);
        setup.child(child++, id++, 291 + (85 / 2), 174);

        options = new String[] { "Off", "Most recent" };
        dropdownMenu(PROGRESS_DROPDOWN, 110, 0, options, Dropdown.EXP_COUNTER_PROGRESS);
        setup.child(child++, id++, 176, 194);
        addText(id, "Progress bar", tda, 2, 0xff981f, true);
        setup.child(child++, id++, 176 + (110 / 2), 174);

        options = new String[] { "Total XP", "Most recent", "Off" };
        dropdownMenu(TYPE_DROPDOWN, 110, 0, options, Dropdown.EXP_COUNTER_TYPE);
        setup.child(child++, id++, 61, 194);
        addText(id, "Counter", tda, 2, 0xff981f, true);
        setup.child(child++, id++, 61 + (110 / 2), 174);

        /* First row */

        options = new String[] { "Off", "On" };
        dropdownMenu(MULTIPLY_DROPDOWN, 110, 1, options, Dropdown.EXP_COUNTER_MULTIPLY);
        setup.child(child++, id++, 341, 148);
        addText(id, "Multiply", tda, 2, 0xff981f, true);
        setup.child(child++, id++, 341 + (110 / 2), 128);

        options = new String[] { "Default", "Slower", "Faster" };
        dropdownMenu(SPEED_DROPDOWN, 75, 0, options, Dropdown.EXP_DROPS_SPEED);
        setup.child(child++, id++, 261, 148);
        addText(id, "Speed", tda, 2, 0xff981f, true);
        setup.child(child++, id++, 261 + (75 / 2), 128);

        options = new String[] { "Smallest", "Medium", "Large" };
        dropdownMenu(SIZE_DROPDOWN, 95, 0, options, Dropdown.EXP_COUNTER_SIZE);
        setup.child(child++, id++, 161, 148);
        addText(id, "Size", tda, 2, 0xff981f, true);
        setup.child(child++, id++, 161 + (95 / 2), 128);

        options = new String[] { "Right", "Middle", "Left" };
        dropdownMenu(POSITION_DROPDOWN, 95, 0, options, Dropdown.EXP_COUNTER_POSITION);
        setup.child(child++, id++, 61, 148);
        addText(id, "Position", tda, 2, 0xff981f, true);
        setup.child(child++, id++, 61 + (95 / 2), 128);
    }

    public static void updateSettings() {
        DropdownMenu drop = Widget.interfaceCache[POSITION_DROPDOWN].dropdown;
        drop.setSelected(drop.getOptions()[Configuration.xpDropsPosition]);

        drop = Widget.interfaceCache[SIZE_DROPDOWN].dropdown;
        drop.setSelected(drop.getOptions()[Configuration.xpCounterSize]);

        drop = Widget.interfaceCache[SPEED_DROPDOWN].dropdown;
        drop.setSelected(drop.getOptions()[Configuration.xpDropsSpeed]);

        drop = Widget.interfaceCache[TYPE_DROPDOWN].dropdown;
        drop.setSelected(drop.getOptions()[Configuration.xpCounterType]);

        drop = Widget.interfaceCache[PROGRESS_DROPDOWN].dropdown;
        drop.setSelected(drop.getOptions()[Configuration.xpCounterProgress]);

        drop = Widget.interfaceCache[COLOUR_DROPDOWN].dropdown;
        drop.setSelected(drop.getOptions()[ExpDropsColor.getIndexForColor(Configuration.xpDropsColour)]);

        drop = Widget.interfaceCache[GROUP_DROPDOWN].dropdown;
        drop.setSelected(drop.getOptions()[Configuration.xpDropsGroup ? 1 : 0]);
    }

    // Separate method for multiply setting as it's set cheaphaxy using quest tab string
    public static void updateMultiplySetting() {
        DropdownMenu drop = Widget.interfaceCache[MULTIPLY_DROPDOWN].dropdown;
        drop.setSelected(drop.getOptions()["@gre@On".equals(Widget.interfaceCache[MULTIPLY_QUEST_TAB].getDefaultText()) ? 1 : 0]);
    }

    public enum ExpDropsColor {
        WHITE(0xffffff),
        LILAC(0xc8c8ff),
        CYAN(0x00ffff),
        JADE(0xc8ffc8),
        LIME(0x64ff64),
        YELLOW(0xffff40),
        ORANGE(0xff981f),
        PINK(0xffc8c8);

        private final int color;

        ExpDropsColor(int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
        }

        public static int getColorForIndex(int index) {
            return ExpDropsColor.values()[index].getColor();
        }

        public static int getIndexForColor(int color) {
            for (ExpDropsColor expDropsColor : values()) {
                if (expDropsColor.getColor() == color) {
                    return expDropsColor.ordinal();
                }
            }
            return 0;
        }

    }
}
