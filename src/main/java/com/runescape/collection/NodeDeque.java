package com.runescape.collection;

public final class NodeDeque {

    public final Node sentinel;
    private Node current;

    public NodeDeque() {
        sentinel = new Node();
        sentinel.previous = sentinel;
        sentinel.next = sentinel;
    }

    public static void method5270(Node first, Node previous) {
        if(first.next != null)
            first.remove();
        first.next = previous.next;
        first.previous = previous;
        first.next.previous = first;
        first.previous.next = first;
    }

    public void addFirst(Node node) {
        if (node.next != null)
            node.remove();
        node.next = sentinel.next;
        node.previous = sentinel;
        node.next.previous = node;
        node.previous.next = node;
    }

    public void addLast(Node node) {
        if (node.next != null)
            node.remove();
        node.next = sentinel;
        node.previous = sentinel.previous;
        node.next.previous = node;
        node.previous.next = node;
    }

   public final void method894(Node class3, Node class3_27_) {
        if (class3.previous != null)
            class3.remove();
        class3.next = class3_27_;
        class3.previous = class3_27_.previous;
        class3.previous.next = class3;
        class3.next.previous = class3;
    }
    public Node removeFirst() {
        Node var1 = this.sentinel.next;
        if(var1 == this.sentinel) {
            return null;
        } else {
            var1.remove();
            return var1;
        }
    }
    public Node removeLast() {
        Node node = sentinel.previous;
        if (node == sentinel) {
            return null;
        } else {
            node.remove();
            return node;
        }
    }

    public Node last() {
        Node node = sentinel.previous;
        if (node == sentinel) {
            current = null;
            return null;
        } else {
            current = node.previous;
            return node;
        }
    }

    public Node first() {
        Node node = sentinel.next;
        if (node == sentinel) {
            current = null;
            return null;
        } else {
            current = node.next;
            return node;
        }
    }

    public Node previous() {
        Node node = current;
        if (node == sentinel) {
            current = null;
            return null;
        } else {
            current = node.previous;
            return node;
        }
    }

    public Node next() {
        Node node = current;
        if (node == sentinel) {
            current = null;
            return null;
        }
        current = node.next;
        return node;
    }

    public void clear() {
        if (sentinel.previous == sentinel)
            return;
        do {
            Node node = sentinel.previous;
            if (node == sentinel)
                return;
            node.remove();
        } while (true);
    }
}
