package com.runescape.cache.graphics.widget;

import com.runescape.Client;
import com.grinder.Configuration;
import com.runescape.cache.Js5;
import com.runescape.cache.def.ItemDefinition;
import com.runescape.cache.graphics.GameFont;
import com.runescape.cache.graphics.sprite.AutomaticSprite;
import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.cache.graphics.sprite.SpriteLoader;
import com.runescape.util.StringUtils;
import com.grinder.client.ClientCompanion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.runescape.cache.graphics.widget.Spellbooks.RequiredItem.*;
import static com.runescape.cache.graphics.widget.Spellbooks.Spellbook.*;
import static com.runescape.cache.graphics.widget.Spellbooks.SpellType.*;

public class Spellbooks extends Widget {

    private static final int USABLE_ON_OBJECT = 4;
    private static final int USABLE_ON_ENTITY = 10;
    private static final int USABLE_ON_INVENTORY = 16;

    private static final int WIDTH = 192;
    private static final int HEIGHT = 261;
    private static final int CUSTOM_HOVERS_START_ID = 75100;
    private static final int ITEM_MODELS_START_ID = 30800;

    public static final int NORMAL_SPELLBOOK_ID = 1151;
    public static final int ANCIENT_SPELLBOOK_ID = 12855;
    public static final int LUNAR_SPELLBOOK_ID = 29999;
    public static final int ARCEUUS_SPELLBOOK_ID = 39999;

    public static final int INVENTORY_CONTAINTER_INTERFACE_ID = 3214;
    public static final int RUNE_POUCH_CONTAINER_INTERFACE_ID = 41710;
    public static final int EQUIPMENT_CONTAINER_INTERFACE_ID = 1688;

    public static final int RUNE_POUCH_ITEM_ID = 12791;

    public static final int NO_SPELLS_MATCH_TEXT_ID = 75098;

    public static final int FILTER_BUTTON_HEIGHT = 17;
    private static final int SPELLS_AREA_HEIGHT = HEIGHT - FILTER_BUTTON_HEIGHT;

    public static final int FILTER_BUTTON_CONFIG = 1156;
    public static final int FILTER_BUTTON_ID = 75099;
    public static final int FILTER_PANEL_ID = 75000;

    public static final int FILTER_COMBAT_BUTTON_CONFIG = 1157;
    public static final int FILTER_COMBAT_BUTTON_ID = FILTER_PANEL_ID + 5;
    public static final int FILTER_TELEPORT_BUTTON_CONFIG = 1158;
    public static final int FILTER_TELEPORT_BUTTON_ID = FILTER_PANEL_ID + 8;
    public static final int FILTER_UTILITY_BUTTON_CONFIG = 1159;
    public static final int FILTER_UTILITY_BUTTON_ID = FILTER_PANEL_ID + 11;
    public static final int FILTER_LEVEL_BUTTON_CONFIG = 1160;
    public static final int FILTER_LEVEL_BUTTON_ID = FILTER_PANEL_ID + 14;
    public static final int FILTER_RUNES_BUTTON_CONFIG = 1161;
    public static final int FILTER_RUNES_BUTTON_ID = FILTER_PANEL_ID + 17;

    public static final int SORT_SPELL_STATE_CONFIG = 1162;
    public static final int SORT_LEVEL_ORDER_BUTTON_ID = FILTER_PANEL_ID + 20;
    public static final int SORT_COMBAT_SPELL_BUTTON_ID = FILTER_PANEL_ID + 23;
    public static final int SORT_TELEPORT_SPELL_BUTTON_ID = FILTER_PANEL_ID + 26;

    private static int customSpellCount;

    public static int currentBookId;

    public static int getCurrentBookId() {
        return currentBookId;
    }

    public static void setCurrentBookId(int currentBookId) {
        Spellbooks.currentBookId = currentBookId;
    }

    enum RequiredItem {
        FIRE_RUNE(554, new int[] { 4694, 4697, 4699 }, new int[] { 1387, 1393, 1401, 3053, 3054, 11787, 11789, 12795, 12796, 11998, 12000, 20714, 20716, 21198, 21200 }), // model id 2399
//        FIRE_RUNE(554, new int[] { 4694, 4697, 4699 }, new int[] { 1387, 1393, 1401, 3053, 3054, 11787, 11789, 12795, 12796, 11998, 12000, 21198, 21200 }), // model id 2399
        WATER_RUNE(555, new int[] { 694, 4695, 4698 }, new int[] { 1383, 1395, 1403, 6562, 6563, 11787, 11789, 12795, 12796, 20730, 25574, 20733, 21006 }), // model id 2652
        AIR_RUNE(556, new int[] { 4697, 4695, 4696 }, new int[] { 1381, 1397, 1405, 11998, 12000, 20730, 20733, 20736, 20739, 15718 }), // model id 2405
        EARTH_RUNE(557, new int[] { 4696, 4699, 4698 }, new int[] { 1385, 1399, 1407, 3053, 3054, 6562, 6563, 20736, 20739, 21198, 21200 }), // model id 2737
        MIND_RUNE(558), // model id 2481
        BODY_RUNE(559), // model id 2340
        DEATH_RUNE(560), // model id 2645
        NATURE_RUNE(561), // model id 2734
        CHAOS_RUNE(562), // model id 2707
        LAW_RUNE(563), // model id 2382
        COSMIC_RUNE(564), // model id 2735
        BLOOD_RUNE(565), // model id 2665
        SOUL_RUNE(566), // model id 2434
        ASTRAL_RUNE(9075), // model id 16742
        WRATH_RUNE(21880),
        IBAN_STAFF(1409, 12658),
        UNPOWERED_ORB(567),
        SARADOMIN_STAFF(2415, 22296),
        GUTHIX_STAFF(2416, 8841, 24144),
        ZAMORAK_STAFF(2417, 11791, 12902, 12904, 22296),
        SLAYERS_STAFF(4170, 21255, 11791, 12902, 12904, 22296),
        HOSIDIOUS_STAFF(15298, 23854),
        BANANA(1963);

        private int itemId; // inventory item to search for
        private int[] alternativeItemIds; // other runes that can be used in place of this rune (other inventory items to search for)
        private int[] staffItemIds; // corresponding staves to the rune and alternative runes (equipment items to search for)

        public int getItemId() {
            return itemId;
        }

        RequiredItem(int itemId) {
            this.itemId = itemId;
        }

        RequiredItem(int itemId, int[] alternativeItemIds, int[] staffItemIds) {
            this.itemId = itemId;
            this.alternativeItemIds = alternativeItemIds;
            this.staffItemIds = staffItemIds;
        }

        RequiredItem(int... staffItemIds) {
            this.staffItemIds = staffItemIds;
        }

        private int[] getValueIndexArray() {
            int length = 1;
            if (itemId != 0)
                length += 6;
            if (alternativeItemIds != null)
                length += alternativeItemIds.length * 6;
            if (staffItemIds != null)
                length += staffItemIds.length * 3;
            int[] valueIndexArray = new int[length];
            int index = 0;

            if (itemId != 0) {
                valueIndexArray[index++] = 4;
                valueIndexArray[index++] = INVENTORY_CONTAINTER_INTERFACE_ID;
                valueIndexArray[index++] = itemId;
                valueIndexArray[index++] = 4;
                valueIndexArray[index++] = RUNE_POUCH_CONTAINER_INTERFACE_ID;
                valueIndexArray[index++] = itemId;
            }

            if (alternativeItemIds != null) {
                for (int i = 0; i < alternativeItemIds.length; i++) {
                    valueIndexArray[index++] = 4;
                    valueIndexArray[index++] = INVENTORY_CONTAINTER_INTERFACE_ID;
                    valueIndexArray[index++] = alternativeItemIds[i];
                    valueIndexArray[index++] = 4;
                    valueIndexArray[index++] = RUNE_POUCH_CONTAINER_INTERFACE_ID;
                    valueIndexArray[index++] = alternativeItemIds[i];
                }
            }

            if (staffItemIds != null) {
                for (int i = 0; i < staffItemIds.length; i++) {
                    valueIndexArray[index++] = 10;
                    valueIndexArray[index++] = EQUIPMENT_CONTAINER_INTERFACE_ID;
                    valueIndexArray[index++] = staffItemIds[i];
                }
            }

            valueIndexArray[index] = 0;

            return valueIndexArray;
        }

    }

    public enum Spellbook {
        NORMAL(NORMAL_SPELLBOOK_ID, 25, 24, 26, 24, WIDTH - 10),
        ANCIENT(ANCIENT_SPELLBOOK_ID, 25, 25, 48, 36, WIDTH),
        LUNAR(LUNAR_SPELLBOOK_ID, 25, 25, 40, 28, WIDTH),
        ARCEUUS(ARCEUUS_SPELLBOOK_ID, 25, 25, 40, 28, WIDTH);

        private int id;
        private int spellWidth;
        private int spellHeight;
        private int defaultIncrX;
        private int defaultIncrY;
        private int width;
        private List<Spell> spells = new ArrayList<>();

        public int getId() {
            return id;
        }

        public int getSpellWidth() {
            return spellWidth;
        }

