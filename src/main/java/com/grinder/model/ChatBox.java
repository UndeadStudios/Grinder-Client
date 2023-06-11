package com.grinder.model;

import com.grinder.Configuration;
import com.grinder.client.ClientCompanion;
import com.grinder.client.ClientUtil;
import com.grinder.client.util.Log;
import com.grinder.client.util.MenuOpcodes;
import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.cache.graphics.GameFont;
import com.runescape.cache.graphics.RSFont;
import com.runescape.cache.graphics.widget.Widget;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.Rasterizer3D;
import com.runescape.input.MouseHandler;
import com.runescape.util.MiscUtils;
import com.runescape.util.StringUtils;

public final class ChatBox {

    private final static int[] CHANNEL_BUTTON_X_VALUES = {5, 69, 133, 197, 261, 325, 389, 453};

    private final static int[] CHANNEL_MODE_TEXT_X = {26, 84, 146, 206, 278, 329, 408, 469};
    private final static int[] CHANNEL_MODE_TEXT_Y = {157, 157, 152, 152, 152, 152, 152, 157};
    private final static String[] CHANNEL_MODE_TEXT = {"All", "Game", "Public", "Private", "Clan", "Requests", "Yell", "Tools"};

    private final static int[] CHANNEL_MODE_TEXT_VALUES_COLORS = {65280, 0xffff00, 0xff0000, 65535};
    private final static int[] CHANNEL_MODE_TEXT_VALUES_X = {160, 224, 288, 352, 416};
    private final static String[] CHANNEL_MODE_TEXT_VALUES = {"On", "Friends", "Off", "Hide"};
    private static final int BACKGROUND_SPRITE_ID = 20;
    private static final int DIVIDER_TEXTURE_SPRITE_ID = 79;
    private static final int CHATBOX_DRAG_DIAMONDS_SPRITE_ID = 949;
    private static final int CHANNEL_BUTTONS_SPRITE_ID = 49;

    private static int backgroundWidth = -1;
    private static int backgroundHeight = -1;

    public static final int CHAT_TYPE_ALL = 0;
    public static final int CHAT_TYPE_MOD_PUBLIC = 1;
    public static final int CHAT_TYPE_PUBLIC = 2;
    public static final int CHAT_TYPE_PRIVATE = 3;
    public static final int CHAT_TYPE_TRADE = 4;
    public static final int CHAT_TYPE_LOGIN_PRIVATE = 5;
    public static final int CHAT_TYPE_TO_PRIVATE = 6;
    public static final int CHAT_TYPE_FROM_MOD_PRIVATE = 7;
    public static final int CHAT_TYPE_DUEL = 8;
    public static final int CHAT_TYPE_CLAN = 9;
    public static final int CHAT_TYPE_YELL = 10;

    public static int chatScrollHeight = 114;
    /**
     * The height of the transparent chat box in resizable mode
     */
    public static int chatBoxHeight = chatScrollHeight;
    public static int chatScrollMax;
    public static int chatBoxScrollPosition;

    public static int channelButtonPos2;
    public static int channelButtonPos1;
    public static int privateChatMode;
    public static int publicChatMode;
    public static int clanChatMode;
    public static int tradeMode;
    public static int yellMode;


    public static boolean updateChatbox;
    public static boolean changeChatArea = false;
    public static boolean showChatComponents = true;

    private static boolean resizingChatArea;
    private static int resizingChatAreaStartHeight;
    private static int resizingChatAreaNewHeight;

    public static void resizeChatArea() {
        resizingChatArea = !resizingChatArea;
        resizingChatAreaStartHeight = chatBoxHeight;
        resizingChatAreaNewHeight = MouseHandler.y;
    }

    public static void stopResizingChatArea() {
        resizingChatArea = false;
    }

