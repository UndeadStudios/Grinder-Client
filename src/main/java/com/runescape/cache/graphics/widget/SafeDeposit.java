package com.runescape.cache.graphics.widget;

import com.runescape.cache.graphics.GameFont;

public class SafeDeposit extends Widget {
	
	public static final int INTERFACE_ID = 38_000;
	
	public static void widget(GameFont[] tda) {
		int id = INTERFACE_ID;
		
		Widget w = addInterface(id++);
		
		w.totalChildren(6);
		
		int child = 0;
		
		addClosableWindow(id, 350, 240, false, "Safe Deposit Box");
		w.child(child++, id, 80, 50);
		id+=8;
		
		// Deposit inventory
		addTimerConfigButton(id, 36, 36, getSprite(9, interfaceLoader, "miscgraphics"), getSprite(0, interfaceLoader, "miscgraphics"), "Deposit inventory", 0, 0, 375);
		w.child(child++, id, 344, 242);
		id++;
		
		addTransparentOnHoverSprite(id, 115, 36, 36);
		offsetSprites(id, 3, 8);
		w.child(child++, id, 344, 242);
		id++;

		// Deposit equipment
		Widget equipmentButtonContainer = addInterface(id);
		equipmentButtonContainer.totalChildren(2);
		w.child(child++, id, 381, 242);
		id++;
		
		addTimerConfigButton(id, 36, 36, getSprite(9, interfaceLoader, "miscgraphics"), getSprite(0, interfaceLoader, "miscgraphics"), "Deposit worn items", 0, 1, 375);
		equipmentButtonContainer.child(0, id++, 0, 0);
		addTransparentOnHoverSprite(id, 116, 36, 36);
		offsetSprites(id, 4, 8);
		equipmentButtonContainer.child(1, id, 0, 0);
		id++;
		
		addText(id, "0 / 10", tda, 2, 0xff981f, true);
		w.child(child++, id, 125, 256);
		id++;
		
		addContainer(id, 1, 7, 4, new String[] { "Withdraw-1", "Withdraw-5", "Withdraw-10", "Withdraw-All", "Withdraw-X" });
		w.child(child++, id, 95, 93);
		id++;
	}

}
