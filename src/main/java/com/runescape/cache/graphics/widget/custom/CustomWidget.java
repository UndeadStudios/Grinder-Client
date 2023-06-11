package com.runescape.cache.graphics.widget.custom;

import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.cache.graphics.sprite.SpriteCompanion;
import com.runescape.cache.graphics.widget.Widget;

import java.awt.*;
import java.util.ArrayList;

public abstract class CustomWidget {

	public static final String[] WITHDRAW_OPTIONS = {
		"Withdraw 1", "Withdraw 5", "Withdraw 10",  "Withdraw X", "Withdraw All",
	};

	public static final int OR1 = 0xFFB000;

	public int frame;

	public int id;

	public ArrayList<WidgetComponent> components;

	public int mainId;

	public int xOffset;

	public int yOffset;

	public boolean fullscreen;

	public CustomWidget(int id) {
		this.mainId = id;
		this.id = id + 1;
		this.components = new ArrayList<WidgetComponent>();
	}

	public void setFullscreen() {
		fullscreen = true;
	}

	public void add(Widget widget, int x, int y) {
		WidgetComponent component = new WidgetComponent(new Point(x, y), widget);
		component.componentId = widget.componentId;
		components.add(component);
	}

	public void addWidget(int id, int x, int y) {
		WidgetComponent component = new WidgetComponent(new Point(x, y), Widget.interfaceCache[id]);
		component.componentId = id;
		components.add(component);
	}

	public abstract String getName();

	public abstract void init();

	public Widget addSprite(int spriteId) {
		Widget tab = Widget.addInterface(id);
		tab.id = id;
		tab.parent = id;
		tab.componentId = id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.hoverType = 52;
		tab.enabledSprite = SpriteCompanion.cacheSprite[spriteId];
		tab.disabledSprite = SpriteCompanion.cacheSprite[spriteId];
		tab.width = tab.enabledSprite.myWidth;
		tab.height = tab.enabledSprite.myHeight;
		id++;
		return tab;
	}

	public Widget addTransparentSprite(int spriteId, int transparency) {
		Widget sprite = addSprite(spriteId);
		sprite.transparency = transparency;
		sprite.drawsTransparent = true;
		return sprite;
	}

	public Widget addSpriteList(int[] list, int offset, boolean verticle) {
		Widget tab = Widget.addInterface(id);
		tab.componentId = id;
		id++;

		tab.totalChildren(list.length);

		int frame = 0;

		int y = 0;
		int x = 0;

		for (int spriteId : list) {

			int height = SpriteCompanion.cacheSprite[spriteId].myHeight / 2;
			int width = SpriteCompanion.cacheSprite[spriteId].myWidth / 2;

			Widget button = addSprite(spriteId);
			tab.child(frame++, button.componentId, x, y);
			if (verticle) {
				y += height + offset;
			} else {
				x += width + offset;
			}
		}
		id++;
		return tab;
	}

	public Widget addCharacter() {
		Widget tab = Widget.addInterface(id);
		tab.componentId = id;
		tab.id = id;
		tab.parent = id;
		tab.type = 6;
		tab.atActionType = 0;
		tab.contentType = 328;
		tab.width = 136;
		tab.height = 168;
		tab.opacity = 0;
		tab.modelZoom = 560;
		tab.modelXAngle = 150;
		tab.modelYAngle = 0;
		tab.defaultAnimationId = -1;
		tab.secondaryAnimationId = -1;
		id++;
		return tab;
	}

	public Widget addNpc() {
		Widget tab = Widget.addInterface(id);
		tab.componentId = id;
		tab.id = id;
		tab.parent = id;
		tab.type = 6;
		tab.atActionType = 0;
		tab.contentType = 3291;
		tab.width = 136;
		tab.height = 168;
		tab.opacity = 0;
		tab.hoverType = 0;
		tab.modelZoom = 1500;
		tab.modelXAngle = 150;
		tab.modelYAngle = 0;
		tab.defaultAnimationId = -1;
		tab.secondaryAnimationId = -1;
		id++;
		return tab;
	}

