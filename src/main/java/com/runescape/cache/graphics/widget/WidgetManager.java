package com.runescape.cache.graphics.widget;

import com.grinder.Configuration;
import com.grinder.client.util.Log;
import com.grinder.client.util.MenuOpcodes;
import com.grinder.model.*;
import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.audio.ButtonSounds;
import com.runescape.cache.anim.Animation;
import com.runescape.cache.def.ItemDefinition;
import com.runescape.cache.def.NpcDefinition;
import com.runescape.cache.def.ObjectDefinition;
import com.runescape.cache.definition.OSObjectDefinition;
import com.runescape.cache.graphics.GameFont;
import com.runescape.cache.graphics.RSFont;
import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.cache.graphics.sprite.SpriteLoader;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.Item;
import com.runescape.entity.Npc;
import com.runescape.entity.Player;
import com.runescape.entity.PlayerRelations;
import com.runescape.entity.model.Model;
import com.grinder.client.ClientCompanion;
import com.grinder.client.ClientUtil;
import com.runescape.input.MouseHandler;
import com.runescape.cache.graphics.sprite.SpriteCompanion;
import com.runescape.io.packets.outgoing.impl.*;
import com.runescape.scene.DynamicObject;
import com.runescape.util.MiscUtils;
import com.runescape.util.StringUtils;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Arrays;

/**
 * Class that contains various widget related methods.
 */
public final class WidgetManager {

    public static void handleTransparentHoverRectangle(int _x, int currentY, Widget childInterface) {
        boolean hover = childInterface.hovered;
        int colour = childInterface.textColor;
        int opacity = childInterface.opacity;
        if (hover)
            opacity = childInterface.transparency;
        if (childInterface.filled)
            Rasterizer2D.drawTransparentBox(_x, currentY, childInterface.width, childInterface.height,
                    colour, 256 - (opacity & 0xff));
        else
            Rasterizer2D.drawTransparentBoxOutline(_x, currentY, childInterface.width,
                    childInterface.height, colour, 256 - (opacity & 0xff));
    }

    public static void handleInput(RSFont newSmallFont, int _x, int currentY, Widget childInterface) {
        boolean selected = ClientCompanion.interfaceInputSelected == childInterface.id;
        Rasterizer2D.drawBoxOutline(_x, currentY, childInterface.width, childInterface.height, 0x252013);
        Rasterizer2D.drawBoxOutline(_x + 1, currentY + 1, childInterface.width - 2, childInterface.height - 2, 0x474745);
        Rasterizer2D.drawTransparentBox(_x + 2, currentY + 2, childInterface.width - 4, childInterface.height - 4, childInterface.textColor, 10);
        String s = childInterface.getDefaultText().isEmpty() ? childInterface.secondaryText : childInterface.getDefaultText();
        int textX = _x + 3;
        int difference = newSmallFont.getTextWidth(childInterface.getDefaultText()) - childInterface.width + 10;
        int tempB = Rasterizer2D.Rasterizer2D_yClipEnd, tempL = Rasterizer2D.Rasterizer2D_xClipStart, tempR = Rasterizer2D.Rasterizer2D_xClipEnd, tempT = Rasterizer2D.Rasterizer2D_yClipStart;
        if (difference > 0) {
            Rasterizer2D.Rasterizer2D_setClip(textX, tempB, textX + childInterface.width + 10, tempT);
            textX -= difference;
        }
        String text = selected ? childInterface.getDefaultText() + (Client.tick % 40 < 20 ? "@whi@|" : "") : s;
        if (childInterface.id == ItemDropFinder.INPUT_ID)
            text = StringUtils.formatText(text);
        newSmallFont.drawBasicString(text, textX, currentY + (childInterface.height / 2) + ((childInterface.height - newSmallFont.baseCharacterHeight) / 2), childInterface.textColor, 0, !selected && childInterface.getDefaultText().isEmpty() ? 128 : 256);
        if (difference > 0) {
            Rasterizer2D.Rasterizer2D_setClip(tempL, tempB, tempR, tempT);
        }
    }

    public static void handleColorPicker(int x, Widget rsInterface, int y, int hoverXOffset, int hoverYOffset, int _x, int currentY, Widget childInterface) {
        int sbWidth = childInterface.width - 31; // including outline
        Rasterizer2D.drawBoxOutline(_x, currentY, sbWidth, childInterface.height, 0x252013);
        Rasterizer2D.drawSaturationBrightnessMap(_x + 1, currentY + 1, sbWidth - 2, childInterface.height - 2, childInterface.hsb[0]);

        int hueWidth = 21; // including outline
        Rasterizer2D.drawBoxOutline(_x + sbWidth + 10, currentY, hueWidth, childInterface.height, 0x252013);
        Rasterizer2D.drawHueMap(_x + sbWidth + 11, currentY + 1, hueWidth - 2, childInterface.height - 2);

        Sprite leftArrow = SpriteLoader.getSprite(194);
        Sprite rightArrow = SpriteLoader.getSprite(195);
        int arrowY = ColorPicker.getPositionForHue(childInterface.hsb[0], childInterface.height - 2) - 4;
        leftArrow.drawSprite(_x + sbWidth + 1, currentY + 1 + arrowY);
        rightArrow.drawSprite(_x + sbWidth + 10 + hueWidth, currentY + 1 + arrowY);

        int[] position = ColorPicker.getPositionForColor(childInterface.hsb, sbWidth - 2, childInterface.height - 2);
        // TODO: cleaner way to get proper drawing area
        Rasterizer2D.Rasterizer2D_setClip(_x + hoverXOffset + 1 - (ClientUI.frameMode == Client.ScreenMode.FIXED ? 4 : 0), currentY + hoverYOffset + 1 + childInterface.height - 2 - (ClientUI.frameMode == Client.ScreenMode.FIXED ? 4 : 0), _x + hoverXOffset + 1 + sbWidth - 2 - (ClientUI.frameMode == Client.ScreenMode.FIXED ? 4 : 0), currentY + hoverYOffset + 1 - (ClientUI.frameMode == Client.ScreenMode.FIXED ? 4 : 0));
        Sprite circle = SpriteLoader.getSprite(632);
        circle.drawAdvancedSprite(_x + position[0] - 6, currentY + position[1] - 6);
        Rasterizer2D.Rasterizer2D_setClip(x, y + rsInterface.height, x + rsInterface.width, y);
    }


    public static void handleColorBox(RSFont newRegularFont, RSFont newSmallFont, int _x, int currentY, Widget childInterface) {
        RSFont font;

        if (childInterface.width >= 70 && childInterface.height >= 50) {
            font = newRegularFont;
        } else {
            font = newSmallFont;
        }

        Rasterizer2D.drawBoxOutline(_x, currentY, childInterface.width, childInterface.height, 0x252013);
        Rasterizer2D.drawBox(_x + 1, currentY + 1, childInterface.width - 2, childInterface.height - 2, childInterface.textColor);
        font.drawCenteredString(String.format("#%06X", (0xFFFFFF & childInterface.textColor)), _x + (childInterface.width / 2), currentY + font.baseCharacterHeight + (childInterface.height / 2) - (font.baseCharacterHeight / 2), 0xFFFFFF, 0);
    }

    public static void handleHoverSpeedSprite(Client client, int _x, int currentY, Widget childInterface) {
        Sprite sprite;
        Sprite hover;

        if (childInterface.spriteXOffset != 0) {
            _x += childInterface.spriteXOffset;
        }

        if (childInterface.spriteYOffset != 0) {
            currentY += childInterface.spriteYOffset;
        }

        if (client.interfaceIsSelected(childInterface)) {
            sprite = childInterface.enabledSprite;
            hover = childInterface.hoverSprite2;
        } else {
            sprite = childInterface.disabledSprite;
            hover = childInterface.hoverSprite1;
        }

        if (sprite != null) {
            boolean drawTransparent = childInterface.drawsTransparent;

            // Check if parent draws as transparent..
            if (!drawTransparent && childInterface.parent > 0
                    && Widget.interfaceCache[childInterface.parent] != null) {
                drawTransparent = Widget.interfaceCache[childInterface.parent].drawsTransparent;
            }

            if (drawTransparent) {
                sprite.drawTransparentSprite(_x, currentY, childInterface.transparency);
            } else if (childInterface.drawsAdvanced) {
                sprite.drawAdvancedSprite(_x, currentY);
            } else {
                sprite.drawSprite(_x, currentY);
            }
        }

        if (hover != null) {
            if (childInterface.hovered) {
                if (childInterface.hoverSpeed != 0) {
                    childInterface.buttonAlpha += childInterface.buttonAlpha < 250 ? childInterface.hoverSpeed : 0;
                    childInterface.buttonAlpha -= childInterface.buttonAlpha > 250 ? 1 : 0;
                    if (childInterface.buttonAlpha < 250) {
                        hover.drawTransparentSprite(_x, currentY, childInterface.buttonAlpha);
                    } else {
                        hover.drawSprite(_x, currentY);
                    }
                } else {
                    hover.drawSprite(_x, currentY);
                }
            } else {
                if (childInterface.hoverSpeed != 0) {
                    childInterface.buttonAlpha -= childInterface.buttonAlpha > 0 ? childInterface.hoverSpeed : 0;
                    hover.drawTransparentSprite(_x, currentY, childInterface.buttonAlpha);
                }
            }
        }
    }

    public static void handleHoverSprite(Client client, int _x, int currentY, Widget childInterface) {
        Sprite sprite;
        boolean hovered = childInterface.hovered;

        if (childInterface.spriteXOffset != 0) {
            _x += childInterface.spriteXOffset;
        }

        if (childInterface.spriteYOffset != 0) {
            currentY += childInterface.spriteYOffset;
        }

        if (client.interfaceIsSelected(childInterface)) {
            sprite = hovered ? childInterface.hoverSprite2 : childInterface.enabledSprite;
        } else {
            sprite = hovered ? childInterface.hoverSprite1 : childInterface.disabledSprite;
        }

        if (sprite != null) {
            boolean drawTransparent = childInterface.drawsTransparent;

            // Check if parent draws as transparent..
            if (!drawTransparent && childInterface.parent > 0
                    && Widget.interfaceCache[childInterface.parent] != null) {
                drawTransparent = Widget.interfaceCache[childInterface.parent].drawsTransparent;
            }

            if (hovered && childInterface.transparentOnHover) {
                drawTransparent = true;
            }

            if (drawTransparent) {
                sprite.drawTransparentSprite(_x, currentY, childInterface.transparency);
            } else if (childInterface.drawsAdvanced) {
                sprite.drawAdvancedSprite(_x, currentY);
            } else {
                sprite.drawSprite(_x, currentY);
            }
        }

        if (childInterface.getDefaultText() != null) {
            final RSFont font = ClientUtil.getRSFont(client.boldText, client.fancyFont, client.fancyFontLarge, client.newBoldFont, client.newFancyFont, client.newFancyFontLarge, client.newRegularFont, client.newSmallFont, client.regularText, client.smallText, childInterface.textDrawingAreas);
            if (font != null) {
                if (childInterface.centerText)
                    font.drawCenteredString(childInterface.getDefaultText(), _x + childInterface.width / 2 + childInterface.textHorizontalOffset, currentY + 4 + childInterface.height / 2, hovered ? childInterface.defaultHoverColor : childInterface.textColor, 0);
                else
                    font.drawBasicString(childInterface.getDefaultText(), _x + (sprite != null ? sprite.myWidth + 5 : 0) + childInterface.textHorizontalOffset, currentY + 4 + childInterface.height / 2, hovered ? childInterface.defaultHoverColor : childInterface.textColor, 0);
            }
        }

        // Button config timers (deposit buttons in bank)
        if (childInterface.timerSpeed > 0 && childInterface.timerStartTime != 0L && System.currentTimeMillis() - childInterface.timerStartTime > childInterface.timerSpeed) {
            client.setButtonConfig(childInterface.valueIndexArray[0][1], 0);
            childInterface.timerStartTime = 0L;
        }
    }

    public static void handleTradeButton(int _x, int currentY, Widget childInterface) {
        boolean left = childInterface.requiredValues[0] == 0;

        if (left) {
            SpriteLoader.getSprite(832).repeatX(_x, currentY, childInterface.width);
            SpriteLoader.getSprite(828).drawSprite(_x, currentY);
        } else {
            SpriteLoader.getSprite(832).repeatX(_x, currentY, childInterface.width);
            SpriteLoader.getSprite(829).drawSprite(childInterface.width + _x - SpriteLoader.getSprite(829).myWidth, currentY);
        }
    }

    public static void handleDarkBox(int _x, int currentY, Widget childInterface) {
        if (childInterface.getBoxBorder() == Widget.BoxBorder.LIGHT) {
            Rasterizer2D.drawDarkBox(_x, currentY, childInterface.width, childInterface.height);
        } else {

            boolean modern = childInterface.getBoxBorder() == Widget.BoxBorder.MODERN;

            if (childInterface.filled) {
                Rasterizer2D.drawDarkBox(_x, currentY, childInterface.width, childInterface.height);
            }

            if (childInterface.width > 64) {
                handleSeparator(_x, currentY, childInterface, modern, 832, 813);
                handleSeparator(_x, childInterface.height + currentY - SpriteLoader.getSprite(modern ? 832 : 814).myHeight, childInterface, modern, 832, 814);
            }

            SpriteLoader.getSprite(modern ? 828 : 809).drawSprite(_x, currentY);

            SpriteLoader.getSprite(modern ? 829 : 810).drawSprite(childInterface.width + _x - SpriteLoader.getSprite(modern ? 829 : 810).myWidth, currentY);

            SpriteLoader.getSprite(modern ? 830 : 811).drawSprite(_x, childInterface.height + currentY - SpriteLoader.getSprite(modern ? 830 : 811).myHeight);
            SpriteLoader.getSprite(modern ? 831 : 812).drawSprite(childInterface.width + _x - SpriteLoader.getSprite(modern ? 831 : 812).myWidth, childInterface.height + currentY - SpriteLoader.getSprite(modern ? 831 : 812).myHeight);
        }
    }

    public static void handleSeparator(int _x, int currentY, Widget childInterface, boolean modernWindow, int i2, int i3) {
        SpriteLoader.getSprite(modernWindow ? i2 : i3).repeatX(_x, currentY, childInterface.width);
    }

