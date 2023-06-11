package com.grinder.ui;

import com.grinder.Configuration;
import com.runescape.Client;
import com.runescape.sign.SignLink;
import com.runescape.util.MiscUtils;

import javax.swing.*;
import java.io.File;
import java.nio.file.Paths;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-17
 */
class UIMenuBar extends JMenuBar {

    private final JMenu usefulLinksMenu = new UIMenu("Links");
    private final JMenu accountInformationMenu = new UIMenu("Account");
    private final JMenu itemInformationMenu = new UIMenu("Item Guides");
    private final JMenu ingameGuidesInformationMenu = new UIMenu("In-Game Guides");
    private final JMenu autoTalker = new UIMenu("Auto-talker");
    private final JMenu miscellaneousMenu = new UIMenu("Miscellaneous");
    private final JMenu developerMenu = new UIMenu("Developer");

    UIMenuBar(final ClientUI clientUI) {
        add(Box.createHorizontalStrut(50));
        constructMenus(clientUI);
        add(Box.createHorizontalStrut(50));
        setBorder(null);

    }

    private void constructMenus(final ClientUI clientUI){
        buildMenuItems(clientUI, UIConstants.USEFUL_LINKS_MENU_ITEM_NAMES, usefulLinksMenu);
        buildMenuItems(clientUI, UIConstants.ACCOUNT_MENU_ITEM_NAMES, accountInformationMenu);
        buildMenuItems(clientUI, UIConstants.ITEM_MENU_ITEM_NAMES, itemInformationMenu);
        buildMenuItems(clientUI, UIConstants.INGAME_GUIDES_ITEM_NAMES, ingameGuidesInformationMenu);
        buildMenuItems(clientUI, UIConstants.AUTO_TALKER_ITEM, autoTalker);
        buildMenuItems(clientUI, UIConstants.MISCELLANEOUS_ITEM_NAMES, miscellaneousMenu);

        add(Box.createHorizontalGlue());
        add(usefulLinksMenu);
        add(Box.createHorizontalGlue());
        add(accountInformationMenu);
        add(Box.createHorizontalGlue());
        add(itemInformationMenu);
        add(Box.createHorizontalGlue());
        add(ingameGuidesInformationMenu);
        add(Box.createHorizontalGlue());
        add(autoTalker);
        add(Box.createHorizontalGlue());
        add(miscellaneousMenu);
        add(Box.createHorizontalGlue());

        if(Configuration.SHOW_DEVELOPER_MENU){
            buildMenuItems(clientUI, UIConstants.DEVELOPER_ITEM_NAMES, developerMenu);
            add(developerMenu);
            add(Box.createHorizontalGlue());
        }
    }

    private void buildMenuItems(final ClientUI clientUI, final String[] itemNames, final JMenu menu) {
        for (final String itemName : itemNames) {
            final JMenuItem item = new UIMenuItem(itemName);
            item.addActionListener(clientUI);
            menu.add(item);
        }
    }

