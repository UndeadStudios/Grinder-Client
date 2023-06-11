package com.runescape.cache.graphics.widget.custom.impl.raids;

import com.runescape.cache.graphics.GameFont;
import com.runescape.cache.graphics.widget.Widget;
import com.runescape.util.SkillConstants;
/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class RaidPartySetupWidget {

	public static void init(int id, GameFont[] tda) {

		int frame = 0;

		String[] BUTTONS = {"Advertise", "@red@Disband", "Back", "Refresh"};

		String[] PREFERENCE =
				{"Preferred party size", "Preferred combat level", "Preferred skill total",};

		id++;
		System.out.println("RaidPartySetupWidget: " + id);
		Widget tab = Widget.addInterface(id);
		id++;

		tab.totalChildren(
				4 + (BUTTONS.length * 3) + (PREFERENCE.length) + SkillConstants.SKILL_COUNT);

		Widget.addAdvancedSprite(id, 1254);
		tab.child(frame++, id, 24, 7);
		id++;

		Widget.addTextClose(40000, tda);
		Widget.setBounds(40000, 412, 18, frame, tab);
		frame++;
		id++;

		Widget.addText(id, "@or1@Raiding Party", tda, 2, 0xFFFFFF, true, true);
		tab.child(frame++, id, 256, 16);
		id++;

		int x = 256;
		int y = 257;

		for (String s : BUTTONS) {
			Widget.addHoverButton(id, 1281, 110, 29, "Select @or1@" + s, -1, id + 1, 5);
			Widget.addHoveredButton(id + 1, 1280, 110, 29, id + 2);
			tab.child(frame++, id, x, y);
			tab.child(frame++, id + 1, x, y);
			id += 3;

			Widget.addText(id, "@or1@" + s, tda, 1, 0xFFFFFF, true, true);
			tab.child(frame++, id, x + 55, y + 7);
			id++;

			x += 113;

			if (x > 400) {
				x = 256;
				y += 30;
			}
		}

		x = 280;
		y = 53;

		for (String s : PREFERENCE) {
			Widget.addHoverClickText(id, s + ": " + id, "Select @or1@" + s, tda, 0, 0xFF981F, true,
					true,
					150);
			Widget.setBounds(id, x, y, frame, tab);
			frame++;
			id++;
			y += 21;
		}

		x = 288;
		y = 134;

		for (int i = 0; i < SkillConstants.SKILL_COUNT; i++) {
			Widget.addText(id, "@or1@" + id, tda, 0, 0xFFFFFF, false, true);
			tab.child(frame++, id, x, y);
			id++;

			y += 26;

			if (y > 250) {
				y = 134;
				x += 43;
			}
		}

		Widget.setBounds(id, 74 - 45, 60, frame, tab);
		frame++;

		Widget scroll = Widget.addInterface(id);
		id++;
		scroll.totalChildren(20 * 6);
		scroll.height = 255;
		scroll.width = 162 + 45;
		scroll.scrollMax = 171 * 2;
		int scroll_frame = 0;

		x = 0;
		y = 0;

		for (int i = 0; i < 20; i++) {

			Widget.addHoverButton(id, 1283, 200, 14, "View Player Stats", -1, id + 1, 5);
			Widget.addHoveredButton(id + 1, 1282, 200, 14, id + 2);
			scroll.child(scroll_frame++, id, x + 7, y);
			scroll.child(scroll_frame++, id + 1, x + 7, y);
			id += 3;

			Widget.addText(id, "@or1@" + id, tda, 0, 0xFFFFFF, true, true);
			scroll.child(scroll_frame++, id, x + 55, y + 2);
			id++;

			Widget.addText(id, "@or1@126", tda, 0, 0xFFFFFF, true, true);
			scroll.child(scroll_frame++, id, x + 123, y + 2);
			id++;

			Widget.addText(id, "@or1@2,147", tda, 0, 0xFFFFFF, true, true);
			scroll.child(scroll_frame++, id, x + 153, y + 2);
			id++;

			Widget.addText(id, "@or1@0", tda, 0, 0xFFFFFF, true, true);
			scroll.child(scroll_frame++, id, x + 188, y + 2);
			id++;

			y += 16;
		}
	}
}
