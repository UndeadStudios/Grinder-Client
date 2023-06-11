package com.runescape.cache.graphics.widget;

import com.runescape.Client;
import com.runescape.cache.graphics.GameFont;
import com.runescape.cache.graphics.sprite.AutomaticSprite;
import com.runescape.util.StringUtils;
import com.grinder.client.ClientUtil;

public class YellCustomizer extends Widget {

    public static final int INTERFACE_ID = 51100;
    public static final int INPUT_ID = INTERFACE_ID + 26;
    public static final int SAVE_CHANGES_BUTTON_ID = INTERFACE_ID + 27;

    public static final int PREVIEW_TEXT_ID = INTERFACE_ID + 7;
    public static final int BRACKET_COLOR_BOX_ID = INTERFACE_ID + 15;
    public static final int BRACKET_SHADOW_COLOR_BOX_ID = INTERFACE_ID + 16;
    public static final int TITLE_COLOR_BOX_ID = INTERFACE_ID + 17;
    public static final int TITLE_SHADOW_COLOR_BOX_ID = INTERFACE_ID + 18;
    public static final int NAME_COLOR_BOX_ID = INTERFACE_ID + 19;
    public static final int NAME_SHADOW_COLOR_BOX_ID = INTERFACE_ID + 20;
    public static final int MESSAGE_COLOR_BOX_ID = INTERFACE_ID + 21;
    public static final int MESSAGE_SHADOW_COLOR_BOX_ID = INTERFACE_ID + 22;

    public static void widget(GameFont[] tda) {
        int id = INTERFACE_ID;

        Widget w = addInterface(id++);

        w.totalChildren(32);
        int child = 0;

        int width = 409;
        int height = 259;
        int x = (512 - width) / 2;
        int y = (334 - height) / 2;

        addBackground(id, width, height, AutomaticSprite.BACKGROUND_BIG_BROWN);
        w.child(child++, id++, x, y);

        addCloseButton(id, false);
        w.child(child++, id++, x + width - 26, y + 3);

        addText(id, "Yell Customizer", tda, 2, 0xffb000, true);
        w.child(child++, id++, 256, y + 4);

        int xInset = 7;
        int yInset = 22;
        int panelSpacing = 10;

        int colorWidth = 207;
        int colorHeight = 144;

        int previewWidth = 375;
        int previewHeight = 56;

        int titleWidth = previewWidth - panelSpacing - colorWidth;
        int titleHeight = 66;

        /*
         * Preview panel
         */

        int previewX = (((width - (xInset * 2)) - previewWidth) / 2) + xInset;
        //int previewY = colorY + colorHeight + panelSpacing;
        int previewY = yInset + panelSpacing;

        addRectangle(id, previewWidth - 2, previewHeight - 2, 0x726451, false);
        w.child(child++, id++, x + previewX + 1, y + previewY + 1);
        addRectangle(id, previewWidth, previewHeight, 0x2e2b23, false);
        w.child(child++, id++, x + previewX, y + previewY);

        addText(id, "Preview", tda, 2, 0xffb000, true);
        w.child(child++, id++, 256, y + previewY + 10);

        addText(id, "[WWWWWWWWWWWW] WWWWWWWWWWWW: This is a test message.", tda, 1, 0xffb000, true, false);
        w.child(child++, id++, 256, y + previewY + 31);

        /*
         * Color panel
         */

        int colorX = (((width - (xInset * 2)) - (colorWidth + panelSpacing + titleWidth)) / 2) + xInset;
        int colorY = previewY + previewHeight + panelSpacing;

        addRectangle(id, colorWidth - 2, colorHeight - 2, 0x726451, false);
        w.child(child++, id++, x + colorX + 1, y + colorY + 1);
        addRectangle(id, colorWidth, colorHeight, 0x2e2b23, false);
        w.child(child++, id++, x + colorX, y + colorY);

        addText(id, "Colors", tda, 2, 0xffb000, true);
        w.child(child++, id++, x + colorX + (colorWidth / 2), y + colorY + 10);

        String[] names = new String[] { "Bracket", "Title", "Name", "Message" };
        int spacingY = 26;
        for (int i = 0; i < names.length; i++) {
            addText(id, names[i], tda, 1, 0xffb000);
            w.child(child++, id++, x + colorX + 12, y + colorY + 36 + (i * spacingY));
        }

        int[] defaultColors = { 0x0033ff, 0x0033ff, 0x00dd00, 0x00dd00 };
        for (int i = 0; i < names.length; i++) {
            addColorBox(id, defaultColors[i], names[i].toLowerCase());
            w.child(child++, id++, x + colorX + colorWidth - 139, y + colorY + 33 + (i * spacingY));
            addColorBox(id, 0, names[i].toLowerCase() + " shadow");
            w.child(child++, id++, x + colorX + colorWidth - 73, y + colorY + 33 + (i * spacingY));
        }

        /*
         * Title panel
         */

        int titleX = colorX + colorWidth + 10;
        //int titleY = colorY + ((colorHeight - titleHeight) / 2);
        int titleY = colorY;

        addRectangle(id, titleWidth - 2, titleHeight - 2, 0x726451, false);
        w.child(child++, id++, x + titleX + 1, y + titleY + 1);
        addRectangle(id, titleWidth, titleHeight, 0x2e2b23, false);
        w.child(child++, id++, x + titleX, y + titleY);

        addText(id, "Title", tda, 2, 0xffb000, true);
        w.child(child++, id++, x + titleX + (titleWidth / 2), y + titleY + 10);

        addInput(id, "", "^(\\w|\\s){0,12}$", titleWidth - 24, 21, 0xffb000);
        w.child(child++, id++, x + titleX + 12, y + titleY + 33);

        /*
         * Buttons
         */

        int buttonWidth = titleWidth;
        int buttonHeight = 29;
        int buttonSpacing = panelSpacing;
        int buttonX = titleX;
        //int buttonX = (((width - (xInset * 2)) - (buttonWidth + buttonSpacing + buttonWidth)) / 2) + xInset;

        int buttonY = titleY + titleHeight + panelSpacing;

        addHoverButton(id, buttonWidth, buttonHeight, AutomaticSprite.BUTTON_BROWN, AutomaticSprite.BUTTON_BROWN_HOVER,"Save Changes");
        w.child(child++, id++, x + buttonX, y + buttonY);
        addHoverButton(id, buttonWidth, buttonHeight, AutomaticSprite.BUTTON_BROWN, AutomaticSprite.BUTTON_BROWN_HOVER,"Discard Changes");
        w.child(child++, id++, x + buttonX, y + buttonY + buttonHeight + buttonSpacing);

        addText(id, "Save Changes", tda, 0, 0xffb000, true);
        w.child(child++, id++, x + buttonX + (buttonWidth / 2), y + buttonY + 9);
        addText(id, "Discard Changes", tda, 0, 0xffb000, true);
        w.child(child++, id++, x + buttonX + (buttonWidth / 2), y + buttonY + buttonHeight + buttonSpacing + 9);

        /*
         * Member's Feature! overlays
         */
        int textId = id++;
        addText(textId, "<img=745> Member's Features!", tda, 0, 0xffb000, true);

            Widget colorOverlay = addInterface(id++);
            w.child(child++, colorOverlay.id, x + colorX + 68, y + colorY + 33);
            colorOverlay.totalChildren(4);
            addRectangle(id, 127, 47, 0, false);
            colorOverlay.child(0, id++, 0, 0);
            addRectangle(id, 127 - 2, 47 - 2, 0x5a5348, false);
            colorOverlay.child(1, id++, 1, 1);
            addTransparentRectangle(id, 127 - 4, 47 - 4, 0x292521, true, 230);
            colorOverlay.child(2, id++, 2, 2);
            colorOverlay.child(3, textId, 127 / 2, 18);

            Widget titleOverlay = addInterface(id++);
            w.child(child++, titleOverlay.id, x + titleX + 12, y + titleY + 33);
            titleOverlay.totalChildren(4);
            addRectangle(id, 134, 21, 0, false);
            titleOverlay.child(0, id++, 0, 0);
            addRectangle(id, 134 - 2, 21 - 2, 0x5a5348, false);
            titleOverlay.child(1, id++, 1, 1);
            addTransparentRectangle(id, 134 - 4, 21 - 4, 0x292521, true, 230);
            titleOverlay.child(2, id++, 2, 2);
            titleOverlay.child(3, textId, 134 / 2, 5);
    }

