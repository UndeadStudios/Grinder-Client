package com.runescape.collection;

public class Node {

    public long key;
    public Node previous;
    public Node next;

    public final void remove() {
        if (next != null) {
            next.previous = previous;
            previous.next = next;
            previous = null;
            next = null;
        }
    }

    public boolean hasNext() {
        return this.next != null;
    }
}
