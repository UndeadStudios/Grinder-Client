package com.runescape.cache.def;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.runescape.Client;
import com.runescape.cache.*;
import com.runescape.cache.anim.Animation;
import com.runescape.collection.DualNode;
import com.runescape.collection.EvictingDualNodeHashTable;
import com.runescape.collection.IterableNodeHashTable;
import com.runescape.collection.OSCollections;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Refactored reference:
 * http://www.rune-server.org/runescape-development/rs2-client/downloads/575183-almost-fully-refactored-317-client.html
 */
public final class NpcDefinition extends DualNode {

    private static final String PETS[][] = {{"318", "Dark Core"}, {"495", "Venenatis Spiderling"},
            {"497", "Callisto Cub"}, {"964", "Hellpuppy"}, {"2055", "Chaos Elemental Jr."},
            {"2130", "Snakeling"}, {"2131", "Magma Snakeling"}, {"2132", "Tanzanite Snakeling"},
            {"5536", "Vet'ion"}, {"5537", "Vet'ion Reborn"}, {"5561", "Scorpias' Offspring"},
            {"5884", "Abyssal Orphan"}, {"5892", "TzRek-Jad"}, {"6628", "Dagganoth Supreme Jr."},
            {"6629", "Dagganoth Prime Jr."}, {"6630", "Dagganoth Rex Jr."}, {"6631", "Chick'arra"},
            {"6632", "General Awwdor"}, {"6633", "Commander Miniana"}, {"6634", "K'ril Tinyroth"},
            {"6635", "Baby Mole"}, {"6636", "Prince Black Dragon"}, {"6637", "Kalphite Princess"},
            {"6638", "Kalphite Princess"}, {"6639", "Smoke Devil"}, {"6640", "Baby Kraken"},
            {"6642", "Penance Princess"}, {"7520", "Olmlet"},
            {"6715", "Heron"}, {"6717", "Beaver"}, {"6718", "Red Chinchompa"}, {"6719", "Grey Chinchompa"},
            {"6720", "Black Chimchompa"}, {"6723", "Rock Golem"}, {"7334", "Giant Squirrel"},
            {"7335", "Tangleroot"}, {"7336", "Rocky"},

            {"7337", "Fire Rift Guardian"}, {"7338", "Air Rift Guardian"}, {"7339", "Mind Rift Guardian"},
            {"7340", "Water Rift Guardian"}, {"7341", "Earth Rift Guardian"}, {"7342", "Body Rift Guardian"},
            {"7343", "Cosmic Rift Guardian"}, {"7344", "Chaos Rift Guardian"}, {"7345", "Nature Rift Guardian"},
            {"7346", "Law Rift Guardian"}, {"7347", "Death Rift Guardian"}, {"7348", "Soul Rift Guardian"},
            {"7349", "Astral Rift Guardian"}, {"7350", "Blood Rift Guardian"}, {"8008", "Corporeal critter"},
            {"8517", "Ikkle Hydra"}, {"8518", "Ikkle Hydra"}, {"8519", "Ikkle Hydra"}, {"8520", "Ikkle Hydra"}};
    public static Client clientInstance;
    public static EvictingDualNodeHashTable NpcDefinition_cached = new EvictingDualNodeHashTable(4096);
    public static EvictingDualNodeHashTable NpcDefinition_cachedModels = new EvictingDualNodeHashTable(50);
    public final int anInt64;
    public int walkTurnRightSequence;
    public int transformVarbit;
    public int walkTurnSequence;
    public int trasnformVarp;
    public int combatLevel;
    public String name;
    public String actions[];
    public int walkSequence;
    public byte size;
    public int[] recolourTarget;
    public int[] retextureTarget;
    public int[] aditionalModels;
    public int headIcon;
    public int[] recolourOriginal;
    public int[] retextureOriginal;
    public int standAnim;
    public long interfaceType;
    public int degreesToTurn;
    public int walkTurnLeftSequence;
    public int turnLeftSequence;
    public int turnRightSequence;
    public int field2003 = -1;
    public int field2004 = -1;
    public int field2005 = -1;
    public int field2006 = -1;
    public int field2007 = -1;
    public int field2008 = -1;
    public int field2009 = -1;
    public int field1989 = -1;
    public boolean clickable;
    public int lightModifier;
    public int scaleY;
    public boolean drawMinimapDot;
    public int transforms[];
    public String description;
    public int scaleXZ;
    public int shadowModifier;
    public boolean priorityRender;
    public int[] modelId;
    public int dropTableZoom = 1500;
    public int id;
    public boolean isWildernessBoss = false;
    IterableNodeHashTable params;

    public NpcDefinition() {
        walkTurnRightSequence = -1;
        transformVarbit = -1;
        walkTurnSequence = -1;
        trasnformVarp = -1;
        combatLevel = -1;
        anInt64 = 1834;
        walkSequence = -1;
        size = 1;
        headIcon = -1;
        standAnim = -1;
        interfaceType = -1L;
        degreesToTurn = 32;
        walkTurnLeftSequence = -1;
        clickable = true;
        scaleY = 128;
        drawMinimapDot = true;
        scaleXZ = 128;
        priorityRender = false;
        recolourOriginal = null;
        recolourTarget = null;
        retextureOriginal = null;
        retextureTarget = null;
        turnLeftSequence = -1;
        turnRightSequence = -1;
        //fixSlide();
    }

