package com.runescape.cache.graphics.widget.component;
import com.runescape.cache.graphics.widget.FontType;

import java.awt.*;

/**
 * Created by Stan van der Bend for Empyrean at 19/06/2018.
 *
 * @author https://www.rune-server.ee/members/StanDev/
 */
public class TextComponent extends ChildComponent {

    public TextComponent(FontType fontType, Color color) {
        this(fontType, null, color.getRGB(), false, false, 0, 0);
    }
    public TextComponent(FontType fontType, Color color, boolean centered) {
        this(fontType, null, color.getRGB(), centered, false, 0, 0);
    }
    public TextComponent(FontType fontType, Color color, boolean centered, boolean shadowed) {
        this(fontType, null, color.getRGB(), centered, shadowed, 0, 0);
    }
    public TextComponent(FontType fontType, Color color, boolean centered, boolean shadowed, int relativeX, int relativeY) {
        this(fontType, null, color.getRGB(), centered, shadowed, relativeX, relativeY);
    }
    public TextComponent(FontType fontType, String text, Color color, boolean centered, boolean shadowed, int relativeX, int relativeY) {
        this(fontType, text, color.getRGB(), centered, shadowed, relativeX, relativeY);
    }
    public TextComponent(FontType fontType, String text, int color, boolean centered, boolean shadowed, int relativeX, int relativeY) {
        super(relativeX, relativeY);
        this.textDrawingAreas = fontType.getRasterizer();
        this.type = 4;
        this.atActionType = 0;
        this.width = 0;
        this.height = 0;
        this.contentType = 0;
        this.opacity = 0;
        this.centerText = centered;
        this.textShadow = shadowed;
        this.setDefaultText(text);
        this.secondaryText = "";
        this.textColor = color;
    }

    public void setText(String text) {
        setDefaultText(text);
    }
}
