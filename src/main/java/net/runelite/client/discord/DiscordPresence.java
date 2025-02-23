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

import java.time.Instant;

/**
 * Represents Discord Rich Presence RPC data
 */
public class DiscordPresence
{
	/**
	 * The user's current party status.
	 * Example: "Looking to Play", "Playing Solo", "In a Group"
	 *
	 * <b>Maximum: 128 characters</b>
	 */
	private String state;

	/**
	 * What the player is currently doing.
	 * Example: "Competitive - Captain's Mode", "In Queue", "Unranked PvP"
	 *
	 * <b>Maximum: 128 characters</b>
	 */
	private String details;

	/**
	 * Unix timestamp (seconds) for the start of the game.
	 */
	private Instant startTimestamp;

	/**
	 * Unix timestamp (seconds) for the end of the game.
	 */
	private Instant endTimestamp;

	/**
	 * Name of the uploaded image for the large profile artwork.
	 * Example: "default"
	 *
	 * <b>Maximum: 32 characters</b>
	 */
	private String largeImageKey;

	/**
	 * Tooltip for the largeImageKey.
	 * Example: "Blade's Edge Arena", "Numbani", "Danger Zone"
	 *
	 * <b>Maximum: 128 characters</b>
	 */
	private String largeImageText;

	/**
	 * Name of the uploaded image for the small profile artwork.
	 * Example: "rogue"
	 *
	 * <b>Maximum: 32 characters</b>
	 */
	private String smallImageKey;

	/**
	 * Tooltip for the smallImageKey.
	 * Example: "Rogue - Level 100"
	 *
	 * <b>Maximum: 128 characters</b>
	 */
	private String smallImageText;

	/**
	 * ID of the player's party, lobby, or group.
	 * Example: "ae488379-351d-4a4f-ad32-2b9b01c91657"
	 *
	 * <b>Maximum: 128 characters</b>
	 */
	private String partyId;

	/**
	 * Current size of the player's party, lobby, or group.
	 * Example: 1
	 */
	private int partySize;

	/**
	 * Maximum size of the player's party, lobby, or group.
	 * Example: 5
	 */
	private int partyMax;

	/**
	 * Unique hashed string for Spectate and Join.
	 * Required to enable match interactive buttons in the user's presence.
	 * Example: "MmhuZToxMjMxMjM6cWl3amR3MWlqZA=="
	 *
	 * <b>Maximum: 128 characters</b>
	 */
	private String matchSecret;

	/**
	 * Unique hashed string for Spectate button.
	 * This will enable the "Spectate" button on the user's presence if whitelisted.
	 * Example: "MTIzNDV8MTIzNDV8MTMyNDU0"
	 *
	 * <b>Maximum: 128 characters</b>
	 */
	private String joinSecret;

	/**
	 * Unique hashed string for chat invitations and Ask to Join.
	 * This will enable the "Ask to Join" button on the user's presence if whitelisted.
	 * Example: "MTI4NzM0OjFpMmhuZToxMjMxMjM="
	 *
	 * <b>Maximum: 128 characters</b>
	 */
	private String spectateSecret;

	/**
	 * Marks the matchSecret as a game session with a specific beginning and end.
	 */
	private boolean instance;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Instant getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(Instant startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public Instant getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(Instant endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public String getLargeImageKey() {
		return largeImageKey;
	}

	public void setLargeImageKey(String largeImageKey) {
		this.largeImageKey = largeImageKey;
	}

	public String getLargeImageText() {
		return largeImageText;
	}

	public void setLargeImageText(String largeImageText) {
		this.largeImageText = largeImageText;
	}

	public String getSmallImageKey() {
		return smallImageKey;
	}

	public void setSmallImageKey(String smallImageKey) {
		this.smallImageKey = smallImageKey;
	}

	public String getSmallImageText() {
		return smallImageText;
	}

	public void setSmallImageText(String smallImageText) {
		this.smallImageText = smallImageText;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public int getPartySize() {
		return partySize;
	}

	public void setPartySize(int partySize) {
		this.partySize = partySize;
	}

	public int getPartyMax() {
		return partyMax;
	}

	public void setPartyMax(int partyMax) {
		this.partyMax = partyMax;
	}

	public String getMatchSecret() {
		return matchSecret;
	}

	public void setMatchSecret(String matchSecret) {
		this.matchSecret = matchSecret;
	}

	public String getJoinSecret() {
		return joinSecret;
	}

	public void setJoinSecret(String joinSecret) {
		this.joinSecret = joinSecret;
	}

	public String getSpectateSecret() {
		return spectateSecret;
	}

	public void setSpectateSecret(String spectateSecret) {
		this.spectateSecret = spectateSecret;
	}

	public boolean isInstance() {
		return instance;
	}

	public void setInstance(boolean instance) {
		this.instance = instance;
	}
}