	public Widget addPixels(int color, int width, int height, int alpha, boolean filled) {
		Widget rsi = Widget.addInterface(id);
		rsi.componentId = id;
		rsi.type = Widget.TYPE_RECTANGLE;
		rsi.opacity = (byte) alpha;
		rsi.textColor = color;
		rsi.defaultHoverColor = color;
		rsi.secondaryHoverColor = color;
		rsi.secondaryColor = color;
		rsi.filled = filled;
		rsi.width = width;
		rsi.height = height;
		id++;
		return rsi;
	}

	public void addBackground(int background) {
		add(addBackground(background, getName()), 0, 0);
	}

	public Widget addBackground(int background, String title) {

		Widget tab = Widget.addInterface(id);
		tab.componentId = id;
		id++;

		Sprite sprite = SpriteCompanion.cacheSprite[background];

		int x = (512 - sprite.myWidth) / 2;
		int y = (334 - sprite.myHeight) / 2;

		int closeX = sprite.myWidth - 73;

		if (sprite.myWidth < 400) {
			closeX = sprite.myWidth - 10;
		}

		int frame = 0;

		tab.totalChildren(3);

		Widget.addSprite(id, background);
		tab.child(frame++, id, x, y);
		id++;

		Widget.addTextClose(40000, Widget.defaultFont);
		tab.child(frame++, 40000, closeX, y + 8);
		id++;

		Widget.addText(id, "@or1@" + title, Widget.defaultFont, 2, 0xFFFFFF, true, true);
		tab.child(frame++, id, 256, y + 7);
		id++;
		return tab;
	}

	public Widget addBackground(int background, String title, int closeX) {

		Widget tab = Widget.addInterface(id);
		tab.componentId = id;
		id++;

		Sprite sprite = SpriteCompanion.cacheSprite[background];

		int x = (512 - sprite.myWidth) / 2;
		int y = (334 - sprite.myHeight) / 2;

		int frame = 0;

		tab.totalChildren(3);

		Widget.addSprite(id, background);
		tab.child(frame++, id, x, y);
		id++;

		Widget.addTextClose(40000, Widget.defaultFont);
		tab.child(frame++, 40000, closeX, y + 8);
		id++;

		Widget.addText(id, "@or1@" + title, Widget.defaultFont, 2, 0xFFFFFF, true, true);
		tab.child(frame++, id, 256, y + 7);
		id++;
		return tab;
	}

	public Widget addItemContainer(int w, int h, int x, int y, String[] actions, String string) {
		System.out.println(getName() + ": " + string + " - " + id);
		Widget tab = Widget.addInterface(id);
		tab.componentId = id;
		tab.width = w;
		tab.height = h;
		tab.inventoryItemId = new int[w * h];
		tab.inventoryAmounts = new int[w * h];
		for (int i = 0; i < tab.inventoryItemId.length; i++) {
			tab.inventoryItemId[i] = 996;
			tab.inventoryAmounts[i] = Integer.MAX_VALUE;
		}
		tab.usableItems = false;
		tab.spritePaddingX = x;
		tab.spritePaddingY = y;
		tab.spritesX = new int[20];
		tab.spritesY = new int[20];
		tab.sprites = new Sprite[20];
		if (actions != null) {
			tab.actions = new String[5];
			tab.actions = actions;
		}
		tab.type = 2;
		id++;
		return tab;
	}

	public Widget addItemContainer(int w, int h, int x, int y, String string) {
		return addItemContainer(w, h, x, y, null, string);
	}

	public Widget addItemContainer(String string) {
		return addItemContainer(1, 1, 0, 0, null, string);
	}

	public Widget addText(String text, int idx, int color, boolean center, boolean rightAligned, boolean rollingText,
			boolean shadow) {
		Widget tab = Widget.addTabInterface(id);
		tab.parent = id;
		tab.id = id;
		tab.componentId = id;
		tab.type = 4;
		tab.atActionType = 0;
		tab.width = 0;
		tab.height = idx == 0 ? 12 : 15;
		tab.contentType = 0;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.centerText = center;
		tab.rightAlignedText = rightAligned;
		tab.rollingText = rollingText;
		tab.textShadow = shadow;
		tab.textDrawingAreas = Widget.defaultFont[idx];
		tab.defaultText = text.contains("#") ? text + ":" + id : text;
		tab.secondaryText = "";
		tab.textColor = color;
		tab.secondaryColor = 0;
		tab.defaultHoverColor = 0;
		tab.secondaryHoverColor = 0;
		id++;
		return tab;
	}

