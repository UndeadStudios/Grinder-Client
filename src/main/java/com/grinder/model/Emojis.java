package com.grinder.model;

import com.runescape.Client;
import com.grinder.Configuration;
import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.cache.graphics.sprite.SpriteLoader;
import com.runescape.draw.Rasterizer2D;
import com.grinder.ui.ClientUI;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that handles emojis in the in-game chat.
 * 
 * @author Blake
 * @author xplicit (basically redone)
 *
 */
public class Emojis {

	private static int hoveredIndex = -1;

	public static boolean menuOpen;

	private static final int BUTTON_X = 496;
	private static final int BUTTON_Y_OFFSET = 46; // offset ***FROM THE BOTTOM OF THE SCREEN*** just used for hovering, will not affect drawing
	private static final int BUTTON_SIZE = 16; // represents the button's hover region width and height

	private static final int MENU_COLUMNS = 8;
	private static final int MENU_EMOJI_SPACING = 20;
	private static final int MENU_BORDER_SPACING = 10;
	private static final int MENU_TEXT_AREA_HEIGHT = 9;
	private static final int MENU_OFFSET = 7;

	private static final int CHAT_SCROLL_BAR_X = 496;
	private static final int CHAT_BORDER_HEIGHT = 7;

	public static int getHoveredIndex() {
		return hoveredIndex;
	}

	public static boolean isHoveringButton() {
		return Client.instance.getMouseX() >= BUTTON_X && Client.instance.getMouseY() > ClientUI.frameHeight - BUTTON_Y_OFFSET && Client.instance.getMouseX() < BUTTON_X + BUTTON_SIZE && Client.instance.getMouseY() < ClientUI.frameHeight - BUTTON_Y_OFFSET + BUTTON_SIZE;
	}

	public static boolean isHoveringMenu() {
		return Configuration.enableEmoticons && menuOpen && Client.instance.getMouseX() >= getMenuX() && Client.instance.getMouseY() >= getMenuY(ClientUI.frameHeight - 165) && Client.instance.getMouseX() < getMenuX() + getMenuWidth() && Client.instance.getMouseY() < getMenuY(ClientUI.frameHeight - 165) + getMenuHeight();
	}

	public static int getMenuWidth() {
		return MENU_COLUMNS * MENU_EMOJI_SPACING + MENU_BORDER_SPACING;
	}

	public static int getMenuHeight() {
		return (getEmojiMenuRow(Emoji.values().length - 1) + 1) * MENU_EMOJI_SPACING + MENU_TEXT_AREA_HEIGHT + MENU_BORDER_SPACING;
	}

	public static int getMenuX() {
		return CHAT_SCROLL_BAR_X - getMenuWidth() - MENU_OFFSET;
	}

	public static int getMenuY(int yOffset) {
		return yOffset + CHAT_BORDER_HEIGHT + MENU_OFFSET;
	}

	// returns column index
	public static int getEmojiMenuColumn(int index) {
		return index % MENU_COLUMNS;
	}

	// returns row index
	public static int getEmojiMenuRow(int index) {
		int row = 0;
		for (int emoji = 0; emoji <= index; emoji += MENU_COLUMNS) {
			if (getEmojiMenuColumn(emoji) == 0) {
				row++;
			}
		}
		return row - 1;
	}

	public static int getEmojiMenuX(Emoji emoji) {
		return getEmojiMenuColumn(emoji.ordinal()) * MENU_EMOJI_SPACING + (MENU_BORDER_SPACING / 2) - ((getSprite5(emoji).myWidth - MENU_EMOJI_SPACING) / 2);
	}

	private static Sprite getSprite5(Emoji emoji) {
		return SpriteLoader.getSprite(emoji.getSpriteId());
	}

	public static int getEmojiMenuY(Emoji emoji) {
		return getEmojiMenuRow(emoji.ordinal()) * MENU_EMOJI_SPACING + (MENU_BORDER_SPACING / 2) - ((getSprite5(emoji).myHeight - MENU_EMOJI_SPACING) / 2);
	}