    public static void handleWindow(int _x, int currentY, Widget childInterface) {
        Rasterizer2D.Rasterizer2D_setClip(_x, childInterface.height + currentY, childInterface.width + _x, currentY);

        SpriteLoader.getSprite(833).repeatBoth(_x, currentY, childInterface.width, childInterface.height);

        handleSeparator(_x, currentY, childInterface, childInterface.isModernWindow(), 821, 813);

        handleSeparator(_x, childInterface.height + currentY - SpriteLoader.getSprite(childInterface.isModernWindow() ? 822 : 814).myHeight, childInterface, childInterface.isModernWindow(), 822, 814);

        SpriteLoader.getSprite(childInterface.isModernWindow() ? 823 : 815).repeatY(_x, currentY, childInterface.height);

        SpriteLoader.getSprite(childInterface.isModernWindow() ? 824 : 816).repeatY(childInterface.width + _x - SpriteLoader.getSprite(childInterface.isModernWindow() ? 824 : 816).myWidth, currentY, childInterface.height);

        SpriteLoader.getSprite(childInterface.isModernWindow() ? 817 : 809).drawSprite(_x, currentY);
        SpriteLoader.getSprite(childInterface.isModernWindow() ? 818 : 810).drawSprite(childInterface.width + _x - SpriteLoader.getSprite(childInterface.isModernWindow() ? 818 : 810).myWidth, currentY);

        SpriteLoader.getSprite(childInterface.isModernWindow() ? 819 : 811).drawSprite(_x, childInterface.height + currentY - SpriteLoader.getSprite(childInterface.isModernWindow() ? 819 : 811).myHeight);
        SpriteLoader.getSprite(childInterface.isModernWindow() ? 820 : 812).drawSprite(childInterface.width + _x - SpriteLoader.getSprite(childInterface.isModernWindow() ? 820 : 812).myWidth, childInterface.height + currentY - SpriteLoader.getSprite(childInterface.isModernWindow() ? 820 : 812).myHeight);
    }

    public static void handlePercentage(RSFont newRegularFont, int _x, int currentY, Widget childInterface) {
        int completed = childInterface.percentageCompleted;
        int total = childInterface.percentageTotal;

        int complete = (completed * childInterface.percentageDimension) / total;
        int percentage = (completed * 100) / total;
        boolean green = percentage == 100;

        if (complete > 0) {
            SpriteLoader.getSprite(green ? 1013 : childInterface.percentageBarStart).drawAdvancedSprite(_x, currentY);
            int barStartWidth = SpriteLoader.getSprite(green ? 1013 : childInterface.percentageBarStart).myWidth;
            for (int i = 0; i < complete; i++) {
                if (i >= barStartWidth && i < childInterface.percentageDimension - barStartWidth) {
                    SpriteLoader.getSprite(green ? 1014 : childInterface.percentageSpriteFull).drawAdvancedSprite(_x + i, currentY);
                }
            }
            Sprite endSprite = SpriteLoader.getSprite(green ? 1015 : childInterface.percentageBarEnd);
            if (percentage == 100) {
                endSprite.drawAdvancedSprite(_x + childInterface.percentageDimension - endSprite.myWidth, currentY);
            }
        }

        String s = percentage + "%";
        newRegularFont.drawBasicString(s, _x + (childInterface.percentageDimension / 2) - (newRegularFont.getTextWidth(s) / 2),
                currentY + 18, 0xffffff, 0);
    }

    public static void handleMap(Client client, int _x, int currentY) {
        Rasterizer2D.drawBox(_x + 46, currentY + 52, 414, 334, 0);
        SpriteCompanion.minimapImage.drawSprite(_x, currentY);

        // Calculate player location relative to map position
        int mapX = (_x + 52) + Client.localPlayer.x / 32;
        int mapY = (currentY + 466) - Client.localPlayer.y / 32;

        Sprite markerSprite = SpriteLoader.getSprite(571);
        Graphics2D g2d = Rasterizer2D.createGraphics(false);

        // Pause rotation every 90 degrees
        if (!(client.markerAngle % 90 == 0 && System.currentTimeMillis() - client.lastMarkerRotation <= 125)) {
            client.markerAngle += 3;
            client.lastMarkerRotation = System.currentTimeMillis();
        }

        g2d.rotate(Math.toRadians(-client.markerAngle), mapX, mapY);
        g2d.drawImage(client.worldMapMarker, mapX - markerSprite.myWidth / 2, mapY - markerSprite.myHeight / 2,
                markerSprite.myWidth, markerSprite.myHeight, null);
        g2d.dispose();
    }

    public static void handleBox(int _x, int currentY, Widget childInterface) {
        // Draw outline
        Rasterizer2D.drawBox(_x - 2, currentY - 2, childInterface.width + 4, childInterface.height + 4,
                0x0e0e0c);
        Rasterizer2D.drawBox(_x - 1, currentY - 1, childInterface.width + 2, childInterface.height + 2,
                0x474745);
        // Draw base box
        if (childInterface.toggled) {
            Rasterizer2D.drawBox(_x, currentY, childInterface.width, childInterface.height,
                    childInterface.secondaryHoverColor);
            childInterface.toggled = false;
        } else {
            Rasterizer2D.drawBox(_x, currentY, childInterface.width, childInterface.height,
                    childInterface.defaultHoverColor);
        }
    }

    public static void handleAdjustableConfig(int _x, int currentY, Widget childInterface) {
        int totalWidth = childInterface.width;
        int spriteWidth = childInterface.enabledSprite.myWidth;
        int totalHeight = childInterface.height;
        int spriteHeight = childInterface.enabledSprite.myHeight;

        Sprite behindSprite = childInterface.active ? childInterface.enabledAltSprite
                : childInterface.disabledAltSprite;

        if (childInterface.toggled) {
            behindSprite.drawSprite(_x, currentY);
            childInterface.enabledSprite.drawAdvancedSprite(_x + (totalWidth / 2) - spriteWidth / 2,
                    currentY + (totalHeight / 2) - spriteHeight / 2, childInterface.spriteOpacity);
            childInterface.toggled = false;
        } else {
            behindSprite.drawSprite(_x, currentY);
            childInterface.enabledSprite.drawSprite(_x + (totalWidth / 2) - spriteWidth / 2,
                    currentY + (totalHeight / 2) - spriteHeight / 2);
        }
    }

    public static boolean handleKeyBindingDopDown(Client client, int _x, int currentY, Widget childInterface) {
        DropdownMenu d = childInterface.dropdown;

        // If dropdown inverted, don't draw following 2 menus
        if (client.dropdownInversionFlag > 0) {
            client.dropdownInversionFlag--;
            return true;
        }

        Rasterizer2D.drawPixels(18, currentY + 1, _x + 1, 0x544834, d.getWidth() - 2);
        Rasterizer2D.drawPixels(16, currentY + 2, _x + 2, 0x2e281d, d.getWidth() - 4);
        client.newRegularFont.drawBasicString(d.getSelected(), _x + 7, currentY + 15, 0xff8a1f, 0);
        SpriteLoader.getSprite(449).drawSprite(_x + d.getWidth() - 18, currentY + 2); // Arrow

        if (d.isOpen()) {

            Widget.interfaceCache[childInterface.id - 1].active = true; // Alter
            // stone
            // colour

            int yPos = currentY + 18;

            // Dropdown inversion for lower stones
            if (childInterface.inverted) {
                yPos = currentY - d.getHeight() - 10;
                client.dropdownInversionFlag = 2;
            }

            Rasterizer2D.drawPixels(d.getHeight() + 12, yPos, _x + 1, 0x544834, d.getWidth() - 2);
            Rasterizer2D.drawPixels(d.getHeight() + 10, yPos + 1, _x + 2, 0x2e281d, d.getWidth() - 4);

            int yy = 2;
            int xx = 0;
            int bb = d.getWidth() / 2;

            for (int i = 0; i < d.getOptions().length; i++) {

                int fontColour = 0xff981f;
                if (childInterface.dropdownHover == i) {
                    fontColour = 0xffffff;
                }

                if (xx == 0) {
                    client.newRegularFont.drawBasicString(d.getOptions()[i], _x + 5, yPos + 14 + yy, fontColour,
                            0x2e281d);
                    xx = 1;

                } else {
                    client.newRegularFont.drawBasicString(d.getOptions()[i], _x + 5 + bb, yPos + 14 + yy,
                            fontColour, 0x2e281d);
                    xx = 0;
                    yy += 15;
                }
            }
        } else {
            Widget.interfaceCache[childInterface.id - 1].active = false;
        }
        return false;
    }

    public static void handleDropdown(int barFillColor, RSFont newSmallFont, int _x, int currentY, Widget childInterface) {
        DropdownMenu d = childInterface.dropdown;

        int bgColour = childInterface.dropdownColours[2];
        int fontColour = 0xfe971e;
        int downArrow = 397;

        if (childInterface.hovered || d.isOpen()) {
            downArrow = 398;
            fontColour = 0xffb83f;
            bgColour = childInterface.dropdownColours[3];
        }

        Rasterizer2D.drawPixels(20, currentY, _x, childInterface.dropdownColours[0], d.getWidth());
        Rasterizer2D.drawPixels(18, currentY + 1, _x + 1, childInterface.dropdownColours[1],
                d.getWidth() - 2);
        Rasterizer2D.drawPixels(16, currentY + 2, _x + 2, bgColour, d.getWidth() - 4);

        int xOffset = childInterface.centerText ? 3 : 16;
        newSmallFont.drawCenteredString(d.getSelected(), _x + (d.getWidth() - xOffset) / 2, currentY + 14,
                fontColour, 0);

        if (d.isOpen()) {
            // Up arrow
            SpriteLoader.getSprite(396).drawSprite(_x + d.getWidth() - 18, currentY + 2);

            Rasterizer2D.drawPixels(d.getHeight(), currentY + 19, _x, childInterface.dropdownColours[0],
                    d.getWidth());
            Rasterizer2D.drawPixels(d.getHeight() - 2, currentY + 20, _x + 1,
                    childInterface.dropdownColours[1], d.getWidth() - 2);
            Rasterizer2D.drawPixels(d.getHeight() - 4, currentY + 21, _x + 2,
                    childInterface.dropdownColours[3], d.getWidth() - 4);

            int yy = 2;
            for (int i = 0; i < d.getOptions().length; i++) {
                if (childInterface.dropdownHover == i) {
                    if (childInterface.id == 28102) {
                        Rasterizer2D.drawTransparentBox(_x + 2, currentY + 19 + yy, d.getWidth() - 4, 13,
                                0xd0914d, 80);
                    } else {
                        Rasterizer2D.drawPixels(13, currentY + 19 + yy, _x + 2,
                                childInterface.dropdownColours[4], d.getWidth() - 4);
                    }
                    newSmallFont.drawCenteredString(d.getOptions()[i], _x + (d.getWidth() - xOffset) / 2,
                            currentY + 29 + yy, 0xffb83f, 0);
                } else {
                    Rasterizer2D.drawPixels(13, currentY + 19 + yy, _x + 2,
                            childInterface.dropdownColours[3], d.getWidth() - 4);
                    newSmallFont.drawCenteredString(d.getOptions()[i], _x + (d.getWidth() - xOffset) / 2,
                            currentY + 29 + yy, 0xfe971e, 0);
                }
                yy += 14;
            }

            ClientUtil.drawScrollbar(_x + d.getWidth() - 18, currentY + 21, childInterface.scrollPosition, d.getHeight() - 4, d.getHeight() - 5, barFillColor,
                    false);
        } else {
            SpriteLoader.getSprite(downArrow).drawSprite(_x + d.getWidth() - 18, currentY + 2);
        }
    }

    public static void handleSlider(int _x, int currentY, Widget childInterface) {
        Slider slider = childInterface.slider;
        if (slider != null) {
            slider.draw(_x, currentY);
        }
    }

    public static void handleConfig(int _x, int currentY, Widget childInterface) {
        Sprite sprite = childInterface.active ? childInterface.enabledSprite
                : childInterface.disabledSprite;
        sprite.drawSprite(_x, currentY);
    }

    public static boolean handleHover(int _x, int currentY, Widget childInterface) {
        // Draw sprite
        boolean flag = false;

        if (childInterface.toggled) {
            childInterface.disabledSprite.drawAdvancedSprite(_x, currentY, childInterface.spriteOpacity);
            flag = true;
            childInterface.toggled = false;
        } else {
            childInterface.enabledSprite.drawAdvancedSprite(_x, currentY, childInterface.spriteOpacity);
        }

        // Draw text
        if (childInterface.getDefaultText() == null) {
            return true;
        }
        if (childInterface.centerText) {
            childInterface.rsFont.drawCenteredString(childInterface.getDefaultText(), _x + childInterface.msgX,
                    currentY + childInterface.msgY,
                    flag ? childInterface.defaultHoverColor : childInterface.textColor, 0);
        } else {
            if (childInterface.rsFont != null)
                childInterface.rsFont.drawBasicString(childInterface.getDefaultText(), _x + 5,
                        currentY + childInterface.msgY,
                        flag ? childInterface.defaultHoverColor : childInterface.textColor, 0);
        }
        return false;
    }

