package com.runescape.cache;

import com.runescape.draw.Rasterizer2D;
import com.runescape.io.Buffer;

public final class IndexedSprite extends Rasterizer2D {

   public byte[] pixels;
   public int[] palette;
   public int subWidth;
   public int subHeight;
   public int xOffset;
   public int yOffset;
   public int width;
   public int height;

   public void normalize() {
      if(this.subWidth != this.width || this.subHeight != this.height) {
         byte[] var1 = new byte[this.width * this.height];
         int var2 = 0;

         for(int var3 = 0; var3 < this.subHeight; ++var3) {
            for(int var4 = 0; var4 < this.subWidth; ++var4) {
               var1[var4 + (var3 + this.yOffset) * this.width + this.xOffset] = this.pixels[var2++];
            }
         }

         this.pixels = var1;
         this.subWidth = this.width;
         this.subHeight = this.height;
         this.xOffset = 0;
         this.yOffset = 0;
      }
   }

   public void shiftColors(int var1, int var2, int var3) {
      for(int var4 = 0; var4 < this.palette.length; ++var4) {
         int var5 = this.palette[var4] >> 16 & 255;
         var5 += var1;
         if(var5 < 0) {
            var5 = 0;
         } else if(var5 > 255) {
            var5 = 255;
         }

         int var6 = this.palette[var4] >> 8 & 255;
         var6 += var2;
         if(var6 < 0) {
            var6 = 0;
         } else if(var6 > 255) {
            var6 = 255;
         }

         int var7 = this.palette[var4] & 255;
         var7 += var3;
         if(var7 < 0) {
            var7 = 0;
         } else if(var7 > 255) {
            var7 = 255;
         }

         this.palette[var4] = var7 + (var6 << 8) + (var5 << 16);
      }

   }

   public void __q_496(int var1, int var2) {
      var1 += this.xOffset;
      var2 += this.yOffset;
      int var3 = var1 + var2 * Rasterizer2D.Rasterizer2D_width;
      int var4 = 0;
      int var5 = this.subHeight;
      int var6 = this.subWidth;
      int var7 = Rasterizer2D.Rasterizer2D_width - var6;
      int var8 = 0;
      int var9;
      if(var2 < Rasterizer2D.Rasterizer2D_yClipStart) {
         var9 = Rasterizer2D.Rasterizer2D_yClipStart - var2;
         var5 -= var9;
         var2 = Rasterizer2D.Rasterizer2D_yClipStart;
         var4 += var9 * var6;
         var3 += var9 * Rasterizer2D.Rasterizer2D_width;
      }

      if(var5 + var2 > Rasterizer2D.Rasterizer2D_yClipEnd) {
         var5 -= var5 + var2 - Rasterizer2D.Rasterizer2D_yClipEnd;
      }

      if(var1 < Rasterizer2D.Rasterizer2D_xClipStart) {
         var9 = Rasterizer2D.Rasterizer2D_xClipStart - var1;
         var6 -= var9;
         var1 = Rasterizer2D.Rasterizer2D_xClipStart;
         var4 += var9;
         var3 += var9;
         var8 += var9;
         var7 += var9;
      }

      if(var6 + var1 > Rasterizer2D.Rasterizer2D_xClipEnd) {
         var9 = var6 + var1 - Rasterizer2D.Rasterizer2D_xClipEnd;
         var6 -= var9;
         var8 += var9;
         var7 += var9;
      }

      if(var6 > 0 && var5 > 0) {
         IndexedSprite_two(Rasterizer2D.Rasterizer2D_pixels, this.pixels, this.palette, var4, var3, var6, var5, var7, var8);
      }
   }

