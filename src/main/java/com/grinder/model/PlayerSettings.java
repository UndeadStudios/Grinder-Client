package com.grinder.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grinder.Configuration;
import com.grinder.client.util.Log;
import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.audio.Audio;
import com.runescape.cache.graphics.widget.Spellbooks;
import com.runescape.sign.SignLink;
import com.runescape.util.EncryptUtils;
import com.grinder.client.ClientCompanion;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author unknown
 * @author Stan (moved this to a file, did some fixes)
 * @version 1.0
 */
public class PlayerSettings {

    private final static File SETTINGS_FILE = Paths.get(SignLink.findcachedir(), "settings.json").toFile();

    public static void loadPlayerData(Client client) {

        if (!SETTINGS_FILE.exists())
            return;

        Log.info("Loading player settings at "+SETTINGS_FILE.getPath());

        try (FileReader fileReader = new FileReader(SETTINGS_FILE)) {
            JsonParser fileParser = new JsonParser();
            Gson builder = new GsonBuilder().create();
            JsonObject reader = (JsonObject) fileParser.parse(fileReader);

            if (reader.has("username")) {
                client.myUsername = reader.get("username").getAsString();
            }

            if (reader.has("password")) {
                client.myPassword = reader.get("password").getAsString();

                String encoded = EncryptUtils.base64decode(EncryptUtils.base64encode(reader.get("password").getAsString()));

                client.myPassword = EncryptUtils.xorMessage(encoded, "MD");
            }

            if (reader.has("remember-username")) {
                LoginScreen.rememberUsername = reader.get("remember-username").getAsBoolean();

                if (LoginScreen.rememberUsername && client.myUsername.length() > 0) {
                    LoginScreen.loginScreenCursorPos = 1;
                }
            }

            if (reader.has("remember-password")) {
                LoginScreen.rememberPassword = reader.get("remember-password").getAsBoolean();
            }

            if (reader.has("run-enabled")) {
                client.settings[429] = reader.get("run-enabled").getAsBoolean() ? 1 : 0;
            }

            if (reader.has("chat-effects")) {
                Configuration.enableChatEffects = reader.get("chat-effects").getAsBoolean();
            }

            if (reader.has("split-private")) {
                Configuration.enableSplitPrivate = reader.get("split-private").getAsBoolean();
            }

            if (reader.has("mouse-buttons")) {
                client.mouseButtonSetting = client.settings[170] = reader.get("mouse-buttons").getAsBoolean() ? 1 : 0;
            }

            if (reader.has("shift-click-drop")) {
                Configuration.enableShiftClickDrop = reader.get("shift-click-drop").getAsBoolean();
            }

            if (reader.has("player-attack-option")) {
                Configuration.playerAttackOptionPriority = reader.get("player-attack-option").getAsByte();
            }

            if (reader.has("npc-attack-option")) {
                Configuration.npcAttackOptionPriority = reader.get("npc-attack-option").getAsByte();
            }

            if (reader.has("transparent-tab-area")) {
                Configuration.enableShiftClickDrop = reader.get("transparent-tab-area").getAsBoolean();
            }

            if (reader.has("change-chat-area")) {
                ChatBox.changeChatArea = reader.get("change-chat-area").getAsBoolean();
            }

            if (reader.has("stack-side-stones")) {
                ClientCompanion.stackSideStones = reader.get("stack-side-stones").getAsBoolean();
            }

            if (reader.has("roofs")) {
                Configuration.enableRoofs = reader.get("roofs").getAsBoolean();
            }

            if (reader.has("orbs")) {
                Configuration.enablePrayerEnergyWorldOrbs = reader.get("orbs").getAsBoolean();
            }

            if (reader.has("spec-orb")) {
                Configuration.enableSpecOrb = reader.get("spec-orb").getAsBoolean();
            }

            if (reader.has("combat-overlay")) {
                Configuration.combatOverlayBox = reader.get("combat-overlay").getAsBoolean();
            }

            if (reader.has("buff-overlay")) {
                Configuration.enableBuffOverlay = reader.get("buff-overlay").getAsBoolean();
            }

            if (reader.has("ground-item-names")) {
                Configuration.enableGroundItemNames = reader.get("ground-item-names").getAsBoolean();
            }

            if (reader.has("fog")) {
                Configuration.enableFog = reader.get("fog").getAsBoolean();
            }

            if (reader.has("timers")) {
                Configuration.enableTimers = reader.get("timers").getAsBoolean();
            }

            if (reader.has("skill-orbs")) {
                Configuration.enableSkillOrbs = reader.get("skill-orbs").getAsBoolean();
            }

            if (reader.has("tweening")) {
                Configuration.enableTweening = reader.get("tweening").getAsBoolean();
            }

            if (reader.has("escape-close-interface")) {
                Configuration.escapeCloseInterface = reader.get("escape-close-interface").getAsBoolean();
            }

            if (reader.has("xp-drops-group")) {
                Configuration.xpDropsGroup = reader.get("xp-drops-group").getAsBoolean();
            }

            if (reader.has("hp-above-heads")) {
                Configuration.hpAboveHeads = reader.get("hp-above-heads").getAsBoolean();
            }

            if (reader.has("brightness-state")) {
                ClientCompanion.brightnessState = reader.get("brightness-state").getAsDouble();
            }

            if (reader.has("key-bindings")) {
                int[] keys = builder.fromJson(reader.get("key-bindings").getAsJsonArray(), int[].class);
                System.arraycopy(keys, 0, Keybinding.KEYBINDINGS, 0, keys.length);
            }

            if (reader.has("autocast-on-top")) {
                Configuration.autocastOnTop = reader.get("autocast-on-top").getAsBoolean();
            }

            if (reader.has("achievement-overlay")) {
                Configuration.achievementProgressOverlay = reader.get("achievement-overlay").getAsBoolean();
            }

            if (reader.has("music-volume"))
                Configuration.gameMusicVolume = reader.get("music-volume").getAsInt();

            if (reader.has("login-music")) {
                Configuration.loginMusic = reader.get("login-music").getAsBoolean();
                if (Configuration.loginMusic)
                    Audio.musicTrackVarpType = 255;
                else
                    Audio.musicTrackVarpType = 0;
            } else
                Configuration.loginMusic = Configuration.gameMusicVolume > 0;

            if (reader.has("sound-volume"))
                Audio.soundEffectVolume = reader.get("sound-volume").getAsInt();
            if (reader.has("object-sound-volume"))
                Audio.areaSoundEffectVolume = reader.get("object-sound-volume").getAsInt();

            if (reader.has("xp-counter-open")) {
                Configuration.xpCounterOpen = reader.get("xp-counter-open").getAsBoolean();
            }

            if (reader.has("xp-drops-position")) {
                Configuration.xpDropsPosition = reader.get("xp-drops-position").getAsInt();
            }

            if (reader.has("xp-counter-size")) {
                Configuration.xpCounterSize = reader.get("xp-counter-size").getAsInt();
            }

            if (reader.has("xp-drops-speed")) {
                Configuration.xpDropsSpeed = reader.get("xp-drops-speed").getAsInt();
            }

            if (reader.has("xp-counter-type")) {
                Configuration.xpCounterType = reader.get("xp-counter-type").getAsInt();
            }

            if (reader.has("xp-counter-progress")) {
                Configuration.xpCounterProgress = reader.get("xp-counter-progress").getAsInt();
            }

            if (reader.has("xp-drops-colour")) {
                Configuration.xpDropsColour = reader.get("xp-drops-colour").getAsInt();
            }

            if (reader.has("chat-box-height")) {
                ChatBox.chatBoxHeight = reader.get("chat-box-height").getAsInt();
            }

            if (reader.has("sort-spell-state")) {
                Configuration.sortSpellsState = reader.get("sort-spell-state").getAsInt();
                client.setButtonConfig(Spellbooks.SORT_SPELL_STATE_CONFIG, Configuration.sortSpellsState);
            }

            if (reader.has("filter-combat-spells")) {
                Configuration.filterCombatSpells = reader.get("filter-combat-spells").getAsBoolean();
                client.setButtonConfig(Spellbooks.FILTER_COMBAT_BUTTON_CONFIG, Configuration.filterCombatSpells ? 1 : 0);
            }

            if (reader.has("filter-teleport-spells")) {
                Configuration.filterTeleportSpells = reader.get("filter-teleport-spells").getAsBoolean();
                client.setButtonConfig(Spellbooks.FILTER_TELEPORT_BUTTON_CONFIG, Configuration.filterTeleportSpells ? 1 : 0);
            }

            if (reader.has("filter-utility-spells")) {
                Configuration.filterUtilitySpells = reader.get("filter-utility-spells").getAsBoolean();
                client.setButtonConfig(Spellbooks.FILTER_UTILITY_BUTTON_CONFIG, Configuration.filterUtilitySpells ? 1 : 0);
            }

            if (reader.has("filter-level")) {
                Configuration.filterLevel = reader.get("filter-level").getAsBoolean();
                client.setButtonConfig(Spellbooks.FILTER_LEVEL_BUTTON_CONFIG, Configuration.filterLevel ? 1 : 0);
            }

            if (reader.has("filter-runes")) {
                Configuration.filterRunes = reader.get("filter-runes").getAsBoolean();
                client.setButtonConfig(Spellbooks.FILTER_RUNES_BUTTON_CONFIG, Configuration.filterRunes ? 1 : 0);
            }

            if (reader.has("accept-aid")) {
                Configuration.acceptAid = reader.get("accept-aid").getAsBoolean();
            }

            if (reader.has("shift-drop")) {
                Configuration.enableShiftClickDrop = reader.get("shift-drop").getAsBoolean();
            }

            if (reader.has("particles")) {
                Configuration.enableParticles = reader.get("particles").getAsBoolean();
            }
            if (reader.has("screen-mode")) {
                Configuration.loginMode = Client.ScreenMode.valueOf(reader.get("screen-mode").getAsString());
            }
        } catch (Exception e) {
            Log.error("Failed to load player settings", e);
        }
    }