    public static void updateInterface() {
        StringBuilder builder = new StringBuilder();

        /*
         * Left bracket
         */
        builder.append("<col=" + String.format("%06X", (0xFFFFFF & Widget.interfaceCache[BRACKET_COLOR_BOX_ID].textColor)) + ">");
        builder.append("<shad=" + Integer.parseInt(String.format("%06X", (0xFFFFFF & Widget.interfaceCache[BRACKET_SHADOW_COLOR_BOX_ID].textColor)), 16) + ">");
        builder.append("[");

        /*
         * Title
         */
        builder.append("<col=" + String.format("%06X", (0xFFFFFF & Widget.interfaceCache[TITLE_COLOR_BOX_ID].textColor)) + ">");
        builder.append("<shad=" + Integer.parseInt(String.format("%06X", (0xFFFFFF & Widget.interfaceCache[TITLE_SHADOW_COLOR_BOX_ID].textColor)), 16) + ">");
        builder.append(Widget.interfaceCache[INPUT_ID].getDefaultText());

        /*
         * Right bracket
         */
        builder.append("<col=" + String.format("%06X", (0xFFFFFF & Widget.interfaceCache[BRACKET_COLOR_BOX_ID].textColor)) + ">");
        builder.append("<shad=" + Integer.parseInt(String.format("%06X", (0xFFFFFF & Widget.interfaceCache[BRACKET_SHADOW_COLOR_BOX_ID].textColor)), 16) + ">");
        builder.append("] ");

        /*
         * Rank and name
         */
        builder.append("<col=" + String.format("%06X", (0xFFFFFF & Widget.interfaceCache[NAME_COLOR_BOX_ID].textColor)) + ">");
        builder.append("<shad=" + Integer.parseInt(String.format("%06X", (0xFFFFFF & Widget.interfaceCache[NAME_SHADOW_COLOR_BOX_ID].textColor)), 16) + ">");
        int myPrivilege = Client.instance.getClientRights();
        int privilegeToShow = Client.instance.myCrown;
        String ranks;
        String name;
        if (Client.localPlayer != null && Client.localPlayer.name != null) {
            name = Client.localPlayer.name;
            ranks = Client.localPlayer.getImages();
        } else {
            name = StringUtils.formatText(ClientUtil.capitalize(Client.instance.getMyUsername()));
            ranks = "";
        }
        builder.append(ranks + name + ": ");

        /*
         * Message
         */
        builder.append("<col=" + String.format("%06X", (0xFFFFFF & Widget.interfaceCache[MESSAGE_COLOR_BOX_ID].textColor)) + ">");
        builder.append("<shad=" + Integer.parseInt(String.format("%06X", (0xFFFFFF & Widget.interfaceCache[MESSAGE_SHADOW_COLOR_BOX_ID].textColor)), 16) + ">");
        builder.append("This is a test message.");

        Widget.interfaceCache[PREVIEW_TEXT_ID].setDefaultText(builder.toString());
    }
}