        public int getSpellHeight() {
            return spellHeight;
        }

        public int getDefaultIncrX() {
            return defaultIncrX;
        }

        public int getDefaultIncrY() {
            return defaultIncrY;
        }

        public int getWidth() {
            return width;
        }

        public List<Spell> getSpells() {
            return spells;
        }

        Spellbook(int id, int spellWidth, int spellHeight, int defaultIncrX, int defaultIncrY, int width) {
            this.id = id;
            this.spellWidth = spellWidth;
            this.spellHeight = spellHeight;
            this.defaultIncrX = defaultIncrX;
            this.defaultIncrY = defaultIncrY;
            this.width = width;
        }

        public static Spellbook forId(int id) {
            for (Spellbook spellbook : values())
                if (spellbook.getId() == id)
                    return spellbook;
            return NORMAL;
        }

    }

    enum SpellType {
        COMBAT,
        UTILITY,
        TELEPORT;
    }

    public enum Spell {
        /**
         * Normals
         */
    	//LUMBRIDGE_HOME_TELEPORT_2(NORMAL, TELEPORT, 19210, "Lumbridge Home\\nTeleport", "Requires no runes - recharge time\\n30 mins. Warning: This spell takes a\\nlong time to cast and will be\\ninterrupted by combat.", null, null, 0),
        LUMBRIDGE_HOME_TELEPORT(NORMAL, TELEPORT, 19210, "Edgeville Home\\nTeleport", "Teleports you to home area", null, null, 0),
        WIND_STRIKE(NORMAL, COMBAT, 1152, "Wind Strike", "A basic Air missile", new RequiredItem[] {AIR_RUNE, MIND_RUNE}, new int[] { 1, 1 }, 1, USABLE_ON_ENTITY),
        CONFUSE(NORMAL, COMBAT, 1153, "Confuse", "Reduces your opponent's attack by\\n5%", new RequiredItem[] {WATER_RUNE, EARTH_RUNE, BODY_RUNE}, new int[] { 3, 2, 1 }, 3, USABLE_ON_ENTITY),
        WATER_STRIKE(NORMAL, COMBAT, 1154, "Water Strike", "A basic Water missile", new RequiredItem[] {WATER_RUNE, AIR_RUNE, MIND_RUNE}, new int[] { 1, 1, 1 }, 5, USABLE_ON_ENTITY),
        LVL_1_ENCHANT(NORMAL, UTILITY, 1155, "Lvl-1 Enchant", "For use on sapphire jewellery", new RequiredItem[] {WATER_RUNE, COSMIC_RUNE}, new int[] { 1, 1 }, 7, USABLE_ON_INVENTORY),
        EARTH_STRIKE(NORMAL, COMBAT, 1156, "Earth Strike", "A basic Earth missile", new RequiredItem[] {EARTH_RUNE, AIR_RUNE, MIND_RUNE}, new int[] { 2, 1, 1 }, 9, USABLE_ON_ENTITY),
        WEAKEN(NORMAL, COMBAT, 1157, "Weaken", "Reduces your opponent's\\nstrength by 5%", new RequiredItem[] {WATER_RUNE, EARTH_RUNE, BODY_RUNE}, new int[] { 3, 2, 1 }, 11, USABLE_ON_ENTITY),
        FIRE_STRIKE(NORMAL, COMBAT, 1158, "Fire Strike", "A basic Fire missile", new RequiredItem[] {FIRE_RUNE, AIR_RUNE, MIND_RUNE}, new int[] { 3, 2, 1 }, 13, USABLE_ON_ENTITY),
        BONES_TO_BANANAS(NORMAL, UTILITY, 1159, "Bones to Bananas", "Changes all held bones into\\nbananas", new RequiredItem[] {EARTH_RUNE, WATER_RUNE, NATURE_RUNE}, new int[] { 2, 2, 1 }, 15),
        WIND_BOLT(NORMAL, COMBAT, 1160, "Wind Bolt", "A low level Air missile", new RequiredItem[] {AIR_RUNE, CHAOS_RUNE}, new int[] { 2, 1 }, 17, USABLE_ON_ENTITY),
        CURSE(NORMAL, COMBAT, 1161, "Curse", "Reduces your opponent's\\ndefence by 5%", new RequiredItem[] {WATER_RUNE, EARTH_RUNE, BODY_RUNE}, new int[] { 2, 3, 1 }, 19, USABLE_ON_ENTITY),
        LOW_LEVEL_ALCHEMY(NORMAL, UTILITY, 1162, "Low Level Alchemy", "Converts an item into gold", new RequiredItem[] {FIRE_RUNE, NATURE_RUNE}, new int[] { 3, 1 }, 21, USABLE_ON_INVENTORY),
        WATER_BOLT(NORMAL, COMBAT, 1163, "Water Bolt", "A low level Water missile", new RequiredItem[] {WATER_RUNE, AIR_RUNE, CHAOS_RUNE}, new int[] { 2, 2, 1 }, 23, USABLE_ON_ENTITY),
        VARROCK_TELEPORT(NORMAL, TELEPORT, 1164, 1098, 1097,"Varrock Teleport", "Teleports you Varrock", new RequiredItem[]{LAW_RUNE, AIR_RUNE, FIRE_RUNE}, new int[] { 1, 3, 1 }, 25),
        LVL_2_ENCHANT(NORMAL, UTILITY, 1165, "Lvl-2 Enchant", "For use on emerald jewellery", new RequiredItem[] {AIR_RUNE, COSMIC_RUNE}, new int[] { 3, 1 }, 27, USABLE_ON_INVENTORY),
        EARTH_BOLT(NORMAL, COMBAT, 1166, "Earth Bolt", "A low level Earth missile", new RequiredItem[] {EARTH_RUNE, AIR_RUNE, CHAOS_RUNE}, new int[] { 3, 2, 1 }, 29, USABLE_ON_ENTITY),
        LUMBRIDGE_TELEPORT(NORMAL, TELEPORT, 1167, 1100, 1099, "Lumbridge Teleport", "Teleports you to Lumbridge", new RequiredItem[]{LAW_RUNE, AIR_RUNE, EARTH_RUNE}, new int[] { 1, 3, 1 }, 31),
        TELEKINETIC_GRAB(NORMAL, UTILITY, 1168, "Telekinetic Grab", "Take an item you can see but can't\\nreach", new RequiredItem[] {AIR_RUNE, LAW_RUNE}, new int[] { 1, 1 }, 33, 1),
        FIRE_BOLT(NORMAL, COMBAT, 1169, "Fire Bolt", "A low level Fire missile", new RequiredItem[] {FIRE_RUNE, AIR_RUNE, CHAOS_RUNE}, new int[] { 4, 3, 1 }, 35, USABLE_ON_ENTITY),
        FALADOR_TELEPORT(NORMAL, TELEPORT, 1170, 1102, 1101, "Falador Teleport", "Teleports you to Falador", new RequiredItem[]{LAW_RUNE, AIR_RUNE, WATER_RUNE}, new int[] { 1, 3, 1 }, 37),
        CRUMBLE_UNDEAD(NORMAL, COMBAT, 1171, "Crumble Undead", "Hits skeletons, ghosts, shades &\\nzombies hard", new RequiredItem[] {EARTH_RUNE, AIR_RUNE, CHAOS_RUNE}, new int[] { 2, 2, 1 }, 39, 2),
        TELEPORT_TO_HOUSE(NORMAL, TELEPORT, 19208, 1104, 1103, "Teleport to House", "Teleports you to your house", new RequiredItem[] {LAW_RUNE, EARTH_RUNE, AIR_RUNE}, new int[] { 1, 1, 1 }, 40),
        WIND_BLAST(NORMAL, COMBAT, 1172, "Wind Blast", "A medium level Air missile", new RequiredItem[] {AIR_RUNE, DEATH_RUNE}, new int[] { 3, 1 }, 41, USABLE_ON_ENTITY),
        SUPERHEAT_ITEM(NORMAL, UTILITY, 1173, "Superheat Item", "Smelt ore without a furnace", new RequiredItem[] {FIRE_RUNE, NATURE_RUNE}, new int[] { 4, 1 }, 43, USABLE_ON_INVENTORY),
        CAMELOT_TELEPORT(NORMAL, TELEPORT, 1174, 1106, 1105, "Camelot Teleport", "Teleports you to Camelot", new RequiredItem[]{LAW_RUNE, AIR_RUNE}, new int[] { 1, 5}, 45),
        WATER_BLAST(NORMAL, COMBAT, 1175, "Water Blast", "A medium level Water missile", new RequiredItem[] {WATER_RUNE, AIR_RUNE, DEATH_RUNE}, new int[] { 3, 3, 1 }, 47, USABLE_ON_ENTITY),
        LVL_3_ENCHANT(NORMAL, UTILITY, 1176, "Lvl-3 Enchant", "For use on ruby jewellery", new RequiredItem[] {FIRE_RUNE, COSMIC_RUNE}, new int[] { 5, 1 }, 49, USABLE_ON_INVENTORY),
        EARTH_BLAST(NORMAL, COMBAT, 1177, "Earth Blast", "A medium level Earth missile", new RequiredItem[] {EARTH_RUNE, AIR_RUNE, DEATH_RUNE}, new int[] { 4, 3, 1 }, 53, USABLE_ON_ENTITY),
        HIGH_LEVEL_ALCHEMY(NORMAL, UTILITY, 1178, "High Level Alchemy", "Converts an item into more gold", new RequiredItem[] {FIRE_RUNE, NATURE_RUNE}, new int[] { 5, 1 }, 55, USABLE_ON_INVENTORY),
        CHARGE_WATER_ORB(NORMAL, UTILITY, 1179, "Charge Water Orb", "Needs to be cast on a water obelisk", new RequiredItem[] {WATER_RUNE, COSMIC_RUNE, UNPOWERED_ORB}, new int[] { 30, 3, 1 }, 56, USABLE_ON_OBJECT),
        LVL_4_ENCHANT(NORMAL, UTILITY, 1180, "Lvl-4 Enchant", "For use on diamond jewellery", new RequiredItem[] {EARTH_RUNE, COSMIC_RUNE}, new int[] { 10, 1 }, 57, USABLE_ON_INVENTORY),
        FIRE_BLAST(NORMAL, COMBAT, 1181, "Fire Blast", "A medium level Fire missile", new RequiredItem[] {FIRE_RUNE, AIR_RUNE, DEATH_RUNE}, new int[] { 5, 4, 1 }, 59, USABLE_ON_ENTITY),
        CHARGE_EARTH_ORB(NORMAL, UTILITY, 1182, "Charge Earth Orb", "Needs to be cast on an earth\\nobelisk", new RequiredItem[] {EARTH_RUNE, COSMIC_RUNE, UNPOWERED_ORB}, new int[] { 30, 3, 1 }, 60, USABLE_ON_OBJECT),
        WIND_WAVE(NORMAL, COMBAT, 1183, "Wind Wave", "A high level Air missile", new RequiredItem[] {AIR_RUNE, BLOOD_RUNE}, new int[] { 5, 1 }, 62, USABLE_ON_ENTITY),
        CHARGE_FIRE_ORB(NORMAL, UTILITY, 1184, "Charge Fire Orb", "Needs to be cast on a fire obelisk", new RequiredItem[] {FIRE_RUNE, COSMIC_RUNE, UNPOWERED_ORB}, new int[] { 30, 3, 1 }, 63, USABLE_ON_OBJECT),
        WATER_WAVE(NORMAL, COMBAT, 1185, "Water Wave", "A high level Water missile", new RequiredItem[] {WATER_RUNE, AIR_RUNE, BLOOD_RUNE}, new int[] { 7, 5, 1 }, 65, USABLE_ON_ENTITY),
        CHARGE_AIR_ORB(NORMAL, UTILITY, 1186, "Charge Air Orb", "Needs to be cast on an air obelisk", new RequiredItem[] {AIR_RUNE, COSMIC_RUNE, UNPOWERED_ORB}, new int[] { 30, 3, 1 }, 66, USABLE_ON_OBJECT),
        LVL_5_ENCHANT(NORMAL, UTILITY, 1187, "Lvl-5 Enchant", "For use on dragonstone jewellery", new RequiredItem[] {WATER_RUNE, EARTH_RUNE, COSMIC_RUNE}, new int[] { 15, 15, 1 }, 68, USABLE_ON_INVENTORY),
        EARTH_WAVE(NORMAL, COMBAT, 1188, "Earth Wave", "A high level Earth missile", new RequiredItem[] {EARTH_RUNE, AIR_RUNE, BLOOD_RUNE}, new int[] { 7, 5, 1 }, 70, USABLE_ON_ENTITY),
        FIRE_WAVE(NORMAL, COMBAT, 1189, "Fire Wave", "A high level Fire missile", new RequiredItem[] {FIRE_RUNE, AIR_RUNE, BLOOD_RUNE}, new int[] { 7, 5, 1 }, 75, USABLE_ON_ENTITY),
        BONES_TO_PEACHES(NORMAL, UTILITY, 15877, "Bones to Peaches", "Turns Bones into Peaches", new RequiredItem[] {NATURE_RUNE, WATER_RUNE, EARTH_RUNE}, new int[] { 2, 4, 4 }, 60),
        SARADOMIN_STRIKE(NORMAL, COMBAT, 1190, "Saradomin Strike", "Summons the power of Saradomin", new RequiredItem[] {FIRE_RUNE, BLOOD_RUNE, AIR_RUNE, SARADOMIN_STAFF}, new int[] { 2, 2, 4, 1 }, 60, USABLE_ON_ENTITY),
        CLAWS_OF_GUTHIX(NORMAL, COMBAT, 1191, "Claws of Guthix", "Summons the power of Guthix", new RequiredItem[] {FIRE_RUNE, BLOOD_RUNE, AIR_RUNE, GUTHIX_STAFF}, new int[] { 1, 2, 4, 1 }, 60, USABLE_ON_ENTITY),
        FLAMES_OF_ZAMORAK(NORMAL, COMBAT, 1192, "Flames of Zamorak", "Summons the power of Zamorak", new RequiredItem[] {FIRE_RUNE, BLOOD_RUNE, AIR_RUNE, ZAMORAK_STAFF}, new int[] { 4, 2, 1, 1 }, 60, USABLE_ON_ENTITY),
        STUN(NORMAL, COMBAT, 1562, "Stun", "Reduces your opponent's attack by\\n10%", new RequiredItem[] {EARTH_RUNE, WATER_RUNE, SOUL_RUNE}, new int[] { 12, 12, 1 }, 80, USABLE_ON_ENTITY),
        CHARGE(NORMAL, COMBAT, 1193, "Charge", "Temporarily increases the power\\nof the three arena spells when\\nwearing Mage Arena capes", new RequiredItem[] {FIRE_RUNE, BLOOD_RUNE, AIR_RUNE}, new int[] { 3, 3, 3 }, 80),
        WIND_SURGE(NORMAL, COMBAT, 30791, 1065, 1061,"Wind Surge", "A very high level Air missile", new RequiredItem[] {AIR_RUNE, WRATH_RUNE}, new int[] { 7, 1 }, 81, USABLE_ON_ENTITY),
        IBAN_BLAST(NORMAL, COMBAT, 1539, "Iban Blast", "Summons the wrath of Iban", new RequiredItem[] {FIRE_RUNE, DEATH_RUNE, IBAN_STAFF}, new int[] { 5, 1, 1 }, 50, USABLE_ON_ENTITY),
        ARDOUGNE_TELEPORT(NORMAL, TELEPORT, 1540, 1108, 1107, "Ardougne Teleport", "Teleports you to Ardougne", new RequiredItem[] {LAW_RUNE, WATER_RUNE}, new int[] { 2, 2 }, 51),
        WATCHTOWER_TELEPORT(NORMAL, TELEPORT, 1541, 1110, 1109, "Watchtower Teleport", "Teleports you to Watchtower", new RequiredItem[] {LAW_RUNE, EARTH_RUNE}, new int[] { 2, 2 }, 58),
        VULNERABILITY(NORMAL, COMBAT, 1542, "Vulnerability", "Reduces your opponent's defence\\nby 10%", new RequiredItem[] {EARTH_RUNE, WATER_RUNE, SOUL_RUNE}, new int[] { 5, 5, 1 }, 66, USABLE_ON_ENTITY),
        ENFEEBLE(NORMAL, COMBAT, 1543, "Enfeeble", "Reduces your opponent's strength\\nby 10%", new RequiredItem[] {EARTH_RUNE, WATER_RUNE, SOUL_RUNE}, new int[] { 8, 8, 1 }, 73, USABLE_ON_ENTITY),
        BIND(NORMAL, COMBAT, 1572, "Bind", "Holds your opponent for 5 seconds", new RequiredItem[] {EARTH_RUNE, WATER_RUNE, NATURE_RUNE}, new int[] { 3, 3, 2 }, 20, USABLE_ON_ENTITY),
        SNARE(NORMAL, COMBAT, 1582, "Snare", "Holds your opponent for 10\\nseconds", new RequiredItem[] {EARTH_RUNE, WATER_RUNE, NATURE_RUNE}, new int[] { 4, 4, 3 }, 50, USABLE_ON_ENTITY),
        ENTANGLE(NORMAL, COMBAT, 1592, "Entangle", "Holds your opponent for 15\\nseconds", new RequiredItem[] {EARTH_RUNE, WATER_RUNE, NATURE_RUNE}, new int[] { 5, 5, 4 }, 79, USABLE_ON_ENTITY),
        TROLLHEIM_TELEPORT(NORMAL, TELEPORT, 7455, 1112, 1111, "Trollheim Teleport", "Teleports you to Trollheim", new RequiredItem[] {LAW_RUNE, FIRE_RUNE}, new int[] { 2, 2 }, 61),
        MAGIC_DART(NORMAL, COMBAT, 12037, "Magic Dart", "A magic dart of slaying", new RequiredItem[] {SLAYERS_STAFF, DEATH_RUNE, MIND_RUNE}, new int[] { 1, 1, 4 }, 50, USABLE_ON_ENTITY),
        TELEOTHER_LUMBRIDGE(NORMAL, TELEPORT, 12425, "Teleother Lumbridge", "Teleports target to Lumbridge", new RequiredItem[] {SOUL_RUNE, LAW_RUNE, EARTH_RUNE}, new int[] { 1, 1, 1 }, 74, USABLE_ON_ENTITY),
        TELEOTHER_FALADOR(NORMAL, TELEPORT, 12435, "Teleother Falador", "Teleports target to Falador", new RequiredItem[] {SOUL_RUNE, LAW_RUNE, WATER_RUNE}, new int[] { 1, 1, 1 }, 82, USABLE_ON_ENTITY),
        WATER_SURGE(NORMAL, COMBAT, 30794, 1066, 1062, "Water Surge", "A very high level Water missile", new RequiredItem[] {AIR_RUNE, WATER_RUNE, WRATH_RUNE}, new int[] { 7, 10, 1 }, 85, USABLE_ON_ENTITY),
        TELE_BLOCK(NORMAL, COMBAT, 12445, "Tele Block", "Stops your target from teleporting", new RequiredItem[] {LAW_RUNE, CHAOS_RUNE, DEATH_RUNE}, new int[] { 1, 1, 1 }, 85, USABLE_ON_ENTITY),
        TELEOTHER_CAMELOT(NORMAL, TELEPORT, 12455, "Teleother Camelot", "Teleports target to Camelot", new RequiredItem[] {SOUL_RUNE, LAW_RUNE}, new int[] { 2, 1 }, 90, USABLE_ON_ENTITY),
        EARTH_SURGE(NORMAL, COMBAT, 30797, 1067, 1063, "Earth Surge", "A very high level Earth missile", new RequiredItem[] {AIR_RUNE, EARTH_RUNE, WRATH_RUNE}, new int[] { 7, 10, 1 }, 90, USABLE_ON_ENTITY),
        LVL_6_ENCHANT(NORMAL, UTILITY, 6003, "Lvl-6 Enchant", "For use on onyx jewellery", new RequiredItem[] {COSMIC_RUNE, FIRE_RUNE, EARTH_RUNE}, new int[] { 1, 20, 20 }, 87, USABLE_ON_INVENTORY),
        APE_ATOLL_TELEPORT(NORMAL, TELEPORT, 18470, "Ape Atoll Teleport", "Teleports you to Ape Atoll", new RequiredItem[] {FIRE_RUNE, WATER_RUNE, LAW_RUNE, BANANA}, new int[] { 2, 2, 2, 1 }, 64),
        ENCHANT_CROSSBOW_BOLT(NORMAL, UTILITY, 19207, 863, 864, "Enchant Crossbow Bolt", "Minimum level 4 Magic Multiple\\nother requirements", null, null, 4),

