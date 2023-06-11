package com.runescape.input;

import com.grinder.Configuration;
import com.grinder.client.ClientCompanion;
import com.grinder.model.Keybinding;
import com.grinder.model.Spinner;
import com.runescape.Client;
import com.grinder.client.util.ASCII;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 11/12/2019
 */
public final class KeyHandler implements KeyListener, FocusListener {

    public static final KeyHandler instance;
    public static boolean[] pressedKeys;
    public static volatile int idleCycles;
    public static int[] keyCodes;
    public static int[] releasedKeyCodes;
    public static int __an_ch;
    public static int releasedKeyIndex;
    public static char[] typedKeyChars;
    public static int[] typedKeys;
    public static int[] __an_cp;
    public static int __an_cl;
    public static int typedKeyCharIndex;
    public static int __an_cz;
    public static int __an_cc;

    public static int readChar() {
        synchronized(KeyHandler.instance) {
            if(KeyHandler.typedKeyCharIndex == KeyHandler.__an_cc) {
                return -1;
            } else {
                int typedKey = KeyHandler.typedKeyChars[KeyHandler.typedKeyCharIndex];
                KeyHandler.typedKeyCharIndex = KeyHandler.typedKeyCharIndex + 1 & 127;
                return typedKey;
            }
        }
    }

    static {
        instance = new KeyHandler();
        pressedKeys = new boolean[112];
        releasedKeyCodes = new int[128];
        __an_ch = 0;
        releasedKeyIndex = 0;
        typedKeyChars = new char[128];
        typedKeys = new int[128];
        __an_cp = new int[128];
        __an_cl = 0;
        typedKeyCharIndex = 0;
        __an_cz = 0;
        __an_cc = 0;
        idleCycles = 0;
        keyCodes = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, 85, 80, 84, -1, 91, -1, -1, -1, 81, 82, 86, -1, -1, -1, -1, -1, -1, -1, -1, 13, -1, -1, -1, -1, 83, 104, 105, 103, 102, 96, 98, 97, 99, -1, -1, -1, -1, -1, -1, -1, 25, 16, 17, 18, 19, 20, 21, 22, 23, 24, -1, -1, -1, -1, -1, -1, -1, 48, 68, 66, 50, 34, 51, 52, 53, 39, 54, 55, 56, 70, 69, 40, 41, 32, 35, 49, 36, 38, 67, 33, 65, 37, 64, -1, -1, -1, -1, -1, 228, 231, 227, 233, 224, 219, 225, 230, 226, 232, 89, 87, -1, 88, 229, 90, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, -1, -1, -1, 101, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 100, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    }

    @Override
    public final void focusGained(FocusEvent var1) { }

    @Override
    public void focusLost(FocusEvent e) {
        if(instance != null) {
            releasedKeyIndex = -1;
        }
    }

    @Override
    public final synchronized void keyPressed(KeyEvent e) {
        if(instance != null) {

            int keyCode = e.getKeyCode();

            //System.out.println("pressed key "+keyCode);

            if (Keybinding.isBound(keyCode))
                return;

            if (keyCode == KeyEvent.VK_ESCAPE && Configuration.escapeCloseInterface) {
                // Game mode chooser and Rules interface cannot be closed with ESC button 51200 and 23344 interface ids
                if (Client.loggedIn && ClientCompanion.openInterfaceId != -1 && ClientCompanion.openInterfaceId != 51200 && ClientCompanion.openInterfaceId != 23344 && !Spinner.isSpinnerSpinning(Client.instance.spinnerSpinning)) {
                    Client.instance.clearTopInterfaces();
                    return;
                }
            }

            if(keyCode >= 0 && keyCode < keyCodes.length) {
                keyCode = keyCodes[keyCode];
                if((keyCode & 128) != 0) {
                    keyCode = -1;
                }
            } else {
                keyCode = -1;
            }
            if(releasedKeyIndex >= 0 && keyCode >= 0) {
                releasedKeyCodes[releasedKeyIndex] = keyCode;
                releasedKeyIndex = releasedKeyIndex + 1 & 127;
                if(__an_ch == releasedKeyIndex) {
                    releasedKeyIndex = -1;
                }
            }

            int var3;
            if(keyCode >= 0) {
                var3 = __an_cz + 1 & 127;
                if(var3 != typedKeyCharIndex) {
                    typedKeys[__an_cz] = keyCode;
                    typedKeyChars[__an_cz] = 0;
                    __an_cz = var3;
                }
            }

            var3 = e.getModifiers();
            if((var3 & 10) != 0 || keyCode == 85 || keyCode == 10) {
                e.consume();
            }
        }
    }



    @Override
    public void keyTyped(KeyEvent e) {
        if(instance != null) {
            char keyChar = e.getKeyChar();
            if(keyChar != 0 && keyChar != '\uffff') {
                boolean found;
                if(keyChar < 128 || keyChar >= 160 && keyChar <= 255) {
                    found = true;
                } else {
                    asciiLoop: {
                        char[] chars = ASCII.cp1252AsciiExtension;

                        for (char c : chars) {
                            if (keyChar == c) {
                                found = true;
                                break asciiLoop;
                            }
                        }

                        found = false;
                    }
                }

                if(found) {
                    int var4 = __an_cz + 1 & 127;
                    if(var4 != typedKeyCharIndex) {
                        typedKeys[__an_cz] = -1;
                        typedKeyChars[__an_cz] = keyChar;
                        __an_cz = var4;
                    }
                }
            }
        }

        e.consume();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(instance != null) {
            int keyCode = e.getKeyCode();
            if(keyCode >= 0 && keyCode < keyCodes.length) {
                keyCode = keyCodes[keyCode] & -129;
            } else {
                keyCode = -1;
            }

            if(releasedKeyIndex >= 0 && keyCode >= 0) {
                releasedKeyCodes[releasedKeyIndex] = ~keyCode;
                releasedKeyIndex = releasedKeyIndex + 1 & 127;
                if(__an_ch == releasedKeyIndex) {
                    releasedKeyIndex = -1;
                }
            }
        }

        e.consume();
    }
}