	public Widget addClickText(String text, int idx, int color, boolean center, boolean textShadow) {
		Widget tab = Widget.addInterface(id);
		tab.parent = id;
		tab.id = id;
		tab.componentId = id;
		tab.type = 4;
		tab.atActionType = 1;
		tab.width = Widget.defaultFont[idx].getTextWidth(text) + 5;
		tab.height = idx == 0 ? 12 : 15;
		tab.contentType = 0;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.centerText = center;
		tab.textShadow = textShadow;
		tab.textDrawingAreas = Widget.defaultFont[idx];
		tab.defaultText = text + " " + (text.contains("#") ? id : "");
		tab.secondaryText = "";
		tab.tooltip = "Select @lre@" + text;
		tab.textColor = color;
		tab.secondaryColor = 0;
		tab.defaultHoverColor = color == 0xFFFFFF ? 0 : 0xFFFFFF;
		tab.secondaryHoverColor = 0;
		id++;
		return tab;
	}

	public Widget addText(String text, int size, int color) {
		return addText(text, size, color, false, false, false, true);
	}

	public Widget addCenteredText(String text, int size, int color) {
		return addText(text, size, color, true, false, false, true);
	}

	public Widget addText(String text, int size) {
		return addText(text, size, CustomWidget.OR1, false, false, false, true);
	}

	public Widget addCenteredText(String text, int size) {
		return addText(text, size, CustomWidget.OR1, true, false, false, true);
	}

	public Widget addTextList(String[] list, int textSize, int colour, boolean center, int offset, boolean verticle) {
		Widget tab = Widget.addInterface(id);
		tab.componentId = id;
		id++;

		tab.totalChildren(list.length);

		int frame = 0;

		int y = 0;
		int x = 50;

		for (String s : list) {
			Widget button = addText(s, textSize, colour, center, false, false, true);
			tab.child(frame++, button.componentId, x, y);
			if (verticle) {
				y += offset;
			} else {
				x += offset;
			}
		}
		id++;
		return tab;
	}

	public Widget addButton(String name, int sprite1, int sprite2, int textSize, int colour, boolean inputField) {
		Widget tab = Widget.addInterface(id);
		tab.componentId = id;
		id++;

		int frame = 0;

		tab.totalChildren(3);

		int width = SpriteCompanion.cacheSprite[sprite1].myWidth;
		int height = SpriteCompanion.cacheSprite[sprite1].myHeight;

		Widget.addHoverButton(id, sprite1, width, height, "Select @lre@" + name, -1, id + 1, 5);
		Widget.addHoveredButton(id + 1, sprite2, width, height, id + 2);
		tab.child(frame++, id, 0, 0);
		tab.child(frame++, id + 1, 0, 0);
		id += 3;

		int textHeight = 13 + textSize;

		Widget.addText(id, textSize == -1 ? "" : (name.contains("#") ? "" + id : name), Widget.defaultFont,
				textSize == -1 ? 0 : textSize, colour, true, true);
		tab.child(frame++, id, (width / 2), (textSize == 0 ? 1 : 0) + ((height / 2) - (textHeight / 2)));
		id++;
		return tab;
	}

	public Widget addButton(String name) {
		return addButton(name, 1201, 1202, 1, OR1, false);
	}

	public Widget addButton(String name, int size) {
		return addButton(name, 1201, 1202, size, OR1, false);
	}

	public Widget addButton(String name, int sprite1, int sprite2, int size) {
		return addButton(name, sprite1, sprite2, size, OR1, false);
	}

	public Widget addInputField(String name, int sprite1, int sprite2, int size) {
		return addButton(name, sprite1, sprite2, size, OR1, true);
	}
	public Widget addDynamicButton(String name, int textSize, int textColor, int width, int height) {
		return addDynamicButton(name, textSize, textColor, 0, 0, width, height);
	}

