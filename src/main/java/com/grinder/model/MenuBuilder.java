package com.grinder.model;

import com.grinder.Configuration;
import com.grinder.client.ClientCompanion;
import com.grinder.client.ClientUtil;
import com.grinder.client.util.MenuOpcodes;
import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.cache.def.ItemDefinition;
import com.runescape.cache.def.NpcDefinition;
import com.runescape.cache.def.ObjectDefinition;
import com.runescape.cache.definition.OSObjectDefinition;
import com.runescape.cache.graphics.widget.*;
import com.runescape.collection.NodeDeque;
import com.runescape.entity.Item;
import com.runescape.entity.Npc;
import com.runescape.entity.Player;
import com.runescape.entity.PlayerRelations;
import com.runescape.entity.model.Model;
import com.runescape.input.KeyHandler;
import com.runescape.input.MouseHandler;
import com.runescape.scene.DynamicObject;
import com.runescape.scene.ViewportMouse;

public final class MenuBuilder {

    public static void addMenuAction(Client client, int opcode, int x, int y, long tag, String action){
        client.menuOpcodes[client.menuOptionsCount] = opcode;
        client.menuActions[client.menuOptionsCount] = action;
        client.menuArguments0[client.menuOptionsCount] = tag;
        client.menuArguments1[client.menuOptionsCount] = x;
        client.menuArguments2[client.menuOptionsCount] = y;
        client.menuOptionsCount++;
    }

    public static void addMenuAction(Client client, int opcode, String action, int... args){
        client.menuOpcodes[client.menuOptionsCount] = opcode;
        client.menuActions[client.menuOptionsCount] = action;
        if (args.length > 0)
            client.menuArguments0[client.menuOptionsCount] = args[0];
        if (args.length > 1)
            client.menuArguments1[client.menuOptionsCount] = args[1];
        if (args.length > 2)
            client.menuArguments2[client.menuOptionsCount] = args[2];
        client.menuOptionsCount++;
    }

    public static void addMenuAction(Client client, int opcode, int... args) {
        client.menuOpcodes[client.menuOptionsCount] = opcode;
        if (args.length > 0)
            client.menuArguments0[client.menuOptionsCount] = args[0];
        if (args.length > 1)
            client.menuArguments1[client.menuOptionsCount] = args[1];
        if (args.length > 2)
            client.menuArguments2[client.menuOptionsCount] = args[2];
    }

    public static void setMenuAction(Client client, int index, int opcode, String action, int... args){
        client.menuOpcodes[index] = opcode;
        client.menuActions[index] = action;
        if (args.length > 0)
            client.menuArguments0[index] = args[0];
        if (args.length > 1)
            client.menuArguments1[index] = args[1];
        if (args.length > 2)
            client.menuArguments2[index] = args[2];
        client.menuOptionsCount = index + 1;
    }

    public static void insertMenuItem(Client client, String action, int opcode, int arg0, int arg1, int arg2) {
        client.menuActions[client.menuOptionsCount] = action;
        insertMenuItem(client, opcode, arg0, arg1, arg2);
    }

    static void insertMenuItem(Client client, int opcode, int arg0, int arg1, int arg2) {
        client.menuOpcodes[client.menuOptionsCount] = opcode;
        client.menuArguments0[client.menuOptionsCount] = arg0;
        client.menuArguments1[client.menuOptionsCount] = arg1;
        client.menuArguments2[client.menuOptionsCount] = arg2;
        client.menuOptionsCount++;
    }

    public static void buildInterfaceMenu(Client client, Widget widget, int widgetX, int widgetY, int mouseMovementX, int mouseMovementY, int scrollPosition) {
        if (widget == null || widget.type != 0 || widget.children == null || widget.invisible || widget.hidden)
            return;

        if (mouseMovementX < widgetX
                || mouseMovementY < widgetY
                || mouseMovementX >= widgetX + widget.width
                || mouseMovementY >= widgetY + widget.height)
            return;

        for (int idx = 0; idx < widget.children.length; idx++) {
            int childX = widget.childX[idx] + widgetX;
            int childY = (widget.childY[idx] + widgetY) - scrollPosition;
            Widget child = Widget.interfaceCache[widget.children[idx]];
            if (child == null || child.invisible || child.hidden) {
                continue;
            }
            childX += child.horizontalOffset;
            childY += child.verticalOffset;

            final boolean isHovering = mouseMovementX >= childX
                    && mouseMovementY >= childY
                    && mouseMovementX < childX + child.width
                    && mouseMovementY < childY + child.height;

            if (isHovering) {
                if (child.hoverType >= 0 || child.defaultHoverColor != 0) {
                    if (child.hoverType >= 0)
                        client.childWidgetHoverType = child.hoverType;
                    else
                        client.childWidgetHoverType = child.id;
                }
                if (child.type == 8)
                    client.childWidgetId = child.id;
            }

            if (child.type == Widget.TYPE_CONTAINER) {
                buildInterfaceMenu(client, child, childX, childY, mouseMovementX, mouseMovementY, child.scrollPosition);
                if (child.scrollMax > child.height)
                    WidgetManager.handleWidgetScrollBarDragging(client, child, child.height, childX + child.width, childY, mouseMovementX, mouseMovementY, child.scrollMax);
            }

            if (isHovering){
                if (child.type == Widget.TYPE_HOVER
                        || child.type == Widget.TYPE_CONFIG_HOVER
                        || child.type == Widget.TYPE_ADJUSTABLE_CONFIG
                        || child.type == Widget.TYPE_BOX)
                    child.toggled = false;

                final String suffix = client.isAdmin() ? " @lre@(" + child.id + ")" : "";
                if (child.atActionType == Widget.OPTION_OK)
                    handleOkOptionHover(client, child);
                else if (child.atActionType == Widget.OPTION_USABLE && client.spellSelected == 0)
                    handleSpellHover(client, child);
                else if (child.atActionType == Widget.OPTION_DROPDOWN)
                    handleDropdownHover(client, widget, mouseMovementX, mouseMovementY, childX, childY, child);
                else if (child.atActionType == Widget.OPTION_COLOR_BOX)
                    handleColorBoxHover(client, child);
                else if (child.atActionType == Widget.OPTION_CLOSE)
                    addMenuAction(client, MenuOpcodes.CLOSE, "Close", 0, 0, child.id);
                else if (child.atActionType == Widget.OPTION_TOGGLE_SETTING)
                    addMenuAction(client, MenuOpcodes.TOOLTIP, child.tooltip + suffix, 0, 0, child.id);
                else if (child.atActionType == Widget.OPTION_CONTINUE && !client.continuedDialogue)
                    addMenuAction(client, MenuOpcodes.CONTINUE_DIALOGUE, child.tooltip, 0, 0, child.id);
                else if(child.atActionType == Widget.OPTION_RESET_SETTING) {
                    boolean flag = child.tooltip == null || child.tooltip.length() == 0
                            || // prevents menu being shown on selected setting (e.g. bank settings menu)
                            (client.interfaceIsSelected(child) && child.hideTooltipIfSelected);
                    if (!flag)
                        addMenuAction(client, MenuOpcodes.SET_CONFIG, child.tooltip + suffix, 0, 0, child.id);
                }
                if (child.actions != null && !child.invisible && !child.hidden)
                    if (!(child.contentType == 206 && client.interfaceIsSelected(child)))
                        handleClanChatMemberListHover(client, child);
                if (child.id > Bank.TAB_BUTTON_START && child.id < Bank.TAB_BUTTON_START + Bank.TOTAL_TABS)
                    client.mouseInvInterfaceIndex = child.id - (Bank.TAB_BUTTON_START);
            }

            if (child.type == Widget.TYPE_INVENTORY
                    && !child.invisible
                    && !child.hidden
                    && !(child.id >= 22035
                    && child.id <= 22042))
                handleInventoryHover(client, mouseMovementX, mouseMovementY, childX, childY, child);
        }
    }