        KOUREND_CASTLE_TELEPORT(NORMAL, TELEPORT, 30330, 988, 989, "Kourend Castle\\nTeleport", "Teleports you to Kourend Castle", new RequiredItem[] {LAW_RUNE, SOUL_RUNE, WATER_RUNE, FIRE_RUNE}, new int[] { 2, 2, 4, 5 }, 69),
        TELEPORT_TO_BOUNTY_TARGET(NORMAL, TELEPORT, 30331, 990, 991, "Teleport to Bounty\\nTarget", "Teleports you near your Bounty\\nHunter target", new RequiredItem[] {LAW_RUNE, DEATH_RUNE, CHAOS_RUNE}, new int[] { 1, 1, 1 }, 85),
        LVL_7_ENCHANT(NORMAL, UTILITY, 30332, 992, 993, "Lvl-7 Enchant", "For use on zenyte jewellery", new RequiredItem[] {COSMIC_RUNE, SOUL_RUNE, BLOOD_RUNE}, new int[] { 1, 20, 20 }, 93, USABLE_ON_INVENTORY),
        FIRE_SURGE(NORMAL, COMBAT, 30799, 1068, 1064, "Fire Surge", "A very high level Fire missile", new RequiredItem[] {AIR_RUNE, FIRE_RUNE, WRATH_RUNE}, new int[] { 7, 10, 1 }, 95, USABLE_ON_ENTITY),


