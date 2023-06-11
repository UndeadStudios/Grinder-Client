package com.runescape.cache;

import java.io.EOFException;
import java.io.IOException;

public class BufferedFile {
   AccessFile accessFile;
   byte[] readBuffer;
   long __q;
   int __w;
   byte[] writeBuffer;
   long __u;
   int __g;
   long __l;
   long __e;
   long capacity;
   long __d;

   public BufferedFile(AccessFile var1, int var2, int var3) throws IOException {
      this.__q = -1L;
      this.__u = -1L;
      this.__g = 0;
      this.accessFile = var1;
      this.capacity = this.__e = var1.length();
      this.readBuffer = new byte[var2];
      this.writeBuffer = new byte[var3];
      this.__l = 0L;
   }

   public void close() throws IOException {
      this.flush();
      this.accessFile.close();
   }

   public void seek(long var1) throws IOException {
      if(var1 < 0L) {
         throw new IOException("");
      } else {
         this.__l = var1;
      }
   }

   public long length() {
      return this.capacity;
   }

   public void readFill(byte[] var1) throws IOException {
      this.read(var1, 0, var1.length);
   }
   public void read(byte[] var1, int var2, int var3) throws IOException {
      try {
         if(var3 + var2 > var1.length) {
            throw new ArrayIndexOutOfBoundsException(var3 + var2 - var1.length);
         }

         if(-1L != this.__u && this.__l >= this.__u && this.__l + (long)var3 <= this.__u + (long)this.__g) {
            System.arraycopy(this.writeBuffer, (int)(this.__l - this.__u), var1, var2, var3);
            this.__l += (long)var3;
            return;
         }

         long var4 = this.__l;
         int var7 = var3;
         int var8;
         if(this.__l >= this.__q && this.__l < this.__q + (long)this.__w) {
            var8 = (int)((long)this.__w - (this.__l - this.__q));
            if(var8 > var3) {
               var8 = var3;
            }

            System.arraycopy(this.readBuffer, (int)(this.__l - this.__q), var1, var2, var8);
            this.__l += (long)var8;
            var2 += var8;
            var3 -= var8;
         }

         if(var3 > this.readBuffer.length) {
            this.accessFile.seek(this.__l);

            for(this.__d = this.__l; var3 > 0; var3 -= var8) {
               var8 = this.accessFile.read(var1, var2, var3);
               if(var8 == -1) {
                  break;
               }

               this.__d += (long)var8;
               this.__l += (long)var8;
               var2 += var8;
            }
         } else if(var3 > 0) {
            this.load();
            var8 = var3;
            if(var3 > this.__w) {
               var8 = this.__w;
            }

            System.arraycopy(this.readBuffer, 0, var1, var2, var8);
            var2 += var8;
            var3 -= var8;
            this.__l += (long)var8;
         }

         if(-1L != this.__u) {
            if(this.__u > this.__l && var3 > 0) {
               var8 = var2 + (int)(this.__u - this.__l);
               if(var8 > var3 + var2) {
                  var8 = var3 + var2;
               }

               while(var2 < var8) {
                  var1[var2++] = 0;
                  --var3;
                  ++this.__l;
               }
            }

            long var13 = -1L;
            long var10 = -1L;
            if(this.__u >= var4 && this.__u < var4 + (long)var7) {
               var13 = this.__u;
            } else if(var4 >= this.__u && var4 < this.__u + (long)this.__g) {
               var13 = var4;
            }

            if((long)this.__g + this.__u > var4 && this.__u + (long)this.__g <= var4 + (long)var7) {
               var10 = (long)this.__g + this.__u;
            } else if((long)var7 + var4 > this.__u && (long)var7 + var4 <= this.__u + (long)this.__g) {
               var10 = (long)var7 + var4;
            }

            if(var13 > -1L && var10 > var13) {
               int var12 = (int)(var10 - var13);
               System.arraycopy(this.writeBuffer, (int)(var13 - this.__u), var1, (int)(var13 - var4) + var2, var12);
               if(var10 > this.__l) {
                  var3 = (int)((long)var3 - (var10 - this.__l));
                  this.__l = var10;
               }
            }
         }
      } catch (IOException var16) {
         this.__d = -1L;
         throw var16;
      }

      if(var3 > 0) {
         throw new EOFException();
      }
   }

