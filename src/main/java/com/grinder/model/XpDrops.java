package com.grinder.model;

import com.runescape.Client;
import com.grinder.Configuration;
import com.runescape.cache.graphics.RSFont;
import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.cache.graphics.sprite.SpriteLoader;
import com.runescape.cache.graphics.widget.Widget;
import com.runescape.draw.Rasterizer2D;
import com.grinder.ui.ClientUI;
import com.runescape.util.MiscUtils;
import com.runescape.util.StringUtils;
import com.grinder.client.ClientUtil;
import com.runescape.cache.graphics.sprite.SpriteCompanion;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XpDrops {

    /**
     * List of xp drops, each xp drop represented by an int array
     * [0] = skill
     * [1] = xp
     * [2] = startY
     * [3] = currentY
     */
    private static List<int[]> xpDrops = new ArrayList<>();

    private static final int XP_DROPS_Y = 158;
    private static final int TOTAL_LEVEL_ICON = 453;

    public static void reset() {
        xpDrops.clear();
    }

    public static void add(int skill, int xp) {
        if (xp <= 0)
            return;

        if (skill == 22) {
            skill = 21;
        }

        int nextY = getNextCurrentY();
        int[] drop = { 1 << skill, xp, nextY, nextY };

        if (Configuration.xpDropsGroup) {
            groupDrop(drop);
        } else {
            xpDrops.add(drop);
        }
    }

    /**
     * Gets the current y value to assign to the next xp drop that is added.
     * Prevents xp drops from overlapping.
     * @return
     */
    private static int getNextCurrentY() {
        int highestY = XP_DROPS_Y - getSpacing();

        for (int[] xpDrop : xpDrops) {
            highestY = Math.max(highestY, xpDrop[3]);
        }

        return highestY + getSpacing();
    }

    private static int getSpacing() {
        return Configuration.xpCounterSize > 0 ? 25 : 17;
    }

    private static void groupDrop(int[] dropToGroup) {
        for (int[] drop : xpDrops) {
            int difference = dropToGroup[3] - getSpacing() - drop[3];
            if (drop[3] >= XP_DROPS_Y && difference == 0) {
                drop[0] |= dropToGroup[0];
                drop[1] += dropToGroup[1];
                return;
            }
        }
        // If we didn't find a drop to group with then add the drop by itself
        xpDrops.add(dropToGroup);
    }

    public static void draw() {
        int drawX = getDrawX();
        int drawY = getDrawY();
        RSFont font = ClientUtil.getRSFont(Client.instance.boldText, Client.instance.fancyFont, Client.instance.fancyFontLarge, Client.instance.newBoldFont, Client.instance.newFancyFont, Client.instance.newFancyFontLarge, Client.instance.newRegularFont, Client.instance.newSmallFont, Client.instance.regularText, Client.instance.smallText, Widget.defaultFont[Configuration.xpCounterSize]);

        /*
         * Counter
         */

        int counterWidth;
        int counterHeight = 0;

        if (showCounter()) {
            counterWidth = getCounterWidth();
            counterHeight = getCounterHeight();
            int counterDrawX = drawX - getCounterDrawXOffset();

            Rasterizer2D.drawStylishBox(counterDrawX, drawY, counterWidth, counterHeight);

            if (showProgressBar()) {
                Rasterizer2D.drawHorizontalLine(counterDrawX + 1, drawY + counterHeight - 11, counterWidth - 2, Rasterizer2D.STYLISH_BOX_OUTLINE_COLOR);
                Rasterizer2D.drawBox(counterDrawX + 2, drawY + counterHeight - 10, counterWidth - 4, 8, 0);
                Rasterizer2D.drawBox(counterDrawX + 3, drawY + counterHeight - 9, getProgressBarWidth(counterWidth), 6, MiscUtils.mixColors(Color.GREEN, Color.RED, getProgressBarPercentage()));
            }

            String totalSkillXp;
            if (showCurrentSkillXp()) {
                Sprite icon = SpriteCompanion.bigSkillSprites[Client.instance.getCurrentSkill()];
                icon.drawSprite(counterDrawX + 2, drawY + ((29 - icon.myHeight) / 2) - icon.drawOffsetY);
                totalSkillXp = String.valueOf(Client.instance.currentExp[Client.instance.getCurrentSkill()]);
            } else {
                // Show total xp
                SpriteLoader.getSprite(TOTAL_LEVEL_ICON).drawAdvancedSprite(counterDrawX + 3, drawY + 3);
                totalSkillXp = String.valueOf(Client.instance.getTotalExp());
            }
            font.drawRightAlignedString(StringUtils.insertCommasToNumber(totalSkillXp), counterDrawX + counterWidth - 4, drawY + 18, 0xFFFFFF, 0);
        }

        /*
         * Drops
         */

        Rasterizer2D.Rasterizer2D_setClip(0, ClientUI.frameHeight, ClientUI.frameWidth, drawY + counterHeight);

        Iterator<int[]> iterator = xpDrops.iterator();
        while (iterator.hasNext()) {
            int[] drop = iterator.next();
            int skill = drop[0];
            int xp = drop[1];
            int startY = drop[2];
            int currentY = drop[3];

            boolean bigSprites = Configuration.xpCounterSize > 0;
            Sprite[] sprites = bigSprites ? SpriteCompanion.bigSkillSprites : SpriteCompanion.smallSkillSprites;
            ArrayList<Sprite> icons = new ArrayList<>();
            int dropWidth = 0;

            for (int i = 0; i < sprites.length; i++) {
                if((skill & (1 << i)) == 0)
                    continue;

                Sprite icon = sprites[i];
                icons.add(icon);
                if (bigSprites) {
                    dropWidth += 24;
                } else {
                    dropWidth += icon.myWidth + 3;
                }
            }
            if (bigSprites) {
                dropWidth += 3; // Add 3 extra to add spacing between the sprite and the text
                                // Big sprites have spacing between them based on their width and drawOffsetX but no explicit spacing like small sprites do
            }

            String xpText = StringUtils.insertCommasToNumber(String.valueOf(xp));
            dropWidth += font.getTextWidth(xpText) - (bigSprites ? 0 : 1);

            int dropDrawX = drawX - getDropDrawXOffset(dropWidth);
            int dropDrawY = drawY + currentY;

            if (startY - currentY >= startY - XP_DROPS_Y) {
                int iconOffset = 0;
                for (Sprite icon : icons) {
                    if (bigSprites) {
                        icon.drawSprite(dropDrawX + iconOffset, dropDrawY - 16);
                        iconOffset += 24;
                    } else {
                        icon.drawAdvancedSprite(dropDrawX + iconOffset, dropDrawY - 10 - ((icon.myHeight - 13) / 2));
                        iconOffset += icon.myWidth + 3;
                    }
                }
                font.drawRightAlignedString(xpText, dropDrawX + dropWidth, dropDrawY, Configuration.xpDropsColour, 0);
            }

            move(drop);

            if (currentY < -getSpacing()) {
                iterator.remove();
            }
        }

        Rasterizer2D.Rasterizer2D_resetClip();
    }

    public static boolean showCounter() {
        return Configuration.xpCounterType != 2;
    }

    private static int getDrawX() {
        int[] xValues = { ClientUI.frameMode == Client.ScreenMode.FIXED ? 510 : ClientUI.frameWidth - 226, (ClientUI.frameWidth - 253) / 2, 2 };
        return xValues[Configuration.xpDropsPosition];
    }

    private static int getDrawY() {
        int hideCounterOffset = !showCounter() ? 2 : 0;
        int[] yValues = { 2 - hideCounterOffset,
                Configuration.enableSkillOrbs && SkillOrbs.showingOrbs() && showCounter() ? 65 : 2 - hideCounterOffset,
                CombatBox.shouldDrawCombatBox(Client.instance.currentInteract) ? 60 - hideCounterOffset : 24};
        return yValues[Configuration.xpDropsPosition];
    }

    private static int getCounterDrawXOffset() {
        int[] xOffsets = { getCounterWidth(), getCounterWidth() / 2, 0 };
        return xOffsets[Configuration.xpDropsPosition];
    }

    private static int getDropDrawXOffset(int dropWidth) {
        int[] xOffsets = { dropWidth, dropWidth / 2, 0 };
        return xOffsets[Configuration.xpDropsPosition];
    }

    public static int getCounterWidth() {
        return Configuration.xpCounterSize * 10 + 119;
    }

    public static int getCounterHeight() {
        return showProgressBar() ? 38 : 29;
    }

    private static boolean showProgressBar() {
        return Configuration.xpCounterProgress == 1 && Client.instance.getCurrentSkill() != -1;
    }

    private static boolean showCurrentSkillXp() {
        return Configuration.xpCounterType == 1 && Client.instance.getCurrentSkill() != -1;
    }

    private static double getProgressBarPercentage() {
        double percentage = 1.0;
        int currentLevel = Client.instance.maximumLevels[Client.instance.getCurrentSkill()];

        if (currentLevel < 200) {
            try {
                int startXp = ClientUtil.getXPForLevel(currentLevel);
                int endXp = ClientUtil.getXPForLevel(currentLevel + 1);
                int gainedXp = Client.instance.currentExp[Client.instance.getCurrentSkill()] - startXp;
                int remainderXp = endXp - startXp; // the amount of xp between the start xp of the current level
                                                   // and the start xp of the next level, NOT the xp remaining to level up
                percentage = (double) gainedXp / (double) remainderXp;
            } catch (ArithmeticException e) {
                e.printStackTrace();
            }
        }

        return percentage;
    }

    private static int getProgressBarWidth(int counterWidth) {
        int fullProgressBarWidth = counterWidth - 6;
        return (int) (getProgressBarPercentage() * fullProgressBarWidth);
    }

    private static void move(int[] drop) {
        if (Configuration.xpDropsSpeed == 0) {
            drop[3] -= Client.tick % 2 == 0 ? 2 : 1;
        } else {
            drop[3] -= Configuration.xpDropsSpeed;
        }
    }

}
