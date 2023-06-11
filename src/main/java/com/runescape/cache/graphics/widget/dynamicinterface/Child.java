package com.runescape.cache.graphics.widget.dynamicinterface;

public class Child {
    int rsi;
    int x;
    int y;

    public Child(int rsi, int x, int y) {
        this.rsi = rsi;
        this.x = x;
        this.y = y;
    }

    public int getInterface() {
        return rsi;
    }
    public void settInterface(int rsi) {
        this.rsi = rsi;
    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public Child duplicate() {
        return new Child(rsi, x, y);
    }
}
