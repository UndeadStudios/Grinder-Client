package com.runescape.cache.graphics.widget.component;

import com.runescape.Client;
import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.input.MouseHandler;

/**
 * Created by Stan van der Bend for Empyrean at 07/08/2018.
 *
 * @author https://www.rune-server.ee/members/StanDev/
 */
public class Component {

    private Sprite sprite;
    private int x, y;
    private int offsetX, offsetY;
    private final int alpha;

    public Component(Sprite sprite, int x, int y, int alpha){
        this.sprite = sprite;
        this.x = x;
        this.y = y;
        this.alpha = alpha;
    }

    public Component(Sprite sprite, int x, int y){
        this(sprite, x, y, 200);
    }

    public void draw(){
        sprite.drawAdvancedSprite(x + offsetX, y + offsetY, alpha);
    }

    public boolean containsMouse(Client client){
        return (MouseHandler.x >= x + offsetX && MouseHandler.x <= x + offsetX + sprite.myWidth)
                && (MouseHandler.y >= y + offsetY && MouseHandler.y <= y + offsetY + sprite.myHeight);
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public Sprite getSprite() {
        return sprite;
    }
}