	/**
	 * Draws the emojis button and menu
	 * @param yOffset
	 */
	public static void draw(int yOffset) {
		if (!Configuration.enableEmoticons)
			return;

		// Button
		Sprite button = SpriteLoader.getSprite(ClientUI.frameMode != Client.ScreenMode.FIXED && ChatBox.changeChatArea ? (isHoveringButton() ? 677 : 676) : (isHoveringButton() ? 675 : 674));
		button.drawAdvancedSprite((BUTTON_SIZE - button.myWidth) / 2 + BUTTON_X, (BUTTON_SIZE - button.myHeight) / 2 + yOffset + 120);

		// Menu
		if (menuOpen) {
			// Background box
			Rasterizer2D.drawTransparentBox(getMenuX() + 2, getMenuY(yOffset) + 2, getMenuWidth() - 4, getMenuHeight() - 4, 0x494034, 240);
			Rasterizer2D.drawBoxOutline(getMenuX() + 1, getMenuY(yOffset) + 1, getMenuWidth() - 2, getMenuHeight() - 2, 0x726451);
			Rasterizer2D.drawRoundedRectangle(getMenuX(), getMenuY(yOffset), getMenuWidth(), getMenuHeight(), 0x2e2b23, 256, false, false);

			// Emoji sprites
			for (Emoji emoji : Emoji.values()) {
				Sprite sprite = getSprite5(emoji);
				int spriteX = getMenuX() + getEmojiMenuX(emoji);
				int spriteY = getMenuY(yOffset) + getEmojiMenuY(emoji);
				sprite.drawAdvancedSprite(spriteX, spriteY);
			}

			// Hover text
			if (hoveredIndex != -1) {
				Emoji hovered = Emoji.values()[hoveredIndex];
				StringBuilder codes = new StringBuilder();
				boolean useCommas = hovered.getShortCodes().size() > 2;
				for (int i = 0; i < hovered.getShortCodes().size(); i++) {
					codes.append("<col=ffb000>" + hovered.getShortCodes().get(i));
					boolean last = i >= hovered.getShortCodes().size() - 1;
					boolean penultimate = i == hovered.getShortCodes().size() - 2;
					if (!last) {
						codes.append("<col=b8b8b8>");
						if (useCommas) {
							codes.append(",");
						}
					}
					if (penultimate) {
						codes.append(" or");
					}
					if (!last) {
						codes.append(" ");
					}
				}
				Client.instance.newSmallFont.drawCenteredString(codes.toString(), getMenuX() + (getMenuWidth() / 2), getMenuY(yOffset) + getMenuHeight() - 6, 0xFFFFFF, 0);
			}
		}
	}

	/**
	 * Sets the hovered emoji button index
	 */
	public static void buttons() {
		// Reset hovered index
		hoveredIndex = -1;

		// Check if we're hovering an emoji button
		if (menuOpen) {
			for (Emoji emoji : Emoji.values()) {
				Sprite sprite = getSprite5(emoji);

				int spriteX = getMenuX() + getEmojiMenuX(emoji);
				int spriteY = getMenuY(ClientUI.frameHeight - 165) + getEmojiMenuY(emoji);

				if (Client.instance.getMouseX() >= spriteX && Client.instance.getMouseY() >= spriteY
						&& Client.instance.getMouseX() < spriteX + sprite.myWidth && Client.instance.getMouseY() < spriteY + sprite.myHeight) {
					hoveredIndex = emoji.ordinal();
					break;
				}
			}
		}
	}

	public static String getHoveredName(){
		return Emojis.Emoji.values()[Emojis.getHoveredIndex()].toString().toLowerCase().replace("_", " ");
	}

	public enum Emoji {
		SMILE(836, ":-)", ":)"),
		WINK(837, ";-)", ";)"),
		SAD(838, ":-(", ":("),
		CRY(839, ";-(", ";("),
		GRIN(840, ":D"),
		SURPRISE(841, ":o"),
		GASP(898, "D:"),
		RAZZ(842, ":p"), // raspberry
		WINKRAZZ(843, ";p"),
		SLANTED(895, ":/"),
		SMUG(896, "^^"),
		GUFFAW(897, "xd"),
		SHAME(899),
		ANGRY(900),
		COOL(901),
		PUKE(902),
		FACEPALM(903, ":fp:"),
		NINJA(904),
		WUB(905),
		LOVE(906, "<3"),
		THINKING(907, ":hmm:"),
		HURR(908),
		FRUSTRATED(909),
		WUT(910, "O.-"),
		TROLL(911, ":tf:"),
		KAPPA(912, ":kap:"),
		ORLY(913);

		private final int spriteId;

		private final List<String> shortCodes = new ArrayList<>();

		Emoji(int spriteId, String... codes) {
			this.spriteId = spriteId;
			this.shortCodes.add(":" + this.toString().toLowerCase() + ":");
			for (String code : codes) {
				this.shortCodes.add(code.toLowerCase());
			}
			this.shortCodes.sort((s1, s2) -> s1.length() - s2.length());
		}

		public int getSpriteId() {
			return spriteId;
		}

		public List<String> getShortCodes() {
			return shortCodes;
		}

		/*
		 * Patterns used for matching short codes.
		 */
		private static Pattern emojiPattern;
		private static Pattern endingWithEmojiPattern;

		/**
		 * A hash collection of the emojis.
		 */
		private static Map<List<String>, Emoji> emojis = new HashMap<>();

		/**
		 * A hash collection of the emoji short codes.
		 */
		private static Map<String, Emoji> emojiShortCodes = new HashMap<>();

		/**
		 * A hash collection of the emoji sprite ids.
		 */
		private static Map<Integer, Emoji> emojiSpriteIds = new HashMap<>();

		/**
		 * Gets the emojis.
		 *
		 * @return the emojis
		 */
		public static Map<List<String>, Emoji> getEmojis() {
			return emojis;
		}

