package com.runescape.cache.graphics.widget;

import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.cache.graphics.GameFont;
import com.runescape.cache.graphics.sprite.AutomaticSprite;

public class ItemDropFinder extends Widget {

    public static final int INTERFACE_ID = 81600;
    private static final int BACKGROUND_ID = INTERFACE_ID + 1;
    private static final int CLOSE_BUTTON_ID = INTERFACE_ID + 2;
    private static final int TITLE_TEXT_ID = INTERFACE_ID + 3;
    private static final int DIVIDER_ID = INTERFACE_ID + 4;
    public static final int INPUT_ID = INTERFACE_ID + 7;
    private static final int SEARCH_NPC_BUTTON_ID = INTERFACE_ID + 8;
    private static final int SEARCH_ITEM_BUTTON_ID = INTERFACE_ID + 9;
    private static final int SELECT_NPC_TEXT_ID = INTERFACE_ID + 10;
    private static final int ENTER_INPUT_TEXT_ID = INTERFACE_ID + 11;
    public static final int LIST_SCROLL_ID = INTERFACE_ID + 12;
    public static final int LIST_START_ID = INTERFACE_ID + 13;
    private static final int NPC_LAYER_ID = INTERFACE_ID + 213;
    private static final int NPC_NAME_ID = INTERFACE_ID + 214;
    private static final int ITEM_SCROLL_BG_BOX_START_ID = INTERFACE_ID + 215;
    public static final int ITEM_SCROLL_ID = INTERFACE_ID + 218;
    private static final int ITEM_CONTAINER_ID = INTERFACE_ID + 219;
    public static final int ITEM_RECTANGLE_START_ID = INTERFACE_ID + 220;
    public static final int ITEM_CHANCE_TEXT_START_ID = INTERFACE_ID + 221;

    public static int width = 501;
    public static int height = 314;

    public static void widget(GameFont[] tda) {
        int id = INTERFACE_ID;

        Widget widget = addInterface(id++);
        widget.totalChildren(13);
        int child = 0;

        int w = width;
        int h = height;
        int x = (512 - w) / 2;
        int y = (334 - h) / 2;

        addBasics("Item Drop Finder", tda, widget, child, id, x, y, w, h, AutomaticSprite.BACKGROUND_BROWN);
        interfaceCache[id + 2].textColor = 0xffb000;
        child += 3;
        id += 3;

        int buttonWidth = 125;

        addVerticalDivider(id, h - 40, AutomaticSprite.DIVIDER_VERTICAL_SMALL_BROWN);
        widget.child(child++, id++, x + 6 + buttonWidth, y + 34);

        addTransparentRectangle(id, buttonWidth, 51, 0, true, 56);
        widget.child(child++, id++, x + 6, y + 34);

        addHorizontalDivider(id, buttonWidth, AutomaticSprite.DIVIDER_HORIZONTAL_SMALL_BROWN);
        widget.child(child++, id++, x + 6, y + 34 + 51);

        addInput(id, "Search", "^(.){0,30}$", buttonWidth - 10, 21, 0xB8B8B8);
        interfaceCache[id].autoUpdate = true;
        widget.child(child++, id++, x + 6 + 5, y + 34 + 5);

        addHoverTextResetSettingButton(id, 44, 17, 873, 874, "NPC", tda, 0, 0xEE9021, 0xffffff, false, 0, 1180);
        widget.child(child++, id++, x + 6 + 5, y + 65);

        addHoverTextResetSettingButton(id, 42, 17, 873, 874, "Item", tda, 0, 0xEE9021, 0xffffff, false, 1, 1180);
        widget.child(child++, id++, x + 6 + 59, y + 65);

        addText(id, "Select an NPC to see its information.", tda, 1, 0xffb000, true);
        widget.child(child++, id++, x + 6 + buttonWidth + 3 + (354 / 2), y + 140);

        addText(id, "Enter a search input.", tda, 0, 0xffb000, true);
        widget.child(child++, id++, x + 6 + (buttonWidth / 2), y + 180);

        Widget listScroll = addInterface(id++);
        listScroll.invisible = true;
        widget.child(child++, listScroll.id, x + 6, y + 34 + 51 + 3);

        int lines = 100;
        int listScrollChild = 0;
        listScroll.width = buttonWidth - 16;
        listScroll.height = 219;
        listScroll.scrollMax = lines * 20;
        listScroll.totalChildren(lines * 2);

        for (int i = 0; i < lines; i++) {
            addHoverText(id, "" + id, "View drop table", tda, 0, 0xccaa5b, false, true, buttonWidth - 16, 0xFFFFFF);
            listScroll.child(listScrollChild++, id++, 3, 5 + i * 20);

            addHorizontalLine(id, buttonWidth - 22, 0x726451);
            listScroll.child(listScrollChild++, id++, 3, 20 + i * 20);
        }

        Widget layer = addInterface(id++);
        layer.invisible = true;
        widget.child(child++, layer.id, x + 6 + buttonWidth + 3, y + 34);

        int layerChild = 0;
        layer.totalChildren(5);

        addText(id, "King Black Dragon", tda, 4, 0xffb000, true);
        layer.child(layerChild++, id++, 354 / 2, 5);

        addNewBox(layer, layerChild, id, 10, 40, 340, 223);
        layerChild += 3;
        id += 3;

        Widget itemScroll = addInterface(id++);
        layer.child(layerChild++, itemScroll.id, 12, 42);
        itemScroll.width = 336 - 16;
        itemScroll.height = itemScroll.scrollMax = 219;
        int itemScrollChild = 0;
        itemScroll.totalChildren(1 + 5 * 20 * 2);

        addContainer(id, 5, 20, 31, 53);
        itemScroll.child(itemScrollChild++, id++, 18, 14);

        for (int i = 0; i < 5 * 20; i++) {
            int col = i % 5;
            int row = (int) Math.ceil((i + 1) / 5.0) - 1;

            addRectangle(id, 58, 80, 0x726451, false);
            //itemScroll.child(itemScrollChild++, id++, col * 63 + 5, row * 85 + 5);
            itemScroll.child(itemScrollChild++, id++, 5, 5);

            addText(id, "" + id + "\\n" + id, tda, 0, 0xffffff, true);
            //itemScroll.child(itemScrollChild++, id++, col * 63 + 4 + 31, row * 85 + 50);
            itemScroll.child(itemScrollChild++, id++, 35, 50);
        }

    }

