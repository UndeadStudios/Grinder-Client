package com.runescape.cache.graphics.widget.custom.impl.raids.storage;

import com.runescape.cache.graphics.widget.Widget;
import com.runescape.cache.graphics.widget.custom.CustomWidget;
/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class RaidStorageCreationWidget extends CustomWidget {

	public RaidStorageCreationWidget() {
		super(67_000);
	}

	@Override
	public String getName() {
		return "Storage Creation Menu";
	}

	@Override
	public void init() {
		addBackground(1284);
		
		Widget con = addItemContainer(1, 3, 0, 45, new String[] {"Make",null,null,null,null}, "storage");
		con.inventoryItemId[0] = 21_038;
		con.inventoryAmounts[0] = 1;
		con.inventoryItemId[1] = 21_039;
		con.inventoryAmounts[1] = 1;
		con.inventoryItemId[2] = 21_040;
		con.inventoryAmounts[2] = 1;
		add(con, 33, 93);
	}
}
