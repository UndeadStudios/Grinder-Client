package com.runescape.input;

import com.grinder.model.ChatBox;
import com.grinder.model.Emojis;
import com.grinder.ui.ClientUI;
import com.runescape.Client;

public class MouseUtil {

    public static boolean getMousePositions(Client client) {
        if (client.mouseInRegion(ClientUI.frameWidth - (ClientUI.frameWidth <= 1000 ? 240 : 420), ClientUI.frameWidth,
                ClientUI.frameHeight - (ClientUI.frameWidth <= 1000 ? 90 : 37), ClientUI.frameHeight)) {
            return false;
        }
        if (client.displayChatComponents()) {
            if (ChatBox.changeChatArea && ClientUI.frameMode != Client.ScreenMode.FIXED) {
                if (Emojis.isHoveringMenu()) {
                    return false;
                } else if (MouseHandler.x > 0 && MouseHandler.x < 494 && MouseHandler.y > ClientUI.frameHeight - 175
                        && MouseHandler.y < ClientUI.frameHeight) {
                    return true;
                } else {
                    if (MouseHandler.x > 494 && MouseHandler.x < 515 && MouseHandler.y > ClientUI.frameHeight - 175
                            && MouseHandler.y < ClientUI.frameHeight) {
                        return false;
                    }
                }
            } else if (!ChatBox.changeChatArea) {
                if (MouseHandler.x > 0 && MouseHandler.x < 519 && MouseHandler.y > ClientUI.frameHeight - 175
                        && MouseHandler.y < ClientUI.frameHeight) {
                    return false;
                }
            }
        }
        if (client.mouseInRegion(ClientUI.frameWidth - 216, ClientUI.frameWidth, 0, 172)) {
            return false;
        }
        if (!client.displaySideStonesStacked()) {
            if (MouseHandler.x > 0 && MouseHandler.y > 0 && MouseHandler.y < ClientUI.frameWidth && MouseHandler.y < ClientUI.frameHeight) {
                return MouseHandler.x < ClientUI.frameWidth - 242 || MouseHandler.y < ClientUI.frameHeight - 335;
            }
            return false;
        }
        if (client.displayTabComponents()) {
            if (ClientUI.frameWidth > 1000) {
                return (MouseHandler.x < ClientUI.frameWidth - 420 || MouseHandler.x > ClientUI.frameWidth || MouseHandler.y < ClientUI.frameHeight - 37
                        || MouseHandler.y > ClientUI.frameHeight)
                        && !client.insideArea(ClientUI.frameWidth - 225, ClientUI.frameWidth, 37, 274, ClientUI.frameHeight);
            } else {
                return (MouseHandler.x < ClientUI.frameWidth - 210 || MouseHandler.x > ClientUI.frameWidth || MouseHandler.y < ClientUI.frameHeight - 74
                        || MouseHandler.y > ClientUI.frameHeight)
                        && !client.insideArea(ClientUI.frameWidth - 225, ClientUI.frameWidth, 74, 274, ClientUI.frameHeight);
            }
        }
        return true;
    }
}
