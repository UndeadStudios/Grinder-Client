package com.runescape.cache;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.SyncFailedException;

public final class AccessFile {

   RandomAccessFile file;
   long capacity;
   long index;

   public AccessFile(File var1, String var2, long var3) throws IOException {
      if(var3 == -1L) {
         var3 = Long.MAX_VALUE;
      }

      if(var1.length() >= var3) {
         var1.delete();
      }

      this.file = new RandomAccessFile(var1, var2);
      this.capacity = var3;
      this.index = 0L;
      int var5 = this.file.read();
      if(var5 != -1 && !var2.equals("r")) {
         this.file.seek(0L);
         this.file.write(var5);
      }

      this.file.seek(0L);
   }

   final void seek(long var1) throws IOException {
      this.file.seek(var1);
      this.index = var1;
   }

   public final void write(byte[] var1, int var2, int var3) throws IOException {
      if((long)var3 + this.index > this.capacity) {
         this.file.seek(this.capacity + 1L);
         this.file.write(1);
         throw new EOFException();
      } else {
         this.file.write(var1, var2, var3);
         this.index += var3;
      }
   }

   public final void close() throws IOException {
      this.closeSync(false);
   }

   public final void closeSync(boolean var1) throws IOException {
      if(this.file != null) {
         if(var1) {
            try {
               this.file.getFD().sync();
            } catch (SyncFailedException var3) {
            }
         }

         this.file.close();
         this.file = null;
      }

   }

   public final long length() throws IOException {
      return this.file.length();
   }

   public final int read(byte[] var1, int var2, int var3) throws IOException {
      int var4 = this.file.read(var1, var2, var3);
      if(var4 > 0) {
         this.index += var4;
      }

      return var4;
   }

   protected void __finalize_211() throws Throwable {
      if(this.file != null) {
         System.out.println();
         this.close();
      }

   }
}
