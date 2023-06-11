package com.runescape.cache.graphics.widget;

import java.util.Arrays;

import com.grinder.model.ChatBox;
import com.runescape.Client;
import com.grinder.Configuration;
import com.runescape.cache.def.ItemDefinition;
import com.runescape.cache.graphics.sprite.AutomaticSprite;
import com.runescape.cache.graphics.sprite.Sprite;
import com.grinder.ui.ClientUI;
import com.grinder.client.ClientCompanion;
import com.grinder.client.ClientUtil;
import com.runescape.cache.graphics.sprite.SpriteLoader;

/**
 * OSRS style bank interface.
 * @author xplicit
 */
public class Bank {

	public static final int INTERFACE_ID = 5292;
	public static final int TOTAL_TABS = 10;
	public static final int START_ID = 50000;
	public static final int SHOW_MENU_BUTTON = START_ID + 5;
	public static final int BANKING_INTERFACE_LAYER = START_ID + 7;
	public static final int TABS_INTERFACE_LAYER = START_ID + 8;
	public static final int TAB_BUTTON_START = START_ID + 10;
	public static final int CONTAINER_START = START_ID + 50;
	public static final int SEARCH_CONTAINER_START = START_ID + 60;
	public static final int BANK_QUANTITY_X_BUTTON = START_ID + 76;
	public static final int PLACEHOLDER_BUTTON = START_ID + 82;
	public static final int SEARCH_BUTTON = START_ID + 84;
	public static final int DEPOSIT_INV_BUTTON = START_ID + 86;
	public static final int DEPOSIT_EQUIP_BUTTON = START_ID + 89;
	public static final int SETTINGS_INTERFACE_LAYER = START_ID + 100;
	public static final int RELEASE_PLACEHOLDERS_BUTTON = START_ID + 111;

    private static int modifiableXValue, currentTab, tabsCount;
    private static String titleText = "", searchInput = "";
    private static int oldHeight, height = 331;
    private static int oldWidth, width = 488;

    public static void setModifiableXValue(int value) {
        modifiableXValue = value;
    }

    public static int getModifiableXValue() {
        return modifiableXValue;
    }

    public static void setSearchInput(String input) {
        searchInput = input;
    }

    public static void setTitleText(String text) {
        titleText = text;
    }

	/**
	 * Checks if an interface ID is a bank tab button
	 * @param excludeTabZero Should the first tab (main tab) be excluded from the check?
	 */
	public static boolean isBankTabButton(int interfaceId, boolean excludeTabZero) {
		return interfaceId >= TAB_BUTTON_START + (excludeTabZero ? 1 : 0) && interfaceId < TAB_BUTTON_START + TOTAL_TABS;
	}

	/**
	 * Sets the current bank tab.
	 * @param tab	The tab to display.
	 */
	public static void setCurrentTab(int tab) {
		currentTab = tab;

		Widget button = Widget.interfaceCache[TAB_BUTTON_START + tab];
		Client.instance.setButtonConfig(button.valueIndexArray[0][1], button.requiredValues[0]);

		closeSearch();
	}

	/**
	 * Checks if the tab button bar is hidden.
	 */
	public static boolean hideTabBar() {
		return tabsCount == 1 && Client.instance.settings[Widget.interfaceCache[START_ID + 107].valueIndexArray[0][1]] == 3;
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

		for (int tab = 0; tab < TOTAL_TABS; tab++) {
			Arrays.fill(Widget.interfaceCache[SEARCH_CONTAINER_START + tab].inventoryItemId, 0);
			Arrays.fill(Widget.interfaceCache[SEARCH_CONTAINER_START + tab].inventoryAmounts, 0);
		}
	}

