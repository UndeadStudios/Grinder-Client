package com.runescape.cache.graphics.widget;

import com.runescape.Client;
import com.runescape.cache.graphics.GameFont;
import com.runescape.cache.graphics.sprite.AutomaticSprite;
import com.runescape.draw.Rasterizer3D;
import com.runescape.util.MiscUtils;
import com.grinder.client.ClientCompanion;

import java.awt.*;

public class ColorPicker extends Widget {

    public static final int INTERFACE_ID = 51150;
    public static final int TITLE_ID = INTERFACE_ID + 3;
    public static final int COLOR_PICKER_ID = INTERFACE_ID + 4;
    public static final int OK_BUTTON_ID = INTERFACE_ID + 5;
    public static final int NEW_COLOR_ID = INTERFACE_ID + 12;
    public static final int CURRENT_COLOR_ID = INTERFACE_ID + 13;
    public static final int INPUT_ID = INTERFACE_ID + 15;

    private static int copiedColor;

    private static int selectedColorBox = -1;

    private static PickerType selectedPickerType;

    private static int selectedPickerX = -1, selectedPickerY = -1, selectedPickerMouseX = -1, selectedPickerMouseY = -1;

    public static void widget(GameFont[] tda) {
        int id = INTERFACE_ID;

        Widget w = addInterface(id++);

        w.totalChildren(15);
        int child = 0;

        int width = 395;
        int height = 307;
        int x = (512 - width) / 2;
        int y = (334 - height) / 2;
        int inset = 7;

        addBackground(id, width, height, AutomaticSprite.BACKGROUND_BIG_BROWN);
        w.child(child++, id++, x, y);

        addCloseButton(id, false);
        w.child(child++, id++, x + width - 26, y + 3);

        addText(id, "Color Picker", tda, 2, 0xffb000, true);
        w.child(child++, id++, 256, y + 4);

        addColorPicker(id, 289, 258);
        w.child(child++, id++, x + inset + 10, y + 32);

        addHoverButton(id, 62, 22, AutomaticSprite.BUTTON_BROWN, AutomaticSprite.BUTTON_BROWN_HOVER,"OK");
        w.child(child++, id++, x + inset + 309, y + 32);
        addHoverButton(id, 62, 22, AutomaticSprite.BUTTON_BROWN, AutomaticSprite.BUTTON_BROWN_HOVER,"Cancel");
        interfaceCache[id].atActionType = 3;
        w.child(child++, id++, x + inset + 309, y + 64);

        addText(id, "OK", tda, 0, 0xffb000, true);
        w.child(child++, id++, x + inset + 309 + 31, y + 37);
        addText(id, "Cancel", tda, 0, 0xffb000, true);
        w.child(child++, id++, x + inset + 309 + 31, y + 69);

        addText(id, "new", tda, 0, 0xffb000, true);
        w.child(child++, id++, x + inset + 309 + 31, y + 127);

        addText(id, "current", tda, 0, 0xffb000, true);
        w.child(child++, id++, x + inset + 309 + 31, y + 127 + 87);

        addRectangle(id, 62, 70, 0x252013, false);
        w.child(child++, id++, x + inset + 309, y + 142);

        addRectangle(id, 60, 34, 0x1d7a1a, true);
        w.child(child++, id++, x + inset + 310, y + 143);

        addRectangle(id, 60, 34, 0x1c2f7a, true);
        w.child(child++, id++, x + inset + 310, y + 143 + 34);

        addText(id, "#", tda, 0, 0xFFFFFF);
        w.child(child++, id++, x + inset + 308, y + 274);

        addInput(id, "", "^\\p{XDigit}{0,6}$", 52, 21, 0xffb000);
        interfaceCache[id].autoUpdate = true;
        w.child(child++, id++, x + inset + 319, y + 269);

    }

    public static void addColorPicker(int id, int width, int height) {
        Widget widget = addInterface(id);
        widget.id = id;
        widget.parent = id;
        widget.type = OPTION_COLOR_PICKER;
        widget.atActionType = TYPE_COLOR_PICKER;
        widget.drawsAdvanced = true;
        widget.width = width;
        widget.height = height;
        widget.hsb = new float[] { 0, 0, 0 };
    }

    public static void open(int button) {
        setSelectedColorBox(button);
        String colorBoxName = Widget.interfaceCache[getSelectedColorBox()].actions[0].replace("Pick ", "");
        boolean noName = colorBoxName.equals("color");
        Widget.interfaceCache[TITLE_ID].setDefaultText("Color Picker" + (noName ? "" : " (" + colorBoxName + ")"));
        Color color = Color.decode(String.format("0x%06X", (0xFFFFFF & Widget.interfaceCache[getSelectedColorBox()].textColor)));
        Widget.interfaceCache[COLOR_PICKER_ID].hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        updateInput();
        ClientCompanion.openInterfaceId2 = INTERFACE_ID;
    }

