package com.runescape.cache;

import com.grinder.client.util.Log;

import java.io.EOFException;
import java.io.IOException;

public final class IndexStore {

   static byte[] IndexStore_buffer;

   final BufferedFile dataFile;
   final BufferedFile indexFile;

   int index;

   int maxEntrySize;

   static {
      IndexStore_buffer = new byte[520];
   }

   public IndexStore(int index, BufferedFile dataFile, BufferedFile indexFile, int maxEntrySize) {
      this.maxEntrySize = 65000;
      this.index = index;
      this.dataFile = dataFile;
      this.indexFile = indexFile;
      this.maxEntrySize = maxEntrySize;
   }

   public byte[] read(int entryIndex) {

      synchronized(dataFile) {
         try {
            if(this.indexFile.length() < (long)(entryIndex * 6 + 6)) {
               return null;
            } else {
               this.indexFile.seek(entryIndex * 6);
               this.indexFile.read(IndexStore_buffer, 0, 6);
               int var3 = ((IndexStore_buffer[0] & 255) << 16) + (IndexStore_buffer[2] & 255) + ((IndexStore_buffer[1] & 255) << 8);
               int var4 = (IndexStore_buffer[5] & 255) + ((IndexStore_buffer[3] & 255) << 16) + ((IndexStore_buffer[4] & 255) << 8);
               if(var3 > this.maxEntrySize) {
                  return null;
               } else if(var4 <= 0 || (long)var4 > this.dataFile.length() / 520L) {
                  return null;
               } else {
                  byte[] var5 = new byte[var3];
                  int var6 = 0;
                  int var7 = 0;

                  while(var6 < var3) {
                     if(var4 == 0) {
                        return null;
                     }

                     this.dataFile.seek(var4 * 520);
                     int var8 = var3 - var6;
                     if(var8 > 512) {
                        var8 = 512;
                     }

                     this.dataFile.read(IndexStore_buffer, 0, var8 + 8);
                     int var9 = (IndexStore_buffer[1] & 255) + ((IndexStore_buffer[0] & 255) << 8);
                     int var10 = (IndexStore_buffer[3] & 255) + ((IndexStore_buffer[2] & 255) << 8);
                     int var11 = ((IndexStore_buffer[5] & 255) << 8) + ((IndexStore_buffer[4] & 255) << 16) + (IndexStore_buffer[6] & 255);
                     int var12 = IndexStore_buffer[7] & 255;
                     if(var9 == entryIndex && var7 == var10 && var12 == this.index) {
                        if(var11 >= 0 && (long)var11 <= this.dataFile.length() / 520L) {
                           for(int var13 = 0; var13 < var8; ++var13) {
                              var5[var6++] = IndexStore_buffer[var13 + 8];
                           }
                           var4 = var11;
                           ++var7;
                           continue;
                        }
                        return null;
                     }
                     return null;
                  }
                  return var5;
               }
            }
         } catch (IOException e) {
            Log.error("Failed to read entry "+entryIndex+" from index "+index+"", e);
            return null;
         }
      }
   }

   public boolean write(int entryIndex, byte[] bytes, int length) {
      synchronized(this.dataFile) {
         if(length >= 0 && length <= this.maxEntrySize) {

            boolean success = this.write0(entryIndex, bytes, length, true);

            if(!success)
               success = this.write0(entryIndex, bytes, length, false);

            return success;
         } else
            throw new IllegalArgumentException("Failed to write entry "+entryIndex+" to index "+index+", entry length "+length+" exceeds "+maxEntrySize+"!");
      }
   }

   boolean write0(int entryIndex, byte[] bytes, int length, boolean firstTry) {
      synchronized(this.dataFile) {
         try {
            int var6;
            boolean success;
            if(firstTry) {

               if(this.indexFile.length() < (long)(entryIndex * 6 + 6))
                  return false;

               this.indexFile.seek(entryIndex * 6);
               this.indexFile.read(IndexStore_buffer, 0, 6);
               var6 = (IndexStore_buffer[5] & 255) + ((IndexStore_buffer[3] & 255) << 16) + ((IndexStore_buffer[4] & 255) << 8);

               if(var6 <= 0 || (long)var6 > this.dataFile.length() / 520L)
                  return false;

            } else {
               var6 = (int)((this.dataFile.length() + 519L) / 520L);
               if(var6 == 0) {
                  var6 = 1;
               }
            }

            IndexStore_buffer[0] = (byte)(length >> 16);
            IndexStore_buffer[1] = (byte)(length >> 8);
            IndexStore_buffer[2] = (byte)length;
            IndexStore_buffer[3] = (byte)(var6 >> 16);
            IndexStore_buffer[4] = (byte)(var6 >> 8);
            IndexStore_buffer[5] = (byte)var6;
            this.indexFile.seek(entryIndex * 6);
            this.indexFile.write(IndexStore_buffer, 0, 6);
            int var7 = 0;
            int var8 = 0;

            while(true) {
               if(var7 < length) {
                  label142: {
                     int var9 = 0;
                     int var14;
                     if(firstTry) {
                        this.dataFile.seek(var6 * 520);

                        try {
                           this.dataFile.read(IndexStore_buffer, 0, 8);
                        } catch (EOFException var16) {
                           break label142;
                        }

                        var14 = (IndexStore_buffer[1] & 255) + ((IndexStore_buffer[0] & 255) << 8);
                        int var11 = (IndexStore_buffer[3] & 255) + ((IndexStore_buffer[2] & 255) << 8);
                        var9 = ((IndexStore_buffer[5] & 255) << 8) + ((IndexStore_buffer[4] & 255) << 16) + (IndexStore_buffer[6] & 255);
                        int var12 = IndexStore_buffer[7] & 255;
                        if(var14 != entryIndex || var11 != var8 || var12 != this.index) {
                           success = false;
                           return success;
                        }

                        if(var9 < 0 || (long)var9 > this.dataFile.length() / 520L) {
                           success = false;
                           return success;
                        }
                     }

                     if(var9 == 0) {
                        firstTry = false;
                        var9 = (int)((this.dataFile.length() + 519L) / 520L);
                        if(var9 == 0) {
                           ++var9;
                        }

                        if(var6 == var9) {
                           ++var9;
                        }
                     }

                     if(length - var7 <= 512) {
                        var9 = 0;
                     }

                     IndexStore_buffer[0] = (byte)(entryIndex >> 8);
                     IndexStore_buffer[1] = (byte)entryIndex;
                     IndexStore_buffer[2] = (byte)(var8 >> 8);
                     IndexStore_buffer[3] = (byte)var8;
                     IndexStore_buffer[4] = (byte)(var9 >> 16);
                     IndexStore_buffer[5] = (byte)(var9 >> 8);
                     IndexStore_buffer[6] = (byte)var9;
                     IndexStore_buffer[7] = (byte)this.index;
                     this.dataFile.seek(var6 * 520);
                     this.dataFile.write(IndexStore_buffer, 0, 8);
                     var14 = length - var7;
                     if(var14 > 512) {
                        var14 = 512;
                     }

                     this.dataFile.write(bytes, var7, var14);
                     var7 += var14;
                     var6 = var9;
                     ++var8;
                     continue;
                  }
               }
               return true;
            }
         } catch (IOException trace) {
            Log.error("Failed to write data to entry "+entryIndex+" in index "+index+" -> "+(firstTry ? "retrying" : "again"), trace);
            return false;
         }
      }
   }
}
