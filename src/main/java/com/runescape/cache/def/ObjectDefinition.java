package com.runescape.cache.def;

import com.grinder.Configuration;
import com.runescape.Client;
import com.runescape.cache.Js5;
import com.runescape.cache.ModelData;
import com.runescape.cache.OsCache;
import com.runescape.cache.Varps;
import com.runescape.cache.anim.Animation;
import com.runescape.cache.definition.ObjectDefinitionHardCode;
import com.runescape.cache.definition.ObjectID;
import com.runescape.collection.DualNode;
import com.runescape.collection.EvictingDualNodeHashTable;
import com.runescape.collection.IterableNodeHashTable;
import com.runescape.collection.OSCollections;
import com.runescape.entity.Renderable;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;

public final class ObjectDefinition extends DualNode {

    public static final Model[] aModelArray741s = new Model[4];
    private final static boolean DUMP_DAT_IDX_TO_CACHE = false;
    public static boolean ObjectDefinition_isLowDetail;
    public static Buffer stream;
    public static int[] streamIndices;
    public static Client clientInstance;
    public static int cacheIndex;
    public static EvictingDualNodeHashTable ObjectDefinition_cached = new EvictingDualNodeHashTable(4096);
    public static EvictingDualNodeHashTable ObjectDefinition_cachedModelData = new EvictingDualNodeHashTable(500);
    public static EvictingDualNodeHashTable ObjectDefinition_cachedModels = new EvictingDualNodeHashTable(30);
    static EvictingDualNodeHashTable ObjectDefinition_cachedEntities = new EvictingDualNodeHashTable(30);
    public static ObjectDefinition[] cache;
    public static int TOTAL_OBJECTS;

    public boolean obstructsGround;
    public byte ambient;
    public int offsetX;
    public String name;
    public int modelSizeY;
    public int contrast;
    public int sizeX;
    public int offsetHeight;
    public int minimapFunction;
    public int[] recolorTo;
    public int modelSizeX;
    public int transformVarbit;
    public boolean isRotated;
    public int type;
    public boolean impenetrable;
    public int mapSceneId;
    public int[] transforms;
    public int supportItems;
    public int sizeY;
    public int interactType;
    public int int4;
    public int int5;
    public int int6;
    public int[] someSoundStuff;
    public int ambientSoundId;
    public int clipType;
    public boolean modelClipped;
    public boolean isSolid;
    public boolean solid;
    public int surroundings;
    public boolean nonFlatShading;
    public int modelHeight;
    public int[] modelIds;
    public int transformConfigId;
    public int decorDisplacement;
    public int[] models;
    public String description;
    public boolean isInteractive;
    public boolean clipped;
    public int animationId;
    public int offsetY;
    public int[] recolorFrom;
    public String[] actions;
    private short[] retextureTo;
    private short[] retextureFrom;
    public boolean boolean3 = true;
    public IterableNodeHashTable params;
    static ModelData[] field2130 = new ModelData[4];

    public ObjectDefinition() {
        type = -1;
    }

    public static void clear() {
        streamIndices = null;
        cache = null;
        stream = null;
    }