    public static void confirmSelection() {
        switch (Widget.interfaceCache[getSelectedColorBox()].parent) {
            case ItemColorCustomizer.ONE_COLOR_PICKER_LAYER_ID:
            case ItemColorCustomizer.TWO_COLOR_PICKERS_LAYER_ID:
            case ItemColorCustomizer.THREE_COLOR_PICKERS_LAYER_ID:
            case ItemColorCustomizer.FOUR_COLOR_PICKERS_LAYER_ID:
            case ItemColorCustomizer.FIVE_COLOR_PICKERS_LAYER_ID:
            case ItemColorCustomizer.SIX_COLOR_PICKERS_LAYER_ID:
                int parent = Widget.interfaceCache[getSelectedColorBox()].parent;
                int index = (getSelectedColorBox() - parent) / 2 - 1;

                Widget colorPicker = Widget.interfaceCache[COLOR_PICKER_ID];
                int color = Color.HSBtoRGB(colorPicker.hsb[0], colorPicker.hsb[1], colorPicker.hsb[2]);
                ItemColorCustomizer.setPreviewColor(color, index);
                break;
        }

        Widget.interfaceCache[getSelectedColorBox()].textColor = Color.HSBtoRGB(Widget.interfaceCache[COLOR_PICKER_ID].hsb[0], Widget.interfaceCache[COLOR_PICKER_ID].hsb[1], Widget.interfaceCache[COLOR_PICKER_ID].hsb[2]);
        Client.instance.clearTopInterfaces();
    }

    public static void updateInterface() {
        Widget.interfaceCache[NEW_COLOR_ID].textColor = Color.HSBtoRGB(Widget.interfaceCache[COLOR_PICKER_ID].hsb[0], Widget.interfaceCache[COLOR_PICKER_ID].hsb[1], Widget.interfaceCache[COLOR_PICKER_ID].hsb[2]);
        if (getSelectedColorBox() != -1) {
            Widget.interfaceCache[CURRENT_COLOR_ID].textColor = Widget.interfaceCache[getSelectedColorBox()].textColor;
        }
    }

    public static void updateInput() {
        Widget.interfaceCache[INPUT_ID].setDefaultText(String.format("%06X", (0xFFFFFF & Color.HSBtoRGB(Widget.interfaceCache[COLOR_PICKER_ID].hsb[0], Widget.interfaceCache[COLOR_PICKER_ID].hsb[1], Widget.interfaceCache[COLOR_PICKER_ID].hsb[2]))));
    }

    public static void updatePicker() {
        String text = Widget.interfaceCache[INPUT_ID].getDefaultText();
        if (text.length() > 0) {
            // Convert triple digit hex
            if (text.length() == 3) {
                String r = text.substring(0, 1);
                String g = text.substring(1, 2);
                String b = text.substring(2, 3);
                text = r + r + g + g + b + b;
            }
        } else {
            text = "0";
        }
        Color color = Color.decode("0x" + text);
        Widget.interfaceCache[COLOR_PICKER_ID].hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    }

    public static float[] getColorForPosition(int x, int y, float hue, int width, int height) {
        float saturation = 0.0f;
        float brightness = 1.0f;
        float incrementX = (0.01f / width) * 100;
        float incrementY = (0.01f / height) * 100;

        return new float[] { hue, saturation + (incrementX * x), brightness - (incrementY * y) };
    }

    public static int[] getPositionForColor(float[] hsb, int width, int height) {
        float saturation = hsb[1];
        float brightness = hsb[2];

        return new int[] { (int) (width * saturation), (int) (height * (1.0f - brightness)) };
    }

    public static float getHueForPosition(int y, int height) {
        return 1.0f - ((0.01f / height) * 100 * y);
    }

    public static int getPositionForHue(float hue, int height) {
        return (int) ((1.0f - hue) * height);
    }

    public static void handleDragging() {
        PickerType type = getSelectedPickerType();
        int dragX = Client.instance.getMouseX() - getSelectedPickerMouseX();
        int dragY = Client.instance.getMouseY() - getSelectedPickerMouseY();

        int currentX = getSelectedPickerMouseX() - getSelectedPickerX();
        int currentY = getSelectedPickerMouseY() - getSelectedPickerY();

        int newX = MiscUtils.ensureRange(currentX + dragX, 0, Widget.interfaceCache[COLOR_PICKER_ID].width - 33);
        int newY = MiscUtils.ensureRange(currentY + dragY, 0, Widget.interfaceCache[COLOR_PICKER_ID].height - 2);

        if (type == PickerType.SATURATION_BRIGHTNESS) {
            Widget.interfaceCache[COLOR_PICKER_ID].hsb = getColorForPosition(newX, newY, Widget.interfaceCache[COLOR_PICKER_ID].hsb[0], Widget.interfaceCache[COLOR_PICKER_ID].width - 33, Widget.interfaceCache[COLOR_PICKER_ID].height - 2);
        } else if (type == PickerType.HUE) {
            Widget.interfaceCache[COLOR_PICKER_ID].hsb[0] = getHueForPosition(newY, Widget.interfaceCache[COLOR_PICKER_ID].height - 2);
        }
        updateInput();
    }

