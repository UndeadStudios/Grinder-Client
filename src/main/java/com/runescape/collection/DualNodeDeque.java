package com.runescape.collection;

public final class DualNodeDeque {

   DualNode sentinel;

   public DualNodeDeque() {
      this.sentinel = new DualNode();
      this.sentinel.previousDual = this.sentinel;
      this.sentinel.nextDual = this.sentinel;
   }

   public void addFirst(DualNode var1) {
      if(var1.nextDual != null) {
         var1.removeDual();
      }

      var1.nextDual = this.sentinel.nextDual;
      var1.previousDual = this.sentinel;
      var1.nextDual.previousDual = var1;
      var1.previousDual.nextDual = var1;
   }

   public void addLast(DualNode var1) {
      if(var1.nextDual != null) {
         var1.removeDual();
      }

      var1.nextDual = this.sentinel;
      var1.previousDual = this.sentinel.previousDual;
      var1.nextDual.previousDual = var1;
      var1.previousDual.nextDual = var1;
   }

   public DualNode removeLast() {
      DualNode var1 = this.sentinel.previousDual;
      if(var1 == this.sentinel) {
         return null;
      } else {
         var1.removeDual();
         return var1;
      }
   }

   public DualNode last() {
      DualNode var1 = this.sentinel.previousDual;
      return var1 == this.sentinel?null:var1;
   }

   public void clear() {
      while(true) {
         DualNode var1 = this.sentinel.previousDual;
         if(var1 == this.sentinel) {
            return;
         }

         var1.removeDual();
      }
   }

   public static void method5220(DualNode var0, DualNode var1) {
      if(var0.nextDual != null) {
         var0.removeDual();
      }

      var0.nextDual = var1;
      var0.previousDual = var1.previousDual;
      var0.nextDual.previousDual = var0;
      var0.previousDual.nextDual = var0;
   }
}
