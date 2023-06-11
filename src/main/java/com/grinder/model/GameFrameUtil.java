package com.grinder.model;

import com.grinder.client.ClientCompanion;
import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.cache.graphics.widget.Widget;
import com.runescape.input.MouseHandler;

public class GameFrameUtil {

    static boolean inRegion(int minXOffset, int maxXOffset, int minY, int maxY) {
        return MouseHandler.x >= ClientUI.frameWidth - minXOffset
                && MouseHandler.x <= ClientUI.frameWidth - maxXOffset
                && MouseHandler.y >= minY
                && MouseHandler.y <= maxY;
    }

    static boolean isHoveringCompass(boolean fixed) {
        return fixed ? MouseHandler.x >= 545
                && MouseHandler.x <= 576
                && MouseHandler.y >= 4
                && MouseHandler.y <= 36
                : inRegion(177, 143, 4, 38);
    }

    public static void shiftMenuOptions(Client client) {
        boolean flag = false;
        while (!flag) {
            flag = true;
            for (int j = 0; j < client.menuOptionsCount - 1; j++) {
                if (client.menuOpcodes[j] < 1000 && client.menuOpcodes[j + 1] > 1000) {
                    String s = client.menuActions[j];
                    client.menuActions[j] = client.menuActions[j + 1];
                    client.menuActions[j + 1] = s;
                    int actionId = client.menuOpcodes[j];
                    client.menuOpcodes[j] = client.menuOpcodes[j + 1];
                    client.menuOpcodes[j + 1] = actionId;
                    actionId = client.menuArguments1[j];
                    client.menuArguments1[j] = client.menuArguments1[j + 1];
                    client.menuArguments1[j + 1] = actionId;
                    actionId = client.menuArguments2[j];
                    client.menuArguments2[j] = client.menuArguments2[j + 1];
                    client.menuArguments2[j + 1] = actionId;
                    long id = client.menuArguments0[j];
                    client.menuArguments0[j] = client.menuArguments0[j + 1];
                    client.menuArguments0[j + 1] = id;
                    flag = false;
                }
            }
        }
    }

    static boolean insideTopRightGameFrame(Client client) {
        return MouseHandler.x >= ClientUI.frameWidth - 259 && MouseHandler.y < ClientUI.frameHeight - GameFrame.getTabAreaHeight(client);
    }

    static boolean isHoveringRightClickChatButtons() {
        return MouseHandler.x >= 5
                && MouseHandler.y >= ClientUI.frameHeight - 23
                && MouseHandler.x < 516
                && MouseHandler.y < ClientUI.frameHeight - 1;
    }

    static Widget getWidget(int openInterfaceId) {
        return Widget.interfaceCache[openInterfaceId];
    }

    static Widget getOpenWidget() {
        return Widget.interfaceCache[ClientCompanion.openInterfaceId];
    }
}
