package com.runescape.cache.graphics.widget;

import com.grinder.Configuration;
import com.grinder.client.ClientCompanion;
import com.grinder.client.ClientUtil;
import com.grinder.model.ChatBox;
import com.grinder.model.Keybinding;
import com.grinder.model.PlayerSettings;
import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.Client.ScreenMode;
import com.runescape.audio.Audio;
import com.runescape.cache.FileArchive;
import com.runescape.cache.graphics.GameFont;
import com.runescape.cache.graphics.RSFont;
import com.runescape.io.jaggrab.JagGrab;
import com.runescape.scene.SceneGraph;

public class SettingsWidget extends Widget {

    public static final int PLAYER_ATTACK_DROPDOWN = 42554;
    public static final int NPC_ATTACK_DROPDOWN = 42556;
    public static final int ZOOM_SLIDER = 42525;
    public static final int BRIGHTNESS_SLIDER = 42526;
    public static final int MUSIC_SLIDER = 42532;
    public static final int SOUND_SLIDER = 42534;
    public static final int AMBIENT_SOUND_SLIDER = 42535;
    /* Settings */
    public static final int FIXED_MODE = 42522;
    public static final int RESIZABLE_MODE = 42523;
    public static final int ACCEPT_AID = 12464;
    public static final int RUN = 19158; // 42507
    public static final int CHAT_EFFECTS = 42541;
    public static final int SPLIT_PRIVATE_CHAT = 42542;
    public static final int MOUSE_BUTTONS = 42551;
    public static final int SHIFT_CLICK_DROP = 42553;
    /* Advanced settings */
    /* Row 1 */
    public static final int TRANSPARENT_SIDE_PANEL = 23004;
    public static final int TRANSPARENT_CHATBOX = 23005;
    public static final int SIDE_STONES_ARRANGEMENT = 23006;
    public static final int ROOF_REMOVAL = 23007;
    public static final int ORBS = 23008;
    public static final int SPEC_ORB = 23009;
    /* Row 2 */
    public static final int COMBAT_OVERLAY = 23010;
    public static final int BUFF_OVERLAY = 23011;
    public static final int GROUND_ITEM_NAMES = 23012;
    public static final int FOG = 23013;
    public static final int TIMERS = 23014;
    public static final int SKILL_ORBS = 23015;
    /* Row 3 */
    public static final int TWEENING = 23016;
    public static final int AUTOCAST = 23017;
    public static final int ACHIEVEMENT_OVERLAY = 23018;
    public static final int PARTICLES = 23019;

    public static final int MUSIC_SPRITE_INSTRUCTION = 100;
    public static final int AUDIO_SPRITE_INSTRUCTION = 101;
    public static final int AMBIENT_SOUND_SPRITE_INSTRUCTION = 102;

    public static void widget(GameFont[] tda) {
        Widget tab = addInterface(42500);

        addSpriteLoader(42501, 355); // Frame
        /* Top buttons */
        configHoverButton(42502, "Display", 164, 165, 166, 167, true, new int[]{42503, 42504, 42505});
        configHoverButton(42503, "Audio", 168, 169, 170, 171, false, new int[]{42502, 42504, 42505});
        configHoverButton(42504, "Chat", 172, 173, 174, 175, false, new int[]{42502, 42503, 42505});
        configHoverButton(42505, "Controls", 176, 177, 178, 179, false, new int[]{42502, 42503, 42504});
		/* Bottom buttons */
        configButton(ACCEPT_AID, "Toggle Accept Aid", 341, 340);

        //configButton(RUN, "Toggle Run", 343, 342);
        hoverButton(42508, "View Account Settings", 494, 494);
        hoverButton(42509, "View Store", 350, 350);
		/* Middle */
        Widget display = addTabInterface(42520);
        Widget audio = addTabInterface(42530);
        Widget chat = addTabInterface(42540);
        Widget controls = addTabInterface(42550);

        tab.totalChildren(13);
        int childNum = 0;

        setBounds(42501, 3, 42, childNum++, tab);

        int x = 0;
        for (int i = 0; i < 4; i++, x += 46) {
            tab.child(childNum++, 42502 + i, 6 + x, 0);
        }

        x = 0;
        interfaceCache[ACCEPT_AID].width = interfaceCache[ACCEPT_AID].height = 40;
        tab.child(childNum++, ACCEPT_AID, 6 + x, 219);
        tab.child(childNum++, 19137, 10 + x, 224);
        x += 46;
        tab.child(childNum++, RUN, 6 + x, 219);
        tab.child(childNum++, 19159, 17 + x, 223); // Icon
        tab.child(childNum++, 19177, 13 + x, 241); // Text
        x += 46;
        tab.child(childNum++, 42508, 6 + x, 219);
        x += 46;
        tab.child(childNum++, 42509, 6 + x, 219);

        /*setBounds(42502, 6, 0, childNum++, tab);
        setBounds(ACCEPT_AID, 6, 219, childNum++, tab);*/
        /*int x = 0;
        for (int i = 0; i < 4; i++, x += 46) {
            setBounds(42502 + i, 6 + x, 0, childNum++, tab);
            setBounds(42506 + i, 6 + x, 219, childNum++, tab);
        }*/

        setBounds(42520, 0, 0, childNum++, tab); // Adjustable middle widget

        displaySettings(display, tda);
        audioSettings(audio, tda);
        chatSettings(chat);
        controlsSettings(controls, tda);
    }