    public static void rightClickChatButtons(Client client) {
        if (MouseHandler.y >= ClientUI.frameHeight - 23 && MouseHandler.y < ClientUI.frameHeight - 1) {
            if (MouseHandler.x >= 5 && MouseHandler.x < 61) {
                MenuBuilder.setMenuAction(client, 1, MenuOpcodes.CHAT_MODE_ALL_SWITCH, "Switch tab");
            } else if (MouseHandler.x >= 69 && MouseHandler.x < 125) {
                MenuBuilder.setMenuAction(client, 1, MenuOpcodes.CHAT_MODE_GAME_SWITCH, "Switch tab");
            } else if (MouseHandler.x >= 133 && MouseHandler.x < 189) {
                MenuBuilder.setMenuAction(client, 1, MenuOpcodes.CHAT_PUBLIC_HIDE, "@yel@Public: @whi@Hide");
                MenuBuilder.setMenuAction(client, 2, MenuOpcodes.CHAT_PUBLIC_OFF, "@yel@Public: @whi@Off");
                MenuBuilder.setMenuAction(client, 3, MenuOpcodes.CHAT_PUBLIC_SHOW_FRIENDS, "@yel@Public: @whi@Show friends");
                MenuBuilder.setMenuAction(client, 4, MenuOpcodes.CHAT_PUBLIC_SHOW_ALL, "@yel@Public: @whi@Show all");
                MenuBuilder.setMenuAction(client, 5, MenuOpcodes.CHAT_PUBLIC_SWITCH, "Switch tab");
            } else if (MouseHandler.x >= 197 && MouseHandler.x < 253) {
                MenuBuilder.setMenuAction(client, 1, MenuOpcodes.CHAT_PRIVATE_OFF, "@yel@Private: @whi@Off");
                MenuBuilder.setMenuAction(client, 2, MenuOpcodes.CHAT_PRIVATE_SHOW_FRIENDS, "@yel@Private: @whi@Show friends");
                MenuBuilder.setMenuAction(client, 3, MenuOpcodes.CHAT_PRIVATE_SHOW_ALL, "@yel@Private: @whi@Show all");
                MenuBuilder.setMenuAction(client, 4, MenuOpcodes.CHAT_PRIVATE_SWITCH, "Switch tab");
            } else if (MouseHandler.x >= 261 && MouseHandler.x < 317) {
                MenuBuilder.setMenuAction(client, 1, MenuOpcodes.CHAT_CLAN_OFF, "@yel@Clan: @whi@Off");
                MenuBuilder.setMenuAction(client, 2, MenuOpcodes.CHAT_CLAN_SHOW_FRIENDS, "@yel@Clan: @whi@Show friends");
                MenuBuilder.setMenuAction(client, 3, MenuOpcodes.CHAT_CLAN_SHOW_ALL, "@yel@Clan: @whi@Show all");
                MenuBuilder.setMenuAction(client, 4, MenuOpcodes.CHAT_CLAN_SWITCH, "Switch tab");
            } else if (MouseHandler.x >= 325 && MouseHandler.x < 381) {
                MenuBuilder.setMenuAction(client, 1, MenuOpcodes.CHAT_TRADE_OFF, "@yel@Trade: @whi@Off");
                MenuBuilder.setMenuAction(client, 2, MenuOpcodes.CHAT_TRADE_SHOW_FRIENDS, "@yel@Trade: @whi@Show friends");
                MenuBuilder.setMenuAction(client, 3, MenuOpcodes.CHAT_TRADE_SHOW_ALL, "@yel@Trade: @whi@Show all");
                MenuBuilder.setMenuAction(client, 4, MenuOpcodes.CHAT_TRADE_SWITCH, "Switch tab");
            } else if (MouseHandler.x >= 389 && MouseHandler.x < 445) {
                MenuBuilder.setMenuAction(client, 1, MenuOpcodes.CHAT_YELL_OFF, "@yel@Yell: @whi@Off");
                MenuBuilder.setMenuAction(client, 2, MenuOpcodes.CHAT_YELL_ON, "@yel@Yell: @whi@On");
                MenuBuilder.setMenuAction(client, 3, MenuOpcodes.CHAT_YELL_SWITCH, "Switch tab");
            } else if (MouseHandler.x >= 453 && MouseHandler.x < 509) {
                MenuBuilder.setMenuAction(client, 1, MenuOpcodes.CHAT_VIEW_TOOLS, "View Tools");
            }
        }
    }