    public static void handleOther(Client client, int x, Widget rsInterface, int y, int _x, int currentY, Widget childInterface) {
        if (childInterface.hoverXOffset != 0) {
            _x += childInterface.hoverXOffset;
        }

        if (childInterface.hoverYOffset != 0) {
            currentY += childInterface.hoverYOffset;
        }

        if (childInterface.regularHoverBox) {
            client.drawHoverBox(_x, currentY, childInterface.getDefaultText());

        } else {
            int boxWidth = 0;
            int boxHeight = 0;
            String message = childInterface.getDefaultText();
            //boolean combatSkill = message.contains("Magic") || message.contains("Strength") || message.contains("Defence") || message.contains("Attack") || message.contains("Ranged");
            if (childInterface.parent == 3917) {
                String[] msg = message.split("\\\\n");
                if (client.executeScript(childInterface, 1) >= 2_048_401_225) {
                    message = msg[0];
                }
            }
            GameFont font = client.regularText;
            for (String text = message; text.length() > 0; ) {
                if (text.contains("%")) {
                    do {
                        int index = text.indexOf("%1");
                        if (index == -1)
                            break;
                        text = text.substring(0, index)
                                + ClientUtil.interfaceIntToString(client.executeScript(childInterface, 0), childInterface.parent != 3917)
                                + text.substring(index + 2);
                    } while (true);
                    do {
                        int index = text.indexOf("%2");
                        if (index == -1)
                            break;
                        text = text.substring(0, index)
                                + ClientUtil.interfaceIntToString(client.executeScript(childInterface, 1), childInterface.parent != 3917)
                                + text.substring(index + 2);
                    } while (true);
                    do {
                        int index = text.indexOf("%3");
                        if (index == -1)
                            break;
                        text = text.substring(0, index)
                                + ClientUtil.interfaceIntToString(client.executeScript(childInterface, 2), childInterface.parent != 3917)
                                + text.substring(index + 2);
                    } while (true);
                    do {
                        int index = text.indexOf("%4");
                        if (index == -1)
                            break;
                        text = text.substring(0, index)
                                + ClientUtil.interfaceIntToString(client.executeScript(childInterface, 3), childInterface.parent != 3917)
                                + text.substring(index + 2);
                    } while (true);
                    do {
                        int index = text.indexOf("%5");
                        if (index == -1)
                            break;
                        text = text.substring(0, index)
                                + ClientUtil.interfaceIntToString(client.executeScript(childInterface, 4), childInterface.parent != 3917)
                                + text.substring(index + 2);
                    } while (true);
                    do {
                        int l7 = text.indexOf("%6");
                        if (l7 == -1)
                            break;
                        text = text.substring(0, l7)
                                + NumberFormat.getIntegerInstance().format(
                                client.executeScript(childInterface, 0) - client.executeScript(childInterface, 1))
                                + text.substring(l7 + 2);
                    } while (true);
                }
                int line = text.indexOf("\\n");
                String drawn;
                if (line != -1) {
                    drawn = text.substring(0, line);
                    text = text.substring(line + 2);
                } else {
                    drawn = text;
                    text = "";
                }
                int j10 = font.getTextWidth(drawn);
                if (j10 > boxWidth) {
                    boxWidth = j10;
                }
                boxHeight += font.verticalSpace + 1;
            }
            boxWidth += 6;
            boxHeight += 7;

            int xPos = (_x + childInterface.width) - 5 - boxWidth;
            int yPos = currentY + childInterface.height + 5;
            if (xPos < _x + 5) {
                xPos = _x + 5;
            }

            if (xPos + boxWidth > x + rsInterface.width) {
                xPos = (x + rsInterface.width) - boxWidth;
            }
            if (yPos + boxHeight > y + rsInterface.height) {
                yPos = (currentY - boxHeight);
            }

            String s2 = message;

            Rasterizer2D.drawBox(xPos, yPos, boxWidth, boxHeight, 0xFFFFA0);
            Rasterizer2D.drawBoxOutline(xPos, yPos, boxWidth, boxHeight, 0);

            // Script hovers here

            for (int j11 = yPos + font.verticalSpace + 2; s2.length() > 0; j11 += font.verticalSpace + 1) {// verticalSpace
                if (s2.contains("%")) {

                    do {
                        int k7 = s2.indexOf("%1");
                        if (k7 == -1)
                            break;
                        s2 = s2.substring(0, k7) + ClientUtil.interfaceIntToString(client.executeScript(childInterface, 0), childInterface.parent != 3917)
                                + s2.substring(k7 + 2);
                    } while (true);

                    do {
                        int l7 = s2.indexOf("%2");
                        if (l7 == -1)
                            break;
                        s2 = s2.substring(0, l7) + ClientUtil.interfaceIntToString(client.executeScript(childInterface, 1), childInterface.parent != 3917)
                                + s2.substring(l7 + 2);
                    } while (true);
                    do {
                        int i8 = s2.indexOf("%3");
                        if (i8 == -1)
                            break;
                        s2 = s2.substring(0, i8) + ClientUtil.interfaceIntToString(client.executeScript(childInterface, 2), childInterface.parent != 3917)
                                + s2.substring(i8 + 2);
                    } while (true);
                    do {
                        int j8 = s2.indexOf("%4");
                        if (j8 == -1)
                            break;
                        s2 = s2.substring(0, j8) + ClientUtil.interfaceIntToString(client.executeScript(childInterface, 3), childInterface.parent != 3917)
                                + s2.substring(j8 + 2);
                    } while (true);
                    do {
                        int k8 = s2.indexOf("%5");
                        if (k8 == -1)
                            break;
                        s2 = s2.substring(0, k8) + ClientUtil.interfaceIntToString(client.executeScript(childInterface, 4), childInterface.parent != 3917)
                                + s2.substring(k8 + 2);
                    } while (true);
                    do {
                        int k8 = s2.indexOf("%6");
                        if (k8 == -1)
                            break;
                        s2 = s2.substring(0, k8)
                                + NumberFormat.getIntegerInstance().format(
                                client.executeScript(childInterface, 0) - client.executeScript(childInterface, 1))
                                + s2.substring(k8 + 2);
                    } while (true);
                }
                int l11 = s2.indexOf("\\n");
                String s5;
                if (l11 != -1) {
                    s5 = s2.substring(0, l11);
                    s2 = s2.substring(l11 + 2);
                } else {
                    s5 = s2;
                    s2 = "";
                }
                if (childInterface.centerText) {
                    font.drawCenteredText(s5, xPos + childInterface.width / 2, j11, yPos, false);
                } else {
                    if (s5.contains("\\r")) {
                        String text = s5.substring(0, s5.indexOf("\\r"));
                        String text2 = s5.substring(s5.indexOf("\\r") + 2);
                        font.drawTextWithPotentialShadow(text, xPos + 3, j11, 0, false);
                        int rightX = boxWidth + xPos - font.getTextWidth(text2) - 2;
                        font.drawTextWithPotentialShadow(text2, rightX, j11, 0, false);
                    } else
                        font.drawTextWithPotentialShadow(s5, xPos + 3, j11, 0, false);
                }
            }
        }
    }

    public static void handleItemList(int _x, int currentY, Widget childInterface) {
        GameFont font = childInterface.textDrawingAreas;
        int slot = 0;
        for (int row = 0; row < childInterface.height; row++) {
            for (int column = 0; column < childInterface.width; column++) {
                if (childInterface.inventoryItemId[slot] > 0) {
                    ItemDefinition item = ItemDefinition.lookup(childInterface.inventoryItemId[slot] - 1);
                    String name = item.name;
                    if (item.stackable || childInterface.inventoryAmounts[slot] != 1)
                        name = name + " x" + ClientUtil.intToKOrMilLongName(childInterface.inventoryAmounts[slot]);
                    int __x = _x + column * (115 + childInterface.spritePaddingX);
                    int __y = currentY + row * (12 + childInterface.spritePaddingY);
                    if (childInterface.centerText)
                        font.drawCenteredText(name, __x + childInterface.width / 2, __y, childInterface.textColor,
                                childInterface.textShadow);
                    else
                        font.drawTextWithPotentialShadow(name, __x, __y, childInterface.textColor, childInterface.textShadow
                        );
                }
                slot++;
            }
        }
    }

    public static void handleModel(Client client, int _x, int currentY, Widget childInterface) {
        int centreX = Rasterizer3D.originViewX;
        int centreY = Rasterizer3D.originViewY;
        Rasterizer3D.originViewX = _x + childInterface.width / 2;
        Rasterizer3D.originViewY = currentY + childInterface.height / 2;
        int sine = Rasterizer3D.Rasterizer3D_sine[childInterface.modelXAngle]
                * childInterface.modelZoom >> 16;
        int cosine = Rasterizer3D.Rasterizer3D_cosine[childInterface.modelXAngle] * childInterface.modelZoom >> 16;
        boolean selected = client.interfaceIsSelected(childInterface);
        int emoteAnimation;
        if (selected)
            emoteAnimation = childInterface.secondaryAnimationId;
        else
            emoteAnimation = childInterface.defaultAnimationId;
        Model model;
        if (emoteAnimation == -1) {
            model = childInterface.method209(null, -1, selected);
        } else {
            Animation animation = Animation.getSequenceDefinition(emoteAnimation);
            model = childInterface.method209(animation,
                    childInterface.currentFrame, selected);
        }
        try {
            if (model != null)
                model.method482(childInterface.modelYAngle, childInterface.modelZAngle, childInterface.modelXAngle, 0, sine,
                        cosine);
        } catch (ArithmeticException ignored) {
        }
        Rasterizer3D.originViewX = centreX;
        Rasterizer3D.originViewY = centreY;
    }

    public static void handleSprite(Client client, int _x, int currentY, Widget childInterface) {
        if (childInterface.id == 34002)
            childInterface.transparency = client.spinnerSpinning ? 255 : 255 - (70 + (int) (70 * Math.sin(Client.tick / 18.0)));

        Sprite sprite;

        if (childInterface.spriteXOffset != 0) {
            _x += childInterface.spriteXOffset;
        }

        if (childInterface.spriteYOffset != 0) {
            currentY += childInterface.spriteYOffset;
        }

        if (client.interfaceIsSelected(childInterface)) {
            sprite = childInterface.enabledSprite;
        } else {
            sprite = childInterface.disabledSprite;
        }

        if (client.spellSelected == 1 && childInterface.id == ClientCompanion.spellId && ClientCompanion.spellId != 0 && sprite != null) {
            sprite.drawSprite(_x, currentY, 0xffffff);
        } else {
            if (sprite != null) {
                boolean drawTransparent = childInterface.drawsTransparent;

                // Check if parent draws as transparent..
                if (!drawTransparent && childInterface.parent > 0
                        && Widget.interfaceCache[childInterface.parent] != null) {
                    drawTransparent = Widget.interfaceCache[childInterface.parent].drawsTransparent;
                }

                if (drawTransparent) {
                    sprite.drawTransparentSprite(_x, currentY, childInterface.transparency);
                } else if (childInterface.drawsAdvanced) {
                    sprite.drawAdvancedSprite(_x, currentY);
                } else {
                    sprite.drawSprite(_x, currentY);
                }
            }
        }
        if (GameFrame.autocast && childInterface.id == client.autoCastId)
            SpriteLoader.getSprite(43).drawSprite(_x, currentY);

        // Button config timers (deposit buttons in bank)
        if (childInterface.timerSpeed > 0 && childInterface.timerStartTime != 0L && System.currentTimeMillis() - childInterface.timerStartTime > childInterface.timerSpeed) {
            client.setButtonConfig(childInterface.valueIndexArray[0][1], 0);
            childInterface.timerStartTime = 0L;
        }
    }

    public static void handleContainer(Client client, int hoverXOffset, int hoverYOffset, int _x, int currentY, Widget childInterface) {
        if (childInterface.scrollPosition > childInterface.scrollMax - childInterface.height)
            childInterface.scrollPosition = childInterface.scrollMax - childInterface.height;
        if (childInterface.scrollPosition < 0)
            childInterface.scrollPosition = 0;
        client.drawInterface(childInterface.scrollPosition, _x, childInterface, currentY, hoverXOffset, hoverYOffset);
        if (childInterface.scrollMax >= childInterface.height && childInterface.height > 0) {
            if (childInterface.id == 36550) {
                ClientUtil.draw508Scrollbar(childInterface.height, childInterface.scrollPosition, currentY, _x + childInterface.width, childInterface.scrollMax, false);
            } else if (childInterface.id == 64001) {
                ClientUtil.drawScrollbar(_x + childInterface.width, currentY, childInterface.scrollPosition, childInterface.height, childInterface.scrollMax, client.scrollBarFillColor,
                        true);
            } else {
                ClientUtil.drawScrollbar(_x + childInterface.width, currentY, childInterface.scrollPosition, childInterface.height, childInterface.scrollMax, client.scrollBarFillColor, false);
            }
        }
    }

    public static void handleRectangle(Client client, int _x, int currentY, Widget childInterface) {
        if (childInterface.id == 34306)
            childInterface.opacity = client.spinnerSpinning ? 0 : (byte) (90 + (70 * Math.sin(Client.tick / 18.0)));

        boolean hover = false;
        if (client.previousBackDialogueChildWidgetHoverType == childInterface.id || client.previousChildWidgetHoverType2 == childInterface.id
                || client.previousChildWidgetHoverType == childInterface.id)
            hover = true;
        int colour;
        if (client.interfaceIsSelected(childInterface)) {
            colour = childInterface.secondaryColor;
            if (hover && childInterface.secondaryHoverColor != 0)
                colour = childInterface.secondaryHoverColor;
        } else {
            colour = childInterface.textColor;
            if (hover && childInterface.defaultHoverColor != 0)
                colour = childInterface.defaultHoverColor;
        }
        if (childInterface.opacity == 0) {
            if (childInterface.filled)
                Rasterizer2D.drawBox(_x, currentY, childInterface.width, childInterface.height, colour);
            else
                Rasterizer2D.drawBoxOutline(_x, currentY, childInterface.width, childInterface.height,
                        colour);
        } else if (childInterface.filled)
            Rasterizer2D.drawTransparentBox(_x, currentY, childInterface.width, childInterface.height,
                    colour, 256 - (childInterface.opacity & 0xff));
        else
            Rasterizer2D.drawTransparentBoxOutline(_x, currentY, childInterface.width,
                    childInterface.height, colour, 256 - (childInterface.opacity & 0xff));
    }

