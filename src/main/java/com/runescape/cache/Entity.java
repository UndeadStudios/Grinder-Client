package com.runescape.cache;

import com.runescape.collection.DualNode;

public abstract class Entity extends DualNode {

   public int height;

   protected Entity() {
      this.height = 1000;
   }

   protected OgModel getModel() {
      return null;
   }

   void renderDraw(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9) {
      OgModel var11 = this.getModel();
      if(var11 != null) {
         this.height = var11.height;
         var11.renderDraw(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }

}
