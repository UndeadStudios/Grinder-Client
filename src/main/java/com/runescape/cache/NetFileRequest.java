package com.runescape.cache;

import com.runescape.collection.DualNode;

public class NetFileRequest extends DualNode {

   public int crc;
   public byte padding;
   public IndexCache indexCache;

}
