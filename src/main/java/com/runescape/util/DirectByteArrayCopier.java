package com.runescape.util;

import java.nio.ByteBuffer;

public class DirectByteArrayCopier extends AbstractByteArrayCopier {

   public ByteBuffer directBuffer;

   @Override
   public byte[] get() {
      byte[] var1 = new byte[this.directBuffer.capacity()];
      this.directBuffer.position(0);
      this.directBuffer.get(var1);
      return var1;
   }

   @Override
   public void set(byte[] var1) {
      this.directBuffer = ByteBuffer.allocateDirect(var1.length);
      this.directBuffer.position(0);
      this.directBuffer.put(var1);
   }
}
