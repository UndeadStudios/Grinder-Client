package com.grinder.model;

import com.grinder.Configuration;
import com.runescape.Client;
import com.runescape.draw.Rasterizer2D;
import com.runescape.entity.Mob;
import com.runescape.entity.Npc;
import com.runescape.entity.Player;
import com.runescape.util.SecondsTimer;

public final class CombatBox {

    public static final SecondsTimer combatBoxTimer = new SecondsTimer();

    public static void drawCombatBox(Client client, Mob currentInteract) {
        int currentHp = currentInteract.currentHealth;
        int maxHp = currentInteract.maxHealth;

        String name = null;
        if (currentInteract instanceof Player) {
            name = ((Player) currentInteract).name;
        } else if (currentInteract instanceof Npc) {
            if (((Npc) currentInteract).desc != null) {
                name = ((Npc) currentInteract).desc.name;
            }
        }

        if (name == null) {
            combatBoxTimer.stop();
            return;
        }

        int height = 38;
        int width = 129;
        int xPos = 2;
        int yPos = 20;
        int barWidth = width - 4;

        Rasterizer2D.drawStylishBox(xPos, yPos, width, height);

        client.newRegularFont.drawCenteredString(name, xPos + (width / 2), yPos + 16, 16777215, 0);

        int percent = (int) (((double) currentHp / (double) maxHp) * barWidth);
        if (percent > barWidth) {
            percent = barWidth;
        }
        Rasterizer2D.drawBox(xPos + 2, yPos + 20, barWidth, 16, 0x651716);
        Rasterizer2D.drawBox(xPos + 2, yPos + 20, percent, 16, 0x098d39);
        client.newRegularFont.drawCenteredString(currentHp + "/" + maxHp, xPos + (width / 2), yPos + 34, 16777215, 0);
    }

    public static boolean shouldDrawCombatBox(Mob currentInteract) {
        if (!Configuration.combatOverlayBox) {
            return false;
        }
        return currentInteract != null && currentInteract.maxHealth != 0 && !combatBoxTimer.finished();
    }

    public static void handleCombatBoxTimers(Client client, Object obj) {
        if (obj instanceof Npc) {
            Npc npc = ((Npc) obj);
            if (Client.localPlayer.targetIndex == -1) {
                if ((npc.targetIndex - 32768) == client.localPlayerIndex) {
                    client.currentInteract = npc;
                    combatBoxTimer.start(6);
                }

            } else {
                if (npc.index == Client.localPlayer.targetIndex) {
                    client.currentInteract = npc;
                    combatBoxTimer.start(6);
                }
            }
        } else if (obj instanceof Player) {
            Player player = ((Player) obj);
            if (Client.localPlayer.targetIndex == -1) {
                if ((player.targetIndex - 32768) == client.localPlayerIndex) {
                    client.currentInteract = player;
                    combatBoxTimer.start(6);
                }
            } else {
                if (player.index == Client.localPlayer.targetIndex - 32768) {
                    client.currentInteract = player;
                    combatBoxTimer.start(6);
                }
            }
        }
    }
}
