package com.runescape.util;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.lang.reflect.Method;

public final class MiscUtils {

    private static final char[] validChars = {'_', 'a', 'b', 'c', 'd', 'e',
            'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9'};

    public static void launchURL(String url) {
        String osName = System.getProperty("os.name");
        try {
            if (osName.startsWith("Mac OS")) {
                Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
                Method openURL = fileMgr.getDeclaredMethod("openURL",
                        new Class[]{String.class});
                openURL.invoke(null, new Object[]{url});
            } else if (osName.startsWith("Windows"))
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            else {
                String[] browsers = {"firefox", "opera", "konqueror", "epiphany", "mozilla",
                        "netscape", "safari"};
                String browser = null;
                for (int count = 0; count < browsers.length && browser == null; count++)
                    if (Runtime.getRuntime().exec(new String[]{"which", browsers[count]})
                            .waitFor() == 0)
                        browser = browsers[count];
                if (browser == null) {
                    throw new Exception("Could not find web browser");
                } else
                    Runtime.getRuntime().exec(new String[]{browser, url});
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static long longForName(String s) {
        long l = 0L;
        for (int i = 0; i < s.length() && i < 12; i++) {
            char c = s.charAt(i);
            l *= 37L;
            if (c >= 'A' && c <= 'Z') {
                l += 1 + c - 65;
            } else if (c >= 'a' && c <= 'z') {
                l += 1 + c - 97;
            } else if (c >= '0' && c <= '9') {
                l += 27 + c - 48;
            }
        }

        for (; l % 37L == 0L && l != 0L; l /= 37L) {
            ;
        }
        return l;
    }

    public static String nameForLong(long l) {
        try {
            if (l <= 0L || l >= 0x5b5b57f8a98a5dd1L) {
                return "invalid_name";
            }

            if (l % 37L == 0L) {
                return "invalid_name";
            }

            int i = 0;
            char ac[] = new char[12];

            while (l != 0L) {
                long l1 = l;
                l /= 37L;
                ac[11 - i++] = validChars[(int) (l1 - l * 37L)];
            }

            return new String(ac, 12 - i, i);
        } catch (RuntimeException runtimeexception) {
            System.out.println("81570, " + l + ", " + (byte) -99 + ", " + runtimeexception.toString());
        }

        throw new RuntimeException();
    }

    public static String fixName(String s) {
        if (s.length() > 0) {
            char ac[] = s.toCharArray();
            for (int j = 0; j < ac.length; j++) {
                if (ac[j] == '_') {
                    ac[j] = ' ';
                    if (j + 1 < ac.length && ac[j + 1] >= 'a' && ac[j + 1] <= 'z') {
                        ac[j + 1] = (char) (ac[j + 1] + 65 - 97);
                    }
                }
            }

            if (ac[0] >= 'a' && ac[0] <= 'z') {
                ac[0] = (char) (ac[0] + 65 - 97);
            }
            return new String(ac);
        } else {
            return s;
        }
    }
    
	public static String insertCommasToNumber(String number) {
		return number.length() < 4 ? number : insertCommasToNumber(number.substring(0, number.length() - 3)) + "," + number.substring(number.length() - 3, number.length());
	}

	public static String insertCommasToNumber(int number) {
		return insertCommasToNumber(number + "");
	}
	
	public static int[] d2Tod1(int[][] array) {
		int[] newArray = new int[array.length*array[0].length];

		for (int i = 0; i < array.length; ++i)
			for (int j = 0; j < array[i].length; ++j) {
				newArray[i*array[0].length+j] = array[i][j];
			}

		return newArray;
	}

	public static int[][] d1Tod2(int[] array, int width) {
		int[][] newArray = new int[array.length/width][width];

		for (int i = 0; i < array.length; ++i) {
			newArray[i/width][i%width] = array[i];
		}

		return newArray;
	}

    /**
     * Methods from the internet used for counter progress bar
     */

    public static int mixColors(Color color1, Color color2, double percent){
        double inverse_percent = 1.0 - percent;
        int redPart = (int) (color1.getRed()*percent + color2.getRed()*inverse_percent);
        int greenPart = (int) (color1.getGreen()*percent + color2.getGreen()*inverse_percent);
        int bluePart = (int) (color1.getBlue()*percent + color2.getBlue()*inverse_percent);
        return getIntFromColor(redPart, greenPart, bluePart);
    }

    private static int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }

    public static int ensureRange(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public static boolean inRange(int value, int min, int max) {
        return (value>= min) && (value<= max);
    }
    
	/**
	 * Gets the data stored in a clipboard.
	 * 
	 * @return The data.
	 */
	public static String getClipboardData() {
		String data = "";

		try {
			data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

}
