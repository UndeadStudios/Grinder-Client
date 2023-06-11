package com.runescape.cache.graphics.widget.dynamicinterface.cons;

import com.runescape.cache.graphics.widget.Widget;
import com.runescape.cache.graphics.widget.dynamicinterface.DynamicInterface;

/**
 * @author Simplex
 */
public class ConstructionLoading extends DynamicInterface {
	public ConstructionLoading() {
		super(128640);
	}

	@Override
	public void build() {
		// add(Widget.addFullscreenBackground(128641, 0, 0, true), 0, 0);
		add(Widget.addSprite(128642, 1032), 138, 50);
		add(Widget.addText(128600, "There's no place like home..", 2, 0xffffff, true, false), 265, 250);
	}
}
