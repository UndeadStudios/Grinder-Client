package com.runescape.cache.graphics.widget;

import com.runescape.Client;
import com.runescape.cache.graphics.GameFont;
import com.runescape.cache.graphics.sprite.AutomaticSprite;
import com.runescape.entity.Player;

import java.util.Arrays;

public class GameModeSetup extends Widget {

    public static final int INTERFACE_ID = 51200;
    public static final int NONE_BUTTON = 52257;
    public static final int ONE_LIFE_BUTTON = 52264;
    public static final int REALISM_BUTTON = 52271;
    public static final int CLASSIC_BUTTON = 52278;
    public static final int PURE_BUTTON = 52285;
    public static final int MASTER_BUTTON = 52292;
    public static final int SPAWN_BUTTON = 52299;
    public static final int STANDARD_BUTTON = 52306;
    public static final int HARDCORE_BUTTON = 52313;
    public static final int ULTIMATE_BUTTON = 52320;

    public static final int CONFIG_ID = 1152;

    public static void widget(GameFont[] tda) {
        int id = INTERFACE_ID;

        Widget w = addInterface(id++);

        w.totalChildren(10);
        int child = 0;

        int width = 480;
        int height = 331;
        int x = (512 - width) / 2;
        int y = (334 - height) / 2;

        // Background
        addBackground(id, width, height, true, AutomaticSprite.BACKGROUND_BLACK);
        w.child(child++, id++, x, y);

        // Close
        /*addCloseButton(id, true);
        w.child(child++, id++, x + width - 28, y + 7);*/
        id++;

        // Title
        addText(id, "Game Mode Setup", tda, 2, 0xff981f, true);
        w.child(child++, id++, 256, y + 10);

        // Character
        addChar(id);
        interfaceCache[id].contentType = 330;
        interfaceCache[id].modelZoom = 589;
        w.child(child++, id++, x + 370, y + 190);

        /**
         * Mode selection panel
         */

        int boxX = x + 11;
        int boxY = y + 40;

        int headerHeight = 21;
        int boxWidth = 390;

        addTransparentRectangle(id, boxWidth, headerHeight, 0xffffff, true, 15);
        w.child(child++, id++, boxX, boxY);

        addRectangle(id, boxWidth - 2, headerHeight - 1, 0x474745, false);
        w.child(child++, id++, boxX + 1, boxY + 1);

        addRectangle(id, boxWidth, 237, 0, false);
        w.child(child++, id++, boxX, boxY);

        addText(id, "Game Mode", tda, 1, 0xffffff, true);
        w.child(child++, id++, boxX + (boxWidth / 2), boxY + 3);

        int buttonY = 0;
        int configID = 0;
        int scrollChildren = 0;
        int buttonHeight = 21;

        int id2 = 52251;
        Widget scrollInterface = addInterface(52250);
        scrollInterface.width = boxWidth + 3;
        scrollInterface.height = 210;
        scrollInterface.scrollMax = 670;
        scrollInterface.totalChildren(70);


        buttonHeight = 55;
        addGameModeButton(scrollInterface, id2, scrollChildren, "None @or1@- <col=ffd5a5>75x Combat, 35x Skilling</col>", "No restrictions or perks will apply to this account. Recommended for\\ngeneral PvP & skilling gameplay. No drop rate boosts.", tda, boxWidth, buttonHeight, boxX - 5, buttonY, configID++);
        id2 += 7;
        scrollChildren += 7;
        buttonY += buttonHeight;

        buttonHeight = 65;
        addGameModeButton(scrollInterface, id2, scrollChildren, "<img=1235> One Life @or1@- <col=ffd5a5>75x Combat, 35x Skilling</col>", "Your account has @red@One Life</col> only. A dangerous death will render your\\naccount not able to perform any actions. Starting with a Unique Title\\n@gre@8%</col> Drop Rate Boost, Unique Crown, and Powerful Gear.", tda, boxWidth, buttonHeight, boxX - 5, buttonY, configID++);
        id2 += 7;
        scrollChildren += 7;
        buttonY += buttonHeight;


        addGameModeButton(scrollInterface, id2, scrollChildren, "<img=1236> Realism @or1@- <col=ffd5a5>15x Combat, 5x Skilling</col>", "Realism has Semi-OSRS XP rates which is lower than normal accounts.\\nStarting with a Unique Title, @gre@8%</col> Drop Rate Boost, Unique Crown, and\\nPowerful Gear.", tda, boxWidth, buttonHeight, boxX - 5, buttonY, configID++);
        id2 += 7;
        scrollChildren += 7;
        buttonY += buttonHeight;

        buttonHeight = 55;
        addGameModeButton(scrollInterface, id2, scrollChildren, "<img=77> Classic @or1@- <col=ffd5a5>50x Combat, 25x Skilling</col>", "Classic has lower experience rates than normal accounts. Starting\\nwith a Unique Title, @gre@5%</col> Drop Rate Boost, and a Unique Crown.", tda, boxWidth, buttonHeight, boxX - 5, buttonY, configID++);
        id2 += 7;
        scrollChildren += 7;
        buttonY += buttonHeight;

        buttonHeight = 85;
        addGameModeButton(scrollInterface, id2, scrollChildren, "<img=1238> Pure @or1@- <col=ffd5a5>625x Combat, 5x Skilling</col>", "Recommended for people wanting to jump into PKing. You can SELECT\\nyour PvP skills with a high combat experience rate. Low skilling\\nexperience rates (including prayer). Has a Unique Title, Unique Crown,\\nExtra PvP Rewards, and a @red@-5%</col> Decreased Drop Rate.", tda, boxWidth, buttonHeight, boxX - 5, buttonY, configID++);
        id2 += 7;
        scrollChildren += 7;
        buttonY += buttonHeight;

        buttonHeight = 85;
        addGameModeButton(scrollInterface, id2, scrollChildren, "<img=1237> Master @or1@- <col=ffd5a5>75 Combat, 5x Skilling</col>", "Recommended for people wanting to jump into PKing. Start with MAX\\ncombat skills. Low skilling experience rates (including prayer). Has a\\nUnique Title, Unique Crown, Extra PvP Rewards, and a @red@-8%</col>\\nDecreased Drop Rate.", tda, boxWidth, buttonHeight, boxX - 5, buttonY, configID++);
        id2 += 7;
        scrollChildren += 7;
        buttonY += buttonHeight;



        addGameModeButton(scrollInterface, id2, scrollChildren, "<img=1242> Spawn @or1@- <col=ffd5a5>75x Combat, 35x Skilling</col>", "An exclusive rank that can @gre@SPAWN</col> items and @gre@SET combat stats</col>.\\nCannot drop or pickup items and excluded from some minigames.\\nCan only trade, stake, and gamble with spawn mode players.\\nCannot attack NPC's attacked by other players and doesn't receive\\nany drops from slain NPC's. Spawn restrictions apply to certain items.", tda, boxWidth, buttonHeight, boxX - 5, buttonY, configID++);
        id2 += 7;
        scrollChildren += 7;
        buttonY += buttonHeight;

        buttonHeight = 55;
        addGameModeButton(scrollInterface, id2, scrollChildren, "<img=807> Standard Iron Man @or1@- <col=ffd5a5>75x Combat, 35x Skilling</col>", "An Iron Man can't trade, stake, receive PK loot, scavenge dropped\\nitems, nor play certain multiplayer minigames.", tda, boxWidth, buttonHeight, boxX - 5, buttonY, configID++);
        id2 += 7;
        scrollChildren += 7;
        buttonY += buttonHeight;

        buttonHeight = 65;
        addGameModeButton(scrollInterface, id2, scrollChildren, "<img=78> Hardcore Iron Man @or1@- <col=ffd5a5>75x Combat, 35x Skilling</col>", "In addition to the standard Iron Man rules, a Hardcore Iron Man only\\nhas 1 life. A dangerous death will result in being downgraded to a\\nstandard Iron Man.", tda, boxWidth, buttonHeight, boxX - 5, buttonY, configID++);
        id2 += 7;
        scrollChildren += 7;
        buttonY += buttonHeight;

        buttonHeight = 55;
        addGameModeButton(scrollInterface, id2, scrollChildren, "<img=808> Ultimate Iron Man @or1@- <col=ffd5a5>75x Combat, 35x Skilling</col>", "In addition to the standard Iron Man rules, an Ultimate Iron Man\\ncan't use banks, nor retain any items on death in dangerous areas.", tda, boxWidth, buttonHeight, boxX - 5, buttonY, configID++);
        id2 += 7;
        scrollChildren += 7;
        buttonY += buttonHeight;



        addHoverButton(id, 120, 38, AutomaticSprite.BUTTON_BIG_BROWN, AutomaticSprite.BUTTON_BROWN_HOVER, "Confirm");
        w.child(child++, id++, boxX + 130, boxY + 242);
        addText(id, "Confirm", tda, 2, 0xff981f, true);
        w.child(child++, id++, boxX + 190, boxY + 242 + 11);
        w.child(child++, 52250, x - 10, y + 65);
        System.out.println("Children in regular interface: " + child);

    }

