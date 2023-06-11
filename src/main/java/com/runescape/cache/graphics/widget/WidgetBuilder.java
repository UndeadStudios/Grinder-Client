package com.runescape.cache.graphics.widget;

import com.runescape.cache.graphics.widget.component.ChildComponent;
import com.runescape.cache.graphics.widget.component.TextComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stan van der Bend for Empyrean at 19/06/2018.
 *
 * @author https://www.rune-server.ee/members/StanDev/
 */
public class WidgetBuilder{

    private final WidgetAlignment allignment;
    private final int id;
    private final int width, height;
    private int nextChildID;
    private final Map<Integer, ChildComponent> children = new HashMap<>();

    public WidgetBuilder(int id, int width, int height, WidgetAlignment allignment) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.allignment = allignment;
        nextChildID = id;
    }

    public void addChild(ChildComponent childComponent){
        childComponent.parent = id;
        childComponent.id = ++nextChildID;
        children.put(nextChildID, childComponent);
    }

    public void update(){
        int childIndex = 0;
        int offsetX = allignment.getXPosition(width);
        int offsetY = allignment.getYPosition(height);

        for(Map.Entry<Integer, ChildComponent> entry : children.entrySet()) {

            final ChildComponent childComponent = entry.getValue();

            if(childComponent instanceof TextComponent) {
                if (childComponent.getDefaultText() == null)
                    childComponent.setDefaultText("setChild[" + entry.getKey() + "]");
                if(childComponent.centerText) {
                    childComponent.width = width;
                    childComponent.height = height;
                }
            }
            Widget.interfaceCache[id].child(childIndex++, entry.getKey(), offsetX + childComponent.getRelativeX(), offsetY + childComponent.getRelativeY());
        }
    }

    public Widget create(){

        final Widget widget = Widget.addInterface(id);

        widget.totalChildren(children.size());

        int childIndex = 0;
        int offsetX = allignment.getXPosition(width);
        int offsetY = allignment.getYPosition(height);

        for(Map.Entry<Integer, ChildComponent> entry : children.entrySet()) {

            final ChildComponent childComponent = entry.getValue();

            if(childComponent instanceof TextComponent) {
                if (childComponent.getDefaultText() == null)
                    childComponent.setDefaultText("setChild[" + entry.getKey() + "]");
                if(childComponent.centerText) {
                    childComponent.width = width;
                    childComponent.height = height;
                }
            }
            Widget.interfaceCache[entry.getKey()] = childComponent;

            widget.child(childIndex++, entry.getKey(), offsetX + childComponent.getRelativeX(), offsetY + childComponent.getRelativeY());
        }
        return widget;
    }
}
