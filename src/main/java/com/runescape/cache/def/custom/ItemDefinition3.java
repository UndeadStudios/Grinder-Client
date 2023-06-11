package com.runescape.cache.def.custom;

import com.runescape.cache.def.ItemDefinition;

import static com.runescape.cache.def.ItemDefinition.lookup;

public class ItemDefinition3 {

    public static void load(int id, ItemDefinition itemDef) {
        switch (id) {

            case 16056:
                itemDef.copy(lookup(15883));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Pernix cowl (midnight)";
                itemDef.original_model_colors = new int[] { 2, 86933, 5, 86933, 86933, 2, 5, 5, -28614, 5, 86933, 6, -28614, 86933 };
                itemDef.modified_model_colors = new int[] { 2, 4548, 5, 1030, 1032, 10, 12, 15, -19028, 20, -19030, 23, -19033, -19037 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 16057;
                break;
            case 16057:
                itemDef.copy(lookup(15884));
                itemDef.actions = new String[5];
                itemDef.name = "Pernix cowl (midnight)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 2, 86933, 5, 86933, 86933, 2, 5, 5, -28614, 5, 5056, 6, -28614, 86933 };
                itemDef.modified_model_colors = new int[] { 2, 4548, 5, 1030, 1032, 10, 12, 15, -19028, 20, -19030, 23, -19033, -19037 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 16056;
                break;
            case 16058:
                itemDef.copy(lookup(15885));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Pernix body (midnight)";
                itemDef.original_model_colors = new int[] { -28614, -28614, -28614, 7, 86933, 86933, 11, -28614, 86933, 15, 80, 785, 86933, 10323, 5, 6, 8, 86933, -28614, -28614, 798, 802, 10, -23524, -28614, 805, -28614, 10, -28614, -28614, 86933, 10, 86933, 86933, 86933, -28614, 10293, 86933, 86933, 15, 86933, 86933 };
                itemDef.modified_model_colors = new int[] { 10306, -14915, -14919, 7, 10311, 1033, 11, -14924, 23566, 15, 80, 785, 10322, 10323, 21, 22, 23, 10328, -14940, -23516, 798, 802, 34, -23524, 10276, 805, -14952, 39, -22505, 10283, 10284, 46, -22513, 10290, 10355, -14900, 10293, 10294, 10359, 56, 10299, 10300 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 16059;
                break;
            case 16059:
                itemDef.copy(lookup(15886));
                itemDef.actions = new String[5];
                itemDef.name = "Pernix body (midnight)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { -28614, -28614, -28614, 7, 86933, 86933, 11, -28614, 86933, 15, 80, 785, 86933, 10323, 21, 22, 23, 86933, -28614, -28614, 798, 802, 34, -23524, -28614, 805, -28614, 39, -28614, -28614, 86933, 46, 86933, 86933, 86933, -28614, 10293, 86933, 86933, 56, 86933, 86933 };
                itemDef.modified_model_colors = new int[] { 10306, -14915, -14919, 7, 10311, 1033, 11, -14924, 23566, 15, 80, 785, 10322, 10323, 21, 22, 23, 10328, -14940, -23516, 798, 802, 34, -23524, 10276, 805, -14952, 39, -22505, 10283, 10284, 46, -22513, 10290, 10355, -14900, 10293, 10294, 10359, 56, 10299, 10300 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 16058;
                break;
            case 16060:
                itemDef.copy(lookup(15887));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Pernix chaps (midnight)";
                itemDef.original_model_colors = new int[] { 0, -28614, 10, 86933, 86933, -28614, 15, 86933, 86933, -28614, -28614, 5, -28614, 8, 18, 86933, 86933, -28614, 86933, 6, -28614, -28614, 10 };
                itemDef.modified_model_colors = new int[] { 0, 10340, 37, -14951, -12264, -12267, 42, -12269, 10285, -12272, 10319, 16, -11250, 50, 18, 10355, 10262, -14935, 10359, 23, 10299, -14942, 30 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 16061;
                break;
            case 16061:
                itemDef.copy(lookup(15888));
                itemDef.actions = new String[5];
                itemDef.name = "Pernix chaps (midnight)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, -28614, 10, 86933, 86933, -28614, 15, 86933, 86933, -28614, -28614, 5, -28614, 8, 18, 86933, 86933, -28614, 86933, 6, -28614, -28614, 10 };
                itemDef.modified_model_colors = new int[] { 0, 10340, 37, -14951, -12264, -12267, 42, -12269, 10285, -12272, 10319, 16, -11250, 50, 18, 10355, 10262, -14935, 10359, 23, 10299, -14942, 30 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 16060;
                break;


            case 16062:
                itemDef.copy(lookup(15877));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Virtus mask (midnight)";
                itemDef.original_model_colors = new int[] { 0, -28614, 10, 86933, 86933, -28614, 15, 86933, 86933, -28614,
                        0, -28614, 10, 86933, 86933, -28614, 15, 86933, 86933, -28614, 0, -28614, 10, 86933, 86933, -28614, 15, 86933, 86933, -28614};

                itemDef.modified_model_colors = new int[] { 0, 10308, -31687, 10313, 10314, 11, 6220, -31693, -31695, 10319, 17, 10321, -16979, 19, 10324, -31703, -16984, -31640, -32729, 10331,
                        -32732, -31710, -31712, 10342, -32745, -31724, -32753, 10355, -31673, 10303 };

                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 16063;
                break;
            case 16063:
                itemDef.copy(lookup(15878));
                itemDef.actions = new String[5];
                itemDef.name = "Virtus mask (midnight)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, -28614, 10, 86933, 86933, -28614, 15, 86933, 86933, -28614,
                        0, -28614, 10, 86933, 86933, -28614, 15, 86933, 86933, -28614, 0, -28614, 10, 86933, 86933, -28614, 15, 86933, 86933, -28614};

                itemDef.modified_model_colors = new int[] { 0, 10308, -31687, 10313, 10314, 11, 6220, -31693, -31695, 10319, 17, 10321, -16979, 19, 10324, -31703, -16984, -31640, -32729, 10331,
                        -32732, -31710, -31712, 10342, -32745, -31724, -32753, 10355, -31673, 10303 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 16062;
                break;
            case 16064:
                itemDef.copy(lookup(15879));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Virtus robe top (midnight)";
                itemDef.original_model_colors = new int[] { 0, -28614, 10, 86933, 86933, -28614, 15, 86933, 86933, -28614, 0, -28614, 10, 86933, 86933, -28614, 15, 86933, 86933, -28614,
                        0, -28614, 10, 86933, 86933, -28614, 15, 86933, 86933, -28614, 0, -28614, 10, 86933, 86933, -28614, 15, 86933, 86933, -28614,
                        -28614, 86933, 5};

                itemDef.modified_model_colors = new int[] { 10308, -26826, 653, 6225, 6353, 785, -16979, 23570, -16984, -31704, 10328, 6297, 10330, -31708, 10331, -16989, 23580, -31709, 10335, 23585, -31714, 802,
                        6307, 10340, -31717, -31718, -31719, 10278, 22566, 10342, -31723, -30701, -30702, 6317, 10350, -31345, 23600, 10355, -31734, 10293, -31737, 10296, 23610 };

                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 16065;
                break;
            case 16065:
                itemDef.copy(lookup(15880));
                itemDef.actions = new String[5];
                itemDef.name = "Virtus robe top (midnight)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, -28614, 10, 86933, 86933, -28614, 15, 86933, 86933, -28614, 0, -28614, 10, 86933, 86933, -28614, 15, 86933, 86933, -28614,
                        0, -28614, 10, 86933, 86933, -28614, 15, 86933, 86933, -28614, 0, -28614, 10, 86933, 86933, -28614, 15, 86933, 86933, -28614,
                        -28614, 86933, 5};

                itemDef.modified_model_colors = new int[] { 10308, -26826, 653, 6225, 6353, 785, -16979, 23570, -16984, -31704, 10328, 6297, 10330, -31708, 10331, -16989, 23580, -31709, 10335, 23585, -31714, 802,
                        6307, 10340, -31717, -31718, -31719, 10278, 22566, 10342, -31723, -30701, -30702, 6317, 10350, -31345, 23600, 10355, -31734, 10293, -31737, 10296, 23610 };

                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 16064;
                break;
            case 16066:
                itemDef.copy(lookup(15881));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Virtus robe bottoms (midnight)";
                itemDef.original_model_colors = new int[] { 0, -28614, 10, 86933, 86933, -28614, 15, 86933, 86933, -28614, 0, -28614, 10, 86933, 86933, -28614, 15, 86933, 86933};
                itemDef.modified_model_colors = new int[] { 10339, 6308, -12264, 10280, -12269, 10317, 10285, -12272, 10319, 785, -16979, 10355, -12276, 10359, -16984, 792, -16989, 10333, 10270 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 16067;
                break;
            case 16067:
                itemDef.copy(lookup(15882));
                itemDef.actions = new String[5];
                itemDef.name = "Virtus robe bottoms (midnight)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, -28614, 10, 86933, 86933, -28614, 15, 86933, 86933, -28614, 0, -28614, 10, 86933, 86933, -28614, 15, 86933, 86933};
                itemDef.modified_model_colors = new int[] { 10339, 6308, -12264, 10280, -12269, 10317, 10285, -12272, 10319, 785, -16979, 10355, -12276, 10359, -16984, 792, -16989, 10333, 10270 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 16066;
                break;
            case 16068:
                itemDef.copy(lookup(1050));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Silver santa hat";
                itemDef.description = "A santa's hat.";
                itemDef.original_model_colors = new int[] { 70, 0 };
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                break;
            case 16070:
                itemDef.copy(lookup(1053));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Silver h'ween mask";
                itemDef.description = "Skrrrrrrrrr!";
                itemDef.original_model_colors = new int[] { 70, 0, 0, 0 };
                itemDef.modified_model_colors = new int[] { 926, 0, 127, 10349 };
                break;
            case 6585:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.actions[2] = "Breakdown";
                break;
            case 16071:
                itemDef.copy(lookup(23351));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Cape of winkle";
                itemDef.original_model_colors = new int[] { 115, 51138, 115, 51138, 51138, 115, 110 };
                itemDef.modified_model_colors = new int[] { 20, 10308, 39, 14395, 924, 30, 47 };
                break;
            case 16072: // Realism weapon
                itemDef.copy(lookup(13111));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Star power";
                itemDef.original_model_colors = new int[] { 6, 6, 38226, 38220, 38225, 38230, 38226, 38226, 38232, 38220, 38226, 38226, 10, 38226, 38226 };
                itemDef.modified_model_colors = new int[] { 49075, 50112, 43179, 43171, 43026, 43034, 43038, 43059, 43030, 43166, 43162, 43286, 49088, 43043, 43047 };
                break;
            case 16073: // Quest scrolls
                itemDef.copy(lookup(24361));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Clock Tower";
                break;
            case 16074: // Quest scrolls
                itemDef.copy(lookup(24361));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Cook's Assistant";
                break;
            case 16075: // Quest scrolls
                itemDef.copy(lookup(24361));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Doric's Quest";
                break;
            case 16076: // Quest scrolls
                itemDef.copy(lookup(24361));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Imp Catcher";
                break;
            case 16077: // Quest scrolls
                itemDef.copy(lookup(24361));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Sheep Shearer";
                break;
            case 16078: // Quest scrolls
                itemDef.copy(lookup(24361));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Witch's Potion";
                break;
            case 16079: // Quest scrolls
                itemDef.copy(lookup(24363));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Druidic Ritual";
                break;
            case 16080: // Quest scrolls
                itemDef.copy(lookup(24363));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Ernest The Chicken";
                break;
            case 16081: // Quest scrolls
                itemDef.copy(lookup(24363));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Gertrude's Cat";
                break;
            case 16082: // Quest scrolls
                itemDef.copy(lookup(24363));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Monk's Friend";
                break;
            case 16083: // Quest scrolls
                itemDef.copy(lookup(24363));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Rune Mysteries";
                break;
            case 16084: // Quest scrolls
                itemDef.copy(lookup(24363));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Goblin Diplomacy";
                break;
            case 16085: // Quest scrolls
                itemDef.copy(lookup(24363));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "The Restless Ghost";
                break;
            case 16086: // Quest scrolls
                itemDef.copy(lookup(24363));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Dwarf Cannon";
                break;
            case 16087: // Quest scrolls
                itemDef.copy(lookup(24363));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Lost City";
                break;
/*            case 16088: // Quest scrolls
                itemDef.copy(lookup(24363));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Monk's Friend";
                break;*/
            case 16089: // Quest scrolls
                itemDef.copy(lookup(24363));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Pirate's Treasure";
                break;
            case 16090: // Quest scrolls
                itemDef.copy(lookup(24363));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Waterfall Quest";
                break;
            case 16091: // Quest scrolls
                itemDef.copy(lookup(24365));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Dragon Slayer";
                break;
            case 16092: // Quest scrolls
                itemDef.copy(lookup(24365));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Monkey Madness";
                break;
            case 16093: // Quest scrolls
                itemDef.copy(lookup(24364));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Desert Treasure";
                break;
            case 16094: // Quest scrolls
                itemDef.copy(lookup(24366));
                itemDef.actions = new String[5];
                itemDef.stackable = false;
                itemDef.actions[0] = "Redeem";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Recipe for Disaster";
                break;
            case 16095:
                itemDef.copy(lookup(12821));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Divine spirit shield (inky)";
                itemDef.original_model_colors = new int[] { 10, 12, 12, 18, 5, 123, 8, 6, 10 };
                itemDef.modified_model_colors = new int[] { -20933, -21606, -21608, -20924, -21612, 123, -21599, -20918, -21610 };
                itemDef.rotation_x = 100;
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 16096;
                break;
            case 16096:
                itemDef.copy(lookup(12822));
                itemDef.actions = new String[5];
                itemDef.name = "Divine spirit shield (inky)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 10, 12, 12, 18, 5, 123, 8, 6, 10 };
                itemDef.modified_model_colors = new int[] { -20933, -21606, -21608, -20924, -21612, 123, -21599, -20918, -21610 };
                itemDef.rotation_x = 100;
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 16095;
                break;
            case 16097:
                itemDef.copy(lookup(15883));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Pernix cowl (Batman 101)";
                itemDef.original_model_colors = new int[] { 2, -30277, 5, -30277, -30277, 2, 5, 5, 950, 5, -30277, 6, 950, -30277 };
                itemDef.modified_model_colors = new int[] { 2, 4548, 5, 1030, 1032, 10, 12, 15, -19028, 20, -19030, 23, -19033, -19037 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 16098;
                break;
            case 16098:
                itemDef.copy(lookup(15884));
                itemDef.actions = new String[5];
                itemDef.name = "Pernix cowl (Batman 101)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 2, -30277, 5, -30277, -30277, 2, 5, 5, 950, 5, 5056, 6, 950, -30277 };
                itemDef.modified_model_colors = new int[] { 2, 4548, 5, 1030, 1032, 10, 12, 15, -19028, 20, -19030, 23, -19033, -19037 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 16097;
                break;
            case 16099:
                itemDef.copy(lookup(15885));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Pernix body (Batman 101)";
                itemDef.original_model_colors = new int[] { 950, 950, 950, 7, -30277, -30277, 11, 950, -30277, 15, 80, 785, -30277, 10323, 5, 6, 8, -30277, 950, 950, 798, 802, 10, -23524, 950, 805, 950, 10, 950, 950, -30277, 10, -30277, -30277, -30277, 950, 10293, -30277, -30277, 15, -30277, -30277 };
                itemDef.modified_model_colors = new int[] { 10306, -14915, -14919, 7, 10311, 1033, 11, -14924, 23566, 15, 80, 785, 10322, 10323, 21, 22, 23, 10328, -14940, -23516, 798, 802, 34, -23524, 10276, 805, -14952, 39, -22505, 10283, 10284, 46, -22513, 10290, 10355, -14900, 10293, 10294, 10359, 56, 10299, 10300 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 16100;
                break;
            case 16100:
                itemDef.copy(lookup(15886));
                itemDef.actions = new String[5];
                itemDef.name = "Pernix body (Batman 101)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 950, 950, 950, 7, -30277, -30277, 11, 950, -30277, 15, 80, 785, -30277, 10323, 21, 22, 23, -30277, 950, 950, 798, 802, 34, -23524, 950, 805, 950, 39, 950, 950, -30277, 46, -30277, -30277, -30277, 950, 10293, -30277, -30277, 56, -30277, -30277 };
                itemDef.modified_model_colors = new int[] { 10306, -14915, -14919, 7, 10311, 1033, 11, -14924, 23566, 15, 80, 785, 10322, 10323, 21, 22, 23, 10328, -14940, -23516, 798, 802, 34, -23524, 10276, 805, -14952, 39, -22505, 10283, 10284, 46, -22513, 10290, 10355, -14900, 10293, 10294, 10359, 56, 10299, 10300 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 16099;
                break;
            case 16101:
                itemDef.copy(lookup(15887));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Pernix chaps (Batman 101)";
                itemDef.original_model_colors = new int[] { 0, 950, 10, -30277, -30277, 950, 15, -30277, -30277, 950, 950, 5, 950, 8, 18, -30277, -30277, 950, -30277, 6, 950, 950, 10 };
                itemDef.modified_model_colors = new int[] { 0, 10340, 37, -14951, -12264, -12267, 42, -12269, 10285, -12272, 10319, 16, -11250, 50, 18, 10355, 10262, -14935, 10359, 23, 10299, -14942, 30 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 16102;
                break;
            case 16102:
                itemDef.copy(lookup(15888));
                itemDef.actions = new String[5];
                itemDef.name = "Pernix chaps (Batman 101)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 950, 10, -30277, -30277, 950, 15, -30277, -30277, 950, 950, 5, 950, 8, 18, -30277, -30277, 950, -30277, 6, 950, 950, 10 };
                itemDef.modified_model_colors = new int[] { 0, 10340, 37, -14951, -12264, -12267, 42, -12269, 10285, -12272, 10319, 16, -11250, 50, 18, 10355, 10262, -14935, 10359, 23, 10299, -14942, 30 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 16101;
                break;
            case 16103:
                itemDef.copy(lookup(15883));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Pernix cowl (frost)";
                itemDef.original_model_colors = new int[] { 38226, 38226, 38226, 5244, 38226, 120, 120, 110, 38226, 120, 38226, 120, 38226, 38226 };
                itemDef.modified_model_colors = new int[] { 2, 4548, 5, 1030, 1032, 10, 12, 15, -19028, 20, -19030, 23, -19033, -19037 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 16104;
                break;
            case 16104:
                itemDef.copy(lookup(15884));
                itemDef.actions = new String[5];
                itemDef.name = "Pernix cowl (frost)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 38226, 38226, 38226, 5244, 38226, 120, 120, 110, 38226, 120, 38226, 120, 38226, 38226 };
                itemDef.modified_model_colors = new int[] { 2, 4548, 5, 1030, 1032, 10, 12, 15, -19028, 20, -19030, 23, -19033, -19037 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 16103;
                break;
            case 16105:
                itemDef.copy(lookup(15885));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Pernix body (frost)";
                itemDef.original_model_colors = new int[] { 38226, 38226, 38226, 5244, 38226, 120, 120, 110, 38226, 120, 38226, 38226, 38226, 5244, 38226, 120, 120, 110, 38226, 120, 38226, 38226, 38226, 5244, 38226, 120, 120, 110, 38226, 120, 38226, 38226, 38226, 5244, 38226, 120, 120, 110, 38226, 120, 38226, 38226 };
                itemDef.modified_model_colors = new int[] { 10306, -14915, -14919, 7, 10311, 1033, 11, -14924, 23566, 15, 80, 785, 10322, 10323, 21, 22, 23, 10328, -14940, -23516, 798, 802, 34, -23524, 10276, 805, -14952, 39, -22505, 10283, 10284, 46, -22513, 10290, 10355, -14900, 10293, 10294, 10359, 56, 10299, 10300 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 16106;
                break;
            case 16106:
                itemDef.copy(lookup(15886));
                itemDef.actions = new String[5];
                itemDef.name = "Pernix body (frost)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 38226, 38226, 38226, 5244, 38226, 120, 120, 110, 38226, 120, 38226, 38226, 38226, 5244, 38226, 120, 120, 110, 38226, 120, 38226, 38226, 38226, 5244, 38226, 120, 120, 110, 38226, 120, 38226, 38226, 38226, 5244, 38226, 120, 120, 110, 38226, 120, 38226, 38226 };
                itemDef.modified_model_colors = new int[] { 10306, -14915, -14919, 7, 10311, 1033, 11, -14924, 23566, 15, 80, 785, 10322, 10323, 21, 22, 23, 10328, -14940, -23516, 798, 802, 34, -23524, 10276, 805, -14952, 39, -22505, 10283, 10284, 46, -22513, 10290, 10355, -14900, 10293, 10294, 10359, 56, 10299, 10300 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 16105;
                break;
            case 16107:
                itemDef.copy(lookup(15887));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Pernix chaps (frost)";
                itemDef.original_model_colors = new int[] { 38226, 38226, 38226, 5244, 38226, 120, 120, 110, 38226, 120, 38226, 38226, 38226, 5244, 38226, 120, 120, 110, 38226, 120, 38226, 38226, 110 };
                itemDef.modified_model_colors = new int[] { 0, 10340, 37, -14951, -12264, -12267, 42, -12269, 10285, -12272, 10319, 16, -11250, 50, 18, 10355, 10262, -14935, 10359, 23, 10299, -14942, 30 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 16108;
                break;
            case 16108:
                itemDef.copy(lookup(15888));
                itemDef.actions = new String[5];
                itemDef.name = "Pernix chaps (frost)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 38226, 38226, 38226, 5244, 38226, 120, 120, 110, 38226, 120, 38226, 38226, 38226, 5244, 38226, 120, 120, 110, 38226, 120, 38226, 38226, 110 };
                itemDef.modified_model_colors = new int[] { 0, 10340, 37, -14951, -12264, -12267, 42, -12269, 10285, -12272, 10319, 16, -11250, 50, 18, 10355, 10262, -14935, 10359, 23, 10299, -14942, 30 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 16107;
                break;
            case 16109:
                itemDef.copy(lookup(25185));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Midnight slayer helmet (i)";
                itemDef.description = "";
                itemDef.original_model_colors = new int[] { 16, 0, 33, 37, 86933, 8, 24, 86933, 86933, 12, 86933, 86933 };
                itemDef.modified_model_colors = new int[] { 16, 0, 33, 37, -15590, 8, 24, -18154, -15821, 12, -15470, -15583 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 16053;
                break;
            case 16110:
                itemDef.copy(lookup(20997));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Batman 101 twisted bow";
                itemDef.description = "";
                itemDef.original_model_colors = new int[] { -30277, -30277, -30277, 5244, -30277, 120, 120, 110, -30277 };
                itemDef.modified_model_colors = new int[] { 13223, 10318, 10334, 16, 0, 33, 8, 24, 41 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 16053;
                break;
            case 16111:
                itemDef.copy(lookup(20998));
                itemDef.actions = new String[5];
                itemDef.name = "Batman 101 twisted bow";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { -30277, -30277, -30277, 5244, -30277, 120, 120, 110, -30277 };
                itemDef.modified_model_colors = new int[] { 13223, 10318, 10334, 16, 0, 33, 8, 24, 41 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 16052;
                break;
            case 27828:
            case 27822:
            case 27812:
            case 27141:
            case 27143:
            case 27145:
            case 27147:
            case 27149:
            case 27151:
            case 27155:
            case 27370:
            case 27440:
            case 2570:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.actions[4] = "Drop";
                break;
            case 27374:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.actions[2] = "Commune";
                itemDef.actions[4] = "Drop";
                break;
            case 27808:
                itemDef.actions = new String[5];
                itemDef.name = "Tztok boots";
                itemDef.actions[1] = "Wear";
                itemDef.actions[4] = "Drop";
                break;
            case 27814:
                itemDef.actions = new String[5];
                itemDef.name = "Tztok-Jad plush";
                itemDef.actions[1] = "Wield";
                itemDef.actions[4] = "Drop";
                break;
            case 27816:
            case 27110:
            case 27428:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.actions[4] = "Drop";
                break;
            case 27473:
                itemDef.actions = new String[5];
                itemDef.name = "Darkmeyer hat";
                itemDef.actions[1] = "Wear";
                itemDef.actions[4] = "Drop";
                break;
            case 27477:
                itemDef.actions = new String[5];
                itemDef.name = "Darkmeyer robe bottom";
                itemDef.actions[1] = "Wear";
                itemDef.actions[4] = "Drop";
                break;
            case 27479:
                itemDef.actions = new String[5];
                itemDef.name = "Darkmeyer boots";
                itemDef.actions[1] = "Wear";
                itemDef.actions[4] = "Drop";
                break;
            case 27481:
                itemDef.actions = new String[5];
                itemDef.name = "Darkmeyer cape";
                itemDef.actions[1] = "Wear";
                itemDef.actions[4] = "Drop";
                break;
            case 27471:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.actions[4] = "Destroy";
                break;
            case 26984:
            case 26986:
            case 26988:
            case 26939:
            case 27023:
            case 27025:
            case 27027:
            case 27029:
            case 27031:
            case 27265:
            case 27267:
            case 27584:
            case 27497:
            case 27499:
            case 27501:
            case 27503:
            case 27505:
            case 27507:
            case 27564:
            case 27582:
            case 27566:
            case 27576:
            case 27578:
            case 27580:
            case 27585:
            case 27586:
            case 27806:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.actions[4] = "Destroy";
                break;

            case 22552:
            case 27662:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.actions[3] = "Dismantle";
                itemDef.actions[4] = "Drop";
                break;
            case 22555:
            case 27665:
            case 25865:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.actions[2] = "Check";
                itemDef.actions[4] = "Uncharge";
                break;
        }
    }

}
