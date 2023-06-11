package com.grinder.model;

import com.grinder.Configuration;
import com.grinder.client.ClientCompanion;
import com.grinder.client.ClientUtil;
import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.cache.FileArchive;
import com.runescape.cache.anim.Animation;
import com.runescape.cache.def.ItemDefinition;
import com.runescape.cache.def.NpcDefinition;
import com.runescape.cache.def.ObjectDefinition;
import com.runescape.cache.graphics.GameFont;
import com.runescape.cache.graphics.PetSystem;
import com.runescape.cache.graphics.RSFont;
import com.runescape.cache.graphics.widget.Widget;
import com.runescape.draw.Rasterizer2D;
import com.runescape.entity.Player;
import com.runescape.io.jaggrab.JagGrab;
import com.runescape.io.packets.outgoing.impl.Command;
import com.runescape.io.packets.outgoing.impl.ExamineOrEditNpc;
import com.runescape.util.InterfaceDebugger;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import static com.runescape.Client.tick;

public class Console {

    public static boolean consoleOpen;
    public final String[] consoleMessages;
    public String consoleInput;
    public String previousMessage;

    public Console() {
        consoleInput = "";
        consoleOpen = false;
        consoleMessages = new String[50];
        consoleMessages[0] = "This is the developer console. To close, press the ` key on your keyboard.";
    }

    public static Client client() {
        return Client.instance;
    }

    public void drawConsole() {
        if (consoleOpen) {
            Rasterizer2D.drawTransparentBox(0, 0, client().getGameComponent().getWidth(), 334, 5320850, 97);
            Rasterizer2D.drawPixels(1, 315, 0, 16777215, client().getGameComponent().getWidth());
            client().newBoldFont.drawBasicString("-->", 11, 328, 16777215, 0);
            if (tick % 20 < 10) {
                client().newBoldFont.drawBasicString(consoleInput + "|", 38, 328, 16777215, 0);
            } else {
                client().newBoldFont.drawBasicString(consoleInput, 38, 328, 16777215, 0);
            }
        }
    }

    public void drawConsoleArea() {
        if (consoleOpen) {
            for (int i = 0, j = 308; i < 17; i++, j -= 18) {
                if (consoleMessages[i] != null) {
                    client().newRegularFont.drawBasicString(consoleMessages[i], 9, j, 0xFFFFFF, 0);
                }
            }
        }
    }


    public void printMessage(String s, int i) {
        if (client().backDialogueId == -1) {
            ChatBox.setUpdateChatbox(true);
        }
        System.arraycopy(consoleMessages, 0, consoleMessages, 1, 16);
        if (i == 0) {
            consoleMessages[0] = date() + ": --> " + s;
            previousMessage = s;
        } else {
            consoleMessages[0] = date() + ": " + s;
        }
    }