    private static void handleSpellHover(Client client, Widget childInterface) {
        if (canAutocastSpell(childInterface))
            addMenuAction(client, MenuOpcodes.SET_AUTO_CAST, "Autocast @gre@" + childInterface.spellName, 0, 0, childInterface.id);
        String actionName = childInterface.selectedActionName;
        if (actionName.contains(" "))
            actionName = actionName.substring(0, actionName.indexOf(" "));
        actionName += " @gre@" + childInterface.spellName;
        if (client.isAdmin())
            actionName +=  " " + childInterface.id;
        addMenuAction(client, MenuOpcodes.SELECT_SPELL, actionName, 0, 0, childInterface.id);
        if (Configuration.autocastOnTop)
            shiftMenuActionRowToTop(client, MenuOpcodes.SET_AUTO_CAST);
    }

    private static void handleOkOptionHover(Client client, Widget childInterface) {
        boolean ignoreToolTip = false;

        if (childInterface.contentType != 0 && (childInterface.parent == 5065 || childInterface.parent == 5715))
            ignoreToolTip = PlayerRelations.buildFriendsListMenu(client, childInterface);

        if (childInterface.tooltip == null || childInterface.tooltip.length() == 0)
            ignoreToolTip = true;

        if (!ignoreToolTip) {
            addMenuAction(client, MenuOpcodes.SELECT_CHILD, client.isHighStaff()
                    ? childInterface.tooltip + " " + childInterface.id
                    : childInterface.tooltip, -1, -1, childInterface.id);
            if (client.isHighStaff())
                addMenuAction(client, MenuOpcodes.TOGGLE_BUTTON, "Toggle @cya@"+ childInterface.id, childInterface.id);
        }
    }

    private static void handleDropdownHover(Client client, Widget widget, int mouseMovementX, int mouseMovementY, int widgetXCoord, int widgetYCoord, Widget childInterface) {
        boolean flag = false;
        childInterface.hovered = false;
        childInterface.dropdownHover = -1;

        if (childInterface.dropdown.isOpen()) {

            // Inverted keybinds dropdown
            if (childInterface.type == Widget.TYPE_KEYBINDS_DROPDOWN && childInterface.inverted
                    && mouseMovementX >= widgetXCoord
                    && mouseMovementX < widgetXCoord + (childInterface.dropdown.getWidth() - 16)
                    && mouseMovementY >= widgetYCoord - childInterface.dropdown.getHeight() - 10
                    && mouseMovementY < widgetYCoord) {

                int yy = mouseMovementY - (widgetYCoord - childInterface.dropdown.getHeight());

                if (mouseMovementX > widgetXCoord + (childInterface.dropdown.getWidth() / 2)) {
                    childInterface.dropdownHover = ((yy / 15) * 2) + 1;
                } else {
                    childInterface.dropdownHover = (yy / 15) * 2;
                }
                flag = true;
            } else if (!childInterface.inverted && mouseMovementX >= widgetXCoord
                    && mouseMovementX < widgetXCoord + (childInterface.dropdown.getWidth() - 16)
                    && mouseMovementY >= widgetYCoord + 19
                    && mouseMovementY < widgetYCoord + 19 + childInterface.dropdown.getHeight()) {

                int yy = mouseMovementY - (widgetYCoord + 19);

                if (childInterface.type == Widget.TYPE_KEYBINDS_DROPDOWN
                        && childInterface.dropdown.doesSplit()) {
                    if (mouseMovementX > widgetXCoord + (childInterface.dropdown.getWidth() / 2)) {
                        childInterface.dropdownHover = ((yy / 15) * 2) + 1;
                    } else {
                        childInterface.dropdownHover = (yy / 15) * 2;
                    }
                } else {
                    childInterface.dropdownHover = yy / 14; // Regular
                    // dropdown
                    // hover
                }
                flag = true;
            }
            if (flag) {
                if (client.menuOptionsCount != 1) {
                    client.menuOptionsCount = 1;
                }
                client.menuActions[client.menuOptionsCount] = "Select";
                insertMenuItem(client, 770, childInterface.id, childInterface.dropdownHover, widget.id);
            }
        }
        if (mouseMovementX >= widgetXCoord && mouseMovementY >= widgetYCoord
                && mouseMovementX < widgetXCoord + childInterface.dropdown.getWidth()
                && mouseMovementY < widgetYCoord + 24 && client.menuOptionsCount == 1) {
            childInterface.hovered = true;
            addMenuAction(client, MenuOpcodes.DROPDOWN, childInterface.dropdown.isOpen() ? "Hide" : "Show", widget.id, 0, childInterface.id);
        }
    }

    private static boolean canAutocastSpell(Widget childInterface) {
        return childInterface.spellName.endsWith("Rush") || childInterface.spellName.endsWith("Burst")
                || childInterface.spellName.endsWith("Blitz")
                || childInterface.spellName.endsWith("Barrage")
                || childInterface.spellName.endsWith("Strike")
                || childInterface.spellName.endsWith("Bolt")
                || childInterface.spellName.equalsIgnoreCase("Crumble undead")
                || childInterface.spellName.endsWith("Blast")
                || childInterface.spellName.endsWith("Wave")
                || childInterface.spellName.equalsIgnoreCase("Claws of Guthix")
                || childInterface.spellName.equalsIgnoreCase("Flames of Zamorak")
                || childInterface.spellName.endsWith("Surge")
                || childInterface.spellName.endsWith("Agony")
                || childInterface.spellName.endsWith("Bash")
                || childInterface.spellName.equalsIgnoreCase("Magic Dart");
    }

