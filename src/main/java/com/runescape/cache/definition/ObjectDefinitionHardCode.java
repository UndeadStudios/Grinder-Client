package com.runescape.cache.definition;

import com.grinder.Configuration;
import com.runescape.cache.OsCache;
import net.runelite.cache.util.IDClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * TODO: should be packed in cache
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 26/02/2020
 */
public class ObjectDefinitionHardCode {

    public static final int[] OBELISK_IDS = {14829, 14830, 14827, 14828, 14826, 14831};

    public static final HashMap<Integer, Integer> idMappings = new HashMap<>();

    static {
        idMappings.put(NullObjectID.NULL_25913, NullObjectID.NULL_25913);
        idMappings.put(NullObjectID.NULL_25916, NullObjectID.NULL_15553);
        idMappings.put(NullObjectID.NULL_25926, NullObjectID.NULL_15553);
        idMappings.put(NullObjectID.NULL_27253, ObjectID.BANK_BOOTH_24);
        idMappings.put(NullObjectID.NULL_27264, ObjectID.BANK_BOOTH_26);
        idMappings.put(NullObjectID.NULL_27291, ObjectID.BANK_BOOTH_18);
        idMappings.put(NullObjectID.NULL_7478, ObjectID.BANK_BOOTH_5);
        idMappings.put(ObjectID.TREE, ObjectID.TREE_2);
        idMappings.put(6944, ObjectID.BANK_BOOTH_2);
        idMappings.put(ObjectID.PORTAL_21, ObjectID.LUMBRIDGE_PORTAL);
    }

    public static void main(String[] args) throws IOException {
        OsCache.loadAndWait();
        System.out.println("DUMPING " + OSObjectDefinition.getObjectDefinitionCount());
        final File ids = Files.createDirectory(Paths.get("dump")).toFile();
        java(ids);
    }

    private static void java(File java) throws IOException {

        java.mkdirs();
        try (IDClass ids = IDClass.create(java, "ObjectID")) {
            try (IDClass nulls = IDClass.create(java, "NullObjectID")) {
                for (int objectId = 0; objectId < OSObjectDefinition.getObjectDefinitionCount(); objectId++) {

                    OSObjectDefinition def = OSObjectDefinition.lookup(objectId);

                    if (def == null)
                        continue;

                    if ("null".equals(def.name))
                        nulls.add(def.name, def.id);
                    else
                        ids.add(def.name, def.id);
                }

            }
        }
    }