        //POLYMORPH(NORMAL, COMBAT, 34500, 1134, 1133, "Polymorph", "Morphs your enemy for a\\nfew seconds", new RequiredItem[] {SOUL_RUNE, COSMIC_RUNE, NATURE_RUNE, WRATH_RUNE }, new int[] { 30, 30, 30, 25 }, 85, USABLE_ON_ENTITY),
        // Transforms the other player to a cow NPC with random movements if possible each tick, and the other player got BLOCK_ALL_BUT_TALKING active for 8 ticks, teletab will be allowed, and during polymorph player takes 50% less damamge
        UNDEAD_BASH(NORMAL, COMBAT, 34501, 1136, 1135, "Undead Bash", "Makes skeletons, ghosts, shades &\\nzombies suffer", new RequiredItem[] {SOUL_RUNE, COSMIC_RUNE, WRATH_RUNE}, new int[] { 15, 15, 5 }, 92, USABLE_ON_ENTITY),
        DEMON_AGONY(NORMAL, COMBAT, 34502, 1138, 1137, "Demon Agony", "A superior dragonfire ball that\\nis a demon's nightmare", new RequiredItem[] {SOUL_RUNE, ASTRAL_RUNE, WRATH_RUNE, HOSIDIOUS_STAFF}, new int[] { 30, 30, 10, 1 }, 96, USABLE_ON_ENTITY),
        /**
         * Ancients
         */