    private static void handleInventoryHover(Client client, int mouseX, int mouseY, int widgetX, int widgetY, Widget widget) {
        int[] itemIds = widget.inventoryItemId;
        int[] itemAmounts = widget.inventoryAmounts;

        // Shop filtering
        if (widget.id == Shop.CONTAINER && Shop.showSearchContainers()) {
            itemIds = Shop.getShopIdTemp();
            itemAmounts = Shop.getShopAmountsTemp();
        }

        // Last hovered container region
        if (mouseX >= widgetX && mouseY >= widgetY - (widget.contentType == 206 ? 5 : 0) // Make the hover region for bank tabs 5 taller, makes dragging items into tabs more aligned with the dividers
                && mouseX < widgetX + widget.width * (32 + widget.spritePaddingX) - widget.spritePaddingX
                && mouseY < widgetY + widget.height * (32 + widget.spritePaddingY) - widget.spritePaddingY) {
            client.mouseInvInterfaceIndex = -1;
            client.lastActiveInvInterface = widget.id;
        }

        // Find last slot of a bank container
        int lastSlot = 0;
        if (widget.contentType == 206) {
            for (int idx = itemIds.length - 1; idx >= 0; idx--) {
                if (itemIds[idx] > 0) {
                    lastSlot = idx;
                    break;
                }
            }
        }

        int k2 = 0;
        rowLoop:
        for (int l2 = 0; l2 < widget.height; l2++) {
            for (int i3 = 0; i3 < widget.width; i3++) {
                // End the loop if we're past the last filled slot of a bank container
                if (widget.contentType == 206 && k2 > lastSlot) {
                    break rowLoop;
                }
                int j3 = widgetX + i3 * (32 + widget.spritePaddingX);
                int k3 = widgetY + l2 * (32 + widget.spritePaddingY);
                if (k2 < 20) {
                    j3 += widget.spritesX[k2];
                    k3 += widget.spritesY[k2];
                }
                if (mouseX >= j3 && mouseY >= k3 && mouseX < j3 + 32
                        && mouseY < k3 + 32) {
                    client.mouseInvInterfaceIndex = k2;
                    client.lastActiveInvInterface = widget.id;
                    if (k2 >= itemIds.length) {
                        continue;
                    }

                    if (itemIds[k2] > 0) {

                        boolean hasDestroyOption = false;
                        ItemDefinition itemDef = ItemDefinition
                                .lookup(itemIds[k2] - 1);
                        if (client.itemSelected == 1 && widget.hasActions) {
                            if (widget.id != client.usedItemInventoryInterfaceId || k2 != client.usedItemInventorySlot) {
                                client.menuActions[client.menuOptionsCount] = "Use " + client.selectedItemName + " with @lre@"
                                        + itemDef.name;
                                insertMenuItem(client, MenuOpcodes.USE_ITEM_ON_ITEM, itemDef.id, k2, widget.id);
                            }
                        } else if (client.spellSelected == 1 && widget.hasActions) {
                            if ((client.spellUsableOn & 0x10) == 16) {
                                client.menuActions[client.menuOptionsCount] = client.spellTooltip + " @lre@" + itemDef.name;
                                insertMenuItem(client, MenuOpcodes.USE_SPELL_ON_ITEM, itemDef.id, k2, widget.id);
                            }
                        } else {
                            // puzzle box
                            if (widget.id == 6980) {
                                client.menuActions[client.menuOptionsCount] = "Move @lre@" + itemDef.name;
                                insertMenuItem(client, MenuOpcodes.ITEM_ACTION_5, itemDef.id, k2, widget.id);
                                continue;
                            } else if (widget.hasActions) {
                                for (int l3 = 4; l3 >= 3; l3--) {
                                    if (itemDef.actions != null && itemDef.actions[l3] != null) {
                                        client.menuActions[client.menuOptionsCount] = itemDef.actions[l3] + " @lre@"
                                                + itemDef.name;
                                        if (l3 == 3)
                                            client.menuOpcodes[client.menuOptionsCount] = MenuOpcodes.ITEM_ACTION_4;
                                        if (l3 == 4) {
                                            client.menuOpcodes[client.menuOptionsCount] = MenuOpcodes.ITEM_ACTION_5;
                                            hasDestroyOption = itemDef.actions[l3].contains("Destroy");
                                        }
                                        client.menuArguments0[client.menuOptionsCount] = itemDef.id;
                                        client.menuArguments1[client.menuOptionsCount] = k2;
                                        client.menuArguments2[client.menuOptionsCount] = widget.id;
                                        client.menuOptionsCount++;
                                    } else if (l3 == 4) {
                                        client.menuActions[client.menuOptionsCount] = "Drop @lre@" + itemDef.name;
                                        insertMenuItem(client, MenuOpcodes.ITEM_ACTION_5, itemDef.id, k2, widget.id);
                                    }
                                }
                            }
                            if (widget.usableItems) {
                                client.menuActions[client.menuOptionsCount] = "Use @lre@" + itemDef.name;
                                insertMenuItem(client, MenuOpcodes.USE_ITEM, itemDef.id, k2, widget.id);

                                if (Configuration.enableShiftClickDrop && !hasDestroyOption && !client.menuOpen
                                        && KeyHandler.pressedKeys[81]) {
                                    client.menuActionsRow("Drop @lre@" + itemDef.name);
                                    ClientCompanion.removeShiftDropOnMenuOpen = true;
                                }
                            }
                            if (widget.hasActions && itemDef.actions != null) {
                                for (int i4 = 2; i4 >= 0; i4--) {
                                    if (itemDef.actions[i4] != null) {
                                        client.menuActions[client.menuOptionsCount] = itemDef.actions[i4] + " @lre@"
                                                + itemDef.name;
                                        if (i4 == 0)
                                            client.menuOpcodes[client.menuOptionsCount] = MenuOpcodes.ITEM_ACTION_1;
                                        if (i4 == 1)
                                            client.menuOpcodes[client.menuOptionsCount] = MenuOpcodes.ITEM_ACTION_2;
                                        if (i4 == 2)
                                            client.menuOpcodes[client.menuOptionsCount] = MenuOpcodes.ITEM_ACTION_3;
                                        client.menuArguments0[client.menuOptionsCount] = itemDef.id;
                                        client.menuArguments1[client.menuOptionsCount] = k2;
                                        client.menuArguments2[client.menuOptionsCount] = widget.id;
                                        client.menuOptionsCount++;
                                    }
                                }
                                if (Configuration.enableShiftClickDrop && !hasDestroyOption && !client.menuOpen
                                        && KeyHandler.pressedKeys[81]) {
                                    client.menuActionsRow("Drop @lre@" + itemDef.name);
                                    ClientCompanion.removeShiftDropOnMenuOpen = true;
                                }
                                /*
                                 * Addons for Coal Bag
                                 */
                                if (KeyHandler.pressedKeys[81] && itemDef.id == 12019) {
                                    client.gameBagAction("Empty @lre@" + itemDef.name, MenuOpcodes.ITEM_ACTION_4);
                                }
                            }

                            //Menu actions, item options etc in interfaces
                            //Hardcoding
                            if (widget.actions != null) {
                                for (int type = widget.actions.length - 1; type >= 0; type--) {
                                    if (widget.actions[type] != null) {
                                        String action = widget.actions[type];

                                        //HARDCODING OF MENU ACTIONS

                                        if (ClientCompanion.openInterfaceId == PriceChecker.INTERFACE_ID && widget.id == 3322) { // Price checker
                                            switch (type) {
                                                case 0:
                                                    action = "Add";
                                                    break;
                                                case 1:
                                                    action = "Add-5";
                                                    break;
                                                case 2:
                                                    action = "Add-10";
                                                    break;
                                                case 3:
                                                    action = "Add-All";
                                                    break;
                                                case 4:
                                                    action = "Add-X";
                                                    break;
                                            }
                                        } else if (ClientCompanion.openInterfaceId == Bank.INTERFACE_ID) { // Bank options
                                            // Placeholder releasing
                                            if (widget.contentType == 206 && itemAmounts[k2] == 0) {
                                                client.menuActions[client.menuOptionsCount] = "Release @lre@" + itemDef.name;
                                                insertMenuItem(client, MenuOpcodes.RELEASE_PLACEHOLDER, itemDef.id, k2, widget.id);
                                                break;
                                            }
                                            // Modifiable x value
                                            if (type == 2 && Bank.getModifiableXValue() > 0) {
                                                client.menuActions[client.menuOptionsCount] = (widget.id == 5064 ? "Deposit-" : "Withdraw-") + Bank.getModifiableXValue() + " @lre@" + itemDef.name;
                                                insertMenuItem(client, MenuOpcodes.BANK_MODIFY_AMOUNT, itemDef.id, k2, widget.id);
                                            }
                                            // Wield/wear
                                            // TODO: ADD SUPPORT FOR EQUIPPING FROM BANK CONTAINERS
                                            if (type == widget.actions.length - 1 && widget.contentType != 206
                                                    && itemDef.actions != null && itemDef.actions[1] != null) {
                                                if (itemDef.id == 12019 || itemDef.id == 24480 ) {//Coal Bag
                                                    String input = itemDef.name.toLowerCase().contains("opened") ? "Empty" : "Fill";
                                                    client.menuActions[client.menuOptionsCount] = input + " @lre@" + itemDef.name;
                                                    insertMenuItem(client, MenuOpcodes.ITEM_ACTION_4, itemDef.id, k2, widget.id);
                                                } else {
                                                    client.menuActions[client.menuOptionsCount] = "Wear" + " @lre@" + itemDef.name;
                                                    insertMenuItem(client, MenuOpcodes.ITEM_ACTION_2, itemDef.id, k2, widget.id);
                                                }
                                            }
                                            // Placeholder
                                            if (type == widget.actions.length - 1 && widget.contentType == 206
                                                    && client.settings[Widget.interfaceCache[Bank.START_ID + 82].valueIndexArray[0][1]] == 0) {
                                                client.menuActions[client.menuOptionsCount] = "Placeholder @lre@" + itemDef.name;
                                                insertMenuItem(client, MenuOpcodes.ADD_PLACEHOLDER, itemDef.id, k2, widget.id);
                                            }
                                        } else if (widget.id == 1688) { // Equipment tab
                                            // Custom coloring items
                                            if (type == 1 && ItemColorCustomizer.ColorfulItem.getItemIds().containsKey(itemDef.id) && itemDef.id != ItemColorCustomizer.ColorfulItem.MAX_HOOD.getItemId()) {
                                                action = "Customize";
                                            }
                                        }

                                        client.menuActions[client.menuOptionsCount] = action + " @lre@" + itemDef.name;

                                        if (type == 0) client.menuOpcodes[client.menuOptionsCount] = MenuOpcodes.ITEM_CONTAINER_ACTION_1;
                                        if (type == 1) client.menuOpcodes[client.menuOptionsCount] = MenuOpcodes.ITEM_CONTAINER_ACTION_2;
                                        if (type == 2) client.menuOpcodes[client.menuOptionsCount] = MenuOpcodes.ITEM_CONTAINER_ACTION_3;
                                        if (type == 3) client.menuOpcodes[client.menuOptionsCount] = MenuOpcodes.ITEM_CONTAINER_ACTION_4;
                                        if (type == 4) client.menuOpcodes[client.menuOptionsCount] = MenuOpcodes.ITEM_CONTAINER_ACTION_5;
                                        if (type == 5) client.menuOpcodes[client.menuOptionsCount] = MenuOpcodes.ITEM_CONTAINER_ACTION_6;
                                        client.menuArguments0[client.menuOptionsCount] = itemDef.id;
                                        client.menuArguments1[client.menuOptionsCount] = k2;
                                        client.menuArguments2[client.menuOptionsCount] = widget.id;
                                        client.menuOptionsCount++;
                                    }
                                }


                                if (ClientCompanion.openInterfaceId == Bank.INTERFACE_ID) {
                                    // Custom bank quantity
                                    int[] qtys = {78, 867, 433, 53};
                                    int selectedTypeIndex = client.settings[Widget.interfaceCache[Bank.START_ID + 73].valueIndexArray[0][1]];
                                    if (selectedTypeIndex > 0)
                                        addMenuActionRowToTop(client, qtys[selectedTypeIndex - 1]);
                                    // Wield/wear shift click
                                    if (KeyHandler.pressedKeys[81] && widget.contentType != 206) {
                                        shiftMenuActionRowToTop(client, itemDef.id == 12019 ? MenuOpcodes.ITEM_ACTION_4 : MenuOpcodes.ITEM_ACTION_2);
                                    }
                                }
                            }
                            if (itemDef.name != null) {
                                if ((client.clientRights >= 4 && client.clientRights <= 7)) {
                                    client.menuActions[client.menuOptionsCount] = "Examine @lre@" + itemDef.name
                                            + " @gre@(@whi@" + (itemIds[k2] - 1)
                                            + "@gre@) int: " + widget.id;
                                } else {
                                    client.menuActions[client.menuOptionsCount] = "Examine @lre@" + itemDef.name;
                                }

                                insertMenuItem(client, MenuOpcodes.EXAMINE_ITEM, itemDef.id, k2, widget.id);
                            }
                        }
                    }
                }
                k2++;
            }
        }
    }

