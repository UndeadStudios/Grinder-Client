package com.runescape.cache.graphics.widget;

import com.runescape.Client;
import com.runescape.cache.graphics.GameFont;
import com.runescape.cache.graphics.sprite.SpriteLoader;

public class QuestTab extends Widget {
    public static final int INTERFACE_ID = 31000;
    public static final int HELP_DESK_BUTTON = INTERFACE_ID + 1;
    public static final int MEMBER_TAB_BUTTON = INTERFACE_ID + 4;
    public static final int STAFF_TAB_BUTTON = INTERFACE_ID + 5;
    public static final int INTERFACE_CHILD_INDEX_FOR_PANEL = 20;
    public static final int HELP_DESK_PANEL = INTERFACE_ID + 100;
    public static final int ACHIEVEMENTS_PANEL = 80000;
    public static final int ACTIVITIES_PANEL = INTERFACE_ID + 300;
    public static final int MEMBER_TAB_PANEL = INTERFACE_ID + 400;
    public static final int STAFF_TAB_PANEL = INTERFACE_ID + 500;
    public static final int SERVER_INFORMATION_PANEL = INTERFACE_ID + 600;

    public static void widget(GameFont[] tda) {
        int id = 31000;
        Widget quest = addTabInterface(id);
        quest.totalChildren(23);
        int child = 0;

        int insetX = 2;
        int insetY = 1;

        String[] tabNames = new String[]{"Help Desk", "Achievements", "Wilderness", "Members Tab", "Staff Tab", "Server Information"};

        int tabBarConfig = 1150;
        int buttonWidth = SpriteLoader.getSprite(952).myWidth;
        for (int tab = 0; tab < tabNames.length; tab++) {
            addHoverResetSettingButton(id + 1 + tab, 952, 954, 953, "Select @gre@" + tabNames[tab], tab, tabBarConfig);
            interfaceCache[id + 1 + tab].drawsAdvanced = true;
            interfaceCache[id + 1 + tab].width = buttonWidth;
            quest.child(child++, id + 1 + tab, tab * buttonWidth + insetX, insetY);
        }

        int[] tabIcons = {791, 789, 794, 747, 790, 781};
        int[] tabIconOffsetsX = {0, 1, 1, 3, 1, 2, 0,};
        int[] tabIconOffsetsY = {0, 1, 1, 2, 2, 0, 0,};
        for (int icon = 0; icon < tabIcons.length; icon++) {
            addAdvancedSprite(id + 10 + icon, tabIcons[icon]);
            quest.child(child++, id + 10 + icon, icon * buttonWidth + insetX + 6 + tabIconOffsetsX[icon], insetY + 5 + tabIconOffsetsY[icon]);
        }

        int width = 186;
        int height = 233;

        // Dark border
        addVerticalLine(id - 1, height, 0x2e2b23);
        quest.child(child++, id - 1, insetX, insetY + 25);
        addVerticalLine(id - 2, height, 0x2e2b23);
        quest.child(child++, id - 2, insetX + (width - 1), insetY + 25);
        addHorizontalLine(id - 3, width, 0x2e2b23);
        quest.child(child++, id - 3, insetX, insetY + 25 + (height - 1));

        // Light border
        addVerticalLine(id - 4, height - 1, 0x726451);
        quest.child(child++, id - 4, insetX + 1, insetY + 25);
        addVerticalLine(id - 5, height - 1, 0x726451);
        quest.child(child++, id - 5, insetX + 1 + (width - 3), insetY + 25);
        addHorizontalLine(id - 6, width - 2, 0x726451);
        quest.child(child++, id - 6, insetX + 1, insetY + 25 + (height - 2));

        // Background color
        addTransparentRectangle(id - 7, width - 4, 21, 0x494034, true, 205);
        quest.child(child++, id - 7, insetX + 2, insetY + 26);
        // Title text divider
        addHorizontalLine(id - 8, width - 4, 0x726451);
        quest.child(child++, id - 8, insetX + 2, insetY + 47);

        // Load the subtitle background sprite
        addSprite(id + 99, 196);

        // The tab to display
        quest.child(child++, id + 100, insetX + 2, insetY + 26);

        // Load tab backgrounds
        addTransparentRectangle(30989, 182, 33, 0x494034, true, 205);
        addTransparentRectangle(30990, 182, 208, 0x2e2b23, true, 103);
        addTransparentRectangle(30991, 182, 208 - 34, 0x2e2b23, true, 103);

        int questTab = 65_300;

        addHoverResetSettingButton(questTab, 952, 954, 953, "Select @gre@Quests", 6, tabBarConfig);
        interfaceCache[questTab].drawsAdvanced = true;
        interfaceCache[questTab].width = buttonWidth;
        quest.child(child++, questTab, 6 * buttonWidth + insetX, insetY);
        questTab += 2;

        addAdvancedSprite(questTab, 1253);
        quest.child(child++, questTab, 6 * buttonWidth + insetX + 6, insetY + 5);

        // Load all of the tabs
        addQuestTabPanel(id + 100, tda, "Help Desk",
                new String[]{"Contribution", "Information", "Customization", "Social", "Guides"},
                new String[][]{{"Vote", "Store", "Redeem"},
                        {"Rules", "Skilling Task", "Daily Tasks", "Item Prices", "Item Drop Finder", "Boss Drop Tables", "Password Changer", "NPC Kill Tracker", "Staff Directory", "Online Staff", "Online Middleman"},
                        {"Rank Chooser", "Title Chooser", "Yell Customizer", "Ingame Hiscores", "My Commands", "Remove Bankpin",
                                "Add Security Email"},
                        {"Discord", "Facebook", "Twitter", "Youtube"}, {"PvM Guides", "Skilling Guides", "Money Making Guides", "Miscellaneous Guides"},}, false, false);


        addQuestTabPanel(80000, tda, "Achievements",
                new String[]{"Easy", "Medium", "Hard", "Elite", "Master", "Special"},
                new String[][]{

                        // 62 easy
                        {"Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement"
                        },


                        // 40 Medium
                        {"Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement"
                        },

                        // 56 Hard
                        {"Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement"
                        },

                        // 40 Elite
                        {"Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement"
                        },

                        // 38 Master
                        {"Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement"
                        },

                        // 20 Other
                        {"Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement",
                                "Achievement", "Achievement", "Achievement", "Achievement", "Achievement"
                        }
                }, false, false);

        addQuestTabPanel(id + 300, tda, "Wilderness",
                new String[]{"@red@Wilderness Boss Spawn (50k BM)", "@red@Porazdir Event Minigame (High Rewards)", "@red@Wilderness Information",},
                new String[][]{{"Time left:", "Teleport to:", "Bonus rewards loot",}, {"Respawn timer:", "Possible Loot"}, {"Wilderness:"},}, true, true);

        addQuestTabPanel(id + 400, tda, "Member's Tab",
                new String[]{"Information", "Teleports", "Features"},
                new String[][]{{"Your Rank:", "Total Spent:", "Member's features", "Purchase Premium Points"}, {"<img=1025> The Deserted Reef", "<img=745> La Isla Ebana", "<img=1026> The Cursed Vault", "<img=1027> The New Peninsula"}, {"::Bank", "::HP", "::Spec",
                        "Slayer KC",
                        "Kamil Entry KC",
                        "Shop Buy Limit",
                        "Unlimited Yells",
                        "Yell Customizer",
                        "GodWars Entry KC",
                        "Bonus Bank Space",
                        "Shift Drop Items",
                        "Run Energy Growth",
                        "Farming Grow Rate",
                        "Boosted Drop Rate",
                        "Clue Scroll Boost",
                        "Thieving Bonus Gold",
                        "Maxhit Equipment Tab",
                        "Motherlode Sack Bonus",
                        "Rug Merchant Discount",
                        "Flaxkeeper Daily Bonus",
                        "Note/Unnote on Bankbooth",
                        "Bonus Experience Modifier",
                        "Firecape Exchange Discount",
                }}, true, true);

        addQuestTabPanel(id + 500, tda, "Staff Tab",
                new String[]{"Server Support", "Moderator", "Global Moderator", "Administrator", "Co-owner",},
                new String[][]{{"Mute", "Unmute", "Jail", "Unjail", "Warn"}, // SERVER SUPPORTER
                        {"Kick", "Ban", "Unban", "Jail 2nd", "Ipmute", "Un-ipmute", "Ipban", "Un-ipban", "Lock", "Unlock", "Teleport To"}, // MODERATOR
                        {"Bank", "Hostban", "Un-hostban", "Teleport To-Me", "Move Home", "Checkbank", "Check Inventory", "Draw Lottery"}, // GLOBAL MODERATOR
                        {"Wipe Account", "Promote to Veteran", "Promote to Respected", "Promote to Ex-Staff", "Promote to Middleman", "Promote to Event Host", "Promote to Server Supporter"}, // ADMINISTRATOR
                        {"Promote to Moderator", "Demote", "Reset Password", "Reset Bank PIN", "Update Server"}, // CO_OWNER
                }, false, false);

        addQuestTabPanel(id + 600, tda, "Information",
                new String[]{"Server", "PvP", "Voting", "Points", "Account", "Statistics"},
                new String[][]{{"Player(s) online:", "Online time:", "Bonus Skill:", "Time left:",}, // Server
                        {"Kills:", "Deaths:", "KDR:", "Killstreak:", "Highest Killstreak:", "Carried Wealth:",}, // PvP
                        {"Voting Points:", "Streak:", "Streak Penalty:", "Next reward:",}, // Voting
                        {"Premium:", "Participation:", "Slayer:", "Slayer Streak:", "Yell:", "Skilling:", "Boss Contract:",}, // Points
                        {"XP Lock:", "Multiply CB XP Drops:", "Crush Vials:", "Join Date:", "Server Time:"}, // Account
                        {"Monsters slain:", "Bosses slain:", "Slayer NPC's slain:", "Spirits slain:", "Barrows looted:", "Trees chopped:", "Bones buried:", "Altar bones:", "Altar recharge:",
                                "Ores mined:", "Bars smelted:", "Gems cut:", "Seeds planted:", "Patches raked:", "Herbs cleaned:", "Potions made:", "Gnome laps:", "Barbarian laps:", "Wildy Laps:", "Pyramid laps:", "Total laps:", "Pickpockets:", "Failed picks:",
                                "Stall thieved:", "Runes crafted:", "Fish caught:", "Fishes cooked:", "Fishes burnt:", "Logs burned:", "Teletabs used:", "Out of prayer:", "Clan messages:", "Tele-other casts:", "Wizard teles:", "Alches:", "Special attacks:", "Veng casts", "Super-heat casts:", "Spells casted:", "Charges casted:", "Aquais Neige:", "Fight Caves:", "Boxes opened:", "Bolts enchanted:", "Food eaten:", "Pots drank:", "Muddy chests:", "Brimstone chests:", "Crystal chests:", "Rare drops:", "RDT drops:", "Ladders climbed:", "Times yelled:", "Emotes played:", "Glory teleports:", "Items dropped:", "Items picked:", "Pets received:", "Contracts completed:", "Contracts failed:", "Minigames won:", "Minigames lost:", "Gambles won:", "Gambles lost:", "Duel wins:", "Duel losses:", "Clues completed:", "Trades complete:", "Larran's chests:"}, // Statistics
                }, true, true);

        addQuestTabPanel(65_200, tda, "Quests",
                new String[]{"Free", "Special"},
                new String[][]{{  "", "", "", "", "", "", "", "", "", "", "", "", "", "","", "", "", "", "", "", "", },
                        {"", "", "", "", "","", "", "", },
                }, false, false);

    }

