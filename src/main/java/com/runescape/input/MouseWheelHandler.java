package com.runescape.input;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 11/12/2019
 */
public final class MouseWheelHandler implements MouseWheel, MouseWheelListener {

    private int rotation = 0;

    public void addTo(Component component){
        component.addMouseWheelListener(this);
    }

    public void removeFrom(Component component){
        component.removeMouseWheelListener(this);
    }

    @Override
    public synchronized void mouseWheelMoved(MouseWheelEvent e) {
        rotation += e.getWheelRotation();
    }

    @Override
    public synchronized int useRotation() {
//        System.out.println("rotation = "+rotation);
        int rotation = this.rotation;
        this.rotation = 0;
        return rotation;
    }
}
