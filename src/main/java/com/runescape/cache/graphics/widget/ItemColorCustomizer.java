package com.runescape.cache.graphics.widget;

import com.runescape.Client;
import com.runescape.cache.ModelData;
import com.runescape.cache.def.ItemDefinition;
import com.runescape.cache.graphics.GameFont;
import com.runescape.cache.graphics.sprite.AutomaticSprite;
import com.runescape.entity.Player;
import com.runescape.entity.model.Model;
import com.runescape.io.packets.outgoing.impl.RecolorItem;
import com.grinder.client.ClientCompanion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ItemColorCustomizer extends Widget {

    public static final int INTERFACE_ID = 51300;
    public static final int TITLE_TEXT_ID = INTERFACE_ID + 3;
    public static final int COLOR_PANEL_TITLE_TEXT_ID = INTERFACE_ID + 6;
    public static final int PREVIEW_COMPONENT_ID = INTERFACE_ID + 10;
    public static final int SAVE_COLOR_BUTTON_ID = INTERFACE_ID + 12;
    public static final int DISCARD_COLOR_BUTTON_ID = INTERFACE_ID + 13;
    public static final int CHARACTER_MODEL_ID = INTERFACE_ID + 16;
    public static final int ITEM_MODEL_ID = INTERFACE_ID + 17;

    public static final int ONE_COLOR_PICKER_LAYER_ID = INTERFACE_ID + 23;
    public static final int TWO_COLOR_PICKERS_LAYER_ID = INTERFACE_ID + 30;
    public static final int THREE_COLOR_PICKERS_LAYER_ID = INTERFACE_ID + 40;
    public static final int FOUR_COLOR_PICKERS_LAYER_ID = INTERFACE_ID + 52;
    public static final int FIVE_COLOR_PICKERS_LAYER_ID = INTERFACE_ID + 66;
    public static final int SIX_COLOR_PICKERS_LAYER_ID = INTERFACE_ID + 82;

    public enum ColorfulItem {
        HWEEN_MASK("h'ween mask", 15196, 1053, new int[] { 926 }, true),
        PARTYHAT("partyhat", 15193, 1044, new int[] { 926 }, true),
        SANTA_HAT("santa hat", 15194, 1050, new int[] { 933 }, true),
        SCARF("gnome scarf", 15300, 9470, new int[] { 119, 103, 127 },true),
        MAX_CAPE("max cape", 15195, 13280, new int[] { 253, 254, 255, 5683, 784, 673, 675, 815, 5708, 668, 4300, 5458, 4316, 902, 5714, 945, 5706, 4820, 972, 5437, 522 },
                new String[] { "Primary", "Secondary", "Tertiary", "Trim", "Rubies" }, true),
        MAX_HOOD("max hood", 15271, 13281, new int[] { 784, 945, 914, 5458, 675, 4820, 972, 685, 815 },
                new String[0], true),
        ANGELIC_CAPE("angelic cape", 15901, 15900, new int[] { 10320, 32450, 10338, 52, 117, 119, 42, 126, 1087 },
                new String[] { "Cape color", "Cape trim", "Dollar sign", "Inner cape", "Wings color" }, true);
        public Integer[] getColorsFor(Player player) {
            return player.getColorfulItemMap().get(this);
        }
        public void updateColorsFor(Player player, Integer[] colors) {
            player.getColorfulItemMap().put(this, colors);
        }

        private int itemId, copyId;
        private int[] colorsToReplace; // colors to find
        private String[] colorNames;
        private String name;
        private boolean useItemModel; // whether to display a character model (false) or item model (true) in the interface preview
        public int getItemId() {
            return itemId;
        }

        public int getCopyId() {
            return copyId;
        }

        public int[] getColorsToReplace() {
            return colorsToReplace;
        }

        public String[] getColorNames() {
            return colorNames;
        }

        public String getName() {
            return name;
        }

        public boolean useItemModel() { return useItemModel; }

        ColorfulItem(String name, int itemId, int copyId, int[] colorsToReplace, boolean useItemModel) {
            this.name = name;
            this.itemId = itemId;
            this.copyId = copyId;
            this.colorsToReplace = colorsToReplace;
            this.colorNames = new String[] { "" };
            this.useItemModel = useItemModel;
        }

        ColorfulItem(String name, int itemId, int copyId, int[] colorsToReplace, String[] colorNames, boolean useItemModel) {
            this.name = name;
            this.itemId = itemId;
            this.copyId = copyId;
            this.colorsToReplace = colorsToReplace;
            this.colorNames = colorNames;
            this.useItemModel = useItemModel;
        }

        public static ColorfulItem[] VALUES = values();

        private static Map<Integer, ColorfulItem> itemIds = new HashMap<>();

        public static Map<Integer, ColorfulItem> getItemIds() {
            return itemIds;
        }

        public static ColorfulItem forId(int itemId) {
            return getItemIds().get(itemId);
        }

        private static Map<String, ColorfulItem> names = new HashMap<>();

        public static Map<String, ColorfulItem> getNames() {
            return names;
        }

        public static ColorfulItem forName(String name) {
            return getNames().get(name);
        }

        private static Map<Integer, ColorfulItem> copyItemIds = new HashMap<>();

        public static Map<Integer, ColorfulItem> getCopyItemIds() {
            return copyItemIds;
        }

        public static ColorfulItem forCopyId(int itemId) {
            return getCopyItemIds().get(itemId);
        }

        /**
         * A map that fills all items' color values to white
         */
        public static Map<ColorfulItem, Integer[]> getDefaultColorfulItemMap() {
            Map<ColorfulItem, Integer[]> map = new HashMap<>();
            Arrays.stream(VALUES).forEach(colorfulItem -> {
                Integer[] colors = new Integer[colorfulItem.getColorNames().length];
                Arrays.fill(colors, 0xffffff);
                map.put(colorfulItem, colors);
            });
            return map;
        }

        static {
            Arrays.stream(VALUES).forEach(colorfulItem -> {
                itemIds.put(colorfulItem.getItemId(), colorfulItem);
                names.put(colorfulItem.getName(), colorfulItem);
                copyItemIds.put(colorfulItem.getCopyId(), colorfulItem);
            });
        }
    }

    public static Integer[] getPlayerColors(Player player, ColorfulItem colorfulItem) {
        return getPlayerColors(player, colorfulItem, false);
    }

    /**
     * Gets the specified player's specified item's color array
     * @param player
     * @param colorfulItem
     * @param flag  Used for max cape changing multiple colors using a single color
     * @return
     */
    public static Integer[] getPlayerColors(Player player, ColorfulItem colorfulItem, boolean flag) {
        Integer[] colors = colorfulItem.getColorsFor(player).clone();

        if (colorfulItem == ColorfulItem.MAX_CAPE && flag) {
            colors = new Integer[colorfulItem.getColorsToReplace().length];
            for (int i = 0; i < colors.length; i++) {
                colors[i] = ColorPicker.hslToRGB(colorfulItem.getColorsToReplace()[i]);
            }
            Integer[] maxColors = player.getColorfulItemMap().get(ColorfulItem.MAX_CAPE);
            //colors[0] = maxColors[0]; // ???
            //colors[1] = maxColors[0]; // ???
            //colors[2] = maxColors[0]; // ???
            colors[3] = maxColors[3]; // bottom trim/accent trim
            colors[4] = maxColors[2]; // dark outer stripes/dark underlay strips
            colors[5] = maxColors[2]; // bottom dark accents
            colors[6] = maxColors[1]; // secondary color
            colors[7] = maxColors[0]; // primary color
            colors[8] = maxColors[3]; // lower trim/rubies trim
            colors[9] = maxColors[0]; // inside primary color
            //colors[10] = maxColors[0]; // ???
            //colors[11] = maxColors[0]; // collar
            //colors[12] = maxColors[0]; // outer collar
            //colors[13] = maxColors[0]; // dark outer stripes very small near shoulders, don't recolor
            //colors[14] = maxColors[0]; // shine of rubies?
            colors[15] = maxColors[4]; // bottom of rubies
            colors[16] = maxColors[3]; // rubies trim
            colors[17] = maxColors[3]; // rubies trim
            colors[18] = maxColors[4]; // top of rubies
            //colors[19] = maxColors[0]; // top of collar
            //colors[20] = maxColors[0]; // black accent, don't recolor
        } else if (colorfulItem == ColorfulItem.MAX_HOOD && flag) {
            colors = new Integer[colorfulItem.getColorsToReplace().length];
            for (int i = 0; i < colors.length; i++) {
                colors[i] = ColorPicker.hslToRGB(colorfulItem.getColorsToReplace()[i]);
            }
            Integer[] maxColors = player.getColorfulItemMap().get(ColorfulItem.MAX_CAPE);
            colors[0] = maxColors[2]; // parts of back
            colors[1] = maxColors[4]; // ruby
            colors[2] = maxColors[3]; // more trim on front
            colors[3] = maxColors[3]; // very small trim around ruby
            colors[4] = maxColors[1]; // accent color stripes
            colors[5] = maxColors[3]; // very small trim around ruby
            colors[6] = maxColors[4]; // ruby
            colors[7] = maxColors[1]; // back top
            colors[8] = maxColors[0]; // primary color
        } else if (colorfulItem == ColorfulItem.SCARF && flag) {
            colors = new Integer[colorfulItem.getColorsToReplace().length];
            for (int i = 0; i < colors.length; i++) {
                colors[i] = ColorPicker.hslToRGB(colorfulItem.getColorsToReplace()[i]);
            }
            Integer[] maxColors = player.getColorfulItemMap().get(ColorfulItem.SCARF);
            colors[0] = maxColors[0]; // parts of back
            colors[1] = maxColors[0]; // parts of back
            colors[2] = maxColors[0]; // parts of back
        } else if (colorfulItem == ColorfulItem.ANGELIC_CAPE && flag) {
            colors = new Integer[colorfulItem.getColorsToReplace().length];
            for (int i = 0; i < colors.length; i++) {
                colors[i] = ColorPicker.hslToRGB(colorfulItem.getColorsToReplace()[i]);
            }
            Integer[] maxColors = player.getColorfulItemMap().get(ColorfulItem.ANGELIC_CAPE);
            colors[0] = maxColors[0]; // cape color
            colors[1] = maxColors[1]; // cape trim
            colors[2] = maxColors[1]; // mini head
            colors[3] = maxColors[1]; // neck trim
            colors[4] = maxColors[2]; // dollar sign
            colors[5] = maxColors[3]; // inside cape
            colors[6] = maxColors[1]; // neck trim
            colors[7] = maxColors[4]; // wings color
            colors[8] = maxColors[3]; // neck color
        }
        return colors;
    }

    public static void setPreviewColor(int color, int index) {
        ColorfulItem colorfulItem = getColorfulItemForTitleText();
        Player dummy = ClientCompanion.dummyPlayer;
        if (colorfulItem != null) {
            colorfulItem.getColorsFor(dummy)[index] = color;
        }
        dummy.colorNeedsUpdate = true;
        dummy.PlayerAppearance_cachedModels.clear();
    }

    public static void onOpenInterface() {
        ColorfulItem colorfulItem = getColorfulItemForTitleText();

        if (colorfulItem == null)
            return;

        // Display correct color picker layer
        int layerId = getColorPickerLayerId(colorfulItem);
        Widget.interfaceCache[INTERFACE_ID].children[6] = layerId;
        // Display correct color picker names
        if (colorfulItem.getColorNames().length > 1)
            for (int i = 0; i < colorfulItem.getColorNames().length; i++)
                Widget.interfaceCache[layerId + ((i + 1) * 2) - 1].defaultText = colorfulItem.getColorNames()[i];

        // Display correct preview component
        boolean itemModel = colorfulItem.useItemModel();
        if (itemModel) {
            Widget.interfaceCache[ITEM_MODEL_ID].defaultMedia = colorfulItem.getItemId();
        }
        Widget.interfaceCache[INTERFACE_ID].children[10] = itemModel ? ITEM_MODEL_ID : CHARACTER_MODEL_ID;

        updateDummyPlayer();
    }

    public static int getColorPickerLayerId(ColorfulItem colorfulItem) {
        int id;
        int colorPickersAmt = colorfulItem.getColorNames().length;
        switch (colorPickersAmt) {
            // interfaces for 2-4 pickers have not been made yet
            /*case 2:
                id = TWO_COLOR_PICKERS_LAYER_ID;
                break;
            case 3:
                id = THREE_COLOR_PICKERS_LAYER_ID;
                break;
            case 4:
                id = FOUR_COLOR_PICKERS_LAYER_ID;
                break;*/
            case 5:
                id = FIVE_COLOR_PICKERS_LAYER_ID;
                break;
            case 6:
                id = SIX_COLOR_PICKERS_LAYER_ID;
                break;
            default:
                id = ONE_COLOR_PICKER_LAYER_ID;
                break;

        }
        return id;
    }

    public static void updateDummyPlayer() {
        Player local = Client.localPlayer;
        DummyPlayerModel.setPlayerModel(local.equipment.clone(), local.idleSequence);
    }

    public static void editColorfulItemActions(ColorfulItem colorfulItem, ItemDefinition itemDef) {
        itemDef.copy(ItemDefinition.lookup(colorfulItem.getCopyId()));
        itemDef.name = "Colorful " + colorfulItem.getName();
        if (colorfulItem != ColorfulItem.MAX_HOOD)
            itemDef.actions[3] = "Customize";
        editItemDefColors(colorfulItem, itemDef, Client.localPlayer);
    }

    public static Model editItemDefColors(ColorfulItem colorfulItem, ItemDefinition itemDef, Player playerToUseColorsFrom) {
        Integer[] colors = getPlayerColors(playerToUseColorsFrom, colorfulItem, true);
        int[] newColors = new int[colors.length];
        int[] replaceColor = colorfulItem.getColorsToReplace();
        for (int i = 0; i < colors.length; i++)
            newColors[i] = ColorPicker.rgbToHSL(colors[i]);

        if (Client.localPlayer == playerToUseColorsFrom) {
            itemDef.original_model_colors = replaceColor;
            itemDef.modified_model_colors = newColors;
            ItemDefinition.sprites.clear();
            ItemDefinition.ItemDefinition_cachedModels.clear();
        }
        return null;
    }

    public static ColorfulItem getColorfulItemForTitleText() {
        String title = Widget.interfaceCache[ItemColorCustomizer.TITLE_TEXT_ID].defaultText;
        String titleName = title.substring(0, title.indexOf(" Color Customizer"));
        return ColorfulItem.forName(titleName.toLowerCase());
    }

    public static ModelData getColorfulItemData(ModelData data, Player player, int itemId, int gender) {
        ColorfulItem colorfulItem = ColorfulItem.forId(itemId);
        if (colorfulItem == null) {
            return data;
        }
        Integer[] colors = getPlayerColors(player, colorfulItem, true);
        int[] replaceColor = colorfulItem.getColorsToReplace();
        int[] newColors = new int[colors.length];
        for (int i = 0; i < colors.length; i++)
            newColors[i] = ColorPicker.rgbToHSL(colors[i]);
        for (int idx = 0; idx < replaceColor.length; idx++) {
            data.recolor((short) replaceColor[idx], (short) newColors[idx]);
        }
        return data;
    }

    public static void saveColor() {
        ColorfulItem colorfulItem = getColorfulItemForTitleText();
        if (colorfulItem != null) {
            int[] colors = new int[colorfulItem.getColorNames().length];
            for (int i = 0; i < colors.length; i++) {
                int boxId = ((i + 1) * 2) + getColorPickerLayerId(colorfulItem);
                colors[i] = Widget.interfaceCache[boxId].textColor;
            }
            Client.instance.sendPacket(new RecolorItem(colorfulItem.getItemId(), colors).create());
            ItemDefinition.ItemDefinition_cachedModels.clear();
        }
    }

    public static void discardColor() {
        ColorfulItem colorfulItem = getColorfulItemForTitleText();
        Integer[] colors =  getPlayerColors(Client.localPlayer, colorfulItem);
        if (colorfulItem != null) {
            updateDummyPlayer();
            for (int i = 0; i < colors.length; i++) {
                int boxId = ((i + 1) * 2) + getColorPickerLayerId(colorfulItem);
                Widget.interfaceCache[boxId].textColor = colors[i];
            }
        }
        Client.instance.sendMessage("Any unsaved changes made have been discarded.");
    }

    public static void widget(GameFont[] tda) {
        int id = INTERFACE_ID;

        Widget w = addInterface(id++);

        w.totalChildren(15);
        int child = 0;

        int width = 353;
        int height = 304;
        int x = (512 - width) / 2;
        int y = (334 - height) / 2;

        addBackground(id, width, height, AutomaticSprite.BACKGROUND_BIG_BROWN);
        w.child(child++, id++, x, y);

        addCloseButton(id, false);
        w.child(child++, id++, x + width - 26, y + 3);

        addText(id, "Item Color Customizer", tda, 2, 0xffb000, true);
        w.child(child++, id++, 256, y + 4);

        int xInset = 7;
        int yInset = 22;
        int panelSpacing = 10;

        int colorWidth = 156;
        int colorHeight = 177;

        int previewWidth = 155;
        int previewHeight = 255;

        /*
         * Color panel
         */

        int colorX = xInset + panelSpacing;
        int colorY = yInset + panelSpacing;

        addRectangle(id, colorWidth - 2, colorHeight - 2, 0x726451, false);
        w.child(child++, id++, x + colorX + 1, y + colorY + 1);
        addRectangle(id, colorWidth, colorHeight, 0x2e2b23, false);
        w.child(child++, id++, x + colorX, y + colorY);

        addText(id, "Color", tda, 2, 0xffb000, true);
        w.child(child++, id++, x + colorX + (colorWidth / 2), y + colorY + 10);

        int colorBoxWidth = 132;
        int colorBoxHeight = 132;
        // Color picker layer placeholder
        Widget colorPlaceholder = addInterface(id);
        w.child(child++, id++, x + colorX + (colorWidth / 2) - (colorBoxWidth / 2), y + colorY + 33);

        /*
         * Preview panel
         */

        int previewX = xInset + panelSpacing + colorWidth + panelSpacing;
        int previewY = yInset + panelSpacing;

        addRectangle(id, previewWidth - 2, previewHeight - 2, 0x726451, false);
        w.child(child++, id++, x + previewX + 1, y + previewY + 1);
        addRectangle(id, previewWidth, previewHeight, 0x2e2b23, false);
        w.child(child++, id++, x + previewX, y + previewY);

        addText(id, "Preview", tda, 2, 0xffb000, true);
        w.child(child++, id++, x + previewX + (previewWidth / 2), y + previewY + 10);

        // Preview component placeholder
        Widget previewPlaceholder = addInterface(id);
        w.child(child++, id++, x + previewX + (previewWidth / 2) - 65, y + previewY + 140);

        /*
         * Buttons
         */

        int buttonWidth = colorWidth;
        int buttonHeight = 29;
        int buttonSpacing = panelSpacing;
        int buttonX = colorX;

        int buttonY = colorY + colorHeight + panelSpacing;

        addHoverButton(id, buttonWidth, buttonHeight, AutomaticSprite.BUTTON_BROWN, AutomaticSprite.BUTTON_BROWN_HOVER,"Save Color");
        w.child(child++, id++, x + buttonX, y + buttonY);
        addHoverButton(id, buttonWidth, buttonHeight, AutomaticSprite.BUTTON_BROWN, AutomaticSprite.BUTTON_BROWN_HOVER,"Discard Color");
        w.child(child++, id++, x + buttonX, y + buttonY + buttonHeight + buttonSpacing);

        addText(id, "Save Color", tda, 0, 0xffb000, true);
        w.child(child++, id++, x + buttonX + (buttonWidth / 2), y + buttonY + 9);
        addText(id, "Discard Color", tda, 0, 0xffb000, true);
        w.child(child++, id++, x + buttonX + (buttonWidth / 2), y + buttonY + buttonHeight + buttonSpacing + 9);

        /*
         * Preview components
         */

        // Character model
        addChar(id);
        interfaceCache[id].contentType = 331;
        interfaceCache[id++].modelZoom = 610;

        // Item model
        addItemModel(id, 13280, 0, 0, 695);
        interfaceCache[id].defaultMediaType = 6;
        interfaceCache[id].horizontalOffset += 67;
        interfaceCache[id].verticalOffset += 15;
        interfaceCache[id++].modelYAngle = 0;

        id += 5; // breathing room

        /*
         * Color picker layers
         */

        // 1 color picker
        Widget layer1 = addInterface(id++);
        layer1.totalChildren(1);
        int layer1Child = 0;
        id++; // used to keep pattern of layer + 2 = color box
        addColorBox(id, 0xffffff, colorBoxWidth, colorBoxHeight);
        interfaceCache[id].parent = ONE_COLOR_PICKER_LAYER_ID;
        layer1.child(layer1Child++, id++, 0, 0);

        id += 4; // breathing room

        // 2 color pickers placeholder (not made yet)
        id++; // layer 2
        id+= 2; // text
        id+= 2; // color box

        id += 5; // breathing room

        // 3 color pickers placeholder (not made yet)
        id++; // layer 3
        id+= 3; // text
        id+= 3; // color box

        id += 5; // breathing room

        // 4 color pickers placeholder (not made yet)
        id++; // layer 4
        id+= 4; // text
        id+= 4; // color box

        id += 5; // breathing room

        // 5 color pickers
        Widget layer5 = addInterface(id++);
        layer5.totalChildren(10);
        int layer5Child = 0;

        for (int i = 0; i < 5; i++) {
            int currentY = i * 27 + 1;
            addText(id, "Text " + i, tda, 0, 0xffb000);
            layer5.child(layer5Child++, id++, 0, currentY + 5);

            addColorBox(id, 0xffffff);
            interfaceCache[id].parent = FIVE_COLOR_PICKERS_LAYER_ID;
            layer5.child(layer5Child++, id++, 71, currentY);
        }

        id += 5; // breathing room

        // 6 color pickers
        Widget layer6 = addInterface(id++);
        layer6.totalChildren(12);
        int layer6Child = 0;

        for (int i = 0; i < 6; i++) {
            int currentY = i * 22 + 1;
            addText(id, "Text " + i, tda, 0, 0xffb000);
            layer6.child(layer6Child++, id++, 0, currentY + 5);

            addColorBox(id, 0xffffff);
            interfaceCache[id].parent = SIX_COLOR_PICKERS_LAYER_ID;
            layer6.child(layer6Child++, id++, 71, currentY);
        }

        id += 5; // breathing room

    }

}