    static OSObjectDefinition transform(OSObjectDefinition objectDef) {

        if (objectDef == null)
            return null;

        if (idMappings.containsKey(objectDef.id))
            return OSObjectDefinition.lookup(idMappings.get(objectDef.id));

        final int id = objectDef.id;

        if (Configuration.snowFloor) {
            if (id == 2092) return OSObjectDefinition.lookup(ObjectID.EVERGREEN); // big evergreen
            if (id == 2091) return OSObjectDefinition.lookup(ObjectID.EVERGREEN_2); // small evergreen
        }


        if (removeObjectName(objectDef.name)) {
            objectDef.modelIds = null;
            objectDef.interactiveState = 0;
            objectDef.isSolid = false;
            return objectDef;
        }

        if (id == 27060) {// xmas
            objectDef.name = "Christmas Tree";
            objectDef.actions[0] = "Take present";
            objectDef.modelIds = new int[]{1614, 39114};
        }

        for (int obelisk : OBELISK_IDS) {
            if (id == obelisk) {
                objectDef.actions = new String[]{"Activate", null, null, null, null};
                break;
            }
        }

        switch (id) {
            case ObjectID.EVERGREEN_3:
            case ObjectID.EVERGREEN_2:
                objectDef.setSize(2, 2);
                objectDef.setModelScale(100);
                break;
            case NullObjectID.NULL_26782:
                objectDef.modelIds = new int[]{28258};
                objectDef.setSize(3, 3);
                objectDef.setModelScale(196);
                objectDef.makeType10();
                break;
            case ObjectID.DEMON:
            case ObjectID.BOXING_MAT_2:
            case ObjectID.BOXING_RING:
            case ObjectID.BOXING_RING_3:
            case ObjectID.BOXING_RING_4:
            case ObjectID.ICON_OF_BOB:
            case ObjectID.HUGE_SPIDER:
                objectDef.removeActions();
                break;
            case ObjectID.FURNACE_17:
                objectDef.setActions("Smelt");
                break;
            case ObjectID.BANK_SAFE:
                objectDef.setActions("Deposit");
                break;
            case ObjectID.BANK_DEPOSIT_BOX:
//              objectDef.minimapFunction = 78;
                break;
            case ObjectID.STONE_CHEST_34586:
                objectDef.name = "Brimstone chest";
                break;
            case ObjectID.LEVER_C:
                objectDef.name = "Lever";
                objectDef.setActions("Pull");
                break;
            case ObjectID.BROKEN_RUNE_CASE:
                objectDef.name = "Ancient rune case";
                objectDef.removeActions();
                break;
            case ObjectID.GIFT_OF_PEACE:
                objectDef.name = "Gift of Christmas";
                break;
            case ObjectID.SNOWY_BIRD_HIDE:
                objectDef.name = "Snowy house";
                break;
            case NullObjectID.NULL_1758:
                objectDef.modelHeight = 250;
                objectDef.modelSizeX = 250;
                break;
            case ObjectID.RUNE_CASKET_3:
                objectDef.name = "Gilded chest";
                objectDef.removeActions();
                break;
            case ObjectID.CHEST_91:
                objectDef.name = "Vault chest";
                objectDef.removeActions();
                break;
            case ObjectID.ORNATE_REJUVENATION_POOL:
                objectDef.setActions("Restore-stats");
                break;
            case ObjectID.COMPOST_BIN_167:
                objectDef.name = "Infernal cape (broken)";
                break;
            case ObjectID.CRAFTING_STALL:
                objectDef.name = "Miscellaneous Stall";
                break;
            case ObjectID.LUMBRIDGE_PORTAL:
                objectDef.name = "Portal";
                objectDef.setActions("Enter");
//                objectDef.description = "No one even dares to get close to the queen's throne.";
                break;
            case ObjectID.PORTAL_36:
                objectDef.setActions("Enter");
//                objectDef.description = "It is said a mysterious living creature lives there and who ever went never came back.";
                break;
            case ObjectID.FANCY_JEWELLERY_BOX:
                objectDef.setActions("Teleport");
                break;
            case ObjectID.ANNAKARL_PORTAL:
                objectDef.name = "Wilderness Stalls";
                objectDef.setActions("Teleport");
                break;
            case ObjectID.BANK_PORTAL:
                objectDef.name = "Bank portal";
                break;
            case ObjectID.DITCH_PORTAL:
                objectDef.name = "Ditch portal";
                break;
            case ObjectID.INFORMATION:
                objectDef.name = "Information";
                objectDef.removeActions();
                break;
            case ObjectID.CLOSED_CHEST_3:
                objectDef.name = "Crystal Chest";
                break;
            case ObjectID.ALTAR_43:
            case ObjectID.ALTAR_45:
                objectDef.name = "@yel@Gilded Altar";
//                objectDef.description = "@yel@The altar is polished diorite, lighter and smoother than the basalt of the cave floor.";
                break;
            case ObjectID.BOAT_7:
                objectDef.name = "Broken boat";
                objectDef.removeActions();
                break;
            case ObjectID.CAVE_EXIT_16:
                objectDef.name = "Boss Entrance";
//                objectDef.minimapFunction = 12;
                break;
            case NullObjectID.NULL_29150:
                objectDef.name = "Magical altar";
                int sizeX = objectDef.sizeX;
                int sizeY = objectDef.sizeY;
                objectDef.copy(ObjectID.ALTAR);
                objectDef.setSize(sizeX, sizeY);
                objectDef.setActions("Venerate", "Switch-normal", "Switch-ancient", "Switch-lunar");
                objectDef.setModelIds(32160);
                break;
            case ObjectID.BANK_BOOTH_2:
            case ObjectID.BANK_BOOTH:
            case ObjectID.BAKERS_STALL:
            case ObjectID.BANK_BOOTH_4:
            case ObjectID.BANK_BOOTH_5:
            case NullObjectID.NULL_7410:
            case ObjectID.BANK_BOOTH_7:
            case ObjectID.BANK_BOOTH_8:
            case ObjectID.BANK_BOOTH_9:
            case ObjectID.BANK_BOOTH_10:
            case ObjectID.BANK_BOOTH_11:
            case ObjectID.BANK_BOOTH_12:
            case ObjectID.BANK_BOOTH_13:
            case ObjectID.BANK_BOOTH_14:
            case ObjectID.BANK_BOOTH_15:
            case ObjectID.BANK_BOOTH_16:
            case ObjectID.BANK_BOOTH_17:
            case ObjectID.BANK_BOOTH_18:
            case ObjectID.BANK_BOOTH_19:
            case ObjectID.BANK_BOOTH_20:
            case ObjectID.BANK_BOOTH_21:
            case ObjectID.BANK_BOOTH_22:
            case ObjectID.BANK_BOOTH_23:
            case ObjectID.BANK_BOOTH_24:
            case ObjectID.BANK_BOOTH_25:
            case ObjectID.BANK_BOOTH_26:
            case ObjectID.BANK_BOOTH_27:
            case ObjectID.BANK_BOOTH_28:
            case ObjectID.BANK_BOOTH_29:
            case ObjectID.BANK_BOOTH_30:
            case ObjectID.BANK_BOOTH_31:
            case ObjectID.BANK_BOOTH_32:
            case ObjectID.BANK_BOOTH_33:
            case ObjectID.BANK_BOOTH_34:
            case ObjectID.BANK_BOOTH_35:
            case ObjectID.BANK_BOOTH_36:
            case ObjectID.BANK_BOOTH_37:
            case ObjectID.BANK_BOOTH_38:
            case ObjectID.BANK_BOOTH_39:
            case ObjectID.BANK_BOOTH_40:
            case ObjectID.BANK_BOOTH_41:
            case ObjectID.BANK_BOOTH_42:
            case ObjectID.BANK_BOOTH_43:
            case ObjectID.BANK_BOOTH_44:
            case ObjectID.BANK_BOOTH_45:
            case ObjectID.BANK_BOOTH_10355:
            case ObjectID.BANK_CHEST:
            case ObjectID.BANK_CHEST_2:
            case ObjectID.BANK_CHEST_3:
            case ObjectID.BANK_CHEST_4:
            case ObjectID.BANK_CHEST_5:
            case ObjectID.OPEN_CHEST_8:
            case ObjectID.OPEN_CHEST_9:
            case ObjectID.OPEN_CHEST_10:
            case ObjectID.OPEN_CHEST_11:
            case ObjectID.OPEN_CHEST_12:
            case ObjectID.OPEN_CHEST_13:
            case ObjectID.OPEN_CHEST_14:
            case ObjectID.OPEN_CHEST_15:
            case ObjectID.OPEN_CHEST_16:
            case ObjectID.OPEN_CHEST_17:
            case ObjectID.OPEN_CHEST_18:
                objectDef.setActions("Bank");
                break;
            case ObjectID.MOUNTED_MAX_CAPE_2:
                objectDef.setActions("Admire", "Claim", "Gamble");
                break;
            case ObjectID.GATE_181:
                objectDef.setActions("Open", "Quick-Entry");
                break;
            case ObjectID.ANCIENT_ALTAR:
                objectDef.name = "Ancient altar";
                objectDef.setActions("Toggle-spells");
                break;
            case ObjectID.LUNAR_ALTAR:
                objectDef.name = "Lunar altar";
                objectDef.setActions("Toggle-spells");
                break;
            case NullObjectID.NULL_7147:
            case NullObjectID.NULL_7149:
                objectDef.name = "Gap";
                objectDef.setActions("Squeeze-Through");
                break;
            case ObjectID.KINGS_LADDER_2:
            case ObjectID.IRON_LADDER_7:
                objectDef.setActions("Climb-down");
                break;
            case NullObjectID.NULL_2164:
                objectDef.name = "Trawler Net";
                objectDef.interactiveState = 1;
                objectDef.setActions("Fix");
                break;
            case NullObjectID.NULL_1293:
                objectDef.name = "Spirit Tree";
                objectDef.interactiveState = 1;
                objectDef.setActions("Teleport");
                break;
            case NullObjectID.NULL_7144:
            case NullObjectID.NULL_7152:
                objectDef.name = "Tendrils";
                objectDef.interactiveState = 1;
                objectDef.setActions("Chop");
                break;
            case ObjectID.CHEST_90:
                objectDef.name = "Valueable Chest";
                objectDef.interactiveState = 1;
                objectDef.setActions("Search");
                break;
            case NullObjectID.NULL_2452:
                objectDef.name = "Mysterious Ruins";
                objectDef.interactiveState = 1;
                objectDef.setActions("Go Through");
                break;
            case NullObjectID.NULL_7153:
                objectDef.name = "Rock";
                objectDef.interactiveState = 1;
                objectDef.setActions("Mine");
                break;
            case ObjectID.MYSTERIOUS_RUINS_2:
            case ObjectID.MYSTERIOUS_RUINS_7:
            case NullObjectID.NULL_2454:
            case NullObjectID.NULL_2455:
            case NullObjectID.NULL_2456:
            case NullObjectID.NULL_2457:
            case NullObjectID.NULL_2460:
            case NullObjectID.NULL_2461:
                objectDef.name = "Mysterious Ruins";
                objectDef.interactiveState = 1;
                break;
            case NullObjectID.NULL_10638:
                objectDef.interactiveState = 1;
                break;
        }
        return objectDef;
    }

    private static boolean removeObjectName(String name) {
        name = name.toLowerCase();
        if (name.contains("web"))
            return !name.contains("rickety");
        return false;
    }

}
