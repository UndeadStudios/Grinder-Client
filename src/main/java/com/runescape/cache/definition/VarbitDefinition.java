package com.runescape.cache.definition;

import com.runescape.cache.AbstractIndexCache;
import com.runescape.collection.DualNode;
import com.runescape.collection.EvictingDualNodeHashTable;
import com.runescape.io.Buffer;

public class VarbitDefinition extends DualNode {

   public static AbstractIndexCache VarbitDefinition_indexCache;
   public static EvictingDualNodeHashTable VarbitDefinition_cached;
   public int varp;
   public int lowBit;
   public int highBit;

   static {
      VarbitDefinition_cached = new EvictingDualNodeHashTable(64);
   }

   public void read(Buffer var1) {
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
         this.varp = var1.getUnsignedLEShort();
         this.lowBit = var1.getUnsignedByte();
         this.highBit = var1.getUnsignedByte();
      }

   }

   static final boolean method4910(int var0, int var1) {
      OSObjectDefinition var2 = OSObjectDefinition.lookup(var0);
      if(var1 == 11) {
         var1 = 10;
      }

      if(var1 >= 5 && var1 <= 8) {
         var1 = 4;
      }

      return var2.__u_421(var1);
   }
}