	/**
	 * Sets the searching versions of the tab containers item arrays and updates the search text.
	 */
	public static void setSearchContainers() {
		if (!isSearching())
			return;

		int foundAmt = 0;
		for(int tab = 0; tab < TOTAL_TABS; tab++) {
			Widget searchTab = Widget.interfaceCache[SEARCH_CONTAINER_START + tab];
			Widget bankTab = Widget.interfaceCache[CONTAINER_START + tab];
			Arrays.fill(searchTab.inventoryItemId, -1);
			Arrays.fill(searchTab.inventoryAmounts, -1);
			for (int slot = 0, searchSlot = 0; slot < bankTab.inventoryItemId.length; slot++) {
				if (bankTab.inventoryItemId[slot] > 0) { //&& bankTab.inventoryAmounts[slot] > 0
					String item = ItemDefinition.lookup(bankTab.inventoryItemId[slot] - 1).name;
					if (item != null && item.toLowerCase().contains(searchInput.toLowerCase())) {
						searchTab.inventoryItemId[searchSlot] = bankTab.inventoryItemId[slot];
						searchTab.inventoryAmounts[searchSlot++] = bankTab.inventoryAmounts[slot];
						foundAmt++;
					}
				}
			}
		}

		Client.instance.enter_name_title = "Show items whose names contain the following text:" + (searchInput.length() > 0 ? " (" + foundAmt + " found)" : "");
	}

	/**
	 * Finds the actual tab container item slot of a given searching tab container item slot.
	 * @param fromInterface     The searching tab's container id.
	 * @param fromSlot          The searching tab's item slot.
	 * @return                  The actual tab's item slot.
	 */
	public static int getActualSlot(int fromInterface, int fromSlot) {
		if (fromInterface == -1 || fromSlot == -1)
			return fromSlot;

		Widget searchTab = Widget.interfaceCache[fromInterface];
		Widget bankTab = Widget.interfaceCache[fromInterface - TOTAL_TABS];
		int itemId = searchTab.inventoryItemId[fromSlot];
		for(int i = 0; i < bankTab.inventoryItemId.length; i++) {
			if(bankTab.inventoryItemId[i] == itemId) {
				return i;
			}
		}

		return fromSlot;
	}

	/**
	 * Updates tooltips in the interface.
	 */
	private static void updateToolTips() {
		Widget button = Widget.interfaceCache[BANK_QUANTITY_X_BUTTON];
        button.tooltip = modifiableXValue < 1 ? "" : "Default quantity: X";

        button = Widget.interfaceCache[PLACEHOLDER_BUTTON];
        button.tooltip = (Client.instance.settings[button.valueIndexArray[0][1]] == 0 ? "Enable" : "Disable") + " @lre@Always set placeholders";
		
		button = Widget.interfaceCache[START_ID + 109];
		button.tooltip = (Client.instance.settings[button.valueIndexArray[0][1]] == 0 ? "Enable" : "Disable") + " @lre@" + button.getDefaultText();

		button = Widget.interfaceCache[START_ID + 110];
		button.tooltip = (Client.instance.settings[button.valueIndexArray[0][1]] == 0 ? "Hide" : "Show") + " @lre@" + button.getDefaultText();
	}

