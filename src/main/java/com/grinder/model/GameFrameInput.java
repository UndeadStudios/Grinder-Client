package com.grinder.model;

import com.grinder.Configuration;
import com.grinder.client.ClientCompanion;
import com.grinder.client.ClientUtil;
import com.grinder.client.util.MenuOpcodes;
import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.cache.graphics.widget.Widget;
import com.runescape.input.MouseHandler;
import com.runescape.input.MouseUtil;

public final class GameFrameInput {

    public static boolean hoveringHPOrb;
    public static boolean hoveringSpecialOrb;
    public static boolean hoveringWorldOrb;
    public static boolean hoveringNewsOrb;
    public static boolean hoveringXPCounterOrb;
    public static boolean hoveringPrayerOrb;
    public static boolean hoveringEnergyOrb;

    public static boolean clickedPrayerOrb;

    public static void processRightClick(Client client) {

        if (ChatBox.isResizingChatArea() || client.itemDragType != Widget.ITEM_DRAG_TYPE_NONE)
            return;

        MenuBuilder.setMenuAction(client, 0, MenuOpcodes.CANCEL, "Cancel");

        client.childWidgetHoverType = 0;
        client.childWidgetId = 0;

        if (client.fullscreenInterfaceID != -1) {
            if (ClientCompanion.openInterfaceId != -1)
                MenuBuilder.buildInterfaceMenu(client, GameFrameUtil.getOpenWidget(), 0, 0, MouseHandler.x, MouseHandler.y, 0);
            if (client.childWidgetHoverType != client.previousChildWidgetHoverType)
                client.previousChildWidgetHoverType = client.childWidgetHoverType;
            if (client.childWidgetId != client.previousChildWidgetId)
                client.previousChildWidgetId = client.childWidgetId;
            return;
        }

        final boolean fixed = ClientUI.frameMode == Client.ScreenMode.FIXED;

        if (fixed) {
            if (client.mouseInRegion(5, 515, 5, 337)) {
                if (ClientCompanion.openInterfaceId2 != -1)
                    MenuBuilder.buildInterfaceMenu(client, GameFrameUtil.getWidget(ClientCompanion.openInterfaceId2), 4, 4, MouseHandler.x, MouseHandler.y, 0);
                else if (ClientCompanion.openInterfaceId != -1)
                    MenuBuilder.buildInterfaceMenu(client, GameFrameUtil.getOpenWidget(), 4, 4, MouseHandler.x, MouseHandler.y, 0);
                else
                    MenuBuilder.buildGameMenu(client);
            }
        } else {
            int w = 512, h = 334;
            int x = (ClientUI.frameWidth / 2) - 256, y = (ClientUI.frameHeight / 2) - 167;
            int x2 = (ClientUI.frameWidth / 2) + 256, y2 = (ClientUI.frameHeight / 2) + 167;
            int count = client.displaySideStonesStacked() ? 3 : 4;
            if (ClientCompanion.openInterfaceId != -1 && (GameFrameUtil.getOpenWidget().width > w || GameFrameUtil.getOpenWidget().height > h)) {
                x = ClientUtil.getLargeResizableInterfaceOffsetLeftX();
                x2 = ClientUtil.getLargeResizableInterfaceOffsetRightX();
                y = ClientUtil.getLargeResizableInterfaceOffsetTopY();
                y2 = ClientUtil.getLargeResizableInterfaceOffsetBottomY();
            }
            for (int i = 0; i < count; i++) {
                if (x + w > (ClientUI.frameWidth - 225))
                    x = Math.max(0, x - 30);
                if (y + h > (ClientUI.frameHeight - 182))
                    y = Math.max(0, y - 30);
            }
            boolean inInterfaceRegion = ClientCompanion.openInterfaceId != -1 && MouseHandler.x > x && MouseHandler.y > y && MouseHandler.x < x2 && MouseHandler.y < y2;
            if (inInterfaceRegion || MouseUtil.getMousePositions(client)) {
                if (ClientCompanion.openInterfaceId2 != -1 && MouseHandler.x > x && MouseHandler.y > y && MouseHandler.x < x2 && MouseHandler.y < y2) {
                    MenuBuilder.buildInterfaceMenu(client, GameFrameUtil.getWidget(ClientCompanion.openInterfaceId2), x, y, MouseHandler.x, MouseHandler.y, 0);
                } else if (ClientCompanion.openInterfaceId != -1 && MouseHandler.x > x && MouseHandler.y > y && MouseHandler.x < x2 && MouseHandler.y < y2) {
                    MenuBuilder.buildInterfaceMenu(client, GameFrameUtil.getOpenWidget(), x, y, MouseHandler.x, MouseHandler.y, 0);
                } else {
                    MenuBuilder.buildGameMenu(client);
                }
            }
        }
        if (client.childWidgetHoverType != client.previousChildWidgetHoverType)
            client.previousChildWidgetHoverType = client.childWidgetHoverType;

        if (client.childWidgetId != client.previousChildWidgetId)
            client.previousChildWidgetId = client.childWidgetId;

        if (client.displayChatComponents()) {
            MenuBuilder.buildSplitPrivateChatMenu(client);
        }
        client.childWidgetHoverType = 0;
        client.childWidgetId = 0;
        if (!client.displaySideStonesStacked()) {
            final int yOffset = ChatBox.getDrawOffset(fixed, 0, ClientUI.frameHeight, 503);
            final int xOffset = ChatBox.getDrawOffset(fixed, 0, ClientUI.frameWidth, 765);

            if (client.mouseInRegion(547 + xOffset, 739 + xOffset, 206 + yOffset, 467 + yOffset)) {
                Widget widgetContainingMenu = null;
                if (client.overlayInterfaceId != -1)
                    widgetContainingMenu = GameFrameUtil.getWidget(client.overlayInterfaceId);
                else if (ClientCompanion.tabInterfaceIDs[ClientCompanion.tabId] != -1)
                    widgetContainingMenu = GameFrameUtil.getWidget(ClientCompanion.tabInterfaceIDs[ClientCompanion.tabId]);
                if (widgetContainingMenu != null)
                    MenuBuilder.buildInterfaceMenu(client, widgetContainingMenu, 547 + xOffset, 205 + yOffset, MouseHandler.x, MouseHandler.y, 0);
            }
        } else {
            final int yOffset = ClientUI.frameWidth >= 1000 ? 37 : 74;
            if (MouseHandler.x > ClientUI.frameWidth - 197
                    && MouseHandler.y > ClientUI.frameHeight - yOffset - 267
                    && MouseHandler.x < ClientUI.frameWidth - 7
                    && MouseHandler.y < ClientUI.frameHeight - yOffset - 7
                    && client.displayTabComponents()) {
                if (client.overlayInterfaceId != -1) {
                    MenuBuilder.buildInterfaceMenu(client, GameFrameUtil.getWidget(client.overlayInterfaceId), ClientUI.frameWidth - 197, ClientUI.frameHeight - yOffset - 267, MouseHandler.x,
                            MouseHandler.y, 0);
                } else if (ClientCompanion.tabInterfaceIDs[ClientCompanion.tabId] != -1) {
                    MenuBuilder.buildInterfaceMenu(client, GameFrameUtil.getWidget(ClientCompanion.tabInterfaceIDs[ClientCompanion.tabId]), ClientUI.frameWidth - 197, ClientUI.frameHeight - yOffset - 267, MouseHandler.x,
                            MouseHandler.y, 0);
                }
            }
        }
        if (client.childWidgetHoverType != client.previousChildWidgetHoverType2) {
            ClientCompanion.tabAreaAltered = true;
            client.previousChildWidgetHoverType2 = client.childWidgetHoverType;
        }
        if (client.childWidgetId != client.previousChildWidgetId2) {
            ClientCompanion.tabAreaAltered = true;
            client.previousChildWidgetId2 = client.childWidgetId;
        }
        client.childWidgetHoverType = 0;
        client.childWidgetId = 0;
        if (ClientUtil.isHoveringResizeChat(client))
            MenuBuilder.setMenuAction(client, 1, MenuOpcodes.RESIZE_CHAT_AREA, "Resize chat");

        if (Configuration.enableEmoticons && client.displayChatComponents() && !client.chatStateCheck()) {
            Emojis.buttons();
            if (Emojis.isHoveringButton())
                MenuBuilder.setMenuAction(client, 1, MenuOpcodes.TOGGLE_EMOJI_MENU, (Emojis.menuOpen ? "Hide" : "Show") + " emojis menu");
            else if (Emojis.menuOpen && Emojis.getHoveredIndex() != -1)
                MenuBuilder.setMenuAction(client, 1, MenuOpcodes.SELECT_EMOJI, "Select " + Emojis.getHoveredName(), 0, 0, Emojis.getHoveredIndex());
        }

        Achievements.buildAchievementsMenu(client);

        if (MouseHandler.x >= 10
                && MouseHandler.y >= (ChatBox.getDrawOffset(fixed, 338, ClientUI.frameHeight, 165)) - client.resizeChatOffset()
                && MouseHandler.x < 496
                && MouseHandler.y < (ChatBox.getDrawOffset(fixed, 463, ClientUI.frameHeight, 40))
                && client.displayChatComponents()) {
            if (client.backDialogueId != -1) {

                MenuBuilder.buildInterfaceMenu(client, GameFrameUtil.getWidget(client.backDialogueId), 20,
                        (ChatBox.getDrawOffset(fixed, 358, ClientUI.frameHeight, 145)), MouseHandler.x,
                        MouseHandler.y, 0);

            } else if (!client.isSearchOpen() && !client.messagePromptRaised
                    && MouseHandler.y >= (ChatBox.getDrawOffset(fixed, 345, ClientUI.frameHeight, 158)) - client.resizeChatOffset()
                    && MouseHandler.x < 496) {
                MenuBuilder.buildChatAreaMenu(client, MouseHandler.y - (ChatBox.getDrawOffset(fixed, 338, ClientUI.frameHeight, 165)));
            }
        }

        if (client.backDialogueId != -1 && client.childWidgetHoverType != client.previousBackDialogueChildWidgetHoverType) {
            ChatBox.setUpdateChatbox(true);
            client.previousBackDialogueChildWidgetHoverType = client.childWidgetHoverType;
        }

        if (client.backDialogueId != -1 && client.childWidgetId != client.previousBackDialogueChildWidgetId) {
            ChatBox.setUpdateChatbox(true);
            client.previousBackDialogueChildWidgetId = client.childWidgetId;
        }

        if (GameFrameUtil.isHoveringRightClickChatButtons())
            ChatBox.rightClickChatButtons(client);

        if (fixed || GameFrameUtil.insideTopRightGameFrame(client))
            processTopRightGameFrameHovers(client);
        else
            resetTopRightGameFrameHovers();

        GameFrameUtil.shiftMenuOptions(client);
    }