    public static void drawChatArea(Client client) {

        final boolean fixedSize = ClientUI.frameMode == Client.ScreenMode.FIXED;
        final int offsetY = getDrawOffset(ClientUI.frameMode == Client.ScreenMode.FIXED, 0, ClientUI.frameHeight, 165);

        if (fixedSize)
            Client.chatboxImageProducer.initDrawingArea();

        Rasterizer3D.scanOffsets = ClientCompanion.anIntArray1180;

        if (backgroundHeight == -1) backgroundHeight = Client.getSpriteHeight(BACKGROUND_SPRITE_ID);
        if (backgroundWidth == -1) backgroundWidth = Client.getSpriteWidth(BACKGROUND_SPRITE_ID);

        if (client.chatStateCheck()) {
            showChatComponents = true;
            Client.drawSprite(BACKGROUND_SPRITE_ID, 0, offsetY);
            Client.drawSprite(DIVIDER_TEXTURE_SPRITE_ID, 0, offsetY + backgroundHeight);
        }
        if (client.displayChatComponents()) {
            if ((changeChatArea && !fixedSize) && !client.chatStateCheck()) {
                if (client.isChatBoxResizable()) {
                    final boolean hovering = resizingChatArea || ClientUtil.isHoveringResizeChat(client);
                    Client.drawAdvancedSprite(CHATBOX_DRAG_DIAMONDS_SPRITE_ID, client.getResizeChatButtonX(), client.getResizeChatButtonY(), hovering ? 128 : 48);
                }
                Rasterizer2D.fillGradientHorizontalLine(7, 6 + offsetY - client.resizeChatOffset(), 505, 0x80c4c4c4, 0x0ac4c4c4);
                Rasterizer2D.fillGradientRectangle(0, 6 + offsetY - client.resizeChatOffset(), backgroundWidth, backgroundHeight - 6 + client.resizeChatOffset(), 0x00000000, 0x50000000);
                Client.drawAdvancedSprite(DIVIDER_TEXTURE_SPRITE_ID, 0, offsetY + backgroundHeight, 112);
            } else {
                Client.drawSprite(BACKGROUND_SPRITE_ID, 0, offsetY);
                Client.drawSprite(DIVIDER_TEXTURE_SPRITE_ID, 0, offsetY + backgroundHeight);
            }
        }

        drawChannelButtons(client.smallText, offsetY);

        if (client.messagePromptRaised) {
            client.newBoldFont.drawCenteredString(client.inputPromptTitle, 259, 60 + offsetY, 0, -1);
            String p = client.promptInput;
            if (Configuration.enableEmoticons && client.inputPromptTitle.contains("Enter a message to send to ")) {
                p = Emojis.Emoji.handleSyntax(p);
            }
            client.newBoldFont.drawCenteredString(p + "*", 259, 80 + offsetY, 128, -1);
        } else if (client.inputDialogState == 1) {
            client.newBoldFont.drawCenteredString(client.enter_amount_title, 259, offsetY + 60, 0, -1);
            client.newBoldFont.drawCenteredString(client.amountOrNameInput + "*", 259, 80 + offsetY, 128, -1);
        } else if (client.inputDialogState == 2) {
            client.newBoldFont.drawCenteredString(client.enter_name_title, 259, 60 + offsetY, 0, -1);
            client.newBoldFont.drawCenteredString(client.amountOrNameInput + "*", 259, 80 + offsetY, 128, -1);
        } else if (client.clickToContinueString != null) {
            client.newBoldFont.drawCenteredString(client.clickToContinueString, 259, 60 + offsetY, 0, -1);
            client.newBoldFont.drawCenteredString("Click to continue", 259, 80 + offsetY, 128, -1);
        } else if (client.backDialogueId != -1) {
            try {
                client.drawInterface(0, 20, Widget.interfaceCache[client.backDialogueId], 20 + offsetY, 0, ClientUI.frameMode == Client.ScreenMode.FIXED ? 338 : 0);
            } catch (Exception ignored) {

            }
        } else if (client.dialogueId != -1) {
            try {
                client.drawInterface(0, 20, Widget.interfaceCache[client.dialogueId], 20 + offsetY, 0, ClientUI.frameMode == Client.ScreenMode.FIXED ? 338 : 0);
            } catch (Exception ignored) {

            }
        } else if (client.displayChatComponents()) {
            final boolean transparent = (changeChatArea && ClientUI.frameMode != Client.ScreenMode.FIXED);
            if (resizingChatArea) { // Resize chat box height while dragging
                chatBoxHeight = MiscUtils.ensureRange(resizingChatAreaStartHeight - (MouseHandler.y - resizingChatAreaNewHeight), chatScrollHeight, ClientUI.frameHeight - 150); // -150 to leave room for split pm
            } else { // Make sure chat box height fits within frame
                chatBoxHeight = MiscUtils.ensureRange(chatBoxHeight, chatScrollHeight, ClientUI.frameHeight - 150);
            }

            final int shadow = transparent ? 0 : -1;
            Rasterizer2D.Rasterizer2D_setClip(8, 120 + offsetY, 497, 7 + offsetY - client.resizeChatOffset());

            int drawX = 10;

            final RSFont font = client.newRegularFont;
            final int totalChatMessagesDrawn = drawChatMessages(client, font, drawX, offsetY, shadow);

            Rasterizer2D.Rasterizer2D_resetClip();
            chatScrollMax = Math.max(totalChatMessagesDrawn * 14 + 2, client.getChatBoxHeight());
            final int scrollBarX = 496;
            final int scrollBarY = 6 + offsetY - client.resizeChatOffset();
            final int scrollBarPos = chatScrollMax - chatBoxScrollPosition - client.getChatBoxHeight();
            final int scrollBarHeight = client.getChatBoxHeight();

            ClientUtil.drawScrollbar(scrollBarX, scrollBarY, scrollBarPos, scrollBarHeight, chatScrollMax, client.scrollBarFillColor, transparent);

            final String name = formatName(client);

            Rasterizer2D.Rasterizer2D_setClip(8, 140 + offsetY, 509, 120 + offsetY);

            final String input = Configuration.enableEmoticons ? Emojis.Emoji.handleSyntax(client.inputString) : client.inputString;
            int textWidth = client.clientRights > 0 || client.myCrown > 0 ? 14 : 0;
            textWidth += font.getTextWidth(name + ": ");
            textWidth += font.getTextWidth(input + "*");

            final int drawY = 132 + offsetY;
            final int drawOffsetX = Math.max(0, textWidth - 487);
            /*if (client.myPrivilege > 0 || client.myCrown > 0) {
                drawModIcon(client, 10-drawOffsetX, drawY-10);
                drawX += 14;
            }*/
            drawX -= drawOffsetX;
            font.drawBasicString(name + ":", drawX - drawOffsetX, drawY, transparent ? 0xFFFFFF : 0, shadow);
            drawX += font.getTextWidth(name + ": ");
            font.drawBasicString(input + "*", drawX - drawOffsetX, drawY, transparent ? 0x7FA9FF : 255, shadow);

            Rasterizer2D.Rasterizer2D_resetClip();

            if (transparent)
                Rasterizer2D.fillGradientHorizontalLine(7, 120 + offsetY, 505, 0x80c4c4c4, 0x0ac4c4c4);
            else
                Rasterizer2D.drawHorizontalLine(7, 120 + offsetY, 505, 0x807660);

            Emojis.draw(offsetY);
        }
        if (client.menuOpen) {
            client.drawMenu(0, ClientUI.frameMode == Client.ScreenMode.FIXED ? 338 : 0);
        }
        if (ClientUI.frameMode == Client.ScreenMode.FIXED) {
            Client.chatboxImageProducer.drawGraphics(client.canvas.getGraphics(), 0, 338);
        }
        ClientCompanion.gameScreenImageProducer.initDrawingArea();
        Rasterizer3D.scanOffsets = ClientCompanion.anIntArray1182;
    }