	public Widget addDynamicButton(String name, int textSize, int textColor, int textXOff, int textYOff, int width,
								   int height) {
		return addDynamicButton(name, textSize, textColor, textXOff, textYOff, width, height, mainId);
	}

	public Widget addDynamicButton(String name, int textSize, int textColor, int textXOff, int textYOff, int width,
								   int height, int layerId) {
		Widget rsi = Widget.addInterface(id, width, height);
		rsi.componentId = id++;
		rsi.layerId = layerId;
		rsi.totalChildren(2);

		Widget button = Widget.addInterface(id);
		button.componentId = id++;
		button.type = Widget.DYNAMIC_BUTTON;
		button.atActionType = 1;
		button.contentType = 0;
		button.tooltip = name;
		button.width = width;
		button.height = height;

		int textHeight = 13 + textSize;

		int textDecrease = textSize > 1 ? -1 : 1;

		Widget text = addText(name, textSize, textColor, true, false, false, true);
		rsi.child(0, button.id, 0, 0);
		rsi.child(1, text.id, width / 2 + textXOff,
				((height / 2) - (textHeight / 2)) + (textSize == 0 ? +textDecrease : -textDecrease) + textYOff);
		id++;
		return rsi;
	}


	public Widget addInputFieldList(String[] list, int sprite1, int sprite2, int textSize, int colour, int offset,
			boolean verticle) {
		Widget tab = Widget.addInterface(id);
		tab.componentId = id;
		id++;

		tab.totalChildren(list.length);

		int height = SpriteCompanion.cacheSprite[sprite1].myHeight + 1;
		int width = SpriteCompanion.cacheSprite[sprite1].myWidth + 1;

		int frame = 0;

		int y = 0;
		int x = 0;

		for (String s : list) {
			boolean triggerType = s.contains("[");

			Widget button = triggerType ? addButton(s, sprite1, sprite2, textSize, colour, false)
					: addInputField(s, sprite1, sprite2, textSize);

			tab.child(frame++, button.componentId, x, y);
			if (verticle) {
				y += height + offset;
			} else {
				x += width + offset;
			}
		}
		id++;
		return tab;
	}

	public Widget addScrollbarWithInputFieldList(String[] list, int sprite1, int sprite2, int size, int colour,
			int scrollWidth, int scrollHeight, int offset, boolean verticle) {
		Widget scroll = Widget.addInterface(id);
		scroll.componentId = id;
		id++;

		scroll.totalChildren(1);

		scroll.height = scrollHeight;
		scroll.width = scrollWidth;

		Sprite sprite = SpriteCompanion.cacheSprite[sprite1];

		int scrollMax = 5 + (sprite.myHeight * (12 + size));

		scroll.scrollMax = scrollMax < scrollHeight ? scrollHeight + 1 : scrollMax;

		int scroll_frame = 0;

		int y = 2;

		Widget l = addInputFieldList(list, sprite1, sprite2, size, colour, offset, verticle);
		Widget.setBounds(l.componentId, 5, y, scroll_frame, scroll);

		scroll_frame++;

		id++;

		return scroll;
	}

	public Widget addButtonList(String[] list, int sprite1, int sprite2, int textSize, int colour, int offset,
			boolean verticle) {
		Widget tab = Widget.addInterface(id);
		tab.componentId = id;
		id++;

		tab.totalChildren(list.length);

		int height = SpriteCompanion.cacheSprite[sprite1].myHeight + 1;
		int width = SpriteCompanion.cacheSprite[sprite1].myWidth + 1;

		int frame = 0;

		int y = 0;
		int x = 0;

		for (String s : list) {
			Widget button = addButton(s, sprite1, sprite2, textSize, colour, false);
			tab.child(frame++, button.componentId, x, y);
			if (verticle) {
				y += height + offset;
			} else {
				x += width + offset;
			}
		}
		id++;
		return tab;
	}

	public Widget addButtonList(String[] list, int sprite1, int sprite2, int offset, boolean verticle) {
		return addButtonList(list, sprite1, sprite2, -1, -1, offset, verticle);
	}

