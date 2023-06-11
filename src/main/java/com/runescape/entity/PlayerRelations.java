package com.runescape.entity;

import com.grinder.client.ClientCompanion;
import com.grinder.model.ChatBox;
import com.runescape.Client;
import com.runescape.cache.graphics.widget.Widget;
import com.runescape.util.StringUtils;

public class PlayerRelations {

    public static int[] friendsNodeIDs;
    public static long[] friendsListAsLongs;
    public static String[] friendsList;
    public static int friendsCount;
    public static int friendServerStatus;
    public static int ignoreCount;
    public static long[] ignoreListAsLongs;

    public static void handleAddFriendPacket(Client client, long encodedName, int world) {

        String name = StringUtils.formatText(StringUtils.decodeBase37(encodedName));

        for (int playerIndex = 0; playerIndex < friendsCount; playerIndex++) {
            if (encodedName != friendsListAsLongs[playerIndex])
                continue;
            if (friendsNodeIDs[playerIndex] != world) {
                friendsNodeIDs[playerIndex] = world;
                if (world >= 2) {
                    client.sendMessage(name + " has logged in.", ChatBox.CHAT_TYPE_LOGIN_PRIVATE, "");
                }
                if (world <= 1) {
                    client.sendMessage(name + " has logged out.", ChatBox.CHAT_TYPE_LOGIN_PRIVATE, "");
                }
            }
            name = null;

        }
        if (name != null && friendsCount < 200) {
            friendsListAsLongs[friendsCount] = encodedName;
            friendsList[friendsCount] = name;
            friendsNodeIDs[friendsCount] = world;
            friendsCount++;
        }
        for (boolean stopSorting = false; !stopSorting; ) {
            stopSorting = true;
            for (int friendIndex = 0; friendIndex < friendsCount - 1; friendIndex++) {
                if (friendsNodeIDs[friendIndex] != ClientCompanion.nodeID && friendsNodeIDs[friendIndex + 1] == ClientCompanion.nodeID
                        || friendsNodeIDs[friendIndex] == 0 && friendsNodeIDs[friendIndex + 1] != 0) {
                    int tempFriendNodeId = friendsNodeIDs[friendIndex];
                    friendsNodeIDs[friendIndex] = friendsNodeIDs[friendIndex + 1];
                    friendsNodeIDs[friendIndex + 1] = tempFriendNodeId;
                    String tempFriendName = friendsList[friendIndex];
                    friendsList[friendIndex] = friendsList[friendIndex + 1];
                    friendsList[friendIndex + 1] = tempFriendName;
                    long tempFriendLong = friendsListAsLongs[friendIndex];
                    friendsListAsLongs[friendIndex] = friendsListAsLongs[friendIndex + 1];
                    friendsListAsLongs[friendIndex + 1] = tempFriendLong;
                    stopSorting = false;
                }
            }
        }
    }

    public static void handleRemoveFriendPacket(long nameHash) {
        for (int i = 0; i < friendsCount; i++) {
            if (friendsListAsLongs[i] != nameHash) {
                continue;
            }

            friendsCount--;
            for (int n = i; n < friendsCount; n++) {
                friendsList[n] = friendsList[n + 1];
                friendsNodeIDs[n] = friendsNodeIDs[n + 1];
                friendsListAsLongs[n] = friendsListAsLongs[n + 1];
            }
            break;
        }
    }

    public static void handleAddIgnorePacket(long encodedName) {
        if (ignoreCount < 200) {
            ignoreListAsLongs[ignoreCount] = encodedName;
            ignoreCount++;
        }
    }

    public static void handleRemoveIgnorePacket(long nameHash) {
        for (int index = 0; index < ignoreCount; index++) {
            if (ignoreListAsLongs[index] == nameHash) {
                ignoreCount--;
                System.arraycopy(ignoreListAsLongs, index + 1, ignoreListAsLongs, index, ignoreCount - index);
                break;
            }
        }
    }


    public static boolean isIgnored(long encodedName) {
        boolean ignored = false;
        for (int index = 0; index < ignoreCount; index++) {
            if (ignoreListAsLongs[index] != encodedName)
                continue;
            ignored = true;
            break;
        }
        return ignored;
    }

    public static boolean buildFriendsListMenu(Client client, Widget class9) {
        int i = class9.contentType;
        if (i >= 1 && i <= 200 || i >= 701 && i <= 900) {
            if (i >= 801)
                i -= 701;
            else if (i >= 701)
                i -= 601;
            else if (i >= 101)
                i -= 101;
            else
                i--;
            client.menuActions[client.menuOptionsCount] = "Remove @whi@" + friendsList[i];
            client.menuOpcodes[client.menuOptionsCount] = 792;
            client.menuOptionsCount++;
            client.menuActions[client.menuOptionsCount] = "Message @whi@" + friendsList[i];
            client.menuOpcodes[client.menuOptionsCount] = 639;
            client.menuOptionsCount++;
            return true;
        }
        if (i >= 401 && i <= 500) {
            client.menuActions[client.menuOptionsCount] = "Remove @whi@" + class9.getDefaultText();
            client.menuOpcodes[client.menuOptionsCount] = 322;
            client.menuOptionsCount++;
            return true;
        } else {
            return false;
        }
    }
}