        //EDGEVILLE_HOME_TELEPORT(ANCIENT, TELEPORT, 21741, "Edgeville Home\\nTeleport", "Requires no runes - recharge time\\n30 mins. Warning: This spell takes a\\nlong time to cast and will be\\ninterrupted by combat.", null, null, 0),
        EDGEVILLE_HOME_TELEPORT(ANCIENT, TELEPORT, 21741, "Edgeville Home\\nTeleport", "Teleports you to home area", null, null, 0),
        ICE_RUSH(ANCIENT, COMBAT, 12861, "Ice Rush", "A single target ice attack", new RequiredItem[] {CHAOS_RUNE, DEATH_RUNE, WATER_RUNE}, new int[] { 2, 2, 2 }, 58, USABLE_ON_ENTITY),
        ICE_BLITZ(ANCIENT, COMBAT, 12871, "Ice Blitz", "A single target strong ice attack", new RequiredItem[] {DEATH_RUNE, BLOOD_RUNE, WATER_RUNE}, new int[] { 2, 2, 3 }, 82, USABLE_ON_ENTITY),
        ICE_BURST(ANCIENT, COMBAT, 12881, "Ice Burst", "A multi-target ice attack", new RequiredItem[] {CHAOS_RUNE, DEATH_RUNE, WATER_RUNE}, new int[] { 4, 2, 4 }, 70, USABLE_ON_ENTITY),
        ICE_BARRAGE(ANCIENT, COMBAT, 12891, "Ice Barrage", "A multi-target strong ice attack", new RequiredItem[] {DEATH_RUNE, BLOOD_RUNE, WATER_RUNE}, new int[] { 4, 2, 6 }, 94, USABLE_ON_ENTITY),
        BLOOD_RUSH(ANCIENT, COMBAT, 12901, "Blood Rush", "A single target blood attack", new RequiredItem[] {CHAOS_RUNE, DEATH_RUNE, BLOOD_RUNE}, new int[] { 2, 2, 1 }, 56, USABLE_ON_ENTITY),
        BLOOD_BLITZ(ANCIENT, COMBAT, 12911, "Blood Blitz", "A single target strong blood attack", new RequiredItem[] {DEATH_RUNE, BLOOD_RUNE}, new int[] { 2, 4 }, 80, USABLE_ON_ENTITY),
        BLOOD_BURST(ANCIENT, COMBAT, 12919, "Blood Burst", "A multi-target blood attack", new RequiredItem[] {CHAOS_RUNE, DEATH_RUNE, BLOOD_RUNE}, new int[] { 4, 2, 2 }, 68, USABLE_ON_ENTITY),
        BLOOD_BARRAGE(ANCIENT, COMBAT, 12929, "Blood Barrage", "A multi-target strong blood attack", new RequiredItem[] {DEATH_RUNE, BLOOD_RUNE, SOUL_RUNE}, new int[] { 4, 4, 1 }, 92, USABLE_ON_ENTITY),
        SMOKE_RUSH(ANCIENT, COMBAT, 12939, "Smoke Rush", "A single target smoke attack", new RequiredItem[] {CHAOS_RUNE, DEATH_RUNE, FIRE_RUNE, AIR_RUNE}, new int[] { 2, 2, 1, 1 }, 50, USABLE_ON_ENTITY),
        SMOKE_BLITZ(ANCIENT, COMBAT, 12951, "Smoke Blitz", "A single target strong smoke attack", new RequiredItem[] {DEATH_RUNE, BLOOD_RUNE, FIRE_RUNE, AIR_RUNE}, new int[] { 2, 2, 2, 2 }, 74, USABLE_ON_ENTITY),
        SMOKE_BURST(ANCIENT, COMBAT, 12963, "Smoke Burst", "A multi-target smoke attack", new RequiredItem[] {CHAOS_RUNE, DEATH_RUNE, FIRE_RUNE, AIR_RUNE}, new int[] { 4, 2, 2, 2 }, 62, USABLE_ON_ENTITY),
        SMOKE_BARRAGE(ANCIENT, COMBAT, 12975, "Smoke Barrage", "A multi-target strong smoke attack", new RequiredItem[] {DEATH_RUNE, BLOOD_RUNE, FIRE_RUNE, AIR_RUNE}, new int[] { 4, 2, 4, 4 }, 86, USABLE_ON_ENTITY),
        SHADOW_RUSH(ANCIENT, COMBAT, 12987, "Shadow Rush", "A single target shadow attack", new RequiredItem[] {CHAOS_RUNE, DEATH_RUNE, AIR_RUNE, SOUL_RUNE}, new int[] { 2, 2, 1, 1 }, 52, USABLE_ON_ENTITY),
        SHADOW_BLITZ(ANCIENT, COMBAT, 12999, "Shadow Blitz", "A single target strong shadow\\nattack", new RequiredItem[] {DEATH_RUNE, BLOOD_RUNE, AIR_RUNE, SOUL_RUNE}, new int[] { 2, 2, 2, 2 }, 76, USABLE_ON_ENTITY),
        SHADOW_BURST(ANCIENT, COMBAT, 13011, "Shadow Burst", "A multi-target shadow attack", new RequiredItem[] {CHAOS_RUNE, DEATH_RUNE, AIR_RUNE, SOUL_RUNE}, new int[] { 4, 2, 2, 2 }, 64, USABLE_ON_ENTITY),
        SHADOW_BARRAGE(ANCIENT, COMBAT, 13023, "Shadow Barrage", "A multi-target strong shadow\\nattack", new RequiredItem[] {DEATH_RUNE, BLOOD_RUNE, AIR_RUNE, SOUL_RUNE}, new int[] { 4, 2, 4, 3 }, 88, USABLE_ON_ENTITY),
        PADDEWWA_TELEPORT(ANCIENT, TELEPORT, 13035, 1118, 1117, "Paddewwa Teleport", "A teleportation spell", new RequiredItem[] {LAW_RUNE, FIRE_RUNE, AIR_RUNE}, new int[] { 2, 1, 1 }, 54),
        SENNTISTEN_TELEPORT(ANCIENT, TELEPORT, 13045, 1120, 1119, "Senntisten Teleport", "A teleportation spell", new RequiredItem[] {LAW_RUNE, SOUL_RUNE}, new int[] { 2, 1 }, 60),
        KHARYRLL_TELEPORT(ANCIENT, TELEPORT, 13053, 1122, 1121, "Kharyll Teleport", "A teleportation spell", new RequiredItem[] {LAW_RUNE, BLOOD_RUNE}, new int[] { 2, 1 }, 66),
        LASSAR_TELEPORT(ANCIENT, TELEPORT, 13061, 1124, 1123, "Lassar Teleport", "A teleportation spell", new RequiredItem[] {LAW_RUNE, WATER_RUNE}, new int[] { 2, 4 }, 72),
        DAREEYAK_TELEPORT(ANCIENT, TELEPORT, 13069, 1126, 1125, "Dareeyak Teleport", "A teleportation spell", new RequiredItem[] {LAW_RUNE, AIR_RUNE, FIRE_RUNE}, new int[] { 2, 2, 3 }, 78),
        CARRALLANGAR_TELEPORT(ANCIENT, TELEPORT, 13079, 1128, 1127, "Carrallangar Teleport", "A teleportation spell", new RequiredItem[] {LAW_RUNE, SOUL_RUNE}, new int[] { 2, 2 }, 84),
        TELEPORT_TO_BOUNTY_TARGET_2(ANCIENT, TELEPORT, 30333, 990, 991, "Teleport to Bounty\\nTarget", "Teleports you near your Bounty\\nHunter target", new RequiredItem[] {LAW_RUNE, DEATH_RUNE, CHAOS_RUNE}, new int[] { 1, 1, 1 }, 85),
        ANNAKARL_TELEPORT(ANCIENT, TELEPORT, 13087, 1130, 1129, "Annakarl Teleport", "A teleportation spell", new RequiredItem[] {LAW_RUNE, BLOOD_RUNE}, new int[] { 2, 2 }, 90),
        GHORROCK_TELEPORT(ANCIENT, TELEPORT, 13095, 999, 1000, "Ghorrock Teleport", "A teleportation spell", new RequiredItem[] {LAW_RUNE, WATER_RUNE}, new int[] { 2, 8 }, 96),

        /**
         * Lunars
         */
        
