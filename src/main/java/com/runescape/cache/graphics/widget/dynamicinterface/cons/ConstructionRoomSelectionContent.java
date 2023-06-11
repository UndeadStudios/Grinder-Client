package com.runescape.cache.graphics.widget.dynamicinterface.cons;

import com.runescape.cache.graphics.widget.Widget;
import com.runescape.cache.graphics.widget.dynamicinterface.DynamicInterface;

/**
 * @author Simplex
 */
public class ConstructionRoomSelectionContent extends DynamicInterface {
	public ConstructionRoomSelectionContent() {
		super(128646);
	}

	@Override
	public void build() {

		int totalRooms = 25;

		Widget.interfaceCache[baseIndex].scrollMax = totalRooms * 65;
		Widget.interfaceCache[baseIndex].width = 330;
		Widget.interfaceCache[baseIndex].height = 220;

		final String[] ROOM_NAMES = new String[] { "Parlour: Lvl 1", "Garden: Lvl 1",
				"Kitchen: lvl 5", "Dining room: lvl 10", "Workshop: lvl 15",
				"Bedroom: Lvl 20", "Hall - Skill Trophies: Lvl 25",
				"Games Room: Lvl 30", "Combat room: Lvl 32",
				"Hall - Quest trophies: Lvl 35", "Menagarie: Lvl 37",
				"Study: Lvl 40", "Costume room: Lvl 42", "Chapel: Lvl 45",
				"Portal chamber: Lvl 50", "Formal garden: Lvl 55",
				"Throne room: Lvl 60", "Oubliette: Lvl 65",
				"Superior Garden: Lvl 65",
				"Dungeon - corridor: Lvl 70", "Dungeon - junction: Lvl 70",
				"Dungeon - stairs: Lvl 70", "Dungeon - pit: Lvl 70",
				"Treasure room: Lvl 75", "Achievement Gallery: Lvl 80" };

		final int[] ROOM_COST = new int[] { 1000, 1000, 5000, 5000, 10000, 10000, 15000,
				25000, 25000, 25000, 30000, 50000, 50000, 50000, 100000, 75000,
				150000, 150000, 75000, 7500, 7500, 7500, 10000, 250000, 200000 };

		int y = 8, x = 4, id = 128647;

		for (int i = 0; i < ROOM_NAMES.length; i++) {
			add(Widget.addRectangleClickable(id++, 0, 0x333333, false, 235, 61), x, y);
			add(Widget.addSprite(id++, 1035+i), x + 4, y + 2);
			add(Widget.addText(id++, ROOM_NAMES[i], 1, 0xCCCCFF, false, true), x + 77, y + 22);
			Widget.addHoverText(id, ROOM_NAMES[i], "Select", Widget.defaultFont, 1, 0xCCCCFF, false, false, 128, 0xffffff);
			add(Widget.addText(id++, ROOM_COST[i] + " Coins", Widget.defaultFont, 1, 0x00C800, false, true), x + 239, y + 25);
			//Widget.interfaceCache[id-1].addConfigRequirement(4, 10, 3214, 995, ROOM_COST[i]);
			y += 64;

		}
	}
}
