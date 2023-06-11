package com.runescape.collection;

public final class EvictingDualNodeHashTable {

   DualNode node;
   int capacity;
   int remainingCapacity;
   NodeHashTable hashTable;
   DualNodeDeque deque;

   public EvictingDualNodeHashTable(int var1) {
      this.node = new DualNode();
      this.deque = new DualNodeDeque();
      this.capacity = var1;
      this.remainingCapacity = var1;

      int var2;
      for(var2 = 1; var2 + var2 < var1; var2 += var2) {
         ;
      }

      this.hashTable = new NodeHashTable(var2);
   }

   public DualNode get(long var1) {
      DualNode var3 = (DualNode)this.hashTable.get(var1);
      if(var3 != null) {
         this.deque.addFirst(var3);
      }

      return var3;
   }
   public void remove(long var1) {
      DualNode node = (DualNode)this.hashTable.get(var1);
      if(node != null) {
         node.remove();
         node.removeDual();
         ++this.remainingCapacity;
      }
   }

   public void put(DualNode var1, long var2) {
      if(this.remainingCapacity == 0) {
         DualNode var4 = this.deque.removeLast();
         var4.remove();
         var4.removeDual();
         if(var4 == this.node) {
            var4 = this.deque.removeLast();
            var4.remove();
            var4.removeDual();
         }
      } else {
         --this.remainingCapacity;
      }

      this.hashTable.put(var1, var2);
      this.deque.addFirst(var1);
   }

   public void clear() {
      this.deque.clear();
      this.hashTable.clear();
      this.node = new DualNode();
      this.remainingCapacity = this.capacity;
   }
}
