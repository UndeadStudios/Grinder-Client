package com.runescape.cache.graphics.widget;

import com.runescape.Client;
import com.runescape.cache.graphics.GameFont;
import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.impl.AddBroadcast;

/**
 * A class that handles the broadcast messages above the chatbox.
 * 
 * @author Blake
 *
 */
public class Broadcast extends Widget {
	
	public static final int INTERFACE_ID = 35_000;
	
	public static final int ADD_BUTTON_ID = INTERFACE_ID + 12;
	
	public static void widget(GameFont[] tda) {
		int id = INTERFACE_ID;
		
		Widget w = addInterface(id++);
		
		w.totalChildren(7 + 1 + 15);
		
		int child = 0;
		
		addClosableWindow(id, 485, 304, true, "Broadcast");
		w.child(child++, id, 14, 16);
		id += 8;
		
		addInput(id, "Text", "^[A-Za-z0-9_., :!?'()]+$", 300, 20, 0xE69138);
		w.child(child++, id, 100, 70);
		id++;
		
		addInput(id, "Link (optional)", "^[A-Za-z0-9_., :!?\\/'%&-]+$", 300, 20, 0xE69138);
		w.child(child++, id, 100, 110);
		id++;
		
		addInput(id, "Expires (optional)", "^[0-9]{1,6}$", 94, 20, 0xE69138);
		w.child(child++, id, 100, 150);
		id++;
		
		addHoverButton(id, 621, 124, 32, "Add", 0, id + 1, 1);
		addHoveredButton(id + 1, 622, 124, 32, id + 2);
		addText(id + 3, "Add", tda, 2, 0xAF6A1A, true);
		
		w.child(child++, id, 275, 150 - 7);
		w.child(child++, id + 1, 275, 150 - 7);
		w.child(child++, id + 3, 335, 150);
		
		id += 4;
		
		addPixels(id, 0, 300, 105, 180, true);
		w.child(child++, id, 100, 189);
		id++;
		
		int yPos = 0;

		for (int i = 0; i < 5; i++) {
			addText(id, "ID: " + id, tda, 0, 0xE69138);
			w.child(child++, id, 105, 195 + yPos);
			yPos += 20;
			id++;
		}

		yPos = 0;
		
		for (int i = 0; i < 5; i++) {
			addHoverButton(id + 1, 644, 18, 18, "Remove", 0, id + 2, 1);
			addHoveredButton(id + 2, 645, 18, 18, id + 3);
			w.child(child++, id + 1, 380, 192 + yPos);
			w.child(child++, id + 2, 380, 192 + yPos);
			yPos += 20;
			id += 3;
		}
	}

	public static void addBroadcast() {
		
		final String text = interfaceCache[INTERFACE_ID + 9].getDefaultText();
		
		final String link = interfaceCache[INTERFACE_ID + 10].getDefaultText();
		
		int time = 0;
		
		if (text.isEmpty()) {
			Client.instance.sendMessage("Please enter the broadcast message.");
			return;
		}
		
		if (!interfaceCache[INTERFACE_ID + 11].getDefaultText().isEmpty()) {
			time = Integer.parseInt(interfaceCache[INTERFACE_ID + 11].getDefaultText());
		}
		
		final OutgoingPacket broadcast = new AddBroadcast(time, text, link);
		
		Client.instance.sendPacket(broadcast.create());
	}
	
}