	/**
	 * Updates the entire interface.
	 */
	public static void updateInterface() {
		// Update bank title
		Widget title = Widget.interfaceCache[5383];
		if (!Client.instance.interfaceIsSelected(Widget.interfaceCache[START_ID + 5])) {
			title.setDefaultText("Bank settings menu");
		} else if (showSearchContainers()) {
			title.setDefaultText("Showing items: @red@" + searchInput.toLowerCase());
		} else if (currentTab != 0) {
			title.setDefaultText("Tab " + currentTab);
		} else {
			if (isSearching() && Client.instance.getInputDialogState() == 0) {
				closeSearch();
			}
			title.setDefaultText(titleText);
		}

		// Show bank interface or bank settings interface
		if (Client.instance.interfaceIsSelected(Widget.interfaceCache[START_ID + 5])) {
			Widget.interfaceCache[5292].children[8] = BANKING_INTERFACE_LAYER;
		} else {
			Widget.interfaceCache[5292].children[8] = SETTINGS_INTERFACE_LAYER;
		}

		// Show the main tab if we are searching
		if (isSearching() && currentTab != 0) {
			currentTab = 0;

			Widget button = Widget.interfaceCache[TAB_BUTTON_START];
			Client.instance.setButtonConfig(button.valueIndexArray[0][1], button.requiredValues[0]);
		}

		// Calculate amount of tabs
		int tabsAmt = 1;
		for (int i = tabsAmt; i < TOTAL_TABS; i++) {
			for (int slot = 0; slot < Widget.interfaceCache[CONTAINER_START + i].inventoryItemId.length; slot++) {
				if (Widget.interfaceCache[CONTAINER_START + i].inventoryItemId[slot] > 0) { //&& Widget.interfaceCache[CONTAINER_START + i].inventoryAmounts[slot] > 0
					tabsAmt++;
					break;
				}
			}
		}
		tabsCount = tabsAmt;

		updateToolTips();

		// Bank width and height
		int vOffset = ClientUI.frameHeight - 503;
		int maxBankHeight = 750; // Could make higher but would have to change getMousePositions method
		if(vOffset + 331 > maxBankHeight) {
			vOffset = maxBankHeight - 331;
		}
		height = 331 + vOffset;

		int hOffset = 0;
		if(Client.instance.settings[Widget.interfaceCache[START_ID + 109].valueIndexArray[0][1]] == 1) {
			width = 488;
		} else {
			hOffset = ClientUI.frameWidth - 765;
			int maxBankWidth = 786;
			if(hOffset + 488 > maxBankWidth) {
				hOffset = maxBankWidth - 488;
			}
			width = 488 + hOffset;
		}

		if(oldWidth != width || oldHeight != height) {
			Widget.addBackground(START_ID, width, height, true, AutomaticSprite.BACKGROUND_BLACK);
			oldWidth = width;
			oldHeight = height;
		}

		// Resizable bank offsets
		Widget.interfaceCache[5292].width = 488 + hOffset + 24; // Interface
		Widget.interfaceCache[5292].height = 331 + vOffset + 3;
		Widget.interfaceCache[START_ID + 1].horizontalOffset = hOffset; // Close button

		//Widget.interfaceCache[]

		Widget.interfaceCache[5383].horizontalOffset = hOffset / 2; // Title text
		Widget.interfaceCache[BANKING_INTERFACE_LAYER].width = 488 + hOffset - 10; // Bank interface
		Widget.interfaceCache[BANKING_INTERFACE_LAYER].height = 331 + vOffset - 40;
		Widget.interfaceCache[TABS_INTERFACE_LAYER].width = 488 + hOffset - 10; // Tab bar
		Widget.interfaceCache[START_ID + 9].width = 488 + hOffset - 10; // Divider line
		Widget.interfaceCache[5385].width = 488 + hOffset - 27; // Scroll bar
		Widget.interfaceCache[5385].height = 331 + vOffset - 121;
		int extraColumns = hOffset / (32 + Widget.interfaceCache[CONTAINER_START].spritePaddingX);
		int containerWidthIncrease = (32 + Widget.interfaceCache[CONTAINER_START].spritePaddingX) * extraColumns;
		int containerXOffset = hOffset / 2 - (containerWidthIncrease / 2);
		for (int i = 0; i < TOTAL_TABS; i++) { // Tabs and containers and dividers
			if (i < 9) {
				Widget.interfaceCache[START_ID + 40 + i].width = 374 + containerWidthIncrease;
				Widget.interfaceCache[START_ID + 40 + i].horizontalOffset = containerXOffset;
			}
			Widget container = Widget.interfaceCache[CONTAINER_START + i];
			Widget searchContainer = Widget.interfaceCache[SEARCH_CONTAINER_START + i];
			container.width = searchContainer.width = extraColumns + 8;
			Widget.interfaceCache[TAB_BUTTON_START + i].horizontalOffset = Widget.interfaceCache[START_ID + 20 + i].horizontalOffset = container.horizontalOffset = searchContainer.horizontalOffset = containerXOffset;
		}
		Widget.interfaceCache[START_ID + 70].verticalOffset = vOffset; // Left button bar
		Widget.interfaceCache[START_ID + 81].verticalOffset = vOffset; // Right button bar
		Widget.interfaceCache[SETTINGS_INTERFACE_LAYER].horizontalOffset = hOffset / 2; // Settings

		// Hide deposit equipment
		if (Client.instance.settings[Widget.interfaceCache[START_ID + 110].valueIndexArray[0][1]] == 1) {
			Widget.interfaceCache[START_ID + 88].invisible = Widget.interfaceCache[START_ID + 89].invisible = true;
			Widget.interfaceCache[START_ID + 70].horizontalOffset = 18;
			Widget.interfaceCache[START_ID + 81].horizontalOffset = hOffset + 20; // WAS 18, CHANGED TO 20 BECAUSE PLACEHOLDERS IS COMMENTED OUT
		} else {
			Widget.interfaceCache[START_ID + 88].invisible = Widget.interfaceCache[START_ID + 89].invisible = false;
			Widget.interfaceCache[START_ID + 70].horizontalOffset = 0;
			Widget.interfaceCache[START_ID + 81].horizontalOffset = hOffset;
		}

		// Hide tab bar button
		Widget tabBarButton = Widget.interfaceCache[START_ID + 107];
		if (tabsCount == 1) {
			Widget.interfaceCache[START_ID + 108].opacity = (byte) 255;
			tabBarButton.tooltip = "Hide";
			tabBarButton.disabledSprite = SpriteLoader.getSprite(873);
			tabBarButton.enabledSprite = SpriteLoader.getSprite(874);
		} else {
			Widget.interfaceCache[START_ID + 108].opacity = 0;
			tabBarButton.tooltip = "";
			tabBarButton.disabledSprite = tabBarButton.enabledSprite = SpriteLoader.getSprite(875);

			// Reset tab display config if somehow it got set to hiding tab bar while there is more than one bank tab
			if (Client.instance.settings[Widget.interfaceCache[START_ID + 107].valueIndexArray[0][1]] == 3) {
				Client.instance.setButtonConfig(tabBarButton.valueIndexArray[0][1], 0);
			}
		}
		
		// Release all placeholders button, text is being set server side so we don't have to do any placeholder calculations here
        Widget releaseButton = Widget.interfaceCache[RELEASE_PLACEHOLDERS_BUTTON];
        releaseButton.tooltip = releaseButton.getDefaultText();
        if (!"Release all placeholders".equals(releaseButton.getDefaultText())) {
            releaseButton.disabledSprite = releaseButton.enabledSprite = releaseButton.hoverSprite1 = releaseButton.hoverSprite2 = SpriteLoader.getSprite(127);
            releaseButton.width = releaseButton.disabledSprite.myWidth;
            releaseButton.height = releaseButton.disabledSprite.myHeight;
            releaseButton.textColor = 0xff981f;
            releaseButton.defaultHoverColor = 0xffffff;
            releaseButton.horizontalOffset = -8;
        } else {
            releaseButton.disabledSprite = releaseButton.enabledSprite = releaseButton.hoverSprite1 = releaseButton.hoverSprite2 = SpriteLoader.getSprite(126);
            releaseButton.width = releaseButton.disabledSprite.myWidth;
            releaseButton.height = releaseButton.disabledSprite.myHeight;
            releaseButton.textColor = releaseButton.defaultHoverColor = 0x8f8f8f;
            releaseButton.horizontalOffset = 0;
        }

        // Tab buttons
		if (hideTabBar()) {
			Widget.interfaceCache[5385].verticalOffset = -42;
			Widget.interfaceCache[5385].height = 331 + vOffset - 121 + 42;
			Widget.interfaceCache[START_ID + 5].horizontalOffset = Widget.interfaceCache[START_ID + 6].horizontalOffset = hOffset + -18;
			Widget.interfaceCache[START_ID + 5].verticalOffset = Widget.interfaceCache[START_ID + 6].verticalOffset = 6;
			Widget.interfaceCache[TABS_INTERFACE_LAYER].invisible = true;
		} else {
			Widget.interfaceCache[5385].verticalOffset = 0;
			Widget.interfaceCache[5385].height = 331 + vOffset - 121;
			Widget.interfaceCache[START_ID + 5].horizontalOffset = Widget.interfaceCache[START_ID + 6].horizontalOffset = hOffset;
			Widget.interfaceCache[START_ID + 5].verticalOffset = Widget.interfaceCache[START_ID + 6].verticalOffset = 0;
			Widget.interfaceCache[TABS_INTERFACE_LAYER].invisible = false;
			for (int tab = 0; tab < TOTAL_TABS; tab++) {
				Widget container = Widget.interfaceCache[CONTAINER_START + tab];

				// First we search for an item in the tab...
				int firstItemSlot = -1;
				int firstItemAmount = 0;
				for (int slot = 0; slot < container.inventoryItemId.length; slot++) {
					if (container.inventoryItemId[slot] > 0) { //&& container.inventoryAmounts[slot] > 0
						firstItemSlot = container.inventoryItemId[slot] - 1;
						firstItemAmount = container.inventoryAmounts[slot];
						break;
					}
				}

				Widget button = Widget.interfaceCache[TAB_BUTTON_START + tab];
				Widget icon = Widget.interfaceCache[START_ID + 20 + tab];

				boolean draggingTab = Client.instance.getItemDragType() != 0 && Client.instance.getAnInt1084() >= TAB_BUTTON_START && Client.instance.getAnInt1084() < TAB_BUTTON_START + TOTAL_TABS && Client.instance.getAnInt1084() - (TAB_BUTTON_START) == tab;

				if (firstItemSlot > 0 || tab == 0) {
					button.actions = tab == 0 ? new String[]{"View all items", "Collapse all tabs" , "Remove placeholders" }
							: new String[]{"View tab @lre@" + tab, "Collapse tab @lre@" + tab, "Remove placeholders @lre@" + tab };

					// Have to set the sprites this way because the new tab button uses different sprites
					button.disabledSprite = SpriteLoader.getSprite(205);
					button.enabledSprite = SpriteLoader.getSprite(207);
					button.hoverSprite1 = SpriteLoader.getSprite(206);
					button.hoverSprite2 = SpriteLoader.getSprite(207);

					button.hidden = false;

					if (tab != 0) {
						Sprite sprite;
						boolean useItemIcon = false;

						switch (Client.instance.settings[Widget.interfaceCache[START_ID + 104].valueIndexArray[0][1]]) {
							case 1:
								// Digit
								sprite = SpriteLoader.getSprite(219 + tab);
								break;
							case 2:
								// Roman numeral
								sprite = SpriteLoader.getSprite(210 + tab);
								break;

							default:
								// First item
								sprite = ItemDefinition.getSprite(firstItemSlot, firstItemAmount, 0);
								useItemIcon = true;
								break;
						}

						int w = 512, h = 334;
						int x = ClientUI.frameMode == Client.ScreenMode.FIXED ? 0 : (ClientUI.frameWidth / 2) - 256;
						int y = ClientUI.frameMode == Client.ScreenMode.FIXED ? 0 : (ClientUI.frameHeight / 2) - 167;
						int count = Client.instance.displaySideStonesStacked() ? 3 : 4;
						if (ClientUI.frameMode != Client.ScreenMode.FIXED) {
							if (Widget.interfaceCache[ClientCompanion.openInterfaceId].width > w || Widget.interfaceCache[ClientCompanion.openInterfaceId].height > h) {
								x = ClientUtil.getLargeResizableInterfaceOffsetLeftX();
								y = ClientUtil.getLargeResizableInterfaceOffsetTopY();
							}
							for (int i = 0; i < count; i++) {
								if (x + w > (ClientUI.frameWidth - 225)) {
									x = x - 30;
									if (x < 0) {
										x = 0;
									}
								}
								if (y + h > (ClientUI.frameHeight - 182)) {
									y = y - 30;
									if (y < 0) {
										y = 0;
									}
								}
							}
						}

						int tileX = 39 + 40 * tab + 3 + 17 + containerXOffset + x;
						int tileY = 4 + 37 + y;

						boolean isIconPlaceholder = useItemIcon && firstItemAmount == 0;

						if (draggingTab && !Client.instance.isMenuOpen()) {
							icon.transparency = 0;

							int differenceX = Client.instance.getMouseX()
									- Client.instance.getPreviousClickMouseX();
							int differenceY = Client.instance.getMouseY()
									- Client.instance.getPreviousClickMouseY();
							if (Client.instance.getDragItemDelay() < Configuration.dragValue) {
								differenceX = 0;
								differenceY = 0;
							}
							if (!Client.instance.getABoolean1242()) {
								differenceX = 0;
								differenceY = 0;
							}

							int amount = isIconPlaceholder ? firstItemAmount : 1;
							ClientUtil.setOverlayItem(sprite, amount, tileX + differenceX, tileY + differenceY, TABS_INTERFACE_LAYER, true);
						} else {
							if (isIconPlaceholder) {
								icon.transparency = 95;
							} else {
								icon.transparency = 255;
							}
						}

						icon.disabledSprite = icon.enabledSprite = sprite;
						icon.hidden = false;
					}
				} else {
					button.hidden = true;
					icon.hidden = true;

					if (tab == tabsCount) {
						button.actions = new String[]{"New tab"};
						button.disabledSprite = button.enabledSprite = button.hoverSprite1 = button.hoverSprite2 = SpriteLoader.getSprite(208);
						button.hidden = false;

						if (draggingTab && !Client.instance.isMenuOpen()) {
							icon.transparency = 128;
						} else {
							icon.transparency = 255;
						}

						icon.disabledSprite = icon.enabledSprite = SpriteLoader.getSprite(210);
						icon.hidden = false;
					}
					// Don't break the loop because we may need to hide tabs after the last tab
				}
			}
		}

		// The scroll container that is the parent of all of the item containers
		Widget scroll = Widget.interfaceCache[5385];
		// Reset scroll's children and hide them
		for (int i = 0; i < scroll.children.length; i++) {
			Widget.interfaceCache[scroll.children[i]].hidden = true;
			Widget.interfaceCache[scroll.children[i]].verticalOffset = 0;
		}

		// Show the correct item container(s)
		if (currentTab == 0) {
			int currentY = 0;

			if (hideTabBar()) {
				currentY += 11;
			}

			for (int tab = 0; tab < tabsCount; tab++) {
				Widget container =  Widget.interfaceCache[START_ID + (showSearchContainers() ? 60 : 50) + tab];

				// Calculate amount of rows that are occupied in this container
				int rows = Widget.getContainerRowsAmount(container);

				// Position
				container.verticalOffset = currentY;

				// Show the container
				container.hidden = false;

				// Increment currentY
				currentY += rows * (32 + container.spritePaddingY) + (rows == 0 ? 5 : 1);

				// Divider
				if (tab != tabsCount - 1) {
					Widget divider = Widget.interfaceCache[START_ID + 40 + tab];

					// Position
					divider.verticalOffset = currentY;

					// Show the divider
					divider.hidden = false;

					// Increment currentY
					currentY += 7;
				}
			}

			// Set scroll max
			int scrollMax = currentY + 3;
			if (scrollMax < scroll.height) {
				scrollMax = scroll.height;
			}
			scroll.scrollMax = scrollMax;
		} else {
			Widget container = Widget.interfaceCache[CONTAINER_START + currentTab];

			// Show the container
			container.hidden = false;

			// Set scroll max
			scroll.scrollMax = Widget.getContainerScrollMax(container);
		}
	}

}
