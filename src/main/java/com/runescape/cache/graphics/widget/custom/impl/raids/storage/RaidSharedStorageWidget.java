package com.runescape.cache.graphics.widget.custom.impl.raids.storage;

import com.runescape.cache.graphics.widget.custom.CustomWidget;
/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class RaidSharedStorageWidget extends CustomWidget {

	public RaidSharedStorageWidget() {
		super(28010);
	}

	@Override
	public String getName() {
		return "Shared Storage";
	}

	@Override
	public void init() {
		addBackground(1259);

		add(addScrollbarWithItem(14, 72, 0, 0, WITHDRAW_OPTIONS, 220, 454), 20,
				88);

		add(addCenteredText("#", 0), 34, 23);
		add(addCenteredText("#", 0), 34, 36);

		add(addClickText("Private", 1, OR1, false, true), 73, 27);
	}
}
