/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
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
package net.runelite.client.discord;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import club.minnced.discord.rpc.DiscordUser;
import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;

import com.grinder.client.util.Log;
import net.runelite.client.discord.events.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class DiscordService implements AutoCloseable
{
	private final EventBus eventBus;
	private final ScheduledExecutorService executorService;
	private final DiscordRPC discordRPC;

	// Hold a reference to the event handlers to prevent the garbage collector from deleting them
	private final DiscordEventHandlers discordEventHandlers;

	private DiscordUser currentUser;

	public DiscordService(
		final EventBus eventBus,
		final ScheduledExecutorService executorService)
	{

		this.eventBus = eventBus;
		this.executorService = executorService;

		DiscordRPC discordRPC = null;
		DiscordEventHandlers discordEventHandlers = null;

		try
		{
			discordRPC = DiscordRPC.INSTANCE;
			discordEventHandlers = new DiscordEventHandlers();
		}
		catch (Error e)
		{
			Log.error("Failed to load Discord library, Discord support will be disabled.", e);
		}

		this.discordRPC = discordRPC;
		this.discordEventHandlers = discordEventHandlers;
	}

	/**
	 * Initializes the Discord service, sets up the event handlers and starts worker thread that will poll discord
	 * events every 2 seconds.
	 * Before closing the application it is recommended to call {@link #close()}
	 */
	public void init()
	{
		if (discordEventHandlers == null)
		{
			return;
		}

		Log.info("Initializing Discord RPC service.");
		discordEventHandlers.ready = this::ready;
		discordEventHandlers.disconnected = this::disconnected;
		discordEventHandlers.errored = this::errored;
		discordEventHandlers.joinGame = this::joinGame;
		discordEventHandlers.spectateGame = this::spectateGame;
		discordEventHandlers.joinRequest = this::joinRequest;
		discordRPC.Discord_Initialize("573302003531382784", discordEventHandlers, true, null);
		executorService.scheduleAtFixedRate(discordRPC::Discord_RunCallbacks, 0, 2, TimeUnit.SECONDS);
	}

	/**
	 * Shuts the RPC connection down.
	 * If not currently connected, this does nothing.
	 */
	@Override
	public void close()
	{
		if (discordRPC != null)
		{
			discordRPC.Discord_Shutdown();
		}
	}

	/**
	 * Updates the currently set presence of the logged in user.
	 * <br>Note that the client only updates its presence every <b>15 seconds</b>
	 * and queues all additional presence updates.
	 *
	 * @param discordPresence The new presence to use
	 */
	public void updatePresence(DiscordPresence discordPresence)
	{
		if (discordRPC == null)
		{
			return;
		}

		final DiscordRichPresence discordRichPresence = new DiscordRichPresence();
		discordRichPresence.state = discordPresence.getState();
		discordRichPresence.details = discordPresence.getDetails();
		discordRichPresence.startTimestamp = discordPresence.getStartTimestamp() != null
			? discordPresence.getStartTimestamp().getEpochSecond()
			: 0;
		discordRichPresence.endTimestamp = discordPresence.getEndTimestamp() != null
			? discordPresence.getEndTimestamp().getEpochSecond()
			: 0;
		discordRichPresence.largeImageKey = Strings.isNullOrEmpty(discordPresence.getLargeImageKey())
			? "default"
			: discordPresence.getLargeImageKey();
		discordRichPresence.largeImageText = discordPresence.getLargeImageText();

		if (!Strings.isNullOrEmpty(discordPresence.getSmallImageKey()))
		{
			discordRichPresence.smallImageKey = discordPresence.getSmallImageKey();
		}

		discordRichPresence.smallImageText = discordPresence.getSmallImageText();
		discordRichPresence.partyId = discordPresence.getPartyId();
		discordRichPresence.partySize = discordPresence.getPartySize();
		discordRichPresence.partyMax = discordPresence.getPartyMax();
		discordRichPresence.matchSecret = discordPresence.getMatchSecret();
		discordRichPresence.joinSecret = discordPresence.getJoinSecret();
		discordRichPresence.spectateSecret = discordPresence.getSpectateSecret();
		discordRichPresence.instance = (byte) (discordPresence.isInstance() ? 1 : 0);

		Log.info("Sending presence update {"+discordPresence+"}");
		discordRPC.Discord_UpdatePresence(discordRichPresence);
	}

	/**
	 * Clears the currently set presence.
	 */
	public void clearPresence()
	{
		if (discordRPC != null)
		{
			discordRPC.Discord_ClearPresence();
		}
	}

	/**
	 * Responds to the given user with the specified reply type.
	 *
	 * @param userId The id of the user to respond to
	 * @param reply  The reply type
	 */
	public void respondToRequest(String userId, DiscordReplyType reply)
	{
		if (discordRPC != null)
		{
			discordRPC.Discord_Respond(userId, reply.ordinal());
		}
	}

	private void ready(DiscordUser user)
	{
		Log.info("Discord RPC service is ready with user {"+user.username+"}.");
		currentUser = user;
		eventBus.post(new DiscordReady(
			user.userId,
			user.username,
			user.discriminator,
			user.avatar));
	}

	private void disconnected(int errorCode, String message)
	{
		eventBus.post(new DiscordDisconnected(errorCode, message));
	}

	private void errored(int errorCode, String message)
	{
		Log.error("Discord error: {"+errorCode+"} - {"+message+"}");
		eventBus.post(new DiscordErrored(errorCode, message));
	}

	private void joinGame(String joinSecret)
	{
		eventBus.post(new DiscordJoinGame(joinSecret));
	}

	private void spectateGame(String spectateSecret)
	{
		eventBus.post(new DiscordSpectateGame(spectateSecret));
	}

	private void joinRequest(DiscordUser user)
	{
		eventBus.post(new DiscordJoinRequest(
			user.userId,
			user.username,
			user.discriminator,
			user.avatar));
	}
}