   public void __o_497(int var1, int var2, int var3, int var4) {
      int var5 = this.subWidth;
      int var6 = this.subHeight;
      int var7 = 0;
      int var8 = 0;
      int var9 = this.width;
      int var10 = this.height;
      int var11 = (var9 << 16) / var3;
      int var12 = (var10 << 16) / var4;
      int var13;
      if(this.xOffset > 0) {
         var13 = (var11 + (this.xOffset << 16) - 1) / var11;
         var1 += var13;
         var7 += var13 * var11 - (this.xOffset << 16);
      }

      if(this.yOffset > 0) {
         var13 = (var12 + (this.yOffset << 16) - 1) / var12;
         var2 += var13;
         var8 += var13 * var12 - (this.yOffset << 16);
      }

      if(var5 < var9) {
         var3 = (var11 + ((var5 << 16) - var7) - 1) / var11;
      }

      if(var6 < var10) {
         var4 = (var12 + ((var6 << 16) - var8) - 1) / var12;
      }

      var13 = var1 + var2 * Rasterizer2D.Rasterizer2D_width;
      int var14 = Rasterizer2D.Rasterizer2D_width - var3;
      if(var2 + var4 > Rasterizer2D.Rasterizer2D_yClipEnd) {
         var4 -= var2 + var4 - Rasterizer2D.Rasterizer2D_yClipEnd;
      }

      int var15;
      if(var2 < Rasterizer2D.Rasterizer2D_yClipStart) {
         var15 = Rasterizer2D.Rasterizer2D_yClipStart - var2;
         var4 -= var15;
         var13 += var15 * Rasterizer2D.Rasterizer2D_width;
         var8 += var12 * var15;
      }

      if(var3 + var1 > Rasterizer2D.Rasterizer2D_xClipEnd) {
         var15 = var3 + var1 - Rasterizer2D.Rasterizer2D_xClipEnd;
         var3 -= var15;
         var14 += var15;
      }

      if(var1 < Rasterizer2D.Rasterizer2D_xClipStart) {
         var15 = Rasterizer2D.Rasterizer2D_xClipStart - var1;
         var3 -= var15;
         var13 += var15;
         var7 += var11 * var15;
         var14 += var15;
      }

      IndexedSprite_something(Rasterizer2D.Rasterizer2D_pixels, this.pixels, this.palette, var7, var8, var13, var14, var3, var4, var11, var12, var5);
   }

   static void IndexedSprite_two(int[] var0, byte[] var1, int[] var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      int var9 = -(var5 >> 2);
      var5 = -(var5 & 3);

      for(int var10 = -var6; var10 < 0; ++var10) {
         int var11;
         byte var12;
         for(var11 = var9; var11 < 0; ++var11) {
            var12 = var1[var3++];
            if(var12 != 0) {
               var0[var4++] = var2[var12 & 255];
            } else {
               ++var4;
            }

            var12 = var1[var3++];
            if(var12 != 0) {
               var0[var4++] = var2[var12 & 255];
            } else {
               ++var4;
            }

            var12 = var1[var3++];
            if(var12 != 0) {
               var0[var4++] = var2[var12 & 255];
            } else {
               ++var4;
            }

            var12 = var1[var3++];
            if(var12 != 0) {
               var0[var4++] = var2[var12 & 255];
            } else {
               ++var4;
            }
         }

         for(var11 = var5; var11 < 0; ++var11) {
            var12 = var1[var3++];
            if(var12 != 0) {
               var0[var4++] = var2[var12 & 255];
            } else {
               ++var4;
            }
         }

         var4 += var7;
         var3 += var8;
      }

   }

   static void IndexedSprite_something(int[] var0, byte[] var1, int[] var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11) {
      int var12 = var3;

      for(int var13 = -var8; var13 < 0; ++var13) {
         int var14 = var11 * (var4 >> 16);

         for(int var15 = -var7; var15 < 0; ++var15) {
            byte var16 = var1[(var3 >> 16) + var14];
            if(var16 != 0) {
               var0[var5++] = var2[var16 & 255];
            } else {
               ++var5;
            }

            var3 += var9;
         }

         var4 += var10;
         var3 = var12;
         var5 += var6;
      }

   }

   public static IndexedSprite method2028(AbstractIndexCache var0, int var1) {
      return !method3643(var0, var1)?null:method3663();
   }

   public static IndexedSprite[] method670(AbstractIndexCache indexdatabase_0, String string_0, String string_1) {
      int int_0 = indexdatabase_0.getArchiveId(string_0);
      int int_1 = indexdatabase_0.getRecordId(int_0, string_1);
      return method634(indexdatabase_0, int_0, int_1);
   }

   static IndexedSprite[] method634(AbstractIndexCache indexdatabase_0, int int_0, int int_1) {
      if (!decodeIfExists(indexdatabase_0, int_0, int_1)) {
         return null;
      } else {
         IndexedSprite[] indexedsprites_0 = new IndexedSprite[indexedSpriteCount];

         for (int int_2 = 0; int_2 < indexedSpriteCount; int_2++) {
            IndexedSprite indexedsprite_0 = indexedsprites_0[int_2] = new IndexedSprite();
            indexedsprite_0.width = indexedSpriteWidth;
            indexedsprite_0.subHeight = indexedSpriteHeight;
            indexedsprite_0.xOffset = indexedSpriteOffsetXs[int_2];
            indexedsprite_0.yOffset = indexedSpriteOffsetYs[int_2];
            indexedsprite_0.subWidth = indexedSpriteWidths[int_2];
            indexedsprite_0.height = indexedSpriteHeights[int_2];
            indexedsprite_0.palette = indexedSpritePalette;
            indexedsprite_0.pixels = spritePixels[int_2];
         }
         cleanPixelLoader();
         return indexedsprites_0;
      }
   }

