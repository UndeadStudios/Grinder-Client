package com.runescape.collection;

import java.util.Iterator;

public class IterableNodeHashTableIterator implements Iterator {

   IterableNodeHashTable hashTable;

   Node __f;
   int __q;
   Node __w;

   IterableNodeHashTableIterator(IterableNodeHashTable var1) {
      this.__w = null;
      this.hashTable = var1;
      this.__u_483();
   }

   void __u_483() {
      this.__f = this.hashTable.buckets[0].previous;
      this.__q = 1;
      this.__w = null;
   }

   public Object next() {
      Node var1;
      if(this.hashTable.buckets[this.__q - 1] != this.__f) {
         var1 = this.__f;
         this.__f = var1.previous;
         this.__w = var1;
         return var1;
      } else {
         do {
            if(this.__q >= this.hashTable.size) {
               return null;
            }

            var1 = this.hashTable.buckets[this.__q++].previous;
         } while(var1 == this.hashTable.buckets[this.__q - 1]);

         this.__f = var1.previous;
         this.__w = var1;
         return var1;
      }
   }

   public boolean hasNext() {
      if(this.hashTable.buckets[this.__q - 1] != this.__f) {
         return true;
      } else {
         while(this.__q < this.hashTable.size) {
            if(this.hashTable.buckets[this.__q++].previous != this.hashTable.buckets[this.__q - 1]) {
               this.__f = this.hashTable.buckets[this.__q - 1].previous;
               return true;
            }

            this.__f = this.hashTable.buckets[this.__q - 1];
         }

         return false;
      }
   }

   public void __remove_486() {
      if(this.__w == null) {
         throw new IllegalStateException();
      } else {
         this.__w.remove();
         this.__w = null;
      }
   }
}
