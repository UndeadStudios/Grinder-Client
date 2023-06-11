package com.grinder.model;

import com.runescape.Client;
import com.grinder.Configuration;
import com.runescape.cache.graphics.widget.Widget;
import com.grinder.client.ClientUtil;

import java.awt.event.KeyEvent;

/**
 * Handles tab keybinds
 *
 * @author Ghost
 */
public class Keybinding {

    public static final int MIN_FRAME = 53009;
    public static final int RESTORE_DEFAULT = 53004;
    public static final int ESCAPE_CONFIG = 53006;
    public static final String[] OPTIONS = {"None", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12"};

    public static final int[] KEYS = {-1, KeyEvent.VK_F1, KeyEvent.VK_F2, KeyEvent.VK_F3, KeyEvent.VK_F4, KeyEvent.VK_F5, KeyEvent.VK_F6, KeyEvent.VK_F7, KeyEvent.VK_F8,
            KeyEvent.VK_F9, KeyEvent.VK_F10, KeyEvent.VK_F11, KeyEvent.VK_F12};


    public static int[] KEYBINDINGS;

    static {
        restoreDefault();
    }

    public static void restoreDefault() {
        KEYBINDINGS = new int[]{
                KeyEvent.VK_F5,
                -1,
                -1,
                KeyEvent.VK_F1,
                KeyEvent.VK_F2,
                KeyEvent.VK_F3,
                KeyEvent.VK_F4,
                KeyEvent.VK_F6,
                KeyEvent.VK_F7,
                KeyEvent.VK_F8,
                KeyEvent.VK_F9,
                KeyEvent.VK_F10,
                KeyEvent.VK_F11,
                KeyEvent.VK_F12,
        };
        Configuration.escapeCloseInterface = true;
    }

    public static void checkDuplicates(int key, int index) {
        for (int i = 0; i < KEYBINDINGS.length; i++) {
            if (KEYS[key] == KEYBINDINGS[i] && i != index && KEYBINDINGS[i] != -1) {
                KEYBINDINGS[i] = -1;
                Widget.interfaceCache[MIN_FRAME + 3 * i].dropdown.setSelected("None");
            }
        }
        if (index != -1 && KEYS[key] == KeyEvent.VK_ESCAPE && Configuration.escapeCloseInterface) {
            Configuration.escapeCloseInterface = !Configuration.escapeCloseInterface;
            Widget.interfaceCache[ESCAPE_CONFIG].active = Configuration.escapeCloseInterface;
        }
    }

    public static void bind(int index, int key) {
        checkDuplicates(key, index);
        KEYBINDINGS[index] = KEYS[key];
        PlayerSettings.savePlayerData(Client.instance);
    }

    public static boolean isBound(int key) {
        for (int i = 0; i < KEYBINDINGS.length; i++) {
            if (key == KEYBINDINGS[i]) {
                ClientUtil.setTab(i);
                return true;
            }
        }
        return false;
    }

    public static void updateInterface() {
        for (int i = 0; i < 14; i++) {
            int id = MIN_FRAME + 3 * i;
            String name;
            int binding = Keybinding.KEYBINDINGS[i];
            if (binding == -1) {
                name = "None";
            } else {
                name = Keybinding.OPTIONS[binding - KeyEvent.VK_F1 + 1];
            }
            Widget.interfaceCache[id].dropdown.setSelected(name);
        }
        Widget.interfaceCache[ESCAPE_CONFIG].active = Configuration.escapeCloseInterface;
    }
}