    public static void setSelected(PickerType type, int x, int y, int mouseX, int mouseY) {
        setSelectedPickerType(type);
        setSelectedPickerX(x);
        setSelectedPickerY(y);
        setSelectedPickerMouseX(mouseX);
        setSelectedPickerMouseY(mouseY);
    }

    public static int hsbToHSL(float[] hsb) {
        int H6bit = (int) (hsb[0] * 63);
        int S3bit = (int) (hsb[1] * 7);
        int L7bit = (int) (hsb[2] * 127);
        return (H6bit << 10) + (S3bit << 7) + (L7bit);
    }

    public static int hslToRGB(int hsl) {
        int color = Rasterizer3D.hslToRgb[hsl];
        double r = (color >> 16 & 0xff);
        double g = (color >> 8 & 0xff);
        double b = (color & 0xff);
        Color f = new Color((int) r, (int) g, (int) b);
        return f.getRGB();
    }

    public static int rgbToHSL(int color) {
        Color col = new Color(color);
        float[] hsb = Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);
        int H6bit = (int) (hsb[0] * 63);
        int S3bit = (int) (hsb[1] * 7);
        int L7bit = (int) (hsb[2] * 127);
        return (H6bit << 10) + (S3bit << 7) + (L7bit);
    }

    public static int getCopiedColor() {
        return copiedColor;
    }

    public static void setCopiedColor(int color) {
        copiedColor = color;
    }

    public static int getSelectedColorBox() {
        return selectedColorBox;
    }

    public static void setSelectedColorBox(int selected) {
        selectedColorBox = selected;
    }

    public static PickerType getSelectedPickerType() {
        return selectedPickerType;
    }

    public static void setSelectedPickerType(PickerType type) {
        selectedPickerType = type;
    }

    public static int getSelectedPickerX() {
        return selectedPickerX;
    }

    public static void setSelectedPickerX(int selected) {
        selectedPickerX = selected;
    }

    public static int getSelectedPickerY() {
        return selectedPickerY;
    }

    public static void setSelectedPickerY(int selected) {
        selectedPickerY = selected;
    }

    public static int getSelectedPickerMouseX() {
        return selectedPickerMouseX;
    }

    public static void setSelectedPickerMouseX(int x) {
        selectedPickerMouseX = x;
    }

    public static int getSelectedPickerMouseY() {
        return selectedPickerMouseY;
    }

    public static void setSelectedPickerMouseY(int y) {
        selectedPickerMouseY = y;
    }

    public static boolean handleAction(Client client, int button, int action) {

        // Pick color
        if (action == 1520) {
            open(button);
            return true;
        }

        // Copy color
        if (action == 1521) {
            Widget wid = interfaceCache[button];
            setCopiedColor(wid.textColor);
            String copiedColor = String.format("%06X", (0xFFFFFF & wid.textColor));
            String copiedName = wid.actions[0].replace("Pick ", "");
            boolean noName = wid.noName;
            client.sendMessage("Copied color " + "<col=" + copiedColor + ">" + "#" + copiedColor + "</col>" + (noName ? "" : " from " + copiedName) + ".");
            return true;
        }
        // Paste color
        if (action == 1522) {
            Widget wid = interfaceCache[button];
            switch (wid.parent) {
                case ItemColorCustomizer.ONE_COLOR_PICKER_LAYER_ID:
                case ItemColorCustomizer.TWO_COLOR_PICKERS_LAYER_ID:
                case ItemColorCustomizer.THREE_COLOR_PICKERS_LAYER_ID:
                case ItemColorCustomizer.FOUR_COLOR_PICKERS_LAYER_ID:
                case ItemColorCustomizer.FIVE_COLOR_PICKERS_LAYER_ID:
                case ItemColorCustomizer.SIX_COLOR_PICKERS_LAYER_ID:
                    int parent = wid.parent;
                    int index = (wid.id - parent) / 2 - 1;
                    ItemColorCustomizer.setPreviewColor(getCopiedColor(), index);
                    break;
            }
            wid.textColor = getCopiedColor();
            String pastedColor = String.format("%06X", (0xFFFFFF & getCopiedColor()));
            String pastedName = wid.actions[0].replace("Pick ", "");
            boolean noName = wid.noName;
            client.sendMessage("Pasted color " + "<col=" + pastedColor + ">" + "#" + pastedColor + "</col>" + (noName ? "" : " to " + pastedName) + ".");
            return true;
        }

        return false;
    }


    public enum PickerType {
        SATURATION_BRIGHTNESS, HUE;
    }
}
