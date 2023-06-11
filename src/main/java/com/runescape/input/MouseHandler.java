package com.runescape.input;

import com.runescape.clock.Time;

import java.awt.event.*;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 11/12/2019
 */
public class MouseHandler implements MouseListener, MouseMotionListener, FocusListener {

    public static final MouseHandler instance;
    public static volatile int idleCycles;
    public static volatile int currentButton, currentButton0;
    public static volatile int x, x0;
    public static volatile int y, y0;
    public static volatile long millis, millis0;
    public static volatile int lastButton, lastButton0;
    public static volatile int lastButton1, lastButton1_0;
    public static volatile int lastButton2, lastButton2_0;
    public static volatile int lastPressedX, lastPressedX0;
    public static volatile int lastPressedY, lastPressedY0;
    public static volatile int lastClickedX, lastClickedX0;
    public static volatile int lastClickedY, lastClickedY0;
    public static volatile long lastPressedTimeMillis, lastPressedTimeMillis0;
    public static volatile int mouseWheelX,  mouseWheelX0;
    public static volatile int mouseWheelY,  mouseWheelY0;
    public static volatile int mouseWheelDragX,  mouseWheelDragX0;
    public static volatile int mouseWheelDragY,  mouseWheelDragY0;
    public static volatile boolean mouseWheelDown, mouseWheelDown0;


    static{
        instance = new MouseHandler();
        idleCycles = 0;
        currentButton = currentButton0 = 0;
        x = x0 = 0;
        y = y0 = -1;
        millis = 0L;
        millis0 = -1L;
        lastButton = lastButton0 = 0;
        lastPressedX = lastPressedX0 = 0;
        lastPressedY = lastPressedY0 = 0;
        lastClickedX = lastClickedX0 = 0;
        lastClickedY = lastClickedY0 = 0;
        mouseWheelDragX = mouseWheelDragX0 = 0;
        mouseWheelDragY = mouseWheelDragY0 = 0;
        lastPressedTimeMillis = lastPressedTimeMillis0 = 0L;
    }

    final int getButton(MouseEvent e){
        int button = e.getButton();
        return !e.isAltDown() && button != 2 ? (!e.isMetaDown() && button != 3?1:2):4;
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
        if(instance != null)
            currentButton0 = 0;
    }

    @Override
    public final void mouseClicked(MouseEvent e) {
        if(instance != null){
            idleCycles = 0;
            lastClickedX0 = e.getX();
            lastClickedY0 = e.getY();
        }
        if(e.isPopupTrigger())
            e.consume();
    }

    @Override
    public final synchronized void mousePressed(MouseEvent e) {
        if(instance != null){
            idleCycles = 0;
            lastPressedX0 = e.getX();
            lastPressedY0 = e.getY();
            lastPressedTimeMillis0 = Time.currentTimeMillis();
            lastButton0 = getButton(e);

            if(lastButton0 == 4){
                mouseWheelDown0 = true;
                mouseWheelX0 = lastPressedX0;
                mouseWheelY0 = lastPressedY0;
            }

//            System.out.println("pressed mouse "+e.getButton()+" or "+lastButton0);
            if(lastButton0 != 0)
                currentButton0 = lastButton0;
        }
        if(e.isPopupTrigger())
            e.consume();
    }

    @Override
    public final synchronized void mouseReleased(MouseEvent e) {
        if(instance != null){
            idleCycles = 0;
            currentButton0 = 0;
            mouseWheelDragX0 = 0;
            mouseWheelDragY0 = 0;
            mouseWheelDown0 = false;
        }
        if(e.isPopupTrigger())
            e.consume();
    }

    @Override
    public final synchronized void mouseEntered(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public final synchronized void mouseExited(MouseEvent e) {
        if(instance != null){
            idleCycles = 0;
            x0 = y0 = -1;
            millis0 = e.getWhen();
        }
    }

    @Override
    public final synchronized void mouseDragged(MouseEvent e) {
        if(instance != null) {
            if (mouseWheelDown0) {
                final int x = e.getX();
                final int y = e.getY();
                mouseWheelDragX0 += mouseWheelX0 - x;
                mouseWheelDragY0 += mouseWheelY0 - y;
                mouseWheelX0 = x;
                mouseWheelY0 = y;
            }
        }
        mouseMoved(e);

    }

    @Override
    public final synchronized void mouseMoved(MouseEvent e) {
        if(instance != null){
            idleCycles = 0;
            x0 = e.getX();
            y0 = e.getY();
            millis0 = e.getWhen();
        }
    }

}
