package com.runescape.util;

public final class ChunkUtil {

    public static int getRotatedMapChunkX(int i, int j, int k) {
        i &= 3;
        if (i == 0)
            return k;
        if (i == 1)
            return j;
        if (i == 2)
            return 7 - k;
        else
            return 7 - j;
    }

    public static int getRotatedMapChunkY(int i, int j, int l) {
        j &= 3;
        if (j == 0)
            return i;
        if (j == 1)
            return 7 - l;
        if (j == 2)
            return 7 - i;
        else
            return l;
    }

    public static int calculateXAfterRotation(int originalXInChunk, int originalYInChunk, int sizeX, int sizeY, int rotation) {
        rotation &= 3;
        if (rotation == 0)
            return originalXInChunk;
        if (rotation == 1)
            return originalYInChunk;
        if (rotation == 2)
            return 7 - originalXInChunk - (sizeX - 1);
        else
            return 7 - originalYInChunk - (sizeY - 1);
    }

    public static int calculateYAfterRotation(int originalXInChunk, int originalYInChunk, int sizeX, int sizeY, int rotation) {
        rotation &= 3;
        if (rotation == 0)
            return originalYInChunk;
        if (rotation == 1)
            return 7 - originalXInChunk - (sizeX - 1);
        if (rotation == 2)
            return 7 - originalYInChunk - (sizeY - 1);
        else
            return originalXInChunk;
    }

}
