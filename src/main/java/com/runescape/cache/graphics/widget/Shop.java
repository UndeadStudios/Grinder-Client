package com.runescape.cache.graphics.widget;

import com.grinder.model.ChatBox;
import com.runescape.Client;
import com.runescape.cache.def.ItemDefinition;

import java.util.Arrays;

public class Shop {

    public static final int INTERFACE_ID = 30900;
    public static final int CONTAINER = INTERFACE_ID + 5;
    public static final int OLD_CONTAINER = 3823;
    public static final int SCROLL = INTERFACE_ID + 4;
    public static final int SEARCH_BUTTON = INTERFACE_ID + 6;
    public static final int NO_ITEMS_FOUND_TEXT = INTERFACE_ID + 8;

    private static int[] shopIdTemp = new int[1000];
    private static int[] shopAmountsTemp = new int[1000];
    private static String searchInput = "";

    public static int[] getShopIdTemp() {
        return shopIdTemp;
    }

    public static int[] getShopAmountsTemp() {
        return shopAmountsTemp;
    }

    public static void setSearchInput(String input) {
        searchInput = input;
    }

    /**
     * Checks if the searching versions of the tab containers should be shown.
     */
    public static boolean showSearchContainers() {
        return isSearching() && searchInput.length() > 0;
    }

    /**
     * Checks if the search button is activated.
     */
    public static boolean isSearching() {
        return !Client.instance.interfaceIsSelected(Widget.interfaceCache[SEARCH_BUTTON]);
    }

    /**
     * Opens the search.
     */
    public static void openSearch() {
        Client.instance.enter_name_title = "Show items whose names contain the following text:";
        Client.instance.setMessagePromptRaised(false);
        Client.instance.setInputDialogState(2);
        Client.instance.setAmountOrNameInput("");
        ChatBox.setUpdateChatbox(true);
        Client.instance.setButtonConfig(Widget.interfaceCache[SEARCH_BUTTON].valueIndexArray[0][1], 1);
    }

    /**
     * Closes the search.
     */
    public static void closeSearch() {
        if (!isSearching())
            return;

        if (Client.instance.getInputDialogState() != 0) {
            Client.instance.setInputDialogState(0);
            ChatBox.setUpdateChatbox(true);
        }

        Client.instance.setButtonConfig(Widget.interfaceCache[SEARCH_BUTTON].valueIndexArray[0][1], 0);

        searchInput = "";

        Arrays.fill(shopIdTemp, 0);
        Arrays.fill(shopAmountsTemp, 0);
    }

    /**
     * Sets the searching arrays.
     */
    public static void setSearchContainer() {
        Widget shop = Widget.interfaceCache[CONTAINER];
        Arrays.fill(shopIdTemp, 0);
        Arrays.fill(shopAmountsTemp, 0);
        for (int slot = 0, searchSlot = 0; slot < shop.inventoryItemId.length; slot++) {
            if (shop.inventoryItemId[slot] > 0) {
                String item = ItemDefinition.lookup(shop.inventoryItemId[slot] - 1).name;
                if (item != null && item.toLowerCase().contains(searchInput.toLowerCase())) {
                    shopIdTemp[searchSlot] = shop.inventoryItemId[slot];
                    shopAmountsTemp[searchSlot++] = shop.inventoryAmounts[slot];
                }
            }
        }
    }

    /**
     * Gets the amount of items being displayed.
     */
    public static int getResultsAmount() {
        int results = 0;
        for (int slot = 0; slot < shopIdTemp.length; slot++) {
            if (shopIdTemp[slot] > 0) {
                results++;
            }
        }
        return results;
    }

    /**
     * Updates the entire interface.
     */
    public static void updateInterface() {
        // Update search button
        if (showSearchContainers()) {
            Widget.interfaceCache[SEARCH_BUTTON].setDefaultText(searchInput.toLowerCase());
            Widget.interfaceCache[SEARCH_BUTTON].textColor = Widget.interfaceCache[SEARCH_BUTTON].defaultHoverColor;
        } else if (isSearching()) {
            if (Client.instance.getInputDialogState() == 0) {
                closeSearch();
            }
            Widget.interfaceCache[SEARCH_BUTTON].setDefaultText("Search");
            Widget.interfaceCache[SEARCH_BUTTON].textColor = Widget.interfaceCache[SEARCH_BUTTON].defaultHoverColor;
        } else {
            Widget.interfaceCache[SEARCH_BUTTON].setDefaultText("Search");
            Widget.interfaceCache[SEARCH_BUTTON].textColor = 0xff981f;
        }

        // Set scrollmax
        Widget.interfaceCache[SCROLL].scrollMax = Widget.getContainerScrollMax(Widget.interfaceCache[CONTAINER]);

        // Show/hide no items found text
        Widget.interfaceCache[NO_ITEMS_FOUND_TEXT].hidden = !showSearchContainers() || getResultsAmount() > 0;
    }
}
