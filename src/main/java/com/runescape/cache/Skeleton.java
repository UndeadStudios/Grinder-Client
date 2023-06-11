package com.runescape.cache;

import com.runescape.api.FrameBase;
import com.runescape.cache.anim.animaya.class220;
import com.runescape.collection.Node;
import com.runescape.io.Buffer;

import java.util.Arrays;

public class Skeleton extends Node implements FrameBase {

   public int id;
   public int count;
   private final int[] transformTypes;
   private final int[][] labels;
   class220 field2504;

   public Skeleton(int var1, byte[] var2) {
      this.id = var1;
      Buffer var3 = new Buffer(var2);
      this.count = var3.getUnsignedByte();
      this.transformTypes = new int[this.count];
      this.labels = new int[this.count][];

      int var4;
      for(var4 = 0; var4 < this.count; ++var4) {
         this.transformTypes[var4] = var3.getUnsignedByte();
      }

      for(var4 = 0; var4 < this.count; ++var4) {
         this.labels[var4] = new int[var3.getUnsignedByte()];
      }

      for(var4 = 0; var4 < this.count; ++var4) {
         for(int var5 = 0; var5 < this.labels[var4].length; ++var5) {
            this.labels[var4][var5] = var3.getUnsignedByte();
         }
      }

      if (var3.index < var3.array.length) {
         var4 = var3.readUnsignedShort();
         if (var4 > 0) {
            this.field2504 = new class220(var3, var4);
         }
      }

   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Skeleton)) return false;

      Skeleton skeleton = (Skeleton) o;

      if (id != skeleton.id) return false;
      if (count != skeleton.count) return false;
      if (!Arrays.equals(transformTypes, skeleton.transformTypes)) return false;
      return Arrays.deepEquals(labels, skeleton.labels);
   }

   @Override
   public int hashCode() {
      int result = id;
      result = 31 * result + count;
      result = 31 * result + Arrays.hashCode(transformTypes);
      result = 31 * result + Arrays.deepHashCode(labels);
      return result;
   }

   @Override
   public int[] getTransformTypes() {
      return transformTypes;
   }

   @Override
   public int[][] getLabels() {
      return labels;
   }

   public class220 method4377() {
      return this.field2504;
   }

}
