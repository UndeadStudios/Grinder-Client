package com.runescape.draw;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

public final class ProducingGraphicsBuffer extends GraphicsBuffer implements ImageProducer, ImageObserver {
	
    private ColorModel colorModel;
    private ImageConsumer imageConsumer;

	public ProducingGraphicsBuffer() {
	}

    public final synchronized void removeConsumer(ImageConsumer imageconsumer) {
    	if (imageConsumer == imageconsumer)
    		imageConsumer = null;
    }
    
    public final boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		return true;
    }
    
    public final void init(Component component, int w, int h, boolean reset, boolean depthBuffering) {
		width = w;
		height = h;
		pixels = new int[h * w + 1];
		if(depthBuffering)
			depth = new float[w*h];
		colorModel = new DirectColorModel(32, 16711680, 65280, 255);
		image = component.createImage(this);
		setPixels();
		component.prepareImage(image, this);
		setPixels();
		component.prepareImage(image, this);
		setPixels();
		component.prepareImage(image, this);
		initDrawingArea();
    }
    
    public final void startProduction(ImageConsumer imageconsumer) {
    	addConsumer(imageconsumer);
    }
    
    private synchronized void setPixels() {
    	if (imageConsumer != null) {
    		imageConsumer.setPixels(0, 0, width, height, colorModel, pixels, 0, width);
    		imageConsumer.imageComplete(2);
    	}
    }
    
    public final synchronized void addConsumer(ImageConsumer imageconsumer) {
		imageConsumer = imageconsumer;
		imageconsumer.setDimensions(width, height);
		imageconsumer.setProperties(null);
		imageconsumer.setColorModel(colorModel);
		imageconsumer.setHints(14);
    }
    
    public final void requestTopDownLeftRightResend(ImageConsumer imageconsumer) {
    	/* empty */
    }
    
    public final void drawGraphics(Graphics graphics, int x, int y) {
    	setPixels();
    	graphics.drawImage(image, x, y, this);
    }
    
    public final synchronized boolean isConsumer(ImageConsumer imageconsumer) {
		return imageconsumer == imageConsumer;
	}
}