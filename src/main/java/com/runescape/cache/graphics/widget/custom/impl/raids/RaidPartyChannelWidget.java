package com.runescape.cache.graphics.widget.custom.impl.raids;

import com.runescape.cache.graphics.GameFont;
import com.runescape.cache.graphics.widget.Widget;
/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class RaidPartyChannelWidget {
	public static void init(GameFont[] tda) {
		int id = 65_600;
		int frame = 0;

		System.out.println("RaidPartyChannelWidget: " + id);

		int config = 800;

		Widget tab = Widget.addTabInterface(id);
		id++;

		tab.totalChildren(9);

		Widget.addAdvancedSprite(id, 1260);
		tab.child(frame++, id, 3, 23);
		id++;

		Widget.addText(id, "@or1@Raiding Party", tda, 2, 0xFFFFFF, true, true);
		tab.child(frame++, id, 90, 4);
		id++;

		Widget.addText(id, "@or1@Party Size: @whi@" + id, tda, 0, 0xFFFFFF, true, true);
		tab.child(frame++, id, 90, 215);
		id++;

		Widget.addText(id, "@or1@Your party is exploring\\n@or1@upper level " + id, tda, 1, 0xFFFFFF,
				true,
				true);
		tab.child(frame++, id, 90, 227);
		id++;

		Widget.addButton(id, 1263, "Refresh");
		tab.child(frame++, id, 160, 3);
		id++;

		Widget.addButtonWithConfig(id, 1261, 1262, 7, 5, "Sort by @gre@Name", 0, config);
		tab.child(frame++, id++, 50, 27);

		Widget.addButtonWithConfig(id, 1261, 1262, 7, 5, "Sort by @gre@Combat", 1, config);
		tab.child(frame++, id++, 50 + 64, 27);

		Widget.addButtonWithConfig(id, 1261, 1262, 7, 5, "Sort by @gre@Total Level", 2, config);
		tab.child(frame++, id++, 50 + 64 + 34, 27);

		tab.child(frame++, id, 7, 36);

		Widget scroll = Widget.addInterface(id);
		id++;
		scroll.totalChildren(60);
		scroll.height = 170;
		scroll.width = 162;
		scroll.scrollMax = 171 * 2;
		int scroll_frame = 0;

		int x = 0;
		int y = 0;

		for (int i = 0; i < 20; i++) {
			Widget.addText(id, "@or1@" + id, tda, 0, 0xFFFFFF, true, true);
			scroll.child(scroll_frame++, id, x + 45, y);
			id++;

			Widget.addText(id, "@or1@" + id, tda, 0, 0xFFFFFF, true, true);
			scroll.child(scroll_frame++, id, x + 105, y);
			id++;

			Widget.addText(id, "@or1@" + id, tda, 0, 0xFFFFFF, true, true);
			scroll.child(scroll_frame++, id, x + 144, y);
			id++;

			y += 16;
		}

		RaidPartySetupWidget.init(id, tda);
	}
}
