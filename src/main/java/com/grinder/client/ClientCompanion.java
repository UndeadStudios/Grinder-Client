package com.grinder.client;

import com.grinder.model.Console;
import com.grinder.particle.ParticleSystem;
import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.draw.GraphicsBuffer;
import com.runescape.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 12/12/2019
 */
public class ClientCompanion {

    public static final int TOTAL_ARCHIVE_CRCS = 9;
    public static final int[][] PLAYER_BODY_RECOLOURS = { // *** IF MODIFYING THIS MAKE SURE TO MODIFY PLAYER LOADING SERVER SIDE TO HAVE CORRECT ARRAY LENGTH ***
            { // Head/Beard
                6798, 107, 10283, 16, 4797, 7744, 5799, 4634, 33697, 22433,
                2983, 54193, 6798, 107, 10283, 16, 4797, 7744, 5799, 4634,
                33697, 22433, 2983, 54193
            }, { // Top
                8741, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153,
                56621, 4783, 1341, 16578, 35003, 25239
            }, { // Bottom
                25238, 8742, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094,
                10153, 56621, 4783, 1341, 16578, 35003
            }, { // Shoes
                4626, 11146, 6439, 12, 4758, 10270
            }, { // Skin
                4550, 4537, 5681, 5673, 5790, 6806, 8076, 4574,
                // Custom skin colors
                20165, 43678, 16895, 28416, 12231, 947 ,60359, 32433
            }
        };
    public static final int[] tabInterfaceIDs = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1 };
    public static final int[] anIntArray1204 = { 9104, 10275, 7595, 3610, 7975, 8526, 918, 38802, 24466, 10145, 58654,
            5027, 1457, 16565, 34991, 25486 };
    public static final int INSTRUCTION_CURRENT_LEVEL = 1;
    public static final int INSTRUCTION_MAX_LEVEL = 2;
    public static final int INSTRUCTION_EXPERIENCE = 3;
    public static final int INSTRUCTION_ITEM_AMOUNT = 4;
    public static final int INSTRUCTION_CLIENT_SETTING = 5;
    public static final int INSTRUCTION_COMBAT_LEVEL = 8;
    public static final int INSTRUCTION_TOTAL_LEVEL = 9;
    public static final int VALUE_LESS_OR_EQUAL = 2;
    public static final int VALUE_GREATER_OR_EQUAL = 3;
    public static final int VALUE_EQUAL = 4;
    public static final String NULLED_MAC = "null";
    public static final String MAC_DENIED_ACCESS = "deniedAccess";
    public static final String MAC_EXCEPTION = "exception";
    public final static NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);
    public final static int[] HITMARKS_562 = {31, 32, 33, 34};
    public final static int[]
            TAB_CLICK_X = {38, 33, 33, 33, 33, 33, 38, 38, 33, 33, 33, 33, 33, 38};
    public final static int[] TAB_CLICK_START = {522, 560, 593, 625, 659, 692, 724, 522, 560, 593, 625, 659, 692, 724};
    public final static int[] TAB_CLICK_Y = {169, 169, 169, 169, 169, 169, 169, 467, 467, 467, 467, 467, 467, 467};
    public static final int PLAYER_OPTION_COUNT = 5; //
    public static boolean rsAlreadyLoaded;
    final static int[] IDs = { 1196, 1199, 1206, 1215, 1224, 1231, 1240, 1249, 1258, 1267, 1274, 1283, 1573, 1290, 1299,
            1308, 1315, 1324, 1333, 1340, 1349, 1358, 1367, 1374, 1381, 1388, 1397, 1404, 1583, 12038, 1414, 1421, 1430,
            1437, 1446, 1453, 1460, 1469, 15878, 1602, 1613, 1624, 7456, 1478, 1485, 1494, 1503, 1512, 1521, 1530, 1544,
            1553, 1563, 1593, 1635, 12426, 12436, 12446, 12456, 6004, 18471,
            /* Ancients */
            12940, 12988, 13036, 12902, 12862, 13046, 12964, 13012, 13054, 12920, 12882, 13062, 12952, 13000, 13070,
            12912, 12872, 13080, 12976, 13024, 13088, 12930, 12892, 13096 };
    public final static int[] runeChildren = { 1202, 1203, 1209, 1210, 1211, 1218, 1219, 1220, 1227, 1228, 1234, 1235, 1236,
                    1243, 1244, 1245, 1252, 1253, 1254, 1261, 1262, 1263, 1270, 1271, 1277, 1278, 1279, 1286, 1287, 1293, 1294,
                    1295, 1302, 1303, 1304, 1311, 1312, 1318, 1319, 1320, 1327, 1328, 1329, 1336, 1337, 1343, 1344, 1345, 1352,
                    1353, 1354, 1361, 1362, 1363, 1370, 1371, 1377, 1378, 1384, 1385, 1391, 1392, 1393, 1400, 1401, 1407, 1408,
                    1410, 1417, 1418, 1424, 1425, 1426, 1433, 1434, 1440, 1441, 1442, 1449, 1450, 1456, 1457, 1463, 1464, 1465,
                    1472, 1473, 1474, 1481, 1482, 1488, 1489, 1490, 1497, 1498, 1499, 1506, 1507, 1508, 1515, 1516, 1517, 1524,
                    1525, 1526, 1533, 1534, 1535, 1547, 1548, 1549, 1556, 1557, 1558, 1566, 1567, 1568, 1576, 1577, 1578, 1586,
                    1587, 1588, 1596, 1597, 1598, 1605, 1606, 1607, 1616, 1617, 1618, 1627, 1628, 1629, 1638, 1639, 1640, 6007,
                    6008, 6011, 8673, 8674, 12041, 12042, 12429, 12430, 12431, 12439, 12440, 12441, 12449, 12450, 12451, 12459,
                    12460, 15881, 15882, 15885, 18474, 18475, 18478 };

    public static final int[] SKILL_EXPERIENCE = {83, 174, 276, 388, 512, 650, 801, 969, 1154, 1358, 1584, 1833, 2107, 2411, 2746, 3115, 3523, 3973, 4470, 5018, 5624, 6291, 7028, 7842, 8740, 9730, 10824, 12031, 13363, 14833, 16456, 18247, 20224, 22406, 24815, 27473, 30408, 33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983, 75127, 83014, 91721, 101333, 111945, 123660, 136594, 150872, 166636, 184040, 203254, 224466, 247886, 273742, 302288, 333804, 368599, 407015, 449428, 496254, 547953, 605032, 668051, 737627, 814445, 899257, 992895, 1096278, 1210421, 1336443, 1475581, 1629200, 1798808, 1986068, 2192818, 2421087, 2673114, 2951373, 3258594, 3597792, 3972294, 4385776, 4842295, 5346332, 5902831, 6517253, 7195629, 7944614, 8771558, 9684577, 10692629, 11805606, 13034431, // 99
            14989595, // 100
            17238034, // 101
            19899587, // 102
            22995537, // 103
            26674823, // 104
            30676046, // 105
            35277453, // 106
            40216297, // 107
            46248742, // 108
            53186053, // 109
            61163961, // 110
            70338555, // 111
            80889338, // 112
            93022739, // 113
            106976150, // 114
            123022573, // 115
            135324830, // 116
            150210561, // 117
            165231618, // 118
            181754779, // 119
            200_000_000, // 120 (The cap limit for combat stats)
            212_420_000, // 121
            227_108_620, // 122
            243_290_568, // 123
            259_719_625, // 124
            275_891_587, // 125
            291_080_746, // 126
            295_588_820, // 127
            308_747_702, // 128
            325_922_473, // 129
            342_514_720, // 130
            359_690_074, // 131
            376_059_082, // 132
            394_264_990, // 133
            413_891_489, // 134
            430_736_063, // 135
            449_522_866, // 136
            468_690_074, // 137
            487_059_082, // 138
            507_264_990, // 139
            526_891_489, // 140
            546_736_063, // 141
            565_522_866, // 142
            585_719_625, // 143
            606_891_587, // 144
            626_080_746, // 145
            646_588_820, // 146
            667_747_702, // 147
            689_922_473, // 148
            710_522_866, // 149
            732_690_074, // 150
            754_891_489, // 151
            777_736_063, // 152
            800_522_866, // 153
            823_690_074, // 154
            846_059_082, // 155
            869_264_990, // 156
            893_891_489, // 157
            917_736_063, // 158
            941_522_866, // 159
            965_719_625, // 160
            990_891_587, // 161
            1_014_262_568, // 162
            1_039_891_587, // 163
            1_064_080_746, // 164
            1_090_588_820, // 165
            1_115_747_702, // 166
            1_140_922_473, // 167
            1_166_522_866, // 168
            1_192_690_074, // 169 25.7
            1_217_515_998, // 170
            1_243_088_187, // 171
            1_269_120_046, // 172
            1_295_146_320, // 173
            1_322_787_742, // 174
            1_348_992_453, // 175
            1_375_102_816, // 176 - 26.6
            1_401_588_820, // 177
            1_428_747_702, // 178
            1_455_922_473, // 179
            1_482_522_866, // 180
            1_510_690_074, // 181
            1_537_515_998, // 182
            1_564_088_187, // 183
            1_592_120_046, // 184

            1_619_922_473, // 185
            1_647_522_866, // 186
            1_675_690_074, // 187
            1_703_515_998, // 188
            1_731_088_187, // 189
            1_759_120_046, // 190
            1_787_146_320, // 191
            1_815_262_568, // 192
            1_844_891_587, // 193
            1_872_080_746, // 194
            1_901_588_820, // 195
            1_930_747_702, // 196
            1_959_262_568, // 197
            1_988_891_587, // 198
            2_018_080_746, // 199

            2_048_401_227 // 200
/*            2_078_090_476, // 196
            2_108_526_480, // 197
            2_139_747_922, // 198
            2_147_112_018 // 199 - 30.0*/
    };

    public static final int ACHIEVEMENT_COMPLETION_DURATION = 250;
    public static final int ACHIEVEMENT_COMPLETION_HEIGHT = 86;
    public static final int ACHIEVEMENT_COMPLETION_SPEED = 4;
    public static final int MAX_SPLIT_PRIVATE_CHAT_WIDTH = 508;
    public static final int[] chatTypeViews = { 0, 5, 1, 2, 11, 3, 4 };
    private final static int LOAD_TITLE_SCREEN_STATE = 0;
    private final static int LOADED_TITLE_SCREEN_STATE = 5;
    private final static int LOADED_CACHE_STATE = 10;
    private final static int TWO_FACTOR_AUTHENTICATION_STATE = 11;
    private final static int CONNECTING_TO_SERVER_STATE = 20;
    private final static int LOADING_MAP_STATE = 25;
    private final static int LOADED_MAP_STATE = 30;
    private final static int CONNECTION_LOST_STATE = 40;
    private final static int WAITING_STATE = 45;

    public static final String validUserPassChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
    public final static int[] SIDE_ICONS_X = {17, 48, 83, 114, 146, 180, 214, 15, 49, 82, 116, 148, 183, 216};
    public final static int[] SIDE_ICONS_Y = {8, 7, 7, 4, 2, 3, 6, 303, 305, 305, 301, 304, 302, 305, 306};
    public final static int[] SIDE_ICONS_ID = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
    public final static int[] SIDE_ICONS_TAB = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
    public final static int[] TEXT_EFFECT_COLOURS = {0xffff00, 0xff0000, 65280, 65535, 0xff00ff, 0xffffff};
    public static double brightnessState = 0.8;
    public static boolean showTabComponents = true;
    public static boolean stackSideStones = false;
    public static boolean transparentTabArea = false;
    public static int openInterfaceId;
    public static int openInterfaceId2;
    public static int spellId = 0;
    public static Player dummyPlayer = new Player(); // A dummy player object used for character model on interfaces
    public static int tabId;
    public static ParticleSystem particleSystem = new ParticleSystem();
    public static int[] BIT_MASKS;
    public static int[] fullScreenTextureArray;
    /* Console */
    public static Console console = new Console();
    public static boolean shiftDown;
    public static boolean enableGridOverlay;
    public static boolean lowMemory = false;
    public static GraphicsBuffer gameScreenImageProducer;
    public static int anInt197;
    public static int anInt1408;
    public static int anInt849;
    public static int anInt854;
    public static int objectOption5ActionCount;
    public static int nodeID = 10;
    public static boolean isMembers = true;
    public static int playerOption2ActionCount;
    public static int anInt1005;
    public static int anInt1051;
    public static boolean tabAreaAltered;
    public static boolean firstRender = false;
    public static int portOffset;
    public static int anInt1117;
    public static int npcOption3ActionCount;
    public static int anInt1142;
    public static int npcOption4ActionCount;
    public static int bank10ActionCount;
    public static int[] anIntArray1180;
    public static int[] anIntArray1181;
    public static int[] anIntArray1182;
    public static int playerOptionActionCount;
    public static int npcOption2ActionCount;
    public static int walkActionCount;
    public static boolean removeShiftDropOnMenuOpen;
    public static int overlayItemAmount;
    public static int overlayItemX;
    public static int overlayItemY;
    public static int overlayItemInterfaceId;
    public static boolean overlayItemHideAmount;
    public static Sprite overlayItemIcon;
    public static int blackJackTimer;
    public static int interfaceInputSelected = -1;
    public static volatile boolean aBoolean831;
    public static boolean genericLoadingError;
    public static boolean loadingError;

    static {
        BIT_MASKS = new int[32];
        int i = 2;
        for (int k = 0; k < 32; k++) {
            BIT_MASKS[k] = i - 1;
            i += i;
        }
    }

}
