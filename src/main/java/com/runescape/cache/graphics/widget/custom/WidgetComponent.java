package com.runescape.cache.graphics.widget.custom;

import java.awt.Point;

import com.runescape.cache.graphics.widget.Widget;

public class WidgetComponent {

	public Point point;

	public Widget component;

	public int componentId;

	public WidgetComponent(Point point, Widget component) {
		this.point = point;
		this.component = component;
	}
}