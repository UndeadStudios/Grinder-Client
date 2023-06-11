package com.runescape.entity;

import com.runescape.scene.DynamicObject;

/**
 * ObjectGenre = 2
 */
public final class GameObject {

    public int plane;
    public int z;
    public int centerX;
    public int centerY;
    public Renderable renderable;
    public int orientation;
    public int startX;
    public int endX;
    public int startY;
    public int endY;
    public int anInt527;
    public int lastRenderTick;
    public long tag;
    /**
     * mask = (byte)((objectRotation << 6) + objectType);
     */

    public int flags;


    public int getKey() {
        return DynamicObject.get_object_key(tag);
    }

    public long getOpcode() {
        return DynamicObject.get_object_opcode(tag);
    }
}