    private static void shiftMenuActionRowToTop(Client client, int type) {
        int row = 0;
        String text = null;
        int selected = 0;
        int first = 0;
        int second = 0;

        // Find the row
        for (int i = 0; i < client.menuOptionsCount; i++) {
            if (client.menuOpcodes[i] == type) {
                row = i;
                text = client.menuActions[i];
                selected = (int) client.menuArguments0[i];
                first = client.menuArguments1[i];
                second = client.menuArguments2[i];
                break;
            }
        }

        // If we didn't find a matching row then stop
        if (text == null)
            return;

        // Shift all rows after the matching row back one
        for (int i = row; i < client.menuOptionsCount; i++) {
            client.menuActions[i] = client.menuActions[i + 1];
            client.menuOpcodes[i] = client.menuOpcodes[i + 1];
            client.menuArguments0[i] = client.menuArguments0[i + 1];
            client.menuArguments1[i] = client.menuArguments1[i + 1];
            client.menuArguments2[i] = client.menuArguments2[i + 1];
        }

        // Decrement total rows by one
        client.menuOptionsCount--;

        // Add the row to the top
        client.menuActions[client.menuOptionsCount] = text;
        insertMenuItem(client, type, selected, first, second);
    }


    private static void addMenuActionRowToTop(Client client, int type) {
        String text = null;
        int selected = 0;
        int first = 0;
        int second = 0;

        // Find the row
        for (int i = 0; i < client.menuOptionsCount; i++) {
            if (client.menuOpcodes[i] == type) {
                text = client.menuActions[i];
                selected = (int) client.menuArguments0[i];
                first = client.menuArguments1[i];
                second = client.menuArguments2[i];
                break;
            }
        }

        // If we didn't find a matching row then stop
        if (text == null)
            return;

        client.menuActions[client.menuOptionsCount] = text;
        insertMenuItem(client, type, selected, first, second);
    }

