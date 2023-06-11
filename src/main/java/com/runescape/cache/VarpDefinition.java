package com.runescape.cache;

import com.runescape.collection.DualNode;
import com.runescape.collection.EvictingDualNodeHashTable;
import com.runescape.io.Buffer;

/**
 * @version 1.0
 * @since 14/02/2020
 */
public class VarpDefinition extends DualNode {

    public static AbstractIndexCache varpDefinitionCacheIndex;
    public static EvictingDualNodeHashTable VarpDefinition_cached;

    public int type;

    static {
        VarpDefinition_cached = new EvictingDualNodeHashTable(64);
    }

    public VarpDefinition() {
        this.type = 0;
    }

    public void read(Buffer buffer) {
        while(true) {

            int var2 = buffer.getUnsignedByte();

            if(var2 == 0)
                return;

            this.readNext(buffer, var2);
        }
    }

    public void readNext(Buffer buffer, int value) {
        if(value == 5)
            this.type = buffer.getUnsignedLEShort();
    }
}
