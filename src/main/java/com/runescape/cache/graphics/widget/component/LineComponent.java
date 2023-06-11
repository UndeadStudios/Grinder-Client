package com.runescape.cache.graphics.widget.component;

/**
 * Created by Stan van der Bend for Empyrean at 22/06/2018.
 *
 * @author https://www.rune-server.ee/members/StanDev/
 */
public class LineComponent extends ChildComponent {

    private final Orientation orientation;

    public LineComponent(int relativeX, int relativeY, Orientation orientation, int length) {
        super(relativeX, relativeY);
        this.type = 18;
        this.textColor = 0xFFFFFF;
        this.atActionType = 0;
        this.opacity = (byte) 256;
        this.contentType = 0;
        this.width = length;
        this.orientation = orientation;
    }

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }
}
