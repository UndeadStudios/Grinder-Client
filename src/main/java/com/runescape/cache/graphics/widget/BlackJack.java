package com.runescape.cache.graphics.widget;

import com.runescape.cache.graphics.GameFont;
import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.cache.graphics.sprite.SpriteLoader;

public class BlackJack extends Widget {
	
	/**
	 * The card deck sprites.
	 */
	public static Sprite[] deck = new Sprite[50];
	
	/**
	 * The interface id.
	 */
    public static final int INTERFACE_ID = 52_000;
    
    /**
     * The first deck sprite id.
     */
    public static final int FIRST_DECK_START = 52_010;
    
    /**
     * The second deck sprite id.
     */
    public static final int SECOND_DECK_START = 52_016;
    
    /**
     * The max amount of cards per hand.
     */
    public static final int MAX_CARDS_PER_HAND = 6;

	/**
	 * Builds the widget.
	 * 
	 * @param tda
	 */
    public static void widget(GameFont[] tda) {
    	int row = 0;
    	
    	int col = 0;
    	
    	/**
    	 * Loads the deck from a sprite sheet.
    	 */
    	for (int i = 0; i < deck.length; i++) {
    		deck[i] = new Sprite(SpriteLoader.getSprite(944), 41, 61, col++ * 42, row * 62);
    		
    		if (i > 0 && i < 48 && i % 12 == 0) {
    			row++;
    			col = 0;
    		}
    	}
    	
		Widget w = addInterface(INTERFACE_ID);
		w.totalChildren(25);

		int id = INTERFACE_ID + 1;
		
		int child = 0;
		
		addClosableWindow(id, 485, 304, true, "Gambling with: Blake2");
		interfaceCache[id + 3].atActionType = 1;
		interfaceCache[id + 4].atActionType = 1;
		w.child(child++, id, 14, 16);
		id += 8;
		
		addTransparentRectangle(id, 471, 205, 0x13891c, true, 75);
		w.child(child++, id, 21, 52);
		id++;
		
		int yPos = 32;
		
		for (int i = 0; i < MAX_CARDS_PER_HAND; i++) {
			addSprite(id, deck[0]);
			interfaceCache[id].hidden = true;
			w.child(child++, id, 177 + 25 + ((i * 14)), yPos + 58);
			id++;
		}
		
		for (int i = 0; i < MAX_CARDS_PER_HAND; i++) {
			addSprite(id, deck[0]);
			interfaceCache[id].hidden = true;
			w.child(child++, id, 177 + 25 + ((i * 14)), yPos + 65 + 58);
			id++;
		}
		
		addText(id, "You:", tda, 4, 0xFF981F);
		w.child(child++, id, 65, 70);
		id++;
		
		addText(id, "Other:", tda, 4, 0xFF981F);
		w.child(child++, id, 65, 150);
		id++;
		
		addHoverButton(id, 621, 124, 32, "Stand", 0, id + 1, 1);
		addHoveredButton(id + 1, 622, 124, 32, id + 2);
		addText(id + 3, "Stand", tda, 2, 0xAF6A1A, true);
		
		w.child(child++, id, 86, 270);
		w.child(child++, id + 1, 86, 270);
		w.child(child++, id + 3, 146, 277);
		
		id += 4;
		
		addHoverButton(id, 621, 124, 32, "Hit", 0, id + 1, 1);
		addHoveredButton(id + 1, 622, 124, 32, id + 2);
		addText(id + 3, "Hit", tda, 2, 0xAF6A1A, true);
		
		w.child(child++, id, 300, 270);
		w.child(child++, id + 1, 300, 270);
		w.child(child++, id + 3, 360, 277);
		
		id += 4;
		
		addText(id, "0", tda, 2, 0xFF981F, true);
		w.child(child++, id, 100, 110);
		id++;
		
		addText(id, "0", tda, 2, 0xFF981F, true);
		w.child(child++, id, 100, 190);
		id++;
		
		addText(id, "", tda, 1, 0xffffff);
		w.child(child++, id, 245, 278);
		id++;
    }
    
    public static void reset() {
    	for (int id = FIRST_DECK_START; id < SECOND_DECK_START + MAX_CARDS_PER_HAND; id++) {
    		interfaceCache[id].hidden = true;
    	}
    	
    	interfaceCache[52034].setDefaultText("");
    }
    
    public static boolean onSpriteUpdate(int interfaceId, int spriteId) {
    	if (interfaceId >= FIRST_DECK_START && interfaceId < SECOND_DECK_START + MAX_CARDS_PER_HAND) {
    		if (spriteId < -1 || spriteId >= deck.length) {
    			return true;
    		}
    		
    		if (spriteId == -1) {
    			interfaceCache[interfaceId].hidden = true;
    		} else {
    			interfaceCache[interfaceId].disabledSprite = deck[spriteId];
    			interfaceCache[interfaceId].hidden = false;
    		}
    		
    		return true;
    	}
    	
    	return false;
    }

}
