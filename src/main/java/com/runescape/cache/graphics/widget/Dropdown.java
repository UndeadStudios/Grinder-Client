package com.runescape.cache.graphics.widget;

import com.grinder.model.PlayerSettings;
import com.runescape.Client;
import com.grinder.Configuration;
import com.runescape.io.packets.outgoing.impl.ClickButtonAction;
import com.grinder.model.Keybinding;

public enum Dropdown {


    KEYBIND_SELECTION() {
        @Override
        public void selectOption(int selected, Widget dropdown) {
            Keybinding.bind((dropdown.id - Keybinding.MIN_FRAME) / 3, selected);
        }
    },

    TELEPORT_SELECTION() {
        @Override
        public void selectOption(int selected, Widget r) {
            int[] teleports = {28103, 28200, 28250, 28400, 28150, 28300, 28350};
            Widget.interfaceCache[28100].children[2] = teleports[selected];
        }
    },

    PLAYER_ATTACK_OPTION_PRIORITY() {
        @Override
        public void selectOption(int selected, Widget r) {
            Configuration.playerAttackOptionPriority = selected;
        }
    },

    NPC_ATTACK_OPTION_PRIORITY() {
        @Override
        public void selectOption(int selected, Widget r) {
            Configuration.npcAttackOptionPriority = selected;
        }
    },

    EXP_COUNTER_POSITION() {
        @Override
        public void selectOption(int selected, Widget r) {
            Configuration.xpDropsPosition = selected;
            PlayerSettings.savePlayerData(Client.instance);
        }
    },

    EXP_COUNTER_SIZE() {
        @Override
        public void selectOption(int selected, Widget r) {
            Configuration.xpCounterSize = selected;
            PlayerSettings.savePlayerData(Client.instance);
        }
    },

    EXP_DROPS_SPEED() {
        @Override
        public void selectOption(int selected, Widget r) {
            Configuration.xpDropsSpeed = selected;
            PlayerSettings.savePlayerData(Client.instance);
        }
    },

    EXP_COUNTER_MULTIPLY() {
        @Override
        public void selectOption(int selected, Widget r) {
            Client.instance.sendPacket(new ClickButtonAction(ExpCounterSetup.MULTIPLY_DROPDOWN, selected).create());
            PlayerSettings.savePlayerData(Client.instance);
        }
    },

    EXP_COUNTER_TYPE() {
        @Override
        public void selectOption(int selected, Widget r) {
            Configuration.xpCounterType = selected;
            PlayerSettings.savePlayerData(Client.instance);
        }
    },

    EXP_COUNTER_PROGRESS() {
        @Override
        public void selectOption(int selected, Widget r) {
            Configuration.xpCounterProgress = selected;
            PlayerSettings.savePlayerData(Client.instance);
        }
    },

    EXP_DROPS_COLOUR() {
        @Override
        public void selectOption(int selected, Widget r) {
            Configuration.xpDropsColour =  ExpCounterSetup.ExpDropsColor.getColorForIndex(selected);
            PlayerSettings.savePlayerData(Client.instance);
        }
    },

    EXP_DROPS_GROUP() {
        @Override
        public void selectOption(int selected, Widget r) {
            Configuration.xpDropsGroup = selected == 1;
            PlayerSettings.savePlayerData(Client.instance);
        }
    };

    private Dropdown() {
    }

    public abstract void selectOption(int selected, Widget r);
}
