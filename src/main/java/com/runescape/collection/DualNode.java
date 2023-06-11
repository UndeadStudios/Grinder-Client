package com.runescape.collection;


public class DualNode extends Node {

   public DualNode previousDual;
   public DualNode nextDual;

   public void removeDual() {
      if(this.nextDual != null) {
         this.nextDual.previousDual = this.previousDual;
         this.previousDual.nextDual = this.nextDual;
         this.previousDual = null;
         this.nextDual = null;
      }
   }
}
