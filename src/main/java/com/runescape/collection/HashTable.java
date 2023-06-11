package com.runescape.collection;

import com.grinder.client.util.Log;

final class HashTable {

    private final int bucketCount;
    private final Node[] buckets;

    /**
     * Creates the HashTable with the specified size.
     */
    public HashTable() {
        int size = 1024;// was parameter
        bucketCount = size;
        buckets = new Node[size];
        for (int index = 0; index < size; index++) {
            Node node = buckets[index] = new Node();
            node.previous = node;
            node.next = node;
        }
    }

    /**
     * Gets the {@link Node} with the specified {@code key} from this
     * HashTable.
     *
     * @param key The key.
     * @return The Linkable, or {@code null} if this HashTable does not contain
     * an associated for the specified key.
     */
    public Node get(long key) {
        Node node = buckets[(int) (key & (long) (bucketCount - 1))];
        for (Node next = node.previous; next != node; next = next.previous)
            if (next.key == key)
                return next;

        return null;
    }

    /**
     * Associates the specified {@link Node} with the specified {@code key}.
     *
     * @param key      The key.
     * @param node The Linkable.
     */
    public void put(Node node, long key) {
        try {
            if (node.next != null)
                node.remove();
            Node current = buckets[(int) (key & (long) (bucketCount - 1))];
            node.next = current.next;
            node.previous = current;
            node.next.previous = node;
            node.previous.next = node;
            node.key = key;
            return;
        } catch (RuntimeException runtimeexception) {
            Log.error("91499, " + node + ", " + key + ", "
                    + (byte) 7, runtimeexception);
        }
        throw new RuntimeException();
    }
}
