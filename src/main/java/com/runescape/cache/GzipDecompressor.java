package com.runescape.cache;

import com.runescape.io.Buffer;

import java.util.zip.Inflater;


public class GzipDecompressor {


   Inflater inflater;


   GzipDecompressor(int var1, int var2, int var3) {
   }

   public GzipDecompressor() {
      this(-1, 1000000, 1000000);
   }

   public void decompress(Buffer var1, byte[] var2) {

      if(var1.array[var1.index] == 31 && var1.array[var1.index + 1] == -117) {
         if(this.inflater == null) {
            this.inflater = new Inflater(true);
         }

         try {
            this.inflater.setInput(var1.array, var1.index + 10, var1.array.length - (var1.index + 8 + 10));
            this.inflater.inflate(var2);
         } catch (Exception var4) {
            this.inflater.reset();
            throw new RuntimeException("");
         }

         this.inflater.reset();
      } else {
         throw new RuntimeException("");
      }
   }

}