    private static void processTopRightGameFrameHovers(Client client) {

        if (ClientCompanion.openInterfaceId == 15244)
            return;

        checkTopRightGameFrameHovers(client);

        final boolean fixed = ClientUI.frameMode == Client.ScreenMode.FIXED;

        if (GameFrameUtil.isHoveringCompass(fixed))
            MenuBuilder.setMenuAction(client, 1, MenuOpcodes.SET_COMPASS_NORTH, "Look North");

        if (client.displaySideStonesStacked()) {
            if (GameFrameUtil.inRegion(26, 1, 2, 24)) {
                MenuBuilder.setMenuAction(client, 1, MenuOpcodes.LOGOUT, "Logout");
            }
        }

        if (!BroadCastManager.broadcastLink.isEmpty())
            MenuBuilder.setMenuAction(client, 1, MenuOpcodes.OPEN_BROADCAST_URL, "Open link");

        if (hoveringNewsOrb)
            MenuBuilder.setMenuAction(client, 1, MenuOpcodes.OPEN_NEWS, "View News/Updates");

        if (hoveringSpecialOrb && Configuration.enableSpecOrb)
            MenuBuilder.setMenuAction(client, 1, MenuOpcodes.USE_SPECIAL_ATTACK,  "Use @gre@Special Attack");

        if (hoveringXPCounterOrb) {
            final boolean isXpLocked = "@red@Locked".equals(GameFrameUtil.getWidget(31655).getDefaultText());
            MenuBuilder.setMenuAction(client, 1, MenuOpcodes.TOGGLE_XP_LOCK,  (isXpLocked ? "Unlock" : "Lock") + " @lre@XP lock");
            MenuBuilder.setMenuAction(client, 2, MenuOpcodes.SET_XP_DROPS,  "Setup @lre@XP drops");
            MenuBuilder.setMenuAction(client, 3, MenuOpcodes.TOGGLE_XP_DROPS,  (Configuration.xpCounterOpen ? "Hide" : "Show") + " @lre@XP drops");
        }

        if (Configuration.enablePrayerEnergyWorldOrbs) {
            if (hoveringPrayerOrb) {
                MenuBuilder.setMenuAction(client, 1, MenuOpcodes.SET_QUICK_PRAYERS, "Setup Quick-prayers");
                MenuBuilder.setMenuAction(client, 2, MenuOpcodes.TOGGLE_QUICK_PRAYERS, (clickedPrayerOrb ? "Deactivate" : "Activate") + " Quick-prayers");
            }
            if (hoveringEnergyOrb)
                MenuBuilder.setMenuAction(client, 1, MenuOpcodes.TOGGLE_RUN, "Toggle Run");

            if (hoveringWorldOrb)
                MenuBuilder.setMenuAction(client, 1, MenuOpcodes.OPEN_WORLD_MAP,  "Floating @lre@World Map");
        }
    }