    private static void handleClanChatMemberListHover(Client client, Widget childInterface) {
        // Clan chat tab member list actions use different button id than id that holds the name

        final int type = childInterface.type;
        final boolean clanChatTab = childInterface.parent == Widget.CLAN_CHAT_PARENT_WIDGET_ID;
        final String defaultText = clanChatTab ? Widget.interfaceCache[childInterface.id + Widget.CLAN_CHAT_MAX_MEMBERS].getDefaultText() : childInterface.getDefaultText();

        final boolean isTextChild = type == Widget.TYPE_TEXT && defaultText.length() > 0;
        final boolean isButtonChild = type == Widget.TYPE_SPRITE || type == Widget.TYPE_HOVER_SPRITE || type == Widget.TYPE_HOVER_SPEED_SPRITE;


        if (isTextChild || isButtonChild) {
            // HARDCODE CLICKABLE TEXT HERE

            boolean drawOptions = true;
            if (clanChatTab) {
                drawOptions = client.showClanOptions && !defaultText.equalsIgnoreCase(Client.localPlayer.name.toLowerCase());
            }

            if (drawOptions) {
                for (int action = childInterface.actions.length - 1; action >= 0; action--) {
                    if (childInterface.actions[action] != null) {
                        String name = (childInterface.type == Widget.TYPE_TEXT ? " @lre@" + defaultText : "");
                        String s = childInterface.actions[action] + name;

                        if (s.contains("img")) {
                            int prefix = s.indexOf("<img=");
                            int suffix = s.indexOf(">");
                            s = s.replaceAll(s.substring(prefix + 5, suffix), "");
                            s = s.replaceAll("</img>", "");
                            s = s.replaceAll("<img=>", "");
                        } else if (s.contains("clan=")) {
                            int prefix = s.indexOf("<clan=");
                            int suffix = s.indexOf(">");
                            s = s.replaceAll(s.substring(prefix + 6, suffix), "");
                            s = s.replaceAll("</clan>", "");
                            s = s.replaceAll("<clan=>", "");
                        }

                        client.menuActions[client.menuOptionsCount] = s;
                        client.menuOpcodes[client.menuOptionsCount] = 647;
                        client.menuArguments1[client.menuOptionsCount] = action;
                        client.menuArguments2[client.menuOptionsCount] = childInterface.id;
                        client.menuOptionsCount++;
                    }
                }
            }
        }
    }

    private static void handlePresetClick(Client client, Widget childInterface)
    {
        boolean flag = childInterface.id >= YellCustomizer.BRACKET_COLOR_BOX_ID
                && childInterface.id <= YellCustomizer.TITLE_SHADOW_COLOR_BOX_ID
                && !client.isHighStaff() && !client.isMember();
        if (!flag) {
            if (childInterface.actions != null) {
                for (int action = childInterface.actions.length - 1; action >= 0; action--) {
                    if (childInterface.actions[action] != null) {
                        addMenuAction(client, 1520 + action, "Load", 0, 0, childInterface.id);
                        addMenuAction(client, 1520 + action, "Delete", 0, 0, childInterface.id);
                    }
                }
            }
        }
    }

    private static void handleColorBoxHover(Client client, Widget childInterface) {
        boolean flag = childInterface.id >= YellCustomizer.BRACKET_COLOR_BOX_ID
                && childInterface.id <= YellCustomizer.TITLE_SHADOW_COLOR_BOX_ID
                && !client.isHighStaff() && !client.isMember();
        if (!flag) {
            if (childInterface.actions != null) {
                for (int action = childInterface.actions.length - 1; action >= 0; action--) {
                    if (childInterface.actions[action] != null)
                        addMenuAction(client, 1520 + action, childInterface.actions[action], 0, 0, childInterface.id);
                }
            }
        }
    }

    public static void buildSplitPrivateChatMenu(Client client) {
        if (client.splitPrivateChat == 0)
            return;
        int message = 0;
        if (client.systemUpdateTime != 0)
            message = 1;
        for (int index = 0; index < 100; index++)
            if (client.chatMessages[index] != null) {
                String msg = client.chatMessages[index];
                int chatType = client.chatTypes[index];
                String chatName = client.chatNames[index];

                byte rights = 0;
                if (ChatBox.showFromSplitPrivateChatMsg(client, chatType, chatName)) {
                    String[] splitMsg = ChatBox.drawChatMessage(client, rights, "From", null, chatName + ":", msg, 0, 0, 0, 0, 0, false, true);
                    int msgs = splitMsg.length;
                    int offSet = ClientUI.frameMode == Client.ScreenMode.FIXED ? 4 : 0;
                    int y = 329 - message * 13;
                    if (ClientUI.frameMode != Client.ScreenMode.FIXED) {
                        y = ClientUI.frameHeight - 170 - message * 13;
                    }
                    y -= client.getChatBoxHeight() - ChatBox.chatScrollHeight;
                    if (MouseHandler.x > offSet + 4 && MouseHandler.y - offSet > y - (13 * msgs) && MouseHandler.y - offSet <= y) {
                        boolean hasRights = rights > 0 && rights < 15;
                        int prefixWidth = client.regularText.getTextWidth("From " + chatName + ": ") + (hasRights ? 14 : 0);
                        int i1 = prefixWidth + Math.max(client.regularText.getTextWidth(splitMsg[0]), msgs > 1 ? client.regularText.getTextWidth(splitMsg[1]) : 0);
                        if (MouseHandler.x < offSet + 4 + i1) {
                            if (!client.isFriendOrSelf(chatName)) {
                                addMenuAction(client, MenuOpcodes.SPLIT_CHAT_ADD_IGNORE, "Add ignore @whi@" + chatName);
                                addMenuAction(client, MenuOpcodes.SPLIT_CHAT_ADD_FRIEND, "Add friend @whi@" + chatName);
                            } else
                                addMenuAction(client, MenuOpcodes.SPLIT_CHAT_MESSAGE, "Message @whi@" + chatName);
                        }
                    }
                    message += msgs;
                    if (message >= 5)
                        return;
                }
                if (ChatBox.showLoginSplitPrivateChatMsg(ChatBox.privateChatMode, client.splitPrivateChat, chatType)) {
                    if (++message >= 5) {
                        return;
                    }
                }
                if (ChatBox.showToSplitPrivateChatMsg(ChatBox.privateChatMode, client.splitPrivateChat, chatType)) {
                    message += ChatBox.drawChatMessage(client, rights, "To", null, chatName + ":", msg, 0, 0, 0, 0, 0, false, true).length;
                    if (message >= 5) {
                        return;
                    }
                }
            }

    }

