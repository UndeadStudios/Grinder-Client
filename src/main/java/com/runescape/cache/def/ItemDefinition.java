
package com.runescape.cache.def;

import com.grinder.client.ClientCompanion;
import com.runescape.Client;
import com.runescape.cache.Js5;
import com.runescape.cache.ModelData;
import com.runescape.cache.OsCache;
import com.runescape.cache.def.custom.ItemDefinition2;
import com.runescape.cache.def.custom.ItemDefinition3;
import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.cache.graphics.widget.ItemColorCustomizer;
import com.runescape.cache.graphics.widget.ItemColorCustomizer.ColorfulItem;
import com.runescape.collection.DualNode;
import com.runescape.collection.EvictingDualNodeHashTable;
import com.runescape.collection.IterableNodeHashTable;
import com.runescape.collection.OSCollections;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;

import java.util.*;

public final class ItemDefinition extends DualNode {
 
    public static EvictingDualNodeHashTable sprites = new EvictingDualNodeHashTable(100);
    public static EvictingDualNodeHashTable ItemDefinition_cached = new EvictingDualNodeHashTable(64);
    public static EvictingDualNodeHashTable ItemDefinition_cachedModels = new EvictingDualNodeHashTable(50);
    public static boolean isMembers = true;
    public int value;
    public int[] modified_model_colors; // base colors
    public int id;
    public int[] original_model_colors; // new colors
    public boolean is_members_only;
    public int noted_item_id;
    public int equipped_model_female_2;
    public int equipped_model_male_1;
    public String groundActions[];
    public int translate_x;
    public String name;
    public int inventory_model;
    public int equipped_model_male_dialogue_1;
    public boolean stackable;
    public String description;
    public int unnoted_item_id;
    public int model_zoom;
    public int equipped_model_male_2;
    public String actions[];
    public int rotation_y;
    public int[] stack_variant_id;
    public int translate_yz;//
    public int equipped_model_female_dialogue_1;
    public int rotation_x;
    public int equipped_model_female_1;
    public int[] stack_variant_size;
    public int team;
    public int rotation_z;
    private byte equipped_model_female_translation_y;
    private int equipped_model_female_3;
    private int equipped_model_male_dialogue_2;
    private int model_scale_x;
    private int equipped_model_female_dialogue_2;
    public int light_mag;
    private int equipped_model_male_3;
    private int model_scale_z;
    private int model_scale_y;
    public int light_intensity;
    private byte equipped_model_male_translation_y;

    private short[] originalTexture;
    private short[] modifiedTexture;
 
    private ItemDefinition() {
        id = -1;
    }
 
    public static void clear() {
        sprites = null;
    }

    public static int getTotalItems() {
        return Js5.configs.getRecordLength(10);
    }
 