   void load() throws IOException {
      this.__w = 0;
      if(this.__d != this.__l) {
         this.accessFile.seek(this.__l);
         this.__d = this.__l;
      }

      int var1;
      for(this.__q = this.__l; this.__w < this.readBuffer.length; this.__w += var1) {
         var1 = this.accessFile.read(this.readBuffer, this.__w, this.readBuffer.length - this.__w);
         if(var1 == -1) {
            break;
         }

         this.__d += (long)var1;
      }

   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      try {
         if(this.__l + (long)var3 > this.capacity) {
            this.capacity = this.__l + (long)var3;
         }

         if(-1L != this.__u && (this.__l < this.__u || this.__l > this.__u + (long)this.__g)) {
            this.flush();
         }

         if(this.__u != -1L && (long)var3 + this.__l > this.__u + (long)this.writeBuffer.length) {
            int var4 = (int)((long)this.writeBuffer.length - (this.__l - this.__u));
            System.arraycopy(var1, var2, this.writeBuffer, (int)(this.__l - this.__u), var4);
            this.__l += (long)var4;
            var2 += var4;
            var3 -= var4;
            this.__g = this.writeBuffer.length;
            this.flush();
         }

         if(var3 <= this.writeBuffer.length) {
            if(var3 > 0) {
               if(this.__u == -1L) {
                  this.__u = this.__l;
               }

               System.arraycopy(var1, var2, this.writeBuffer, (int)(this.__l - this.__u), var3);
               this.__l += (long)var3;
               if(this.__l - this.__u > (long)this.__g) {
                  this.__g = (int)(this.__l - this.__u);
               }

            }
         } else {
            if(this.__d != this.__l) {
               this.accessFile.seek(this.__l);
               this.__d = this.__l;
            }

            this.accessFile.write(var1, var2, var3);
            this.__d += (long)var3;
            if(this.__d > this.__e) {
               this.__e = this.__d;
            }

            long var9 = -1L;
            long var6 = -1L;
            if(this.__l >= this.__q && this.__l < (long)this.__w + this.__q) {
               var9 = this.__l;
            } else if(this.__q >= this.__l && this.__q < (long)var3 + this.__l) {
               var9 = this.__q;
            }

            if(this.__l + (long)var3 > this.__q && (long)var3 + this.__l <= (long)this.__w + this.__q) {
               var6 = this.__l + (long)var3;
            } else if((long)this.__w + this.__q > this.__l && this.__q + (long)this.__w <= (long)var3 + this.__l) {
               var6 = (long)this.__w + this.__q;
            }

            if(var9 > -1L && var6 > var9) {
               int var8 = (int)(var6 - var9);
               System.arraycopy(var1, (int)(var9 + (long)var2 - this.__l), this.readBuffer, (int)(var9 - this.__q), var8);
            }

            this.__l += (long)var3;
         }
      } catch (IOException var12) {
         this.__d = -1L;
         throw var12;
      }
   }

   void flush() throws IOException {
      if(this.__u != -1L) {
         if(this.__u != this.__d) {
            this.accessFile.seek(this.__u);
            this.__d = this.__u;
         }

         this.accessFile.write(this.writeBuffer, 0, this.__g);
         this.__d += (long)(this.__g * 1290782301) * -1558233611L;
         if(this.__d > this.__e) {
            this.__e = this.__d;
         }

         long var1 = -1L;
         long var3 = -1L;
         if(this.__u >= this.__q && this.__u < (long)this.__w + this.__q) {
            var1 = this.__u;
         } else if(this.__q >= this.__u && this.__q < this.__u + (long)this.__g) {
            var1 = this.__q;
         }

         if(this.__u + (long)this.__g > this.__q && this.__u + (long)this.__g <= this.__q + (long)this.__w) {
            var3 = this.__u + (long)this.__g;
         } else if((long)this.__w + this.__q > this.__u && (long)this.__w + this.__q <= (long)this.__g + this.__u) {
            var3 = this.__q + (long)this.__w;
         }

         if(var1 > -1L && var3 > var1) {
            int var5 = (int)(var3 - var1);
            System.arraycopy(this.writeBuffer, (int)(var1 - this.__u), this.readBuffer, (int)(var1 - this.__q), var5);
         }

         this.__u = -1L;
         this.__g = 0;
      }

   }
}