    public static void addQuestTabPanel(int id, GameFont[] tda, String title, String[] subtitles, String[][] lines, boolean amounts, boolean refreshButton) {
        boolean achievementPanel = id == 80000;

        Widget panel = addTabInterface(id);
        panel.totalChildren(achievementPanel ? 9 : refreshButton ? 4 : 3);
        int child = 0;

        addText(id + 1, title, tda, 2, 0xffb000);
        panel.child(child++, id + 1, 4, 3);

        int width = 182;
        int height = 208;
        int scrollHeight = height;
        if (achievementPanel)
            scrollHeight -= 34;

        panel.child(child++, achievementPanel ? 30991 : 30990, 0, 22);

        panel.child(child++, id + 2, 0, 22);

        Widget scroll = addTabInterface(id + 2);
        scroll.width = width - 16;
        scroll.height = scrollHeight;

        int linesAmt = 0;
        for (int i = 0; i < lines.length; i++) {
            for (int j = 0; j < lines[i].length; j++) {
                linesAmt++;
            }
        }

        scroll.scrollMax = (subtitles.length * 20) + (linesAmt * 15);
        if (scroll.scrollMax <= scroll.height) {
            scroll.width += 16;
        }

        if (amounts)
            linesAmt *= 2;

        scroll.totalChildren(subtitles.length * 2 + linesAmt);
        int scrollChild = 0;

        int y = 0;
        id += 10;
        for (int sub = 0; sub < subtitles.length; sub++) {
            // Background
            scroll.child(scrollChild++, 31099, 0, y);

            // Subtitle
            addText(id, subtitles[sub], tda, 0, 0xffb000);
            scroll.child(scrollChild++, id++, 4, y + 3);
            y += 20;

            // Lines
            for (int line = 0; line < lines[sub].length; line++) {
                addHoverClickText(id, lines[sub][line], "Select @gre@" + lines[sub][line], tda, 0, 0xEE9021, false, true, scroll.width);
                scroll.child(scrollChild++, id++, 9, y);

                if (amounts) {
                    //boolean joinDate = id == 31260 || id == 31262;
                    addRightAlignedText(id, id + " Player(s)", tda, 0, 0xB8B8B8, true);
                    scroll.child(scrollChild++, id++, scroll.width - 9, y); //joinDate ? 118 : 140
                }

                y += 15;
            }
        }

        if (refreshButton) {
            addAdvancedHoverButton(id, 879, 880, "Refresh");
            panel.child(child++, id++, width - SpriteLoader.getSprite(879).myWidth - 5, 5);
        } else if (achievementPanel) {
            int currY = 22 + scrollHeight;
            // Divider
            panel.child(child++, 30992, 0, currY);

            // Background
            panel.child(child++, 30989, 0, currY + 1);

            addText(id, "Total Completed:", tda, 0, 0xffb000);
            panel.child(child++, id++, 5, currY + 5);

            addText(id, "Achievement Points:", tda, 0, 0xffb000);
            panel.child(child++, id++, 5, currY + 20);

            addRightAlignedText(id, "0/0 " + id, tda, 0, 0xB8B8B8, true);
            panel.child(child++, id++, width - 4, currY + 5);

            addRightAlignedText(id, "0 " + id, tda, 0, 0xB8B8B8, true);
            panel.child(child++, id++, width - 4, currY + 20);
        }
    }

    public static void updateInterface() {
        Widget button = Widget.interfaceCache[HELP_DESK_BUTTON];
        int config = Client.instance.settings[button.valueIndexArray[0][1]];
        int panelIndex = (Widget.interfaceCache[INTERFACE_ID].children[INTERFACE_CHILD_INDEX_FOR_PANEL] - HELP_DESK_PANEL) / 100;
        int[] interfaceIds = {HELP_DESK_PANEL, ACHIEVEMENTS_PANEL, ACTIVITIES_PANEL, MEMBER_TAB_PANEL, STAFF_TAB_PANEL, SERVER_INFORMATION_PANEL, 65200};
        if (config != panelIndex) {
            Widget.interfaceCache[INTERFACE_ID].children[INTERFACE_CHILD_INDEX_FOR_PANEL] = interfaceIds[config];
        }
    }
}