   static boolean decodeIfExists(AbstractIndexCache index, int archiveId, int fileId) {
      byte[] data = index.takeRecord(archiveId, fileId);
      if (data == null) {
         return false;
      } else {
         decodeSprite(data);
         return true;
      }
   }

   static int indexedSpriteCount;
   static int indexedSpriteWidth;
   static int indexedSpriteHeight;
   static int[] indexedSpriteOffsetXs;
   static int[] indexedSpriteOffsetYs;
   static int[] indexedSpritePalette;
   static int[] indexedSpriteWidths;
   static int[] indexedSpriteHeights;
   static byte[][] spritePixels;

   static boolean method3643(AbstractIndexCache var0, int var1) {
      byte[] var2 = var0.takeRecordFlat(var1);
      if(var2 == null) {
         return false;
      } else {
         decodeSprite(var2);
         return true;
      }
   }
   static IndexedSprite method3663() {
      IndexedSprite var0 = new IndexedSprite();
      var0.width = indexedSpriteWidth;
      var0.height = indexedSpriteHeight;
      var0.xOffset = indexedSpriteOffsetXs[0];
      var0.yOffset = indexedSpriteOffsetYs[0];
      var0.subWidth = indexedSpriteWidths[0];
      var0.subHeight = indexedSpriteHeights[0];
      var0.palette = indexedSpritePalette;
      var0.pixels = spritePixels[0];
      cleanPixelLoader();
      return var0;
   }
   static void cleanPixelLoader() {
      indexedSpriteOffsetXs = null;
      indexedSpriteOffsetYs = null;
      indexedSpriteWidths = null;
      indexedSpriteHeights = null;
      indexedSpritePalette = null;
      spritePixels = null;
   }

   static void decodeSprite(byte[] var0) {
      Buffer var1 = new Buffer(var0);
      var1.index = var0.length - 2;
      indexedSpriteCount = var1.getUnsignedLEShort();
      indexedSpriteOffsetXs = new int[indexedSpriteCount];
      indexedSpriteOffsetYs = new int[indexedSpriteCount];
      indexedSpriteWidths = new int[indexedSpriteCount];
      indexedSpriteHeights = new int[indexedSpriteCount];
      spritePixels = new byte[indexedSpriteCount][];
      var1.index = var0.length - 7 - indexedSpriteCount * 8;
      indexedSpriteWidth = var1.getUnsignedLEShort();
      indexedSpriteHeight = var1.getUnsignedLEShort();
      int var2 = (var1.getUnsignedByte() & 255) + 1;

      int var3;
      for(var3 = 0; var3 < indexedSpriteCount; ++var3) {
         indexedSpriteOffsetXs[var3] = var1.getUnsignedLEShort();
      }

      for(var3 = 0; var3 < indexedSpriteCount; ++var3) {
         indexedSpriteOffsetYs[var3] = var1.getUnsignedLEShort();
      }

      for(var3 = 0; var3 < indexedSpriteCount; ++var3) {
         indexedSpriteWidths[var3] = var1.getUnsignedLEShort();
      }

      for(var3 = 0; var3 < indexedSpriteCount; ++var3) {
         indexedSpriteHeights[var3] = var1.getUnsignedLEShort();
      }

      var1.index = var0.length - 7 - indexedSpriteCount * 8 - (var2 - 1) * 3;
      indexedSpritePalette = new int[var2];

      for(var3 = 1; var3 < var2; ++var3) {
         indexedSpritePalette[var3] = var1.readMedium();
         if(indexedSpritePalette[var3] == 0) {
            indexedSpritePalette[var3] = 1;
         }
      }

      var1.index = 0;

      for(var3 = 0; var3 < indexedSpriteCount; ++var3) {
         int var4 = indexedSpriteWidths[var3];
         int var5 = indexedSpriteHeights[var3];
         int var6 = var4 * var5;
         byte[] var7 = new byte[var6];
         spritePixels[var3] = var7;
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
