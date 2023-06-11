package com.runescape.util;

import com.runescape.cache.AbstractIndexCache;
import com.runescape.cache.Bzip2Decompressor;
import com.runescape.io.Buffer;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 14/02/2020
 */
public class ByteArrayUtil {

    public static synchronized byte[] method862(int var0) {
        return ByteArrayPool.ByteArrayPool_get(var0, false);
    }

    public static byte[] byteArrayFromObject(Object var0, boolean var1) {
        if(var0 == null) {
            return null;
        } else if(var0 instanceof byte[]) {
            byte[] var6 = (byte[])((byte[])var0);
            if(var1) {
                int var4 = var6.length;
                byte[] var5 = new byte[var4];
                System.arraycopy(var6, 0, var5, 0, var4);
                return var5;
            } else {
                return var6;
            }
        } else if(var0 instanceof AbstractByteArrayCopier) {
            AbstractByteArrayCopier var2 = (AbstractByteArrayCopier)var0;
            return var2.get();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static byte[] decompressBytes(byte[] var0) {
        Buffer buffer = new Buffer(var0);
        int type = buffer.readUnsignedByte();
        int length = buffer.readInt();
        if(length < 0 || AbstractIndexCache.always0 != 0 && length > AbstractIndexCache.always0) {
            throw new RuntimeException();
        } else if(type == 0) {
            byte[] var4 = new byte[length];
            buffer.copyArray(var4, 0, length);
            return var4;
        } else {
            int var6 = buffer.readInt();
            if(var6 < 0 || AbstractIndexCache.always0 != 0 && var6 > AbstractIndexCache.always0) {
                throw new RuntimeException();
            } else {
                byte[] var5 = new byte[var6];
                if(type == 1) {
                    Bzip2Decompressor.Bzip2Decompressor_decompress(var5, var6, var0, length, 9);
                } else {
                    AbstractIndexCache.gzipDecompressor.decompress(buffer, var5);
                }

                return var5;
            }
        }
    }

    public static Object byteArrayToObject(byte[] var0, boolean var1) {
        if(var0 == null) {
            return null;
        } else {
            if(var0.length > 136 && !AbstractByteArrayCopier.directBufferUnavailable) {
                try {
                    DirectByteArrayCopier var2 = new DirectByteArrayCopier();
                    var2.set(var0);
                    return var2;
                } catch (Throwable var3) {
                    AbstractByteArrayCopier.directBufferUnavailable = true;
                }
            }

            return var0;
        }
    }
}
