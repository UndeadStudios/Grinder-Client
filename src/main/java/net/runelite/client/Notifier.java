/*
 * Copyright (c) 2016-2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client;

import com.google.common.base.Strings;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.grinder.ui.ClientUI;
import com.grinder.GrinderScape;
import com.grinder.GrinderScapeProperties;
import com.grinder.client.config.GrinderScapeConfig;
import com.grinder.client.util.Log;
import net.runelite.api.Constants;
import net.runelite.api.GameState;
import net.runelite.client.config.FlashNotification;
import net.runelite.client.util.OSType;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Notifier
{
	// Default timeout of notification in milliseconds
	private static final int DEFAULT_TIMEOUT = 10000;
	private static final String DOUBLE_QUOTE = "\"";
	private static final Escaper SHELL_ESCAPE = Escapers.builder()
		.addEscape('"', "'")
		.build();

	// Notifier properties
	private static final Color FLASH_COLOR = new Color(255, 0, 0, 70);
	private static final int MINIMUM_FLASH_DURATION_MILLIS = 2000;
	private static final int MINIMUM_FLASH_DURATION_TICKS = MINIMUM_FLASH_DURATION_MILLIS / Constants.CLIENT_TICK_LENGTH;

	private final String appName;
	private final ClientUI clientUI;
	private final GrinderScapeConfig grinderScapeConfig;
	private final ScheduledExecutorService executorService;
	private final Path notifyIconPath;
	private final boolean terminalNotifierAvailable;

	private Instant flashStart;
	private long mouseLastPressedMillis;

	public Notifier(
			final ClientUI clientUI,
			final GrinderScapeConfig grinderScapeConfig,
			final ScheduledExecutorService executorService)
	{
		this.appName = "GrinderScape";
		this.clientUI = clientUI;
		this.grinderScapeConfig = grinderScapeConfig;
		this.executorService = executorService;
		this.notifyIconPath = GrinderScape.GRINDER_SCAPE_DIR.toPath().resolve("icon.png");
		this.terminalNotifierAvailable =
				!Strings.isNullOrEmpty(GrinderScapeProperties.getLauncherVersion())
						&& isTerminalNotifierAvailable();
	}

	public void notify(String message)
	{
		notify(message, TrayIcon.MessageType.NONE);
	}

	public void notify(String message, TrayIcon.MessageType type)
	{
		if (!grinderScapeConfig.sendNotificationsWhenFocused() && clientUI.isFocused())
		{
			return;
		}

		if (grinderScapeConfig.requestFocusOnNotification())
		{
			clientUI.requestFocus();
		}

		if (grinderScapeConfig.enableTrayNotifications())
		{
			sendNotification(appName, message, type);
		}

		if (grinderScapeConfig.enableNotificationSound())
		{
			Toolkit.getDefaultToolkit().beep();
		}

		if (grinderScapeConfig.flashNotification() != FlashNotification.DISABLED)
		{
			flashStart = Instant.now();
			mouseLastPressedMillis = clientUI.getMouseLastPressedMillis();
		}
	}

	public void processFlash(final Graphics2D graphics)
	{
		if (flashStart == null || clientUI.getGameState() != GameState.LOGGED_IN)
		{
			flashStart = null;
			return;
		}

		FlashNotification flashNotification = grinderScapeConfig.flashNotification();

		if (clientUI.getGameCycle() % 40 >= 20
			// For solid colour, fall through every time.
			&& (flashNotification == FlashNotification.FLASH_TWO_SECONDS
			|| flashNotification == FlashNotification.FLASH_UNTIL_CANCELLED))
		{
			return;
		}

		final Color color = graphics.getColor();
		graphics.setColor(FLASH_COLOR);
		graphics.fill(new Rectangle(clientUI.getCanvas().getSize()));
		graphics.setColor(color);

		if (!Instant.now().minusMillis(MINIMUM_FLASH_DURATION_MILLIS).isAfter(flashStart))
		{
			return;
		}

		switch (flashNotification)
		{
			case FLASH_TWO_SECONDS:
			case SOLID_TWO_SECONDS:
				flashStart = null;
				break;
			case SOLID_UNTIL_CANCELLED:
			case FLASH_UNTIL_CANCELLED:
				// Any interaction with the client since the notification started will cancel it after the minimum duration
				if (clientUI.getMouseIdleTicks() < MINIMUM_FLASH_DURATION_TICKS
					|| clientUI.getKeyboardIdleTicks() < MINIMUM_FLASH_DURATION_TICKS
					|| clientUI.getMouseLastPressedMillis() > mouseLastPressedMillis)
				{
					flashStart = null;
				}
				break;
		}
	}

	private void sendNotification(
		final String title,
		final String message,
		final TrayIcon.MessageType type)
	{
		final String escapedTitle = SHELL_ESCAPE.escape(title);
		final String escapedMessage = SHELL_ESCAPE.escape(message);

		switch (OSType.getOSType())
		{
			case Linux:
				sendLinuxNotification(escapedTitle, escapedMessage, type);
				break;
			case MacOS:
				sendMacNotification(escapedTitle, escapedMessage);
				break;
			default:
				sendTrayNotification(title, message, type);
		}
	}

	private void sendTrayNotification(
		final String title,
		final String message,
		final TrayIcon.MessageType type)
	{
		if (clientUI.getTrayIcon() != null)
		{
			clientUI.getTrayIcon().displayMessage(title, message, type);
		}
	}

	private void sendLinuxNotification(
		final String title,
		final String message,
		final TrayIcon.MessageType type)
	{
		final List<String> commands = new ArrayList<>();
		commands.add("notify-send");
		commands.add(title);
		commands.add(message);
		commands.add("-i");
		commands.add(SHELL_ESCAPE.escape(notifyIconPath.toAbsolutePath().toString()));
		commands.add("-u");
		commands.add(toUrgency(type));
		commands.add("-t");
		commands.add(String.valueOf(DEFAULT_TIMEOUT));

		executorService.submit(() ->
		{
			try
			{
				Process notificationProcess = sendCommand(commands);

				boolean exited = notificationProcess.waitFor(500, TimeUnit.MILLISECONDS);
				if (exited && notificationProcess.exitValue() == 0)
				{
					return;
				}
			}
			catch (IOException | InterruptedException ex)
			{
				ex.printStackTrace();
				System.err.println("error sending notification");
			}

			// fall back to tray notification
			sendTrayNotification(title, message, type);
		});
	}

	private void sendMacNotification(final String title, final String message)
	{
		final List<String> commands = new ArrayList<>();

		if (terminalNotifierAvailable)
		{
			commands.add("terminal-notifier");
			commands.add("-group");
			commands.add("net.grinder.launcher");
			commands.add("-sender");
			commands.add("net.grinder.launcher");
			commands.add("-message");
			commands.add(DOUBLE_QUOTE + message + DOUBLE_QUOTE);
			commands.add("-title");
			commands.add(DOUBLE_QUOTE + title + DOUBLE_QUOTE);
		}
		else
		{
			commands.add("osascript");
			commands.add("-e");

			final String script = "display notification " + DOUBLE_QUOTE +
				message +
				DOUBLE_QUOTE +
				" with title " +
				DOUBLE_QUOTE +
				title +
				DOUBLE_QUOTE;

			commands.add(script);
		}

		try
		{
			sendCommand(commands);
		}
		catch (IOException ex)
		{
			Log.error("Error sending notification ", ex);
		}
	}

	private static Process sendCommand(final List<String> commands) throws IOException
	{
		return new ProcessBuilder(commands.toArray(new String[0]))
			.redirectErrorStream(true)
			.start();
	}

	private boolean isTerminalNotifierAvailable()
	{
		if (OSType.getOSType() == OSType.MacOS)
		{
			try
			{
				final Process exec = Runtime.getRuntime().exec(new String[]{"terminal-notifier", "-help"});
				exec.waitFor();
				return exec.exitValue() == 0;
			}
			catch (IOException | InterruptedException e)
			{
				return false;
			}
		}

		return false;
	}

	private static String toUrgency(TrayIcon.MessageType type)
	{
		switch (type)
		{
			case WARNING:
			case ERROR:
				return "critical";
			default:
				return "normal";
		}
	}
}
