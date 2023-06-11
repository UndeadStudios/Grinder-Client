package com.runescape.collection;

import com.runescape.io.Buffer;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 26/02/2020
 */
public class OSCollections {

    public static String method3238(IterableNodeHashTable var0, int var1, String var2) {
        if(var0 == null) {
            return var2;
        } else {
            ObjectNode var3 = (ObjectNode)var0.get(var1);
            return var3 == null?var2:(String)var3.obj;
        }
    }

    public static IterableNodeHashTable readStringIntParameters(Buffer buffer, IterableNodeHashTable hashTable) {
        int var2 = buffer.readUnsignedByte();
        int var3;
        if(hashTable == null) {
            var3 = method1759(var2);
            hashTable = new IterableNodeHashTable(var3);
        }

        for(var3 = 0; var3 < var2; ++var3) {
            boolean var4 = buffer.readUnsignedByte() == 1;
            int var5 = buffer.readMedium();
            Node var6;
            if(var4) {
                var6 = new ObjectNode(buffer.readStringCp1252NullTerminated());
            } else {
                var6 = new IntegerNode(buffer.readInt());
            }

            hashTable.put(var6, var5);
        }

        return hashTable;
    }


    public static int method1759(int var0) {
        --var0;
        var0 |= var0 >>> 1;
        var0 |= var0 >>> 2;
        var0 |= var0 >>> 4;
        var0 |= var0 >>> 8;
        var0 |= var0 >>> 16;
        return var0 + 1;
    }

}