    public static void drawChannelButtons(GameFont font, final int yOffset) {
        Client.drawSprite(CHANNEL_BUTTONS_SPRITE_ID, 0, backgroundHeight + yOffset);
        if (channelButtonPos1 >= 0 && channelButtonPos1 <= 6)
            Client.drawSprite(16, CHANNEL_BUTTON_X_VALUES[channelButtonPos1], backgroundHeight + yOffset);
        if (channelButtonPos2 == channelButtonPos1) {
            if (channelButtonPos2 >= 0 && channelButtonPos2 <= 7) {
                Client.drawSprite(17, CHANNEL_BUTTON_X_VALUES[channelButtonPos2], backgroundHeight + yOffset);
            }
        } else {
            if (channelButtonPos2 >= 0) {
                if (channelButtonPos2 <= 6)
                    Client.drawSprite(15, CHANNEL_BUTTON_X_VALUES[channelButtonPos2], backgroundHeight + yOffset);
                else if (channelButtonPos2 == 7)
                    Client.drawSprite(18, CHANNEL_BUTTON_X_VALUES[channelButtonPos2], backgroundHeight + yOffset);
            }
        }
        final int[] modes = {publicChatMode, privateChatMode, clanChatMode, tradeMode, yellMode};
        for (int i = 0; i < CHANNEL_MODE_TEXT.length; i++)
            font.drawTextWithPotentialShadow(CHANNEL_MODE_TEXT[i], CHANNEL_MODE_TEXT_X[i], CHANNEL_MODE_TEXT_Y[i] + yOffset, 0xffffff, true);
        for (int i = 0; i < CHANNEL_MODE_TEXT_VALUES.length; i++)
            font.drawCenteredText(CHANNEL_MODE_TEXT_VALUES[modes[i]], CHANNEL_MODE_TEXT_VALUES_X[i], 163 + yOffset, CHANNEL_MODE_TEXT_VALUES_COLORS[modes[i]], true);
    }