    public static void displaySettings(Widget display, GameFont[] tda) {
		/* Mouse zoom */
        hoverButton(42521, "Restore Default Zoom", 189, 190);
		/* Screen sizes */
        configHoverButton(FIXED_MODE, "Fixed mode", 185, 185, 485, 186, true, 42523);
        configHoverButton(RESIZABLE_MODE, "Resizable mode", 187, 188, 484, 484, false, 42522);
		/* Advanced options */
        hoverButton(42524, "Configure @lre@Advanced options", 353, 353, "Advanced options", Widget.newFonts[1], 0xff981f, 0xffffff, true);
		/* Sliders */
        slider(ZOOM_SLIDER, 0, (ClientUI.frameMode == ScreenMode.RESIZABLE || ClientUI.frameMode == ScreenMode.FULLSCREEN) ? 200 : 600, 128, 354, 481, 1);
        slider(BRIGHTNESS_SLIDER, 0.6, 1.0, 128, 482, 481, 2);
		/* Brightness */
        addSpriteLoader(42527, 483);

        display.totalChildren(7);
        int childNum = 0;

        setBounds(42521, 11, 50, childNum++, display);
        setBounds(42522, 25, 118, childNum++, display);
        setBounds(42523, 102, 118, childNum++, display);
        setBounds(42524, 25, 176, childNum++, display);
        setBounds(42525, 47, 59, childNum++, display);
        setBounds(42526, 47, 92, childNum++, display);
        setBounds(42527, 11, 83, childNum++, display);
    }

    public static void audioSettings(Widget audio, GameFont[] tda) {
        //addSpriteLoader(42531, 498);
        interfaceCache[19131].valueIndexArray[0][0] = MUSIC_SPRITE_INSTRUCTION;
        slider(MUSIC_SLIDER, 0, 255 + 15, 128, 482, 499, 3); // extra 15 on max to make small values mute volume, see music slider case
        //addSpriteLoader(42533, 497);
        interfaceCache[19149].valueIndexArray[0][0] = AUDIO_SPRITE_INSTRUCTION;
        slider(SOUND_SLIDER, 0, 127+15, 128, 482, 499, 4);

        interfaceCache[19155].valueIndexArray[0][0] = AMBIENT_SOUND_SPRITE_INSTRUCTION;
        slider(AMBIENT_SOUND_SLIDER, 0, 127+15, 128, 482, 499, 5);

        //hoverButton(42535, "Previous", 399, 399);
        //hoverButton(42536, "Next", 400, 400);
        //addSpriteLoader(42537, 402);
        //hoverButton(42538, "", 401, 401, "Adventure", Widget.newFonts[0], 0xff981f, 0xffffff, true);
       // addText(42539, "Now Playing:", tda, 1, 0xfe971e, false, true);

        audio.totalChildren(10);
        int childNum = 0;

        //setBounds(42531, 12, 56, childNum++, audio);
        setBounds(19131, 12, 56, childNum++, audio);
        setBounds(MUSIC_SLIDER, 50, 65, childNum++, audio);
        //setBounds(42533, 11, 95, childNum++, audio);
        setBounds(19149, 11, 95, childNum++, audio);
        setBounds(SOUND_SLIDER, 50, 104, childNum++, audio);

        setBounds(19155, 11, 134, childNum++, audio);
        setBounds(AMBIENT_SOUND_SLIDER, 50, 143, childNum++, audio);

        setBounds(42537, 15, 142, childNum++, audio);
        setBounds(42539, 43, 147, childNum++, audio);
        setBounds(42538, 32, 177, childNum++, audio);
//        setBounds(42535, 13, 181, childNum++, audio);
        setBounds(42536, 165, 181, childNum++, audio);

    }

