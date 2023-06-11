package com.runescape.cache.definition;

import com.runescape.cache.AbstractIndexCache;
import com.runescape.cache.Js5;
import com.runescape.cache.ModelData;
import com.runescape.collection.DualNode;
import com.runescape.collection.EvictingDualNodeHashTable;
import com.runescape.io.Buffer;

public class KitDefinition extends DualNode {

   public static AbstractIndexCache __im_f;
   public static int __im_q;
   static EvictingDualNodeHashTable KitDefinition_cached;

   public int __o;
   int[] __u;
   short[] recolorFrom;
   short[] recolorTo;
   short[] retextureFrom;
   short[] retextureTo;
   int[] archives;
   public boolean __k;

   static {
      KitDefinition_cached = new EvictingDualNodeHashTable(64);
   }

   public static KitDefinition getKitDefinition(int var0) {
      KitDefinition var1 = (KitDefinition)KitDefinition.KitDefinition_cached.get((long)var0);
      if(var1 != null) {
         return var1;
      } else {
         byte[] var2 = Js5.configs.takeRecord(3, var0);
         var1 = new KitDefinition();
         if(var2 != null) {
            var1.read(new Buffer(var2));
         }

         KitDefinition.KitDefinition_cached.put(var1, (long)var0);
         return var1;
      }
   }

   KitDefinition() {
      this.__o = -1;
      this.archives = new int[]{-1, -1, -1, -1, -1};
      this.__k = false;
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
         this.__o = var1.getUnsignedByte();
      } else {
         int var3;
         int var4;
         if(var2 == 2) {
            var3 = var1.getUnsignedByte();
            this.__u = new int[var3];

            for(var4 = 0; var4 < var3; ++var4) {
               this.__u[var4] = var1.getUnsignedLEShort();
            }
         } else if(var2 == 3) {
            this.__k = true;
         } else if(var2 == 40) {
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
         } else if(var2 >= 60 && var2 < 70) {
            this.archives[var2 - 60] = var1.getUnsignedLEShort();
         }
      }

   }

   public boolean __w_413() {
      if(this.__u == null) {
         return true;
      } else {
         boolean var1 = true;

         for(int var2 = 0; var2 < this.__u.length; ++var2) {
            if(!__im_f.tryLoadRecord(this.__u[var2], 0)) {
               var1 = false;
            }
         }

         return var1;
      }
   }

   public ModelData __o_414() {
      if(this.__u == null) {
         return null;
      } else {
         ModelData[] var1 = new ModelData[this.__u.length];

         for(int var2 = 0; var2 < this.__u.length; ++var2) {
            var1[var2] = ModelData.ModelData_get(__im_f, this.__u[var2], 0);
         }

         ModelData var4;
         if(var1.length == 1) {
            var4 = var1[0];
         } else {
            var4 = new ModelData(var1, var1.length);
         }

         int var3;
         if(this.recolorFrom != null) {
            for(var3 = 0; var3 < this.recolorFrom.length; ++var3) {
               var4.recolor(this.recolorFrom[var3], this.recolorTo[var3]);
            }
         }

         if(this.retextureFrom != null) {
            for(var3 = 0; var3 < this.retextureFrom.length; ++var3) {
               var4.retexture(this.retextureFrom[var3], this.retextureTo[var3]);
            }
         }

         return var4;
      }
   }
   public boolean __u_415() {
      boolean var1 = true;

      for(int var2 = 0; var2 < 5; ++var2) {
         if(this.archives[var2] != -1 && !__im_f.tryLoadRecord(this.archives[var2], 0)) {
            var1 = false;
         }
      }

      return var1;
   }

   public ModelData __g_416() {
      ModelData[] var1 = new ModelData[5];
      int var2 = 0;

      for(int var3 = 0; var3 < 5; ++var3) {
         if(this.archives[var3] != -1) {
            var1[var2++] = ModelData.ModelData_get(__im_f, this.archives[var3], 0);
         }
      }

      ModelData var5 = new ModelData(var1, var2);
      int var4;
      if(this.recolorFrom != null) {
         for(var4 = 0; var4 < this.recolorFrom.length; ++var4) {
            var5.recolor(this.recolorFrom[var4], this.recolorTo[var4]);
         }
      }

      if(this.retextureFrom != null) {
         for(var4 = 0; var4 < this.retextureFrom.length; ++var4) {
            var5.retexture(this.retextureFrom[var4], this.retextureTo[var4]);
         }
      }

      return var5;
   }


}