    public static void handleInventory(Client client, Widget rsInterface, int _x, int currentY, Widget childInterface) {
        int[] itemIdArray = childInterface.inventoryItemId;
        int[] itemAmountsArray = childInterface.inventoryAmounts;

        // Shop filtering
        if (childInterface.id == Shop.CONTAINER && Shop.showSearchContainers()) {
            itemIdArray = Shop.getShopIdTemp();
            itemAmountsArray = Shop.getShopAmountsTemp();
        }

        int item = 0;
        for (int row = 0; row < childInterface.height; row++) {
            for (int column = 0; column < childInterface.width; column++) {
                int tileX = _x + column
                        * (32 + childInterface.spritePaddingX);
                int tileY = currentY
                        + row * (32 + childInterface.spritePaddingY);
                if (item < 20) {
                    tileX += childInterface.spritesX[item];
                    tileY += childInterface.spritesY[item];
                }

                boolean isSpriteId = item < itemIdArray.length && itemIdArray[item] < -1;

                if (item < itemIdArray.length && itemIdArray[item] > 0 || isSpriteId) {
                    if (childInterface.id == 36250 && isSpriteId) {
                        tileX += 4;
                    }
                    int dragOffsetX = 0;
                    int dragOffsetY;
                    int bindX = 0;
                    int bindY = 0;
                    int itemId = itemIdArray[item]
                            - 1;
                    if (tileX > Rasterizer2D.Rasterizer2D_xClipStart - 32
                            && tileX < Rasterizer2D.Rasterizer2D_xClipEnd
                            && tileY > Rasterizer2D.Rasterizer2D_yClipStart - 32
                            && tileY < Rasterizer2D.Rasterizer2D_yClipEnd
                            || client.itemDragType != Widget.ITEM_DRAG_TYPE_NONE
                            && client.anInt1085 == item) {
                        int outlineColour = 0;
                        int itemOpacity = 256;
                        if (childInterface.contentType == 206) {
                            if (itemAmountsArray[item] == 0) {
                                itemOpacity = 95;
                            }
                        }
                        if (client.itemSelected == 1 && client.usedItemInventorySlot == item
                                && client.usedItemInventoryInterfaceId == childInterface.id)
                            outlineColour = 0xffffff;

                        if((childInterface.id >= 64628 && childInterface.id <= 64845) && itemAmountsArray[item] == 0)
                            itemOpacity = 80;

                        Sprite item_icon = isSpriteId ? SpriteLoader.getSprite(Math.abs(itemId)) : ItemDefinition.getSprite(itemId, itemAmountsArray[item], outlineColour);

                        if (item_icon != null) {
                            if (client.itemDragType != Widget.ITEM_DRAG_TYPE_NONE
                                    && client.anInt1085 == item
                                    && client.anInt1084 == childInterface.id) {
                                dragOffsetX = MouseHandler.x
                                        - client.previousClickMouseX;
                                dragOffsetY = MouseHandler.y
                                        - client.previousClickMouseY;
                                if (client.dragItemDelay < Configuration.dragValue) {
                                    dragOffsetX = 0;
                                    dragOffsetY = 0;
                                }
                                // Bank dragging item sprite
                                if (ClientCompanion.openInterfaceId == Bank.INTERFACE_ID) {
                                    if (!client.mouseOutOfDragZone) {
                                        dragOffsetX = 0;
                                        dragOffsetY = 0;
                                    }

                                    boolean useAmount = item_icon.maxWidth == 33 || itemAmountsArray[item] != 1;
                                    ClientUtil.setOverlayItem(item_icon, useAmount ? itemAmountsArray[item] : 1,
                                            tileX + dragOffsetX, tileY + dragOffsetY,
                                            childInterface.contentType == 206 ? Bank.BANKING_INTERFACE_LAYER : rsInterface.id, false);

                                    if (Bank.hideTabBar()) {
                                        // Hiding bank tab bar, use normal scroll region
                                        if (tileY + dragOffsetY < Rasterizer2D.Rasterizer2D_yClipStart
                                                && rsInterface.scrollPosition > 0) {
                                            int i10 = (client.tickDelta
                                                    * (Rasterizer2D.Rasterizer2D_yClipStart
                                                    - tileY
                                                    - dragOffsetY))
                                                    / 3;
                                            if (i10 > client.tickDelta * 10)
                                                i10 = client.tickDelta * 10;
                                            if (i10 > rsInterface.scrollPosition)
                                                i10 = rsInterface.scrollPosition;
                                            rsInterface.scrollPosition -=
                                                    i10;
                                            client.previousClickMouseY += i10;
                                        }
                                        if (tileY + dragOffsetY
                                                + 32 > Rasterizer2D.Rasterizer2D_yClipEnd
                                                && rsInterface.scrollPosition < rsInterface.scrollMax
                                                - rsInterface.height) {
                                            int j10 = (client.tickDelta
                                                    * ((tileY + dragOffsetY
                                                    + 32)
                                                    - Rasterizer2D.Rasterizer2D_yClipEnd))
                                                    / 3;
                                            if (j10 > client.tickDelta * 10)
                                                j10 = client.tickDelta * 10;
                                            if (j10 > rsInterface.scrollMax
                                                    - rsInterface.height
                                                    - rsInterface.scrollPosition)
                                                j10 = rsInterface.scrollMax
                                                        - rsInterface.height
                                                        - rsInterface.scrollPosition;
                                            rsInterface.scrollPosition +=
                                                    j10;
                                            client.previousClickMouseY -= j10;
                                        }
                                    } else if (client.mouseOutOfDragZone) {
                                        // Custom scroll region to prevent scrolling when dragging items to bank tab bar (like OSRS)
                                        if (tileY + dragOffsetY >= Rasterizer2D.Rasterizer2D_yClipStart - 8
                                                && tileY + dragOffsetY < Rasterizer2D.Rasterizer2D_yClipStart + 17
                                                && rsInterface.scrollPosition > 0
                                                && client.dragItemDelay >= 15) {
                                            int i10 = (client.tickDelta
                                                    * (Rasterizer2D.Rasterizer2D_yClipStart + 17
                                                    - tileY
                                                    - dragOffsetY))
                                                    / 3;
                                            if (i10 > client.tickDelta * 10)
                                                i10 = client.tickDelta * 10;
                                            if (i10 > rsInterface.scrollPosition)
                                                i10 = rsInterface.scrollPosition;
                                            rsInterface.scrollPosition -=
                                                    i10;
                                            client.previousClickMouseY += i10;
                                        }
                                        if (tileY + dragOffsetY
                                                + 32 > Rasterizer2D.Rasterizer2D_yClipEnd - 17
                                                && rsInterface.scrollPosition < rsInterface.scrollMax
                                                - rsInterface.height) {
                                            int j10 = (client.tickDelta
                                                    * ((tileY + dragOffsetY
                                                    + 32)
                                                    - (Rasterizer2D.Rasterizer2D_yClipEnd - 17)))
                                                    / 3;
                                            if (j10 > client.tickDelta * 10)
                                                j10 = client.tickDelta * 10;
                                            if (j10 > rsInterface.scrollMax
                                                    - rsInterface.height
                                                    - rsInterface.scrollPosition)
                                                j10 = rsInterface.scrollMax
                                                        - rsInterface.height
                                                        - rsInterface.scrollPosition;
                                            rsInterface.scrollPosition +=
                                                    j10;
                                            client.previousClickMouseY -= j10;
                                        }
                                    }
                                } else {
                                    if (dragOffsetX < 5
                                            && dragOffsetX > -5)
                                        dragOffsetX = 0;
                                    if (dragOffsetY < 5
                                            && dragOffsetY > -5)
                                        dragOffsetY = 0;
                                    bindX = tileX + dragOffsetX;
                                    if (bindX < Rasterizer2D.Rasterizer2D_xClipStart) {
                                        bindX = Rasterizer2D.Rasterizer2D_xClipStart - (dragOffsetX);
                                        if (dragOffsetX < Rasterizer2D.Rasterizer2D_xClipStart)
                                            bindX = Rasterizer2D.Rasterizer2D_xClipStart;
                                    }
                                    if (bindX > Rasterizer2D.Rasterizer2D_xClipEnd - 32) {
                                        bindX = Rasterizer2D.Rasterizer2D_xClipEnd - 32;
                                    }
                                    bindY = tileY + dragOffsetY;
                                    if (bindY < Rasterizer2D.Rasterizer2D_yClipStart && rsInterface.scrollPosition == 0) {
                                        bindY = Rasterizer2D.Rasterizer2D_yClipStart - (dragOffsetY);
                                        if (dragOffsetY < Rasterizer2D.Rasterizer2D_yClipStart)
                                            bindY = Rasterizer2D.Rasterizer2D_yClipStart;
                                    }
                                    if (bindY > Rasterizer2D.Rasterizer2D_yClipEnd - 32)
                                        bindY = Rasterizer2D.Rasterizer2D_yClipEnd - 32;

                                    if (tileY + dragOffsetY < Rasterizer2D.Rasterizer2D_yClipStart
                                            && rsInterface.scrollPosition > 0) {
                                        int i10 = (client.tickDelta
                                                * (Rasterizer2D.Rasterizer2D_yClipStart
                                                - tileY
                                                - dragOffsetY))
                                                / 3;
                                        if (i10 > client.tickDelta * 10)
                                            i10 = client.tickDelta * 10;
                                        if (i10 > rsInterface.scrollPosition)
                                            i10 = rsInterface.scrollPosition;
                                        rsInterface.scrollPosition -=
                                                i10;
                                        client.previousClickMouseY += i10;
                                        bindY = Rasterizer2D.Rasterizer2D_yClipStart;
                                    }
                                    if (tileY + dragOffsetY
                                            + 32 > Rasterizer2D.Rasterizer2D_yClipEnd
                                            && rsInterface.scrollPosition < rsInterface.scrollMax
                                            - rsInterface.height) {
                                        int j10 = (client.tickDelta
                                                * ((tileY + dragOffsetY
                                                + 32)
                                                - Rasterizer2D.Rasterizer2D_yClipEnd))
                                                / 3;
                                        if (j10 > client.tickDelta * 10)
                                            j10 = client.tickDelta * 10;
                                        if (j10 > rsInterface.scrollMax
                                                - rsInterface.height
                                                - rsInterface.scrollPosition)
                                            j10 = rsInterface.scrollMax
                                                    - rsInterface.height
                                                    - rsInterface.scrollPosition;
                                        rsInterface.scrollPosition +=
                                                j10;
                                        client.previousClickMouseY -= j10;
                                    }
                                    item_icon.drawSprite1(bindX, bindY);
                                }
                            } else if (client.atInventoryInterfaceType != 0
                                    && client.atInventoryIndex == item
                                    && client.atInventoryInterface == childInterface.id
                                    && client.atInventoryItemId == itemId) {
                                bindX = tileX + dragOffsetX;
                                bindY = tileY;
                                item_icon.drawSprite1(tileX,
                                        tileY);
                            } else {
                                bindX = tileX + dragOffsetX;
                                bindY = tileY;
                                if (itemOpacity != 256) {
                                    item_icon.drawTransparentSprite(tileX, tileY, itemOpacity);
                                } else {
                                    item_icon.drawSprite(tileX, tileY);
                                }
                            }
                            if (item_icon.maxWidth == 33 || itemAmountsArray[item] != 1) {
                                boolean drawItemAmounts = true;

                                if (childInterface.id == 42752 || (childInterface.id >= 22035 && childInterface.id <= 22043)
                                        || childInterface.id == 41042 || childInterface.id == 41107 || childInterface.id == 36250
                                        || ((childInterface.id >= 64628 && childInterface.id <= 64845) && itemAmountsArray[item] == 0)) {
                                    drawItemAmounts = false;
                                }

                                if (drawItemAmounts) {
                                    int amount = itemAmountsArray[item];
                                    boolean bankTabContainer = childInterface.id >= Bank.CONTAINER_START && childInterface.id < Bank.CONTAINER_START + Bank.TOTAL_TABS
                                            || childInterface.id >= Bank.SEARCH_CONTAINER_START && childInterface.id < Bank.SEARCH_CONTAINER_START + Bank.TOTAL_TABS;
                                    if (amount >= 1500000000 && childInterface.drawInfinity) {
                                        SpriteLoader.getSprite(105).drawSprite(tileX, tileY);
                                    } else if (amount == 0 && bankTabContainer) { // Bank placeholder text
                                        client.newSmallFont.drawBasicString(ClientUtil.intToKOrMil(amount), bindX, bindY + 9, 0xFFFF00, 1, 120);
                                    } else {
                                        client.smallText.render(0, ClientUtil.intToKOrMil(amount), bindY + 10, bindX + 1); // Shadow
                                        if (amount >= 0)
                                            client.smallText.render(0xFFFF00, ClientUtil.intToKOrMil(amount), bindY + 9, bindX);
                                        if (amount >= 100000)
                                            client.smallText.render(0xFFFFFF, ClientUtil.intToKOrMil(amount), bindY + 9, bindX);
                                        if (amount >= 10000000)
                                            client.smallText.render(0x00FF80, ClientUtil.intToKOrMil(amount), bindY + 9, bindX);
                                    }
                                }
                            }
                        }
                    }
                } else if (childInterface.sprites != null && item < 20) {
                    Sprite image = SpriteLoader.getSprite(childInterface.sprites, item);
                    if (image != null)
                        image.drawSprite(tileX, tileY);
                }
                item++;
            }
        }
    }

    public static void handleChatArea(Client client) {

        if (client.backDialogueId == -1) {

            /*
             * Chat scroll bar dragging, note: it is upside down
             */
            client.chatBoxWidget.scrollPosition = ChatBox.chatScrollMax - ChatBox.chatBoxScrollPosition - client.getChatBoxHeight();

            final int chatBarXOffset = 496;
            final int chatBarYOffset = (ClientUI.frameMode == Client.ScreenMode.FIXED ? 344 : ClientUI.frameHeight - 159 - client.resizeChatOffset());

            if (MouseHandler.x >= chatBarXOffset && MouseHandler.x <= 512
                    && MouseHandler.y > chatBarYOffset
                    && ChatBox.chatScrollMax > client.getChatBoxHeight()) {

                handleWidgetScrollBarDragging(client, client.chatBoxWidget, client.getChatBoxHeight(), chatBarXOffset, 0, MouseHandler.x,
                        MouseHandler.y - chatBarYOffset,
                        ChatBox.chatScrollMax);
            }
            int i = ChatBox.chatScrollMax - client.getChatBoxHeight() - client.chatBoxWidget.scrollPosition;
            if (i < 0) {
                i = 0;
            }
            if (i > ChatBox.chatScrollMax - client.getChatBoxHeight()) {
                i = ChatBox.chatScrollMax - client.getChatBoxHeight();
            }
            if (ChatBox.chatBoxScrollPosition != i) {
                ChatBox.chatBoxScrollPosition = i;
                ChatBox.setUpdateChatbox(true);
            }
        }
        if (client.backDialogueId != -1) {
            boolean flag2 = false;

            try {
                flag2 = client.processWidgetAnimations(client.tickDelta, client.backDialogueId);
            } catch (Exception ex) {
                Log.error("Failed to process widget animations, backDialogueId = "+ client.backDialogueId, ex);
            }

            if (flag2) {
                ChatBox.setUpdateChatbox(true);
            }
        }
        if (client.atInventoryInterfaceType == 3)
            ChatBox.setUpdateChatbox(true);
        if (client.itemDragType == Widget.ITEM_DRAG_TYPE_BACK_DIALOGUE)
            ChatBox.setUpdateChatbox(true);
        if (client.clickToContinueString != null)
            ChatBox.setUpdateChatbox(true);
        if (client.menuOpen && GameFrame.menuScreenArea == 2)
            ChatBox.setUpdateChatbox(true);
    }