        //LUMBRIDGE_HOME_TELEPORT_2(LUNAR, TELEPORT, 30016, 987, "Lumbridge Home\\nTeleport", "Requires no runes - recharge time\\n30 mins. Warning: This spell takes a\\nlong time to cast and will be\\ninterrupted by combat.", null, null, 0),
        LUMBRIDGE_HOME_TELEPORT_2(LUNAR, TELEPORT, 30016, 987, "Edgeville Home\\nTeleport", "Teleports you to home area", null, null, 0),
        //BAKE_PIE(LUNAR, UTILITY, 30017, 246, 285, "Bake Pie", "Bake pies without a stove", new RequiredItem[] {ASTRAL_RUNE, FIRE_RUNE, WATER_RUNE}, new int[] { 1, 5, 4 }, 65, USABLE_ON_INVENTORY),
        //CURE_PLANT(LUNAR, UTILITY, 30025, 247, 286, "Cure Plant", "Cure disease on farming patch", new RequiredItem[] {ASTRAL_RUNE, EARTH_RUNE}, new int[] { 1, 8 }, 66, USABLE_ON_OBJECT),
        //MONSTER_EXAMINE(LUNAR, COMBAT, 30032, 248, 287, "Monster Examine", "Detect the combat statistics of a\\nmonster", new RequiredItem[] {ASTRAL_RUNE, COSMIC_RUNE, MIND_RUNE}, new int[] { 1, 1, 1 }, 66, USABLE_ON_ENTITY),
        //NPC_CONTACT(LUNAR, UTILITY, 30040, 249, 288, "NPC Contact", "Speak with varied NPCs", new RequiredItem[] {ASTRAL_RUNE, COSMIC_RUNE, AIR_RUNE}, new int[] { 1, 1, 2 }, 67, USABLE_ON_ENTITY),
        //CURE_OTHER(LUNAR, COMBAT, 30048, 250, 289, "Cure Other", "Cure poisoned players", new RequiredItem[] {ASTRAL_RUNE, LAW_RUNE, EARTH_RUNE}, new int[] { 1, 1, 10 }, 68, USABLE_ON_ENTITY),
        //HUMIDIFY(LUNAR, UTILITY, 30056, 251, 290, "Humidify", "Fills certain vessels with water", new RequiredItem[] {ASTRAL_RUNE, WATER_RUNE, FIRE_RUNE}, new int[] { 1, 3, 1 }, 68),
        MOONCLAN_TELEPORT(LUNAR, TELEPORT, 30064, 1141, 1140, "Moonclan Teleport", "Teleports you to the Lunar Isle", new RequiredItem[] {ASTRAL_RUNE, LAW_RUNE, EARTH_RUNE}, new int[] { 2, 1, 2 }, 69),
        //MINIGAME_TELEPORT_3(LUNAR, TELEPORT, 30075, 981, "Minigame Teleport", "Teleports you to minigames", null, null, 70),
        OURANIA_TELEPORT(LUNAR, TELEPORT, 30065, 1143, 1142, "Ourania Teleport", "Teleports you to the Ourania Cave", new RequiredItem[] {ASTRAL_RUNE, LAW_RUNE, EARTH_RUNE}, new int[] { 2, 1, 6 }, 71),
        //CURE_ME(LUNAR, COMBAT, 30091, 255, 294, "Cure Me", "Cures Poison", new RequiredItem[] {ASTRAL_RUNE, COSMIC_RUNE, LAW_RUNE}, new int[] { 2, 2, 1 }, 71),
        //HUNTER_KIT(LUNAR, UTILITY, 30099, 256, 295, "Hunter Kit", "Get a kit of hunting gear", new RequiredItem[] {ASTRAL_RUNE, EARTH_RUNE}, new int[] { 2, 2 }, 71),
        WATERBIRTH_TELEPORT(LUNAR, TELEPORT, 30106, 1145, 1144, "Waterbirth Teleport", "Teleports you to Waterbirth Island", new RequiredItem[] {ASTRAL_RUNE, LAW_RUNE, WATER_RUNE}, new int[] { 2, 1, 1 }, 72),
        BARBARIAN_TELEPORT(LUNAR, TELEPORT, 30114, 1147, 1146, "Barbarian Teleport", "Teleports you to the Barbarian\\noutpost", new RequiredItem[] {ASTRAL_RUNE, LAW_RUNE, FIRE_RUNE}, new int[] { 2, 2, 3 }, 75),
        //CURE_GROUP(LUNAR, COMBAT, 30122, 259, 298, "Cure Group", "Cures poison on players", new RequiredItem[] {ASTRAL_RUNE, COSMIC_RUNE, LAW_RUNE}, new int[] { 2, 2, 2 }, 74),
        //STAT_SPY(LUNAR, UTILITY, 30130, 260, 299, "Stat Spy", "Cast on another player to see\\ntheir skill levels", new RequiredItem[] {ASTRAL_RUNE, COSMIC_RUNE, BODY_RUNE}, new int[] { 2, 2, 5 }, 75, USABLE_ON_ENTITY),
        KHAZARD_TELEPORT(LUNAR, TELEPORT, 30138, 1149, 1148, "Khazard Teleport", "Teleports you to Port\\nKhazard", new RequiredItem[] {ASTRAL_RUNE, LAW_RUNE, WATER_RUNE}, new int[] { 2, 2, 4 }, 78),
        FISHING_GUILD_TELEPORT(LUNAR, TELEPORT, 30146, 1151, 1150, "Fishing Guild Teleport", "Teleports you to Fishing Guild", new RequiredItem[] {ASTRAL_RUNE, LAW_RUNE, WATER_RUNE}, new int[] { 3, 3, 10 }, 85),
        //SUPERGLASS_MAKE(LUNAR, UTILITY, 30154, 263, 302, "Superglass Make", "Make glass without a furnace", new RequiredItem[] {ASTRAL_RUNE, FIRE_RUNE, AIR_RUNE}, new int[] { 2, 6, 10 }, 77, USABLE_ON_INVENTORY),
        CATHERBY_TELEPORT(LUNAR, TELEPORT, 30169, 1153, 1152, "Catherby Teleport", "Teleports you to Catherby", new RequiredItem[] {ASTRAL_RUNE, LAW_RUNE, WATER_RUNE}, new int[] { 3, 3, 10 }, 87),
        //DREAM(LUNAR, UTILITY, 30178, 266, 305, "Dream", "Take a rest and restore hitpoints\\n3 times faster", new RequiredItem[] {ASTRAL_RUNE, COSMIC_RUNE, BODY_RUNE}, new int[] { 2, 1, 5 }, 79),
        //STRING_JEWELLERY(LUNAR, UTILITY, 30186, 267, 306, "String Jewellery", "String amulets without wool", new RequiredItem[] {ASTRAL_RUNE, EARTH_RUNE, WATER_RUNE}, new int[] { 2, 10, 5 }, 80),
        //STAT_RESTORE_POT_SHARE(LUNAR, COMBAT, 30194, 268, 307, "Stat Restore Pot\\nShare", "Share a potion with up to 4 nearby\\nplayers", new RequiredItem[] {ASTRAL_RUNE, EARTH_RUNE, WATER_RUNE}, new int[] { 2, 10, 10 }, 81),
        //MAGIC_IMBUE(LUNAR, UTILITY, 30202, 269, 308, "Magic Imbue", "Combine runes without a talisman", new RequiredItem[] {ASTRAL_RUNE, FIRE_RUNE, WATER_RUNE}, new int[] { 2, 7, 7 }, 82),
        //FERTILE_SOIL(LUNAR, UTILITY, 30210, 270, 309, "Fertile Soil", "Fertilise a farming patch with\\nsuper compost", new RequiredItem[] {ASTRAL_RUNE, NATURE_RUNE, EARTH_RUNE}, new int[] { 3, 2, 15 }, 83, USABLE_ON_OBJECT),
        //BOOST_POTION_SHARE(LUNAR, COMBAT, 30218, 271, 310, "Boost Potion Share", "Share a potion with up to 4\\nnearby players", new RequiredItem[] {ASTRAL_RUNE, EARTH_RUNE, WATER_RUNE}, new int[] { 3, 10, 12 }, 84),
        TELEPORT_TO_BOUNTY_TARGET_3(LUNAR, TELEPORT, 30334, 990, 991, "Teleport to Bounty\\nTarget", "Teleports you near your Bounty\\nHunter target", new RequiredItem[] {LAW_RUNE, DEATH_RUNE, CHAOS_RUNE}, new int[] { 1, 1, 1 }, 85),
        //PLANK_MAKE(LUNAR, UTILITY, 30242, 274, 313, "Plank Make", "Turn logs into planks", new RequiredItem[] {ASTRAL_RUNE, EARTH_RUNE, NATURE_RUNE}, new int[] { 3, 15, 1 }, 86),
        ICE_PLATEAU_TELEPORT(LUNAR, TELEPORT, 30163, 1155, 1154, "Ice Plateau Teleport", "Teleports you to Ice Plateau", new RequiredItem[] {ASTRAL_RUNE, LAW_RUNE, WATER_RUNE}, new int[] { 3, 3, 8 }, 89),

        //ENERGY_TRANSFER(LUNAR, UTILITY, 30282, 279, 318, "Energy Transfer", "Spend HP and SA energy to\\ngive another SA and run energy", new RequiredItem[] {ASTRAL_RUNE, LAW_RUNE, NATURE_RUNE}, new int[] { 3, 2, 1 }, 91, USABLE_ON_ENTITY),
        //HEAL_OTHER(LUNAR, COMBAT, 30290, 280, 319, "Heal Other", "Transfer up to 75% of hitpoints\\nto another player", new RequiredItem[] {ASTRAL_RUNE, LAW_RUNE, BLOOD_RUNE}, new int[] { 3, 3, 1 }, 92, USABLE_ON_ENTITY),
        VENGEANCE_OTHER(LUNAR, COMBAT, 30298, 281, 320, "Vengeance Other", "Allows another player to rebound\\n, damage to an opponent", new RequiredItem[] {ASTRAL_RUNE, DEATH_RUNE, EARTH_RUNE}, new int[] { 3, 1, 10 }, 93, USABLE_ON_ENTITY),
        VENGEANCE(LUNAR, COMBAT, 30306, 282, 321, "Vengeance", "Rebound damage to an opponent", new RequiredItem[] {ASTRAL_RUNE, DEATH_RUNE, EARTH_RUNE}, new int[] { 4, 2, 10 }, 94),
        //HEAL_GROUP(LUNAR, COMBAT, 30314, 283, 322, "Heal Group", "Transfer up to 75% of hitpoints\\nto a group", new RequiredItem[] {ASTRAL_RUNE, BLOOD_RUNE, LAW_RUNE}, new int[] { 4, 3, 6 }, 95),
        SPELLBOOK_SWAP(LUNAR, UTILITY, 30322, 284, 323, "Spellbook Swap", "Change to another spellbook for 1\\nspell cast", new RequiredItem[] {ASTRAL_RUNE, COSMIC_RUNE, LAW_RUNE}, new int[] { 3, 2, 1 }, 96),


        // Arceeus spellbook
        REANIMATE_GOBLIN(ARCEUUS, UTILITY, 32100, 284, 323, "Reanimate Goblin", "Change to another spellbook for 1\\nspell cast", new RequiredItem[] {ASTRAL_RUNE, COSMIC_RUNE, LAW_RUNE}, new int[] { 3, 2, 1 }, 3)

        ;







        private String name;
        private Spellbook spellbook;
        private SpellType spellType;
        private int id;
        private int hoverId;
        private Sprite disabledSprite;
        private Sprite enabledSprite;
        private String description;
        private RequiredItem[] requiredItems;
        private int[] runeAmts;
        private int level;
        private int usableOn;

        public String getName() {
            return name;
        }

        public Spellbook getSpellbook() {
            return spellbook;
        }

        public SpellType getSpellType() {
            return spellType;
        }

        public int getId() {
            return id;
        }

        public int getLevel() {
            return level;
        }

        Spell(Spellbook spellbook, SpellType spellType, int id, String name, String description, RequiredItem[] requiredItems, int[] runeAmts, int level) {
            this.spellbook = spellbook;
            this.spellType = spellType;
            this.id = id;
            this.hoverId = interfaceCache[id].hoverType;
            this.disabledSprite = interfaceCache[id].disabledSprite;
            this.enabledSprite = interfaceCache[id].enabledSprite;
            this.name = name;
            this.description = description;
            this.requiredItems = requiredItems;
            this.runeAmts = runeAmts;
            this.level = level;
        }

        Spell(Spellbook spellbook, SpellType spellType, int id, String name, String description, RequiredItem[] requiredItems, int[] runeAmts, int level, int usableOn) {
            this.spellbook = spellbook;
            this.spellType = spellType;
            this.id = id;
            this.hoverId = interfaceCache[id].hoverType;
            this.disabledSprite = interfaceCache[id].disabledSprite;
            this.enabledSprite = interfaceCache[id].enabledSprite;
            this.name = name;
            this.description = description;
            this.requiredItems = requiredItems;
            this.runeAmts = runeAmts;
            this.level = level;
            this.usableOn = usableOn;
        }

        Spell(Spellbook spellbook, SpellType spellType, int id, int spriteId, String name, String description, RequiredItem[] requiredItems, int[] runeAmts, int level) {
            this.spellbook = spellbook;
            this.spellType = spellType;
            this.id = id;
            this.hoverId = CUSTOM_HOVERS_START_ID + (customSpellCount++ * 10);
            this.disabledSprite = SpriteLoader.getSprite(spriteId);
            this.enabledSprite = SpriteLoader.getSprite(spriteId);
            this.name = name;
            this.description = description;
            this.requiredItems = requiredItems;
            this.runeAmts = runeAmts;
            this.level = level;
        }