    public static void savePlayerData(Client client) {
        try {


            if (!SETTINGS_FILE.exists()) {
                if(!SETTINGS_FILE.createNewFile()){
                    Log.error("Could not create player settings file at "+SETTINGS_FILE.getPath());
                    return;
                }
            }

            Log.info("Saving player settings at "+SETTINGS_FILE.getPath());

            try (FileWriter writer = new FileWriter(SETTINGS_FILE)) {

                Gson builder = new GsonBuilder().setPrettyPrinting().create();
                JsonObject object = new JsonObject();

                object.addProperty("username", LoginScreen.rememberUsername ? client.myUsername : "");
                object.addProperty("password", LoginScreen.rememberPassword ? EncryptUtils.xorMessage(client.myPassword, "MD") : "");
                object.addProperty("remember-username", LoginScreen.rememberUsername);
                object.addProperty("remember-password", LoginScreen.rememberPassword);
                object.addProperty("run-enabled", client.settings[429] == 1);
                object.addProperty("chat-effects", Configuration.enableChatEffects);
                object.addProperty("split-private", Configuration.enableSplitPrivate);
                object.addProperty("mouse-buttons", client.settings[170] == 1);
                object.addProperty("shift-click-drop", Configuration.enableShiftClickDrop);
                object.addProperty("player-attack-option", Configuration.playerAttackOptionPriority);
                object.addProperty("npc-attack-option", Configuration.npcAttackOptionPriority);
                object.addProperty("transparent-tab-area", ClientCompanion.transparentTabArea);
                object.addProperty("change-chat-area", ChatBox.changeChatArea);
                object.addProperty("stack-side-stones", ClientCompanion.stackSideStones);
                object.addProperty("roofs", Configuration.enableRoofs);
                object.addProperty("orbs", Configuration.enablePrayerEnergyWorldOrbs);
                object.addProperty("spec-orb", Configuration.enableSpecOrb);
                object.addProperty("combat-overlay", Configuration.combatOverlayBox);
                object.addProperty("buff-overlay", Configuration.enableBuffOverlay);
                object.addProperty("ground-item-names", Configuration.enableGroundItemNames);
                object.addProperty("fog", Configuration.enableFog);
                object.addProperty("timers", Configuration.enableTimers);
                object.addProperty("skill-orbs", Configuration.enableSkillOrbs);
                object.addProperty("tweening", Configuration.enableTweening);
                object.addProperty("esc-close-interface", Configuration.escapeCloseInterface);
                object.addProperty("xp-drops-group", Configuration.xpDropsGroup);
                object.addProperty("hp-above-heads", Configuration.hpAboveHeads);
                object.addProperty("brightness-state", ClientCompanion.brightnessState);
                object.add("key-bindings", builder.toJsonTree(Keybinding.KEYBINDINGS));
                object.addProperty("autocast-on-top", Configuration.autocastOnTop);
                object.addProperty("achievement-overlay", Configuration.achievementProgressOverlay);
                object.addProperty("login-music", Configuration.loginMusic);
                object.addProperty("music-volume", Audio.musicTrackVarpType);
                object.addProperty("sound-volume", Audio.soundEffectVolume);
                object.addProperty("object-sound-volume", Audio.areaSoundEffectVolume);
                object.addProperty("xp-counter-open", Configuration.xpCounterOpen);
                object.addProperty("xp-drops-position", Configuration.xpDropsPosition);
                object.addProperty("xp-counter-size", Configuration.xpCounterSize);
                object.addProperty("xp-drops-speed", Configuration.xpDropsSpeed);
                object.addProperty("xp-counter-type", Configuration.xpCounterType);
                object.addProperty("xp-counter-progress", Configuration.xpCounterProgress);
                object.addProperty("xp-drops-colour", Configuration.xpDropsColour);
                object.addProperty("chat-box-height", ChatBox.chatBoxHeight);
                object.addProperty("sort-spell-state", Configuration.sortSpellsState);
                object.addProperty("filter-combat-spells", Configuration.filterCombatSpells);
                object.addProperty("filter-teleport-spells", Configuration.filterTeleportSpells);
                object.addProperty("filter-utility-spells", Configuration.filterUtilitySpells);
                object.addProperty("filter-level", Configuration.filterLevel);
                object.addProperty("filter-runes", Configuration.filterRunes);
                object.addProperty("accept-aid", Configuration.acceptAid);
                object.addProperty("shift-drop", Configuration.enableShiftClickDrop);
                object.addProperty("particles", Configuration.enableParticles);
                object.addProperty("screen-mode", ClientUI.frameMode.name());

                writer.write(builder.toJson(object));
                writer.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
