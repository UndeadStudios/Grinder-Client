package com.runescape.cache.graphics.sprite;

import com.runescape.cache.AbstractIndexCache;
import com.runescape.cache.IndexedSprites;
import com.runescape.cache.graphics.FileOperations;
import com.runescape.io.Buffer;
import com.runescape.sign.SignLink;
import net.runelite.cache.io.OutputStream;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

public class SpriteLoader {


	public static SpriteLoader[] cache;

	public static Sprite[] sprites = null;

	public static int totalSprites;


	/**
	 * Gets the name of a specified sprite index.
	 * 
	 * @param index
	 * @return
	 */
	public static String getName(int index) {
		if (cache[index].name != null) {
			return cache[index].name;
		} else {
			return "null";
		}
	}

	/**
	 * Gets the drawOffsetX of a specified sprite index.
	 * 
	 * @param index
	 * @return
	 */
	public static int getOffsetX(int index) {
		return cache[index].drawOffsetX;
	}

	/**
	 * Gets the drawOffsetY of a specified sprite index.
	 * 
	 * @param index
	 * @return
	 */
	public static int getOffsetY(int index) {
		return cache[index].drawOffsetY;
	}
	public static byte[] getData(int index) {
		return cache[index].spriteData;
	}

	public static void createSprite(SpriteLoader sprite, boolean second) {
		if (!second) {
			sprites[sprite.id] = new Sprite(sprite.spriteData);
			sprites[sprite.id].drawOffsetX = sprite.drawOffsetX;
			sprites[sprite.id].drawOffsetY = sprite.drawOffsetY;
		}
	}
	/**
	 * Loads the sprite data and index files from the cache location. This can be
	 * edited to use an archive such as config or media to load from the cache.
	 * 
	 * @param archive
	 */
	public static void loadSprites(Runnable step) {
		DataInputStream indexFile = null;
		DataInputStream dataFile = null;
		try {
			Buffer index = new Buffer(FileOperations.readFile(SignLink.findcachedir() + "sprites.idx"));
			Buffer data = new Buffer(FileOperations.readFile(SignLink.findcachedir() + "sprites.dat"));
			indexFile = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(index.array)));
			dataFile = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(data.array)));
			totalSprites = indexFile.readInt();
			if (cache == null) {
				cache = new SpriteLoader[totalSprites];
				sprites = new Sprite[totalSprites];
			}
			for (int i = 0; i < totalSprites; i++) {
				int id = indexFile.readInt();
				if (cache[id] == null) {
					cache[id] = new SpriteLoader();
				}
				cache[id].readValues(dataFile, indexFile);
				step.run();
				createSprite(cache[id], false);
			}
			indexFile.close();
			dataFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (indexFile != null) {
					indexFile.close();
				}
				if (dataFile != null) {
					dataFile.close();
				}
			} catch (Exception e) {
			}
		}
	}

	public String name;

	public int id;
	public int drawOffsetX;
	public int drawOffsetY;
	public byte[] spriteData;

	/**
	 * Sets the default values.
	 */
	public SpriteLoader() {
		name = "name";
		id = -1;
		drawOffsetX = 0;
		drawOffsetY = 0;
		spriteData = null;
	}

	public static Sprite getSprite(int spriteIndex) {
		return spriteIndex >= SpriteCompanion.cacheSprite.length
				? null : SpriteCompanion.cacheSprite[spriteIndex];
	}

	public static Sprite getSprite(Sprite[] cacheSprite, int i) {
		return cacheSprite[i];
	}

	public static Sprite getSprite(boolean rememberUsernameHover, int i, int i2) {
		return getSprite(rememberUsernameHover ? i : i2);
	}

	public static int getSpriteCount(){
		return SpriteCompanion.cacheSprite.length;
	}

	public static void clearSprites(){
		SpriteCompanion.cacheSprite = null;
	}

	public static void init(){
		SpriteCompanion.cacheSprite = sprites;
	}

	/**
	 * Reads the information from the index and data files.
	 * 
	 * @param index
	 *            holds the sprite indices
	 * @param data
	 *            holds the sprite data per index
	 * @throws IOException
	 */
	public void readValues(DataInputStream data, DataInputStream indexFile) throws IOException {
		do {
			int opCode = data.readByte();
			if (opCode == 0) {
				return;
			}
			if (opCode == 1) {
				id = data.readShort();
			} else if (opCode == 2) {
				name = data.readUTF();
			} else if (opCode == 3) {
				drawOffsetX = data.readShort();
			} else if (opCode == 4) {
				drawOffsetY = data.readShort();
			} else if (opCode == 5) {
				byte[] dataread = new byte[indexFile.readInt()];
				data.readFully(dataread);
				spriteData = dataread;
			}
		} while (true);
	}

	public byte[] write() {
		OutputStream outputStream = new OutputStream();
		if(id != -1){
			outputStream.writeByte(1);
			outputStream.writeShort(id);
		}
		if(name != null && !name.equals("name")) {
			outputStream.writeByte(2);
			outputStream.writeBytes(name.getBytes(StandardCharsets.UTF_8));
		}
		if(drawOffsetX != 0) {
			outputStream.writeByte(3);
			outputStream.writeShort(drawOffsetX);
		}
		if(drawOffsetY != 0) {
			outputStream.writeByte(4);
			outputStream.writeShort(drawOffsetY);
		}
		outputStream.writeByte(5);
		outputStream.writeBytes(spriteData);

		outputStream.writeByte(0);

		return outputStream.flip();
	}

	public void write(DataOutputStream outputStream) throws IOException {
		if(id != -1){
			outputStream.write(1);
			outputStream.writeShort(id);
		}
		if(name != null && !name.equals("name")) {
			outputStream.write(2);
			outputStream.writeUTF(name);
		}
		if(drawOffsetX != 0) {
			outputStream.write(3);
			outputStream.writeShort(drawOffsetX);
		}
		if(drawOffsetY != 0) {
			outputStream.write(4);
			outputStream.writeShort(drawOffsetY);
		}
		outputStream.write(5);
		outputStream.write(spriteData);

		outputStream.write(0);
	}


	public static boolean method4392(AbstractIndexCache var0, int var1, int var2) {
		byte[] var3 = var0.takeRecord(var1, var2);
		if(var3 == null) {
			return false;
		} else {
			decodeSprite(var3);
			return true;
		}
	}


	static void decodeSprite(byte[] var0) {
		Buffer var1 = new Buffer(var0);
		var1.index = var0.length - 2;
		IndexedSprites.indexedSpriteCount = var1.getUnsignedLEShort();
		IndexedSprites.indexedSpriteOffsetXs = new int[IndexedSprites.indexedSpriteCount];
		IndexedSprites.indexedSpriteOffsetYs = new int[IndexedSprites.indexedSpriteCount];
		IndexedSprites.indexedSpriteWidths = new int[IndexedSprites.indexedSpriteCount];
		IndexedSprites.indexedSpriteHeights = new int[IndexedSprites.indexedSpriteCount];
		IndexedSprites.spritePixels = new byte[IndexedSprites.indexedSpriteCount][];
		var1.index = var0.length - 7 - IndexedSprites.indexedSpriteCount * 8;
		IndexedSprites.indexedSpriteWidth = var1.getUnsignedLEShort();
		IndexedSprites.indexedSpriteHeight = var1.getUnsignedLEShort();
		int var2 = (var1.getUnsignedByte() & 255) + 1;

		int var3;
		for(var3 = 0; var3 < IndexedSprites.indexedSpriteCount; ++var3) {
			IndexedSprites.indexedSpriteOffsetXs[var3] = var1.getUnsignedLEShort();
		}

		for(var3 = 0; var3 < IndexedSprites.indexedSpriteCount; ++var3) {
			IndexedSprites.indexedSpriteOffsetYs[var3] = var1.getUnsignedLEShort();
		}

		for(var3 = 0; var3 < IndexedSprites.indexedSpriteCount; ++var3) {
			IndexedSprites.indexedSpriteWidths[var3] = var1.getUnsignedLEShort();
		}

		for(var3 = 0; var3 < IndexedSprites.indexedSpriteCount; ++var3) {
			IndexedSprites.indexedSpriteHeights[var3] = var1.getUnsignedLEShort();
		}

		var1.index = var0.length - 7 - IndexedSprites.indexedSpriteCount * 8 - (var2 - 1) * 3;
		IndexedSprites.indexedSpritePalette = new int[var2];

		for(var3 = 1; var3 < var2; ++var3) {
			IndexedSprites.indexedSpritePalette[var3] = var1.readMedium();
			if(IndexedSprites.indexedSpritePalette[var3] == 0) {
				IndexedSprites.indexedSpritePalette[var3] = 1;
			}
		}

		var1.index = 0;

		for(var3 = 0; var3 < IndexedSprites.indexedSpriteCount; ++var3) {
			int var4 = IndexedSprites.indexedSpriteWidths[var3];
			int var5 = IndexedSprites.indexedSpriteHeights[var3];
			int var6 = var4 * var5;
			byte[] var7 = new byte[var6];
			IndexedSprites.spritePixels[var3] = var7;
			int var8 = var1.getUnsignedByte();
			int var9;
			if(var8 == 0) {
				for(var9 = 0; var9 < var6; ++var9) {
					var7[var9] = var1.readByte();
				}
			} else if(var8 == 1) {
				for(var9 = 0; var9 < var4; ++var9) {
					for(int var10 = 0; var10 < var5; ++var10) {
						var7[var9 + var10 * var4] = var1.readByte();
					}
				}
			}
		}

	}
}