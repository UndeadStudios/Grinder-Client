package com.runescape.cache.graphics.widget.dynamicinterface.cons;

import com.runescape.cache.graphics.widget.Widget;
import com.runescape.cache.graphics.widget.dynamicinterface.DynamicInterface;

/**
 * @author Simplex
 */
public class ConstructionRoomSelection extends DynamicInterface {
	public ConstructionRoomSelection() {
		super(128643);
	}

	@Override
	public void build() {
		interfaceOffsetX = 3;
		// add background
		add(Widget.addSprite(128644, 1033), 10, 10);
		// add close button
		add(Widget.interfaceCache[138323], 466, 17);
		// add interface content
		add(Widget.interfaceCache[128646], 30, 75);
		// add bounding rectangle
		Widget rect = Widget.addRectangle(128601,
				Widget.interfaceCache[128646].width+20,
				Widget.interfaceCache[128646].height+3,
				0x333333, false);
		add(rect,29, 73);

		// add title
		add(Widget.addText(128602, "Room Selection", 2, 0xFF981F, true, true), 250, 19);
		// Add container text
		add(Widget.addText(128603, "Select a room to build", 1, 0xFF981F, false, true), 18, 50);
	}
}