    public static void chatSettings(Widget chat) {
        configButton(CHAT_EFFECTS, "Toggle Chat Effects", 487, 486);
        configButton(SPLIT_PRIVATE_CHAT, "Toggle Split Private Chat", 489, 488);
        hoverButton(42543, "Notifications", 490, 490);
        hoverButton(42544, "Configure @lre@Display name", 353, 353, "Display name", Widget.newFonts[1], 0xff981f, 0xffffff, true);

        chat.totalChildren(4);
        int childNum = 0;

        int[] buttons = new int[]{42541, 42542, 42543};
        int x = 25;
        for (int btn : buttons) {
            setBounds(btn, x, 80, childNum++, chat);
            x += 50;
        }
        setBounds(42544, 25, 145, childNum++, chat);
    }

    public static void controlsSettings(Widget controls, GameFont[] tda) {
        configButton(MOUSE_BUTTONS, "Toggle number of Mouse Buttons", 492, 491);
        hoverButton(42552, "Keybinding", 493, 493);
        configButton(SHIFT_CLICK_DROP, "Toggle Shift Click Drop", 496, 495);


        String[] options = {"Depends on combat levels", "Always right-click", "Left-click where available", "Hidden"};

        dropdownMenu(PLAYER_ATTACK_DROPDOWN, 166, 0, options, Dropdown.PLAYER_ATTACK_OPTION_PRIORITY);
        addText(42555, "Player 'Attack' options:", tda, 1, 0xfe971e, false, true);

        dropdownMenu(NPC_ATTACK_DROPDOWN, 166, 2, options, Dropdown.NPC_ATTACK_OPTION_PRIORITY);
        addText(42557, "NPC 'Attack' options:", tda, 1, 0xfe971e, false, true);

        controls.totalChildren(7);
        int childNum = 0;

        int[] buttons = new int[]{42551, 42552, 42553};
        int x = 25;
        for (int btn : buttons) {
            setBounds(btn, x, 60, childNum++, controls);
            x += 50;
        }

        setBounds(42555, 13, 114, childNum++, controls);
        setBounds(42556, 13, 181, childNum++, controls);
        setBounds(42557, 13, 161, childNum++, controls);
        setBounds(42554, 13, 134, childNum++, controls);
    }

    public static void advancedWidget(GameFont[] tda) {
        Widget widget = addInterface(23000);
        addSpriteLoader(23001, 393);
        addText(23002, "Advanced Options", tda, 2, 0xff981f, true, true);
        addCloseButton(23003, true);

		/* Row 1 */
        configButton(TRANSPARENT_SIDE_PANEL, "Transparent Side-panel", 501, 500);
        configButton(TRANSPARENT_CHATBOX, "Transparent Chatbox", 503, 502);
        configButton(SIDE_STONES_ARRANGEMENT, "Side-stones Arrangement", 505, 504);
        configButton(ROOF_REMOVAL, "Roof-removal", 344, 349);
        configButton(ORBS, "Orbs", 507, 506);
        configButton(SPEC_ORB, "Special Orb", 526, 527);

		/* Row 2 */
        configButton(COMBAT_OVERLAY, "Combat Overlay", 510, 511);
        configButton(BUFF_OVERLAY, "Buff Overlay", 508, 509);
        configButton(GROUND_ITEM_NAMES, "Ground Item Names", 512, 513);
        configButton(FOG, "Fog", 518, 519);
        configButton(TIMERS, "Timers", 524, 525);
        configButton(SKILL_ORBS, "Skill Orbs", 514, 515);

		/* Row 3 */
        configButton(TWEENING, "Tweening", 522, 523);
        configButton(AUTOCAST, "Autocast On Top", 684, 685);
        configButton(ACHIEVEMENT_OVERLAY, "Achievement Progress Overlay", 947, 948);
        configButton(PARTICLES, "Particles", 996, 997);


        widget.totalChildren(19);
        int childNum = 0;
        setBounds(23001, 100, 60, childNum++, widget);
        setBounds(23002, 253, 70, childNum++, widget);
        setBounds(23003, 382, 67, childNum++, widget);

        int x = 110;
        int y = 104;
        for (int i = TRANSPARENT_SIDE_PANEL; i <= PARTICLES; i++, x += 50) {
            if (i == 23010 || i == 23016) {
                x = 110;
                y += 50;
            } // Next row
            setBounds(i, x, y, childNum++, widget);
        }
    }
    public static Client client() {
        return Client.instance;
    }

