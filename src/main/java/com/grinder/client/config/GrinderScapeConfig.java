package com.grinder.client.config;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.FlashNotification;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 14/12/2019
 */
@ConfigGroup("grinder-scape")
public interface GrinderScapeConfig extends Config {

    @ConfigItem(
            keyName = "notificationTray",
            name = "Enable tray notifications",
            description = "Enables tray notifications",
            position = 20
    )
    default boolean enableTrayNotifications()
    {
        return true;
    }

    @ConfigItem(
            keyName = "notificationRequestFocus",
            name = "Request focus on notification",
            description = "Toggles window focus request",
            position = 21
    )
    default boolean requestFocusOnNotification()
    {
        return true;
    }

    @ConfigItem(
            keyName = "notificationSound",
            name = "Enable sound on notifications",
            description = "Enables the playing of a beep sound when notifications are displayed",
            position = 22
    )
    default boolean enableNotificationSound()
    {
        return true;
    }

    @ConfigItem(
            keyName = "notificationGameMessage",
            name = "Enable game message notifications",
            description = "Puts a notification message in the chatbox",
            position = 23
    )
    default boolean enableGameMessageNotification()
    {
        return false;
    }

    @ConfigItem(
            keyName = "notificationFlash",
            name = "Enable flash notification",
            description = "Flashes the game frame as a notification",
            position = 24
    )
    default FlashNotification flashNotification()
    {
        return FlashNotification.DISABLED;
    }

    @ConfigItem(
            keyName = "notificationFocused",
            name = "Send notifications when focused",
            description = "Toggles all notifications for when the client is focused",
            position = 25
    )
    default boolean sendNotificationsWhenFocused()
    {
        return false;
    }


}
