package com.runescape.cache.graphics.widget;

import com.runescape.Client;
import com.runescape.cache.graphics.GameFont;
import com.runescape.cache.graphics.sprite.AutomaticSprite;

public class PriceChecker extends Widget {
    public static final int INTERFACE_ID = 42000;
    public static final int GLOW_SPRITE = 18361;

    public static void widget(GameFont[] tda) {
        Widget rsi = addInterface(INTERFACE_ID);
        rsi.totalChildren(16);
        int child = 0;

        // Background
        addBackground(18245, 480, 314, true, AutomaticSprite.BACKGROUND_BLACK);
        rsi.child(child++, 18245, 16, 10);

        // Divider
        addHorizontalDivider(18246, 470, AutomaticSprite.DIVIDER_HORIZONTAL_BLACK);
        rsi.child(child++, 18246, 21, 272);

        // Close
        addCloseButton(18247, true);
        rsi.child(child++, 18247, 468, 17);

        // Title
        addText(18248, "Guide Prices", tda, 2, 0xff981f, true);
        rsi.child(child++, 18248, 256, 20);

        // Scroll bar
        Widget scroll = addInterface(18499);
        scroll.width = 448;
        scroll.height = scroll.scrollMax = 222;
        rsi.child(child++, scroll.id, 22, 50);
        scroll.totalChildren(31);
        int scrollChild = 0;

        String[] actions = { "Remove", "Remove-5", "Remove-10", "Remove-All", "Remove-X" };
        addContainer(18500, scroll.id, 0, 5, 6, 57, 35, actions);
        interfaceCache[18500].allowSwapItems = false;
        scroll.child(scrollChild++, 18500, 30, 0);

        int textX = 0;
        int textY = 0;
        for (int i = 0; i < interfaceCache[18500].inventoryItemId.length; i++) {
            addText(18300 + i, "", tda, 0, 0xffffff, true);
            scroll.child(scrollChild++, 18300 + i, (32 + interfaceCache[18500].spritePaddingX) / 2 + 4 + textX, 32 + textY);

            if ((i + 1) % 5 == 0) {
                textX = 0;
                textY += 32 + interfaceCache[18500].spritePaddingY;
            } else {
                textX += 32 + interfaceCache[18500].spritePaddingX;
            }
        }

        // Total guide price
        addText(18350, "Total guide price:", tda, 1, 0xff981f, true);
        rsi.child(child++, 18350, 257, 282);
        addText(18351, "0", tda, 1, 0xffffff, true);
        rsi.child(child++, 18351, 258, 297);

        // Search for item
        addButton(18360, 891, "Search for item");
        rsi.child(child++, 18360, 26, 280);

        // Glow sprite
        addTransparentSprite(GLOW_SPRITE, 892, 0);
        rsi.child(child++, GLOW_SPRITE, 26, 280);

        // Item icon
        /*addSprite(18362, 891);
        rsi.child(child++, 18362, 26, 280);*/

        // Search icon
        addSprite(18363, 893);
        rsi.child(child++, 18363, 28, 282);

        // Item name
        addText(18364, "", tda, 1, 0xff981f);
        rsi.child(child++, 18364, 71, 282);

        // Item price
        addText(18365, "", tda, 1, 0xffffff);
        rsi.child(child++, 18365, 71, 297);

        // Remove all
        addHoverButton(18370, getSprite(0, interfaceLoader, "miscgraphics"), getSprite(9, interfaceLoader, "miscgraphics"), "Remove all");
        rsi.child(child++, 18370, 410, 280);
        addSprite(18371, 894);
        offsetSprites(18371, 3, 8);
        rsi.child(child++, 18371, 410, 280);

        // Add all
        addHoverButton(18372, getSprite(0, interfaceLoader, "miscgraphics"), getSprite(9, interfaceLoader, "miscgraphics"), "Add all");
        rsi.child(child++, 18372, 450, 280);
        addSprite(18373, 115); // sprite already gets offset in bank method
        rsi.child(child++, 18373, 450, 280);
    }

    public static void updateInterface() {
        Widget.interfaceCache[GLOW_SPRITE].transparency = 90 + (int) (70 * Math.sin(Client.tick / 18.0));
    }
}
