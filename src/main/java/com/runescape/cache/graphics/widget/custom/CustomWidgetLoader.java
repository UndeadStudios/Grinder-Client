package com.runescape.cache.graphics.widget.custom;

import com.runescape.cache.graphics.widget.Widget;
import com.runescape.cache.graphics.widget.custom.impl.AchievementWidget;
import com.runescape.cache.graphics.widget.custom.impl.NPCStatsWidget;
import com.runescape.cache.graphics.widget.custom.impl.WildyScoreboardWidget;
import com.runescape.cache.graphics.widget.custom.impl.raids.RaidIngameWidget;
import com.runescape.cache.graphics.widget.custom.impl.raids.RaidPartiesWidget;
import com.runescape.cache.graphics.widget.custom.impl.raids.RaidPartyChannelWidget;
import com.runescape.cache.graphics.widget.custom.impl.raids.RaidRewardWidget;
import com.runescape.cache.graphics.widget.custom.impl.raids.storage.RaidPrivateStorageWidget;
import com.runescape.cache.graphics.widget.custom.impl.raids.storage.RaidSharedStorageWidget;
import com.runescape.cache.graphics.widget.custom.impl.raids.storage.RaidStorageCreationWidget;

public class CustomWidgetLoader {

	public static final int OR1 = 0xFFB000;

	public static void init() {
		init(new AchievementWidget(25_000));
		init(new NPCStatsWidget(25_700));
		init(new WildyScoreboardWidget(32_110));

		init(new RaidPartiesWidget());
		RaidPartyChannelWidget.init(Widget.defaultFont);
		init(new RaidStorageCreationWidget());
		init(new RaidSharedStorageWidget());
		init(new RaidPrivateStorageWidget());
		init(new RaidIngameWidget());
		init(new RaidRewardWidget());
	}

	public static void init(CustomWidget widget) {

		Widget tab = Widget.addInterface(widget.mainId);

		int frame = 0;

		widget.init();

		tab.totalChildren(widget.components.size());

		for (WidgetComponent w : widget.components) {
			tab.child(frame++, w.componentId, w.point.x + widget.xOffset, w.point.y + widget.yOffset);
		}

		String name = widget.getName();

		//System.out.println("CustomWidget: " + name + " id: " + widget.mainId + " with "
		//		+ widget.components.size() + " components");
		//System.out.println("--");
	}
}
