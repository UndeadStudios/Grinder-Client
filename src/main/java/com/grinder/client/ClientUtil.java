package com.grinder.client;

import com.grinder.Configuration;
import com.grinder.GrinderScape;
import com.grinder.client.util.Log;
import com.grinder.model.LoginScreen;
import com.grinder.net.CacheDownloader;
import com.runescape.cache.graphics.sprite.SpriteCompanion;
import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.cache.FileArchive;
import com.runescape.cache.graphics.GameFont;
import com.runescape.cache.graphics.RSFont;
import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.cache.graphics.sprite.SpriteLoader;
import com.runescape.cache.graphics.widget.Widget;
import com.runescape.clock.Time;
import com.runescape.draw.Rasterizer2D;
import com.runescape.input.MouseHandler;
import com.runescape.io.jaggrab.JagGrab;
import com.runescape.util.SkillConstants;
import net.runelite.client.util.OSType;

import java.awt.*;
import java.io.*;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 12/12/2019
 */
public class ClientUtil {

    public static void setOverlayItem(Sprite icon, int amount, int x, int y, int interfaceId, boolean hideAmount) {
        ClientCompanion.overlayItemIcon = icon;
        ClientCompanion.overlayItemAmount = amount;
        ClientCompanion.overlayItemX = x;
        ClientCompanion.overlayItemY = y;
        ClientCompanion.overlayItemInterfaceId = interfaceId;
        ClientCompanion.overlayItemHideAmount = hideAmount;
    }

    public static String intToKOrMilLongName(int i) {
        String s = String.valueOf(i);
        for (int k = s.length() - 3; k > 0; k -= 3)
            s = s.substring(0, k) + "," + s.substring(k);
        if (s.length() > 8)
            s = "@gre@" + s.substring(0, s.length() - 8) + " million @whi@(" + s + ")";
        else if (s.length() > 4)
            s = "@cya@" + s.substring(0, s.length() - 4) + "K @whi@(" + s + ")";
        return " " + s;
    }

    public static String capitalize(String s) {
        return capitalizeString(s);
    }