    private static int drawChatMessages(Client client, RSFont font, int drawX, int offsetY, int shadow) {
        int totalChatMessagesDrawn = 0;
        for (int k = 0; k < 500; k++) {
            if (client.chatMessages[k] != null) {

                String chatMessage = client.chatMessages[k];
                final int chatMessageType = client.chatTypes[k];
                final int chatMessageY = (client.getChatBoxHeight() - (totalChatMessagesDrawn * 14)) + chatBoxScrollPosition + 2 - client.resizeChatOffset();
                final boolean drawChatMessage = chatMessageY > -client.resizeChatOffset() && chatMessageY < 210;

                if (Configuration.enableEmoticons && drawChatMessage && chatMessageType != CHAT_TYPE_ALL)
                    chatMessage = Emojis.Emoji.handleSyntax(chatMessage);
                if (chatMessage.startsWith("Yellow:")) {
                    chatMessage = chatMessage.substring(7);
                } else if (chatMessage.startsWith("Red:")) {
                    chatMessage = chatMessage.substring(4);
                } else if (chatMessage.startsWith("Green:")) {
                    chatMessage = chatMessage.substring(6);
                } else if (chatMessage.startsWith("Cyan:")) {
                    chatMessage = chatMessage.substring(5);
                } else if (chatMessage.startsWith("Purple:")) {
                    chatMessage = chatMessage.substring(7);
                } else if (chatMessage.startsWith("White:")) {
                    chatMessage = chatMessage.substring(6);
                } else if (chatMessage.startsWith("Flash1:")) {
                    chatMessage = chatMessage.substring(7);
                } else if (chatMessage.startsWith("Flash2:")) {
                    chatMessage = chatMessage.substring(7);
                } else if (chatMessage.startsWith("Flash3:")) {
                    chatMessage = chatMessage.substring(7);
                } else if (chatMessage.startsWith("Glow1:")) {
                    chatMessage = chatMessage.substring(6);
                } else if (chatMessage.startsWith("Glow2:")) {
                    chatMessage = chatMessage.substring(6);
                } else if (chatMessage.startsWith("Glow3:")) {
                    chatMessage = chatMessage.substring(6);
                }
                if (chatMessage.startsWith("wave:")) {
                    chatMessage = chatMessage.substring(5);
                } else if (chatMessage.startsWith("wave2:")) {
                    chatMessage = chatMessage.substring(6);
                } else if (chatMessage.startsWith("shake:")) {
                    chatMessage = chatMessage.substring(6);
                } else if (chatMessage.startsWith("scroll:")) {
                    chatMessage = chatMessage.substring(7);
                } else if (chatMessage.startsWith("slide:")) {
                    chatMessage = chatMessage.substring(6);
                }
                chatMessage = chatMessage.substring(0, 1).toUpperCase() + chatMessage.substring(1);
                String chatMessageSenderName = client.chatNames[k];
                String chatMessageSenderTitle = client.chatTitles[k];

                byte rights = 0;

                boolean altColor = changeChatArea && ClientUI.frameMode != Client.ScreenMode.FIXED;

                if (showGameChatMsg(client.chatTypeView, chatMessageType)) {
                    totalChatMessagesDrawn += drawChatMessage(client, chatMessage, null, null, null, altColor ? 0xFFFFFF : 0, drawX, offsetY, chatMessageY, shadow, (byte) 0, drawChatMessage, altColor);
                } else if (showPublicChatMsg(client, chatMessageType, chatMessageSenderName)) {
                    totalChatMessagesDrawn += drawChatMessage(client, chatMessage, null, chatMessageSenderName, chatMessageSenderTitle, altColor ? 0x7FA9FF : 255, drawX, offsetY, chatMessageY, shadow, rights, drawChatMessage, altColor);
                } else if (showFromPrivateChatMsg(client, chatMessageType, chatMessageSenderName)) {
                    totalChatMessagesDrawn += drawChatMessage(client, chatMessage, "From", chatMessageSenderName, null, 0x800000, drawX, offsetY, chatMessageY, shadow, rights, drawChatMessage, altColor);
                } else if (showTradeChatMsg(client, chatMessageType, chatMessageSenderName)) {
                    if (drawChatMessage) {
                        font.drawBasicString(chatMessageSenderName + " " + chatMessage, drawX, chatMessageY + offsetY, 0x800080, shadow);
                        totalChatMessagesDrawn++;
                    }
                } else if (showLoginPrivateChatMsg(client.chatTypeView, privateChatMode, client.splitPrivateChat, chatMessageType)) {
                    if (drawChatMessage) {
                        font.drawBasicString(chatMessage, drawX, chatMessageY + offsetY, 0x800000, shadow);
                        totalChatMessagesDrawn++;
                    }
                } else if (showToPrivateChatMsg(client.chatTypeView, privateChatMode, client.splitPrivateChat, chatMessageType)) {
                    totalChatMessagesDrawn += drawChatMessage(client, chatMessage, "To", chatMessageSenderName, null, 0x800000, drawX, offsetY, chatMessageY, shadow, rights, drawChatMessage, altColor);
                } else if (showRequestsChatMsg(client, chatMessageType, chatMessageSenderName)) {
                    if (drawChatMessage) {
                        font.drawBasicString(chatMessageSenderName + " " + chatMessage, drawX, chatMessageY + offsetY, 0x7e3200, shadow);
                        totalChatMessagesDrawn++;
                    }
                    if (chatMessageType == 11 && (clanChatMode == 0)) {
                        if (client.chatTypeView == 11) {
                            if (drawChatMessage) {
                                font.drawBasicString(chatMessageSenderName + " " + chatMessage, drawX, chatMessageY + offsetY, 0x7e3200, shadow);
                                totalChatMessagesDrawn++;
                            }
                        }
                    }
                } else {
                    final int nameIndex = chatMessage.indexOf(']') + 2;
                    final int nameEndIndex = chatMessage.indexOf(':');

                    if (nameIndex > 1 && nameEndIndex > -1) {
                        final String name = nameIndex > nameEndIndex ? "" : chatMessage.substring(nameIndex, nameEndIndex);
                        final int messageIndex = nameEndIndex + (name.isEmpty() ? 1 : 2);
                        if (messageIndex >= 0) {
                            if (messageIndex >= chatMessage.length()) {
                                Log.error("Invalid message {"+chatMessage+"}");
                                continue;
                            }
                            final String message = chatMessage.substring(messageIndex);
                            if (showClanChatMsg(client, chatMessageType, chatMessageSenderName)) {
                                totalChatMessagesDrawn += drawChatMessage(client, chatMessage, drawX, offsetY, shadow, chatMessageY, drawChatMessage, altColor, name, message);
                            } else if (showYellChatMsg(client.chatTypeView, yellMode, chatMessageType)) {
                                totalChatMessagesDrawn += drawChatMessage(client, chatMessage, drawX, offsetY, shadow, chatMessageY, drawChatMessage, altColor, name, message);
                            }
                        }
                    }
                }
            }
        }
        return totalChatMessagesDrawn;
    }