	public Widget addConfigButton(String text, int disabledSpriteId, int enabledSpriteId, int configId,
			int configSlot) {
		Widget tab = Widget.addInterface(id);

		int height = SpriteCompanion.cacheSprite[disabledSpriteId].myHeight + 1;
		int width = SpriteCompanion.cacheSprite[disabledSpriteId].myWidth + 1;

		tab.componentId = id;
		tab.id = id;
		tab.parent = id;
		tab.type = 5;
		tab.atActionType = 1;
		tab.contentType = -1;
		tab.opacity = 0;
		tab.width = width;
		tab.height = height;
		tab.tooltip = text;
		tab.valueCompareType = new int[1];
		tab.requiredValues = new int[1];
		tab.valueCompareType[0] = 1;
		tab.requiredValues[0] = configSlot;
		tab.valueIndexArray = new int[1][3];
		tab.valueIndexArray[0][0] = 5;
		tab.valueIndexArray[0][1] = configId;
		tab.valueIndexArray[0][2] = 0;
		if (disabledSpriteId != -1)
			tab.disabledSprite = SpriteCompanion.cacheSprite[disabledSpriteId];
		if (enabledSpriteId != -1)
			tab.enabledSprite = SpriteCompanion.cacheSprite[enabledSpriteId];
		id++;
		return tab;
	}

	public Widget addButtonConfigList(String[] list, int sprite1, int sprite2, int offset, boolean verticle,
			int configId) {
		Widget tab = Widget.addInterface(id);
		tab.componentId = id;
		id++;

		tab.totalChildren(list.length);

		int height = SpriteCompanion.cacheSprite[sprite1].myHeight + 1;
		int width = SpriteCompanion.cacheSprite[sprite1].myWidth + 1;

		int frame = 0;

		int y = 0;
		int x = 0;

		int slot = 0;

		for (String s : list) {
			Widget button = addConfigButton(s, sprite1, sprite2, configId, slot);
			tab.child(frame++, button.componentId, x, y);
			slot++;
			if (verticle) {
				y += height + offset;
			} else {
				x += width + offset;
			}
		}
		id++;
		return tab;
	}

	public Widget addScrollbarWithItem(int w, int h, int x, int y, String[] action, int scrollHeight, int scrollWidth) {
		System.out.println(getName() + " scrollbar: " + id);
		Widget scroll = Widget.addInterface(id);
		scroll.componentId = id;
		id++;

		scroll.totalChildren(1);
		scroll.height = scrollHeight;
		scroll.width = scrollWidth;
		scroll.scrollMax = (y + 32) * h;
		int scroll_frame = 0;

		System.out.println(getName() + " container: " + w + "x" + h + " = " + (w * h) + ". " + id);
		Widget item = Widget.addToItemGroup(id, w, h, x, y, action == null ? false : true, "", "", "");

		for (int i = 0; i < item.inventoryItemId.length; i++) {
			item.inventoryItemId[i] = 996;
			item.inventoryAmounts[i] = i;
		}

		item.actions = new String[5];

		for (int i = 0; i < item.actions.length; i++) {
			item.actions[i] = null;
		}

		if (action != null) {
			for (int i = 0; i < action.length; i++) {
				item.actions[i] = action[i];
			}
		}

		Widget.setBounds(id, 5, 2 + y, scroll_frame, scroll);
		scroll_frame++;
		id++;
		return scroll;
	}

	public Widget addScrollbarWithClickText(String text, String tooltip, int size, int colour, int scrollHeight,
			int scrollWidth, int lineAmount) {
		System.out.println("scrollbar with text "+tooltip+" "+lineAmount+", "+id);
		Widget scroll = Widget.addInterface(id);
		scroll.componentId = id;
		id++;
		scroll.totalChildren(lineAmount);
		scroll.height = scrollHeight;
		scroll.width = scrollWidth;
		int scrollMax = 5 + (lineAmount * (12 + size));
		scroll.scrollMax = scrollMax < scrollHeight ? scrollHeight+1 : scrollMax;
		int scroll_frame = 0;

		int y = 2;

		for (int i = 0; i < lineAmount; i++) {
			Widget.addHoverText(id, text + (text.contains("#") ? "" + id : ""), tooltip, Widget.defaultFont, size, colour,
					false, true, 150);
			Widget.setBounds(id, 5, y, scroll_frame, scroll);
			scroll_frame++;
			id++;
			y += 12 + size;
		}
		return scroll;
	}