    public static void executeCommand(String cmd) {
        switch (cmd) {
            case UIConstants.SWITCH_LOCAL_WORLD:
                Configuration.connected_world = Configuration.LOCAL_WORLD;
                break;
            case UIConstants.SWITCH_WORLD_1:
                Configuration.connected_world = Configuration.WORLD_1;
                break;
            case UIConstants.SWITCH_WORLD_2:
                Configuration.connected_world = Configuration.WORLD_2;
                break;
        }
        switch (cmd.toLowerCase()) {

            case "exit":
                System.exit(0);
                break;

            case "homepage":
                MiscUtils.launchURL("https://www.grinderscape.org/");
                break;

            case "discord":
                MiscUtils.launchURL("https://discord.gg/b46xx5u/");
                break;

            case "wiki":
                MiscUtils.launchURL("http://wiki.grinderscape.org");
                break;

            case "vote":
                MiscUtils.launchURL("https://www.grinderscape.org/vote");
                break;

            case "store":
                MiscUtils.launchURL("https://www.grinderscape.org/store");
                break;

            case "highscores":
                MiscUtils.launchURL("https://www.grinderscape.org/highscores");
                break;

            //String[] ACCOUNT_MENU_ITEM_NAMES = new String[] {"Rules", "Events", "Unlock Account", "Submit Appeal", "Rank Requests", "Official Middlemen", "Report Bugs", "Report Player", "Report Staff",  "Recover Lost Account" };

            case "help":
                MiscUtils.launchURL("https://discord.com/channels/358664434324865024/992202052434403378");
                break;

            case "rules":
                MiscUtils.launchURL("https://discord.com/channels/358664434324865024/1013175046040191026");
                break;
            case "prices":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/Prices");
                break;
            case "item prices":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/Prices");
                break;
            case "general guides":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/General_guides");
                break;
            case "clue scrolls":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/General_guides/Clue_Scrolls");
                break;
            case "events":
                MiscUtils.launchURL("https://discord.com/channels/358664434324865024/753670585074188318");
                break;
            case "unlock account":
                MiscUtils.launchURL("https://discord.com/channels/358664434324865024/992202052434403378");
                break;
            case "submit appeal":
                MiscUtils.launchURL("https://discord.com/channels/358664434324865024/998991618956853268");
                break;
            case "rank requests":
                MiscUtils.launchURL("https://discord.com/channels/358664434324865024/723234942141726753");
                break;
            case "report bugs":
                MiscUtils.launchURL("https://discord.com/channels/358664434324865024/979502536660779050");
                break;
            case "report player":
                MiscUtils.launchURL("https://discord.com/channels/358664434324865024/992202052434403378");
                break;
            case "recover lost account":
                MiscUtils.launchURL("https://discord.com/channels/358664434324865024/992202052434403378");
                break;
            case "mystery boxes rewards":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/General_guides/Mystery_boxes");
                break;
            case "brimstone key":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/General_guides/Useable_Keys/Brimstone_Key");
                break;
            case "muddy key":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/General_guides/Useable_Keys/Muddy_Key");
                break;
            case "crystal key":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/General_guides/Useable_Keys/Crystal_Key");
            case "drops list":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/Bestiary");
                break;
            case "item effects":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/In-Game_Section/General_guides/Combat_Equipment_Effects");
                break;
            case "member benefits":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/General_guides/Donator_benefits");
                break;
            case "commands":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/In-Game_Section/General_guides/Commands");
                break;
            case "lottery":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/General_guides/Coins_lottery");
                break;
            case "game modes":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/Skill_guides");
                break;
            case "ingame staff":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/Server_team_hub/_Server_Teams/Staff_Team");
                break;
            case "achivements":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/General_guides/Achievements");
                break;
            case "changing password":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/Forums_Section/Changing_passwords");
                break;
            case "beginners guide":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/General_guides/Beginners_Guide");
                break;
            case "skilling guide index":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/Skill_guides");
                break;
            case "bossing index":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/Bestiary/Bossing_index");
                break;
            case "bestiary":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/Bestiary/Monster_list");
                break;
            case "quests":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/Quests");
                break;
            case "whips special effects":
                MiscUtils.launchURL("https://wiki.grinderscape.org/Main_page/Prices/Whips");
                break;
            case "auto-typer":
                Client.instance.sendMessage("You can use ::autotype [message] to start the auto-typer every 5 seconds.");
                Client.instance.sendMessage("If you move or do any action it will interrupt the auto-typer.");
                break;
            case "update cache":
                final File cacheDir = Paths.get(SignLink.findcachedir()).toFile();
                if(cacheDir.exists()){
                    final File[] files = cacheDir.listFiles();
                    for(File file : files){
                        if(file.canRead()){

                        }
                    }
                }
                break;
            default:
                if (Client.loggedIn) {
                    Client.instance.sendMessage("Coming soon.");
                }
                break;
        }
    }
}