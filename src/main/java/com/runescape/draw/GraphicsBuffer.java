package com.runescape.draw;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

public abstract class GraphicsBuffer {
	
    Image image;
    public int width;
    public int[] pixels;
    float[] depth;
    public int height;

    protected GraphicsBuffer() {
    }

    public abstract void init(Component component, int width, int height, boolean reset, boolean depthBuffering);
    
    public abstract void drawGraphics(Graphics graphics, int x, int y);
    
    public final void initDrawingArea() {
        Rasterizer2D.Rasterizer2D_replace(pixels, depth, width, height);
    }
}