	public Widget addScrollbarWithClickTextBackground(String text, String tooltip, int size, int colour,
			int background1, int background2, int hover, int scrollHeight, int scrollWidth, int lineAmount) {
		System.out.println(getName() + " scrollbar: " + text + " " + tooltip + " " + id);
		Widget scroll = Widget.addInterface(id);
		scroll.componentId = id;
		id++;
		scroll.totalChildren(lineAmount * 3);
		scroll.height = scrollHeight;
		scroll.width = scrollWidth;
		int scrollMax = 5 + (lineAmount * (12 + size));
		scroll.scrollMax = scrollMax < scrollHeight ? scrollHeight : scrollMax;
		int scroll_frame = 0;

		int y = 2;

		int sprite = 0;

		for (int i = 0; i < lineAmount; i++) {
			int sprite1 = sprite % 2 == 1 ? background1 : background2;

			sprite++;

			int width = SpriteCompanion.cacheSprite[sprite1].myWidth;
			int height = SpriteCompanion.cacheSprite[sprite1].myHeight;

			Widget.addHoverButton(id, sprite1, width, height, "Select @lre@" + text, -1, id + 1, 5);
			Widget.addHoveredButton(id + 1, hover, width, height, id + 2);
			scroll.child(scroll_frame++, id, 0, y);
			scroll.child(scroll_frame++, id + 1, 0, y);
			id += 3;

			Widget.addText(id, (text.contains("#") ? "" + id : text), Widget.defaultFont, size, colour, false, true);
			scroll.child(scroll_frame++, id, 4, y + 2);
			id++;

			y += 15 + size;
		}
		return scroll;
	}

	public Widget addModel() {
		Widget tab = Widget.addInterface(id);
		tab.componentId = id;
		tab.id = id;
		tab.parent = id;
		id++;
		tab.type = 6;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.width = 50;
		tab.height = 50;
		tab.opacity = 0;
		return tab;
	}

	public Widget addPercentageBar(int percentageDimension, int percentageBarStart, int percentageBarEnd,
								   int percentageSpritePart, int percentageSpriteEmpty, int percentageTotal, int percentageMultiplier) {
		System.out.println(getName()+" "+id+" percentage bar: "+percentageDimension);
		Widget bar = Widget.addPercentageBar(id,  percentageDimension,  percentageBarStart,  percentageBarEnd,
		 percentageSpritePart,  percentageSpriteEmpty,  percentageTotal,  percentageMultiplier);
		bar.componentId= id;
		id++;
		return bar;
	}

	public Widget addGroup(WidgetGroup group) {
		Widget scroll = Widget.addInterface(id);
		scroll.componentId = id;

		id++;

		int single = 0;

		if (group.singleComponents != null) {
			single = group.singleComponents.size();
		}

		scroll.totalChildren(single + (group.lines * group.multipleComponents.size()));

		scroll.height = group.scrollHeight;
		scroll.width = group.scrollWidth;

		int scrollMax = (group.difference / 2) + (group.difference * group.lines);

		if (scrollMax < group.scrollHeight) {
			scrollMax = group.scrollHeight + 1;
		}

		scroll.scrollMax = scrollMax;

		int scroll_frame = 0;

		int y = 0;

		for (int i = 0; i < group.lines; i++) {
			for (WidgetComponent w : group.multipleComponents) {
				Widget rs = Widget.copy(id, w.componentId);
				rs.parent = scroll.id;
				Widget.setBounds(rs.id, w.point.x + group.offsetX, w.point.y + y + group.offsetY, scroll_frame, scroll);
				scroll_frame++;
				id++;
			}
			y += group.difference;
		}

		id++;

		if (group.singleComponents != null) {
			for (WidgetComponent w : group.singleComponents) {
				Widget rs = Widget.copy(id, w.componentId);
				Widget.setBounds(rs.id, w.point.x + group.offsetX, w.point.y + group.offsetY, scroll_frame, scroll);
				scroll_frame++;
				id++;
			}
		}
		return scroll;
	}
}