    public static ItemDefinition lookup(int itemId) {
        ItemDefinition itemDef = (ItemDefinition)ItemDefinition_cached.get(itemId);
        if (itemDef != null) {
            return itemDef;
        } else {
            byte[] data = Js5.configs.takeRecord(10, itemId);
            itemDef = new ItemDefinition();
            itemDef.id = itemId;
            itemDef.setDefaults();
            if (data != null) {
                itemDef.readValues(new Buffer(data));
            }
        }

        if (itemDef.noted_item_id != -1)
            itemDef.toNote();
        if (itemId == 4724 || itemId == 4726 || itemId == 4728 || itemId == 4730 || itemId == 4753 || itemId == 4755
                || itemId == 4757 || itemId == 4759 || itemId == 4745 || itemId == 4747 || itemId == 4749
                || itemId == 4751 || itemId == 4708 || itemId == 4710 || itemId == 4712 || itemId == 4714
                || itemId == 4732 || itemId == 4734 || itemId == 4736 || itemId == 4738 || itemId == 4716
                || itemId == 4718 || itemId == 4720 || itemId == 4722) {
            itemDef.actions[2] = "Set";
        }

        // System.out.println("Item: "+itemDef.name+", equip models:
        // "+itemDef.equipped_model_male_1+", "+itemDef.equipped_model_male_2+",
        // "+itemDef.equipped_model_male_3);

        // Colorful item recoloring

        if(Client.instance != null) {
            ColorfulItem colorfulItem = ColorfulItem.forId(itemId);
            if (colorfulItem != null)
                ItemColorCustomizer.editColorfulItemActions(colorfulItem, itemDef);
        }
        /*
         * Place customs here
         */
        switch (itemId) {
            case 7510: // RFD QUEST ITEMS
            case 7573:
            case 7468:
            case 7579:
                itemDef.actions = new String[5];
                break;
            case 7479:
                itemDef.actions = new String[5];
                itemDef.actions[0] = "Eat";
                itemDef.actions[4] = "Destroy";
                break;
        case 9813:
        case 13068:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
            break;
            case 5589:
                itemDef.name = "Anti-addictivo potion";
                break;
        case 12249:
        	itemDef.equipped_model_male_1 = 28555;
        	break;
        case 12251:
        	itemDef.equipped_model_male_1 = 28553;
        	break;
        case 757:
            itemDef.name = "Voting guide";
            break;
        case 27552:
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.actions[4] = "Drop";
            itemDef.name = "Masori avernic defender";
            break;
        case 27550:
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.actions[4] = "Drop";
            itemDef.name = "Infernal defender";
            break;
        case 11844:
            itemDef.name = "Spiritual mage";
            break;
        case 11845:
            itemDef.name = "Spiritual ranger";
            break;
        case 11846:
            itemDef.name = "Spiritual warrior";
            break;
        case 20527:
            itemDef.name = "OSRS token";
            break;
        case 20355:
            itemDef.name = "Clue package";
        	itemDef.actions = new String[5];
            itemDef.actions[0] = "Activate";
            break;
        case 21802:
        	itemDef.actions = new String[5];
        	itemDef.actions[0] = "Teleport";
        	break;
        case 15274:
            itemDef.copy(lookup(739));
            itemDef.name = "Aggressivity potion";
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Drink";
            itemDef.description = "Makes you vulnerable to all monsters!";
            itemDef.noted_item_id = -1;
            itemDef.unnoted_item_id = 15275;
            break;
        case 15890:
            itemDef.copy(lookup(7993));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Eat";
            itemDef.name = "Big shark";
            itemDef.description = "It's a monster big fish!";
            itemDef.original_model_colors = new int[] { 0, 6073, 5738 };
            itemDef.modified_model_colors = new int[] { 0, 103, 61 };
            itemDef.noted_item_id = -1;
            itemDef.unnoted_item_id = 15891;
            break;
        case 15891:
            itemDef.copy(lookup(386));
            itemDef.actions = new String[5];
            itemDef.name = "Big shark";
            itemDef.original_model_colors = new int[] { 0, 6073, 5738 };
            itemDef.modified_model_colors = new int[] { 0, 103, 61 };
            itemDef.noted_item_id = 799;
            itemDef.unnoted_item_id = 15890;
            break;
        case 13282:
            itemDef.name = "Max cape (broken)";
            break;
        case 13656:
        case 12863:
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            break;
            case 15275:
            itemDef.copy(lookup(2429));
            itemDef.name = "Aggressivity potion";
            itemDef.actions = new String[5];
            itemDef.description = "Swap this note at any bank for the equivalent item.";
            itemDef.noted_item_id = 799;
            itemDef.unnoted_item_id = 15274;
            break;
        case 15276:
            itemDef.copy(lookup(1050));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Event santa hat";
            itemDef.description = "A santa's hat.";
            itemDef.original_model_colors = new int[] { 1000, 127 };
            itemDef.modified_model_colors = new int[] { 933, 10351 };
            itemDef.noted_item_id = -1;
            itemDef.unnoted_item_id = 15277;
            break;
        case 15277:
            itemDef.copy(lookup(1051));
            itemDef.actions = new String[5];
            itemDef.name = "Event santa hat";
            itemDef.description = "Swap this note at any bank for the equivalent item.";
            itemDef.original_model_colors = new int[] { 1000, 127 };
            itemDef.modified_model_colors = new int[] { 933, 10351 };
            itemDef.noted_item_id = 799;
            itemDef.unnoted_item_id = 15276;
            break;
        case 15278:
            itemDef.copy(lookup(4069));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Santa armour";
            itemDef.description = "A santa's armour.";
            itemDef.original_model_colors = new int[] { 127, 950 };
            itemDef.modified_model_colors = new int[] { 61, 41 };
            itemDef.noted_item_id = -1;
            itemDef.unnoted_item_id = 15279;
            break;
        case 15279:
            itemDef.copy(lookup(1128));
            itemDef.actions = new String[5];
            itemDef.name = "Santa armour";
            itemDef.description = "Swap this note at any bank for the equivalent item.";
            itemDef.original_model_colors = new int[] { 127, 950 };
            itemDef.modified_model_colors = new int[] { 61, 41 };
            itemDef.noted_item_id = 799;
            itemDef.unnoted_item_id = 15278;
            break;
        case 15280:
            itemDef.copy(lookup(4070));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Santa armour";
            itemDef.description = "A santa's armour.";
            itemDef.original_model_colors = new int[] { 127, 950, 950 };
            itemDef.modified_model_colors = new int[] { 61, 41, 57 };
            itemDef.noted_item_id = -1;
            itemDef.unnoted_item_id = 15281;
            break;
        case 15281:
            itemDef.copy(lookup(1080));
            itemDef.actions = new String[5];
            itemDef.name = "Santa armour";
            itemDef.description = "Swap this note at any bank for the equivalent item.";
            itemDef.original_model_colors = new int[] { 127, 950, 950 };
            itemDef.modified_model_colors = new int[] { 61, 41, 57 };
            itemDef.noted_item_id = 799;
            itemDef.unnoted_item_id = 15280;
            break;
        case 15282:
            itemDef.copy(lookup(4071));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Santa helm";
            itemDef.description = "A santa's helm.";
            itemDef.original_model_colors = new int[] { 127, 950 };
            itemDef.modified_model_colors = new int[] { 926, 61 };
            itemDef.noted_item_id = -1;
            itemDef.unnoted_item_id = 15283;
            break;
        case 15283:
            itemDef.copy(lookup(1148));
            itemDef.actions = new String[5];
            itemDef.name = "Santa helm";
            itemDef.description = "Swap this note at any bank for the equivalent item.";
            itemDef.original_model_colors = new int[] { 127, 950 };
            itemDef.modified_model_colors = new int[] { 926, 61 };
            itemDef.noted_item_id = 799;
            itemDef.unnoted_item_id = 15282;
            break;
        case 15284:
            itemDef.copy(lookup(4072));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Santa shield";
            itemDef.description = "A santa's shield.";
            itemDef.original_model_colors = new int[] { 127, 950, 950 };
            itemDef.modified_model_colors = new int[] { 61, 7054, 57 };
            itemDef.noted_item_id = -1;
            itemDef.unnoted_item_id = 15285;
            break;
        case 15285:
            itemDef.copy(lookup(1202));
            itemDef.actions = new String[5];
            itemDef.name = "Santa shield";
            itemDef.description = "Swap this note at any bank for the equivalent item.";
            itemDef.original_model_colors = new int[] { 127, 950, 950 };
            itemDef.modified_model_colors = new int[] { 61, 7054, 57 };
            itemDef.noted_item_id = 799;
            itemDef.unnoted_item_id = 15284;
            break;
        case 15286:
            itemDef.copy(lookup(3472));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Santa skirt";
            itemDef.description = "A santa's skirt.";
            itemDef.original_model_colors = new int[] { 127, 950, 950, 950 };
            itemDef.modified_model_colors = new int[] { 61, 41, 57, 25238 };
            itemDef.noted_item_id = -1;
            itemDef.unnoted_item_id = 15287;
            break;
        case 15287:
            itemDef.copy(lookup(3668));
            itemDef.actions = new String[5];
            itemDef.name = "Santa skirt";
            itemDef.description = "Swap this note at any bank for the equivalent item.";
            itemDef.original_model_colors = new int[] { 127, 950, 950, 950 };
            itemDef.modified_model_colors = new int[] { 61, 41, 57, 25238 };
            itemDef.noted_item_id = 799;
            itemDef.unnoted_item_id = 15286;
            break;
        case 26316:
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.actions[4] = "Destroy";
            break;
        case 15288:
            itemDef.copy(lookup(4068));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.name = "Santa sword";
            itemDef.description = "A santa's sword.";
            itemDef.original_model_colors = new int[] { 950, 950, 950, 950, 950, 950 };
            itemDef.modified_model_colors = new int[] { -22215, -21544, -22301, -23441, -22050, 127};
            itemDef.noted_item_id = -1;
            itemDef.unnoted_item_id = 15289;
            break;
        case 15289:
            itemDef.copy(lookup(1280));
            itemDef.actions = new String[5];
            itemDef.name = "Santa sword";
            itemDef.description = "Swap this note at any bank for the equivalent item.";
            itemDef.original_model_colors = new int[] { 950, 950, 950, 950, 950, 950 };
            itemDef.modified_model_colors = new int[] { -22215, -21544, -22301, -23441, -22050, 127};
            itemDef.noted_item_id = 799;
            itemDef.unnoted_item_id = 15288;
            break;
        case 15290:
            itemDef.copy(lookup(8839));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Void knight top (t)";
            itemDef.description = "A void knight top (t).";
            itemDef.original_model_colors = new int[] { -27310, -27310, -27310 };
            itemDef.modified_model_colors = new int[] { 16, 24, 90 };
            break;
        case 15291:
            itemDef.copy(lookup(8840));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Void knight robe (t)";
            itemDef.description = "A void knight robe (t).";
            itemDef.original_model_colors = new int[] { -27310, -27310, -27310 };
            itemDef.modified_model_colors = new int[] { 20, 90, 8 };
            break;
        case 15292:
            itemDef.copy(lookup(11663));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Void mage helm (t)";
            itemDef.description = "A void mage helm (t).";
            itemDef.original_model_colors = new int[] { -27310, -27310 };
            itemDef.modified_model_colors = new int[] { 70, 24 };
            break;
        case 15293:
            itemDef.copy(lookup(11664));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Void ranger helm (t)";
            itemDef.description = "A void ranger helm (t).";
            itemDef.original_model_colors = new int[] { -27310, -27310, -27310 };
            itemDef.modified_model_colors = new int[] { 78, 74, 90 };
            break;
        case 15294:
            itemDef.copy(lookup(11665));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Void melee helm (t)";
            itemDef.description = "A void melee helm (t).";
            itemDef.original_model_colors = new int[] { -27310, -27310, -27310 };
            itemDef.modified_model_colors = new int[] { 24, 90, 53 };
            break;
        case 15295:
            itemDef.copy(lookup(13072));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Elite void top (t)";
            itemDef.description = "An elite void top (t).";
            itemDef.original_model_colors = new int[] { -27310, -27310, -27310, -27310, -27310, -27310 };
            itemDef.modified_model_colors = new int[] { 24, 7442, 7446, 7322, 7326, 16 };
            break;
        case 15296:
            itemDef.copy(lookup(13073));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Elite void robe (t)";
            itemDef.description = "An elite void robe (t).";
            itemDef.original_model_colors = new int[] { -27310, -27310, -27310, -27310, -27310, -27310, -27310, -27310, -27310, -27310, -27310, -27310, -27310 };
            itemDef.modified_model_colors = new int[] { 115, 111, 119, 66, 12, 127, 82, 21568, 21576, 21568, 21559, 21572, 21563 };
            break;
        case 15297:
            itemDef.copy(lookup(2631));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Highwayman mask (t)";
            itemDef.description = "A highwayman mask (t).";
            itemDef.original_model_colors = new int[] { -27310 };
            itemDef.modified_model_colors = new int[] { 43146 };
            itemDef.noted_item_id = -1;
            itemDef.unnoted_item_id = 15461;
            break;
        case 15298:
            itemDef.copy(lookup(23854));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.name = "Staff of dominance";
            itemDef.description = "A special staff once used by the rulers of Hosidious.";
            itemDef.noted_item_id = -1;
            itemDef.unnoted_item_id = 15299;
            break;
        case 15299:
            itemDef.copy(lookup(1382));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.name = "Hosidious bane staff";
            itemDef.description = "Swap this note at any bank for the equivalent item.";
            itemDef.noted_item_id = 799;
            itemDef.unnoted_item_id = 15298;
            break;
            case 15301:
                itemDef.copy(lookup(20095));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Sky blue ankou mask";
                itemDef.description = "A gilded ankou mask.";
                itemDef.original_model_colors = new int[] { 38226 };
                itemDef.modified_model_colors = new int[] { 960 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15462;
                break;
            case 15302:
                itemDef.copy(lookup(20098));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Sky blue ankou top";
                itemDef.description = "A gilded ankou top.";
                itemDef.original_model_colors = new int[] { 38226 };
                itemDef.modified_model_colors = new int[] { 960 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15463;
                break;
            case 15303:
                itemDef.copy(lookup(20104));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Sky blue ankou leggings";
                itemDef.description = "A gilded ankou leggings.";
                itemDef.original_model_colors = new int[] { 38226 };
                itemDef.modified_model_colors = new int[] { 960 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15464;
                break;
            case 15304:
                itemDef.copy(lookup(20101));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Sky blue ankou gloves";
                itemDef.description = "A gilded ankou gloves.";
                itemDef.original_model_colors = new int[] { 38226 };
                itemDef.modified_model_colors = new int[] { 960 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15465;
                break;
            case 15305:
                itemDef.copy(lookup(20107));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Sky blue ankou socks";
                itemDef.description = "A gilded ankou socks.";
                itemDef.original_model_colors = new int[] { 38226 };
                itemDef.modified_model_colors = new int[] { 960 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15466;
                break;

            case 15306: // Blue
                itemDef.copy(lookup(1305));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Dragon longsword (or)";
                itemDef.description = "A very powerful and stylish sword.";
                itemDef.original_model_colors = new int[] { 38693 };
                itemDef.modified_model_colors = new int[] { 922 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15467;
                break;
            case 15307: // Orange
                itemDef.copy(lookup(1305));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Dragon longsword (or)";
                itemDef.description = "A very powerful and stylish sword.";
                itemDef.original_model_colors = new int[] { 120, 6073 };
                itemDef.modified_model_colors = new int[] { 37, 922 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15468;
                break;
            case 15308: // Green
                itemDef.copy(lookup(1305));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Dragon longsword (or)";
                itemDef.description = "A very powerful and stylish sword.";
                itemDef.original_model_colors = new int[] { 37, 86933 };
                itemDef.modified_model_colors = new int[] { 37, 922 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15469;
                break;
            case 15309: // Pink
                itemDef.copy(lookup(1305));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Dragon longsword (or)";
                itemDef.description = "A very powerful and stylish sword.";
                itemDef.original_model_colors = new int[] { 120, 51136 };
                itemDef.modified_model_colors = new int[] { 37, 922 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15470;
                break;
            // Red
            case 15310:
                itemDef.copy(lookup(2627));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Rune full helm (t)";
                itemDef.description = "Rune helmet with a red trim.";
                itemDef.original_model_colors = new int[] { 36133, 950 };
                itemDef.modified_model_colors = new int[] { 61, 926 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15471;
                break;
            case 15311:
                itemDef.copy(lookup(2623));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Rune platebody (t)";
                itemDef.description = "Rune platebody with a red trim.";
                itemDef.original_model_colors = new int[] { -29403, 950, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 24 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15472;

                break;
            case 15312:
                itemDef.copy(lookup(2625));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Rune platelegs (t)";
                itemDef.description = "Rune platelegs with a red trim.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15473;
                break;
            case 15313:
                itemDef.copy(lookup(2629));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Rune kiteshield (t)";
                itemDef.description = "Rune kiteshield with a red trim.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 950 };
                itemDef.modified_model_colors = new int[] { 61, 57, 7054 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15474;
                break;
            case 15314:
                itemDef.copy(lookup(3477));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Rune plateskirt (t)";
                itemDef.description = "Rune plateskirt with a red trim.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 950, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57, 25238 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15475;
                break;

            case 15315:
                itemDef.copy(lookup(2587));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Black full helm (t)";
                itemDef.description = "Black helmet with a red trim.";
                itemDef.original_model_colors = new int[] { 0, 950 };
                itemDef.modified_model_colors = new int[] { 61, 926 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15476;
                break;
            case 15316:
                itemDef.copy(lookup(2583));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Black platebody (t)";
                itemDef.description = "Black platebody with a red trim.";
                itemDef.original_model_colors = new int[] { 950, 8, 0 };
                itemDef.modified_model_colors = new int[] { 24, 61, 41 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15477;
                break;
            case 15317:
                itemDef.copy(lookup(2585));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Black platelegs (t)";
                itemDef.description = "Black platelegs with a red trim.";
                itemDef.original_model_colors = new int[] { 0, 0, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15478;
                break;
            case 15318:
                itemDef.copy(lookup(2589));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Black kiteshield (t)";
                itemDef.description = "Black kiteshield with a red trim.";
                itemDef.original_model_colors = new int[] { 0, 0, 950 };
                itemDef.modified_model_colors = new int[] { 61, 57, 7054 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15479;
                break;
            case 15319:
                itemDef.copy(lookup(3472));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Black plateskirt (t)";
                itemDef.description = "Black plateskirt with a red trim.";
                itemDef.original_model_colors = new int[] { 0, 0, 950, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57, 25238 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15480;
                break;
            // Blue
            case 15320:
                itemDef.copy(lookup(2587));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Black full helm (t)";
                itemDef.description = "Black helmet with a blue trim.";
                itemDef.original_model_colors = new int[] { 0, 38693 };
                itemDef.modified_model_colors = new int[] { 61, 926 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15481;
                break;
            case 15321:
                itemDef.copy(lookup(2583));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Black platebody (t)";
                itemDef.description = "Black platebody with a blue trim.";
                itemDef.original_model_colors = new int[] { 38693, 8, 0 };
                itemDef.modified_model_colors = new int[] { 24, 61, 41 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15482;
                break;
            case 15322:
                itemDef.copy(lookup(2585));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Black platelegs (t)";
                itemDef.description = "Black platelegs with a blue trim.";
                itemDef.original_model_colors = new int[] { 0, 0, 38693 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15483;
                break;
            case 15323:
                itemDef.copy(lookup(2589));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Black kiteshield (t)";
                itemDef.description = "Black kiteshield with a blue trim.";
                itemDef.original_model_colors = new int[] { 0, 0, 38693 };
                itemDef.modified_model_colors = new int[] { 61, 57, 7054 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15484;
                break;
            case 15324:
                itemDef.copy(lookup(3472));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Black plateskirt (t)";
                itemDef.description = "Black plateskirt with a blue trim.";
                itemDef.original_model_colors = new int[] { 0, 0, 38693, 38693 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57, 25238 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15485;
                break;
            // Green
            case 15325:
                itemDef.copy(lookup(2587));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Black full helm (t)";
                itemDef.description = "Black helmet with a green trim.";
                itemDef.original_model_colors = new int[] { 0, 86933 };
                itemDef.modified_model_colors = new int[] { 61, 926 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15486;
                break;
            case 15326:
                itemDef.copy(lookup(2583));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Black platebody (t)";
                itemDef.description = "Black platebody with a green trim.";
                itemDef.original_model_colors = new int[] { 86933, 8, 0 };
                itemDef.modified_model_colors = new int[] { 24, 61, 41 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15487;
                break;
            case 15327:
                itemDef.copy(lookup(2585));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Black platelegs (t)";
                itemDef.description = "Black platelegs with a green trim.";
                itemDef.original_model_colors = new int[] { 0, 0, 86933 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15488;
                break;
            case 15328:
                itemDef.copy(lookup(2589));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Black kiteshield (t)";
                itemDef.description = "Black kiteshield with a green trim.";
                itemDef.original_model_colors = new int[] { 0, 0, 86933 };
                itemDef.modified_model_colors = new int[] { 61, 57, 7054 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15489;
                break;
            case 15329:
                itemDef.copy(lookup(3472));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Black plateskirt (t)";
                itemDef.description = "Black plateskirt with a green trim.";
                itemDef.original_model_colors = new int[] { 0, 0, 86933, 86933 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57, 25238 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15490;
                break;

            // Blue
            case 15330:
                itemDef.copy(lookup(2627));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Rune full helm (t)";
                itemDef.description = "Rune helmet with a blue trim.";
                itemDef.original_model_colors = new int[] { 36133, 43968 };
                itemDef.modified_model_colors = new int[] { 61, 926 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15491;
                break;
            case 15331:
                itemDef.copy(lookup(2623));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Rune platebody (t)";
                itemDef.description = "Rune platebody with a blue trim.";
                itemDef.original_model_colors = new int[] { -29403,-28266, 43968 };
                itemDef.modified_model_colors = new int[] { 61, 41, 24 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15492;
                break;
            case 15332:
                itemDef.copy(lookup(2625));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Rune platelegs (t)";
                itemDef.description = "Rune platelegs with a blue trim.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 43968 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15493;
                break;
            case 15333:
                itemDef.copy(lookup(2629));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Rune kiteshield (t)";
                itemDef.description = "Rune kiteshield with a blue trim.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 43968 };
                itemDef.modified_model_colors = new int[] { 61, 57, 7054 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15494;
                break;
            case 15334:
                itemDef.copy(lookup(3477));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Rune plateskirt (t)";
                itemDef.description = "Rune plateskirt with a blue trim.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 43968, 43968 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57, 25238 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15495;
                break;
            // Green
            case 15335:
                itemDef.copy(lookup(2627));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Rune full helm (t)";
                itemDef.description = "Rune helmet with a green trim.";
                itemDef.original_model_colors = new int[] { 36133, 86933 };
                itemDef.modified_model_colors = new int[] { 61, 926 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15496;
                break;
            case 15336:
                itemDef.copy(lookup(2623));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Rune platebody (t)";
                itemDef.description = "Rune platebody with a green trim.";
                itemDef.original_model_colors = new int[] { -29403,86933, 86933 };
                itemDef.modified_model_colors = new int[] { 61, 41, 24 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15497;
                break;
            case 15337:
                itemDef.copy(lookup(2625));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Rune platelegs (t)";
                itemDef.description = "Rune platelegs with a green trim.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 86933 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15498;
                break;
            case 15338:
                itemDef.copy(lookup(2629));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Rune kiteshield (t)";
                itemDef.description = "Rune kiteshield with a green trim.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 86933 };
                itemDef.modified_model_colors = new int[] { 61, 57, 7054 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15499;
                break;
            case 15339:
                itemDef.copy(lookup(3477));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Rune plateskirt (t)";
                itemDef.description = "Rune plateskirt with a green trim.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 86933, 86933 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57, 25238 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15500;
                break;

            // Pink
            case 15340:
                itemDef.copy(lookup(2627));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Rune full helm (p)";
                itemDef.description = "Rune helmet with a pink trim.";
                itemDef.original_model_colors = new int[] { 36133, 51136 };
                itemDef.modified_model_colors = new int[] { 61, 926 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15501;
                break;
            case 15341:
                itemDef.copy(lookup(2623));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Rune platebody (p)";
                itemDef.description = "Rune platebody with a pink trim.";
                itemDef.original_model_colors = new int[] { -29403,-28266, 51136 };
                itemDef.modified_model_colors = new int[] { 61, 41, 24 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15502;
                break;
            case 15342:
                itemDef.copy(lookup(2625));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Rune platelegs (p)";
                itemDef.description = "Rune platelegs with a pink trim.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 51136 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15503;
                break;
            case 15343:
                itemDef.copy(lookup(2629));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Rune kiteshield (p)";
                itemDef.description = "Rune kiteshield with a pink trim.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 51136 };
                itemDef.modified_model_colors = new int[] { 61, 57, 7054 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15504;
                break;
            case 15344:
                itemDef.copy(lookup(3477));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Rune plateskirt (p)";
                itemDef.description = "Rune plateskirt with a pink trim.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 51136, 51136 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57, 25238 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15505;
                break;

                // Pink
            case 15345:
                itemDef.copy(lookup(4587));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Dragon scimitar (or)";
                itemDef.description = "A vicious, curved and stylish sword.";
                itemDef.original_model_colors = new int[] { 51136, 51136 };
                itemDef.modified_model_colors = new int[] { 933, 935 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15511;
                break;

            case 15346:
                itemDef.copy(lookup(4587));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Dragon scimitar (p)";
                itemDef.description = "A poisionous, vicious, stylish curved sword.";
                itemDef.original_model_colors = new int[] { 933, 13221 };
                itemDef.modified_model_colors = new int[] { 933, 935 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15512;
                break;
            case 15347:
                itemDef.copy(lookup(4587));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Dragon scimitar (p+)";
                itemDef.description = "A poisionous, vicious, stylish curved sword.";
                itemDef.original_model_colors = new int[] { 933, 13221 };
                itemDef.modified_model_colors = new int[] { 933, 935 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15513;
                break;
            case 15348:
                itemDef.copy(lookup(4587));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Dragon scimitar (p++)";
                itemDef.description = "An extremely poisionous, vicious, stylish curved sword.";
                itemDef.original_model_colors = new int[] { 933, 13221 };
                itemDef.modified_model_colors = new int[] { 933, 935 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15514;
                break;

            // Green
            case 15349:
                itemDef.copy(lookup(4587));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Dragon scimitar (or)";
                itemDef.description = "A vicious, curved and stylish sword.";
                itemDef.original_model_colors = new int[] { 86933, 86933 };
                itemDef.modified_model_colors = new int[] { 933, 935 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15515;
                break;
            // Blue
            case 15350:
                itemDef.copy(lookup(4587));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Dragon scimitar (or)";
                itemDef.description = "A vicious, curved and stylish sword.";
                itemDef.original_model_colors = new int[] { 43968, 43968 };
                itemDef.modified_model_colors = new int[] { 933, 935 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15516;
                break;
            // White
            case 15351:
                itemDef.copy(lookup(4587));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Dragon scimitar (or)";
                itemDef.description = "A vicious, curved and stylish sword.";
                itemDef.original_model_colors = new int[] { 125, 125 };
                itemDef.modified_model_colors = new int[] { 933, 935 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15517;
                break;

            case 15352:
                itemDef.copy(lookup(4151));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Bronze whip";
                itemDef.description = "A whip that is made out of Bronze material.";
                itemDef.original_model_colors = new int[] { 5652 };
                itemDef.modified_model_colors = new int[] { 528 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15518;
                break;
            case 15353:
                itemDef.copy(lookup(4151));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Iron whip";
                itemDef.description = "A whip that is made out of solid Iron material.";
                itemDef.original_model_colors = new int[] { 33 };
                itemDef.modified_model_colors = new int[] { 528 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15519;
                break;
            case 15354:
                itemDef.copy(lookup(4151));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Steel whip";
                itemDef.description = "A whip that is made out of hardened Steel material.";
                itemDef.original_model_colors = new int[] { 75 };
                itemDef.modified_model_colors = new int[] { 528 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15520;
                break;
            case 15355:
                itemDef.copy(lookup(4151));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Mithril whip";
                itemDef.description = "A whip that is made out of Mithril material.";
                itemDef.original_model_colors = new int[] { -6449257 };
                itemDef.modified_model_colors = new int[] { 528 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15521;
                break;
            case 15356:
                itemDef.copy(lookup(4151));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Adamant whip";
                itemDef.description = "A whip that is made out of Adamantite material.";
                itemDef.original_model_colors = new int[] { 21662 };
                itemDef.modified_model_colors = new int[] { 528 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15522;
                break;
            case 15357:
                itemDef.copy(lookup(4151));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Rune whip";
                itemDef.description = "A whip that is made out of military grade Runite material.";
                itemDef.original_model_colors = new int[] { 36133 };
                itemDef.modified_model_colors = new int[] { 528 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15523;
                break;

            case 15358:
                itemDef.copy(lookup(1038));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Peach partyhat";
                itemDef.description = "A nice hat from a cracker.";
                itemDef.original_model_colors = new int[] { 3610 };
                itemDef.modified_model_colors = new int[] { 926 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15524;
                break;
            case 15359:
                itemDef.copy(lookup(1038));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Cream partyhat";
                itemDef.description = "A nice hat from a cracker.";
                itemDef.original_model_colors = new int[] { 8500 };
                itemDef.modified_model_colors = new int[] { 926 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15525;
                break;
            case 15360:
                itemDef.copy(lookup(1038));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Turqouise partyhat";
                itemDef.description = "A nice hat from a cracker.";
                itemDef.original_model_colors = new int[] { 25131 };
                itemDef.modified_model_colors = new int[] { 926 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15526;
                break;
            case 15361:
                itemDef.copy(lookup(1050));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Neon green santa hat";
                itemDef.description = "A santa's hat.";
                itemDef.original_model_colors = new int[] { 23421, 926 };
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15527;
                break;
            case 15362:
                itemDef.copy(lookup(1050));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Baby blue santa hat";
                itemDef.description = "A santa's hat.";
                itemDef.original_model_colors = new int[] { 38226, 127 };
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15528;
                break;
            case 15363:
                itemDef.copy(lookup(1050));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Orange blue santa hat";
                itemDef.description = "A santa's hat.";
                itemDef.original_model_colors = new int[] { 4960, 38693 };
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15529;
                break;
            case 15364:
                itemDef.copy(lookup(1050));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Dark yellow santa hat";
                itemDef.description = "A santa's hat.";
                itemDef.original_model_colors = new int[] { 11200, 0 };
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15530;
                break;
            case 15365:
                itemDef.copy(lookup(1050));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Pinky santa hat";
                itemDef.description = "A santa's hat.";
                itemDef.original_model_colors = new int[] { 325, 5136 };
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15531;
                break;
            case 15366:
                itemDef.copy(lookup(1050));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Yellow santa hat";
                itemDef.description = "A santa's hat.";
                itemDef.original_model_colors = new int[] { 11200, 95 };
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15532;
                break;
            case 15367:
                itemDef.copy(lookup(1050));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Dwarf santa hat";
                itemDef.description = "A santa's hat.";
                itemDef.original_model_colors = new int[] { 38693, 86933 };
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15533;
                break;
            case 15368:
                itemDef.copy(lookup(1050));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Winter santa hat";
                itemDef.description = "A santa's hat.";
                itemDef.original_model_colors = new int[] { 17500, 6067 };
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15534;
                break;
            case 15369:
                itemDef.copy(lookup(1050));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Summer santa hat";
                itemDef.description = "A santa's hat.";
                itemDef.original_model_colors = new int[] { 947, 6067 };//947
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15535;
                break;
            case 15370:
                itemDef.copy(lookup(4151));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.actions[2] = "Check";
                itemDef.name = "Barrows whip";
                itemDef.description = "The memorial weapon named after the six Barrows brothers were entombed after death.";
                itemDef.original_model_colors = new int[] { 12821, 10508 };
                itemDef.modified_model_colors = new int[] { 528, 0 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15371;
                break;
            case 15371:
                itemDef.copy(lookup(4152));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Barrows whip";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 12821, 10508 };
                itemDef.modified_model_colors = new int[] { 528, 0 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15370;
                break;
            case 15372:
                itemDef.copy(lookup(1053));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Midnight h'ween mask";
                itemDef.description = "A h'ween mask from Saturday's night party.";
                itemDef.original_model_colors = new int[] { 120, 0, 0, 8 };
                itemDef.modified_model_colors = new int[] { 926, 0, 127, 10349 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15536;
                break;
            case 15373:
                itemDef.copy(lookup(1050));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Midnight santa hat";
                itemDef.description = "A santa hat from Saturday's night party.";
                itemDef.original_model_colors = new int[] { 127, 0 };
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15537;
                break;
            case 15374:
                itemDef.copy(lookup(11863));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Midnight partyhat";
                itemDef.description = "A partyhat from Saturday night party.";
                itemDef.original_model_colors = new int[] { 0, 127, 0, 127, 0, 127, 127, 0, 127, 0, 0 };
                itemDef.modified_model_colors = new int[] { 6067, 947, 55217, 11185, 17331, 43955, 27571, 38835, 11187, 55196, 55186 };//55196,55186

                //itemDef.original_model_colors = new int[] { 127, 0, 127, 0, 127, 0, 0, 127, 0, 127, 127 };
                //itemDef.modified_model_colors = new int[] { 6067, 947, 55217, 11185, 17331, 43955, 27571, 38835, 11187, 55196, 55186 };//55196,55186
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15538;
                break;
            case 15375:
                itemDef.copy(lookup(1053));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Invader h'ween mask";
                itemDef.description = "Based on the model of the pixelated art of space alien invaders.";
                itemDef.original_model_colors = new int[] { 55196, 115, 23421, 23421 };
                itemDef.modified_model_colors = new int[] { 926, 0, 127, 10349 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15539;
                break;
            /*case 15376:
                itemDef.copy(lookup(1053));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Dracula h'ween mask";
                itemDef.description = "A white halloween mask.";
                itemDef.original_model_colors = new int[] { 120, 0, 0, 933 };
                itemDef.modified_model_colors = new int[] { 926, 0, 127, 10349 };
                break;*/
            case 15376:
                itemDef.copy(lookup(1053));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Dracula h'ween mask";
                itemDef.description = "Skrrrrrrrrr!";
                itemDef.original_model_colors = new int[] { 120, 933, 933, 933 };
                itemDef.modified_model_colors = new int[] { 926, 0, 127, 10349 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15540;
                break;
            case 6570: // make fire cape retexturing work
            case 12773:
            case 10566:
            case 10637:
            case 20445:
                itemDef.originalTexture = new short[]{40};
                itemDef.modifiedTexture = new short[]{40};
                break;
            case 15377:
                itemDef.copy(lookup(6570));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Hydro cape";
                itemDef.description = "A cape of shimmering fountains and water.";
                itemDef.original_model_colors = new int[] { -22052 };
                itemDef.modified_model_colors = new int[] { 924 };
                itemDef.originalTexture = new short[] {24};
                itemDef.modifiedTexture = new short[] {40};
                break;
            case 15378:
                itemDef.copy(lookup(5608));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Golden fox";
                itemDef.description = "How did you get your hands on such a thing?";
                itemDef.original_model_colors = new int[] { 7114, 120, 7097, 120, 7097, 7140, 7097, 7114  };
                itemDef.modified_model_colors = new int[] { 4890, 4647, 2702, 0, 5912, 5010, 3980, 6722 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15542;
                break;
            case 15379:
                itemDef.copy(lookup(4566));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Golden chicken";
                itemDef.description = "Yep. Definitely a golden chicken that lays golden eggs perhaps.";
                itemDef.original_model_colors = new int[] { 24, 7097, 7114 };
                itemDef.modified_model_colors = new int[] { 926, 10714, 5712 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15543;
                break;
            case 15380:
                itemDef.copy(lookup(4084));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Golden sled";
                itemDef.description = "A bright shiny golden sled. I better not fall off it!";
                itemDef.original_model_colors = new int[] { 7114, 7097, 7114 };
                itemDef.modified_model_colors = new int[] { 6594, 5559, 5799 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15544;
                break;
            case 15381: // Red
                itemDef.copy(lookup(1052));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Cape of legends";
                itemDef.description = "The cape worn by members of the Legends Guild, with a decorated color.";
                itemDef.original_model_colors = new int[] { 950, 950 };
                itemDef.modified_model_colors = new int[] { 9327, 123 };
                break;
            case 15382: // Golden
                itemDef.copy(lookup(1052));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Cape of legends";
                itemDef.description = "The cape worn by members of the Legends Guild, with a decorated color.";
                itemDef.original_model_colors = new int[] { 7114, 7114 };
                itemDef.modified_model_colors = new int[] { 9327, 123 };
                break;
            case 15383: // Blue
                itemDef.copy(lookup(1052));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Cape of legends";
                itemDef.description = "The cape worn by members of the Legends Guild, with a decorated color.";
                itemDef.original_model_colors = new int[] { 38693, 38693 };
                itemDef.modified_model_colors = new int[] { 9327, 123 };
                break;
            case 15384: // Green
                itemDef.copy(lookup(1052));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Cape of legends";
                itemDef.description = "The cape worn by members of the Legends Guild, with a decorated color.";
                itemDef.original_model_colors = new int[] { 86933, 86933 };
                itemDef.modified_model_colors = new int[] { 9327, 123 };
                break;
            case 15385: // Pink
                itemDef.copy(lookup(1052));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Cape of legends";
                itemDef.description = "The cape worn by members of the Legends Guild, with a decorated color.";
                itemDef.original_model_colors = new int[] { 51136, 51136 };
                itemDef.modified_model_colors = new int[] { 9327, 123 };
                break;
            case 15386:
                itemDef.copy(lookup(11335));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Gilded dragon full helm";
                itemDef.description = "Protects your head and looks very impressive too when its gold plated.";
                itemDef.original_model_colors = new int[] { 7114, 7097, 7114, 7097, 7114, 7097, 7114, 7097, 7114, 7097 };
                itemDef.modified_model_colors = new int[] { 918, 929, 914, 922, 924, 933, 1808, 1934, 916, 920 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15545;
                break;
            case 15387:
                itemDef.copy(lookup(21892));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Gilded dragon platebody";
                itemDef.description = "Provides excellent protection and looks very impressive too when its gold plated.";
                itemDef.original_model_colors = new int[] { 0, 7114, 7097, 7114, 7114, 520, 902, 43119, 7097 };
                itemDef.modified_model_colors = new int[] { 0, 916, 920, 912, 926, 520, 902, 43119, 922 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15546;
                break;
            case 15388:
                itemDef.copy(lookup(4087));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Gilded dragon platelegs";
                itemDef.description = "Looks pretty heavy and impressive when its gold plated.";
                itemDef.original_model_colors = new int[] { 7097, 7114, 7097 };
                itemDef.modified_model_colors = new int[] { 912, 926, 908 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15547;
                break;
            case 15389:
                itemDef.copy(lookup(3140));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Gilded dragon chainbody";
                itemDef.description = "A series of connected metal rings which are gold plated.";
                itemDef.original_model_colors = new int[] { 7114, 3, 4, 5, 6 };
                itemDef.modified_model_colors = new int[] { 922, 908, 914, 679, 559 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15548;
                break;
            case 15390:
                itemDef.copy(lookup(21895));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Gilded dragon kiteshield";
                itemDef.description = "An ancient and powerful looking Dragon Kiteshield that is gold plated.";
                itemDef.original_model_colors = new int[] { 6024, 268, 933, 933, 7117, 7097 };
                itemDef.modified_model_colors = new int[] { 6024, 268, 528, 37, 924, 918 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15549;
                break;
            case 15391:
                itemDef.copy(lookup(1187));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Gilded dragon sq shield";
                itemDef.description = "An ancient metal and powerful looking Dragon Square shield that is gold plated.";
                itemDef.original_model_colors = new int[] { 950, 7114, 933 };
                itemDef.modified_model_colors = new int[] { 37, 924, 7054 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15550;
                break;
            case 15392:
                itemDef.copy(lookup(4585));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Gilded dragon plateskirt";
                itemDef.description = "This looks pretty heavy and impressive when its gold plated.";
                itemDef.original_model_colors = new int[] { 7114, 7097, 7114, 926 };
                itemDef.modified_model_colors = new int[] { 908, 926, 912, 918 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15551;
                break;
            case 15393:
                itemDef.copy(lookup(1149));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Gilded dragon med helm";
                itemDef.description = "Makes the wearer pretty intimidating and impressive at the same time.";
                itemDef.original_model_colors = new int[] { 7097, 7097 };
                itemDef.modified_model_colors = new int[] { 64408, 61 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15552;
                break;
            case 15394:
                itemDef.copy(lookup(10551));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Hardened fighter torso";
                itemDef.description = "A hardened Penance Fighter torso armour.";
                itemDef.original_model_colors = new int[] { 24, 12 };
                itemDef.modified_model_colors = new int[] { 14395, 14387 };
                break;
            case 15395:
                itemDef.copy(lookup(10551));
                itemDef.actions = new String[5];
                itemDef.name = "Hardened fighter torso (broken)";
                itemDef.description = "A broken hardened Penance Fighter torso armour.";
                itemDef.original_model_colors = new int[] { 12, 16 };
                itemDef.modified_model_colors = new int[] { 14395, 14387 };
                break;
            case 15396:
                itemDef.copy(lookup(10551));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Army fighter torso";
                itemDef.description = "An army graded Penance Fighter torso armour.";
                itemDef.original_model_colors = new int[] { 21675, 21682 };
                itemDef.modified_model_colors = new int[] { 14395, 14387 };
                break;
            case 15397:
                itemDef.copy(lookup(10551));
                itemDef.actions = new String[5];
                itemDef.name = "Army fighter torso (broken)";
                itemDef.description = "A broken army graded Penance Fighter torso armour.";
                itemDef.original_model_colors = new int[] { 21662, 21672 };
                itemDef.modified_model_colors = new int[] { 14395, 14387 };
                break;
            case 15398:
                itemDef.copy(lookup(2637));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Yellow beret";
                itemDef.description = "Parlez-vous francais?";
                itemDef.original_model_colors = new int[] { 11200 };
                itemDef.modified_model_colors = new int[] { 10659 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15553;
                break;
            case 15399:
                itemDef.copy(lookup(2637));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Blue beret";
                itemDef.description = "Parlez-vous francais?";
                itemDef.original_model_colors = new int[] { 38226 };
                itemDef.modified_model_colors = new int[] { 10659 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15554;
                break;
            case 15400:
                itemDef.copy(lookup(2637));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Green beret";
                itemDef.description = "Parlez-vous francais?";
                itemDef.original_model_colors = new int[] { 86933 };
                itemDef.modified_model_colors = new int[] { 10659 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15555;
                break;
            case 15401:
                itemDef.copy(lookup(2637));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Violet beret";
                itemDef.description = "Parlez-vous francais?";
                itemDef.original_model_colors = new int[] { 49863 };
                itemDef.modified_model_colors = new int[] { 10659 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15556;
                break;
            case 15402:
                itemDef.copy(lookup(2637));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Purple beret";
                itemDef.description = "Parlez-vous francais?";
                itemDef.original_model_colors = new int[] { 51136 };
                itemDef.modified_model_colors = new int[] { 10659 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15557;
                break;
            case 15403:
                itemDef.copy(lookup(2637));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Orange beret";
                itemDef.description = "Parlez-vous francais?";
                itemDef.original_model_colors = new int[] { 6073 };
                itemDef.modified_model_colors = new int[] { 10659 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15558;
                break;
            case 15404:
                itemDef.copy(lookup(2637));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Gum beret";
                itemDef.description = "Parlez-vous francais?";
                itemDef.original_model_colors = new int[] { 129570 };
                itemDef.modified_model_colors = new int[] { 10659 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15559;
                break;
            case 15405:
                itemDef.copy(lookup(2637));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Coffee beret";
                itemDef.description = "Parlez-vous francais?";
                itemDef.original_model_colors = new int[] { 266770 };
                itemDef.modified_model_colors = new int[] { 10659 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15560;
                break;
            case 15406: // Blue
                itemDef.copy(lookup(2581));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Robin hood hat";
                itemDef.description = "Endorsed by Robin Hood.";
                itemDef.original_model_colors = new int[] { 38693, 38693, 38693, 120, 120 };
                itemDef.modified_model_colors = new int[] { 15009, 17294, 15252, 922, 4003 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15407;
                break;
            case 15407: // Blue
                itemDef.copy(lookup(2582));
                itemDef.actions = new String[5];
                itemDef.name = "Robin hood hat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 43968, 43968, 43968, 120, 120 };
                itemDef.modified_model_colors = new int[] { 15009, 17294, 15252, 922, 4003 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15406;
                break;
            case 15408: // Yellow
                itemDef.copy(lookup(2581));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Robin hood hat";
                itemDef.description = "Endorsed by Robin Hood.";
                itemDef.original_model_colors = new int[] { 11200, 11200, 11200, 120, 120 };
                itemDef.modified_model_colors = new int[] { 15009, 17294, 15252, 922, 4003 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15409;
                break;
            case 15409: // Yellow
                itemDef.copy(lookup(2582));
                itemDef.actions = new String[5];
                itemDef.name = "Robin hood hat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 11200, 11200, 11200, 120, 120 };
                itemDef.modified_model_colors = new int[] { 15009, 17294, 15252, 922, 4003 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15408;
                break;
            case 15410: // Purple
                itemDef.copy(lookup(2581));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Robin hood hat";
                itemDef.description = "Endorsed by Robin Hood.";
                itemDef.original_model_colors = new int[] { 51136, 51136, 51136, 120, 120 };
                itemDef.modified_model_colors = new int[] { 15009, 17294, 15252, 922, 4003 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15411;
                break;
            case 15411: // Purple
                itemDef.copy(lookup(2582));
                itemDef.actions = new String[5];
                itemDef.name = "Robin hood hat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 51136, 51136, 51136, 120, 120 };
                itemDef.modified_model_colors = new int[] { 15009, 17294, 15252, 922, 4003 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15410;
                break;
            case 15412: // Orange
                itemDef.copy(lookup(2581));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Robin hood hat";
                itemDef.description = "Endorsed by Robin Hood.";
                itemDef.original_model_colors = new int[] { 6073, 6073, 6073, 120, 120 };
                itemDef.modified_model_colors = new int[] { 15009, 17294, 15252, 922, 4003 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15413;
                break;
            case 15413: // Orange
                itemDef.copy(lookup(2582));
                itemDef.actions = new String[5];
                itemDef.name = "Robin hood hat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 6073, 6073, 6073, 120, 120 };
                itemDef.modified_model_colors = new int[] { 15009, 17294, 15252, 922, 4003 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15412;
                break;
            case 15414: // Red
                itemDef.copy(lookup(2581));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Robin hood hat";
                itemDef.description = "Endorsed by Robin Hood.";
                itemDef.original_model_colors = new int[] { 950, 950, 950, 120, 120 };
                itemDef.modified_model_colors = new int[] { 15009, 17294, 15252, 922, 4003 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15415;
                break;
            case 15415: // Red
                itemDef.copy(lookup(2582));
                itemDef.actions = new String[5];
                itemDef.name = "Robin hood hat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 950, 950, 950, 120, 120 };
                itemDef.modified_model_colors = new int[] { 15009, 17294, 15252, 922, 4003 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15414;
                break;
            case 15416: // White
                itemDef.copy(lookup(2581));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Robin hood hat";
                itemDef.description = "Endorsed by Robin Hood.";
                itemDef.original_model_colors = new int[] { 125, 125, 125, 5, 5 };
                itemDef.modified_model_colors = new int[] { 15009, 17294, 15252, 922, 4003 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15417;
                break;
            case 15417: // White
                itemDef.copy(lookup(2582));
                itemDef.actions = new String[5];
                itemDef.name = "Robin hood hat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 125, 125, 125, 5, 5 };
                itemDef.modified_model_colors = new int[] { 15009, 17294, 15252, 922, 4003 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15416;
                break;
            case 15418: // Blue
                itemDef.copy(lookup(2577));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Ranger boots";
                itemDef.description = "Lightweight boots ideal for rangers.";
                itemDef.original_model_colors = new int[] { 38693, 120 };
                itemDef.modified_model_colors = new int[] { 15252, 17294 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15419;
                break;
            case 15419: // Blue
                itemDef.copy(lookup(2578));
                itemDef.actions = new String[5];
                itemDef.name = "Ranger boots";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 38693, 120 };
                itemDef.modified_model_colors = new int[] { 15252, 17294 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15418;
                break;
            case 15420: // Yellow
                itemDef.copy(lookup(2577));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Ranger boots";
                itemDef.description = "Lightweight boots ideal for rangers.";
                itemDef.original_model_colors = new int[] { 11200, 120 };
                itemDef.modified_model_colors = new int[] { 15252, 17294 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15421;
                break;
            case 15421: // Yellow
                itemDef.copy(lookup(2578));
                itemDef.actions = new String[5];
                itemDef.name = "Ranger boots";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 11200, 120 };
                itemDef.modified_model_colors = new int[] { 15252, 17294 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15420;
                break;
            case 15422: // Purple
                itemDef.copy(lookup(2577));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Ranger boots";
                itemDef.description = "Lightweight boots ideal for rangers.";
                itemDef.original_model_colors = new int[] { 51136, 8 };
                itemDef.modified_model_colors = new int[] { 15252, 17294 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15423;
                break;
            case 15423: // Purple
                itemDef.copy(lookup(2578));
                itemDef.actions = new String[5];
                itemDef.name = "Ranger boots";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 51136, 8 };
                itemDef.modified_model_colors = new int[] { 15252, 17294 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15422;
                break;
            case 15424: // Orange
                itemDef.copy(lookup(2577));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Ranger boots";
                itemDef.description = "Lightweight boots ideal for rangers.";
                itemDef.original_model_colors = new int[] { 6073, 8 };
                itemDef.modified_model_colors = new int[] { 15252, 17294 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15425;
                break;
            case 15425: // Orange
                itemDef.copy(lookup(2578));
                itemDef.actions = new String[5];
                itemDef.name = "Ranger boots";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 6073, 8 };
                itemDef.modified_model_colors = new int[] { 15252, 17294 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15424;
                break;
            case 15426: // Red
                itemDef.copy(lookup(2577));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Ranger boots";
                itemDef.description = "Lightweight boots ideal for rangers.";
                itemDef.original_model_colors = new int[] { 950, 120 };
                itemDef.modified_model_colors = new int[] { 15252, 17294 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15427;
                break;
            case 15427: // Red
                itemDef.copy(lookup(2578));
                itemDef.actions = new String[5];
                itemDef.name = "Ranger boots";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 950, 120 };
                itemDef.modified_model_colors = new int[] { 15252, 17294 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15426;
                break;
            case 15428: // White
                itemDef.copy(lookup(2577));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Ranger boots";
                itemDef.description = "Lightweight boots ideal for rangers.";
                itemDef.original_model_colors = new int[] { 120, 8 };
                itemDef.modified_model_colors = new int[] { 15252, 17294 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15429;
                break;
            case 15429: // White
                itemDef.copy(lookup(2578));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Ranger boots";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 120, 8 };
                itemDef.modified_model_colors = new int[] { 15252, 17294 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15428;
                break;
            case 15430:
                itemDef.copy(lookup(4152));
                itemDef.actions = new String[5];
                itemDef.name = "Dragon whip";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 950 };
                itemDef.modified_model_colors = new int[] { 528 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15155;
                break;
            case 15431:
                itemDef.copy(lookup(5608));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Midnight fox";
                itemDef.description = "Awwwooooooooh!";
                itemDef.original_model_colors = new int[] { 930, 21675, 5622, 910, 950, 4, 12821, 12800 };
                itemDef.modified_model_colors = new int[] { 0, 4890, 2702, 4647, 6722, 3980, 5010, 5912 };
                break;
            case 15432:
                itemDef.copy(lookup(5607));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Golden backpack";
                itemDef.description = "Golden.";
                itemDef.original_model_colors = new int[] { 7097, 120, 7114, 7097, 7114};
                itemDef.modified_model_colors = new int[] { 6674, 18, 6430, 6554, 6550 };
                break;
            case 15433:
                itemDef.copy(lookup(861));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Dragon shortbow";
                itemDef.description = "Crafted with perfection from a dragon's skin which makes it light and very effective..";
                itemDef.original_model_colors = new int[] { 955 };
                itemDef.modified_model_colors = new int[] { 6674 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15434;
                break;
            case 15434:
                itemDef.copy(lookup(862));
                itemDef.actions = new String[5];
                itemDef.name = "Dragon shortbow";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 955 };
                itemDef.modified_model_colors = new int[] { 6674 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15433;
                break;
            case 15435:
                itemDef.copy(lookup(12399));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Partyhat and specs";
                itemDef.description = "This is what robbers wore after robbing the vault.";
                itemDef.original_model_colors = new int[] { 7114, 7097 };
                itemDef.modified_model_colors = new int[] { 43963, 33 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15436;
                break;
            case 15436:
                itemDef.copy(lookup(12400));
                itemDef.actions = new String[5];
                itemDef.name = "Partyhat and specs";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 7114, 7097 };
                itemDef.modified_model_colors = new int[] { 43963, 33 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15435;
                break;
            case 15437:
                itemDef.copy(lookup(11863));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Birthday partyhat";
                itemDef.description = "Happy birthday!";
                itemDef.original_model_colors = new int[] { 6073, 11187, 6073, 11187, 6073, 11187, 11187, 6073, 11187, 6073, 6073 };
                itemDef.modified_model_colors = new int[] { 6067, 947, 55217, 11185, 17331, 43955, 27571, 38835, 11187, 55196, 55186 };
                //itemDef.original_model_colors = new int[] { 6073, 11200, 6073, 11200, 6073, 11200, 11200, 6073, 11200, 6073, 6073 };
                //itemDef.modified_model_colors = new int[] { 6067, 947, 55217, 11185, 17331, 43955, 27571, 38835, 11187, 55196, 55186 };//55196,55186
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15438;
                break;
            case 15438:
                itemDef.copy(lookup(1051));
                itemDef.actions = new String[5];
                itemDef.name = "Birthday partyhat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 6073, 11187, 6073, 11187, 6073, 11187, 11187, 6073, 11187, 6073, 6073 };
                itemDef.modified_model_colors = new int[] { 6067, 947, 55217, 11185, 17331, 43955, 27571, 38835, 11187, 55196, 55186 };//55196,55186
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15437;
                break;
            case 15439:
                itemDef.copy(lookup(15271));
                itemDef.actions = new String[5];
                itemDef.name = "Colorful max hood (broken)";
                break;
            case 15440: // Red
                itemDef.copy(lookup(8839));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Void knight top (t)";
                itemDef.description = "A void knight top (t).";
                itemDef.original_model_colors = new int[] { 950, 950, 950 };
                itemDef.modified_model_colors = new int[] { 16, 24, 90 };
                break;
            case 15441: // Red
                itemDef.copy(lookup(8840));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Void knight robe (t)";
                itemDef.description = "A void knight robe (t).";
                itemDef.original_model_colors = new int[] { 950, 950, 950 };
                itemDef.modified_model_colors = new int[] { 20, 90, 8 };
                break;
            case 15442: // Red
                itemDef.copy(lookup(11663));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Void mage helm (t)";
                itemDef.description = "A void mage helm (t).";
                itemDef.original_model_colors = new int[] { 950, 950 };
                itemDef.modified_model_colors = new int[] { 70, 24 };
                break;
            case 15443: // Red
                itemDef.copy(lookup(11664));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Void ranger helm (t)";
                itemDef.description = "A void ranger helm (t).";
                itemDef.original_model_colors = new int[] { 950, 950, 950 };
                itemDef.modified_model_colors = new int[] { 78, 74, 90 };
                break;
            case 15444: // Red
                itemDef.copy(lookup(11665));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Void melee helm (t)";
                itemDef.description = "A void melee helm (t).";
                itemDef.original_model_colors = new int[] { 950, 950, 950 };
                itemDef.modified_model_colors = new int[] { 24, 90, 53 };
                break;
            case 15445: // Red
                itemDef.copy(lookup(13072));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Elite void top (t)";
                itemDef.description = "An elite void top (t).";
                itemDef.original_model_colors = new int[] { 950, 950, 950, 950, 950, 950 };
                itemDef.modified_model_colors = new int[] { 24, 7442, 7446, 7322, 7326, 16 };
                break;
            case 15446: // Red
                itemDef.copy(lookup(13073));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Elite void robe (t)";
                itemDef.description = "An elite void robe (t).";
                itemDef.original_model_colors = new int[] { 950, 950, 950, 950, 950, 950, 950, 950, 950, 950, 950, 950, 950 };
                itemDef.modified_model_colors = new int[] { 115, 111, 119, 66, 12, 127, 82, 21568, 21576, 21568, 21559, 21572, 21563 };
                break;
            case 15447: // Green
                itemDef.copy(lookup(8839));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Void knight top (t)";
                itemDef.description = "A void knight top (t).";
                itemDef.original_model_colors = new int[] { 86933, 86933, 86933 };
                itemDef.modified_model_colors = new int[] { 16, 24, 90 };
                break;
            case 15448: // Green
                itemDef.copy(lookup(8840));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Void knight robe (t)";
                itemDef.description = "A void knight robe (t).";
                itemDef.original_model_colors = new int[] { 86933, 86933, 86933 };
                itemDef.modified_model_colors = new int[] { 20, 90, 8 };
                break;
            case 15449: // Green
                itemDef.copy(lookup(11663));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Void mage helm (t)";
                itemDef.description = "A void mage helm (t).";
                itemDef.original_model_colors = new int[] { 86933, 86933 };
                itemDef.modified_model_colors = new int[] { 70, 24 };
                break;
            case 15450: // Green
                itemDef.copy(lookup(11664));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Void ranger helm (t)";
                itemDef.description = "A void ranger helm (t).";
                itemDef.original_model_colors = new int[] { 86933, 86933, 86933 };
                itemDef.modified_model_colors = new int[] { 78, 74, 90 };
                break;
            case 15451: // Green
                itemDef.copy(lookup(11665));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Void melee helm (t)";
                itemDef.description = "A void melee helm (t).";
                itemDef.original_model_colors = new int[] { 86933, 86933, 86933 };
                itemDef.modified_model_colors = new int[] { 24, 90, 53 };
                break;
            case 15452: // Green
                itemDef.copy(lookup(13072));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Elite void top (t)";
                itemDef.description = "An elite void top (t).";
                itemDef.original_model_colors = new int[] { 86933, 86933, 86933, 86933, 86933, 86933 };
                itemDef.modified_model_colors = new int[] { 24, 7442, 7446, 7322, 7326, 16 };
                break;
            case 15453: // Green
                itemDef.copy(lookup(13073));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Elite void robe (t)";
                itemDef.description = "An elite void robe (t).";
                itemDef.original_model_colors = new int[] { 86933, 86933, 86933, 86933, 86933, 86933, 86933, 86933, 86933, 86933, 86933, 86933, 86933 };
                itemDef.modified_model_colors = new int[] { 115, 111, 119, 66, 12, 127, 82, 21568, 21576, 21568, 21559, 21572, 21563 };
                break;
            case 15454: // Pink
                itemDef.copy(lookup(8839));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Void knight top (t)";
                itemDef.description = "A void knight top (t).";
                itemDef.original_model_colors = new int[] { 51136, 51136, 51136 };
                itemDef.modified_model_colors = new int[] { 16, 24, 90 };
                break;
            case 15455: // Pink
                itemDef.copy(lookup(8840));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Void knight robe (t)";
                itemDef.description = "A void knight robe (t).";
                itemDef.original_model_colors = new int[] { 51136, 51136, 51136 };
                itemDef.modified_model_colors = new int[] { 20, 90, 8 };
                break;
            case 15456: // Pink
                itemDef.copy(lookup(11663));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Void mage helm (t)";
                itemDef.description = "A void mage helm (t).";
                itemDef.original_model_colors = new int[] { 51136, 51136 };
                itemDef.modified_model_colors = new int[] { 70, 24 };
                break;
            case 15457: // Pink
                itemDef.copy(lookup(11664));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Void ranger helm (t)";
                itemDef.description = "A void ranger helm (t).";
                itemDef.original_model_colors = new int[] { 51136, 51136, 51136 };
                itemDef.modified_model_colors = new int[] { 78, 74, 90 };
                break;
            case 15458: // Pink
                itemDef.copy(lookup(11665));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Void melee helm (t)";
                itemDef.description = "A void melee helm (t).";
                itemDef.original_model_colors = new int[] { 51136, 51136, 51136 };
                itemDef.modified_model_colors = new int[] { 24, 90, 53 };
                break;
            case 15459: // Pink
                itemDef.copy(lookup(13072));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Elite void top (t)";
                itemDef.description = "An elite void top (t).";
                itemDef.original_model_colors = new int[] { 51136, 51136, 51136, 51136, 51136, 51136 };
                itemDef.modified_model_colors = new int[] { 24, 7442, 7446, 7322, 7326, 16 };
                break;
            case 15460: // Pink
                itemDef.copy(lookup(13073));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Elite void robe (t)";
                itemDef.description = "An elite void robe (t).";
                itemDef.original_model_colors = new int[] { 51136, 51136, 51136, 51136, 51136, 51136, 51136, 51136, 51136, 51136, 51136, 51136, 51136 };
                itemDef.modified_model_colors = new int[] { 115, 111, 119, 66, 12, 127, 82, 21568, 21576, 21568, 21559, 21572, 21563 };
                break;
                // Noted versions work below
            case 15461:
                itemDef.copy(lookup(2632));
                itemDef.actions = new String[5];
                itemDef.name = "Highwayman mask (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { -27310 };
                itemDef.modified_model_colors = new int[] { 43146 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15297;
                break;
            case 15462:
                itemDef.copy(lookup(20096));
                itemDef.actions = new String[5];
                itemDef.name = "Sky blue ankou mask";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 38226 };
                itemDef.modified_model_colors = new int[] { 960 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15301;
                break;
            case 15463:
                itemDef.copy(lookup(20099));
                itemDef.actions = new String[5];
                itemDef.name = "Sky blue ankou top";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 38226 };
                itemDef.modified_model_colors = new int[] { 960 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15302;
                break;
            case 15464:
                itemDef.copy(lookup(20105));
                itemDef.actions = new String[5];
                itemDef.name = "Sky blue ankou leggings";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 38226 };
                itemDef.modified_model_colors = new int[] { 960 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15303;
                break;
            case 15465:
                itemDef.copy(lookup(20102));
                itemDef.actions = new String[5];
                itemDef.name = "Sky blue ankou gloves";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 38226 };
                itemDef.modified_model_colors = new int[] { 960 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15304;
                break;
            case 15466:
                itemDef.copy(lookup(20108));
                itemDef.actions = new String[5];
                itemDef.name = "Sky blue ankou socks";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 38226 };
                itemDef.modified_model_colors = new int[] { 960 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15305;
                break;
            case 15467: // Blue
                itemDef.copy(lookup(1306));
                itemDef.actions = new String[5];
                itemDef.name = "Dragon longsword (or)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 38693 };
                itemDef.modified_model_colors = new int[] { 922 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15306;
                break;
            case 15468: // Orange
                itemDef.copy(lookup(1306));
                itemDef.actions = new String[5];
                itemDef.name = "Dragon longsword (or)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 120, 6073 };
                itemDef.modified_model_colors = new int[] { 37, 922 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15307;
                break;
            case 15469: // Green
                itemDef.copy(lookup(1306));
                itemDef.actions = new String[5];
                itemDef.name = "Dragon longsword (or)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 37, 86933 };
                itemDef.modified_model_colors = new int[] { 37, 922 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15308;
                break;
            case 15470: // Pink
                itemDef.copy(lookup(1306));
                itemDef.actions = new String[5];
                itemDef.name = "Dragon longsword (or)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 120, 51136 };
                itemDef.modified_model_colors = new int[] { 37, 922 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15309;
                break;
            // Red
            case 15471:
                itemDef.copy(lookup(2628));
                itemDef.actions = new String[5];
                itemDef.name = "Rune full helm (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 36133, 950 };
                itemDef.modified_model_colors = new int[] { 61, 926 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15310;
                break;
            case 15472:
                itemDef.copy(lookup(2624));
                itemDef.actions = new String[5];
                itemDef.name = "Rune platebody (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { -29403, 950, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 24 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15311;

                break;
            case 15473:
                itemDef.copy(lookup(2626));
                itemDef.actions = new String[5];
                itemDef.name = "Rune platelegs (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15312;
                break;
            case 15474:
                itemDef.copy(lookup(2630));
                itemDef.actions = new String[5];
                itemDef.name = "Rune kiteshield (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 950 };
                itemDef.modified_model_colors = new int[] { 61, 57, 7054 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15313;
                break;
            case 15475:
                itemDef.copy(lookup(3477));
                itemDef.actions = new String[5];
                itemDef.name = "Rune plateskirt (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 950, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57, 25238 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15314;
                break;

            case 15476:
                itemDef.copy(lookup(2588));
                itemDef.actions = new String[5];
                itemDef.name = "Black full helm (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 950 };
                itemDef.modified_model_colors = new int[] { 61, 926 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15315;
                break;
            case 15477:
                itemDef.copy(lookup(2584));
                itemDef.actions = new String[5];
                itemDef.name = "Black platebody (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 950, 8, 0 };
                itemDef.modified_model_colors = new int[] { 24, 61, 41 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15316;
                break;
            case 15478:
                itemDef.copy(lookup(2586));
                itemDef.actions = new String[5];
                itemDef.name = "Black platelegs (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 0, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15317;
                break;
            case 15479:
                itemDef.copy(lookup(2590));
                itemDef.actions = new String[5];
                itemDef.name = "Black kiteshield (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 0, 950 };
                itemDef.modified_model_colors = new int[] { 61, 57, 7054 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15318;
                break;
            case 15480:
                itemDef.copy(lookup(3473));
                itemDef.actions = new String[5];
                itemDef.name = "Black plateskirt (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 0, 950, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57, 25238 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15319;
                break;
            // Blue
            case 15481:
                itemDef.copy(lookup(2588));
                itemDef.actions = new String[5];
                itemDef.name = "Black full helm (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 950 };
                itemDef.modified_model_colors = new int[] { 61, 926 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15320;
                break;
            case 15482:
                itemDef.copy(lookup(2584));
                itemDef.actions = new String[5];
                itemDef.name = "Black platebody (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 950, 8, 0 };
                itemDef.modified_model_colors = new int[] { 24, 61, 41 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15321;
                break;
            case 15483:
                itemDef.copy(lookup(2586));
                itemDef.actions = new String[5];
                itemDef.name = "Black platelegs (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 0, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15322;
                break;
            case 15484:
                itemDef.copy(lookup(2590));
                itemDef.actions = new String[5];
                itemDef.name = "Black kiteshield (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 0, 950 };
                itemDef.modified_model_colors = new int[] { 61, 57, 7054 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15323;
                break;
            case 15485:
                itemDef.copy(lookup(3473));
                itemDef.actions = new String[5];
                itemDef.name = "Black plateskirt (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 0, 950, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57, 25238 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15324;
                break;

            // Green
            case 15486:
                itemDef.copy(lookup(2588));
                itemDef.actions = new String[5];
                itemDef.name = "Black full helm (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 950 };
                itemDef.modified_model_colors = new int[] { 61, 926 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15325;
                break;
            case 15487:
                itemDef.copy(lookup(2584));
                itemDef.actions = new String[5];
                itemDef.name = "Black platebody (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 950, 8, 0 };
                itemDef.modified_model_colors = new int[] { 24, 61, 41 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15326;
                break;
            case 15488:
                itemDef.copy(lookup(2586));
                itemDef.actions = new String[5];
                itemDef.name = "Black platelegs (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 0, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15327;
                break;
            case 15489:
                itemDef.copy(lookup(2590));
                itemDef.actions = new String[5];
                itemDef.name = "Black kiteshield (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 0, 950 };
                itemDef.modified_model_colors = new int[] { 61, 57, 7054 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15328;
                break;
            case 15490:
                itemDef.copy(lookup(3473));
                itemDef.actions = new String[5];
                itemDef.name = "Black plateskirt (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 0, 950, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57, 25238 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15329;
                break;

            // Blue
            case 15491:
                itemDef.copy(lookup(2628));
                itemDef.actions = new String[5];
                itemDef.name = "Rune full helm (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 36133, 950 };
                itemDef.modified_model_colors = new int[] { 61, 926 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15330;
                break;
            case 15492:
                itemDef.copy(lookup(2624));
                itemDef.actions = new String[5];
                itemDef.name = "Rune platebody (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { -29403, 950, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 24 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15331;

                break;
            case 15493:
                itemDef.copy(lookup(2626));
                itemDef.actions = new String[5];
                itemDef.name = "Rune platelegs (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15332;
                break;
            case 15494:
                itemDef.copy(lookup(2630));
                itemDef.actions = new String[5];
                itemDef.name = "Rune kiteshield (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 950 };
                itemDef.modified_model_colors = new int[] { 61, 57, 7054 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15333;
                break;
            case 15495:
                itemDef.copy(lookup(3477));
                itemDef.actions = new String[5];
                itemDef.name = "Rune plateskirt (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 950, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57, 25238 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15334;
                break;
            // Green
            case 15496:
                itemDef.copy(lookup(2628));
                itemDef.actions = new String[5];
                itemDef.name = "Rune full helm (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 36133, 950 };
                itemDef.modified_model_colors = new int[] { 61, 926 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15335;
                break;
            case 15497:
                itemDef.copy(lookup(2624));
                itemDef.actions = new String[5];
                itemDef.name = "Rune platebody (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { -29403, 950, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 24 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15336;

                break;
            case 15498:
                itemDef.copy(lookup(2626));
                itemDef.actions = new String[5];
                itemDef.name = "Rune platelegs (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15337;
                break;
            case 15499:
                itemDef.copy(lookup(2630));
                itemDef.actions = new String[5];
                itemDef.name = "Rune kiteshield (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 950 };
                itemDef.modified_model_colors = new int[] { 61, 57, 7054 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15338;
                break;
            case 15500:
                itemDef.copy(lookup(3477));
                itemDef.actions = new String[5];
                itemDef.name = "Rune plateskirt (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 950, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57, 25238 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15339;
                break;

            // Pink
            case 15501:
                itemDef.copy(lookup(2628));
                itemDef.actions = new String[5];
                itemDef.name = "Rune full helm (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 36133, 950 };
                itemDef.modified_model_colors = new int[] { 61, 926 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15340;
                break;
            case 15502:
                itemDef.copy(lookup(2624));
                itemDef.actions = new String[5];
                itemDef.name = "Rune platebody (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { -29403, 950, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 24 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15341;

                break;
            case 15503:
                itemDef.copy(lookup(2626));
                itemDef.actions = new String[5];
                itemDef.name = "Rune platelegs (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15342;
                break;
            case 15504:
                itemDef.copy(lookup(2630));
                itemDef.actions = new String[5];
                itemDef.name = "Rune kiteshield (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 950 };
                itemDef.modified_model_colors = new int[] { 61, 57, 7054 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15343;
                break;
            case 15505:
                itemDef.copy(lookup(3477));
                itemDef.actions = new String[5];
                itemDef.name = "Rune plateskirt (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 36133, 36133, 950, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57, 25238 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15344;
                break;
            // Pink
            case 15506:
                itemDef.copy(lookup(2588));
                itemDef.actions = new String[5];
                itemDef.name = "Black full helm (p)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 950 };
                itemDef.modified_model_colors = new int[] { 61, 926 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15180;
                break;
            case 15507:
                itemDef.copy(lookup(2584));
                itemDef.actions = new String[5];
                itemDef.name = "Black platebody (p)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 950, 8, 0 };
                itemDef.modified_model_colors = new int[] { 24, 61, 41 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15178;
                break;
            case 15508:
                itemDef.copy(lookup(2586));
                itemDef.actions = new String[5];
                itemDef.name = "Black platelegs (p)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 0, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15179;
                break;
            case 15509:
                itemDef.copy(lookup(2590));
                itemDef.actions = new String[5];
                itemDef.name = "Black kiteshield (p)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 0, 950 };
                itemDef.modified_model_colors = new int[] { 61, 57, 7054 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15159;
                break;
            case 15510:
                itemDef.copy(lookup(3473));
                itemDef.actions = new String[5];
                itemDef.name = "Black plateskirt (p)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 0, 950, 950 };
                itemDef.modified_model_colors = new int[] { 61, 41, 57, 25238 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15181;
                break;
            // Pink
            case 15511:
                itemDef.copy(lookup(4588));
                itemDef.actions = new String[5];
                itemDef.name = "Dragon scimitar (or)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 51136, 51136 };
                itemDef.modified_model_colors = new int[] { 933, 935 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15345;
                break;

            case 15512:
                itemDef.copy(lookup(4588));
                itemDef.actions = new String[5];
                itemDef.name = "Dragon scimitar (p)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 933, 13221 };
                itemDef.modified_model_colors = new int[] { 933, 935 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15346;
                break;
            case 15513:
                itemDef.copy(lookup(4588));
                itemDef.actions = new String[5];
                itemDef.name = "Dragon scimitar (p+)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 933, 13221 };
                itemDef.modified_model_colors = new int[] { 933, 935 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15347;
                break;
            case 15514:
                itemDef.copy(lookup(4588));
                itemDef.actions = new String[5];
                itemDef.name = "Dragon scimitar (p++)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 933, 13221 };
                itemDef.modified_model_colors = new int[] { 933, 935 };
                itemDef.noted_item_id = 79;
                itemDef.unnoted_item_id = 15348;
                break;

            // Green
            case 15515:
                itemDef.copy(lookup(4588));
                itemDef.actions = new String[5];
                itemDef.name = "Dragon scimitar (or)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 86933, 86933 };
                itemDef.modified_model_colors = new int[] { 933, 935 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15349;
                break;
            // Blue
            case 15516:
                itemDef.copy(lookup(4588));
                itemDef.actions = new String[5];
                itemDef.name = "Dragon scimitar (or)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 43968, 43968 };
                itemDef.modified_model_colors = new int[] { 933, 935 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15350;
                break;
            // White
            case 15517:
                itemDef.copy(lookup(4588));
                itemDef.actions = new String[5];
                itemDef.name = "Dragon scimitar (or)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 125, 125 };
                itemDef.modified_model_colors = new int[] { 933, 935 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15351;
                break;
            case 15518:
                itemDef.copy(lookup(4152));
                itemDef.actions = new String[5];
                itemDef.name = "Bronze whip";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 5652 };
                itemDef.modified_model_colors = new int[] { 528 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15352;
                break;
            case 15519:
                itemDef.copy(lookup(4152));
                itemDef.actions = new String[5];
                itemDef.name = "Iron whip";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 33 };
                itemDef.modified_model_colors = new int[] { 528 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15353;
                break;
            case 15520:
                itemDef.copy(lookup(4152));
                itemDef.actions = new String[5];
                itemDef.name = "Steel whip";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 75 };
                itemDef.modified_model_colors = new int[] { 528 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15354;
                break;
            case 15521:
                itemDef.copy(lookup(4152));
                itemDef.actions = new String[5];
                itemDef.name = "Mithril whip";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { -6449257 };
                itemDef.modified_model_colors = new int[] { 528 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15355;
                break;
            case 15522:
                itemDef.copy(lookup(4152));
                itemDef.actions = new String[5];
                itemDef.name = "Adamant whip";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 21662 };
                itemDef.modified_model_colors = new int[] { 528 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15356;
                break;
            case 15523:
                itemDef.copy(lookup(4152));
                itemDef.actions = new String[5];
                itemDef.name = "Rune whip";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 36133 };
                itemDef.modified_model_colors = new int[] { 528 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15357;
                break;

            case 15524:
                itemDef.copy(lookup(1039));
                itemDef.actions = new String[5];
                itemDef.name = "Peach partyhat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 3610 };
                itemDef.modified_model_colors = new int[] { 926 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15358;
                break;
            case 15525:
                itemDef.copy(lookup(1039));
                itemDef.actions = new String[5];
                itemDef.name = "Cream partyhat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 8500 };
                itemDef.modified_model_colors = new int[] { 926 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15359;
                break;
            case 15526:
                itemDef.copy(lookup(1039));
                itemDef.actions = new String[5];
                itemDef.name = "Turqouise partyhat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 25131 };
                itemDef.modified_model_colors = new int[] { 926 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15360;
                break;
            case 15527:
                itemDef.copy(lookup(1051));
                itemDef.actions = new String[5];
                itemDef.name = "Neon green santa hat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 23421, 926 };
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15361;
                break;
            case 15528:
                itemDef.copy(lookup(1051));
                itemDef.actions = new String[5];
                itemDef.name = "Baby blue santa hat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 38226, 127 };
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15362;
                break;
            case 15529:
                itemDef.copy(lookup(1051));
                itemDef.actions = new String[5];
                itemDef.name = "Orange blue santa hat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 4960, 38693 };
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15363;
                break;
            case 15530:
                itemDef.copy(lookup(1051));
                itemDef.actions = new String[5];
                itemDef.name = "Dark yellow santa hat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 11200, 0 };
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15364;
                break;
            case 15531:
                itemDef.copy(lookup(1051));
                itemDef.actions = new String[5];
                itemDef.name = "Pinky santa hat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 325, 5136 };
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15365;
                break;
            case 15532:
                itemDef.copy(lookup(1051));
                itemDef.actions = new String[5];
                itemDef.name = "Yellow santa hat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 11200, 95 };
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15366;
                break;
            case 15533:
                itemDef.copy(lookup(1051));
                itemDef.actions = new String[5];
                itemDef.name = "Dwarf santa hat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 38693, 86933 };
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15367;
                break;
            case 15534:
                itemDef.copy(lookup(1051));
                itemDef.actions = new String[5];
                itemDef.name = "Winter santa hat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 17500, 6067 };
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15368;
                break;
            case 15535:
                itemDef.copy(lookup(1051));
                itemDef.actions = new String[5];
                itemDef.name = "Summer santa hat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 947, 6067 };//947
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15369;
                break;
            case 15536:
                itemDef.copy(lookup(1054));
                itemDef.actions = new String[5];
                itemDef.name = "Midnight h'ween mask";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 120, 0, 0, 8 };
                itemDef.modified_model_colors = new int[] { 926, 0, 127, 10349 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15372;
                break;
            case 15537:
                itemDef.copy(lookup(1051));
                itemDef.actions = new String[5];
                itemDef.name = "Midnight santa hat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 127, 0 };
                itemDef.modified_model_colors = new int[] { 933, 10351 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15373;
                break;
            case 15538:
                itemDef.copy(lookup(1054));
                itemDef.actions = new String[5];
                itemDef.name = "Midnight partyhat";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 127, 0, 127, 0, 127, 127, 0, 127, 0, 0 };
                itemDef.modified_model_colors = new int[] { 6067, 947, 55217, 11185, 17331, 43955, 27571, 38835, 11187, 55196, 55186 };//55196,55186
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15374;
                break;
            case 15539:
                itemDef.copy(lookup(1054));
                itemDef.actions = new String[5];
                itemDef.name = "Invader h'ween mask";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 55196, 115, 23421, 23421 };
                itemDef.modified_model_colors = new int[] { 926, 0, 127, 10349 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15375;
                break;
            case 15540:
                itemDef.copy(lookup(1054));
                itemDef.actions = new String[5];
                itemDef.name = "Dracula h'ween mask";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 120, 933, 933, 933 };
                itemDef.modified_model_colors = new int[] { 926, 0, 127, 10349 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15376;
                break;
            case 15541:
                itemDef.copy(lookup(20445));
                itemDef.actions = new String[5];
                itemDef.name = "Hydro cape (broken)";
                itemDef.description = "A broken cape of shimmering fountains and water.";
                itemDef.original_model_colors = new int[] { -22052 };
                itemDef.modified_model_colors = new int[] { 924 };
                itemDef.originalTexture = new short[] {24};
                itemDef.modifiedTexture = new short[] {40};
                break;
            /*case 15542:
                itemDef.copy(lookup(5608));
                itemDef.actions = new String[5];
                itemDef.name = "Golden fox";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 7114, 120, 7097, 120, 7097, 7140, 7097, 7114  };
                itemDef.modified_model_colors = new int[] { 4890, 4647, 2702, 0, 5912, 5010, 3980, 6722 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15542;
                break;
            case 15543:
                itemDef.copy(lookup(4566));
                itemDef.actions = new String[5];
                itemDef.name = "Gilded rubber chicken";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 24, 7097, 7114 };
                itemDef.modified_model_colors = new int[] { 926, 10714, 5712 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15543;
                break;
            case 15544:
                itemDef.copy(lookup(4084));
                itemDef.actions = new String[5];
                itemDef.name = "Golden sled";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 7114, 7097, 7114 };
                itemDef.modified_model_colors = new int[] { 6594, 5559, 5799 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15544;
                break;*/
            case 15545:
                itemDef.copy(lookup(11336));
                itemDef.actions = new String[5];
                itemDef.name = "Gilded dragon full helm";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 7114, 7097, 7114, 7097, 7114, 7097, 7114, 7097 };
                itemDef.modified_model_colors = new int[] { 918, 929, 914, 922, 924, 933, 1808, 1934 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15386;
                break;
            case 15546:
                itemDef.copy(lookup(21893));
                itemDef.actions = new String[5];
                itemDef.name = "Gilded dragon platebody";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 7114, 7097, 7114, 7114, 520, 902, 43119, 7097 };
                itemDef.modified_model_colors = new int[] { 0, 916, 920, 912, 926, 520, 902, 43119, 922 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15387;
                break;
            case 15547:
                itemDef.copy(lookup(4088));
                itemDef.actions = new String[5];
                itemDef.name = "Gilded dragon platelegs";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 7097, 7114, 7097 };
                itemDef.modified_model_colors = new int[] { 912, 926, 908 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15388;
                break;
            case 15548:
                itemDef.copy(lookup(3141));
                itemDef.actions = new String[5];
                itemDef.name = "Gilded dragon chainbody";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 7114, 3, 4, 679, 559 };
                itemDef.modified_model_colors = new int[] { 922, 908, 914, 679, 559 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15389;
                break;
            case 15549:
                itemDef.copy(lookup(21896));
                itemDef.actions = new String[5];
                itemDef.name = "Gilded dragon kiteshield";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 6024, 268, 933, 933, 7117, 7097 };
                itemDef.modified_model_colors = new int[] { 6024, 268, 528, 37, 924, 918 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15390;
                break;
            case 15550:
                itemDef.copy(lookup(1188));
                itemDef.actions = new String[5];
                itemDef.name = "Gilded dragon sq shield";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 950, 7114, 933 };
                itemDef.modified_model_colors = new int[] { 37, 924, 7054 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15391;
                break;
            case 15551:
                itemDef.copy(lookup(4586));
                itemDef.actions = new String[5];
                itemDef.name = "Gilded dragon plateskirt";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 7114, 7097, 7114, 926 };
                itemDef.modified_model_colors = new int[] { 908, 926, 912, 918 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15392;
                break;
            case 15552:
                itemDef.copy(lookup(1150));
                itemDef.actions = new String[5];
                itemDef.name = "Gilded dragon med helm";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 7097 };
                itemDef.modified_model_colors = new int[] { 64408 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15393;
                break;
            case 15553:
                itemDef.copy(lookup(2638));
                itemDef.actions = new String[5];
                itemDef.name = "Yellow beret";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 11200 };
                itemDef.modified_model_colors = new int[] { 10659 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15398;
                break;
            case 15554:
                itemDef.copy(lookup(2638));
                itemDef.actions = new String[5];
                itemDef.name = "Blue beret";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 38226 };
                itemDef.modified_model_colors = new int[] { 10659 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15399;
                break;
            case 15555:
                itemDef.copy(lookup(2638));
                itemDef.actions = new String[5];
                itemDef.name = "Green beret";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 86933 };
                itemDef.modified_model_colors = new int[] { 10659 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15400;
                break;
            case 15556:
                itemDef.copy(lookup(2638));
                itemDef.actions = new String[5];
                itemDef.name = "Violet beret";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 49863 };
                itemDef.modified_model_colors = new int[] { 10659 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15401;
                break;
            case 15557:
                itemDef.copy(lookup(2638));
                itemDef.actions = new String[5];
                itemDef.name = "Purple beret";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 51136 };
                itemDef.modified_model_colors = new int[] { 10659 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15402;
                break;
            case 15558:
                itemDef.copy(lookup(2638));
                itemDef.actions = new String[5];
                itemDef.name = "Orange beret";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 6073 };
                itemDef.modified_model_colors = new int[] { 10659 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15403;
                break;
            case 15559:
                itemDef.copy(lookup(2638));
                itemDef.actions = new String[5];
                itemDef.name = "Gum beret";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 129570 };
                itemDef.modified_model_colors = new int[] { 10659 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15404;
                break;
            case 15560:
                itemDef.copy(lookup(2638));
                itemDef.actions = new String[5];
                itemDef.name = "Coffee beret";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 266770 };
                itemDef.modified_model_colors = new int[] { 10659 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15405;
                break;
           case 23497:
                itemDef.name = "Hydro coin";
                itemDef.stackable = true;
                break;
            case 15690:
                itemDef.copy(lookup(21298));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Hydro helmet";
                itemDef.description = "Provides excellent protection.";
                itemDef.original_model_colors = new int[] { -29744, -22052, -29744, -22062, -22068, -22072 };
                itemDef.modified_model_colors = new int[] { 941, 28, 0, 12, 20, 24 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15691;
                break;
            case 15691:
                itemDef.copy(lookup(21299));
                itemDef.actions = new String[5];
                itemDef.name = "Hydro helmet";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { -29744, -22052, -29744, -22020, -22010, -22000 };
                itemDef.modified_model_colors = new int[] { 941, 28, 0, 12, 20, 24 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15690;
                break;
            case 15692:
                itemDef.copy(lookup(21301));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Hydro platebody";
                itemDef.description = "Provides excellent protection.";
                itemDef.original_model_colors = new int[] { -29744, -22052, -22052, -22052, -22052, -22052, -22052, -22045, -22042, -22038 };
                itemDef.modified_model_colors = new int[] { 943, 530, 406, 280, 152, 148, 28, 24, 20, 16 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15693;
                break;
            case 15693:
                itemDef.copy(lookup(21302));
                itemDef.actions = new String[5];
                itemDef.name = "Hydro platebody";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { -29744, -22052, -21928, -21802, -21660, -21537, -21530, -21525, -21520, -21515 };
                itemDef.modified_model_colors = new int[] { 943, 530, 406, 280, 152, 148, 28, 24, 20, 16 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15692;
                break;

            case 15694:
                itemDef.copy(lookup(21304));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Hydro platelegs";
                itemDef.description = "Provides excellent protection.";
                itemDef.original_model_colors = new int[] { -29744, -22052, -22052, -22052, -22040, -22035 };
                itemDef.modified_model_colors = new int[] { 943, 530, 152, 28, 24, 20 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15695;
                break;
            case 15695:
                itemDef.copy(lookup(21305));
                itemDef.actions = new String[5];
                itemDef.name = "Hydro platelegs";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { -29744, -22052, -21660, -21530, -21525, -21520 };
                itemDef.modified_model_colors = new int[] { 943, 530, 152, 28, 24, 20 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15694;
                break;

            // TODO: Gilded dragon boots, Bandos chest (g), Bandos tassets (g), Bandos boots (g),
            // Shayzien blowpipe, Yellowish blowpipe
            // Neitznot helmet & guard (light blue, greenish, red, black)
            // Bumblebee h'ween mask, pink top hat, brown top hat, white top hat / colorful (Top hat, sled / whip / satchel /
            // Gold Pirate's hat / Gold Veracs / Gold Twisted bow / DFS / Spirit shield / Gilded battle-axe / D long / D scim / Dagger / D bones / D axe / Robin hood hat / Ranger gloves
            // $10.00 Bond, $25.00 BOND, $50.00 BOND, $100.00 BOND
            // Bandos boots (g)
            // Max level lamp (1 skill 99) 34,500 premium points (ANY RANK)F
            case 15696:
                itemDef.copy(lookup(20997));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Dark twisted bow";
                itemDef.description = "";
                itemDef.original_model_colors = new int[] { 0, 0, 0 };
                itemDef.modified_model_colors = new int[] { 13223, 10318, 10334 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15697;
                break;
            case 15697:
                itemDef.copy(lookup(20998));
                itemDef.actions = new String[5];
                itemDef.name = "Dark twisted bow";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 0, 0 };
                itemDef.modified_model_colors = new int[] { 13223, 10318, 10334 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15696;
                break;
            case 15701: // Raw big shark
                itemDef.copy(lookup(7993));
                itemDef.actions = new String[5];
                itemDef.name = "Raw big shark";
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15702;
                break;
            case 15702: // Noted raw big shark
                itemDef.copy(lookup(386));
                itemDef.actions = new String[5];
                itemDef.name = "Raw big shark";
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15701;
                break;
            case 15703: // Burnt big shark
                itemDef.copy(lookup(7993));
                itemDef.actions = new String[5];
                itemDef.name = "Burnt big shark";
                itemDef.original_model_colors = new int[] { 127, 0, 22 };
                itemDef.modified_model_colors = new int[] { 0, 103, 61 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15704;
                break;
            case 15704: // Noted burnt big shark
                itemDef.copy(lookup(388));
                itemDef.actions = new String[5];
                itemDef.name = "Burnt big shark";
                itemDef.original_model_colors = new int[] { 127, 0, 22 };
                itemDef.modified_model_colors = new int[] { 0, 103, 61 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15703;
                break;
            case 15706:
                itemDef.copy(lookup(23874));
                itemDef.actions = new String[5];
                itemDef.actions[0] = "Eat";
                itemDef.name = "Paddlefish";
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15707;
                break;
            case 15707:
                itemDef.copy(lookup(386));
                itemDef.actions = new String[5];
                itemDef.name = "Paddlefish";
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15706;
                break;
            case 15708:
                itemDef.copy(lookup(23872));
                itemDef.actions = new String[5];
                itemDef.name = "Raw paddlefish";
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15709;
                break;
            case 15709:
                itemDef.copy(lookup(384));
                itemDef.actions = new String[5];
                itemDef.name = "Raw paddlefish";
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15708;
                break;
            case 15710:
                itemDef.copy(lookup(26467));
                itemDef.actions = new String[5];
                itemDef.name = "Superior void knight gloves (broken)";
                break;
            case 15711:
                itemDef.copy(lookup(26469));
                itemDef.actions = new String[5];
                itemDef.name = "Superior void top (broken)";
                break;
            case 15712:
                itemDef.copy(lookup(26471));
                itemDef.actions = new String[5];
                itemDef.name = "Superior void robe (broken)";
                break;
            case 15713:
                itemDef.copy(lookup(26473));
                itemDef.actions = new String[5];
                itemDef.name = "Superior void mage helm (broken)";
                break;
            case 15714:
                itemDef.copy(lookup(26475));
                itemDef.actions = new String[5];
                itemDef.name = "Superior void ranger helm (broken)";
                break;
            case 15715:
                itemDef.copy(lookup(26477));
                itemDef.actions = new String[5];
                itemDef.name = "Superior void melee helm (broken)";
                break;
            case 26467:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Superior void knight gloves";
                break;
            case 26469:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Superior void top";
                break;
            case 26471:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Superior void robe";
                break;
            case 26473:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Superior void mage helm";
                break;
            case 26475:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Superior void ranger helm";
                break;
            case 26477:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Superior void melee helm";
                break;
            case 11832:
            case 11834:
            case 11826:
            case 11828:
            case 11830:
            case 21018:
            case 21021:
            case 21024:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.actions[2] = "Breakdown";
                break;
            case 26382: // Torva
            case 26384:
            case 26386:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.actions[2] = "Breakdown";
                break;
            case 11847:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.actions[4] = "Drop";
                break;
            case 19556:
            case 24867:
            case 24863:
            case 24866:
            case 24864:
            case 24865:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.actions[4] = "Destroy";
                break;
            case 25139:
                itemDef.name = "Salvaged fragments";
                break;
            case 24761:
                itemDef.name = "Strange XP tome";
                break;
            case 15698: // Noted raw tetra
                itemDef.copy(lookup(386));
                itemDef.actions = new String[5];
                itemDef.name = "Raw tetra";
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 25664;
                break;
            case 15699: // Noted tetra
                itemDef.copy(lookup(386));
                itemDef.actions = new String[5];
                itemDef.name = "Tetra";
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 25666;
                break;
            case 15700: // Noted ruined tetra
                itemDef.copy(lookup(386));
                itemDef.actions = new String[5];
                itemDef.name = "Burnt tetra";
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 25668;
                break;
            case 8322:
            itemDef.copy(lookup(13316));
            itemDef.name = "10,000 Blood Money";
            itemDef.description = "Exchange for 10,000 Blood Money.";
            itemDef.stackable = true;
            break;
            case 10476: // Purple sweets
            case 24982: // White
            case 24984: // Red
            case 24986: // Black
                itemDef.stackable = true;
                break;
        case 8465:
            itemDef.copy(lookup(1004));
            itemDef.name = "1,000,000 coins";
            itemDef.description = "Exchange for 1,000,000 coins.";
            itemDef.stackable = true;
            break;
        case 15198:
            itemDef.copy(lookup(1004));
            itemDef.name = "10,000,000 coins";
            itemDef.description = "Exchange for 10,000,000 coins.";
            itemDef.stackable = true;
            break;
        case 15199:
            itemDef.copy(lookup(4834));
            itemDef.name = "25 Ourg bones";
            itemDef.description = "Exchange for 25 Ourg bones.";
            itemDef.stackable = false;
            break;
        case 14158:
            itemDef.copy(lookup(22124));
            itemDef.name = "50 Superior dragon bones";
            itemDef.description = "Exchange for 50 Superior dragon bones.";
            itemDef.stackable = false;
            break;
        case 14159:
            itemDef.copy(lookup(13441));
            itemDef.name = "250 Anglerfish";
            itemDef.description = "Exchange for 250 Anglerfish.";
            itemDef.stackable = false;
            break;
        case 14160:
            itemDef.copy(lookup(4770));
            itemDef.name = "500 Amethyst arrows";
            itemDef.description = "Exchange for 500 Amethyst arrows.";
            itemDef.stackable = false;
            break;
        case 14161:
            itemDef.copy(lookup(8479));
            itemDef.name = "500 Dragon bolts";
            itemDef.description = "Exchange for 500 Dragon bolts.";
            itemDef.stackable = false;
            break;
        case 14162:
            itemDef.copy(lookup(385));
            itemDef.name = "1000 Cooked shark";
            itemDef.description = "Exchange for 1000 Cooked sharks.";
            itemDef.stackable = false;
            break;
        case 15197:
            itemDef.copy(lookup(15195));
            itemDef.actions = new String[5];
            itemDef.name = "Colorful max cape (broken)";
            break;
        case 784:
            itemDef.name = "Guide book";
            break;
       /* case 19707:
            itemDef.name = "<col=4B0082>Amulet of eternal glory";
            break;*/
        case 21297:
        	itemDef.name = "Infernal cape (broken)";
        	break;
        case 4212:
        case 4214:
        case 4215:
        case 4216:
        case 4217:
        case 4218:
        case 4219:
        case 4220:
        case 4221:
        case 4222:
        case 4223:
        case 11748:
        case 11749:
        case 11750:
        case 11751:
        case 11752:
        case 11753:
        case 11754:
        case 11755:
        case 11756:
        case 11757:
        case 11758:
        case 4224:
        case 4225:
        case 4226:
        case 4227:
        case 4228:
        case 4229:
        case 4230:
        case 4231:
        case 4232:
        case 4233:
        case 4234:
        case 11759:
        case 11760:
        case 11761:
        case 11762:
        case 11763:
        case 11764:
        case 11765:
        case 11766:
        case 11767:
        case 11768:
        case 11769:
            case 9946:
            //case 12785:
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            break;
        case 11739:
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "Herb box";
            break;
        case 4202:
            itemDef.name = "Double combat ring";
            itemDef.actions[1] = "Wear";
            itemDef.actions[2] = "Check";
            break;
        case 25898: // Slayer helmets with no dis-assemble option
        case 25900:
        case 25904:
        case 25906:
        case 25910:
        case 25912:
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.actions[2] = "Check";
            break;
        case 23808:
                itemDef.actions = new String[5];
            itemDef.actions[4] = "Destroy";
            break;
       /* case 12931:
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.actions[2] = "Check";
            itemDef.actions[4] = "Drop";
            break;*/
       /* case 12808:
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.actions[2] = "Check";
            itemDef.actions[4] = "Drop";
            break;*/
         case 25738: // Scythe
            case 25741:
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.actions[2] = "Check";
            itemDef.actions[4] = "Drop";
            break;
        case 26520: // Cannon base (or)
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Set-up";
            break;
        case 26522: // Cannon (or) parts
            case 26524:
            case 26526:
            itemDef.actions = new String[5];
            break;
            case 26156:
                itemDef.name = "One life helm";
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.actions[4] = "Destroy";
                break;
            case 26158:
                itemDef.name = "One life platebody";
                break;
            case 26166:
                itemDef.name = "One life platelegs";
                break;
            case 26168:
                itemDef.name = "One life bracers";
                break;
            case 26170:
                itemDef.name = "Realism helm";
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.actions[4] = "Destroy";
                break;
            case 26172:
                itemDef.name = "Realism platebody";
                break;
            case 26180:
                itemDef.name = "Realism platelegs";
                break;
            case 26182:
                itemDef.name = "Realism bracers";
                break;
            case 13319:
                itemDef.name = "Royale victory cape";
                break;
            case 5733:
                itemDef.actions = new String[5];
                itemDef.actions[0] = "Eat";
                itemDef.actions[1] = "Inspect";
                itemDef.actions[2] = "Set-Skills";
                itemDef.actions[3] = "Spawn";
                itemDef.actions[4] = "Pvp-Presets";
                break;
            case 15716: // One life blade
                itemDef.copy(lookup(21646));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Nightmare warblade";
                itemDef.original_model_colors = new int[] { 15, 22, 15, 22, 32407, 35503, 8, 12 }; // blackish-white
                itemDef.modified_model_colors = new int[] { 7585, 6554, 6550, 7461, 10295, 10287, 8481, 7452 };
                break;
            case 15717: // Realism weapon
                itemDef.copy(lookup(13111));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Starshatterer";
                itemDef.original_model_colors = new int[] { 835, 820, 1, 3, 3, 2, 3, 3, 2, 1, 4, 5, 835, 5, 5 };
                itemDef.modified_model_colors = new int[] { 49075, 50112, 43179, 43171, 43026, 43034, 43038, 43059, 43030, 43166, 43162, 43286, 49088, 43043, 43047 };
                break;
            case 15718: // Realism staff
                itemDef.copy(lookup(12795));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Divinelight battlestaff";
                itemDef.original_model_colors = new int[] { 57411, 2080, 54350, 62130, 895, 57423, 835 };
                itemDef.modified_model_colors = new int[] { 6435, 5276, 0, 549, 34136, 41614, 40491 };
                break;
            case 15719: // Realism bow
                itemDef.copy(lookup(23857));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.actions[4] = "Destroy";
                itemDef.name = "Firesoul shortbow";
                break;
           case 15720:
                itemDef.copy(lookup(26484));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
               itemDef.actions[2] = "Check";
                //itemDef.name = "Malevolent whip";
                itemDef.name = "Torva whip";
                //itemDef.original_model_colors = new int[] { 7452, 10295, 10295, 7452, 7452, 10295, 7452, 10295, 6554, 6550, 6554, 68, 4, 10287, 7452, 10295, 6554, 6550, 68, 51, 22, 7452, 10295, 6554, 6550, 57, 20, 28, 152, 530, 24, 20, 28, 20 }; //32
                itemDef.original_model_colors = new int[] { 33, 68, 4, 72, 45, 78, -16112, 82, 51, -16116, 20, 22, 86, 24, 57, 33, 68, 4, 72, 45, 78, -16112, 82, 51, -16116, 20, 22, 86, 24, 57, 33, -16116, 68, 82 }; //34
                itemDef.modified_model_colors = new int[] { -22374, -22380, -22370, -32100, 530, 20, 6550, 10295, 152, 6554, 7452, 28, -22367, -32095, 32316, 30528, 31553, 31525, 22380, 22374, 22370, 22367, 30391, 30381, 30395, 30517, 32095, 32100, 32425, 32419, 32538, 29650, 31533, 31525 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15721;
                break;
            case 15721:
                itemDef.copy(lookup(4152));
                itemDef.actions = new String[5];
                itemDef.name = "Torva whip";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 33, 68, 4, 72, 45, 78, -16112, 82, 51, -16116, 20, 22, 86, 24, 57, 33, 68, 4, 72, 45, 78, -16112, 82, 51, -16116, 20, 22, 86, 24, 57, 33, -16116, 68, 82 }; //34
                itemDef.modified_model_colors = new int[] { -22374, -22380, -22370, -32100, 530, 20, 6550, 10295, 152, 6554, 7452, 28, -22367, -32095, 32316, 30528, 31553, 31525, 22380, 22374, 22370, 22367, 30391, 30381, 30395, 30517, 32095, 32100, 32425, 32419, 32538, 29650, 31533, 31525 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15720;
                break;
            case 15722:
                itemDef.addCustomItem(4151, 60000, 60001, "Infernal whip");
                itemDef.actions[2] = "Check";
                itemDef.addCustomTexture(40, 59);
                itemDef.unNotedItem(15722);
                break;
            case 15723:
                itemDef.addCustomItem(4152, 60000, 60001, "Infernal whip");
                itemDef.addCustomTexture(40, 59);
                itemDef.notedItem(15723);
                break;

            case 15724:
//                itemDef.copy(lookup(6832));
//                itemDef.actions = new String[5];
//                itemDef.actions[0] = "Open";
//                itemDef.name = "$50 mystery box";
                itemDef.copy(lookup(6830));
                itemDef.actions = new String[5];
                itemDef.actions[0] = "Open";
                itemDef.name = "VIP mystery box";
                //itemDef.inventory_model = 64984;
                //itemDef.addCustomTexture(59, 40);
               // itemDef.model_zoom = 1350;
                break;
            case 15725:
                itemDef.copy(lookup(6833));
                itemDef.actions = new String[5];
                itemDef.actions[0] = "Open";
                itemDef.name = "$100 mystery box";
                break;
            case 15726:
                itemDef.copy(lookup(13569));
                itemDef.actions = new String[5];
                itemDef.actions[0] = "Open";
                itemDef.name = "Superior Void Knight equipment";
                break;
            case 15727:
                itemDef.copy(lookup(6188));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Golden frog mask";
                itemDef.original_model_colors = new int[] { 7114, 7114, 7114 };
                itemDef.modified_model_colors = new int[] { 25490, 26520, 22412 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15728;
                break;
            case 15728:
                itemDef.copy(lookup(6188));
                itemDef.actions = new String[5];
                itemDef.actions[0] = "Open";
                itemDef.name = "Golden frog mask";
                itemDef.original_model_colors = new int[] { 7114, 7114, 7114 };
                itemDef.modified_model_colors = new int[] { 25490, 26520, 22412 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15728;
                break;
            case 15729:
                itemDef.copy(lookup(26484));
                itemDef.actions = new String[5];
                itemDef.name = "Torva whip (damaged)";
                //itemDef.original_model_colors = new int[] { 7452, 10295, 10295, 7452, 7452, 10295, 7452, 10295, 6554, 6550, 6554, 68, 4, 10287, 7452, 10295, 6554, 6550, 68, 51, 22, 7452, 10295, 6554, 6550, 57, 20, 28, 152, 530, 24, 20, 28, 20 }; //32
                itemDef.original_model_colors = new int[] { 15, 35, 4, 55, 30, 55, -16101, 55, 35, -16116, 15, 10, 55, 12, 57, 33, 38, 4, 55, 28, 66, -16112, 66, 40, -16116, 10, 10, 67, 15, 43, 22, -16110, 58, 62 }; //34
                itemDef.modified_model_colors = new int[] { -22374, -22380, -22370, -32100, 530, 20, 6550, 10295, 152, 6554, 7452, 28, -22367, -32095, 32316, 30528, 31553, 31525, 22380, 22374, 22370, 22367, 30391, 30381, 30395, 30517, 32095, 32100, 32425, 32419, 32538, 29650, 31533, 31525 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15720;
                break;
            case 15730:
                itemDef.addCustomItem(4151, 60000, 60001, "Infernal whip (uncharged)");
                itemDef.addCustomTexture(40, 59);
                itemDef.actions = new String[5];
                break;
            case 15731:
                itemDef.copy(lookup(7478));
                itemDef.actions = new String[5];
                itemDef.original_model_colors = new int[] { 5652, 5652, 5652, 5652, 5652, 5652 };
                itemDef.modified_model_colors = new int[] { 7892, 7888, 7756, 7752, 7748, 7739 };
                itemDef.actions[0] = "Redeem";
                itemDef.name = "5k premium points";
                break;
            case 15732:
                itemDef.copy(lookup(7478));
                itemDef.actions = new String[5];
                itemDef.original_model_colors = new int[] { 675, 675, 675, 675, 675, 675 };
                itemDef.modified_model_colors = new int[] { 7892, 7888, 7756, 7752, 7748, 7739 };
                itemDef.actions[0] = "Redeem";
                itemDef.name = "10k premium points";
                break;
            case 15733:
                itemDef.copy(lookup(7478));
                itemDef.actions = new String[5];
                itemDef.original_model_colors = new int[] { 689484, 689484, 689484, 689484, 689484, 689484 };
                itemDef.modified_model_colors = new int[] { 7892, 7888, 7756, 7752, 7748, 7739 };
                itemDef.actions[0] = "Redeem";
                itemDef.name = "25k premium points";
                break;
            case 15734:
                itemDef.copy(lookup(7478));
                itemDef.actions = new String[5];
                itemDef.original_model_colors = new int[] { 49863, 49863, 49863, 49863, 49863, 49863 };
                itemDef.modified_model_colors = new int[] { 7892, 7888, 7756, 7752, 7748, 7739 };
                itemDef.actions[0] = "Redeem";
                itemDef.name = "50k premium points";
                break;
            case 15735:
                itemDef.copy(lookup(7478));
                itemDef.actions = new String[5];
                itemDef.original_model_colors = new int[] { 7114, 7114, 7114, 7114, 7114, 7114 };
                itemDef.modified_model_colors = new int[] { 7892, 7888, 7756, 7752, 7748, 7739 };
                itemDef.actions[0] = "Redeem";
                itemDef.name = "100k premium points";
                break;
            // NEW CUSTOM ITEMS
            case 15736:
                itemDef.copy(lookup(20997));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Diamond twisted bow";
                itemDef.description = "";
                itemDef.original_model_colors = new int[] { -24764, -24764, -24764, -22409, -24764, -22409, -24764, -22409, -24764 };
                itemDef.modified_model_colors = new int[] { 13223, 10318, 10334, 16, 0, 33, 8, 24, 41 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15737;
                break;
            case 15737:
                itemDef.copy(lookup(20998));
                itemDef.actions = new String[5];
                itemDef.name = "Diamond twisted bow";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { -24764, -24764, -24764, -22409, -24764, -22409, -24764, -22409, -24764 };
                itemDef.modified_model_colors = new int[] { 13223, 10318, 10334, 16, 0, 33, 8, 24, 41 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15736;
                break;
            case 15738:
                itemDef.copy(lookup(26382));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Torva full helm (diamond)";
                itemDef.description = "";
                itemDef.original_model_colors = new int[] { -24764, -24762, -24730, -24760, -24750, -24748, -24744, -24710, -24780, -24785, -24764, -24720, -24764, -24764, -24755, -24780, -24764, -24764, -24764, -24764, -24764, -24764, -24764 };
                itemDef.modified_model_colors = new int[] { 33, 35, 68, 37, 39, 41, 43, 12, 76, 78, 47, 16, 18, 20, 53, 86, 55, 88, 24, 26, 28, 61, 30}; //23
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15739;
                break;
            case 15739:
                itemDef.copy(lookup(26409));
                itemDef.actions = new String[5];
                itemDef.name = "Torva full helm (diamond)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { -24764, -24762, -24730, -24760, -24750, -24748, -24744, -24710, -24780, -24785, -24764, -24720, -24764, -24764, -24755, -24780, -24764, -24764, -24764, -24764, -24764, -24764, -24764 };
                itemDef.modified_model_colors = new int[] { 33, 35, 68, 37, 39, 41, 43, 12, 76, 78, 47, 16, 18, 20, 53, 86, 55, 88, 24, 26, 28, 61, 30}; //23
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15738;
                break;
            case 15740:
                itemDef.copy(lookup(26384));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Torva platebody (diamond)";
                itemDef.description = "";
                itemDef.original_model_colors = new int[] { -24764, -24762, -24730, -24760, -24750, -24748, -24744, -24710, -24780, -24785, -24764, -24720, -24764, -24764, -24755, -24780, -24764, -24764, -24764, -24764, -24764, -24764, -24764 };
                itemDef.modified_model_colors = new int[] { 33, 35, 68, 37, 39, 41, 43, 12, 76, 78, 47, 16, 18, 20, 53, 86, 55, 88, 24, 26, 28, 61, 30}; //23
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15741;
                break;
            case 15741:
                itemDef.copy(lookup(26410));
                itemDef.actions = new String[5];
                itemDef.name = "Torva platebody (diamond)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { -24764, -24762, -24730, -24760, -24750, -24748, -24744, -24710, -24780, -24785, -24764, -24720, -24764, -24764, -24755, -24780, -24764, -24764, -24764, -24764, -24764, -24764, -24764 };
                itemDef.modified_model_colors = new int[] { 33, 35, 68, 37, 39, 41, 43, 12, 76, 78, 47, 16, 18, 20, 53, 86, 55, 88, 24, 26, 28, 61, 30}; //23
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15740;
                break;
            case 15742:
                itemDef.copy(lookup(26386));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Torva platelegs (diamond)";
                itemDef.description = "";
                itemDef.original_model_colors = new int[] { -24764, -24762, -24730, -24760, -24750, -24748, -24744, -24710, -24780, -24785, -24764, -24720, -24764, -24764, -24755, -24780, -24764, -24764, -24764, -24764, -24764, -24764, -24764, 120, 120, 120, 120 };
                itemDef.modified_model_colors = new int[] { 33, 35, 68, 37, 39, 41, 43, 12, 76, 78, 47, 16, 18, 20, 53, 86, 55, 88, 24, 26, 28, 61, 30, 82, 51, 57, 45}; //23
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15743;
                break;
            case 15743:
                itemDef.copy(lookup(26411));
                itemDef.actions = new String[5];
                itemDef.name = "Torva platelegs (diamond)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { -24764, -24762, -24730, -24760, -24750, -24748, -24744, -24710, -24780, -24785, -24764, -24720, -24764, -24764, -24755, -24780, -24764, -24764, -24764, -24764, -24764, -24764, -24764 };
                itemDef.modified_model_colors = new int[] { 33, 35, 68, 37, 39, 41, 43, 12, 76, 78, 47, 16, 18, 20, 53, 86, 55, 88, 24, 26, 28, 61, 30}; //23
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15742;
                break;
            case 15744:
                itemDef.copy(lookup(20997));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Infamous Lord's Tbow";
                itemDef.description = "";
                itemDef.original_model_colors = new int[] { 0, 926, 926, 926, 926, 926, 926, 0, 926 };
                itemDef.modified_model_colors = new int[] { 13223, 10318, 10334, 16, 0, 33, 8, 24, 41 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15745;
                break;
            case 15745:
                itemDef.copy(lookup(20998));
                itemDef.actions = new String[5];
                itemDef.name = "Infamous Lord's Tbow";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 0, 926, 926, 926, 926, 926, 926, 0, 926 };
                itemDef.modified_model_colors = new int[] { 13223, 10318, 10334, 16, 0, 33, 8, 24, 41 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15744;
                break;
            case 15746:
                itemDef.copy(lookup(22981));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Supreme wizard gauntlets";
                itemDef.original_model_colors = new int[] { 10266, -24762, -24762, -24762, 10266, 10266, 10266, 10266, 10266, 10266 };
                itemDef.modified_model_colors = new int[] { 16, 30643, 12484, 13493, -32630, 8, 24, 10411, 12 };
                break;
            case 15747:
                itemDef.copy(lookup(4734));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Ursa crossbow";
                itemDef.description = "";
                itemDef.original_model_colors = new int[] { 51138, 120, 51138, 51100, 51138, 51138, 51138, 51138, 51138, 51100, 51100, 51100 };
                itemDef.modified_model_colors = new int[] { 10512, 49, 10388, 10760, 10508, 8722, 10520, 6073, 8602, 7516, 10529, -14398 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15748;
                break;
            case 15748:
                itemDef.copy(lookup(4735));
                itemDef.actions = new String[5];
                itemDef.name = "Ursa crossbow";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 51138, 120, 51138, 51100, 51138, 51138, 51138, 51138, 51138, 51100, 51100, 51100 };
                itemDef.modified_model_colors = new int[] { 10512, 49, 10388, 10760, 10508, 8722, 10520, 6073, 8602, 7516, 10529, -14398 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15747;
                break;
            case 15749: //Celestial_Wings
                itemDef.copy(lookup(6570));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Celestial wing";
                itemDef.description = "A sacred light emitting high frequency gamma rays in all directions.";
                itemDef.inventory_model = 64924;
                itemDef.equipped_model_male_1 = 64923;
                itemDef.equipped_model_female_1 = 64923;
                break;

            case 15750: //Spider Wings
                itemDef.copy(lookup(6570));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Tarantula's cephalothorax";
                itemDef.description = "Tarantula's head and the thorax fused together offering great powers and protection.";
                itemDef.inventory_model = 64926;
                itemDef.equipped_model_male_1 = 64925;
                itemDef.equipped_model_female_1 = 64925;
                break;
            case 15751:
                itemDef.copy(lookup(4587));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.actions[2] = "Check";
                itemDef.name = "Bandos whip";
                itemDef.description = "A whip from the warchief Graardor ***General***!";
                itemDef.model_zoom = 1900;
                itemDef.rotation_x = 572;
                itemDef.inventory_model = 64974;
                itemDef.equipped_model_male_1 = 64975;
                itemDef.equipped_model_female_1 = 64975;
                itemDef.translate_x = -10;
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15752;
                break;
            case 15752:
                itemDef.copy(lookup(4152));
                itemDef.actions = new String[5];
                itemDef.name = "Bandos whip";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                //itemDef.model_zoom = 1900;
               // itemDef.rotation_x = 572;
                //itemDef.inventory_model = 64974;
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15751;
                break;
            case 15753:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Brown paper bag";
                itemDef.description = "A brown paper bag with 2 eyeholes cut into it, makes some people look better.";
                itemDef.inventory_model = 64983;
                itemDef.equipped_model_male_1 = 64978;
                itemDef.equipped_model_female_1 = 64978;
                itemDef.model_zoom = 1351;
                itemDef.rotation_x = 150;
                itemDef.rotation_y = 150;
                itemDef.rotation_z = 0;
                //itemDef.translate_yz = -10;
                break;
            case 15754:
                itemDef.copy(lookup(26382));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Torva full helm (zenyte)";
                itemDef.description = "";
                itemDef.original_model_colors = new int[] { 5056, 5050, 5020, 5076, 5050, 5060, 5040, 5070, 5080, 5090, 5056, 5020, 5056, 5056, 5055, 5080, 5056, 5056, 5056, 5056, 5056, 5056, 5056 };
                itemDef.modified_model_colors = new int[] { 33, 35, 68, 37, 39, 41, 43, 12, 76, 78, 47, 16, 18, 20, 53, 86, 55, 88, 24, 26, 28, 61, 30}; //23
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15755;
                break;
            case 15755:
                itemDef.copy(lookup(26409));
                itemDef.actions = new String[5];
                itemDef.name = "Torva full helm (zenyte)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 5056, 5050, 5020, 5076, 5050, 5060, 5040, 5070, 5080, 5090, 5056, 5020, 5056, 5056, 5055, 5080, 5056, 5056, 5056, 5056, 5056, 5056, 5056 };
                itemDef.modified_model_colors = new int[] { 33, 35, 68, 37, 39, 41, 43, 12, 76, 78, 47, 16, 18, 20, 53, 86, 55, 88, 24, 26, 28, 61, 30}; //23
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15754;
                break;
            case 15756:
                itemDef.copy(lookup(26384));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Torva platebody (zenyte)";
                itemDef.description = "";
                itemDef.original_model_colors = new int[] { 5056, 5050, 5020, 5076, 5050, 5060, 5040, 5070, 5080, 5090, 5056, 5020, 5056, 5056, 5055, 5080, 5056, 5056, 5056, 5056, 5056, 5056, 5056 };
                itemDef.modified_model_colors = new int[] { 33, 35, 68, 37, 39, 41, 43, 12, 76, 78, 47, 16, 18, 20, 53, 86, 55, 88, 24, 26, 28, 61, 30}; //23
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15757;
                break;
            case 15757:
                itemDef.copy(lookup(26410));
                itemDef.actions = new String[5];
                itemDef.name = "Torva platebody (zenyte)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 5056, 5050, 5020, 5076, 5050, 5060, 5040, 5070, 5080, 5090, 5056, 5020, 5056, 5056, 5055, 5080, 5056, 5056, 5056, 5056, 5056, 5056, 5056 };
                itemDef.modified_model_colors = new int[] { 33, 35, 68, 37, 39, 41, 43, 12, 76, 78, 47, 16, 18, 20, 53, 86, 55, 88, 24, 26, 28, 61, 30}; //23
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15756;
                break;
            case 15758:
                itemDef.copy(lookup(26386));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Torva platelegs (zenyte)";
                itemDef.description = "";
                itemDef.original_model_colors = new int[] { 5056, 5050, 5020, 5076, 5050, 5060, 5040, 5070, 5080, 5090, 5056, 5020, 5056, 5056, 5055, 5080, 5056, 5056, 5056, 5056, 5056, 5056, 5056, 120, 120, 120, 120 };
                itemDef.modified_model_colors = new int[] { 33, 35, 68, 37, 39, 41, 43, 12, 76, 78, 47, 16, 18, 20, 53, 86, 55, 88, 24, 26, 28, 61, 30, 82, 51, 57, 45}; //23
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15759;
                break;
            case 15759:
                itemDef.copy(lookup(26411));
                itemDef.actions = new String[5];
                itemDef.name = "Torva platelegs (zenyte)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 5056, 5050, 5020, 5076, 5050, 5060, 5040, 5070, 5080, 5090, 5056, 5020, 5056, 5056, 5055, 5080, 5056, 5056, 5056, 5056, 5056, 5056, 5056 };
                itemDef.modified_model_colors = new int[] { 33, 35, 68, 37, 39, 41, 43, 12, 76, 78, 47, 16, 18, 20, 53, 86, 55, 88, 24, 26, 28, 61, 30}; //23
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15758;
                break;
            case 15760:
                itemDef.copy(lookup(13652));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Dragon claws (pink)";
                itemDef.description = "Claws made for Master joe111.";
                itemDef.original_model_colors = new int[] { 57983, 57968, 57972, 57976 };
                itemDef.modified_model_colors = new int[] { 929, 914, 918, 922 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15761;
                break;
            case 15761:
                itemDef.copy(lookup(20848));
                itemDef.actions = new String[5];
                itemDef.name = "Dragon claws (pink)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 57983, 57968, 57972, 57976 };
                itemDef.modified_model_colors = new int[] { 929, 914, 918, 922 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15760;
                break;
            case 15762:
                itemDef.copy(lookup(12817));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Elysian spirit shield (t)";
                itemDef.description = "Shield made for Runestake.";
                itemDef.original_model_colors = new int[] { 86933, -12871, -15689, -15808, 86933, -12871, -12871, -12871 };
                itemDef.modified_model_colors = new int[] { 127, -29125, -29110, -21610, 105, -29019, -28716, -29116 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15763;
                break;
            case 15763:
                itemDef.copy(lookup(12818));
                itemDef.actions = new String[5];
                itemDef.name = "Elysian spirit shield (t)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 86933, 22464, -15689, -15808, 86933, -12871, -12871, -12871 };
                itemDef.modified_model_colors = new int[] { 127, -29125, -29110, -21610, 105, -29019, -28716, -29116 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15762;
                break;
            case 15764:
                itemDef.copy(lookup(26382));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Torva full helm (dark)";
                itemDef.description = "";
                itemDef.original_model_colors = new int[] { 15, 11, 20, 45, 11, 55, 25, 40, 33, 15, 15, 20, 15, 15, 10, 33, 15, 15, 15, 15, 15, 15, 15 };
                itemDef.modified_model_colors = new int[] { 33, 35, 68, 37, 39, 41, 43, 12, 76, 78, 47, 16, 18, 20, 53, 86, 55, 88, 24, 26, 28, 61, 30}; //23
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15765;
                break;
            case 15765:
                itemDef.copy(lookup(26409));
                itemDef.actions = new String[5];
                itemDef.name = "Torva full helm (dark)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 15, 11, 20, 45, 11, 55, 25, 40, 33, 15, 15, 20, 15, 15, 10, 33, 15, 15, 15, 15, 15, 15, 15 };
                itemDef.modified_model_colors = new int[] { 33, 35, 68, 37, 39, 41, 43, 12, 76, 78, 47, 16, 18, 20, 53, 86, 55, 88, 24, 26, 28, 61, 30}; //23
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15764;
                break;
            case 15766:
                itemDef.copy(lookup(26384));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Torva platebody (dark)";
                itemDef.description = "";
                itemDef.original_model_colors = new int[] { 15, 11, 20, 45, 11, 55, 25, 40, 33, 15, 15, 20, 15, 15, 10, 33, 15, 15, 15, 15, 15, 15, 15 };
                itemDef.modified_model_colors = new int[] { 33, 35, 68, 37, 39, 41, 43, 12, 76, 78, 47, 16, 18, 20, 53, 86, 55, 88, 24, 26, 28, 61, 30}; //23
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15767;
                break;
            case 15767:
                itemDef.copy(lookup(26410));
                itemDef.actions = new String[5];
                itemDef.name = "Torva platebody (dark)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 15, 11, 20, 45, 11, 55, 25, 40, 33, 15, 15, 20, 15, 15, 10, 33, 15, 15, 15, 15, 15, 15, 15 };
                itemDef.modified_model_colors = new int[] { 33, 35, 68, 37, 39, 41, 43, 12, 76, 78, 47, 16, 18, 20, 53, 86, 55, 88, 24, 26, 28, 61, 30}; //23
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15766;
                break;
            case 15768:
                itemDef.copy(lookup(26386));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Torva platelegs (dark)";
                itemDef.description = "";
                itemDef.original_model_colors = new int[] { 15, 11, 20, 45, 11, 55, 25, 40, 33, 15, 15, 20, 15, 15, 10, 33, 15, 15, 15, 15, 15, 15, 15, 120, 120, 120, 120 };
                itemDef.modified_model_colors = new int[] { 33, 35, 68, 37, 39, 41, 43, 12, 76, 78, 47, 16, 18, 20, 53, 86, 55, 88, 24, 26, 28, 61, 30, 82, 51, 57, 45}; //23
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15769;
                break;
            case 15769:
                itemDef.copy(lookup(26411));
                itemDef.actions = new String[5];
                itemDef.name = "Torva platelegs (dark)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 15, 11, 20, 45, 11, 55, 25, 40, 33, 15, 15, 20, 15, 15, 10, 33, 15, 15, 15, 15, 15, 15, 15 };
                itemDef.modified_model_colors = new int[] { 33, 35, 68, 37, 39, 41, 43, 12, 76, 78, 47, 16, 18, 20, 53, 86, 55, 88, 24, 26, 28, 61, 30}; //23
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15768;
                break;
            case 15770:
                itemDef.copy(lookup(26484));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Torva whip (dark)";
                itemDef.original_model_colors = new int[] { 13, 48, 4, 52, 25, 58, -16112, 12, 31, -16116, 10, 12, 56, 14, 37, 13, 48, 4, 42, 45, 58, -16112, 62, 31, -16116, 10, 12, 56, 20, 37, 33, -16116, 68, 52 };
                itemDef.modified_model_colors = new int[] { -22374, -22380, -22370, -32100, 530, 20, 6550, 10295, 152, 6554, 7452, 28, -22367, -32095, 32316, 30528, 31553, 31525, 22380, 22374, 22370, 22367, 30391, 30381, 30395, 30517, 32095, 32100, 32425, 32419, 32538, 29650, 31533, 31525 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15771;
                break;
            case 15771:
                itemDef.copy(lookup(4152));
                itemDef.actions = new String[5];
                itemDef.name = "Torva whip (dark)";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 13, 48, 4, 52, 25, 58, -16112, 12, 31, -16116, 10, 12, 56, 14, 37, 13, 48, 4, 42, 45, 58, -16112, 62, 31, -16116, 10, 12, 56, 20, 37, 33, -16116, 68, 52 };
                itemDef.modified_model_colors = new int[] { -22374, -22380, -22370, -32100, 530, 20, 6550, 10295, 152, 6554, 7452, 28, -22367, -32095, 32316, 30528, 31553, 31525, 22380, 22374, 22370, 22367, 30391, 30381, 30395, 30517, 32095, 32100, 32425, 32419, 32538, 29650, 31533, 31525 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15770;
                break;
            case 15772:
                itemDef.copy(lookup(20997));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Nanslayer twisted bow";
                itemDef.description = "";
                itemDef.original_model_colors = new int[] { 834, 49986, 834, 834, 49986, 834, 834, 49986, 834 };
                itemDef.modified_model_colors = new int[] { 13223, 10318, 10334, 16, 0, 33, 8, 24, 41 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15773;
                break;
            case 15773:
                itemDef.copy(lookup(20998));
                itemDef.actions = new String[5];
                itemDef.name = "Nanslayer twisted bow";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 834, 49986, 834, 834, 49986, 834, 834, 49986, 834 };
                itemDef.modified_model_colors = new int[] { 13223, 10318, 10334, 16, 0, 33, 8, 24, 41 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15772;
                break;

            case 15797:
                itemDef.copy(lookup(4587));
                itemDef.actions = new String[5];
                itemDef.name = "Bandos whip (damaged)";
                itemDef.description = "A damaged whip from the warchief Graardor ***General***!";
                itemDef.model_zoom = 1900;
                itemDef.rotation_x = 572;
                itemDef.inventory_model = 64974;
                itemDef.equipped_model_male_1 = 64975;
                itemDef.equipped_model_female_1 = 64975;
                break;
            case 15798:
                itemDef.copy(lookup(12821));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.name = "Divine spirit shield";
                itemDef.original_model_colors = new int[] { 25248, 25248, 25248, 25248, 25048, 123, 25048, 25148, 25048 };
                itemDef.modified_model_colors = new int[] { -20933, -21606, -21608, -20924, -21612, 123, -21599, -20918, -21610 };
                itemDef.rotation_x = 100;
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15799;
                break;
            case 15799:
                itemDef.copy(lookup(12822));
                itemDef.actions = new String[5];
                itemDef.name = "Divine spirit shield";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 25248, 25248, 25248, 25248, 25048, 123, 25048, 25148, 25048 };
                itemDef.modified_model_colors = new int[] { -20933, -21606, -21608, -20924, -21612, 123, -21599, -20918, -21610 };
                itemDef.rotation_x = 100;
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15798;
                break;
            case 15800:
                itemDef.copy(lookup(11943));
                itemDef.actions = new String[5];
                itemDef.actions[0] = "Bury";
                itemDef.name = "Sea dragon bones";
                itemDef.original_model_colors = new int[] { 127, -21059 };
                itemDef.modified_model_colors = new int[] { 115, 127 };
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15801;
                break;
            case 15801:
                itemDef.copy(lookup(11944));
                itemDef.actions = new String[5];
                itemDef.name = "Sea dragon bones";
                itemDef.original_model_colors = new int[] { 127, -21059 };
                itemDef.modified_model_colors = new int[] { 115, 127 };
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15800;
                break;
            case 15802:
                itemDef.copy(lookup(12823));
                itemDef.actions = new String[5];
                itemDef.name = "Divine sigil";
                itemDef.original_model_colors = new int[] { 25248, 25048 };
                itemDef.modified_model_colors = new int[] { -20163, -20175 };
                itemDef.rotation_x = 200;
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15803;
                break;
            case 15803:
                itemDef.copy(lookup(12824));
                itemDef.actions = new String[5];
                itemDef.name = "Divine sigil";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 25248, 25048 };
                itemDef.modified_model_colors = new int[] { -20163, -20175 };
                itemDef.rotation_x = 200;
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15802;
                break;
            case 15804:
                itemDef.copy(lookup(12765));
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wield";
                itemDef.name = "Dragon hunter bow";
                itemDef.original_model_colors = new int[] { 950, 950, 950, 950, 950, 926, 10417, 12, 3974, 6808, 3594 };
                itemDef.modified_model_colors = new int[] { 1571, 1575, 1436, 2454, 2576, 10417, 7331, 3974, 6808, 3594 };
                itemDef.rotation_x = 50;
                itemDef.noted_item_id = -1;
                itemDef.unnoted_item_id = 15805;
                break;
            case 15805:
                itemDef.copy(lookup(11236));
                itemDef.actions = new String[5];
                itemDef.name = "Dragon hunter bow";
                itemDef.description = "Swap this note at any bank for the equivalent item.";
                itemDef.original_model_colors = new int[] { 950, 950, 950, 950, 950, 926, 10417, 12, 3974, 6808, 3594 };
                itemDef.modified_model_colors = new int[] { 1571, 1575, 1436, 2454, 2576, 10417, 7331, 3974, 6808, 3594 };
                itemDef.rotation_x = 50;
                itemDef.noted_item_id = 799;
                itemDef.unnoted_item_id = 15804;
        case 23971: // Crystal gear
        case 23975:
        case 23979:
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.actions[2] = "Check";
        break;
        case 26708: // Remove dismantle for (or) items (we don't want it reversible)
            case 26870:
            case 26874:
            case 26862:
            case 26864:
            case 26866:
            case 26860:
            case 26850:
            case 26858:
            case 26868:
            case 25818:
            case 25102:
        case 25467:
        case 26531:
        case 26533:
        case 26535:
        case 26537:
        case 26539:
        case 25373:
        case 25376:
        case 25378:
        case 26710:
        case 26712:
        case 25916:
        case 25918:
        case 26486:
        case 25734:
        case 26488:
        case 26490:
        case 26492:
        case 26494:
        case 26496:
            case 24525:
            case 24527:
            case 26549:
            case 25987:
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            break;
            case 26714:
            case 26715:
            case 26716:
            case 26718:
            case 26719:
            case 26720:
                itemDef.actions = new String[5];
                itemDef.actions[1] = "Wear";
                itemDef.actions[2] = "Breakdown";
                break;
        case 13197:
        case 13199:
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.actions[2] = "Check";
            itemDef.actions[3] = "Restore";
            itemDef.actions[4] = "Destroy";
            break;
        case 13342:
        case 21898:
        case 13329:
        case 13331:
        case 13333:
        case 13335:
        case 13337:
        case 21285:
        case 21784:
        case 21776:
        case 21780:
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.actions[3] = "Teleports";
            break;
        case 21205:
            itemDef.actions = new String[5];
            itemDef.name = "Elder maul (broken)";
            break;
        case 20593: // Temporary AGS
            itemDef.name = "Armadyl godsword (c)";
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.actions[2] = "Check";
            break;
        case 20784: // Temporary CLAWS
            itemDef.name = "Dragon claws (c)";
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.actions[2] = "Check";
            break;
        case 20405: // Temporary CLAWS
            itemDef.name = "Abyssal whip (c)";
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.actions[2] = "Check";
            break;
        case 20408: // Temporary DBOW
            itemDef.name = "Dark bow (c)";
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.actions[2] = "Check";
            break;
        case 3840: // Religious books
        case 3842:
        case 3844:
        case 12608:
        case 12610:
        case 12612:
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            break;
        case 22625: // PVP ITEMS
        case 22628:
        case 22631:
        case 22622:
        case 22610:
        case 22613:
        case 22616:
        case 22619:
        case 22638:
        case 22641:
        case 22644:
        case 22650:
        case 22653:
        case 22656:
        case 22647:
        case 22636:
            itemDef.actions[2] = "Check";
            break;
        case 12898:
            itemDef.name = "<col=DF1E44>Dice";
            itemDef.actions = new String[5];
            itemDef.actions[2] = "Roll";
            itemDef.description = "Rolls a random chance from 0-100 as a number.";
            break;
        case 10833:
            itemDef.name = "<col=DF1E44>Dice bag";
            itemDef.actions = new String[5];
            itemDef.actions[2] = "Roll";
            itemDef.description = "Rolls a random chance from 0-100 as a number.";
            break;
        case 6465:
            itemDef.name = "@yel@Fairy ring(+)";
            itemDef.actions[1] = "Wear";
            itemDef.actions[2] = "Transform";
            break;
        case 15020:
            itemDef.copy(lookup(21003));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.actions[2] = "Check";
            itemDef.name = "Elder maul (c)";
			break;
        case 15021:
            itemDef.copy(lookup(21006));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.actions[2] = "Check";
            itemDef.name = "Kodai wand (c)";
			break;
        case 15022:
            itemDef.copy(lookup(19481));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.actions[2] = "Check";
            itemDef.name = "Heavy ballista (c)";
			break;
        case 15023:
            itemDef.copy(lookup(20997));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.actions[2] = "Check";
            itemDef.name = "Twisted bow (c)";
			break;
        case 15024:
            itemDef.copy(lookup(12821));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.actions[2] = "Check";
            itemDef.name = "Spectral spirit shield (c)";
			break;
        case 15025:
            itemDef.copy(lookup(12825));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.actions[2] = "Check";
            itemDef.name = "Arcane spirit shield (c)";
			break;
        case 15026:
            itemDef.copy(lookup(11832));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.actions[2] = "Check";
            itemDef.name = "Bandos chestplate (c)";
			break;
        case 15027:
            itemDef.copy(lookup(11834));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.actions[2] = "Check";
            itemDef.name = "Bandos tassets (c)";
			break;
        case 15028:
            itemDef.copy(lookup(11826));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.actions[2] = "Check";
            itemDef.name = "Armadyl helmet (c)";
			break;
        case 15029:
            itemDef.copy(lookup(11828));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.actions[2] = "Check";
            itemDef.name = "Armadyl chestplate (c)";
			break;
        case 15030:
            itemDef.copy(lookup(11830));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.actions[2] = "Check";
            itemDef.name = "Armadyl chainskirt (c)";
			break;
        case 15031:
            itemDef.copy(lookup(5022));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Redeem";
            itemDef.name = "Voting ticket";
            itemDef.stackable = true;
			break;
        case 15152:
            itemDef.copy(lookup(4587));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.actions[2] = "Inspect";
            itemDef.inventory_model  = 65047;
            itemDef.equipped_model_male_1  = 65048;
            itemDef.equipped_model_female_1  = 65048;
            itemDef.name = "Lava blade";
            itemDef.description = "One of the sharpest and deadliest blade's in the game.";
            itemDef.original_model_colors = new int[] { 6073, 5959 };
            itemDef.modified_model_colors = new int[] { 933, 935 };
			break;

        case 15153:
            itemDef.copy(lookup(4151));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.name = "Ice whip";
            itemDef.description = "A whip that has some variants of ultramarine.";
            itemDef.original_model_colors = new int[] { 43968 };
            itemDef.modified_model_colors = new int[] { 528 };
			break;
        case 15154:
            itemDef.copy(lookup(4151));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.name = "Pimpz whip";
            itemDef.description = "A whip for pimpz.";
            itemDef.original_model_colors = new int[] { 51138 };
            itemDef.modified_model_colors = new int[] { 528 };
			break;
        case 15155:
            itemDef.copy(lookup(4151));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.name = "Dragon whip";
            itemDef.description = "A dragon's tail.";
            itemDef.original_model_colors = new int[] { 950 };
            itemDef.modified_model_colors = new int[] { 528 };
            itemDef.noted_item_id = -1;
            itemDef.unnoted_item_id = 15430;
			break;
        case 15156:
            itemDef.copy(lookup(4151));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.name = "Sunfire whip";
            itemDef.description = "A whip with sparkling gold effect.";
            itemDef.original_model_colors = new int[] { 7120 };
            itemDef.modified_model_colors = new int[] { 528 };
			break;
        case 15157:
            itemDef.copy(lookup(4151));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.name = "Null whip";
            itemDef.description = "...Unknown?!";
            itemDef.original_model_colors = new int[] { 0 };
            itemDef.modified_model_colors = new int[] { 528 };
			break;
        case 15158:
            itemDef.copy(lookup(4151));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.name = "Crystal whip";
            itemDef.description = "Made by the ancient white knights of Falador.";
            itemDef.original_model_colors = new int[] { -22409 };
            itemDef.modified_model_colors = new int[] { 528 };
			break;
        case 15159:
            itemDef.copy(lookup(2589));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Black kiteshield (p)";
            itemDef.description = "Black kiteshield with a purple trim.";
            itemDef.original_model_colors = new int[] { 0, 0, 51136 };
            itemDef.modified_model_colors = new int[] { 61, 57, 7054 };
            itemDef.noted_item_id = -1;
            itemDef.unnoted_item_id = 15509;
			break;
        case 15160:
            itemDef.copy(lookup(20368));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.actions[2] = "Inspect";
            itemDef.name = "Dragon godsword";
            itemDef.description = "A dragon's godsword.";
            itemDef.original_model_colors = new int[] { 920, 0, 3, 0, 3, 0, 3, 930, 920, 926, 921, 930 };
            itemDef.modified_model_colors = new int[] { 920, 43059, 43072, 26, 35, 22, 30, 43113, 43092, 43121, 43096, 43117 };
			break;
        case 15161:
            itemDef.copy(lookup(1038));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            //itemDef.name = "<col=87CEFA>Baby blue partyhat";
            itemDef.name = "Baby blue partyhat";
            itemDef.description = "A nice hat from a cracker.";
            itemDef.original_model_colors = new int[] { 38226 };
            itemDef.modified_model_colors = new int[] { 926 };
			break;
        case 15162:
            itemDef.copy(lookup(1038));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Orange partyhat";
            itemDef.description = "A nice hat from a cracker.";
            itemDef.original_model_colors = new int[] { 6073 };
            itemDef.modified_model_colors = new int[] { 926 };
			break;
        case 15163:
            itemDef.copy(lookup(4151));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.name = "Forest whip";
            itemDef.description = "An ancient old whip.";
            itemDef.original_model_colors = new int[] { 350770 };
            itemDef.modified_model_colors = new int[] { 528 };
			break;
        case 15164:
            itemDef.copy(lookup(4151));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.name = "Rose whip";
            itemDef.description = "A whip derived from roses and true romance.";
            itemDef.original_model_colors = new int[] { 380770 };
            itemDef.modified_model_colors = new int[] { 528 };
			break;
        case 15165:
            itemDef.copy(lookup(4151));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.name = "Banana whip";
            itemDef.description = "Perhaps, a monkey dropped this by mistake.";
            itemDef.original_model_colors = new int[] { 338770 };
            itemDef.modified_model_colors = new int[] { 528  };
			break;
        case 15166:
            itemDef.copy(lookup(11802));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.name = "Gilded godsword";
            itemDef.description = "A gilded godsword.";
            itemDef.original_model_colors = new int[] { 7097, 7114, 7114, 7114, 7097, 7114, 7114, 7114, 7114, 7097, 7114, 7097 };
            itemDef.modified_model_colors = new int[] { 920, 43059, 43072, 26, 35, 22, 30, 43113, 43092, 43121, 43096, 43117 };
			break;
        case 15167:
            itemDef.copy(lookup(1050));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Butter santa hat";
            itemDef.description = "A santa's hat.";
            itemDef.original_model_colors = new int[] { 43934, 0 };
            itemDef.modified_model_colors = new int[] { 933, 10351 };
			break;
        case 15168:
            itemDef.copy(lookup(1050));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Azure santa hat";
            itemDef.description = "A santa's hat.";
            itemDef.original_model_colors = new int[] { 43968, 127 };
            itemDef.modified_model_colors = new int[] { 933, 10351 };
			break;
        case 15169:
            itemDef.copy(lookup(1050));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Gilded santa hat";
            itemDef.description = "A santa's hat.";
            itemDef.original_model_colors = new int[] { 7114, 7097 };
            itemDef.modified_model_colors = new int[] { 933, 10351 };
			break;
        case 15170:
            itemDef.copy(lookup(1050));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Pimpz santa hat";
            itemDef.description = "A santa's hat.";
            itemDef.original_model_colors = new int[] { 51136, 51136 };
            itemDef.modified_model_colors = new int[] { 933, 10351 };
			break;
        case 15171:
            itemDef.copy(lookup(1053));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Exclusive h'ween mask";
            itemDef.description = "A colorful h'ween mask.";
            itemDef.original_model_colors = new int[] { 1000 };
            itemDef.modified_model_colors = new int[] { 926 };
			break;
        case 15172:
            itemDef.copy(lookup(1201));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Rune dragon kiteshield";
            itemDef.description = "A strong metal shield.";
            itemDef.original_model_colors = new int[] { 926, 7114, 7114 };
            itemDef.modified_model_colors = new int[] { 61, 57, 7114 };
			break;
        case 15173:
            itemDef.copy(lookup(1127));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Rune dragon platebody";
            itemDef.description = "A strong metal platebody.";
            itemDef.original_model_colors = new int[] { 926, 926, 7114 };
            itemDef.modified_model_colors = new int[] { 61, 57, 7114 };
			break;
        case 15174:
            itemDef.copy(lookup(1079));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Rune dragon platelegs";
            itemDef.description = "A strong metal platelegs.";
            itemDef.original_model_colors = new int[] { 926, 926, 926 };
            itemDef.modified_model_colors = new int[] { 61, 41, 57 };
			break;
        case 15175:
            itemDef.copy(lookup(1163));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Rune dragon full helm";
            itemDef.description = "A strong metal helmet.";
            itemDef.original_model_colors = new int[] { 926, 7114, 7114 };
            itemDef.modified_model_colors = new int[] { 61, 57, 7114 };
			break;
        case 15176:
            itemDef.copy(lookup(4720));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Dragon dh platebody";
            itemDef.description = "A strong metal shield.";
            itemDef.original_model_colors = new int[] { 933, 933, 7114 };
            itemDef.modified_model_colors = new int[] { 61, 57, 7114 };
			break;
        case 15177:
            itemDef.copy(lookup(1333));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.name = "Blood scimitar";
            itemDef.description = "A very razor sharp scimitar.";
            itemDef.original_model_colors = new int[] { 926, 7114, 7114 };
            itemDef.modified_model_colors = new int[] { 61, 57, 7114 };
			break;
        case 15178:
            itemDef.copy(lookup(2583));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Black platebody (p)";
            itemDef.description = "Black platebody with a purple trim.";
            itemDef.original_model_colors = new int[] { 51136, 8, 0 };
            itemDef.modified_model_colors = new int[] { 24, 61, 41 };
            itemDef.noted_item_id = -1;
            itemDef.unnoted_item_id = 15507;
			break;
        case 15179:
            itemDef.copy(lookup(1079));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Black platelegs (p)";
            itemDef.description = "Black platelegs with a purple trim.";
            itemDef.original_model_colors = new int[] { 0, 0, 51136 };
            itemDef.modified_model_colors = new int[] { 61, 41, 57 };
            itemDef.noted_item_id = -1;
            itemDef.unnoted_item_id = 15508;
			break;
        case 15180:
            itemDef.copy(lookup(2595));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Black full helm (p)";
            itemDef.description = "Black helmet with a purple trim.";
            itemDef.original_model_colors = new int[] { 0, 51136 };
            itemDef.modified_model_colors = new int[] { 61, 926 };
            itemDef.noted_item_id = -1;
            itemDef.unnoted_item_id = 15506;
			break;
        case 15181:
            itemDef.copy(lookup(3473));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Black plateskirt (p)";
            itemDef.description = "Black plateskirt with a purple trim.";
            itemDef.original_model_colors = new int[] { 0, 0, 51136, 51136 };
            itemDef.modified_model_colors = new int[] { 61, 41, 57, 25238 };
            itemDef.noted_item_id = -1;
            itemDef.unnoted_item_id = 15510;
			break;
        case 15182:
            itemDef.copy(lookup(1053));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Yellow h'ween mask";
            itemDef.description = "A yellow halloween mask.";
            itemDef.original_model_colors = new int[] { 11200 };
            itemDef.modified_model_colors = new int[] { 926 };
			break;
        case 15183:
            itemDef.copy(lookup(1053));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Purple h'ween mask";
            itemDef.description = "A purple halloween mask.";
            itemDef.original_model_colors = new int[] { 374770 };
            itemDef.modified_model_colors = new int[] { 926 };
			break;
        case 15184:
            itemDef.copy(lookup(1053));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "White h'ween mask";
            itemDef.description = "A white halloween mask.";
            itemDef.original_model_colors = new int[] { 120 };
            itemDef.modified_model_colors = new int[] { 926 };
			break;
        case 15185:
            itemDef.copy(lookup(1053));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Orange h'ween mask";
            itemDef.description = "A orange halloween mask.";
            itemDef.original_model_colors = new int[] { 461770 };
            itemDef.modified_model_colors = new int[] { 926 };
			break;
        case 15186:
            itemDef.copy(lookup(1053));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Crayon h'ween mask";
            itemDef.description = "A crayon halloween mask.";
            itemDef.original_model_colors = new int[] { 305770 };
            itemDef.modified_model_colors = new int[] { 926 };
			break;
        case 15187:
            itemDef.copy(lookup(1038));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Coffee partyhat";
            itemDef.description = "A nice hat from a cracker.";
            itemDef.original_model_colors = new int[] { 266770 };
            itemDef.modified_model_colors = new int[] { 926 };
			break;
        case 15188:
            itemDef.copy(lookup(1038));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Camo partyhat";
            itemDef.description = "A nice hat from a cracker.";
            itemDef.original_model_colors = new int[] { 479770 };
            itemDef.modified_model_colors = new int[] { 926 };
			break;
        case 15189:
            itemDef.copy(lookup(1038));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Aqua partyhat";
            itemDef.description = "A nice hat from a cracker.";
            itemDef.original_model_colors = new int[] { 226770 };
            itemDef.modified_model_colors = new int[] { 926 };
			break;
        case 15190:
            itemDef.copy(lookup(1038));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Gum partyhat";
            itemDef.description = "A nice hat from a cracker.";
            itemDef.original_model_colors = new int[] { 129770 };
            itemDef.modified_model_colors = new int[] { 926 };
			break;
        case 15191:
            itemDef.copy(lookup(1038));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Teal partyhat";
            itemDef.description = "A nice hat from a cracker.";
            itemDef.original_model_colors = new int[] { 12821 };
            itemDef.modified_model_colors = new int[] { 926 };
			break;
        case 15192:
            itemDef.copy(lookup(1050));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Fuchsia santa hat";
            itemDef.description = "A santa's hat.";
            itemDef.original_model_colors = new int[] { 123456, 127 };
            itemDef.modified_model_colors = new int[] { 933, 10351 };
			break;
        case 15200:
            itemDef.copy(lookup(13346));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "<col=A0522D>Barrows mystery box";
            itemDef.description = "A barrows container mystery box.";
            itemDef.original_model_colors = new int[] { 266770 };
            itemDef.modified_model_colors = new int[] { 22410 };
			break;
        case 15201:
            itemDef.copy(lookup(6199));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "<col=D2691E>Legendary mystery box";
            itemDef.description = "A legendary mystery box.";
            itemDef.original_model_colors = new int[] { 0, 11200 };
            itemDef.modified_model_colors = new int[] { 2999, 22410 };
			break;
        case 15202:
            itemDef.copy(lookup(6199));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "<col=DF1E44>PVP mystery box";
            itemDef.description = "A PVP mystery box.";
            itemDef.original_model_colors = new int[] { 127, 950 };
            itemDef.modified_model_colors = new int[] { 2999, 22410 };
			break;
        case 15203:
            itemDef.copy(lookup(6199));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "<col=FFFF00>Gilded mystery box";
            itemDef.description = "A PVP mystery box.";
            itemDef.original_model_colors = new int[] { 127, 7114 };
            itemDef.modified_model_colors = new int[] { 2999,22410 };
			break;
        case 15204:
            itemDef.copy(lookup(8152));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "@or1@Sacred mystery box";
            itemDef.description = "An epic rares mystery box.";
            //itemDef.original_model_colors = new int[] { 127, 0 };
            //itemDef.modified_model_colors = new int[] { 2999,22410 };
			break;
        case 15205:
            itemDef.copy(lookup(6199));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "<col=5D7DFA>Super mystery box";
            itemDef.description = "A super mystery box.";
            itemDef.original_model_colors = new int[] { 127, 43968 };
            itemDef.modified_model_colors = new int[] { 2999, 22410 };
			break;
        case 15206:
            itemDef.copy(lookup(6199));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "<col=EE82EE>Extreme mystery box";
            itemDef.description = "An extreme mystery box.";
            itemDef.original_model_colors = new int[] { 127, 49863 };
            itemDef.modified_model_colors = new int[] { 2999, 22410 };
			break;
        case 15207:
            itemDef.copy(lookup(6199));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "Voting mystery box";
            itemDef.description = "A voting mystery box.";
            itemDef.original_model_colors = new int[] { 3423 };
            itemDef.modified_model_colors = new int[] { 22410 };
			break;
        case 15208:
            itemDef.copy(lookup(9083));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "Void Knight equipment";
            itemDef.description = "A set containing Void Knight's equipment.";
			break;
        case 15209:
            itemDef.copy(lookup(11666));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "Elite Void Knight equipment";
            itemDef.description = "A set containing elite Void Knight's equipment.";
			break;
        case 15210:
            itemDef.copy(lookup(405));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "3rd age melee set";
            itemDef.description = "A 3rd age melee set.";
			break;
        case 15211:
            itemDef.copy(lookup(405));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "3rd age range set";
            itemDef.description = "A 3rd age range set.";
			break;
        case 15212:
            itemDef.copy(lookup(405));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "3rd age mage set";
            itemDef.description = "A 3rd age mage set.";
			break;
        case 15213:
            itemDef.copy(lookup(405));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "Infinity robes set";
            itemDef.description = "An infinity robes set.";
			break;
        case 15214:
            itemDef.copy(lookup(405));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "Corrupted armour set";
            itemDef.description = "A corrupted armour set.";
			break;
        case 15215:
            itemDef.copy(lookup(13346));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "<img=740> Staff's present";
            itemDef.description = "A staff's present.";
			break;
        case 15216:
            itemDef.copy(lookup(3849));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "Statius's armour set";
            itemDef.description = "A Statius armour set.";
			break;
        case 15217:
            itemDef.copy(lookup(3849));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "Vesta's armour set";
            itemDef.description = "A Vesta's armour set.";
			break;
        case 15218:
            itemDef.copy(lookup(3849));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "Morrigan's armour set";
            itemDef.description = "A Morrigan's armour set.";
			break;
        case 15219:
            itemDef.copy(lookup(3849));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "Zuriel's armour set";
            itemDef.description = "A Zuriels armour set.";
			break;
        case 15220: 
            itemDef.copy(lookup(1037));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Gilded bunny ears";
            itemDef.description = "Gilded bunny ears.";
            itemDef.original_model_colors = new int[] { 6850, 7114 };
            itemDef.modified_model_colors = new int[] { 10351, 220 };
			break;
        case 15221: 
            itemDef.copy(lookup(1275));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.name = "Gilded pickaxe";
            itemDef.description = "Gilded pickaxe.";
            itemDef.original_model_colors = new int[] { 7114 };
            itemDef.modified_model_colors = new int[] { 61 };
			break;
        case 15222: 
            itemDef.copy(lookup(1359));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.name = "Gilded axe";
            itemDef.description = "Gilded axe.";
            itemDef.original_model_colors = new int[] { 7114 };
            itemDef.modified_model_colors = new int[] { 61 };
			break;
        case 15223:
            itemDef.copy(lookup(12765));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.name = "Gilded dark bow";
            itemDef.description = "A gilded darkbow.";
            itemDef.original_model_colors = new int[] { 7114, 7114, 7114, 7114, 7114 };
            itemDef.modified_model_colors = new int[] { 1571,1575,1436,2454,2576 };
			break;
        case 15224:
            itemDef.copy(lookup(9185));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.name = "Gilded crossbow";
            itemDef.description = "A gilded crossbow.";
            itemDef.original_model_colors = new int[] { 7114, 7114, 7114, 7114, 7114, 7114 };
            itemDef.modified_model_colors = new int[] { 6447,6443,6439,5656,5652,5904 };
			break;
        case 15225:
            itemDef.copy(lookup(11785));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.name = "Gilded armadyl crossbow";
            itemDef.description = "A gilded crossbow.";
            itemDef.original_model_colors = new int[] { 7114, 7114, 7114, 7114 };
            itemDef.modified_model_colors = new int[] { 5409,5404,6449,7390 };
			break;
        case 15226:
            itemDef.copy(lookup(6585));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Amulet of fury (g)";
            itemDef.description = "An amulet of fury with a golden finish";
            itemDef.original_model_colors = new int[] { 7114, 7114, 7114 };
            itemDef.modified_model_colors = new int[] { 43164, 43074, 43086 };
			break;
        case 15227:
            itemDef.copy(lookup(8839));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Void knight top (g)";
            itemDef.description = "A void knight top (g).";
            itemDef.original_model_colors = new int[] { 7114, 7114, 7114 };
            itemDef.modified_model_colors = new int[] { 16, 24, 90 };
			break;
        case 15228:
            itemDef.copy(lookup(8840));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Void knight robe (g)";
            itemDef.description = "A void knight robe (g).";
            itemDef.original_model_colors = new int[] { 7114, 7114, 7114 };
            itemDef.modified_model_colors = new int[] { 20, 90, 8 };
			break;
        case 15229:
            itemDef.copy(lookup(11663));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Void mage helm (g)";
            itemDef.description = "A void mage helm (g).";
            itemDef.original_model_colors = new int[] { 7114, 7114 };
            itemDef.modified_model_colors = new int[] { 70, 24 };
			break;
        case 15230:
            itemDef.copy(lookup(11664));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Void ranger helm (g)";
            itemDef.description = "A void ranger helm (g).";
            itemDef.original_model_colors = new int[] { 7114, 7114, 7114 };
            itemDef.modified_model_colors = new int[] { 78, 74, 90 };
			break;
        case 15231:
            itemDef.copy(lookup(11665));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Void melee helm (g)";
            itemDef.description = "A void melee helm (g).";
            itemDef.original_model_colors = new int[] { 7114, 7114, 7114 };
            itemDef.modified_model_colors = new int[] { 24, 90, 53 };
			break;
        case 15232:
            itemDef.copy(lookup(13072));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Elite void top (g)";
            itemDef.description = "An elite void top (g).";
            itemDef.original_model_colors = new int[] { 7114, 7114, 7114, 7114, 7114, 7114 };
            itemDef.modified_model_colors = new int[] { 24, 7442, 7446, 7322, 7326, 16 };
			break;
        case 15233:
            itemDef.copy(lookup(13073));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Elite void robe (g)";
            itemDef.description = "An elite void robe (g).";
            itemDef.original_model_colors = new int[] { 7114, 7114, 7114, 7114, 7114, 7114, 7114, 7114, 7114, 7114, 7114, 7114, 7114 };
            itemDef.modified_model_colors = new int[] { 115, 111, 119, 66, 12, 127, 82, 21568, 21576, 21568, 21559, 21572, 21563 };
			break;
        case 15234:
            itemDef.copy(lookup(2631));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Highwayman mask (g)";
            itemDef.description = "A highwayman mask (g).";
            itemDef.original_model_colors = new int[] { 7114 };
            itemDef.modified_model_colors = new int[] { 43146 };
			break;
        case 15235:
            itemDef.copy(lookup(12335));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Gilded briefcase";
            itemDef.description = "A luxurious golden briefcase.";
            itemDef.original_model_colors = new int[] { 7114, 7097 };
            itemDef.modified_model_colors = new int[] { 12, 4 };
			break;
        case 15236:
            itemDef.copy(lookup(9470));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Gilded gnome scarf";
            itemDef.description = "A gilded gnome scarf.";
            itemDef.original_model_colors = new int[] { 7114, 7114, 7114 };
            itemDef.modified_model_colors = new int[] { 119, 103, 127 };
			break;
        case 15237:
            itemDef.copy(lookup(10887));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Gilded barrelchest anchor";
            itemDef.description = "A golden barrelchest anchor.";
            itemDef.original_model_colors = new int[] { 7097, 7114, 7114, 7114, 7097 };
            itemDef.modified_model_colors = new int[] { 10283, 10287, 10279, 10291, 10275 };
			break;
        case 15238:
            itemDef.copy(lookup(12432));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Gilded top hat";
            itemDef.description = "A gilded top hat.";
            itemDef.original_model_colors = new int[] { 7114, 929 };
            itemDef.modified_model_colors = new int[] { 12, 691 };
			break;
        case 15239:
            itemDef.copy(lookup(20095));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Gilded ankou mask";
            itemDef.description = "A gilded ankou mask.";
            itemDef.original_model_colors = new int[] { 7114 };
            itemDef.modified_model_colors = new int[] { 960 };
			break;
        case 15240:
            itemDef.copy(lookup(20098));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Gilded ankou top";
            itemDef.description = "A gilded ankou top.";
            itemDef.original_model_colors = new int[] { 7114 };
            itemDef.modified_model_colors = new int[] { 960 };
			break;
        case 15241:
            itemDef.copy(lookup(20104));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Gilded ankou's leggings";
            itemDef.description = "A gilded ankou leggings.";
            itemDef.original_model_colors = new int[] { 7114 };
            itemDef.modified_model_colors = new int[] { 960 };
			break;
        case 15242:
            itemDef.copy(lookup(20101));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Gilded ankou gloves";
            itemDef.description = "A gilded ankou gloves.";
            itemDef.original_model_colors = new int[] { 7114 };
            itemDef.modified_model_colors = new int[] { 960 };
			break;
        case 15243:
            itemDef.copy(lookup(20107));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Gilded ankou socks";
            itemDef.description = "A gilded ankou socks.";
            itemDef.original_model_colors = new int[] { 7114 };
            itemDef.modified_model_colors = new int[] { 960 };
			break;
        case 15244:
            itemDef.copy(lookup(20056));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Gilded ale of the gods";
            itemDef.description = "A gilded ale of the gods.";
            itemDef.original_model_colors = new int[] { 7114, 7114, 7114 };
            itemDef.modified_model_colors = new int[] { 6579, 6587, 6596 };
			break;
        case 15245:
            itemDef.copy(lookup(20095));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Ankou mask";
            itemDef.description = "Ankou mask.";
            itemDef.original_model_colors = new int[] { 127 };
            itemDef.modified_model_colors = new int[] { 960 };
			break;
        case 15246:
            itemDef.copy(lookup(20098));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Ankou top";
            itemDef.description = "Ankou top.";
            itemDef.original_model_colors = new int[] { 127 };
            itemDef.modified_model_colors = new int[] { 960 };
			break;
        case 15247:
            itemDef.copy(lookup(20104));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Ankou leggings";
            itemDef.description = "Ankou leggings.";
            itemDef.original_model_colors = new int[] { 127 };
            itemDef.modified_model_colors = new int[] { 960 };
			break;
        case 15248:
            itemDef.copy(lookup(20101));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Ankou gloves";
            itemDef.description = "Ankou gloves.";
            itemDef.original_model_colors = new int[] { 127 };
            itemDef.modified_model_colors = new int[] { 960 };
			break;
        case 15249:
            itemDef.copy(lookup(20107));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Ankou socks";
            itemDef.description = "Ankou socks.";
            itemDef.original_model_colors = new int[] { 127 };
            itemDef.modified_model_colors = new int[] { 960 };
			break;
        case 15250:
            itemDef.copy(lookup(13652));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.name = "Gilded claws";
            itemDef.description = "A gilded claws.";
            itemDef.original_model_colors = new int[] { 7114, 7114, 7114, 7114 };
            itemDef.modified_model_colors = new int[] { 929, 922, 918, 914};
			break;
        case 15251:
            itemDef.copy(lookup(9470));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Gnome scarf";
            itemDef.description = "A gnome scarf.";
            itemDef.original_model_colors = new int[] { 725, 725, 725 };
            itemDef.modified_model_colors = new int[] { 119, 103, 127 };
			break;
        case 15252:
            itemDef.copy(lookup(9470));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Gnome scarf";
            itemDef.description = "A gnome scarf.";
            itemDef.original_model_colors = new int[] { 22250, 22250, 22250 };
            itemDef.modified_model_colors = new int[] { 119, 103, 127 };
			break;
        case 15253:
            itemDef.copy(lookup(9470));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Gnome scarf";
            itemDef.description = "A gnome scarf.";
            itemDef.original_model_colors = new int[] { 23970, 23970, 23970 };
            itemDef.modified_model_colors = new int[] { 119, 103, 127 };
			break;
        case 15254:
            itemDef.copy(lookup(19941));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Gilded casket";
            itemDef.description = "A gilded casket.";
            itemDef.original_model_colors = new int[] { 7097, 7114, 7114, 7097, 43968, 7114, 7097, 7114, 7114, 7114, 7097 };
            itemDef.modified_model_colors = new int[] { 43059, 7062, 7952, 43053, 11195, 7064, 43063, 6932, 7068, 7054, 43045 };
			break;
        case 15255:
            itemDef.copy(lookup(1050));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Caramel santa hat";
            itemDef.description = "A santa's hat.";
            itemDef.original_model_colors = new int[] { 660, 0 };
            itemDef.modified_model_colors = new int[] { 933, 10351 };
			break;
        case 15256:
            itemDef.copy(lookup(1050));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Cream santa hat";
            itemDef.description = "A santa's hat.";
            itemDef.original_model_colors = new int[] { 9583, 675 };
            itemDef.modified_model_colors = new int[] { 933, 10351 };
			break;
        case 15257:
            itemDef.copy(lookup(1050));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Violeta's santa hat";
            itemDef.description = "A santa's hat.";
            itemDef.original_model_colors = new int[] { 49500, 49863 };
            itemDef.modified_model_colors = new int[] { 933, 10351 };
			break;
        case 15258:
            itemDef.copy(lookup(1050));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Chocolate santa hat";
            itemDef.description = "A santa's hat.";
            itemDef.original_model_colors = new int[] { 5652, 27831 };
            itemDef.modified_model_colors = new int[] { 933, 10351 };
			break;
        case 15259:
            itemDef.copy(lookup(1050));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Strawberry santa hat";
            itemDef.description = "A santa's hat.";
            itemDef.original_model_colors = new int[] { 3700, 0 };
            itemDef.modified_model_colors = new int[] { 933, 10351 };
			break;
        case 15260:
            itemDef.copy(lookup(1050));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Vanilla santa hat";
            itemDef.description = "A santa's hat.";
            itemDef.original_model_colors = new int[] { 5738, 7833 };
            itemDef.modified_model_colors = new int[] { 933, 10351 };
			break;
        case 15261:
            itemDef.copy(lookup(536));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Bury";
            itemDef.name = "Gilded bones (1kg)";
            itemDef.description = "Bones made from gold.";
            itemDef.original_model_colors = new int[] { 7114 };
            itemDef.modified_model_colors = new int[] { 127 };
            itemDef.noted_item_id = -1;
            itemDef.unnoted_item_id = 15262;
			break;
        case 15262:
            itemDef.copy(lookup(537));
            itemDef.actions = new String[5];
            itemDef.name = "Gilded bones (1kg)";
            itemDef.original_model_colors = new int[] { 7114 };
            itemDef.modified_model_colors = new int[] { 127 };
            itemDef.noted_item_id = 799;
            itemDef.unnoted_item_id = 15261;
            break;
        case 15263:
            itemDef.copy(lookup(6759));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "Justiciar armour set";
            itemDef.description = "A Justiciar armour set.";
			break;
        case 15264:
            itemDef.copy(lookup(405));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "Super rings set";
            itemDef.description = "A set containing all the superior rings.";
			break;
        case 15265:
            itemDef.copy(lookup(405));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "Super boots set";
            itemDef.description = "A set containing primordial, pegasian, and eternal boots.";
			break;
        case 15266:
            itemDef.copy(lookup(405));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "Spirit shield set";
            itemDef.description = "A set containing all the three spirit shields.";
			break;
        case 15267:
            itemDef.copy(lookup(13346));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "Coins mystery box";
            itemDef.description = "A present given by luck to those who login daily.";
			break;
        case 15268:
            itemDef.copy(lookup(1053));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Gum h'ween mask";
            itemDef.description = "A gum h'ween mask.";
            itemDef.original_model_colors = new int[] { 129770 };
            itemDef.modified_model_colors = new int[] { 926 };
			break;
        case 15269:
            itemDef.copy(lookup(1053));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Teal h'ween mask";
            itemDef.description = "A teal h'ween mask.";
            itemDef.original_model_colors = new int[] { 12821 };
            itemDef.modified_model_colors = new int[] { 926 };
			break;
        case 15270:
            itemDef.copy(lookup(1053));
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wear";
            itemDef.name = "Gilded h'ween mask";
            itemDef.description = "A gilded h'ween mask.";
            itemDef.original_model_colors = new int[] { 7114 };
            itemDef.modified_model_colors = new int[] { 926 };
			break;
        case 15272: // 15271 is colorful maxhood
            itemDef.copy(lookup(4707));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Read";
            itemDef.name = "Hunter tome (10)";
            itemDef.stackable = false;
            break;
        case 15273:
            itemDef.copy(lookup(4707));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Read";
            itemDef.name = "Hunter tome (1)";
            itemDef.stackable = false;
            break;
        case 6640:
            itemDef.name = "Dragon's tail";
            itemDef.description = "An extraordinary looking dragon's tail.";
			break;
        case 1004:
            itemDef.name = "Coins";
			break;
        case 12955:
            itemDef.name = "Starter pack";
            break;
        case 13302:
            itemDef.name = "Crate key";
            itemDef.original_model_colors = new int[] { 8128 };
            itemDef.modified_model_colors = new int[] { 5231 };
            break;
        case 5022:
            itemDef.name = "Spin ticket";
            itemDef.original_model_colors = new int[] { 100 };
            itemDef.modified_model_colors = new int[] { 10562 };
            // [10458,0,0],"newModelColor":[10562
            break;
        case 5509:
        case 5510:
        case 5512:
        case 5514:
            case 26784:
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Fill";
            itemDef.actions[1] = null;
            itemDef.actions[2] = "Empty";
            itemDef.actions[3] = "Check";
            break;
        case 12791:
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";	
            itemDef.actions[1] = null;
            itemDef.actions[2] = null;
            itemDef.actions[3] = null;
            itemDef.actions[4] = "Destroy";
        	break;
        //case 22547:
        //case 22552:
        //case 22542:
        case 6788:
        case 6789:
            itemDef.actions = new String[5];
            itemDef.actions[0] = null;
            itemDef.actions[1] = null;
            break;
        case 13173:
        case 13175:
        case 13038:
        case 13036:
        case 21279:
        case 12881:
        case 12875:
        case 12873:
        case 12883:
        case 12879:
        case 12877:
        case 21049:
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            break;
        case 5516:
        	itemDef.name = "Blood talisman";
        	break;
        case 13190:
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Bind";
            itemDef.name = "$50 Member's rank bond";
            itemDef.original_model_colors = new int[] { 689484, 689484, 689484, 689484, -32707, 689484, -32715, 9164, 7087, 689484, -32690, 689484, 11092, 8117, 11224, -32698, 689484, 7997 };
            itemDef.modified_model_colors = new int[] { 20416, 9152, 22464, 22305, -32707, 22181, -32715, 9164, 7087, 22449, -32690, 22451, 11092, 8117, 11224, -32698, 21435, 7997 };
            break;
        case 11738:
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Open";
            itemDef.name = "Skilling supplies";
            break;
        case 7774:
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Redeem";
            itemDef.name = "5 Yell credits";
            break;
        case 7780:
            itemDef.name = "Fishing tome (1)";
            break;
            case 7783:
                itemDef.name = "Agility tome (1)";
                break;
            case 7789:
                itemDef.name = "Slayer tome (1)";
                break;
            case 7788:
                itemDef.name = "Slayer tome (10)";
                break;
            case 7798:
                itemDef.name = "Woodcutting tome (1)";
                break;
            case 7779:
                itemDef.name = "Fishing tome (10)";
                break;
            case 7782:
                itemDef.name = "Agility tome (10)";
                break;
            case 7791:
                itemDef.name = "Mining tome (10)";
                break;
            case 7793:
                itemDef.name = "Mining tome (1)";
                break;
            case 7796:
                itemDef.name = "Firemaking tome (10)";
                break;
            case 7795:
                itemDef.name = "Firemaking tome (1)";
                break;
            case 7797:
                itemDef.name = "Woodcutting tome (10)";
                break;
            case 7785:
                itemDef.name = "Thieving tome (10)";
                break;
            case 7786:
                itemDef.name = "Thieving tome (5)";
                break;
            case 7787:
                itemDef.name = "Thieving tome (1)";
                break;
        case 10944:
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Redeem";
            itemDef.name = "Dicer's rank";
            break;
        case 7775:
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Redeem";
            itemDef.name = "10 Yell credits";
            break;
        case 7776:
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Redeem";
            itemDef.name = "25 Yell credits";
            break;
        case 9013:
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.actions[2] = "Teleport";
            itemDef.actions[3] = "Invoke";
            itemDef.actions[4] = "Destroy";
            break;

/*        case 20368:
        case 20370:
        case 20372:
        case 20374:
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.actions[4] = "Destroy";
            break;*/
        /*case 22322:
        case 22325:
        case 22545:
        case 22550:
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            break;*/
        /*case 22555:
        case 22323:
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.actions[2] = "Check";
            break;*/
        case 8013:
            itemDef.name = "Home teleport";
            break;
        case 2528:
            itemDef.name = "Reset lamp";
            break;
        case 2543:
            itemDef.copy(lookup(21034));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Read";
            itemDef.name = "Rigour scroll";
            itemDef.stackable = false;
            break;
        case 2542:
            itemDef.copy(lookup(21047));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Read";
            itemDef.name = "Preserve scroll";
            itemDef.stackable = false;
            break;
        case 2544:
            itemDef.copy(lookup(21079));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Read";
            itemDef.name = "Augury scroll";
            itemDef.stackable = false;
            break;
        case 2545:
            itemDef.copy(lookup(12846));
            itemDef.actions = new String[5];
            itemDef.actions[0] = "Read";
            itemDef.name = "Target-teleport scroll";
            itemDef.stackable = false;
            break;
        case 12926:
            itemDef.actions = new String[5];
            itemDef.actions[1] = "Wield";
            itemDef.actions[2] = "Check";
            itemDef.actions[3] = "Empty";
            break;
        case 26394: // Bandosian components
            itemDef.stackable = true;
        break;
        /*case 11863:
            itemDef.name = "<col=DC143C>R<col=80080>a<col=FFF000>i<col=00FF00>n<col=FF69B4>b<col=0000CD>o<col=FFFFFF>w @or1@partyhat";
			break;*/
        }
        ItemDefinition2.load(itemId, itemDef);
        ItemDefinition3.load(itemId, itemDef);
        ItemDefinition_cached.put(itemDef, itemId);
        return itemDef;
    }

    private static final boolean PRINT_MODEL_COLORS = true;
 
    public static Sprite getSprite(int itemId, int stackSize, int outlineColor) {
        if (outlineColor == 0) {
            Sprite sprite = (Sprite) sprites.get(itemId);
            if (sprite != null && sprite.maxHeight != stackSize && sprite.maxHeight != -1) {

                sprite.remove();
                sprite = null;
            }
            if (sprite != null)
                return sprite;
        }
        ItemDefinition itemDef = lookup(itemId);
        if (itemDef.stack_variant_id == null)
            stackSize = -1;
        if (stackSize > 1) {
            int stack_item_id = -1;
            for (int j1 = 0; j1 < 10; j1++)
                if (stackSize >= itemDef.stack_variant_size[j1] && itemDef.stack_variant_size[j1] != 0)
                    stack_item_id = itemDef.stack_variant_id[j1];

            if (stack_item_id != -1)
                itemDef = lookup(stack_item_id);
        }
        Model model = itemDef.getModel(1);
        if (model == null) {
            return null;
        }
        if (PRINT_MODEL_COLORS)
            System.out.println(itemId + " - " + itemDef.name + " inv colors: " + Arrays.toString(model.getColors().toArray()));
        Sprite sprite = null;
        if (itemDef.noted_item_id != -1) {
            sprite = getSprite(itemDef.unnoted_item_id, 10, -1);
            if (sprite == null)
                return null;
        }
        Sprite enabledSprite = new Sprite(32, 32);
        int centerX = Rasterizer3D.originViewX;
        int centerY = Rasterizer3D.originViewY;
        int lineOffsets[] = Rasterizer3D.scanOffsets;
        int pixels[] = Rasterizer2D.Rasterizer2D_pixels;
        float depth[] = Rasterizer2D.depthBuffer;
        int width = Rasterizer2D.Rasterizer2D_width;
        int height = Rasterizer2D.Rasterizer2D_height;
        int vp_left = Rasterizer2D.Rasterizer2D_xClipStart;
        int vp_right = Rasterizer2D.Rasterizer2D_xClipEnd;
        int vp_top = Rasterizer2D.Rasterizer2D_yClipStart;
        int vp_bottom = Rasterizer2D.Rasterizer2D_yClipEnd;
        Rasterizer3D.notTextured = false;
        Rasterizer2D.Rasterizer2D_replace(enabledSprite.myPixels, new float[32*32], 32, 32);
        Rasterizer2D.drawBox(0, 0, 32, 32, 0);
        Rasterizer3D.useViewport();
        int k3 = itemDef.model_zoom;
        if (outlineColor == -1)
            k3 = (int) ((double) k3 * 1.5D);
        if (outlineColor > 0)
            k3 = (int) ((double) k3 * 1.04D);
        int l3 = Rasterizer3D.Rasterizer3D_sine[itemDef.rotation_y] * k3 >> 16;
        int i4 = Rasterizer3D.Rasterizer3D_cosine[itemDef.rotation_y] * k3 >> 16;
        model.calculateBoundsCylinder();
        model.method482(itemDef.rotation_x, itemDef.rotation_z, itemDef.rotation_y, itemDef.translate_x,
                l3 + model.height / 2 + itemDef.translate_yz, i4 + itemDef.translate_yz);

        enabledSprite.outline(1);
        if (outlineColor > 0) {
            enabledSprite.outline(16777215);
        }
        if (outlineColor == 0) {
            enabledSprite.shadow(3153952);
        }

        Rasterizer2D.Rasterizer2D_replace(enabledSprite.myPixels, new float[32*32], 32, 32);

        if (itemDef.noted_item_id != -1) {
            int old_w = sprite.maxWidth;
            int old_h = sprite.maxHeight;
            sprite.maxWidth = 32;
            sprite.maxHeight = 32;
            sprite.drawSprite(0, 0);
            sprite.maxWidth = old_w;
            sprite.maxHeight = old_h;
        }
        if (outlineColor == 0)
            sprites.put(enabledSprite, itemId);
        Rasterizer2D.Rasterizer2D_replace(pixels, depth, width, height);
        Rasterizer2D.Rasterizer2D_setClip(vp_left, vp_bottom, vp_right, vp_top);
        Rasterizer3D.originViewX = centerX;
        Rasterizer3D.originViewY = centerY;
        Rasterizer3D.scanOffsets = lineOffsets;
        Rasterizer3D.notTextured = true;
        if (itemDef.stackable)
            enabledSprite.maxWidth = 33;
        else
            enabledSprite.maxWidth = 32;
        enabledSprite.maxHeight = stackSize;
        return enabledSprite;
    }

    public boolean isDialogueModelCached(int var1) {
        int var2 = equipped_model_male_dialogue_1;
        int var3 = equipped_model_male_dialogue_2;
        if (var1 == 1) {
            var2 = equipped_model_female_dialogue_1;
            var3 = equipped_model_female_dialogue_2;
        }

        if (var2 == -1) {
            return true;
        } else {
            boolean var4 = true;
            if (!Js5.models.tryLoadRecord(var2, 0)) {
                var4 = false;
            }

            if (var3 != -1 && !Js5.models.tryLoadRecord(var3, 0)) {
                var4 = false;
            }

            return var4;
        }
    }

    public final ModelData method3921(int var1) {
        int var2 = equipped_model_male_dialogue_1;
        int var3 = equipped_model_male_dialogue_2;
        if (var1 == 1) {
            var2 = equipped_model_female_dialogue_1;
            var3 = equipped_model_female_dialogue_2;
        }

        if (var2 == -1) {
            return null;
        } else {
            ModelData var4 = ModelData.ModelData_get(Js5.models, var2, 0);
            if (var3 != -1) {
                ModelData var5 = ModelData.ModelData_get(Js5.models, var3, 0);
                ModelData[] var6 = new ModelData[]{var4, var5};
                var4 = new ModelData(var6, 2);
            }

            int var7;
            if (modified_model_colors != null) {
                for(var7 = 0; var7 < modified_model_colors.length; ++var7) {
                    var4.recolor((short) modified_model_colors[var7], (short) original_model_colors[var7]);
                }
            }

            if (modifiedTexture != null) {
                for(var7 = 0; var7 < modifiedTexture.length; ++var7) {
                    var4.retexture(modifiedTexture[var7], originalTexture[var7]);
                }
            }

            return var4;
        }
    }
    public final boolean method3918(int var1) {
        int var2 = this.equipped_model_male_1;
        int var3 = this.equipped_model_male_2;
        int var4 = this.equipped_model_male_3;
        if (var1 == 1) {
            var2 = this.equipped_model_female_1;
            var3 = this.equipped_model_female_2;
            var4 = this.equipped_model_female_3;
        }

        if (var2 == -1) {
            return true;
        } else {
            boolean var5 = true;
            if (!OsCache.indexCache7.tryLoadRecord(var2, 0)) {
                var5 = false;
            }

            if (var3 != -1 && !OsCache.indexCache7.tryLoadRecord(var3, 0)) {
                var5 = false;
            }

            if (var4 != -1 && !OsCache.indexCache7.tryLoadRecord(var4, 0)) {
                var5 = false;
            }

            return var5;
        }
    }

    public ModelData method3919(int var1) {
        int var2 = this.equipped_model_male_1;
        int var3 = this.equipped_model_male_2;
        int var4 = this.equipped_model_male_3;
        if (var1 == 1) {
            var2 = this.equipped_model_female_1;
            var3 = this.equipped_model_female_2;
            var4 = this.equipped_model_female_3;
        }

        if (var2 == -1) {
            return null;
        } else {
            ModelData var5 = ModelData.ModelData_get(OsCache.indexCache7, var2, 0);
            if (var3 != -1) {
                ModelData var6 = ModelData.ModelData_get(OsCache.indexCache7, var3, 0);
                if (var4 != -1) {
                    ModelData var7 = ModelData.ModelData_get(OsCache.indexCache7, var4, 0);
                    ModelData[] var8 = new ModelData[]{var5, var6, var7};
                    var5 = new ModelData(var8, 3);
                } else {
                    ModelData[] var10 = new ModelData[]{var5, var6};
                    var5 = new ModelData(var10, 2);
                }
            }

            if (var1 == 0 && equipped_model_male_translation_y != 0) {
                var5.changeOffset(0, equipped_model_male_translation_y, 0);
            }

            if (var1 == 1 && equipped_model_female_translation_y != 0) {
                var5.changeOffset(0, equipped_model_female_translation_y, 0);
            }

            /*ModelData overrideModelData = ItemColorCustomizer.getColorfulItemData(var5, ClientCompanion.dummyPlayer, id, var1);
            if (overrideModelData != null) {
                var5 = overrideModelData;
            } else {*/
                int var9;
                if (modified_model_colors != null) {
                    for(var9 = 0; var9 < modified_model_colors.length; ++var9) {
                        var5.recolor((short) modified_model_colors[var9], (short) original_model_colors[var9]);
                    }
                }

                if (modifiedTexture != null) {
                    for(var9 = 0; var9 < modifiedTexture.length; ++var9) {
                        var5.retexture(modifiedTexture[var9], originalTexture[var9]);
                    }
                }
            //}

            return var5;
        }
    }

    private void setDefaults() {
        inventory_model = 0;
        name = null;
        description = null;
        modified_model_colors = null;
        original_model_colors = null;
        model_zoom = 2000;
        rotation_y = 0;
        rotation_x = 0;
        rotation_z = 0;
        translate_x = 0;
        translate_yz = 0;
        stackable = false;
        value = 1;
        is_members_only = false;
        groundActions = null;
        actions = null;
        equipped_model_male_1 = -1;
        equipped_model_male_2 = -1;
        equipped_model_male_translation_y = 0;
        equipped_model_female_1 = -1;
        equipped_model_female_2 = -1;
        equipped_model_female_translation_y = 0;
        equipped_model_male_3 = -1;
        equipped_model_female_3 = -1;
        equipped_model_male_dialogue_1 = -1;
        equipped_model_male_dialogue_2 = -1;
        equipped_model_female_dialogue_1 = -1;
        equipped_model_female_dialogue_2 = -1;
        stack_variant_id = null;
        stack_variant_size = null;
        unnoted_item_id = -1;
        noted_item_id = -1;
        model_scale_x = 128;
        model_scale_y = 128;
        model_scale_z = 128;
        light_intensity = 0;
        light_mag = 0;
        team = 0;
    }

    public void copy(ItemDefinition copy) {
        rotation_x = copy.rotation_x;
        rotation_y = copy.rotation_y;
        rotation_z = copy.rotation_z;
        model_scale_x = copy.model_scale_x;
        model_scale_y = copy.model_scale_y;
        model_scale_z = copy.model_scale_z;
        model_zoom = copy.model_zoom;
        translate_x = copy.translate_x;
        translate_yz = copy.translate_yz;
        inventory_model = copy.inventory_model;
        stackable = copy.stackable;
        //modified_model_colors = copy.modified_model_colors;
        //original_model_colors = copy.original_model_colors;
        //modifiedTexture = copy.modifiedTexture;
        //originalTexture = copy.originalTexture;
        if (copy.modified_model_colors != null)
            modified_model_colors = copy.modified_model_colors.clone();
        if (copy.original_model_colors != null)
            original_model_colors = copy.original_model_colors.clone();
        if (copy.modifiedTexture != null)
            modifiedTexture = copy.modifiedTexture.clone();
        if (copy.originalTexture != null)
            originalTexture = copy.originalTexture.clone();
        equipped_model_female_1 = copy.equipped_model_female_1;
        equipped_model_female_2 = copy.equipped_model_female_2;
        equipped_model_female_3 = copy.equipped_model_female_3;
        equipped_model_male_1 = copy.equipped_model_male_1;
        equipped_model_male_2 = copy.equipped_model_male_2;
        equipped_model_male_3 = copy.equipped_model_male_3;
        equipped_model_male_dialogue_1 = copy.equipped_model_male_dialogue_1;
        equipped_model_male_dialogue_2 = copy.equipped_model_male_dialogue_2;
        equipped_model_female_dialogue_1 = copy.equipped_model_female_dialogue_1;
        equipped_model_female_dialogue_2 = copy.equipped_model_female_dialogue_2;
        //actions = copy.actions;
        if (copy.actions != null)
            actions = copy.actions.clone();
        if (copy.stack_variant_id != null)
            stack_variant_id = copy.stack_variant_id.clone();
        if (copy.stack_variant_size != null)
            stack_variant_size = copy.stack_variant_size.clone();
        if (copy.groundActions != null)
            groundActions = copy.groundActions.clone();
    }

    private void toNote() {
        ItemDefinition itemDef = lookup(noted_item_id);
        inventory_model = itemDef.inventory_model;
        model_zoom = itemDef.model_zoom;
        rotation_y = itemDef.rotation_y;
        rotation_x = itemDef.rotation_x;

        rotation_z = itemDef.rotation_z;
        translate_x = itemDef.translate_x;
        translate_yz = itemDef.translate_yz;
        modified_model_colors = itemDef.modified_model_colors;
        original_model_colors = itemDef.original_model_colors;
        modifiedTexture = itemDef.modifiedTexture;
        originalTexture = itemDef.originalTexture;
        ItemDefinition itemDef_1 = lookup(unnoted_item_id);
        name = itemDef_1.name;
        is_members_only = itemDef_1.is_members_only;
        value = itemDef_1.value;
        String s = "a";
        if (name != null)   {
            char c = name.charAt(0);
            if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U')
                s = "an";
        }
        description = ("Swap this note at any bank for " + s + " " + itemDef_1.name + ".");
        stackable = true;
    }

    public final Model getModel(int var1) {
        if (this.stack_variant_id != null && var1 > 1) {
            int var2 = -1;

            for(int var3 = 0; var3 < 10; ++var3) {
                if (var1 >= this.stack_variant_size[var3] && this.stack_variant_size[var3] != 0) {
                    var2 = this.stack_variant_id[var3];
                }
            }

            if (var2 != -1) {
                return lookup(var2).getModel(1);
            }
        }

        Model var5 = (Model)ItemDefinition_cachedModels.get((long)this.id);
        if (var5 != null) {
            return var5;
        } else {
            ModelData var6 = ModelData.ModelData_get(OsCache.indexCache7, inventory_model, 0);
            if (var6 == null) {
                return null;
            } else {
                if (model_scale_x != 128 || model_scale_y != 128 || model_scale_z != 128) {
                    var6.resize(model_scale_x, model_scale_y, model_scale_z);
                }

                int var4;
                if (modified_model_colors != null) {
                    for(var4 = 0; var4 < modified_model_colors.length; ++var4) {
                        var6.recolor((short) modified_model_colors[var4], (short) original_model_colors[var4]);
                    }
                }
                if (modifiedTexture != null) {
                    for(var4 = 0; var4 < modifiedTexture.length; ++var4) {
                        var6.retexture(modifiedTexture[var4], originalTexture[var4]);
                    }
                }

                var6 = ItemColorCustomizer.getColorfulItemData(var6, Client.localPlayer, id, Client.localPlayer.getGender());

                var5 = var6.toModel(light_intensity + 64, light_mag + 768, -50, -10, -50);
                var5.isSingleTile = true;
                ItemDefinition_cachedModels.put(var5, (long)this.id);
                return var5;
            }
        }
    }

    public final ModelData getModelData(int var1) {
        int var3;
        if (stack_variant_id != null && var1 > 1) {
            int var2 = -1;

            for(var3 = 0; var3 < 10; ++var3) {
                if (var1 >= stack_variant_size[var3] && stack_variant_size[var3] != 0) {
                    var2 = stack_variant_id[var3];
                }
            }

            if (var2 != -1) {
                return lookup(var2).getModelData(1);
            }
        }

        ModelData var4 = ModelData.ModelData_get(OsCache.indexCache7, inventory_model, 0);
        if (var4 == null) {
            return null;
        } else {
            if (model_scale_x != 128 || model_scale_y != 128 || model_scale_z != 128) {
                var4.resize(model_scale_x, model_scale_y, model_scale_z);
            }

            if (modified_model_colors != null) {
                for(var3 = 0; var3 < modified_model_colors.length; ++var3) {
                    var4.recolor((short) modified_model_colors[var3], (short) original_model_colors[var3]);
                }
            }
            if (modifiedTexture != null) {
                for(var3 = 0; var3 < modifiedTexture.length; ++var3) {
                    var4.retexture(modifiedTexture[var3], originalTexture[var3]);
                }
            }

            return var4;
        }
    }
 
    public void readValues(Buffer buffer) {
        do {
            int opCode = buffer.readUnsignedByte();
            if (opCode == 0)
                return;
            if (opCode == 1)
                inventory_model = buffer.readUShort();
            else if (opCode == 2)
                name = buffer.readStringCp1252NullTerminated();
            else if (opCode == 4)
                model_zoom = buffer.readUShort();
            else if (opCode == 5)
                rotation_y = Math.min(2047, buffer.readUShort());
            else if (opCode == 6) {
                rotation_x = Math.min(2047, buffer.readUShort());
            } else if (opCode == 7) {
                translate_x = buffer.readUShort();
                if (translate_x > 32767)
                    translate_x -= 0x10000;
            } else if (opCode == 8) {
                translate_yz = buffer.readUShort();
                if (translate_yz > 32767)
                    translate_yz -= 0x10000;
            } else if (opCode == 9)
                buffer.readStringCp1252NullTerminated();
            else if (opCode == 11)
                stackable = true;
            else if (opCode == 12)
                value = buffer.readInt();
            else if (opCode == 13)
                buffer.readUnsignedByte();
            else if (opCode == 14)
                buffer.readUnsignedByte();
            else if (opCode == 16)
                is_members_only = true;
            else if (opCode == 23) {
                equipped_model_male_1 = buffer.readUShort();
                equipped_model_male_translation_y = buffer.readSignedByte();
            } else if (opCode == 24)
                equipped_model_male_2 = buffer.readUShort();
            else if (opCode == 25) {
                equipped_model_female_1 = buffer.readUShort();
                equipped_model_female_translation_y = buffer.readSignedByte();
            } else if (opCode == 26)
                equipped_model_female_2 = buffer.readUShort();
            else if (opCode == 27)
                buffer.readUnsignedByte();
            else if (opCode >= 30 && opCode < 35) {
                if (groundActions == null)
                    groundActions = new String[5];
                groundActions[opCode - 30] = buffer.readStringCp1252NullTerminated();
                if (groundActions[opCode - 30].equalsIgnoreCase("hidden"))
                    groundActions[opCode - 30] = null;
            } else if (opCode >= 35 && opCode < 40) {
                if (actions == null)
                    actions = new String[5];
                actions[opCode - 35] = buffer.readStringCp1252NullTerminated();
            } else if (opCode == 40) {
                int j = buffer.readUnsignedByte();
                modified_model_colors = new int[j];
                original_model_colors = new int[j];
                for (int k = 0; k < j; k++) {
                    modified_model_colors[k] = buffer.readUShort();
                    original_model_colors[k] = buffer.readUShort();
                }
            } else if (opCode == 41) {
                int len = buffer.readUnsignedByte();
                modifiedTexture = new short[len];
                originalTexture = new short[len];
                for (int i = 0; i < len; i++) {
                    modifiedTexture[i] = (short) buffer.readUShort();
                    originalTexture[i] = (short) buffer.readUShort();
                }
            } else if (opCode == 42) {
                int shiftClickIndex = buffer.readUnsignedByte();     
            } else if (opCode == 65) {
                boolean searchable = true;
            } else if (opCode == 75) {
                buffer.readShort();
            } else if (opCode == 78)
                equipped_model_male_3 = buffer.readUShort();
            else if (opCode == 79)
                equipped_model_female_3 = buffer.readUShort();
            else if (opCode == 90)
                equipped_model_male_dialogue_1 = buffer.readUShort();
            else if (opCode == 91)
                equipped_model_female_dialogue_1 = buffer.readUShort();
            else if (opCode == 92)
                equipped_model_male_dialogue_2 = buffer.readUShort();
            else if (opCode == 93)
                equipped_model_female_dialogue_2 = buffer.readUShort();
            else if (opCode == 94)
                category = buffer.readUShort();
            else if (opCode == 95)
                rotation_z = Math.min(2047, buffer.readUShort());
            else if (opCode == 97)
                unnoted_item_id = buffer.readUShort();
            else if (opCode == 98)
                noted_item_id = buffer.readUShort();
            else if (opCode >= 100 && opCode < 110) {
 
                if (stack_variant_id == null) {
                    stack_variant_id = new int[10];
                    stack_variant_size = new int[10];
                }
                stack_variant_id[opCode - 100] = buffer.readUShort();
                stack_variant_size[opCode - 100] = buffer.readUShort();
 
                /*
                 * int length = buffer.readUnsignedByte(); stack_variant_id =
                 * new int [length]; stack_variant_size = new int[length]; for
                 * (int i2 = 0; i2< length; i2++) { stack_variant_id[i2] =
                 * buffer.readUShort(); stack_variant_size[i2] =
                 * buffer.readUShort(); }
                 */
            } else if (opCode == 110)
                model_scale_x = buffer.readUShort();
            else if (opCode == 111)
                model_scale_y = buffer.readUShort();
            else if (opCode == 112)
                model_scale_z = buffer.readUShort();
            else if (opCode == 113)
                light_intensity = buffer.readSignedByte();
            else if (opCode == 114)
                light_mag = buffer.readSignedByte() * 5;
            else if (opCode == 115)
                team = buffer.readUnsignedByte();
	        else if (opCode == 139) {
                unnoted_item_id = buffer.readUShort();
	        } else if (opCode == 140) {
                noted_item_id = buffer.readUShort();
	        } else if (opCode == 148) {
	        	int placeholder_id  = buffer.readUShort(); // placeholder id
	        } else if (opCode == 149) {
                int placeholder_template_id = buffer.readUShort(); // placeholder template
            } else if (opCode == 249) {
                this.params = OSCollections.readStringIntParameters(buffer, this.params);
            }
        } while (true);
    }

    IterableNodeHashTable params;
    int category;

    public static String itemModels(int itemID) {
        int inv = lookup(itemID).inventory_model;
        int male = lookup(itemID).equipped_model_male_1;
        int female = lookup(itemID).equipped_model_female_1;
        String name = lookup(itemID).name;
        return "<col=225>"+name+"</col> (<col=800000000>"+itemID+"</col>) - [inv: <col=800000000>"+inv+"</col>] - [male: <col=800000000>"+male+"</col>] - [female: <col=800000000>"+female+"</col>]";
    }

    private void addCustomItem(int base, int inv, int equip, String name) {
        copy(lookup(base));
        inventory_model = inv;
        equipped_model_male_1 = equip;
        equipped_model_female_1 = equip;
        this.name = name;
    }

    public void addCustomTexture(int original, int modified) {
        modifiedTexture = new short[] {(short) original};
        originalTexture = new short[] {(short) modified};
    }


    private void unNotedItem(int notedId) { // Input the main item id
        noted_item_id = -1;
        unnoted_item_id = notedId + 1;
    }
    private void notedItem(int unNotedId) { // Input the main item id
        noted_item_id = 799;
        unnoted_item_id = unNotedId - 1;
    }

}