    public static void dumpNpcHeights() {

        final JsonArray array = new JsonArray();

        for (int npcId = 0; npcId < getTotalNpcs(); npcId++) {
            final NpcDefinition definition = lookup(npcId);
            if (definition != null) {
                if (definition.combatLevel > 0) {
                    Model model = definition.getModel(null, -1, null, -1);
                    if (model != null) {
                        model.calculateBoundsCylinder();
                        JsonObject obj = new JsonObject();
                        obj.addProperty("npc", definition.id);
                        obj.addProperty("height", model.height);
                        array.add(obj);
                    }
                }
            }
        }

        final File file = Paths.get("npc_model_heights.json").toFile();

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            final FileWriter writer = new FileWriter(file);

            new GsonBuilder().setPrettyPrinting().create().toJson(array, writer);

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lookup an NpcDefinition by its id
     *
     * @param id
     */
    public static NpcDefinition lookup(int id) {
        NpcDefinition definition = (NpcDefinition) NpcDefinition_cached.get(id);
        if (definition != null) {
            return definition;
        } else {
            byte[] data = Js5.configs.takeRecord(9, id);
            definition = new NpcDefinition();
            definition.interfaceType = id;
            definition.id = id;
            if (data != null) {
                definition.readValues(new Buffer(data));
            }
        }

        for (int i = 0; i < PETS.length; i++) {
            if (id == Integer.parseInt(PETS[i][0])) {
                definition.fixSlide();
                definition.name = PETS[i][1];
            }
        }
        if (id == Integer.parseInt(PETS[22][0]) || id == Integer.parseInt(PETS[23][0])) {
            definition.actions[2] = "Pick-up";
            definition.actions[3] = "Metamorphosis";
        }
        if (id == Integer.parseInt(PETS[8][0]) || id == Integer.parseInt(PETS[9][0])
                || id >= Integer.parseInt(PETS[37][0]) && id <= Integer.parseInt(PETS[50][0])
                || id >= Integer.parseInt(PETS[30][0]) && id <= Integer.parseInt(PETS[32][0])) {
            definition.actions[3] = "Metamorphosis";
        }
        if (id == Integer.parseInt(PETS[52][0]) || id == Integer.parseInt(PETS[53][0])
                || id >= Integer.parseInt(PETS[54][0]) && id <= Integer.parseInt(PETS[55][0])) {
            definition.actions[3] = "Metamorphosis";
        }

        if (definition.id == 11280) {
            definition.headIcon = 9;
        } else if (definition.id == 11281) {
            definition.headIcon = 17;
        } else if (definition.id == 11282) {
            definition.headIcon = 16;
        }

        if (definition.combatLevel > 3 && definition.id != 3127 && definition.id != 7307 && definition.id != 2909
                && definition.id != 3462 && definition.id != 3463 && definition.id != 3464 && definition.id != 3473
                && definition.id != 9001
                && definition.id != 9002
                && definition.id != 9003
                && definition.id != 9004
                && definition.id != 9005
                && definition.id != 9006
                && definition.id != 9007
                && definition.id != 9008
                && definition.id != 9009
                && definition.id != 9010
                && definition.id != 9011
                && definition.id != 9012
                && definition.id != 9013
                && definition.id != 9014
                && definition.id != 9015
                && definition.id != 9016
                && definition.id != 9017
                && definition.id != 9018
                && definition.id != 9019
                && definition.id != 6368
                && definition.id != 1129
                && definition.id != 6349
                && definition.id != 6346
                && definition.id != 6344
                && definition.id != 6321
                && definition.id != 3456
                && !(definition.id >= 4880 && definition.id <= 4889)
                && !(definition.id >= 1689 && definition.id <= 1750)
                && !(definition.id >= 3116 && definition.id <= 3127)
                && !(definition.id >= 2189 && definition.id <= 2194)
                && !(definition.id >= 11283 && definition.id <= 11286)
        ) {
            if (definition.actions != null) {
                if (definition.actions.length < 1) {
                    definition.actions = new String[5];
                }
                definition.actions[3] = "View Drop Table";
            }
        }
        if ((definition.combatLevel > 300 || definition.id == 4067
                || definition.id == 6502 || definition.id == 6382 || definition.id == 6345
                || definition.id == 6477 || definition.id == 6477 || definition.id == 4922 || definition.id == 882
                || definition.id == 4315 || definition.id == 4922 || definition.id == 239
                || definition.id == 9346 || definition.id == 9347 || definition.id == 9350
                || definition.id == 5822 || definition.id == 3475)
        && !(definition.id > 6300 && definition.id < 6390)) {
            if (definition.actions != null) {
                if (definition.actions.length < 1) {
                    definition.actions = new String[5];
                }
                definition.actions[4] = "View Stats";
            }
        }
        //if (definition.size > 1) {
//			definition.fixSlide();
        //}
//
//		if(definition.walkTurnSequence <= 0)
//			definition.walkTurnSequence = definition.walkSequence;
//		if(definition.walkTurnRightSequence <= 0)
//			definition.walkTurnRightSequence = definition.walkSequence;
//		if(definition.walkTurnLeftSequence <= 0)
//			definition.walkTurnLeftSequence = definition.walkSequence;

        switch (id) {
            case 3390:
                definition.name = "Granny Mike";
                definition.actions = new String[7];
                break;
            case 3835:
                definition.name = "Mia";
                definition.actions = new String[7];
                break;
            case 1129:
                definition.name = "Kamil";
                break;
            case 6321:
                definition.name = "Jungle Demon";
                break;
            case 1030:
                definition.name = "Santa";
                definition.modelId = new int[]{28989, 28978, 34426, 28981, 28983, 28979, 4925, 9103, 4396};
                definition.aditionalModels = definition.modelId;
                definition.size = 2;
                definition.walkSequence = 3039;
                definition.standAnim = 3040;
                definition.actions = new String[7];
                definition.scaleXZ *= 1.55;
                definition.scaleY *= 1.30;
//				definition.actions[0] = "Talk-to";
                definition.fixSlide();
                break;
            case 4315:
                definition.dropTableZoom = 3000;
                definition.walkSequence = definition.standAnim;
                break;
            case 1325: // Npcs that you cannot interact with
            case 1898:
            case 4923:
            case 4092:
            case 4094:
            case 5452:
            case 5451:
            case 6057:
            case 7374:
            case 7377:
            case 7380:
            case 7378:
            case 1808:
                //case 2895:
            case 4065:
            case 4066:
            case 544:
            case 2895:
            case 2152:
            case 5450:
            case 5210:
            case 3115:
            case 2914:
            case 5039:
            case 2034:
            case 2134:
            case 2035:
            case 2036:
            case 2039:
                //case 2040:
            case 1335:
            case 4586:
            case 4587:
            case 7654:
            case 6799:
            case 7518:
            case 1120:
            case 3246:
            case 2033:
            case 2037:
            case 1327:
            case 9348:
            case 9312:
            case 671:
            case 8039:
            case 7954:
            case 8038:
            case 2153:
            case 4027:
            case 4040:
            case 4041:
            case 6553:
            case 1324:
            case 5037:
            case 5216:
            case 2881:
            case 2902:
            case 5209:
            case 2922:
            case 1106:
            case 1104:
            case 4574:
            case 4588:
            case 1912:
            case 1910:
            case 1921:
            case 1922:
            case 1920:
            case 5214:
            case 1909:
            case 2680:
            case 5083:
                //case 8043:
            case 1326:
            case 3097:
            case 1579:
            case 5790:
            case 4162:
            case 5525:
            case 1397:
            case 4894:
                //case 821:
            case 2872:
            case 1352:
            case 3221:
            case 2812:
            case 921:
            case 3223:
            case 3369:
            case 3217:
            case 7742:
            case 7743:
                // Mount karuulm
            case 8557:
            case 8560:
            case 8552:
            case 8549:
            case 8548:
            case 8555:
            case 8553:
            case 8550:
            case 8556:
            case 3218:
            case 2788:
                //case 4626:
            case 3220:
            case 8051:
            case 3224:
            case 4288:
            case 4287:
            case 3086:
            case 1351:
            case 4627:
            case 5949:
            case 4285:
            case 2878:
            case 2876:
            case 2877:
                //case 3231:
            case 2873:
            case 2874:
            case 6089:
            case 5951:
            case 1903:
            case 1904:
            case 1905:
            case 7673:
            case 1906:
            case 1907:
            case 1908:
            case 3255:
            case 5950:
            case 5952:
            case 310:
            case 2879:
            case 3349:
                //case 3344:
            case 8681:
            case 542:
            case 3302:
            case 4087:
            case 4085:
            case 4089:
            case 4090:
            case 4091:
            case 4086:
            case 4083:
            case 4084:
            case 4102:
            case 5517:
            case 1660:
            case 1661:
            case 1659:
            case 1656:
            case 1657:
            case 2687:
            case 6586:
            case 6478:
            case 6524:
            case 6526:
            case 3096:
            case 2123:
            case 1260:
            case 5832:
            case 6750:
            case 5510:
            case 2480:
            case 3479:
            case 4284:
            case 2116:
            case 4274:
            case 4275:
            case 5527:
            case 1160:
            case 2560:
            case 3214:
            case 278:
            case 1305:
            case 5896:
                //case 4737:
            case 5524:
            case 3100:
            case 7117:
            case 2556:
            case 2679:
            case 4687:
            case 5422:
            case 7712:
            case 7713:
            case 2691:
            case 2663:
            case 501:
            case 1336:
            case 1337:
            case 2684:
            case 4733:
            case 3647:
            case 4916:
            case 4917:
            case 8680:
            case 5384:
            case 4920:
            case 5316:
            case 4707:
            case 2345:
            case 2835:
            case 23:
            case 8689:
                //case 9263:
            case 5359:
            case 5363:
                //case 5340:
            case 5362:
            case 1783:
                //case 6527:
                //case 3924:
            case 7714:
            case 7715:
            case 7720:
            case 7719:
            case 3444:
            case 3219:
            case 1289:
                //case 7734:
            case 4984:
            case 2662:
            case 2659:
                //case 4280:
            case 3955:
            case 3954:
            case 8694:
            case 2675:
            case 1103:
            case 1105:
            case 1107:
            case 1146:
                definition.actions = new String[7];
                break;
            case 8687: // Shops only
            case 3199: // Trade only
            case 2364:
            case 6059:
            case 8532:
            case 1079:
            case 5721:
            case 8538:
            case 3213:
            case 3894:
            case 1944:
            case 3842:
            case 6069:
            case 679:
            case 2893:
            case 3533:
            case 1833:
            case 4754:
            case 3891:
            case 4204:
            case 3779:
            case 6943:
            case 4022:
            case 4761:
            case 8688:
            case 2297:
            case 2784:
            case 5249:
            case 2198:
            case 2196:
            case 6945:
            case 3203:
                //case 2891:
            case 3654:
            case 4424:
            case 16:
            case 12:
                //case 2875:
            case 2200:
            case 3102:
            case 3103:
                definition.actions = new String[7];
                definition.actions[0] = "Trade";
                break;
			/*case 3101: // Sawmill construction operator (plank)
				definition.actions = new String[7];
				definition.actions[0] = "Trade";
				break;*/
            case 6424: // Gabooty shilo village
                definition.actions = new String[7];
                definition.actions[0] = "Trade-Co-op";
                definition.standAnim = 813;
                definition.walkSequence = 820;
                break;
            case 1014: // Gambler Shaggy
                definition.actions = new String[7];
                definition.name = "Shaggy";
                definition.actions[0] = "Trade";
                break;
            case 4280: // Talk only
            case 3953:
            case 5833:
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                break;
            case 5513:
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Exchange";
                break;
            case 0: // Exchange only
                definition.actions = new String[7];
                definition.actions[0] = "Exchange";
                break;
            case 3550: // Attack + Pickpocket
                definition.actions = new String[7];
                definition.actions = new String[]{null, "Attack", "Pickpocket", "View Drop Table", null, null, null};
                break;
            case 3443: // Attack only
            case 6956:
            case 4652:
            case 4130:
            case 4643:
            case 1298:
            case 1294:
            case 1293:
            case 1297:
            case 3071:
            case 3262:
                //case 2895:
                //case 3521:
            case 3072:
            case 4919:
            case 4926:
            case 4655:
            case 5186:
            case 522:
            case 3276:
            case 3055:
            case 4277:
            case 3056:
            case 3275:
            case 299:
            case 3057:
            case 3067:
            case 3058:
            case 3059:
            case 3060:
            case 3061:
            case 3062:
            case 3063:
            case 3064:
            case 3065:
            case 3068:
            case 3069:
            case 3070:
            case 4097:
            case 4088:
            case 4099:
            case 4100:
            case 4104:
            case 6087:
            case 6086:
            case 6094:
            case 6096:
                definition.actions = new String[7];
                definition.actions = new String[]{null, "Attack", null, null, null, null, null};
                break;
            case 5919:
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Trade";
                definition.actions[3] = "Upgrade";
                break;
            case 5981:
                definition.actions = new String[7];
                definition.name = "Temple Guardian";
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Trade";
                break;
            case 5810:
            case 118:
            case 5045:
            case 2578:
            case 2658:
            case 3212:
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Trade";
                definition.actions[3] = "Get-task";
                break;
            case 5789:
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Trade";
                definition.actions[3] = "Pay";
                definition.actions[4] = "Get-task";
                break;
            case 8683:
                definition.actions = new String[7];
                definition.name = "Lowe";
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Trade";
                break;
            case 5233:
                definition.actions = new String[7];
                definition.actions[0] = "Big Net";
                break;
            case 5234:
                definition.actions = new String[7];
                definition.actions[0] = "Harpoon";
                definition.actions[2] = "Net";
                break;
            case 1478:
                definition.scaleY = 100;
                definition.scaleXZ = 130;
                definition.name = "Santa's minion";
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                final int[] oldModelIds = definition.modelId;
                final int[] newModelsId = new int[oldModelIds.length + 1];
                System.arraycopy(oldModelIds, 0, newModelsId, 1, oldModelIds.length);
                definition.modelId = newModelsId;
                definition.modelId[0] = 366;
                break;
            case 3528:
                definition.name = "Gertrude";
                break;
            case 277: // Town crier with santa hat
                final int[] oldModelIds2 = definition.modelId;
                final int[] newModelsId2 = new int[oldModelIds2.length + 1];
                System.arraycopy(oldModelIds2, 0, newModelsId2, 1, oldModelIds2.length);
                definition.modelId = newModelsId2;
                definition.modelId[0] = 189;
                //definition.actions = new String[] { "Talk-to", null, "Bank", null, null, null, null };
                break;
            case 3023:
                definition.fixSlide();
                break;
            case 7417:
                definition.actions = new String[7];
                definition.actions = new String[]{"Bank", null, null, null, null, null, null};
                break;
            case 2042:
            case 2043:
            case 2044:
                definition.dropTableZoom = 3500;
                break;
            case 2215:
            case 3162:
            case 3132:
            case 3129:
            case 6495:
            case 963:
            case 965:
            case 4303:
            case 4304:
            case 6500:
            case 6501:
            case 8615:
            case 8616:
            case 8617:
            case 8618:
            case 8619:
            case 8620:
            case 8621:
            case 8622:
            case 8059:
            case 8061:
            case 2005:
            case 7656:
            case 6492:
                definition.dropTableZoom = 3000;
                break;
            case 5597:
            case 1626:
            case 11986:
            case 11983:
            case 11984:
            case 5591:
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Pick-up";
                break;
            case 4062:
                definition.name = "High Priest Store";
                definition.actions = new String[7];
                definition.actions[0] = "Trade";
                break;
            case 5441:
                definition.name = "Deposit Security";
                definition.actions = new String[7];
                definition.actions[0] = "Inquire";
                break;
            case 4058:
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Boss-Drops";
                definition.actions[3] = "Item-Finder";
                break;
            case 3310:
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "My-Commands";
                definition.actions[3] = "Title-Chooser";
                definition.actions[4] = "Display-Rank";
                break;
            case 1357:
                definition.name = "Skilling Tools Store";
                definition.actions = new String[7];
                definition.actions[0] = "Trade";
                break;
            case 1602:
                definition.name = "Captain of the Guard";
                definition.actions = new String[7];
                definition.actions[0] = "Trade";
                break;
            case 2887:
                definition.name = "Fancy Dress Store";
                definition.actions = new String[7];
                definition.actions[0] = "Trade";
                break;
            case 5449:
                //definition.name = "Herblore store";
                definition.actions = new String[]{"Talk-to", null, "Trade", "Decant", "Quick-decant", null, null};
                break;
            case 5442:
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Bank-PIN";
                definition.actions[3] = "Change-Password";
                break;
            case 5958:
                definition.name = "Farming Sore";
                definition.actions = new String[7];
                //definition.actions[0] = "Talk-to";
                definition.actions[2] = "Trade";
                break;
            case 6530:
                definition.name = "Crafting Store";
                definition.actions = new String[7];
                definition.actions[0] = "Trade";
                break;
            case 2367:
                definition.name = "Robes Store";
                definition.actions = new String[7];
                definition.actions[0] = "Trade";
                break;
            case 6019:
                definition.name = "Untradables Store";
                definition.actions = new String[7];
                definition.actions[0] = "Trade";
                break;
            case 1011:
                definition.name = "Gambler";
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Lottery";
                definition.actions[3] = "Buy-Rank";
//                final int[] oldModelIds4 = definition.modelId;
//                final int[] newModelsId4 = new int[oldModelIds4.length + 1];
//                System.arraycopy(oldModelIds4, 0, newModelsId4, 1, oldModelIds4.length);
//                definition.modelId = newModelsId4;
//                definition.modelId[0] = 366;
                break;
            case 1013:
                definition.name = "Oldschool Runescape Store";
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Sell-Your-Items";
                definition.actions[3] = "Buy-OSRS-Tokens";
                definition.actions[4] = "Exchange-Tokens";
                break;
            case 4853:
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                //definition.actions[2] = "Exchange";
                break;
/*            case 7312:
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Exchange";
                definition.actions[3] = "Limited-Items";
                break;*/
            case 2457:
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Trade";
                break;
            case 4409:
                definition.name = "Bloody Skilling Store";
                definition.actions = new String[7];
                definition.actions[2] = "Trade";
                break;
            case 2183:
                definition.name = "Tzhaar Store";
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Trade";
                definition.actions[3] = "Fight-Caves";
                break;
            case 7307:
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Teleport";
                definition.actions[3] = "Trade";
                definition.size = 2;
                definition.scaleXZ *= 1.35;
                definition.scaleY *= 1.35;
                definition.combatLevel = 198;
                break;
            case 3473:
                definition.combatLevel = 512;
                break;
            case 3982:
                definition.combatLevel = 182;
                break;
            case 7515:
                definition.actions = new String[]{null, "Attack", null, null, null, null, null};
                definition.combatLevel = 2350;
                definition.size = 4;
                definition.scaleXZ *= 1.5;
                definition.scaleY *= 1.5;
                break;
            case 823:
                definition.combatLevel = 178;
                break;
            case 3360:
                definition.actions = new String[7];
                definition.actions[0] = "Disturb";
                definition.actions[2] = "Claim Max-cape";
                break;
            case 3519:
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Achievements";
                definition.actions[3] = "Daily-Tasks";
                break;
            case 1577:
                definition.name = "Pure item Store";
                definition.actions = new String[7];
                definition.actions[0] = "Trade";
                break;
            case 3309:
                definition.name = "Wizard Store";
                definition.actions = new String[7];
                definition.actions[0] = "Trade";
                break;
            case 2882:
                definition.name = "Melee Store";
                definition.actions = new String[7];
                definition.actions[0] = "Trade";
                break;
            case 2883:
                definition.name = "Ranged Store";
                definition.actions = new String[7];
                definition.actions[0] = "Trade";
                break;
            case 5036:
                definition.name = "Consumables Store";
                definition.actions = new String[7];
                definition.actions[0] = "Trade";
                break;
            case 4250:
                definition.name = "Skilling Point Store";
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Trade";
                break;
            case 7650:
                definition.name = "Agility Ticket Store";
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Trade";
                definition.actions[3] = "Barbarian Outpost";
                definition.actions[4] = "Brimhaven Agility Arena";
                break;
            case 2580:
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Trade";
                definition.actions[3] = "The-Abyss";
                definition.actions[4] = "Essence-Mine";
                break;
            case 2886:
            case 11435:
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Trade";
                definition.actions[3] = "Get-task";
                definition.actions[4] = "Essence-Mine";
                break;
            case 5792:
            case 3216:
                definition.name = "Premium Points Store";
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Exchange";
                definition.actions[3] = "Limited-Items";
                definition.actions[4] = "View-Rewards";
                break;
            case 3438:
                definition.name = "Vote Point Store";
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Exchange";
                definition.actions[3] = "Redeem";
                break;
            case 1633:
            case 1634:
                definition.walkSequence = 2064;
                definition.walkTurnSequence = 2064;
                definition.walkTurnRightSequence = 2064;
                definition.walkTurnLeftSequence = 2064;
                break;
	/*	case 514:
		case 515:
			definition.name = "General store";
			definition.actions = new String[7];
			definition.actions[0] = "Trade";
			break;*/
		/*case 6494:
		case 2005:
		case 2006:
			definition.fixSlide();
			break;*/
		/*case 4067: // Fixes sliding
			definition.combatLevel = 480;
			definition.turn90CCWAnimIndex = definition.walkAnim;
			definition.turn90CWAnimIndex = definition.walkAnim;
			definition.turn180AnimIndex = definition.walkAnim;
			break;*/
		/*case 2240:
		case 2241:
		case 6615:
		case 2006:
		case 2216:
		case 2217:
		case 2218:
			definition.turn90CCWAnimIndex = definition.walkAnim;
			definition.turn90CWAnimIndex = definition.walkAnim;
			definition.turn180AnimIndex = definition.walkAnim;
			break;*/
		/*case 6600:
			definition.turn90CCWAnimIndex = definition.walkAnim;
			definition.turn90CWAnimIndex = definition.walkAnim;
			definition.turn180AnimIndex = definition.walkAnim;
			break;*/
            case 8100:
                definition.copy(lookup(4067));
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Pick-up";
                definition.name = "Black Titan brute";
                definition.combatLevel = 0;
                definition.scaleXZ *= 0.40;
                definition.scaleY *= 0.40;
                definition.size = 1;
                definition.fixSlide();
                break;
            case 8101:
                definition.copy(lookup(5129));
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Pick-up";
                definition.name = "Glod Jr.";
                definition.combatLevel = 0;
                definition.scaleXZ *= 0.40;
                definition.scaleY *= 0.40;
                definition.size = 1;
                definition.fixSlide();
                break;
            case 8102:
                definition.copy(lookup(6593));
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Pick-up";
                definition.name = "Lava dragon pet";
                definition.combatLevel = 0;
                definition.scaleXZ *= 0.40;
                definition.scaleY *= 0.40;
                definition.size = 1;
                definition.fixSlide();
                break;
            case 8103:
                definition.copy(lookup(882));
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Pick-up";
                definition.name = "Slash Bash critter";
                definition.combatLevel = 0;
                definition.scaleXZ *= 0.40;
                definition.scaleY *= 0.40;
                definition.size = 1;
                definition.fixSlide();
                break;
            case 8104:
                definition.copy(lookup(6477));
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Pick-up";
                definition.name = "Mutant Tarn Jr.";
                definition.combatLevel = 0;
                definition.scaleXZ *= 0.35;
                definition.scaleY *= 0.35;
                definition.size = 1;
                definition.fixSlide();
                break;
            case 8105:
                definition.copy(lookup(3473));
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Pick-up";
                definition.name = "The Inadequacy Jr.";
                definition.combatLevel = 0;
                definition.scaleXZ *= 0.25;
                definition.scaleY *= 0.25;
                definition.size = 1;
                definition.fixSlide();
                break;
            case 8106:
                definition.copy(lookup(6352));
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Pick-up";
                definition.name = "The untouchable creature";
                definition.combatLevel = 0;
                definition.scaleXZ *= 0.40;
                definition.scaleY *= 0.40;
                definition.size = 1;
                definition.fixSlide();
                break;
            case 8107:
                definition.copy(lookup(6440));
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Pick-up";
                definition.name = "Skeleton guard";
                definition.combatLevel = 0;
                definition.scaleXZ *= 0.50;
                definition.scaleY *= 0.50;
                definition.size = 1;
                definition.fixSlide();
                break;
            // Pets
            case 3875: // Duel arena guards Jex
                definition.standAnim = 813;
                break;
            case 5340: // Shilo village spawn
                definition.actions = new String[7];
                definition.standAnim = 813;
                break;
            case 1895: // Gamble arena announcer
                definition.actions = new String[7];
                definition.standAnim = 866;
                break;
            case 7724:
            case 7285:
                definition.standAnim = 2065;
                definition.walkSequence = 2064;
                definition.walkTurnSequence = 2064;
                definition.walkTurnRightSequence = 2064;
                definition.walkTurnLeftSequence = 2064;
                break;
            case 497: // Callisto pet
                definition.scaleXZ = 45;
                definition.size = 2;
                break;
            case 6609: // Callisto
                definition.size = 4;
                break;
            case 995:
                definition.recolourOriginal = new int[2];
                definition.recolourTarget = new int[2];
                definition.recolourOriginal[0] = 528;
                definition.recolourTarget[0] = 926;
                break;
            case 7456:
                definition.actions = new String[]{"Talk-to", null, "Repairs", null, null, null, null};
                break;
            case 5212:
                definition.name = "Borat";
                definition.actions = new String[]{"Talk-to", null, "Trade", null, null, null, null};
                break;
            case 2913: // SKill masters
            case 7716:
            case 3225:
                definition.actions = new String[]{"Talk-to", null, "Trade", "Get-task", null, null, null};
                break;
            case 3307:

                definition.actions = new String[]{"Trade", null, null, null, null, null, null};
                break;
            case 7632:
                definition.actions = new String[]{"Trade", null, null, null, null, null, null};
                break;
		/*case 394:
		case 395:
		case 7677:
			final int[] oldModelIds1 = definition.modelId;
			final int[] newModelsId1 = new int[oldModelIds1.length+2];
			System.arraycopy(oldModelIds1, 0, newModelsId1, 2, oldModelIds1.length);
			definition.modelId = newModelsId1;
			definition.modelId[0] = 189;
			definition.modelId[1] = 366;
			definition.actions = new String[] { "Talk-to", null, "Bank", null, null, null, null };
			break;*/
            case 1274:
                definition.combatLevel = 35;
                break;
            case 4067:
                definition.combatLevel = 424;
                definition.size *= 2;
                definition.scaleXZ *= 1.5;
                definition.scaleY *= 1.5;
                break;
            case 6502:
                definition.name = "King Black Dragon @cya@Spirit";
                definition.isWildernessBoss = true;
                definition.combatLevel = 901;
                definition.size *= 2;
                definition.scaleXZ *= 1.5;
                definition.scaleY *= 1.5;
                break;
            case 6494:
                definition.name = "General Graardor @cya@Spirit";
                definition.isWildernessBoss = true;
                definition.combatLevel = 888;
                definition.dropTableZoom = 3000;
                definition.size = 6;
                definition.scaleXZ *= 1.5;
                definition.scaleY *= 1.5;
                break;
            case 6382:
                definition.name = "Jungle Demon @cya@Spirit";
                definition.isWildernessBoss = true;
                definition.combatLevel = 735;
                definition.size = 5;
                definition.scaleXZ *= 1.5;
                definition.scaleY *= 1.5;
                break;
            case 6345:
                definition.name = "Kamil @cya@Spirit";
                definition.isWildernessBoss = true;
                definition.combatLevel = 820;
                definition.size = 2;
                definition.scaleXZ *= 1.5;
                definition.scaleY *= 1.5;
                break;
            case 9346:
                definition.copy(lookup(6477));
                definition.name = "Mutant Tarn @cya@Spirit";
                definition.isWildernessBoss = true;
                definition.combatLevel = 1015;
                //definition.size = 8;
                definition.scaleXZ *= 1.1;
                definition.scaleY *= 1.1;
                break;
            case 9347:
                definition.copy(lookup(319));
                definition.name = "Corporeal Beast @cya@Spirit";
                definition.isWildernessBoss = true;
                definition.combatLevel = 1125;
                definition.scaleXZ *= 1.1;
                definition.scaleY *= 1.1;
                break;
            case 9350:
                definition.copy(lookup(4067));
                definition.name = "Black Knight Titan @cya@Spirit";
                definition.isWildernessBoss = true;
                definition.combatLevel = 636;
                definition.size = 4;
                definition.scaleXZ *= 1.5;
                definition.scaleY *= 1.5;
                break;
            case 3936:
                definition.name = "Traveler Greg";
                break;
            case 2668:
            case 7413:
                definition.actions = new String[]{null, "Hit", null, null, null, null, null};
                break;
            case 2851: // Ice warrior near ice queen lair
                definition.combatLevel = 111;
                //definition.fixSlide();
                break;
		/*case 5007:
			definition.fixSlide();
			break;*/
            case 2660:
                definition.combatLevel = 0;
                definition.actions = new String[]{"Trade", null, null, null, null, null, null};
                definition.name = "Pker";
                break;
            case 6477:
                definition.combatLevel = 677;
                definition.size = 4;
                definition.scaleXZ *= 1.5;
                definition.scaleY *= 1.5;
                break;
            case 6471:
                definition.combatLevel = 131;
                break;
            case 4922:
                definition.combatLevel = 555;
                break;
            case 5567:
                definition.name = "Outlet PvP-Store";
                definition.combatLevel = 0;
                definition.actions = new String[7];
                definition.actions[0] = "Trade";
                break;
            case 882:
                definition.size = 2;
                definition.combatLevel = 343;
                break;
            case 5816:
                definition.combatLevel = 38;
                break;
            case 318:
            case 6715:
                definition.actions = new String[7];
                definition.actions[0] = "Talk-to";
                definition.actions[2] = "Pick-up";
                break;
            case 100:
            case 7206:
                definition.drawMinimapDot = true;
                break;
            case 306:
                definition.name = "Grinderscape Guide";
                definition.actions = new String[]{"Tutorial", null, null, null, null, null, null};
                break;
            case 4282:
                definition.name = "Prince Ali";
                definition.actions = new String[]{"Trade", null, null, null, null, null, null};
                break;
            case 3226:
                definition.actions = new String[]{"Talk-to", null, "Trade", "Get-task", null, null, null};
                break;
            case 1498:
                definition.actions = new String[]{"Lure", null, "Bait", null, null, null, null};
                break;
            case 1499:
                definition.actions = new String[]{"Cage", null, "Harpoon", null, null, null, null};
                break;
            case 1500:
                definition.actions = new String[]{"Net", null, "Harpoon", null, null, null, null};
                break;
            case 772:
                definition.actions = new String[]{"Talk-to", null, null, "Boss-Contract", null, null, null};
                break;
            case 1504:
                definition.actions = new String[]{null, null, "Trade", "Hunter-Zone", null, null, null};
                break;
            case 3193:
                definition.name = "Thieving master";
                definition.actions = new String[]{"Talk-to", null, "Trade", "Get-task", null, null, null};
                break;
            case 1306:
                definition.actions = new String[]{"Talk-to", null, "Makeover", null, null, null, null};
                break;
            case 1158:
                definition.name = "@or1@Maxed bot";
                definition.combatLevel = 126;
                definition.actions = new String[]{null, "Attack", null, null, null, null, null};
                definition.modelId[5] = 268; // platelegs rune
                definition.modelId[0] = 18954; // Str cape
                definition.modelId[1] = 21873; // Head - neitznot
                definition.modelId[8] = 15413; // Shield rune defender
                definition.modelId[7] = 5409; // weapon whip
                definition.modelId[4] = 13307; // Gloves barrows
                definition.modelId[6] = 3704; // boots climbing
                definition.modelId[9] = 290; // amulet glory
                break;
            case 1012:
                definition.name = "Angelic gambler";
                definition.combatLevel = 183;
                definition.actions = new String[]{"Talk-to", null, "Gamble-Colorful", null, null, null, null};
                final int[] oldModelIds7 = definition.modelId;
                final int[] newModelsId7 = new int[oldModelIds7.length + 1];
                System.arraycopy(oldModelIds7, 0, newModelsId7, 1, oldModelIds7.length);
                definition.modelId = newModelsId7;
                definition.modelId[0] = 45001; // angelic cape
                break;
//            case 1613:
//            case 1618:
//                definition.walkSequence = 2064;
//                definition.walkTurnSequence = 2064;
//                definition.walkTurnRightSequence = 2064;
//                definition.walkTurnLeftSequence = 2064;
//                final int[] oldModelIds8 = definition.modelId;
//                final int[] newModelsId8 = new int[oldModelIds8.length + 1];
//                System.arraycopy(oldModelIds8, 0, newModelsId8, 1, oldModelIds8.length);
//                definition.modelId = newModelsId8;
//                definition.modelId[0] = 366;
//                break;
            case 9000:
                definition.copy(lookup(3451));
                definition.name = "Xagthan";
                definition.actions = new String[]{"Exchange", null, null, null, null, null, null};
                break;
            case 9001: // Water elemental 2
                definition.copy(lookup(1370));
                definition.recolourTarget = new int[]{-22052, -22052};
                definition.recolourOriginal = new int[]{38350, 37460};
                break;
            case 9002: // Water wizard
                definition.copy(lookup(1557));
                definition.combatLevel = 54;
                definition.size = 2;
                definition.scaleXZ *= 1.25;
                definition.scaleY *= 1.20;
                break;
            case 5129:
                definition.combatLevel = 638;
                break;
            case 9003: // Water wizard 2
                definition.copy(lookup(1557));
                definition.combatLevel = 54;
                definition.size = 2;
                definition.scaleXZ *= 1.25;
                definition.scaleY *= 1.20;
                definition.recolourTarget = new int[]{-22052, -22052, 7700, -22052, 11200, -22052};
                definition.recolourOriginal = new int[]{-25047, -25047, -25706, -21612, -21612, -21568};
                break;
            case 9004: // Icefiend
                definition.copy(lookup(4813));
                definition.combatLevel = 75;
                definition.size = 2;
                definition.scaleXZ *= 1.55;
                definition.scaleY *= 1.30;
                break;
            case 9005: // Icefiend 2
                definition.copy(lookup(4813));
                definition.combatLevel = 75;
                definition.size = 2;
                definition.scaleXZ *= 1.55;
                definition.scaleY *= 1.30;
                definition.recolourTarget = new int[]{33354, 33354, 33354, 33354, -22052, -22052, -22052, 33364, -22052, -22052, 33368, 33368, 33368, -22052, -22052, -22052, -22052, -29744};
                definition.recolourOriginal = new int[]{33354, 33346, 33364, 33341, 33348, 33339, 33463, 33707, 33455, 334697, 33368, 34142, 32995, 33701, 33703, 33585, 33697, 34140};
                break;
            case 9006: // Hydro troll
                definition.copy(lookup(5824));
                definition.combatLevel = 117;
                definition.name = "Hydro troll";
                definition.recolourTarget = new int[]{-29744, 21570, 122};
                definition.recolourOriginal = new int[]{61, 21570, 6430};
                break;
            case 9007: // Hydro troll 2
                definition.copy(lookup(5824));
                definition.combatLevel = 117;
                definition.name = "Hydro troll";
                definition.recolourTarget = new int[]{-22052, 21570, 122};
                definition.recolourOriginal = new int[]{61, 21570, 6430};
                break;
            case 9008: // Ice spider
                definition.copy(lookup(3022));
                definition.combatLevel = 97;
                break;
            case 9009: // Ice spider 2
                definition.copy(lookup(3022));
                definition.combatLevel = 97;
                definition.recolourTarget = new int[]{-22052, -22052, -22052, -22052, -22052, -22052};
                definition.recolourOriginal = new int[]{33228, 33088, 33098, 33104, 33112, 34223};
                break;
            case 9010: // Ice giant
                definition.copy(lookup(7880));
                definition.combatLevel = 144;
                definition.size = 3;
                definition.scaleXZ *= 1.55;
                definition.scaleY *= 1.30;
                break;
            case 9011: // Ice giant 2
                definition.copy(lookup(7880));
                definition.combatLevel = 144;
                definition.size = 3;
                definition.scaleXZ *= 1.55;
                definition.scaleY *= 1.30;
                definition.recolourTarget = new int[]{-22052, -22052, -22052, -22052, -22052};
                definition.recolourOriginal = new int[]{33228, 33112, 33088, 33075, 33112};
                break;
            case 9012: // Armaros
                definition.copy(lookup(3477));
                definition.combatLevel = 244;
                definition.size = 3;
                definition.scaleXZ *= 1.55;
                definition.scaleY *= 1.30;
                definition.name = "Armaros";
                definition.recolourTarget = new int[]{-29744, -29744, -29744, -29744, -29744, -29744, -29744, -29744, -29744, -29744, 125, 125};
                definition.recolourOriginal = new int[]{3261, 3266, 301, 293, 3369, 3373, 3365, 3377, 202, 214, 206, 198};
                break;
            case 9013: // Armaros 22
                definition.copy(lookup(3477));
                definition.combatLevel = 244;
                definition.size = 3;
                definition.scaleXZ *= 1.55;
                definition.scaleY *= 1.30;
                definition.name = "Armaros";
                definition.recolourTarget = new int[]{-22052, -22052, -22052, -22052, -22052, -22052, -22052, -22052, -22052, -22052, 125, 125};
                definition.recolourOriginal = new int[]{3261, 3266, 301, 293, 3369, 3373, 3365, 3377, 202, 214, 206, 198};
                break;
            case 9014: // Hydro warrior
                definition.copy(lookup(2851));
                definition.combatLevel = 167;
                definition.size = 2;
                definition.scaleXZ *= 1.55;
                definition.scaleY *= 1.30;
                definition.name = "Hydro warrior";
                definition.recolourTarget = new int[]{-29744, 125, -29744, -29744, -29744, 120};
                definition.recolourOriginal = new int[]{61, 41, 4550, 57, 12, 24};
                break;
            case 9015: // Hydro warrior 2
                definition.copy(lookup(2851));
                definition.combatLevel = 167;
                definition.size = 2;
                definition.scaleXZ *= 1.55;
                definition.scaleY *= 1.30;
                definition.name = "Hydro warrior";
                definition.recolourTarget = new int[]{-22052, 125, -22052, -22052, -22052, 120};
                definition.recolourOriginal = new int[]{61, 41, 4550, 57, 12, 24};
                break;
            case 9016: // Krampus
                definition.copy(lookup(1443));
                definition.combatLevel = 381;
                definition.actions = new String[]{null, "Attack", null, null, null, null, null};
                definition.name = "Krampus";
                definition.recolourTarget = new int[]{-29744, 125, -29744, -29744, -29744, 120};
                definition.recolourOriginal = new int[]{910, 912, 1938, 1814, 1690, 0};
                break;
            case 9017: // Krampus 2
                definition.copy(lookup(1443));
                definition.combatLevel = 381;
                definition.actions = new String[]{null, "Attack", null, null, null, null, null};
                definition.name = "Krampus";
                definition.recolourTarget = new int[]{-22052, 125, -22052, -22052, -22052, 120};
                definition.recolourOriginal = new int[]{910, 912, 1938, 1814, 1690, 0};
                break;
            case 9018: // Nykur
                definition.copy(lookup(3474));
                definition.combatLevel = 421;
                definition.actions = new String[]{null, "Attack", null, null, null, null, null};
                definition.name = "Nykur";
                definition.recolourTarget = new int[]{120, 120, 120, 120, 120, -22052, 8390, 8369};
                definition.recolourOriginal = new int[]{284, 280, 404, 289, 165, 51078, 278, 274};
                break;
            case 9019: // Nykur 2
                definition.copy(lookup(3474));
                definition.combatLevel = 421;
                definition.actions = new String[]{null, "Attack", null, null, null, null, null};
                definition.name = "Nykur";
                definition.recolourTarget = new int[]{120, 120, 120, 120, 120, -22052, -22052, -22052};
                definition.recolourOriginal = new int[]{284, 280, 404, 289, 165, 51078, 278, 274};
                break;
            case 3462:
                definition.actions = new String[]{null, "Attack", null, null, null, null, null};
                definition.standAnim = 808;
                definition.walkSequence = 819;
                definition.walkTurnSequence = 808;
                definition.walkTurnRightSequence = 808;
                definition.walkTurnLeftSequence = 808;
                break;
            case 3463:
                definition.actions = new String[]{null, "Attack", null, null, null, null, null};
                definition.standAnim = 808;
                definition.walkSequence = 1660;
                definition.walkTurnSequence = 808;
                definition.walkTurnRightSequence = 808;
                definition.walkTurnLeftSequence = 808;
                break;
            case 3464:
                definition.actions = new String[]{null, "Attack", null, null, null, null, null};
                //definition.modelId[9] = 9642; // amulet fury
                //definition.modelId[4] = 13307; // Gloves barrows
                definition.standAnim = 813;
                definition.walkSequence = 1146;
                definition.walkTurnSequence = 813;
                definition.walkTurnRightSequence = 813;
                definition.walkTurnLeftSequence = 813;
                break;

            case 1200:
                definition.copy(lookup(1158));
                definition.modelId[7] = 539; // weapon dds
                break;
            case 8008:
                definition.walkSequence = 1684;
                break;
            case 4096:
                definition.name = "@or1@Archer bot";
                definition.combatLevel = 90;
                definition.actions = new String[]{null, "Attack", null, null, null, null, null};
                definition.modelId[0] = 20423; // cape avas
                definition.modelId[1] = 21873; // Head - neitznot
                definition.modelId[7] = 31237; // weapon crossbow
                definition.modelId[4] = 13307; // Gloves barrows
                definition.modelId[6] = 3704; // boots climbing
                definition.modelId[5] = 20139; // platelegs zammy hides
                definition.modelId[2] = 20157; // platebody zammy hides
                definition.standAnim = 7220;
                definition.walkSequence = 7223;
                definition.walkTurnSequence = 7220;
                definition.walkTurnRightSequence = 7220;
                definition.walkTurnLeftSequence = 7220;
                break;
            case 9020: // Merodach
                definition.copy(lookup(274));
                definition.combatLevel = 2000;
                definition.actions = new String[]{null, "Attack", null, "View Drop Table", null, null, null};
                definition.name = "Merodach";
                definition.scaleXZ *= 1.50;
                definition.scaleY *= 1.50;
                definition.recolourTarget = new int[]{127, 127, 127, 127, 127, 127};
                definition.recolourOriginal = new int[]{-22256,-27364,-27359,-27479,-27471,-27446};
                definition.size = 7;
                break;
            case 9021: // Brutal Sea Dragon
                definition.copy(lookup(6593));
                definition.combatLevel = 288;
                definition.actions = new String[]{null, "Attack", null, "View Drop Table", null, null, null};
                definition.name = "Brutal sea dragon";
                definition.scaleXZ *= 1.10;
                definition.scaleY *= 1.10;
                definition.recolourTarget = new int[]{-21059, 5219, 2469, 809, 142, 910, 5198, -21059, 1814, 5206, -27417, 2588, 5214};
                definition.recolourOriginal = new int[]{0, 5219, 2469, 809, 142, 910, 5198, 16, 1814, 5206, -27417, 2588, 5214};
                definition.retextureTarget = new int[]{24};
                definition.retextureOriginal = new int[]{40};
                break;
            case 1576:
                definition.actions = new String[]{"Trade", null, "Equipment", "Ammunition", null, null, null};
                break;
            case 3343:
                definition.actions = new String[]{"Trade", null, "Heal", null, null, null, null};
                break;
            case 506:
                definition.actions = new String[]{"Trade", null, null, null, null, null, null};
                break;
            case 308:
            case 315:
            case 316:
            case 317:
            case 7943:
                definition.actions = new String[]{"Talk-to", null, "Trade", "Sell Emblems", "Request Skull", null, null};
                break;

        }

//		final SecureRandom random = new SecureRandom();
//		final int[] options = new int[]{29616, 29624, 33102, 33114, 33103, 33111};
//		definition.modelId = new int[]{ options[random.nextInt(options.length-1)]};
        NpcDefinition_cached.put(definition, id);
        return definition;
    }

    public static int getTotalNpcs() {
        return Js5.configs.getRecordLength(9);
    }

    public static void clear() {
        NpcDefinition_cachedModels.clear();
        NpcDefinition_cached.clear();
    }

    private void copy(NpcDefinition copy) {
        size = copy.size;
        degreesToTurn = copy.degreesToTurn;
        standAnim = copy.standAnim;
        walkSequence = copy.walkSequence;
        walkTurnSequence = copy.walkTurnSequence;
        walkTurnLeftSequence = copy.walkTurnLeftSequence;
        walkTurnRightSequence = copy.walkTurnRightSequence;
        turnLeftSequence = copy.turnLeftSequence;
        turnRightSequence = copy.turnRightSequence;
        transformVarbit = copy.transformVarbit;
        trasnformVarp = copy.trasnformVarp;
        combatLevel = copy.combatLevel;
        name = copy.name;
        description = copy.description;
        headIcon = copy.headIcon;
        clickable = copy.clickable;
        lightModifier = copy.lightModifier;
        scaleY = copy.scaleY;
        scaleXZ = copy.scaleXZ;
        drawMinimapDot = copy.drawMinimapDot;
        shadowModifier = copy.shadowModifier;
		/*retextureOriginal = new int[copy.retextureOriginal.length];
		for (int i = 0; i < retextureOriginal.length; i++) {
			retextureOriginal[i] = copy.retextureOriginal[i];
		};
		retextureTarget = new int[copy.retextureTarget.length];
		for (int i = 0; i < retextureTarget.length; i++) {
			retextureTarget[i] = copy.retextureTarget[i];
		};*/
		/*recolourOriginal = new int[copy.recolourOriginal.length];
		for (int i = 0; i < recolourOriginal.length; i++) {
			recolourOriginal[i] = copy.recolourOriginal[i];
		};
		recolourTarget = new int[copy.recolourTarget.length];
		for (int i = 0; i < recolourTarget.length; i++) {
			recolourTarget[i] = copy.recolourTarget[i];
		};*/
        actions = new String[copy.actions.length];
        for (int i = 0; i < actions.length; i++) {
            actions[i] = copy.actions[i];
        }
        modelId = new int[copy.modelId.length];
        for (int i = 0; i < modelId.length; i++) {
            modelId[i] = copy.modelId[i];
        }
        priorityRender = copy.priorityRender;
    }

    private void fixSlide() {
        // Fix "slide" anim issue
        walkTurnSequence = walkSequence;
        walkTurnRightSequence = walkSequence;
        walkTurnLeftSequence = walkSequence;
    }

    public Model model() {
        if (transforms != null) {
            NpcDefinition entityDef = transform();
            if (entityDef == null)
                return null;
            else
                return entityDef.model();
        } else {
            ModelData data = getModelData(aditionalModels);
            if (data == null) {
                return null;
            }
            Model model = data.toModel(64 + lightModifier, shadowModifier * 5 + 850, -30, -50, -30);
            if (recolourOriginal != null) {
                for (int index = 0; index < recolourOriginal.length; index++)
                    model.recolor(recolourOriginal[index], recolourTarget[index]);
            }
            if (retextureOriginal != null) {
                for (int index = 0; index < retextureOriginal.length; index++)
                    model.retexture((short) retextureOriginal[index], (short) retextureTarget[index]);
            }
            return model;
        }
    }

    public NpcDefinition transform() {
        int var1 = -1;
        if (this.transformVarbit != -1) {
            var1 = OsCache.getVarbit(this.transformVarbit);
        } else if (this.trasnformVarp != -1) {
            var1 = Varps.Varps_main[this.trasnformVarp];
        }

        int var2;
        if (var1 >= 0 && var1 < this.transforms.length - 1) {
            var2 = this.transforms[var1];
        } else {
            var2 = this.transforms[this.transforms.length - 1];
        }

        return var2 != -1 ? lookup(var2) : null;
    }

    private static final boolean PRINT_MODEL_COLORS = false;

    public Model getModel(Animation var1, int var2, Animation var3, int var4) {
        try {
		if(this.transforms != null) {
			NpcDefinition var12 = this.transform();
			return var12 == null?null:var12.getModel(var1, var2, var3, var4);
		} else {
			Model var8 = (Model)NpcDefinition_cachedModels.get(id);
            if (var8 == null) {
                ModelData var9 = getModelData(modelId);
                if (var9 == null) {
                    return null;
                }

                var8 = var9.toModel(64 + lightModifier, shadowModifier * 5 + 850, -30, -50, -30);
                NpcDefinition_cachedModels.put(var8, id);
            }

			Model var11;
			if(var1 != null && var3 != null) {
				var11 = var1.applyTransformations(var8, var2, var3, var4);
			} else if(var1 != null) {
				var11 = var1.transformActorModel(var8, var2);
			} else if(var3 != null) {
				var11 = var3.transformActorModel(var8, var4);
			} else {
				var11 = var8.toSharedSequenceModel(true);
			}

			if(this.scaleXZ != 128 || this.scaleY != 128) {
				var11.scale(this.scaleXZ, this.scaleY, this.scaleXZ);
			}

			return var11;
		}
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
    public void readValues(Buffer stream) {
        int last = -1;
        do {
            int opCode = stream.readUnsignedByte();
            if (opCode == 0)
                return;
            int var4;
            if (opCode == 1) {
                int j = stream.readUnsignedByte();
                modelId = new int[j];
                for (int j1 = 0; j1 < j; j1++)
                    modelId[j1] = stream.readUnsignedShort();
            } else if (opCode == 2)
                name = stream.readStringCp1252NullTerminated();
            else if (opCode == 12) {
                size = (byte) stream.readUnsignedByte();
            } else if (opCode == 13)
                standAnim = stream.readUnsignedShort();
            else if (opCode == 14) {
                walkSequence = stream.readUnsignedShort();
            } else if (opCode == 15) {
                turnLeftSequence = stream.readUnsignedShort();
            } else if (opCode == 16) {
                turnRightSequence = stream.readUnsignedShort();
            } else if (opCode == 17) {
                walkSequence = stream.readUnsignedShort();
                walkTurnSequence = stream.readUnsignedShort();
                walkTurnLeftSequence = stream.readUnsignedShort();
                walkTurnRightSequence = stream.readUnsignedShort();
            } else if(opCode == 18){
                int category = stream.readUnsignedShort();
            } else if (opCode >= 30 && opCode < 35) {
                if (actions == null)
                    actions = new String[5];
                actions[opCode - 30] = stream.readStringCp1252NullTerminated();
                if (actions[opCode - 30].equalsIgnoreCase("hidden"))
                    actions[opCode - 30] = null;
            } else if (opCode == 40) {
                int colours = stream.readUnsignedByte();
                recolourOriginal = new int[colours];
                recolourTarget = new int[colours];
                for (int k1 = 0; k1 < colours; k1++) {
                    recolourOriginal[k1] = stream.readUnsignedShort();
                    recolourTarget[k1] = stream.readUnsignedShort();
                }
            } else if (opCode == 41) {
                int textures = stream.readUnsignedByte();
                retextureOriginal = new int[textures];
                retextureTarget = new int[textures];
                for (int k1 = 0; k1 < textures; k1++) {
                    retextureOriginal[k1] = stream.readUnsignedShort();
                    retextureTarget[k1] = stream.readUnsignedShort();
                }

            } else if (opCode == 60) {
                int additionalModelLen = stream.readUnsignedByte();
                aditionalModels = new int[additionalModelLen];
                for (int l1 = 0; l1 < additionalModelLen; l1++)
                    aditionalModels[l1] = stream.readUnsignedShort();

            } else if (opCode == 93)
                drawMinimapDot = false;
            else if (opCode == 95)
                combatLevel = stream.readUnsignedShort();
            else if (opCode == 97)
                scaleXZ = stream.readUnsignedShort();
            else if (opCode == 98)
                scaleY = stream.readUnsignedShort();
            else if (opCode == 99)
                priorityRender = true;
            else if (opCode == 100)
                lightModifier = stream.readByte();
            else if (opCode == 101)
                shadowModifier = stream.readByte();
            else {
                int var5;
                if (opCode == 102) {
                    headIcon = stream.readUnsignedByte();
                    var4 = 0;

                    for(var5 = headIcon; var5 != 0; var5 >>= 1) {
                        ++var4;
                    }

                    int[] headIconArchiveIds = new int[var4];
                    short[] headIconSpriteIndex = new short[var4];

                    for(int var6 = 0; var6 < var4; ++var6) {
                        if ((headIcon & 1 << var6) == 0) {
                            headIconArchiveIds[var6] = -1;
                            headIconSpriteIndex[var6] = -1;
                        } else {
                            headIconArchiveIds[var6] = stream.readNullableLargeSmart();
                            headIconSpriteIndex[var6] = (short)stream.readShortSmartSub();
                        }
                    }
                } else if (opCode == 103)
                    degreesToTurn = stream.readUnsignedShort();
                else if (opCode != 106 && opCode != 118) {
                    if (opCode == 107) {
                        clickable = false;
                    } else if (opCode == 109) {
                        boolean rotationFlag = false;
                    } else if (opCode == 111) {
                        boolean isPet = true;
                    } else if (opCode == 114) {
                        this.field2003 = stream.readUnsignedShort();
                    } else if (opCode == 115) {
                        this.field2003 = stream.readUnsignedShort();
                        this.field2004 = stream.readUnsignedShort();
                        this.field2005 = stream.readUnsignedShort();
                        this.field2006 = stream.readUnsignedShort();
                    } else if (opCode == 116) {
                        this.field2007 = stream.readUnsignedShort();
                    } else if (opCode == 117) {
                        this.field2007 = stream.readUnsignedShort();
                        this.field2008 = stream.readUnsignedShort();
                        this.field2009 = stream.readUnsignedShort();
                        this.field1989 = stream.readUnsignedShort();
                    } else if (opCode == 249) {
                        params = OSCollections.readStringIntParameters(stream, this.params);
                    }
                } else {
                    transformVarbit = stream.readUnsignedShort();
                    if (transformVarbit == 65535)
                        transformVarbit = -1;
                    trasnformVarp = stream.readUnsignedShort();
                    if (trasnformVarp == 65535)
                        trasnformVarp = -1;
                    int var3 = -1;
                    if (opCode == 118)
                        var3 = stream.readUnsignedShort();

                    int childCount = stream.readUnsignedByte();
                    transforms = new int[childCount + 2];
                    for (int i2 = 0; i2 <= childCount; i2++) {
                        transforms[i2] = stream.readUnsignedShort();
                        if (transforms[i2] == 65535)
                            transforms[i2] = -1;
                    }
                    transforms[childCount + 1] = var3;
                }
            }
            last = opCode;
        } while (true);
    }

    public ModelData getModelData(int[] var1) {
        int[] var3 = var1;

        if (var3 == null) {
            return null;
        } else {
            boolean var4 = false;
            for (int var5 = 0; var5 < var3.length; ++var5) {
                if (var3[var5] != -1 && !Js5.models.tryLoadRecord(var3[var5], 0)) {
                    var4 = true;
                }
            }
            if (var4) {
                return null;
            } else {
                ModelData[] var9 = new ModelData[var3.length];

                for(int var6 = 0; var6 < var3.length; ++var6) {
                    var9[var6] = ModelData.ModelData_get(Js5.models, var3[var6], 0);
                }

                ModelData var10;
                if (var9.length == 1) {
                    var10 = var9[0];
                    if (var10 == null) {
                        var10 = new ModelData(var9, var9.length);
                    }
                } else {
                    var10 = new ModelData(var9, var9.length);
                }

                int[] var7;
                int var8;
                if (this.recolourOriginal != null) {
                    var7 = this.recolourTarget;
                    /*if (var2 != null && var2.field1983 != null) {
                        var7 = var2.field1983;
                    }*/

                    for(var8 = 0; var8 < this.recolourOriginal.length; ++var8) {
                        var10.recolor((short) this.recolourOriginal[var8], (short) var7[var8]);
                    }
                }

                if (this.retextureOriginal != null) {
                    var7 = this.retextureTarget;
                    /*if (var2 != null && var2.field1984 != null) {
                        var7 = var2.field1984;
                    }*/

                    for(var8 = 0; var8 < this.retextureOriginal.length; ++var8) {
                        var10.retexture((short) this.retextureOriginal[var8], (short) var7[var8]);
                    }
                }

                return var10;
            }
        }
    }

    public final ModelData method3670() {
        if (this.transforms != null) {
            NpcDefinition var2 = this.transform();
            return var2 == null ? null : var2.method3670();
        } else {
            return this.getModelData(this.aditionalModels);
        }
    }

}
