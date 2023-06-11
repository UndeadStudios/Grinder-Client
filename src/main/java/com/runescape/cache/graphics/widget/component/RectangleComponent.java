package com.runescape.cache.graphics.widget.component;

import java.awt.*;

/**
 * Created by Stan van der Bend for Empyrean at 19/06/2018.
 *
 * @author https://www.rune-server.ee/members/StanDev/
 */
public class RectangleComponent extends ChildComponent {


    public RectangleComponent(int width, int height) {
        this(0, 0, width, height, 0, 0xAFEEEE, true);
    }
    public RectangleComponent(int relativeX, int relativeY, int width, int height) {
        this(relativeX, relativeY, width, height, 0, 0xAFEEEE, true);
    }
    public RectangleComponent(int relativeX, int relativeY, int width, int height, int alpha) {
        this(relativeX, relativeY, width, height, alpha, 0xAFEEEE, true);
    }
    public RectangleComponent(int relativeX, int relativeY, int width, int height, int alpha, int color) {
        this(relativeX, relativeY, width, height, alpha, color, true);
    }
    public RectangleComponent(int relativeX, int relativeY, int width, int height, int alhpa, Color color, boolean filled) {
        super(relativeX, relativeY);
        this.textColor = color.getRGB();
        this.filled = filled;
        this.type = 3;
        this.atActionType = 0;
        this.contentType = 0;
        this.opacity = (byte) alhpa;
        this.width = width;
        this.height = height;
    }
    public RectangleComponent(int relativeX, int relativeY, int width, int height, int alhpa, int color, boolean filled) {
        super(relativeX, relativeY);
        this.textColor = color;
        this.filled = filled;
        this.type = 3;
        this.atActionType = 0;
        this.contentType = 0;
        this.opacity = (byte) alhpa;
        this.width = width;
        this.height = height;
    }
}