    private static int drawChatMessage(Client client, String chatMessage, String from, String chatMessageSenderName, String o, int i, int drawX, int offsetY, int chatMessageY, int shadow, byte rights, boolean drawChatMessage, boolean altColor) {
        return drawChatMessage(client,
                rights, from, o,
                chatMessageSenderName,
                chatMessage, drawX,
                chatMessageY + offsetY,
                altColor ? 0xFFFFFF : 0,
                i,
                shadow,
                drawChatMessage,
                false).length;
    }

    private static int drawChatMessage(Client client, String chatMessage, int drawX, int offsetY, int shadow, int chatMessageY, boolean drawChatMessage, boolean altColor, String name, String message) {
        return drawChatMessage(client,
                (byte) 0,
                chatMessage.substring(0, chatMessage.indexOf(']') + 1),
                null,
                name,
                message,
                drawX,
                chatMessageY + offsetY,
                altColor ? 0xFFFFFF : 0,
                altColor ? 0xFFFFFF : 0,
                shadow,
                drawChatMessage,
                false).length;
    }

    private static String formatName(Client client) {
        String formattedName;
        if (Client.localPlayer != null) {
            formattedName = Client.localPlayer.getImages();
            if (Client.localPlayer.getNameWithTitle() != null)
                formattedName += Client.localPlayer.getNameWithTitle();
        } else {
            formattedName = StringUtils.formatText(ClientUtil.capitalize(client.myUsername));
        }
        return formattedName;
    }

