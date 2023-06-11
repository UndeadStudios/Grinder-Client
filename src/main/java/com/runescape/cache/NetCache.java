package com.runescape.cache;

import com.runescape.collection.DualNodeDeque;
import com.runescape.collection.NodeHashTable;
import com.runescape.io.Buffer;
import com.runescape.net.AbstractSocket;

import java.util.zip.CRC32;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 13/02/2020
 */
public class NetCache {

    public static AbstractSocket socket;
    public static int totalTimeMillisPassed;
    public static long lastTimeMillis;
    public static NodeHashTable pendingPriorityWrites;
    public static int pendingPriorityWritesCount;
    public static NodeHashTable pendingPriorityResponses;
    public static int pendingPriorityResponsesCount;
    public static DualNodeDeque pendingWritesQueue;
    public static NodeHashTable pendingWrites;
    public static int pendingWritesCount;
    public static NodeHashTable pendingResponses;
    public static int pendingResponsesCount;
    public static Buffer responseHeaderBuffer;
    public static int responseExpectedBytes;
    public static CRC32 crc;
    public static IndexCache[] indexCaches;
    public static byte randomValue;
    public static int crcMismatches;
    public static int ioExceptions;

    static {
        totalTimeMillisPassed = 0;
        pendingPriorityWrites = new NodeHashTable(4096);
        pendingPriorityWritesCount = 0;
        pendingPriorityResponses = new NodeHashTable(32);
        pendingPriorityResponsesCount = 0;
        pendingWritesQueue = new DualNodeDeque();
        pendingWrites = new NodeHashTable(4096);
        pendingWritesCount = 0;
        pendingResponses = new NodeHashTable(4096);
        pendingResponsesCount = 0;
        responseHeaderBuffer = new Buffer(8);
        responseExpectedBytes = 0;
        crc = new CRC32();
        indexCaches = new IndexCache[256];
        randomValue = 0;
        crcMismatches = 0;
        ioExceptions = 0;
    }
}