		/**
		 * Gets the emoji short codes.
		 *
		 * @return the short codes
		 */
		public static Map<String, Emoji> getEmojiShortCodes() {
			return emojiShortCodes;
		}

		/**
		 * Gets the emoji sprite ids.
		 *
		 * @return the sprite ids
		 */
		public static Map<Integer, Emoji> getEmojiSpriteIds() {
			return emojiSpriteIds;
		}

		/**
		 * Gets the emoji for the specified <code>shortCode</code>.
		 *
		 * @param shortCode
		 * @return the emoji
		 */
		public static Emoji forShortCode(String shortCode) {
			return emojiShortCodes.get(shortCode.toLowerCase());
		}

		/**
		 * Gets the emoji for the specified <code>spriteId</code>.
		 *
		 * @param spriteId
		 * @return the emoji
		 */
		public static Emoji forSpriteId(String spriteId) {
			return emojiSpriteIds.get(spriteId);
		}

		/**
		 * Checks if a given id matches any emoji sprite id.
		 *
		 * @return if a match was found
		 */
		public static boolean isEmojiSprite(int id) {
			return emojiSpriteIds.containsKey(id);
		}

		/**
		 * Finds and replaces all strings that match emoji pattern.
		 *
		 * @return the text with the emoji short codes replaced by their images.
		 */
		public static String handleSyntax(String text) {
			StringBuffer result = new StringBuffer();
			Matcher matcher = emojiPattern.matcher(text);

			while (matcher.find()) {
				String candidate = matcher.group();
				if (matcher.groupCount() > 0) {
					candidate = candidate.substring(matcher.group(1).length());
				}
				Emoji emoji = forShortCode(candidate);
				if (emoji != null) {
					String replacement = "$1<img=" + emoji.getSpriteId() + ">";
					matcher.appendReplacement(result, replacement);
				}
			}

			matcher.appendTail(result);

			return result.toString();
		}

		/**
		 * Gets the emoji of the last word in a string.
		 *
		 * @return the emoji, null if word is not an emoji short code
		 */
		public static String getEndingWordEmoji(String input) {
			Matcher matcher = endingWithEmojiPattern.matcher(input);

			while (matcher.find()) {
				String candidate = matcher.group();
				if (matcher.groupCount() > 0) {
					candidate = candidate.substring(matcher.group(1).length());
				}
				Emoji emoji = forShortCode(candidate);
				if (emoji != null) {
					return candidate;
				}
			}

			return null;
		}

		/**
		 * Build the maps and patterns.
		 */
		static {

			Pattern standardPattern = Pattern.compile(":\\w+:"); // "(:|;)-?(\)|\(|\w|\/)" extra part of regex that matches :) :( etc,
																 // removed to improve readability as it seems to not improve performance at all
			// Regex created by adding any emoji shortcodes that don't follow the standard :emoji: format
			StringBuilder nonStandardRegex = new StringBuilder();

			Arrays.stream(values()).forEach(e -> {

				emojis.put(e.getShortCodes(), e);

				for (String s : e.shortCodes) {

					emojiShortCodes.put(s, e);

					if (!standardPattern.matcher(s).matches()) {
						if (nonStandardRegex.length() != 0) {
							nonStandardRegex.append("|");
						}
						nonStandardRegex.append(escapeMetaCharacters(s));
					}

				}

				emojiSpriteIds.put(e.getSpriteId(), e);

			});

			// The regex representing the combined standard and non standard regexes
			StringBuilder regex = new StringBuilder();
			// Add in regex before for case insensitivity and requiring start of line or whitespace or <> tag (e.g. <col=ff00ff>) or @@ tag (e.g. @red@)
			regex.append("(?i)(\\s|^|<\\w+=?(\\p{XDigit}{0,6}|\\d+)>|@\\w{3}@)(");
			regex.append(standardPattern.toString());
			// Add in the non standard regex if it exists
			if (!nonStandardRegex.toString().isEmpty()) {
				regex.append("|");
				regex.append(nonStandardRegex.toString());
			}
			regex.append(")");

			// Set the emoji pattern
			emojiPattern = Pattern.compile(regex.toString());
			// Duplicate the pattern and add checking for end of line for use with checking if a string ends with an emoji, do it here so we only have to compile it once
			endingWithEmojiPattern = Pattern.compile(emojiPattern.toString() + "$");

		}

	}

	/*
	 * Quick and dirty way to add in \\ in front of regex meta characters.
	 * from https://stackoverflow.com/questions/32498432/add-escape-in-front-of-special-character-for-a-string?rq=1
	 */
	public static String escapeMetaCharacters(String inputString){
		final String[] metaCharacters = {"\\","^","$","{","}","[","]","(",")",".","*","+","?","|","-","&","%"}; //"<",">",

		for (int i = 0 ; i < metaCharacters.length ; i++){
			if(inputString.contains(metaCharacters[i])){
				inputString = inputString.replace(metaCharacters[i],"\\"+metaCharacters[i]);
			}
		}
		return inputString;
	}

}
