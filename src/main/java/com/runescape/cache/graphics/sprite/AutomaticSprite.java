package com.runescape.cache.graphics.sprite;

public enum AutomaticSprite {

    BACKGROUND_BLACK(SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites, 5), SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites, 0),
            SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites, 5), SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites, 6), SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites, 8), SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites, 7),
            SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites, 1), SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites, 2), SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites, 3), SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites, 4), false, 0),
    BACKGROUND_BROWN(SpriteLoader.getSprite(881), SpriteLoader.getSprite(882),
            SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites2, 1), SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites2, 4), SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites2, 3), SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites2, 2),
            SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites2, 6), SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites2, 5), SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites2, 7), SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites2, 0), true, 1),
    BACKGROUND_BIG_BROWN(null, SpriteLoader.getSprite(882),
            SpriteLoader.getSprite(883), SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites2, 4), SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites2, 3), SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites2, 2),
            SpriteLoader.getSprite(884), SpriteLoader.getSprite(885), SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites2, 7), SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites2, 0), false, 1),

    BUTTON_GREY(SpriteLoader.getSprite(955),
            SpriteLoader.getSprite(956), SpriteLoader.getSprite(957), SpriteLoader.getSprite(958), SpriteLoader.getSprite(959),
            SpriteLoader.getSprite(960), SpriteLoader.getSprite(961), SpriteLoader.getSprite(962), SpriteLoader.getSprite(963), 3),
    BUTTON_RED(SpriteLoader.getSprite(964),
            SpriteLoader.getSprite(965), SpriteLoader.getSprite(966), SpriteLoader.getSprite(967), SpriteLoader.getSprite(968),
            SpriteLoader.getSprite(969), SpriteLoader.getSprite(970), SpriteLoader.getSprite(971), SpriteLoader.getSprite(972), 3),
    BUTTON_BROWN(null,
            SpriteLoader.getSprite(914), SpriteLoader.getSprite(915), SpriteLoader.getSprite(916), SpriteLoader.getSprite(917),
            SpriteLoader.getSprite(918), SpriteLoader.getSprite(919), SpriteLoader.getSprite(920), SpriteLoader.getSprite(921), 2),
    BUTTON_BROWN_HOVER(SpriteLoader.getSprite(938),
            SpriteLoader.getSprite(930), SpriteLoader.getSprite(931), SpriteLoader.getSprite(932), SpriteLoader.getSprite(933),
            SpriteLoader.getSprite(934), SpriteLoader.getSprite(935), SpriteLoader.getSprite(936), SpriteLoader.getSprite(937), 2),
    BUTTON_BIG_BROWN(null,
            SpriteLoader.getSprite(922), SpriteLoader.getSprite(923), SpriteLoader.getSprite(924), SpriteLoader.getSprite(925),
            SpriteLoader.getSprite(926), SpriteLoader.getSprite(927), SpriteLoader.getSprite(928), SpriteLoader.getSprite(929), 2),

    DIVIDER_VERTICAL_BLACK(SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites, 7), true),
    DIVIDER_VERTICAL_BROWN(SpriteLoader.getSprite(890), true),
    DIVIDER_VERTICAL_BIG_BROWN(SpriteLoader.getSprite(890), true),
    DIVIDER_VERTICAL_SMALL_BROWN(SpriteLoader.getSprite(1023), true),

    DIVIDER_HORIZONTAL_BLACK(SpriteLoader.getSprite(SpriteCompanion.autoBackgroundSprites, 5), false),
    DIVIDER_HORIZONTAL_BROWN(SpriteLoader.getSprite(881), false),
    DIVIDER_HORIZONTAL_BIG_BROWN(SpriteLoader.getSprite(881), false),
    DIVIDER_HORIZONTAL_SMALL_BROWN(SpriteLoader.getSprite(1024), false);

    private Sprite divider, background, topBorder, rightBorder, bottomBorder, leftBorder, topLeftCorner, topRightCorner, bottomLeftCorner, bottomRightCorner;
    private boolean dividerOverlaps;
    private int backgroundOffset;

    /**
     * Backgrounds
     */
    AutomaticSprite(Sprite divider, Sprite background, Sprite topBorder, Sprite rightBorder, Sprite bottomBorder, Sprite leftBorder, Sprite topLeftCorner, Sprite topRightCorner, Sprite bottomLeftCorner, Sprite bottomRightCorner, boolean dividerOverlaps, int backgroundOffset) {
        this.divider = divider;
        this.background = background;
        this.topBorder = topBorder;
        this.rightBorder = rightBorder;
        this.bottomBorder = bottomBorder;
        this.leftBorder = leftBorder;
        this.topLeftCorner = topLeftCorner;
        this.topRightCorner = topRightCorner;
        this.bottomLeftCorner = bottomLeftCorner;
        this.bottomRightCorner = bottomRightCorner;
        this.dividerOverlaps = dividerOverlaps;
        this.backgroundOffset = backgroundOffset;
    }

    /**
     * Buttons
     */
    AutomaticSprite(Sprite background, Sprite topBorder, Sprite rightBorder, Sprite bottomBorder, Sprite leftBorder, Sprite topLeftCorner, Sprite topRightCorner, Sprite bottomLeftCorner, Sprite bottomRightCorner, int backgroundOffset) {
        this.background = background;
        this.topBorder = topBorder;
        this.rightBorder = rightBorder;
        this.bottomBorder = bottomBorder;
        this.leftBorder = leftBorder;
        this.topLeftCorner = topLeftCorner;
        this.topRightCorner = topRightCorner;
        this.bottomLeftCorner = bottomLeftCorner;
        this.bottomRightCorner = bottomRightCorner;
        this.backgroundOffset = backgroundOffset;
    }

    /**
     * Dividers
     */
    AutomaticSprite(Sprite sprite, boolean vertical) {
        if (vertical)
            this.leftBorder = sprite;
        else
            this.topBorder = sprite;
    }

    public Sprite getDivider() {
        return divider;
    }

    public Sprite getBackground() {
        return background;
    }

    public Sprite getTopBorder() {
        return topBorder;
    }

    public Sprite getRightBorder() {
        return rightBorder;
    }

    public Sprite getBottomBorder() {
        return bottomBorder;
    }

    public Sprite getLeftBorder() {
        return leftBorder;
    }

    public Sprite getTopLeftCorner() {
        return topLeftCorner;
    }

    public Sprite getTopRightCorner() {
        return topRightCorner;
    }

    public Sprite getBottomLeftCorner() {
        return bottomLeftCorner;
    }

    public Sprite getBottomRightCorner() {
        return bottomRightCorner;
    }

    public boolean isDividerOverlaps() {
        return dividerOverlaps;
    }

    public int getBackgroundOffset() {
        return backgroundOffset;
    }

    public Sprite getBackground(int width, int height, boolean divider, int dividerY) {
        return generateSprite(width, height, dividerY, isDividerOverlaps(), getBackgroundOffset(), divider ? getDivider() : null, getBackground(), getTopBorder(), getRightBorder(), getBottomBorder(), getLeftBorder(), getTopLeftCorner(), getTopRightCorner(), getBottomLeftCorner(), getBottomRightCorner());
    }

    public Sprite getButton(int width, int height) {
        return generateSprite(width, height, 0, isDividerOverlaps(), getBackgroundOffset(), getDivider(), getBackground(), getTopBorder(), getRightBorder(), getBottomBorder(), getLeftBorder(), getTopLeftCorner(), getTopRightCorner(), getBottomLeftCorner(), getBottomRightCorner());
    }

    public Sprite getDivider(int val, boolean vertical) {
        return generateSprite(vertical ? getLeftBorder().myWidth : val, vertical ? val : getTopBorder().myHeight, 0, isDividerOverlaps(), getBackgroundOffset(), getDivider(), getBackground(), getTopBorder(), getRightBorder(), getBottomBorder(), getLeftBorder(), getTopLeftCorner(), getTopRightCorner(), getBottomLeftCorner(), getBottomRightCorner());
    }

    public static Sprite generateSprite(int width, int height, int dividerY, boolean dividerOverlaps, int backgroundOffset,
                                        Sprite divider, Sprite background,
                                        Sprite topBorder, Sprite rightBorder, Sprite bottomBorder, Sprite leftBorder,
                                        Sprite topLeftCorner, Sprite topRightCorner, Sprite bottomLeftCorner, Sprite bottomRightCorner) {

        int[][] pixels = new int[height][width];

        // Background
        if (background != null)
            fillPixels(pixels, background, 0, backgroundOffset, width, height - (backgroundOffset * 2));

        // Top border
        if (topBorder != null)
            fillPixels(pixels, topBorder, topRightCorner != null ? topRightCorner.myWidth : 0, 0, width - (topRightCorner != null ? topRightCorner.myWidth : 0), topBorder.myHeight);

        // Left border
        if (leftBorder != null)
            fillPixels(pixels, leftBorder, 0, topLeftCorner != null ? topLeftCorner.myHeight : 0, leftBorder.myWidth, height - (bottomLeftCorner != null ? bottomLeftCorner.myHeight : 0));

        // Right border
        if (rightBorder != null)
            fillPixels(pixels, rightBorder, width - rightBorder.myWidth, topRightCorner != null ? topRightCorner.myHeight : 0, width, height - (bottomRightCorner != null ? bottomRightCorner.myHeight : 0));

        // Bottom border
        if (bottomBorder != null)
            fillPixels(pixels, bottomBorder, bottomRightCorner != null ? bottomRightCorner.myWidth : 0, height - bottomBorder.myHeight, width - (bottomRightCorner != null ? bottomRightCorner.myWidth : 0), height);

        // Top left corner
        if (topLeftCorner != null)
            insertPixels(pixels, topLeftCorner, 0, 0, true);

        // Top right corner
        if (topRightCorner != null)
            insertPixels(pixels, topRightCorner, width - topRightCorner.myWidth, 0, true);

        // Bottom left corner
        if (bottomLeftCorner != null)
            insertPixels(pixels, bottomLeftCorner, 0, height - bottomLeftCorner.myHeight, true);

        // Bottom right corner
        if (bottomRightCorner != null)
            insertPixels(pixels, bottomRightCorner, width - bottomRightCorner.myWidth, height - bottomRightCorner.myHeight, true);

        // Divider
        if (divider != null)
            fillPixels(pixels, divider, leftBorder.myWidth - (dividerOverlaps ? 1 : 0), dividerY, width - rightBorder.myWidth + (dividerOverlaps ? 1 : 0), dividerY + divider.myHeight);

        return new Sprite(width, height, 0, 0, d2Tod1(pixels));
    }

    public static void insertPixels(int[][] pixels, Sprite image, int x, int y, boolean ignoreTransparency) {
        int[][] imagePixels = d1Tod2(image.myPixels, image.myWidth);

        for (int j = y; j < y + image.myHeight; j++) {
            for (int i = x; i < x + image.myWidth; i++) {
                if (ignoreTransparency && imagePixels[j - y][i - x] == 0)
                    continue;
                pixels[j][i] = imagePixels[j - y][i - x];
            }
        }
    }

    public static void fillPixels(int[][] pixels, Sprite image, int startX, int startY, int endX, int endY) {
        int[][] imagePixels = d1Tod2(image.myPixels, image.myWidth);

        for (int j = startY; j < endY; j++) {
            for (int i = startX; i < endX; i++) {
                pixels[j][i] = imagePixels[(j - startY) % image.myHeight][(i - startX) % image.myWidth];
            }
        }
    }

    public static int[] d2Tod1(int[][] array) {
        int[] newArray = new int[array.length * array[0].length];

        for (int i = 0; i < array.length; ++i)
            for (int j = 0; j < array[i].length; ++j) {
                newArray[i * array[0].length + j] = array[i][j];
            }

        return newArray;
    }

    public static int[][] d1Tod2(int[] array, int width) {
        int[][] newArray = new int[array.length / width][width];

        for (int i = 0; i < array.length; ++i) {
            newArray[i / width][i % width] = array[i];
        }

        return newArray;
    }

}