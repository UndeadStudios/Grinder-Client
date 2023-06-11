package com.runescape.util;

import com.grinder.client.util.Log;
import com.runescape.cache.graphics.RSFont;
import com.runescape.sign.SignLink;
import com.grinder.client.ClientUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {

    private static final char[] BASE_37_CHARACTERS = {'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static long encodeBase37(String string) {
        long encoded = 0L;
        for (int index = 0; index < string.length() && index < 12; index++) {
            char c = string.charAt(index);
            encoded *= 37L;
            if (c >= 'A' && c <= 'Z')
                encoded += (1 + c) - 65;
            else if (c >= 'a' && c <= 'z')
                encoded += (1 + c) - 97;
            else if (c >= '0' && c <= '9')
                encoded += (27 + c) - 48;
        }

        for (; encoded % 37L == 0L && encoded != 0L; encoded /= 37L)
            ;
        return encoded;
    }

    public static String decodeBase37(long encoded) {
        try {
            if (encoded <= 0L || encoded >= 0x5b5b57f8a98a5dd1L)
                return "invalid_name";
            if (encoded % 37L == 0L)
                return "invalid_name";
            int length = 0;
            char chars[] = new char[12];
            while (encoded != 0L) {
                long l1 = encoded;
                encoded /= 37L;
                chars[11 - length++] = BASE_37_CHARACTERS[(int) (l1 - encoded * 37L)];
            }
            return new String(chars, 12 - length, length);
        } catch (RuntimeException runtimeexception) {
            Log.error("81570, " + encoded + ", " + (byte) -99, runtimeexception);
        }
        throw new RuntimeException();
    }

    public static long hashSpriteName(String name) {
        name = name.toUpperCase();
        long hash = 0L;
        for (int index = 0; index < name.length(); index++) {
            hash = (hash * 61L + (long) name.charAt(index)) - 32L;
            hash = hash + (hash >> 56) & 0xffffffffffffffL;
        }
        return hash;
    }

    /**
     * Used to format a users ip address on the welcome screen.
     */
    public static String decodeIp(int ip) {
        return (ip >> 24 & 0xff) + "." + (ip >> 16 & 0xff) + "." + (ip >> 8 & 0xff) + "." + (ip & 0xff);
    }

    public static String capitalize(String s) {
        return ClientUtil.capitalizeString(s);
    }

    /**
     * Capitalizes the first letter in said string.
     *
     * @param name
     *            The string to capitalize.
     * @return The string with the first char capitalized.
     */
    public static String capitalizeFirst(String name) {
        if (name.length() < 1)
            return "";
        StringBuilder builder = new StringBuilder(name.length());
        char first = Character.toUpperCase(name.charAt(0));
        builder.append(first).append(name.toLowerCase().substring(1));
        return builder.toString();
    }

    /**
     * Capitalizes the first letter of each word in said string.
     * From https://stackoverflow.com/questions/1892765/how-to-capitalize-the-first-character-of-each-word-in-a-string
     * Because we do not have apache.commons library
     *
     * @param string
     *            The string to capitalize.
     * @return The string with the first char of each word capitalized.
     */
    public static String capitalizeEachWord(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    /**
     * Used to format a players name.
     */
    public static String formatText(String t) {
        if (t.length() > 0) {
            char chars[] = t.toCharArray();
            for (int index = 0; index < chars.length; index++)
                if (chars[index] == '_') {
                    chars[index] = ' ';
                    if (index + 1 < chars.length && chars[index + 1] >= 'a' && chars[index + 1] <= 'z')
                        chars[index + 1] = (char) ((chars[index + 1] + 65) - 97);
                }

            if (chars[0] >= 'a' && chars[0] <= 'z')
                chars[0] = (char) ((chars[0] + 65) - 97);
            return new String(chars);
        } else {
            return t;
        }
    }

    public static String insertCommasToNumber(String number) {
        return number.length() < 4 ? number : insertCommasToNumber(number
                .substring(0, number.length() - 3))
                + ","
                + number.substring(number.length() - 3, number.length());
    }

    /**
     * Used for the login screen to hide a users password
     */
    public static String passwordAsterisks(String password) {
        StringBuffer stringbuffer = new StringBuffer();
        for (int index = 0; index < password.length(); index++)
            stringbuffer.append("*");
        return stringbuffer.toString();
    }
    
	public static int countOccurrences(String haystack, char needle) {
		int count = 0;
		for (int i = 0; i < haystack.length(); i++) {
			if (haystack.charAt(i) == needle) {
				count++;
			}
		}
		return count;
	}

	private static final Pattern WORD_PATTERN = Pattern.compile("\\s[^\\s]+");

    /**
     * Cuts a string into more than one line if it exceeds the specified max width.
     * @param input
     * @param lineWidth
     * @param font
     * @return the split version of the input
     */
	public static String[] splitString(String input, int lineWidth, RSFont font) {
	    String[] split = new String[] { input };

	    if (font.getTextWidth(input, true) <= lineWidth) {
	        return split;
        }

        Matcher matcher = WORD_PATTERN.matcher(input);

        while (matcher.find()) {
            String current = input.substring(0, matcher.end());
            if (font.getTextWidth(current, true) > lineWidth) {
                String line1 = input.substring(0, matcher.start());
                String line2 = input.substring(matcher.start() + 1);
                return new String[] { line1, line2 };
            }
        }

	    return split;
    }

    /**
     * COMMONS LANG3 STRINGUTILS
     * Just needed this one method (String.replace has unnecessary Patter compile overhead) and didn't want to add the entire library
     *
     * <p>Replaces a String with another String inside a larger String,
     * for the first {@code max} values of the search String,
     * case sensitively/insensisitively based on {@code ignoreCase} value.</p>
     *
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     *
     * <pre>
     * StringUtils.replace(null, *, *, *, false)         = null
     * StringUtils.replace("", *, *, *, false)           = ""
     * StringUtils.replace("any", null, *, *, false)     = "any"
     * StringUtils.replace("any", *, null, *, false)     = "any"
     * StringUtils.replace("any", "", *, *, false)       = "any"
     * StringUtils.replace("any", *, *, 0, false)        = "any"
     * StringUtils.replace("abaa", "a", null, -1, false) = "abaa"
     * StringUtils.replace("abaa", "a", "", -1, false)   = "b"
     * StringUtils.replace("abaa", "a", "z", 0, false)   = "abaa"
     * StringUtils.replace("abaa", "A", "z", 1, false)   = "abaa"
     * StringUtils.replace("abaa", "A", "z", 1, true)   = "zbaa"
     * StringUtils.replace("abAa", "a", "z", 2, true)   = "zbza"
     * StringUtils.replace("abAa", "a", "z", -1, true)  = "zbzz"
     * </pre>
     *
     * @param text  text to search and replace in, may be null
     * @param searchString  the String to search for (case insensitive), may be null
     * @param replacement  the String to replace it with, may be null
     * @param max  maximum number of values to replace, or {@code -1} if no maximum
     * @param ignoreCase if true replace is case insensitive, otherwise case sensitive
     * @return the text with any replacements processed,
     *  {@code null} if null String input
     */
    public static String replace(final String text, String searchString, final String replacement, int max, final boolean ignoreCase) {
        if (text.isEmpty() || searchString.isEmpty() || replacement == null || max == 0) {
            return text;
            }
        String searchText = text;
        if (ignoreCase) {
            searchText = text.toLowerCase();
            searchString = searchString.toLowerCase();
            }
        int start = 0;
        int end = searchText.indexOf(searchString, start);
        if (end == -1) {
            return text;
            }
        final int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = increase < 0 ? 0 : increase;
        increase *= max < 0 ? 16 : max > 64 ? 64 : max;
        final StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != -1) {
            buf.append(text, start, end).append(replacement);
            start = end + replLength;
            if (--max == 0) {
                break;
                }
            end = searchText.indexOf(searchString, start);
            }
        buf.append(text, start, text.length());
        return buf.toString();
    }

    public static int countMatches(String string, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);

        int count = 0;

        while (matcher.find()) {
            count++;
        }

        return count;
    }

    public static String formatTimeInMinutes(int paramInt) {
        int i = (int) Math.floor((float)paramInt / 60D);
        int j = paramInt - i * 60;
        String str1 = "" + i;
        String str2 = "" + j;
        if (j < 10) {
            str2 = "0" + str2;
        }
        if (i < 10) {
            str1 = "0" + str1;
        }
        return str1 + ":" + str2;
    }

    public static String formatCoins(int coins) {
        if (coins >= 0 && coins < 10000)
            return String.valueOf(coins);
        if (coins >= 10000 && coins < 10000000)
            return coins / 1000 + "K";
        if (coins >= 10000000 && coins < 999999999)
            return coins / 1000000 + "M";
        if (coins >= 999999999)
            return "*";
        else
            return "?";
    }
}