    public static void addGameModeButton(Widget widget, int id, int child, String name, String description, GameFont[] tda, int width, int height, int x, int y, int configID) {
        addTransparentHoverRectangle(id, width, height, 0xffffff, true, 15, 30);
        widget.child(child++, id++, x, y);

        addRectangle(id, width - 2, height - 1, 0x474745, false);
        widget.child(child++, id++, x + 1, y + 1);

        addRectangle(id, width - 2, height - 1, 0x474745, false);
        widget.child(child++, id++, x + 1, y + 1);

        addHorizontalLine(id, width - 2, 0);
        widget.child(child++, id++, x + 1, y);

/*        if (name.equals("Normal @or1@- <col=ffd5a5>75x Combat, 35x Skilling, 15x Post-120</col>")) { // single line
            addText(id, name, tda, 0, 0xffffff);
            widget.child(child++, id++, x + 25, y + 5);
            addText(id, description, tda, 0, 0xff981f);
            widget.child(child++, id++, x + 25 + Client.instance.newSmallFont.getTextWidth("Normal @or1@- <col=ffd5a5>75x Combat, 35x Skilling, 15x Post-120</col> "), y + 5);
        } else {*/
            addText(id, name, tda, 0, 0xffffff);
            widget.child(child++, id++, x + 25, y + 5);
            addText(id, description, tda, 0, 0xff981f);
            widget.child(child++, id++, x + 25, y + 30);
      //  }

        addResetSettingButton(id, width, height, 6, (height - 15) / 2,873, 874, name, configID, CONFIG_ID);
        widget.child(child++, id++, x, y);
    }