    static int getDrawOffset(boolean condition, int trueOffset, int maxHeightOrWidth, int falseOffset) {
        return condition ? trueOffset : maxHeightOrWidth - falseOffset;
    }

    public static String[] drawChatMessage(Client client, byte rights, String preposition, String title, String name, String msg, int x, int y, int prefixColor, int msgColor, int shadow, boolean draw, boolean splitPm) {
        RSFont font = client.newRegularFont;

        StringBuilder prefix = new StringBuilder();
        if (preposition != null)
            prefix.append(preposition).append(" ");
        if (title != null && !title.isEmpty() && !title.startsWith("@su"))
            prefix.append(title).append(" ");
        if (name != null)
            prefix.append(name);
        if (title != null && !title.isEmpty() && title.startsWith("@su"))
            prefix.append(" ").append(title.substring(3));
        if (name != null)
            prefix.append(": ");

        String prefixText = prefix.toString();
        int prefixWidth = font.getTextWidth(prefixText, true);
        String[] msgs = StringUtils.splitString(msg, (splitPm ? ClientCompanion.MAX_SPLIT_PRIVATE_CHAT_WIDTH : 485) - prefixWidth, font);
        boolean split = msgs.length > 1;
        int messageHeight = splitPm ? 13 : 14;

        if (draw) {
            y -= split ? messageHeight : 0;

            font.drawBasicString(prefixText, x, y, prefixColor, shadow);

            font.drawBasicString(msgs[0], x + prefixWidth, y, msgColor, shadow);

            if (split)
                font.drawBasicString(msgs[1], x + prefixWidth, y + messageHeight, RSFont.textColor, RSFont.textShadowColor);
        }

        return msgs;
    }

    public static boolean showGameChatMsg(int chatTypeView, int chatType) {
        return chatType == CHAT_TYPE_ALL && (chatTypeView == 5 || chatTypeView == 0);
    }

