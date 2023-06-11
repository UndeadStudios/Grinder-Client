package com.runescape.cache;

import com.runescape.collection.NodeDeque;
import com.runescape.io.Buffer;

public class TextureProvider implements TextureLoader {

   Texture[] textures;
   NodeDeque deque;
   int capacity;
   int remaining;
   double brightness0;
   int textureSize;
   AbstractIndexCache indexCache;

   public TextureProvider(AbstractIndexCache var1, AbstractIndexCache var2, int var3, double var4, int var6) {
      this.deque = new NodeDeque();
      this.remaining = 0;
      this.brightness0 = 1.0D;
      this.textureSize = 128;
      this.indexCache = var2;
      this.capacity = var3;
      this.remaining = this.capacity;
      this.brightness0 = var4;
      this.textureSize = var6;
      int[] var7 = var1.getRedcordOrNull(0);
      int var8 = var7.length;
      this.textures = new Texture[var1.getRecordLength(0)];

      for (int i : var7) {
         Buffer var10 = new Buffer(var1.takeRecord(0, i));
         this.textures[i] = new Texture(var10);
      }
   }

   public int __m_212() {
      int var1 = 0;
      int var2 = 0;
      Texture[] var3 = this.textures;

      for (Texture var5 : var3) {
         if (var5 != null && var5.fileIds != null) {
            var1 += var5.fileIds.length;
            int[] var6 = var5.fileIds;

            for (int var8 : var6) {
               if (this.indexCache.__u_393(var8)) {
                  ++var2;
               }
            }
         }
      }

      if(var1 == 0) {
         return 0;
      } else {
         return var2 * 100 / var1;
      }
   }

   public void setBrightness(double var1) {
      this.brightness0 = var1;
      this.clear();
   }

   public int[] getTexturePixels(int var1) {
      Texture var2 = this.textures[var1];
      if(var2 != null) {
         if(var2.pixels != null) {
            this.deque.addLast(var2);
            var2.isLoaded = true;
            return var2.pixels;
         }

         boolean var3 = var2.load(this.brightness0, this.textureSize, this.indexCache);
         if(var3) {
            if(this.remaining == 0) {
               Texture var4 = (Texture)this.deque.removeFirst();
               var4.reset();
            } else {
               --this.remaining;
            }

            this.deque.addLast(var2);
            var2.isLoaded = true;
            return var2.pixels;
         }
      }

      return null;
   }

   public int getAverageTextureRGB(int var1) {
      return this.textures[var1] != null?this.textures[var1].averageRGB :0;
   }

   public boolean vmethod4757(int var1) {
      return this.textures[var1].__u;
   }

   public boolean isLowDetail(int var1) {
      return this.textureSize == 64;
   }

   public void clear() {
      for (Texture texture : this.textures) {
         if (texture != null) {
            texture.reset();
         }
      }

      this.deque = new NodeDeque();
      this.remaining = this.capacity;
   }

   public void animate(int var1) {
      for (Texture var3 : this.textures) {
         if (var3 != null && var3.animationDirection != 0 && var3.isLoaded) {
            var3.animate(var1);
            var3.isLoaded = false;
         }
      }
   }
}