    public String date() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Server time
        return sdf.format(date);
    }

    public void sendCommandPacket(String cmd) {
        if (cmd.startsWith("editnpc")) {
            try {
                String[] args = consoleInput.split(" ");
                int index = Integer.parseInt(args[1]);
                Client.instance.sendPacket(new ExamineOrEditNpc(index).create());
            }catch (Exception ignored) {}
        }
		if (cmd.startsWith("freeint")) {
			try {
				String[] args = consoleInput.split(" ");
				int id = Integer.parseInt(args[1]);
				int amount = Integer.parseInt(args[2]);
				int count = 0;

				for (int i = id; i < id + amount; i++) {
					Widget w1 = Widget.interfaceCache[i];

					if (w1 == null) {
						count++;
					}
				}

				ClientCompanion.console.printMessage("There are " + (count + "/" + amount) + " free interfaces on " + id + "-" + (id + amount) + ".", 1);
			} catch (Exception e) {
				ClientCompanion.console.printMessage("Error whilst executing command.", 1);
			}
    	} else if (cmd.startsWith("findnpc")) {
            String name = cmd.substring(("findnpc").length() + 1);
            for (int i = 0; i < NpcDefinition.getTotalNpcs(); i++) {
                NpcDefinition def = NpcDefinition.lookup(i);
                if (def == null || def.name == null ||
                        !def.name.toLowerCase().contains(name)) {
                    continue;
                }
                printMessage("NPC " + i + ", name: " + def.name + ", stand anim: " + def.standAnim, 1);
            }
            return;
        } else if (cmd.startsWith("finditem")) {
        	String name = cmd.substring(("finditem").length() + 1);
            for (int i = 0; i < ItemDefinition.getTotalItems(); i++) {
                ItemDefinition def = ItemDefinition.lookup(i);
                if (def == null || def.name == null ||
                        !def.name.toLowerCase().contains(name)) {
                    continue;
                }
                printMessage("Item " + i + ", name: " + def.name + ", modelId: " + def.inventory_model, 1);
            }
            return;
        } else if (cmd.startsWith("findobject")) {
        	String name = cmd.substring(("findobject").length() + 1);
            for (int i = 0; i < ObjectDefinition.TOTAL_OBJECTS; i++) {
                ObjectDefinition def = ObjectDefinition.lookup(i);
                if (def == null || def.name == null ||
                        !def.name.toLowerCase().contains(name)) {
                    continue;
                }
                printMessage("Object " + i + ", name: " + def.name, 1);
            }
            return;
        } else if (cmd.equalsIgnoreCase("cls") || cmd.equalsIgnoreCase("clear")) {
            for (int j = 0; j < 17; j++) {
                consoleMessages[j] = null;
            }
            return;
        } else if (cmd.startsWith("modelids")) {
		    try {
                String id = cmd.substring(("modelids").length() + 1);
                Client.instance.sendMessage(ItemDefinition.itemModels(Integer.parseInt(id)));
            } catch (Exception e) {
		        e.printStackTrace();
            }
            return;
        } else if (cmd.startsWith("anim")) {
            try {
                Player player = client().localPlayer;
                int animation = Integer.parseInt(cmd.substring(("anim").length() + 1));
                if (animation == 65535)
                    animation = -1;
                int delay = 0;

                if (animation == player.getSequence() && animation != -1) {
                    int replayMode = Animation.getSequenceDefinition(animation).replayMode;
                    if (replayMode == 1) {
                        player.sequenceFrame = 0;
                        player.sequenceFrameCycle = 0;
                        player.sequenceDelay = delay;
                        player.currentAnimationLoopCount = 0;
                    }
                    if (replayMode == 2)
                        player.currentAnimationLoopCount = 0;
                } else if (animation == -1 || player.getSequence() == -1
                        || Animation.getSequenceDefinition(animation).forcedPriority >= Animation.getSequenceDefinition(player.getSequence()).forcedPriority) {
                    player.setSequence(animation);
                    player.sequenceFrame = 0;
                    player.sequenceFrameCycle = 0;
                    player.sequenceDelay = delay;
                    player.currentAnimationLoopCount = 0;
                    player.__ch = player.pathLength;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (cmd.startsWith("colors")) {
            try {
                //Player player = client().localPlayer;
                int itemId = Integer.parseInt(cmd.substring(("colors").length() + 1));
                ItemDefinition def = ItemDefinition.lookup(itemId);
                Set<Integer> colors = def.getModel(1).getColors();
                System.out.println(itemId + " - " + def.name + " inv colors: " + Arrays.toString(colors.toArray()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (cmd.startsWith("pet")) {
            try {
                int npcId = Integer.parseInt(cmd.substring(("pet").length() + 1));
                PetSystem.petSelected = npcId;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        switch (cmd.toLowerCase()) {
            case "crash":
                int[] boo = new int[1];
                int d = boo[-1];
                break;
            case "help":
                ClientCompanion.console.printMessage("Type 'commands' to see a list of available commands.", 1);
                break;
            case "commands":
                showCommands();
                break;
            case "console":
                ClientUI.launchConsole();
                break;
            case "dev":
                ClientUI.launchDevTool();
                break;
            case "fps":
                Configuration.displayFps = !Configuration.displayFps;
                ClientCompanion.console.printMessage("FPS " + (Configuration.displayFps ? "on" : "off"), 1);
                break;
            case "data":
                Configuration.clientData = !Configuration.clientData;
                ClientCompanion.console.printMessage("Data " + (Configuration.clientData ? "on" : "off"), 1);
                break;
            case "dumpnpcheights":
                NpcDefinition.dumpNpcHeights();
                break;
            case "gc":
                System.gc();
                break;
            case "childids":
                for (int i = 0; i < 84000; i++) {
                    client().sendString("" + i, i);
                }
                break;
            case "finterface":
                try {
                    String[] args = client().inputString.split(" ");
                    int id1 = Integer.parseInt(args[1]);
                    int id2 = Integer.parseInt(args[2]);
                    client().fullscreenInterfaceID = id1;
                    ClientCompanion.openInterfaceId = id2;
                    ClientCompanion.console.printMessage("Opened interface " + id1 + " " + id2 + ".", 1);
                } catch (Exception e) {
                    ClientCompanion.console.printMessage("Failed to open interface.", 1);
                }
                break;
            case "music":
                client().toggleMusicMute();
                break;
            case "rint":
                GameFont fonts[] = {client().smallText, client().regularText, client().boldText, client().fancyFont, client().fancyFontLarge};
                FileArchive interfaces = ClientUtil.createArchive(client(), 3, "interface", "interface", JagGrab.CRCs[JagGrab.INTERFACE_CRC], 35);
                FileArchive graphics = ClientUtil.createArchive(client(), 4, "2d graphics", "media", JagGrab.CRCs[JagGrab.MEDIA_CRC], 40);
                Widget.load(interfaces, fonts, graphics, new RSFont[]{client().newSmallFont, client().newRegularFont, client().newBoldFont, client().newFancyFont, client().newFancyFontLarge});
                break;
            case "rsi":
            	new InterfaceDebugger().setVisible(true);
            	break;
            case "grid":
                ClientCompanion.enableGridOverlay = !ClientCompanion.enableGridOverlay;
                break;
            case "emojis":
                Configuration.enableEmoticons = !Configuration.enableEmoticons;
                break;
            case "messages":
                for (int i = 0; i < 250; i++) {
                    //Client.instance.sendMessage("@red@" + i +  "aaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaa aaaaaaaaa aaaaa aaaaaaaaa aaaaaa aaaaaa aaaaaa aaaaa", 1, i % 2 == 0 ? "Name" : "Test");
                    Client.instance.sendMessage(":) ;) :( ;( :D :o D: :p ;p :/ ^^ xD :shame: :angry: :cool: :puke: :fp: xd xd xd :frustrated: ;-(", 1, i % 2 == 0 ? "Name" : "Test");
                }
                break;
            case "fixed":
                ClientUI.frameMode(Client.ScreenMode.FIXED, true);
                break;
            case "resize":
                ClientUI.frameMode(Client.ScreenMode.RESIZABLE, true);
                break;
            case "full":
                ClientUI.frameMode(Client.ScreenMode.FULLSCREEN, true);
                break;
        }

        /** Server commands **/
        if (client().loggedIn) {
            client().sendPacket(new Command(cmd).create());
        }
    }

    public final void showCommands() {
        ClientCompanion.console.printMessage("commands - This command", 1);
        ClientCompanion.console.printMessage("cls - Clear console", 1);
        ClientCompanion.console.printMessage("displayfps - Toggle FPS", 1);

        // Mod commands
        if (client().getClientRights() >= 1 && client().getClientRights() <= 4) {
            String[] cmds = new String[]{"mute $plr - Mute player",
                    "unmute $plr - Unmute player",
                    "kick $plr - Kick player"};
            for (String cmd : cmds) {
                ClientCompanion.console.printMessage(cmd, 1);
            }
        }
    }
}
