package com.grinder.javafx;

import com.runescape.cache.graphics.sprite.Sprite;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;

import java.awt.image.BufferedImage;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 21/12/2019
 */
public class FXUtil {

    public static JFXPanel fxPanel;

    static {
        System.out.println("init");
    }
    public static void createPanel() {
        fxPanel = new JFXPanel();
    }

    public static javafx.scene.image.Image toFxImage(Sprite sprite){
        BufferedImage bufferedimage = new BufferedImage(sprite.myWidth, sprite.myHeight, 1);
        bufferedimage.setRGB(0, 0, sprite.myWidth, sprite.myHeight, sprite.myPixels, 0, sprite.myWidth);
        return SwingFXUtils.toFXImage(bufferedimage, null);
    }
}
