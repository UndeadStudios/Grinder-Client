package com.runescape.util;


public class ByteArrayPool {

   static int ByteArrayPool_smallCount;
   static int ByteArrayPool_mediumCount;
   static int ByteArrayPool_largeCount;
   static byte[][] ByteArrayPool_small;
   static byte[][] ByteArrayPool_medium;
   static byte[][] ByteArrayPool_large;
   static int[] firstSizes;
   static int[] emptyFirstSizes;
   static byte[][][] secondSizes;

   static {
      ByteArrayPool_smallCount = 0;
      ByteArrayPool_mediumCount = 0;
      ByteArrayPool_largeCount = 0;
      ByteArrayPool_small = new byte[1000][];
      ByteArrayPool_medium = new byte[250][];
      ByteArrayPool_large = new byte[50][];
   }

   public static synchronized byte[] ByteArrayPool_get(int var0, boolean var1) {
      byte[] var2;
      if(var0 != 100) {
         if(var0 < 100) {
            ;
         }
      } else if(ByteArrayPool_smallCount > 0) {
         var2 = ByteArrayPool_small[--ByteArrayPool_smallCount];
         ByteArrayPool_small[ByteArrayPool_smallCount] = null;
         return var2;
      }

      if(var0 != 5000) {
         if(var0 < 5000) {
            ;
         }
      } else if(ByteArrayPool_mediumCount > 0) {
         var2 = ByteArrayPool_medium[--ByteArrayPool_mediumCount];
         ByteArrayPool_medium[ByteArrayPool_mediumCount] = null;
         return var2;
      }

      if(var0 != 30000) {
         if(var0 < 30000) {
            ;
         }
      } else if(ByteArrayPool_largeCount > 0) {
         var2 = ByteArrayPool_large[--ByteArrayPool_largeCount];
         ByteArrayPool_large[ByteArrayPool_largeCount] = null;
         return var2;
      }

      if(secondSizes != null) {
         for(int var4 = 0; var4 < firstSizes.length; ++var4) {
            if(firstSizes[var4] != var0) {
               if(var0 < firstSizes[var4]) {
                  ;
               }
            } else if(emptyFirstSizes[var4] > 0) {
               byte[] var3 = secondSizes[var4][--emptyFirstSizes[var4]];
               secondSizes[var4][emptyFirstSizes[var4]] = null;
               return var3;
            }
         }
      }

      return new byte[var0];
   }
   public static void setUp(int[] sizes, int[] sizes2) {
      if(sizes != null && sizes2 != null) {
         firstSizes = sizes;
         emptyFirstSizes = new int[sizes.length];
         secondSizes = new byte[sizes.length][][];

         for(int var2 = 0; var2 < firstSizes.length; ++var2) {
            secondSizes[var2] = new byte[sizes2[var2]][];
         }

      } else {
         firstSizes = null;
         emptyFirstSizes = null;
         secondSizes = null;
      }
   }

   static synchronized void add(byte[] array) {
      if(array.length == 100 && ByteArrayPool_smallCount < 1000) {
         ByteArrayPool_small[++ByteArrayPool_smallCount - 1] = array;
      } else if(array.length == 5000 && ByteArrayPool_mediumCount < 250) {
         ByteArrayPool_medium[++ByteArrayPool_mediumCount - 1] = array;
      } else if(array.length == 30000 && ByteArrayPool_largeCount < 50) {
         ByteArrayPool_large[++ByteArrayPool_largeCount - 1] = array;
      } else {
         if(secondSizes != null) {
            for(int var1 = 0; var1 < firstSizes.length; ++var1) {
               if(array.length == firstSizes[var1] && emptyFirstSizes[var1] < secondSizes[var1].length) {
                  secondSizes[var1][emptyFirstSizes[var1]++] = array;
                  return;
               }
            }
         }

      }
   }
}
