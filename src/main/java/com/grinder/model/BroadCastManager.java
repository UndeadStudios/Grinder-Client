package com.grinder.model;

import com.runescape.input.KeyHandler;
import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.cache.graphics.sprite.SpriteLoader;
import com.runescape.cache.graphics.widget.Widget;
import com.runescape.util.MiscUtils;
import com.grinder.client.ClientCompanion;

import java.util.*;

/**
 * @version 1.0
 * @since 12/12/2019
 */
public class BroadCastManager {

    public static Map<String, String> broadcast = new LinkedHashMap<>();
    public static String broadcastLink = "";

    public static void handleBroadcastInputSelection(int key) {
        if (key == 9) {
            if (KeyHandler.pressedKeys[81]) {
                if (ClientCompanion.interfaceInputSelected == 35009) {
                    ClientCompanion.interfaceInputSelected = 35011;
                } else if (ClientCompanion.interfaceInputSelected == 35010) {
                    ClientCompanion.interfaceInputSelected = 35009;
                } else if (ClientCompanion.interfaceInputSelected == 35011) {
                    ClientCompanion.interfaceInputSelected = 35010;
                }
            } else {
                if (ClientCompanion.interfaceInputSelected == 35009) {
                    ClientCompanion.interfaceInputSelected = 35010;
                } else if (ClientCompanion.interfaceInputSelected == 35010) {
                    ClientCompanion.interfaceInputSelected = 35011;
                } else if (ClientCompanion.interfaceInputSelected == 35011) {
                    ClientCompanion.interfaceInputSelected = 35009;
                }
            }
        }
    }

    public static void drawBroadcast(Client client) {

        if (broadcast.isEmpty())
            return;

        int xOffset = 4;
        int yOffset = ClientUI.frameMode == Client.ScreenMode.FIXED ? 0 : ClientUI.frameHeight - 498;

        if (client.systemUpdateTime != 0)
            yOffset -= 14;

        String hoverLink = "";

        List<Map.Entry<String, String>> list = new ArrayList<>(broadcast.entrySet());

        Collections.reverse(list);

        for (Map.Entry<String, String> e : list) {
            StringBuilder message = new StringBuilder(e.getKey());

            boolean containsLink = !e.getValue().isEmpty();

            boolean linkHover = false;

            if (containsLink) {
                message.insert(0, "      ");
                SpriteLoader.getSprite(998).drawSprite(70, 317 + yOffset - client.resizeChatOffset());
                linkHover = client.mouseInRegion(xOffset, xOffset + client.regularText.getTextWidth(message.toString()) + 66, 329 + yOffset - client.resizeChatOffset() - 4, 329 + yOffset - client.resizeChatOffset() + 4);

                if (hoverLink.isEmpty() && linkHover) {
                    hoverLink = e.getValue();
                }
            }

            client.regularText.render(!client.menuOpen && linkHover ? 0xffffff : 0xffff00, "Broadcast: " + message.toString(), 329 + yOffset - client.resizeChatOffset(), xOffset);

            yOffset -= 14;
        }

        if (!client.menuOpen && !hoverLink.isEmpty()) {
            broadcastLink = hoverLink;
        } else if (!client.menuOpen) {
            broadcastLink = "";
        }
    }

    public static void handleClipboardInBroadcastMenu() {
        final Widget input = Widget.interfaceCache[ClientCompanion.interfaceInputSelected];
        final String data = MiscUtils.getClipboardData();

        if (!data.isEmpty()) {
            input.setDefaultText(data);
        }
    }
}