    public static boolean showPublicChatMsg(Client client, int chatType, String chatName) {
        return (chatType == CHAT_TYPE_MOD_PUBLIC || chatType == CHAT_TYPE_PUBLIC) && (chatType == CHAT_TYPE_MOD_PUBLIC || publicChatMode == 0 || publicChatMode == 1 && client.isFriendOrSelf(chatName))
                && (client.chatTypeView == 1 || client.chatTypeView == 0);
    }

    public static boolean showFromPrivateChatMsg(Client client, int chatType, String chatName) {
        return (chatType == CHAT_TYPE_PRIVATE || chatType == CHAT_TYPE_FROM_MOD_PRIVATE) && (client.splitPrivateChat == 0 || client.chatTypeView == 2)
                && (chatType == CHAT_TYPE_FROM_MOD_PRIVATE || privateChatMode == 0 || privateChatMode == 1 && client.isFriendOrSelf(chatName))
                && (client.chatTypeView == 2 || client.chatTypeView == 0);
    }

    public static boolean showTradeChatMsg(Client client, int chatType, String chatName) {
        return chatType == CHAT_TYPE_TRADE && (tradeMode == 0 || tradeMode == 1 && client.isFriendOrSelf(chatName)) && (client.chatTypeView == 3 || client.chatTypeView == 0);
    }

    public static boolean showLoginPrivateChatMsg(int chatTypeView, int privateChatMode, int splitPrivateChat, int chatType) {
        return chatType == CHAT_TYPE_LOGIN_PRIVATE && (splitPrivateChat == 0 || chatTypeView == 2) && privateChatMode < 2 && (chatTypeView == 2 || chatTypeView == 0);
    }

    public static boolean showToPrivateChatMsg(int chatTypeView, int privateChatMode, int splitPrivateChat, int chatType) {
        return chatType == CHAT_TYPE_TO_PRIVATE && (splitPrivateChat == 0 || chatTypeView == 2) && privateChatMode < 2 && (chatTypeView == 2 || chatTypeView == 0);
    }

    public static boolean showRequestsChatMsg(Client client, int chatType, String chatName) {
        return chatType == CHAT_TYPE_DUEL
                && (tradeMode == 0 || tradeMode == 1
                && client.isFriendOrSelf(chatName))
                && (client.chatTypeView == 3 || client.chatTypeView == 0);
    }

    public static boolean showClanChatMsg(Client client, int chatType, String chatName) {
        return chatType == CHAT_TYPE_CLAN && (clanChatMode == 0 || clanChatMode == 1 && client.isFriendOrSelf(chatName)) && (client.chatTypeView == 11 || client.chatTypeView == 0);
    }

    public static boolean showYellChatMsg(int chatTypeView, int yellMode, int chatType) {
        return chatType == CHAT_TYPE_YELL && yellMode == 0 && (chatTypeView == 4 || chatTypeView == 0);
    }

    public static boolean showFromSplitPrivateChatMsg(Client client, int chatType, String chatName) {
        return (chatType == CHAT_TYPE_PRIVATE || chatType == CHAT_TYPE_FROM_MOD_PRIVATE) && client.splitPrivateChat == 1
                && (chatType == CHAT_TYPE_FROM_MOD_PRIVATE || privateChatMode == 0 || privateChatMode == 1 && client.isFriendOrSelf(chatName));
    }

    public static boolean showLoginSplitPrivateChatMsg(int privateChatMode, int splitPrivateChat, int chatType) {
        return chatType == CHAT_TYPE_LOGIN_PRIVATE && splitPrivateChat == 1 && privateChatMode < 2;
    }

    public static boolean showToSplitPrivateChatMsg(int privateChatMode, int splitPrivateChat, int chatType) {
        return chatType == CHAT_TYPE_TO_PRIVATE && splitPrivateChat == 1 && privateChatMode < 2;
    }

    public static boolean shouldRepaintChatBox() {
        return updateChatbox || resizingChatArea;
    }

    public static boolean isResizingChatArea() {
        return resizingChatArea;
    }

    public static void setUpdateChatbox(boolean updateChatbox) {
        ChatBox.updateChatbox = updateChatbox;
    }
}
