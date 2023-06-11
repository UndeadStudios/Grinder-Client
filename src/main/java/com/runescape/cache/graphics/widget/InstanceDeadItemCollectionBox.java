package com.runescape.cache.graphics.widget;

import com.runescape.cache.graphics.GameFont;

public class InstanceDeadItemCollectionBox extends Widget {
	
	public static final int INTERFACE_ID = 23400;
	
	public static void widget(GameFont[] tda) {
		int id = INTERFACE_ID;
		
		Widget w = addInterface(id++);
		
		w.totalChildren(4);
		
		int child = 0;
		
		addClosableWindow(id, 350, 240, false, "Instance Dead Item Collection Box");
		w.child(child++, id, 80, 50);
		id+=8;

		// Deposit equipment
		Widget equipmentButtonContainer = addInterface(id);
		equipmentButtonContainer.totalChildren(2);
		w.child(child++, id, 381, 242);
		id++;
		
		addTimerConfigButton(id, 36, 36, getSprite(9, interfaceLoader, "miscgraphics"), getSprite(0, interfaceLoader, "miscgraphics"), "Collect in bank", 0, 1, 375);
		equipmentButtonContainer.child(0, id, 0, 0);
		id++;
		addTransparentOnHoverSprite(id, 749, 13, 13);
		offsetSprites(id, 11, 11);
		equipmentButtonContainer.child(1, id, 0, 0);
		id++;
		
		addText(id, "0 / ?", tda, 2, 0xff981f, true);
		w.child(child++, id, 125, 256);
		id++;
		
		addContainer(id, 1, 7, 4);
		w.child(child, id, 95, 93);
	}

}