    public static void handleWidgetScrollBarDragging(Client client, Widget widget, int widgetHeight, int xOffset, int yOffset, int mouseX, int mouseY, int scrollMax) {
        int anInt992;
        if (client.draggingScrollBar)
            anInt992 = 32;
        else
            anInt992 = 0;
        client.draggingScrollBar = false;

        int dragAmount;

        if(MouseHandler.currentButton == 1) {
            if (mouseX >= xOffset && mouseX < xOffset + 16 && mouseY >= yOffset && mouseY < yOffset + 16) {
                widget.scrollPosition -= 4;//anInt1213 * 4;
            } else if (mouseX >= xOffset && mouseX < xOffset + 16 && mouseY >= (yOffset + widgetHeight) - 16 && mouseY < yOffset + widgetHeight) {
                widget.scrollPosition += 4;//anInt1213 * 4;
            } else if (mouseX >= xOffset - anInt992 && mouseX < xOffset + 16 + anInt992 && mouseY >= yOffset + 16 && mouseY < (yOffset + widgetHeight) - 16/* && anInt1213 > 0*/) {
                dragAmount = ((widgetHeight - 32) * widgetHeight) / scrollMax;
                if (dragAmount < 8)
                    dragAmount = 8;
                int i2 = mouseY - yOffset - 16 - dragAmount / 2;
                int j2 = widgetHeight - 32 - dragAmount;
                widget.scrollPosition = ((scrollMax - widgetHeight) * i2) / j2;
                client.draggingScrollBar = true;
            }
        }
    }