    public static void buildGameMenu(Client client) {

        if (client.itemSelected == 0 && client.spellSelected == 0)
            addMenuAction(client, MenuOpcodes.TELEPORT_OR_WALK_HERE, client.shiftTeleport() ? "Teleport here" : "Walk here", 0, MouseHandler.x,  MouseHandler.y);

        long previous = -1L;
        for (int objectIndex = 0; objectIndex < Model.objectCount; objectIndex++) {

            long tag = OSObjectDefinition.USE_OSRS
                    ? ViewportMouse.ViewportMouse_entityTags[objectIndex]
                    : Model.objectUIDs[objectIndex];
            int x = DynamicObject.get_object_x(tag);
            int y = DynamicObject.get_object_y(tag);
            int opcode = DynamicObject.get_object_opcode(tag);
            int entity_id = DynamicObject.get_object_key(tag);
            int entity_index = DynamicObject.get_object_index(tag);

            if (tag == previous)
                continue;

            previous = tag;

//            System.out.println("tag = "+ tag+" \t x = "+x+"\t y = "+y+" \t opcode = "+opcode+" \t entity_id = "+entity_id+" \t entity_index = "+entity_index);

            if (opcode == 2 && client.scene.getObjectFlags(Client.plane, x, y, tag) >= 0) {
                String objectName;
                String[] objectActions;
                int objectType;
                ObjectDefinition def = ObjectDefinition.lookup(entity_id);
                if (def.transforms != null)
                    def = def.transform();
                if (def == null)
                    continue;
                objectName = def.name;
                objectActions = def.actions;
                objectType = def.type;
                if (client.itemSelected == 1) {
                    addMenuAction(client, MenuOpcodes.USE_ITEM_ON_OBJECT, x, y, tag, "Use " + client.selectedItemName + " with @cya@" + objectName);
                } else if (client.spellSelected == 1) {
                    addUseSpellMenuOption(client, tag, x, y, objectName);
                } else {

                    if (objectActions != null)
                        addObjectMenuActions(client, tag, x, y, objectName, objectActions);

                    if (client.isHighStaff())
                        addMenuAction(client, MenuOpcodes.EDIT_OBJECT, (x + client.baseX), (y + client.baseY), tag, "Toggle @cya@"
                                + objectName + " @gre@(@whi@"
                                + entity_id + "@gre@) (@whi@"
                                + (x + client.baseX) + ","
                                + (y + client.baseY) + "@gre@)");
                    else {
                        if ((client.clientRights >= 4 && client.clientRights <= 7)) {
                            client.menuActions[client.menuOptionsCount] = "Examine @cya@"
                                    + objectName + " @gre@(@whi@"
                                    + entity_id + "@gre@) (@whi@"
                                    + (x + client.baseX) + ","
                                    + (y + client.baseY) + "@gre@)";
                        } else {
                            client.menuActions[client.menuOptionsCount] = "Examine @cya@" + objectName;
                        }
                        insertMenuItem(client, MenuOpcodes.EXAMINE_OBJECT, objectType << 32, x, y);
                    }
                }
            }
            if (opcode == 1) {
                int npcIndex = OSObjectDefinition.USE_OSRS ? entity_id : entity_index;
                Npc npc = client.npcs[npcIndex];
                try {
                    if (npc.desc.size == 1 && (npc.x & 0x7f) == 64 && (npc.y & 0x7f) == 64) {
                        for (int j2 = 0; j2 < client.npcCount; j2++) {
                            Npc npc2 = client.npcs[client.npcIndices[j2]];
                            if (npc2 != null && npc2 != npc && npc2.desc.size == 1 && npc2.x == npc.x
                                    && npc2.y == npc.y) {
                                if (npc2.showActions()) {
                                    createNpcMenu(client, npc2.desc, client.npcIndices[j2], x, y);
                                }
                            }
                        }
                        for (int l2 = 0; l2 < client.Players_count; l2++) {
                            Player player = client.players[client.Players_indices[l2]];
                            if (player != null && player.x == npc.x && player.y == npc.y)
                                createPlayerMenu(client, player, client.Players_indices[l2], x, y);
                        }
                    }
                    if (npc.showActions()) {
                        createNpcMenu(client, npc.desc, npcIndex, x, y);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (opcode == 0) {

                Player player = client.players[entity_id];
                if ((player.x & 0x7f) == 64 && (player.y & 0x7f) == 64) {
                    for (int k2 = 0; k2 < client.npcCount; k2++) {
                        Npc npc = client.npcs[client.npcIndices[k2]];
                        if (npc != null  && npc.desc != null && npc.desc.size == 1
                                && npc.x == player.x
                                && npc.y == player.y)
                            createNpcMenu(client, npc.desc, client.npcIndices[k2], x, y);
                    }

                    for (int i3 = 0; i3 < client.Players_count; i3++) {
                        Player other = client.players[client.Players_indices[i3]];
                        if (other != null && other != player
                                && other.x == player.x
                                && other.y == player.y)
                            createPlayerMenu(client, other, client.Players_indices[i3], x, y);
                    }

                }
                createPlayerMenu(client, player, entity_id, x, y);
            }
            if (opcode == 3) {
                NodeDeque class19 = client.groundItems[Client.plane][x][y];
                if (class19 != null) {
                    for (Item item = (Item) class19.first(); item != null; item = (Item) class19.next()) {
                        ItemDefinition itemDef = ItemDefinition.lookup(item.ID);
                        if (client.itemSelected == 1) {
                            client.menuActions[client.menuOptionsCount] = "Use " + client.selectedItemName + " with @lre@" + itemDef.name;
                            insertMenuItem(client, MenuOpcodes.USE_ITEM__ON__ITEM_ON_GROUND, item.ID, x, y);
                        } else if (client.spellSelected == 1) {
                            if ((client.spellUsableOn & 1) == 1) {
                                client.menuActions[client.menuOptionsCount] = client.spellTooltip + " @lre@" + itemDef.name;
                                insertMenuItem(client, MenuOpcodes.USE_SPELL_ON_GROUND_ITEM, item.ID, x, y);
                            }
                        } else {
                            for (int j3 = 4; j3 >= 0; j3--)
                                if (itemDef.groundActions != null && itemDef.groundActions[j3] != null) {
                                    client.menuActions[client.menuOptionsCount] = itemDef.groundActions[j3] + " @lre@" + itemDef.name;
                                    if (j3 == 0)
                                        client.menuOpcodes[client.menuOptionsCount] = 652;
                                    if (j3 == 1)
                                        client.menuOpcodes[client.menuOptionsCount] = 567;
                                    if (j3 == 2)
                                        client.menuOpcodes[client.menuOptionsCount] = MenuOpcodes.PICKUP_ITEM_ON_GROUND;
                                    if (j3 == 3)
                                        client.menuOpcodes[client.menuOptionsCount] = 244;
                                    if (j3 == 4)
                                        client.menuOpcodes[client.menuOptionsCount] = 213;
                                    client.menuArguments0[client.menuOptionsCount] = item.ID;
                                    client.menuArguments1[client.menuOptionsCount] = x;
                                    client.menuArguments2[client.menuOptionsCount] = y;
                                    client.menuOptionsCount++;
                                } else if (j3 == 2) {
                                    client.menuActions[client.menuOptionsCount] = "Take @lre@" + itemDef.name;
                                    insertMenuItem(client, MenuOpcodes.PICKUP_ITEM_ON_GROUND, item.ID, x, y);
                                }
                        }
                        if ((client.clientRights >= 4 && client.clientRights <= 7)) {
                            client.menuActions[client.menuOptionsCount] = "Examine @lre@" + itemDef.name + " @gre@ (@whi@" + item.ID
                                    + "@gre@)";

                        } else {
                            client.menuActions[client.menuOptionsCount] = "Examine @lre@" + itemDef.name;
                        }
                        insertMenuItem(client, 1448, item.ID, x, y);
                    }
                }
            }
        }
    }

    private static void addObjectMenuActions(Client client, long tag, int x, int y, String objectName, String[] actions) {

        for (int type = 4; type >= 0; type--) {
            if (actions[type] != null) {
                client.menuActions[client.menuOptionsCount] = actions[type] + " @cya@" + objectName;
                if (type == 0)
                    client.menuOpcodes[client.menuOptionsCount] = 502;
                if (type == 1)
                    client.menuOpcodes[client.menuOptionsCount] = 900;
                if (type == 2)
                    client.menuOpcodes[client.menuOptionsCount] = 113;
                if (type == 3)
                    client.menuOpcodes[client.menuOptionsCount] = 872;
                if (type == 4)
                    client.menuOpcodes[client.menuOptionsCount] = 1062;
                client.menuArguments0[client.menuOptionsCount] = tag;
                client.menuArguments1[client.menuOptionsCount] = x;
                client.menuArguments2[client.menuOptionsCount] = y;
                client.menuOptionsCount++;
            }
        }
    }

    public static void addUseSpellMenuOption(Client client, long tag, int x, int y, String objectName) {
        if ((client.spellUsableOn & 4) == 4) {
            client.menuActions[client.menuOptionsCount] = client.spellTooltip + " @cya@" + objectName;
            client.menuOpcodes[client.menuOptionsCount] = 956;
            client.menuArguments0[client.menuOptionsCount] = tag;
            client.menuArguments1[client.menuOptionsCount] = x;
            client.menuArguments2[client.menuOptionsCount] = y;
            client.menuOptionsCount++;
        }
    }

    public static void createNpcMenu(Client client, NpcDefinition npc, int npcHash, int x, int y) {
        if (ClientCompanion.openInterfaceId == 15244) {
            return;
        }
        if (client.menuOptionsCount >= 400)
            return;
        if (npc.transforms != null)
            npc = npc.transform();
        if (npc == null)
            return;
        if (!npc.clickable)
            return;
        String s = npc.name;

        if (npc.combatLevel != 0)
            s = s + ClientUtil.combatDiffColor(Client.localPlayer.combatLevel, npc.combatLevel) + " (level-" + npc.combatLevel
                    + ")";
        if (client.itemSelected == 1) {
            client.menuActions[client.menuOptionsCount] = "Use " + client.selectedItemName + " with @yel@" + s;
            insertMenuItem(client, MenuOpcodes.USE_ITEM_ON_NPC, npcHash, x, y);
            return;
        }
        if (client.spellSelected == 1) {
            if ((client.spellUsableOn & 2) == 2) {
                client.menuActions[client.menuOptionsCount] = client.spellTooltip + " @yel@" + s;
                insertMenuItem(client, MenuOpcodes.USE_SPELL_ON_NPC, npcHash, x, y);
            }
        } else {
            if (npc.actions != null) {
                for (int l = 4; l >= 0; l--)
                    if (npc.actions[l] != null && !npc.actions[l].equalsIgnoreCase("attack")) {
                        client.menuActions[client.menuOptionsCount] = npc.actions[l] + " @yel@" + s;
                        if (l == 0)
                            client.menuOpcodes[client.menuOptionsCount] = 20;
                        if (l == 1)
                            client.menuOpcodes[client.menuOptionsCount] = 412;
                        if (l == 2)
                            client.menuOpcodes[client.menuOptionsCount] = 225;
                        if (l == 3)
                            client.menuOpcodes[client.menuOptionsCount] = 965;
                        if (l == 4)
                            client.menuOpcodes[client.menuOptionsCount] = 478;
                        client.menuArguments0[client.menuOptionsCount] = npcHash;
                        client.menuArguments1[client.menuOptionsCount] = x;
                        client.menuArguments2[client.menuOptionsCount] = y;
                        client.menuOptionsCount++;
                    }

            }
            if (npc.actions != null) {
                for (int i1 = 4; i1 >= 0; i1--) {
                    if (npc.actions[i1] != null && npc.actions[i1].equalsIgnoreCase("attack")) {

                        char c = '\0';
                        if (Configuration.npcAttackOptionPriority == 0 && npc.combatLevel > Client.localPlayer.combatLevel)
                            c = '\u07D0';
                        else if (Configuration.npcAttackOptionPriority == 1)
                            c = '\u07D0';
                        else if (Configuration.npcAttackOptionPriority == 3)
                            continue;

                        int actionType = -1;

                        if (i1 == 0) actionType = 20 + c;
                        else if (i1 == 1) actionType = 412 + c;
                        else if (i1 == 2) actionType = 225 + c;
                        else if (i1 == 3) actionType = 965 + c;
                        else if (i1 == 4) actionType = 478 + c;

                        insertMenuItem(client, npc.actions[i1] + " @yel@" + s, actionType, npcHash, x, y);
                    }
                }
            }
            final String text = client.isHighStaff() ? "Edit @yel@" + s + " @gre@(@whi@" + npc.id + ")" : (client.clientRights >= 4 && client.clientRights <= 7) ? "Examine @yel@" + s + " @gre@(@whi@" + npc.id + ")" : "Examine @yel@" + s;
            insertMenuItem(client, text, MenuOpcodes.EXAMINE_NPC, npcHash, x, y);
        }
    }

    public static void createPlayerMenu(Client client, Player player, int playerHash, int x, int y) {
        if (ClientCompanion.openInterfaceId == 15244) {
            return;
        }
        if (player == Client.localPlayer || player.isHidden)
            return;
        if (client.menuOptionsCount >= 400)
            return;

        String s = player.getNameWithTitle() + ClientUtil.combatDiffColor(Client.localPlayer.combatLevel, player.combatLevel) + " (level: " + player.combatLevel + ")";

        if (client.itemSelected == 1) {
            client.menuActions[client.menuOptionsCount] = "Use " + client.selectedItemName + " with @whi@" + s;
            insertMenuItem(client, 491, playerHash, x, y);
        } else if (client.spellSelected == 1) {
            if ((client.spellUsableOn & 8) == 8) {
                client.menuActions[client.menuOptionsCount] = client.spellTooltip + " @whi@" + s;
                insertMenuItem(client, 365, playerHash, x, y);
            }
        } else {
            for (int type = ClientCompanion.PLAYER_OPTION_COUNT-1; type >= 0; type--) {
                if (client.playerOptions[type] != null) {
                    client.menuActions[client.menuOptionsCount] = client.playerOptions[type] + " @whi@" + s;

                    char c = '\0';
                    if (client.playerOptions[type].equalsIgnoreCase("attack")) {

                        if (Configuration.playerAttackOptionPriority == 0) {
                            if (player.combatLevel > Client.localPlayer.combatLevel)
                                c = '\u07D0';
                        } else if (Configuration.playerAttackOptionPriority == 1) {
                            c = '\u07D0';
                        } else if (Configuration.playerAttackOptionPriority == 3) {
                            continue;
                        }

                        if (Client.localPlayer.team != 0 && player.team != 0)
                            if (Client.localPlayer.team == player.team) {
                                c = '\u07D0';
                            } else {
                                c = '\0';
                            }

                    } else if (client.playerOptionsHighPriority[type])
                        c = '\u07D0';
                    int actionType = -1;
                    if (type == 0) actionType = 561 + c;
                    else if (type == 1) actionType = 779 + c;
                    else if (type == 2) actionType = 27 + c;
                    else if (type == 3) actionType = 577 + c;
                    else if (type == 4) actionType = 729 + c;
//                    System.out.println("playerOptions = "+ Arrays.toString(playerOptions) +" type = "+type+", actionType = "+actionType+", c = "+(int)c);
                    insertMenuItem(client, actionType, playerHash, x, y);
                }
            }
        }
        if(client.isStaff()){
            insertMenuItem(client, "@red@Punish @whi@" + s, MenuOpcodes.PUNISH_PLAYER, playerHash, x, y);
        }
        for (int row = 0; row < client.menuOptionsCount; row++) {
            if (client.menuOpcodes[row] == 519) {
                client.menuActions[row] = (client.shiftTeleport() ? "Teleport here" : "Walk here") + " @whi@" + s;
                return;
            }
        }
    }


    public static void buildChatAreaMenu(Client client, int j) {
        if (client.inputDialogState == 3) {
            return;
        }

        if (Emojis.isHoveringMenu()) {
            return;
        }

        char c = '\0';
        if (ClientUI.frameMode != Client.ScreenMode.FIXED && ChatBox.changeChatArea)
            c = '\u07D0';

        int l = 0;
        for (int i1 = 0; i1 < 500; i1++) {
            if (client.chatMessages[i1] == null)
                continue;
            int chatType = client.chatTypes[i1];
            int k1 = (client.getChatBoxHeight() - (l * 14)) + ChatBox.chatBoxScrollPosition + 3 - client.resizeChatOffset();
            String chatName = client.chatNames[i1];

            byte rights = 0;

            if (chatName == null) {
                l++;
                continue;
            }

            if (ChatBox.showGameChatMsg(client.chatTypeView, chatType)) {
                l += getChatMessageLines(client, client.chatMessages[i1]);
            } else if (ChatBox.showPublicChatMsg(client, chatType, chatName)) {
                int msgs = getChatMessageLines(client, rights, null, chatName + ":", client.chatMessages[i1]);
                if (j > k1 - (14 * msgs) && j <= k1 && !chatName.equals(Client.localPlayer.name)) {
                    if (!client.isFriendOrSelf(chatName)) {
                        client.menuActions[client.menuOptionsCount] = "Add ignore @whi@" + chatName;
                        client.menuOpcodes[client.menuOptionsCount] = 42 + c;
                        client.menuOptionsCount++;
                        client.menuActions[client.menuOptionsCount] = "Add friend @whi@" + chatName;
                        client.menuOpcodes[client.menuOptionsCount] = 337 + c;
                        client.menuOptionsCount++;
                    } else {
                        client.menuActions[client.menuOptionsCount] = "Message @whi@" + chatName;
                        client.menuOpcodes[client.menuOptionsCount] = 2639 + c;
                        client.menuOptionsCount++;
                    }
                }
                l += msgs;
            } else if (ChatBox.showFromPrivateChatMsg(client, chatType, chatName)) {
                int msgs = getChatMessageLines(client, rights, "From", chatName + ":", client.chatMessages[i1]);
                if (j > k1 - (14 * msgs) && j <= k1) {
                    if (!client.isFriendOrSelf(chatName)) {
                        client.menuActions[client.menuOptionsCount] = "Add ignore @whi@" + chatName;
                        client.menuOpcodes[client.menuOptionsCount] = 42 + c;
                        client.menuOptionsCount++;
                        client.menuActions[client.menuOptionsCount] = "Add friend @whi@" + chatName;
                        client.menuOpcodes[client.menuOptionsCount] = 337 + c;
                        client.menuOptionsCount++;
                    } else {
                        client.menuActions[client.menuOptionsCount] = "Message @whi@" + chatName;
                        client.menuOpcodes[client.menuOptionsCount] = 2639 + c;
                        client.menuOptionsCount++;
                    }
                }
                l += msgs;
            } else if (ChatBox.showTradeChatMsg(client, chatType, chatName)) {
                if (j > k1 - 14 && j <= k1) {
                    client.menuActions[client.menuOptionsCount] = "Accept trade @whi@" + chatName;
                    client.menuOpcodes[client.menuOptionsCount] = 484 + c;
                    client.menuOptionsCount++;
                }
                l++;
            } else if (ChatBox.showLoginPrivateChatMsg(client.chatTypeView, ChatBox.privateChatMode, client.splitPrivateChat, chatType)) {
                l++;
            } else if (ChatBox.showToPrivateChatMsg(client.chatTypeView, ChatBox.privateChatMode, client.splitPrivateChat, chatType)) {
                l += getChatMessageLines(client, rights, "To", chatName + ":", client.chatMessages[i1]);
            } else if (ChatBox.showRequestsChatMsg(client, chatType, chatName)) {
                if (j > k1 - 14 && j <= k1) {
                    client.menuActions[client.menuOptionsCount] = "Accept challenge @whi@" + chatName;
                    client.menuOpcodes[client.menuOptionsCount] = 6 + c;
                    client.menuOptionsCount++;
                }
                l++;
                if (chatType == 11 && (ChatBox.clanChatMode == 0)) {
                    if (client.chatTypeView == 11) {
                        l++;
                    }
                }
            } else if (ChatBox.showClanChatMsg(client, chatType, chatName)) {
                int msgs = getChatMessageLines(client, client.chatMessages[i1]);
                if (j > k1 - (14 * msgs) && j <= k1 && !chatName.equals(Client.localPlayer.name)) {
                    if (!client.isFriendOrSelf(chatName)) {
                        client.menuActions[client.menuOptionsCount] = "Add ignore @whi@" + chatName;
                        client.menuOpcodes[client.menuOptionsCount] = 42 + c;
                        client.menuOptionsCount++;
                        client.menuActions[client.menuOptionsCount] = "Add friend @whi@" + chatName;
                        client.menuOpcodes[client.menuOptionsCount] = 337 + c;
                        client.menuOptionsCount++;
                    } else {
                        client.menuActions[client.menuOptionsCount] = "Message @whi@" + chatName;
                        client.menuOpcodes[client.menuOptionsCount] = 2639 + c;
                        client.menuOptionsCount++;
                    }
                }
                l += msgs;
            } else if (ChatBox.showYellChatMsg(client.chatTypeView, ChatBox.yellMode, chatType)) {
                int msgs = getChatMessageLines(client, client.chatMessages[i1]);
                if (j > k1 - (14 * msgs) && j <= k1 && !chatName.equals(Client.localPlayer.name)) {
                    if (!client.isFriendOrSelf(chatName)) {
                        client.menuActions[client.menuOptionsCount] = "Add ignore @whi@" + chatName;
                        client.menuOpcodes[client.menuOptionsCount] = 42 + c;
                        client.menuOptionsCount++;
                        client.menuActions[client.menuOptionsCount] = "Add friend @whi@" + chatName;
                        client.menuOpcodes[client.menuOptionsCount] = 337 + c;
                        client.menuOptionsCount++;
                    } else {
                        client.menuActions[client.menuOptionsCount] = "Message @whi@" + chatName;
                        client.menuOpcodes[client.menuOptionsCount] = 2639 + c;
                        client.menuOptionsCount++;
                    }
                }
                l += msgs;
            }
        }
    }

    public static int getChatMessageLines(Client client, String msg) {
        return ChatBox.drawChatMessage(client, (byte) 0, null, null, null, msg, 0, 0, 0, 0, 0, false, false).length;
    }

    public static int getChatMessageLines(Client client, byte rights, String preposition, String name, String msg) {
        return ChatBox.drawChatMessage(client, rights, preposition, null, name, msg, 0, 0, 0, 0, 0, false, false).length;
    }
}
