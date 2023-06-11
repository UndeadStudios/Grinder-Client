package com.runescape.cache.graphics.widget.custom;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.runescape.cache.graphics.widget.Widget;

public class WidgetGroup extends Widget {

	public int lines;

	public int scrollWidth;

	public int scrollHeight;

	public int difference;

	public int offsetX;

	public int offsetY;

	public int scrollMax;

	public List<WidgetComponent> multipleComponents;

	public List<WidgetComponent> singleComponents;

	public WidgetGroup(int lines, int scrollWidth, int scrollHeight, int difference, int offsetX, int offsetY) {
		this.lines = lines;
		this.scrollHeight = scrollHeight;
		this.scrollWidth = scrollWidth;
		this.difference = difference;
		this.multipleComponents = new ArrayList<WidgetComponent>();
		this.singleComponents = new ArrayList<WidgetComponent>();
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	public WidgetGroup(int lines, int scrollWidth, int scrollHeight, int difference) {
		this(lines, scrollWidth, scrollHeight, difference, 0, 0);
	}

	public WidgetGroup(int scrollWidth, int scrollHeight, int scrollMax) {
		this(1, scrollWidth, scrollHeight, 0, 0, 0);
		this.scrollMax = scrollMax;
	}

	public WidgetGroup(int scrollWidth, int scrollHeight) {
		this(1, scrollWidth, scrollHeight, 0, 0, 0);
	}

	public WidgetGroup() {
		this(500, 500);
	}

	public void add(Widget widget, int x, int y) {
		WidgetComponent component = new WidgetComponent(new Point(x, y), widget);
		component.componentId = widget.componentId;
		multipleComponents.add(component);
	}

	public void addSingle(Widget widget, int x, int y) {
		WidgetComponent component = new WidgetComponent(new Point(x, y), widget);
		component.componentId = widget.componentId;
		singleComponents.add(component);
	}
}
