package com.runescape.cache.graphics.widget.custom.impl;

import com.runescape.cache.graphics.widget.Widget;
import com.runescape.cache.graphics.widget.custom.CustomWidget;

public class WildyScoreboardWidget extends CustomWidget {

	private static final String[] TYPE = { "Today", "This Week", "All Time", };

	private static final String[] HEADER = { "Kills", "Kill Streak" };

	public WildyScoreboardWidget(int id) {
		super(id);
	}

	@Override
	public String getName() {
		return "Wilderness Scoreboard";
	}

	@Override
	public void init() {
		addBackground(1091);

		add(addCenteredText("View the Wilderness activity and history of Player Killing.", 0), 256, 40);

		add(addButtonConfigList(TYPE, 1094, 1095, 20, false, 634), 100, 63);
		add(addTextList(TYPE, 2, OR1, true, 118, false), 100, 67);

		add(addText("Player", 1), 65, 95);
		add(addTextList(HEADER, 1, OR1, true, 151, false), 173, 95);

		add(addCenteredText("#", 1), 256, 294);
		
		add(addMenu(), 21, 111);
		
		add(addButton("Sort", 1089, 1090, -1), 55, 99);
		
		add(addButton("Sort", 1089, 1090, -1), 200, 99);
		
		add(addButton("Sort", 1089, 1090, -1), 335, 99);
	}

	private Widget addMenu() {
		
		int lineAmount = 100;
		
		int scrollHeight = 176;
		int scrollWidth = 455;
		
		
		Widget scroll = Widget.addInterface(id);
		
		scroll.componentId = id;
		
		id++;
		
		scroll.totalChildren(lineAmount * 4);
		
		scroll.height = scrollHeight;
		scroll.width = scrollWidth;
		
		int scrollMax = 5 + (lineAmount * (12));
		
		scroll.scrollMax = scrollMax < scrollHeight ? scrollHeight : scrollMax;
		
		int scroll_frame = 0;

		int y = 2;

		int sprite = 0;

		for (int i = 0; i < lineAmount; i++) {
			int sprite1 = sprite % 2 == 1 ? 1092 : 1093;
			
			Widget background = addSprite(sprite1);
			scroll.child(scroll_frame++, background.componentId, 0, y);
			id++;

			sprite++;

			Widget.addText(id, "PlayerName "+id, Widget.defaultFont, 0, OR1, true, true);
			scroll.child(scroll_frame++, id, 62, y + 5);
			id++;

			Widget.addText(id, "Kills "+id, Widget.defaultFont, 0, OR1, true, true);
			scroll.child(scroll_frame++, id, 202, y + 5);
			id++;

			Widget.addText(id, "1,000 PK Point "+id, Widget.defaultFont, 0, OR1, true, true);
			scroll.child(scroll_frame++, id, 352, y + 5);
			id++;
			y+= 21;

		}
		return scroll;
	}
}
