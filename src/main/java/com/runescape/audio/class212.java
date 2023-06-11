package com.runescape.audio;


public class class212 {

   public static void method4109(byte[] var0, int var1, byte[] var2, int var3, int var4) {
      if(var2 == var0) {
         if(var3 == var1) {
            return;
         }

         if(var3 > var1 && var3 < var4 + var1) {
            --var4;
            var1 += var4;
            var3 += var4;
            var4 = var1 - var4;

            for(var4 += 7; var1 >= var4; var2[var3--] = var0[var1--]) {
               var2[var3--] = var0[var1--];
               var2[var3--] = var0[var1--];
               var2[var3--] = var0[var1--];
               var2[var3--] = var0[var1--];
               var2[var3--] = var0[var1--];
               var2[var3--] = var0[var1--];
               var2[var3--] = var0[var1--];
            }

            for(var4 -= 7; var1 >= var4; var2[var3--] = var0[var1--]) {
               ;
            }

            return;
         }
      }

      var4 += var1;

      for(var4 -= 7; var1 < var4; var2[var3++] = var0[var1++]) {
         var2[var3++] = var0[var1++];
         var2[var3++] = var0[var1++];
         var2[var3++] = var0[var1++];
         var2[var3++] = var0[var1++];
         var2[var3++] = var0[var1++];
         var2[var3++] = var0[var1++];
         var2[var3++] = var0[var1++];
      }

      for(var4 += 7; var1 < var4; var2[var3++] = var0[var1++]) {
         ;
      }
   }

   public static void clearIntArray(int[] array, int onset, int length) {
      for(length = length + onset - 7; onset < length; array[onset++] = 0) {
         array[onset++] = 0;
         array[onset++] = 0;
         array[onset++] = 0;
         array[onset++] = 0;
         array[onset++] = 0;
         array[onset++] = 0;
         array[onset++] = 0;
      }

      for(length += 7; onset < length; array[onset++] = 0) {
         ;
      }

   }
}
