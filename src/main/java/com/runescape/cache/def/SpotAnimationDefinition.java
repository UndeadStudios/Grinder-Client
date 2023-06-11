package com.runescape.cache.def;

import com.runescape.cache.*;
import com.runescape.collection.DualNode;
import com.runescape.collection.EvictingDualNodeHashTable;
import com.runescape.io.Buffer;

public class SpotAnimationDefinition extends DualNode {

   public static AbstractIndexCache SpotAnimationDefinition_modelIndexCache;
   static EvictingDualNodeHashTable SpotAnimationDefinition_cached;
   static EvictingDualNodeHashTable SpotAnimationDefinition_cachedModels;

   int id;
   int archive;
   public int sequence;
   short[] recolorFrom;
   short[] recolorTo;
   short[] retextureFrom;
   short[] retextureTo;
   int widthScale;
   int heightScale;
   int orientation;
   int __a;
   int __z;

   static {
      SpotAnimationDefinition_cached = new EvictingDualNodeHashTable(64);
      SpotAnimationDefinition_cachedModels = new EvictingDualNodeHashTable(30);
   }

   SpotAnimationDefinition() {
      this.sequence = -1;
      this.widthScale = 128;
      this.heightScale = 128;
      this.orientation = 0;
      this.__a = 0;
      this.__z = 0;
   }
   public static SpotAnimationDefinition getSpotAnimationDefinition(int var0) {
      SpotAnimationDefinition var1 = (SpotAnimationDefinition)SpotAnimationDefinition.SpotAnimationDefinition_cached.get((long)var0);
      if(var1 != null) {
         return var1;
      } else {
         byte[] var2 = Js5.configs.takeRecord(13, var0);
         var1 = new SpotAnimationDefinition();
         var1.id = var0;
         if(var2 != null) {
            var1.read(new Buffer(var2));
         }

         SpotAnimationDefinition.SpotAnimationDefinition_cached.put(var1, (long)var0);
         return var1;
      }
   }

   void read(Buffer var1) {
      while(true) {
         int var2 = var1.getUnsignedByte();
         if(var2 == 0) {
            return;
         }

         this.readNext(var1, var2);
      }
   }

   void readNext(Buffer var1, int var2) {
      if(var2 == 1) {
         this.archive = var1.getUnsignedLEShort();
      } else if(var2 == 2) {
         this.sequence = var1.getUnsignedLEShort();
      } else if(var2 == 4) {
         this.widthScale = var1.getUnsignedLEShort();
      } else if(var2 == 5) {
         this.heightScale = var1.getUnsignedLEShort();
      } else if(var2 == 6) {
         this.orientation = var1.getUnsignedLEShort();
      } else if(var2 == 7) {
         this.__a = var1.getUnsignedByte();
      } else if(var2 == 8) {
         this.__z = var1.getUnsignedByte();
      } else {
         int var3;
         int var4;
         if(var2 == 40) {
            var3 = var1.getUnsignedByte();
            this.recolorFrom = new short[var3];
            this.recolorTo = new short[var3];

            for(var4 = 0; var4 < var3; ++var4) {
               this.recolorFrom[var4] = (short)var1.getUnsignedLEShort();
               this.recolorTo[var4] = (short)var1.getUnsignedLEShort();
            }
         } else if(var2 == 41) {
            var3 = var1.getUnsignedByte();
            this.retextureFrom = new short[var3];
            this.retextureTo = new short[var3];

            for(var4 = 0; var4 < var3; ++var4) {
               this.retextureFrom[var4] = (short)var1.getUnsignedLEShort();
               this.retextureTo[var4] = (short)var1.getUnsignedLEShort();
            }
         }
      }
   }
}