    public static void settings(int button) {
        switch (button) {
            case 42502:
            case 42503:
            case 42504:
            case 42505:
                switchSettings(button);
                break;
            case FIXED_MODE:
                Configuration.loginMode = ScreenMode.FIXED;
                ClientUI.frameMode(Client.ScreenMode.FIXED, false);
                Slider slider;
                Client.cameraZoom = 600;
                slider = Widget.interfaceCache[ZOOM_SLIDER].slider;
                slider.setValue(600);
                break;
            case RESIZABLE_MODE:
                Configuration.loginMode = ScreenMode.RESIZABLE;
                ClientUI.frameMode(ScreenMode.RESIZABLE, false);
                Client.cameraZoom = 0;
                slider = Widget.interfaceCache[ZOOM_SLIDER].slider;
                slider.setValue(0);
                break;
            case SHIFT_CLICK_DROP:
                Configuration.enableShiftClickDrop = !Configuration.enableShiftClickDrop;
                PlayerSettings.savePlayerData(Client.instance);
                break;
            case 42521:
                if (ClientUI.frameMode == ScreenMode.RESIZABLE || ClientUI.frameMode == ScreenMode.FULLSCREEN) {
                    Client.cameraZoom = 0;
                    slider = Widget.interfaceCache[ZOOM_SLIDER].slider;
                    slider.setValue(0);
                    return;
                }
                Client.cameraZoom = 600;
                slider = Widget.interfaceCache[ZOOM_SLIDER].slider;
                slider.setValue(600);
                break;
            case 42552:
                Keybinding.updateInterface();
                break;
        }
    }

    public static void advancedSettings(int button) {
        switch (button) {
            case TRANSPARENT_SIDE_PANEL:
                ClientCompanion.transparentTabArea = !ClientCompanion.transparentTabArea;
                break;
            case TRANSPARENT_CHATBOX:
                ChatBox.changeChatArea = !ChatBox.changeChatArea;
                break;
            case SIDE_STONES_ARRANGEMENT:
            	if (ClientUI.frameMode == ScreenMode.FIXED) {
            		return;
            	}
            	ClientCompanion.stackSideStones = !ClientCompanion.stackSideStones;
                break;
            case ROOF_REMOVAL:
                Configuration.enableRoofs = !Configuration.enableRoofs;
                break;
            case ORBS:
                Configuration.enablePrayerEnergyWorldOrbs = !Configuration.enablePrayerEnergyWorldOrbs;
                break;
            case FOG:
                Configuration.enableFog = !Configuration.enableFog;
                break;
            case GROUND_ITEM_NAMES:
                Configuration.enableGroundItemNames = !Configuration.enableGroundItemNames;
                break;
            case SKILL_ORBS:
                Configuration.enableSkillOrbs = !Configuration.enableSkillOrbs;
                break;
            case TWEENING:
                Configuration.enableTweening = !Configuration.enableTweening;
                break;
            case AUTOCAST:
                Configuration.autocastOnTop = !Configuration.autocastOnTop;
                break;
            case COMBAT_OVERLAY:
                Configuration.combatOverlayBox = !Configuration.combatOverlayBox;
                break;
            case BUFF_OVERLAY:
                Configuration.enableBuffOverlay = !Configuration.enableBuffOverlay;
                break;
            case TIMERS:
                Configuration.enableTimers = !Configuration.enableTimers;
                break;
            case SPEC_ORB:
                Configuration.enableSpecOrb = !Configuration.enableSpecOrb;
                break;
            case ACHIEVEMENT_OVERLAY:
                Configuration.achievementProgressOverlay = !Configuration.achievementProgressOverlay;
                break;
            case PARTICLES:
                Configuration.enableParticles = !Configuration.enableParticles;
                if(Configuration.enableParticles){
                    ClientCompanion.particleSystem.enable();
                } else
                    ClientCompanion.particleSystem.disable();
                break;
        }
        PlayerSettings.savePlayerData(Client.instance);
    }