    public static void dumpNames() throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter("./Cache/object_names.txt"));
        for (int i = 0; i < TOTAL_OBJECTS; i++) {
            ObjectDefinition def = lookup(i);
            String name = def == null ? "null" : def.name;
            writer.write("ID: " + i + ", name: " + name + "");
            writer.newLine();
        }
        writer.close();
    }

    public static void find(String key) {
        for (int i = 0; i < TOTAL_OBJECTS; i++) {
            ObjectDefinition def = lookup(i);
            String name = def == null ? "null" : def.name;
            String description = def == null ? "null" : def.description;
            final boolean nameContainsKey = def != null && name != null && name.toLowerCase().contains(key);
            final boolean descriptionContainsKey = def != null && description != null && description.toLowerCase().contains(key);
            if (nameContainsKey || descriptionContainsKey) {
                System.out.println(name + " " + i + ", models = " + Arrays.toString(def.modelIds));
            }
        }
    }

    public static ObjectDefinition lookup(int id) {
        ObjectDefinition objectDef = (ObjectDefinition) ObjectDefinition_cached.get(id);

        if (objectDef != null) {
            return objectDef;
        }

        if (id == 25913)
            id = 15552;

        if (id == 1276)
            id = 1277;

        if (id == 25916 || id == 25926)
            id = 15553;

        if (id == 25917)
            id = 15554;

        if(Configuration.snowFloor) {
            if (id == 2092) id = 1318; // big evergreen
            if (id == 2091) id = 1319; // small evergreen
        }

        byte[] data = Js5.configs.takeRecord(6, id);
        objectDef = new ObjectDefinition();
        objectDef.type = id;
        objectDef.reset();
        if (data != null) {
            objectDef.readValues(new Buffer(data));
        }
        objectDef = postDecode(id, objectDef);

        ObjectDefinition_cached.put(objectDef, id);
        return objectDef;
    }

    private static ObjectDefinition postDecode(int id, ObjectDefinition objectDef) {
        boolean removeObject = id == 190;
        /*if (objectDef.name != null) {
            if ((objectDef.name.toLowerCase().contains("inconspicuous"))) {
                if (!objectDef.name.toLowerCase().contains("hotspot") && !objectDef.name.toLowerCase().contains("rickety") )
                    removeObject = true;
            }
        }*/


        // right click objects without actions
        // objectDef.isInteractive = true;

        if(id == 13830) {
            // temp fix for window spaces - will overwrite window spaces in future
            objectDef.reset();
            objectDef.copy(lookup(13099));
        }

        if(id == 27060) {// xmas
            objectDef.name = "Christmas Tree";
            objectDef.description = "Better late than never!";
            objectDef.actions[0] = "Take present";
            objectDef.modelIds = new int[]{1614, 39114};
        }
        if (removeObject) {
            objectDef.modelIds = null;
            objectDef.isInteractive = false;
            objectDef.solid = false;
            return objectDef;
        }

        for (int obelisk : ObjectDefinitionHardCode.OBELISK_IDS) {
            if (id == obelisk) {
                objectDef.actions = new String[]{"Activate", null, null, null, null};
            }
        }

        if (id == 30282) {
            objectDef.sizeX = 9;
            objectDef.sizeY = 9;
        }

        switch(id) {
            case 130:
            case 129:
            case 124:
            case 125:
            case 126:
            case 127:
            case 128:
                objectDef.isInteractive = false;
                objectDef.animationId = -1;
                objectDef.isSolid = false;
                objectDef.transformConfigId = -1;
                objectDef.transformVarbit = -1;
                break;
            case 2146:
                objectDef.actions = new String[5];
                objectDef.actions[0] = "Search";
                objectDef.name = "Strange Altar";
                objectDef.isInteractive = true;
                break;
            case 30012:
                objectDef.actions = new String[5];
                objectDef.actions[0] = "Chop";
                break;
            case 6440:
                objectDef.name = "Entrance";
                objectDef.actions[0] = "Pass through";
                objectDef.isInteractive = true;
            case 26739:
                objectDef.actions = new String[5];
                objectDef.actions[0] = "Enter";
                objectDef.name = "Minigame portal";
                break;
            case 26198:
                objectDef.actions = new String[5];
                objectDef.actions[0] = "Steal-from";
                objectDef.name = "Birthday stall";
                break;
            case 2091:
            case 1319:
                objectDef.sizeX = 2;
                objectDef.sizeY = 2;
                objectDef.modelSizeX = 100;
                objectDef.modelHeight = 100;
                objectDef.modelSizeY = 100;
                break;
            case 26782:
                objectDef.modelIds = new int[]{28258};
                objectDef.sizeX = 3;
                objectDef.sizeY = 3;
                objectDef.impenetrable = false;
                objectDef.isInteractive = true;
                objectDef.modelSizeX = 196;
                objectDef.modelHeight = 196;
                objectDef.modelSizeY = 196;
                objectDef.solid = false;
                break;
            case 13968: // Objects that don't work or being used for now
            case 20873:
            case 2079:
            case 388:
            case 380:
            case 25349:
            case 18493:
            case 30279:
            case 30280:
            case 5618:
            case 378:
                //case 350:
            case 3193:
            case 4859:
            case 4771:
            case 30108:
            case 29322:
            case 6672:
            case 6673:
            case 32660:
            case 41437:
            case 32656:
            case 33085:
            case 32653:
            case 23043: // raids object perhaps
            case 32160:
            case 32158:
            case 32156:
            case 32189:
            case 5989:
            case 32144:
            case 27377:
            case 36219:
            case 34741:
            case 46995:
            case 47175:
            case 23055:
            case 32508:
            case 32132:
            case 24964:
            case 28981:
            case 29033:
            case 9625:
            case 28969:
            case 29990:
            case 33337:
            case 38426:
            case 31617:
            case 20164:
            case 21246:
            case 31616:
            case 33227:
            case 15127:
            case 19039:
            case 25014:
            case 4251:
            case 2844:
            case 40887:
            case 30376:
            case 27552:
            case 369:
            case 4381: // castlewars catapult
            case 4382: // castlewars catapult
            case 42935:
            case 42936:
            case 3500:
            case 42854:
            case 2852:
            case 5163:
            case 8085:
            case 13975:
            case 2677:
            case 24041:
            case 24110:
            case 9740:
            case 31987:
            case 26017:
            case 14306:
            case 28855:
            case 35846:
            case 787:
            case 68:
            case 63:
            case 9370:
            case 6969:
            case 2350:
            case 2375:
            case 2380:
            case 33654:
            case 25818:
            case 26292:
            case 26115:
            case 30180:
            case 10836:
            case 10561:
            case 15597:
            case 36679:
            case 31825:
            case 3577:
            case 1657:
            case 3443:
            case 20134:
            case 11695:
            case 11742:
            case 1781:
            case 20679:
            case 3759:
            case 6114:
            case 25286:
            case 25274:
            case 30177:
            case 19337:
            case 684:
            case 17209:
            case 15653:
            case 5168:
            case 5158:
            case 10041:
            case 152:
            case 2616:
            case 10321:
            case 37388:
            case 20877:
            case 5109:
            case 14996:
            case 2670:
            case 2809:
            case 16884:
            case 29495:
            case 26273:
            case 26277:
            case 26278:
            case 3662:
            case 3742:
            case 10698:
            case 24452:
            case 34396:
            case 26709:
            case 26766:
            case 34499:
            case 11737:
            case 11736:
            case 34655: // Mount karuulum agility obstacle
            case 24428:
            case 11798:
            case 24621:
            case 24457:
            case 24459:
            case 6455:
            case 24556:
            case 15484:
            case 12137:
            case 12232:
            case 15948:
            case 15970:
            case 12233:
            case 12138:
            case 24545:
            case 24541:
            case 12234:
            case 15765:
            case 11735:
            case 12139:
            case 33338:
            case 24544:
            case 26279:
            case 26280:
            case 26291:
            case 26270:
            case 2808:
            case 2807:
            case 2810:
            case 2832:
            case 29627:
            case 29636:
            case 14914:
            case 18044:
            case 32161:
                //case 29488:
            case 4820:
                //case 29489:
            case 4142:
            case 16105:
            case 16646:
            case 30185:
            case 389:
            case 21176:
            case 153:
            case 26712:
            case 21305:
            case 678:
                objectDef.actions = new String[5];
                break;
            case 13378:
            case 13127:
            case 13129:
            case 13131:
            case 13178:
            case 13132:
            case 14837:
            case 14838:
                objectDef.actions = new String[5];
                objectDef.actions = null;
                break;
            case 32187:
            case 32190:
            case 32057:
                objectDef.actions = new String[5];
                objectDef.actions[0] = "Search";
                break;
            case 27096:
                objectDef.actions = new String[5];
                objectDef.actions[0] = "Use";
                break;
            case 22721:
                objectDef.actions = new String[5];
                objectDef.actions[0] = "Smelt";
                break;
            case 13370:
                objectDef.actions = new String[5];
                break;
            case 22822:
                objectDef.actions = new String[5];
                objectDef.actions[0] = "Deposit";
                break;
            case 27291:
                objectDef.copy(lookup(18491));
                break;
            /*
             * Minigame Aquais Neige
             */
            case 6443: // Entrance
                //objectDef.copy(lookup(10595));
                break;
            case 19764: // Exit
                //objectDef.copy(lookup(6447));
                objectDef.actions = new String[5];
                objectDef.actions[0] = "Enter";
                objectDef.name = "Cave Exit";
                break;
            case 40385: // Hween
            case 40383:
                objectDef.actions = new String[5];
                objectDef.actions[0] = "Take-candy";
                break;
            case 27253:
                objectDef.copy(lookup(24101));
                break;
            case 6944:
                objectDef.copy(lookup(6943));
                break;
            case 27264:
                objectDef.copy(lookup(25808));
                break;
            case 7478:
                objectDef.copy(lookup(7409));
                break;
            case 6948:
                objectDef.minimapFunction = 78;
                break;
            case 34586:
                objectDef.name = "Brimstone chest";
                break;
            case 31558:
                objectDef.name = "Exit stairs";
                break;
            case 14911:
                objectDef.actions = new String[5];
                objectDef.name = "Tightrope";
                objectDef.actions[0] = "Cross";
                break;
            case 11455:
                objectDef.actions = new String[5];
                objectDef.actions[0] = "Pull";
                objectDef.name = "Lever";
                break;
            case 1276:
                objectDef.modelHeight = 200;
                objectDef.modelSizeX = 150;
                break;
            case 18052:
                objectDef.actions = new String[5];
                objectDef.name = "Ancient rune case";
                break;
            case 20656:
                objectDef.name = "Gift of Christmas";
                break;
            case 21181:
                objectDef.name = "Snowy house";
                break;
            case 1758:
                objectDef.modelHeight = 250;
                objectDef.modelSizeX = 250;
                break;
            case 9074:
                objectDef.actions = new String[5];
                objectDef.name = "Gilded chest";
                break;
            case 28792:
                objectDef.actions = new String[5];
                objectDef.name = "Vault chest";
                break;
        }



        if (id == 29241) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Restore-stats";
        }
        if (id == 21297) {
            objectDef.name = "Infernal cape (broken)";
        }
        if (id == 4874) {
            objectDef.name = "Miscellaneous Stall";
        }

        /*if (id == 7318) {
            objectDef.copy(lookup(13616));
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Enter";
            objectDef.name = "Portal";
            objectDef.description = "No one even dares to get close to the queen's throne.";
        }
        if (id == 12260) {
        	objectDef.actions[0] = "Enter";
        	objectDef.description = "It is said a mysterious living creature lives there and who ever went never came back.";
        }*/
        if (id == 34947) {
            objectDef.copy(lookup(13616));
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Enter";
            objectDef.name = "Portal of Champions";
            objectDef.description = "It is said a mysterious living creature lives there and who ever went never came back.";
        }
        if (id == 31618) {
            objectDef.copy(lookup(13616));
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Enter";
            objectDef.name = "Icy portal";
            objectDef.description = "No one even dares to get close to the queen's throne.";
        }
        if (id == 37501) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Teleport";
        }
        if (id == 29341) {
            objectDef.actions = new String[5];
            objectDef.name = "Wilderness Stalls";
            objectDef.actions[0] = "Teleport";
        }
        if (id == 4150) {
            objectDef.name = "Bank portal";
        } else if (id == 4151) {
            objectDef.name = "Ditch portal";
        }
        if (id == 3785) {
            objectDef.actions = null;
        }
        if (id == 26756) {
            objectDef.name = "Information";
            objectDef.actions = null;
        }
        if (id == 172) {
            objectDef.name = "Crystal Chest";
        }
        if (id == 18258 || id == 20377) {
            objectDef.name = "@yel@Gilded Altar";
            objectDef.description = "@yel@The altar is polished diorite, lighter and smoother than the basalt of the cave floor.";
        }

        if (id == 21175) {
            objectDef.name = "Broken boat";
            objectDef.actions = null;
        }

        if (id == 6447) {
            objectDef.minimapFunction = 12;
            objectDef.name = "Boss Entrance";
        }
        if (id == 29150) {
            int sizeX = objectDef.sizeX;
            int sizeY = objectDef.sizeY;

            objectDef.copy(lookup(409));

            objectDef.actions = new String[5];
            objectDef.actions[0] = "Venerate";
            objectDef.actions[1] = "Switch-normal";
            objectDef.actions[2] = "Switch-ancient";
            objectDef.actions[3] = "Switch-lunar";
            objectDef.name = "Magical altar";
            objectDef.modelIds = new int[1];
            objectDef.modelIds[0] = 32160;
            objectDef.sizeX = sizeX;
            objectDef.sizeY = sizeY;
        }

        if (id == 6943 || id == 6084 || id == 6945
                || id == 6946 || id == 7409 || id == 7410 || id == 10083
                || id == 10517 || id == 11338 || id == 12798 || id == 12799
                || id == 12800 || id == 12801 || id == 2693
                || id == 4483 || id == 10562 || id == 14382 || id == 14886
                || id == 16995 || id == 16996 || id == 21301 || id == 34343
                || id == 18491 || id == 10355 || id == 10583 || id == 24101 || id == 24347 || id == 26711
                || id == 30267 || id == 14367 || id == 16700 || id == 3194 || id == ObjectID.BANK
                || id == ObjectID.BANK_BOOTH || id == ObjectID.BANK_BOOTH_2
                || id == ObjectID.BANK_BOOTH_3 || id == ObjectID.BANK_BOOTH_4
                || id == ObjectID.BANK_BOOTH_5 || id == ObjectID.BANK_BOOTH_6
                || id == ObjectID.BANK_BOOTH_7 || id == ObjectID.BANK_BOOTH_8
                || id == ObjectID.BANK_BOOTH_9 || id == ObjectID.BANK_BOOTH_10
                || id == ObjectID.BANK_BOOTH_11 || id == ObjectID.BANK_BOOTH_12
                || id == ObjectID.BANK_BOOTH_13 || id == ObjectID.BANK_BOOTH_14
                || id == ObjectID.BANK_BOOTH_15 || id == ObjectID.BANK_BOOTH_16
                || id == ObjectID.BANK_BOOTH_17 || id == ObjectID.BANK_BOOTH_18
                || id == ObjectID.BANK_BOOTH_19 || id == ObjectID.BANK_BOOTH_20
                || id == ObjectID.BANK_BOOTH_21 || id == ObjectID.BANK_BOOTH_22
                || id == ObjectID.BANK_BOOTH_23 || id == ObjectID.BANK_BOOTH_24
                || id == ObjectID.BANK_BOOTH_25 || id == ObjectID.BANK_BOOTH_26
                || id == ObjectID.BANK_BOOTH_27 || id == ObjectID.BANK_BOOTH_28
                || id == ObjectID.BANK_BOOTH_29 || id == ObjectID.BANK_BOOTH_30
                || id == ObjectID.BANK_BOOTH_33 || id == ObjectID.BANK_BOOTH_34
                || id == ObjectID.BANK_BOOTH_35 || id == ObjectID.BANK_BOOTH_36
                || id == ObjectID.BANK_BOOTH_37 || id == ObjectID.BANK_BOOTH_38
                || id == ObjectID.BANK_BOOTH_39 || id == ObjectID.BANK_BOOTH_40
                || id == ObjectID.BANK_BOOTH_41 || id == ObjectID.BANK_BOOTH_42
                || id == ObjectID.BANK_BOOTH_43 || id == ObjectID.BANK_BOOTH_44
                || id == ObjectID.BANK_BOOTH_45
                || id == ObjectID.BANK_BOOTH_10355
                || id == ObjectID.TIGHTROPE_4 // Varrock bank booths (W big bank)
                || id == ObjectID.CHEST_89 || id == 34343
                || id == 27291 || id == 32666 || id == 36559
                || id == ObjectID.BANK_CHEST || id == ObjectID.BANK_CHEST_2
                || id == ObjectID.BANK_CHEST_3 || id == ObjectID.BANK_CHEST_4
                || id == ObjectID.BANK_CHEST_5 || id == ObjectID.BANK_CHEST_6
                || id == ObjectID.BANK_CHEST_7 || id == ObjectID.BANK_CHEST_8
                || id == ObjectID.BANK_CHEST_9 || id == ObjectID.BANK_CHEST_10
                || id == ObjectID.BANK_CHEST_11 || id == ObjectID.BANK_CHEST_12
                || id == ObjectID.BANK_CHEST_13 || id == ObjectID.BANK_CHEST_14
                || id == ObjectID.BANK_CHEST_15 || id == ObjectID.BANK_CHEST_16
                || id == ObjectID.BANK_CHEST_17 || id == ObjectID.BANK_CHEST_18
                || id == ObjectID.OPEN_CHEST || id == ObjectID.OPEN_CHEST_2
                || id == ObjectID.OPEN_CHEST_3
                || id == ObjectID.OPEN_CHEST_5 || id == ObjectID.OPEN_CHEST_6
                || id == ObjectID.OPEN_CHEST_7 || id == ObjectID.OPEN_CHEST_8
                || id == 7478 || id == 27253 || id == 32666) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Bank";
            objectDef.actions[2] = "Collect";
            objectDef.actions[3] = "Open-presets";
        }

        if (id == 29170) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Admire";
            objectDef.actions[1] = "Claim";
            objectDef.actions[2] = "Gamble";
        }

        if (id == 12203) {
            objectDef.isInteractive = false;
            objectDef.solid = false;
        }

        if (id == 26760) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Open";
            objectDef.actions[1] = "Quick-Entry";
        }

        if (id == 34662) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Unlock";
        }

        if (id == 34831 || id == 34832) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Unlock";
        }

        if (id == 3652) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Enter";
        }

        if (id == 6552) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Toggle-spells";
            objectDef.name = "Ancient altar";
        }

        if (id == -1) {
            objectDef.modelIds = null;
            objectDef.isInteractive = false;
            objectDef.solid = false;
        }

        if (id == 14911) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Toggle-spells";
            objectDef.name = "Lunar altar";
        }

        if (id == 10230 || id == 10177) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Climb-down";
        }
        if (id == 16683) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Climb-up";
        }
        if (id == 40417) { // Ingame the object shows as 3831 object id for some reason
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Climb-down";
        }
        if (id == 16683) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Climb-up";
            objectDef.actions[1] = "Climb-down";
        }

        if (id == 2164) {
            objectDef.isInteractive = true;
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Fix";
            objectDef.actions[1] = null;
            objectDef.name = "Trawler Net";
        }
        if (id == 1293) {
            objectDef.isInteractive = true;
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Teleport";
            objectDef.actions[1] = null;
            objectDef.name = "Spirit Tree";
        }


        if (id == 26757) {
            objectDef.isInteractive = true;
            objectDef.actions = new String[5];
            objectDef.actions[1] = "Search";
            objectDef.name = "Valueable Chest";
        }

        switch (id) {
            case 10638:
                objectDef.isInteractive = true;
                return objectDef;
        }

        return objectDef;
    }


    public void reset() {
        modelIds = null;
        models = null;
        name = null;
        description = null;
        recolorFrom = null;
        recolorTo = null;
        retextureFrom = null;
        retextureTo = null;
        sizeX = 1;
        sizeY = 1;
        solid = true;
        impenetrable = true;
        isInteractive = false;
        clipType = -256;
        nonFlatShading = false;
        modelClipped = false;
        animationId = -1;
        decorDisplacement = 16;
        ambient = 0;
        contrast = 0;
        actions = null;
        minimapFunction = -1;
        mapSceneId = -1;
        isRotated = false;
        clipped = true;
        modelSizeX = 128;
        modelHeight = 128;
        modelSizeY = 128;
        surroundings = 0;
        offsetX = 0;
        offsetHeight = 0;
        offsetY = 0;
        obstructsGround = false;
        isSolid = false;

        this.ambientSoundId = -1;
        this.int4 = 0;
        this.int5 = 0;
        this.int6 = 0;

        supportItems = -1;
        transformConfigId = -1;
        transformVarbit = -1;
        transforms = null;
    }

    public boolean needsModelFiles() {
        if (modelIds == null)
            return true;
        boolean var1 = true;
        for(int var2 = 0; var2 < this.modelIds.length; ++var2) {
            // if ((modelIds[var2] & 0xffff) != 46749 && (modelIds[var2] & 0xffff) != 46750)
            var1 &= Js5.models.tryLoadRecord(this.modelIds[var2] & '\uffff', 0);
        }
        return var1;
    }

    public final Renderable getEntity(int var1, int var2, int[][] var3, int var4, int var5, int var6) {
        long var7;
        if (this.models == null) {
            var7 = (long)(var2 + (this.type << 10));
        } else {
            var7 = (long)(var2 + (var1 << 3) + (this.type << 10));
        }

        Object var9 = (Renderable)ObjectDefinition_cachedEntities.get(var7);
        if (var9 == null) {
            ModelData var10 = this.getModelData(var1, var2);
            if (var10 == null) {
                return null;
            }

            if (!this.nonFlatShading) {
                var9 = var10.toModel(this.ambient + 64, this.contrast + 768, -50, -10, -50);
            } else {
                var10.ambient = (short)(this.ambient + 64);
                var10.contrast = (short)(this.contrast + 768);
                var10.calculateVertexNormals();
                var9 = var10;
            }

            ObjectDefinition_cachedEntities.put((DualNode)var9, var7);
        }

        if (this.nonFlatShading) {
            var9 = ((ModelData)var9).copyModelData();
        }

        if (this.clipType * 256 >= 0) {
            if (var9 instanceof Model) {
                var9 = ((Model)var9).contourGround(var3, var4, var5, var6, true, this.clipType * 256);
            } else if (var9 instanceof ModelData) {
                var9 = ((ModelData)var9).method4172(var3, var4, var5, var6, true, this.clipType * 256);
            }
        }

        return (Renderable)var9;
    }

    public boolean modelsReady(int var1) {
        if (this.models != null) {
            for(int var4 = 0; var4 < this.modelIds.length; ++var4) {
                if (this.modelIds[var4] == var1) {
                    return Js5.models.tryLoadRecord(this.modelIds[var4] & '\uffff', 0);
                }
            }

            return true;
        } else if (this.modelIds == null) {
            return true;
        } else if (var1 != 10) {
            return true;
        } else {
            boolean var2 = true;

            for(int var3 = 0; var3 < this.modelIds.length; ++var3) {
                var2 &= Js5.models.tryLoadRecord(this.modelIds[var3] & '\uffff', 0);
            }

            return var2;
        }
    }

    private void copyModel(ObjectDefinition copy) {
        name = copy.name;
        modelSizeX = copy.modelSizeX;
        modelHeight = copy.modelHeight;
        modelSizeY = copy.modelSizeY;
        obstructsGround = copy.obstructsGround;
        offsetX = copy.offsetX;
        contrast = copy.contrast;
        sizeX = copy.sizeX;
        offsetHeight = copy.offsetHeight;
        recolorTo = copy.recolorTo;
        type = copy.type;
        impenetrable = copy.impenetrable;
        sizeY = copy.sizeY;
        modelClipped = copy.modelClipped;
        isSolid = copy.isSolid;
        solid = copy.solid;
        surroundings = copy.surroundings;
        nonFlatShading = copy.nonFlatShading;
        decorDisplacement = copy.decorDisplacement;
        models = copy.models;
        description = copy.description;
        isInteractive = copy.isInteractive;
        clipped = copy.clipped;
        offsetY = copy.offsetY;
        recolorFrom = copy.recolorFrom;
        actions = copy.actions;
        retextureTo = copy.retextureTo;
        retextureFrom = copy.retextureFrom;
        modelIds = copy.modelIds;
        ambientSoundId = copy.ambientSoundId;
        someSoundStuff = copy.someSoundStuff;
    }

    private void copy(ObjectDefinition copy) {
        name = copy.name;
        modelSizeX = copy.modelSizeX;
        modelHeight = copy.modelHeight;
        modelSizeY = copy.modelSizeY;
        obstructsGround = copy.obstructsGround;
        offsetX = copy.offsetX;
        contrast = copy.contrast;
        sizeX = copy.sizeX;
        offsetHeight = copy.offsetHeight;
        recolorTo = copy.recolorTo;
        type = copy.type;
        impenetrable = copy.impenetrable;
        sizeY = copy.sizeY;
        modelClipped = copy.modelClipped;
        isSolid = copy.isSolid;
        solid = copy.solid;
        surroundings = copy.surroundings;
        nonFlatShading = copy.nonFlatShading;
        decorDisplacement = copy.decorDisplacement;
        models = copy.models;
        description = copy.description;
        isInteractive = copy.isInteractive;
        clipped = copy.clipped;
        offsetY = copy.offsetY;
        recolorFrom = copy.recolorFrom;
        actions = copy.actions;
        retextureTo = copy.retextureTo;
        retextureFrom = copy.retextureFrom;
        transforms = copy.transforms;
        modelIds = copy.modelIds;
        ambientSoundId = copy.ambientSoundId;
        someSoundStuff = copy.someSoundStuff;
    }

    public ObjectDefinition transform() {
        int morphismIndex = -1;

        if (this.transformVarbit != -1) {
            morphismIndex = OsCache.getVarbit(this.transformVarbit);
        } else if (this.transformConfigId != -1) {
            morphismIndex = Varps.Varps_main[this.transformConfigId];
        }

        int morphismId;

        if (morphismIndex >= 0 && morphismIndex < this.transforms.length - 1) {
            morphismId = this.transforms[morphismIndex];
        } else {
            morphismId = this.transforms[this.transforms.length - 1];
        }

        return morphismId != -1 ? lookup(morphismId) : null;
    }

    public Model getModel(int var1, int var2, int[][] var3, int var4, int var5, int var6) {
        long var7;
        if (this.models == null) {
            var7 = (long)(var2 + (this.type << 10));
        } else {
            var7 = (long)(var2 + (var1 << 3) + (this.type << 10));
        }

        Model var9 = (Model)ObjectDefinition_cachedModels.get(var7);
        if (var9 == null) {
            ModelData var10 = this.getModelData(var1, var2);
            if (var10 == null) {
                return null;
            }

            var9 = var10.toModel(this.ambient + 64, this.contrast + 768, -50, -10, -50);
            ObjectDefinition_cachedModels.put(var9, var7);
        }

        if (this.clipType * 256 >= 0) {
            var9 = var9.contourGround(var3, var4, var5, var6, true, this.clipType * 256);
        }

        return var9;
    }

    public final Model getModelDynamic(int var1, int var2, int[][] var3, int var4, int var5, int var6, Animation var7, int var8) {
        long var9;
        if (this.models == null) {
            var9 = (long)(var2 + (this.type << 10));
        } else {
            var9 = (long)(var2 + (var1 << 3) + (this.type << 10));
        }

        Model var11 = (Model)ObjectDefinition_cachedModels.get(var9);
        if (var11 == null) {
            ModelData var12 = this.getModelData(var1, var2);
            if (var12 == null) {
                return null;
            }

            var11 = var12.toModel(this.ambient + 64, this.contrast + 768, -50, -10, -50);
            ObjectDefinition_cachedModels.put(var11, var9);
        }

        if (var7 == null && this.clipType * 256 == -1) {
            return var11;
        } else {
            if (var7 != null) {
                var11 = var7.transformObjectModel(var11, var8, var2);
            } else {
                var11 = var11.toSharedSequenceModel(true);
            }

            if (this.clipType * 256 >= 0) {
                var11 = var11.contourGround(var3, var4, var5, var6, false, this.clipType * 256);
            }

            return var11;
        }
    }

    final ModelData getModelData(int var1, int var2) {
        ModelData var3 = null;
        boolean var4;
        int var5;
        int var7;
        if (this.models == null) {
            if (var1 != 10) {
                return null;
            }

            if (this.modelIds == null) {
                return null;
            }

            var4 = this.isRotated;
            if (var1 == 2 && var2 > 3) {
                var4 = !var4;
            }

            var5 = this.modelIds.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                var7 = this.modelIds[var6];
                if (var4) {
                    var7 += 65536;
                }

                var3 = (ModelData)ObjectDefinition_cachedModelData.get((long)var7);
                if (var3 == null) {
                    var3 = ModelData.ModelData_get(Js5.models, var7 & '\uffff', 0);
                    if (var3 == null) {
                        return null;
                    }

                    if (var4) {
                        var3.__t_226();
                    }

                    ObjectDefinition_cachedModelData.put(var3, (long)var7);
                }

                if (var5 > 1) {
                    field2130[var6] = var3;
                }
            }

            if (var5 > 1) {
                var3 = new ModelData(field2130, var5);
            }
        } else {
            int var9 = -1;

            for(var5 = 0; var5 < this.models.length; ++var5) {
                if (this.models[var5] == var1) {
                    var9 = var5;
                    break;
                }
            }

            if (var9 == -1) {
                return null;
            }

            var5 = this.modelIds[var9];
            boolean var10 = this.isRotated ^ var2 > 3;
            if (var10) {
                var5 += 65536;
            }

            var3 = (ModelData)ObjectDefinition_cachedModelData.get((long)var5);
            if (var3 == null) {
                var3 = ModelData.ModelData_get(Js5.models, var5 & '\uffff', 0);
                if (var3 == null) {
                    return null;
                }

                if (var10) {
                    var3.__t_226();
                }

                ObjectDefinition_cachedModelData.put(var3, (long)var5);
            }
        }

        if (this.modelSizeX == 128 && this.modelHeight == 128 && this.modelSizeY == 128) {
            var4 = false;
        } else {
            var4 = true;
        }

        boolean var11;
        if (this.offsetX == 0 && this.offsetHeight == 0 && this.offsetY == 0) {
            var11 = false;
        } else {
            var11 = true;
        }

        ModelData var8 = new ModelData(var3, var2 == 0 && !var4 && !var11, this.recolorFrom == null, this.retextureFrom == null, true);
        if (var1 == 4 && var2 > 3) {
            var8.method4176(256);
            var8.changeOffset(45, 0, -45);
        }

        var2 &= 3;
        if (var2 == 1) {
            var8.method4177();
        } else if (var2 == 2) {
            var8.method4175();
        } else if (var2 == 3) {
            var8.method4205();
        }

        if (this.recolorFrom != null) {
            for(var7 = 0; var7 < this.recolorFrom.length; ++var7) {
                var8.recolor((short) this.recolorFrom[var7], (short) this.recolorTo[var7]);
            }
        }

        if (this.retextureFrom != null) {
            for(var7 = 0; var7 < this.retextureFrom.length; ++var7) {
                var8.retexture(this.retextureFrom[var7], this.retextureTo[var7]);
            }
        }

        if (var4) {
            var8.resize(this.modelSizeX, this.modelHeight, this.modelSizeY);
        }

        if (var11) {
            var8.changeOffset(this.offsetX, this.offsetHeight, this.offsetY);
        }

        return var8;
    }

    /*public Model model(int j, int k, int l) {
        Model model = null;
        long l1;
        if (modelTypes == null) {
            if (j != 10)
                return null;
            l1 = (long) ((type << 6) + l) + ((long) (k + 1) << 32);
            Model model_1 = (Model) models.get(l1);
            if (model_1 != null) {
                return model_1;
            }
            if (modelIds == null)
                return null;
            boolean flag1 = isRotated ^ (l > 3);
            int k1 = modelIds.length;
            for (int i2 = 0; i2 < k1; i2++) {
                int l2 = modelIds[i2];
                if (flag1)
                    l2 += 0x10000;
                model = (Model) baseModels.get(l2);
                if (model == null) {
                    model = Model.getModel(l2 & 0xffff);
                    if (model == null)
                        return null;
                    if (flag1)
                        model.method477();
                    baseModels.put(model, l2);
                }
                if (k1 > 1)
                    aModelArray741s[i2] = model;
            }

            if (k1 > 1)
                model = new Model(k1, aModelArray741s);
        } else {
            int i1 = -1;
            for (int j1 = 0; j1 < modelTypes.length; j1++) {
                if (modelTypes[j1] != j)
                    continue;
                i1 = j1;
                break;
            }

            if (i1 == -1)
                return null;
            l1 = (long) ((type << 8) + (i1 << 3) + l) + ((long) (k + 1) << 32);
            Model model_2 = (Model) models.get(l1);
            if (model_2 != null) {
                return model_2;
            }
            if (modelIds == null) {
                return null;
            }
            int j2 = modelIds[i1];
            boolean flag3 = isRotated ^ (l > 3);
            if (flag3)
                j2 += 0x10000;
            model = (Model) baseModels.get(j2);
            if (model == null) {
                model = Model.getModel(j2 & 0xffff);
                if (model == null)
                    return null;
                if (flag3)
                    model.method477();
                baseModels.put(model, j2);
            }
        }
        boolean flag;
        flag = modelSizeX != 128 || modelHeight != 128 || modelSizeY != 128;
        boolean flag2;
        flag2 = offsetX != 0 || offsetHeight != 0 || offsetY != 0;
        Model model_3 = new Model(recolorFrom == null,
                RSFrame317.noAnimationInProgress(k), l == 0 && k == -1 && !flag
                && !flag2, retextureFrom == null, model);
        if (k != -1) {
            model_3.skin();
            model_3.applyTransform(k);
            model_3.faceLabelsAlpha = null;
            model_3.vertexLabels = null;
        }
        while (l-- > 0)
            model_3.rotate90Degrees();
        if (recolorFrom != null) {
            for (int k2 = 0; k2 < recolorFrom.length; k2++)
                model_3.recolor(recolorFrom[k2],
                        recolorTo[k2]);

        }
        if (retextureFrom != null) {
            for (int k2 = 0; k2 < retextureFrom.length; k2++)
                model_3.retexture(retextureFrom[k2],
                        retextureTo[k2]);

        }
        if (flag)
            model_3.scale(modelSizeX, modelSizeY, modelHeight);
        if (flag2)
            model_3.translate(offsetX, offsetHeight, offsetY);

        model_3.light(64 + ambient, 768 + contrast, -50, -10, -50, !nonFlatShading);
        if (supportItems == 1)
            model_3.itemDropHeight = model_3.height;
        models.put(model_3, l1);
        return model_3;
    }*/

    public void readValues(Buffer stream) {
        int flag = -1;
        do {
            int type = stream.readUnsignedByte();
            if (type == 0)
                break;
            if (type == 1) {
                int len = stream.readUnsignedByte();
                if (len > 0) {
                    if (modelIds == null || ObjectDefinition_isLowDetail) {
                        models = new int[len];
                        modelIds = new int[len];
                        for (int k1 = 0; k1 < len; k1++) {
                            modelIds[k1] = stream.readUnsignedShort();
                            models[k1] = stream.readUnsignedByte();
                        }
                    } else {
                        stream.index += len * 3;
                    }
                }
            } else if (type == 2)
                name = stream.readStringCp1252NullTerminated();
            else if (type == 5) {
                int len = stream.readUnsignedByte();
                if (len > 0) {
                    if (modelIds == null || ObjectDefinition_isLowDetail) {
                        models = null;
                        modelIds = new int[len];
                        for (int l1 = 0; l1 < len; l1++)
                            modelIds[l1] = stream.readUnsignedShort();
                    } else {
                        stream.index += len * 2;
                    }
                }
            } else if (type == 14)
                sizeX = stream.readUnsignedByte();
            else if (type == 15)
                sizeY = stream.readUnsignedByte();
            else if (type == 17) {
                interactType = 0;
                solid = false;
            }
            else if (type == 18)
                impenetrable = false;
            else if (type == 19)
                isInteractive = (stream.readUnsignedByte() == 1);
            else if (type == 21) {
                clipType = 0;
            } else if (type == 22) {
                nonFlatShading = true;
            } else if (type == 23)
                modelClipped = true;
            else if (type == 24) {
                animationId = stream.readUnsignedShort();
                if (animationId == 65535)
                    animationId = -1;
            } else if (type == 27) {
                interactType = 1;
            } else if (type == 28)
                decorDisplacement = stream.readUnsignedByte();
            else if (type == 29)
                ambient = stream.readByte();
            else if (type == 39)
                contrast = stream.readByte() * 25;
            else if (type >= 30 && type < 35) {
                if (actions == null)
                    actions = new String[10];
                actions[type - 30] = stream.readStringCp1252NullTerminated();
                if (actions[type - 30].equalsIgnoreCase("hidden"))
                    actions[type - 30] = null;
            } else if (type == 40) {
                int i1 = stream.readUnsignedByte();
                recolorFrom = new int[i1];
                recolorTo = new int[i1];
                for (int i2 = 0; i2 < i1; i2++) {
                    recolorFrom[i2] = stream.readUnsignedShort();
                    recolorTo[i2] = stream.readUnsignedShort();
                }
            } else if (type == 41) {
                int j2 = stream.readUnsignedByte();
                retextureFrom = new short[j2];
                retextureTo = new short[j2];
                for (int k = 0; k < j2; k++) {
                    retextureFrom[k] = (short) stream.readUnsignedShort();
                    retextureTo[k] = (short) stream.readUnsignedShort();
                }
            } else if (type == 61) {
                int category = stream.readUnsignedShort();
            } else if (type == 62)
                isRotated = true;
            else if (type == 64)
                clipped = false;
            else if (type == 65)
                modelSizeX = stream.readUnsignedShort();
            else if (type == 66)
                modelHeight = stream.readUnsignedShort();
            else if (type == 67)
                modelSizeY = stream.readUnsignedShort();
            else if (type == 68) {
                int id = stream.readUnsignedShort();
                mapSceneId = (id <= 100 ? id : -1);
            } else if (type == 69)
                surroundings = stream.readUnsignedByte();
            else if (type == 70)
                offsetX = stream.readShort();
            else if (type == 71)
                offsetHeight = stream.readShort();
            else if (type == 72)
                offsetY = stream.readShort();
            else if (type == 73)
                obstructsGround = true;
            else if (type == 74)
                isSolid = true;
            else if (type == 75) {
                supportItems = stream.readUnsignedByte();
            } else if (type != 77 && type != 92) {
                if (type == 78) {
                    ambientSoundId = stream.readUnsignedShort(); // ambient sound id
                    int4 = stream.readUnsignedByte();
                } else if (type == 79) {
                    int5 = stream.readUnsignedShort();
                    int6 = stream.readUnsignedShort();
                    int4 = stream.readUnsignedByte();
                    int len = stream.readUnsignedByte();
                    someSoundStuff = new int[len];
                    for (int i = 0; i < len; i++) {
                        someSoundStuff[i] = stream.readUnsignedShort();
                    }
                } else if (type == 81) {
                    clipType = stream.readUnsignedByte() * 65536;
                } else if (type == 82) {
                    minimapFunction = stream.readUnsignedShort();
                } else if(type == 89) {
                    boolean3 = false;
                } else if (type == 249) {
                    params = OSCollections.readStringIntParameters(stream, params);
                }
            } else {
                transformVarbit = stream.readUnsignedShort();
                if (transformVarbit == 65535)
                    transformVarbit = -1;
                transformConfigId = stream.readUnsignedShort();
                if (transformConfigId == 65535)
                    transformConfigId = -1;
                int var3 = -1;
                if(type == 92) {
                    var3 = stream.readUnsignedShort();
                    if (var3 == 65535) {
                        var3 = -1;
                    }
                }

                int j1 = stream.readUnsignedByte();
                transforms = new int[j1 + 2];
                for (int j2 = 0; j2 <= j1; j2++) {
                    transforms[j2] = stream.readUnsignedShort();
                    if (transforms[j2] == 65535)
                        transforms[j2] = -1;
                }

                transforms[j1 + 1] = var3;
            }
        } while (true);
        if (flag == -1 && name != "null" && name != null) {
            isInteractive = modelIds != null
                    && (models == null || models[0] == 10);
            if (actions != null)
                isInteractive = true;
        }
        if (isSolid) {
            interactType = 0;
            solid = false;
            impenetrable = false;
        }
        if (supportItems == -1)
            supportItems = solid ? 1 : 0;
    }

    public boolean hasSound() {

//        if(childrenIDs != null)
//            return false;
//
//        return this.ambientSoundId > 0 && ambientSoundId < Short.MAX_VALUE;
        if (this.transforms == null) {
            if (ambientSoundId != -1)
                return true;

            if (int5 > 0 || int6 > 0) {
                return someSoundStuff != null;
            }

        } else {
            for (int childrenID : this.transforms) {
                if (childrenID != -1) {
                    ObjectDefinition var2 = ObjectDefinition.lookup(childrenID);
                    if (var2.ambientSoundId != -1)
                        return true;
                    if (var2.int5 > 0 || var2.int6 > 0) {
                        if (var2.someSoundStuff != null)
                            return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "ObjectDefinition{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", modelIds=" + Arrays.toString(modelIds) +
                ", modelTypes=" + Arrays.toString(models) +
                ", description='" + description + '\'' +
                ", animation=" + animationId +
                ", interactions=" + Arrays.toString(actions) +
                '}';
    }
}