    public static void updateInterface() {
        width = Math.min(Client.instance.contentWidth - 264, 816); // 765 - 501 = 264, 816 = 10 rows
        height = Client.instance.contentHeight - 189; // 503 - 314 = 189

        if (interfaceCache[BACKGROUND_ID].width == width && interfaceCache[BACKGROUND_ID].height == height)
            return;

        int wDiff = width - 501;
        int hDiff = height - 314;

        addBackground(BACKGROUND_ID, width, height, true, AutomaticSprite.BACKGROUND_BROWN);
        addVerticalDivider(DIVIDER_ID, 274 + hDiff, AutomaticSprite.DIVIDER_VERTICAL_SMALL_BROWN);
        interfaceCache[INTERFACE_ID].width = 512 + wDiff;
        interfaceCache[INTERFACE_ID].height = 334 + hDiff;

        interfaceCache[CLOSE_BUTTON_ID].horizontalOffset = wDiff;
        interfaceCache[TITLE_TEXT_ID].horizontalOffset = wDiff / 2;
        interfaceCache[ENTER_INPUT_TEXT_ID].verticalOffset = hDiff / 2;
        interfaceCache[SELECT_NPC_TEXT_ID].horizontalOffset = wDiff / 2;
        interfaceCache[SELECT_NPC_TEXT_ID].verticalOffset = hDiff / 2;

        interfaceCache[LIST_SCROLL_ID].height = 219 + hDiff;

        interfaceCache[NPC_LAYER_ID].width = 512 + wDiff;
        interfaceCache[NPC_LAYER_ID].height = 512 + hDiff;
        interfaceCache[NPC_NAME_ID].horizontalOffset = wDiff / 2;
        interfaceCache[ITEM_SCROLL_ID].width = 320 + wDiff;
        interfaceCache[ITEM_SCROLL_ID].height = 219 + hDiff;
        interfaceCache[ITEM_SCROLL_BG_BOX_START_ID].width = 340 + wDiff;
        interfaceCache[ITEM_SCROLL_BG_BOX_START_ID].height = 223 + hDiff;
        interfaceCache[ITEM_SCROLL_BG_BOX_START_ID + 1].width = 338 + wDiff;
        interfaceCache[ITEM_SCROLL_BG_BOX_START_ID + 1].height = 221 + hDiff;
        interfaceCache[ITEM_SCROLL_BG_BOX_START_ID + 2].width = 336 + wDiff;
        interfaceCache[ITEM_SCROLL_BG_BOX_START_ID + 2].height = 219 + hDiff;

        updateListScroll();
        updateItemScroll();
    }

    public static void updateListScroll() {
        int count = 0;
        for (int i = 0; i < 100; i++)
            if (!interfaceCache[LIST_START_ID + i * 2].defaultText.isEmpty())
                count++;
        interfaceCache[LIST_SCROLL_ID].scrollMax = Math.max(interfaceCache[LIST_SCROLL_ID].height, count * 20);
    }

    public static void updateItemScroll() {
        int cols = 5;
        if (ClientUI.frameMode != Client.ScreenMode.FIXED) {
            cols += (width - 501) / 63;
        }
        int count = 0;
        Widget container = interfaceCache[ITEM_CONTAINER_ID];
        container.width = cols;
        container.height = (int) Math.ceil((double) container.inventoryItemId.length / cols);
        for (int i = 0; i < container.inventoryItemId.length; i++)
            if (container.inventoryItemId[i] > 0)
                count++;
        interfaceCache[ITEM_SCROLL_ID].scrollMax = Math.max(interfaceCache[ITEM_SCROLL_ID].height, (int) Math.ceil((double) count / cols) * 85 + 5);

        for (int i = 0; i < container.inventoryItemId.length; i++) {
            int col = i % container.width;
            int row = (int) Math.ceil((i + 1) / (double) container.width) - 1;

            interfaceCache[ITEM_RECTANGLE_START_ID + i * 2].horizontalOffset = col * 63;
            interfaceCache[ITEM_RECTANGLE_START_ID + i * 2].verticalOffset = row * 85;

            interfaceCache[ITEM_CHANCE_TEXT_START_ID + i * 2].horizontalOffset = col * 63;
            interfaceCache[ITEM_CHANCE_TEXT_START_ID + i * 2].verticalOffset = row * 85;
        }
    }

}