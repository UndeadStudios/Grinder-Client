package com.runescape.draw;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public final class BasicGraphicsBuffer extends GraphicsBuffer {
	
    private Component component;

    @Override
    public final void init(Component component, int w, int h, boolean bool, boolean depthBuffering) {
    	pixels = new int[w * h + 1];
    	if(depthBuffering)
    		depth = new float[w * h + 1];
    	height = h;
    	width = w;
    	DataBufferInt databufferint = new DataBufferInt(pixels, pixels.length);
		DirectColorModel directcolormodel = new DirectColorModel(32, 16711680, 65280, 255);
		WritableRaster writableraster = Raster.createWritableRaster((directcolormodel.createCompatibleSampleModel(width, height)), databufferint, null);
		image = new BufferedImage(directcolormodel, writableraster, bool, new Hashtable());
		this.component = component;
		initDrawingArea();
    }

	@Override
    public final void drawGraphics(Graphics graphics, int x, int y) {
//		System.out.println(""+width+", "+height+", "+x+", "+y);
	    graphics.drawImage(image, x, y, component);
    }

	public void setComponent(Component component) {
		this.component = component;
	}

	public void drawOnComponent(int int_0, int int_1) {
		drawGraphics(this.component.getGraphics(), int_0, int_1);
	}
}