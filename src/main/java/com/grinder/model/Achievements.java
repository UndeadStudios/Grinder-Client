package com.grinder.model;

import com.grinder.Configuration;
import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.cache.graphics.widget.Widget;
import com.grinder.client.ClientCompanion;
import com.runescape.input.MouseHandler;

/**
 * @version 1.0
 * @since 12/12/2019
 */
public class Achievements {

    public static int achievementProgressionTime = 0;
    public static int achievementProgressionY = 0;
    public static int achievementCompletionTime = 0;
    public static int achievementCompletionY = ClientCompanion.ACHIEVEMENT_COMPLETION_HEIGHT;

    public static void drawAchievements(Client client) {
        int y = ClientUI.frameMode == Client.ScreenMode.FIXED ? 0 : ClientUI.frameHeight - 503;

        if (Configuration.achievementProgressOverlay && achievementProgressionTime > 0) {
            client.drawInterface(achievementProgressionY, 0, Widget.interfaceCache[81000],
                    !client.displayChatComponents()
                            ? y + 140
                            : y - client.resizeChatOffset(),  0, 0);

            achievementProgressionTime--;

            if (achievementProgressionY < 0) {
                achievementProgressionY++;
            }
        }

        if (achievementCompletionTime > 0) {
            client.drawInterface(achievementCompletionY, 0, Widget.interfaceCache[81020], 0, 0, 0);

            int speed = ClientCompanion.ACHIEVEMENT_COMPLETION_SPEED;
            int timeToMove = ClientCompanion.ACHIEVEMENT_COMPLETION_HEIGHT / speed;
            boolean hovered = client.menuOptionsCount > 2 && client.menuArguments2[client.menuOptionsCount - 1] == 81025;

            if (achievementCompletionTime > ClientCompanion.ACHIEVEMENT_COMPLETION_DURATION - timeToMove) {
                achievementCompletionY -= speed;
                achievementCompletionTime--;
            } else if (achievementCompletionTime <= timeToMove) {
                achievementCompletionY += speed;
                achievementCompletionTime--;
            } else {
                if (hovered) {
                    achievementCompletionTime = ClientCompanion.ACHIEVEMENT_COMPLETION_DURATION / 2;
                } else {
                    achievementCompletionTime--;
                }
            }
        }
    }

    public static void buildAchievementsMenu(Client client) {
        if (achievementCompletionTime > 0 && ClientCompanion.openInterfaceId == -1) {
            MenuBuilder.buildInterfaceMenu(client, Widget.interfaceCache[81020], 0,
                    0, MouseHandler.x,
                    MouseHandler.y,
                    achievementCompletionY);
        }
    }

    public static boolean handleAchivementTextUpdate(int childId, String text) {
        switch(childId) {
            case 81024:
                Widget.interfaceCache[81025].tooltip = text;
                achievementCompletionY = ClientCompanion.ACHIEVEMENT_COMPLETION_HEIGHT;
                achievementCompletionTime = ClientCompanion.ACHIEVEMENT_COMPLETION_DURATION;
                return true;
            case 81003:
            case 81004:
                if (achievementProgressionTime == 0) {
                    achievementProgressionY = -50;
                }

                achievementProgressionTime = 300;
                return true;
        }
        return false;
    }
}