    public static void processMenuActions(Client client, int action, int arg0, int arg1, int arg2, long tag, String string) {


        if (client.inputDialogState != 0 && client.inputDialogState != 3 && !client.isSearchOpen()) {
            client.inputDialogState = 0;
            ChatBox.setUpdateChatbox(true);
        }

        ButtonSounds.handleButton(client, arg2);

        if (action >= 2000)
            action -= 2000;

        if (action == MenuOpcodes.TOGGLE_EMOJI_MENU || action == MenuOpcodes.TOGGLE_RUN) {
            client.playSound(2266);
            ChatBox.setUpdateChatbox(true);
        }

        if(action == MenuOpcodes.SELECT_EMOJI)
            ChatBox.setUpdateChatbox(true);

        if(action == MenuOpcodes.PUNISH_PLAYER){
            client.sendPacket(new Command("punish "+arg0).create());
            return;
        }
        if(action == MenuOpcodes.EDIT_OBJECT){
            final int objectId = DynamicObject.get_object_key(tag);
            client.sendPacket(new Command("tobj "+objectId+" "+arg1+" "+arg2).create());
            return;
        }
        if(action == MenuOpcodes.TOGGLE_BUTTON){
            client.sendPacket(new Command("tbut "+ arg0).create());
            return;
        }

        // Bank search containers, use the values of our actual containers instead of the search containers
        if (Bank.showSearchContainers() && Widget.interfaceCache[arg2] != null && Widget.interfaceCache[arg2].contentType == 206) {
            arg1 = Bank.getActualSlot(arg2, arg1);
            arg2 -= Bank.TOTAL_TABS;
        }

        if (action == MenuOpcodes.OPEN_NEWS) {
            client.newsflash = false;
            client.playSound(2266);
            client.sendPacket(new ClickButton(697).create());
            return;
        }
        
        if (action == MenuOpcodes.OPEN_BROADCAST_URL && !BroadCastManager.broadcastLink.isEmpty()
                && ClientCompanion.openInterfaceId == -1) {
            MiscUtils.launchURL(BroadCastManager.broadcastLink);
            return;
        }
        if (GameFrame.handleAction(client, action, arg2))
            return;

        // Placeholder releasing
        if (action == MenuOpcodes.RELEASE_PLACEHOLDER) {
            if (client.menuOpen) {
                client.sendPacket(new ItemContainerOption1(arg2, arg1, arg0).create());
            } else {
                GameFrame.determineMenuSize(client);
            }
        }

        try {
            if (action == MenuOpcodes.DROPDOWN) {
                Widget d = Widget.interfaceCache[arg2];
                Widget p = Widget.interfaceCache[arg0];
                if (!d.dropdown.isOpen()) {
                    if (p.dropdownOpen != null) {
                        p.dropdownOpen.dropdown.setOpen(false);
                    }
                    p.dropdownOpen = d;
                } else {
                    p.dropdownOpen = null;
                }
                d.dropdown.setOpen(!d.dropdown.isOpen());
            } else if (action == 770) {
                final Widget d = Widget.interfaceCache[arg2];
                final Widget p = Widget.interfaceCache[arg0];
                final DropdownMenu dropdownMenu = p.dropdown;
                final String[] options = dropdownMenu.getOptions();
                if (arg1 >= options.length)
                    return;
                dropdownMenu.setSelected(options[arg1]);
                dropdownMenu.setOpen(false);
                dropdownMenu.getDrop().selectOption(arg1, p);
                d.dropdownOpen = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ColorPicker.handleAction(client, arg2, action);


        // Resize chat
        if (action == MenuOpcodes.RESIZE_CHAT_AREA) {
            ChatBox.resizeChatArea();
        }

        // button clicks
        switch (action) {

            case 1315:
            case 1316:
            case 1317:
            case 1318:
            case 1319:
            case 1320:
            case 1321:
            case 879:
            case MenuOpcodes.OPEN_WORLD_MAP:
            case 475:
            case MenuOpcodes.TOGGLE_XP_LOCK:
            case MenuOpcodes.TOGGLE_RUN:
                // button click
                client.sendPacket(new ClickButton(action).create());
                break;
        }

        // HP Orb button
        if (action == MenuOpcodes.TOGGLE_HEALTH_ABOVE_HEAD && Configuration.enablePrayerEnergyWorldOrbs) { // Toggle HP above
            // heads
            client.playSound(2266);
            Configuration.hpAboveHeads = !Configuration.hpAboveHeads;
            return;
        }

        // Prayer Orb button
        if (action == MenuOpcodes.TOGGLE_QUICK_PRAYERS && Configuration.enablePrayerEnergyWorldOrbs) {
            GameFrameInput.clickedPrayerOrb = !GameFrameInput.clickedPrayerOrb;
            client.playSound(2266);
            client.sendPacket(new ClickButton(1500).create());
            return;
        }

        // Run Orb button
        if (action == MenuOpcodes.TOGGLE_RUN) {
            client.settings[429] = client.settings[429] == 0 ? 1 : 0;
        }

        if (action == MenuOpcodes.TOGGLE_XP_DROPS) {
            client.playSound(2266);
            Configuration.xpCounterOpen = !Configuration.xpCounterOpen;
            // Reset the xp drops array on closing the exp counter
            if (!Configuration.xpCounterOpen) {
                XpDrops.reset();
            }
            PlayerSettings.savePlayerData(client);
            return;
        } else if (action == MenuOpcodes.SET_XP_DROPS) {
            client.playSound(2266);
            client.sendPacket(new ClickButton(257).create());
            return;
        }

        if (action == MenuOpcodes.SET_AUTO_CAST) {
            Widget widget = Widget.interfaceCache[arg2];
            client.sendPacket(new ClickButton(widget.id).create());
        }

        if (action == MenuOpcodes.USE_ITEM_ON_NPC) {
            Npc npc = client.npcs[arg0];
            if (npc != null) {
                client.doWalkTo(2, npc.pathY[0], npc.pathX[0]);
                client.crossX = MouseHandler.lastPressedX;
                client.crossY = MouseHandler.lastPressedY;
                client.crossType = 2;
                client.crossIndex = 0;
                client.sendPacket(new ItemOnNpc(client.usedItemId, arg0, client.usedItemInventorySlot, client.usedItemInventoryInterfaceId).create());
            }
        }

        if (action == MenuOpcodes.PICKUP_ITEM_ON_GROUND) {
            boolean canReach = client.doWalkTo(2, arg2, arg1);
            if (!canReach)
                canReach = client.doWalkTo(2, arg2, arg1);
            client.crossX = MouseHandler.lastPressedX;
            client.crossY = MouseHandler.lastPressedY;
            client.crossType = 2;
            client.crossIndex = 0;
            client.sendPacket(new PickupItem(arg2 + client.baseY, arg0, arg1 + client.baseX).create());
        }

        if (action == MenuOpcodes.USE_ITEM_ON_OBJECT && client.clickObject(tag, arg2, arg1)) {
            final int objectId = DynamicObject.get_object_key(tag);
            client.sendPacket(
                    new ItemOnObject(client.usedItemInventoryInterfaceId, objectId, arg2 + client.baseY, client.usedItemInventorySlot, arg1 + client.baseX, client.usedItemId)
                            .create());
        }

        if (action == MenuOpcodes.USE_ITEM__ON__ITEM_ON_GROUND) {
            boolean canReach = client.doWalkTo(2, arg2,
                    arg1);
            if (!canReach)
                canReach = client.doWalkTo(2, arg2, arg1);
            client.crossX = MouseHandler.lastPressedX;
            client.crossY = MouseHandler.lastPressedY;
            client.crossType = 2;
            client.crossIndex = 0;
            // item on ground item
            client.sendPacket(
                    new ItemOnGroundItem(client.usedItemInventoryInterfaceId, client.usedItemId, arg0, arg2 + client.baseY, client.usedItemInventorySlot, arg1 + client.baseX)
                            .create());
        }

        // item option 1
        if (action == MenuOpcodes.ITEM_ACTION_1) {
            client.sendPacket(new UseItem(arg2, arg0, arg1).create());
            openInventory(client, arg0, arg1, arg2);
        }

        // widget action
        if (action == MenuOpcodes.SELECT_CHILD) {
            Widget widget = Widget.interfaceCache[arg2];
            if (widget.openMenuButton && !client.menuOpen) {
                GameFrame.determineMenuSize(client);
            } else {
                boolean flag8 = true;

                if (widget.type == Widget.TYPE_CONFIG || widget.id == 50007) { // Placeholder
                    // toggle
                    widget.active = !widget.active;
                } else if (widget.type == Widget.TYPE_CONFIG_HOVER) {
                    Widget.handleConfigHover(widget);
                }

                if (widget.contentType > 0)
                    flag8 = client.promptUserForInput(widget);
                if (flag8) {

                    // Button config timers (deposit buttons in bank)
                    if (widget.timerSpeed > 0) {
                        client.setButtonConfig(Widget.interfaceCache[arg2].valueIndexArray[0][1], 1);
                        Widget.interfaceCache[arg2].timerStartTime = System.currentTimeMillis();
                    }

                    SettingsWidget.settings(arg2);
                    SettingsWidget.advancedSettings(arg2);

                    switch (arg2) {
                        case 81025:
                            Achievements.achievementCompletionTime = ClientCompanion.ACHIEVEMENT_COMPLETION_HEIGHT / ClientCompanion.ACHIEVEMENT_COMPLETION_SPEED;
                            client.sendPacket(new ClickButton(arg2).create());
                            break;
                        case Widget.WILDERNESS_BUTTON:
                        case Widget.TRAINING_BUTTON:
                        case Widget.MINIGAME_BUTTON:
                        case Widget.SKILLING_BUTTON:
                        case Widget.CITIES_BUTTON:
                        case Widget.BOSSES_BUTTON:
                        case Widget.SLAYER_BUTTON:
                            Widget.Navigation nav = Widget.Navigation.forButton(arg2);

                            client.sendString(StringUtils.capitalizeFirst(nav.name()) + " Teleports", 47003);

                            int interfaceId = nav.getInterfaceId();
                            client.resetAnimation(interfaceId);
                            if (client.overlayInterfaceId != -1) {
                                client.overlayInterfaceId = -1;
                                ClientCompanion.tabAreaAltered = true;
                            }
                            if (client.backDialogueId != -1) {
                                client.backDialogueId = -1;
                                ChatBox.setUpdateChatbox(true);
                            }
                            if (client.inputDialogState != 0) {
                                client.inputDialogState = 0;
                                ChatBox.setUpdateChatbox(true);
                            }
                            ClientCompanion.openInterfaceId = interfaceId;
                            client.continuedDialogue = false;
                            break;
                        case Broadcast.ADD_BUTTON_ID:
                            Broadcast.addBroadcast();
                            break;
                        case ChangePassword.CONFIRM_BUTTON:
                            ChangePassword.sendValidationRequestToServer();
                            break;
                        case SettingsWidget.CHAT_EFFECTS:
                            Configuration.enableChatEffects = !Configuration.enableChatEffects;
                            client.sendConfiguration(171, Configuration.enableChatEffects ? 1 : 0);
                            PlayerSettings.savePlayerData(client);
                            break;
                        case SettingsWidget.SPLIT_PRIVATE_CHAT:
                            Configuration.enableSplitPrivate = !Configuration.enableSplitPrivate;
                            client.sendConfiguration(287, Configuration.enableSplitPrivate ? 1 : 0);
                            PlayerSettings.savePlayerData(client);
                            break;
                        case SettingsWidget.MOUSE_BUTTONS:
                            client.sendConfiguration(170, client.settings[170] == 1 ? 0 : 1);
                            PlayerSettings.savePlayerData(client);
                            break;
                        case SettingsWidget.ACCEPT_AID:
                            Configuration.acceptAid = !Configuration.acceptAid;
                            client.sendConfiguration(427, Configuration.acceptAid ? 1 : 0);
                            PlayerSettings.savePlayerData(client);
                            break;
                        case 19144:
                            client.inventoryOverlay(15106, 3213);
                            client.resetAnimation(15106);
                            ChatBox.setUpdateChatbox(true);
                            break;

                        case Keybinding.RESTORE_DEFAULT:
                            Keybinding.restoreDefault();
                            Keybinding.updateInterface();
                            client.sendMessage("Default keys loaded.", ChatBox.CHAT_TYPE_ALL, "");
                            PlayerSettings.savePlayerData(client);
                            break;
                        case Keybinding.ESCAPE_CONFIG:
                            Configuration.escapeCloseInterface = !Configuration.escapeCloseInterface;
                            Widget.interfaceCache[Keybinding.ESCAPE_CONFIG].active = Configuration.escapeCloseInterface;
                            PlayerSettings.savePlayerData(client);
                            break;

                        case Bank.SEARCH_BUTTON:
                            if (!Bank.isSearching()) {
                                Bank.openSearch();
                            } else {
                                Bank.closeSearch();
                            }
                            break;

                        case Shop.SEARCH_BUTTON:
                            if (!Shop.isSearching()) {
                                Shop.openSearch();
                            } else {
                                Shop.closeSearch();
                            }
                            break;

                        case ColorPicker.OK_BUTTON_ID:
                            ColorPicker.confirmSelection();
                            break;

                        case YellCustomizer.SAVE_CHANGES_BUTTON_ID:
                            client.sendPacket(new YellEdit(Widget.interfaceCache[YellCustomizer.INPUT_ID].getDefaultText(),
                                    Widget.interfaceCache[YellCustomizer.BRACKET_COLOR_BOX_ID].textColor,
                                    Widget.interfaceCache[YellCustomizer.BRACKET_SHADOW_COLOR_BOX_ID].textColor,
                                    Widget.interfaceCache[YellCustomizer.TITLE_COLOR_BOX_ID].textColor,
                                    Widget.interfaceCache[YellCustomizer.TITLE_SHADOW_COLOR_BOX_ID].textColor,
                                    Widget.interfaceCache[YellCustomizer.NAME_COLOR_BOX_ID].textColor,
                                    Widget.interfaceCache[YellCustomizer.NAME_SHADOW_COLOR_BOX_ID].textColor,
                                    Widget.interfaceCache[YellCustomizer.MESSAGE_COLOR_BOX_ID].textColor,
                                    Widget.interfaceCache[YellCustomizer.MESSAGE_SHADOW_COLOR_BOX_ID].textColor).create());
                            break;

                        case ItemColorCustomizer.SAVE_COLOR_BUTTON_ID:
                            ItemColorCustomizer.saveColor();
                            break;
                        case ItemColorCustomizer.DISCARD_COLOR_BUTTON_ID:
                            ItemColorCustomizer.discardColor();
                            break;

                        /* Faster spec bars toggle **/
                        case 29138:
                        case 29038:
                        case 29063:
                        case 29113:
                        case 29163:
                        case 29188:
                        case 29213:
                        case 29238:
                        case 30007:
                        case 48023:
                        case 33033:
                        case 30108:
                        case 7473:
                        case 7562:
                        case 7487:
                        case 7788:
                        case 8481:
                        case 7612:
                        case 7587:
                        case 7662:
                        case 7462:
                        case 7548:
                        case 7687:
                        case 7537:
                        case 7623:
                        case 12322:
                        case 7637:
                        case 12311:
                        case 155:

                            /*
                              Just update the color of the bar before sending packet,
                              to make it look smoother
                             */
                            WeaponInterface wepInterface = WeaponInterface.get(ClientCompanion.tabInterfaceIDs[0]);
                            if (wepInterface != null && wepInterface.getSpecialMeter() > 0) {
                                boolean active = Widget.interfaceCache[wepInterface.getSpecialMeter()].getDefaultText()
                                        .contains("@yel@");
                                if (active) {
                                    Widget.interfaceCache[wepInterface
                                            .getSpecialMeter()].setDefaultText(Widget.interfaceCache[wepInterface
                                            .getSpecialMeter()].getDefaultText().replaceAll("@yel@", "@bla@"));
                                } else {
                                    Widget.interfaceCache[wepInterface
                                            .getSpecialMeter()].setDefaultText(Widget.interfaceCache[wepInterface
                                            .getSpecialMeter()].getDefaultText().replaceAll("@bla@", "@yel@"));
                                }
                            }

                            client.sendPacket(new SpecialAttack(arg2).create());
                            break;

                        default:
                            // System.out.println("BUTTON = "+button);
                            client.sendPacket(new ClickButton(arg2).create());
                            break;
                    }
                }
            }
        }

        // player option
        if (action == 561) {
            Player player = client.players[arg0];
            if (player != null) {
                client.doWalkTo(2, player.pathY[0],
                        player.pathX[0]);
                client.crossX = MouseHandler.lastPressedX;
                client.crossY = MouseHandler.lastPressedY;
                client.crossType = 2;
                client.crossIndex = 0;
                ClientCompanion.playerOptionActionCount += arg0;
                if (ClientCompanion.playerOptionActionCount >= 90) {
                    // (anti-cheat)
                    // outgoing.writeOpcode(136);
                    ClientCompanion.playerOptionActionCount = 0;
                }
                client.sendPacket(new FirstPlayerAction(arg0).create());
            }
        }

        // npc option 1
        if (action == 20) {
            Npc npc = client.npcs[arg0];
            if (npc != null) {


                client.doWalkTo(2, npc.pathY[0], npc.pathX[0]);

                client.crossX = MouseHandler.lastPressedX;
                client.crossY = MouseHandler.lastPressedY;
                client.crossType = 2;
                client.crossIndex = 0;
                // npc action 1
                client.sendPacket(new NpcOption1(arg0).create());
            }
        }

        // player option 2
        if (action == 779) {
            Player player = client.players[arg0];
            if (player != null) {
                client.doWalkTo(2, player.pathY[0],
                        player.pathX[0]);
                client.crossX = MouseHandler.lastPressedX;
                client.crossY = MouseHandler.lastPressedY;
                client.crossType = 2;
                client.crossIndex = 0;
                // player option 2
                client.sendPacket(new SecondPlayerAction(arg0).create());
            }
        }

        // clicking tiles
        if (action == MenuOpcodes.TELEPORT_OR_WALK_HERE) {
            if (!client.menuOpen) {
                client.scene.clickTile(MouseHandler.lastPressedY - 4, MouseHandler.lastPressedX - 4, Client.plane);
            } else {
                client.scene.clickTile(arg2 - 4, arg1 - 4, Client.plane);
            }
        }

        // object option 5
        if (action == 1062) {
            ClientCompanion.objectOption5ActionCount += client.baseX;
            if (ClientCompanion.objectOption5ActionCount >= 113) {
                // validates clicking object option 4
                // outgoing.writeOpcode(183);
                // outgoing.writeTriByte(0xe63271);
                ClientCompanion.objectOption5ActionCount = 0;
            }
            client.clickObject(tag, arg2, arg1);

            // object option 5
            client.sendPacket(new ObjectOption5(DynamicObject.get_object_key(tag), arg1 + client.baseX, arg2 + client.baseY).create());
        }

        // continue dialogue
        if (action == MenuOpcodes.CONTINUE_DIALOGUE && !client.continuedDialogue) {
            client.sendPacket(new NextDialogue(arg2).create());
            client.continuedDialogue = true;
        }

        // Pressed button
        if (action == 647) {
            // Bank tab buttons
            if (Bank.isBankTabButton(arg2, false) && arg1 == 0) {
                int tab = arg2 - (Bank.TAB_BUTTON_START);
                if (!Arrays.equals(Widget.interfaceCache[arg2].actions, new String[]{"New tab"}))
                    Bank.setCurrentTab(tab);
            }

            client.sendPacket(new ClickButtonAction(arg2, arg1).create());
        }

        // using bank all option of the bank interface
        if (action == MenuOpcodes.ITEM_CONTAINER_ACTION_4) {
            client.sendPacket(new ItemContainerOption4(arg1, arg2, arg0).create());
            if (ClientCompanion.openInterfaceId != Bank.INTERFACE_ID)
                openInventory(client, arg0, arg1, arg2);
        }

        // All but one bank option
        if (action == MenuOpcodes.ITEM_CONTAINER_ACTION_6) {
            client.sendPacket(new ItemContainerOption6(arg1, arg2, arg0).create());
            if (ClientCompanion.openInterfaceId != Bank.INTERFACE_ID)
                openInventory(client, arg0, arg1, arg2);
        }

        if (action == MenuOpcodes.BANK_MODIFY_AMOUNT) {
            client.sendPacket(new ItemContainerOption7(arg1, arg2, arg0).create());
            if (ClientCompanion.openInterfaceId != Bank.INTERFACE_ID)
                openInventory(client, arg0, arg1, arg2);
        }

        if (action == MenuOpcodes.ADD_PLACEHOLDER) {
            client.sendPacket(new ItemContainerOption8(arg1, arg2, arg0).create());
            if (ClientCompanion.openInterfaceId != Bank.INTERFACE_ID)
                openInventory(client, arg0, arg1, arg2);
        }

        if (action == MenuOpcodes.ADD_FRIEND || action == MenuOpcodes.ADD_IGNORE || action == MenuOpcodes.REMOVE_FRIEND || action == MenuOpcodes.REMOVE_IGNORE) {
            String nameString;
            int indexOf = string.indexOf("@whi@");
            if (indexOf != -1) {
                nameString = string.substring(indexOf + 5);

                int last = nameString.lastIndexOf(">");
                if (last != -1) {
                    nameString = nameString.substring(last + 1);
                }

                long usernameHash = StringUtils.encodeBase37(nameString.trim());
                if (action == MenuOpcodes.ADD_FRIEND) client.addFriend(usernameHash);
                if (action == MenuOpcodes.ADD_IGNORE) client.addIgnore(usernameHash);
                if (action == MenuOpcodes.REMOVE_FRIEND) client.removeFriend(usernameHash);
                if (action == MenuOpcodes.REMOVE_IGNORE) client.removeIgnore(usernameHash);
            }
        }

        if (action == MenuOpcodes.ITEM_CONTAINER_ACTION_5) {
            client.sendPacket(new ItemContainerOption5(arg1, arg2, arg0).create());
            if (ClientCompanion.openInterfaceId != Bank.INTERFACE_ID) {
                openInventory(client, arg0, arg1, arg2);
            }
        }

        if (action == MenuOpcodes.ITEM_ACTION_3) {
            client.sendPacket(new ItemOption3(arg0, arg1, arg2).create());
            openInventory(client, arg0, arg1, arg2);
        }
        if (action == 484 || action == 6) {

            int indexOf = string.indexOf("@whi@");
            if (indexOf != -1) {
                string = string.substring(indexOf + 5).trim();
                String username = StringUtils.formatText(StringUtils.decodeBase37(StringUtils.encodeBase37(string)));
                boolean flag9 = false;
                for (int count = 0; count < client.Players_count; count++) {
                    Player player = client.players[client.Players_indices[count]];
                    if (player == null || player.name == null || !player.name.equalsIgnoreCase(username)) {
                        continue;
                    }
                    client.doWalkTo(2, player.pathY[0],
                            player.pathX[0]);

                    // accepting trade
                    if (action == 484) {
                        // sendPacket(new ChatboxTrade(playerList[count]));
                        client.sendPacket(new FourthPlayerAction(client.Players_indices[count]).create());
                    }

                    // accepting a challenge
                    if (action == 6) {
                        ClientCompanion.playerOptionActionCount += arg0;
                        if (ClientCompanion.playerOptionActionCount >= 90) {
                            // (anti-cheat)
                            // outgoing.writeOpcode(136);
                            ClientCompanion.playerOptionActionCount = 0;
                        }

                        client.sendPacket(new ChatboxDuel(client.Players_indices[count]).create());
                    }
                    flag9 = true;
                    break;
                }

                if (!flag9)
                    client.sendMessage("Unable to find " + username, ChatBox.CHAT_TYPE_ALL, "!");
            }
        }

        if (action == MenuOpcodes.USE_ITEM_ON_ITEM) {
            client.sendPacket(new ItemOnItem(arg1, client.usedItemInventorySlot, arg0, client.usedItemInventoryInterfaceId, client.usedItemId, arg2).create());
            openInventory(client, arg0, arg1, arg2);
        }

        // Using the drop option of an item
        if (action == MenuOpcodes.ITEM_ACTION_5) {
            // Drop item
            client.sendPacket(new DropItem(arg0, arg2, arg1).create());
            openInventory(client, arg0, arg1, arg2);
        }
        // useable spells
        if (action == 626) {
            Widget widget = Widget.interfaceCache[arg2];
            client.spellSelected = 1;
            ClientCompanion.spellId = widget.id;
            client.selectedSpellId = arg2;
            client.spellUsableOn = widget.spellUsableOn;
            client.itemSelected = 0;
            String actionName = widget.selectedActionName;
            if (actionName.contains(" "))
                actionName = actionName.substring(0, actionName.indexOf(" "));
            String s8 = widget.selectedActionName;
            if (s8.contains(" "))
                s8 = s8.substring(s8.indexOf(" ") + 1);
            client.spellTooltip = actionName + " " + widget.spellName + " " + s8;
            if (client.spellUsableOn == 16) {
                ClientCompanion.tabId = 3;
                ClientCompanion.tabAreaAltered = true;
            }
            return;
        }

        // Using the bank 5 option on a bank widget
        if (action == 78) {
            // bank 5
            client.sendPacket(new ItemContainerOption2(arg2, arg0, arg1).create());
            if (ClientCompanion.openInterfaceId != Bank.INTERFACE_ID) {
                openInventory(client, arg0, arg1, arg2);
            }
        }

        // player option 2
        if (action == 27) {
            Player player = client.players[arg0];
            if (player != null) {
                client.doWalkTo(2, player.pathY[0],
                        player.pathX[0]);
                client.crossX = MouseHandler.lastPressedX;
                client.crossY = MouseHandler.lastPressedY;
                client.crossType = 2;
                client.crossIndex = 0;
                ClientCompanion.playerOption2ActionCount += arg0;
                if (ClientCompanion.playerOption2ActionCount >= 54) {
                    // (anti-cheat)
                    // outgoing.writeOpcode(189);
                    // outgoing.writeByte(234);
                    ClientCompanion.playerOption2ActionCount = 0;
                }
                // attack player
                client.sendPacket(new ThirdPlayerAction(arg0).create());
            }
        }

        // Used for lighting logs
        if (action == 213) {
            boolean flag3 = client.doWalkTo(2, arg2,
                    arg1);
            if (!flag3)
                flag3 = client.doWalkTo(2, arg2, arg1);
            client.crossX = MouseHandler.lastPressedX;
            client.crossY = MouseHandler.lastPressedY;
            client.crossType = 2;
            client.crossIndex = 0;
            // light item
            /*
             * outgoing.writeOpcode(79); outgoing.writeLEShort(button +
             * regionBaseY); outgoing.writeShort(clicked);
             * outgoing.writeShortA(first + regionBaseX);
             */
        }

        // Using the unequip option on the equipment tab interface
        if (action == MenuOpcodes.ITEM_CONTAINER_ACTION_1) {
            client.sendPacket(new ItemContainerOption1(arg2, arg1, arg0).create());
            openInventory(client, arg0, arg1, arg2);
        }

        if (action == 1004) {
            if (ClientCompanion.tabInterfaceIDs[10] != -1) {
                ClientCompanion.tabId = 10;
                ClientCompanion.tabAreaAltered = true;
            }
        }
        GameFrame.handleChatBoxAction(client, action);

        if (action == MenuOpcodes.ITEM_ACTION_4) {
            client.sendPacket(new ItemOption2(arg2, arg1, arg0).create());
            openInventory(client, arg0, arg1, arg2);
        }

        // clicking some sort of tile
        if (action == 652) {
            boolean canReach = client.doWalkTo(2, arg2,
                    arg1);
            if (!canReach)
                canReach = client.doWalkTo(2, arg2, arg1);
            client.crossX = MouseHandler.lastPressedX;
            client.crossY = MouseHandler.lastPressedY;
            client.crossType = 2;
            client.crossIndex = 0;
        }

        if (action == MenuOpcodes.USE_SPELL_ON_GROUND_ITEM) {
            boolean canReach = client.doWalkTo(2, arg2, arg1);
            if (!canReach)
                canReach = client.doWalkTo(2, arg2, arg1);
            client.crossX = MouseHandler.lastPressedX;
            client.crossY = MouseHandler.lastPressedY;
            client.crossType = 2;
            client.crossIndex = 0;
            // magic on ground item
            client.sendPacket(new MagicOnGroundItem(arg2 + client.baseY, arg0, arg1 + client.baseX, client.selectedSpellId).create());
        }
        if (action == MenuOpcodes.SET_CONFIG) {
            boolean configFlag = false; // flag for preventing config button from changing config
            TitleChooser.handleButton(arg2);
            /*
              Additional client side button actions
             */
            switch (arg2) {
/*                case QuestTab.MEMBER_TAB_BUTTON:
                    if (!client.isHighStaff() && !client.isMember()) {
                        configFlag = true;
                        client.sendMessage("<img=745>@red@ This tab is only available to players with the members rank.");
                    }
                    break;*/
                case QuestTab.STAFF_TAB_BUTTON:
                    if (!client.isStaff()) {
                        configFlag = true;
                        client.sendMessage("<img=743>@red@ This tab is only available to staff ranked members.");
                    }
                    break;
                case GameModeSetup.NONE_BUTTON:
                case GameModeSetup.CLASSIC_BUTTON:
                case GameModeSetup.PURE_BUTTON:
                case GameModeSetup.MASTER_BUTTON:
                case GameModeSetup.SPAWN_BUTTON:
                    GameModeSetup.setEquipment(GameModeSetup.IronmanEquip.NONE);
                    break;
                case GameModeSetup.ONE_LIFE_BUTTON:
                    GameModeSetup.setEquipment(GameModeSetup.IronmanEquip.ONE_LIFE);
                    break;
                case GameModeSetup.REALISM_BUTTON:
                    GameModeSetup.setEquipment(GameModeSetup.IronmanEquip.REALISM);
                    break;

                case GameModeSetup.STANDARD_BUTTON:
                    GameModeSetup.setEquipment(GameModeSetup.IronmanEquip.STANDARD);
                    break;
                case GameModeSetup.HARDCORE_BUTTON:
                    GameModeSetup.setEquipment(GameModeSetup.IronmanEquip.HARDCORE);
                    break;
                case GameModeSetup.ULTIMATE_BUTTON:
                    GameModeSetup.setEquipment(GameModeSetup.IronmanEquip.ULTIMATE);
                    break;

                case Spellbooks.SORT_LEVEL_ORDER_BUTTON_ID:
                case Spellbooks.SORT_COMBAT_SPELL_BUTTON_ID:
                case Spellbooks.SORT_TELEPORT_SPELL_BUTTON_ID:
                    Configuration.sortSpellsState = (arg2 - Spellbooks.SORT_LEVEL_ORDER_BUTTON_ID) / 3;
                    Spellbooks.update(Spellbooks.Spellbook.forId(Spellbooks.getCurrentBookId()));
                    PlayerSettings.savePlayerData(client);
                    break;
            }

            /*
              Client side button action or send packet
             */
            switch (arg2) {
/*                case 930:
                    Client.cameraZoom = 1200;
                    break;
                case 931:
                    Client.cameraZoom = 800;
                    break;
                case 932:
                    Client.cameraZoom = 400;
                    break;
                case 933:
                    Client.cameraZoom = 200;
                    break;
                case 934:
                    Client.cameraZoom = 0;
                    break;*/
                default:
                    client.sendPacket(new ClickButton(arg2).create());
                    break;
            }

            if (!configFlag) {
                Widget widget = Widget.interfaceCache[arg2];
                if (widget.valueIndexArray != null && widget.valueIndexArray[0][0] == 5) {
                    int i2 = widget.valueIndexArray[0][1];
                    if (client.settings[i2] != widget.requiredValues[0]) {
                        client.settings[i2] = widget.requiredValues[0];
                        client.updateVarp(i2);
                    }
                }
            }
        }

        // Using the 2nd option of an npc
        if (action == 225) {
            Npc npc = client.npcs[arg0];
            if (npc != null) {
                client.doWalkTo(2, npc.pathY[0],
                        npc.pathX[0]);
                client.crossX = MouseHandler.lastPressedX;
                client.crossY = MouseHandler.lastPressedY;
                client.crossType = 2;
                client.crossIndex = 0;
                ClientCompanion.npcOption2ActionCount += arg0;
                if (ClientCompanion.npcOption2ActionCount >= 85) {
                    // (anti-cheat)
                    // outgoing
                    // .writeOpcode(230);
                    // outgoing.writeByte(239);
                    ClientCompanion.npcOption2ActionCount = 0;
                }
                // npc option 2
                client.sendPacket(new NpcOption2(arg0).create());
            }
        }

        // Using the 3rd option of an npc
        if (action == 965) {
            Npc npc = client.npcs[arg0];
            if (npc != null) {
                client.doWalkTo(2, npc.pathY[0],
                        npc.pathX[0]);
                client.crossX = MouseHandler.lastPressedX;
                client.crossY = MouseHandler.lastPressedY;
                client.crossType = 2;
                client.crossIndex = 0;
                ClientCompanion.npcOption3ActionCount++;
                if (ClientCompanion.npcOption3ActionCount >= 96) {
                    // (anti-cheat)
                    // outgoing.writeOpcode(152);
                    // outgoing.writeByte(88);
                    ClientCompanion.npcOption3ActionCount = 0;
                }
                // npc option 3
                client.sendPacket(new NpcOption3(arg0).create());
            }
        }

        // Using a spell on an npc
        if (action == 413) {
            Npc npc = client.npcs[arg0];
            if (npc != null) {
                client.doWalkTo(2, npc.pathY[0],
                        npc.pathX[0]);
                client.crossX = MouseHandler.lastPressedX;
                client.crossY = MouseHandler.lastPressedY;
                client.crossType = 2;
                client.crossIndex = 0;
                // magic on npc
                client.sendPacket(new MagicOnNpc(arg0, client.selectedSpellId).create());
            }
        }

        if (action == 956) {
            //System.out.println("arg0: " + arg0 + " - arg1: " + arg1 + " - arg2: " + arg2);
            //arg0 - objectId
            //arg1 - objectX
            //arg2 - objectY
            client.sendPacket(new MagicOnObject(arg0, arg1, arg2, client.selectedSpellId).create());
        }

        // Close open interfaces
        if (action == 200) {
            if (arg2 == 34206 && client.spinnerSpinning) {
                client.sendMessage("You can not close the Mystery Box interface during a spin.");
            } else {
                client.clearTopInterfaces();
            }
        }

        if (action == MenuOpcodes.EXAMINE_NPC) {
            Npc npc = client.npcs[arg0];
            if (npc != null) {
                NpcDefinition entityDef = npc.desc;
                if (entityDef.transforms != null)
                    entityDef = entityDef.transform();
                if (entityDef != null) {
                    if (Client.instance.isHighStaff()) {
                        client.sendPacket(new ExamineOrEditNpc(arg0).create());
                    } else
                        client.sendPacket(new ExamineOrEditNpc(entityDef.id).create());
                }
            }
        }

        if (action == 900) {
            client.clickObject(tag, arg2, arg1);
            // object option 2
            client.sendPacket(new ObjectOption2(DynamicObject.get_object_key(tag), arg2 + client.baseY, arg1 + client.baseX).create());
        }

        // Using the "Attack" option on a npc
        if (action == 412) {
            Npc npc = client.npcs[arg0];
            if (npc != null) {
                client.doWalkTo(2, npc.pathY[0],
                        npc.pathX[0]);
                client.crossX = MouseHandler.lastPressedX;
                client.crossY = MouseHandler.lastPressedY;
                client.crossType = 2;
                client.crossIndex = 0;
                client.sendPacket(new AttackNpc(arg0).create());
            }
        }

        // Using spells on a player
        if (action == 365) {
            Player player = client.players[arg0];
            if (player != null) {
                client.doWalkTo(2, player.pathY[0],
                        player.pathX[0]);
                client.crossX = MouseHandler.lastPressedX;
                client.crossY = MouseHandler.lastPressedY;
                client.crossType = 2;
                client.crossIndex = 0;
                // spells on plr
                client.sendPacket(new MagicOnPlayer(arg0, client.selectedSpellId).create());
            }
        }


        // Using the 4th option of a player
        if (action == 577) {
            Player player = client.players[arg0];
            if (player != null) {
                client.doWalkTo(2, player.pathY[0],
                        player.pathX[0]);
                client.crossX = MouseHandler.lastPressedX;
                client.crossY = MouseHandler.lastPressedY;
                client.crossType = 2;
                client.crossIndex = 0;
                // trade request
                client.sendPacket(new FourthPlayerAction(arg0).create());
            }
        }
        // Using the fifth Option of a player
        if (action == 729) {
            Player player = client.players[arg0];
            if (player != null) {
                client.doWalkTo(2, player.pathY[0],
                        player.pathX[0]);
                client.crossX = MouseHandler.lastPressedX;
                client.crossY = MouseHandler.lastPressedY;
                client.crossType = 2;
                client.crossIndex = 0;
                client.sendPacket(new FifthPlayerAction(arg0).create());
            }
        }

        // Using a spell on an item
        if (action == 956 && client.clickObject(tag, arg2, arg1)) {
            // magic on item
            // sendPacket(new MagicOnItem(first + regionBaseX, anInt1137, button
            // + regionBaseY, get_object_key(objectUid)));
        }

        // Some walking action (packet 23)
        if (action == 567) {
            boolean flag6 = client.doWalkTo(2, arg2,
                    arg1);
            if (!flag6)
                flag6 = client.doWalkTo(2, arg2, arg1);
            client.crossX = MouseHandler.lastPressedX;
            client.crossY = MouseHandler.lastPressedY;
            client.crossType = 2;
            client.crossIndex = 0;
        }

        // Using the bank 10 option on the bank interface
        if (action == 867) {

            if ((arg0 & 3) == 0)
                ClientCompanion.bank10ActionCount++;

            if (ClientCompanion.bank10ActionCount >= 59)
                ClientCompanion.bank10ActionCount = 0;

            // bank 10
            client.sendPacket(new ItemContainerOption3(arg2, arg0, arg1).create());
            if (ClientCompanion.openInterfaceId != Bank.INTERFACE_ID)
                openInventory(client, arg0, arg1, arg2);
        }

        // Withdraw All but one
        if (action == 291) {
            client.sendPacket(new ItemContainerOption6(arg2, arg0, arg1).create());
            if (ClientCompanion.openInterfaceId != Bank.INTERFACE_ID)
                openInventory(client, arg0, arg1, arg2);
        }

        // Using a spell on an inventory item
        if (action == MenuOpcodes.USE_SPELL_ON_ITEM) {
            client.sendPacket(new MagicOnInventoryItem(arg1, arg0, arg2, client.selectedSpellId).create());
            openInventory(client, arg0, arg1, arg2);
        }


        if (action == 606) {
            client.sendMessage("Coming soon.");
        }

        // Using an inventory item on a player
        if (action == 491) {
            Player player = client.players[arg0];

            if (player != null) {
                client.doWalkTo(2, player.pathY[0],
                        player.pathX[0]);
                client.crossX = MouseHandler.lastPressedX;
                client.crossY = MouseHandler.lastPressedY;
                client.crossType = 2;
                client.crossIndex = 0;
                // TODO item on player
                client.sendPacket(new ItemOnPlayer(client.usedItemInventoryInterfaceId, arg0, client.usedItemId, client.usedItemInventorySlot).create());
            }
        }

        // reply to private message
        if (action == 639) {

            int indexOf = string.indexOf("@whi@");

            if (indexOf != -1) {
                long usernameHash = StringUtils.encodeBase37(string.substring(indexOf + 5).trim());
                int resultIndex = -1;
                for (int friendIndex = 0; friendIndex < PlayerRelations.friendsCount; friendIndex++) {
                    if (PlayerRelations.friendsListAsLongs[friendIndex] != usernameHash) {
                        continue;
                    }
                    resultIndex = friendIndex;
                    break;
                }

                if (resultIndex != -1 && PlayerRelations.friendsNodeIDs[resultIndex] > 0) {
                    ChatBox.setUpdateChatbox(true);
                    client.inputDialogState = 0;
                    client.messagePromptRaised = true;
                    client.promptInput = "";
                    client.friendsListAction = 3;
                    client.encodedPrivateMessageRecipientName = PlayerRelations.friendsListAsLongs[resultIndex];
                    client.privateMessageRecipientName = PlayerRelations.friendsList[resultIndex];
                    //client.inputPromptTitle = "Enter a message to send to " + PlayerRelations.friendsList[resultIndex];
                    client.inputPromptTitle = "Enter a message to send to " + PlayerRelations.friendsList[resultIndex];
                }
            }
        }

        // Using the equip option of an item in the inventory
        if (action == 454) {
            // equip item
            client.sendPacket(new EquipItem(arg0, arg1, arg2).create());
            openInventory(client, arg0, arg1, arg2);
        }

        // Npc option 4
        if (action == 478) {
            Npc npc = client.npcs[arg0];
            if (npc != null) {
                client.doWalkTo(2, npc.pathY[0],
                        npc.pathX[0]);
                client.crossX = MouseHandler.lastPressedX;
                client.crossY = MouseHandler.lastPressedY;
                client.crossType = 2;
                client.crossIndex = 0;

                if ((arg0 & 3) == 0) {
                    ClientCompanion.npcOption4ActionCount++;
                }

                if (ClientCompanion.npcOption4ActionCount >= 53) {
                    ClientCompanion.npcOption4ActionCount = 0;
                }

                // npc option 4
                client.sendPacket(new NpcOption4(arg0).create());
            }
        }

        // Object option 3
        if (action == 113) {
            client.clickObject(tag, arg2, arg1);
            // object option 3
            client.sendPacket(new ObjectOption3(arg1 + client.baseX, arg2 + client.baseY, DynamicObject.get_object_key(tag)).create());
        }

        // Object option 4
        if (action == 872) {
            client.clickObject(tag, arg2, arg1);
            client.sendPacket(new ObjectOption4(arg1 + client.baseX, DynamicObject.get_object_key(tag), arg2 + client.baseY).create());
        }

        // Object option 1
        if (action == 502) {
            client.clickObject(tag, arg2, arg1);
            client.sendPacket(new ObjectOption1(arg1 + client.baseX, DynamicObject.get_object_key(tag), arg2 + client.baseY).create());
        }

        if (action == 169) {

            switch (arg2) {
                case Bank.SHOW_MENU_BUTTON:
                    // Bank "Show menu"  button
                    Bank.closeSearch();
                    break;

                case Spellbooks.FILTER_BUTTON_ID:
                    int state = client.settings[Widget.interfaceCache[arg2].valueIndexArray[0][1]];
                    ClientCompanion.tabInterfaceIDs[6] = state == 0 ? Spellbooks.FILTER_PANEL_ID : Spellbooks.currentBookId;
                    break;
                case Spellbooks.FILTER_COMBAT_BUTTON_ID:
                    Configuration.filterCombatSpells = !Configuration.filterCombatSpells;
                    Spellbooks.update(Spellbooks.Spellbook.forId(Spellbooks.currentBookId));
                    PlayerSettings.savePlayerData(client);
                    break;
                case Spellbooks.FILTER_TELEPORT_BUTTON_ID:
                    Configuration.filterTeleportSpells = !Configuration.filterTeleportSpells;
                    Spellbooks.update(Spellbooks.Spellbook.forId(Spellbooks.currentBookId));
                    PlayerSettings.savePlayerData(client);
                    break;
                case Spellbooks.FILTER_UTILITY_BUTTON_ID:
                    Configuration.filterUtilitySpells = !Configuration.filterUtilitySpells;
                    Spellbooks.update(Spellbooks.Spellbook.forId(Spellbooks.currentBookId));
                    PlayerSettings.savePlayerData(client);
                    break;
                case Spellbooks.FILTER_LEVEL_BUTTON_ID:
                    Configuration.filterLevel = !Configuration.filterLevel;
                    Spellbooks.update(Spellbooks.Spellbook.forId(Spellbooks.currentBookId));
                    PlayerSettings.savePlayerData(client);
                    break;
                case Spellbooks.FILTER_RUNES_BUTTON_ID:
                    Configuration.filterRunes = !Configuration.filterRunes;
                    Spellbooks.update(Spellbooks.Spellbook.forId(Spellbooks.currentBookId));
                    PlayerSettings.savePlayerData(client);
                    break;

                default:
                    client.sendPacket(new ClickButton(arg2).create());
                    break;
            }

            if (arg2 != 22845 && arg2 != 65035
                    && arg2 != 60038
                    && arg2 != 60039
                    && arg2 != 60040
                    && arg2 != 60041
                    && arg2 != 60042
            ) { // Exclude config buttons that are handled server-sided
                Widget widget = Widget.interfaceCache[arg2];

                int parent = widget.parent;
                if (parent != 5608) {
                    if (widget.valueIndexArray != null && widget.valueIndexArray[0][0] == 5) {
                        int setting = widget.valueIndexArray[0][1];
                        client.settings[setting] = 1 - client.settings[setting];
                        client.updateVarp(setting);
                    }
                }
            }

        }

        if (action == MenuOpcodes.USE_ITEM) {
            client.itemSelected = 1;
            client.usedItemInventorySlot = arg1;
            client.usedItemInventoryInterfaceId = arg2;
            client.usedItemId = arg0;
            client.selectedItemName = ItemDefinition.lookup(arg0).name;
            client.spellSelected = 0;
            return;
        }

        if (action == MenuOpcodes.EXAMINE_OBJECT) {
            if (OSObjectDefinition.USE_OSRS) {
                client.sendMessage("Not implemented yet, must be handled server sided :(", ChatBox.CHAT_TYPE_ALL, "Stan");
            } else {
                ObjectDefinition definition = ObjectDefinition.lookup((int) tag);
                String message;
                if (definition.description != null)
                    message = definition.description;
                else
                    message = "It's a " + definition.name + ".";
                client.sendMessage(message, ChatBox.CHAT_TYPE_ALL, "");
            }
        }

        // Click First Option Ground Item
        if (action == 244) {
            boolean flag7 = client.doWalkTo(2, arg2,
                    arg1);
            if (!flag7)
                flag7 = client.doWalkTo(2, arg2, arg1);
            client.crossX = MouseHandler.lastPressedX;
            client.crossY = MouseHandler.lastPressedY;
            client.crossType = 2;
            client.crossIndex = 0;
            client.sendPacket(new SecondGroundItemOption(arg2 + client.baseY, arg0, arg1 + client.baseX).create());
        }

        if (action == 1448 || action == MenuOpcodes.EXAMINE_ITEM) {
            ItemDefinition definition = ItemDefinition.lookup(arg0);

            if (definition != null) {
                if (action == 1448 && (arg0 == 995 || arg0 == 13307)) {
                    Item groundItem = client.getGroundItem();

                    if (groundItem != null) {
                        if (groundItem.itemCount > 99999) {
                            client.sendMessage(MiscUtils.insertCommasToNumber(groundItem.itemCount) + " x " + definition.name, ChatBox.CHAT_TYPE_ALL, "");
                        } else {
                            client.sendMessage("Lovely money!", ChatBox.CHAT_TYPE_ALL, "");
                        }
                    }
                } else {
                    client.sendPacket(new ExamineItem(arg0, arg2).create());
                }
            }
        }

        client.itemSelected = 0;
        client.spellSelected = 0;

    }

    private static void openInventory(Client client, int itemId, int containerItemIndex, int containerInterfaceId) {
        client.atInventoryLoopCycle = 0;
        client.atInventoryInterface = containerInterfaceId;
        client.atInventoryIndex = containerItemIndex;
        client.atInventoryItemId = itemId;
        client.atInventoryInterfaceType = 2;
        if (Widget.interfaceCache[containerInterfaceId].parent == ClientCompanion.openInterfaceId)
            client.atInventoryInterfaceType = 1;
        if (Widget.interfaceCache[containerInterfaceId].parent == client.backDialogueId)
            client.atInventoryInterfaceType = 3;
    }

    public static boolean handleText(Client client, Widget rsInterface, int _x, int currentY, Widget childInterface) {
        GameFont textDrawingArea = childInterface.textDrawingAreas;
        String text = childInterface.getDefaultText();
        if (text == null) {
            return true;
        }

        boolean flag1 = client.previousBackDialogueChildWidgetHoverType == childInterface.id || client.previousChildWidgetHoverType2 == childInterface.id
                || client.previousChildWidgetHoverType == childInterface.id;
        int colour;
        if (client.interfaceIsSelected(childInterface)) {
            colour = childInterface.secondaryColor;
            if (flag1 && childInterface.secondaryHoverColor != 0)
                colour = childInterface.secondaryHoverColor;
            if (childInterface.secondaryText.length() > 0)
                text = childInterface.secondaryText;
        } else {
            colour = childInterface.textColor;
            if (flag1 && childInterface.defaultHoverColor != 0)
                colour = childInterface.defaultHoverColor;
        }
        if (childInterface.atActionType == Widget.OPTION_CONTINUE && client.continuedDialogue) {
            text = "Please wait...";
            colour = childInterface.textColor;
        }
        if (Rasterizer2D.Rasterizer2D_width == 519) {
            if (colour == 0xffff00)
                colour = 255;
            if (colour == 49152)
                colour = 0xffffff;
        }
        if (ClientUI.frameMode != Client.ScreenMode.FIXED) {
            if ((client.backDialogueId != -1 || client.dialogueId != -1
                    || childInterface.getDefaultText().contains("Click here to continue"))
                    && (rsInterface.id == client.backDialogueId || rsInterface.id == client.dialogueId)) {
                if (colour == 0xffff00) {
                    colour = 255;
                }
                if (colour == 49152) {
                    colour = 0xffffff;
                }
            }
        }
        if ((childInterface.parent == 1151) || (childInterface.parent == 12855)) {
            switch (colour) {
                case 16773120:
                    colour = 0xFE981F;
                    break;
                case 7040819:
                    colour = 0xAF6A1A;
                    break;
            }
        }

        int image = -1;
        final String INITIAL_MESSAGE = text;
        /*if (text.toLowerCase().contains("<img=")) { // THis is handled by the font......
            int prefix = text.indexOf("<img=");
            int suffix = text.indexOf(">", prefix);
            if (suffix != -1) {
                try {
                    image = Integer.parseInt(text.substring(prefix + 5, suffix));
                    text = text.replaceAll(text.substring(prefix + 5, suffix), "");
                    text = text.replaceAll("</img>", "");
                    text = text.replaceAll("<img=>", "");
                } catch (NumberFormatException | IllegalStateException nfe) {
                    text = INITIAL_MESSAGE;
                }
            }
        }*/

        for (int drawY = currentY + textDrawingArea.verticalSpace; text
                .length() > 0; drawY += textDrawingArea.verticalSpace) {

            /*if (image != -1 && image < SpriteLoader.getSpriteCount()) {

                // CLAN CHAT LIST = 37128
                if (childInterface.parent == 37128) {
                    SpriteLoader.getSprite(image).drawAdvancedSprite(_x, drawY - SpriteLoader.getSprite(image).myHeight - 1);
                    _x += SpriteLoader.getSprite(image).myWidth + 3;
                } else {
                    SpriteLoader.getSprite(image).drawAdvancedSprite(_x, drawY - SpriteLoader.getSprite(image).myHeight + 3);
                    _x += SpriteLoader.getSprite(image).myWidth + 4;
                }
            }*/

            if (text.contains("%")) {
                do {
                    int index = text.indexOf("%1");
                    if (index == -1)
                        break;
                    if (childInterface.id < 4000 || childInterface.id > 5000 && childInterface.id != 13921
                            && childInterface.id != 13922 && childInterface.id != 12171
                            && childInterface.id != 12172) {
                        text = text.substring(0, index) + StringUtils.formatCoins(client.executeScript(childInterface, 0))
                                + text.substring(index + 2);

                    } else {
                        text = text.substring(0, index)
                                + ClientUtil.interfaceIntToString(client.executeScript(childInterface, 0))
                                + text.substring(index + 2);

                    }
                } while (true);
                do {
                    int index = text.indexOf("%2");
                    if (index == -1) {
                        break;
                    }
                    text = text.substring(0, index) + ClientUtil.interfaceIntToString(client.executeScript(childInterface, 1))
                            + text.substring(index + 2);
                } while (true);
                do {
                    int index = text.indexOf("%3");

                    if (index == -1) {
                        break;
                    }

                    text = text.substring(0, index) + ClientUtil.interfaceIntToString(client.executeScript(childInterface, 2))
                            + text.substring(index + 2);
                } while (true);
                do {
                    int index = text.indexOf("%4");

                    if (index == -1) {
                        break;
                    }
                    text = text.substring(0, index) + ClientUtil.interfaceIntToString(client.executeScript(childInterface, 3))
                            + text.substring(index + 2);
                } while (true);
                do {
                    int index = text.indexOf("%5");

                    if (index == -1) {
                        break;
                    }

                    text = text.substring(0, index) + ClientUtil.interfaceIntToString(client.executeScript(childInterface, 4))
                            + text.substring(index + 2);
                } while (true);
            }

            int line = text.indexOf("\\n");

            String drawn;

            if (line != -1) {
                drawn = text.substring(0, line);
                text = text.substring(line + 2);
            } else {
                drawn = text;
                text = "";
            }
            RSFont font = null;
            if (textDrawingArea == client.smallText) {
                font = client.newSmallFont;
            } else if (textDrawingArea == client.regularText) {
                font = client.newRegularFont;
            } else if (textDrawingArea == client.boldText) {
                font = client.newBoldFont;
            } else if (textDrawingArea == client.fancyFont) {
                font = client.newFancyFont;
            } else if (textDrawingArea == client.fancyFontLarge) {
                font = client.newFancyFontLarge;
            }
            if (font != null) {
                if (childInterface.centerText) {
                    if (TitleChooser.isComponent(childInterface.id) && drawn.equals("Locked")) {
                        font.drawCenteredString(drawn, _x + childInterface.width / 2, drawY, colour,
                                childInterface.textShadow ? 0 : -1, 120);
                    } else {
                        font.drawCenteredString(drawn, _x + childInterface.width / 2, drawY, colour,
                                childInterface.textShadow ? 0 : -1);
                    }
                } else if (childInterface.rightAlignedText) {
                    font.drawRightAlignedString(drawn, _x, drawY, colour,
                            childInterface.textShadow ? 0 : -1);
                } else if (childInterface.rollingText) {
                    font.drawRollingText(drawn, _x, drawY, colour, childInterface.textShadow ? 0 : -1);
                } else {
                    font.drawBasicString(drawn, _x, drawY, colour, childInterface.textShadow ? 0 : -1);
                }
            }
        }
        return false;
    }
}