    public enum IronmanEquip {
        NONE(new int[0]),
        ONE_LIFE(new int[] { 26156, 26158, 26166, 26168, 13319, 15716 }),
        REALISM(new int[] { 26170, 26172, 26180, 26182, 13319, 15717 }),
        CLASSIC(new int[0]),
        PURE(new int[0]),
        MASTER(new int[0]),
        SPAWN(new int[0]),
        STANDARD(new int[] { 12810, 12811, 12812 }),
        HARDCORE(new int[] { 20792, 20794, 20796 }),
        ULTIMATE(new int[] { 12813, 12814, 12815 });

        private int[] equipment;

        IronmanEquip(int[] equipment) {
            this.equipment = equipment;
        }

        public int[] getEquipment() {
            return equipment;
        }
    }

    public static void setEquipment(IronmanEquip ironEquip) {
        Player local = Client.localPlayer;
        int[] equip;
        boolean empty = Arrays.equals(ironEquip.equipment, new int[0]);

        if (empty) {
            equip = local.equipment;
        } else {
            equip = new int[] { ironEquip.equipment[0] + 512, 0, ironEquip.equipment[1] + 512, 0,
                    0, ironEquip.equipment[2] + 512, 0, 0,
                    0, local.equipment[9], local.equipment[10], 0 };
        }

        DummyPlayerModel.setPlayerModel(equip, 808);
    }

}