    private static void checkTopRightGameFrameHovers(Client client) {

        final boolean fixed = ClientUI.frameMode == Client.ScreenMode.FIXED;
        final boolean specOrb = Configuration.enableSpecOrb;

        // Xp counter hover
        int orbX = GameFrame.getXDrawOffset(fixed, 516, 216);
        int orbY = 21;
        hoveringXPCounterOrb = client.mouseInRegion(orbX, orbX + 27 - 1, orbY, orbY + 27 - 1);

        /*
         * Left side orbs
         */
        int offsetX = (ChatBox.getDrawOffset(fixed, 516, ClientUI.frameWidth, 217)) + 3; // +3 on x and y to skip over orb borders like OSRS does
        int offsetY = 3;
        int orbWidth = 57 - 6 - 1; // -6 to account for skipping borders, -1 to account for mouseInRegion checking <= instead of <
        int orbHeight = 34 - 6 - 1;

        orbX = offsetX;
        orbY = offsetY + 41;
        hoveringHPOrb = !hoveringXPCounterOrb && client.mouseInRegion(orbX, orbX + orbWidth, orbY, orbY + orbHeight);

        int orbOffsetY = specOrb ? 0 : 11;
        orbX = offsetX;
        orbY = offsetY + 75 + orbOffsetY;
        hoveringPrayerOrb = client.mouseInRegion(orbX, orbX + orbWidth, orbY, orbY + orbHeight);

        int orbOffsetX = specOrb ? 0 : 13;
        orbOffsetY = specOrb ? 0 : 15;
        orbX = offsetX + 10 + orbOffsetX;
        orbY = offsetY + 107 + orbOffsetY;
        hoveringEnergyOrb = client.mouseInRegion(orbX, orbX + orbWidth, orbY, orbY + orbHeight);

        orbX = offsetX + 32;
        orbY = offsetY + 132;
        hoveringSpecialOrb = !hoveringEnergyOrb && Configuration.enableSpecOrb && client.mouseInRegion(orbX, orbX + orbWidth, orbY, orbY + orbHeight);

        /*
         * Right side orbs
         */
        offsetX = (ChatBox.getDrawOffset(fixed, 516, ClientUI.frameWidth, 217)) + 4; // +4 on x and y to skip over orb borders like OSRS does
        offsetY = 4;
        orbWidth = 30 - 8 - 1; // -8 to account for skipping borders, -1 to account for mouseInRegion checking <= instead of <
        orbHeight = 30 - 8 - 1;

        orbOffsetX = fixed ? offsetX + 196 : ClientUI.frameWidth - 34 + 3;
        orbOffsetY = fixed ? offsetY + 117 : 139 + 3;
        hoveringWorldOrb = client.mouseInRegion(orbOffsetX, orbOffsetX + orbWidth, orbOffsetY, orbOffsetY + orbHeight);

        orbOffsetX = fixed ? offsetX + 167 : ClientUI.frameWidth - 63 + 3;
        orbOffsetY = fixed ? offsetY + 135 : 157 + 3;
        hoveringNewsOrb = client.mouseInRegion(orbOffsetX, orbOffsetX + orbWidth, orbOffsetY, orbOffsetY + orbHeight);
    }

    private static void resetTopRightGameFrameHovers() {
        hoveringEnergyOrb =
                hoveringPrayerOrb =
                        hoveringHPOrb =
                                hoveringSpecialOrb =
                                        hoveringXPCounterOrb =
                                                hoveringWorldOrb =
                                                        hoveringNewsOrb = false;
    }
}