        Spell(Spellbook spellbook, SpellType spellType, int id, int disabledSprite, int enabledSprite, String name, String description, RequiredItem[] requiredItems, int[] runeAmts, int level) {
            this.spellbook = spellbook;
            this.spellType = spellType;
            this.id = id;
            this.hoverId = CUSTOM_HOVERS_START_ID + (customSpellCount++ * 10);
            this.disabledSprite = SpriteLoader.getSprite(disabledSprite);
            this.enabledSprite = SpriteLoader.getSprite(enabledSprite);
            this.name = name;
            this.description = description;
            this.requiredItems = requiredItems;
            this.runeAmts = runeAmts;
            this.level = level;
        }

        Spell(Spellbook spellbook, SpellType spellType, int id, int disabledSprite, int enabledSprite, String name, String description, RequiredItem[] requiredItems, int[] runeAmts, int level, int usableOn) {
            this.spellbook = spellbook;
            this.spellType = spellType;
            this.id = id;
            this.hoverId = CUSTOM_HOVERS_START_ID + (customSpellCount++ * 10);
            this.disabledSprite = SpriteLoader.getSprite(disabledSprite);
            this.enabledSprite = SpriteLoader.getSprite(enabledSprite);
            this.name = name;
            this.description = description;
            this.requiredItems = requiredItems;
            this.runeAmts = runeAmts;
            this.level = level;
            this.usableOn = usableOn;
        }

    }

    public static void widgets(GameFont[] tda) {
        /**
         * Build spellbook spell lists
         */
        for (Spell spell : Spell.values()) {
            spell.getSpellbook().getSpells().add(spell);
        }

        /**
         * Runes
         */
        int runeId = ITEM_MODELS_START_ID;
        for (RequiredItem requiredItem : RequiredItem.values()) {

                addItemModel(runeId, requiredItem);
                runeId++;
            
        }

        /**
         * Spellbooks
         */
        for (Spellbook spellbook : Spellbook.values()) {
            widget(spellbook, tda);
        }
        addText(NO_SPELLS_MATCH_TEXT_ID, "No spells match your selected\\nfilters.", tda, 1, 0xff981f, true);
        int buttonWidth = 46;
        int buttonHeight = FILTER_BUTTON_HEIGHT;
        addHoverTextConfigButton(FILTER_BUTTON_ID, buttonWidth, buttonHeight, AutomaticSprite.BUTTON_GREY, AutomaticSprite.BUTTON_RED, "Filters", tda, 0, 0xff981f, 0xffffff, 1, FILTER_BUTTON_CONFIG);

        /**
         * Panel
         */
        filterPanel(tda);
    }

    public static void widget(Spellbook spellbook, GameFont[] tda) {
        int id = spellbook.getId();
        List<Spell> spells = spellbook.getSpells();

        int parent = id;
        Widget book = addTabInterface(id++);

        /**
         * Buttons
         */
        for (Spell spell : spells) {
            addSpell(spell.id, spell.hoverId, parent, spell.disabledSprite, spell.enabledSprite, spellbook.getSpellWidth(), spellbook.getSpellHeight(), spell.usableOn != 0 ? OPTION_USABLE : OPTION_OK, spell.name, spell.description, spell.usableOn, spell.requiredItems, spell.runeAmts, spell.level, tda);
        }
    }

    public static void filterPanel(GameFont[] tda) {
        int id = FILTER_PANEL_ID;
        Widget panel = addTabInterface(id++);
        panel.totalChildren(27);
        int child = 0;

        addSprite(id, 994);
        panel.child(child++, id++, 5, 4);

        addText(id, "Spell Filters", tda, 2, 0xff981f, true);
        panel.child(child++, id++, 95, 10);

        int currentY = 30;
        addPanelToggleButton(panel, child, id, "Show <col=ffffff>Combat</col> spells", 174, 25, 8, currentY, 0, FILTER_COMBAT_BUTTON_CONFIG, tda);
        currentY += 25;
        child += 3;
        id += 3;

        addPanelToggleButton(panel, child, id, "Show <col=ffffff>Teleport</col> spells", 174, 25, 8, currentY, 0, FILTER_TELEPORT_BUTTON_CONFIG, tda);
        currentY += 25;
        child += 3;
        id += 3;

        addPanelToggleButton(panel, child, id, "Show <col=ffffff>Utility</col> spells", 174, 25, 8, currentY, 0, FILTER_UTILITY_BUTTON_CONFIG, tda);
        currentY += 25;
        child += 3;
        id += 3;

        addPanelToggleButton(panel, child, id, "Show spells you lack the\\nMagic level to cast", 174, 25, 8, currentY, 0, FILTER_LEVEL_BUTTON_CONFIG, tda);
        currentY += 25;
        child += 3;
        id += 3;

        addPanelToggleButton(panel, child, id, "Show spells you lack the\\nrunes to cast", 174, 25, 8, currentY, 0, FILTER_RUNES_BUTTON_CONFIG, tda);
        currentY += 25;
        child += 3;
        id += 3;

        currentY += 3;

        int configIndex = 0;
        addPanelResetButton(panel, child, id, "Sort by level order", 174, 25, 8, currentY, configIndex++, SORT_SPELL_STATE_CONFIG, tda);
        currentY += 25;
        child += 3;
        id += 3;

        addPanelResetButton(panel, child, id, "Sort by <col=ffffff>Combat</col> spells first", 174, 25, 8, currentY, configIndex++, SORT_SPELL_STATE_CONFIG, tda);
        currentY += 25;
        child += 3;
        id += 3;

        addPanelResetButton(panel, child, id, "Sort by <col=ffffff>Teleport</col> spells first", 174, 25, 8, currentY, configIndex++, SORT_SPELL_STATE_CONFIG, tda);
        currentY += 25;
        child += 3;
        id += 3;

        panel.child(child++, FILTER_BUTTON_ID, 72, SPELLS_AREA_HEIGHT);
    }

    private static void addPanelToggleButton(Widget widget, int child, int id, String name, int width, int height, int x, int y, int configIndex, int config, GameFont[] tda) {
        addTransparentHoverRectangle(id, width, height, 0xffffff, true, 0, 30);
        widget.child(child++, id++, x, y);

        boolean isSplit = name.contains("\\n");
        int textY = isSplit ? y + 1 : y + 7;
        addText(id, name, tda, 0, 0xff981f);
        widget.child(child++, id++, x + 23, textY);

        addToggleSettingButton(id, width, height, 4, (height - 15) / 2,875, 874, name, configIndex, config);
        widget.child(child++, id++, x, y);
    }

    private static void addPanelResetButton(Widget widget, int child, int id, String name, int width, int height, int x, int y, int configIndex, int config, GameFont[] tda) {
        addTransparentHoverRectangle(id, width, height, 0xffffff, true, 0, 30);
        widget.child(child++, id++, x, y);

        boolean isSplit = name.contains("\\n");
        int textY = isSplit ? y + 1 : y + 7;
        addText(id, name, tda, 0, 0xff981f);
        widget.child(child++, id++, x + 23, textY);

        addResetSettingButton(id, width, height, 4, (height - 15) / 2,873, 874, name, configIndex, config);
        widget.child(child++, id++, x, y);
    }