    public static void switchSettings(int button) {
        int tab = button - 42502;
        int[] tabs = new int[]{42520, 42530, 42540, 42550};
        Widget.interfaceCache[42500].children[12] = tabs[tab];
    }

    public static void updateSettings() {
		/* Settings */
        Widget.interfaceCache[CHAT_EFFECTS].active = Configuration.enableChatEffects;
        Widget.interfaceCache[SPLIT_PRIVATE_CHAT].active = Configuration.enableSplitPrivate;
        Widget.interfaceCache[MOUSE_BUTTONS].active = Client.instance.settings[170] == 0;
        Widget.interfaceCache[SHIFT_CLICK_DROP].active = Configuration.enableShiftClickDrop;

        Widget.interfaceCache[PLAYER_ATTACK_DROPDOWN].dropdown.setSelected(Widget.interfaceCache[42554].dropdown.getOptions()[Configuration.playerAttackOptionPriority]);
        Widget.interfaceCache[NPC_ATTACK_DROPDOWN].dropdown.setSelected(Widget.interfaceCache[42556].dropdown.getOptions()[Configuration.npcAttackOptionPriority]);

        Widget.interfaceCache[ZOOM_SLIDER].slider.setValue(Client.cameraZoom);
        Widget.interfaceCache[BRIGHTNESS_SLIDER].slider.setValue(ClientCompanion.brightnessState);
        Widget.interfaceCache[MUSIC_SLIDER].slider.setValue(255 - Audio.musicTrackVarpType);
        Widget.interfaceCache[SOUND_SLIDER].slider.setValue(127 - Audio.soundEffectVolume);
        Widget.interfaceCache[AMBIENT_SOUND_SLIDER].slider.setValue(127 - Audio.areaSoundEffectVolume);

		/* Advanced settings*/
        Widget.interfaceCache[TRANSPARENT_SIDE_PANEL].active = ClientCompanion.transparentTabArea;
        Widget.interfaceCache[TRANSPARENT_CHATBOX].active = ChatBox.changeChatArea;
        Widget.interfaceCache[SIDE_STONES_ARRANGEMENT].active = ClientCompanion.stackSideStones;
        Widget.interfaceCache[ROOF_REMOVAL].active = Configuration.enableRoofs;
        Widget.interfaceCache[ORBS].active = Configuration.enablePrayerEnergyWorldOrbs;
        Widget.interfaceCache[TIMERS].active = Configuration.enableTimers;
        Widget.interfaceCache[SPEC_ORB].active = Configuration.enableSpecOrb;


        Widget.interfaceCache[COMBAT_OVERLAY].active = Configuration.combatOverlayBox;
        Widget.interfaceCache[BUFF_OVERLAY].active = Configuration.enableBuffOverlay;
        Widget.interfaceCache[GROUND_ITEM_NAMES].active = Configuration.enableGroundItemNames;
        Widget.interfaceCache[FOG].active = Configuration.enableFog;
        Widget.interfaceCache[SKILL_ORBS].active = Configuration.enableSkillOrbs;

        Widget.interfaceCache[TWEENING].active = Configuration.enableTweening;
        Widget.interfaceCache[AUTOCAST].active = Configuration.autocastOnTop;
        Widget.interfaceCache[ACHIEVEMENT_OVERLAY].active = Configuration.achievementProgressOverlay;
        Widget.interfaceCache[PARTICLES].active = Configuration.enableParticles;

        Widget.interfaceCache[ACCEPT_AID].active = Configuration.acceptAid;
//        Widget.interfaceCache[FIXED_MODE].active = Configuration.loginMode == ScreenMode.FIXED;
//        Widget.interfaceCache[RESIZABLE_MODE].active = Configuration.loginMode == ScreenMode.RESIZABLE;

    }

}
