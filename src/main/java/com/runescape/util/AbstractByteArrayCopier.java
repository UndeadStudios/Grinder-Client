package com.runescape.util;


public abstract class AbstractByteArrayCopier {

   public static boolean directBufferUnavailable;

   static {
      directBufferUnavailable = false;
   }

   public abstract byte[] get();

   public abstract void set(byte[] var1);

}
