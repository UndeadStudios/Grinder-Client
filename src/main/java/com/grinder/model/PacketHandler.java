package com.grinder.model;

import com.grinder.Configuration;
import com.grinder.client.ClientCompanion;
import com.grinder.client.GameShell;
import com.grinder.client.util.Log;
import com.grinder.ui.ClientUI;
import com.grinder.ui.SnapshotCache;
import com.runescape.Client;
import com.runescape.audio.Audio;
import com.runescape.cache.OsCache;
import com.runescape.cache.config.VariableBits;
import com.runescape.cache.def.ItemDefinition;
import com.runescape.cache.def.ObjectDefinition;
import com.runescape.cache.definition.OSObjectDefinition;
import com.runescape.cache.definition.VarbitDefinition;
import com.runescape.cache.graphics.PetSystem;
import com.runescape.cache.graphics.sprite.SpriteLoader;
import com.runescape.cache.graphics.widget.*;
import com.runescape.collection.NodeDeque;
import com.runescape.entity.*;
import com.runescape.entity.model.IdentityKit;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;
import com.runescape.net.IsaacCipher;
import com.runescape.net.NetSocket;
import com.runescape.scene.AnimableObject;
import com.runescape.scene.DynamicObject;
import com.runescape.scene.Projectile;
import com.runescape.scene.object.FloorDecoration;
import com.runescape.scene.object.SpawnedObject;
import com.runescape.scene.object.WallDecoration;
import com.runescape.scene.object.BoundaryObject;
import com.runescape.util.MiscUtils;
import com.runescape.util.PacketMetaData;
import com.runescape.util.StringUtils;
import com.runescape.util.TextInput;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Objects;

public class PacketHandler {

