package com.runescape.cache.graphics.widget;

import com.grinder.Configuration;
import com.grinder.client.ClientCompanion;
import com.grinder.client.util.InterfaceUtility;
import com.grinder.model.Keybinding;
import com.runescape.Client;
import com.runescape.cache.FileArchive;
import com.runescape.cache.Js5;
import com.runescape.cache.ModelData;
import com.runescape.cache.OsCache;
import com.runescape.cache.anim.Animation;
import com.runescape.cache.def.ItemDefinition;
import com.runescape.cache.def.NpcDefinition;
import com.runescape.cache.graphics.GameFont;
import com.runescape.cache.graphics.RSFont;
import com.runescape.cache.graphics.sprite.AutomaticSprite;
import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.cache.graphics.sprite.SpriteCompanion;
import com.runescape.cache.graphics.sprite.SpriteLoader;
import com.runescape.cache.graphics.widget.ItemColorCustomizer.ColorfulItem;
import com.runescape.cache.graphics.widget.component.RectangleComponent;
import com.runescape.cache.graphics.widget.component.TextComponent;
import com.runescape.cache.graphics.widget.custom.CustomWidgetLoader;
import com.runescape.cache.graphics.widget.dynamicinterface.DynamicInterface;
import com.runescape.collection.EvictingDualNodeHashTable;
import com.runescape.entity.Player.Rights;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;
import com.runescape.util.PacketMetaData;
import com.runescape.util.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Previously known as RSInterface, which is a class used to create and show
 * game interfaces.
 */
public class Widget {
public int componentId;

	public boolean isToggled;
	public int toggleColor;
	public int toggleTransparency;
	public int layerId;
	private static final int ARRAY_SIZE = 184000;

	public static final int ITEM_DRAG_TYPE_NONE = 0;
	public static final int ITEM_DRAG_TYPE_REGULAR = 2;
	public static final int ITEM_DRAG_TYPE_OPEN_INTERFACE = 1;
	public static final int ITEM_DRAG_TYPE_BACK_DIALOGUE = 3;

	public static final int OPTION_OK = 1;
	public static final int OPTION_USABLE = 2;
	public static final int OPTION_CLOSE = 3;
	public static final int OPTION_TOGGLE_SETTING = 4;
	public static final int OPTION_RESET_SETTING = 5;
	public static final int OPTION_CONTINUE = 6;
	public static final int OPTION_DROPDOWN = 7;
	public static final int OPTION_COLOR_BOX = 31;
	public static final int OPTION_COLOR_PICKER = 32;
	public static final int OPTION_INPUT = 33;
	public static final int OPTION_PRESET = 34;

	public static final int TYPE_CONTAINER = 0;
	public static final int TYPE_MODEL_LIST = 1;
	public static final int TYPE_INVENTORY = 2;
	public static final int TYPE_RECTANGLE = 3;
	public static final int TYPE_TEXT = 4;
	public static final int TYPE_SPRITE = 5;
	public static final int TYPE_MODEL = 6;
	public static final int TYPE_ITEM_LIST = 7;
	public static final int TYPE_OTHER = 8;
	public static final int TYPE_HOVER = 9;
	public static final int TYPE_CONFIG = 10;
	public static final int TYPE_CONFIG_HOVER = 11;
	public static final int TYPE_SLIDER = 12;
	public static final int TYPE_DROPDOWN = 13;
	public static final int TYPE_ROTATING = 14;
	public static final int TYPE_KEYBINDS_DROPDOWN = 15;
	public static final int TYPE_TICKER = 16;
	public static final int TYPE_ADJUSTABLE_CONFIG = 17;
	public static final int TYPE_BOX = 18;
	public static final int TYPE_MAP = 19;
	public static final int TYPE_PERCENTAGE = 20;
	public static final int TYPE_WINDOW = 24;
	public static final int TYPE_HORIZONTAL_SEPARATOR = 25;
	public static final int TYPE_VERTICAL_SEPARATOR = 26;
	public static final int TYPE_DARK_BOX = 27;
	public static final int TYPE_TRADE_BUTTON = 28;
	public static final int TYPE_HOVER_SPRITE = 29;
	public static final int TYPE_HOVER_SPEED_SPRITE = 30;
	public static final int TYPE_COLOR_BOX = 31;
	public static final int TYPE_COLOR_PICKER = 32;
	public static final int TYPE_INPUT = 33;
	public static final int TYPE_TRANSPARENT_HOVER_RECTANGLE = 34;
	public static final int DYNAMIC_BUTTON = 35;

	public static final int BEGIN_READING_PRAYER_INTERFACE = 6;// Amount of
																// total custom
																// prayers we've
																// added
	public static final int CUSTOM_PRAYER_HOVERS = 3; // Amount of custom prayer
														// hovers we've added
	public static final int PRAYER_INTERFACE_CHILDREN = 80 + BEGIN_READING_PRAYER_INTERFACE + CUSTOM_PRAYER_HOVERS;
	private static final EvictingDualNodeHashTable models = new EvictingDualNodeHashTable(30);
	public static final int CLAN_CHAT_MAX_MEMBERS = 200;
	public static final int CLAN_CHAT_PARENT_WIDGET_ID = 37128;
	public static FileArchive interfaceLoader;
	public static Widget[] interfaceCache;
	public static RSFont[] newFonts;
	private static EvictingDualNodeHashTable spriteCache;
	public int hoverXOffset = 0;
	public int hoverYOffset = 0;
	public int spriteXOffset = 0;
	public int spriteYOffset = 0;
	public boolean regularHoverBox;
	public int transparency = 255;
	public String hoverText;
	public boolean drawsTransparent;
	public boolean drawsAdvanced;
	public Sprite disabledSprite;
	public int lastFrameTime;
	public Sprite[] sprites;
	public int[] requiredValues;
	public int contentType;
	public int[] spritesX;
	public int defaultHoverColor;
	public int atActionType;
	public String spellName;
	public int secondaryColor;
	public int width;
	public String tooltip;
	public String selectedActionName;
	public boolean centerText;
	public boolean rightAlignedText;
	public boolean rollingText;
	public int scrollPosition;
	public String[] actions;
	public int[][] valueIndexArray;
	public boolean filled;
	public String secondaryText;
	public int hoverType;
	public int spritePaddingX;
	public int textColor;
	public int defaultMediaType;
	public int defaultMedia;
	public boolean replaceItems;
	public int parent;
	public int spellUsableOn;
	public int secondaryHoverColor;
	public int[] children;
	public int[] childX;
	public boolean usableItems;
	public GameFont textDrawingAreas;
	public boolean inventoryHover;
	public int spritePaddingY;
	public int[] valueCompareType;
	public int currentFrame;
	public int[] spritesY;
	public String defaultText;
	public boolean hasActions;
	public int id;
	public int[] inventoryAmounts;
	public int[] inventoryItemId;
	public Sprite defaultSprite;
	public byte opacity;
	public int defaultAnimationId;
	public int secondaryAnimationId;
	public boolean allowSwapItems;
	public Sprite enabledSprite;
	public int scrollMax;
	public int type;
	public int horizontalOffset;
	public int verticalOffset;
	public boolean invisible;
	public boolean hidden;
	public int height;
	public boolean textShadow;
	public int modelZoom;
	public int modelXAngle;
	public int modelYAngle;
	public int modelZAngle;
	public int[] childY;
	public DropdownMenu dropdown;
	public int[] dropdownColours;
	public boolean hovered = false;
	public Widget dropdownOpen;
	public int dropdownHover = -1;
	public Slider slider;
	public RSFont rsFont;
	public int msgX, msgY;
	public boolean toggled = false;
	public Sprite enabledAltSprite;
	public Sprite disabledAltSprite;
	public int[] buttonsToDisable;
	public boolean active;
	public boolean inverted;
	public int spriteOpacity;
	public String[] tooltips;
	public boolean newScroller;
	public boolean drawInfinity;
	protected int modelType;
	protected int modelId;
	public boolean transparentOnHover;
	public Sprite hoverSprite1;
	public Sprite hoverSprite2;
	public int hoverSpeed;
	public int buttonAlpha;
	public long timerStartTime;
	public int timerSpeed;
	public boolean hideTooltipIfSelected;
	public boolean openMenuButton;
	public int textHorizontalOffset;
	private static List<Widget> widgetWithInstructions = new ArrayList<>();
	public float[] hsb;
	public Pattern pattern;
	public boolean autoUpdate;
	public boolean noName;

	public Widget() {
	}
	private static void printChildContaining(String text) {
		for (int i = 0; i < interfaceCache.length; i++) {
			Widget widget = interfaceCache[i];
			if (widget != null) {
				if(widget.getDefaultText() != null){
					if(widget.getDefaultText().toLowerCase().contains(text.toLowerCase())){
						System.out.println("widget["+i+"] contains "+text+", parent = "+widget.parent);
					}
				}
			}
		}

	}

	private static void printFreeIdRange(int minimumFreeSlotsAvailable){

		int start = 0;

		int freeSlots = 0;
		boolean started = false;

		for(int i = 0; i < interfaceCache.length; i++){
			Widget widget = interfaceCache[i];
			if(widget != null)
			{
				if(started){
					if(freeSlots >= minimumFreeSlotsAvailable)
						System.out.println("RANGE	["+start+", "+(start+freeSlots)+"]	(has "+freeSlots+" free slots)");
				}
				started = false;
				freeSlots = 0;
				if(widget.children != null) {
					i += widget.children.length;
				}
			} else {
				if(!started) {
					start = i;
					started = true;
				}
				freeSlots++;
			}
		}
	}
	public static void load(FileArchive interfaces, GameFont[] font, FileArchive graphics,
							RSFont[] newFontSystem) {
		spriteCache = new EvictingDualNodeHashTable(50000);
		Buffer buffer = new Buffer(interfaces.readFile("data"));
		newFonts = newFontSystem;
		int defaultParentId = -1;
		buffer.readUShort();
		interfaceCache = new Widget[ARRAY_SIZE];

		while (buffer.index < buffer.array.length) {
			int interfaceId = buffer.readUShort();
			if (interfaceId == 65535) {
				defaultParentId = buffer.readUShort();
				interfaceId = buffer.readUShort();
			}

			Widget widget = interfaceCache[interfaceId] = new Widget();
			widget.id = interfaceId;
			widget.parent = defaultParentId;
			widget.type = buffer.readUnsignedByte();
			widget.atActionType = buffer.readUnsignedByte();
			widget.contentType = buffer.readUShort();
			widget.width = buffer.readUShort();
			widget.height = buffer.readUShort();
			if (widget.id == 1688) {
				widget.width = 3;
				widget.height = 6;
			}
			widget.opacity = (byte) buffer.readUnsignedByte();
			widget.hoverType = buffer.readUnsignedByte();
			if (widget.hoverType != 0)
				widget.hoverType = (widget.hoverType - 1 << 8) + buffer.readUnsignedByte();
			else
				widget.hoverType = -1;
			int operators = buffer.readUnsignedByte();
			if (operators > 0) {
				widget.valueCompareType = new int[operators];
				widget.requiredValues = new int[operators];
				for (int index = 0; index < operators; index++) {
					widget.valueCompareType[index] = buffer.readUnsignedByte();
					widget.requiredValues[index] = buffer.readUShort();
				}

			}
			int scripts = buffer.readUnsignedByte();
			if (scripts > 0) {
				widget.valueIndexArray = new int[scripts][];
				for (int script = 0; script < scripts; script++) {
					boolean addTolist = false;

					int instructions = buffer.readUShort();
					widget.valueIndexArray[script] = new int[instructions];
					for (int instruction = 0; instruction < instructions; instruction++) {
						widget.valueIndexArray[script][instruction] = buffer.readUShort();
						if (instruction == 10) {
							addTolist = true;
						}
					}

					if (addTolist) {
						widgetWithInstructions.add(widget);
					}
				}

			}
			if (widget.type == TYPE_CONTAINER) {
				widget.drawsTransparent = false;
				widget.scrollMax = buffer.readUShort();
				widget.invisible = buffer.readUnsignedByte() == 1;
				int length = buffer.readUShort();

				if (widget.id == 5608) {

					widget.children = new int[PRAYER_INTERFACE_CHILDREN];
					widget.childX = new int[PRAYER_INTERFACE_CHILDREN];
					widget.childY = new int[PRAYER_INTERFACE_CHILDREN];

					for (int index = 0; index < length; index++) {
						widget.children[BEGIN_READING_PRAYER_INTERFACE + index] = buffer.readUShort();
						widget.childX[BEGIN_READING_PRAYER_INTERFACE + index] = buffer.readShort();
						widget.childY[BEGIN_READING_PRAYER_INTERFACE + index] = buffer.readShort();
					}

				} else {
					widget.children = new int[length];
					widget.childX = new int[length];
					widget.childY = new int[length];

					for (int index = 0; index < length; index++) {
						widget.children[index] = buffer.readUShort();
						widget.childX[index] = buffer.readShort();
						widget.childY[index] = buffer.readShort();
					}
				}
			}
			if (widget.type == TYPE_MODEL_LIST) {
				buffer.readUShort();
				buffer.readUnsignedByte();
			}
			if (widget.type == TYPE_INVENTORY) {
				widget.inventoryItemId = new int[widget.width * widget.height];
				widget.inventoryAmounts = new int[widget.width * widget.height];
				widget.allowSwapItems = buffer.readUnsignedByte() == 1;
				widget.hasActions = buffer.readUnsignedByte() == 1;
				widget.usableItems = buffer.readUnsignedByte() == 1;
				widget.replaceItems = buffer.readUnsignedByte() == 1;
				widget.spritePaddingX = buffer.readUnsignedByte();
				widget.spritePaddingY = buffer.readUnsignedByte();
				widget.spritesX = new int[20];
				widget.spritesY = new int[20];
				widget.sprites = new Sprite[20];
				for (int j2 = 0; j2 < 20; j2++) {
					int k3 = buffer.readUnsignedByte();
					if (k3 == 1) {
						widget.spritesX[j2] = buffer.readShort();
						widget.spritesY[j2] = buffer.readShort();
						String s1 = buffer.readString();
						if (graphics != null && s1.length() > 0) {
							int i5 = s1.lastIndexOf(",");
							int index = Integer.parseInt(s1.substring(i5 + 1));

							String name = s1.substring(0, i5);
							widget.sprites[j2] = getSprite(index, graphics, name);
						}
					}
				}
				if (widget.id == 1688) {
					widget.spritesX[14] = -112;
					widget.spritesY[14] = -198;
					widget.sprites[14] = getSprite(12, graphics, "wornicons");
					widget.spritesX[15] = 112;
					widget.spritesY[15] = -238;
					widget.sprites[15] = getSprite(13, graphics, "wornicons");
					widget.spritesX[16] = 56;
					widget.spritesY[16] = -120;
					widget.sprites[16] = getSprite(14, graphics, "wornicons");
				}

				widget.actions = new String[5];
				for (int actionIndex = 0; actionIndex < 5; actionIndex++) {
					widget.actions[actionIndex] = buffer.readString();
					if (widget.actions[actionIndex].length() == 0)
						widget.actions[actionIndex] = null;
					if (widget.parent == 1644)
						widget.actions[2] = "Operate";
					if (widget.parent == 3824) {
						widget.actions[4] = "Buy X";
					}
					if (widget.parent == 3822) {
						widget.actions[4] = "Sell X";
					}
				}
			}
			if (widget.type == TYPE_RECTANGLE)
				widget.filled = buffer.readUnsignedByte() == 1;
			if (widget.type == TYPE_TEXT || widget.type == TYPE_MODEL_LIST) {
				widget.centerText = buffer.readUnsignedByte() == 1;
				int k2 = buffer.readUnsignedByte();
				if (font != null)
					widget.textDrawingAreas = font[k2];
				widget.textShadow = buffer.readUnsignedByte() == 1;
			}

			if (widget.type == TYPE_TEXT) {
				widget.defaultText = buffer.readString().replaceAll("RuneScape", "Grinderscape");
				widget.secondaryText = buffer.readString();
			}

			if (widget.type == TYPE_MODEL_LIST || widget.type == TYPE_RECTANGLE || widget.type == TYPE_TEXT)
				widget.textColor = buffer.readInt();
			if (widget.type == TYPE_RECTANGLE || widget.type == TYPE_TEXT) {
				widget.secondaryColor = buffer.readInt();
				widget.defaultHoverColor = buffer.readInt();
				widget.secondaryHoverColor = buffer.readInt();
			}
			if (widget.type == TYPE_SPRITE) {
				widget.drawsTransparent = false;
				String name = buffer.readString();
				if (graphics != null && name.length() > 0) {
					int index = name.lastIndexOf(",");
					widget.disabledSprite = getSprite(Integer.parseInt(name.substring(index + 1)), graphics,
							name.substring(0, index));
				}
				name = buffer.readString();
				if (graphics != null && name.length() > 0) {
					int index = name.lastIndexOf(",");
					widget.enabledSprite = getSprite(Integer.parseInt(name.substring(index + 1)), graphics,
							name.substring(0, index));
				}
			}
			if (widget.type == TYPE_MODEL) {
				int content = buffer.readUnsignedByte();
				if (content != 0) {
					widget.defaultMediaType = 1;
					widget.defaultMedia = (content - 1 << 8) + buffer.readUnsignedByte();
				}
				content = buffer.readUnsignedByte();
				if (content != 0) {
					widget.modelType = 1;
					widget.modelId = (content - 1 << 8) + buffer.readUnsignedByte();
				}
				content = buffer.readUnsignedByte();
				if (content != 0)
					widget.defaultAnimationId = (content - 1 << 8) + buffer.readUnsignedByte();
				else
					widget.defaultAnimationId = -1;
				content = buffer.readUnsignedByte();
				if (content != 0)
					widget.secondaryAnimationId = (content - 1 << 8) + buffer.readUnsignedByte();
				else
					widget.secondaryAnimationId = -1;
				widget.modelZoom = buffer.readUShort();
				widget.modelXAngle = buffer.readUShort();
				widget.modelYAngle = buffer.readUShort();
			}
			if (widget.type == TYPE_ITEM_LIST) {
				widget.inventoryItemId = new int[widget.width * widget.height];
				widget.inventoryAmounts = new int[widget.width * widget.height];
				widget.centerText = buffer.readUnsignedByte() == 1;
				int l2 = buffer.readUnsignedByte();
				if (font != null)
					widget.textDrawingAreas = font[l2];
				widget.textShadow = buffer.readUnsignedByte() == 1;
				widget.textColor = buffer.readInt();
				widget.spritePaddingX = buffer.readShort();
				widget.spritePaddingY = buffer.readShort();
				widget.hasActions = buffer.readUnsignedByte() == 1;
				widget.actions = new String[5];
				for (int actionCount = 0; actionCount < 5; actionCount++) {
					widget.actions[actionCount] = buffer.readString();
					if (widget.actions[actionCount].length() == 0)
						widget.actions[actionCount] = null;
				}

			}
			if (widget.atActionType == OPTION_USABLE || widget.type == TYPE_INVENTORY) {
				widget.selectedActionName = buffer.readString();
				widget.spellName = buffer.readString();
				widget.spellUsableOn = buffer.readUShort();
			}

			if (widget.type == 8) {
				widget.defaultText = buffer.readString();
			}

			if (widget.atActionType == OPTION_OK || widget.atActionType == OPTION_TOGGLE_SETTING
					|| widget.atActionType == OPTION_RESET_SETTING || widget.atActionType == OPTION_CONTINUE) {
				widget.tooltip = buffer.readString();
				if (widget.tooltip.length() == 0) {
					// TODO
					if (widget.atActionType == OPTION_OK)
						widget.tooltip = "Ok";
					if (widget.atActionType == OPTION_TOGGLE_SETTING)
						widget.tooltip = "Select";
					if (widget.atActionType == OPTION_RESET_SETTING)
						widget.tooltip = "Select";
					if (widget.atActionType == OPTION_CONTINUE)
						widget.tooltip = "Continue";
				}
			}
		}

		interfaceLoader = interfaces;
		defaultFont = font;

		/*
		 * Populate the interface manager
		 */
		DynamicInterface.populate();

		for (DynamicInterface rsi : DynamicInterface.dynamicInterfaces) {
			rsi.build();
			rsi.position();
		}

		clanChatTab(font);
		clanChatSetup(font);
		Spellbooks.widgets(font);
		quickPrayers(font);
		equipmentScreen(font);
		equipmentTab(font);
		itemsKeptOnDeath(font);
		//bounty(textDrawingAreas);
		worldMap();
		inventory();
		fullscreenLogin();
		shop();
        newShop(font);
		itemFinder(font);
		prayerBook();
		PriceChecker.widget(font);

		bank(font);

		SettingsWidget.widget(font);
		SettingsWidget.advancedWidget(font);
		ExpCounterSetup.widget(font);
		keybinding(font);
		levelUpInterfaces();
		GodWars(font);
		teleport(font);
		presets(font);
		editSkillTab(font);
		lootingBag(font);
		commands(font);
		runePouch(font);
		tradeFirst(font);
		tradeSecond(font);
		skillGuide(font);
		emojisPanel(font);
		jewelryMaking(font);
		boltEnchanting(font);
		slayerInterfaces(font);
		TitleChooser.widget(font);
		newMysteryBox(font);
		oldMysteryBox(font);
		mysteryBoxReward(font);
		mysteryBoxRewards(font);
		QuestTab.widget(font);
		vote(font);
		YellCustomizer.widget(font);
		ColorPicker.widget(font);
		GameModeSetup.widget(font);
		BlackJack.widget(font);
		ChangePassword.widget(font);
		ItemColorCustomizer.widget(font);
		Broadcast.widget(font);
		SafeDeposit.widget(font);
		//InstanceDeadItemCollectionBox.widget(font);
		dicing(font);
		flowerGame(font);
		achievementCompletion(font);
		achievementProgression(font);
		bossDropTables(font);
		ItemDropFinder.widget(font);
		myCommands(font);
		resetLamp();
		combatLamp(font);
		catapultDefence(font);
		catapultAction(font);
		dummies(font);
		motherlodeMine(font);
		debugBox();
		CollectionLog();
		CustomWidgetLoader.init();
		PresetLoader();

		spriteCache = null;
		if (Configuration.DUMP_INTERFACE_INFORMATION) {
			InterfaceUtility.dumpInterfaceInformation();
		}
		/*
		 * int lastNull = -1; for (int a = interfaceCache.length - 1; a > 0;
		 * a--) { if (lastNull == -1 && interfaceCache[a] == null) { lastNull =
		 * a; } if (lastNull > 0 && interfaceCache[a] != null) {
		 * System.out.println("START " + (a + 1) + " LAST: " + lastNull +
		 * " SIZE: " + (lastNull - a)); lastNull = -1; } }
		 */
//		printFreeIdRange(200);
	}

	private static void PresetLoader()
	{
		int id = 63517;

		for (int i = 0; i < 20; i += 2) {
			Widget widget = interfaceCache[id+i];

			widget.atActionType = OPTION_PRESET;
			widget.actions = new String[]{"Select", "Load", "Delete", "Edit Name"};
		}
	}

	public static GameFont[] defaultFont;

	public static void combatLamp(GameFont[] tda) {
		int id = 77300;
		Widget w = addInterface(id++);
		w.totalChildren(17);
		int child = 0;

		w.child(child++, 2809, 244, 152);
		w.child(child++, 2811, 433, 12);

		addText(id, "Choose a stat you wish to gain experience in!", tda, 3, 0xFFFF00, true);
		w.child(child++, id++, 256, 110);

		String[] skills = { "Attack", "Strength", "Ranged", "Magic", "Defence", "Hitpoints" };
		int config = 1170;
		int configIndex = 1;
		int buttonX = 188;
		int buttonY = 140;
		int buttonSize = 36;
		for (int i = 0; i < skills.length; i++) {
			addResetSettingButton(id, buttonSize, buttonSize, AutomaticSprite.BUTTON_GREY, AutomaticSprite.BUTTON_RED, "Choose " + skills[i], configIndex++, config);
			w.child(child++, id++, buttonX, buttonY);

			addAdvancedSprite(id, 1001 + i);
			w.child(child++, id++, buttonX, buttonY);

			if ((i + 1) % 3 == 0) {
				buttonX = 188;
				buttonY += 15 + buttonSize;
			} else {
				buttonX += 15 + buttonSize;
			}
		}

		addHoverTextTimerConfigButton(id, 138, 25, AutomaticSprite.BUTTON_RED, AutomaticSprite.BUTTON_GREY, "Confirm", tda, 1, 0xff981f, 0xffffff, 0, 1171, 375);
		interfaceCache[id].textHorizontalOffset = 12;
		w.child(child++, id++, 188, 242);

		addAdvancedSprite(id, 1007);
		w.child(child++, id++, 188 + 35, 243);
	}

	public static void resetLamp() {
		interfaceCache[2810].defaultText = "Choose the stat you wish to be reset!";
		int[] spriteIds = {2812, 2813, 2814, 2815, 2816, 2817, 2818, 2819, 2820, 2821, 2822, 2823, 2824, 2825, 2826, 2827, 2828, 2829, 2830, 12034, 13914};
		for (int spriteId : spriteIds) {
			interfaceCache[spriteId].disabledSprite = AutomaticSprite.BUTTON_GREY.getButton(25, 25);
			interfaceCache[spriteId].enabledSprite = AutomaticSprite.BUTTON_RED.getButton(25, 25);
		}
	}

	public static void dicing(GameFont[] wid) {
		int id = 60000;

		Widget w = addInterface(id++);

		setChildren(36 + 3, w);

		int child = 0;

		addClosableWindow(id, 485, 304, true, "Gambling with: Blake2");
		w.child(child++, id, 14, 16);
		id += 8;

		addTransparentRectangle(id, 187, 90 + 12, 0, true, 36);
		w.child(child++, id, 20, 263 - 51);
		id++;

		addTransparentRectangle(id, 180 + 99, 90 + 12, 0, true, 36);
		w.child(child++, id, 21 + 185 + 8, 263 - 51);
		id++;

		addHorizontalDivider(id, 473, AutomaticSprite.DIVIDER_HORIZONTAL_BROWN);
		w.child(child++, id, 20, 180);
		id++;

		addVerticalDivider(id, 87, AutomaticSprite.DIVIDER_VERTICAL_BROWN);
		w.child(child++, id, 254, 51);
		id++;

		addVerticalDivider(id, 130, AutomaticSprite.DIVIDER_VERTICAL_BROWN);
		w.child(child++, id, 176, 50);
		id++;

		addVerticalDivider(id, 130, AutomaticSprite.DIVIDER_VERTICAL_BROWN);
		w.child(child++, id, 332, 50);
		id++;

		int tradeX = 218;
		int tradeY = 100 - 36;

		addDarkBox(id, 78, 43, false, BoxBorder.MODERN);
		w.child(child++, id, tradeX, tradeY);
		id += 2;

		addDarkBox(id, 78, 43, false, BoxBorder.MODERN);
		w.child(child++, id, tradeX, tradeY + 53);
		id += 2;

		addRectangle(id, 66, 31, 0, true);
		w.child(child++, id, tradeX + 6, tradeY + 6);
		id++;

		addRectangle(id, 66, 31, 0, true);
		w.child(child++, id, tradeX + 6, tradeY + 53 + 6);
		id++;

		addHoverClickText(id, "Accept", "Accept", wid, 1, 0x00ff00, true, false, 66);
		w.child(child++, id, tradeX + 5, tradeY + 13);
		id++;

		addHoverClickText(id, "Decline", "Decline", wid, 1, 0xff0000, true, false, 66);
		w.child(child++, id, tradeX + 5, tradeY + 53 + 13);
		id++;

		Widget scroll_1 = addInterface(id);
		scroll_1.width = 134;
		scroll_1.height = 128;
		scroll_1.scrollMax = 305;
		scroll_1.totalChildren(2);
		w.child(child++, id, 25, 51);
		id++;

		addContainer(id, 1, 3, 8, "Withdraw-1", "Withdraw-5", "Withdraw-10", "Withdraw-All", "Withdraw-X");

		scroll_1.child(0, id, 0, 18);
		id++;

		addText(id, "You: (@whi@2,147,483,648</col>)", wid, 1, 0xff9040, false);
		scroll_1.child(1, id, 3, 2);
		id++;

		Widget scroll_2 = addInterface(id);
		scroll_2.width = 134;
		scroll_2.height = 128;
		scroll_2.scrollMax = 305;
		scroll_2.totalChildren(2);
		w.child(child++, id, 342, 51);
		id++;

		addContainer(id, 1, 3, 8);

		scroll_2.child(0, id, 0, 18);
		id++;

		addText(id, "Other: (@whi@2,147,483,648</col>)", wid, 1, 0xff9040, false);
		scroll_2.child(1, id, -1, 2);
		id++;

		/**
		 * Common text
		 */
		addText(id, "Waiting for other player...", wid, 1, 0xffffff, true);
		w.child(child++, id, 257, 162);
		id++;

		addText(id, "Select a game mode:", wid, 2, 0xFF9900);
		w.child(child++, id++, 46, 225 - 37);

		addText(id, "Rules & Tutorial", wid, 2, 0xFF9900);
		w.child(child++, id++, 48 + 250, 225 - 37);

		String[] text = new String[] {"1. Both players deposit desired items to gamble.", "2. When ready, both players accept the stake.", "3. Whoever rolls the highest wins the bet.", "4. 10% Tax is applicable on currency items.", "@yel@All bets are final - No refunds!"};

		for (int i = 0; i < text.length; i++) {
			addText(id, text[i], wid, 1, 0xff9933, true);
			w.child(child++, id++, 102 + 250, 220 + (i * 18));
		}

		addHorizontalDivider(id, 473, AutomaticSprite.DIVIDER_HORIZONTAL_BROWN);
		w.child(child++, id, 20, 194 + 12);
		id++;

		String[] gameModes = new String[] { "55 x 2 (Host: You)", "55 x 2 (Host: Other)", "Dice duel x3 (Both host)", "Flower poker", "Blackjack" };

		for (int i = 0; i < gameModes.length; i++) {
			addHoverTextResetSettingButton(id, 24 + wid[0].getTextWidth(gameModes[i]), 17, 134, 133, gameModes[i], wid, 0, 0xff981f, 0xffffff, false, i, 1123);
			w.child(child++, id++, 27, 215 + (i * 20));
		}

		addVerticalDivider(id, 22, AutomaticSprite.DIVIDER_VERTICAL_BROWN);
		w.child(child++, id, 254 - 46, 163 + 22);
		id++;

		addVerticalDivider(id, 104, AutomaticSprite.DIVIDER_VERTICAL_BROWN);
		w.child(child++, id, 254 - 46, 163 + 22 + 26);
		id++;
		addText(id, ""+id, wid, 2, 0xff0000);
		w.child(child++, id++, 30, 160);
		addText(id, ""+id, wid, 2, 0xff0000);
		w.child(child++, id++, 342, 160);

		final int itemZoom = 70;
		final int xOffset = -5;
		final int heightOffset = 8;

		addItemModel(id, -1, 0, 0, itemZoom);
		w.child(child++, id++, 106+xOffset, 160+heightOffset);
		addItemModel(id, -1, 0, 0, itemZoom);
		w.child(child++, id++, 126+xOffset, 160+heightOffset);
		addItemModel(id, -1, 0, 0, itemZoom);
		w.child(child++, id++, 146+xOffset, 158+heightOffset);

		addItemModel(id, -1, 0, 0, itemZoom);
		w.child(child++, id++, 422+xOffset, 160+heightOffset);
		addItemModel(id, -1, 0, 0, itemZoom);
		w.child(child++, id++, 442+xOffset, 160+heightOffset);
		addItemModel(id, -1, 0, 0, itemZoom);
		w.child(child++, id++, 462+xOffset, 158+heightOffset);
//		// my tax
//		addContainer(id, 1, 3, 1, "Check Tax");
//		w.child(child++, id++, 130, 160);
//
//		// other tax
//		addContainer(id, 1, 3, 1, "Check Tax");
//		w.child(child++, id++, 442, 160);
	}

	public static void flowerGame(GameFont[] wid) {
		int id = 61000;

		Widget w = addInterface(id++);

		setChildren(6, w);

		int child = 0;

		int baseX = 117; int baseY = 63;

		addClosableWindow(id, 485 / 2, 304 / 2 + 36, true, "Gambling with: Blake2");
		w.child(child++, id, baseX + 14, baseY + 16);
		id += 8;

		addText(id, "Blake:", wid, 0, 0xff981f);
		w.child(child++, id++, baseX + 30, baseY + 60);

		addContainer(id, 60_000, 1, 5, 1, 12, 4,  new String[] {});
		w.child(child++, id++, baseX + 32, baseY + 60 + 14);

		addText(id, "Blake2:", wid, 0, 0xff981f);
		w.child(child++, id++, baseX + 30, baseY + 115);

		addContainer(id, 60_000, 1, 5, 1, 12, 4,  new String[] {});
		for (int i = 0; i < interfaceCache[id].inventoryItemId.length; i++) {
			interfaceCache[id].inventoryItemId[i] = 2465;
			interfaceCache[id].inventoryAmounts[i] = 1;
		}
		w.child(child++, id++, baseX + 32, baseY + 115 + 14);

		addText(id, "Result: -", wid, 0, 0x00ff00, true);
		w.child(child++, id++, baseX + 130, baseY + 122 + 52);

	}

	public static void lootingBag(GameFont[] tda) {
		Widget tab = addInterface(26700);
		addSprite(26701, 665);
		addHoverButton(26702, 666, 16, 16, "Close", 0, 26703, 1);
		addHoveredButton(26703, 667, 16, 16, 26704);

		addText(26705, "Looting bag", tda, 2, 0xFF9900, true, true);

		addToItemGroup(26706, 4, 7, 13, 0, false, "", "", "");

		addText(26707, "Value: 0 coins", tda, 0, 0xFF9900, true, true);

		tab.totalChildren(6);
		tab.child(0, 26701, 9, 21);
		tab.child(1, 26702, 168, 4);
		tab.child(2, 26703, 168, 4);
		tab.child(3, 26705, 95, 4);
		tab.child(4, 26706, 12, 23);
		tab.child(5, 26707, 95, 250);
		rules(tda);
	}

	public static void minigames(GameFont[] tda) {
		int id = 29343;
		int frame = 0;

		Widget w = addInterface(id);
		id++;

		w.totalChildren(4);

		addSprite(id, 452);
		w.child(frame++, id, 195, 8);
		id++;

		addTransparentSprite(id, 682, 32);
		w.child(frame++, id, 5, 20);
		id++;

		addText(id, id + "@or1@Next Game:\\n@whi@Weapon Minigame", tda, 1, 0, true, true);
		w.child(frame++, id, 255, 10);
		id++;

		addText(id,
				id + "@or1@Player(s): 50\\n@or1@Time left: 20 minutes\\n\\nPotential winning:\\n@red@400,000 GP",
				tda, 1, 0xfffff, true, true);
		w.child(frame++, id, 75, 34);
		id += 30;

		frame = 0;

		//System.out.println("weapon game: " + id);
		w = addInterface(id);
		id++;

		w.totalChildren(3);

		addTransparentSprite(id, 683, 98);
		w.child(frame++, id, 2, 60);
		id++;

		addText(id, "@or1@Weapon game" + id, tda, 2, 0xfffff, true, true);
		w.child(frame++, id, 73, 67);
		id++;

		addText(id, "" + id, tda, 0, 0xfffff, true, true);
		w.child(frame++, id, 73, 90);
		id++;
	}
	public static void commands(GameFont[] tda) {

		int id = 23035;
		int frame = 0;

		Widget w = addInterface(id);
		id++;

		w.totalChildren(3);

		addSprite(id, 673);
		w.child(frame++, id, 13, 25);
		id++;

		addText(id, "@or1@Grinderscape Commands", tda, 2, 0, true, true);
		w.child(frame++, id, 255, 29);
		id++;

		w.child(frame++, id, 0, 70);

		frame = 0;
		Widget scroll = addInterface(id);
		id++;
		scroll.totalChildren(100);
		scroll.height = 188;
		scroll.width = 478;
		scroll.scrollMax = 2000;

		int y = 10;

		for (int i = 0; i < 100; i++) {
			addText(id, "@or2@" + id, tda, 1, 0, true, true);
			scroll.child(frame++, id, 255, y);
			id++;
			y += 18;
		}
	}
	public static void rules(GameFont[] tda) {
		minigames(tda);
		int id = 23344;
		int frame = 0;

		Widget w = addInterface(id);
		id++;

		String[][] rules = { { "Flaming and/or Harassment:", "The staff do not tolerate any form of harassment, ",
				"flaming, or denigration of another member, whoever",
				" they may be. This also includes threatening and exploiting.", },

				{ "Links:", " Any suspicious links (often referred as ip grabbing) ",
						"which pose a threat to another server member will ", "be dealt with accordingly.", },

				{ "Evading Punishment:", "Naturally, if a member who is currently facing a consequence of",
						"breaking a rule has been discovered evading their specific punishment,",
						"staff will be forced to advance the severity of the member's punishment.",
						"However, we advise you not to evade your punishment as this just",
						"extends the duration of the punishment for you.", },

				{ "Racism:", "Any form of racism and/or Nazism spotted in the chat will be",
						"immediately and harshly dealt with as staff have zero tolerance",
						"for racism. This includes general banter and memes consisting of racism.", },

				{ "Spreading / Leaking Personal Images and Information: ",
						"Members who are caught leaking personal images and/or information",
						"of other members in the chat (without authorised permission) will",
						"suffer the necessary consequences.", },

				{ "Advertising: ", "Staff will not tolerate any advertising. Mentioning the names",
						"of other RSPS's is not allowed. Warnings and further sanctions",
						"will be given if you are caught/found advertising.", },

				{ "Bug Exploiting and/or PKing", "In case of exploiting a bug and using it to harm the server",
							"is considered against the rules. Wilderness ragging is disallowed.",
							"Boosting in the wilderness is also considered illegal.",}

		};

		w.totalChildren(5);

		addSprite(id, 673);
		w.child(frame++, id, 13, 25);
		id++;

		addText(id, "@or1@Grinderscape Rules", tda, 2, 0, true, true);
		w.child(frame++, id, 255, 29);
		id++;

		addText(id, "@or1@Please take your time to read the rules below and ensure you understand them.", tda, 0, 0,
				true, true);
		w.child(frame++, id, 256, 51);
		id++;

		addHoverClickText(id, "Accept", "Accept Grinderscape Rules", tda, 2, 0xEE9021, true, true, 70);
		w.child(frame++, id, 222, 273);
		id++;

		w.child(frame++, id, 0, 70);

		frame = 0;
		Widget scroll = addInterface(id);
		id++;
		scroll.totalChildren(rules.length * 2);
		scroll.height = 188;
		scroll.width = 478;
		scroll.scrollMax = 500;

		int y = 10;

		int[] rulesIncrease = { 50, // 1
				50, // 2
				70, // 3
				50, // 4
				50, // 5
				50, // 6
				50, // 7
		};

		for (int i = 0; i < rules.length; i++) {

			addHoverClickText(id, (i + 1) + ". " + rules[i][0], "Rule Understood", tda, 2, 0xEE9021, true, true, 90);
			scroll.child(frame++, id, 220, y);
			id++;

			y += 18;

			String text = "";

			for (int l = 1; l < rules[i].length; l++) {
				text += "@or2@" + rules[i][l] + "\\n";
			}
			addText(id, "@or2@" + text, tda, 1, 0, true, true);
			scroll.child(frame++, id, 255, y);
			id++;

			y += rulesIncrease[i];
		}
	}

	private static void tradeFirst(GameFont[] tda) {
		Widget rsi = addInterface(3323);

		int id = 48050;

		addClosableWindow(id + 1, 485, 304, true, "Trading With: Blake");

		addText(id + 9, "Your Offer", tda, 1, 0xFF981F);
		addText(id + 10, "Opponent's Offer", tda, 1, 0xFF981F);

		addDarkBox(id + 11, 78, 43, false, BoxBorder.MODERN);
		addDarkBox(id + 13, 78, 43, false, BoxBorder.MODERN);
		addVerticalSeparator(id + 15, 260, true);

		addDarkBox(id + 17, 99, 56, true, BoxBorder.MODERN);
		addText(id + 19, "Blake", tda, 0, 0xFF981F, true);
		addText(id + 20, "has 27 free", tda, 0, 0xFF981F, true);
		addText(id + 21, "inventory slots.", tda, 0, 0xFF981F);

		interfaceCache[3413].width = 250;
		interfaceCache[3414].width = 250;
		interfaceCache[3415].spritePaddingX = 14;
		interfaceCache[3416].spritePaddingX = 14;

		rsi.totalChildren(17);

		int idx = 0;

		setBounds(id + 1, 14, 16, idx++, rsi);
		setBounds(id + 9, 103, 54, idx++, rsi);
		setBounds(id + 10, 327, 54, idx++, rsi);

		setBounds(id + 15, 253, 52, idx++, rsi);

		setBounds(id + 17, 208, 87 - 18, idx++, rsi);
		setBounds(id + 19, 257, 102 - 24, idx++, rsi);
		setBounds(id + 20, 257, 102 - 12, idx++, rsi);
		setBounds(id + 21, 218, 102, idx++, rsi);

		setBounds(id + 11, 218, 167, idx++, rsi);
		setBounds(3420, 224, 173, idx++, rsi);
		setBounds(3421, 239, 181, idx++, rsi);

		setBounds(id + 13, 218, 240, idx++, rsi);
		setBounds(3422, 224, 246, idx++, rsi);
		setBounds(3423, 238, 254, idx++, rsi);

		setBounds(3431, 157, 290, idx++, rsi);

		setBounds(3413, 15, 55, idx++, rsi);
		setBounds(3414, 261 - 8, 56, idx++, rsi);
	}

	private static void catapultDefence(GameFont[] tda) {
		int id = 58500;

		Widget root = addInterface(id++);

		int child = 0;
		root.totalChildren(12);

		// Add the background
		//addSprite(id, 1226);
		addModel(id,3395,0,0,564,522,2044,0,-1);
		root.child(child++, id++, 230+32/2, 164+32/2);

		// Add the close button
		addHoverButton(id, 569, 26, 23, "Close", 250, id + 1, 3);
		root.child(child++, id++, 380,25);
		addHoveredButton(id, 570, 26, 23, id + 1);
		root.child(child++, id++, 380, 25);
		id++;

		addText(id, "Catapult Defence Game Key", tda,2,0xff6600,true,true);
		root.child(child++, id++, 129+246/2, 30);

		addModel(id, 15376,32,32,350,85,1721,0,4165);
		root.child(child++, id++, 146, 85);

		addModel(id, 15373,32,32,350,57,1664,0,4165);
		root.child(child++, id++, 147, 145);

		addModel(id, 15375,32,32,350,114,108,0,4165);
		root.child(child++, id++, 148, 209);

		addModel(id, 15374,32,32,350,34,1625,0,4165);
		root.child(child++, id++, 143, 284);

		String[] texts = {
				"Spiky ball. Use stab defence.",
				"Anvil. Use blunt defence.",
				"Slashing blades. Use slash defence.",
				"Magic missile. Use magic defence.",
		};
		for (int i = 0; i < texts.length; i++) {
			addText(id, texts[i], tda, 1, 0xffff00, false, true);
			root.child(child++, id++,   202,  38 +16 + i * 67);
		}
	}

	private static void addCs1VarComparison(int componentId, int id, int value) {
		Widget widget = Widget.interfaceCache[componentId];
		widget.valueIndexArray = new int[][] {{5,id,0}};
		widget.valueCompareType = new int[]{1};
		widget.requiredValues = new int[] {value};
	}

	private static void dummies(GameFont[] tda) {

		int id = 58530;
		int child = 0;
		Widget root = addInterface(id++);
		root.totalChildren(29);


		// Add the background
		addModel(id, 11365,32,32,541,513,3,0,-1);
		root.child(child++, id++, 224, 166);

		// Add the close button
		addHoverButton(id, 569, 26, 23, "Close", 250, id + 1, 3);
		root.child(child++, id++, 450,23);
		addHoveredButton(id, 570, 26, 23, id + 1);
		root.child(child++, id++, 450, 23);
		id++;

		String title = "Attack Dummy Key";
		addText(id, title, tda,2,0xff6600,false,true);
		root.child(child++, id++, tda[2].getTextWidth(title) / 2, 29);

		String[] description = new String[]{
				"" ,
				"These are the types" ,
				"of attack to use on" ,
				"each of the dummies" ,
				"that pop up from" ,
				"the floor." ,
				"" ,
				"You will find that you" ,
				"need more than one" ,
				"weapon to be" ,
				"successful."};

		for(int lineIndex  = 0; lineIndex < description.length; lineIndex++){
			addText(id, description[lineIndex], tda, 1, 0xffff00, true, true);
			root.child(child++, id++, 53+138/2, 46 + (tda[1].verticalSpace * lineIndex));
		}

		addModel(id, 15364, 32, 32, 682, 0, 1813, 0, -1);
		root.child(child++, id++, 83, 278);
		addRightAlignedText(id,  "Accurate", tda, 1, 0xFFFFFF, false);
		root.child(child++, id++, 93 + 90, 280);

		addModel(id, 15366, 32, 32, 682, 11, 1875, 0, -1);
		root.child(child++, id++, 231, 89);
		addRightAlignedText(id,  "Slash", tda, 1, 0xFFFFFF, false);
		root.child(child++, id++, 269 + 55, 86);

		addModel(id, 15365, 32, 32, 682, 22, 1887, 0, -1);
		root.child(child++, id++, 228, 189);
		addRightAlignedText(id,  "Aggressive", tda, 1, 0xFFFFFF, false);
		root.child(child++, id++, 248 + 79, 183);

		addModel(id, 15363, 32, 32, 682, 0, 1830, 0, -1);
		root.child(child++, id++, 214, 276);
		addRightAlignedText(id,  "Controlled", tda, 1, 0xFFFFFF, false);
		root.child(child++, id++, 248 + 79, 280);

		addModel(id, 15367, 32, 32, 764, 2012, 1864, 0, -1);
		root.child(child++, id++, 363, 96);
		addRightAlignedText(id,  "Crush", tda, 1, 0xFFFFFF, false);
		root.child(child++, id++, 413 + 51 , 86);

		addModel(id, 15370, 32, 32, 682, 0, 1864, 0, -1);
		root.child(child++, id++, 363, 179);
		addRightAlignedText(id, "Stab", tda, 1, 0xFFFFFF, false);
		root.child(child++, id++, 409 + 55 , 183);

		addModel(id, 15369, 32, 32, 682, 2012, 1836, 0, -1);
		root.child(child++, id++, 356, 271);
		addRightAlignedText(id, "Defensive", tda, 1, 0xFFFFFF, false);
		root.child(child, id, 381 + 84 , 280);
	}

	private static void catapultAction(GameFont[] tda) {
		int id = 58514;

		Widget root = addInterface(id++);

		int child = 0;
		root.totalChildren(14);

		addRectangle(id,165, 232,0x333333,true);
		root.child(child++, id++, 11,22);

		addRectangle(id,155, 53,0x666666,true);
		root.child(child++, id++, 17,27);

		addRectangle(id,155, 53,0x666666,true);
		root.child(child++, id++, 17,83);

		addRectangle(id,155, 53,0x666666,true);
		root.child(child++, id++, 16,139);

		addRectangle(id,155, 53,0x666666,true);
		root.child(child++, id++, 16,196);

		addModel(id, 15376,32,32,426,39,1676,0,4165);
		root.child(child++, id++, 29, 59);

		addModel(id, 15373,32,32,426,11,1619,0,4165);
		root.child(child++, id++, 26, 108);

		addModel(id, 15375,32,32,426,0,1950,0,4165);
		root.child(child++, id++, 29, 166);

		addModel(id, 15374,32,32,400,0,1573,0,4165);
		root.child(child++, id++, 25, 225);

		String[] texts = {" Use stab defence", " Use blunt defence", " Use slash defence", " Use magic defence"};
		for (int i = 0; i < texts.length; i++) {
			int textWidth = tda[1].getTextWidth(texts[i]);
			addHoverText(id, texts[i],"Ok", tda,1,0xff6633,false,true,200,0xffff00);
			addCs1VarComparison(id, 751, i);
			root.child(child++, id++, 170-textWidth, 27+i*56+17);
		}

		String title = "Defence style.";
		addText(id, title, tda, 2, 0xffffff,true,true);
		root.child(child, id, tda[2].getTextWidth(title), 4);
	}

	private static void motherlodeMine(GameFont[] tda) {
		int id = 58560;
		Widget root = addInterface(id++);

		int child = 0;
		root.totalChildren(3);

		addSprite(id,1226);
		root.child(child++,id++,2,2);

		addText(id,"0",tda,4,0xFFFFFF,true,true);
		root.child(child++,id++,37,2);

		addModel(id,7701,70,40,900,113,0,0,-1);
		root.child(child,id,2,40);
	}

	private static void tradeSecond(GameFont[] tda) {
		int id = 56000;
		Widget main = addInterface(id);

		setChildren(3, main);

		interfaceCache[3557].defaultText = "";
		interfaceCache[3558].defaultText = "";

		setBounds(48100, 0, 0, 0, main);

		setBounds(id + 10, 24, 74, 1, main);
		setBounds(id + 50, 262, 74, 2, main);

		Widget scroll = addInterface(id + 10);
		setChildren(1, scroll);
		scroll.width = 213;
		scroll.height = 205;
		scroll.scrollMax = 28 * 15;

		addText(id + 40, "Absolutely nothing!", tda, 2, 0xffffff, true);
		setBounds(id + 40, 103, 10, 0, scroll);

		scroll = addInterface(id + 50);
		setChildren(1, scroll);
		scroll.width = 213;
		scroll.height = 205;
		scroll.scrollMax = 28 * 15;

		addText(id + 80, "Absolutely nothing!", tda, 2, 0xffffff, true);
		setBounds(id + 80, 114, 10, 0, scroll);

		Widget rsi = addInterface(48100);

		addWindow(48101, 485, 304, true);
		addHorizontalSeparator(48102, 473, true);
		addVerticalSeparator(48104, 256, true);

		addText(48106, "Are you sure you want to make this trade?", tda, 1, 0x01ffff, true);
		addText(48107, "There is <col=ff0000>NO WAY</col> to reverse a trade if you change your mind.", tda, 0, 0xffffff);
		addText(48108, "You are about to give:\\n(Value: @whi@50,000,000 Coins@yel@)", tda, 0, 0xffff00, true);
		addText(48109, "In return you will receive:\\n(Value: @whi@50,000,000 Coins@yel@)", tda, 0, 0xffff00, true);

		addHoverButton(48110, 825, 16, 16, "Close", 250, 48111, 3);
		addHoveredButton(48111, 826, 16, 16, 48112);

		addTradeButton(48113, 73, 25, true);
		addTradeButton(48115, 73, 25, false);

		addText(48117, "Trading with: ", tda, 2, 0x11ffff);
		addText(48118, "Blake", tda, 2, 0x11ffff, true);

		rsi.totalChildren(17);

		int idx = 0;

		setBounds(48101, 14, 16, idx++, rsi);
		setBounds(48102, 20, 52, idx++, rsi);
		setBounds(48104, 254, 58, idx++, rsi);
		setBounds(48106, 254, 22, idx++, rsi);
		setBounds(48107, 107, 38, idx++, rsi);
		setBounds(48108, 80 + 57, 62, idx++, rsi);
		setBounds(48109, 310 + 67, 62, idx++, rsi);
		setBounds(48110, 474, 25, idx++, rsi);
		setBounds(48111, 474, 25, idx++, rsi);
		setBounds(3546, 188, 295, idx++, rsi);
		setBounds(3547, 201, 296, idx++, rsi);
		setBounds(48113, 182, 289, idx++, rsi);
		setBounds(3548, 260, 295, idx++, rsi);
		setBounds(3549, 274, 296, idx++, rsi);
		setBounds(48115, 259, 289, idx++, rsi);
		setBounds(48117, 61, 281, idx++, rsi);
		setBounds(48118, 102, 297, idx++, rsi);
	}

	public static void runePouch(GameFont[] tda) {
		Widget tab = addInterface(26699);
		addSprite(41701, 668);
		addSprite(41705, 669);
		addSprite(41706, 670);
		addText(41702, "Rune pouch", tda, 2, 0xFFA500, true, true);
		addText(41703, "Pouch", tda, 2, 0xFFA500, true, true);
		addText(41704, "Inventory", tda, 2, 0xFFA500, true, true);
		addHoverButton(41707, 671, 21, 21, "Close window", 0, 41708, 3);
		addHoveredButton(41708, 672, 21, 21, 41709);
		Widget add = addInterface(41710);
		addToItemGroup(add, 3, 1, 26, 1, "Withdraw-1", "Withdraw-5", "Withdraw-10", "Withdraw-All", "Withdraw-X");
		add = addInterface(41711);
		addToItemGroup(add, 7, 4, 16, 4, "Store-1", "Store-5", "Store-10", "Store-All", "Store-X");
		tab.totalChildren(10);
		tab.child(0, 41701, 0, 0);
		tab.child(1, 41702, 253, 29);
		tab.child(2, 41703, 253, 62);
		tab.child(3, 41704, 253, 137);
		tab.child(4, 41705, 105, 57);
		tab.child(5, 41706, 342, 57);
		tab.child(6, 41707, 406, 26);
		tab.child(7, 41708, 406, 26);
		tab.child(8, 41710, 186, 86);
		tab.child(9, 41711, 98, 154);
	}

	public static void addToItemGroup(Widget rsi, int w, int h, int x, int y, String...actions) {
		rsi.width = w;
		rsi.height = h;
		rsi.inventoryItemId = new int[w * h];
		rsi.inventoryAmounts = new int[w * h];
		rsi.usableItems = false;
		rsi.spritePaddingX = x;
		rsi.spritePaddingY = y;
		rsi.spritesX = new int[20];
		rsi.spritesY = new int[20];
		rsi.sprites = new Sprite[20];
		rsi.actions = actions;
		rsi.type = 2;
	}

	public static Widget addToItemGroup(int id, int w, int h, int x, int y, boolean actions, String action1,
			String action2, String action3) {
		Widget rsi = addInterface(id);
		rsi.width = w;
		rsi.height = h;
		rsi.inventoryItemId = new int[w * h];
		rsi.inventoryAmounts = new int[w * h];
		rsi.usableItems = false;
		rsi.spritePaddingX = x;
		rsi.spritePaddingY = y;
		rsi.spritesX = new int[20];
		rsi.spritesY = new int[20];
		rsi.sprites = new Sprite[20];
		rsi.actions = new String[5];
		if (actions) {
			rsi.actions[0] = action1;
			rsi.actions[1] = action2;
			rsi.actions[2] = action3;
		}
		rsi.type = TYPE_INVENTORY;
		return rsi;
	}

	public static void editSkillTab(GameFont[] font) {
		Widget widget = interfaceCache[3917];

		extendChildren(widget, 1);

		createTooltip(27656, 62, 30, font, "Total XP: 0");

		widget.child(55, 27656, 127, 220);
	}

	public static void rankChooser(GameFont[] tda) {
		int id = 59000;
		int child = 0;

		Widget w = addInterface(id++);
		w.totalChildren(12);

		addSprite(id, 618);
		w.child(child++, id++, 7, 2);

		addText(id, "@or1@Rank Chooser", tda, 2, 0, true, true);
		w.child(child++, id++, 255, 13);

		addText(id, "Default rank:", tda, 2, 0xEE9021);
		w.child(child++, id++, 288, 115);

		addText(id, "None", tda, 1, 0xEE9021, true);
		w.child(child++, id++, 331, 135);

		addText(id, "Selected rank:", tda, 2, 0xEE9021);
		w.child(child++, id++, 280 + 4, 175);

		addText(id, "Server Supporter", tda, 1, 0xEE9021, true);
		w.child(child++, id++, 331, 195);

		addText(id, "The ranks highlighted in @gre@green @or1@are available for you to choose.", tda, 0, 0xEE9021);
		w.child(child++, id++, 179, 57);

		addHoverButton(id, 621, 124, 32, "Confirm", -1, id + 1, 1);
		addHoveredButton(id + 1, 622, 124, 32, id + 2);
		w.child(child++, id, 269, 273);
		w.child(child++, id + 1, 269, 273);
		id += 3;

		addText(id, "Confirm", tda, 2, 0xEE9021);
		w.child(child++, id++, 304, 281);

		addTextClose(id, tda);
		w.child(child++, id++, 420, 14);

		Widget scroll = addInterface(id);
		w.child(child++, id++, 15, 56);

		int len = Rights.values().length;
		len -= 10; // skip None rights
		//len += 1; // account for Classic game mode
		scroll.totalChildren(len);
		scroll.height = 247;
		scroll.width = 142;
		scroll.scrollMax = len * 20;
		int scrollChild = 0;

        /**
         * Hard code in classic game mode first
         */
/*		String text = "<img=77> Classic";
		addHoverClickText(id, text, "Click to select", tda, 0, 0xff0000, false, true, text.length() * 4);
		scroll.child(scrollChild, id++, 15, 5 + scrollChild++ * 20);*/

        /**
         * Rights ranks
         */
		for (Rights rights : Rights.values()) {
		    if (rights == Rights.NONE) continue;
			if (rights == Rights.DICER) continue;
			if (rights == Rights.BRONZE_MEMBER) continue;
			if (rights == Rights.RUBY_MEMBER) continue;
			if (rights == Rights.TOPAZ_MEMBER) continue;
			if (rights == Rights.AMETHYST_MEMBER) continue;
			if (rights == Rights.LEGENDARY_MEMBER) continue;
			if (rights == Rights.PLATINUM_MEMBER) continue;
			if (rights == Rights.TITANIUM_MEMBER) continue;
			if (rights == Rights.DIAMOND_MEMBER) continue;

			String text = "<img=" + rights.getSpriteId() + "> " + rights.formattedName();
			addHoverClickText(id, text, "Click to select", tda, 0, 0xff0000, false, true, text.length() * 4);
			scroll.child(scrollChild, id++, 15, 5 + scrollChild++ * 20);
		}
	}

	public static void npcDropTable(GameFont[] tda) {
		rankChooser(tda);

		String[] DES = { "Retreats: @or1@true", "Aggressive: @or1@true", "Poisonous: @or1@false", "Max hit: @or1@10",
				"Hitpoints: @or1@240", "Slayer requirement: @or1@1", "Respawn time: @or1@1 minute", };

		int id = 43000;
		int frame = 0;

		Widget w = addInterface(id);
		id++;

		w.totalChildren(15 + DES.length);

		addSprite(id, 647);
		w.child(frame++, id, 13, 23);
		id++;

		addText(id, "@or1@Grinderscape NPC Information", tda, 2, 0, true, true);
		w.child(frame++, id, 255, 28);
		id++;
		addText(id, "@or1@View NPC statistics and drop table below", tda, 0, 0, true, true);
		w.child(frame++, id, 256, 50);
		id++;

		addText(id, "@or1@King Black Dragon: " + id, tda, 2, 0, true, true);
		w.child(frame++, id, 97, 72);
		id++;

		int y = 97;
		for (String s : DES) {
			addText(id, "@or2@" + id, tda, 0, 0, false, true);
			w.child(frame++, id, 29, y);
			id++;
			y += 12;
		}

		addText(id, "Always drops:", tda, 0, 0xffffff, false, true);
		w.child(frame++, id, 60, 192);
		id++;

		addText(id, "Other drops:", tda, 0, 0xffffff, true, true);
		w.child(frame++, id, 330, 74);
		id++;

		addText(id, "@or1@ " + id, tda, 0, 0xffffff, true, true);
		w.child(frame++, id, 330, 286);
		id++;

		/*addPet(id);
		w.child(frame++, id, 270, 195);*/
		id++;

		int x = 0;
		y = 0;

		for (int i = 0; i < 6; i++) {
			addItemOnInterface(id, 30000, new String[] {});
			setBounds(id, x + 40, y + 220, frame, w);
			frame++;
			id++;

			x += 37;

			if (x == 3 * 37) {
				x = 0;
				y += 40;
			}
		}

		w.child(frame++, id, 200, 88);

		int framed = frame;
		frame = 0;
		Widget scroll = addInterface(id);
		id++;
		scroll.totalChildren(56);
		scroll.height = 194;
		scroll.width = 278;
		scroll.scrollMax = 330;

		x = 0;
		y = 0;

		for (int i = 0; i < 56; i++) {
			addItemOnInterface(id, 30000, new String[] { "Check Drop Rate" });
			setBounds(id, x, y + 5, frame, scroll);
			frame++;
			id++;

			x += 37;

			if (x == 7 * 37) {
				x = 0;
				y += 38;
			}
		}

		addTextClose(40000, tda);
		setBounds(40000, 420, 29, framed, w);
		frame++;
		id++;
	}

	public static void notes(GameFont[] tda) {
		npcDropTable(tda);
		int id = 32000;
		int frame = 0;

		Widget w = addTabInterface(id);
		id++;

		w.totalChildren(7);

		addSprite(id, 641);
		w.child(frame++, id, 1, 30);
		id++;

		addText(id, "@or1@Notes", tda, 1, 0, true, true);
		w.child(frame++, id, 92, 9);
		id++;

		addButton(id, id, 18, 18, 642, 642, id + 1, "Add note");
		setBounds(id, 9, 6, frame, w);
		frame++;

		addHoveredButton(id + 1, 643, 18, 18, id + 2);
		setBounds(id + 1, 9, 6, frame, w);
		frame++;
		id += 3;

		addButton(id, id, 18, 18, 644, 644, id + 1, "Delete all notes");
		setBounds(id, 158, 238, frame, w);
		frame++;

		addHoveredButton(id + 1, 645, 18, 18, id + 2);
		setBounds(id + 1, 158, 238, frame, w);
		frame++;
		id += 3;

		w.child(frame++, id, 0, 32);

		frame = 0;
		Widget scroll = addInterface(id);
		id++;
		scroll.totalChildren(100);
		scroll.height = 198;
		scroll.width = 175;
		scroll.scrollMax = 1510;

		int y = 10;

		for (int i = 0; i < 100; i++) {
			addHoverClickText(id, "Note " + id, "Clear note", tda, 0, 0xEE9021, false, true, 150);
			setBounds(id, 7, y, frame, scroll);
			frame++;
			id++;
			y += 15;
		}
	}
	private static void debugBox(){
		WidgetBuilder builder = new WidgetBuilder(23139, 130, 180, WidgetAlignment.BOTTOM_RIGHT);
		builder.addChild(new RectangleComponent(0, 0, 130, 180, 100, Color.BLACK, true));
		builder.addChild(new TextComponent(FontType.NORMAL, Color.WHITE, false, true, 5, 5));
		builder.addChild(new TextComponent(FontType.NORMAL, Color.WHITE, false, true, 5, 25));
		builder.addChild(new TextComponent(FontType.NORMAL, Color.WHITE, false, true, 5, 45));
		builder.addChild(new TextComponent(FontType.NORMAL, Color.WHITE, false, true, 5, 65));
		builder.addChild(new TextComponent(FontType.NORMAL, Color.WHITE, false, true, 5, 85));
		builder.addChild(new TextComponent(FontType.NORMAL, Color.WHITE, false, true, 5, 105));
		builder.addChild(new TextComponent(FontType.NORMAL, Color.WHITE, false, true, 5, 125));
		builder.addChild(new TextComponent(FontType.NORMAL, Color.WHITE, false, true, 5, 145));
		builder.create();

	}
	public static void vote(GameFont[] tda) {
		notes(tda);
		int id = 55000;
		int frame = 0;

		Widget w = addInterface(id);
		id++;

		w.totalChildren(11);

		addSprite(id, 640);
		w.child(frame++, id, 13, 19);
		id++;

		addText(id, "@or1@Grinderscape Rewards", tda, 2, 0, true, true);
		w.child(frame++, id, 255, 23);
		id++;

		addText(id, "@or1@View the reward selection below and claim your prize.", tda, 0, 0, true, true);
		w.child(frame++, id, 256, 45);
		id++;

		addText(id, "The rewards are all chosen randomly", tda, 0, 0xffffff, true, true);
		w.child(frame++, id, 256, 67);
		id++;

		addText(id, "", tda, 0, 0xffffff, false, true);
		w.child(frame++, id, 23, 67);
		id++;

		addHoverClickText(id, "", "Vote for Grinderscape", tda, 0, 0xEE9021, false, true, 150);
		setBounds(id, 400, 67, frame, w);
		frame++;
		id++;

		addHoverClickText(id, "Claim reward", "Claim reward", tda, 0, 0xEE9021, true, true, 150);
		setBounds(id, 180, 288, frame, w);
		frame++;
		id++;

		//System.out.println("win: " + id);
		addItemOnInterface(id, 55000, new String[] {});
		setBounds(id, 239, 203, frame, w);
		frame++;
		id++;

		addText(id, "" + id, tda, 1, 0xfffff, true, true);
		w.child(frame++, id, 255, 259);
		id++;

		w.child(frame++, id, 30, 83);

		int framed = frame;

		frame = 0;
		//System.out.println("scroll: " + id);
		Widget scroll = addInterface(id);
		id++;
		scroll.totalChildren(99);
		scroll.height = 87;
		scroll.width = 448;
		scroll.scrollMax = 430;

		int x = 0;
		int y = 0;

		//System.out.println("slots: " + id);
		for (int i = 0; i < 99; i++) {
			addItemOnInterface(id, 55000, new String[] {});
			setBounds(id, x + 30, y + 20, frame, scroll);
			frame++;
			id++;

			x += 40;

			if (x == 10 * 40) {
				x = 0;
				y += 50;
			}
		}

		addTextClose(40000, tda);
		setBounds(40000, 420, 25, framed, w);
		frame++;
		id++;
	}

	public static void oldMysteryBox(GameFont[] tda) {
		int id = 78000;
		Widget w = addInterface(id++);

		w.totalChildren(15);
		int child = 0;

		int width = 428;
		int height = 318;
		int x = (512 - width) / 2;
		int y = (334 - height) / 2;

		addBackground(id, width, height, true, AutomaticSprite.BACKGROUND_BROWN);
		w.child(child++, id++, x, y);

		//addCloseButton(id, false);
		w.child(child++, 34206, x + width - 25, y + 9);

		addText(id, "Mystery Box", tda, 2, 0xff981f, true);
		w.child(child++, id++, 256, y + 10);

		addText(id, "View the reward selection below and claim your prize.", tda, 0, 0xffb000, true);
		w.child(child++, id++, 256, y + 44);

		w.child(child++, 34100, 71, y + 65);
		w.child(child++, 34201, 71, y + 65);
		w.child(child++, 34202, 70, y + 64);
		addSprite(id, 869);
		w.child(child++, id++, 251, y + 64);

		addText(id, "Possible rewards:", tda, 2, 0xffb000, true);
		w.child(child++, id++, 256, y + 123);

		int scrollBarHeight = 115;
		addTransparentRectangle(id, 368, scrollBarHeight, 0, true, 25);
		w.child(child++, id++, 72, y + 145);
		addRectangle(id, 370, scrollBarHeight + 2, 0x726451, false);
		w.child(child++, id++, 71, y + 144);
		addRectangle(id, 372, scrollBarHeight + 4, 0x463d32, false);
		w.child(child++, id++, 70, y + 143);

		Widget scroll = addInterface(id++);
		scroll.totalChildren(1);
		scroll.width = 368 - 16;
		scroll.height = scroll.scrollMax = scrollBarHeight;
		addContainer(id, 8, 13, 12, 6);
		scroll.child(0, id++, 6, 3);
		w.child(child++, scroll.id, 72, y + 145);

		int bWidth = 130;
		int bHeight = 30;
		addHoverButton(34003, bWidth, bHeight, AutomaticSprite.BUTTON_BROWN, AutomaticSprite.BUTTON_BROWN_HOVER, "Spin");
		w.child(child++, 34003, x + 149, y + 271);
		addText(id, "Spin", tda, 2, 0xffb000, true);
		w.child(child++, id++, x + 149 + (bWidth / 2), y + 271 + ((bHeight - (tda[2].verticalSpace + 3)) / 2));


	}

	public static void addBasics(String title, GameFont[] tda, Widget w, int child, int id, int x, int y, int width, int height, AutomaticSprite automaticSprite) {
		int closeX = 0;
		int closeY = 0;
		int textColor = 0;
		int textY = 0;

		switch (automaticSprite) {
			case BACKGROUND_BROWN:
				closeX = x + width - 25;
				closeY = y + 9;
				textColor = 0xff981f;
				textY = y + 10;
				break;
			case BACKGROUND_BIG_BROWN:
				closeX = x + width - 26;
				closeY = y + 3;
				textColor = 0xffb000;
				textY = y + 4;
				break;
		}

		addBackground(id, width, height, true, automaticSprite);
		w.child(child++, id++, x, y);
		addCloseButton(id, false);
		w.child(child++, id++, closeX, closeY);
		addText(id, title, tda, 2, textColor, true);
		w.child(child++, id++, 256, textY);
	}

	public static void addNewBox(Widget w, int child, int id, int x, int y, int width, int height) {
		addRectangle(id, width, height, 0x2e2b23, false);
		w.child(child++, id++, x, y);
		addRectangle(id, width - 2, height - 2, 0x726451, false);
		w.child(child++, id++, x + 1, y + 1);
		addTransparentRectangle(id, width - 4, height - 4, 0, true, 25);
		w.child(child++, id++, x + 2, y + 2);
	}

	public static void addOldBox(Widget w, int child, int id, int x, int y, int width, int height) {
		addRectangle(id, width, height, 0x0d0d0b, false);
		w.child(child++, id++, x, y);
		addRectangle(id, width - 2, height - 2, 0x464644, false);
		w.child(child++, id++, x + 1, y + 1);
		addTransparentRectangle(id, width - 4, height - 4, 0, true, 25);
		w.child(child++, id++, x + 2, y + 2);
	}

	public static void newMysteryBox(GameFont[] tda) {
		Widget list = addInterface(34000);

		Widget items = addInterface(34100);

		int width = 428;
		int height = 269;
		int x = (512 - width) / 2;
		int y = (334 - height) / 2;

		addBackground(34203, width, height, AutomaticSprite.BACKGROUND_BIG_BROWN);

		addText(34207, "Mystery Box", tda, 2, 0xffb000, true);

		addCloseButton(34206, false);
		addPixels(34301, 0x342b1c, width - 14, height - 29, 75, true);
		addBackground(34302, 180, 115, AutomaticSprite.BACKGROUND_BROWN);
		addText(34304, "You won:", tda, 2, 0xffb000, true, true);
		addText(34305, "name_here", tda, 1, 0xffb000, true, true);

		addAdvancedSprite(34319, 1010);
		addRectangle(34320, 44, 44, 0x463d32, false);

		addItem(34303);

		items.width = 370;

		addTransparentSprite(34002, 869, 0);

		/**
		 * Keep order same as server side enum
		 */
		String[] boxNames = { "Barrows Mystery Box", "Legendary Mystery Box", "PVP Mystery Box",
				"Gilded Mystery Box", "Sacred Mystery Box", "$50 Mystery Box", "$100 Mystery Box", "Super Mystery Box",
				"Extreme Mystery Box", "Staff Present", "Daily Luck Present",
				"Voting Mystery Box", "Mystery Box" };

		int openButtonWidth = 150;
		int openButtonHeight = 50;
		int buttonWidth = 102;
		int buttonHeight = 30;
		for (int i = 0; i < boxNames.length; i++) {
			addHoverButton(34321 + i, openButtonWidth, openButtonHeight, AutomaticSprite.BUTTON_BROWN, AutomaticSprite.BUTTON_BROWN_HOVER, "Open " + boxNames[i]);
			interfaceCache[34321 + i].invisible = true;
			addHoverButton(34351 + i, buttonWidth, buttonHeight, AutomaticSprite.BUTTON_BROWN, AutomaticSprite.BUTTON_BROWN_HOVER, "View " + boxNames[i] + " Rewards");
			interfaceCache[34351 + i].invisible = true;
		}

		addText(34004, "OPEN BOX", tda, 2, 0xffb000, false);
		addItem(34005);
		interfaceCache[34005].inventoryItemId[0] = 6199 + 1;
		interfaceCache[34005].inventoryAmounts[0] = 1;

		addText(34308, "View Rewards", tda, 0, 0xffb000, false);
		addAdvancedSprite(34309, 1008);

		addHoverButton(34310, buttonWidth, buttonHeight, AutomaticSprite.BUTTON_BROWN, AutomaticSprite.BUTTON_BROWN_HOVER, "Buy Boxes");
		addText(34311, "Buy Boxes", tda, 0, 0xffb000, false);
		addAdvancedSprite(34312, 1009);

		addAdvancedSprite(34101, 870);

		addTransparentRectangle(34306, 374, 46, 0xFFD700, false, 0);
		addRectangle(34202, 372, 44, 0x463d32, false);
		addRectangle(34201, 370, 42, 0x726451, false);
		addContainer(34200, 300, 1, 9, 0);

		addTransparentRectangle(34313, 340, 70, 0, true, 25);
		addRectangle(34314, 342, 72, 0x726451, false);
		addRectangle(34315, 344, 74, 0x463d32, false);

		addText(34316, "Feeling Lucky?", tda, 2, 0xEE9021, true);
		addText(34317, "Open the mystery box by clicking on the spin button below.", tda, 1, 0xEE9021, true);
		addText(34318, "You can click on view rewards button to check the prizes.", tda, 1, 0xEE9021, true);

		int listChild = 0;
		int itemChild = 0;
		items.totalChildren(2);
		items.child(itemChild++, 34101, 0, 0);
		items.child(itemChild++, 34200, 5, 5);
		list.totalChildren(21 + (boxNames.length * 2));
		list.child(listChild++, 34203, x, y);
		list.child(listChild++, 34207, 256, y + 4);
		list.child(listChild++, 34313, x + 44, y + 44);
		list.child(listChild++, 34314, x + 43, y + 43);
		list.child(listChild++, 34315, x + 42, y + 42);
		list.child(listChild++, 34316, 256, y + 52);
		list.child(listChild++, 34317, 256, y + 72);
		list.child(listChild++, 34318, 256, y + 89);
		list.child(listChild++, items.id, 71, y + (height - 27 - 40 - 20 - 42) - 2);
		list.child(listChild++, 34201, 71, y + (height - 27 - 40 - 20 - 42) - 2);
		list.child(listChild++, 34202, 70, y + (height - 27 - 40 - 20 - 42) - 3);
		list.child(listChild++, 34306, 69, y + (height - 27 - 40 - 20 - 42) - 4);
		list.child(listChild++, 34002, 251, y + (height - 27 - 40 - 20 - 42) - 3);
		list.child(listChild++, 34206, x + width - 26, y + 3);
		for (int i = 0; i < boxNames.length; i++) {
			list.child(listChild++, 34321 + i, x + 139, y + (height - 27 - openButtonHeight));
			list.child(listChild++, 34351 + i, x + 27, y + (height - 27 - buttonHeight));
		}
		list.child(listChild++, 34004, x + 139 + 58, y + (height - 27 - openButtonHeight) + ((openButtonHeight - (tda[2].verticalSpace + 3)) / 2));
		list.child(listChild++, 34005, x + 139 + 10, y + (height - 27 - openButtonHeight) + ((openButtonHeight - 32) / 2));
		list.child(listChild++, 34308, x + 27 + 24, y + (height - 27 - buttonHeight) + ((buttonHeight - (tda[0].verticalSpace + 2)) / 2));
		list.child(listChild++, 34309, x + 27 + 9, y + (height - 27 - buttonHeight) + ((buttonHeight - 14) / 2));
		list.child(listChild++, 34310, x + 299, y + (height - 27 - buttonHeight));
		list.child(listChild++, 34311, x + 299 + 32, y + (height - 27 - buttonHeight) + ((buttonHeight - (tda[0].verticalSpace + 2)) / 2));
		list.child(listChild++, 34312, x + 299 + 17, y + (height - 27 - buttonHeight) + ((buttonHeight - 14) / 2));
	}

	public static void mysteryBoxReward(GameFont[] tda) {
		int id = 77475;
		Widget w = addInterface(id++);

		w.totalChildren(11);
		int child = 0;

		int width = 220;
		int height = 156;
		int x = (512 - width) / 2;
		int y = (334 - height) / 2;

		addBackground(id, width, height, AutomaticSprite.BACKGROUND_BIG_BROWN);
		w.child(child++, id++, x, y);

		addCloseButton(id, false);
		w.child(child++, id++, x + width - 26, y + 3);

		addText(id, "Reward", tda, 2, 0xffb000, true);
		w.child(child++, id++, 256, y + 4);

		addTransparentRectangle(id, width - 14 - 40, 55, 0, true, 25);
		w.child(child++, id++, x + 7 + 20, y + 84);
		addRectangle(id, width - 14 - 38, 57, 0x726451, false);
		w.child(child++, id++, x + 7 + 19, y + 83);
		addRectangle(id, width - 14 - 36, 59, 0x463d32, false);
		w.child(child++, id++, x + 7 + 18, y + 82);

		addText(id, "You won:", tda, 2, 0xEE9021, true);
		w.child(child++, id++, 256, y + 92);

		addText(id, "" + id, tda, 1, 0xEE9021, true);
		w.child(child++, id++, 256, y + 112);

		addAdvancedSprite(id, 1010);
		w.child(child++, id++, x + (width / 2) - 21, y + 32);

		addRectangle(id, 44, 44, 0x463d32, false);
		w.child(child++, id++, x + (width / 2) - 22, y + 32 - 1);

		addItem(id);
		w.child(child++, id++, x + (width / 2) - 16, y + 32 + 4);
	}

	public static void mysteryBoxRewards(GameFont[] tda) {
		int id = 77500;
		Widget w = addInterface(id++);

		w.totalChildren(6);
		int child = 0;

		int width = 378;
		int height = 308;
		int x = (512 - width) / 2;
		int y = (334 - height) / 2;

		addBackground(id, width, height, AutomaticSprite.BACKGROUND_BIG_BROWN);
		w.child(child++, id++, x, y);

		addCloseButton(id, false);
		w.child(child++, id++, x + width - 26, y + 3);

		addText(id, "Mystery Box Rewards", tda, 2, 0xffb000, true);
		w.child(child++, id++, 256, y + 4);

		Widget scroll = addInterface(id++);
		w.child(child++, scroll.id, x + 8, y + 23);
		scroll.totalChildren(201);
		scroll.height = scroll.scrollMax = height - 31;
		scroll.width = 363 - 16;
		int scrollChild = 0;

		addContainer(id, 5, 20, 37, 37);
		scroll.child(scrollChild++, id++, 18, 9);

		int textX = 34;
		int textY = 48;
		for (int i = 0; i < 100; i++) {
			addAdvancedSprite(id, 1011);
			scroll.child(scrollChild++, id++, textX - 34, textY - 48);

			addText(id, "" + id, tda, 0, 0xffb000, true);
			scroll.child(scrollChild++, id++, textX, textY);

			if ((i + 1) % 5 == 0) {
				textX = 34;
				textY += 69;
			} else {
				textX += 69;
			}
		}

		addRectangle(id, scroll.width - 1, scroll.height, 0x726451, false);
		w.child(child++, id++, x + 8, y + 23);

		addRectangle(id, scroll.width - 1 + 2, scroll.height + 2, 0x463d32, false);
		w.child(child++, id++, x + 7, y + 22);

	}

	public static void playerTitles(GameFont[] tda) {
		int id = 57000;
		int frame = 0;

		Widget w = addInterface(id);
		id++;

		w.totalChildren(19);

		addSprite(id, 633);
		w.child(frame++, id, 13, 19);// 34
		id++;

		addText(id, "@or1@Grinderscape Player Title Selection", tda, 2, 0, true, true);
		w.child(frame++, id, 255, 23);
		id++;

		addText(id, "@or1@View and select player titles below", tda, 0, 0, true, true);
		w.child(frame++, id, 256, 45);
		id++;

		addButton(id, id, 115, 24, 635, 635, id + 1, "Select @red@Wilderness");
		setBounds(id, 21, 64, frame, w);
		frame++;

		addHoveredButton(id + 1, 634, 115, 24, id + 2);
		setBounds(id + 1, 21, 64, frame, w);
		frame++;
		id += 3;

		addButton(id, id, 115, 24, 635, 635, id + 1, "Select @gre@Monster Killing");
		setBounds(id, 136, 64, frame, w);
		frame++;

		addHoveredButton(id + 1, 634, 115, 24, id + 2);
		setBounds(id + 1, 136, 64, frame, w);
		frame++;
		id += 3;

		addButton(id, id, 115, 24, 635, 635, id + 1, "Select @gre@Skilling");
		setBounds(id, 251, 64, frame, w);
		frame++;

		addHoveredButton(id + 1, 634, 115, 24, id + 2);
		setBounds(id + 1, 251, 64, frame, w);
		frame++;
		id += 3;

		addButton(id, id, 126, 24, 635, 635, id + 1, "Select @gre@Other");
		setBounds(id, 367, 64, frame, w);
		frame++;

		addHoveredButton(id + 1, 636, 126, 24, id + 2);
		setBounds(id + 1, 367, 64, frame, w);
		frame++;
		id += 3;

		addText(id, "@red@Wilderness", tda, 1, 0, false, true);
		w.child(frame++, id, 39, 65);
		id++;

		addText(id, "@or1@Monster Killing", tda, 1, 0, false, true);
		w.child(frame++, id, 152, 65);
		id++;

		addText(id, "@or1@Skilling", tda, 1, 0, false, true);
		w.child(frame++, id, 288, 65);
		id++;

		addText(id, "@or1@Other", tda, 1, 0, false, true);
		w.child(frame++, id, 410, 65);
		id++;

		addText(id, "@or1@Total titles unlocked: 33/500", tda, 0, 0, false, true);
		w.child(frame++, id, 345, 288);
		id++;

		addText(id, "Current title: @or1@Test1 @or3@The Developa", tda, 0, 0xffffff, false, true);
		w.child(frame++, id, 25, 288);
		id++;

		w.child(frame++, id, 30, 83);

		int framed = frame;

		frame = 0;
		Widget scroll = addInterface(id);
		id++;
		scroll.totalChildren(50 * 5);
		scroll.height = 195;
		scroll.width = 448;
		scroll.scrollMax = 2050;

		int y = 10;

		for (int i = 0; i < 50; i++) {

			addTransparentSprite(id, 637, 98);
			scroll.child(frame++, id, 5, y);
			id++;

			addText(id, "@or1@Player  Title " + id, tda, 1, 0, false, true);
			scroll.child(frame++, id, 12, y + 3);
			id++;

			addText(id, "Reach a killstreak of " + id, tda, 0, 0xffffff, false, true);
			scroll.child(frame++, id, 12, y + 22);
			id++;

			addText(id, "5" + id + "%", tda, 0, 0xffffff, false, true);
			scroll.child(frame++, id, 212, y + 15);
			id++;

			addHoverClickText(id, "@red@Locked", "Select title", tda, 0, 0xEE9021, true, true, 150);
			setBounds(id, 305, y + 15, frame, scroll);
			frame++;
			id++;

			y += 40;
		}

		addTextClose(40000, tda);
		setBounds(40000, 420, 25, framed, w);
		frame++;
		id++;
	}

	public static void news(GameFont[] tda) {
		playerTitles(tda);
		int id = 49600;
		int frame = 0;

		Widget w = addInterface(id);
		id++;

		w.totalChildren(5);

		addSprite(id, 626);
		w.child(frame++, id, 13, 34);
		id++;

		addText(id, "@or1@Grinderscape News", tda, 2, 0, true, true);
		w.child(frame++, id, 255, 38);
		id++;

		addText(id, "@or1@View all Grinderscape news and updates below", tda, 0, 0, true, true);
		w.child(frame++, id, 256, 60);
		id++;

		w.child(frame++, id, 30, 79);

		frame = 0;
		//System.out.println("				scroll news: " + id);
		Widget scroll = addInterface(id);
		id++;
		scroll.totalChildren(100);
		scroll.height = 214;
		scroll.width = 448;
		scroll.scrollMax = 700;

		int y = 10;

		for (int i = 0; i < 20; i++) {

			addTransparentSprite(id, 627, 40);
			scroll.child(frame++, id, 0, y);
			id++;

			addText(id, "", tda, 2, 0, false, true);
			scroll.child(frame++, id, 7, y + 3);
			id++;

			addText(id, "", tda, 0, 0xffffff, false, true);
			scroll.child(frame++, id, 7, y + 23);
			id++;

			addHoverClickText(id, "View Thread", "View thread", tda, 0, 0xEE9021, false, true, 150);
			setBounds(id, 365, y + 45, frame, scroll);
			frame++;
			id++;

			addText(id, "", tda, 0, 0xffffff, false, true);
			scroll.child(frame++, id, 368, y + 5);
			id++;

			y += 68;
		}
		addTextClose(40000, tda);
		setBounds(40000, 420, 40, 4, w);
		frame++;
		id++;
	}

	public static void killLog(GameFont[] tda) {
		news(tda);

		int id = 48200;

		Widget w = addInterface(id++);
		w.totalChildren(10);
		int child = 0;

		addSprite(id, 624);
		w.child(child++, id, 51, 54);
		id++;

		w.child(child++, 43702, 438, 57); // Close button

		addText(id, "Grinderscape NPC Kill Log" + id, tda, 2, 0xff981f, true);
		w.child(child++, id++, 255, 58);

		addText(id, "@or1@View your Boss and Monster killing statsistics below" + id, tda, 0, 0, true, true);
		w.child(child++, id, 256, 80);
		id++;

		addText(id, "@or1@NPC NAME HERE" + id, tda, 2, 0, true, true);
		w.child(child++, id, 335, 100);
		id++;

		addText(id, "@or2@Total Boss killcount: " + id, tda, 0, 0, false, true);
		w.child(child++, id, 63, 239);
		id++;

		addText(id, "@or2@Total NPC killcount: " + id, tda, 0, 0, false, true);
		w.child(child++, id, 63, 249);
		id++;

		addText(id, "@or3@Amount Killed: " + id, tda, 0, 0, false, true);
		w.child(child++, id, 223, 125);
		id++;

		addText(id, "@or3@Fastest Kill: " + id, tda, 0, 0, false, true);
		w.child(child++, id, 223, 135);
		id++;

//		addPet(id);
//		w.child(child++, id, 335, 195);
//		id++;

		w.child(child++, id, 58, 98);

		child = 0;
		//System.out.println("Killtracker scroll: " + id);
		Widget scroll = addInterface(id);
		id++;
		scroll.totalChildren(900);
		scroll.height = 131;
		scroll.width = 136;
		scroll.scrollMax = 16 * 300;

		int y = 0;

		for (int i = 0; i < 300; i++) {

			addSprite(id, 625);
			scroll.child(child++, id, 0, y);
			id++;

			addHoverClickText(id, "", "View Tracking", tda, 0, 0xEE9021, false, true, 150);
			setBounds(id, 5, y + 3, child, scroll);
			child++;
			id++;

			addText(id, "0", tda, 0, 0xB8B8B8, false, true);
			setBounds(id, 110, y + 3, child, scroll);
			child++;
			id++;

			y += 16;
		}
	}


	public static void achievement(GameFont[] tda) {
		killLog(tda);
		int id = 77400;

		Widget w = addInterface(id++);
		w.totalChildren(36);
		int child = 0;

		int width = 398;
		int height = 295;
		int x = (512 - width) / 2;
		int y = (334 - height) / 2;

		addBasics("Achievement", tda, w, child, id, x, y, width, height, AutomaticSprite.BACKGROUND_BIG_BROWN);
		child += 3;
		id += 3;

		int boxX = x + 16;
		int boxY = y + 32;
		int boxWidth = width - 32;
		int boxHeight = 55;
		addPanel("Description:", tda, w, child, id, boxX, boxY, boxWidth, boxHeight);
		child += 4;
		id += 4;

		addText(id, "Description of achievement goes here " + id, tda, 1, 0xB8B8B8, true);
		w.child(child++, id++, boxX + (boxWidth / 2), boxY + 30);

		boxY += boxHeight + 10;
		boxHeight = 103;
		addPanel("Reward:", tda, w, child, id, boxX, boxY, boxWidth, boxHeight);
		child += 4;
		id += 4;

		addText(id, "Reward text goes here" + id, tda, 1, 0xB8B8B8, true);
		w.child(child++, id++, boxX + (boxWidth / 2), boxY + 30);

		// Odd number of items
		for (int i = 0; i < 9; i++) {
			addItem(id);
			w.child(child++, id++, boxX + (boxWidth / 2) - 172 + (39 * i), boxY + 55);
		}

		// Even number of items
		for (int i = 0; i < 8; i++) {
			addItem(id);
			w.child(child++, id++, boxX + (boxWidth / 2) - 156 + (39 * i), boxY + 55);
		}

		boxY += boxHeight + 10;
		boxHeight = 68;
		addPanel("Progress:", tda, w, child, id, boxX, boxY, boxWidth, boxHeight);
		child += 4;
		id += 4;

		addAdvancedSprite(id, 1012);
		w.child(child++, id++, boxX + 12, boxY + 31 + 1);

		addPercentageBar(id, 342, 648, 650, 649, 0, 100, 100);
		w.child(child++, id++, boxX + 12, boxY + 31);

	}

	public static void addPanel(String title, GameFont[] tda, Widget w, int child, int id, int x, int y, int width, int height) {
		addNewBox(w, child, id, x, y, width, height);
		child += 3;
		id += 3;
		addText(id, title, tda, 2, 0xffb000, true);
		w.child(child++, id++, x + (width / 2), y + 10);
	}

	public static void achievementProgression(GameFont[] tda) {
		int id = 81000;

		Widget w = addInterface(id++);

		setChildren(6, w);

		int child = 0;

		addTransparentRectangle(id, 240, 62, 0, true, 90);
		setBounds(id, 135, 268, child++, w);
		id++;

		addTransparentRectangle(id, 242, 64, 0xff9040, false, 100);
		setBounds(id, 135 - 1, 268 - 1, child++, w);
		id++;

		addText(id, "Need food", tda, 0, 0xff9040, true);
		w.child(child++, id, 131 + 11 + 108, 288 - 16);
		id++;

		addText(id, "Steal from Bakery stall 100 times", tda, 0, 0xff9040, true);
		w.child(child++, id, 131 + 11 + 108, 287);
		id++;

		addAdvancedSprite(id, 886);
		w.child(child++, id, 131 + 11, 302 + 1);
		id++;

		addPercentageBar(id, 226, 648, 650, 649, 0, 100, 100);
		w.child(child++, id, 131 + 11, 302);
		id++;
	}

	public static void achievementCompletion(GameFont[] tda) {
		int id = 81020;

		Widget w = addInterface(id++);

		setChildren(5, w);

		int child = 0;

		int x = 100;

		int y = 0;

		addTransparentRectangle(81021, 240, 62, 0, true, 130);
		setBounds(81021, x +35, y + 18, child++, w);

		addTransparentRectangle(81022, 242, 64, 0xff9040, false, 60);
		setBounds(81022, x + 35 - 1, y + 18 - 1, child++, w);

		addTransparentHoverRectangle(81025, 242, 64, 0xff9040, false, 0, 255);
		interfaceCache[81025].atActionType = OPTION_OK;
		interfaceCache[81025].tooltip = "Achievement";
		setBounds(81025, x + 35 - 1, y + 18 - 1, child++, w);

		addText(81023, "Achievement complete!", tda, 4, 0xff9040, true);
		w.child(child++, 81023, x + 31 + 13 + 110, y + 22);

		addText(81024, "Need food", tda, 3, 0xff9040, true);
		w.child(child++, 81024, x + 31 + 13 + 108, y + 53);
	}

	public static void bossDropTables(GameFont[] tda) {
		int id = 81200;

		Widget w = addInterface(id++);
		w.totalChildren(29);
		int child = 0;

		int width = 498;
		int height = 314;
		int x = (512 - width) / 2;
		int y = (334 - height) / 2;

		addBasics("Boss Drop Tables", tda, w, child, id, x, y, width, height, AutomaticSprite.BACKGROUND_BROWN);
		interfaceCache[id + 2].textColor = 0xffb000;
		child += 3;
		id += 3;

		int buttonWidth = 125;

		addVerticalDivider(id, height - 40, AutomaticSprite.DIVIDER_VERTICAL_SMALL_BROWN);
		w.child(child++, id++, x + 6 + buttonWidth, y + 34);

		addTransparentRectangle(id, buttonWidth, 17, 0, true, 56);
		w.child(child++, id++, x + 6, y + 34);

		addHorizontalDivider(id, buttonWidth, AutomaticSprite.DIVIDER_HORIZONTAL_SMALL_BROWN);
		w.child(child++, id++, x + 6, y + 34 + 17);

		addText(id, "Bosses", tda, 0, 0xffb000, true);
		w.child(child++, id++, x + 6 + (buttonWidth / 2), y + 37);

		addText(id, "Select a boss to see its information.", tda, 1, 0xffb000, true);
		w.child(child++, id++, x + 6 + buttonWidth + 3 + (354 / 2), y + 140);

		/*addPet(id);
		PetSystem.petSelected = 239;
		w.child(child++, id++, x + 6 + buttonWidth + 3 + 115, y + 180);*/
		id++;

		addText(id, "King Black Dragon", tda, 4, 0xffb000, true);
		interfaceCache[id].invisible = true;
		w.child(child++, id++, x + 6 + buttonWidth + 3 + (354 / 2), y + 39);

		addVerticalLine(id, 58, 0x726451);
		interfaceCache[id].invisible = true;
		w.child(child++, id++, x + 6 + buttonWidth + 3 + 105, y + 74);

		addVerticalLine(id, 58, 0x726451);
		interfaceCache[id].invisible = true;
		w.child(child++, id++, x + 6 + buttonWidth + 3 + 85 + 166, y + 74);

		addRightAlignedText(id, "Combat Level", tda, 0, 0xffb000);
		interfaceCache[id].invisible = true;
		w.child(child++, id++, x + 6 + buttonWidth + 3 + 105 - 7, y + 78);

		addRightAlignedText(id, "Hitpoints", tda, 0, 0xffb000);
		interfaceCache[id].invisible = true;
		w.child(child++, id++, x + 6 + buttonWidth + 3 + 105 - 7, y + 78 + 19);

		addRightAlignedText(id, "Max Hit", tda, 0, 0xffb000);
		interfaceCache[id].invisible = true;
		w.child(child++, id++, x + 6 + buttonWidth + 3 + 105 - 7, y + 78 + 38);

		addRightAlignedText(id, "Slayer Level", tda, 0, 0xffb000);
		interfaceCache[id].invisible = true;
		w.child(child++, id++, x + 6 + buttonWidth + 3 + 85 + 166 - 7, y + 78);

		addRightAlignedText(id, "Kill Count", tda, 0, 0xffb000);
		interfaceCache[id].invisible = true;
		w.child(child++, id++, x + 6 + buttonWidth + 3 + 85 + 166 - 7, y + 78 + 19);

		addRightAlignedText(id, "Fastest Teleport", tda, 0, 0xffb000);
		interfaceCache[id].invisible = true;
		w.child(child++, id++, x + 6 + buttonWidth + 3 + 85 + 166 - 7, y + 78 + 38);

		addText(id, "276", tda, 0, 0xccaa5b);
		interfaceCache[id].invisible = true;
		w.child(child++, id++, x + 6 + buttonWidth + 3 + 105 + 7, y + 78);

		addText(id, "240", tda, 0, 0xccaa5b);
		interfaceCache[id].invisible = true;
		w.child(child++, id++, x + 6 + buttonWidth + 3 + 105 + 7, y + 78 + 19);

		addText(id, "65", tda, 0, 0xccaa5b);
		interfaceCache[id].invisible = true;
		w.child(child++, id++, x + 6 + buttonWidth + 3 + 105 + 7, y + 78 + 38);

		addText(id, "None", tda, 0, 0xccaa5b);
		interfaceCache[id].invisible = true;
		w.child(child++, id++, x + 6 + buttonWidth + 3 + 85 + 166 + 7, y + 78);

		addText(id, "122", tda, 0, 0xccaa5b);
		interfaceCache[id].invisible = true;
		w.child(child++, id++, x + 6 + buttonWidth + 3 + 85 + 166 + 7, y + 78 + 19);

		addText(id, "Teleport text here", tda, 0, 0xccaa5b);
		interfaceCache[id].invisible = true;
		w.child(child++, id++, x + 6 + buttonWidth + 3 + 85 + 166 + 7, y + 78 + 38);

		addText(id, "Click on an item to check its drop rate.", tda, 0, 0xffb000, true);
		interfaceCache[id].invisible = true;
		w.child(child++, id++, x + 6 + buttonWidth + 3 + (354 / 2), y + height - 6 - 15);

		addNewBox(w, child, id, x + 6 + buttonWidth + 3 + 10, y + 141, 337, 148);
		interfaceCache[id].invisible = true;
		interfaceCache[id + 1].invisible = true;
		interfaceCache[id + 2].invisible = true;
		child += 3;
		id += 3;

		Widget scroll1 = addInterface(id++);
		interfaceCache[scroll1.id].invisible = true;
		w.child(child++, scroll1.id, x + 6 + buttonWidth + 3 + 12, y + 143);
		scroll1.width = 333 - 16;
		scroll1.height = scroll1.scrollMax = 144;
		scroll1.totalChildren(1);

			addContainer(id, 7, 15, 14, 3, "Check drop rate", null, null, null, null);
			scroll1.child(0, id++, 5, 5);

		Widget scroll2 = addInterface(id++);
		w.child(child++, scroll2.id, x + 6, y + 34 + 17 + 3);

		int lines = 100;
		int scroll2Child = 0;
		scroll2.width = buttonWidth - 16;
		scroll2.height = 253;
		scroll2.scrollMax = lines * 20;
		scroll2.totalChildren(lines * 2);

			for (int i = 0; i < lines; i++) {
				addHoverText(id, "" + id, "View drop table", tda, 0, 0xccaa5b, false, true, buttonWidth - 16, 0xFFFFFF);
				scroll2.child(scroll2Child++, id++, 3, 5 + i * 20);

				addHorizontalLine(id, buttonWidth - 22, 0x726451);
				scroll2.child(scroll2Child++, id++, 3, 20 + i * 20);
			}

	}

	public static void myCommands(GameFont[] tda) {
		int id = 82100;
		Widget w = addInterface(id++);
		w.totalChildren(10);
		int child = 0;

		int width = 472;
		int height = 294;
		int x = 20;
		int y = 20;
		addBackground(id, width, height, true, AutomaticSprite.BACKGROUND_BROWN);
		w.child(child++, id++, x, y);

		addText(id, "My Commands", tda, 2, 0xffb000);
		w.child(child++, id++, x + 189, y + 10);

		w.child(child++, 43702, x + width - 26, y + 10); // Close button

		addRectangle(id, 448, 242, 0, false);
		w.child(child++, id++, x + 12, y + 40);

		Widget scroll = addInterface(id);
		scroll.width = 449 - 16;
		scroll.height = scroll.scrollMax = 224;
		w.child(child++, id++, x + 11, y + 58);
		int lines = 300;
		scroll.totalChildren((lines * 2) + (lines / 2));
		int scrollChild = 0;

		int currentY = 1;
		for (int i = 0; i < lines; i++) {
			int rowHeight = 17;
			if (i % 2 == 0) {
				addTransparentRectangle(id, 436, rowHeight, 0xffffff, true, 10);
				scroll.child(scrollChild++, id++, 0, currentY);
			} else {
				id++;
			}

			addHoverText(id, "" + id, "Send command", tda, 0, 0xEE9021, false, true, 165, 0xFFFFFF);
			scroll.child(scrollChild++, id++, 6, currentY + 3);

			addText(id, "" + id, tda, 0, 0xB8B8B8);
			scroll.child(scrollChild++, id++, 176, currentY + 3);

			currentY += rowHeight;
		}

		addText(id, "Command", tda, 2, 0xffb000);
		w.child(child++, id++, x + 17, x + 42);

		addText(id, "Description", tda, 2, 0xffb000);
		w.child(child++, id++, x + 187, x + 42);

		addHorizontalLine(id, 447, 0);
		w.child(child++, id++, x + 13, x + 58);

		addHorizontalLine(id, 448, 0);
		w.child(child++, id++, x + 12, y + 281);

		addVerticalLine(id, 242, 0);
		w.child(child++, id++, x + 182, y + 40);

	}

	public static void GodWars(GameFont[] TDA) {
		Widget rsinterface = addInterface(16210);
		addText(16211, "NPC killcount", TDA, 0, 0xff9040);
		addText(16212, "Armadyl kills", TDA, 0, 0xff9040);
		addText(16213, "Bandos kills", TDA, 0, 0xff9040);
		addText(16214, "Saradomin kills", TDA, 0, 0xff9040);
		addText(16215, "Zamorak kills", TDA, 0, 0xff9040);
		addText(16216, "", TDA, 0, 0x66FFFF);//armadyl
		addText(16217, "", TDA, 0, 0x66FFFF);//bandos
		addText(16218, "", TDA, 0, 0x66FFFF);//saradomin
		addText(16219, "", TDA, 0, 0x66FFFF);//zamorak
		rsinterface.scrollMax = 0;
		rsinterface.children = new int[9];
		rsinterface.childX = new int[9];
		rsinterface.childY = new int[9];
		rsinterface.children[0] = 16211;
			rsinterface.childX[0] = -52+375+30;		rsinterface.childY[0] = 7;
		rsinterface.children[1] = 16212;
			rsinterface.childX[1] = -52+375+30;		rsinterface.childY[1] = 30;
		rsinterface.children[2] = 16213;
			rsinterface.childX[2] = -52+375+30;		rsinterface.childY[2] = 44;
		rsinterface.children[3] = 16214;
		rsinterface.childX[3] = -52+375+30;		rsinterface.childY[3] = 58;
			rsinterface.children[4] = 16215;
			rsinterface.childX[4] = -52+375+30;		rsinterface.childY[4] = 73;

		rsinterface.children[5] = 16216;
			rsinterface.childX[5] = -52+460+60;		rsinterface.childY[5] = 31;
		rsinterface.children[6] = 16217;
			rsinterface.childX[6] = -52+460+60;		rsinterface.childY[6] = 45;
		rsinterface.children[7] = 16218;
			rsinterface.childX[7] = -52+460+60;		rsinterface.childY[7] = 59;
		rsinterface.children[8] = 16219;
			rsinterface.childX[8] = -52+460+60;		rsinterface.childY[8] = 74;
	}

	/**
	 * The navigations
	 */
	public static final int WILDERNESS_BUTTON = 47017;
	public static final int TRAINING_BUTTON = 47023;
	public static final int MINIGAME_BUTTON = 47041;
	public static final int SKILLING_BUTTON = 47035;
	public static final int CITIES_BUTTON = 47011;
	public static final int BOSSES_BUTTON = 47029;
	public static final int SLAYER_BUTTON = 47047;

	public static Widget addFurnitureX(int i) {
		Widget rsi = interfaceCache[i] = new Widget();
		rsi.id = i;
		rsi.parent = i;
		rsi.type = 5;
		rsi.atActionType = 0;
		rsi.contentType = 0;
		rsi.opacity = 0;
		rsi.enabledSprite = SpriteCompanion.cacheSprite[1060];
		rsi.width = 512;
		rsi.height = 334;
		setSelectableValues(i, 1000 + i - 138324, 1);
		return rsi;
	}

	public static void setSelectableValues(int frame, int configId,
										   int requiredValue) {
		Widget rsi = interfaceCache[frame];
		rsi.valueCompareType = new int[] { 5 };
		rsi.requiredValues = new int[] { requiredValue };
		rsi.valueIndexArray = new int[1][3];
		rsi.valueIndexArray[0][0] = 5;
		rsi.valueIndexArray[0][1] = configId;
		rsi.valueIndexArray[0][2] = 0;
	}
	public static Widget addFurnitureModels(int i) {
		Widget Tab = interfaceCache[i] = new Widget();
		Tab.actions = new String[5];
		Tab.spritesX = new int[20];
		Tab.inventoryAmounts = new int[12];
		Tab.inventoryItemId = new int[12];
		Tab.spritesY = new int[20];
		Tab.children = new int[0];
		Tab.childX = new int[0];
		Tab.childY = new int[0];
		Tab.filled = false;
		Tab.usableItems = false;
		Tab.textShadow = false;
		Tab.spritePaddingX = 164;
		Tab.spritePaddingY = 37;
		Tab.type = 2;
		Tab.parent = 138272;
		Tab.actions = new String[] {"Build", null, null, null, null};
		Tab.id = 138274;
		Tab.width = 2;
		Tab.height = 4;
		return Tab;
	}

	public enum Navigation {
		WILDERNESS(WILDERNESS_BUTTON, 46000),

		TRAINING(TRAINING_BUTTON, 46001),

		MINIGAME(MINIGAME_BUTTON, 46002),

		SKILLING(SKILLING_BUTTON, 46003),

		CITIES(CITIES_BUTTON, 46004),

		BOSSES(BOSSES_BUTTON, 46005),

		SLAYER(SLAYER_BUTTON, 46006)

		;

		/**
		 * The button id
		 */
		private int button;

		/**
		 * The interface id
		 */
		private int interfaceId;

		/**
		 * Represents a new teleport navigation
		 *
		 * @param button
		 *            the button
		 * @param interfaceId
		 *            the interface id
		 */
		Navigation(int button, int interfaceId) {
			this.setButton(button);
			this.setInterfaceId(interfaceId);
		}

		/**
		 * Sets the button
		 *
		 * @return the button
		 */
		public int getButton() {
			return button;
		}

		/**
		 * Sets the button
		 *
		 * @param button
		 *            the button
		 */
		public void setButton(int button) {
			this.button = button;
		}

		/**
		 * Sets the interfaceId
		 *
		 * @return the interfaceId
		 */
		public int getInterfaceId() {
			return interfaceId;
		}

		/**
		 * Sets the interfaceId
		 *
		 * @param interfaceId
		 *            the interfaceId
		 */
		public void setInterfaceId(int interfaceId) {
			this.interfaceId = interfaceId;
		}

		/**
		 * Gets for button
		 *
		 * @param button
		 *            the button
		 * @return the navigation
		 */
		public static Navigation forButton(int button) {
			return Arrays.stream(values()).filter(c -> c.getButton() == button).findFirst().orElse(null);
		}
	}

	public static void teleport(GameFont[] tda) {
		achievement(tda);

		Widget bosses = addInterface(46000); // wild

		bosses.totalChildren(2);

		bosses.child(0, 47000, 0, 0);
		bosses.child(1, 47100, 195, 72);

		bosses = addInterface(46001); // monsters

		bosses.totalChildren(2);

		bosses.child(0, 47000, 0, 0);
		bosses.child(1, 89000, 195, 72);

		bosses = addInterface(46002); // minigames

		bosses.totalChildren(2);

		bosses.child(0, 47000, 0, 0);
		bosses.child(1, 47600, 195, 72);

		bosses = addInterface(46003); // skilling

		bosses.totalChildren(2);

		bosses.child(0, 47000, 0, 0);
		bosses.child(1, 44700, 195, 72);

		bosses = addInterface(46004); // cities

		bosses.totalChildren(2);

		bosses.child(0, 47000, 0, 0);
		bosses.child(1, 45800, 195, 72);

		bosses = addInterface(46005); // BOSSES

		bosses.totalChildren(2);

		bosses.child(0, 47000, 0, 0);
		bosses.child(1, 88000, 195, 72);

		bosses = addInterface(46006); // BOSSES

		bosses.totalChildren(2);

		bosses.child(0, 47000, 0, 0);
		bosses.child(1, 77000, 195, 72);

		int id = 47000;
		int frame = 0;

		Widget w = addInterface(id);
		id++;

		w.totalChildren(31);

		addSprite(id, 618);
		w.child(frame++, id, 7, 2);
		id++;

		addText(id, "@or1@Grinderscape Teleport", tda, 2, 0, true, true);
		w.child(frame++, id, 255, 13);
		id++;

		addText(id, "Wilderness Teleports " + id, tda, 2, 0xFFB914, true, true);
		w.child(frame++, id, 335, 55);
		id++;

		addText(id, "@or1@Select your teleport destination from the menu below", tda, 0, 0, true, true);
		w.child(frame++, id, 256, 35);
		id++;

		addText(id, "@or2@Previous teleports:", tda, 1, 0, false, true);
		w.child(frame++, id, 275, 272);
		id++;

		addHoverClickText(id, "1. None ", "Teleport to previous location", tda, 0, 0xEE9021, true, true, 120);
		setBounds(id, 207, 285, frame, w);
		frame++;
		id++;

		addHoverClickText(id, "2. None", "Teleport to previous location", tda, 0, 0xEE9021, true, true, 120);
		setBounds(id, 207, 297, frame, w);
		frame++;
		id++;

		addHoverClickText(id, "3. None", "Teleport to previous location", tda, 0, 0xEE9021, true, true, 120);
		setBounds(id, 341, 285, frame, w);
		frame++;
		id++;

		addHoverClickText(id, "4. None", "Teleport to previous location", tda, 0, 0xEE9021, true, true, 120);
		setBounds(id, 341, 297, frame, w);
		frame++;
		id++;

		addTextClose(40000, tda);
		setBounds(40000, 420, 15, frame, w);
		frame++;
		id++;

		String[] NAV = { "Cities", "@red@Wilderness", "Combat Training", "Bosses", "Skilling", "Minigames", "Slayer" };

		int boss_id = 608;
		int boss_x = 36;
		int boss_y = 59;

		for (String titleStrings : NAV) {
			addButton(id, boss_id + 1, 122, 30, 622, 622, id + 1, "Select @gre@" + titleStrings);
			setBounds(id, boss_x - 1, boss_y - 1, frame, w);
			frame++;

			addHoveredButton(id + 1, 621, 122, 30, boss_id);
			setBounds(id + 1, boss_x - 1, boss_y - 1, frame, w);
			frame++;
			id += 2;

			addText(id, "@or1@" + titleStrings, tda, 2, 0, true, true);
			w.child(frame, id, boss_x + 62, boss_y + 8);
			frame++;
			id++;

			boss_id += 2;
			id += 3;
			boss_y += 35;
		}

		/**
		 * Wild scroll
		 */
		String[][] WILD = {
				{ "Edgeville", "Safe Zone" },
				{ "Mage Bank", "Safe Zone" },

				{ "Dark Fortress", "Wild Level @red@14" },
				{ "Revenants Cave", "Wild Level @red@17" },
				{ "Green Dragons", "Wild Level @red@20", },
				{ "Bandits Camp", "Wild Level @red@23 Multi" },

				{ "Crazy Archaeologist", "Wild level @red@22" },
				{ "Chronozon", "Wild level @red@26" },
				{ "Chaos Fanatic", "Wild level @red@39" },
				{ "Merodach", "Wild level @red@53" },

				{ "Barrelchest", "Wild level @red@24 Multi" },
				{ "Venenatis", "Wild level @red@31 Multi" },
				{ "Vet'ion", "Wild level @red@33 Multi" },
				{ "Callisto", "Wild level @red@39 Multi" },
				{ "King Black Dragon", "Wild level @red@42" },
				{ "Galvek", "Wild level @red@45 Multi" },
				{ "Chaos Elemental", "Wild level @red@49 Multi" },
				{ "Scorpia", "Wild level @red@54 Multi" },

				{ "Black Chinchompa", "Wild Level @red@32" },
				{ "Chinchompa Hill", "Wild Level @red@33"},
				{ "Chaos Altar", "Wild level @red@38" },
				{ "Wilderness Agility", "Wild level @red@50" },
				{ "Wilderness Thieving", "Wild Level @red@53 Multi"},
				{ "Wild Resource Area", "Wild Level @red@54 Multi" },
				{ "Demonic Ruins", "Wild Level @red@46 Multi" },
				{ "Deep Obelisks", "Wild Level @red@50 Multi" },
		};
		teleportScroll(47100, WILD, tda);

		/**
		 * Monsters
		 */
		String[][] MONSTERS = {
				{ "Lumbridge Farm", "Low lvl" }, { "Neitiznot Yaks", "Low lvl" },
				{ "Relleka Crabs", "Low lvl" }, { "South Hosidius", "Low lvl" },
				{ "Fenkenstrain Castle", "Low lvl" }, { "Al Kharid Temple", "Low lvl" },
				{ "Stronghold Security", "All levels" }, { "Varrock Sewers", "Med lvl" },
				{ "Edgeville Dungeon", "Med lvl" }, { "Gnome Tortoise", "Med lvl" },
				{ "Canifis Werewolves", "Med lvl" }, { "Falador Mole Lair", "Med lvl"},
				{ "Pothole Dungeon", "Med lvl" }, { "Khazard Battlefield", "Med lvl" },
				{ "Kalphite's Lair", "Med lvl" }, { "Tarn's Lair", "Med lvl"},
				{ "Ice Canyon", "Med lvl" }, { "Asgarnian Ice Cave", "Med lvl" },
				{ "Crocodile Camp", "Med lvl" }, { "Karamja's Dungeon", "Med lvl" },
				{ "Fremennik Isles", "Med lvl" }, { "Crash Island", "Med lvl" },
				{ "Smoke Tunnels", "Med lvl" }, { "Taverly Dungeon", "Med-high lvl" },
				{ "Brimhaven Dungeon", "Med-high lvl" }, { "Dagannoth's Lair", "Med-high lvl" },
				{ "Ape Atoll Dungeon", "Med-high lvl" }, { "Bandit's Camp", "Med-high lvl" },
				{ "Tzhaar", "High lvl" }, { "Mor Ul Rek", "High lvl"},
				{ "Suqah Diplomacy", "High lvl" }, { "Ancient Dungeon", "High lvl" },
				{ "Mount Firewake", "High lvl" }, { "Godwars Dungeon", "High lvl" },
				{ "Smoke Devil Dungeon", "High lvl" }, { "Lithkren Vault", "High lvl" },
				{ "Shadow Dungeon", "High lvl"},
		};
		teleportScroll(89000, MONSTERS, tda);

		/**
		 * Minigames
		 */
		String[][] MINIGAMES = {

				/*{ "Barrows", "Barrows Armour" }, { "Castle Wars", "Decorative Armour" }, { "Clan Wars", "Clan Rankings" }, { "Duel Arena", "Staking" },
				{ "Fight Cave", "Fire Cape" }, { "Fight Pits", "Fun PVP" }, { "Warriors Guild", "Torso/Defender" }, { "Fun PvP", "Fun PK'ing" },
				 { "Dice Arena", "Gambling pub" }, { "Pest Control", "Void Armour" },*/

				{ "Barrows", "Barrows Brothers" }, { "Duel Arena", "Friendly Stake" },
				{ "Fight Cave", "Defeat Tz-Tok-Jad" }, { "Fight Pits", "Winner Champion" },
				{ "Warriors Guild", "Collect Defenders" }, { "Pest Control", "Defend The Void" },
				{ "Free PvP Arena", "Fun PVP" }, { "Medallion Casino", "Gamble 'em" },
				{ "Blast Furnace", "Smelt Ores" }, { "Motherlode Mine", "Dwarven Mine" },
				{ "Aquais Neige", "The Iceberg" }, { "Castle Wars", "Capture the Flag" },
				{ "Wintertodt", "Snow Storm" }, { "Tears of Guthix", "Experience Rewards" },
				{ "Chambers of Xeric", "Raids I" }, { "Theatre of Blood", "Raids II" },
				{ "Porazdir Event", "Wilderness Event" }, { "Puro Puro", "Hunter Minigame" },
		};
		teleportScroll(47600, MINIGAMES, tda);

		/**
		 * Skills
		 */
		String[][] SKILLS = {
				{ "Draynor forest", "Woodcutting skill" }, { "Camelot jungle", "Woodcutting skill" },
				{ "Teak woodland", "Woodcutting skill" }, { "Hardwood Grove", "Woodcutting skill" },
				{ "Sorcerer's Tower", "Woodcutting skill" }, { "Neitznot plantation", "Woodcutting skill" },
				{ "Catherby Shore", "Fishing skill" }, { "Karamja's Dock", "Fishing skill" },
				{ "Lumbridge Swamp", "Fishing" },	{ "Barbarian village", "Fishing skill" },
				{ "Neitiznot mine", "Mining skill" }, { "Rune Essence mine", "Mining skill" },
				{ "Dwarven mine", "Mining skill" }, { "Al Kharid mine", "Mining skill" },
				{ "Brimhaven Peninsula", "Mining skill" }, { "Motherlode mine", "Mining skill" },
				{ "Limestone Quarry", "Mining skill" }, { "Bandit Camp Quarry", "Mining skill" },
				{ "Trahaearn Mine", "Mining skill" }, { "Dorgesh-Kaan Mine", "Mining skill" },
				{ "Salt Mine", "Mining skill" }, { "Dondakan's Mine", "Mining skill" },
				{ "Arceuus Essence Mine", "Mining skill" }, { "Daeyalt Essence Mine", "Mining skill" },
				{ "Varrock Mine", "Mining skill" }, { "Gems mine", "Mining skill" },
				{ "Gnome Stronghold", "Agility skill" }, { "Barbarian Outpost", "Agility skill" },
				{ "Draynor Rooftop", "Agility skill" }, { "Al Kharid Rooftop", "Agility skill" },
				{ "Varrock Rooftop", "Agility skill" }, { "Canifis Rooftop", "Agility skill" },
				{ "Falador Rooftop", "Agility skill" }, { "Seers Rooftop", "Agility skill" },
				{ "Pollnivneach Rooftop", "Agility skill" }, { "Rellekka Rooftop", "Agility skill" },
				{ "Ardougne Rooftop", "Agility skill" }, { "Brimhaven Arena", "Agility skill" },
				{ "Pyramid Agility", "Agility skill" }, { "Rellekka Hunter", "Hunter skill" },
				{ "Piscatoris Hunter", "Hunter skill" }, { "Uzer Hunter area", "Hunter skill" },
				{ "Falconry", "Hunter skill" },
				{ "Falador's farm", "Farming skill" }, { "Catherby's farm", "Farming skill" },
				{ "Falador's oven", "Crafting skill" }, { "Flax field", "Fletching skill" },
				{ "Air altar", "Runecrafting skill" }, { "The Abyss", "Runecrafting skill" },
				{ "Wrath altar", "Runecrafting skill" }, { "Ourania altar", "Runecrafting skill" },
				{ "Ardougne Stalls", "Thieving skill" }, { "Master Farmer", "Thieving skill" },
				{ "Woodcutting Guild", "Woodcutting lvl 60" },
				{ "Fishing Guild", "Fishing level 68" },
				{ "Cooking Guild", "Cooking lvl 32" },
				{ "Dwarf Mining Guild", "Mining level 60" },
				{ "Crafting Guild", "Crafting lvl 60" },
				{ "Farming Guild", "Farming lvl 60" },
				{ "Wizard's Guild", "Magic lvl 60" },
				{ "Archer's Guild", "Ranging lvl 60" },
				{ "The Deserted Reef", "Bronze members only" },
				{ "La Isla Ebana", "Ruby members only" },
				{ "The New Peninsula", "Platinum members only" },

		};
		teleportScroll(44700, SKILLS, tda);

		/**
		 * Boss scroll
		 */
		String[][] BOSS = {
				{ "The Cursed Vault", "Legendary members only" }, { "Obor", "Giant key"},
				{ "Bryophyta", "Mossy key" }, { "Cerberus", "Level 91 Slayer" },
				{ "General Graardor", "Godwars" }, { "Kree'arra", "Godwars" },
				{ "K'ril Tsutsaroth", "Godwars" }, { "Commander Zilyana", "Godwars" },
				{ "Dagannoth Kings", "Dagannoth lair" }, { "Kalphite Queen", "Kalphite's lair" },
				{ "Corporeal Beast", "Corporeal Beast's lair" }, { "Mutant Tarn", "Tarn's lair" },
				{ "Black Knight Titan", "Yanille dungeon" }, { "The Untouchable", "Yanille dungeon" },
				{ "Jungle Demon", "Shazyen's jungle" }, { "Ice Troll King", "Ice path" },
				{ "Ice Queen", "Ice queen's lair" }, { "Sea Troll Queen", "North Miscellania" },
				{ "Giant Mole", "Falador's lair" }, { "Giant Sea Snake", "Snake pit" },
				{ "Slash Bash", "Karamja's dungeon" }, { "Kamil", "Ice canyon" },
				{ "Skeleton Hellhound", "Smoke tunnels" }, { "Demonic Gorilla's", " Crash Site Cavern" },
				{ "Lizardman Shaman", "Lizardman canyon" }, { "Alchemical Hydra", "Mount Karuulm" },
				{ "Vorkath", "Vorkath's lair" }, { "Zulrah", "Zul-Andra" },
				{ "Porazdir", "Hell Demond" },
				{ "Glod", "Muscular Giant" }, { "Fragment of Seren", "Iorwerth Lord" },
				{ "The Nightmare", "Ashihama's Lair" }, { "Nex", "Zarosian Godwars" },

		};
		teleportScroll(88000, BOSS, tda);
		/**
		 * City scroll
		 */
		String[][] CITY = { { "Ape Atoll", "" }, { "Al Kharid", "" }, { "Ardougne", "" }, { "Burthorpe", "" },
				{ "Camelot", "" }, { "Catherby", "" }, { "Canifis", "" }, { "Draynor", "" }, { "Edgeville", "" },
				{ "Falador", "" }, { "Grand Exchange", "" }, { "Market Place", "" }, { "Karamja", "" },
				{ "Lumbridge", "" }, { "Lunar Isle", "" }, { "Neitznot", "" }, { "Pollnivneach", "" },
				{ "TzHaar City", "" }, { "Varrock", "" }, { "Yanille", "" }, { "Fremennik", "" },
				{ "Shilo Village", "" },  { "West Ardougne", "" }, { "Tai Bwo Wannai", "" },{"Tree Gnome Stronghold", ""} };
		teleportScroll(45800, CITY, tda);
		/**
		 * Slayer scroll
		 */
		String[][] SLAYER = { { "Turael", "1-20 Slayer" }, { "Mazchna", "0-40 Slayer" },
				{ "Vannaka", "40-60 Slayer" }, { "Chaeldar", "60-70 Slayer" },
				{ "Duradel", "70+ Slayer" }, { "Konar quo Maten", "75+ Combat lvl" },
				{ "Nieve", "85+ Combat lvl" }, { "Krystilia", "@red@Wilderness Slayer" },
				{ "Slayer Cave", "" }, { "Slayer Tower", "" },
				{ "Slayer Stronghold", "" }, { "Kourend Catacombs", "" },
				{ "Feldip Hills", "" }, { "Chasm of Fire", "" },
				{ "Mos Le'Harmless", "" }, { "Killerwatt Plane", "" },
				{ "Karuulm Cave", "" }, { "Lizardman Caves", "" },

		};
		teleportScroll(77000, SLAYER, tda);

	}

	private static void teleportScroll(int id, String[][] text, GameFont[] tda) {
		Widget boss_scroll = addInterface(id);
		id++;
		boss_scroll.totalChildren(text.length * 3);
		boss_scroll.height = 195;
		boss_scroll.width = 282;
		boss_scroll.scrollMax = Math.max(boss_scroll.height, (int) Math.ceil(text.length / 2.0) * 35 + 19);
		id++;
		int frame = 0;

		int boss_id = 608;
		int boss_x = 0;
		int boss_y = 12;

		int xIncrease = 7;

		for (int i = 0; i < text.length; i++) {

			//addButton(id, boss_id + 1, 115, 31, 619, 619, id + 1, "Select @gre@" + text[i][0]);
			addHoverButton(id, 619, 620, "Select @gre@" + text[i][0]);
			setBounds(id, boss_x + xIncrease, boss_y, frame, boss_scroll);
			frame++;

			/*addHoveredButton(id + 1, 620, 115, 31, id + 2);
			setBounds(id + 1, boss_x + xIncrease, boss_y, frame, boss_scroll);
			frame++;*/
			id += 3;

			boolean secondLine = text[i][1].length() > 1;

			addText(id, "@or1@" + text[i][0], tda, 1, 0, true, true);
			boss_scroll.child(frame, id, boss_x + 57 + xIncrease, boss_y + (secondLine ? 3 : 8));
			frame++;
			id++;

			addText(id, "@or2@" + text[i][1], tda, 0, 0, true, true);
			boss_scroll.child(frame, id, boss_x + 57 + xIncrease, boss_y + 17);
			frame++;
			id++;

			id += 3;
			boss_x += 135;
			if (boss_x == 270) {
				boss_x = 0;
				boss_y += 35;
			}
		}
	}

	public static void addHoverClickText(int id, String text, String tooltip, GameFont[] tda, int idx, int color,
										 boolean center, boolean textShadow, int width) {
		Widget rsinterface = addInterface(id);
		rsinterface.id = id;
		rsinterface.parent = id;
		rsinterface.type = 4;
		rsinterface.atActionType = 1;
		rsinterface.width = width;
		rsinterface.height = 11;
		rsinterface.contentType = 0;
		rsinterface.opacity = 0;
		rsinterface.hoverType = -1;
		rsinterface.centerText = center;
		rsinterface.textShadow = textShadow;
		rsinterface.textDrawingAreas = tda[idx];
		rsinterface.defaultText = text;
		rsinterface.secondaryText = "";
		rsinterface.tooltip = tooltip;
		rsinterface.textColor = color;
		rsinterface.secondaryColor = 0;
		rsinterface.defaultHoverColor = 0xFFFFFF;
		rsinterface.secondaryHoverColor = 0;
	}

	public static void addTextClose(int id, GameFont[] tda) {
		Widget rsinterface = addInterface(id);
		rsinterface.id = id;
		rsinterface.parent = id;
		rsinterface.type = 4;
		rsinterface.atActionType = 3;
		rsinterface.width = 70;
		rsinterface.height = 11;
		rsinterface.contentType = 0;
		rsinterface.opacity = 0;
		rsinterface.hoverType = -1;
		rsinterface.centerText = false;
		rsinterface.textShadow = true;
		rsinterface.textDrawingAreas = tda[0];
		rsinterface.defaultText = "Close Window";
		rsinterface.secondaryText = "";
		rsinterface.tooltip = "Close";
		rsinterface.textColor = 0x808080;
		rsinterface.secondaryColor = 0;
		rsinterface.defaultHoverColor = 0xFFFFFF;
		rsinterface.secondaryHoverColor = 0;
	}

	public static void presets(GameFont[] tda) {
		Widget w = addInterface(92000);
		w.totalChildren(89);

		// Add background sprite
		addSprite(92001, 574);

		// Add title
		addText(92002, "Presets", tda, 2, 0xff981f, true, false);

		// Add categories
		addText(92003, "@or1@Spellbook", tda, 0, 0, false, false);
		addText(92004, "@or1@Inventory", tda, 0, 0, false, false);
		addText(92005, "@or1@Equipment", tda, 0, 0, false, false);
		addText(92006, "@or1@Stats", tda, 0, 0, false, false);

		// Add stats strings
		for (int i = 0; i <= 6; i++) {
			addText(92007 + i, "", tda, 2, 0xFFD700, false, false);
		}

		// Add spellbook string
		addText(92014, "", tda, 1, 0, true, false);

		// Add inventory
		for (int i = 0; i < 28; i++) {
			addItemOnInterface(92015 + i, 92000, new String[] {});
		}

		// Add equipment
		for (int i = 0; i < 14; i++) {
			addItemOnInterface(92044 + i, 92000, new String[] {});
		}

		// Open presets on death text
		addText(92059, "@or1@Open on death: ", tda, 1, 0, false, false);

		// Open presets on death config tick
		addButton(92060, 92000, 14, 15, 1, 987, 332, 334, -1, "Toggle");

		// Set preset button
		addButton(92061, 92000, 146, 26, 576, 576, 92062, "Select");
		addHoveredButton(92062, 577, 146, 26, 92063);

		// Load preset button
		addButton(92064, 92000, 146, 26, 576, 576, 92065, "Select");
		addHoveredButton(92065, 577, 146, 26, 92066);

		// Preset buttons text
		addText(92067, "Edit this preset", tda, 2, 0xff981f, false, false);
		addText(92068, "Load this preset", tda, 2, 0xff981f, false, false);

		addCloseButton(92093, false);

		// Global Presets
		Widget list = addInterface(92069);
		list.totalChildren(10);
		for (int i = 92070, child = 0, yPos = 3; i < 92080; i++, yPos += 20) {
			addHoverText(i, "Empty", null, tda, 1, 0xff8000, false, true, 240, 0xFFFFFF);
			interfaceCache[i].actions = new String[] { "Select" };
			list.children[child] = i;
			list.childX[child] = 5;
			list.childY[child] = yPos;
			child++;
		}
		list.height = 98;
		list.width = 85;
		list.scrollMax = 210;

		// Global presets title
		addText(92080, "@whi@Global Presets", tda, 0, 0, false, false);

		// Custom Presets
		list = addInterface(92081);
		list.totalChildren(10);
		for (int i = 92082, child = 0, yPos = 3; i < 92092; i++, yPos += 20) {
			addHoverText(i, "Empty", null, tda, 1, 0xff8000, false, true, 240, 0xFFFFFF);
			interfaceCache[i].actions = new String[] { "Select" };
			list.children[child] = i;
			list.childX[child] = 5;
			list.childY[child] = yPos;
			child++;
		}
		list.height = 107;
		list.width = 85;
		list.scrollMax = 210;

		// Custom presets title
		addText(92092, "@whi@Your Presets", tda, 0, 0, false, false);

		// Children
		int child = 0;
		w.child(child++, 92001, 7, 2); // Background sprite
		w.child(child++, 92093, 482, 5); // Close button
		w.child(child++, 92002, 253, 5); // Title
		w.child(child++, 92003, 42, 26); // Category 1 - spellbook
		w.child(child++, 92004, 180, 26); // Category 1 - inventory
		w.child(child++, 92005, 333, 26); // Category 1 - equipment
		w.child(child++, 92006, 453, 26); // Category 1 - stats

		// Stats
		for (int i = 0, yPos = 55; i <= 6; i++, yPos += 40) {
			w.child(child++, 92007 + i, 469, yPos);
		}

		// Spellbook
		w.child(child++, 92014, 65, 46); // Spellbook

		// Inventory
		for (int i = 0, xPos = 130, yPos = 48; i < 28; i++, xPos += 39) {
			w.child(child++, 92015 + i, xPos, yPos);
			if (xPos >= 247) {
				xPos = 91;
				yPos += 39;
			}
		}

		// Equipment bg sprites
		w.child(child++, 1645, 337, 149 - 52 - 17);
		w.child(child++, 1646, 337, 163 - 17);
		w.child(child++, 1647, 337, 114);
		w.child(child++, 1648, 337, 58 + 146 - 17);
		w.child(child++, 1649, 282, 110 - 44 + 118 - 13 + 5 - 17);
		w.child(child++, 1650, 260 + 22, 58 + 154 - 17);
		w.child(child++, 1651, 260 + 134, 58 + 118 - 17);
		w.child(child++, 1652, 260 + 134, 58 + 154 - 17);
		w.child(child++, 1653, 260 + 48, 58 + 81 - 17);
		w.child(child++, 1654, 260 + 107, 58 + 81 - 17);
		w.child(child++, 1655, 260 + 58, 58 + 42 - 17);
		w.child(child++, 1656, 260 + 112, 58 + 41 - 17);
		w.child(child++, 1657, 260 + 78, 58 + 4 - 17);
		w.child(child++, 1658, 260 + 37, 58 + 43 - 17);
		w.child(child++, 1659, 260 + 78, 58 + 43 - 17);
		w.child(child++, 1660, 260 + 119, 58 + 43 - 17);
		w.child(child++, 1661, 260 + 22, 58 + 82 - 17);
		w.child(child++, 1662, 260 + 78, 58 + 82 - 17);
		w.child(child++, 1663, 260 + 134, 58 + 82 - 17);
		w.child(child++, 1664, 260 + 78, 58 + 122 - 17);
		w.child(child++, 1665, 260 + 78, 58 + 162 - 17);
		w.child(child++, 1666, 260 + 22, 58 + 162 - 17);
		w.child(child++, 1667, 260 + 134, 58 + 162 - 17);

		// Equipment
		w.child(child++, 92044, 341, 47); // Head slot
		w.child(child++, 92045, 300, 86); // Cape slot
		w.child(child++, 92046, 341, 86); // Amulet slot
		w.child(child++, 92047, 285, 125); // Weapon slot
		w.child(child++, 92048, 341, 125); // Body slot
		w.child(child++, 92049, 396, 125); // Shield slot
		w.child(child++, 92051, 341, 165); // Legs slot

		w.child(child++, 92053, 285, 205); // Hands slot
		w.child(child++, 92054, 341, 205); // Feet slot
		w.child(child++, 92056, 397, 205); // Ring slot
		w.child(child++, 92057, 381, 86); // Ammo slot

		// Open preset interface on death
		w.child(child++, 92059, 300, 243); // Open presets on death text
		w.child(child++, 92060, 400, 243); // Open presets on death tick config

		// Buttons
		w.child(child++, 92061, 285, 263); // Button 1 - Save This Preset
		w.child(child++, 92062, 285, 263); // Button 1 hover - Save This Preset

		w.child(child++, 92064, 285, 294); // Button 2 - Load This Preset
		w.child(child++, 92065, 285, 294); // Button 2 hover - Load This Preset

		// Button text
		w.child(child++, 92067, 306, 267); // Save this preset text
		w.child(child++, 92068, 306, 299); // Load this preset text

		// Preset lists
		w.child(child++, 92069, 12, 90); // Global presets list
		w.child(child++, 92080, 24, 75); // Global presets list text title

		w.child(child++, 92081, 12, 214); // Custom presets list
		w.child(child++, 92092, 28, 200); // Custom presets list text title
	}

	public static final int KEYBINDING_INTERFACE_ID = 53000;

	public static void keybinding(GameFont[] tda) {
		Widget tab = addInterface(KEYBINDING_INTERFACE_ID);

		addSpriteLoader(53001, 430);
		addText(53002, "Keybinding", tda, 2, 0xff8a1f, false, true);
		addCloseButton(53003, true);

		hoverButton(Keybinding.RESTORE_DEFAULT, "Restore Defaults", 447, 448, "Restore Defaults", newFonts[2], 0xff8a1f,
				0xff8a1f, true);

		addText(53005, "Esc closes current interface", tda, 1, 0xff8a1f, false, true);
		configButton(Keybinding.ESCAPE_CONFIG, "Select", 348, 347);

		tab.totalChildren(48);
		int childNum = 0;

		setBounds(53001, 5, 17, childNum++, tab);
		setBounds(53002, 221, 27, childNum++, tab);
		setBounds(53003, 479, 24, childNum++, tab);
		setBounds(Keybinding.RESTORE_DEFAULT, 343, 275, childNum++, tab);
		setBounds(53005, 60, 285, childNum++, tab);
		setBounds(Keybinding.ESCAPE_CONFIG, 35, 285, childNum++, tab);

		/* Tabs and dropdowns */

		int x = 31;
		int y = 63;
		childNum = 47;

		for (int i = 0; i < 14; i++, y += 43) {

			addSpriteLoader(53007 + 3 * i, 431 + i);
			configButton(53008 + 3 * i, "", 446, 445);

			boolean inverted =  i == 9 || i == 4;
			keybindingDropdown(53009 + 3 * i, 86, 0, Keybinding.OPTIONS, Dropdown.KEYBIND_SELECTION, false);

			setBounds(Keybinding.MIN_FRAME - 2 + 3 * i, x + stoneOffset(431 + i, true), y + stoneOffset(431 + i, false),
					childNum--, tab);
			setBounds(Keybinding.MIN_FRAME - 1 + 3 * i, x, y, childNum--, tab);
			setBounds(Keybinding.MIN_FRAME + 3 * i, x + 39, y + 4, childNum--, tab);

			if (i == 4 || i == 9) {
				x += 160;
				y = 20;
			}
		}
	}

	public static int stoneOffset(int spriteId, boolean xOffset) {
		Sprite stone =  SpriteLoader.getSprite( 445);
		Sprite icon =  SpriteLoader.getSprite( spriteId);

		if (xOffset) {
			return (stone.myWidth / 2) - icon.myWidth / 2;
		}
		return (stone.myHeight / 2) - icon.myHeight / 2;
	}


	public static void addButton(int i, int parent, int w, int h, int config, int configFrame, int sprite1, int sprite2,
			int hoverOver, String tooltip) {
		Widget p = addInterface(i);
		p.parent = parent;
		p.type = TYPE_SPRITE;
		p.atActionType = 1;
		p.width = w;
		p.height = h;
		p.requiredValues = new int[1];
		p.valueCompareType = new int[1];
		p.valueCompareType[0] = 1;
		p.requiredValues[0] = config;
		p.valueIndexArray = new int[1][3];
		p.valueIndexArray[0][0] = 5;
		p.valueIndexArray[0][1] = configFrame;
		p.valueIndexArray[0][2] = 0;
		p.tooltip = tooltip;
		p.defaultText = tooltip;
		p.hoverType = hoverOver;
		p.disabledSprite =  SpriteLoader.getSprite( sprite1);
		p.enabledSprite =  SpriteLoader.getSprite( sprite2);
	}

	public static Widget addButton(int i, int parent, int w, int h, int sprite1, int sprite2, int hoverOver,
			String tooltip) {
		Widget p = addInterface(i);
		p.parent = parent;
		p.type = TYPE_SPRITE;
		p.atActionType = 1;
		p.width = w;
		p.height = h;
		p.tooltip = tooltip;
		p.defaultText = tooltip;
		p.hoverType = hoverOver;
		p.disabledSprite =  SpriteLoader.getSprite( sprite1);
		p.enabledSprite =  SpriteLoader.getSprite( sprite2);

		return p;
	}

	public static void addButton(int i, int parent, int w, int h, Sprite sprite1, Sprite sprite2, int hoverOver,
			String tooltip) {
		Widget p = addInterface(i);
		p.parent = parent;
		p.type = TYPE_SPRITE;
		p.atActionType = 1;
		p.width = w;
		p.height = h;
		p.tooltip = tooltip;
		p.defaultText = tooltip;
		p.hoverType = hoverOver;
		p.disabledSprite = sprite1;
		p.enabledSprite = sprite2;
	}

	public static void addHoveredButtonWTooltip(int i, int spriteId, int w, int h, int IMAGEID, int tooltipId,
			String hover, int hoverXOffset, int hoverYOffset) {// hoverable
		// button
		Widget tab = addInterface(i);
		tab.parent = i;
		tab.id = i;
		tab.type = 0;
		tab.atActionType = 0;
		tab.width = w;
		tab.height = h;
		tab.invisible = true;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.scrollMax = 0;
		addHoverImage_sprite_loader(IMAGEID, spriteId);
		tab.totalChildren(1);
		tab.child(0, IMAGEID, 0, 0);

		Widget p = addInterface(tooltipId);
		p.parent = i;
		p.type = 8;
		p.width = w;
		p.height = h;

		p.hoverText = p.defaultText = p.tooltip = hover;

		p.hoverXOffset = hoverXOffset;
		p.hoverYOffset = hoverYOffset;
		p.regularHoverBox = true;

	}

	public static void prayerBook() {

		Widget rsinterface = interfaceCache[5608];

		// Moves down chivalry
		rsinterface.childX[50 + BEGIN_READING_PRAYER_INTERFACE] = 10;
		rsinterface.childY[50 + BEGIN_READING_PRAYER_INTERFACE] = 195;
		rsinterface.childX[51 + BEGIN_READING_PRAYER_INTERFACE] = 10;
		rsinterface.childY[51 + BEGIN_READING_PRAYER_INTERFACE] = 195;
		rsinterface.childX[63 + BEGIN_READING_PRAYER_INTERFACE] = 10;
		rsinterface.childY[63 + BEGIN_READING_PRAYER_INTERFACE] = 190;
		// Adjust prayer glow sprites position - Chivalry
		interfaceCache[rsinterface.children[50 + BEGIN_READING_PRAYER_INTERFACE]].spriteXOffset = -7;
		interfaceCache[rsinterface.children[50 + BEGIN_READING_PRAYER_INTERFACE]].spriteYOffset = -2;

		// Moves piety to the right
		setBounds(19827, 43, 191, 52 + BEGIN_READING_PRAYER_INTERFACE, rsinterface);

		// Adjust prayer glow sprites position - Piety
		interfaceCache[rsinterface.children[52 + BEGIN_READING_PRAYER_INTERFACE]].spriteXOffset = -2;
		interfaceCache[rsinterface.children[52 + BEGIN_READING_PRAYER_INTERFACE]].spriteYOffset = 2;

		rsinterface.childX[53 + BEGIN_READING_PRAYER_INTERFACE] = 43;
		rsinterface.childY[53 + BEGIN_READING_PRAYER_INTERFACE] = 204;
		rsinterface.childX[64 + BEGIN_READING_PRAYER_INTERFACE] = 43;
		rsinterface.childY[64 + BEGIN_READING_PRAYER_INTERFACE] = 190;

		// Now we add new prayers..
		// AddPrayer adds a glow at the id
		// Adds the actual prayer sprite at id+1
		// Adds a hover box at id + 2
		addPrayer(28001, "Activate @or1@Preserve", 31, 32, 150, -2, -1, 151, 152, 1, 708, 28003);
		setBounds(28001, 153, 158, 0, rsinterface); // Prayer glow sprite
		setBounds(28002, 153, 158, 1, rsinterface); // Prayer sprites

		addPrayer(28004, "Activate @or1@Rigour", 31, 32, 150, -3, -5, 153, 154, 1, 710, 28006);
		setBounds(28004, 84, 198, 2, rsinterface); // Prayer glow sprite
		setBounds(28005, 84, 198, 3, rsinterface); // Prayer sprites

		addPrayer(28007, "Activate @or1@Augury", 31, 32, 150, -3, -5, 155, 156, 1, 712, 28009);
		setBounds(28007, 120, 198, 4, rsinterface); // Prayer glow sprite
		setBounds(28008, 120, 198, 5, rsinterface); // Prayer sprites

		// Now we add hovers..
		addPrayerHover(28003, "Level 55\nPreserve\nBoosted stats last 20% longer.", -135, -60);
		setBounds(28003, 153, 158, 86, rsinterface); // Hover box

		addPrayerHover(28006,
				"Level 74\nRigour\nIncreases your Ranged attack\nby 20% and damage by 23%,\nand your defence by 25%",
				-70, -100);
		setBounds(28006, 84, 200, 87, rsinterface); // Hover box

		addPrayerHover(28009, "Level 77\nAugury\nIncreases your Magic attack\nby 25% and your defence by 25%", -110,
				-100);
		setBounds(28009, 120, 198, 88, rsinterface); // Hover box

	}

	public static void addPrayer(int ID, String tooltip, int w, int h, int glowSprite, int glowX, int glowY,
			int disabledSprite, int enabledSprite, int config, int configFrame, int hover) {
		Widget p = addTabInterface(ID);

		// Adding config-toggleable glow on the prayer
		// Also clickable
		p.parent = 5608;
		p.type = TYPE_SPRITE;
		p.atActionType = 1;
		p.width = w;
		p.height = h;
		p.requiredValues = new int[1];
		p.valueCompareType = new int[1];
		p.valueCompareType[0] = 1;
		p.requiredValues[0] = config;
		p.valueIndexArray = new int[1][3];
		p.valueIndexArray[0][0] = 5;
		p.valueIndexArray[0][1] = configFrame;
		p.valueIndexArray[0][2] = 0;
		p.tooltip = tooltip;
		p.defaultText = tooltip;
		p.hoverType = 52;
		p.enabledSprite =  SpriteLoader.getSprite( glowSprite);
		p.spriteXOffset = glowX;
		p.spriteYOffset = glowY;

		// Adding config-toggleable prayer sprites
		// not clickable
		p = addTabInterface(ID + 1);
		p.parent = 5608;
		p.type = TYPE_SPRITE;
		p.atActionType = 0;
		p.width = w;
		p.height = h;
		p.requiredValues = new int[1];
		p.valueCompareType = new int[1];
		p.valueCompareType[0] = 2;
		p.requiredValues[0] = 1;
		p.valueIndexArray = new int[1][3];
		p.valueIndexArray[0][0] = 5;
		p.valueIndexArray[0][1] = configFrame + 1;
		p.valueIndexArray[0][2] = 0;
		p.tooltip = tooltip;
		p.defaultText = tooltip;
		p.enabledSprite =  SpriteLoader.getSprite( disabledSprite); // imageLoader(disabledSprite,
																// "s");
		p.disabledSprite =  SpriteLoader.getSprite( enabledSprite); // imageLoader(enabledSprite,
																// "s");
		p.hoverType = hover;
	}

	public static void addPrayerHover(int ID, String hover, int xOffset, int yOffset) {
		// Adding hover box
		Widget p = addTabInterface(ID);
		p.parent = 5608;
		p.type = TYPE_OTHER;
		p.width = 40;
		p.height = 32;
		p.hoverText = p.defaultText = hover;
		p.hoverXOffset = xOffset;
		p.hoverYOffset = yOffset;
		p.regularHoverBox = true;
	}

	private static void inventory() {
		// Set proper width and height for the inventory interface (makes item dragging binding look nicer)
		Widget.interfaceCache[3213].width = 190;
		Widget.interfaceCache[3213].height = 261;
	}

	public static void fullscreenLogin() {
		Widget loginScreen = interfaceCache[15244];

		int id = 15271;
		addTransparentHoverRectangle(id, Widget.interfaceCache[15251].width, Widget.interfaceCache[15251].height, 0xffffff, true, 0, 24);
		insertNewChild(loginScreen, id, getChildX(15251), getChildY(15251));
	}

	public static void shop() {
		Widget shopContainer = interfaceCache[3900];
		shopContainer.drawInfinity = true;
		shopContainer.actions = new String[] { "Value", "Buy 1", "Buy 5", "Buy 10", "Buy 50", "Buy X" };

		Widget inventoryContainer = interfaceCache[3823];
		inventoryContainer.actions = new String[] { "Value", "Sell 1", "Sell 5", "Sell 10", "Sell 50", "Sell X" };

		Widget pointsText = interfaceCache[24126];
		pointsText.textColor = 0xff981f;
		pointsText.textShadow = true;
		pointsText.verticalOffset = 1;
	}

	/**
	 * Simple Interface for viewing Items
	 */
	public static void itemFinder(GameFont[] tda) {
		int id = 56_300;
		Widget itemFinder = addInterface(id++);

		itemFinder.totalChildren(4);
		int child = 0;

		addBackground(id, 480, 320, true, AutomaticSprite.BACKGROUND_BLACK);
		itemFinder.child(child++, id++, 16, 7);

		addCloseButton(id, true);
		itemFinder.child(child++, id++, 468, 14);

		Widget scroll = addInterface(id++);
		itemFinder.child(child++, scroll.id, 26, 47);
		scroll.width = 444;
		scroll.height = scroll.scrollMax = 260;
		scroll.totalChildren(1);

		addContainer(id, scroll.id, 0, 10, 180, 13, 10, "Spawn 1", "Spawn 5", "Spawn 10", "Spawn 100", "Spawn Max");
		interfaceCache[id].drawInfinity = true;
		scroll.child(0, id++, 0, 0);

		addText(id, "@gre@Searching for @whi@[Partyhat]", tda, 2, 0xff981f, true);
		itemFinder.child(child++, id++, 256, 17);
	}

	public static void newShop(GameFont[] tda) {
	    int id = 30900;
	    Widget shop = addInterface(id++);
	    shop.totalChildren(8);
	    int child = 0;

	    addBackground(id, 480, 320, true, AutomaticSprite.BACKGROUND_BLACK);
	    shop.child(child++, id++, 16, 7);

        addCloseButton(id, true);
        shop.child(child++, id++, 468, 14);

        addText(id, "New Shop", tda, 2, 0xff981f, true);
        shop.child(child++, id++, 256, 17);

            Widget scroll = addInterface(id++);
            shop.child(child++, scroll.id, 26, 47);
            scroll.width = 444;
            scroll.height = scroll.scrollMax = 230;
            scroll.totalChildren(1);

            addContainer(id, scroll.id, 0, 10, 180, 13, 10, interfaceCache[3900].actions);
            interfaceCache[id].drawInfinity = true;
            scroll.child(0, id++, 0, 0);

		addHoverTextConfigButton(id, 888, 887, "Search", tda, 1, 0xff981f, 0xffffff, 0, 1155);
		interfaceCache[id].textHorizontalOffset = 12;
		shop.child(child++, id++, 181, 281);

		addSprite(id, 889);
		shop.child(child++, id++, 188, 291);

        addText(id, "No matching items found.", tda, 1, 0xff981f, true);
        shop.child(child++, id++, scroll.width / 2 + 26, 75);

        //addText(id, "", tda, 0, 0xff981f);
        shop.child(child++, 24_126, 26, 18);
    }

	public static void bounty(GameFont[] TDA) {
		Widget tab = addInterface(23300);
		addTransparentSprite(23301, 97, 150);

		addConfigSprite(23303, -1, 98, 0, 876);
		// addSprite(23304, 104);
		addText(23305, "---", TDA, 0, 0xffff00, true, true);
		addText(23306, "Target:", TDA, 0, 0xffff00, true, true);
		addText(23307, "None", TDA, 1, 0xffffff, true, true);
		addText(23308, "Level: ------", TDA, 0, 0xffff00, true, true);

		addText(23309, "Current  Record", TDA, 0, 0xffff00, true, true);
		addText(23310, "0", TDA, 0, 0xffff00, true, true);
		addText(23311, "0", TDA, 0, 0xffff00, true, true);
		addText(23312, "0", TDA, 0, 0xffff00, true, true);
		addText(23313, "0", TDA, 0, 0xffff00, true, true);
		addText(23314, "Rogue:", TDA, 0, 0xffff00, true, true);
		addText(23315, "Hunter:", TDA, 0, 0xffff00, true, true);

		addConfigSprite(23316, -1, 99, 0, 877);
		addConfigSprite(23317, -1, 100, 0, 878);
		addConfigSprite(23318, -1, 101, 0, 879);
		addConfigSprite(23319, -1, 102, 0, 880);
		addConfigSprite(23320, -1, 103, 0, 881);
		addText(23321, "Level: ", TDA, 1, 0xFFFF33, true, false);

		// Kda
		addTransparentSprite(23322, 97, 150);
		addText(23323, "Targets killed: 0", TDA, 0, 0xFFFF33, true, false);
		addText(23324, "Players killed: 0", TDA, 0, 0xFFFF33, true, false);
		addText(23325, "Deaths: 0", TDA, 0, 0xFFFF33, true, false);

		tab.totalChildren(17);
		tab.child(0, 23301, 319, 1);
		tab.child(1, 23322, 319, 47);
		// tab.child(1, 23302, 339, 56);
		tab.child(2, 23303, 345, 58);
		// tab.child(2, 23304, 348, 73);
		tab.child(3, 23305, 358, 77);
		tab.child(4, 23306, 455, 51);
		tab.child(5, 23307, 456, 64);
		tab.child(6, 23308, 457, 80);
		// tab.child(8, 23309, 460, 59);
		// tab.child(9, 23310, 438, 72);
		// tab.child(10, 23311, 481, 72);
		// tab.child(11, 23312, 438, 85);
		// tab.child(12, 23313, 481, 85);
		// tab.child(13, 23314, 393, 72);
		// tab.child(14, 23315, 394, 85);
		tab.child(7, 23316, 345, 58);
		tab.child(8, 23317, 345, 58);
		tab.child(9, 23318, 345, 58);
		tab.child(10, 23319, 345, 58);
		tab.child(11, 23320, 345, 58);

		tab.child(12, 23323, 435, 6);
		tab.child(13, 23324, 435, 19);
		tab.child(14, 23325, 435, 32);

		interfaceCache[197].childX[0] = 0;
		interfaceCache[197].childY[0] = 0;

		tab.child(15, 197, 331, 6);
		tab.child(16, 23321, 361, 31);

	}

	public static void itemsKeptOnDeath(GameFont[] tda) {

		removeSomething(16999); // close button in text

		removeSomething(10494);
		removeSomething(10600);

		/*
		 * Remove old items on interface
		 */

		// Top Row
		for (int top = 17108; top <= 17111; top++) {
			removeSomething(top);
		}
		// 1st row
		for (int top = 17112; top <= 17119; top++) {
			removeSomething(top);
		}
		// 2nd row
		for (int top = 17120; top <= 17127; top++) {
			removeSomething(top);
		}
		// 3rd row
		for (int top = 17128; top <= 17135; top++) {
			removeSomething(top);
		}
		// 4th row
		for (int top = 17136; top <= 17142; top++) {
			removeSomething(top);
		}
		// 5th row
		for (int top = 17143; top <= 17148; top++) {
			removeSomething(top);
		}

		// 6th row (4 items)
		for (int top = 17149; top <= 17152; top++) {
			removeSomething(top);
		}

		Widget rsinterface = addInterface(17100);
		addBackground(17101, 498, 317, 28, true, AutomaticSprite.BACKGROUND_BROWN);
		addText(17103, "Items Kept on Death", tda, 2, 0xff981f, false, true);
		addText(17104, "Items you will keep on death if not skulled:", tda, 1, 0xff981f, false, true);
		addText(17105, "Items you will lose on death if not skulled:", tda, 1, 0xff981f, false, true);
		addText(17106, "Information:", tda, 1, 0xff981f, false, true);
		addText(17107, "~ 3 ~", tda, 1, 0xffcc33, true, true); // tilde's (~) get removed in RSFont.drawBasicString

		// Vertical divider
		addVerticalDivider(18401, 278, AutomaticSprite.DIVIDER_VERTICAL_BROWN);

		// Items background
		addTransparentRectangle(18402, 316, 270, 0, true, 56);

		// Items line divider
		addRectangle(18403, 309, 1, 0x726451, false);

		// Information line divider
		addRectangle(18404, 101, 1, 0x726451, false);

		// Max items kept on death
		addText(18405, "Max items kept on death :", tda, 1, 0xffcc33, true, true);

		// Value of lost items
		addText(18406, "Value of lost items: ", tda, 0, 0xff981f, true, true);
		addText(18407, "0 gp", tda, 0, 0xff981f, true, true);

		// Info text
		addText(18408, "The normal amount of items", tda, 0, 0xff981f);
		addText(18409, "kept is three.", tda, 0, 0xff981f);

		addText(18410, "You have no factors affecting", tda, 0, 0xff981f);
		addText(18411, "the items you keep.", tda, 0, 0xff981f);
		addText(18412, "", tda, 0, 0xff981f);

		addText(18413, "", tda, 0, 0xff981f);
		addText(18414, "", tda, 0, 0xff981f);
		addText(18415, "", tda, 0, 0xff981f);
		addText(18416, "", tda, 0, 0xff981f);

		// Items kept (separate container for each item to allow for sending stackable items singularly)
		String[] options = { null };
		addItemOnInterface(17108, 17100, options);
		addItemOnInterface(17109, 17100, options);
		addItemOnInterface(17110, 17100, options);
		addItemOnInterface(17111, 17100, options);
		addItemOnInterface(17112, 17100, options);
		addItemOnInterface(17113, 17100, options);
		addItemOnInterface(17114, 17100, options);
		addItemOnInterface(17115, 17100, options);

		// Items lost container
		addContainer(17116, 0, 8, 5, 6, 6);

		setChildren(35, rsinterface);
		int child_index = 0;
		setBounds(17101, 8, 9, child_index++, rsinterface);
		setBounds(43702, 481, 18, child_index++, rsinterface); // Close button
		setBounds(18401, 338, 42, child_index++, rsinterface);
		setBounds(18402, 19, 46, child_index++, rsinterface);
		setBounds(18403, 21, 64, child_index++, rsinterface);
		setBounds(18403, 21, 124, child_index++, rsinterface);
		setBounds(18404, 347, 64, child_index++, rsinterface);
		setBounds(16999, 478, 14, child_index++, rsinterface);
		setBounds(17103, 186, 19, child_index++, rsinterface);
		setBounds(17104, 24, 50, child_index++, rsinterface);
		setBounds(17105, 24, 110, child_index++, rsinterface);
		setBounds(17106, 348, 47, child_index++, rsinterface);

		setBounds(18408, 348, 66, child_index++, rsinterface);
		setBounds(18409, 348, 78, child_index++, rsinterface);

		setBounds(18410, 348, 102, child_index++, rsinterface);
		setBounds(18411, 348, 114, child_index++, rsinterface);
		setBounds(18412, 348, 126, child_index++, rsinterface);

		setBounds(18413, 348, 150, child_index++, rsinterface);
		setBounds(18414, 348, 162, child_index++, rsinterface);
		setBounds(18415, 348, 174, child_index++, rsinterface);
		setBounds(18416, 348, 186, child_index++, rsinterface);


		setBounds(18405, 422, 277, child_index++, rsinterface);
		setBounds(17107, 422, 301, child_index++, rsinterface);
		setBounds(17149, 23, 132, child_index++, rsinterface);

		setBounds(18406, 421, 249, child_index++, rsinterface);
		setBounds(18407, 421, 264, child_index++, rsinterface);

		setBounds(17108, 24, 72, child_index++, rsinterface);
		setBounds(17109, 64, 72, child_index++, rsinterface);
		setBounds(17110, 104, 72, child_index++, rsinterface);
		setBounds(17111, 144, 72, child_index++, rsinterface);
		setBounds(17112, 184, 72, child_index++, rsinterface);
		setBounds(17113, 224, 72, child_index++, rsinterface);
		setBounds(17114, 264, 72, child_index++, rsinterface);
		setBounds(17115, 304, 72, child_index++, rsinterface);

		setBounds(17116, 24, 132, child_index++, rsinterface);
	}

	public static void itemsOnDeathDATA(GameFont[] tda) {
		Widget RSinterface = addInterface(17315);
		addText(17309, "", 0xff981f, false, false, 0, tda, 0);
		addText(17310, "The normal amount of", 0xff981f, false, false, 0, tda, 0);
		addText(17311, "items kept is three.", 0xff981f, false, false, 0, tda, 0);
		addText(17312, "", 0xff981f, false, false, 0, tda, 0);
		addText(17313, "If you are skulled,", 0xff981f, false, false, 0, tda, 0);
		addText(17314, "you will lose all your", 0xff981f, false, false, 0, tda, 0);
		addText(17317, "items, unless an item", 0xff981f, false, false, 0, tda, 0);
		addText(17318, "protecting prayer is", 0xff981f, false, false, 0, tda, 0);
		addText(17319, "used.", 0xff981f, false, false, 0, tda, 0);
		addText(17320, "", 0xff981f, false, false, 0, tda, 0);
		addText(17321, "Item protecting prayers", 0xff981f, false, false, 0, tda, 0);
		addText(17322, "will allow you to keep", 0xff981f, false, false, 0, tda, 0);
		addText(17323, "one extra item.", 0xff981f, false, false, 0, tda, 0);
		addText(17324, "", 0xff981f, false, false, 0, tda, 0);
		addText(17325, "The items kept are", 0xff981f, false, false, 0, tda, 0);
		addText(17326, "selected by the server", 0xff981f, false, false, 0, tda, 0);
		addText(17327, "and include the most", 0xff981f, false, false, 0, tda, 0);
		addText(17328, "expensive items you're", 0xff981f, false, false, 0, tda, 0);
		addText(17329, "carrying.", 0xff981f, false, false, 0, tda, 0);
		addText(17330, "", 0xff981f, false, false, 0, tda, 0);
		RSinterface.parent = 17315;
		RSinterface.id = 17315;
		RSinterface.type = 0;
		RSinterface.atActionType = 0;
		RSinterface.contentType = 0;
		RSinterface.width = 130;
		RSinterface.height = 197;
		RSinterface.opacity = 0;
		RSinterface.hoverType = -1;
		RSinterface.scrollMax = 280;
		RSinterface.children = new int[20];
		RSinterface.childX = new int[20];
		RSinterface.childY = new int[20];
		RSinterface.children[0] = 17309;
		RSinterface.childX[0] = 0;
		RSinterface.childY[0] = 0;
		RSinterface.children[1] = 17310;
		RSinterface.childX[1] = 0;
		RSinterface.childY[1] = 12;
		RSinterface.children[2] = 17311;
		RSinterface.childX[2] = 0;
		RSinterface.childY[2] = 24;
		RSinterface.children[3] = 17312;
		RSinterface.childX[3] = 0;
		RSinterface.childY[3] = 36;
		RSinterface.children[4] = 17313;
		RSinterface.childX[4] = 0;
		RSinterface.childY[4] = 48;
		RSinterface.children[5] = 17314;
		RSinterface.childX[5] = 0;
		RSinterface.childY[5] = 60;
		RSinterface.children[6] = 17317;
		RSinterface.childX[6] = 0;
		RSinterface.childY[6] = 72;
		RSinterface.children[7] = 17318;
		RSinterface.childX[7] = 0;
		RSinterface.childY[7] = 84;
		RSinterface.children[8] = 17319;
		RSinterface.childX[8] = 0;
		RSinterface.childY[8] = 96;
		RSinterface.children[9] = 17320;
		RSinterface.childX[9] = 0;
		RSinterface.childY[9] = 108;
		RSinterface.children[10] = 17321;
		RSinterface.childX[10] = 0;
		RSinterface.childY[10] = 120;
		RSinterface.children[11] = 17322;
		RSinterface.childX[11] = 0;
		RSinterface.childY[11] = 132;
		RSinterface.children[12] = 17323;
		RSinterface.childX[12] = 0;
		RSinterface.childY[12] = 144;
		RSinterface.children[13] = 17324;
		RSinterface.childX[13] = 0;
		RSinterface.childY[13] = 156;
		RSinterface.children[14] = 17325;
		RSinterface.childX[14] = 0;
		RSinterface.childY[14] = 168;
		RSinterface.children[15] = 17326;
		RSinterface.childX[15] = 0;
		RSinterface.childY[15] = 180;
		RSinterface.children[16] = 17327;
		RSinterface.childX[16] = 0;
		RSinterface.childY[16] = 192;
		RSinterface.children[17] = 17328;
		RSinterface.childX[17] = 0;
		RSinterface.childY[17] = 204;
		RSinterface.children[18] = 17329;
		RSinterface.childX[18] = 0;
		RSinterface.childY[18] = 216;
		RSinterface.children[19] = 17330;
		RSinterface.childX[19] = 0;
		RSinterface.childY[19] = 228;
	}

	public static void clanChatTab(GameFont[] tda) {
		Widget tab = addTabInterface(CLAN_CHAT_PARENT_WIDGET_ID);

		addHoverButton(37129, 72, 32, AutomaticSprite.BUTTON_BROWN, AutomaticSprite.BUTTON_BROWN_HOVER, "Select");
		addHoverButton(37132, 72, 32, AutomaticSprite.BUTTON_BROWN, AutomaticSprite.BUTTON_BROWN_HOVER, "Select");

		addConfigButton(37142, CLAN_CHAT_PARENT_WIDGET_ID, 945, 946, 24, 22, "Toggle lootshare", 1, Widget.OPTION_TOGGLE_SETTING, 542);
		//addButton(37142, 0, "/Clan Chat/Lootshare", 32, 32, "Toggle lootshare", Widget.OPTION_TOGGLE_SETTING);
		addText(37135, "Join Chat", tda, 0, 0xff981f, true, true);
		addText(37136, "Clan Setup", tda, 0, 0xff981f, true, true);

		addSpriteLoader(37137, 681);

		addText(37138, "Clan Chat", tda, 2, 0xff981f, true, true);
		addText(37139, "Talking in: Not in chat", tda, 0, 0xff981f, false, true);
		addText(37140, "Owner: None", tda, 0, 0xff981f, false, true);

		addTransparentRectangle(37141, 174, 158, 0, true, 56);

		tab.totalChildren(11);
		tab.child(0, 16126, 0, 221);
		tab.child(1, 16126, 0, 59);

		tab.child(0, 37137, 2, 55);
		tab.child(1, 37141, 8, 61);
		tab.child(2, 37143, 8, 61);
		tab.child(3, 37129, 18, 227);
		tab.child(4, 37132, 100, 227);
		tab.child(5, 37135, 54, 237);
		tab.child(6, 37136, 136, 237);
		tab.child(7, 37138, 95, 4);
		tab.child(8, 37139, 11, 25);
		tab.child(9, 37140, 26, 40);
		tab.child(10, 37142, 145, 28);
		/* Text area */
		Widget list = addTabInterface(37143);
		list.height = list.scrollMax = 158;
		list.width = 158;
		list.totalChildren(400);
		int child = 0;
		for (int i = 0; i < CLAN_CHAT_MAX_MEMBERS; i++) {
			addText(37144 + i, "", tda, 0, 0xffffff, false, false);
			interfaceCache[37144 + i].actions = new String[] { "Kick", "Demote", "Ban",
					"Recruit", "Corporal", "Sergeant", "Lieutenant", "Captain", "General" };
			interfaceCache[37144 + i].width = list.width;
			interfaceCache[37144 + i].parent = CLAN_CHAT_PARENT_WIDGET_ID;
			list.child(child++, 37144 + i, 2, i * 14 + 3);

			addText(37344 + i, "", tda, 0, 0xffffff, false, false);
			list.child(child++, 37344 + i, 12, i * 14 + 1);
		}
	}

	/**
	 * The clan chat setup interface
	 *
	 * @param tda
	 */
	public static void clanChatSetup(GameFont[] tda) {
		Widget rsi = addInterface(43700);
		rsi.totalChildren(14 + 14);
		int count = 0;
		/* Background */
		addBackground(43701, 480, 305, true, AutomaticSprite.BACKGROUND_BROWN);
		rsi.child(count++, 43701, 16, 14);
		/* Divider */
		addVerticalDivider(43931, 265, AutomaticSprite.DIVIDER_VERTICAL_BROWN);
		rsi.child(count++, 43931, 180, 48);
		/* Close button */
		addCloseButton(43702, false);
		rsi.child(count++, 43702, 470, 24);
		/* Clan Setup title */
		addText(43703, "Clan Chat Setup", tda, 2, 0xff981f, true, true);
		rsi.child(count++, 43703, 256, 23);
		/* Setup buttons */
		String[] titles = { "Clan Name:", "Who can enter chat?", "Who can talk on chat?", "Who can kick on chat?", "Who can ban on chat?" };
		String[] defaults = { "N/A", "Anyone", "Anyone", "Anyone", "Anyone" };
		String[] whoCan = { "Anyone", "Any friends", "Recruit+", "Corporal+", "Sergeant+", "Lieutenant+", "Captain+", "General+", "Only me" };
		for (int index = 0, id = 43704, y = 53; index < titles.length; index++, id += 3, y += 39) {
			addButton(id, 799, "");
			interfaceCache[id].atActionType = 0;
			if (index > 0) {
				interfaceCache[id].actions = whoCan;
			} else {
				interfaceCache[id].actions = new String[] { "Change title", "Delete clan" };
			}
			interfaceCache[id].type = TYPE_HOVER_SPRITE;
			addText(id + 1, titles[index], tda, 0, 0xFF981F, true, true);
			addText(id + 2, defaults[index], tda, 2, 0xFFFFFF, true, true);
			rsi.child(count++, id, 26, y);
			rsi.child(count++, id + 1, 101, y + 4);
			rsi.child(count++, id + 2, 101, y + 17);
		}
		/* Table */
		addSprite(43719, 805);
		rsi.child(count++, 43719, 198, 66);
		/* Labels */
		int id = 43720;
		int y = 70;
		addText(id, "Ranked Members", tda, 2, 0xFF981F, false, true);
		rsi.child(count++, id++, 203, y);
		addText(id, "Banned Members", tda, 2, 0xFF981F, false, true);
		rsi.child(count++, id++, 340, y);
		/* Ranked members list */
		//System.out.println("Ranked members list: " + id);
		Widget list = addInterface(id++);
		int lines = 100;
		list.totalChildren(lines);
		String[] ranks = { "Demote", "Ban", "Recruit", "Corporal", "Sergeant", "Lieutenant", "Captain", "General" };
		list.childY[0] = 2;
		for (int index = id; index < id + lines; index++) {
			addText(index, "", tda, 1, 0xffffff, false, true);
			interfaceCache[index].actions = ranks;
			interfaceCache[index].width = 100;
			list.children[index - id] = index;
			list.childX[index - id] = 2;
			list.childY[index - id] = (index - id > 0 ? list.childY[index - id - 1] + 14 : 0);
		}
		id += lines;
		list.width = 119;
		list.height = list.scrollMax = 210;
		//list.scrollMax = (lines * 14) + 2;
		rsi.child(count++, list.id, 200, 88);
		/* Banned members list */
		list = addInterface(id++);
		list.totalChildren(lines);
		list.childY[0] = 2;
		for (int index = id; index < id + lines; index++) {
			addText(index, "", tda, 1, 0xffffff, false, true);
			interfaceCache[index].actions = new String[] { "Unban" };
			interfaceCache[index].width = 100;
			list.children[index - id] = index;
			list.childX[index - id] = 0;
			list.childY[index - id] = (index - id > 0 ? list.childY[index - id - 1] + 14 : 0);
		}
		id += lines + 1;
		list.width = 119;
		list.height = list.scrollMax = 210;
		//list.scrollMax = (lines * 14) + 2;
		rsi.child(count++, list.id, 340, 88);
		/* Table info text */
		y = 43;
		/*addText(id, "You can manage both ranked and banned members here.", tda, 0, 0xFF981F, true, true);
		rsi.child(count++, id++, 338, y);*/
		id++;
		addText(id, "Right click on a name to edit the member.", tda, 0, 0xFF981F, true, true);
		rsi.child(count++, id++, 338, y + 9);
		/* Add ranked member button */
		y = 71;
		addButton(id, 802, "Add ranked member");
		interfaceCache[id].type = TYPE_HOVER_SPRITE;
		interfaceCache[id].hoverSprite1 =  SpriteLoader.getSprite( 803);
		interfaceCache[id].atActionType = 5;
		rsi.child(count++, id++, 320, y);
		/* Add banned member button */
		addButton(id, 802, "Add banned member");
		interfaceCache[id].type = TYPE_HOVER_SPRITE;
		interfaceCache[id].hoverSprite1 =  SpriteLoader.getSprite( 803);
		interfaceCache[id].atActionType = 5;
		rsi.child(count++, id++, 460, y);

		/* White text */
		addText(id, "Right-click on white\\ntext to change\\noptions.", tda, 2, 0xffff9d, true);
		rsi.child(count++, id++, 98, 261);

		/* Hovers */
		int[] clanSetup = { 43702, 43704, 43707, 43710, 43713, 43716, 43826, 43827 };
		String[] names = { "close", "sprite", "sprite", "sprite", "sprite", "sprite", "plus", "plus" };
		int[] ids = { 1, 3, 3, 3, 3, 3, 1, 1 };
		for (int index = 0; index < clanSetup.length; index++) {
			rsi = interfaceCache[clanSetup[index]];

			if (index != 0) {
				rsi.hoverSprite1 =  SpriteLoader.getSprite( 800);
			}
		}

	}

	public static void addHoverText2(int id, String text, String[] tooltips, GameFont[] tda, int idx, int color,
									 boolean center, boolean textShadowed, int width) {
		Widget rsinterface = addInterface(id);
		rsinterface.id = id;
		rsinterface.parent = id;
		rsinterface.type = 4;
		rsinterface.atActionType = 1;
		rsinterface.width = width;
		rsinterface.height = 11;
		rsinterface.contentType = 0;
		rsinterface.opacity = 0;
		rsinterface.hoverType = -1;
		rsinterface.centerText = center;
		rsinterface.textShadow = textShadowed;
		rsinterface.textDrawingAreas = tda[idx];
		rsinterface.defaultText = text;
		rsinterface.secondaryText = "";
		rsinterface.textColor = color;
		rsinterface.secondaryColor = 0;
		rsinterface.defaultHoverColor = 0xffffff;
		rsinterface.secondaryHoverColor = 0;
		rsinterface.tooltips = tooltips;
	}

	public static void addText2(int id, String text, GameFont[] tda, int idx, int color, boolean center,
								boolean shadow) {
		Widget tab = addInterface(id);
		tab.parent = id;
		tab.id = id;
		tab.type = 4;
		tab.atActionType = 0;
		tab.width = 0;
		tab.height = 11;
		tab.contentType = 0;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.centerText = center;
		tab.textShadow = shadow;
		tab.textDrawingAreas = tda[idx];
		tab.defaultText = text;
		tab.secondaryText = "";
		tab.textColor = color;
		tab.secondaryColor = 0;
		tab.defaultHoverColor = 0;
		tab.secondaryHoverColor = 0;
	}

	public static void addAdvancedSprite(int id, int spriteId) {
		Widget widget = addInterface(id);
		widget.id = id;
		widget.parent = id;
		widget.type = 5;
		widget.atActionType = 0;
		widget.contentType = 0;
		widget.hoverType = 52;
		widget.enabledSprite =  SpriteLoader.getSprite( spriteId);
		widget.disabledSprite =  SpriteLoader.getSprite( spriteId);
		widget.drawsAdvanced = true;
		widget.width = 512;
		widget.height = 334;
	}

	public static void addConfigSprite(int id, int spriteId, int spriteId2, int state, int config) {
		Widget widget = addInterface(id);
		widget.id = id;
		widget.parent = id;
		widget.type = 5;
		widget.atActionType = 0;
		widget.contentType = 0;
		widget.width = 512;
		widget.height = 334;
		widget.opacity = 0;
		widget.hoverType = -1;
		widget.valueCompareType = new int[1];
		widget.requiredValues = new int[1];
		widget.valueCompareType[0] = 1;
		widget.requiredValues[0] = state;
		widget.valueIndexArray = new int[1][3];
		widget.valueIndexArray[0][0] = 5;
		widget.valueIndexArray[0][1] = config;
		widget.valueIndexArray[0][2] = 0;
		widget.enabledSprite = spriteId < 0 ? null :  SpriteLoader.getSprite( spriteId);
		widget.disabledSprite = spriteId2 < 0 ? null :  SpriteLoader.getSprite( spriteId2);
	}

	public static Widget addSprite(int id, int spriteId) {
		Widget rsint = interfaceCache[id] = new Widget();
		rsint.id = id;
		rsint.parent = id;
		rsint.type = 5;
		rsint.atActionType = 0;
		rsint.contentType = 0;
		rsint.opacity = 0;
		rsint.hoverType = 0;

		if (spriteId != -1) {
			rsint.disabledSprite =  SpriteLoader.getSprite( spriteId);
			rsint.enabledSprite =  SpriteLoader.getSprite( spriteId);
		}

		rsint.width = 0;
		rsint.height = 0;

		return rsint;
	}

	public static void addSprite(int id, Sprite sprite) {
		Widget rsint = interfaceCache[id] = new Widget();
		rsint.id = id;
		rsint.parent = id;
		rsint.type = 5;
		rsint.atActionType = 0;
		rsint.contentType = 0;
		rsint.opacity = 0;
		rsint.hoverType = 0;
		rsint.disabledSprite = sprite;
		rsint.enabledSprite = sprite;
		rsint.width = 0;
		rsint.height = 0;
	}

	public int percentageCompleted;
	public int percentageTotal;
	public int percentageMultiplier;
	public int percentageSpriteEmpty;
	public int percentageSpriteFull;
	public int percentageBarStart;
	public int percentageBarEnd;
	public int percentageDimension;

	public static Widget addPercentageBar(int id, int percentageDimension, int percentageBarStart, int percentageBarEnd,
			int percentageSpritePart, int percentageSpriteEmpty, int percentageTotal, int percentageMultiplier) {
		Widget rs = interfaceCache[id] = new Widget();
		rs.id = id;
		rs.parent = id;
		rs.type = 20;
		rs.atActionType = 0;
		rs.contentType = 0;
		rs.opacity = 0;
		rs.hoverType = 0;
		rs.percentageCompleted = 0;
		rs.percentageDimension = percentageDimension;
		rs.percentageBarStart = percentageBarStart;
		rs.percentageBarEnd = percentageBarEnd;
		rs.percentageSpriteFull = percentageSpritePart;
		rs.percentageSpriteEmpty = percentageSpriteEmpty;
		rs.percentageTotal = percentageTotal;
		rs.percentageMultiplier = percentageMultiplier;
		rs.width = 0;
		rs.height = 0;
		return rs;
	}

	public static void addText(int id, String text, GameFont[] wid, int idx, int color) {
		Widget rsinterface = addInterface(id);
		rsinterface.id = id;
		rsinterface.parent = id;
		rsinterface.type = 4;
		rsinterface.atActionType = 0;
		rsinterface.width = 174;
		rsinterface.height = 11;
		rsinterface.contentType = 0;
		rsinterface.opacity = 0;
		rsinterface.centerText = false;
		rsinterface.textShadow = true;
		rsinterface.textDrawingAreas = wid[idx];
		rsinterface.defaultText = text;
		rsinterface.secondaryText = "";
		rsinterface.textColor = color;
		rsinterface.secondaryColor = 0;
		rsinterface.defaultHoverColor = 0;
		rsinterface.secondaryHoverColor = 0;
	}

	public static void equipmentTab(GameFont[] wid) {
		Widget tab = addTabInterface(1644);
		tab.totalChildren(9);
		int child = 0;

			Widget equipment = addInterface(27649);
			tab.child(child++, equipment.id, 21, 4);
			equipment.totalChildren(27);
			int equipChild = 0;
			equipment.child(equipChild++, 1645, 56, 35);
			equipment.child(equipChild++, 1646, 56, 59);
			equipment.child(equipChild++, 1647, 56, 89);
			equipment.child(equipChild++, 1648, 56, 143);
			equipment.child(equipChild++, 1649, 0, 114);
			equipment.child(equipChild++, 1650, 0, 150);
			equipment.child(equipChild++, 1651, 112, 114);
			equipment.child(equipChild++, 1652, 112, 150);
			equipment.child(equipChild++, 1653, 35, 77);
			equipment.child(equipChild++, 1654, 89, 77);
			equipment.child(equipChild++, 1655, 32, 38);
			equipment.child(equipChild++, 1656, 86, 38);
			equipment.child(equipChild++, 1657, 56, 0);
			equipment.child(equipChild++, 1658, 15, 39);
			equipment.child(equipChild++, 1659, 56, 39);
			equipment.child(equipChild++, 1660, 97, 39);
			equipment.child(equipChild++, 1661, 0, 78);
			equipment.child(equipChild++, 1662, 56, 78);
			equipment.child(equipChild++, 1663, 112, 78);
			equipment.child(equipChild++, 1664, 56, 118);
			equipment.child(equipChild++, 1665, 56, 158);
			equipment.child(equipChild++, 1666, 0, 158);
			equipment.child(equipChild++, 1667, 112, 158);

			//Newer slots
			equipment.child(equipChild++, 65122, 112, 0);
			equipment.child(equipChild++, 65123, 0, 0);
			equipment.child(equipChild++, 65121, 112, 118);

			//Container child
			equipment.child(equipChild++, 1688, 2, 40);

		addHoverButton(27651, 40, 40, AutomaticSprite.BUTTON_BROWN, AutomaticSprite.BUTTON_BROWN_HOVER, "View equipment stats");
		tab.child(child++, 27651, 7, 208);

		addHoverButton(27652, 40, 40, AutomaticSprite.BUTTON_BROWN, AutomaticSprite.BUTTON_BROWN_HOVER, "View guide prices");
		tab.child(child++, 27652, 52, 208);

		addHoverButton(27653, 40, 40, AutomaticSprite.BUTTON_BROWN, AutomaticSprite.BUTTON_BROWN_HOVER, "View items kept on death");
		tab.child(child++, 27653, 97, 208);

		addHoverButton(27654, 40, 40, AutomaticSprite.BUTTON_BROWN, AutomaticSprite.BUTTON_BROWN_HOVER, "Call follower");
		tab.child(child++, 27654, 142, 208);

		addSprite(27660, 146);
		tab.child(child++, 27660, 12, 212);

		addSprite(27661, 147);
		tab.child(child++, 27661, 59, 213);

		addSprite(27662, 148);
		tab.child(child++, 27662, 101, 213);

		addSprite(27663, 149);
		tab.child(child++, 27663, 147, 216);
	}

	public static void removeConfig(int id) {
		@SuppressWarnings("unused")
		Widget rsi = interfaceCache[id] = new Widget();
	}

	public static void equipmentScreen(GameFont[] wid) {
		Widget tab = addInterface(15106);
		tab.totalChildren(24);
		int child = 0;
		addBackground(15107, 500, 324, false, AutomaticSprite.BACKGROUND_BLACK);

		addCloseButton(15210, true);

		addText(15111, "Equip Your Character...", wid, 2, 0xff981f, false, true);
		addText(15112, "Attack bonus", wid, 2, 0xff981f, false, true);
		addText(15113, "Defence bonus", wid, 2, 0xff981f, false, true);
		addText(15114, "Other bonuses", wid, 2, 0xff981f, false, true);

		addText(15115, "Melee: ---", wid, 1, 0xff981f, false, true);
		addText(15116, "Ranged: ---", wid, 1, 0xff981f, false, true);
		addText(15117, "Magic: ---", wid, 1, 0xff981f, false, true);

		addText(15118, "Max hits", wid, 2, 0xff981f, false, true);

		for (int i = 1675; i <= 1684; i++) {
			textSize(i, wid, 1);
		}
		textSize(1686, wid, 1);
		textSize(1687, wid, 1);
		addChar(15125);

		// Background
		tab.child(child++, 15107, 6, 5);
		// Close button
		tab.child(child++, 15210, 478, 12);
		// Equip Your Character...
		tab.child(child++, 15111, 17, 15);
		// Attack bonuses
		int y = 41;
		for (int i = 1675; i <= 1679; i++) {
			tab.child(child++, i, 359, y);
			y += 13;
		}
		// Defence bonuses
		tab.child(child++, 1680, 359, 126);
		tab.child(child++, 1681, 359, 139);
		tab.child(child++, 1682, 359, 152);
		tab.child(child++, 1683, 359, 165);
		tab.child(child++, 1684, 359, 178);

		// Other bonuses
		tab.child(child++, 1686, 359, 211);
		tab.child(child++, 1687, 359, 224);

		// Character
		tab.child(child++, 15125, 195, 207);

		// Titles
		tab.child(child++, 15112, 351, 27);
		tab.child(child++, 15113, 351, 112);
		tab.child(child++, 15114, 351, 197);
		tab.child(child++, 15118, 351, 243);

		// Equipment
		tab.child(child++, 27649, 21, 67);

		// Max hits
		tab.child(child++, 15115, 359, 257);
		tab.child(child++, 15116, 359, 270);
		tab.child(child++, 15117, 359, 283);

		for (int i = 1675; i <= 1684; i++) {
			Widget rsi = interfaceCache[i];
			rsi.textColor = 0xff981f;
			rsi.centerText = false;
		}
		for (int i = 1686; i <= 1687; i++) {
			Widget rsi = interfaceCache[i];
			rsi.textColor = 0xff981f;
			rsi.centerText = false;
		}
	}

	public static void addChar(int ID) {
		Widget t = interfaceCache[ID] = new Widget();
		t.id = ID;
		t.parent = ID;
		t.type = 6;
		t.atActionType = 0;
		t.contentType = 328;
		t.width = 136;
		t.height = 168;
		t.opacity = 0;
		t.modelZoom = 560;
		t.modelXAngle = 150;
		t.modelYAngle = 0;
		t.defaultAnimationId = -1;
		t.secondaryAnimationId = -1;
	}

	public static void addPet(int ID) {
		Widget petCanvas = interfaceCache[ID] = new Widget();
		petCanvas.id = ID;
		petCanvas.parent = ID;
		petCanvas.type = 6;
		petCanvas.atActionType = 0;
		petCanvas.contentType = 3291;
		petCanvas.width = 136;
		petCanvas.height = 168;
		petCanvas.opacity = 0;
		petCanvas.hoverType = 0;
		petCanvas.modelZoom = 3000;
		petCanvas.modelXAngle = 150;
		petCanvas.modelYAngle = 0;
		petCanvas.defaultAnimationId = -1;
		petCanvas.secondaryAnimationId = -1;
	}

	public static void addButton(int id, int sid, String tooltip) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = id;
		tab.type = 5;
		tab.atActionType = 1;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.disabledSprite =  SpriteLoader.getSprite( sid);// imageLoader(sid,
														// spriteName);
		tab.enabledSprite =  SpriteLoader.getSprite( sid);// imageLoader(sid,
													// spriteName);
		tab.width = tab.disabledSprite.myWidth;
		tab.height = tab.enabledSprite.myHeight;
		tab.tooltip = tooltip;
	}

	public static void addTooltipBox(int id, String text) {
		Widget rsi = addInterface(id);
		rsi.id = id;
		rsi.parent = id;
		rsi.type = 8;
		rsi.defaultText = text;
	}

	public static void addTooltip(int id, String text) {
		Widget rsi = addInterface(id);
		rsi.id = id;
		rsi.type = 0;
		rsi.invisible = true;
		rsi.hoverType = -1;
		addTooltipBox(id + 1, text);
		rsi.totalChildren(1);
		rsi.child(0, id + 1, 0, 0);
	}

	public static Widget addInterface(int id) {

		//if(interfaceCache[id] != null)
			//System.err.println("Overriding interface["+id+"]!");

		Widget rsi = interfaceCache[id] = new Widget();
		rsi.id = id;
		rsi.parent = id;
		rsi.width = 512;
		rsi.height = 334;
		return rsi;
	}
	public static Widget addInterface(int id, int width, int height) {

		//if(interfaceCache[id] != null)
		//System.err.println("Overriding interface["+id+"]!");

		Widget rsi = interfaceCache[id] = new Widget();
		rsi.id = id;
		rsi.parent = id;
		rsi.width = width;
		rsi.height = height;
		return rsi;
	}
	/**
	 * Adds a wrapper for the interface.
	 *
	 * @param wrapperId
	 *            The wrapper id.
	 * @param width
	 *            The width.
	 * @param height
	 *            The height.
	 * @param interfaceId
	 *            The interface id.
	 * @return The interface.
	 */
	public static Widget addWrapper(int wrapperId, int width, int height, int interfaceId) {
		Widget wrapper = addInterface(wrapperId);
		wrapper.width = width;
		wrapper.height = height;
		wrapper.totalChildren(1);

		setBounds(interfaceId, 0, 0, 0, wrapper);

		Widget rsi = addInterface(interfaceId);
		wrapper.parent = wrapperId;

		return rsi;
	}
	public static Widget addText(int id, String text, int idx, int color, boolean center,
								 boolean shadow) {
		Widget tab = addTabInterface(id);
		tab.parent = id;
		tab.id = id;
		tab.type = 4;
		tab.atActionType = 0;
		tab.width = 0;
		tab.height = 11;
		tab.contentType = 0;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.centerText = center;
		tab.textShadow = shadow;
		tab.textDrawingAreas = defaultFont[idx];
		tab.defaultText = text;
		tab.secondaryText = "";
		tab.textColor = color;
		tab.secondaryColor = 0;
		tab.defaultHoverColor = 0;
		tab.secondaryHoverColor = 0;
		return tab;
	}
	public static void addText(int id, String text, GameFont[] tda, int idx, int color, boolean centered) {
		Widget rsi = interfaceCache[id] = new Widget();
		if (centered)
			rsi.centerText = true;
		rsi.textShadow = true;
		rsi.textDrawingAreas = tda[idx];
		rsi.defaultText = text;
		rsi.textColor = color;
		rsi.id = id;
		rsi.type = 4;
	}

	public static void textColor(int id, int color) {
		Widget rsi = interfaceCache[id];
		rsi.textColor = color;
	}

	public static void textSize(int id, GameFont[] tda, int idx) {
		Widget rsi = interfaceCache[id];
		rsi.textDrawingAreas = tda[idx];
	}

	public static void addCacheSprite(int id, int sprite1, int sprite2, String sprites) {
		Widget rsi = interfaceCache[id] = new Widget();
		rsi.disabledSprite = getSprite(sprite1, interfaceLoader, sprites);
		rsi.enabledSprite = getSprite(sprite2, interfaceLoader, sprites);
		rsi.parent = id;
		rsi.id = id;
		rsi.type = 5;
	}

	public static void sprite1(int id, int sprite) {
		Widget class9 = interfaceCache[id];
		class9.disabledSprite =  SpriteLoader.getSprite( sprite);
	}

	public static void addActionButton(int id, int sprite, int sprite2, int width, int height, String s) {
		Widget rsi = interfaceCache[id] = new Widget();
		rsi.disabledSprite =  SpriteLoader.getSprite( sprite);
		if (sprite2 == sprite)
			rsi.enabledSprite =  SpriteLoader.getSprite( sprite);
		else
			rsi.enabledSprite =  SpriteLoader.getSprite( sprite2);
		rsi.tooltip = s;
		rsi.contentType = 0;
		rsi.atActionType = 1;
		rsi.width = width;
		rsi.hoverType = 52;
		rsi.parent = id;
		rsi.id = id;
		rsi.type = 5;
		rsi.height = height;
	}

	public static void addToggleButton(int id, int sprite, int setconfig, int width, int height, String s) {
		Widget rsi = addInterface(id);
		rsi.disabledSprite =  SpriteLoader.getSprite( sprite);
		rsi.enabledSprite =  SpriteLoader.getSprite( sprite);
		rsi.requiredValues = new int[1];
		rsi.requiredValues[0] = 1;
		rsi.valueCompareType = new int[1];
		rsi.valueCompareType[0] = 1;
		rsi.valueIndexArray = new int[1][3];
		rsi.valueIndexArray[0][0] = 5;
		rsi.valueIndexArray[0][1] = setconfig;
		rsi.valueIndexArray[0][2] = 0;
		rsi.atActionType = 4;
		rsi.width = width;
		rsi.hoverType = -1;
		rsi.parent = id;
		rsi.id = id;
		rsi.type = 5;
		rsi.height = height;
		rsi.tooltip = s;
	}

	public static void removeSomething(int id) {
		@SuppressWarnings("unused")
		Widget rsi = interfaceCache[id] = new Widget();
	}

	public static void quickPrayers(GameFont[] TDA) {
		int frame = 0;
		Widget tab = addTabInterface(17200);

		addTransparentSprite(17235, 131, 50);
		addSpriteLoader(17201, 132);
		addText(17231, "Select your quick prayers below.", TDA, 0, 0xFF981F, false, true);

		int child = 17202;
		int config = 620;
		for (int i = 0; i < 29; i++) {
			addConfigButton(child++, 17200, 134, 133, 14, 15, "Select", 0, 1, config++);
		}

		addHoverButton(17232, 135, 190, 24, "Confirm Selection", -1, 17233, 1);
		addHoveredButton(17233, 136, 190, 24, 17234);

		setChildren(64, tab);//
		setBounds(5632, 5, 8 + 20, frame++, tab);
		setBounds(5633, 44, 8 + 20, frame++, tab);
		setBounds(5634, 79, 11 + 20, frame++, tab);
		setBounds(19813, 116, 10 + 20, frame++, tab);
		setBounds(19815, 153, 9 + 20, frame++, tab);
		setBounds(5635, 5, 48 + 20, frame++, tab);
		setBounds(5636, 44, 47 + 20, frame++, tab);
		setBounds(5637, 79, 49 + 20, frame++, tab);
		setBounds(5638, 116, 50 + 20, frame++, tab);
		setBounds(5639, 154, 50 + 20, frame++, tab);
		setBounds(5640, 4, 84 + 20, frame++, tab);
		setBounds(19817, 44, 87 + 20, frame++, tab);
		setBounds(19820, 81, 85 + 20, frame++, tab);
		setBounds(5641, 117, 85 + 20, frame++, tab);
		setBounds(5642, 156, 87 + 20, frame++, tab);
		setBounds(5643, 5, 125 + 20, frame++, tab);
		setBounds(5644, 43, 124 + 20, frame++, tab);
		setBounds(13984, 83, 124 + 20, frame++, tab);
		setBounds(5645, 115, 121 + 20, frame++, tab);
		setBounds(19822, 154, 124 + 20, frame++, tab);
		setBounds(19824, 5, 160 + 20, frame++, tab);
		setBounds(5649, 41, 158 + 20, frame++, tab);
		setBounds(5647, 79, 163 + 20, frame++, tab);
		setBounds(5648, 116, 158 + 20, frame++, tab);

		// Preserve
		setBounds(28002, 157, 160 + 20, frame++, tab);

		// Chivarly
		setBounds(19826, 10, 208, frame++, tab);

		// Piety
		setBounds(19828, 45, 207 + 13, frame++, tab);

		// Rigour
		setBounds(28005, 85, 210, frame++, tab);

		// Augury
		setBounds(28008, 124, 210, frame++, tab);

		setBounds(17235, 0, 25, frame++, tab);// Faded backing
		setBounds(17201, 0, 22, frame++, tab);// Split
		setBounds(17201, 0, 237, frame++, tab);// Split

		setBounds(17202, 5 - 3, 8 + 17, frame++, tab);
		setBounds(17203, 44 - 3, 8 + 17, frame++, tab);
		setBounds(17204, 79 - 3, 8 + 17, frame++, tab);
		setBounds(17205, 116 - 3, 8 + 17, frame++, tab);
		setBounds(17206, 153 - 3, 8 + 17, frame++, tab);
		setBounds(17207, 5 - 3, 48 + 17, frame++, tab);
		setBounds(17208, 44 - 3, 48 + 17, frame++, tab);
		setBounds(17209, 79 - 3, 48 + 17, frame++, tab);
		setBounds(17210, 116 - 3, 48 + 17, frame++, tab);
		setBounds(17211, 153 - 3, 48 + 17, frame++, tab);
		setBounds(17212, 5 - 3, 85 + 17, frame++, tab);
		setBounds(17213, 44 - 3, 85 + 17, frame++, tab);
		setBounds(17214, 79 - 3, 85 + 17, frame++, tab);
		setBounds(17215, 116 - 3, 85 + 17, frame++, tab);
		setBounds(17216, 153 - 3, 85 + 17, frame++, tab);
		setBounds(17217, 5 - 3, 124 + 17, frame++, tab);
		setBounds(17218, 44 - 3, 124 + 17, frame++, tab);
		setBounds(17219, 79 - 3, 124 + 17, frame++, tab);
		setBounds(17220, 116 - 3, 124 + 17, frame++, tab);
		setBounds(17221, 153 - 3, 124 + 17, frame++, tab);
		setBounds(17222, 5 - 3, 160 + 17, frame++, tab);
		setBounds(17223, 44 - 3, 160 + 17, frame++, tab);
		setBounds(17224, 79 - 3, 160 + 17, frame++, tab);
		setBounds(17225, 116 - 3, 160 + 17, frame++, tab);
		setBounds(17226, 153 - 3, 160 + 17, frame++, tab);

		setBounds(17227, 1, 207 + 4, frame++, tab); // Chivalry toggle button
		setBounds(17228, 41, 207 + 4, frame++, tab); // Piety toggle button
		setBounds(17229, 77, 207 + 4, frame++, tab); // Rigour toggle button
		setBounds(17230, 116, 207 + 4, frame++, tab); // Augury toggle button

		setBounds(17231, 5, 5, frame++, tab);// text
		setBounds(17232, 0, 237, frame++, tab);// confirm
		setBounds(17233, 0, 237, frame++, tab);// Confirm hover
	}

	public static void addTransparentSprite(int id, int spriteId, int transparency) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.transparency = transparency;
		tab.hoverType = 52;
		tab.disabledSprite =  SpriteLoader.getSprite( spriteId);
		tab.enabledSprite =  SpriteLoader.getSprite( spriteId);
		tab.width = 512;
		tab.height = 334;
		tab.drawsTransparent = true;
	}

	public static void Pestpanel(GameFont[] tda) {
		Widget RSinterface = addInterface(21119);
		addText(21120, "What", 0x999999, false, true, 52, tda, 1);
		addText(21121, "What", 0x33cc00, false, true, 52, tda, 1);
		addText(21122, "(Need 5 to 25 players)", 0xFFcc33, false, true, 52, tda, 1);
		addText(21123, "Points", 0x33ccff, false, true, 52, tda, 1);
		int last = 4;
		RSinterface.children = new int[last];
		RSinterface.childX = new int[last];
		RSinterface.childY = new int[last];
		setBounds(21120, 15, 12, 0, RSinterface);
		setBounds(21121, 15, 30, 1, RSinterface);
		setBounds(21122, 15, 48, 2, RSinterface);
		setBounds(21123, 15, 66, 3, RSinterface);
	}

	public static void Pestpanel2(GameFont[] tda) {
		Widget RSinterface = addInterface(21100);
		addSprite(21101, 0, "Pest Control/PEST1");
		addSprite(21102, 1, "Pest Control/PEST1");
		addSprite(21103, 2, "Pest Control/PEST1");
		addSprite(21104, 3, "Pest Control/PEST1");
		addSprite(21105, 4, "Pest Control/PEST1");
		addSprite(21106, 5, "Pest Control/PEST1");
		addText(21107, "", 0xCC00CC, false, true, 52, tda, 1);
		addText(21108, "", 0x0000FF, false, true, 52, tda, 1);
		addText(21109, "", 0xFFFF44, false, true, 52, tda, 1);
		addText(21110, "", 0xCC0000, false, true, 52, tda, 1);
		addText(21111, "250", 0x99FF33, false, true, 52, tda, 1);// w purp
		addText(21112, "250", 0x99FF33, false, true, 52, tda, 1);// e blue
		addText(21113, "250", 0x99FF33, false, true, 52, tda, 1);// se yel
		addText(21114, "250", 0x99FF33, false, true, 52, tda, 1);// sw red
		addText(21115, "200", 0x99FF33, false, true, 52, tda, 1);// attacks
		addText(21116, "0", 0x99FF33, false, true, 52, tda, 1);// knights hp
		addText(21117, "Time Remaining:", 0xFFFFFF, false, true, 52, tda, 0);
		addText(21118, "", 0xFFFFFF, false, true, 52, tda, 0);
		int last = 18;
		RSinterface.children = new int[last];
		RSinterface.childX = new int[last];
		RSinterface.childY = new int[last];
		setBounds(21101, 361, 26, 0, RSinterface);
		setBounds(21102, 396, 26, 1, RSinterface);
		setBounds(21103, 436, 26, 2, RSinterface);
		setBounds(21104, 474, 26, 3, RSinterface);
		setBounds(21105, 3, 21, 4, RSinterface);
		setBounds(21106, 3, 50, 5, RSinterface);
		setBounds(21107, 371, 60, 6, RSinterface);
		setBounds(21108, 409, 60, 7, RSinterface);
		setBounds(21109, 443, 60, 8, RSinterface);
		setBounds(21110, 479, 60, 9, RSinterface);
		setBounds(21111, 362, 10, 10, RSinterface);
		setBounds(21112, 398, 10, 11, RSinterface);
		setBounds(21113, 436, 10, 12, RSinterface);
		setBounds(21114, 475, 10, 13, RSinterface);
		setBounds(21115, 32, 32, 14, RSinterface);
		setBounds(21116, 32, 62, 15, RSinterface);
		setBounds(21117, 8, 88, 16, RSinterface);
		setBounds(21118, 87, 88, 17, RSinterface);
	}

	public static void addHoverBox(int id, int ParentID, String text, String text2, int configId, int configFrame) {
		Widget rsi = addInterface(id);
		rsi.id = id;
		rsi.parent = ParentID;
		rsi.type = 8;
		rsi.secondaryText = text;
		rsi.defaultText = text2;
		rsi.valueCompareType = new int[1];
		rsi.requiredValues = new int[1];
		rsi.valueCompareType[0] = 1;
		rsi.requiredValues[0] = configId;
		rsi.valueIndexArray = new int[1][3];
		rsi.valueIndexArray[0][0] = 5;
		rsi.valueIndexArray[0][1] = configFrame;
		rsi.valueIndexArray[0][2] = 0;
	}

	public static void addItemOnInterface(int childId, int parent, String[] options) {
		Widget rsi = interfaceCache[childId] = new Widget();
		rsi.actions = new String[5];
		rsi.spritesX = new int[20];
		rsi.inventoryItemId = new int[30];
		rsi.inventoryAmounts = new int[30];
		rsi.spritesY = new int[20];
		rsi.children = new int[0];
		rsi.childX = new int[0];
		rsi.childY = new int[0];
		for (int i = 0; i < rsi.actions.length; i++) {
			if (i < options.length) {
				if (options[i] != null) {
					rsi.actions[i] = options[i];
				}
			}
		}
		rsi.centerText = true;
		rsi.filled = false;
		rsi.replaceItems = false;
		rsi.usableItems = false;
		// rsi.isInventoryInterface = false;
		rsi.allowSwapItems = false;
		rsi.spritePaddingX = 4;
		rsi.spritePaddingY = 5;
		rsi.height = 1;
		rsi.width = 1;
		rsi.parent = parent;
		rsi.id = childId;
		rsi.type = TYPE_INVENTORY;
	}

	public static void addItem(int id, int itemId, int amount) {
		addItem(id, itemId, amount, false, null, null, null, null);
	}

	public static void addItem(int id, int itemId, int amount, boolean actions, String action1, String action2,
			String action3, String action4) {
		Widget rsi = addInterface(id);
		rsi.width = 1;
		rsi.height = 1;
		rsi.inventoryItemId = new int[] { itemId + 1 };
		rsi.inventoryAmounts = new int[] { amount };
		rsi.usableItems = false;
		rsi.spritePaddingX = 16;
		rsi.spritePaddingY = 16;
		rsi.spritesX = new int[20];
		rsi.spritesY = new int[20];
		rsi.sprites = new Sprite[20];
		rsi.type = TYPE_INVENTORY;
		rsi.actions = new String[5];
		if (actions) {
			rsi.actions[0] = action1;
			rsi.actions[1] = action2;
			rsi.actions[2] = action3;
			rsi.actions[3] = action4;
		}
	}

	public static Widget addText(int id, String text, GameFont[] tda, int idx, int color, boolean center, boolean shadow) {
		return addText(id, text, tda, idx, color, center, false, false, shadow);
	}

	public static Widget addRightAlignedText(int id, String text, GameFont[] tda, int idx, int color) {
		return addText(id, text, tda, idx, color, false, true, false, true);
	}

	public static Widget addRightAlignedText(int id, String text, GameFont[] tda, int idx, int color, boolean shadow) {
		return addText(id, text, tda, idx, color, false, true, false, shadow);
	}

	public static Widget addRollingText(int id, String text, GameFont[] tda, int idx, int color, boolean shadow) {
		return addText(id, text, tda, idx, color, false, false, true, shadow);
	}

	public static Widget addText(int id, String text, GameFont[] tda, int idx, int color, boolean center,
			boolean rightAligned, boolean rollingText, boolean shadow) {
		Widget tab = addInterface(id);
		tab.parent = id;
		tab.id = id;
		tab.type = 4;
		tab.atActionType = 0;
		tab.width = 0;
		tab.height = 11;
		tab.contentType = 0;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.centerText = center;
		tab.rightAlignedText = rightAligned;
		tab.rollingText = rollingText;
		tab.textShadow = shadow;
		tab.textDrawingAreas = tda[idx];
		tab.defaultText = text;
		tab.secondaryText = "";
		tab.textColor = color;
		tab.secondaryColor = 0;
		tab.defaultHoverColor = 0;
		tab.secondaryHoverColor = 0;
		return tab;
	}

	public static void addText(int i, String s, int k, boolean l, boolean m, int a, GameFont[] TDA, int j) {
		Widget RSInterface = addInterface(i);
		RSInterface.parent = i;
		RSInterface.id = i;
		RSInterface.type = 4;
		RSInterface.atActionType = 0;
		RSInterface.width = 0;
		RSInterface.height = 0;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = a;
		RSInterface.centerText = l;
		RSInterface.textShadow = m;
		RSInterface.textDrawingAreas = TDA[j];
		RSInterface.defaultText = s;
		RSInterface.secondaryText = "";
		RSInterface.textColor = k;
	}

	public static void addConfigButton(int widgetId, int parentWidgetId, int disabledSpriteId, int enabledSpriteId, int width, int height, String toolTipText,
			int configID, int actionType, int configFrame) {
		Widget Tab = addInterface(widgetId);
		Tab.parent = parentWidgetId;
		Tab.id = widgetId;
		Tab.type = 5;
		Tab.atActionType = actionType;
		Tab.contentType = 0;
		Tab.width = width;
		Tab.height = height;
		Tab.opacity = 0;
		Tab.hoverType = -1;
		Tab.valueCompareType = new int[1];
		Tab.requiredValues = new int[1];
		Tab.valueCompareType[0] = 1;
		Tab.requiredValues[0] = configID;
		Tab.valueIndexArray = new int[1][3];
		Tab.valueIndexArray[0][0] = 5;
		Tab.valueIndexArray[0][1] = configFrame;
		Tab.valueIndexArray[0][2] = 0;
		Tab.disabledSprite =  SpriteLoader.getSprite( disabledSpriteId);// imageLoader(bID, bName);
		Tab.enabledSprite =  SpriteLoader.getSprite( enabledSpriteId);
		Tab.tooltip = toolTipText;
	}

	public static void addSprite(int id, int spriteId, String spriteName) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.disabledSprite = imageLoader(spriteId, spriteName);
		tab.enabledSprite = imageLoader(spriteId, spriteName);
		tab.width = 512;
		tab.height = 334;
	}

	// OSRS style bank interface by xplicit
	private static void bank(GameFont[] tda) {
		// Set proper width and height for the bank inventory interface (makes item dragging binding look nicer)
		Widget.interfaceCache[5063].width = 190;
		Widget.interfaceCache[5063].height = 261;
		// Change bank inventory container options
		Widget.interfaceCache[5064].actions = new String[] { "Deposit-1", "Deposit-5", "Deposit-10", "Deposit-X", "Deposit-All" };

		Widget bankBg = addInterface(5292);
		bankBg.totalChildren(10);
		int bankBgChild = 0;
		int startId = 50000;
		int configStart = 1112;

		// Background
		addBackground(startId, 488, 331, true, AutomaticSprite.BACKGROUND_BLACK);
		bankBg.child(bankBgChild++, startId, 12, 2);

		// Close button
		addCloseButton(startId + 1, true);
		bankBg.child(bankBgChild++, startId + 1, 472, 9);

		// Title text
		interfaceCache[5383].centerText = true;
		interfaceCache[5383].width = 0;
		bankBg.child(bankBgChild++, 5383, 12 + (488 / 2), 12);

		// Capacity
		addText(startId + 2, "", tda, 0, 0xff981f, true);
		bankBg.child(bankBgChild++, startId + 2, 30, 8);
		addText(startId + 3, "", tda, 0, 0xff981f, true);
		bankBg.child(bankBgChild++, startId + 3, 30, 20);
		addRectangle(startId + 4, 13, 1, 0xff981f, false);
		bankBg.child(bankBgChild++, startId + 4, 25, 19);

		// Bank settings button
		addToggleSettingButton(startId + 5, 230, 229, "Show menu", 0, configStart++);
		bankBg.child(bankBgChild++, startId + 5, 463, 44);
		addTransparentOnHoverSprite(startId + 6, 231, 25, 25);
		offsetSprites(startId + 6, 5, 4);
		bankBg.child(bankBgChild++, startId + 6, 463, 44);

			// Banking interface
			Widget bankInterface = addInterface(startId + 7);
			bankInterface.width = 478;
			bankInterface.height = 291;
			bankBg.child(bankBgChild++, startId + 7, 17, 37);
			bankInterface.totalChildren(4);
			int bankInterfaceChild = 0;

				// Tab bar
				Widget tabBar = addInterface(startId + 8);
				tabBar.width = 478;
				tabBar.height = 40;
				bankInterface.child(bankInterfaceChild++, startId + 8, 0, 0);
				tabBar.totalChildren(21);
				int tabBarChild = 0;

					// Divider line
					addRectangle(startId + 9, 478, 1, 0x2e2b23, false);
					tabBar.child(tabBarChild++, startId + 9, 0, 39);

					// Tab buttons
					int tabConfig = configStart++;
					for (int i = 0; i < 10; i++) {
						boolean mainTab = i == 0;
						String[] actions = mainTab ? new String[] { "View all items", "Collapse all tabs", "Remove placeholders" }
						: new String[] { "View tab @lre@"+i, "Collapse tab @lre@"+i, "Remove placeholders @lre@"+i};
						addHoverConfigButton(startId + 10 + i, 36, 32, 205, 207, 206, 207, actions, i, tabConfig);
						offsetSprites(startId + 10 + i, -3, -4);
						interfaceCache[startId + 10 + i].allowSwapItems = true;
						tabBar.child(tabBarChild++, startId + 10 + i, 39 + 40 * i + 3, 4);

						// Hide all tabs except the main tab by default
						if(!mainTab)
							interfaceCache[startId + 10 + i].hidden = true;
					}

					// Offset new tab sprites
					offsetSprite( SpriteLoader.getSprite( 208), -3, -4);
					offsetSprite( SpriteLoader.getSprite( 210), 9, 9);

					// Tab icons
					for (int i = 0; i < 10; i++) {
						boolean mainTab = i == 0;
						addTransparentSprite(startId + 20 + i, mainTab ? 209 : 0, 255);
						tabBar.child(tabBarChild++, startId + 20 + i, 39 + 40 * i + (mainTab ? 9 : 3), mainTab ? 13 : 4);

						// Hide all tabs except the main tab by default
						if(!mainTab)
							interfaceCache[startId + 20 + i].hidden = true;
					}

				// Scroll bar container (contains item containers)
				Widget scroll = interfaceCache[5385];
				scroll.width = 461;
				scroll.height = 210;
				scroll.scrollMax = 211;
				bankInterface.child(bankInterfaceChild++, 5385, 0, 42);
				scroll.totalChildren(29);
				int scrollChild = 0;

					// Tab dividers
					for (int i = 0; i < 9; i++) {
						addTransparentRectangle(startId + 40 + i, 374, 2, 0x362c21, false, 128);
						interfaceCache[startId + 40 + i].hidden = true;
						scroll.child(scrollChild++, startId + 40 + i, 52, 0);
					}

					// Tab item containers
					for (int i = 0; i < 10; i++) {
						addContainer(startId + 50 + i, scroll.id, 206, 8, 95, 16, 4, "Withdraw-1", "Withdraw-5", "Withdraw-10", "Withdraw-X", "Withdraw-All", "Withdraw-All-but-1");
						interfaceCache[startId + 50 + i].allowSwapItems = true;
						interfaceCache[startId + 50 + i].hidden = true;
						scroll.child(scrollChild++, startId + 50 + i, 52, 0);
					}

					// Tab item containers (searching)
					for (int i = 0; i < 10; i++) {
						addContainer(startId + 60 + i, scroll.id, 206, 8, 95, 16, 4, "Withdraw-1", "Withdraw-5", "Withdraw-10", "Withdraw-X", "Withdraw-All", "Withdraw-All-but-1");
						interfaceCache[startId + 60 + i].allowSwapItems = true;
						interfaceCache[startId + 60 + i].hidden = true;
						scroll.child(scrollChild++, startId + 60 + i, 52, 0);
					}

				// Left bottom bar that contains buttons
				Widget leftButtonBar = addInterface(startId + 70);
				bankInterface.child(bankInterfaceChild++, startId + 70, 0, 254);
				leftButtonBar.totalChildren(13);
				int leftButtonBarChild = 0;

					// Rearrange mode
					leftButtonBar.child(leftButtonBarChild++, 5390, 2, 0);
					removeSomething(8130);
					addHoverTextResetSettingButton(8130, 119, 120, "Swap", tda, 1, 0xff981f, 0xffffff, 0, 304);
					leftButtonBar.child(leftButtonBarChild++, 8130, 0, 15);
					removeSomething(8131);
					addHoverTextResetSettingButton(8131, 119, 120, "Insert", tda, 1, 0xff981f, 0xffffff, 1, 304);
					leftButtonBar.child(leftButtonBarChild++, 8131, 50, 15);

					// Withdraw as
					leftButtonBar.child(leftButtonBarChild++, 5388, 115, 0);
					removeSomething(5387);
					addHoverTextResetSettingButton(5387, 119, 120, "Item", tda, 1, 0xff981f, 0xffffff, 0, 115);
					leftButtonBar.child(leftButtonBarChild++, 5387, 100, 15);
					removeSomething(5386);
					addHoverTextResetSettingButton(5386, 119, 120, "Note", tda, 1, 0xff981f, 0xffffff, 1, 115);
					leftButtonBar.child(leftButtonBarChild++, 5386, 150, 15);

					// Quantity
					addText(startId + 71, "Quantity:", tda, 1, 0xff981f, false);
					leftButtonBar.child(leftButtonBarChild++, startId + 71, 236, 0);
					addButton(startId + 72, 25, 22, "Set custom quantity");
					leftButtonBar.child(leftButtonBarChild++, startId + 72, 200 + 25 * 3, 15);
					String[] qtys = new String[] { "1", "5", "10", "X", "All" };
					int qtysConfig = configStart++;
					for (int i = 0; i < qtys.length; i++) {
						addHoverTextResetSettingButton(startId + 73 + i, 121, 122, qtys[i], tda, 1, 0xff981f, 0xffffff, i, qtysConfig);
						interfaceCache[startId + 73 + i].tooltip = "Default quantity: "+qtys[i];
						leftButtonBar.child(leftButtonBarChild++, startId + 73 + i, 200 + 25 * i, 15);
					}

				// Right bottom bar that contains buttons
				Widget rightButtonBar = addInterface(startId + 81);
				bankInterface.child(bankInterfaceChild++, startId + 81, 0, 254);
				rightButtonBar.totalChildren(7);
				int rightButtonBarChild = 0;

					// Placeholders
					addToggleSettingButton(startId + 82, 36, 36, getSprite(9, interfaceLoader, "miscgraphics"), getSprite(0, interfaceLoader, "miscgraphics"), "Enable @lre@Always set placeholders", 0, configStart++);
					rightButtonBar.child(rightButtonBarChild++, startId + 82, 326, 0);
					addTransparentOnHoverSprite(startId + 83, 118, 36, 36);
					offsetSprites(startId + 83, 10, 8);
					rightButtonBar.child(rightButtonBarChild++, startId + 83, 326, 0);

					// Search
					addConfigButton(startId + 84, 36, 36, getSprite(9, interfaceLoader, "miscgraphics"), getSprite(0, interfaceLoader, "miscgraphics"), "Search", 0, configStart++);
					rightButtonBar.child(rightButtonBarChild++, startId + 84, 365, 0);
					addTransparentOnHoverSprite(startId + 85, 117, 36, 36);
					offsetSprites(startId + 85, 10, 10);
					rightButtonBar.child(rightButtonBarChild++, startId + 85, 365, 0);

					// Deposit inventory
					addTimerConfigButton(startId + 86, 36, 36, getSprite(9, interfaceLoader, "miscgraphics"), getSprite(0, interfaceLoader, "miscgraphics"), "Deposit inventory", 0, configStart++, 375);
					rightButtonBar.child(rightButtonBarChild++, startId + 86, 404, 0);
					addTransparentOnHoverSprite(startId + 87, 115, 36, 36);
					offsetSprites(startId + 87, 3, 8);
					rightButtonBar.child(rightButtonBarChild++, startId + 87, 404, 0);

					// Deposit equipment
					Widget equipmentButtonContainer = addInterface(startId + 88);
					equipmentButtonContainer.totalChildren(2);
					rightButtonBar.child(rightButtonBarChild++, startId + 88, 441, 0);
						addTimerConfigButton(startId + 89, 36, 36, getSprite(9, interfaceLoader, "miscgraphics"), getSprite(0, interfaceLoader, "miscgraphics"), "Deposit worn items", 0, configStart++, 375);
						equipmentButtonContainer.child(0, startId + 89, 0, 0);
						addTransparentOnHoverSprite(startId + 90, 116, 36, 36);
						offsetSprites(startId + 90, 4, 8);
						equipmentButtonContainer.child(1, startId + 90, 0, 0);

			// Bank settings menu interface, load all of the interface components here but don't add it as a child (show menu button toggles which interface id is the child)
			Widget settings = addInterface(startId + 100);
			settings.totalChildren(11);
			int settingsChild = 0;

				// Tab display
				addRectangle(startId + 101, 276, 90, 0x726451, false);
				settings.child(settingsChild++, startId + 101, 101, 11);
				addRectangle(startId + 102, 278, 92, 0x2e2b23, false);
				settings.child(settingsChild++, startId + 102, 100, 10);
				addText(startId + 103, "Tab display:", tda, 2, 0xff981f);
				settings.child(settingsChild++, startId + 103, 104, 14);
				String[] tabs = new String[] { "First item in tab", "Digit (1, 2, 3)", "Roman numeral (I, II, III)", "Hide tab bar" };
				String[] tabTips = new String[] { "First item", "Digit", "Roman numeral", "Hide" };
				int tabsConfig = configStart++;
				for (int i = 0; i < tabs.length; i++) {
					addHoverTextResetSettingButton(startId + 104 + i, 268, 17, 873, 874, tabs[i], tda, 1, 0xff981f, 0xffffff, false, i, tabsConfig);
					interfaceCache[startId + 104 + i].tooltip = tabTips[i];
					settings.child(settingsChild++, startId + 104 + i, 105, 32 + 17 * i);
				}
				// Red strikethrough line for hide tab bar text
				addRectangle(startId + 108, Client.instance.newRegularFont.getTextWidth("Hide tab bar"), 1, 0x800000, false);
				settings.child(settingsChild++, startId + 108, 125, 32 + 17 * 3 + 8);

				// Fixed width (Incinerator on OSRS)
				addHoverTextToggleSettingButton(startId + 109,  SpriteLoader.getSprite( 875).myWidth + 5 + Client.instance.newRegularFont.getTextWidth("Fixed width"),
						17, 874, 875, "Fixed width", tda, 1, 0xff981f, 0xffffff, false, 0, configStart++);
				settings.child(settingsChild++, startId + 109, 94, 109);

				// 'Deposit worn items' button
				addHoverTextToggleSettingButton(startId + 110,  SpriteLoader.getSprite( 875).myWidth + 5 + Client.instance.newRegularFont.getTextWidth("'Deposit worn items' button"),
						17, 874, 875, "'Deposit worn items' button", tda, 1, 0xff981f, 0xffffff, false, 1, configStart++);
				settings.child(settingsChild++, startId + 110, 209, 109);

				// Release placeholders
				addHoverTextButton(startId + 111, 126, 126, "Release all placeholders", tda, 1, 0x8f8f8f, 0x8f8f8f);
				interfaceCache[startId + 111].openMenuButton = true;
				settings.child(settingsChild++, startId + 111, 150, 133);
				/*addHoverTextButton(startId + 112, 127, 127, "Release all placeholders (800)", tda, 1, 0xff981f, 0xffffff);
				settings.child(settingsChild++, startId + 112, 142, 133);
				interfaceCache[startId + 112].hidden = true;

				// Bank fillers
				addRectangle(startId + 114, 202, 101, 0x726451, false);
				settings.child(settingsChild++, startId + 114, 138, 179);
				addRectangle(startId + 115, 204, 103, 0x2e2b23, false);
				settings.child(settingsChild++, startId + 115, 137, 178);
				addText(startId + 116, "Bank Fillers", tda, 2, 0xff981f, true);
				settings.child(settingsChild++, startId + 116, 137 + (204 / 2), 182);
				String[] fill = new String[] { "1", "10", "50", "X", "All" };
				int fillConfig = configStart++;
				for (int i = 0; i < fill.length; i++) {
					addHoverTextResetSettingButton(startId + 117 + i, getSprite(0, interfaceLoader, "miscgraphics"), getSprite(9, interfaceLoader, "miscgraphics"), fill[i], tda, 2, 0xffffff, 0xd8b5a1, i, fillConfig);
					settings.child(settingsChild++, startId + 117 + i, 143 + 39 * i, 203);
				}
				addHoverTextButton(startId + 122, 125, 125, "Fill", tda, 2, 0xffffff, 0xff0000);
				settings.child(settingsChild++, startId + 122, 143, 243);*/

		// Preset button
		//addButton(startId + 112, 1246, "Presets");


		//Add Preset Button
		int id = startId + 112;
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_HOVER_SPRITE;
		b.atActionType = OPTION_PRESET;
		int sprite = 1246;
		int hoverSprite = 1247;
		b.disabledSprite = b.enabledSprite =  SpriteLoader.getSprite( sprite);
		b.hoverSprite1 = b.hoverSprite2 =  SpriteLoader.getSprite( hoverSprite);
		b.width = b.disabledSprite.myWidth;
		b.height = b.disabledSprite.myHeight;
		b.actions = new String[]{"Open Presets"};
		b.tooltip = "Open presets";

		bankBg.child(bankBgChild++, startId + 112, 448, 9);

		interfaceCache[7423].actions = new String[] { "Deposit-1", "Deposit-5", "Deposit-10", "Deposit-X", "Deposit-All" };
	}

	public static void skillGuide(GameFont[] tda) {
		int id = 36100;
		Widget mainScroll = addInterface(id);
		Widget list = addInterface(id + 450);
		Widget side = addInterface(id + 50);
		int pos = id + 51;
		int y = 0;
		side.width = 260;
		list.height = 237;
		list.width = 284;
		list.scrollMax = 1750;
		addSprite(id + 1, 845);
		addSprite(id + 451, 846);
		addSprite(id + 452, 847);
		addButton(id + 4, id, 24, 24, 850, 850, id + 5, "Close");
		addHoveredButton_sprite_loader(id + 5, 851, 24, 24, id + 6);

		for (int i = 0; i < 14; i++) {
			addHoverText(id + 453 + i, "", "Open", tda, 3, 0x46320a, true, false, 150);
		}

		addText(id + 2, "Attack", tda, 4, 0x46320a, true, false);
		addText(id + 3, "Weapons", tda, 0, 0x446320a, true, false);
		Widget container = addInterface(id + 150);
		container.spritesX = new int[20];
		container.spritesY = new int[20];
		container.inventoryItemId = new int[100];
		container.inventoryAmounts = new int[100];
		container.filled = false;
		container.replaceItems = false;
		container.usableItems = false;
		container.allowSwapItems = false;
		container.spritePaddingX = 0;
		container.spritePaddingY = 3;
		container.height = 100;
		container.width = 1;
		container.parent = 42000;
		container.type = TYPE_INVENTORY;
		for (int i = 0; i < 400; i++) {
			addText(pos + i, "", tda, 3, 0x46320a, true, false);
			addText(pos + (i + 1), "", tda, 1, 0x46320a, false, false);
			addText(pos + (i + 2), "", tda, 1, 0x46320a, false, false);
			i += 3;
		}
		mainScroll.totalChildren(7);
		side.totalChildren(5 + 6 + 5);
		list.totalChildren(1051);
		int scrollChild = 0;
		mainScroll.child(scrollChild++, id + 1, 0, 0);
		mainScroll.child(scrollChild++, id + 4, 470, 5);
		mainScroll.child(scrollChild++, id + 5, 470, 5);
		mainScroll.child(scrollChild++, id + 2, 180, 5);
		mainScroll.child(scrollChild++, id + 3, 180, 32);
		mainScroll.child(scrollChild++, id + 50, 355, 35);
		mainScroll.child(scrollChild++, id + 450, 30, 80);
		int sideChild = 0;
		side.child(sideChild++, id + 451, 0, 0);
		side.child(sideChild++, id + 452, 0, 66 * 4);
		for (int i = 0; i < 14; i++) {
			side.child(sideChild++, id + 453 + i, 0, 10 + (i * 18));
		}
		int listChild = 0;
		list.child(listChild++, id + 150, 22, 0);
		for (int i = 0; i < 1400; i++) {
			list.child(listChild++, pos + i, 11, y + 2);
			list.child(listChild++, pos + (i + 1), 61, y);
			list.child(listChild++, pos + (i + 2), 61, y + 13);
			y += 35;
			i += 3;
		}
	}


	public static void emojisPanel(GameFont[] tda) {
		int id = 64000;
		Widget widget = addInterface(id);
		setChildren(1, widget);
		setBounds(id + 1, 394, 233, 0, widget); //389, 264

		Widget mainScroll = addInterface(id + 1);
		mainScroll.height = 100;
		mainScroll.width = 100;
		mainScroll.scrollMax = 200;

		setChildren(12, mainScroll);

		int child = 0;

		addPixels(id + 2, 0x000000, 100, 200, 150, true);
		setBounds(id + 2, 0, 0, child++, mainScroll);

		int xPos = 0;
		int yPos = 0;

		for (int i = 0; i <= 10; i++) {
			addButton(id + 3 + i, 834 + i, "Select");
			setBounds(id + 3 + i, 5 + xPos, 5 + yPos, child++, mainScroll);
			xPos += 25;
			if (i != 0 && (i % 4) == 0) {
				xPos = 0;
				yPos += 25;
			}
		}
	}

	public static void jewelryMaking(GameFont[] tda) {
		Widget tab = addInterface(58000);

		addClosableWindow(58001, 485, 304, false, "What would you like to make?");

		addText(58009, "Rings", tda, 2, 0xff9933);
		addText(58010, "Necklaces", tda, 2, 0xff9933);
		addText(58011, "Amulets", tda, 2, 0xff9933);
		addText(58012, "Bracelets", tda, 2, 0xff9933);

		addText(58022, "You need a ring mould to craft rings.", tda, 1, 0xff9933);
		addText(58023, "You need a necklace mould to craft rings.", tda, 1, 0xff9933);
		addText(58024, "You need an amulet mould to craft rings.", tda, 1, 0xff9933);
		addText(58025, "You need a bracelet mould to craft rings.", tda, 1, 0xff9933);

		int[] containerIds = new int[] { 58018, 58019, 58020, 58021 };

		for (int i = 0; i < containerIds.length; i++) {
			addContainer(containerIds[i], 1, 8, 1, "Make-1", "Make-5", "Make-10", null, "Make-X");
			interfaceCache[containerIds[i]].defaultSprite =  SpriteLoader.getSprite( 854 + i);
			interfaceCache[containerIds[i]].hidden = true;
		}

		tab.totalChildren(13);
		tab.child(0, 58001, 14, 16);

		int y = 62;
		int add = 62;

		/* Headers */
		tab.child(1, 58009, 80, y);
		tab.child(2, 58010, 80, y + add);
		tab.child(3, 58011, 80, y + add * 2);
		tab.child(4, 58012, 80, y + add * 3);

		y = 88;
		add = 60;

		/* Containers */
		tab.child(5, 58018, 85, y);
		tab.child(6, 58019, 85, y + add);
		tab.child(7, 58020, 85, y + add * 2);
		tab.child(8, 58021, 85, y + add * 3);

		y = 86;
		add = 63;
		/* Messages */
		tab.child(9, 58022, 97, y);
		tab.child(10, 58023, 97, y + add);
		tab.child(11, 58024, 97, y + add * 2);
		tab.child(12, 58025, 97, y + add * 3);
	}

	public static void teleportOptions(Widget scroll, GameFont[] tda, String[] options, int hoverFrameStart,
			int textFrameStart) {
		int y = 1;
		for (int frame = 0; frame < options.length; frame++, y += 35) {
			hoverButton(hoverFrameStart + frame, "Teleport", 113, 112, 80);
			addText(textFrameStart + frame, options[frame], tda, 1, 0xFFA500, true, true);

			scroll.child(frame, hoverFrameStart + frame, 0, y);
			scroll.child(frame + options.length, textFrameStart + frame, 99, y + 9);
		}
	}

	public static Widget addHoverText(int id, String text, String tooltip, GameFont[] tda, int idx, int color,
									  boolean center, boolean textShadow, int width, int hoveredColor) {
		Widget rsinterface = addInterface(id);
		rsinterface.id = id;
		rsinterface.parent = id;
		rsinterface.type = 4;
		rsinterface.atActionType = 1;
		rsinterface.width = width;
		rsinterface.height = 13;
		rsinterface.contentType = 0;
		rsinterface.opacity = 0;
		rsinterface.hoverType = -1;
		rsinterface.centerText = center;
		rsinterface.textShadow = textShadow;
		rsinterface.textDrawingAreas = tda[idx];
		rsinterface.defaultText = text;
		rsinterface.secondaryText = "";
		rsinterface.textColor = color;
		rsinterface.secondaryColor = 0;
		rsinterface.defaultHoverColor = hoveredColor;
		rsinterface.secondaryHoverColor = 0;
		rsinterface.tooltip = tooltip;
		return rsinterface;
	}

	public static void addHoveredConfigButton(Widget original, int ID, int IMAGEID, int disabledID, int enabledID) {
		Widget rsint = addInterface(ID);
		rsint.parent = original.id;
		rsint.id = ID;
		rsint.type = 0;
		rsint.atActionType = 0;
		rsint.contentType = 0;
		rsint.width = original.width;
		rsint.height = original.height;
		rsint.opacity = 0;
		rsint.hoverType = -1;
		Widget hover = addInterface(IMAGEID);
		hover.type = 5;
		hover.width = original.width;
		hover.height = original.height;
		hover.valueCompareType = original.valueCompareType;
		hover.requiredValues = original.requiredValues;
		hover.valueIndexArray = original.valueIndexArray;
		if (disabledID != -1)
			hover.disabledSprite =  SpriteLoader.getSprite( disabledID);
		if (enabledID != -1)
			hover.enabledSprite =  SpriteLoader.getSprite( enabledID);
		rsint.totalChildren(1);
		setBounds(IMAGEID, 0, 0, 0, rsint);
		rsint.tooltip = original.tooltip;
		rsint.invisible = true;
	}

	public static void addHoverConfigButton(int id, int hoverOver, int disabledID, int enabledID, int width, int height,
			String tooltip, int[] valueCompareType, int[] requiredValues, int[][] valueIndexArray) {
		Widget rsint = addInterface(id);
		rsint.parent = id;
		rsint.id = id;
		rsint.type = 5;
		rsint.atActionType = 5;
		rsint.contentType = 206;
		rsint.width = width;
		rsint.height = height;
		rsint.opacity = 0;
		rsint.hoverType = hoverOver;
		rsint.valueCompareType = valueCompareType;
		rsint.requiredValues = requiredValues;
		rsint.valueIndexArray = valueIndexArray;
		if (disabledID != -1)
			rsint.disabledSprite =  SpriteLoader.getSprite( disabledID);
		if (enabledID != -1)
			rsint.enabledSprite =  SpriteLoader.getSprite( enabledID);
		rsint.tooltip = tooltip;
	}

	public static void addButton(int id, Sprite enabled, Sprite disabled, String tooltip, int w, int h) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = id;
		tab.type = 5;
		tab.atActionType = 1;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.disabledSprite = disabled;
		tab.enabledSprite = enabled;
		tab.width = w;
		tab.height = h;
		tab.tooltip = tooltip;
	}

	public static void addConfigButton(int ID, int pID, Sprite disabled, Sprite enabled, int width, int height,
			String tT, int configID, int aT, int configFrame) {
		Widget Tab = addInterface(ID);
		Tab.parent = pID;
		Tab.id = ID;
		Tab.type = 5;
		Tab.atActionType = aT;
		Tab.contentType = 0;
		Tab.width = width;
		Tab.height = height;
		Tab.opacity = 0;
		Tab.hoverType = -1;
		Tab.valueCompareType = new int[1];
		Tab.requiredValues = new int[1];
		Tab.valueCompareType[0] = 1;
		Tab.requiredValues[0] = configID;
		Tab.valueIndexArray = new int[1][3];
		Tab.valueIndexArray[0][0] = 5;
		Tab.valueIndexArray[0][1] = configFrame;
		Tab.valueIndexArray[0][2] = 0;
		Tab.disabledSprite = disabled;
		Tab.enabledSprite = enabled;
		Tab.tooltip = tT;
	}

	public static Widget addItem(int id) {
		return addContainer(id, id, 0, 1, 1, 0, 0, (String) null);
	}

	public static Widget addContainer(int id, int width, int height, int paddingX, int paddingY) {
		return addContainer(id, id, 0, width, height, paddingX, paddingY, (String) null);
	}

	public static Widget addContainer(int id, int width, int height, int paddingX, int paddingY, String... actions) {
		return addContainer(id, id, 0, width, height, paddingX, paddingY, actions);
	}

	public static Widget addContainer(int id, int contentType, int width, int height, String... actions) {
		return addContainer(id, id, contentType, width, height, 16, 4, actions);
	}

	public static Widget addContainer(int id, int parent, int contentType, int width, int height, int paddingX, int paddingY, String... actions) {
		Widget container = addInterface(id);
		container.parent = parent;
		container.type = 2;
		container.contentType = contentType;
		container.width = width;
		container.height = height;
		container.sprites = new Sprite[20];
		container.spritesX = new int[20];
		container.spritesY = new int[20];
		container.spritePaddingX = paddingX;
		container.spritePaddingY = paddingY;
		container.inventoryItemId = new int[width * height];
		container.inventoryAmounts = new int[width * height];
		container.actions = actions;
		return container;
	}

	public static Widget addContainer(int id, int contentType, int width, int height, int spritePaddingX, int spritePaddingY) {
		return addContainer(id, id, contentType, width, height, spritePaddingX, spritePaddingY);
	}

	public static Widget addContainer(int id, int parent, int contentType, int width, int height, int paddingX, int paddingY) {
		Widget container = addInterface(id);
		container.parent = parent;
		container.type = 2;
		container.contentType = contentType;
		container.width = width;
		container.height = height;
		container.sprites = new Sprite[20];
		container.spritesX = new int[20];
		container.spritesY = new int[20];
		container.spritePaddingX = paddingX;
		container.spritePaddingY = paddingY;
		container.inventoryItemId = new int[width * height];
		container.inventoryAmounts = new int[width * height];
		return container;
	}

	public static void addSpriteLoader(int childId, int spriteId) {
		Widget rsi = interfaceCache[childId] = new Widget();
		rsi.id = childId;
		rsi.parent = childId;
		rsi.type = 5;
		rsi.atActionType = 0;
		rsi.contentType = 0;
		rsi.disabledSprite =  SpriteLoader.getSprite( spriteId);
		rsi.enabledSprite =  SpriteLoader.getSprite( spriteId);

		// rsi.sprite1.spriteLoader = rsi.sprite2.spriteLoader = true;
		// rsi.hoverSprite1 = Client.cacheSprite[hoverSpriteId];
		// rsi.hoverSprite2 = Client.cacheSprite[hoverSpriteId];
		// rsi.hoverSprite1.spriteLoader = rsi.hoverSprite2.spriteLoader = true;
		// rsi.sprite1 = rsi.sprite2 = spriteId;
		// rsi.hoverSprite1Id = rsi.hoverSprite2Id = hoverSpriteId;
		rsi.width = rsi.disabledSprite.myWidth;
		rsi.height = rsi.enabledSprite.myHeight - 2;
		// rsi.isFalseTooltip = true;
	}

	public static void addSprite(int childId, Sprite sprite1, Sprite sprite2) {
		Widget rsi = interfaceCache[childId] = new Widget();
		rsi.id = childId;
		rsi.parent = childId;
		rsi.type = 5;
		rsi.atActionType = 0;
		rsi.contentType = 0;
		rsi.disabledSprite = sprite1;
		rsi.enabledSprite = sprite2;
		rsi.width = rsi.disabledSprite.myWidth;
		rsi.height = rsi.enabledSprite.myHeight - 2;
	}

	public static void addButtonWSpriteLoader(int id, int spriteId) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = id;
		tab.type = 5;
		tab.atActionType = 1;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.disabledSprite =  SpriteLoader.getSprite( spriteId);
		tab.enabledSprite =  SpriteLoader.getSprite( spriteId);
		tab.width = tab.disabledSprite.myWidth;
		tab.height = tab.enabledSprite.myHeight - 2;
	}

	public static void addButtonWithConfig(int id, int disabledSpriteId, int enabledSpriteId, int width, int height,
			String text, int configId, int configFrame) {
		Widget tab = addInterface(id);
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
		tab.requiredValues[0] = configId;
		tab.valueIndexArray = new int[1][3];
		tab.valueIndexArray[0][0] = 5;
		tab.valueIndexArray[0][1] = configFrame;
		tab.valueIndexArray[0][2] = 0;
		if (disabledSpriteId != -1)
			tab.disabledSprite =  SpriteLoader.getSprite( disabledSpriteId);
		if (enabledSpriteId != -1)
			tab.enabledSprite =  SpriteLoader.getSprite( enabledSpriteId);
	}

	public static void addHoverButtonWConfig(int i, int spriteId, int spriteId2, int width, int height, String text,
			int contentType, int hoverOver, int aT, int configId, int configFrame) {// hoverable
		// button
		Widget tab = addInterface(i);
		tab.id = i;
		tab.parent = i;
		tab.type = 5;
		tab.atActionType = aT;
		tab.contentType = contentType;
		tab.opacity = 0;
		tab.hoverType = hoverOver;
		tab.width = width;
		tab.height = height;
		tab.tooltip = text;
		tab.valueCompareType = new int[1];
		tab.requiredValues = new int[1];
		tab.valueCompareType[0] = 1;
		tab.requiredValues[0] = configId;
		tab.valueIndexArray = new int[1][3];
		tab.valueIndexArray[0][0] = 5;
		tab.valueIndexArray[0][1] = configFrame;
		tab.valueIndexArray[0][2] = 0;
		if (spriteId != -1)
			tab.disabledSprite =  SpriteLoader.getSprite( spriteId);
		if (spriteId2 != -1)
			tab.enabledSprite =  SpriteLoader.getSprite( spriteId2);
	}

	public static void addHoverButton(int i, int spriteId, int width, int height, String text, int contentType,
			int hoverOver, int aT, String[] actions) {
		addHoverButton(i, spriteId, width, height, text, contentType, hoverOver, aT);
		interfaceCache[i].actions = actions;
	}

	public static void addHoverButton(int i, int spriteId, int width, int height, String text, int contentType,
			int hoverOver, int aT) {// hoverable
		// button
		Widget tab = addInterface(i);
		tab.id = i;
		tab.parent = i;
		tab.type = 5;
		tab.atActionType = aT;
		tab.contentType = contentType;
		tab.opacity = 0;
		tab.hoverType = hoverOver;
		tab.disabledSprite =  SpriteLoader.getSprite( spriteId);
		tab.enabledSprite =  SpriteLoader.getSprite( spriteId);
		tab.width = width;
		tab.height = height;
		tab.tooltip = text;
	}

	public static void addHoveredButton(int i, int spriteId, int w, int h, int IMAGEID) {// hoverable
		// button
		Widget tab = addInterface(i);
		tab.parent = i;
		tab.id = i;
		tab.type = 0;
		tab.atActionType = 0;
		tab.width = w;
		tab.height = h;
		tab.invisible = true;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.scrollMax = 0;
		addHoverImage_sprite_loader(IMAGEID, spriteId);
		tab.totalChildren(1);
		tab.child(0, IMAGEID, 0, 0);
	}

	public static void addHoverImage_sprite_loader(int i, int spriteId) {
		Widget tab = addInterface(i);
		tab.id = i;
		tab.parent = i;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.width = 512;
		tab.height = 334;
		tab.opacity = 0;
		tab.hoverType = 52;
		tab.disabledSprite =  SpriteLoader.getSprite( spriteId);
		tab.enabledSprite =  SpriteLoader.getSprite( spriteId);
	}

	public static void addBankItem(int index, Boolean hasOption) {
		Widget rsi = interfaceCache[index] = new Widget();
		rsi.actions = new String[5];
		rsi.spritesX = new int[20];
		rsi.inventoryAmounts = new int[30];
		rsi.inventoryItemId = new int[30];
		rsi.spritesY = new int[20];

		rsi.children = new int[0];
		rsi.childX = new int[0];
		rsi.childY = new int[0];

		// rsi.hasExamine = false;

		rsi.spritePaddingX = 24;
		rsi.spritePaddingY = 24;
		rsi.height = 5;
		rsi.width = 6;
		rsi.parent = 5292;
		rsi.id = index;
		rsi.type = 2;
	}

	public static void addHoveredButton(int i, String imageName, int j, int w, int h, int IMAGEID) {// hoverable
		// button
		Widget tab = addInterface(i);
		tab.parent = i;
		tab.id = i;
		tab.type = 0;
		tab.atActionType = 0;
		tab.width = w;
		tab.height = h;
		tab.invisible = true;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.scrollMax = 0;
		addHoverImage(IMAGEID, j, j, imageName);
		tab.totalChildren(1);
		tab.child(0, IMAGEID, 0, 0);
	}

	public static void addHoverImage(int i, int j, int k, String name) {
		Widget tab = addInterface(i);
		tab.id = i;
		tab.parent = i;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.width = 512;
		tab.height = 334;
		tab.opacity = 0;
		tab.hoverType = 52;
		tab.disabledSprite = imageLoader(j, name);
		tab.enabledSprite = imageLoader(k, name);
	}

	public static Widget addTabInterface(int id) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;// 250
		tab.parent = id;// 236
		tab.type = 0;// 262
		tab.atActionType = 0;// 217
		tab.contentType = 0;
		tab.width = 190;// 220
		tab.height = 261;// 267
		tab.opacity = (byte) 0;
		tab.hoverType = -1;// Int 230
		return tab;
	}

	public static Widget addTabInterface(int id, Widget toClone) {

		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = toClone.parent;
		tab.type = toClone.type;
		tab.atActionType = toClone.atActionType;
		tab.contentType = toClone.contentType;
		tab.width = toClone.width;
		tab.height = toClone.height;
		tab.opacity = toClone.opacity;
		tab.hoverType = toClone.hoverType;

		return tab;
	}

	private static Sprite imageLoader(int i, String s) {
		long l = (StringUtils.hashSpriteName(s) << 8) + (long) i;
		Sprite sprite = (Sprite) spriteCache.get(l);
		if (sprite != null)
			return sprite;
		try {
			sprite = new Sprite(s + "" + i);
			spriteCache.put(sprite, l);
		} catch (Exception exception) {
			return null;
		}
		return sprite;
	}

	public static Sprite getSprite(int i, FileArchive streamLoader, String s) {
		long l = (StringUtils.hashSpriteName(s) << 8) + (long) i;
		Sprite sprite = (Sprite) spriteCache.get(l);
		if (sprite != null)
			return sprite;
		try {
			sprite = new Sprite(streamLoader, s, i);
			spriteCache.put(sprite, l);
		} catch (Exception _ex) {
			return null;
		}
		return sprite;
	}

	public static void method208(boolean flag, Model model) {
		int i = 0;// was parameter
		int j = 5;// was parameter
		if (flag)
			return;
		models.clear();
		if (model != null && j != 4)
			models.put(model, (j << 16) + i);
	}

	private static void levelUpInterfaces() {
		Widget attack = interfaceCache[6247];
		Widget defence = interfaceCache[6253];
		Widget str = interfaceCache[6206];
		Widget hits = interfaceCache[6216];
		Widget rng = interfaceCache[4443];
		Widget pray = interfaceCache[6242];
		Widget mage = interfaceCache[6211];
		Widget cook = interfaceCache[6226];
		Widget wood = interfaceCache[4272];
		Widget flet = interfaceCache[6231];
		Widget fish = interfaceCache[6258];
		Widget fire = interfaceCache[4282];
		Widget craf = interfaceCache[6263];
		Widget smit = interfaceCache[6221];
		Widget mine = interfaceCache[4416];
		Widget herb = interfaceCache[6237];
		Widget agil = interfaceCache[4277];
		Widget thie = interfaceCache[4261];
		Widget slay = interfaceCache[12122];
		Widget farm = addInterface(5267);
		Widget rune = interfaceCache[4267];
		Widget cons = addInterface(7267);
		Widget hunt = addInterface(8267);
		Widget summ = addInterface(9267);
		Widget dung = addInterface(10267);
		addSkillChatSprite(29578, 0);
		addSkillChatSprite(29579, 1);
		addSkillChatSprite(29580, 2);
		addSkillChatSprite(29581, 3);
		addSkillChatSprite(29582, 4);
		addSkillChatSprite(29583, 5);
		addSkillChatSprite(29584, 6);
		addSkillChatSprite(29585, 7);
		addSkillChatSprite(29586, 8);
		addSkillChatSprite(29587, 9);
		addSkillChatSprite(29588, 10);
		addSkillChatSprite(29589, 11);
		addSkillChatSprite(29590, 12);
		addSkillChatSprite(29591, 13);
		addSkillChatSprite(29592, 14);
		addSkillChatSprite(29593, 15);
		addSkillChatSprite(29594, 16);
		addSkillChatSprite(29595, 17);
		addSkillChatSprite(29596, 18);
		addSkillChatSprite(29567, 19);
		addSkillChatSprite(29598, 20);
		addSkillChatSprite(29599, 21);
		addSkillChatSprite(29600, 22);
		addSkillChatSprite(29601, 23);
		addSkillChatSprite(29602, 24);
		setChildren(4, attack);
		setBounds(29578, 20, 30, 0, attack);
		setBounds(4268, 80, 15, 1, attack);
		setBounds(4269, 80, 45, 2, attack);
		setBounds(358, 95, 75, 3, attack);
		setChildren(4, defence);
		setBounds(29579, 20, 30, 0, defence);
		setBounds(4268, 80, 15, 1, defence);
		setBounds(4269, 80, 45, 2, defence);
		setBounds(358, 95, 75, 3, defence);
		setChildren(4, str);
		setBounds(29580, 20, 30, 0, str);
		setBounds(4268, 80, 15, 1, str);
		setBounds(4269, 80, 45, 2, str);
		setBounds(358, 95, 75, 3, str);
		setChildren(4, hits);
		setBounds(29581, 20, 30, 0, hits);
		setBounds(4268, 80, 15, 1, hits);
		setBounds(4269, 80, 45, 2, hits);
		setBounds(358, 95, 75, 3, hits);
		setChildren(4, rng);
		setBounds(29582, 20, 30, 0, rng);
		setBounds(4268, 80, 15, 1, rng);
		setBounds(4269, 80, 45, 2, rng);
		setBounds(358, 95, 75, 3, rng);
		setChildren(4, pray);
		setBounds(29583, 20, 30, 0, pray);
		setBounds(4268, 80, 15, 1, pray);
		setBounds(4269, 80, 45, 2, pray);
		setBounds(358, 95, 75, 3, pray);
		setChildren(4, mage);
		setBounds(29584, 20, 30, 0, mage);
		setBounds(4268, 80, 15, 1, mage);
		setBounds(4269, 80, 45, 2, mage);
		setBounds(358, 95, 75, 3, mage);
		setChildren(4, cook);
		setBounds(29585, 20, 30, 0, cook);
		setBounds(4268, 80, 15, 1, cook);
		setBounds(4269, 80, 45, 2, cook);
		setBounds(358, 95, 75, 3, cook);
		setChildren(4, wood);
		setBounds(29586, 20, 30, 0, wood);
		setBounds(4268, 80, 15, 1, wood);
		setBounds(4269, 80, 45, 2, wood);
		setBounds(358, 95, 75, 3, wood);
		setChildren(4, flet);
		setBounds(29587, 20, 30, 0, flet);
		setBounds(4268, 80, 15, 1, flet);
		setBounds(4269, 80, 45, 2, flet);
		setBounds(358, 95, 75, 3, flet);
		setChildren(4, fish);
		setBounds(29588, 20, 30, 0, fish);
		setBounds(4268, 80, 15, 1, fish);
		setBounds(4269, 80, 45, 2, fish);
		setBounds(358, 95, 75, 3, fish);
		setChildren(4, fire);
		setBounds(29589, 20, 30, 0, fire);
		setBounds(4268, 80, 15, 1, fire);
		setBounds(4269, 80, 45, 2, fire);
		setBounds(358, 95, 75, 3, fire);
		setChildren(4, craf);
		setBounds(29590, 20, 30, 0, craf);
		setBounds(4268, 80, 15, 1, craf);
		setBounds(4269, 80, 45, 2, craf);
		setBounds(358, 95, 75, 3, craf);
		setChildren(4, smit);
		setBounds(29591, 20, 30, 0, smit);
		setBounds(4268, 80, 15, 1, smit);
		setBounds(4269, 80, 45, 2, smit);
		setBounds(358, 95, 75, 3, smit);
		setChildren(4, mine);
		setBounds(29592, 20, 30, 0, mine);
		setBounds(4268, 80, 15, 1, mine);
		setBounds(4269, 80, 45, 2, mine);
		setBounds(358, 95, 75, 3, mine);
		setChildren(4, herb);
		setBounds(29593, 20, 30, 0, herb);
		setBounds(4268, 80, 15, 1, herb);
		setBounds(4269, 80, 45, 2, herb);
		setBounds(358, 95, 75, 3, herb);
		setChildren(4, agil);
		setBounds(29594, 20, 30, 0, agil);
		setBounds(4268, 80, 15, 1, agil);
		setBounds(4269, 80, 45, 2, agil);
		setBounds(358, 95, 75, 3, agil);
		setChildren(4, thie);
		setBounds(29595, 20, 30, 0, thie);
		setBounds(4268, 80, 15, 1, thie);
		setBounds(4269, 80, 45, 2, thie);
		setBounds(358, 95, 75, 3, thie);
		setChildren(4, slay);
		setBounds(29596, 20, 30, 0, slay);
		setBounds(4268, 80, 15, 1, slay);
		setBounds(4269, 80, 45, 2, slay);
		setBounds(358, 95, 75, 3, slay);
		setChildren(4, farm);
		setBounds(29567, 20, 30, 0, farm);
		setBounds(4268, 80, 15, 1, farm);
		setBounds(4269, 80, 45, 2, farm);
		setBounds(358, 95, 75, 3, farm);
		setChildren(4, rune);
		setBounds(29598, 20, 30, 0, rune);
		setBounds(4268, 80, 15, 1, rune);
		setBounds(4269, 80, 45, 2, rune);
		setBounds(358, 95, 75, 3, rune);
		setChildren(4, cons);
		setBounds(29599, 20, 30, 0, cons);
		setBounds(4268, 80, 15, 1, cons);
		setBounds(4269, 80, 45, 2, cons);
		setBounds(358, 95, 75, 3, cons);
		setChildren(4, hunt);
		setBounds(29600, 20, 30, 0, hunt);
		setBounds(4268, 80, 15, 1, hunt);
		setBounds(4269, 80, 45, 2, hunt);
		setBounds(358, 95, 75, 3, hunt);
		setChildren(4, summ);
		setBounds(29601, 20, 30, 0, summ);
		setBounds(4268, 80, 15, 1, summ);
		setBounds(4269, 80, 45, 2, summ);
		setBounds(358, 95, 75, 3, summ);
		setChildren(4, dung);
		setBounds(29602, 20, 30, 0, dung);
		setBounds(4268, 80, 15, 1, dung);
		setBounds(4269, 80, 45, 2, dung);
		setBounds(358, 95, 75, 3, dung);
	}

	public static void addSkillChatSprite(int id, int skill) {
		addSpriteLoader(id, 456 + skill);
	}

	public static void setChildren(int total, Widget i) {
		i.children = new int[total];
		i.childX = new int[total];
		i.childY = new int[total];
	}

	public static void setBounds(int ID, int X, int Y, int frame, Widget r) {
		r.children[frame] = ID;
		r.childX[frame] = X;
		r.childY[frame] = Y;
	}

	public static Widget addButton(int i, int j, String name, int W, int H, String S, int AT) {
		Widget RSInterface = addInterface(i);
		RSInterface.id = i;
		RSInterface.parent = i;
		RSInterface.type = 5;
		RSInterface.atActionType = AT;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 52;
		RSInterface.disabledSprite = imageLoader(j, name);
		RSInterface.enabledSprite = imageLoader(j, name);
		RSInterface.width = W;
		RSInterface.height = H;
		RSInterface.tooltip = S;

		return RSInterface;
	}

	public static void addSprites(int ID, int i, int i2, String name, int configId, int configFrame) {
		Widget tab = addInterface(ID);
		tab.id = ID;
		tab.parent = ID;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.width = 512;
		tab.height = 334;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.valueCompareType = new int[1];
		tab.requiredValues = new int[1];
		tab.valueCompareType[0] = 1;
		tab.requiredValues[0] = configId;
		tab.valueIndexArray = new int[1][3];
		tab.valueIndexArray[0][0] = 5;
		tab.valueIndexArray[0][1] = configFrame;
		tab.valueIndexArray[0][2] = 0;
		tab.disabledSprite = imageLoader(i, name);
		tab.enabledSprite = imageLoader(i2, name);
	}

    public static void addCloseButton(int id, int enabledSprite, int disabledSprite) {
        Widget b = addInterface(id);
        b.id = id;
        b.parent = id;
        b.type = TYPE_HOVER_SPRITE;
        b.atActionType = OPTION_CLOSE;
        b.disabledSprite = b.enabledSprite =  SpriteLoader.getSprite( enabledSprite);
        b.hoverSprite1 = b.hoverSprite2 =  SpriteLoader.getSprite( disabledSprite);
        b.width = b.disabledSprite.myWidth;
        b.height = b.disabledSprite.myHeight;
    }

	public static void hoverButton(int id, String tooltip, int enabledSprite, int disabledSprite) {
		hoverButton(id, tooltip, enabledSprite, disabledSprite, 255);
	}

	public static void hoverButton(int id, String tooltip, int enabledSprite, int disabledSprite, int opacity) {
		Widget tab = addInterface(id);
		tab.tooltip = tooltip;
		tab.atActionType = 1;
		tab.type = TYPE_HOVER;
		tab.enabledSprite =  SpriteLoader.getSprite( enabledSprite);
		tab.disabledSprite =  SpriteLoader.getSprite( disabledSprite);
		tab.width = tab.enabledSprite.myWidth;
		tab.height = tab.disabledSprite.myHeight;
		tab.toggled = false;
		tab.spriteOpacity = opacity;
	}

	public static void hoverButton(int id, String tooltip, int enabledSprite, int disabledSprite, String buttonText,
			RSFont rsFont, int colour, int hoveredColour, boolean centerText) {
		Widget tab = addInterface(id);
		tab.tooltip = tooltip;
		tab.atActionType = 1;
		tab.type = TYPE_HOVER;
		tab.enabledSprite =  SpriteLoader.getSprite( enabledSprite);
		tab.disabledSprite =  SpriteLoader.getSprite( disabledSprite);
		tab.width = tab.enabledSprite.myWidth;
		tab.height = tab.disabledSprite.myHeight;
		tab.msgX = tab.width / 2;
		tab.msgY = (tab.height / 2) + 4;
		tab.defaultText = buttonText;
		tab.toggled = false;
		tab.rsFont = rsFont;
		tab.textColor = colour;
		tab.defaultHoverColor = hoveredColour;
		tab.centerText = centerText;
		tab.spriteOpacity = 255;
	}

	public static void addPixels(int id, int color, int width, int height, int alpha, boolean filled) {
		Widget rsi = addInterface(id);
		rsi.type = TYPE_RECTANGLE;
		rsi.opacity = (byte) alpha;
		rsi.textColor = color;
		rsi.defaultHoverColor = color;
		rsi.secondaryHoverColor = color;
		rsi.secondaryColor = color;
		rsi.filled = filled;
		rsi.width = width;
		rsi.height = height;
	}

	public static void addPixels(int id, int color, int hoverColor, int width, int height, int alpha, boolean filled) {
		Widget rsi = addInterface(id);
		rsi.type = TYPE_RECTANGLE;
		rsi.opacity = (byte) alpha;
		rsi.textColor = color;
		rsi.textColor = color;
		rsi.defaultHoverColor = color;
		rsi.secondaryHoverColor = color;
		rsi.secondaryColor = color;
		rsi.filled = filled;
		rsi.width = width;
		rsi.height = height;
		rsi.contentType = 0;
		rsi.atActionType = 1;
		rsi.hoverType = id;
	}

	public static void configButton(int id, String tooltip, int enabledSprite, int disabledSprite) {
		Widget tab = addInterface(id);
		tab.tooltip = tooltip;
		tab.atActionType = OPTION_OK;
		tab.type = TYPE_CONFIG;
		tab.enabledSprite =  SpriteLoader.getSprite( enabledSprite);
		tab.disabledSprite =  SpriteLoader.getSprite( disabledSprite);
		tab.width = tab.enabledSprite.myWidth;
		tab.height = tab.disabledSprite.myHeight;
		tab.active = false;
	}

	public static void adjustableConfig(int id, String tooltip, int sprite, int opacity, int enabledSpriteBehind,
			int disabledSpriteBehind) {
		Widget tab = addInterface(id);
		tab.tooltip = tooltip;
		tab.atActionType = OPTION_OK;
		tab.type = TYPE_ADJUSTABLE_CONFIG;
		tab.enabledSprite =  SpriteLoader.getSprite( sprite);
		tab.enabledAltSprite =  SpriteLoader.getSprite( enabledSpriteBehind);
		tab.disabledAltSprite =  SpriteLoader.getSprite( disabledSpriteBehind);
		tab.width = tab.enabledAltSprite.myWidth;
		tab.height = tab.disabledAltSprite.myHeight;
		tab.spriteOpacity = opacity;
	}

	public static void configHoverButton(int id, String tooltip, int enabledSprite, int disabledSprite,
			int enabledAltSprite, int disabledAltSprite) {
		Widget tab = addInterface(id);
		tab.tooltip = tooltip;
		tab.atActionType = OPTION_OK;
		tab.type = TYPE_CONFIG_HOVER;
		tab.enabledSprite =  SpriteLoader.getSprite( enabledSprite);
		tab.disabledSprite =  SpriteLoader.getSprite( disabledSprite);
		tab.width = tab.enabledSprite.myWidth;
		tab.height = tab.disabledSprite.myHeight;
		tab.enabledAltSprite =  SpriteLoader.getSprite( enabledAltSprite);
		tab.disabledAltSprite =  SpriteLoader.getSprite( disabledAltSprite);
		tab.spriteOpacity = 255;
	}

	public static void configHoverButton(int id, String tooltip, int enabledSprite, int disabledSprite,
			int enabledAltSprite, int disabledAltSprite, boolean active, int... buttonsToDisable) {
		Widget tab = addInterface(id);
		tab.tooltip = tooltip;
		tab.atActionType = OPTION_OK;
		tab.type = TYPE_CONFIG_HOVER;
		tab.enabledSprite =  SpriteLoader.getSprite( enabledSprite);
		tab.disabledSprite =  SpriteLoader.getSprite( disabledSprite);
		tab.width = tab.enabledSprite.myWidth;
		tab.height = tab.disabledSprite.myHeight;
		tab.enabledAltSprite =  SpriteLoader.getSprite( enabledAltSprite);
		tab.disabledAltSprite =  SpriteLoader.getSprite( disabledAltSprite);
		tab.buttonsToDisable = buttonsToDisable;
		tab.active = active;
		tab.spriteOpacity = 255;
	}

	public static void configHoverButton(int id, String tooltip, int enabledSprite, int disabledSprite,
			int enabledAltSprite, int disabledAltSprite, boolean active, String buttonText, RSFont rsFont, int colour,
			int hoveredColour, boolean centerText, int... buttonsToDisable) {
		Widget tab = addInterface(id);
		tab.tooltip = tooltip;
		tab.atActionType = OPTION_OK;
		tab.type = TYPE_CONFIG_HOVER;
		tab.enabledSprite =  SpriteLoader.getSprite( enabledSprite);
		tab.disabledSprite =  SpriteLoader.getSprite( disabledSprite);
		tab.width = tab.enabledSprite.myWidth;
		tab.height = tab.disabledSprite.myHeight;
		tab.enabledAltSprite =  SpriteLoader.getSprite( enabledAltSprite);
		tab.disabledAltSprite =  SpriteLoader.getSprite( disabledAltSprite);
		tab.buttonsToDisable = buttonsToDisable;
		tab.active = active;
		tab.msgX = tab.width / 2;
		tab.msgY = (tab.height / 2) + 4;
		tab.defaultText = buttonText;
		tab.rsFont = rsFont;
		tab.textColor = colour;
		tab.defaultHoverColor = hoveredColour;
		tab.centerText = centerText;
		tab.spriteOpacity = 255;
	}

	public static void handleConfigHover(Widget widget) {
		if (widget.active) {
			return;
		}
		widget.active = true;

		configHoverButtonSwitch(widget);
		disableOtherButtons(widget);
	}

	public static void configHoverButtonSwitch(Widget widget) {
		Sprite[] backup = new Sprite[] { widget.enabledSprite, widget.disabledSprite };

		widget.enabledSprite = widget.enabledAltSprite;
		widget.disabledSprite = widget.disabledAltSprite;

		widget.enabledAltSprite = SpriteLoader.getSprite(backup, 0);
		widget.disabledAltSprite = SpriteLoader.getSprite(backup, 1);
	}

	public static void disableOtherButtons(Widget widget) {
		if (widget.buttonsToDisable == null) {
			return;
		}
		for (int btn : widget.buttonsToDisable) {
			Widget btnWidget = interfaceCache[btn];

			if (btnWidget.active) {

				btnWidget.active = false;
				configHoverButtonSwitch(btnWidget);
			}
		}
	}

	public static void slider(int id, double min, double max, int width, int icon, int background, int contentType) {
		Widget widget = addInterface(id);
		widget.width = width;
		widget.height = 14;
		widget.slider = new Slider( SpriteLoader.getSprite( icon),  SpriteLoader.getSprite( background), min, max);
		widget.type = TYPE_SLIDER;
		widget.contentType = contentType;
	}

	public static void keybindingDropdown(int id, int width, int defaultOption, String[] options, Dropdown d,
			boolean inverted) {
		Widget widget = addInterface(id);
		widget.type = TYPE_KEYBINDS_DROPDOWN;
		widget.dropdown = new DropdownMenu(width, true, defaultOption, options, d);
		widget.atActionType = OPTION_DROPDOWN;
		widget.inverted = inverted;
	}

	public static void dropdownMenu(int id, int width, int defaultOption, String[] options, Dropdown d) {
		dropdownMenu(id, width, defaultOption, options, d,
				new int[] { 0x0d0d0b, 0x464644, 0x473d32, 0x51483c, 0x787169 }, false);
	}

	public static void dropdownMenu(int id, int width, int defaultOption, String[] options, Dropdown d,
			int[] dropdownColours, boolean centerText) {
		Widget menu = addInterface(id);
		menu.type = TYPE_DROPDOWN;
		menu.dropdown = new DropdownMenu(width, false, defaultOption, options, d);
		menu.atActionType = OPTION_DROPDOWN;
		menu.dropdownColours = dropdownColours;
		menu.centerText = centerText;
	}

	public static void rotatingSprite(int id, int spriteId) {
		Widget widget = interfaceCache[id] = new Widget();
		widget.id = id;
		widget.parent = id;
		widget.type = TYPE_ROTATING;
		widget.atActionType = 0;
		widget.contentType = 0;
		widget.disabledSprite =  SpriteLoader.getSprite( spriteId);
		widget.enabledSprite =  SpriteLoader.getSprite( spriteId);
		widget.width = widget.disabledSprite.myWidth;
		widget.height = widget.enabledSprite.myHeight - 2;
	}

	public static void addTicker(int id) {
		Widget widget = interfaceCache[id] = new Widget();
		widget.id = id;
		widget.parent = id;
		widget.type = TYPE_TICKER;
		widget.atActionType = 0;
		widget.contentType = 0;
	}

	public static void addItemModel(int id, int item, int w, int h, int zoom) {
		addItemModel(id, item, w, h, zoom, 0, 0);
	}

	public static void addItemModel(int id, int item, int w, int h, int zoom, int rotationOffsetX, int rotationOffsetY) {
		Widget widget = addInterface(id);
		widget.contentType = 329;
		widget.type = TYPE_MODEL;
		widget.defaultMediaType = 4;
		widget.defaultMedia = item;
		widget.width = w;
		widget.height = h;
		Js5.whenLoaded(() -> {
			if (widget.defaultMedia != -1) {
				ItemDefinition itemDef = ItemDefinition.lookup(item);

				if (itemDef == null) {
					return;
				}

				widget.modelXAngle = itemDef.rotation_y + rotationOffsetY;
				widget.modelYAngle = itemDef.rotation_x + rotationOffsetX;
				widget.modelZoom = (ItemDefinition.lookup(item).model_zoom * 100) / zoom;
			}
		});
	}
	public static void addModel(int id, int item, int w, int h, int zoom, int xan, int yan, int zan, int anim) {
		Widget widget = addInterface(id);
		widget.contentType = 329;
		widget.type = TYPE_MODEL;
		widget.defaultMediaType = 1;
		widget.defaultMedia = item;
		widget.width = w;
		widget.height = h;
		widget.modelXAngle = xan;
		widget.modelYAngle = yan;
		widget.modelZAngle = zan;
		widget.defaultAnimationId = anim;
		widget.modelZoom = zoom;
	}

	static void addBox(int id, int w, int h, int colour, int hoverColour, String tooltip) {
		Widget box = addInterface(id);
		box.type = TYPE_BOX;
		box.width = w;
		box.height = h;
		box.atActionType = 1;
		box.tooltip = tooltip;
		box.defaultHoverColor = colour;
		box.secondaryHoverColor = hoverColour;
	}

	public static void addWorldMap(int id) {
		Widget box = addInterface(id);
		box.type = TYPE_MAP;
		box.width = 400;
		box.height = 400;
		box.atActionType = 1;
	}

	private static void worldMap() {
		Widget map = addInterface(54000);
		map.totalChildren(3);

		addWorldMap(54001);
		addSpriteLoader(54002, 568);
		addCloseButton(54003, 569, 570);

		setBounds(54001, 0, -52, 0, map);
		setBounds(54002, 46, 0, 1, map);
		setBounds(54003, 431, 10, 2, map);
	}

	public void swapInventoryItems(int i, int j) {
		int id = inventoryItemId[i];
		inventoryItemId[i] = inventoryItemId[j];
		inventoryItemId[j] = id;
		id = inventoryAmounts[i];
		inventoryAmounts[i] = inventoryAmounts[j];
		inventoryAmounts[j] = id;
	}

	public void totalChildren(int id, int x, int y) {
		children = new int[id];
		childX = new int[x];
		childY = new int[y];
	}

	public void child(int id, int interID, int x, int y) {
		children[id] = interID;
		childX[id] = x;
		childY[id] = y;
	}

	public void totalChildren(int t) {
		children = new int[t];
		childX = new int[t];
		childY = new int[t];
	}

	public Model method209(Animation anim, int frameId, boolean flag) {
		int var7;
		int var8;
		if (flag) {
			var7 = modelType;
			var8 = modelId;
		} else {
			var7 = defaultMediaType;
			var8 = defaultMedia;
		}
		long var9 = (var7 << 16) + var8;
		Model var11 = (Model) models.get(var9);
		if (var11 == null) {
			ModelData var12 = null;
			int var13 = 64;
			int var14 = 768;
			switch (var7) {
				case 1:
					var12 = ModelData.ModelData_get(OsCache.indexCache7, var8, 0);
					break;
				case 2:
					var12 = NpcDefinition.lookup(var8).method3670();
					break;
				case 3:
					var12 = Client.localPlayer.getModelData();
					break;
				case 4:
					ItemDefinition var15 = ItemDefinition.lookup(var8);
					var12 = var15.getModelData(10);
					var13 += var15.light_intensity;
					var14 += var15.light_mag;
					break;
				case 5:
				default:
					break;
				case 6: // Custom type for item model on interface to use dummy player colors
					ColorfulItem colorfulItem = null;
					if (ColorfulItem.getItemIds().containsKey(var8)) {
						colorfulItem = ColorfulItem.forId(var8);
					} else if (ColorfulItem.getCopyItemIds().containsKey(var8)) {
						colorfulItem = ColorfulItem.forCopyId(var8);
					}
					ItemDefinition itemDef = ItemDefinition.lookup(colorfulItem.getItemId());
					var12 = itemDef.getModelData(1);
					var13 += itemDef.light_intensity;
					var14 += itemDef.light_mag;
					var12 = ItemColorCustomizer.getColorfulItemData(var12, ClientCompanion.dummyPlayer, var8, Client.localPlayer.getGender());
					break;
			}

			if (var12 == null) {
				return null;
			}

			var11 = var12.toModel(var13, var14, -50, -10, -50);
			models.put(var11, var9);
		}

		if (anim != null) {
			var11 = anim.transformWidgetModel(var11, frameId);
		}

		var11.calculateBoundsCylinder();
		return var11;
	}

	public static void createTooltip(int id, int width, int height, GameFont[] font, String tooltip) {
		Widget rsi = addInterface(id);
		rsi.type = 8;
		rsi.inventoryHover = true;
		rsi.defaultText = tooltip;
		rsi.textDrawingAreas = font[1];
		rsi.width = width;
		rsi.height = height;
	}

	public static void insertNewChild(Widget widget, int id, int x, int y) {
		int[] newChildren = new int[widget.children.length + 1];
		int[] newChildX = new int[widget.childX.length + 1];
		int[] newChildY = new int[widget.childY.length + 1];

		System.arraycopy(widget.children, 0, newChildren, 0, widget.children.length);
		System.arraycopy(widget.childX, 0, newChildX, 0, widget.childX.length);
		System.arraycopy(widget.childY, 0, newChildY, 0, widget.childY.length);

		widget.children = newChildren;
		widget.childX = newChildX;
		widget.childY = newChildY;
		widget.children[widget.children.length - 1] = id;
		widget.childX[widget.childX.length - 1] = x;
		widget.childY[widget.childY.length - 1] = y;
	}

	public static void insertNewChild(Widget widget, int index, int id, int x, int y) {
		int[] newChildren = new int[widget.children.length + 1];
		int[] newChildX = new int[widget.childX.length + 1];
		int[] newChildY = new int[widget.childY.length + 1];

		System.arraycopy(widget.children, 0, newChildren, 0, index);
		System.arraycopy(widget.childX, 0, newChildX, 0, index);
		System.arraycopy(widget.childY, 0, newChildY, 0, index);

		System.arraycopy(widget.children, index, newChildren, index + 1, widget.children.length - index);
		System.arraycopy(widget.childX, index, newChildX, index + 1, widget.children.length - index);
		System.arraycopy(widget.childY, index, newChildY, index + 1, widget.children.length - index);

		newChildren[index] = id;
		newChildX[index] = x;
		newChildY[index] = y;

		widget.children = newChildren;
		widget.childX = newChildX;
		widget.childY = newChildY;
	}

	public static void swapChildrenIndexes(Widget widget, int fromIndex, int toIndex) {
		int toIndexId = widget.children[toIndex];
		int toIndexX = widget.childX[toIndex];
		int toIndexY = widget.childY[toIndex];

		widget.children[toIndex] = widget.children[fromIndex];
		widget.childX[toIndex] = widget.childX[fromIndex];
		widget.childY[toIndex] = widget.childY[fromIndex];

		widget.children[fromIndex] = toIndexId;
		widget.childX[fromIndex] = toIndexX;
		widget.childY[fromIndex] = toIndexY;

	}

	private static void shiftChildrenIds(Widget widget, int direction, int startIndex, int endIndex) {
		for (int index = startIndex; index < endIndex; index++) {
			widget.children[index] = widget.children[index + direction];
		}
	}

	public static void extendChildren(Widget widget, int extendBy) {
		int[] childIds = new int[widget.children.length + extendBy];
		int[] childX = new int[widget.childX.length + extendBy];
		int[] childY = new int[widget.childY.length + extendBy];

		System.arraycopy(widget.children, 0, childIds, 0, widget.children.length);
		System.arraycopy(widget.childX, 0, childX, 0, widget.childX.length);
		System.arraycopy(widget.childY, 0, childY, 0, widget.childY.length);

		widget.totalChildren(childIds.length);

		System.arraycopy(childIds, 0, widget.children, 0, childIds.length - extendBy);
		System.arraycopy(childX, 0, widget.childX, 0, childX.length - extendBy);
		System.arraycopy(childY, 0, widget.childY, 0, childY.length - extendBy);
	}

	public static void removeChild(Widget widget, int index) {
		int[] childIds = new int[widget.children.length - 1];
		int[] childX = new int[widget.childX.length - 1];
		int[] childY = new int[widget.childY.length - 1];
		int i = 0;
		for (int c = 0; c < widget.children.length; c++) {
			if (c == index) {
				continue;
			}
			childIds[i] = widget.children[c];
			childX[i] = widget.childX[c];
			childY[i] = widget.childY[c];
			i++;
		}
		widget.totalChildren(childIds.length);
		System.arraycopy(childIds, 0, widget.children, 0, childIds.length);
		System.arraycopy(childX, 0, widget.childX, 0, childX.length);
		System.arraycopy(childY, 0, widget.childY, 0, childY.length);
	}

	public static void printChildren(Widget widget, String parentName) {
		for (int i = 0; i < widget.children.length; i++) {
			System.out.println(parentName + ".child(child++, " + widget.children[i] + ", " + widget.childX[i] + ", " + widget.childY[i] + ");");
		}
	}

	public static int getChildX(int id) {
		Widget w = interfaceCache[interfaceCache[id].parent];
		for (int i = 0; i < w.children.length; i++)
			if (id == w.children[i])
				return w.childX[i];
		return 0;
	}

	public static int getChildY(int id) {
		Widget w = interfaceCache[interfaceCache[id].parent];
		for (int i = 0; i < w.children.length; i++)
			if (id == w.children[i])
				return w.childY[i];
		return 0;
	}

	public static void Rules(GameFont[] tda) {
		Widget tab = addTabInterface(25000);
		Widget scroll = addTabInterface(25005);

		addSprite(25001, 0, "Interfaces/Rules/BG");
		addText(25002, "test", tda, 2, 0xAF6A1A, true, true);

		tab.totalChildren(1);
		tab.child(0, 25001, 5, 10);
		//tab.child(1, 25002, 250, 100);

		scroll.totalChildren(1);
		scroll.child(0, 25002, 250, 100);
		scroll.width = 129;
		scroll.height = 188;
		scroll.scrollMax = 400;

	}
	public static void slayerLogInterface(GameFont[] tda) {
		int id = 59250;
		Widget log = addInterface(id++);
		log.totalChildren(6);
		int child = 0;

		int width = 472;
		int height = 294;
		int x = 20;
		int y = 20;
		addBackground(id, width, height, true, AutomaticSprite.BACKGROUND_BROWN);
		log.child(child++, id++, x, y);

		addText(id, "Slayer Kill Log", tda, 2, 0xff981f);
		log.child(child++, id++, x + 189, y + 10);

		log.child(child++, 43702, x + width - 26, y + 10); // Close button

		Widget scroll = addInterface(id);
		scroll.width = 452 - 16;
		scroll.height = scroll.scrollMax = 244;
		log.child(child++, id++, x + 10, y + 40);
		int lines = 100;
		scroll.totalChildren(4 + (lines * 4) + (lines / 2));
		int scrollChild = 0;

			addText(id, "Monster", tda, 2, 0xff981f);
			scroll.child(scrollChild++, id++, 0, 0);

			addRightAlignedText(id, "Kills", tda, 2, 0xff981f);
			scroll.child(scrollChild++, id++, 305, 0);

			addRightAlignedText(id, "Streak", tda, 2, 0xff981f);
			scroll.child(scrollChild++, id++, 390, 0);

			addHorizontalLine(id, 433, 0);
			scroll.child(scrollChild++, id++, 2, 18);

			int currentY = 20;
			for (int i = 0; i < lines; i++) {
				int rowHeight = 18;
				if (i % 2 == 0) {
					addTransparentRectangle(id, 436, rowHeight, 0xffffff, true, 10);
					scroll.child(scrollChild++, id++, 0, currentY);
				} else {
					id++;
				}

				addText(id, "" + id, tda, 1, 0xff981f);
				scroll.child(scrollChild++, id++, 0, currentY);

				addRightAlignedText(id, "" + id, tda, 1, 0xff981f);
				scroll.child(scrollChild++, id++, 305, currentY);

				addRightAlignedText(id, "" + id, tda, 1, 0xff981f);
				scroll.child(scrollChild++, id++, 390, currentY);

				addHoverButton(id, 737, 995, "Reset Streak");
				scroll.child(scrollChild++, id++, 404, currentY);

				currentY += rowHeight;
			}


		addVerticalLine(id, 244, 0);
		log.child(child++, id++, x + 213, y + 40);

		addVerticalLine(id, 244, 0);
		log.child(child++, id++, x + 317, y + 40);
	}

	public static void CollectionLog() {
		Widget widget = interfaceCache[64627];
		for(int i = 0; i < 36; i++) {
			removeChild(widget, i);
		}
		addToItemGroup(64628, 6, 35 , 9, 5, false, "", "", "");

		widget.child(0, 64628, 7, 5);
	}
	public static void slayerInterfaces(GameFont[] t) {
		slayerLogInterface(t);
		int id = 60101;
		int frame = 0;

		/*
		 * The unlock
		 */
		Widget unlock = addInterface(id);
		// System.out.println("extend id: " + unlock.id);
		unlock.totalChildren(4);
		id++;

		addSpriteLoader(id, 686);
		setBounds(id, 15, 15, frame, unlock);
		frame++;
		id++;

		addSpriteLoader(id, 692);
		setBounds(id, 112, 51, frame, unlock);
		frame++;
		id++;

		setBounds(62100, 0, 0, frame, unlock);
		frame++;

		setBounds(62243, 26, 72, frame, unlock);
		frame++;

		id++;
		frame = 0;

		/*
		 * Confirm
		 */
		Widget confirm = addInterface(id);
		// System.out.println("confirm id: " + confirm.id);
		confirm.totalChildren(7);
		id++;

		addSpriteLoader(id, 733);
		setBounds(id, 15, 15, frame, confirm);
		frame++;
		id++;

		addText(id, id + "Bloodveld", t, 1, 0xEE9021, true, true);
		setBounds(id, 250, 100, frame, confirm);
		frame++;
		id++;

		addText(id,
				id + "Your current task will be cancelled, and the\\nSlayer Masters will be blocked from\\nassigning this category to you again.",
				t, 1, 0xEE9021, true, true);
		setBounds(id, 250, 130, frame, confirm);
		frame++;
		id++;

		addText(id, id + "Cost: 100 points", t, 1, 0xff0000, true, true);
		setBounds(id, 250, 190, frame, confirm);
		frame++;
		id++;

		addText(id, id + "If you unblock this creature in future, you\\nwill not get your points back", t, 1, 0xEE9021,
				true, true);
		setBounds(id, 250, 220, frame, confirm);
		frame++;
		id++;

		addHoverClickText(id, "Back", "Go back", t, 0, 0xEE9021, false, true, 30);
		setBounds(id, 180, 261, frame, confirm);
		frame++;
		id++;

		addHoverClickText(id, "Confirm", "Confirm selection", t, 0, 0xEE9021, false, true, 50);
		setBounds(id, 285, 261, frame, confirm);
		frame++;
		id++;

		id++;
		frame = 0;

		/*
		 * Extending
		 */
		Widget extend = addInterface(id);
		// System.out.println("unlock id: " + extend.id);
		extend.totalChildren(4);
		id++;

		addSpriteLoader(id, 686);
		setBounds(id, 15, 15, frame, extend);
		frame++;
		id++;

		addSpriteLoader(id, 692);
		setBounds(id, 26, 51, frame, extend);
		frame++;
		id++;

		setBounds(62100, 0, 0, frame, extend);
		frame++;

		setBounds(62109, 26, 72, frame, extend);
		frame++;

		id++;
		frame = 0;

		/*
		 * The shop
		 */
		Widget shop = addInterface(id);
		// System.out.println("shop id: " + shop.id);
		id++;
		shop.totalChildren(40);

		addSpriteLoader(id, 686);
		setBounds(id, 15, 15, frame, shop);
		frame++;
		id++;

		addSpriteLoader(id, 692);
		setBounds(id, 198, 51, frame, shop);
		frame++;
		id++;

		setBounds(62100, 0, 0, frame, shop);
		frame++;

		// System.out.println("container id: " + id);
		addSlayerItems(id, id, new String[] { "Check Value", "Buy 1", "Buy 5", "Buy 10", "Buy X" });

		int scrollId = 22_653;

		Widget scroll = addInterface(scrollId);
		scroll.height = 228;
		scroll.width = 438;
		scroll.scrollMax = 2000;
		scroll.totalChildren(1);
		setBounds(id, 15, 15, 0, scroll);

		setBounds(scrollId, 30, 75, frame, shop);
		frame++;
		id++;

		int shop_x = 50;
		int shop_y = 110;
		for (int i = 0; i < 36; i++) {
			addText(id, "", t, 0, 0xEE9021, true, true);
			setBounds(id, shop_x, shop_y, frame, shop);
			frame++;
			id++;

			shop_x += 50;

			if (shop_x == 500) {
				shop_y += 50;
				shop_x = 50;
			}

		}

		id++;
		frame = 0;

		/*
		 * Task interface
		 */
		Widget tasks = addInterface(id);
		// System.out.println("task id: " + tasks.id);

		tasks.totalChildren(18);
		id++;

		addSpriteLoader(id, 713);
		setBounds(id, 15, 15, frame, tasks);
		frame++;
		id++;

		addSpriteLoader(id, 692);
		setBounds(id, 311 - 27, 51, frame, tasks);
		frame++;
		id++;

		setBounds(62100, 0, 0, frame, tasks);
		frame++;

		int task_y = 178;

		for (int i = 0; i < 6; i++) {
			addText(id, "" + id, t, 1, 0xEE9021, false, true);
			setBounds(id, 246, task_y, frame, tasks);
			frame++;
			id++;

			addHoverText(id, "Unblock task", "Unblock task", t, 0, 0xff0000, false, true, 40);
			setBounds(id, 385, task_y + 2, frame, tasks);
			frame++;
			id++;

			task_y += 22;
		}

		addHoverText(id, "Cancel task", "Cancel task", t, 0, 0xEE9021, false, true, 40);
		setBounds(id, 314, 139, frame, tasks);
		frame++;
		id++;

		addHoverText(id, "Block task", "Cancel task", t, 0, 0xEE9021, false, true, 40);
		setBounds(id, 415, 139, frame, tasks);
		frame++;
		id++;

		addText(id, "" + id, t, 1, 0xffffff, true, true);
		setBounds(id, 158, 140, frame, tasks);
		frame++;
		id++;

		/*
		 * The text
		 */
		Widget text = addInterface(62100);
		text.totalChildren(7);
		id = 62101;
		frame = 0;

		addText(id, id + "", t, 0, 0xEE9021, true, true);
		setBounds(id, 474, 55, frame, text);
		frame++;
		id++;

		addHoverClickText(id, "Unlock", "Select - Unlock", t, 0, 0xEE9021, false, true, 40);
		setBounds(id, 51, 56, frame, text);
		frame++;
		id++;

		addHoverClickText(id, "Extend", "Select - Extend", t, 0, 0xEE9021, false, true, 50);
		setBounds(id, 136, 56, frame, text);
		frame++;
		id++;

		addHoverClickText(id, "Buy", "Select - Buy", t, 0, 0xEE9021, false, true, 30);
		setBounds(id, 229, 56, frame, text);
		frame++;
		id++;

		addHoverClickText(id, "Tasks", "Select - Tasks", t, 0, 0xEE9021, false, true, 50);
		setBounds(id, 311, 56, frame, text);
		frame++;
		id++;

		addHoverButton_sprite_loader(id, 693, 21, 21, "Exit", -1, id + 1, 3);
		setBounds(id, 475, 22, frame, text);
		frame++;

		addHoveredButton_sprite_loader(id + 1, 694, 21, 21, id + 2);
		setBounds(id + 1, 475, 22, frame, text);
		frame++;
		id += 3;

		/*
		 * Unlock scroll
		 */
		Widget unlock_scroll = addInterface(id);
		 //System.out.println("unlock scroll id: " + id);
		String[] NAME = { "Gargoyle smasher", "Slug Salter",

				"Reptile freezer", "'Shroom spayer", "Broader fletching", "Malevolent masquerade",

				"Ring bling", "Seeing red", "I hope you mith me", "Watch the birdie",

				"Hot stuff", "Reptile got ripped", "Like a boss", "King black bonnet", "Kalphite khat", "Unholy helmet",

				"Bigger and Badder", "Undead Head", "Twisted Vision"

		};

		String[] DESCRIPTION = {
				"Automatically smash gargoyless when\\nthey're on critical health, if you have the\\nright tool. @red@(120 points)",
				"Automatically salt rock slugs whe they're\\non critical health, if you have salt. @red@(80\\n@red@points)",
				"Automatically freeze desert lizards when\\nthey're on critical health, if you have ice\\nwater. @red@(90 points)",
				"Automatically spray mutated zygomites\\nwhen they're on critical health, if you\\nhave fungicide. @red@(110 points)",
				"Learn to fletch broad arrows (with level 52\\nFletching) and broad bolts (with level 55\\nFletching). @red@(300 points)",
				"Learn to combine the protective Slayer\\nheadgear and Slayer gem into one\\nuniversal helmet, with level 55 Crafting.\\n@red@(400 points)",
				"Learn to craft your own Slayer Rings, with\\nlevel 75 crafting. @red@(300 points)",
				"Duradel and Nieve will be able to assign\\nRed Dragons as your task. @red@(50 points)",
				"Duradel and Nieve will be able to assign\\nMithril Dragons as your task. @red@(80 points)",
				"Duradel, Nieve and Chaeldar will be able\\nto assign Aviansies as your task. @red@(80\\n@red@points)",

				"Duradel and Nieve will be able to assign\\nTzHaar as your task. You may also be\\noffered a chance to slay TzTok-Jad. @red@(100\\n@red@points)",
				"Duradel, Nieve and chaeldar will be able\\nto assign you Lizardmen. You need\\nShayzien House favour to fight lizardmen.\\n@red@(75 points)",
				"Duradel and Nieve will be able to assign\\nboss monsters as your task. They will\\nchoose which boss you must kill. @red@(200\\n@red@points)",
				"Learn how to combine a KBD head with your\\nslayer helm to colour it black. @red@(1000 points)",
				"Learn how to combine a Kalphite Queen\\nhead with your slayer helm to colour it\\ngreen. @red@(1000 points)",
				"Learn how to combine an Abysssal Demon\\nhead with your slayer helm to colour it red.\\n @red@(1000 points)",
				"Increase the risk against certain slayer \\nmonsters with the chance of a superior \\nversion spawning whilst on a Slayer task.\\n@red@(150 points)",
				"Learn how to combine Vorkath's head with\\nyour slayer helm to colour it turquoise.\\n@red@(1000 points)",
				"Learn how to combine Twisted Horns with\\nyour slayer helm to theme it like the Great\\nOlm. @red@(1000 points)"

		};

		boolean[] size = { true, true, true, true, true, true,

				false, false,

				true, true, true, true,

				true, true, true, true, true, true, true };

		unlock_scroll.totalChildren(5 * NAME.length);

		id++;
		int sprite = 697;

		int purchasedConfig = 1100;

		int x = 0;
		int y = 0;
		frame = 0;

		for (int i = 0; i < NAME.length; i++) {

			addHoverButton(id, size[i] ? 687 : 689, size[i] ? 688 : 690, "Unlock");
			//addHoverButton_sprite_loader(id, size[i] ? 687 : 689, 224, 84, "Unlock", -1, id + 1, 1);
			setBounds(id, x, y, frame, unlock_scroll);
			frame++;

			/*addHoveredButton_sprite_loader(id + 1, size[i] ? 688 : 690, 224, size[i] ? 84 : 64, id + 2);
			setBounds(id + 1, x, y, frame, unlock_scroll);
			frame++;*/

			id += 3;

			//System.out.println(NAME[i]);

			addText(id, NAME[i], t, 1, 0xEE9021, true, true);
			setBounds(id, x + 141, y + 12, frame, unlock_scroll);
			frame++;
			id++;

			addText(id, DESCRIPTION[i], t, 0, 0xEE9021, false, true);
			setBounds(id, x + 5, y + (size[i] ? 40 : 39), frame, unlock_scroll);
			frame++;
			id++;

			int spriteId;

			switch (NAME[i]) {
				case "Bigger and Badder":
					spriteId = 1096;
					break;
				case "Undead Head":
					spriteId = 1230;
					break;
				case "Twisted Vision":
					spriteId = 1231;
					break;
				default:
					spriteId = sprite++;
					break;
			}

			addSpriteLoader(id, spriteId != -1 ? spriteId : sprite++);
			setBounds(id, x + 5, y + 7, frame, unlock_scroll);
			frame++;
			id++;

			addHoverButtonWConfig(id, 695, 696, 14, 15, "", -1, -1, -1, 0, purchasedConfig);
			purchasedConfig++;
			setBounds(id, x + 40, y + 13, frame, unlock_scroll);
			frame++;
			id++;

			x += 227;

			if (x == 454) {
				y += size[i] ? 86 : 66;
				x = 0;
			}
		}

		unlock_scroll.height = 237;
		unlock_scroll.width = 452;
		unlock_scroll.scrollMax = 840; //680

		/*
		 * Extend scroll
		 */
		//System.out.println("SLAYER SH");
		//System.out.println(id);
		Widget extend_scroll = addInterface(id);
		//System.out.println("extend scroll id: " + extend_scroll.id + " - extend config: " + purchasedConfig);
		String[] NAME2 = {

				"Need more darkness", "Ankou very much", "Suq-a-nother one", "Fire and Darkness", "Pedal to the metals",
				"I really mith you",

				"Spiritual fervour", "Birds of a feather", "Greater challenge", "It's dark in here", "Bleed me dry",
				"Smell ya later",

				"Horrorific", "To dust you shall return", "Wyver-nother one", "Get smashed", "Nech please",
				"Augment my abbies",

				"Krack on", };

		String[] DESCRIPTION2 = { "Whenever you get a Dark Beast task, it\\nwill be a bigger task. @red@(100 points)",
				"Whenever you get an Ankou task, it will be\\na bigger task. @red@(100 points)",
				"Whenever you get a Suqah task, it will be\\na bigger task. @red@(100 points)",
				"Whenever you get a Black Dragon task, it\\nwill be a bigger task. @red@(50 points)",
				"Whenever you get a Bronze, Iron, or Steel\\nDragon task, it will be a bigger task. @red@(100\\n@red@points)",
				"Whenever you get a Mithril Dragon task, it\\nwill be a bigger task. @red@(120 points)",

				"Whenever you get a Spiritual Creature\\ntask, it will be a bigger task. @red@(100 points)",
				"Whenever you get an Aviansie task, it will\\nbe a bigger task. @red@(100 points)",
				"Whenever you get a Grater Demon task,\\nit will be a bigger task. @red@(100 points)",
				"Whenever you get a Black Demon task, it\\nwill be a bigger task. @red@(100 points)",
				"Whenever you get a Bloodvekld task, it will\\nbe a bigger task. @red@(75 points).",

				"Whenever you get an Aberrant Spectre\\ntask, it will be a bigger task. @red@(100points)",
				"Whenever you get a Cave Horror task, it\\nwill be a bigger task. @red@(100 points)",
				"Whenever you get a Dust Devil task, it will\\nbe a bigger task. @red@(100 points)",
				"Whenever you get a Skeletal Wyvren task,\\nit will be a bigger task. @red@(100 points)",
				"Whenever you get a Gargoyle task, it will\\nbe a bigger task. @red@(100 points)",

				"Whenever you get a Nechryael task, it will\\nbe a bigger task. @red@(100 points)",
				"Whenever you get an Abbysal Demon task,\\nit will be a bigger task. @red@(100 points)",
				"Whenever you get a Cave Kraken task, it\\nwill be a bigger task. @red@(100 points)",

		};

		boolean[] size2 = {

				false, false,

				false, false,

				true, true,

				false, false,

				false, false,

				false, false,

				false, false,

				false, false,

				false, false, false, };

		extend_scroll.totalChildren(5 * NAME2.length);

		id++;

		sprite = 714;
		x = 0;
		y = 0;
		frame = 0;

		for (int i = 0; i < NAME2.length; i++) {

			addHoverButton(id, size2[i] ? 687 : 689, size2[i] ? 688 : 690, "Extend");
			//addHoverButton_sprite_loader(id, size2[i] ? 687 : 689, 224, 84, "Extend", -1, id + 1, 1);
			setBounds(id, x, y, frame, extend_scroll);
			frame++;

			/*addHoveredButton_sprite_loader(id + 1, size2[i] ? 688 : 690, 224, size2[i] ? 84 : 64, id + 2);
			setBounds(id + 1, x, y, frame, extend_scroll);
			frame++;*/

			id += 3;

			addText(id, NAME2[i], t, 1, 0xEE9021, true, true);
			setBounds(id, x + 141, y + 12, frame, extend_scroll);
			frame++;
			id++;

			addText(id, DESCRIPTION2[i], t, 0, 0xEE9021, false, true);
			setBounds(id, x + 5, y + (size2[i] ? 40 : 39), frame, extend_scroll);
			frame++;
			id++;

			addSpriteLoader(id, sprite);
			setBounds(id, x + 5, y + 7, frame, extend_scroll);
			frame++;
			id++;
			sprite++;

			addHoverButtonWConfig(id, 695, 696, 14, 15, "", -1, -1, -1, 0, purchasedConfig);
			purchasedConfig++;
			setBounds(id, x + 40, y + 13, frame, extend_scroll);
			frame++;
			id++;

			x += 227;

			if (x == 454) {
				y += size2[i] ? 86 : 66;
				x = 0;
			}
		}

		extend_scroll.height = 237;
		extend_scroll.width = 452;
		extend_scroll.scrollMax = 680;
	}

	public static void boltEnchanting(GameFont[] tda) {
		Widget tab = addInterface(42750);
		addSprite(42751, 862);
		itemDisplay(42752, 66, 100, 5, 5, "Enchant");
		addHoverButton(42753, 107, 21, 21, "Close", 250, 42754, 3);
		addHoveredButton(42754, 108, 21, 21, 42755);
		addText(42756, "Magic 4", tda, 1, 0xff9933, true, true);
		addText(42757, "Magic 7", tda, 1, 0xff9933, true, true);
		addText(42758, "Magic 14", tda, 1, 0xff9933, true, true);
		addText(42759, "Magic 24", tda, 1, 0xff9933, true, true);
		addText(42760, "Magic 27", tda, 1, 0xff9933, true, true);
		addText(42761, "Magic 29", tda, 1, 0xff9933, true, true);
		addText(42762, "Magic 49", tda, 1, 0xff9933, true, true);
		addText(42763, "Magic 57", tda, 1, 0xff9933, true, true);
		addText(42764, "Magic 68", tda, 1, 0xff9933, true, true);
		addText(42765, "Magic 87", tda, 1, 0xff9933, true, true);
		addText(42766, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42767, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42768, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42769, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42770, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42771, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42772, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42773, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42774, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42775, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42776, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42777, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42778, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42779, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42780, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42781, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42782, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42783, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42784, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42785, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42786, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42787, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42788, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42789, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42790, "0/0", tda, 0, 0xFA0A0A, true, true);
		addText(42791, "0/0", tda, 0, 0xFA0A0A, true, true);

		tab.totalChildren(40);
		tab.child(0, 42751, 11, 15);
		tab.child(1, 42752, 40, 100);
		tab.child(2, 42753, 471, 22);
		tab.child(3, 42754, 471, 22);
		tab.child(4, 42756, 56, 70);
		tab.child(5, 42757, 156, 70);
		tab.child(6, 42758, 256, 70);
		tab.child(7, 42759, 352, 70);
		tab.child(8, 42760, 443, 70);
		tab.child(9, 42761, 65, 200);
		tab.child(10, 42762, 152, 200);
		tab.child(11, 42763, 245, 200);
		tab.child(12, 42764, 342, 200);
		tab.child(13, 42765, 438, 200);
		tab.child(14, 42766, 41, 162);
		tab.child(15, 42767, 74, 162);
		tab.child(16, 42768, 124, 162);
		tab.child(17, 42769, 155, 162);
		tab.child(18, 42770, 185, 162);
		tab.child(19, 42771, 233, 162);
		tab.child(20, 42772, 265, 162);
		tab.child(21, 42773, 331, 162);
		tab.child(22, 42774, 364, 162);
		tab.child(23, 42775, 415, 162);
		tab.child(24, 42776, 447, 162);
		tab.child(25, 42777, 476, 162);
		tab.child(26, 42778, 40, 290);
		tab.child(27, 42779, 72, 290);
		tab.child(28, 42780, 126, 290);
		tab.child(29, 42781, 156, 290);
		tab.child(30, 42782, 185, 290);
		tab.child(31, 42783, 221, 290);
		tab.child(32, 42784, 250, 290);
		tab.child(33, 42785, 280, 290);
		tab.child(34, 42786, 314, 290);
		tab.child(35, 42787, 344, 290);
		tab.child(36, 42788, 372, 290);
		tab.child(37, 42789, 413, 290);
		tab.child(38, 42790, 445, 290);
		tab.child(39, 42791, 475, 290);
	}

	public static void itemDisplay(int index, int itemSpaceX, int itemSpaceY, int itemX, int itemY, String... options) {
		Widget rsi = interfaceCache[index] = new Widget();
		rsi.actions = new String[options.length];
		for (int i = 0; i < options.length; i++) {
			rsi.actions[i] = options[i];
		}
		rsi.spritesX = new int[20];
		rsi.inventoryAmounts = new int[30];
		rsi.inventoryItemId = new int[30];
		rsi.spritesY = new int[20];
		rsi.children = new int[0];
		rsi.childX = new int[0];
		rsi.childY = new int[0];
		rsi.centerText = true;
		rsi.filled = false;
		rsi.replaceItems = false;
		rsi.usableItems = false;
		rsi.allowSwapItems = false;
		rsi.textShadow = false;
		rsi.spritePaddingX = itemSpaceX;
		rsi.spritePaddingY = itemSpaceY;
		rsi.height = itemY;
		rsi.width = itemX;
		rsi.parent = index;
		rsi.id = index;
		rsi.type = 2;
	}

	public static void addHoveredButton_sprite_loader(int i, int spriteId, int w, int h, int IMAGEID) {// hoverable
		// button
		Widget tab = addInterface(i);
		tab.parent = i;
		tab.id = i;
		tab.type = 0;
		tab.atActionType = 0;
		tab.width = w;
		tab.height = h;
		tab.invisible = true;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.scrollMax = 0;
		addHoverImage_sprite_loader(IMAGEID, spriteId);
		tab.totalChildren(1);
		tab.child(0, IMAGEID, 0, 0);
	}

	public static void addHoverText(int id, String text, String tooltip, GameFont[] tda, int idx, int color,
									boolean center, boolean textShadow, int width) {
		Widget rsinterface = addInterface(id);
		rsinterface.id = id;
		rsinterface.parent = id;
		rsinterface.type = 4;
		rsinterface.atActionType = 1;
		rsinterface.width = width;
		rsinterface.height = 11;
		rsinterface.contentType = 0;
		rsinterface.opacity = 0;
		rsinterface.hoverType = -1;
		rsinterface.centerText = center;
		rsinterface.textShadow = textShadow;
		rsinterface.textDrawingAreas = tda[idx];
		rsinterface.defaultText = text;
		rsinterface.secondaryText = "";
		rsinterface.tooltip = tooltip;
		rsinterface.textColor = color;
		rsinterface.secondaryColor = 0;
		rsinterface.defaultHoverColor = 0xFFFFFF;
		rsinterface.secondaryHoverColor = 0;
	}

	public static void addHoverButton_sprite_loader(int i, int spriteId, int width, int height, String text,
			int contentType, int hoverOver, int aT) {// hoverable
		// button
		Widget tab = addInterface(i);
		tab.id = i;
		tab.parent = i;
		tab.type = 5;
		tab.atActionType = aT;
		tab.contentType = contentType;
		tab.opacity = 0;
		tab.hoverType = hoverOver;
		tab.disabledSprite =  SpriteLoader.getSprite( spriteId);
		tab.enabledSprite =  SpriteLoader.getSprite( spriteId);
		tab.width = width;
		tab.height = height;
		tab.tooltip = text;
	}

	public String getDefaultText() {
		return defaultText;
	}

	public void setDefaultText(String defaultText) {
		this.defaultText = defaultText;
	}

	public enum BorderStyle {
		BLACK, BROWN, BIG_BROWN, SMALL_BROWN;
	}

	public static Widget addRectangleClickable(int id, int opacity, int color,
											   boolean filled, int width, int height) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.textColor = color;
		tab.filled = filled;
		tab.id = id;
		tab.parent = id;
		tab.type = 3;
		tab.atActionType = 5;
		tab.contentType = 0;
		tab.opacity = (byte) opacity;
		tab.width = width;
		tab.height = height;
		tab.tooltip = "Select";
		return tab;
	}

	public static void addBackground(int id, int width, int height, AutomaticSprite automaticSprite) {
		addBackground(id, width, height, 29, false, automaticSprite);
	}

	public static void addBackground(int id, int width, int height, boolean divider, AutomaticSprite automaticSprite) {
		addBackground(id, width, height, 29, divider, automaticSprite);
	}

	public static void addBackground(int id, int width, int height, int dividerY, boolean divider, AutomaticSprite automaticSprite) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = id;
		tab.type = TYPE_SPRITE;
		tab.width = width;
		tab.height = height;
		tab.disabledSprite = tab.enabledSprite = automaticSprite.getBackground(width, height, divider, dividerY);
	}

	public static void addVerticalDivider(int id, int height, AutomaticSprite automaticSprite) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = id;
		tab.type = TYPE_SPRITE;
		tab.height = height;
		tab.disabledSprite = tab.enabledSprite = automaticSprite.getDivider(height, true);
		tab.width = tab.disabledSprite.myWidth;
	}

	public static void addHorizontalDivider(int id, int width, AutomaticSprite automaticSprite) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = id;
		tab.type = TYPE_SPRITE;
		tab.width = width;
		tab.disabledSprite = tab.enabledSprite = automaticSprite.getDivider(width, false);
		tab.height = tab.disabledSprite.myHeight;
	}

	public static void addHoverButton(int id, int width, int height, AutomaticSprite buttonStyle, AutomaticSprite hoverButtonStyle, String tooltip) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_HOVER_SPRITE;
		b.atActionType = OPTION_OK;
		b.width = width;
		b.height = height;
		b.tooltip = tooltip;
		b.drawsAdvanced = true;
		b.disabledSprite = b.enabledSprite = buttonStyle.getButton(width, height);
		b.hoverSprite1 = b.hoverSprite2 = hoverButtonStyle.getButton(width, height);
	}

	public static void addTextHoverButton(Widget w, int child, int id, int x, int y, String text, String tooltip, GameFont[] tda, int idx, int width, int height, AutomaticSprite buttonStyle, AutomaticSprite hoverButtonStyle) {
		addHoverButton(id, width, height, AutomaticSprite.BUTTON_BROWN, AutomaticSprite.BUTTON_BROWN_HOVER, tooltip);
		w.child(child++, id++, x, y);
		addText(id, text, tda, idx, 0xffb000, true);
		w.child(child++, id++, x + (width / 2), y + ((height - (tda[idx].verticalSpace + 3)) / 2));
	}

	public static void addVerticalLine(int id, int h, int color) {
		Widget r = interfaceCache[id] = new Widget();
		r.id = id;
		r.parent = id;
		r.type = TYPE_RECTANGLE;
		r.width = 1;
		r.height = h;
		r.textColor = color;
	}

	public static void addHorizontalLine(int id, int w, int color) {
		Widget r = interfaceCache[id] = new Widget();
		r.id = id;
		r.parent = id;
		r.type = TYPE_RECTANGLE;
		r.width = w;
		r.height = 1;
		r.textColor = color;
	}

	public static Widget addRectangle(int id, int w, int h, int color, boolean filled) {
		Widget r = interfaceCache[id] = new Widget();
		r.id = id;
		r.parent = id;
		r.type = TYPE_RECTANGLE;
		r.width = w;
		r.height = h;
		r.textColor = color;
		r.filled = filled;
		return r;
	}

	public static void addTransparentRectangle(int id, int w, int h, int color, boolean filled, int opacity) {
		Widget r = interfaceCache[id] = new Widget();
		r.id = id;
		r.parent = id;
		r.type = TYPE_RECTANGLE;
		r.width = w;
		r.height = h;
		r.textColor = color;
		r.filled = filled;
		r.opacity = (byte) (256 - opacity);
	}

	public static void addButton(int id, int width, int height, String tooltip) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_SPRITE;
		b.atActionType = OPTION_OK;
		b.width = width;
		b.height = height;
		b.tooltip = tooltip;
	}

	public static void addConfigButton(int id, int width, int height, Sprite disabledSprite, Sprite enabledSprite, String tooltip, int configID, int configFrame) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_SPRITE;
		b.atActionType = OPTION_OK;
		b.disabledSprite = disabledSprite;
		b.enabledSprite = enabledSprite;
		b.width = width;
		b.height = height;
		b.tooltip = tooltip;
		b.valueCompareType = new int[1];
		b.requiredValues = new int[1];
		b.valueCompareType[0] = 1;
		b.requiredValues[0] = configID;
		b.valueIndexArray = new int[1][3];
		b.valueIndexArray[0][0] = 5;
		b.valueIndexArray[0][1] = configFrame;
		b.valueIndexArray[0][2] = 0;
	}

	public static void addTimerConfigButton(int id, int width, int height, Sprite disabledSprite, Sprite enabledSprite, String tooltip, int configID, int configFrame, int timerSpeed) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_SPRITE;
		b.atActionType = OPTION_OK;
		b.disabledSprite = disabledSprite;
		b.enabledSprite = enabledSprite;
		b.width = width;
		b.height = height;
		b.tooltip = tooltip;
		b.valueCompareType = new int[1];
		b.requiredValues = new int[1];
		b.valueCompareType[0] = 1;
		b.requiredValues[0] = configID;
		b.valueIndexArray = new int[1][3];
		b.valueIndexArray[0][0] = 5;
		b.valueIndexArray[0][1] = configFrame;
		b.valueIndexArray[0][2] = 0;
		b.timerSpeed = timerSpeed;
	}

	public static void addHoverButton(int id, int sprite, int hoverSprite, String tooltip) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_HOVER_SPRITE;
		b.atActionType = OPTION_OK;
		b.disabledSprite = b.enabledSprite =  SpriteLoader.getSprite( sprite);
		b.hoverSprite1 = b.hoverSprite2 =  SpriteLoader.getSprite( hoverSprite);
		b.width = b.disabledSprite.myWidth;
		b.height = b.disabledSprite.myHeight;
		b.centerText = true;
		b.textShadow = true;
		b.tooltip = tooltip;
	}

	public static void addAdvancedHoverButton(int id, int sprite, int hoverSprite, String tooltip) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_HOVER_SPRITE;
		b.atActionType = OPTION_OK;
		b.disabledSprite = b.enabledSprite =  SpriteLoader.getSprite( sprite);
		b.hoverSprite1 = b.hoverSprite2 =  SpriteLoader.getSprite( hoverSprite);
		b.width = b.disabledSprite.myWidth;
		b.height = b.disabledSprite.myHeight;
		b.centerText = true;
		b.textShadow = true;
		b.tooltip = tooltip;
		b.drawsAdvanced = true;
	}


	public static void addHoverButton(int id, Sprite sprite, Sprite hoverSprite, String tooltip) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_HOVER_SPRITE;
		b.atActionType = OPTION_OK;
		b.disabledSprite = b.enabledSprite = sprite;
		b.hoverSprite1 = b.hoverSprite2 = hoverSprite;
		b.width = b.disabledSprite.myWidth;
		b.height = b.disabledSprite.myHeight;
		b.centerText = true;
		b.textShadow = true;
		b.tooltip = tooltip;
	}

	public static void addHoverTextButton(int id, int disabledSprite, int enabledSprite, String text, GameFont[] tda, int idx, int color, int hoverColor) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_HOVER_SPRITE;
		b.atActionType = OPTION_OK;
		b.disabledSprite = b.hoverSprite1 =  SpriteLoader.getSprite( disabledSprite);
		b.enabledSprite = b.hoverSprite2 =  SpriteLoader.getSprite( enabledSprite);
		b.width = b.disabledSprite.myWidth;
		b.height = b.disabledSprite.myHeight;
		b.centerText = true;
		b.textShadow = true;
		b.textDrawingAreas = tda[idx];
		b.defaultText = text;
		b.textColor = color;
		b.defaultHoverColor = hoverColor;
		b.tooltip = text;
	}

	public static void addHoverTextConfigButton(int id, int disabledSprite, int enabledSprite, String text, GameFont[] tda, int idx, int color, int hoverColor, int configID, int configFrame) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_HOVER_SPRITE;
		b.atActionType = OPTION_OK;
		b.disabledSprite = b.hoverSprite1 =  SpriteLoader.getSprite( disabledSprite);
		b.enabledSprite = b.hoverSprite2 =  SpriteLoader.getSprite( enabledSprite);
		b.width = b.disabledSprite.myWidth;
		b.height = b.disabledSprite.myHeight;
		b.centerText = true;
		b.textShadow = true;
		b.textDrawingAreas = tda[idx];
		b.defaultText = text;
		b.textColor = color;
		b.defaultHoverColor = hoverColor;
		b.tooltip = text;
		b.valueCompareType = new int[1];
		b.requiredValues = new int[1];
		b.valueCompareType[0] = 1;
		b.requiredValues[0] = configID;
		b.valueIndexArray = new int[1][3];
		b.valueIndexArray[0][0] = 5;
		b.valueIndexArray[0][1] = configFrame;
		b.valueIndexArray[0][2] = 0;
	}

	public static void addHoverTextTimerConfigButton(int id, int width, int height, AutomaticSprite disabledStyle, AutomaticSprite enabledStyle, String text, GameFont[] tda, int idx, int color, int hoverColor, int configID, int configFrame, int timerSpeed) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_HOVER_SPRITE;
		b.atActionType = OPTION_OK;
		b.disabledSprite = b.hoverSprite1 = disabledStyle.getButton(width, height);
		b.enabledSprite = b.hoverSprite2 = enabledStyle.getButton(width, height);
		b.width = b.disabledSprite.myWidth;
		b.height = b.disabledSprite.myHeight;
		b.centerText = true;
		b.textShadow = true;
		b.textDrawingAreas = tda[idx];
		b.defaultText = text;
		b.textColor = color;
		b.defaultHoverColor = hoverColor;
		b.tooltip = text;
		b.valueCompareType = new int[1];
		b.requiredValues = new int[1];
		b.valueCompareType[0] = 1;
		b.requiredValues[0] = configID;
		b.valueIndexArray = new int[1][3];
		b.valueIndexArray[0][0] = 5;
		b.valueIndexArray[0][1] = configFrame;
		b.valueIndexArray[0][2] = 0;
		b.timerSpeed = timerSpeed;
	}

	public static void addHoverTextConfigButton(int id, int width, int height, AutomaticSprite disabledStyle, AutomaticSprite enabledStyle, String text, GameFont[] tda, int idx, int color, int hoverColor, int configID, int configFrame) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_HOVER_SPRITE;
		b.atActionType = OPTION_TOGGLE_SETTING;
		b.disabledSprite = b.hoverSprite1 = disabledStyle.getButton(width, height);
		b.enabledSprite = b.hoverSprite2 = enabledStyle.getButton(width, height);
		b.width = b.disabledSprite.myWidth;
		b.height = b.disabledSprite.myHeight;
		b.centerText = true;
		b.textShadow = true;
		b.textDrawingAreas = tda[idx];
		b.defaultText = text;
		b.textColor = color;
		b.defaultHoverColor = hoverColor;
		b.tooltip = text;
		b.valueCompareType = new int[1];
		b.requiredValues = new int[1];
		b.valueCompareType[0] = 1;
		b.requiredValues[0] = configID;
		b.valueIndexArray = new int[1][3];
		b.valueIndexArray[0][0] = 5;
		b.valueIndexArray[0][1] = configFrame;
		b.valueIndexArray[0][2] = 0;
	}

	public static void addCloseButton(int id, boolean big) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_HOVER_SPRITE;
		b.atActionType = OPTION_CLOSE;
		int sprite = big ? 107 : 825;
		int hoverSprite = big ? 108 : 826;
		b.disabledSprite = b.enabledSprite =  SpriteLoader.getSprite( sprite);
		b.hoverSprite1 = b.hoverSprite2 =  SpriteLoader.getSprite( hoverSprite);
		b.width = b.disabledSprite.myWidth;
		b.height = b.disabledSprite.myHeight;
	}

	public static void addHoverConfigButton(int id, int width, int height, int disabledSprite, int enabledSprite, int hoverSprite1, int hoverSprite2, String[] actions, int configID, int configFrame) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_HOVER_SPRITE;
		b.atActionType = OPTION_OK;
		b.disabledSprite =  SpriteLoader.getSprite( disabledSprite);
		b.enabledSprite =  SpriteLoader.getSprite( enabledSprite);
		b.hoverSprite1 =  SpriteLoader.getSprite( hoverSprite1);
		b.hoverSprite2 =  SpriteLoader.getSprite( hoverSprite2);
		b.width = width;
		b.height = height;
		b.actions = actions;
		b.valueCompareType = new int[1];
		b.requiredValues = new int[1];
		b.valueCompareType[0] = 1;
		b.requiredValues[0] = configID;
		b.valueIndexArray = new int[1][3];
		b.valueIndexArray[0][0] = 5;
		b.valueIndexArray[0][1] = configFrame;
		b.valueIndexArray[0][2] = 0;
	}

	public static void addToggleSettingButton(int id, int disabledSprite, int enabledSprite, String tooltip, int configID, int configFrame) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_SPRITE;
		b.atActionType = OPTION_TOGGLE_SETTING;
		b.disabledSprite =  SpriteLoader.getSprite( disabledSprite);
		b.enabledSprite =  SpriteLoader.getSprite( enabledSprite);
		b.width = b.disabledSprite.myWidth;
		b.height = b.disabledSprite.myHeight;
		b.valueCompareType = new int[1];
		b.requiredValues = new int[1];
		b.valueCompareType[0] = 1;
		b.requiredValues[0] = configID;
		b.valueIndexArray = new int[1][3];
		b.valueIndexArray[0][0] = 5;
		b.valueIndexArray[0][1] = configFrame;
		b.valueIndexArray[0][2] = 0;
		b.tooltip = tooltip;
	}

	public static void addToggleSettingButton(int id, int width, int height, Sprite disabledSprite, Sprite enabledSprite, String tooltip, int configID, int configFrame) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_SPRITE;
		b.atActionType = OPTION_TOGGLE_SETTING;
		b.disabledSprite = disabledSprite;
		b.enabledSprite = enabledSprite;
		b.width = width;
		b.height = height;
		b.valueCompareType = new int[1];
		b.requiredValues = new int[1];
		b.valueCompareType[0] = 1;
		b.requiredValues[0] = configID;
		b.valueIndexArray = new int[1][3];
		b.valueIndexArray[0][0] = 5;
		b.valueIndexArray[0][1] = configFrame;
		b.valueIndexArray[0][2] = 0;
		b.tooltip = tooltip;
	}

	public static void addToggleSettingButton(int id, int width, int height, int drawOffsetX, int drawOffsetY, int disabledSprite, int enabledSprite, String tooltip, int configID, int configFrame) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_SPRITE;
		b.atActionType = OPTION_TOGGLE_SETTING;
		// Use copy of desired sprite so the offsets don't affect other uses the desired sprite
		b.disabledSprite = new Sprite( SpriteLoader.getSprite( disabledSprite));
		b.enabledSprite = new Sprite( SpriteLoader.getSprite( enabledSprite));
		offsetSprites(id, drawOffsetX, drawOffsetY);
		b.width = width;
		b.height = height;
		b.valueCompareType = new int[1];
		b.requiredValues = new int[1];
		b.valueCompareType[0] = 1;
		b.requiredValues[0] = configID;
		b.valueIndexArray = new int[1][3];
		b.valueIndexArray[0][0] = 5;
		b.valueIndexArray[0][1] = configFrame;
		b.valueIndexArray[0][2] = 0;
		b.tooltip = tooltip.replace("\\n", " ");
		b.centerText = true;
		b.textShadow = true;
	}

	public static void addHoverTextToggleSettingButton(int id, int width, int height, int disabledSprite, int enabledSprite, String text, GameFont[] tda, int idx, int color, int hoverColor, boolean centerText, int configID, int configFrame) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_HOVER_SPRITE;
		b.atActionType = OPTION_TOGGLE_SETTING;
		b.disabledSprite = b.hoverSprite1 =  SpriteLoader.getSprite( disabledSprite);
		b.enabledSprite = b.hoverSprite2 =  SpriteLoader.getSprite( enabledSprite);
		b.width = width;
		b.height = height;
		b.centerText = centerText;
		b.textShadow = true;
		b.textDrawingAreas = tda[idx];
		b.defaultText = text;
		b.textColor = color;
		b.defaultHoverColor = hoverColor;
		b.tooltip = text;
		b.valueCompareType = new int[1];
		b.requiredValues = new int[1];
		b.valueCompareType[0] = 1;
		b.requiredValues[0] = configID;
		b.valueIndexArray = new int[1][3];
		b.valueIndexArray[0][0] = 5;
		b.valueIndexArray[0][1] = configFrame;
		b.valueIndexArray[0][2] = 0;
	}

	public static void addResetSettingButton(int id, int width, int height, AutomaticSprite disabledStyle, AutomaticSprite enabledStyle, String tooltip, int configID, int configFrame) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_SPRITE;
		b.atActionType = OPTION_RESET_SETTING;
		b.disabledSprite = disabledStyle.getButton(width, height);
		b.enabledSprite = enabledStyle.getButton(width, height);
		b.width = b.disabledSprite.myWidth;
		b.height = b.disabledSprite.myHeight;
		b.centerText = true;
		b.textShadow = true;
		b.tooltip = tooltip;
		b.valueCompareType = new int[1];
		b.requiredValues = new int[1];
		b.valueCompareType[0] = 1;
		b.requiredValues[0] = configID;
		b.valueIndexArray = new int[1][3];
		b.valueIndexArray[0][0] = 5;
		b.valueIndexArray[0][1] = configFrame;
		b.valueIndexArray[0][2] = 0;
	}

	public static void addResetSettingButton(int id, int disabledSprite, int enabledSprite, String tooltip, int configID, int configFrame) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_SPRITE;
		b.atActionType = OPTION_RESET_SETTING;
		b.disabledSprite =  SpriteLoader.getSprite( disabledSprite);
		b.enabledSprite =  SpriteLoader.getSprite( enabledSprite);
		b.width = b.disabledSprite.myWidth;
		b.height = b.disabledSprite.myHeight;
		b.centerText = true;
		b.textShadow = true;
		b.tooltip = tooltip;
		b.valueCompareType = new int[1];
		b.requiredValues = new int[1];
		b.valueCompareType[0] = 1;
		b.requiredValues[0] = configID;
		b.valueIndexArray = new int[1][3];
		b.valueIndexArray[0][0] = 5;
		b.valueIndexArray[0][1] = configFrame;
		b.valueIndexArray[0][2] = 0;
	}

	public static void addResetSettingButton(int id, int width, int height, int drawOffsetX, int drawOffsetY, int disabledSprite, int enabledSprite, String tooltip, int configID, int configFrame) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_SPRITE;
		b.atActionType = OPTION_RESET_SETTING;
		// Use copy of desired sprite so the offsets don't affect other uses the desired sprite
		b.disabledSprite = new Sprite( SpriteLoader.getSprite( disabledSprite));
		b.enabledSprite = new Sprite( SpriteLoader.getSprite( enabledSprite));
		offsetSprites(id, drawOffsetX, drawOffsetY);
		b.width = width;
		b.height = height;
		b.centerText = true;
		b.textShadow = true;
		b.tooltip = tooltip;
		b.valueCompareType = new int[1];
		b.requiredValues = new int[1];
		b.valueCompareType[0] = 1;
		b.requiredValues[0] = configID;
		b.valueIndexArray = new int[1][3];
		b.valueIndexArray[0][0] = 5;
		b.valueIndexArray[0][1] = configFrame;
		b.valueIndexArray[0][2] = 0;
	}

	public static void addHoverResetSettingButton(int id, int disabledSprite, int enabledSprite, int hoverSprite1, String tooltip, int configID, int configFrame) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_HOVER_SPRITE;
		b.atActionType = OPTION_RESET_SETTING;
		b.disabledSprite =  SpriteLoader.getSprite( disabledSprite);
		b.enabledSprite = b.hoverSprite2 =  SpriteLoader.getSprite( enabledSprite);
		b.hoverSprite1 =  SpriteLoader.getSprite( hoverSprite1);
		b.width = b.disabledSprite.myWidth;
		b.height = b.disabledSprite.myHeight;
		b.centerText = true;
		b.textShadow = true;
		b.tooltip = tooltip;
		b.valueCompareType = new int[1];
		b.requiredValues = new int[1];
		b.valueCompareType[0] = 1;
		b.requiredValues[0] = configID;
		b.valueIndexArray = new int[1][3];
		b.valueIndexArray[0][0] = 5;
		b.valueIndexArray[0][1] = configFrame;
		b.valueIndexArray[0][2] = 0;
	}

	public static void addHoverTextResetSettingButton(int id, int disabledSprite, int enabledSprite, String text, GameFont[] tda, int idx, int color, int hoverColor, int configID, int configFrame) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_HOVER_SPRITE;
		b.atActionType = OPTION_RESET_SETTING;
		b.disabledSprite = b.hoverSprite1 =  SpriteLoader.getSprite( disabledSprite);
		b.enabledSprite = b.hoverSprite2 =  SpriteLoader.getSprite( enabledSprite);
		b.width = b.disabledSprite.myWidth;
		b.height = b.disabledSprite.myHeight;
		b.centerText = true;
		b.textShadow = true;
		b.textDrawingAreas = tda[idx];
		b.defaultText = text;
		b.textColor = color;
		b.defaultHoverColor = hoverColor;
		b.tooltip = text;
		b.valueCompareType = new int[1];
		b.requiredValues = new int[1];
		b.valueCompareType[0] = 1;
		b.requiredValues[0] = configID;
		b.valueIndexArray = new int[1][3];
		b.valueIndexArray[0][0] = 5;
		b.valueIndexArray[0][1] = configFrame;
		b.valueIndexArray[0][2] = 0;
	}

	public static void addHoverTextResetSettingButton(int id, int width, int height, int disabledSprite, int enabledSprite, String text, GameFont[] tda, int idx, int color, int hoverColor, boolean centerText, int configID, int configFrame) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_HOVER_SPRITE;
		b.atActionType = OPTION_RESET_SETTING;
		b.disabledSprite = b.hoverSprite1 =  SpriteLoader.getSprite( disabledSprite);
		b.enabledSprite = b.hoverSprite2 =  SpriteLoader.getSprite( enabledSprite);
		b.width = width;
		b.height = height;
		b.centerText = centerText;
		b.textShadow = true;
		b.textDrawingAreas = tda[idx];
		b.defaultText = text;
		b.textColor = color;
		b.defaultHoverColor = hoverColor;
		b.tooltip = text;
		b.valueCompareType = new int[1];
		b.requiredValues = new int[1];
		b.valueCompareType[0] = 1;
		b.requiredValues[0] = configID;
		b.valueIndexArray = new int[1][3];
		b.valueIndexArray[0][0] = 5;
		b.valueIndexArray[0][1] = configFrame;
		b.valueIndexArray[0][2] = 0;
	}

	public static void addTransparentOnHoverSprite(int id, int sprite, int width, int height) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_HOVER_SPRITE;
		b.atActionType = OPTION_OK;
		b.disabledSprite = b.enabledSprite =  SpriteLoader.getSprite( sprite);
		b.hoverSprite1 = b.hoverSprite2 =  SpriteLoader.getSprite( sprite);
		b.width = width;
		b.height = height;
		b.transparentOnHover = true;
		b.transparency = 176;
	}

	public static void addColorBox(int id, int defaultColor) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_COLOR_BOX;
		b.atActionType = OPTION_COLOR_BOX;
		b.textColor = defaultColor;
		b.width = 61;
		b.height = 21;
		b.actions = new String[] { "Pick color", "Copy color", "Paste copied color" };
		b.noName = true;
	}

	public static void addColorBox(int id, int defaultColor, String colorName) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_COLOR_BOX;
		b.atActionType = OPTION_COLOR_BOX;
		b.textColor = defaultColor;
		b.width = 61;
		b.height = 21;
		b.actions = new String[] { "Pick " + colorName + " color", "Copy " + colorName + " color", "Paste copied color" };
	}

	public static void addColorBox(int id, int defaultColor, String colorName, int width, int height) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_COLOR_BOX;
		b.atActionType = OPTION_COLOR_BOX;
		b.textColor = defaultColor;
		b.width = width;
		b.height = height;
		b.actions = new String[] { "Pick " + colorName + " color", "Copy " + colorName + " color", "Paste copied color" };
	}

	public static void addColorBox(int id, int defaultColor, int width, int height) {
		Widget b = addInterface(id);
		b.id = id;
		b.parent = id;
		b.type = TYPE_COLOR_BOX;
		b.atActionType = OPTION_COLOR_BOX;
		b.textColor = defaultColor;
		b.width = width;
		b.height = height;
		b.actions = new String[] { "Pick color", "Copy color", "Paste copied color" };
		b.noName = true;
	}

	public static void addInput(int id, String defaultText, int width, int height, int textColor) {
		Widget i = addInterface(id);
		i.id = id;
		i.parent = id;
		i.type = TYPE_INPUT;
		i.atActionType = OPTION_INPUT;
		i.defaultText = "";
		i.secondaryText = defaultText;
		i.textColor = textColor;
		i.width = width;
		i.height = height;
		i.actions = new String[] { "Select", "Cut", "Copy", "Paste", "Delete" };
	}

	public static void addInput(int id, String defaultText, String regex, int width, int height, int textColor) {
		Widget i = addInterface(id);
		i.id = id;
		i.parent = id;
		i.type = TYPE_INPUT;
		i.atActionType = OPTION_INPUT;
		i.defaultText = "";
		i.secondaryText = defaultText;
		i.pattern = Pattern.compile(regex);
		i.textColor = textColor;
		i.width = width;
		i.height = height;
		i.actions = new String[] { "Select", "Cut", "Copy", "Paste", "Delete" };
	}

	public static void addTransparentHoverRectangle(int id, int width, int height, int color, boolean filled, int opacity, int hoveredOpacity) {
		Widget r = addInterface(id);
		r.id = id;
		r.parent = id;
		r.type = TYPE_TRANSPARENT_HOVER_RECTANGLE;
		r.textColor = color;
		r.filled = filled;
		r.opacity = (byte) (255 - opacity);
		r.transparency = (byte) (255 - hoveredOpacity);
		r.width = width;
		r.height = height;
	}

	public static void addTransparentHoverRectangleResetSettingButton(int id, int width, int height, int color, boolean filled, int opacity, String tooltip, int configFrame, int configID) {
		Widget r = addInterface(id);
		r.id = id;
		r.parent = id;
		r.type = TYPE_TRANSPARENT_HOVER_RECTANGLE;
		r.atActionType = OPTION_RESET_SETTING;
		r.textColor = color;
		r.filled = filled;
		r.opacity = (byte) (256 - opacity);
		r.width = width;
		r.height = height;
		r.tooltip = tooltip;
		r.valueCompareType = new int[1];
		r.requiredValues = new int[1];
		r.valueCompareType[0] = 1;
		r.requiredValues[0] = configID;
		r.valueIndexArray = new int[1][3];
		r.valueIndexArray[0][0] = 5;
		r.valueIndexArray[0][1] = configFrame;
		r.valueIndexArray[0][2] = 0;
	}

	public static void offsetSprite(Sprite sprite, int xOff, int yOff) {
		sprite.setDrawOffsetX(xOff);
		sprite.setDrawOffsetY(yOff);
	}

	public static void offsetSprites(int id, int xOff, int yOff) {
		Widget widget = interfaceCache[id];
		Sprite[] sprites = new Sprite[] { widget.disabledSprite, widget.enabledSprite, widget.hoverSprite1, widget.hoverSprite2 };
		for (Sprite sprite : sprites) {
			if(sprite == null)
				continue;
			sprite.setDrawOffsetX(xOff);
			sprite.setDrawOffsetY(yOff);
		}
	}

	public static void addSlayerItems(int childId, int interfaceId, String[] options) {
		Widget rsi = interfaceCache[childId] = new Widget();
		rsi.actions = new String[5];
		int total = 120;
		rsi.spritesY = new int[total];
		rsi.spritesX = new int[total];
		rsi.inventoryItemId = new int[total];
		rsi.inventoryAmounts = new int[total];
		rsi.children = new int[0];
		rsi.childX = new int[0];
		rsi.childY = new int[0];
		for (int i = 0; i < rsi.actions.length; i++) {
			if (i < options.length) {
				if (options[i] != null) {
					rsi.actions[i] = options[i];
				}
			}
		}
		rsi.centerText = true;
		rsi.filled = false;
		rsi.replaceItems = false;
		rsi.usableItems = false;
		rsi.allowSwapItems = false;
		rsi.spritePaddingX = 19;
		rsi.spritePaddingY = 20;
		rsi.height = 15;
		rsi.width = 8;
		rsi.parent = interfaceId;
		rsi.id = childId;
		rsi.type = TYPE_INVENTORY;
	}

	/**
	 * Adds a window with the specified width and height.
	 *
	 * @param id
	 *            The id.
	 * @param width
	 *            The width.
	 * @param height
	 *            The height.
	 * @param modernBorder
	 *            If the border is modern.
	 */
	public static void addWindow(int id, int width, int height, boolean modernBorder) {
		Widget widget = addInterface(id);
		widget.id = id;
		widget.type = 24;
		widget.width = width;
		widget.height = height;
		widget.modernWindow = modernBorder;
	}

	/**
	 * Adds a closable window with the specified width, height and title.
	 *
	 * @param id
	 *            The id.
	 * @param width
	 *            The width.
	 * @param height
	 *            The height.
	 * @param title
	 *            The title.
	 * @param modernBorder
	 *            If the border is modern.
	 */
	public static void addClosableWindow(int id, int width, int height, boolean modernBorder, String title) {
		Widget widget = addInterface(id);

		addWindow(id + 1, width, height, modernBorder);
		addText(id + 2, title, defaultFont, 2, 0xFF981F, true);

		addHoverButton(id + 3, modernBorder ? 825 : 693, modernBorder ? 16 : 21, modernBorder ? 16 : 21, "Close", -1, id + 4, 3);
		addHoveredButton(id + 4, modernBorder ? 826 : 694, modernBorder ? 16 : 21, modernBorder ? 16 : 21, id + 5);

		addHorizontalSeparator(id + 6, width - 10, modernBorder);

		setChildren(5, widget);
		setBounds(id + 1, 0, 0, 0, widget);
		setBounds(id + 2, width / 2, 10, 1, widget);

		setBounds(id + 3, width - (modernBorder ? 26 : 28), modernBorder ? 10 : 7, 2, widget);
		setBounds(id + 4, width - (modernBorder ? 26 : 28), modernBorder ? 10 : 7, 3, widget);

		setBounds(id + 6, 5, 29, 4, widget);
	}

	/**
	 * Adds a horizontal separator. Must be added last because it has its own
	 * drawing area.
	 *
	 * @param id
	 *            The id.
	 * @param width
	 *            The width.
	 * @param modernBorder
	 *            If the border is modern.
	 */
	public static void addHorizontalSeparator(int id, int width, boolean modernBorder) {
		Widget rsi = addWrapper(id, width, 6, id + 1);
		rsi.type = 25;
		rsi.width = width;
		rsi.height = 6;
		rsi.modernWindow = modernBorder;
	}

	/**
	 * Adds a vertical separator. Must be added last because it has its own
	 * drawing area.
	 *
	 * @param id
	 *            The id.
	 * @param height
	 *            The height.
	 */
	public static void addVerticalSeparator(int id, int height, boolean modernBorder) {
		Widget widget = addWrapper(id, 6, height, id + 1);
		widget.type = 26;
		widget.width = 6;
		widget.height = height;
		widget.modernWindow = modernBorder;
	}

	/**
	 * Adds a dark box with a border.
	 *
	 * @param id
	 *            The id.
	 * @param width
	 *            The width.
	 * @param height
	 *            The height.
	 * @param border
	 *            The border.
	 */
	public static void addDarkBox(int id, int width, int height, boolean fill, BoxBorder border) {
		Widget widget = addWrapper(id, width, height, id + 1);
		widget.type = 27;
		widget.width = width;
		widget.height = height;
		widget.filled = fill;
		widget.boxBorder = border;
	}

	public static void addTradeButton(int id, int width, int height, boolean left) {
		Widget widget = addWrapper(id, width, height, id + 1);
		widget.type = 28;
		widget.width = width;
		widget.height = height;
		widget.boxBorder = BoxBorder.MODERN;
		widget.requiredValues = new int[1];
		widget.requiredValues[0] = left ? 0 : 1;
	}

	private BoxBorder boxBorder;

	public BoxBorder getBoxBorder() {
		return boxBorder;
	}

	public enum BoxBorder {
		LIGHT, DARK, MODERN;
	}

	private boolean modernWindow;

	public boolean isModernWindow() {
		return modernWindow;
	}

	/**
	 * Called upon the {@link PacketMetaData#PACKET_53__SEND_UPDATE_ITEMS} packet.
	 */
	public void onUpdateItems() {
		if (contentType == 206 && ClientCompanion.openInterfaceId == 5292) {
			Bank.setSearchContainers();
		} else if (id == Shop.CONTAINER && Shop.showSearchContainers()) {
			Shop.setSearchContainer();
		} else if (id >= 58018 && id <= 58021) {
			boolean empty = true;

			for (int i = 0; i < inventoryItemId.length; i++) {
				if (inventoryItemId[i] != 0) {
					empty = false;
					break;
				}
			}

			hidden = empty ? true : false;
			interfaceCache[id + 4].hidden = empty ? false : true;
		} else if (Configuration.filterRunes &&
				(id == Spellbooks.INVENTORY_CONTAINTER_INTERFACE_ID || id == Spellbooks.EQUIPMENT_CONTAINER_INTERFACE_ID || id == Spellbooks.RUNE_POUCH_CONTAINER_INTERFACE_ID)) {
			Spellbooks.update(Spellbooks.Spellbook.forId(Spellbooks.getCurrentBookId()));
		} else if (id == 34200) {
			Widget.interfaceCache[34101].horizontalOffset = 0;
			Widget.interfaceCache[34200].horizontalOffset = 0;
			Client.instance.spinnerSpinning = false;
		}
	}

	public static int getContainerRowsAmount(Widget container) {
		int rows = 0;
		int[] itemIdArray;
		if (container.id == Shop.CONTAINER && Shop.showSearchContainers()) {
			itemIdArray = Shop.getShopIdTemp();
		} else {
			itemIdArray = container.inventoryItemId;
		}

		label0: for(int j = 0; j < itemIdArray.length; j += container.width) {

			//Is this row empty or not?
			//Set to true default
			boolean emptyRow = true;

			//Check the next rows for items...
			label1: for(int k = 0; k < itemIdArray.length; k++) {

				if(j + k >= itemIdArray.length) {
					break label0;
				}

				//Check for items...
				if(itemIdArray[j + k] > 0) { //&& container.inventoryAmounts[j + k] > 0
					emptyRow = false;
					break label1;
				}
			}

			//If the row wasn't empty, increment the amount of rows we have.
			if(!emptyRow) {
				rows++;
			}
		}

		return rows;
	}

	public static int getContainerScrollMax(Widget container) {
		int scrollMax = getContainerRowsAmount(container) * (32 + container.spritePaddingY);
		if (container.id >= Bank.CONTAINER_START && container.id < Bank.CONTAINER_START + Bank.TOTAL_TABS) {
			scrollMax += 4;
		}
		return Math.max(scrollMax, interfaceCache[container.parent].height);
	}

	public static Widget copy(int id, int fromId) {
		Widget from = interfaceCache[fromId];

		Widget rsi = addInterface(id);
		rsi.type = from.type;
		rsi.hoverType = from.hoverType;
		rsi.contentType = from.contentType;
		rsi.atActionType = from.atActionType;
		rsi.hoverText = from.hoverText;
		rsi.disabledSprite = from.disabledSprite;
		rsi.enabledSprite = from.enabledSprite;
		rsi.enabledAltSprite = from.enabledAltSprite;
		if (from.children != null) {
			rsi.children = arrayCopy(from.children);
			rsi.childX = arrayCopy(from.childX);
			rsi.childY = arrayCopy(from.childY);
		}
		rsi.selectedActionName = from.selectedActionName;
		rsi.tooltip = from.tooltip;
		rsi.spellName = from.spellName;
		rsi.spellUsableOn = from.spellUsableOn;
		if (from.valueIndexArray != null) {
			rsi.valueIndexArray = new int[from.valueIndexArray.length][50];

			for (int i = 0; i < from.valueIndexArray.length; i++) {
				for (int j = 0; j < from.valueIndexArray[i].length; j++) {
					rsi.valueIndexArray[i][j] = from.valueIndexArray[i][j];
				}
			}
		}
		rsi.filled = from.filled;
		if (from.requiredValues != null) {
			rsi.requiredValues = arrayCopy(from.requiredValues);
		}
		if (from.valueCompareType != null) {
			rsi.valueCompareType = arrayCopy(from.valueCompareType);
		}
		rsi.width = from.width;
		rsi.height = from.height;
		rsi.drawsTransparent = from.drawsTransparent;
		rsi.hidden = from.hidden;
		rsi.textDrawingAreas = from.textDrawingAreas;
		rsi.textColor = from.textColor;
		rsi.rollingText = from.rollingText;
		rsi.textShadow = from.textShadow;
		rsi.textColor = from.textColor;
		rsi.centerText = from.centerText;
		rsi.defaultText = from.defaultText;
		rsi.secondaryText = from.secondaryText;
		rsi.secondaryColor = from.secondaryColor;
		rsi.defaultHoverColor = from.defaultHoverColor;
		rsi.secondaryHoverColor = from.secondaryHoverColor;
		rsi.modelXAngle = from.modelXAngle;
		rsi.modelYAngle = from.modelYAngle;
		rsi.modelZAngle = from.modelZAngle;
		rsi.modelZoom = from.modelZoom;
		rsi.defaultMedia = from.defaultMedia;

		return rsi;
	}

	public static int[] arrayCopy(int[] from) {
		int[] array = new int[from.length];

		for (int i = 0; i < from.length; i++) {
			array[i] = from[i];
		}

		return array;
	}

}
