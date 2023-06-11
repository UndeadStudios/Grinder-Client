package com.runescape.cache.graphics.widget.dynamicinterface;
public class ButtonActionListener {
	public ButtonActionListener(int id, Runnable container) {
		this.id = id;
		this.container = container;
	}
	public Runnable container;
	public int id;
}
