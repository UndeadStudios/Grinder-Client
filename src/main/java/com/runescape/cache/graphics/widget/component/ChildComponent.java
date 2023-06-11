package com.runescape.cache.graphics.widget.component;


import com.runescape.cache.graphics.widget.Widget;

/**
 * Created by Stan van der Bend for Empyrean at 19/06/2018.
 *
 * @author https://www.rune-server.ee/members/StanDev/
 */
public class ChildComponent extends Widget {

    private int relativeX;
    private int relativeY;

    public ChildComponent(int relativeX, int relativeY) {
        this.relativeX = relativeX;
        this.relativeY = relativeY;
    }

    public void register(int uniqueID, Widget parent, int childIndex){
        this.id = uniqueID;
        this.parent = parent.id;
        Widget.interfaceCache[uniqueID] = this;
        Widget.setBounds(uniqueID, relativeX, relativeY, childIndex, parent);
    }

    public int getRelativeX() {
        return relativeX;
    }

    public int getRelativeY() {
        return relativeY;
    }

}
