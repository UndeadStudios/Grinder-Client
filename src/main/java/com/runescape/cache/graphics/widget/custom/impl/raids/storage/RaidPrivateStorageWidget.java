package com.runescape.cache.graphics.widget.custom.impl.raids.storage;

import com.runescape.cache.graphics.widget.custom.CustomWidget;
/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class RaidPrivateStorageWidget extends CustomWidget {

	public RaidPrivateStorageWidget() {
		super(28020);
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public void init() {
		addBackground(1258);
		add(addCenteredText("#", 0), 85, 23);
		add(addCenteredText("#", 0), 85, 35);
		add(addClickText("Shared", 1, OR1, false, true), 123, 28);
		add(addScrollbarWithItem(10, 15, 2, 2, WITHDRAW_OPTIONS, 191, 349), 75, 52);
		add(addClickText("Withdraw All", 1, OR1, false, true), 136, 254);
		add(addClickText("Deposit All", 1, OR1, false, true), 310, 254);
	}
}