    public static boolean parseFrame(Client client, IsaacCipher decryption, NetSocket socketStream) {

        if (socketStream == null)
            return false;

        try {

            int available = socketStream.available();

            if (available == 0)
                return false;

            if (client.opcode == -1) {

                socketStream.read(client.incoming.array, 0, 1);
                client.opcode = client.incoming.array[0] & 0xff;
                if (decryption != null)
                    client.opcode = client.opcode - decryption.getNextKey() & 0xff;

                client.packetSize = PacketMetaData.PACKET_LENGTHS[client.opcode];
                // System.out.println("received "+opcode+" "+packetSize);
                available--;
            }

            // small variable sized packet
            if (client.packetSize == -1) {
                if (available > 0) {
                    socketStream.read(client.incoming.array, 0, 1);
                    client.packetSize = client.incoming.array[0] & 0xff;
                    available--;
                } else
                    return false;
            }

            // large variable sized packet
            if (client.packetSize == -2) {
                if (available > 1) {
                    socketStream.read(client.incoming.array, 0, 2);
                    client.incoming.index = 0;
                    client.packetSize = client.incoming.readUShort();
                    available -= 2;
                } else
                    return false;
            }

            if (available < client.packetSize) {
//                Logger.logWarning(opcode + ": Less bytes available than expected [" + available + " < " + packetSize + "]");
                return false;
            }
            //if (opcode == 1)
            //System.out.println("received " + opcode);

            client.incoming.index = 0;
            socketStream.read(client.incoming.array, 0, client.packetSize);

            client.thirdLastOpcode = client.secondLastOpcode;
            client.secondLastOpcode = client.lastOpcode;
            client.lastOpcode = client.opcode;

            if (client.opcode == PacketMetaData.PACKET_203__SEND_APPEARANCE_CONFIG) {
                int contentType = client.incoming.readUShort();

                int value = client.incoming.readUShort();

                if (contentType >= 300 && contentType <= 313) {
                    int k = (contentType - 300) / 2;

                    if (value != -1) {
                        client.anIntArray1065[k] = value;
                        client.aBoolean1031 = true;
                    }
                }

                if (contentType >= 314 && contentType <= 323) {
                    int l = (contentType - 314) / 2;
                    client.characterDesignColours[l] = value;
                    client.aBoolean1031 = true;
                }
                if (contentType == 324 && !client.maleCharacter) {
                    client.maleCharacter = true;
                    IdentityKit.changeCharacterGender(client);
                }
                if (contentType == 325 && client.maleCharacter) {
                    client.maleCharacter = false;
                    IdentityKit.changeCharacterGender(client);
                }

                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_7__FADE_SCREEN) {
                final String text = client.incoming.readString();
                final int state = client.incoming.readSignedByte();
                final int seconds = client.incoming.readSignedByte();
                final int opacity = client.incoming.readUnsignedByte();

                if (state == 0) {
                    if (client.screenFadeManager != null) {
                        client.screenFadeManager.stop();
                    }
                } else if (state == 3) {
                    // still black background
                    client.screenFadeManager = new ScreenFadeManager(opacity);
                    client.screenFadeManager.start();
                } else {
                    // fade out
                    client.screenFadeManager = new ScreenFadeManager(state, seconds, text);
                    client.screenFadeManager.start();
                }


                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_108__SET_TOTAL_EXP) {
                client.totalExp = client.incoming.readLong();
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_116__SEND_EXP_DROP) {
                int skillId = client.incoming.readUnsignedByte();
                int experience = client.incoming.readInt();
                if (Configuration.enableSkillOrbs) {
                    SkillOrbs.orbs[skillId].receivedExperience();
                }
                if (Configuration.xpCounterOpen) {
                    XpDrops.add(skillId, experience);
                }
                if (skillId != 3) {
                    client.currentSkill = skillId;
                    //GrinderScape.discord.setTrainingId(skillId);
                }
                client.totalExp += experience;
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_243__ADD_BROADCAST_MESSAGE) {
                String message = client.incoming.readString();
                String link = client.incoming.readString();
                BroadCastManager.broadcast.put(message, link);
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_245__REMOVE_BROADCAST_MESSAGE) {
                String message = client.incoming.readString();
                BroadCastManager.broadcast.remove(message);
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_183__SHOW_TELEPORT_INTERFACE) {
                int menu = client.incoming.readUnsignedByte();
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_105__INTERFACE_TEXT_CLEAR) {
                int textFrom = client.incoming.readInt();
                int textTo = client.incoming.readInt();
                for (int i = textFrom; i <= textTo; i++) {
                    client.sendString("", i);
                }
                // Drop tables rectangles clearing
                if (textFrom == ItemDropFinder.ITEM_CHANCE_TEXT_START_ID) {
                    for (int i = 0; i < 100; i++) {
                        Widget.interfaceCache[ItemDropFinder.ITEM_RECTANGLE_START_ID + i * 2].invisible = true;
                    }
                } else if (textFrom == ItemDropFinder.LIST_START_ID) {
                    for (int i = 0; i < 100; i++) {
                        Widget.interfaceCache[ItemDropFinder.LIST_START_ID + i * 2 + 1].invisible = true;
                    }
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_10__SET_SCROLL_BAR_HEIGHT) {
                int interface_ = client.incoming.readInt();
                int scrollMax = client.incoming.readShort();
                Widget w = Widget.interfaceCache[interface_];
                if (w != null) {
                    w.scrollMax = scrollMax;
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_9__INTERFACE_SCROLL_RESET) {
                int interface_ = client.incoming.readInt();
                Widget w = Widget.interfaceCache[interface_];
                if (w != null) {
                    w.scrollPosition = 0;
                }
                switch (interface_) {
                    case ItemDropFinder.LIST_SCROLL_ID:
                        ItemDropFinder.updateListScroll();
                        break;
                    case ItemDropFinder.ITEM_SCROLL_ID:
                        ItemDropFinder.updateItemScroll();
                        break;
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_127__UPDATE_PLAYER_RIGHTS) {
                client.clientRights = client.incoming.readUnsignedByte();
                client.myCrown = client.incoming.readUnsignedByte();
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_81__PLAYER_UPDATING) {
                client.updatePlayers(client.packetSize, client.incoming);
                client.receivedPlayerUpdatePacket = false;
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_186__SEND_SPECIAL_ATTACK_STATE) {
                client.specialEnabled = client.incoming.readNegUByte();
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_123__SEND_CONSOLE_COMMAND) {
                String msg = client.incoming.readString();
                ClientCompanion.console.printMessage(msg, 1);
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_115__SHOW_CLANCHAT_OPTIONS) {
                client.showClanOptions = client.incoming.readUnsignedByte() == 1;
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_178__CLEAR_REGIONAL_SPAWNS) {
                client.clearRegionalSpawns();
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_64__CLEAR_REGION_PACKET) {
                client.localX = client.incoming.readNegUByte();
                client.localY = client.incoming.readUByteS();
                for (int x = client.localX; x < client.localX + 8; x++) {
                    for (int y = client.localY; y < client.localY + 8; y++)
                        if (client.groundItems[Client.plane][x][y] != null) {
                            client.groundItems[Client.plane][x][y] = null;
                            client.updateGroundItems(x, y);
                        }
                }
                for (SpawnedObject object = (SpawnedObject) client.spawns
                        .last(); object != null; object = (SpawnedObject) client.spawns.previous())
                    if (object.x >= client.localX && object.x < client.localX + 8 && object.y >= client.localY && object.y < client.localY + 8
                            && object.plane == Client.plane)
                        object.getLongetivity = 0;
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_185__SHOW_PLAYER_HEAD_ON_INTERFACE) {
                int playerHeadModelId = client.incoming.readLEUShortA();
                Widget.interfaceCache[playerHeadModelId].defaultMediaType = 3;
                if (Client.localPlayer.npcDefinition == null)
                    Widget.interfaceCache[playerHeadModelId].defaultMedia = (Client.localPlayer.appearanceColors[0] << 25)
                            + (Client.localPlayer.appearanceColors[4] << 20) + (Client.localPlayer.equipment[0] << 15)
                            + (Client.localPlayer.equipment[8] << 10) + (Client.localPlayer.equipment[11] << 5)
                            + Client.localPlayer.equipment[1];
                else
                    Widget.interfaceCache[playerHeadModelId].defaultMedia = (int) (0x12345678L
                            + Client.localPlayer.npcDefinition.interfaceType);
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_107__RESET_CAMERA) {
                client.isCameraLocked = false;
                for (int l = 0; l < 5; l++)
                    Camera.quakeDirectionActive[l] = false;
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_72__CLEAN_ITEMS_OF_INTERFACE) {
                int id = client.incoming.readUShort();
                Widget widget = Widget.interfaceCache[id];
                for (int slot = 0; slot < widget.inventoryItemId.length; slot++) {
                    widget.inventoryItemId[slot] = -1;
                    widget.inventoryItemId[slot] = 0;
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_166__SPIN_CAMERA) {
                client.isCameraLocked = true;
                client.x = client.incoming.readUnsignedByte();
                client.y = client.incoming.readUnsignedByte();
                client.height = client.incoming.readUShort();
                client.speed = client.incoming.readUnsignedByte();
                client.angle = client.incoming.readUnsignedByte();
                if (client.angle >= 100) {
                    client.cameraX = client.x * 128 + 64;
                    client.cameraZ = client.y * 128 + 64;
                    client.cameraY = client.getTileHeight(Client.plane, client.cameraZ, client.cameraX) - client.height;
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_134__SEND_SKILL) {
                int skill = client.incoming.readUnsignedByte();
                int level = client.incoming.readInt();
                int maxLevel = client.incoming.readInt();
                int experience = client.incoming.readInt();

                if (skill < client.currentExp.length) {
                    client.currentExp[skill] = experience;
                    client.currentLevels[skill] = level;
                    client.maximumLevels[skill] = maxLevel;

                    if (skill == 3 && Client.localPlayer != null) {
                        Client.localPlayer.currentHealth = level;
                        Client.localPlayer.maxHealth = maxLevel;
                    }
                    if (Configuration.filterLevel)
                        Spellbooks.update(Spellbooks.Spellbook.forId(Spellbooks.getCurrentBookId()));
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_71__SEND_SIDE_TAB) {
                int id = client.incoming.readUShort();
                int tab = client.incoming.readUByteA();
                if (id == 65535)
                    id = -1;
                ClientCompanion.tabInterfaceIDs[tab] = id;

                if (tab == 6 && id != Spellbooks.getCurrentBookId()) {
                    Spellbooks.setCurrentBookId(id);
                    Spellbooks.update(Spellbooks.Spellbook.forId(id));
                    client.setButtonConfig(Spellbooks.FILTER_BUTTON_CONFIG, 0);
                }

                ClientCompanion.tabAreaAltered = true;
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_74__PLAY_SONG) { // music packet
                int track = client.incoming.readLEUShortA();
                int volume = client.incoming.readUnsignedByte();
                int delay = client.incoming.readUShort();
                if (track == 0xffff)
                    track = -1;

                if (volume == 1) {
                    Audio.requestMusicTrack2(track);
                } else {
                    Audio.requestMusicTrack(track);
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_109__LOGOUT) {
                client.resetLogout();
                client.opcode = -1;
                return false;
            }

            if (client.opcode == PacketMetaData.PACKET_70__MOVE_COMPONENT) {
                int horizontalOffset = client.incoming.readShort();
                int verticalOffset = client.incoming.readLEShort();
                int id = client.incoming.readLEUShort();
                Widget widget = Widget.interfaceCache[id];
                widget.horizontalOffset = horizontalOffset;
                widget.verticalOffset = verticalOffset;
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_73__SEND_MAP_REGION || client.opcode == PacketMetaData.PACKET_241__SEND_REGION_MAP_REGION) {
//                clearRegionalSpawns();
                // int[] constructedRegionIDs = new int[676];

                int regionX = client.currentRegionX;
                int regionY = client.currentRegionY;
                if (client.opcode == PacketMetaData.PACKET_73__SEND_MAP_REGION) {
                    regionX = client.incoming.readUShortA();
                    regionY = client.incoming.readUShort();
                    client.isInInstance = false;
                } else if (client.opcode == PacketMetaData.PACKET_241__SEND_REGION_MAP_REGION) {
                    for (int z = 0; z < 4; z++) {
                        for (int x = 0; x < 13; x++) {
                            for (int y = 0; y < 13; y++) {
                                int visible = client.incoming.readByte();
                                if (visible == 5) {
                                    client.instanceChunkTemplates[z][x][y] = (int) client.incoming.readLong();
                                } else {
                                    client.instanceChunkTemplates[z][x][y] = -1;
                                }
                            }
                        }
                    }

                    regionX = client.incoming.readUShort();
                    regionY = client.incoming.readUShort();
                    client.isInInstance = true;
                } else if (client.opcode == PacketMetaData.PACKET_239__SEND_REGION_MAP_CHUNK) {
                    int chunkX = client.incoming.readByte();
                    int chunkY = client.incoming.readByte();
                    int chunkZ = client.incoming.readByte();
                    client.instanceChunkTemplates[chunkZ][chunkX][chunkY] = (int) client.incoming.readLong();
                    client.isInInstance = true;
                }

                if (client.opcode != PacketMetaData.PACKET_241__SEND_REGION_MAP_REGION && client.currentRegionX == regionX
                        && client.currentRegionY == regionY && client.getLoadingStage() == Client.GAME_ASSETS_LOADED) {
                    client.opcode = -1;
                    return true;
                }
                client.currentRegionX = regionX;
                client.currentRegionY = regionY;
                client.baseX = (client.currentRegionX - 6) * 8;
                client.baseY = (client.currentRegionY - 6) * 8;
                client.inTutorialIsland = (client.currentRegionX / 8 == 48 || client.currentRegionX / 8 == 49) && client.currentRegionY / 8 == 48;
                if (client.currentRegionX / 8 == 48 && client.currentRegionY / 8 == 148)
                    client.inTutorialIsland = true;
                if (OSObjectDefinition.USE_OSRS)
                    GameShell.updateGameState(25);
                else
                    client.setLoadingStage(1);
                client.loadingStartTime = System.currentTimeMillis();
                ClientCompanion.gameScreenImageProducer.initDrawingArea();
                client.drawLoadingMessages();
                ClientCompanion.gameScreenImageProducer.drawGraphics(client.canvas.getGraphics(), ClientUI.frameMode == Client.ScreenMode.FIXED ? 4 : 0, ClientUI.frameMode == Client.ScreenMode.FIXED ? 4 : 0
                );
                if (client.opcode == 73) {
                    int regionCount = 0;
                    for (int x = (client.currentRegionX - 6) / 8; x <= (client.currentRegionX + 6) / 8; x++) {
                        for (int y = (client.currentRegionY - 6) / 8; y <= (client.currentRegionY + 6) / 8; y++)
                            regionCount++;
                    }
                    client.regionLandArchives = new byte[regionCount][];
                    client.regionMapArchives = new byte[regionCount][];
                    client.regions = new int[regionCount];
                    client.regionLandArchiveIds = new int[regionCount];
                    client.regionMapArchiveIds = new int[regionCount];
                    regionCount = 0;

                    for (int x = (client.currentRegionX - 6) / 8; x <= (client.currentRegionX + 6) / 8; x++) {
                        for (int y = (client.currentRegionY - 6) / 8; y <= (client.currentRegionY + 6) / 8; y++) {
                            client.regions[regionCount] = (x << 8) + y;
                            if (client.inTutorialIsland
                                    && (y == 49 || y == 149 || y == 147 || x == 50 || x == 49 && y == 47)) {
                                client.regionLandArchiveIds[regionCount] = -1;
                                client.regionMapArchiveIds[regionCount] = -1;
                                regionCount++;
                            } else {
//                                int map = regionLandArchiveIds[regionCount] = OsCache.indexCache5.getArchiveId("l" + regionX + "_" + regionY);
                                int map = client.regionLandArchiveIds[regionCount] = client.resourceProvider.resolve(0, y, x);
                                if (map != -1) {
//                                    System.out.println("Trying to load region map "+map);
//                                    OsCache.indexCache5.tryLoadArchive(map);
                                    client.resourceProvider.provide(3, map);
                                } else
                                    System.err.println("Could not load region land archive at " + x + ", " + y + " regionId=" + client.regions[regionCount]);

//                                int landscape = regionMapArchiveIds[regionCount] = OsCache.indexCache5.getArchiveId("m" + regionX + "_" + regionY);
                                int landscape = client.regionMapArchiveIds[regionCount] = client.resourceProvider.resolve(1, y, x);
                                if (landscape != -1) {
//                                    System.out.println("Trying to load region landscape "+landscape);
//                                    OsCache.indexCache5.tryLoadArchive(landscape);
                                    client.resourceProvider.provide(3, landscape);
                                } else
                                    System.err.println("Coul not load region map archive at " + x + ", " + y + " regionId=" + client.regions[regionCount]);

                                regionCount++;
                            }
                        }
                    }
                }

                if (client.opcode == 241) {
                    int totalLegitChunks = 0;
                    int[] totalChunks = new int[676];
                    for (int z = 0; z < 4; z++) {
                        for (int x = 0; x < 13; x++) {
                            for (int y = 0; y < 13; y++) {
                                int tileBits = client.instanceChunkTemplates[z][x][y];
                                if (tileBits != -1) {
                                    int xCoord = tileBits >> 14 & 0x3ff;
                                    int yCoord = tileBits >> 3 & 0x7ff;
                                    int mapRegion = (xCoord / 8 << 8) + yCoord / 8;
                                    for (int idx = 0; idx < totalLegitChunks; idx++) {
                                        if (totalChunks[idx] != mapRegion)
                                            continue;
                                        mapRegion = -1;

                                    }
                                    if (mapRegion != -1) {
                                        totalChunks[totalLegitChunks++] = mapRegion;
                                    }
                                }
                            }
                        }
                    }
                    client.regionLandArchives = new byte[totalLegitChunks][];
                    client.regionMapArchives = new byte[totalLegitChunks][];
                    client.regions = new int[totalLegitChunks];
                    client.regionLandArchiveIds = new int[totalLegitChunks];
                    client.regionMapArchiveIds = new int[totalLegitChunks];
                    for (int idx = 0; idx < totalLegitChunks; idx++) {
                        int region = client.regions[idx] = totalChunks[idx];
                        int l30 = region >> 8 & 0xff;
                        int l31 = region & 0xff;
                        // TODO: conver these resourceProvider calls to OSCache, see packet 73
                        int terrainMapId = client.regionLandArchiveIds[idx] = client.resourceProvider.resolve(0, l31, l30);
                        if (terrainMapId != -1)
                            client.resourceProvider.provide(3, terrainMapId);
                        int objectMapId = client.regionMapArchiveIds[idx] = client.resourceProvider.resolve(1, l31, l30);
                        if (objectMapId != -1)
                            client.resourceProvider.provide(3, objectMapId);
                    }
                }
                int dx = client.baseX - client.previousAbsoluteX;
                int dy = client.baseY - client.previousAbsoluteY;
                client.previousAbsoluteX = client.baseX;
                client.previousAbsoluteY = client.baseY;
                for (int index = 0; index < client.npcCount; index++) {
                    Npc npc = client.npcs[client.npcIndices[index]];
                    if (npc != null) {
                        for (int point = 0; point < 10; point++) {
                            npc.pathX[point] -= dx;
                            npc.pathY[point] -= dy;
                        }
                        npc.x -= dx * 128;
                        npc.y -= dy * 128;
                    }
                }
                for (int index = 0; index < client.MAX_ENTITY_COUNT; index++) {
                    Player player = client.players[index];
                    if (player != null) {
                        for (int point = 0; point < 10; point++) {
                            player.pathX[point] -= dx;
                            player.pathY[point] -= dy;
                        }
                        player.x -= dx * 128;
                        player.y -= dy * 128;
                    }
                }
                client.receivedPlayerUpdatePacket = true;
                byte startX = 0;
                byte endX = 104;
                byte stepX = 1;
                if (dx < 0) {
                    startX = 103;
                    endX = -1;
                    stepX = -1;
                }
                byte startY = 0;
                byte endY = 104;
                byte stepY = 1;

                if (dy < 0) {
                    startY = 103;
                    endY = -1;
                    stepY = -1;
                }
                for (int x = startX; x != endX; x += stepX) {
                    for (int y = startY; y != endY; y += stepY) {
                        int shiftedX = x + dx;
                        int shiftedY = y + dy;
                        for (int plane = 0; plane < 4; plane++)
                            if (shiftedX >= 0 && shiftedY >= 0 && shiftedX < 104 && shiftedY < 104) {
                                client.groundItems[plane][x][y] = client.groundItems[plane][shiftedX][shiftedY];
                            } else {
                                client.groundItems[plane][x][y] = null;
                            }
                    }
                }
                for (SpawnedObject object = (SpawnedObject) client.spawns
                        .last(); object != null; object = (SpawnedObject) client.spawns.previous()) {
                    object.x -= dx;
                    object.y -= dy;
                    if (object.x < 0 || object.y < 0 || object.x >= 104 || object.y >= 104)
                        object.remove();
                }
                if (client.destinationX != 0) {
                    client.destinationX -= dx;
                    client.destinationY -= dy;
                }
                client.isCameraLocked = false;
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_208__SEND_WALKABLE_INTERFACE) {
                int interfaceId = client.incoming.readInt();
                //if (interfaceId == -1)
                //    GrinderScape.discord.ignoreBottomLine = false;
                if (interfaceId >= 0)
                    client.resetAnimation(interfaceId);
                client.openWalkableInterface = interfaceId;
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_99__SEND_MINIMAP_STATE) {
                GameFrame.minimapState = client.incoming.readUnsignedByte();
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_75__SHOW_NPC_HEAD_ON_INTERFACE) {
                int npcId = client.incoming.readLEUShortA();
                int interfaceId = client.incoming.readLEUShortA();
                Widget.interfaceCache[interfaceId].defaultMediaType = 2;
                Widget.interfaceCache[interfaceId].defaultMedia = npcId;

//                NpcDefinition definition = NpcDefinition.lookup(npcId);
//                System.out.println("Widget.interfaceCache[interfaceId].modelRotation1 = "+Widget.interfaceCache[interfaceId].modelRotation1);
//                System.out.println("Widget.interfaceCache[interfaceId].modelRotation2 = "+Widget.interfaceCache[interfaceId].modelRotation2);
                int zoom = 796;
                if (npcId == 3774) {
                    zoom = 1200;
                }
                Widget.interfaceCache[interfaceId].modelXAngle = 40;
                Widget.interfaceCache[interfaceId].modelYAngle = 1882;
                Widget.interfaceCache[interfaceId].modelZoom = zoom;

                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_114__SYSTEM_UPDATE) {
                client.systemUpdateTime = client.incoming.readLEUShort() * 30;
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_60__SEND_MULTIPLE_MAP_PACKETS) {
                client.localY = client.incoming.readUnsignedByte();
                client.localX = client.incoming.readNegUByte();
                while (client.incoming.index < client.packetSize) {
                    int k3 = client.incoming.readUnsignedByte();
                    parseRegionPackets(client, client.incoming, k3);
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_35__SEND_EARTHQUAKE) {
                int quakeDirection = client.incoming.readUnsignedByte();
                int quakeMagnitude = client.incoming.readUnsignedByte();
                int quakeAmplitude = client.incoming.readUnsignedByte();
                int fourPiOverPeriod = client.incoming.readUnsignedByte();
                Camera.quakeDirectionActive[quakeDirection] = true;
                Camera.quakeMagnitudes[quakeDirection] = quakeMagnitude;
                Camera.quakeAmplitudes[quakeDirection] = quakeAmplitude;
                client.quake4PiOverPeriods[quakeDirection] = fourPiOverPeriod;
                client.quakeTimes[quakeDirection] = 0;
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_174__PLAY_SOUND_EFFECT) {
                int soundId = client.incoming.readUShort();
                int startDelay = client.incoming.readUShort();
                int loopAmount = client.incoming.readUnsignedByte();
                int loopDelay = client.incoming.readUShort();
                client.playSound(soundId, loopAmount, startDelay);
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_175__STOP_SOUND_EFFECT) {
                int id = client.incoming.readUShort();
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_38__SET_AUTOCAST_ID) {
                int auto = client.incoming.readUShort();
                if (auto == -1) {
                    GameFrame.autocast = false;
                    client.autoCastId = 0;
                } else {
                    GameFrame.autocast = true;
                    client.autoCastId = auto;
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_104__SEND_PLAYER_OPTION) {
                int slot = client.incoming.readNegUByte();
                int lowPriority = client.incoming.readUByteA();
                String message = client.incoming.readString();

                if (slot >= 0 && slot < ClientCompanion.PLAYER_OPTION_COUNT) {
                    if (message.equalsIgnoreCase("null"))
                        message = null;
                    client.playerOptions[slot] = message;
                    client.playerOptionsHighPriority[slot] = lowPriority == 0;
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_78__CLEAR_MINIMAP_FLAG) {
                int x = client.incoming.readUnsignedByte();
                int y = client.incoming.readUnsignedByte();
                client.destinationX = x;
                client.destinationY = y;
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_250__ENABLE_NOCLIP) {
                for (int plane = 0; plane < 4; plane++) {
                    for (int x = 1; x < 103; x++) {
                        for (int y = 1; y < 103; y++) {
                            client.collisionMaps[plane].clipData[x][y] = 0;
                        }
                    }
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_251__SEND_URL) {
                String url = client.incoming.readString();
                MiscUtils.launchURL(url);
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_252__SEND_SPECIAL_MESSAGE) {
                int type = client.incoming.readUnsignedByte();
                String name = client.incoming.readString();
                String message = client.incoming.readString();
                long encodedName = StringUtils.encodeBase37(name);
                boolean ignored = PlayerRelations.isIgnored(encodedName);
                if (!ignored) {
                    client.sendMessage(message, type, name);
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_253__SEND_MESSAGE) {
                String message = client.incoming.readString();

                if (message.equals(":startspin:")) {
                    client.spinSpeed = 1;
                    client.spinnerSpinning = true;
                } else if (message.equals(":newsflash:")) {
                    client.newsflash = true;
                } else if (message.startsWith("bj_timer:")) {
                    ClientCompanion.blackJackTimer = Integer.parseInt(message.split(":")[1]);

                    if (ClientCompanion.blackJackTimer == 0) {
                        client.sendString("", 52034);
                    }

                } else if (message.startsWith("displaynpc:")) {
                    String[] split = message.split(":");
                    PetSystem.petSelected = Integer.parseInt(split[1]);
                } else if (message.startsWith("percentage:")) {
                    String[] split = message.split(":");
                    int id = Integer.parseInt(split[1]);
                    int value = Integer.parseInt(split[2]);
                    Widget w = Widget.interfaceCache[id];
                    w.percentageCompleted = value;
                } else if (message.endsWith(":tradereq:")) {
                    String name = message.substring(0, message.indexOf(":"));
                    long encodedName = StringUtils.encodeBase37(name);
                    boolean ignored = PlayerRelations.isIgnored(encodedName);
                    if (!ignored && client.onTutorialIsland == 0)
                        client.sendMessage("wishes to trade with you.", ChatBox.CHAT_TYPE_TRADE, name);
                } else if (message.endsWith(":gamblereq:")) {
                    String name = message.substring(0, message.indexOf(":"));
                    long encodedName = StringUtils.encodeBase37(name);
                    boolean ignored = PlayerRelations.isIgnored(encodedName);
                    if (!ignored && client.onTutorialIsland == 0)
                        client.sendMessage("wishes to gamble with you.", ChatBox.CHAT_TYPE_DUEL, name);
                } else if (message.endsWith(":duelreq:")) {
                    String name = message.substring(0, message.indexOf(":"));
                    long encodedName = StringUtils.encodeBase37(name);
                    boolean ignored = PlayerRelations.isIgnored(encodedName);
                    if (!ignored && client.onTutorialIsland == 0)
                        client.sendMessage("wishes to duel with you.", ChatBox.CHAT_TYPE_DUEL, name);
                } else if (message.endsWith(":chalreq:")) {
                    String name = message.substring(0, message.indexOf(":"));
                    long encodedName = StringUtils.encodeBase37(name);
                    boolean ignored = PlayerRelations.isIgnored(encodedName);
                    if (!ignored && client.onTutorialIsland == 0) {
                        String msg = message.substring(message.indexOf(":") + 1, message.length() - 9);
                        client.sendMessage(msg, ChatBox.CHAT_TYPE_DUEL, name);
                    }
                } else if (message.startsWith("[Alch Logs]")) {

                    final String startString = message.split("alched")[0];
                    final String itemName = message.substring(message.indexOf("@red@") + 5, message.indexOf("@bla@"));
                    final String amountString = message.substring(message.lastIndexOf("@red@") + 5, message.lastIndexOf("@bla@"));

                    boolean found = false;
                    for (int index = 0; index < 6; index++) {

                        final String oldMessage = client.chatMessages[index];

                        if (oldMessage == null)
                            continue;

                        if (!oldMessage.startsWith(startString))
                            continue;

                        final String oldItemName = oldMessage.substring(oldMessage.indexOf("@red@") + 5, oldMessage.indexOf("@bla@")).trim();
                        final String oldAmountString = oldMessage.substring(oldMessage.lastIndexOf("@red@") + 5, oldMessage.lastIndexOf("@bla@")).trim();

                        if (Objects.equals(itemName, oldItemName)) {
                            final String[] split = oldMessage.split("@red@")[0].trim().split(" ");
                            final String[] newSplit;
                            if (split[split.length - 1].contains("x")) {
                                newSplit = Arrays.copyOf(split, split.length);
                                newSplit[split.length - 1] = 1 + Integer.parseInt(split[split.length - 1].replace("x", "").trim()) + "x";
                            } else {
                                newSplit = Arrays.copyOf(split, split.length + 1);
                                newSplit[split.length] = "2x";
                            }
                            final long oldAmount = Long.parseLong(oldAmountString.replaceAll(",", "").replace(".", ""));
                            final long amount = Long.parseLong(amountString.replaceAll(",", "").replace(".", ""));
                            client.chatMessages[index] = oldMessage
                                    .replace(String.join(" ", split), String.join(" ", newSplit))
                                    .replace(oldAmountString, NumberFormat.getInstance().format(oldAmount + amount));
                            found = true;
                            break;
                        }
                    }
                    if (!found)
                        client.sendMessage(message, ChatBox.CHAT_TYPE_ALL, "");
                } else {
                    client.sendMessage(message, ChatBox.CHAT_TYPE_ALL, "");
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_1__STOP_ALL_ANIMATIONS) {
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_242__CLAN_MEMBER_LOGGED_IN) {
                final long encodedName = client.incoming.readLong();
                final boolean add = client.incoming.readUnsignedByte() == 1;
                if (add)
                    client.clanMembers.add(encodedName);
                else
                    client.clanMembers.remove(encodedName);
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_50__ADD_FRIEND) {
                long encodedName = client.incoming.readLong();
                int world = client.incoming.readUnsignedByte();
                PlayerRelations.handleAddFriendPacket(client, encodedName, world);
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_51__REMOVE_FRIEND) {
                long nameHash = client.incoming.readLong();
                PlayerRelations.handleRemoveFriendPacket(nameHash);
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_214__ADD_IGNORE) {
                long encodedName = client.incoming.readLong();
                PlayerRelations.handleAddIgnorePacket(encodedName);
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_215__REMOVE_IGNORE) {
                long nameHash = client.incoming.readLong();
                PlayerRelations.handleRemoveIgnorePacket(nameHash);
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_111__SEND_TOGGLE_QUICK_PRAYERS) {
                GameFrameInput.clickedPrayerOrb = client.incoming.readUnsignedByte() == 1;
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_110__SEND_ORB_CONFIG) {
                client.runEnergy = client.incoming.readUnsignedByte();
                client.poisonStatus = client.incoming.readUnsignedByte();
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_113__SEND_TOGGLE_RUN) {
                client.settings[429] = client.incoming.readUnsignedByte();
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_62__SEND_EXIT) {
                System.exit(1);
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_254__SEND_HINT_ICON) {
                // the first byte, which indicates the type of mob
                int hintIconDrawType = client.incoming.readUnsignedByte();
                if (hintIconDrawType == 1) // NPC Hint Arrow
                    client.mobOverlayRenderer.setHintIconNpcId(client.incoming.readUShort());
                if (hintIconDrawType >= 2 && hintIconDrawType <= 6) { // Location
                    int hintIconLocationArrowRelX = 0;
                    int hintIconLocationArrowRelY = 0;
                    if (hintIconDrawType == 2) { // Center
                        hintIconLocationArrowRelX = 64;
                        hintIconLocationArrowRelY = 64;
                    }
                    if (hintIconDrawType == 3) { // West side
                        hintIconLocationArrowRelX = 0;
                        hintIconLocationArrowRelY = 64;
                    }
                    if (hintIconDrawType == 4) { // East side
                        hintIconLocationArrowRelX = 128;
                        hintIconLocationArrowRelY = 64;
                    }
                    if (hintIconDrawType == 5) { // South side
                        hintIconLocationArrowRelX = 64;
                        hintIconLocationArrowRelY = 0;
                    }
                    if (hintIconDrawType == 6) { // North side
                        hintIconLocationArrowRelX = 64;
                        hintIconLocationArrowRelY = 128;
                    }
                    hintIconDrawType = 2;
                    client.mobOverlayRenderer.setHintIconLocationArrowRelX(hintIconLocationArrowRelX);
                    client.mobOverlayRenderer.setHintIconLocationArrowRelY(hintIconLocationArrowRelY);
                    client.mobOverlayRenderer.setHintIconX(client.incoming.readUShort());
                    client.mobOverlayRenderer.setHintIconY(client.incoming.readUShort());
                    client.mobOverlayRenderer.setHintIconLocationArrowHeight(client.incoming.readUnsignedByte());
                }
                if (hintIconDrawType == 10) // Player Hint Arrow
                    client.mobOverlayRenderer.setHintIconPlayerId(client.incoming.readUShort());

                client.mobOverlayRenderer.setHintIconDrawType(hintIconDrawType);
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_248__SEND_DUO_INTERFACE) {
                int mainInterfaceId = client.incoming.readUShortA();
                int sidebarOverlayInterfaceId = client.incoming.readUShort();
                if (client.backDialogueId != -1) {
                    client.backDialogueId = -1;
                    ChatBox.setUpdateChatbox(true);
                }
                if (client.inputDialogState != 0 && !client.isSearchOpen()) {
                    client.inputDialogState = 0;
                    ChatBox.setUpdateChatbox(true);
                }
                ClientCompanion.openInterfaceId = mainInterfaceId;
                client.overlayInterfaceId = sidebarOverlayInterfaceId;
                ClientCompanion.tabAreaAltered = true;
                client.continuedDialogue = false;
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_247__CHANGE_PASSWORD_RESPONSE) {
                final boolean isValid = client.incoming.readSignedByte() == 1;
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_68__RESET_INTERFACE) {
                for (int k5 = 0; k5 < client.settings.length; k5++)
                    if (client.settings[k5] != client.defaultSettings[k5]) {
                        client.settings[k5] = client.defaultSettings[k5];
                        client.updateVarp(k5);
                    }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_196__SEND_RECEIVED_PRIVATE_MESSAGE) {
                long encodedName = client.incoming.readLong();
                int messageId = client.incoming.readInt();
                int rights = client.incoming.readInt();
                int rightsToShow = client.incoming.readByte();
                boolean ignoreRequest = false;

                if (rights <= 1) {
                    for (int index = 0; index < PlayerRelations.ignoreCount; index++) {
                        if (PlayerRelations.ignoreListAsLongs[index] != encodedName)
                            continue;
                        ignoreRequest = true;
                    }
                }
                if (!ignoreRequest && client.onTutorialIsland == 0)
                    try {
                        client.privateMessageIds[client.privateMessageCount] = messageId;
                        client.privateMessageCount = (client.privateMessageCount + 1) % 100;
//                        String message = incoming.readString();// ChatMessageCodec.decode(packetSize
                        String message = TextInput.readFromStream(client.packetSize - 17, client.incoming);
                        if (rights > 0 || rightsToShow + 1 > 0) {
                            final String images = Player.getImages(rights, rightsToShow);
                            client.sendMessage(message, ChatBox.CHAT_TYPE_FROM_MOD_PRIVATE, images + StringUtils.formatText(StringUtils.decodeBase37(encodedName)));
                        } else {
                            client.sendMessage(message, ChatBox.CHAT_TYPE_PRIVATE, StringUtils.formatText(StringUtils.decodeBase37(encodedName)));
                        }

                    } catch (Exception ex) {
                        Log.error("cde1", ex);
                    }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_85__SEND_REGION) {
                client.localY = client.incoming.readNegUByte();
                client.localX = client.incoming.readNegUByte();
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_24__SEND_FLASHING_SIDEBAR) {
                client.flashingSidebarId = client.incoming.readUByteS();
                if (client.flashingSidebarId == ClientCompanion.tabId) {
                    if (client.flashingSidebarId == 3)
                        ClientCompanion.tabId = 1;
                    else
                        ClientCompanion.tabId = 3;
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_246__SEND_ITEM_TO_INTERFACE) {
                int widget = client.incoming.readLEUShort();
                int scale = client.incoming.readUShort();
                int item = client.incoming.readUShort();
                if (item == 65535) {
                    Widget.interfaceCache[widget].defaultMediaType = 0;
                } else {
                    ItemDefinition definition = ItemDefinition.lookup(item);
                    Widget.interfaceCache[widget].defaultMediaType = 4;
                    Widget.interfaceCache[widget].defaultMedia = item;
                    Widget.interfaceCache[widget].modelXAngle = definition.rotation_y;
                    Widget.interfaceCache[widget].modelYAngle = definition.rotation_x;
                    Widget.interfaceCache[widget].modelZoom = (definition.model_zoom * 100) / scale;
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_171__SEND_INTERFACE_VISIBILITY_STATE) {
                boolean hide = client.incoming.readUnsignedByte() == 1;
                int id = client.incoming.readInt();
                Widget.interfaceCache[id].invisible = hide;
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_137__UPDATE_SPECIAL_ATTACK_ORB) {
                client.specialAttack = client.incoming.readUnsignedByte();
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_126__SET_INTERFACE_TEXT) {
                try {

                    final String text = client.incoming.readString();
                    final int id = client.incoming.readInt();

                    client.sendString(text, id);

                  /*  switch (id) {
                        case 199:
                            GrinderScape.discord.ignoreBottomLine = true;
                            GrinderScape.discord.setLocation("Wilderness " + text);
                            break;
                    }*/

                    if (!Achievements.handleAchivementTextUpdate(id, text)) {
                        switch (id) {
                            case 5383:
                                Bank.setTitleText(text);
                                break;
                            case ExpCounterSetup.MULTIPLY_QUEST_TAB:
                                ExpCounterSetup.updateMultiplySetting();
                                break;
                        }
                    }

                } catch (Exception e) {
                    Log.error("Failed to SET_INTERFACE_TEXT, opcode = " + client.opcode, e);
                }
                client.opcode = -1;
                return true;
            }


            if (client.opcode == PacketMetaData.PACKET_244__SET_INTERFACE_TOOLTIP) {
                try {
                    String tooltip = client.incoming.readString();
                    int id = client.incoming.readInt();
                    Widget.interfaceCache[id].tooltip = tooltip;
                } catch (Exception e) {
                    Log.error("Failed to SET_INTERFACE_TOOLTIP, opcode = " + client.opcode, e);
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_206__UPDATE_CHAT_MODES) {
                int gameChat = client.incoming.readUnsignedByte();
                ChatBox.publicChatMode = client.incoming.readUnsignedByte();
                ChatBox.privateChatMode = client.incoming.readUnsignedByte();
                ChatBox.clanChatMode = client.incoming.readUnsignedByte();
                ChatBox.tradeMode = client.incoming.readUnsignedByte();
                ChatBox.yellMode = client.incoming.readUnsignedByte();
                ChatBox.setUpdateChatbox(true);
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_140__SEND_SPRITE_CHANGE) {
                int interfaceId = client.incoming.readInt();

                int disabledSprite = client.incoming.readShort();

                int enabledSprite = client.incoming.readShort();

                if (!BlackJack.onSpriteUpdate(interfaceId, disabledSprite) && Widget.interfaceCache[interfaceId] != null) {
                    Widget.interfaceCache[interfaceId].disabledSprite = SpriteLoader.getSprite(disabledSprite);
                    Widget.interfaceCache[interfaceId].enabledSprite = SpriteLoader.getSprite(enabledSprite);
                }

                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_240__SEND_PLAYER_WEIGHT) {
                client.weight = client.incoming.readShort();
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_8__SEND_MODEL_TO_INTERFACE) {
                int id = client.incoming.readLEUShortA();
                int model = client.incoming.readUShort();
                Widget.interfaceCache[id].defaultMediaType = 1;
                Widget.interfaceCache[id].defaultMedia = model;
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_122__SEND_CHANGE_INTERFACE_COLOUR) {
                int id = client.incoming.readInt();
                if(Widget.interfaceCache[id]!=null) {
                    Widget.interfaceCache[id].textColor = client.incoming.readInt();
                } else {
                    System.out.println("Color change packer error: "+id);
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_53__SEND_UPDATE_ITEMS) {
                try {

                    int interfaceId = client.incoming.readInt();
                    int itemCount = client.incoming.readShort();

                    Widget widget = Widget.interfaceCache[interfaceId];

                    if (widget == null || widget.inventoryItemId == null || widget.inventoryAmounts == null) {
                        client.opcode = -1;
                        return true;
                    }

                    for (int slot = 0; slot < itemCount; slot++) {
                        if (slot == widget.inventoryItemId.length)
                            break;
                        int amount = client.incoming.readInt();
                        widget.inventoryItemId[slot] = amount == -1 ? -1 : client.incoming.readShort();
                        widget.inventoryAmounts[slot] = amount;
                    }

                    for (int slot = itemCount; slot < widget.inventoryItemId.length; slot++) {
                        widget.inventoryItemId[slot] = 0;
                        widget.inventoryAmounts[slot] = 0;
                    }

                    widget.onUpdateItems();
                } catch (Exception e) {
                    Log.error("Failed to SEND_UPDATE_ITEMS, opcode = " + client.opcode, e);
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_55__SEND_CURRENT_BANK_TAB) {
                Bank.setCurrentTab(client.incoming.readUnsignedByte());
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_56__SEND_MODIFIABLE_X_VALUE) {
                int value = client.incoming.readInt();
                Bank.setModifiableXValue(value);
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_54__SEND_EFFECT_TIMER) {
                try {
                    int timer = client.incoming.readShort();
                    int sprite = client.incoming.readShort();
                    EffectTimerManager.addEffectTimer(new EffectTimer(timer, sprite));
                } catch (Exception e) {
                    Log.error("Failed to SEND_EFFECT_TIMER, opcode = " + client.opcode, e);
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_231__TOGGLE_NPC_DEBUG) {

                boolean toggled = client.incoming.readSignedByte() == 1;
                int npcIndex = client.incoming.readShort();
                final Npc npc = client.npcs[npcIndex];

                if (toggled) {

                    final int messages = client.incoming.readSignedByte();
                    final String[] debugMessages = new String[messages];

                    for (int i = 0; i < messages; i++)
                        debugMessages[i] = client.incoming.readString();

                    if (npc != null)
                        npc.debugMessages = debugMessages;

                } else if (npc != null) {
                    npc.debugMessages = null;
                }

                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_221__SET_FRIENDSERVER_STATUS) {
                PlayerRelations.friendServerStatus = client.incoming.readUnsignedByte();
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_177__MOVE_CAMERA) {
                client.isCameraLocked = true;
                Camera.cinematicCamXViewpointLoc = client.incoming.readUnsignedByte();
                Camera.cinematicCamYViewpointLoc = client.incoming.readUnsignedByte();
                Camera.cinematicCamZViewpointLoc = client.incoming.readUShort();
                Camera.constCinematicCamRotationSpeed = client.incoming.readUnsignedByte();
                Camera.varCinematicCamRotationSpeedPromille = client.incoming.readUnsignedByte();
                if (Camera.varCinematicCamRotationSpeedPromille >= 100) {
                    int cinCamXViewpointPos = Camera.cinematicCamXViewpointLoc * 128 + 64;
                    int cinCamYViewpointPos = Camera.cinematicCamYViewpointLoc * 128 + 64;
                    int cinCamZViewpointPos = client.getTileHeight(Client.plane, cinCamYViewpointPos, cinCamXViewpointPos)
                            - Camera.cinematicCamZViewpointLoc;
                    int dXPos = cinCamXViewpointPos - client.cameraX;
                    int dYPos = cinCamYViewpointPos - client.cameraZ;
                    int dZPos = cinCamZViewpointPos - client.cameraY;
                    int flatDistance = (int) Math.sqrt(dXPos * dXPos + dYPos * dYPos);
                    client.cameraPitch = (int) (Math.atan2(dZPos, flatDistance) * 325.94900000000001D) & 0x7ff;
                    client.cameraYaw = (int) (Math.atan2(dXPos, dYPos) * -325.94900000000001D) & 0x7ff;
                    if (client.cameraPitch < 128)
                        client.cameraPitch = 128;
                    if (client.cameraPitch > 383)
                        client.cameraPitch = 383;
                }
                client.opcode = -1;
                return true;
            }
            if (client.opcode == PacketMetaData.PACKET_180__VARBIT_SMALL) {
                setVarbitFromServer(client.incoming.readUShort(), client.incoming.readSignedByte());
                client.opcode = -1;
                return true;
            }
            if (client.opcode == PacketMetaData.PACKET_181__VARBIT_LARGE) {
                setVarbitFromServer(client.incoming.readUShort(), client.incoming.readInt());
                client.opcode = -1;
                return true;
            }
            if (client.opcode == PacketMetaData.PACKET_179__SET_CAMERA_POS) {
                client.cameraX = client.incoming.readUShort();
                client.cameraZ = client.incoming.readUShort();
                int z = client.incoming.readUShort();
                client.cameraY = z > 0 ? z : client.cameraY;
                client.isCameraLocked = true;
                client.opcode = -1;
                return true;
            }
            if (client.opcode == PacketMetaData.PACKET_249__SEND_INITIALIZE_PACKET) {
                int member = client.incoming.readUByteA();
                client.localPlayerIndex = client.incoming.readShort();
                client.opcode = -1;
                return true;
            }
            if (client.opcode == PacketMetaData.PACKET_65__NPC_UPDATING) {
                client.updateNPCs(client.incoming, client.packetSize, false);
                client.opcode = -1;
                return true;
            }
            if (client.opcode == PacketMetaData.PACKET_66__NPC_UPDATING_LARGE) {
                client.updateNPCs(client.incoming, client.packetSize, true);
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_27__SEND_ENTER_AMOUNT) {
                client.enter_amount_title = client.incoming.readString();
                client.messagePromptRaised = false;
                client.inputDialogState = 1;
                client.amountOrNameInput = "";
                ChatBox.setUpdateChatbox(true);
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_187__SEND_ENTER_NAME) { // Send Enter Name
                // Dialogue
                // (still allows
                // numbers)
                client.enter_name_title = client.incoming.readString();
                client.messagePromptRaised = false;
                client.inputDialogState = 2;
                client.amountOrNameInput = "";
                ChatBox.setUpdateChatbox(true);
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_97__SEND_NON_WALKABLE_INTERFACE) {
                int interfaceId = client.incoming.readInt();
                client.resetAnimation(interfaceId);
                if (client.overlayInterfaceId != -1) {
                    client.overlayInterfaceId = -1;
                    ClientCompanion.tabAreaAltered = true;
                }
                if (client.backDialogueId != -1) {
                    client.backDialogueId = -1;
                    ChatBox.setUpdateChatbox(true);
                }
                if (client.inputDialogState != 0) {
                    client.inputDialogState = 0;
                    ChatBox.setUpdateChatbox(true);
                }
                if (interfaceId == 15244) {
                    client.fullscreenInterfaceID = 17511;
                    ClientCompanion.openInterfaceId = 15244;
                }

                if (interfaceId == BlackJack.INTERFACE_ID) {
                    BlackJack.reset();
                }

                if (interfaceId == GameModeSetup.INTERFACE_ID) {
                    client.settings[GameModeSetup.CONFIG_ID] = 0;
                    GameModeSetup.setEquipment(GameModeSetup.IronmanEquip.values()[client.settings[GameModeSetup.CONFIG_ID]]);
                }

                if (interfaceId == ItemColorCustomizer.INTERFACE_ID) {
                    ItemColorCustomizer.onOpenInterface();
                }

                if (interfaceId == Widget.KEYBINDING_INTERFACE_ID) {
                    Keybinding.updateInterface();
                }

                if (interfaceId == ChangePassword.CHANGEPASSWORD_INTERFACE_ID)
                    ClientCompanion.interfaceInputSelected = ChangePassword.CHANGEPASSWORD_ENTER_PASSWORD_INPUT_ID;

                if (interfaceId == ItemDropFinder.INTERFACE_ID)
                    ClientCompanion.interfaceInputSelected = ItemDropFinder.INPUT_ID;

                if (interfaceId == 36100) {
                    int count = 0;

                    for (int i = 0; i < 14; i++) {
                        Widget w = Widget.interfaceCache[36100 + 453 + i];

                        if (w == null || w.getDefaultText().isEmpty()) {
                            break;
                        }

                        count++;
                    }

                    Widget side = Widget.interfaceCache[36100 + 50];
                    side.height = 22 + count * 18;
                    Widget.setBounds(36100 + 452, 0, 13 + count * 18, 1, side);
                }
                if (interfaceId == 36100) {
                    for (int i = 0; i < 14; i++) {
                        Widget w = Widget.interfaceCache[interfaceId + 453 + i];

                        if (w != null) {
                            w.tooltip = w.getDefaultText().isEmpty() ? "" : "Open";
                        }
                    }
                }
                ClientCompanion.openInterfaceId = interfaceId;
                client.continuedDialogue = false;
                client.opcode = -1;

                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_98__SEND_OVERLAY_OPEN_INTERFACE) {
                ClientCompanion.openInterfaceId2 = client.incoming.readInt();
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_87__SEND_CONFIG_INT) {
                int id = client.incoming.readLEUShort();
                int value = client.incoming.readMEInt();
                client.defaultSettings[id] = value;
                if (client.settings[id] != value) {
                    client.settings[id] = value;

                    client.updateVarp(id);
                    if (client.dialogueId != -1)
                        ChatBox.setUpdateChatbox(true);
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_36__SEND_CONFIG_BYTE) {
                int id = client.incoming.readLEUShort();
                byte value = client.incoming.readSignedByte();

                if (id == 999) {
                    client.placeholdersConfigIntercept(value);
                } else if (id < client.defaultSettings.length) {
                    client.defaultSettings[id] = value;
                    if (client.settings[id] != value) {
                        client.settings[id] = value;
                        client.updateVarp(id);
                        if (client.dialogueId != -1)
                            ChatBox.setUpdateChatbox(true);
                    }
                }
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_61__SEND_MULTICOMBAT_ICON) {
                client.multicombat = client.incoming.readUnsignedByte(); // 1 is active
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_200__SEND_ANIMATE_INTERFACE) {
                int id = client.incoming.readUShort();
                int animation = client.incoming.readShort();
                Widget widget = Widget.interfaceCache[id];
                widget.defaultAnimationId = animation;
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_219__CLOSE_INTERFACE) {
                client.onCloseInterface();
                if (client.overlayInterfaceId != -1) {
                    client.overlayInterfaceId = -1;
                    ClientCompanion.tabAreaAltered = true;
                }
                if (client.backDialogueId != -1) {
                    client.backDialogueId = -1;
                    ChatBox.setUpdateChatbox(true);
                }
                if (client.inputDialogState != 0) {
                    client.inputDialogState = 0;
                    ChatBox.setUpdateChatbox(true);
                }
                if (client.fullscreenInterfaceID != -1) {
                    client.fullscreenInterfaceID = -1;
                }

                if (client.screenFadeManager != null && client.screenFadeManager.isFixedOpacity())
                    client.screenFadeManager.stop();
                ClientCompanion.openInterfaceId = -1;
                ClientCompanion.openInterfaceId2 = -1;
                ClientCompanion.interfaceInputSelected = -1;
                client.continuedDialogue = false;
                client.opcode = -1;
                return true;
            }
            if (client.opcode == PacketMetaData.PACKET_34__UPDATE_SPECIFIC_ITEM) {

                int interfaceId = client.incoming.readUShort();
                Widget widget = Widget.interfaceCache[interfaceId];

                if (widget == null || widget.inventoryItemId == null) {
                    client.opcode = -1;
                    return true;
                }

                while (client.incoming.index < client.packetSize) {
                    int slot = client.incoming.readUnsignedByte();
                    int itemAmount = client.incoming.readInt();
                    int itemInvId = client.incoming.readUShort();

                    if (slot >= 0 && slot < widget.inventoryItemId.length) {
                        widget.inventoryItemId[slot] = itemInvId;
                        widget.inventoryAmounts[slot] = itemAmount;
                    }
                }

                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_4__SEND_GFX || client.opcode == PacketMetaData.PACKET_44__SEND_GROUND_ITEM || client.opcode == PacketMetaData.PACKET_84__SEND_ALTER_GROUND_ITEM_COUNT || client.opcode == PacketMetaData.PACKET_101__SEND_REMOVE_OBJECT || client.opcode == 239 || client.opcode == PacketMetaData.PACKET_117__SEND_PROJECTILE || client.opcode == PacketMetaData.PACKET_147__TRANSFORM_PLAYER_TO_OBJECT || client.opcode == PacketMetaData.PACKET_151__SEND_OBJECT || client.opcode == PacketMetaData.PACKET_156__REMOVE_GROUND_ITEM || client.opcode == PacketMetaData.PACKET_160__ANIMATE_OBJECT || client.opcode == PacketMetaData.PACKET_216__MAKE_ITEM_PUBLIC) {
                parseRegionPackets(client, client.incoming, client.opcode);
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_106__SWITCH_TAB) {
                ClientCompanion.tabId = client.incoming.readNegUByte();
                ClientCompanion.tabAreaAltered = true;
                client.opcode = -1;
                return true;
            }
            if (client.opcode == PacketMetaData.PACKET_164__SEND_NONWALKABLE_CHATBOX_INTERFACE) {
                int id = client.incoming.readLEUShort();

                client.resetAnimation(id);
                if (client.overlayInterfaceId != -1) {
                    client.overlayInterfaceId = -1;
                    ClientCompanion.tabAreaAltered = true;
                }
                client.backDialogueId = id;
                ChatBox.setUpdateChatbox(true);
                if (ClientCompanion.openInterfaceId != 6965)
                    ClientCompanion.openInterfaceId = -1;
                ClientCompanion.openInterfaceId2 = -1;
                ClientCompanion.interfaceInputSelected = -1;
                client.continuedDialogue = false;
                client.opcode = -1;
                return true;
            }

            if (client.opcode == PacketMetaData.PACKET_238__SEND_MONSTER_HUNT_TRACKER) {
                MonsterHuntTrackerManager.parsePacket(client.incoming);
                client.opcode = -1;
                return true;
            }

            Log.error("Packet Error: " +
                    "T1 - " + client.opcode + "," + client.packetSize + " - " + client.secondLastOpcode + "," + client.thirdLastOpcode);
            client.resetLogout();
        } catch (IOException _ex) {
            Log.error("packet read error", _ex);
        } catch (Exception exception) {

            StringBuilder s2 = new StringBuilder("T2 - " + client.opcode + "," + client.secondLastOpcode + "," + client.thirdLastOpcode + " - " + client.packetSize + ","
                    + (client.baseX + Client.localPlayer.pathX[0]) + "," + (client.baseY + Client.localPlayer.pathY[0]) + " - ");

            for (int j15 = 0; j15 < client.packetSize && j15 < 50; j15++)
                s2.append(client.incoming.array[j15]).append(",");

            Log.error("Packet Error: " + s2.toString(), exception);

            if (client.opcode == PacketMetaData.PACKET_65__NPC_UPDATING) {
                SnapshotCache.serialise();
            }
        }
        client.opcode = -1;
        return true;
    }

    private static void setVarbitFromServer(int id, int value) {
        if (!OSObjectDefinition.USE_OSRS) {
            setVarbitFromServer317(id, value);
            return;
        }
        VarbitDefinition definition = OsCache.getVarbitDefinition(id);
        int baseVar = definition.varp;
        int startBit = definition.lowBit;
        int endBit = definition.highBit;
        int range = Buffer.BIT_MASKS[endBit - startBit];
        if (value < 0 || value > range) {
            value = 0;
        }
        range <<= startBit;
        int varValue = Client.instance.settings[baseVar] & ~range | value << startBit & range;
        Client.instance.settings[baseVar] = varValue;
        Client.instance.updateVarp(baseVar);
    }

    private static void setVarbitFromServer317(int id, int value) {
        VariableBits bits = VariableBits.varbits[id];
        int baseVar = bits.setting;
        int startBit = bits.low;
        int endBit = bits.high;
        int range = Buffer.BIT_MASKS[endBit - startBit];
        if (value < 0 || value > range) {
            value = 0;
        }

        range <<= startBit;
        int varValue = Client.instance.settings[baseVar] & ~range | value << startBit & range;
        Client.instance.settings[baseVar] = varValue;
        Client.instance.updateVarp(baseVar);
    }

    public static void parseRegionPackets(Client client, Buffer stream, int packetType) {
        if (packetType == PacketMetaData.PACKET_84__SEND_ALTER_GROUND_ITEM_COUNT) {
            int offset = stream.readUnsignedByte();
            int xLoc = client.localX + (offset >> 4 & 7);
            int yLoc = client.localY + (offset & 7);
            int itemId = stream.readUShort();
            int oldItemCount = stream.readUShort();
            int newItemCount = stream.readUShort();
            if (xLoc >= 0 && yLoc >= 0 && xLoc < 104 && yLoc < 104) {
                NodeDeque groundItemsDeque = client.groundItems[Client.plane][xLoc][yLoc];
                if (groundItemsDeque != null) {
                    for (Item groundItem = (Item) groundItemsDeque
                            .last(); groundItem != null; groundItem = (Item) groundItemsDeque
                            .previous()) {
                        if (groundItem.ID != (itemId & 0x7fff) || groundItem.itemCount != oldItemCount)
                            continue;
                        groundItem.itemCount = newItemCount;
                        break;
                    }

                    client.updateGroundItems(xLoc, yLoc);
                }
            }
            return;
        }
        if (packetType == 239) {
            int offset = stream.getUnsignedByte();
            int soundX = (offset >> 4 & 7) + client.localX;
            int soundY = (offset & 7) + client.localY;
            int delay = stream.getUnsignedByte();
            int radius = stream.getUnsignedByte();
            int loopCount = stream.getUnsignedByte();
            int id = stream.readUShort();
            if (soundX >= 0 && soundY >= 0 && soundX < 104 && soundY < 104) {
                int audibleRange = radius + 1;
                if (Client.localPlayer.pathX[0] >= soundX - audibleRange && Client.localPlayer.pathX[0] <= audibleRange + soundX && Client.localPlayer.pathY[0] >= soundY - audibleRange && Client.localPlayer.pathY[0] <= audibleRange + soundY && Audio.areaSoundEffectVolume != 0 && loopCount > 0 && Audio.soundEffectCount < 50) {
                    Audio.soundEffectIds[Audio.soundEffectCount] = id;
                    Audio.areaSoundVolumeModifier[Audio.soundEffectCount] = loopCount;
                    Audio.queuedSoundEffectDelays[Audio.soundEffectCount] = delay;
                    Audio.soundEffects[Audio.soundEffectCount] = null;
                    Audio.soundLocations[Audio.soundEffectCount] = radius + (soundY << 8) + (soundX << 16);
                    ++Audio.soundEffectCount;
                }
            }
        }
        if (packetType == PacketMetaData.PACKET_216__MAKE_ITEM_PUBLIC) {
            int itemId = stream.readUShortA();
            int bitpack = stream.readUByteS();
            int xTile = client.localX + (bitpack >> 4 & 7);
            int yTile = client.localY + (bitpack & 7);
            int ownerIndex = stream.readUShortA();
            int count = stream.readUShort();
            if (xTile >= 0 && yTile >= 0 && xTile < 104 && yTile < 104 && ownerIndex != client.localPlayerIndex) {
                Item class30_sub2_sub4_sub2_2 = new Item();
                class30_sub2_sub4_sub2_2.ID = itemId;
                class30_sub2_sub4_sub2_2.itemCount = count;
                if (client.groundItems[Client.plane][xTile][yTile] == null)
                    client.groundItems[Client.plane][xTile][yTile] = new NodeDeque();
                client.groundItems[Client.plane][xTile][yTile].addFirst(class30_sub2_sub4_sub2_2);
                client.updateGroundItems(xTile, yTile);
            }
            return;
        }
        if (packetType == PacketMetaData.PACKET_156__REMOVE_GROUND_ITEM) {
            int offset = stream.readUByteA();
            int xLoc = client.localX + (offset >> 4 & 7);
            int yLoc = client.localY + (offset & 7);
            int itemId = stream.readUShort();
            if (xLoc >= 0 && yLoc >= 0 && xLoc < 104 && yLoc < 104) {
                NodeDeque groundItemsDeque = client.groundItems[Client.plane][xLoc][yLoc];
                if (groundItemsDeque != null) {
                    for (Item item = (Item) groundItemsDeque
                            .last(); item != null; item = (Item) groundItemsDeque.previous()) {
                        if (item.ID != (itemId & 0x7fff))
                            continue;
                        item.remove();
                        break;
                    }

                    if (groundItemsDeque.last() == null)
                        client.groundItems[Client.plane][xLoc][yLoc] = null;
                    client.updateGroundItems(xLoc, yLoc);
                }
            }
            return;
        }
        if (packetType == PacketMetaData.PACKET_160__ANIMATE_OBJECT) {
            int offset = stream.readUByteS();
            int xLoc = client.localX + (offset >> 4 & 7);
            int yLoc = client.localY + (offset & 7);
            int objectTypeFace = stream.readUByteS();
            int objectType = objectTypeFace >> 2;
            int objectFace = objectTypeFace & 3;
            int objectGenre = client.objectGroups[objectType];
            int animId = stream.readUShortA();
            if (xLoc >= 0 && yLoc >= 0 && xLoc < 103 && yLoc < 103) {
                int height_x0_y0 = Client.Tiles_heights[Client.plane][xLoc][yLoc];
                int height_x1_y0 = Client.Tiles_heights[Client.plane][xLoc + 1][yLoc];
                int height_x1_y1 = Client.Tiles_heights[Client.plane][xLoc + 1][yLoc + 1];
                int heightx0_y1 = Client.Tiles_heights[Client.plane][xLoc][yLoc + 1];
                if (objectGenre == 0) {// WallObject
                    BoundaryObject boundaryObjectObject = client.scene.getWallObject(Client.plane, xLoc, yLoc);
                    if (boundaryObjectObject != null) {
                        int objectId = DynamicObject.get_object_key(boundaryObjectObject.tag);
                        if (objectType == 2) {
                            boundaryObjectObject.renderable1 = new DynamicObject(objectId, 4 + objectFace, 2, xLoc, yLoc, height_x1_y0,
                                    height_x1_y1, height_x0_y0, heightx0_y1, animId, false);
                            boundaryObjectObject.renderable2 = new DynamicObject(objectId, objectFace + 1 & 3, 2, xLoc, yLoc, height_x1_y0,
                                    height_x1_y1, height_x0_y0, heightx0_y1, animId, false);
                        } else {
                            boundaryObjectObject.renderable1 = new DynamicObject(objectId, objectFace, objectType, xLoc, yLoc, height_x1_y0,
                                    height_x1_y1, height_x0_y0, heightx0_y1, animId, false);
                        }
                    }
                }
                if (objectGenre == 1) { // WallDecoration
                    WallDecoration wallDecoration = client.scene.getWallDecoration(xLoc, yLoc, Client.plane);
                    if (wallDecoration != null)
                        wallDecoration.renderable1 = new DynamicObject(DynamicObject.get_object_key(wallDecoration.tag), 0, 4, xLoc, yLoc, height_x1_y0,
                                height_x1_y1, height_x0_y0, heightx0_y1, animId, false);
                }
                if (objectGenre == 2) { // TiledObject
                    GameObject tiledObject = client.scene.getObjectOnTile(xLoc, yLoc, Client.plane);
                    if (objectType == 11)
                        objectType = 10;
                    if (tiledObject != null)
                        tiledObject.renderable = new DynamicObject(DynamicObject.get_object_key(tiledObject.tag), objectFace, objectType, xLoc, yLoc,
                                height_x1_y0, height_x1_y1, height_x0_y0, heightx0_y1, animId, false);
                }
                if (objectGenre == 3) { // GroundDecoration
                    FloorDecoration floorDecoration = client.scene.getGroundDecoration(yLoc, xLoc, Client.plane);
                    if (floorDecoration != null)
                        floorDecoration.renderable = new DynamicObject(DynamicObject.get_object_key(floorDecoration.tag), objectFace,
                                22, xLoc, yLoc, height_x1_y0, height_x1_y1, height_x0_y0, heightx0_y1, animId, false);
                }
            }
            return;
        }
        if (packetType == PacketMetaData.PACKET_147__TRANSFORM_PLAYER_TO_OBJECT) {
            int offset = stream.readUByteS();
            int xLoc = client.localX + (offset >> 4 & 7);
            int yLoc = client.localY + (offset & 7);
            int playerIndex = stream.readUShort();
            byte byte0GreaterXLoc = stream.readByteS();
            int startDelay = stream.readLEUShort();
            byte byte1GreaterYLoc = stream.readNegByte();
            int stopDelay = stream.readUShort();
            int objectTypeFace = stream.readUByteS();
            int objectType = objectTypeFace >> 2;
            int objectFace = objectTypeFace & 3;
            int objectGenre = client.objectGroups[objectType];
            byte byte2LesserXLoc = stream.readSignedByte();
            int objectId = stream.readUShort();
            byte byte3LesserYLoc = stream.readNegByte();
            Player player;
            if (playerIndex == client.localPlayerIndex)
                player = Client.localPlayer;
            else
                player = client.players[playerIndex];
            if (player != null) {
                Model model;
                int sizeX;
                int sizeY;
                if (OSObjectDefinition.USE_OSRS) {
                    OSObjectDefinition OS_object = OSObjectDefinition.lookup(objectId);
                    sizeX = OS_object.sizeX;
                    sizeY = OS_object.sizeY;
                    int x1 = xLoc + (sizeX >> 1);
                    int x2 = xLoc + (sizeX + 1 >> 1);
                    int y1 = yLoc + (sizeY >> 1);
                    int y2 = yLoc + (sizeY + 1 >> 1);
                    int[][] var25 = Client.Tiles_heights[Client.plane];
                    int var26 = var25[x2][y1] + var25[x1][y1] + var25[x1][y2] + var25[x2][y2] >> 2;
                    int var27 = (xLoc << 7) + (sizeX << 6);
                    int var28 = (yLoc << 7) + (sizeY << 6);

                    model = OS_object.getModel(objectType, objectFace, var25, var27, var26, var28);

                } else {
                    ObjectDefinition var18 = ObjectDefinition.lookup(objectId);
                    sizeX = var18.sizeX;
                    sizeY = var18.sizeY;
                    int x1 = xLoc + (sizeX >> 1);
                    int x2 = xLoc + (sizeX + 1 >> 1);
                    int y1 = yLoc + (sizeY >> 1);
                    int y2 = yLoc + (sizeY + 1 >> 1);
                    int[][] var25 = Client.Tiles_heights[Client.plane];
                    int var26 = var25[x2][y1] + var25[x1][y1] + var25[x1][y2] + var25[x2][y2] >> 2;
                    int var27 = (xLoc << 7) + (sizeX << 6);
                    int var28 = (yLoc << 7) + (sizeY << 6);
                    model = var18.getModel(objectType, objectFace, var25, var27, var26, var28);
                }

                if (model != null) {
                    client.requestSpawnObject(stopDelay + 1, -1, 0, objectGenre, yLoc, 0, Client.plane, xLoc, startDelay + 1);
                    player.objectModelStart = startDelay + Client.tick;
                    player.objectModelStop = stopDelay + Client.tick;
                    player.playerModel = model;
                    if (objectFace == 1 || objectFace == 3) {
                        int copyX = sizeX;
                        copyX = sizeY;
                        sizeX = copyX;
                    }
                    player.objectXPos = xLoc * 128 + sizeX * 64;
                    player.objectYPos = yLoc * 128 + sizeY * 64;
                    player.objectCenterHeight = client.getTileHeight(Client.plane, player.objectYPos, player.objectXPos);
                    if (byte2LesserXLoc > byte0GreaterXLoc) {
                        byte tmp = byte2LesserXLoc;
                        byte2LesserXLoc = byte0GreaterXLoc;
                        byte0GreaterXLoc = tmp;
                    }
                    if (byte3LesserYLoc > byte1GreaterYLoc) {
                        byte tmp = byte3LesserYLoc;
                        byte3LesserYLoc = byte1GreaterYLoc;
                        byte1GreaterYLoc = tmp;
                    }
                    player.objectAnInt1719LesserXLoc = xLoc + byte2LesserXLoc;
                    player.objectAnInt1721GreaterXLoc = xLoc + byte0GreaterXLoc;
                    player.objectAnInt1720LesserYLoc = yLoc + byte3LesserYLoc;
                    player.objectAnInt1722GreaterYLoc = yLoc + byte1GreaterYLoc;
                }
            }
        }
        if (packetType == PacketMetaData.PACKET_151__SEND_OBJECT) {
            int offset = stream.readUByteA();
            int x = client.localX + (offset >> 4 & 7);
            int y = client.localY + (offset & 7);
            int id = stream.readLEUShort();
            int objectTypeFace = stream.readUByteS();
            int type = objectTypeFace >> 2;
            int orientation = objectTypeFace & 3;
            int group = client.objectGroups[type];
            if (x >= 0 && y >= 0 && x < 104 && y < 104) {
//                System.out.println("offset ="+offset+", x = "+x+", y = "+y+", plane = "+plane+", type = "+type+", orientation = "+orientation+", group = "+group);
                client.requestSpawnObject(-1, id, orientation, group, y, type, Client.plane, x, 0);
            }
            return;
        }
        if (packetType == PacketMetaData.PACKET_4__SEND_GFX) {
            int offset = stream.readUnsignedByte();
            int xLoc = client.localX + (offset >> 4 & 7);
            int yLoc = client.localY + (offset & 7);
            int gfxId = stream.readUShort();
            int gfxHeight = stream.readUnsignedByte();
            int gfxDelay = stream.readUShort();
            if (xLoc >= 0 && yLoc >= 0 && xLoc < 104 && yLoc < 104) {
                xLoc = xLoc * 128 + 64;
                yLoc = yLoc * 128 + 64;
                AnimableObject loneGfx = new AnimableObject(Client.plane, Client.tick, gfxDelay, gfxId,
                        client.getTileHeight(Client.plane, yLoc, xLoc) - gfxHeight, yLoc, xLoc);
                client.incompleteAnimables.addFirst(loneGfx);
            }
            return;
        }
        if (packetType == PacketMetaData.PACKET_44__SEND_GROUND_ITEM) {
            int itemId = stream.readLEUShortA();
            int itemCount = stream.readInt();
            int offset = stream.readUnsignedByte();
            int xLoc = client.localX + (offset >> 4 & 7);
            int yLoc = client.localY + (offset & 7);
            if (xLoc >= 0 && yLoc >= 0 && xLoc < 104 && yLoc < 104) {
                Item groundItem = new Item();
                groundItem.ID = itemId;
                groundItem.itemCount = itemCount;
                if (client.groundItems[Client.plane][xLoc][yLoc] == null)
                    client.groundItems[Client.plane][xLoc][yLoc] = new NodeDeque();
                client.groundItems[Client.plane][xLoc][yLoc].addFirst(groundItem);
                client.updateGroundItems(xLoc, yLoc);
            }
            return;
        }
        if (packetType == PacketMetaData.PACKET_101__SEND_REMOVE_OBJECT) {
            int objectTypeFace = stream.readNegUByte();
            int type = objectTypeFace >> 2;
            int orientation = objectTypeFace & 3;
            int group = client.objectGroups[type];
            int offset = stream.readUnsignedByte();
            int x = client.localX + (offset >> 4 & 7);
            int y = client.localY + (offset & 7);
            if ((x + client.baseX) != 2840)
                if (x >= 0 && y >= 0 && x < 104 && y < 104) {
                    client.requestSpawnObject(-1, -1, orientation, group, y, type, Client.plane, x, 0);
                }
            return;
        }
        if (packetType == PacketMetaData.PACKET_117__SEND_PROJECTILE) {
            int offset = stream.readUnsignedByte();
            int x1 = client.localX + (offset >> 4 & 7);
            int y1 = client.localY + (offset & 7);
            int x2 = x1 + stream.readSignedByte();
            int y2 = y1 + stream.readSignedByte();
            int target = stream.readShort();
            int gfxMoving = stream.readUShort();
            int startHeight = stream.readUnsignedByte() * 4;
            int endHeight = stream.readUnsignedByte() * 4;
            int startDelay = stream.readUShort();
            int speed = stream.readUShort();
            int initialSlope = stream.readUnsignedByte();
            int frontOffset = stream.readUnsignedByte();
            if (x1 >= 0 && y1 >= 0 && x1 < 104 && y1 < 104 && x2 >= 0 && y2 >= 0 && x2 < 104 && y2 < 104
                    && gfxMoving != 65535) {
                x1 = x1 * 128 + 64;
                y1 = y1 * 128 + 64;
                x2 = x2 * 128 + 64;
                y2 = y2 * 128 + 64;
                Projectile projectile = new Projectile(initialSlope, endHeight, startDelay + Client.tick, speed + Client.tick,
                        frontOffset, Client.plane, client.getTileHeight(Client.plane, y1, x1) - startHeight, y1, x1, target, gfxMoving);

                projectile.calculateIncrements(startDelay + Client.tick, y2, client.getTileHeight(Client.plane, y2, x2) - endHeight, x2);

                if (target == 0)
                    projectile.lockOnPosition(x2, y2);

                client.projectiles.addFirst(projectile);

            }
        }
    }
}
