package com.grinder.cache;

import com.runescape.cache.graphics.sprite.SpriteLoader;
import com.runescape.io.Buffer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;
import java.util.zip.GZIPOutputStream;

/**
 * @author Savions.
 */
public class SpritePacker {

	private static final boolean CONVERT_BLACK_TO_PINK = true;
	private static final String[] NEW_SPRITES = { "S1061I0", "S1102I0" };

	public static final void main(String[] args) throws Exception {
		SpriteLoader.loadSprites(() -> {});
		try {
			Buffer indexOutput = new Buffer(1000000);
			DataOutputStream outputStream = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(
					Paths.get("./sprites.dat").toFile())));
			indexOutput.writeInt(SpriteLoader.totalSprites + NEW_SPRITES.length);
			for (int i = 0; i < SpriteLoader.totalSprites; i++) {
				SpriteLoader.cache[i].write(outputStream);
				indexOutput.writeInt(i);
				indexOutput.writeInt(SpriteLoader.cache[i].spriteData.length);
			}
			for (int i = 0; i < + NEW_SPRITES.length; i++) {
				SpriteLoader loader = new SpriteLoader();
				loader.name = "" + NEW_SPRITES[i];
				loader.id = SpriteLoader.totalSprites + i;

				File file = new File("sprites/" + NEW_SPRITES[i] + ".png");
				if (CONVERT_BLACK_TO_PINK) {
					BufferedImage imgBuf = ImageIO.read(file);
					for (int x = 0; x < imgBuf.getWidth(); x++) {
						for (int y = 0; y  < imgBuf.getHeight(); y++) {
							if ((imgBuf.getRGB(x, y) & 0xFFFFFF) == 0) {
								imgBuf.setRGB(x, y, imgBuf.getRGB(x, y) | ((0xff << 16) | 0xff));
								System.out.println("did my best lad");
							}
						}
					}
					ImageIO.write(imgBuf, "png", file);
				}
				byte[] data = new byte[(int) file.length()];
				FileInputStream d = new FileInputStream(file);
				d.read(data);
				loader.spriteData = data;
				loader.write(outputStream);
				indexOutput.writeInt(loader.id);
				indexOutput.writeInt(loader.spriteData.length);
				System.out.println("Added " + NEW_SPRITES[i] + ", new id=" + loader.id);
			}
			//sprites.dat
			outputStream.flush();
			outputStream.close();

			//sprites.indx
			outputStream = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(
					Paths.get("./sprites.idx").toFile())));
			for (int i = 0; i < indexOutput.index; i++) {
				outputStream.writeByte(indexOutput.array[i]);
			}
			outputStream.flush();
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}