    private static void addSpell(int id, int hoverId, int parent, Sprite disabledSprite, Sprite enabledSprite, int width, int height, int atActionType, String name, String description, int usableOn, RequiredItem[] requiredItems, int[] runeAmts, int level, GameFont[] tda) {
        Widget spell = interfaceCache[id] = new Widget();
        disabledSprite.setDrawOffsetX((width - disabledSprite.myWidth) / 2);
        disabledSprite.setDrawOffsetY((height - disabledSprite.myHeight) / 2);
        enabledSprite.setDrawOffsetX((width - enabledSprite.myWidth) / 2);
        enabledSprite.setDrawOffsetY((height - enabledSprite.myHeight) / 2);

        spell.id = id;
        spell.parent = parent;
        spell.width = width;
        spell.height = height;
        spell.atActionType = atActionType;
        spell.type = TYPE_SPRITE;
        spell.hoverType = hoverId;
        if (atActionType == OPTION_USABLE) {
            spell.spellUsableOn = usableOn;
            spell.selectedActionName = "Cast on";
            spell.spellName = name.replace("\\n", " ");
        } else {
            spell.tooltip = "Cast @gre@" + name.replace("\\n", " ");;
        }
        spell.disabledSprite = disabledSprite;
        spell.enabledSprite = enabledSprite;

        if (requiredItems != null) {
            spell.requiredValues = new int[runeAmts.length + 1];
            for (int i = 0; i < runeAmts.length; i++) {
                spell.requiredValues[i] = runeAmts[i] - 1;
            }
            spell.valueCompareType = new int[requiredItems.length + 1];
            for (int i = 0; i < spell.valueCompareType.length; i++) {
                spell.valueCompareType[i] = ClientCompanion.VALUE_GREATER_OR_EQUAL;
            }
            spell.valueIndexArray = new int[requiredItems.length + 1][];
            for (int i = 0; i < runeAmts.length; i++) {
                spell.valueIndexArray[i] = requiredItems[i].getValueIndexArray();
            }
        } else {
            spell.requiredValues = new int[1];
            spell.valueCompareType = new int[] { ClientCompanion.VALUE_GREATER_OR_EQUAL };
            spell.valueIndexArray = new int[1][];
        }
        spell.requiredValues[spell.requiredValues.length - 1] = level - 1;
        spell.valueIndexArray[spell.valueIndexArray.length - 1] = new int[] { 1, 6, 0 };

        /**
         * Hover box
         */
        Widget hBox = interfaceCache[hoverId] = new Widget();
        hBox.id = hoverId++;
        hBox.parent = parent;
        hBox.width = WIDTH + 6;
        hBox.hoverType = -1;
        hBox.invisible = true;
        hBox.totalChildren((requiredItems != null ? (requiredItems.length * 2) : 0) + 5);
        int child = 0;

        int descLines = StringUtils.countMatches(description, "\\\\n") + 1;
        int descIncrementY = (descLines - 1) * 10 + 8;
        String title = "Level " + level + ": " + name;
        int titleLines = StringUtils.countMatches(title, "\\\\n") + 1;
        int titleIncrementY;
        if (titleLines <= 1) {
            titleIncrementY = 10;
        } else {
            titleIncrementY = 22;
        }

        int currentY = 4;
        int boxHeight = currentY + titleIncrementY + 5 + descIncrementY + 2 + (requiredItems != null ? 6 + 39 : 0) + 6;

        hBox.height = boxHeight + 5;

        addTransparentRectangle(hoverId, 180, boxHeight, 0, true, 220);
        hBox.child(child++, hoverId++, 5, 5);

        addRectangle(hoverId, 179, boxHeight - 1, 0x2e2b23, false);
        hBox.child(child++, hoverId++, 6, 6);

        addRectangle(hoverId, 179, boxHeight - 1, 0x726451, false);
        hBox.child(child++, hoverId++, 5, 5);

        addText(hoverId, title, tda, 1, 0xff981f, true);
        hBox.child(child++, hoverId++, 6 + (178 / 2), currentY + 3);
        currentY += titleIncrementY + 5;

        addText(hoverId, description, tda, 0, 0xaf6a1a, true);
        hBox.child(child++, hoverId++, 6 + (178 / 2), currentY + 4);
        currentY += descIncrementY + 2;

        if (requiredItems != null) {
            currentY += 6;
            int boxInset = 8; // how far away a rune will be from the edge of the box if 4 runes are displayed, affects spacing for all amounts of runes
            int incrementX = (180 - (boxInset * 2)) / requiredItems.length;
            for (int i = 0; i < requiredItems.length; i++) {
                int x = boxInset + 5 + (incrementX * i) + ((incrementX - 27) / 2);
                hBox.child(child++, ITEM_MODELS_START_ID + requiredItems[i].ordinal(), x, currentY + 3);
                addItemText(hoverId, parent, requiredItems[i], runeAmts[i], tda);
                hBox.child(child++, hoverId++, x + 13, currentY + 32 + 3);
            }
        }

    }

    private static void addItemModel(int id, RequiredItem requiredItem) {
        Widget model = interfaceCache[id] = new Widget();
        model.id = id;
        model.parent = id;
        model.width = 28;
        model.height = 28;
        model.type = TYPE_MODEL;
        //model.defaultMediaType = 1;
        //model.defaultMedia = item.modelId;
        model.defaultMediaType = 4;
        int itemId = requiredItem.itemId;
        if (requiredItem.itemId == 0)
            itemId = requiredItem.staffItemIds[0];
        model.defaultMedia = itemId;
        /*model.modelRotation1 = 512;
        model.modelRotation2 = 1024;
        model.modelZoom = 730;*/
        int finalItemId = itemId;
        Js5.whenLoaded(() -> {
            ItemDefinition itemDef = ItemDefinition.lookup(finalItemId);
            model.modelXAngle = itemDef.rotation_y;// + rotationOffsetY;
            model.modelYAngle = itemDef.rotation_x;// + rotationOffsetX;
            model.modelZoom = (ItemDefinition.lookup(finalItemId).model_zoom * 100) / 96;
        });
    }

    private static void addItemText(int id, int parent, RequiredItem requiredItem, int amount, GameFont[] tda) {
        Widget text = interfaceCache[id] = new Widget();
        text.id = id;
        text.parent = parent;
        text.type = TYPE_TEXT;
        text.defaultText = "%1/" + amount;
        text.secondaryText = "";
        text.textColor = 0xc00000;
        text.secondaryColor = 0x00c000;
        text.centerText = true;
        text.requiredValues = new int[] { 0 };
        text.valueCompareType = new int[] { ClientCompanion.VALUE_GREATER_OR_EQUAL };
        text.valueIndexArray = new int[][] { requiredItem.getValueIndexArray() };
        text.textShadow = true;
        text.textDrawingAreas = tda[0];
    }

    public static void update(Spellbook spellbook) {
        int id = spellbook.getId();
        List<Spell> spells = getOrganizedList(spellbook.getSpells());
        int columns;
        int incrementX;
        int incrementY;
        int insetX;
        int insetY;
        int size = spells.size();

        if (size <= 0) {
            columns = 0;
            incrementX = 0;
            incrementY = 0;
            insetX = 0;
            insetY = 0;
        } else {
            int width = spellbook.getWidth();
            int height = SPELLS_AREA_HEIGHT;
            columns = 4;
            // Tries to fit within height with 4 columns, if it doesn't fit then increases columns and tries again
            while (
                    (
                    (((int) Math.ceil((double) size / columns)) - 1) // rows - 1
                    * ((int) Math.ceil((double) width / columns) - (spellbook.getDefaultIncrX() - spellbook.getDefaultIncrY())) // incrementY
                    )
                    + spellbook.getSpellHeight() // dont count height of entire increment, just count height of spell (this is why it is rows - 1 above)
                    > height) {
                columns++;
            }
            int rows = (int) Math.ceil((double) size / columns);
            incrementX = (int) Math.ceil((double) width / columns);
            if (incrementX * rows < height) {
                incrementY = incrementX;
            } else {
                incrementY = incrementX - (spellbook.getDefaultIncrX() - spellbook.getDefaultIncrY());
            }
            insetX = (WIDTH - ((incrementX * (columns - 1)) + spellbook.getSpellWidth())) / 2;
            insetY = Math.min(insetX, (height - ((incrementY * (rows - 1)) + spellbook.getSpellHeight())) / 2);
        }

        boolean drawText = spells.isEmpty();

        Widget book = addTabInterface(id++);
        book.totalChildren(spells.size() * 2 + 1 + (drawText ? 1 : 0));
        int child = 0;

        /**
         * Buttons
         */
        int index = 0;
        for (Spell spell : spells) {
            int row = (int) Math.ceil(index / columns);
            int column = index % columns;
            book.child(child++, spell.id, column * incrementX + insetX, row * incrementY + insetY);
            index++;
        }

        if (drawText)
            book.child(child++, NO_SPELLS_MATCH_TEXT_ID, 95, 106);

        /**
         * Organization buttons
         */
        book.child(child++, FILTER_BUTTON_ID, 72, SPELLS_AREA_HEIGHT);

        /**
         * Hovers
         */
        index = 0;
        for (Spell spell : spells) {
            int row = (int) Math.ceil(index / columns);
            int buttonY = row * incrementY + insetY;

            int y = book.height - interfaceCache[spell.hoverId].height - 5 - FILTER_BUTTON_HEIGHT;

            if (buttonY > HEIGHT / 2 - 25)
                y = 0;

            book.child(child++, spell.hoverId, 0, y);

            index++;
        }

    }

    public static List<Spell> getOrganizedList(List<Spell> spells) {
        List<Spell> copy = spells.stream()
                .filter(s -> s.getSpellType() == COMBAT && !Configuration.filterCombatSpells
                || s.getSpellType() == TELEPORT && !Configuration.filterTeleportSpells
                || s.getSpellType() == UTILITY && !Configuration.filterUtilitySpells)
                .filter(s -> s.getLevel() <= Client.instance.currentLevels[6] || !Configuration.filterLevel)
                .filter(s -> hasRequiredItems(interfaceCache[s.getId()]) || !Configuration.filterRunes)
                .sorted(Comparator.comparingInt(Spell::getLevel)).collect(Collectors.toList());
        if (Configuration.sortSpellsState != 0) {
            copy = copy.stream().sorted(Configuration.sortSpellsState == 2 ? Comparator.comparing(Spell::getSpellType).reversed() :
                    Comparator.comparing(Spell::getSpellType)).collect(Collectors.toList());
        }
        return copy;
    }

    public static boolean hasRequiredItems(Widget widget) {
        if (widget.valueCompareType == null)
            return true;
        for (int i = 0; i < widget.valueCompareType.length - 1; i++) {
            int value = Client.instance.executeScript(widget, i);
            int requiredValue = widget.requiredValues[i];
            if (value <= requiredValue)
                return false;
        }
        return true;
    }

}
