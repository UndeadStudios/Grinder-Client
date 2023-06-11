package com.runescape.util;

import com.runescape.cache.IndexCache;
import com.runescape.cache.IndexedSprite;
import com.runescape.cache.graphics.sprite.Sprite;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public final class SpriteUtil {

    public static Sprite load(byte[] bytes_0) {
        BufferedImage bufferedimage_0 = null;

        try {
            bufferedimage_0 = ImageIO.read(new ByteArrayInputStream(bytes_0));
            int int_0 = bufferedimage_0.getWidth();
            int int_1 = bufferedimage_0.getHeight();
            int[] ints_0 = new int[int_0 * int_1];
            PixelGrabber pixelgrabber_0 = new PixelGrabber(bufferedimage_0, 0, 0, int_0, int_1, ints_0, 0, int_0);
            pixelgrabber_0.grabPixels();
            return new Sprite(ints_0, int_0, int_1);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return new Sprite(0, 0);
    }
}