    public static String capitalizeString(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (i == 0) {
                s = String.format("%s%s", Character.toUpperCase(s.charAt(0)), s.substring(1));
            }
            if (!Character.isLetterOrDigit(s.charAt(i))) {
                if (i + 1 < s.length()) {
                    s = String.format("%s%s%s", s.subSequence(0, i + 1), Character.toUpperCase(s.charAt(i + 1)),
                            s.substring(i + 2));
                }
            }
        }
        return s;
    }

    public static String intToKOrMil(int j) {
        if (j < 0x186a0)
            return String.valueOf(j);
        if (j < 0x989680)
            return j / 1000 + "K";
        else
            return j / 0xf4240 + "M";
    }

    public static void setTab(int id) {
        ClientCompanion.tabId = id;
        ClientCompanion.tabAreaAltered = true;
    }

    public static String combatDiffColor(int i, int j) {
        int k = i - j;
        if (k < -9) return "@red@";
        if (k < -6) return "@or3@";
        if (k < -3) return "@or2@";
        if (k < 0) return "@or1@";
        if (k > 9) return "@gre@";
        if (k > 6) return "@gr3@";
        if (k > 3) return "@gr2@";
        if (k > 0) return "@gr1@";
        else return "@yel@";
    }

    /**
     * Gets the progress color for the xp bar
     */
    public static int getProgressColor(int percent) {
        if (percent <= 15) return 0x808080;
        if (percent <= 45) return 0x7f7f00;
        if (percent <= 65) return 0x999900;
        if (percent <= 75) return 0xb2b200;
        if (percent <= 90) return 0x007f00;
        return 31744;
    }

    public static int getXPForLevel(int level) {
        return ClientCompanion.SKILL_EXPERIENCE[level];
        /*int points = 0;
        int output = 0;
        for (int lvl = 1; lvl <= level; lvl++) {
            points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
            if (lvl >= level) {
                return output;
            }
            output = (int) Math.floor((float)points / 4);
        }
        return 0;*/
    }


    public static int getLargeResizableInterfaceOffsetLeftX() {
        int screenWidth = ClientUI.frameWidth;
        int widgetWidth = Widget.interfaceCache[ClientCompanion.openInterfaceId].width;
        return (screenWidth / 2) - ((widgetWidth + 200) / 2);
    }

    public static int getLargeResizableInterfaceOffsetTopY() {
        int screenHeight = ClientUI.frameHeight;
        int canvasHeight = Optional.ofNullable(Client.chatboxImageProducer).map(buffer -> buffer.height).orElse(0);
        int widgetHeight = Widget.interfaceCache[ClientCompanion.openInterfaceId].height;
        return ((screenHeight - canvasHeight) / 2) - (widgetHeight / 2);
    }

    public static int getLargeResizableInterfaceOffsetRightX() { return (ClientUI.frameWidth / 2) + ((Widget.interfaceCache[ClientCompanion.openInterfaceId].width + 200) / 2); }

    public static int getLargeResizableInterfaceOffsetBottomY() { return ((ClientUI.frameHeight - Client.chatboxImageProducer.height) / 2) + (Widget.interfaceCache[ClientCompanion.openInterfaceId].height / 2); }

    @SuppressWarnings("rawtypes")
    public static Class method868(String string) {
        Class var_class;
        try {
            var_class = Class.forName(string);
        } catch (ClassNotFoundException classnotfoundexception) {
            Log.error("Failed to load class for name "+string, classnotfoundexception);
            throw new NoClassDefFoundError(classnotfoundexception.getMessage());
        }
        return var_class;
    }

    public static String getMotherboardSN() {

        if(OSType.getOSType() != OSType.Windows)
            return "";

        final StringBuilder result = new StringBuilder();
        try {
            final File file = File.createTempFile(".~DFSdd32D", ".vbs");
            final FileWriter fw = new FileWriter(file);
            final String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                    + "Set colItems = objWMIService.ExecQuery _ \n" + "   (\"Select * from Win32_BaseBoard\") \n"
                    + "For Each objItem in colItems \n" + "    Wscript.Echo objItem.SerialNumber \n"
                    + "    exit for  ' do the first cpu only! \n" + "Next \n";
            write(result, file, fw, vbs);
        } catch (Exception e) {
            Log.error("Failed to get motherboard info from user", e);
        }
        return result.toString().trim();
    }

    private static void write(StringBuilder result, File file, FileWriter fw, String vbs) throws IOException {

        fw.write(vbs);
        fw.close();

        final Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
        final BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;

        while ((line = input.readLine()) != null)
            result.append(line);

        input.close();

        if(!file.delete())
            System.err.println("Could not deleted file at "+file);

    }

    public static String getHDSerialNumber(String drive) {

        if(OSType.getOSType() != OSType.Windows)
            return "";

        StringBuilder result = new StringBuilder();
        try {
            File file = File.createTempFile(".~DFSdd32D3D", ".vbs");

            FileWriter fw = new FileWriter(file);

            String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"
                    + "Set colDrives = objFSO.Drives\n" + "Set objDrive = colDrives.item(\"" + drive + "\")\n"
                    + "Wscript.Echo objDrive.SerialNumber"; // see
            // note
            write(result, file, fw, vbs);
        } catch (Exception e) {
            Log.error("Failed to get serial number from user", e);
        }
        return result.toString().trim();
    }

    /**
     * The offset used to center the player in fullscreen if the client is not stretched double the size (so that it can center properly) - thing1.
     * This really should not have to be a thing but whoever did fullscreen originally butched the hell out of the camera focusing...
     *
     * TODO: remove this
     */
    public static int getCameraOffsetX() {
        if (ClientUI.frameMode != Client.ScreenMode.FIXED) {
            int screenAreaWidthFixed = 512;
            if (screenAreaWidthFixed * 2 > ClientUI.screenAreaWidth) {
                //offsetX = screenAreaWidthFixed - 232 * 2; //total width minus tab area size to the edge of the frame (because I'm OCD AF).
                return 100; //This looks better however IMO.
            }
        }
        return 0;
    }

    public static boolean inCircle(int circleX, int circleY, int clickX, int clickY, int radius) {
        return Math.pow((circleX + radius - clickX), 2)
                + Math.pow((circleY + radius - clickY), 2) < Math.pow(radius, 2);
    }

    public static boolean isHoveringResizeChat(Client client) { // extra 2px on each side so it is easier to click
        return MouseHandler.x > client.getResizeChatButtonX() - 2 && MouseHandler.x <= client.getResizeChatButtonX() + SpriteLoader.getSprite(949).myWidth + 2
                && MouseHandler.y > client.getResizeChatButtonY() - 2 && MouseHandler.y <= client.getResizeChatButtonY() + SpriteLoader.getSprite(949).myHeight + 2;
    }

    public static String interfaceIntToString(int j) {
        return interfaceIntToString(j, true);
    }

    public static String interfaceIntToString(int j, boolean check) {
        if (!check || j < 0x3b9ac9ff)
            return ClientCompanion.NUMBER_FORMAT.format(j);
        else
            return "*";
    }

    public static void showErrorScreen(Client client) {
        Graphics g = client.getGameComponent().getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 765, 503);
