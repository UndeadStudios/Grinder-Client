package com.grinder.client.util;

import com.runescape.Client;

/**
 * Contains magic constants representative of action opcodes.
 *
 * For configuring the opcodes
 * @see Client#addMenuAction(int, int, int, long, String)
 *
 * For handling of the opcodes:
 * @see Client#processMenuActions(int, int, int, int, long, String)
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 26/03/2020
 */
public final class MenuOpcodes {

    public static final int ADD_IGNORE = 42;
    public static final int ITEM_CONTAINER_ACTION_5 = 53;
    public static final int USE_ITEM_ON_OBJECT = 62;
    public static final int ITEM_ACTION_1 = 74;
    public static final int ITEM_CONTAINER_ACTION_2 = 78;
    public static final int USE_SPELL_ON_GROUND_ITEM = 94;
    public static final int SET_AUTO_CAST = 104;
    public static final int TOOLTIP = 169;

    public static final int CLOSE = 200;
    public static final int PICKUP_ITEM_ON_GROUND = 234;
    public static final int SET_XP_DROPS = 257;
    public static final int TOGGLE_XP_DROPS = 258;

    public static final int SELECT_CHILD = 315;
    public static final int REMOVE_IGNORE = 322;
    public static final int ADD_FRIEND = 337;

    public static final int USE_SPELL_ON_NPC = 413;
    public static final int ITEM_CONTAINER_ACTION_4 = 431;
    public static final int ITEM_CONTAINER_ACTION_6 = 432;
    public static final int BANK_MODIFY_AMOUNT = 433;
    public static final int ADD_PLACEHOLDER = 434;
    public static final int USE_ITEM = 447;
    public static final int ITEM_ACTION_2 = 454;
    public static final int TOGGLE_XP_LOCK = 476;
    public static final int ITEM_ACTION_4 = 493;

    public static final int USE_ITEM__ON__ITEM_ON_GROUND = 511;
    public static final int TELEPORT_OR_WALK_HERE = 519;
    public static final int ITEM_ACTION_3 = 539;
    public static final int USE_SPELL_ON_ITEM = 543;
    public static final int USE_ITEM_ON_NPC = 582;

    public static final int CHAT_VIEW_TOOLS = 606;

    public static final int SELECT_SPELL = 626;
    public static final int ITEM_CONTAINER_ACTION_1 = 632;
    public static final int RELEASE_PLACEHOLDER = 633;
    public static final int SET_CONFIG = 646;
    public static final int CONTINUE_DIALOGUE = 679;
    public static final int SET_COMPASS_NORTH = 696;
    public static final int OPEN_NEWS = 697;
    public static final int OPEN_BROADCAST_URL = 698;

    public static final int LOGOUT = 700;
    public static final int DROPDOWN = 769;
    public static final int REMOVE_FRIEND = 792;

    public static final int ITEM_ACTION_5 = 847;
    public static final int OPEN_WORLD_MAP = 850;
    public static final int USE_SPECIAL_ATTACK = 851;
    public static final int ITEM_CONTAINER_ACTION_3 = 867;
    public static final int USE_ITEM_ON_ITEM = 870;

    public static final int CHAT_YELL_SWITCH = 974;
    public static final int CHAT_YELL_ON = 975;
    public static final int CHAT_YELL_OFF = 976;

    public static final int CHAT_TRADE_SWITCH = 984;
    public static final int CHAT_TRADE_SHOW_ALL = 985;
    public static final int CHAT_TRADE_SHOW_FRIENDS = 986;
    public static final int CHAT_TRADE_OFF = 987;

    public static final int CHAT_PRIVATE_SWITCH = 989;
    public static final int CHAT_PRIVATE_SHOW_ALL = 990;
    public static final int CHAT_PRIVATE_SHOW_FRIENDS = 991;
    public static final int CHAT_PRIVATE_OFF = 992;

    public static final int CHAT_PUBLIC_SWITCH = 993;
    public static final int CHAT_PUBLIC_SHOW_ALL = 994;
    public static final int CHAT_PUBLIC_SHOW_FRIENDS = 995;
    public static final int CHAT_PUBLIC_OFF = 996;
    public static final int CHAT_PUBLIC_HIDE = 997;

    public static final int CHAT_MODE_GAME_SWITCH = 998;
    public static final int CHAT_MODE_ALL_SWITCH = 999;

    public static final int CHAT_CLAN_SWITCH = 1000;
    public static final int CHAT_CLAN_SHOW_ALL = 1001;
    public static final int CHAT_CLAN_SHOW_FRIENDS = 1002;
    public static final int CHAT_CLAN_OFF = 1003;

    public static final int EXAMINE_NPC = 1025;
    public static final int TOGGLE_RUN = 1050;

    public static final int CANCEL = 1107;
    public static final int EXAMINE_ITEM = 1125;
    public static final int EXAMINE_OBJECT = 1226;

    public static final int TOGGLE_QUICK_PRAYERS = 1500;
    public static final int SET_QUICK_PRAYERS = 1506;
    public static final int TOGGLE_HEALTH_ABOVE_HEAD = 1508;
    public static final int TOGGLE_EMOJI_MENU = 1510;
    public static final int SELECT_EMOJI = 1511;
    public static final int RESIZE_CHAT_AREA = 1523;

    public static final int TOGGLE_BUTTON = 1997;
    public static final int EDIT_OBJECT = 1998;
    public static final int PUNISH_PLAYER = 1999;

    public static final int SPLIT_CHAT_ADD_IGNORE = 2042;
    public static final int SPLIT_CHAT_ADD_FRIEND = 2337;
    public static final int SPLIT_CHAT_MESSAGE = 2639;

}