//		method4(1);
        if (ClientCompanion.loadingError) {
            ClientCompanion.aBoolean831 = false;
            g.setFont(new Font("Helvetica", 1, 16));
            g.setColor(Color.yellow);
            int k = 35;
            g.drawString("Sorry, an error has occured whilst loading " + Configuration.CLIENT_NAME, 30, k);
            k += 50;
            g.setColor(Color.white);
            g.drawString("To fix this try the following (in order):", 30, k);
            k += 50;
            g.setColor(Color.white);
            g.setFont(new Font("Helvetica", 1, 12));
            g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, k);
            k += 30;
            g.drawString("2: Try clearing your web-browsers cache from tools->internet options", 30, k);
            k += 30;
            g.drawString("3: Try using a different game-world", 30, k);
            k += 30;
            g.drawString("4: Try rebooting your computer", 30, k);
            k += 30;
            g.drawString("5: Try selecting a different version of Java from the play-game menu", 30, k);
        }
        if (ClientCompanion.genericLoadingError) {
            ClientCompanion.aBoolean831 = false;
            g.setFont(new Font("Helvetica", 1, 20));
            g.setColor(Color.white);
            g.drawString("Error - unable to load game!", 50, 50);
            g.drawString("To play " + Configuration.CLIENT_NAME + " make sure you play from", 50, 100);
            g.drawString("https://www.grinderscape.org", 50, 150);
        }
        if (ClientCompanion.rsAlreadyLoaded) {
            ClientCompanion.aBoolean831 = false;
            g.setColor(Color.yellow);
            int l = 35;
            g.drawString("Error a copy of " + Configuration.CLIENT_NAME + " already appears to be loaded", 30, l);
            l += 50;
            g.setColor(Color.white);
            g.drawString("To fix this try the following (in order):", 30, l);
            l += 50;
            g.setColor(Color.white);
            g.setFont(new Font("Helvetica", 1, 12));
            g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, l);
            l += 30;
            g.drawString("2: Try rebooting your computer, and reloading", 30, l);
            l += 30;
        }
    }

    public static String getMacAddress() {
        String mc = ClientCompanion.NULLED_MAC;
        try {
            InetAddress add = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(add);
            if (ni != null) {
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    mc = sb.toString();
                } else {
                    mc = ClientCompanion.MAC_DENIED_ACCESS;
                }
            }
        } catch (UnknownHostException | SocketException e) {
            Log.error("Failed to get mac address!", e);
            return ClientCompanion.MAC_EXCEPTION;
        }
        return mc;
    }

    public static void drawBackground() {
        ClientUI.backgroundSprite.drawSprite(0, 0);
//        SpriteCompanion.cacheSprite[798].drawSprite(0, 0);
    }

    public static RSFont getRSFont(GameFont boldText, GameFont fancyFont, GameFont fancyFontLarge, RSFont newBoldFont, RSFont newFancyFont, RSFont newFancyFontLarge, RSFont newRegularFont, RSFont newSmallFont, GameFont regularText, GameFont smallText, GameFont textDrawingArea) {
        RSFont font = null;
        if (textDrawingArea == smallText) {
            font = newSmallFont;
        } else if (textDrawingArea == regularText) {
            font = newRegularFont;
        } else if (textDrawingArea == boldText) {
            font = newBoldFont;
        } else if (textDrawingArea == fancyFont) {
            font = newFancyFont;
        } else if (textDrawingArea == fancyFontLarge)
            font = newFancyFontLarge;
        return font;
    }

    public static void drawTransparentScrollBar(int x, int y, int height, int maxScroll, int pos) {
        /*
         * Arrows
         */
        SpriteLoader.getSprite(29).drawTransparentSprite(x, y, 84);
        SpriteLoader.getSprite(30).drawTransparentSprite(x, y + height - 16, 84);

        /*
         * Background
         */
        Rasterizer2D.drawTransparentBoxOutline(x, y + 16, 16, height - 32, 0xffffff, 20);
        Rasterizer2D.drawTransparentBox(x + 1, y + 17, 14, height - 34, 0xffffff, 16);

        /*
         * Outline
         */
        int barHeight = Math.max(((height - 32) * height) / maxScroll, 10);
        int barPos;
        if (height == maxScroll) {
            barPos = ((height - 32 - barHeight) * pos);
        } else {
            barPos = ((height - 32 - barHeight) * pos) / (maxScroll - height);
        }
        Rasterizer2D.drawTransparentBoxOutline(x, y + 16 + barPos, 16, barHeight, 0xffffff, 24);
        Rasterizer2D.drawTransparentHorizontalLine(x + 1, y + 16 + barPos, 14, 0xffffff, 12);
        Rasterizer2D.drawTransparentHorizontalLine(x + 1, y + barHeight + 15 + barPos, 14, 0xffffff, 12);
        Rasterizer2D.drawTransparentBox(x + 1, y + 17 + barPos, 1, 1, 0xffffff, 36);
        Rasterizer2D.drawTransparentBox(x + 14, y + 17 + barPos, 1, 1, 0xffffff, 36);
        Rasterizer2D.drawTransparentBox(x + 1, y + barHeight + 14 + barPos, 1, 1, 0xffffff, 36);
        Rasterizer2D.drawTransparentBox(x + 14, y + barHeight + 14 + barPos, 1, 1, 0xffffff, 36);

        /*
         * Fill
         */
        Rasterizer2D.drawTransparentHorizontalLine(x + 2, y + 17 + barPos, 12, 0xffffff, 16);
        Rasterizer2D.drawTransparentHorizontalLine(x + 2, y + barHeight + 14 + barPos, 12, 0xffffff, 16);
        Rasterizer2D.drawTransparentVerticalLine(x + 1, y + 18 + barPos, barHeight - 4, 0xffffff, 16);
        Rasterizer2D.drawTransparentVerticalLine(x + 14, y + 18 + barPos, barHeight - 4, 0xffffff, 16);
        Rasterizer2D.drawTransparentBox(x + 2, y + 18 + barPos, 12, barHeight - 4, 0xffffff, 16);
    }

    public static void draw508Scrollbar(int height, int pos, int y, int x, int maxScroll, boolean transparent) {
        if (transparent) {
            drawTransparentScrollBar(x, y, height, maxScroll, pos);
        } else {
            SpriteCompanion.scrollBar3.drawSprite(x, y);
            SpriteCompanion.scrollBar4.drawSprite(x, (y + height) - 16);
            Rasterizer2D.drawBox(x, y + 16, 16, height - 32, 0x746241);
            Rasterizer2D.drawBox(x, y + 16, 15, height - 32, 0x77603e);
            Rasterizer2D.drawBox(x, y + 16, 14, height - 32, 0x95784a);
            Rasterizer2D.drawBox(x, y + 16, 12, height - 32, 0x997c52);
            Rasterizer2D.drawBox(x, y + 16, 11, height - 32, 0x9e8155);
            Rasterizer2D.drawBox(x, y + 16, 10, height - 32, 0xa48558);
            Rasterizer2D.drawBox(x, y + 16, 8, height - 32, 0xaa8b5c);
            Rasterizer2D.drawBox(x, y + 16, 6, height - 32, 0xb09060);
            Rasterizer2D.drawBox(x, y + 16, 3, height - 32, 0x866c44);
            Rasterizer2D.drawBox(x, y + 16, 1, height - 32, 0x7c6945);

            int k1 = Math.max(((height - 32) * height) / maxScroll, 10);
            int l1 = ((height - 32 - k1) * pos) / (maxScroll - height);
            int l2 = ((height - 32 - k1) * pos) / (maxScroll - height) + 6;
            Rasterizer2D.drawVerticalLine(x + 1, y + 16 + l1, k1, 0x5c492d);
            Rasterizer2D.drawVerticalLine(x + 14, y + 16 + l1, k1, 0x5c492d);
            Rasterizer2D.drawHorizontalLine(x + 1, y + 16 + l1, 14, 0x5c492d);
            Rasterizer2D.drawHorizontalLine(x + 1, y + 15 + l1 + k1, 14, 0x5c492d);
            Rasterizer2D.drawHorizontalLine(x + 4, y + 18 + l1, 8, 0x664f2b);
            Rasterizer2D.drawHorizontalLine(x + 4, y + 13 + l1 + k1, 8, 0x664f2b);
            Rasterizer2D.drawHorizontalLine(x + 3, y + 19 + l1, 2, 0x664f2b);
            Rasterizer2D.drawHorizontalLine(x + 11, y + 19 + l1, 2, 0x664f2b);
            Rasterizer2D.drawHorizontalLine(x + 3, y + 12 + l1 + k1, 2, 0x664f2b);
            Rasterizer2D.drawHorizontalLine(x + 11, y + 12 + l1 + k1, 2, 0x664f2b);
            Rasterizer2D.drawHorizontalLine(x + 3, y + 14 + l1 + k1, 11, 0x866c44);
            Rasterizer2D.drawHorizontalLine(x + 3, y + 17 + l1, 11, 0x866c44);
            Rasterizer2D.drawVerticalLine(x + 13, y + 12 + l2, k1 - 4, 0x866c44);
            Rasterizer2D.drawVerticalLine(x + 3, y + 13 + l2, k1 - 6, 0x664f2b);
            Rasterizer2D.drawVerticalLine(x + 12, y + 13 + l2, k1 - 6, 0x664f2b);
            Rasterizer2D.drawHorizontalLine(x + 2, y + 18 + l1, 2, 0x866c44);
            Rasterizer2D.drawHorizontalLine(x + 2, y + 13 + l1 + k1, 2, 0x866c44);
            Rasterizer2D.drawHorizontalLine(x + 12, y + 18 + l1, 1, 0x866c44);
            Rasterizer2D.drawHorizontalLine(x + 12, y + 13 + l1 + k1, 1, 0x866c44);
        }
    }

    public static void drawScrollbar(int x, int y, int pos, int height, int maxScroll, int barFillColor, boolean transparent) {
        if (transparent) {
            drawTransparentScrollBar(x, y, height, maxScroll, pos);
        } else {
            SpriteCompanion.scrollBar1.drawSprite(x, y);
            SpriteCompanion.scrollBar2.drawSprite(x, (y + height) - 16);
            Rasterizer2D.drawBox(x, y + 16, 16, height - 32, 0x000001);
            Rasterizer2D.drawBox(x, y + 16, 15, height - 32, 0x3d3426);
            Rasterizer2D.drawBox(x, y + 16, 13, height - 32, 0x342d21);
            Rasterizer2D.drawBox(x, y + 16, 11, height - 32, 0x2e281d);
            Rasterizer2D.drawBox(x, y + 16, 10, height - 32, 0x29241b);
            Rasterizer2D.drawBox(x, y + 16, 9, height - 32, 0x252019);
            Rasterizer2D.drawBox(x, y + 16, 1, height - 32, 0x000001);
            int barHeight = Math.max(((height - 32) * height) / maxScroll, 10);
            int barPos;
            if (height == maxScroll) {
                barPos = ((height - 32 - barHeight) * pos);
            } else {
                barPos = ((height - 32 - barHeight) * pos) / (maxScroll - height);
            }
            Rasterizer2D.drawBox(x, y + 16 + barPos, 16, barHeight, barFillColor);
            Rasterizer2D.drawVerticalLine(x, y + 16 + barPos, barHeight, 0x000001);
            Rasterizer2D.drawVerticalLine(x + 1, y + 16 + barPos, barHeight, 0x817051);
            Rasterizer2D.drawVerticalLine(x + 2, y + 16 + barPos, barHeight, 0x73654a);
            Rasterizer2D.drawVerticalLine(x + 3, y + 16 + barPos, barHeight, 0x6a5c43);
            Rasterizer2D.drawVerticalLine(x + 4, y + 16 + barPos, barHeight, 0x6a5c43);
            Rasterizer2D.drawVerticalLine(x + 5, y + 16 + barPos, barHeight, 0x655841);
            Rasterizer2D.drawVerticalLine(x + 6, y + 16 + barPos, barHeight, 0x655841);
            Rasterizer2D.drawVerticalLine(x + 7, y + 16 + barPos, barHeight, 0x61553e);
            Rasterizer2D.drawVerticalLine(x + 8, y + 16 + barPos, barHeight, 0x61553e);
            Rasterizer2D.drawVerticalLine(x + 9, y + 16 + barPos, barHeight, 0x5d513c);
            Rasterizer2D.drawVerticalLine(x + 10, y + 16 + barPos, barHeight, 0x5d513c);
            Rasterizer2D.drawVerticalLine(x + 11, y + 16 + barPos, barHeight, 0x594e3a);
            Rasterizer2D.drawVerticalLine(x + 12, y + 16 + barPos, barHeight, 0x594e3a);
            Rasterizer2D.drawVerticalLine(x + 13, y + 16 + barPos, barHeight, 0x514635);
            Rasterizer2D.drawVerticalLine(x + 14, y + 16 + barPos, barHeight, 0x4b4131);
            Rasterizer2D.drawHorizontalLine(x, y + 16 + barPos, 15, 0x000001);
            Rasterizer2D.drawHorizontalLine(x, y + 17 + barPos, 15, 0x000001);
            Rasterizer2D.drawHorizontalLine(x, y + 17 + barPos, 14, 0x655841);
            Rasterizer2D.drawHorizontalLine(x, y + 17 + barPos, 13, 0x6a5c43);
            Rasterizer2D.drawHorizontalLine(x, y + 17 + barPos, 11, 0x6d5f48);
            Rasterizer2D.drawHorizontalLine(x, y + 17 + barPos, 10, 0x73654a);
            Rasterizer2D.drawHorizontalLine(x, y + 17 + barPos, 7, 0x76684b);
            Rasterizer2D.drawHorizontalLine(x, y + 17 + barPos, 5, 0x7b6a4d);
            Rasterizer2D.drawHorizontalLine(x, y + 17 + barPos, 4, 0x7e6e50);
            Rasterizer2D.drawHorizontalLine(x, y + 17 + barPos, 3, 0x817051);
            Rasterizer2D.drawHorizontalLine(x, y + 17 + barPos, 2, 0x000001);
            Rasterizer2D.drawHorizontalLine(x, y + 18 + barPos, 16, 0x000001);
            Rasterizer2D.drawHorizontalLine(x, y + 18 + barPos, 15, 0x564b38);
            Rasterizer2D.drawHorizontalLine(x, y + 18 + barPos, 14, 0x5d513c);
            Rasterizer2D.drawHorizontalLine(x, y + 18 + barPos, 11, 0x625640);
            Rasterizer2D.drawHorizontalLine(x, y + 18 + barPos, 10, 0x655841);
            Rasterizer2D.drawHorizontalLine(x, y + 18 + barPos, 7, 0x6a5c43);
            Rasterizer2D.drawHorizontalLine(x, y + 18 + barPos, 5, 0x6e6046);
            Rasterizer2D.drawHorizontalLine(x, y + 18 + barPos, 4, 0x716247);
            Rasterizer2D.drawHorizontalLine(x, y + 18 + barPos, 3, 0x7b6a4d);
            Rasterizer2D.drawHorizontalLine(x, y + 18 + barPos, 2, 0x817051);
            Rasterizer2D.drawHorizontalLine(x, y + 18 + barPos, 1, 0x000001);
            Rasterizer2D.drawHorizontalLine(x, y + 19 + barPos, 16, 0x000001);
            Rasterizer2D.drawHorizontalLine(x, y + 19 + barPos, 15, 0x514635);
            Rasterizer2D.drawHorizontalLine(x, y + 19 + barPos, 14, 0x564b38);
            Rasterizer2D.drawHorizontalLine(x, y + 19 + barPos, 11, 0x5d513c);
            Rasterizer2D.drawHorizontalLine(x, y + 19 + barPos, 9, 0x61553e);
            Rasterizer2D.drawHorizontalLine(x, y + 19 + barPos, 7, 0x655841);
            Rasterizer2D.drawHorizontalLine(x, y + 19 + barPos, 5, 0x6a5c43);
            Rasterizer2D.drawHorizontalLine(x, y + 19 + barPos, 4, 0x6e6046);
            Rasterizer2D.drawHorizontalLine(x, y + 19 + barPos, 3, 0x73654a);
            Rasterizer2D.drawHorizontalLine(x, y + 19 + barPos, 2, 0x817051);
            Rasterizer2D.drawHorizontalLine(x, y + 19 + barPos, 1, 0x000001);
            Rasterizer2D.drawHorizontalLine(x, y + 20 + barPos, 16, 0x000001);
            Rasterizer2D.drawHorizontalLine(x, y + 20 + barPos, 15, 0x4b4131);
            Rasterizer2D.drawHorizontalLine(x, y + 20 + barPos, 14, 0x544936);
            Rasterizer2D.drawHorizontalLine(x, y + 20 + barPos, 13, 0x594e3a);
            Rasterizer2D.drawHorizontalLine(x, y + 20 + barPos, 10, 0x5d513c);
            Rasterizer2D.drawHorizontalLine(x, y + 20 + barPos, 8, 0x61553e);
            Rasterizer2D.drawHorizontalLine(x, y + 20 + barPos, 6, 0x655841);
            Rasterizer2D.drawHorizontalLine(x, y + 20 + barPos, 4, 0x6a5c43);
            Rasterizer2D.drawHorizontalLine(x, y + 20 + barPos, 3, 0x73654a);
            Rasterizer2D.drawHorizontalLine(x, y + 20 + barPos, 2, 0x817051);
            Rasterizer2D.drawHorizontalLine(x, y + 20 + barPos, 1, 0x000001);
            Rasterizer2D.drawVerticalLine(x + 15, y + 16 + barPos, barHeight, 0x000001);
            Rasterizer2D.drawHorizontalLine(x, y + 15 + barPos + barHeight, 16, 0x000001);
            Rasterizer2D.drawHorizontalLine(x, y + 14 + barPos + barHeight, 15, 0x000001);
            Rasterizer2D.drawHorizontalLine(x, y + 14 + barPos + barHeight, 14, 0x3f372a);
            Rasterizer2D.drawHorizontalLine(x, y + 14 + barPos + barHeight, 10, 0x443c2d);
            Rasterizer2D.drawHorizontalLine(x, y + 14 + barPos + barHeight, 9, 0x483e2f);
            Rasterizer2D.drawHorizontalLine(x, y + 14 + barPos + barHeight, 7, 0x4a402f);
            Rasterizer2D.drawHorizontalLine(x, y + 14 + barPos + barHeight, 4, 0x4b4131);
            Rasterizer2D.drawHorizontalLine(x, y + 14 + barPos + barHeight, 3, 0x564b38);
            Rasterizer2D.drawHorizontalLine(x, y + 14 + barPos + barHeight, 2, 0x000001);
            Rasterizer2D.drawHorizontalLine(x, y + 13 + barPos + barHeight, 16, 0x000001);
            Rasterizer2D.drawHorizontalLine(x, y + 13 + barPos + barHeight, 15, 0x443c2d);
            Rasterizer2D.drawHorizontalLine(x, y + 13 + barPos + barHeight, 11, 0x4b4131);
            Rasterizer2D.drawHorizontalLine(x, y + 13 + barPos + barHeight, 9, 0x514635);
            Rasterizer2D.drawHorizontalLine(x, y + 13 + barPos + barHeight, 7, 0x544936);
            Rasterizer2D.drawHorizontalLine(x, y + 13 + barPos + barHeight, 6, 0x564b38);
            Rasterizer2D.drawHorizontalLine(x, y + 13 + barPos + barHeight, 4, 0x594e3a);
            Rasterizer2D.drawHorizontalLine(x, y + 13 + barPos + barHeight, 3, 0x625640);
            Rasterizer2D.drawHorizontalLine(x, y + 13 + barPos + barHeight, 2, 0x6a5c43);
            Rasterizer2D.drawHorizontalLine(x, y + 13 + barPos + barHeight, 1, 0x000001);
            Rasterizer2D.drawHorizontalLine(x, y + 12 + barPos + barHeight, 16, 0x000001);
            Rasterizer2D.drawHorizontalLine(x, y + 12 + barPos + barHeight, 15, 0x443c2d);
            Rasterizer2D.drawHorizontalLine(x, y + 12 + barPos + barHeight, 14, 0x4b4131);
            Rasterizer2D.drawHorizontalLine(x, y + 12 + barPos + barHeight, 12, 0x544936);
            Rasterizer2D.drawHorizontalLine(x, y + 12 + barPos + barHeight, 11, 0x564b38);
            Rasterizer2D.drawHorizontalLine(x, y + 12 + barPos + barHeight, 10, 0x594e3a);
            Rasterizer2D.drawHorizontalLine(x, y + 12 + barPos + barHeight, 7, 0x5d513c);
            Rasterizer2D.drawHorizontalLine(x, y + 12 + barPos + barHeight, 4, 0x61553e);
            Rasterizer2D.drawHorizontalLine(x, y + 12 + barPos + barHeight, 3, 0x6e6046);
            Rasterizer2D.drawHorizontalLine(x, y + 12 + barPos + barHeight, 2, 0x7b6a4d);
            Rasterizer2D.drawHorizontalLine(x, y + 12 + barPos + barHeight, 1, 0x000001);
            Rasterizer2D.drawHorizontalLine(x, y + 11 + barPos + barHeight, 16, 0x000001);
            Rasterizer2D.drawHorizontalLine(x, y + 11 + barPos + barHeight, 15, 0x4b4131);
            Rasterizer2D.drawHorizontalLine(x, y + 11 + barPos + barHeight, 14, 0x514635);
            Rasterizer2D.drawHorizontalLine(x, y + 11 + barPos + barHeight, 13, 0x564b38);
            Rasterizer2D.drawHorizontalLine(x, y + 11 + barPos + barHeight, 11, 0x594e3a);
            Rasterizer2D.drawHorizontalLine(x, y + 11 + barPos + barHeight, 9, 0x5d513c);
            Rasterizer2D.drawHorizontalLine(x, y + 11 + barPos + barHeight, 7, 0x61553e);
            Rasterizer2D.drawHorizontalLine(x, y + 11 + barPos + barHeight, 5, 0x655841);
            Rasterizer2D.drawHorizontalLine(x, y + 11 + barPos + barHeight, 4, 0x6a5c43);
            Rasterizer2D.drawHorizontalLine(x, y + 11 + barPos + barHeight, 3, 0x73654a);
            Rasterizer2D.drawHorizontalLine(x, y + 11 + barPos + barHeight, 2, 0x7b6a4d);
            Rasterizer2D.drawHorizontalLine(x, y + 11 + barPos + barHeight, 1, 0x000001);
        }
    }

    public static int getTimeOfLastGC() {
        int lastTime = 0;
        if(GrinderScape.garbageCollector == null || !GrinderScape.garbageCollector.isValid()) {
            try {
                for (GarbageCollectorMXBean collectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
                    if (collectorMXBean.isValid()) {
                        GrinderScape.garbageCollector = collectorMXBean;
                        GameShell.garbageCollectorLastCheckTimeMs = -1L;
                        GameShell.garbageCollectorLastCollectionTime = -1L;
                    }
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        if(GrinderScape.garbageCollector != null) {
            long now = Time.currentTimeMillis();
            long collectionTime = GrinderScape.garbageCollector.getCollectionTime();
            if(GameShell.garbageCollectorLastCollectionTime != -1L) {
                long delta = collectionTime - GameShell.garbageCollectorLastCollectionTime;
                long difference = now - GameShell.garbageCollectorLastCheckTimeMs;
                if(0L != difference) {
                    lastTime = (int)(delta * 100L / difference);
                }
            }

            GameShell.garbageCollectorLastCollectionTime = collectionTime;
            GameShell.garbageCollectorLastCheckTimeMs = now;
        }

        return lastTime;
    }

    public static FileArchive createArchive(Client client, int file, String displayedName, String name, int expectedCRC, int x) {
        byte[] buffer = null;

        try {
            if (client.indices[0] != null)
                buffer = client.indices[0].decompress(file);
        } catch (Exception _ex) {
        }

        // Compare crc...
        if (buffer != null) {
            if (Configuration.JAGCACHED_ENABLED) {
                if (!JagGrab.compareCrc(buffer, expectedCRC)) {
                    buffer = null;
                }
            }
        }

        if (buffer != null) {
            FileArchive streamLoader = new FileArchive(buffer);
            return streamLoader;
        }

        // Retry to redl cache cause it's obvious corrupt or something
        if (buffer == null && !Configuration.JAGCACHED_ENABLED) {
            System.out.println("Buffer nulled");
            CacheDownloader.init(true);
            return createArchive(client, file, displayedName, name, expectedCRC, x);
        }

        while (buffer == null) {
            LoginScreen.drawLoadingText(client, x, "Requesting " + displayedName);
            try (DataInputStream in = JagGrab.openJagGrabRequest(name)) {

                // Try to get the file..
                buffer = JagGrab.getBuffer(in);

                // Compare crc again...
                if (buffer != null) {
                    if (!JagGrab.compareCrc(buffer, expectedCRC)) {
                        buffer = null;
                    }
                }

                // Write file
                if (buffer != null) {
                    try {
                        if (client.indices[0] != null)
                            client.indices[0].writeFile(buffer.length, buffer, file);
                    } catch (Exception _ex) {
                        client.indices[0] = null;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                buffer = null;
            }

            if (buffer == null) {
                JagGrab.error("Archives");
            }
        }

        FileArchive streamLoader_1 = new FileArchive(buffer);
        return streamLoader_1;
    }
}
