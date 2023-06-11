package com.grinder.model;

import com.grinder.Configuration;
import com.grinder.client.util.MenuOpcodes;
import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.cache.def.NpcDefinition;
import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.cache.graphics.sprite.SpriteLoader;
import com.runescape.cache.graphics.widget.Bank;
import com.runescape.cache.graphics.widget.Widget;
import com.runescape.collection.NodeDeque;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.Npc;
import com.runescape.entity.Player;
import com.runescape.entity.PlayerRelations;
import com.runescape.entity.model.Model;
import com.runescape.io.packets.outgoing.impl.ChatSettings;
import com.runescape.io.packets.outgoing.impl.ClickButton;
import com.grinder.client.ClientCompanion;
import com.runescape.input.MouseHandler;
import com.runescape.cache.graphics.sprite.SpriteCompanion;
import com.runescape.util.StringUtils;

import java.util.List;

/**
 * @version 1.0
 * @since 12/12/2019
 */
public class GameFrame {

    public static boolean autocast;
    public static int minimapState;
    public static int mapIconCount;
    public static int[] mapIconXs;
    public static int[] mapIconYs;
    public static int menuScreenArea;
    public static int menuOffsetX;
    public static int menuOffsetY;
    public static int menuWidth;
    public static int menuHeight;

    public static void processTabClick(Client client) {
        if (MouseHandler.lastButton == 1) {
            if (!client.displaySideStonesStacked()) {
                int xOffset = ChatBox.getDrawOffset(ClientUI.frameMode == Client.ScreenMode.FIXED, 0, ClientUI.frameWidth, 765);
                int yOffset = ChatBox.getDrawOffset(ClientUI.frameMode == Client.ScreenMode.FIXED, 0, ClientUI.frameHeight, 503);
                for (int i = 0; i < ClientCompanion.TAB_CLICK_X.length; i++) {
                    if (MouseHandler.x >= ClientCompanion.TAB_CLICK_START[i] + xOffset
                            && MouseHandler.x <= ClientCompanion.TAB_CLICK_START[i] + ClientCompanion.TAB_CLICK_X[i] + xOffset
                            && MouseHandler.y >= ClientCompanion.TAB_CLICK_Y[i] + yOffset && MouseHandler.y < ClientCompanion.TAB_CLICK_Y[i] + 37 + yOffset
                            && ClientCompanion.tabInterfaceIDs[i] != -1) {
                        ClientCompanion.tabId = i;
                        ClientCompanion.tabAreaAltered = true;

                        break;
                    }
                }
            } else if (ClientUI.frameWidth < 1000) {
                if (MouseHandler.lastPressedX >= ClientUI.frameWidth - 226 && MouseHandler.lastPressedX <= ClientUI.frameWidth - 195
                        && MouseHandler.lastPressedY >= ClientUI.frameHeight - 72 && MouseHandler.lastPressedY < ClientUI.frameHeight - 40
                        && ClientCompanion.tabInterfaceIDs[0] != -1) {
                    if (ClientCompanion.tabId == 0) {
                        ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                    } else {
                        ClientCompanion.showTabComponents = true;
                    }
                    ClientCompanion.tabId = 0;
                    ClientCompanion.tabAreaAltered = true;

                }
                if (MouseHandler.lastPressedX >= ClientUI.frameWidth - 194 && MouseHandler.lastPressedX <= ClientUI.frameWidth - 163
                        && MouseHandler.lastPressedY >= ClientUI.frameHeight - 72 && MouseHandler.lastPressedY < ClientUI.frameHeight - 40
                        && ClientCompanion.tabInterfaceIDs[1] != -1) {
                    if (ClientCompanion.tabId == 1) {
                        ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                    } else {
                        ClientCompanion.showTabComponents = true;
                    }
                    ClientCompanion.tabId = 1;
                    ClientCompanion.tabAreaAltered = true;

                }
                if (MouseHandler.lastPressedX >= ClientUI.frameWidth - 162 && MouseHandler.lastPressedX <= ClientUI.frameWidth - 131
                        && MouseHandler.lastPressedY >= ClientUI.frameHeight - 72 && MouseHandler.lastPressedY < ClientUI.frameHeight - 40
                        && ClientCompanion.tabInterfaceIDs[2] != -1) {
                    if (ClientCompanion.tabId == 2) {
                        ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                    } else {
                        ClientCompanion.showTabComponents = true;
                    }
                    ClientCompanion.tabId = 2;
                    ClientCompanion.tabAreaAltered = true;

                }
                if (MouseHandler.lastPressedX >= ClientUI.frameWidth - 129 && MouseHandler.lastPressedX <= ClientUI.frameWidth - 98
                        && MouseHandler.lastPressedY >= ClientUI.frameHeight - 72 && MouseHandler.lastPressedY < ClientUI.frameHeight - 40
                        && ClientCompanion.tabInterfaceIDs[3] != -1) {
                    if (ClientCompanion.tabId == 3) {
                        ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                    } else {
                        ClientCompanion.showTabComponents = true;
                    }
                    ClientCompanion.tabId = 3;
                    ClientCompanion.tabAreaAltered = true;

                }
                if (MouseHandler.lastPressedX >= ClientUI.frameWidth - 97 && MouseHandler.lastPressedX <= ClientUI.frameWidth - 66
                        && MouseHandler.lastPressedY >= ClientUI.frameHeight - 72 && MouseHandler.lastPressedY < ClientUI.frameHeight - 40
                        && ClientCompanion.tabInterfaceIDs[4] != -1) {
                    if (ClientCompanion.tabId == 4) {
                        ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                    } else {
                        ClientCompanion.showTabComponents = true;
                    }
                    ClientCompanion.tabId = 4;
                    ClientCompanion.tabAreaAltered = true;

                }
                if (MouseHandler.lastPressedX >= ClientUI.frameWidth - 65 && MouseHandler.lastPressedX <= ClientUI.frameWidth - 34
                        && MouseHandler.lastPressedY >= ClientUI.frameHeight - 72 && MouseHandler.lastPressedY < ClientUI.frameHeight - 40
                        && ClientCompanion.tabInterfaceIDs[5] != -1) {
                    if (ClientCompanion.tabId == 5) {
                        ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                    } else {
                        ClientCompanion.showTabComponents = true;
                    }
                    ClientCompanion.tabId = 5;
                    ClientCompanion.tabAreaAltered = true;

                }
                if (MouseHandler.lastPressedX >= ClientUI.frameWidth - 33 && MouseHandler.lastPressedX <= ClientUI.frameWidth
                        && MouseHandler.lastPressedY >= ClientUI.frameHeight - 72 && MouseHandler.lastPressedY < ClientUI.frameHeight - 40
                        && ClientCompanion.tabInterfaceIDs[6] != -1) {
                    if (ClientCompanion.tabId == 6) {
                        ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                    } else {
                        ClientCompanion.showTabComponents = true;
                    }
                    ClientCompanion.tabId = 6;
                    ClientCompanion.tabAreaAltered = true;

                }

                if (MouseHandler.lastPressedX >= ClientUI.frameWidth - 194 && MouseHandler.lastPressedX <= ClientUI.frameWidth - 163
                        && MouseHandler.lastPressedY >= ClientUI.frameHeight - 37 && MouseHandler.lastPressedY < ClientUI.frameHeight
                        && ClientCompanion.tabInterfaceIDs[8] != -1) {
                    if (ClientCompanion.tabId == 8) {
                        ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                    } else {
                        ClientCompanion.showTabComponents = true;
                    }
                    ClientCompanion.tabId = 8;
                    ClientCompanion.tabAreaAltered = true;

                }
                if (MouseHandler.lastPressedX >= ClientUI.frameWidth - 162 && MouseHandler.lastPressedX <= ClientUI.frameWidth - 131
                        && MouseHandler.lastPressedY >= ClientUI.frameHeight - 37 && MouseHandler.lastPressedY < ClientUI.frameHeight
                        && ClientCompanion.tabInterfaceIDs[9] != -1) {
                    if (ClientCompanion.tabId == 9) {
                        ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                    } else {
                        ClientCompanion.showTabComponents = true;
                    }
                    ClientCompanion.tabId = 9;
                    ClientCompanion.tabAreaAltered = true;

                }
                if (MouseHandler.lastPressedX >= ClientUI.frameWidth - 129 && MouseHandler.lastPressedX <= ClientUI.frameWidth - 98
                        && MouseHandler.lastPressedY >= ClientUI.frameHeight - 37 && MouseHandler.lastPressedY < ClientUI.frameHeight
                        && ClientCompanion.tabInterfaceIDs[10] != -1) {
                    if (ClientCompanion.tabId == 7) {
                        ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                    } else {
                        ClientCompanion.showTabComponents = true;
                    }
                    ClientCompanion.tabId = 7;
                    ClientCompanion.tabAreaAltered = true;

                }
                if (MouseHandler.lastPressedX >= ClientUI.frameWidth - 97 && MouseHandler.lastPressedX <= ClientUI.frameWidth - 66
                        && MouseHandler.lastPressedY >= ClientUI.frameHeight - 37 && MouseHandler.lastPressedY < ClientUI.frameHeight
                        && ClientCompanion.tabInterfaceIDs[11] != -1) {
                    if (ClientCompanion.tabId == 11) {
                        ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                    } else {
                        ClientCompanion.showTabComponents = true;
                    }
                    ClientCompanion.tabId = 11;
                    ClientCompanion.tabAreaAltered = true;

                }
                if (MouseHandler.lastPressedX >= ClientUI.frameWidth - 65 && MouseHandler.lastPressedX <= ClientUI.frameWidth - 34
                        && MouseHandler.lastPressedY >= ClientUI.frameHeight - 37 && MouseHandler.lastPressedY < ClientUI.frameHeight
                        && ClientCompanion.tabInterfaceIDs[12] != -1) {
                    if (ClientCompanion.tabId == 12) {
                        ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                    } else {
                        ClientCompanion.showTabComponents = true;
                    }
                    ClientCompanion.tabId = 12;
                    ClientCompanion.tabAreaAltered = true;

                }
                if (MouseHandler.lastPressedX >= ClientUI.frameWidth - 33 && MouseHandler.lastPressedX <= ClientUI.frameWidth
                        && MouseHandler.lastPressedY >= ClientUI.frameHeight - 37 && MouseHandler.lastPressedY < ClientUI.frameHeight
                        && ClientCompanion.tabInterfaceIDs[13] != -1) {
                    if (ClientCompanion.tabId == 13) {
                        ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                    } else {
                        ClientCompanion.showTabComponents = true;
                    }
                    ClientCompanion.tabId = 13;
                    ClientCompanion.tabAreaAltered = true;

                }
            } else {
                if (MouseHandler.y >= ClientUI.frameHeight - 37 && MouseHandler.y <= ClientUI.frameHeight) {
                    if (MouseHandler.x >= ClientUI.frameWidth - 417 && MouseHandler.x <= ClientUI.frameWidth - 386) {
                        if (ClientCompanion.tabId == 0) {
                            ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                        } else {
                            ClientCompanion.showTabComponents = true;
                        }
                        ClientCompanion.tabId = 0;
                        ClientCompanion.tabAreaAltered = true;
                    }
                    if (MouseHandler.x >= ClientUI.frameWidth - 385 && MouseHandler.x <= ClientUI.frameWidth - 354) {
                        if (ClientCompanion.tabId == 1) {
                            ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                        } else {
                            ClientCompanion.showTabComponents = true;
                        }
                        ClientCompanion.tabId = 1;
                        ClientCompanion.tabAreaAltered = true;
                    }
                    if (MouseHandler.x >= ClientUI.frameWidth - 353 && MouseHandler.x <= ClientUI.frameWidth - 322) {
                        if (ClientCompanion.tabId == 2) {
                            ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                        } else {
                            ClientCompanion.showTabComponents = true;
                        }
                        ClientCompanion.tabId = 2;
                        ClientCompanion.tabAreaAltered = true;
                    }
                    if (MouseHandler.x >= ClientUI.frameWidth - 321 && MouseHandler.x <= ClientUI.frameWidth - 290) {
                        if (ClientCompanion.tabId == 3) {
                            ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                        } else {
                            ClientCompanion.showTabComponents = true;
                        }
                        ClientCompanion.tabId = 3;
                        ClientCompanion.tabAreaAltered = true;
                    }
                    if (MouseHandler.x >= ClientUI.frameWidth - 289 && MouseHandler.x <= ClientUI.frameWidth - 258) {
                        if (ClientCompanion.tabId == 4) {
                            ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                        } else {
                            ClientCompanion.showTabComponents = true;
                        }
                        ClientCompanion.tabId = 4;
                        ClientCompanion.tabAreaAltered = true;
                    }
                    if (MouseHandler.x >= ClientUI.frameWidth - 257 && MouseHandler.x <= ClientUI.frameWidth - 226) {
                        if (ClientCompanion.tabId == 5) {
                            ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                        } else {
                            ClientCompanion.showTabComponents = true;
                        }
                        ClientCompanion.tabId = 5;
                        ClientCompanion.tabAreaAltered = true;
                    }
                    if (MouseHandler.x >= ClientUI.frameWidth - 225 && MouseHandler.x <= ClientUI.frameWidth - 194) {
                        if (ClientCompanion.tabId == 6) {
                            ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                        } else {
                            ClientCompanion.showTabComponents = true;
                        }
                        ClientCompanion.tabId = 6;
                        ClientCompanion.tabAreaAltered = true;
                    }
                    if (MouseHandler.x >= ClientUI.frameWidth - 193 && MouseHandler.x <= ClientUI.frameWidth - 163) {
                        if (ClientCompanion.tabId == 8) {
                            ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                        } else {
                            ClientCompanion.showTabComponents = true;
                        }
                        ClientCompanion.tabId = 8;
                        ClientCompanion.tabAreaAltered = true;
                    }
                    if (MouseHandler.x >= ClientUI.frameWidth - 162 && MouseHandler.x <= ClientUI.frameWidth - 131) {
                        if (ClientCompanion.tabId == 9) {
                            ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                        } else {
                            ClientCompanion.showTabComponents = true;
                        }
                        ClientCompanion.tabId = 9;
                        ClientCompanion.tabAreaAltered = true;
                    }
                    if (MouseHandler.x >= ClientUI.frameWidth - 130 && MouseHandler.x <= ClientUI.frameWidth - 99) {
                        if (ClientCompanion.tabId == 7) {
                            ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                        } else {
                            ClientCompanion.showTabComponents = true;
                        }
                        ClientCompanion.tabId = 7;
                        ClientCompanion.tabAreaAltered = true;
                    }
                    if (MouseHandler.x >= ClientUI.frameWidth - 98 && MouseHandler.x <= ClientUI.frameWidth - 67) {
                        if (ClientCompanion.tabId == 11) {
                            ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                        } else {
                            ClientCompanion.showTabComponents = true;
                        }
                        ClientCompanion.tabId = 11;
                        ClientCompanion.tabAreaAltered = true;
                    }
                    if (MouseHandler.x >= ClientUI.frameWidth - 66 && MouseHandler.x <= ClientUI.frameWidth - 45) {
                        if (ClientCompanion.tabId == 12) {
                            ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                        } else {
                            ClientCompanion.showTabComponents = true;
                        }
                        ClientCompanion.tabId = 12;
                        ClientCompanion.tabAreaAltered = true;
                    }
                    if (MouseHandler.x >= ClientUI.frameWidth - 31 && MouseHandler.x <= ClientUI.frameWidth) {
                        if (ClientCompanion.tabId == 13) {
                            ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                        } else {
                            ClientCompanion.showTabComponents = true;
                        }
                        ClientCompanion.tabId = 13;
                        ClientCompanion.tabAreaAltered = true;
                    }
                }
            }
        }
    }

    public static void markMinimap(int minimapOrientation, int minimapRotation, int minimapZoom, Sprite sprite, int x, int y) {
        if (sprite == null) {
            return;
        }
        int angle = minimapOrientation + minimapRotation & 0x7ff;
        int l = x * x + y * y;
        if (l > (ClientUI.frameMode == Client.ScreenMode.FIXED ? 6400 : 5200)) {
            return;
        }
        int sineAngle = Model.SINE[angle];
        int cosineAngle = Model.COSINE[angle];
        sineAngle = (sineAngle * 256) / (minimapZoom + 256);
        cosineAngle = (cosineAngle * 256) / (minimapZoom + 256);
        int spriteOffsetX = y * sineAngle + x * cosineAngle >> 16;
        int spriteOffsetY = y * cosineAngle - x * sineAngle >> 16;
        if (ClientUI.frameMode == Client.ScreenMode.FIXED) {
            sprite.drawSprite(((94 + spriteOffsetX) - sprite.maxWidth / 2) + 4 + 30,
                    83 - spriteOffsetY - sprite.maxHeight / 2 - 4 + 5);
        } else {
            sprite.drawSprite(((94 + spriteOffsetX) - sprite.maxWidth / 2) + 4 + 30 + (ClientUI.frameWidth - 212),
                    83 - spriteOffsetY - sprite.maxHeight / 2 - 4 + 5 - 2);
        }
    }

    public static boolean handleAction(Client client, int action, int button) {

        // World map orb
        if (action == MenuOpcodes.OPEN_WORLD_MAP) {
            client.playSound(2266);
            client.sendPacket(new ClickButton(156).create());
            return true;
        }

        // Spec orb
        if (action == MenuOpcodes.USE_SPECIAL_ATTACK) {
            client.playSound(2266);
            client.sendPacket(new ClickButton(155).create());
            return true;
        }

        // reset compass to north
        if (action == MenuOpcodes.SET_COMPASS_NORTH) {
            client.playSound(2266);
            setNorth(client);
            return true;
        }

        // Select quick prayers
        if (action == MenuOpcodes.SET_QUICK_PRAYERS && Configuration.enablePrayerEnergyWorldOrbs) {
            client.playSound(2266);
            client.sendPacket(new ClickButton(MenuOpcodes.SET_QUICK_PRAYERS).create());
            return true;
        }

        // Click logout tab
        if (action == MenuOpcodes.LOGOUT) {
            if (ClientCompanion.tabInterfaceIDs[10] != -1) {
                if (ClientCompanion.tabId == 10) {
                    ClientCompanion.showTabComponents = !ClientCompanion.showTabComponents;
                } else {
                    ClientCompanion.showTabComponents = true;
                }
                ClientCompanion.tabId = 10;
                ClientCompanion.tabAreaAltered = true;
            }
            return true;
        }

        // Toggle emojis menu
        if (action == MenuOpcodes.TOGGLE_EMOJI_MENU && Configuration.enableEmoticons) {
            Emojis.menuOpen = !Emojis.menuOpen;
        }

        // Select emoji from menu
        if (action == MenuOpcodes.SELECT_EMOJI && Configuration.enableEmoticons && Emojis.menuOpen && button != -1) {
            List<String> shortCodes = Emojis.Emoji.values()[button].getShortCodes();
            String shortCode = shortCodes.get(0);

            if (client.inputString.length() + shortCode.length() <= 120) {
                Emojis.menuOpen = false;
                client.inputString += (client.inputString.length() >= 1 && !client.inputString.endsWith(" ") ? " " : "") + shortCode + " ";
            } else {
                client.sendMessage("Your message is too long to add this emoji.");
            }
            return true;
        }
        return false;
    }

    public static void setNorth(Client client) {
        client.cameraX2 = 0;
        client.cameraY2 = 0;
        client.cameraRotation = 0;
        client.minimapOrientation = 0;
        client.minimapRotation = 0;
        client.minimapZoom = 0;
    }

    public static void handleChatBoxAction(Client client, int action) {
        if (action == 1003) {
            ChatBox.clanChatMode = 2;
            ChatBox.setUpdateChatbox(true);

            client.sendPacket(new ChatSettings(client.gameChatMode, ChatBox.publicChatMode, ChatBox.privateChatMode, ChatBox.clanChatMode, ChatBox.tradeMode, ChatBox.yellMode).create());
        }
        if (action == 1002) {
            ChatBox.clanChatMode = 1;
            ChatBox.setUpdateChatbox(true);

            client.sendPacket(new ChatSettings(client.gameChatMode, ChatBox.publicChatMode, ChatBox.privateChatMode, ChatBox.clanChatMode, ChatBox.tradeMode, ChatBox.yellMode).create());
        }
        if (action == 1001) {
            ChatBox.clanChatMode = 0;
            ChatBox.setUpdateChatbox(true);

            client.sendPacket(new ChatSettings(client.gameChatMode, ChatBox.publicChatMode, ChatBox.privateChatMode, ChatBox.clanChatMode, ChatBox.tradeMode, ChatBox.yellMode).create());
        }
        if (action == 1000) {
            ChatBox.channelButtonPos1 = 4;
            client.chatTypeView = 11;
            ChatBox.setUpdateChatbox(true);
        }

        if (action == 999) {
            ChatBox.channelButtonPos1 = 0;
            client.chatTypeView = 0;
            ChatBox.setUpdateChatbox(true);
        }
        if (action == 998) {
            ChatBox.channelButtonPos1 = 1;
            client.chatTypeView = 5;
            ChatBox.setUpdateChatbox(true);
        }

        // public chat "hide" option
        if (action == 997) {
            ChatBox.publicChatMode = 3;
            ChatBox.setUpdateChatbox(true);

            client.sendPacket(new ChatSettings(client.gameChatMode, ChatBox.publicChatMode, ChatBox.privateChatMode, ChatBox.clanChatMode, ChatBox.tradeMode, ChatBox.yellMode).create());
        }

        // public chat "off" option
        if (action == 996) {
            ChatBox.publicChatMode = 2;
            ChatBox.setUpdateChatbox(true);

            client.sendPacket(new ChatSettings(client.gameChatMode, ChatBox.publicChatMode, ChatBox.privateChatMode, ChatBox.clanChatMode, ChatBox.tradeMode, ChatBox.yellMode).create());
        }

        // public chat "friends" option
        if (action == 995) {
            ChatBox.publicChatMode = 1;
            ChatBox.setUpdateChatbox(true);

            client.sendPacket(new ChatSettings(client.gameChatMode, ChatBox.publicChatMode, ChatBox.privateChatMode, ChatBox.clanChatMode, ChatBox.tradeMode, ChatBox.yellMode).create());
        }

        // public chat "on" option
        if (action == 994) {
            ChatBox.publicChatMode = 0;
            ChatBox.setUpdateChatbox(true);

            client.sendPacket(new ChatSettings(client.gameChatMode, ChatBox.publicChatMode, ChatBox.privateChatMode, ChatBox.clanChatMode, ChatBox.tradeMode, ChatBox.yellMode).create());
        }

        // public chat main click
        if (action == 993) {
            ChatBox.channelButtonPos1 = 2;
            client.chatTypeView = 1;
            ChatBox.setUpdateChatbox(true);
        }

        // private chat "off" option
        if (action == 992) {
            ChatBox.privateChatMode = 2;
            ChatBox.setUpdateChatbox(true);

            client.sendPacket(new ChatSettings(client.gameChatMode, ChatBox.publicChatMode, ChatBox.privateChatMode, ChatBox.clanChatMode, ChatBox.tradeMode, ChatBox.yellMode).create());
        }

        // private chat "friends" option
        if (action == 991) {
            ChatBox.privateChatMode = 1;
            ChatBox.setUpdateChatbox(true);

            client.sendPacket(new ChatSettings(client.gameChatMode, ChatBox.publicChatMode, ChatBox.privateChatMode, ChatBox.clanChatMode, ChatBox.tradeMode, ChatBox.yellMode).create());
        }

        // private chat "on" option
        if (action == 990) {
            ChatBox.privateChatMode = 0;
            ChatBox.setUpdateChatbox(true);

            client.sendPacket(new ChatSettings(client.gameChatMode, ChatBox.publicChatMode, ChatBox.privateChatMode, ChatBox.clanChatMode, ChatBox.tradeMode, ChatBox.yellMode).create());
        }

        // private chat main click
        if (action == 989) {
            ChatBox.channelButtonPos1 = 3;
            client.chatTypeView = 2;
            ChatBox.setUpdateChatbox(true);
        }

        // trade message privacy option "off" option
        if (action == 987) {
            ChatBox.tradeMode = 2;
            ChatBox.setUpdateChatbox(true);

            client.sendPacket(new ChatSettings(client.gameChatMode, ChatBox.publicChatMode, ChatBox.privateChatMode, ChatBox.clanChatMode, ChatBox.tradeMode, ChatBox.yellMode).create());
        }

        // trade message privacy option "friends" option
        if (action == 986) {
            ChatBox.tradeMode = 1;
            ChatBox.setUpdateChatbox(true);

            client.sendPacket(new ChatSettings(client.gameChatMode, ChatBox.publicChatMode, ChatBox.privateChatMode, ChatBox.clanChatMode, ChatBox.tradeMode, ChatBox.yellMode).create());
        }

        // trade message privacy option "on" option
        if (action == 985) {
            ChatBox.tradeMode = 0;
            ChatBox.setUpdateChatbox(true);

            client.sendPacket(new ChatSettings(client.gameChatMode, ChatBox.publicChatMode, ChatBox.privateChatMode, ChatBox.clanChatMode, ChatBox.tradeMode, ChatBox.yellMode).create());
        }

        // trade message privacy option main click
        if (action == 984) {
            ChatBox.channelButtonPos1 = 5;
            client.chatTypeView = 3;
            ChatBox.setUpdateChatbox(true);
        }

        // yell message privacy option "off" option
        if (action == 976) {
            ChatBox.yellMode = 2;
            ChatBox.setUpdateChatbox(true);

            client.sendPacket(new ChatSettings(client.gameChatMode, ChatBox.publicChatMode, ChatBox.privateChatMode, ChatBox.clanChatMode, ChatBox.tradeMode, ChatBox.yellMode).create());
        }

        // yell message privacy option "on" option
        if (action == 975) {
            ChatBox.yellMode = 0;
            ChatBox.setUpdateChatbox(true);

            client.sendPacket(new ChatSettings(client.gameChatMode, ChatBox.publicChatMode, ChatBox.privateChatMode, ChatBox.clanChatMode, ChatBox.tradeMode, ChatBox.yellMode).create());
        }

        // yell message main click
        if (action == 974) { // was 980
            ChatBox.channelButtonPos1 = 6;
            client.chatTypeView = 4;
            ChatBox.setUpdateChatbox(true);
        }
    }

    public static void drawSideIcons(Client client) {
        int xOffset = ChatBox.getDrawOffset(ClientUI.frameMode == Client.ScreenMode.FIXED, 0, ClientUI.frameWidth, 247);
        int yOffset = ChatBox.getDrawOffset(ClientUI.frameMode == Client.ScreenMode.FIXED, 0, ClientUI.frameHeight, 336);
        if (!client.displaySideStonesStacked()) {
            for (int i = 0; i < ClientCompanion.SIDE_ICONS_TAB.length; i++) {
                if (ClientCompanion.tabInterfaceIDs[ClientCompanion.SIDE_ICONS_TAB[i]] != -1) {
                    if (ClientCompanion.SIDE_ICONS_ID[i] != -1) {
                        Sprite sprite = SpriteLoader.getSprite(SpriteCompanion.sideIcons, ClientCompanion.SIDE_ICONS_ID[i]);
                        sprite.drawSprite(ClientCompanion.SIDE_ICONS_X[i] + xOffset, ClientCompanion.SIDE_ICONS_Y[i] + yOffset);
                    }
                }
            }
        } else if (ClientUI.frameWidth < 1000) {
            int[] iconId = {0, 1, 2, 3, 4, 5, 6, -1, 8, 9, 7, 11, 12, 13};
            int[] iconX = {219, 189, 156, 126, 94, 62, 30, 219, 189, 156, 124, 92, 59, 28};
            int[] iconY = {67, 69, 67, 69, 72, 72, 69, 32, 29, 29, 32, 30, 33, 31, 32};
            for (int i = 0; i < ClientCompanion.SIDE_ICONS_TAB.length; i++) {
                if (ClientCompanion.tabInterfaceIDs[ClientCompanion.SIDE_ICONS_TAB[i]] != -1) {
                    if (iconId[i] != -1) {
                        Sprite sprite = SpriteLoader.getSprite(SpriteCompanion.sideIcons, iconId[i]);
                        sprite.drawSprite(ClientUI.frameWidth - iconX[i], ClientUI.frameHeight - iconY[i]);
                    }
                }
            }
        } else {
            int[] iconId = {0, 1, 2, 3, 4, 5, 6, -1, 8, 9, 7, 11, 12, 13};
            int[] iconX = {50, 80, 114, 143, 176, 208, 240, 242, 273, 306, 338, 370, 404, 433};
            int[] iconY = {30, 32, 30, 32, 34, 34, 32, 32, 29, 29, 32, 31, 32, 32, 32};
            for (int i = 0; i < ClientCompanion.SIDE_ICONS_TAB.length; i++) {
                if (ClientCompanion.tabInterfaceIDs[ClientCompanion.SIDE_ICONS_TAB[i]] != -1) {
                    if (iconId[i] != -1) {
                        Sprite sprite = SpriteLoader.getSprite(SpriteCompanion.sideIcons, iconId[i]);
                        sprite.drawSprite(ClientUI.frameWidth - 461 + iconX[i], ClientUI.frameHeight - iconY[i]);
                    }
                }
            }
        }
    }

    public static void drawRedStones(Client client) {

        final int[] redStonesX = {6, 44, 77, 110, 143, 176, 209, 6, 44, 77, 110, 143, 176, 209},
                redStonesY = {0, 0, 0, 0, 0, 0, 0, 298, 298, 298, 298, 298, 298, 298},
                redStonesId = {35, 39, 39, 39, 39, 39, 36, 37, 39, 39, 39, 39, 39, 38};

        int xOffset = ChatBox.getDrawOffset(ClientUI.frameMode == Client.ScreenMode.FIXED, 0, ClientUI.frameWidth, 247);
        int yOffset = ChatBox.getDrawOffset(ClientUI.frameMode == Client.ScreenMode.FIXED, 0, ClientUI.frameHeight, 336);
        if (!client.displaySideStonesStacked()) {
            if (ClientCompanion.tabInterfaceIDs[ClientCompanion.tabId] != -1 && ClientCompanion.tabId != 15) {
                SpriteLoader.getSprite(redStonesId[ClientCompanion.tabId]).drawSprite(redStonesX[ClientCompanion.tabId] + xOffset, redStonesY[ClientCompanion.tabId] + yOffset);
            }
        } else if (ClientUI.frameWidth < 1000) {
            int[] stoneX = {226, 194, 162, 130, 99, 65, 34, 219, 195, 161, 130, 98, 65, 33};
            int[] stoneY = {73, 73, 73, 73, 73, 73, 73, -1, 37, 37, 37, 37, 37, 37, 37};
            if (ClientCompanion.tabInterfaceIDs[ClientCompanion.tabId] != -1 && ClientCompanion.tabId != 10 && client.displayTabComponents()) {
                if (ClientCompanion.tabId == 7) {
                    SpriteLoader.getSprite(39).drawSprite(ClientUI.frameWidth - 130, ClientUI.frameHeight - 37);
                }
                SpriteLoader.getSprite(39).drawSprite(ClientUI.frameWidth - stoneX[ClientCompanion.tabId], ClientUI.frameHeight - stoneY[ClientCompanion.tabId]);
            }
        } else {
            int[] stoneX = {417, 385, 353, 321, 289, 256, 224, 129, 193, 161, 130, 98, 65, 33};
            if (ClientCompanion.tabInterfaceIDs[ClientCompanion.tabId] != -1 && ClientCompanion.tabId != 10 && client.displayTabComponents()) {
                SpriteLoader.getSprite(39).drawSprite(ClientUI.frameWidth - stoneX[ClientCompanion.tabId], ClientUI.frameHeight - 37);
            }
        }
    }

    public static void drawTabArea(Client client) {

        final boolean fixed = ClientUI.frameMode == Client.ScreenMode.FIXED;
        final int xOffset = ChatBox.getDrawOffset(fixed, 0, ClientUI.frameWidth, 241);
        final int yOffset = ChatBox.getDrawOffset(fixed, 0, ClientUI.frameHeight, 336);

        if (fixed) {
            Client.tabImageProducer.initDrawingArea();
        }
        Rasterizer3D.scanOffsets = ClientCompanion.anIntArray1181;
        if (fixed) {
            SpriteLoader.getSprite(21).drawSprite(0, 0);
        } else if (!ClientCompanion.stackSideStones) {
            Rasterizer2D.drawTransparentBox(ClientUI.frameWidth - 217, ClientUI.frameHeight - 304, 195, 270, 0x3E3529,
                    ClientCompanion.transparentTabArea ? 80 : 256);
            SpriteLoader.getSprite(47).drawSprite(xOffset, yOffset);
        } else {
            if (ClientUI.frameWidth >= 1000) {
                if (client.displayTabComponents()) {
                    Rasterizer2D.drawTransparentBox(ClientUI.frameWidth - 197, ClientUI.frameHeight - 304, 197, 265, 0x3E3529,
                            ClientCompanion.transparentTabArea ? 80 : 256);
                    SpriteLoader.getSprite(50).drawSprite(ClientUI.frameWidth - 204, ClientUI.frameHeight - 311);
                }
                for (int x = ClientUI.frameWidth - 417, y = ClientUI.frameHeight - 37, index = 0; x <= ClientUI.frameWidth - 30
                        && index < 13; x += 32, index++) {
                    SpriteLoader.getSprite(46).drawSprite(x, y);
                }
            } else {
                if (client.displayTabComponents()) {
                    Rasterizer2D.drawTransparentBox(ClientUI.frameWidth - 197, ClientUI.frameHeight - 341, 195, 265, 0x3E3529,
                            ClientCompanion.transparentTabArea ? 80 : 256);
                    SpriteLoader.getSprite(50).drawSprite(ClientUI.frameWidth - 204, ClientUI.frameHeight - 348);
                }
                for (int x = ClientUI.frameWidth - 226, y = ClientUI.frameHeight - 73, index = 0; x <= ClientUI.frameWidth - 32
                        && index < 7; x += 32, index++) {
                    SpriteLoader.getSprite(46).drawSprite(x, y);
                }
                for (int x = ClientUI.frameWidth - 226, y = ClientUI.frameHeight - 37, index = 0; x <= ClientUI.frameWidth - 32
                        && index < 7; x += 32, index++) {
                    SpriteLoader.getSprite(46).drawSprite(x, y);
                }
            }
        }
        if (client.overlayInterfaceId == -1) {
            drawRedStones(client);
            drawSideIcons(client);
        }
        if (client.displayTabComponents()) {
            int x = ChatBox.getDrawOffset(fixed, 31, ClientUI.frameWidth, 215);
            int y = ChatBox.getDrawOffset(fixed, 37, ClientUI.frameHeight, 299);
            if (client.displaySideStonesStacked()) {
                x = ClientUI.frameWidth - 197;
                y = ChatBox.getDrawOffset(ClientUI.frameWidth >= 1000, ClientUI.frameHeight - 303, ClientUI.frameHeight, 340);
            }
            try {
                if (client.overlayInterfaceId != -1) {
                    client.drawInterface(0, x, Widget.interfaceCache[client.overlayInterfaceId], y, fixed ? 516 : 0, fixed ? 168 : 0);
                } else if (ClientCompanion.tabInterfaceIDs[ClientCompanion.tabId] != -1) {
                    client.drawInterface(0, x, Widget.interfaceCache[ClientCompanion.tabInterfaceIDs[ClientCompanion.tabId]], y, fixed ? 516 : 0, fixed ? 168 : 0);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (client.menuOpen) {
            client.drawMenu(fixed ? 516 : 0, fixed ? 168 : 0);
        }
        if (fixed) {
            Client.tabImageProducer.drawGraphics(client.canvas.getGraphics(), 516, 168);
            ClientCompanion.gameScreenImageProducer.initDrawingArea();
        }
        Rasterizer3D.scanOffsets = ClientCompanion.anIntArray1182;
    }

    public static void drawMinimap(Client client) {
        if (ClientUI.frameMode == Client.ScreenMode.FIXED) {
            client.minimapImageProducer.initDrawingArea();
        }
        if (minimapState == 2) {
            if (ClientUI.frameMode == Client.ScreenMode.FIXED) {
                SpriteLoader.getSprite(45).drawSprite(50, 8);
                SpriteLoader.getSprite(19).drawSprite(0, 0);
            } else {
                SpriteLoader.getSprite(44).drawSprite(ClientUI.frameWidth - 181, 0);
                SpriteLoader.getSprite(45).drawSprite(ClientUI.frameWidth - 158, 7);
            }
            if (client.displaySideStonesStacked()) {
                if (MouseHandler.x >= ClientUI.frameWidth - 26 && MouseHandler.x <= ClientUI.frameWidth - 1 && MouseHandler.y >= 2
                        && MouseHandler.y <= 24 || ClientCompanion.tabId == 15) {
                    SpriteLoader.getSprite(27).drawSprite(ClientUI.frameWidth - 25, 2);
                } else {
                    SpriteLoader.getSprite(27).drawAdvancedSprite(ClientUI.frameWidth - 25, 2, 165);
                }
            }
            drawAllOrbs(client);
            SpriteCompanion.compass.rotate(33, client.minimapOrientation, client.anIntArray1057, 256, client.anIntArray968, 25,
                    (ClientUI.frameMode == Client.ScreenMode.FIXED ? 4 : 5), (ChatBox.getDrawOffset(ClientUI.frameMode == Client.ScreenMode.FIXED, 29, ClientUI.frameWidth, 176)),
                    33, 25);
            if (client.menuOpen) {
                client.drawMenu(ClientUI.frameMode == Client.ScreenMode.FIXED ? 516 : 0, 0);
            }
            if (ClientUI.frameMode == Client.ScreenMode.FIXED) {
                client.minimapImageProducer.initDrawingArea();
            }
            return;
        }
        int angle = client.minimapOrientation + client.minimapRotation & 0x7ff;
        int centreX = 48 + Client.localPlayer.x / 32;
        int centreY = 464 - Client.localPlayer.y / 32;
        SpriteCompanion.minimapImage.rotate(151, angle, client.minimapLineWidth, 256 + client.minimapZoom, client.minimapLeft,
                centreY, (ClientUI.frameMode == Client.ScreenMode.FIXED ? 9 : 7), (ChatBox.getDrawOffset(ClientUI.frameMode == Client.ScreenMode.FIXED, 54, ClientUI.frameWidth, 158)),
                146, centreX);
        for (int icon = 0; icon < mapIconCount; icon++) {
            int mapX = (mapIconXs[icon] * 4 + 2) - Client.localPlayer.x / 32;
            int mapY = (mapIconYs[icon] * 4 + 2) - Client.localPlayer.y / 32;
            markMinimap(client.minimapOrientation, client.minimapRotation, client.minimapZoom, SpriteLoader.getSprite(SpriteCompanion.mapIcons, icon), mapX, mapY);
        }
        drawCustomMinimapIcon(client, 3, 3093, 3498);
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                NodeDeque class19 = client.groundItems[Client.plane][x][y];
                if (class19 != null) {
                    int mapX = (x * 4 + 2) - Client.localPlayer.x / 32;
                    int mapY = (y * 4 + 2) - Client.localPlayer.y / 32;
                    markMinimap(client.minimapOrientation, client.minimapRotation, client.minimapZoom, SpriteCompanion.mapDotItem, mapX, mapY);
                }
            }
        }
        for (int n = 0; n < client.npcCount; n++) {
            Npc npc = client.npcs[client.npcIndices[n]];
            if (npc != null && npc.isVisible()) {
                NpcDefinition entityDef = npc.desc;
                if (entityDef.transforms != null) {
                    entityDef = entityDef.transform();
                }
                if (entityDef != null && entityDef.drawMinimapDot && entityDef.clickable) {
                    int mapX = npc.x / 32 - Client.localPlayer.x / 32;
                    int mapY = npc.y / 32 - Client.localPlayer.y / 32;
                    markMinimap(client.minimapOrientation, client.minimapRotation, client.minimapZoom, SpriteCompanion.mapDotNPC, mapX, mapY);
                }
            }
        }
        for (int p = 0; p < client.Players_count; p++) {
            Player player = client.players[client.Players_indices[p]];
            if (player != null && player.isVisible() && !player.isHidden) {
                long nameHash = StringUtils.encodeBase37(player.name);

                int mapX = player.x / 32 - Client.localPlayer.x / 32;
                int mapY = player.y / 32 - Client.localPlayer.y / 32;
                boolean friend = false;
                boolean clanMember = client.clanMembers.contains(nameHash);

                for (int f = 0; f < PlayerRelations.friendsCount; f++) {
                    if (nameHash != PlayerRelations.friendsListAsLongs[f] || PlayerRelations.friendsNodeIDs[f] == 0) {
                        continue;
                    }
                    friend = true;
                    break;
                }
                boolean team = false;
                if (player.team != 0 && Client.localPlayer.team == player.team) {
                    team = true;
                }
                if (friend) {
                    markMinimap(client.minimapOrientation, client.minimapRotation, client.minimapZoom, SpriteCompanion.mapDotFriend, mapX, mapY);
                } else if (clanMember) {
                    markMinimap(client.minimapOrientation, client.minimapRotation, client.minimapZoom, SpriteCompanion.mapDotClan, mapX, mapY);
                } else if (team) {
                    markMinimap(client.minimapOrientation, client.minimapRotation, client.minimapZoom, SpriteCompanion.mapDotTeam, mapX, mapY);
                } else {
                    markMinimap(client.minimapOrientation, client.minimapRotation, client.minimapZoom, SpriteCompanion.mapDotPlayer, mapX, mapY);
                }
            }
        }
        client.mobOverlayRenderer.markMinimap(client);

        if (client.destinationX != 0) {
            int mapX = (client.destinationX * 4 + 2) - Client.localPlayer.x / 32;
            int mapY = (client.destinationY * 4 + 2) - Client.localPlayer.y / 32;
            markMinimap(client.minimapOrientation, client.minimapRotation, client.minimapZoom, SpriteCompanion.mapFlag, mapX, mapY);
        }
        if(Client.localPlayer == null || !Client.localPlayer.isHidden)
            Rasterizer2D.drawBox((ChatBox.getDrawOffset(ClientUI.frameMode == Client.ScreenMode.FIXED, 127, ClientUI.frameWidth, 88)),
                    (ClientUI.frameMode == Client.ScreenMode.FIXED
                            ? 83 : 80), 3, 3, 0xffffff);
        if (ClientUI.frameMode == Client.ScreenMode.FIXED) {
            SpriteLoader.getSprite(19).drawSprite(0, 0);
        } else {
            SpriteLoader.getSprite(44).drawSprite(ClientUI.frameWidth - 181, 0);
        }
        SpriteCompanion.compass.rotate(33, client.minimapOrientation, client.anIntArray1057, 256, client.anIntArray968, 25,
                (ClientUI.frameMode == Client.ScreenMode.FIXED ? 4 : 5), (ChatBox.getDrawOffset(ClientUI.frameMode == Client.ScreenMode.FIXED, 29, ClientUI.frameWidth, 176)),
                33, 25);
        if (client.displaySideStonesStacked()) {
            if (MouseHandler.x >= ClientUI.frameWidth - 26 && MouseHandler.x <= ClientUI.frameWidth - 1 && MouseHandler.y >= 2
                    && MouseHandler.y <= 24 || ClientCompanion.tabId == 10) {
                SpriteLoader.getSprite(27).drawSprite(ClientUI.frameWidth - 25, 2);
            } else {
                SpriteLoader.getSprite(27).drawAdvancedSprite(ClientUI.frameWidth - 25, 2, 165);
            }
        }
        drawAllOrbs(client);

        if (client.menuOpen) {
            client.drawMenu(ClientUI.frameMode == Client.ScreenMode.FIXED ? 516 : 0, 0);
        }
        if (ClientUI.frameMode == Client.ScreenMode.FIXED) {
            ClientCompanion.gameScreenImageProducer.initDrawingArea();
        }
    }

    public static void drawAllOrbs(Client client) {

        boolean fixed = ClientUI.frameMode == Client.ScreenMode.FIXED;
        boolean specOrb = Configuration.enableSpecOrb;
        int xOffset = ChatBox.getDrawOffset(fixed, 0, ClientUI.frameWidth, 217);

        if (specOrb) {
            drawSpecialOrb(client, xOffset);
        }

        if (!Configuration.enablePrayerEnergyWorldOrbs) {
            return;
        }

        drawHpOrb(client, xOffset);
        drawPrayerOrb(client, xOffset, specOrb ? 0 : 11);
        drawRunOrb(client, specOrb ? xOffset : xOffset + 13, specOrb ? 0 : 15);

        Sprite bg = SpriteLoader.getSprite(42);
        /* World map */
        bg.drawSprite(ChatBox.getDrawOffset(fixed, 196, ClientUI.frameWidth, 34), fixed ? 117 : 139);
        SpriteLoader.getSprite(GameFrameInput.hoveringWorldOrb ? 52 : 51).drawSprite(ChatBox.getDrawOffset(fixed, 200, ClientUI.frameWidth, 30), fixed ? 121 : 143);
        /* Xp counter */
        int offSprite = Configuration.xpCounterOpen ? 53 : 22;
        int onSprite = Configuration.xpCounterOpen ? 54 : 23;
        SpriteLoader.getSprite(GameFrameInput.hoveringXPCounterOrb ? onSprite : offSprite).drawSprite(ChatBox.getDrawOffset(fixed, 0, ClientUI.frameWidth, 216), 21);

        /* Notifications */
        bg.drawSprite(ChatBox.getDrawOffset(fixed, 167, ClientUI.frameWidth, 63), fixed ? 135 : 157);
        SpriteLoader.getSprite(GameFrameInput.hoveringNewsOrb ? 853 : 852).drawSprite(ChatBox.getDrawOffset(fixed, 172, ClientUI.frameWidth, 58), fixed ? 140 : 162);
        if (client.newsflash && !GameFrameInput.hoveringNewsOrb) {
            int alpha = 128 + (int) (128 * Math.sin(Client.tick / 15.0));
            SpriteLoader.getSprite(853).drawTransparentSprite(ChatBox.getDrawOffset(fixed, 172, ClientUI.frameWidth, 58), fixed ? 140 : 162, alpha);
        }
    }

    private static void drawSpecialOrb(Client client, int xOffset) {
        Sprite image = SpriteLoader.getSprite(GameFrameInput.hoveringSpecialOrb ? 8 : 7);
        Sprite fill = SpriteLoader.getSprite(client.specialEnabled == 0 ? 5 : 6);
        Sprite sword = SpriteLoader.getSprite(55);
        double percent = client.specialAttack / (double) 100;
        image.drawSprite(32 + xOffset, 132);
        fill.drawSprite(59 + xOffset, 136);
        SpriteLoader.getSprite(14).myHeight = (int) (26 * (1 - percent));
        SpriteLoader.getSprite(14).drawSprite(59 + xOffset, 136);
        sword.drawSprite(59 + xOffset, 136);
        client.smallText.drawCenteredText(client.specialAttack + "", 47 + xOffset, 158, getOrbTextColor((int) (percent * 100)), true);
    }

    public static int getOrbTextColor(int statusInt) {
        if (statusInt >= 75)
            return 0x00FF00;
        else if (statusInt >= 50)
            return 0xFFFF00;
        else if (statusInt >= 25)
            return 0xFF981F;
        else
            return 0xFF0000;
    }

    public static void drawCustomMinimapIcon(Client client, int sprite, int x, int y) {
        markMinimap(client.minimapOrientation, client.minimapRotation, client.minimapZoom, SpriteLoader.getSprite(sprite), x, y);
        markMinimap(client.minimapOrientation, client.minimapRotation, client.minimapZoom, SpriteLoader.getSprite(SpriteCompanion.mapFunctions, 55), ((3700 - client.baseX) * 4 + 2) - Client.localPlayer.x / 32,
                ((2955 - client.baseY) * 4 + 2) - Client.localPlayer.y / 32);
    }

    public static void processMenuClick(Client client) {
        if (client.itemDragType != Widget.ITEM_DRAG_TYPE_NONE)
            return;
        int j = MouseHandler.lastButton;
        if (client.spellSelected == 1 && MouseHandler.lastPressedX >= 516 && MouseHandler.lastPressedY >= 160 && MouseHandler.lastPressedX <= 765
                && MouseHandler.lastPressedY <= 205)
            j = 0;
        if (client.menuOpen) {
            if (j != 1) {
                int k = MouseHandler.x;
                int j1 = MouseHandler.y;
                if (menuScreenArea == 0) {
                    k -= 4;
                    j1 -= 4;
                }
                if (menuScreenArea == 1) {
                    k -= 519;
                    j1 -= 168;
                }
                if (menuScreenArea == 2) {
                    k -= 17;
                    j1 -= 338;
                }
                if (menuScreenArea == 3) {
                    k -= 519;
                }
                if (k < menuOffsetX - 10 || k > menuOffsetX + menuWidth + 10 || j1 < menuOffsetY - 10
                        || j1 > menuOffsetY + menuHeight + 10) {
                    client.menuOpen = false;
                    if (menuScreenArea == 2)
                        ChatBox.setUpdateChatbox(true);
                }
            }
            if (j == 1) {
                int l = menuOffsetX;
                int k1 = menuOffsetY;
                int i2 = menuWidth;
                int k2 = MouseHandler.lastPressedX;
                int l2 = MouseHandler.lastPressedY;
                switch (menuScreenArea) {
                    case 0:
                        k2 -= 4;
                        l2 -= 4;
                        break;
                    case 1:
                        k2 -= 519;
                        l2 -= 168;
                        break;
                    case 2:
                        k2 -= 5;
                        l2 -= 338;
                        break;
                    case 3:
                        k2 -= 519;
                        break;
                }
                int i3 = -1;
                for (int j3 = 0; j3 < client.menuOptionsCount; j3++) {
                    int k3 = k1 + 31 + (client.menuOptionsCount - 1 - j3) * 15;
                    if (k2 > l && k2 < l + i2 && l2 > k3 - 13 && l2 < k3 + 3)
                        i3 = j3;
                }
                if (i3 != -1)
                    client.processMenuActions(i3);
                client.menuOpen = false;
                if (menuScreenArea == 2) {
                    ChatBox.setUpdateChatbox(true);
                }
            }
        } else {
            // Process dragging and non menu action based clicks here, as drags and clicks handled in buildInterfaceMenu are not designed to be used that way and will result in bugs
            client.processInterfaceClick();
            if (j == 1 && client.menuOptionsCount > 0) {
                int opcode = client.menuOpcodes[client.menuOptionsCount - 1];
                int arg1 = client.menuArguments1[client.menuOptionsCount - 1];
                int arg2 = client.menuArguments2[client.menuOptionsCount - 1];
                if (opcode == MenuOpcodes.ITEM_CONTAINER_ACTION_1
                        || opcode == MenuOpcodes.ITEM_CONTAINER_ACTION_2
                        || opcode == MenuOpcodes.ITEM_CONTAINER_ACTION_3
                        || opcode == MenuOpcodes.ITEM_CONTAINER_ACTION_4
                        || opcode == MenuOpcodes.ITEM_CONTAINER_ACTION_5
                        || opcode == MenuOpcodes.ITEM_ACTION_1
                        || opcode == MenuOpcodes.ITEM_ACTION_2
                        || opcode == MenuOpcodes.ITEM_ACTION_3
                        || opcode == MenuOpcodes.ITEM_ACTION_4
                        || opcode == MenuOpcodes.ITEM_ACTION_5
                        || opcode == MenuOpcodes.USE_ITEM
                        || opcode == MenuOpcodes.EXAMINE_ITEM
                        || opcode == MenuOpcodes.RELEASE_PLACEHOLDER
                        || opcode == MenuOpcodes.BANK_MODIFY_AMOUNT
                        // Bank tab rearranging
                        || (opcode == 647 && Bank.isBankTabButton(arg2, true) && arg1 == 0)) {

                    Widget widget = Widget.interfaceCache[arg2];
                    if (widget.allowSwapItems || widget.replaceItems) {
                        client.mouseOutOfDragZone = false;
                        client.dragItemDelay = 0;
                        client.anInt1084 = arg2;
                        client.anInt1085 = arg1;
                        client.itemDragType = Widget.ITEM_DRAG_TYPE_REGULAR;
                        client.previousClickMouseX = MouseHandler.lastPressedX;
                        client.previousClickMouseY = MouseHandler.lastPressedY;
                        if (Widget.interfaceCache[arg2].parent == ClientCompanion.openInterfaceId)
                            client.itemDragType = Widget.ITEM_DRAG_TYPE_OPEN_INTERFACE;
                        if (Widget.interfaceCache[arg2].parent == client.backDialogueId)
                            client.itemDragType = Widget.ITEM_DRAG_TYPE_BACK_DIALOGUE;
                        return;
                    }
                }
            }
            if (j == 1 && (client.mouseButtonSetting == 1 || client.menuHasAddFriend(client.menuOptionsCount - 1)) && client.menuOptionsCount > 2)
                j = 2;
            if (j == 1 && client.menuOptionsCount > 0)
                client.processMenuActions(client.menuOptionsCount - 1);
            if (j == 2 && client.menuOptionsCount > 0)
                determineMenuSize(client);
            client.processMainScreenClick();
            processTabClick(client);
            processChatModeClick(client);
        }
    }

    public static void processChatModeClick(Client client) {
        final int yOffset = ChatBox.getDrawOffset(ClientUI.frameMode == Client.ScreenMode.FIXED, 0, ClientUI.frameHeight, 503);
        int buttons = 8;
        int xPos = 5;
        int yPos = 480;
        int buttonWidth = 56;
        int buttonHeight = 22;
        int spacing = 8;

        boolean hovered = false;
        for (int i = 0; i < buttons; i++) {
            if (MouseHandler.x >= xPos && MouseHandler.x < xPos + buttonWidth && MouseHandler.y >= yOffset + yPos && MouseHandler.y < yOffset + yPos + buttonHeight) {
                ChatBox.channelButtonPos2 = i;
                ChatBox.setUpdateChatbox(true);
                hovered = true;
                if (i < ClientCompanion.chatTypeViews.length
                        && MouseHandler.lastButton == 1
                        && MouseHandler.lastPressedX >= xPos && MouseHandler.lastPressedX < xPos + buttonWidth && MouseHandler.lastPressedY >= yOffset + yPos
                        && MouseHandler.lastPressedY <= yOffset + yPos + buttonHeight) {
                    if (ClientUI.frameMode != Client.ScreenMode.FIXED) {
                        if (client.setChannel != i) {
                            ChatBox.channelButtonPos1 = i;
                            client.chatTypeView = ClientCompanion.chatTypeViews[i];
                            ChatBox.setUpdateChatbox(true);
                            client.setChannel = i;
                        } else {
                            ChatBox.showChatComponents = !ChatBox.showChatComponents;
                        }
                    } else {
                        ChatBox.channelButtonPos1 = i;
                        client.chatTypeView = ClientCompanion.chatTypeViews[i];
                        ChatBox.setUpdateChatbox(true);
                        client.setChannel = i;
                    }
                }
                break;
            }
            xPos += buttonWidth + spacing;
        }
        if (!hovered) {
            ChatBox.channelButtonPos2 = -1;
        }
    }

    public static void determineMenuSize(Client client) {
        int boxLength = client.newBoldFont.getTextWidth("Choose option");
        for (int row = 0; row < client.menuOptionsCount; row++) {
            int actionLength = client.newBoldFont.getTextWidth(client.menuActions[row]);
            if (actionLength > boxLength)
                boxLength = actionLength;
        }
        boxLength += 8;
        int offset = 15 * client.menuOptionsCount + 21;

        if (MouseHandler.lastPressedX > 0 && MouseHandler.lastPressedY > 0 && MouseHandler.lastPressedX < ClientUI.frameWidth
                && MouseHandler.lastPressedY < ClientUI.frameHeight) {
            int xClick = MouseHandler.lastPressedX - boxLength / 2;
            if (xClick + boxLength > ClientUI.frameWidth - 4) {
                xClick = ClientUI.frameWidth - 4 - boxLength;
            }
            if (xClick < 0) {
                xClick = 0;
            }
            int yClick = MouseHandler.lastPressedY;
            if (yClick + offset > ClientUI.frameHeight - 6) {
                yClick = ClientUI.frameHeight - 6 - offset;
            }
            if (yClick < 0) {
                yClick = 0;
            }
            client.menuOpen = true;

            if (ClientCompanion.removeShiftDropOnMenuOpen && client.menuArguments2[client.menuOptionsCount - 1] == 3214) {
                ClientCompanion.removeShiftDropOnMenuOpen = false;
                GameFrameInput.processRightClick(client);
            }

            menuOffsetX = xClick;
            menuOffsetY = yClick;
            menuWidth = boxLength;
            menuHeight = 15 * client.menuOptionsCount + 22;
        }
    }

    public static void refreshMinimap(int minimapOrientation, int minimapRotation, int minimapZoom, Sprite sprite, int j, int k) {
        int l = k * k + j * j;
        if (l > 4225 && l < 0x15f90) {
            int i1 = minimapOrientation + minimapRotation & 0x7ff;
            int j1 = Model.SINE[i1];
            int k1 = Model.COSINE[i1];
        } else {
            markMinimap(minimapOrientation, minimapRotation, minimapZoom, sprite, k, j);
        }
    }

    public static int getTabAreaHeight(Client client) {
        if (client.displaySideStonesStacked()) {
            int height = SpriteLoader.getSprite(46).myHeight;
            if (ClientUI.frameWidth < 1000) { // Second stack of stones
                height += SpriteLoader.getSprite(46).myHeight;
            }
            if (client.displayTabComponents()) {
                height += SpriteLoader.getSprite(50).myHeight;
            }
            return height;
        } else {
            return SpriteLoader.getSprite(47).myHeight;
        }
    }

    static int getXDrawOffset(boolean condition, int trueOffsetX, int falseOffsetX) {
        return ChatBox.getDrawOffset(condition, trueOffsetX, ClientUI.frameWidth, falseOffsetX);
    }

    public static void drawHpOrb(Client client, int xOffset) {
        int poisonType = client.poisonStatus;
        int hover = poisonType == 0 ? 8 : 7;
        //Sprite bg = SpriteLoader.getSprite(GameFrame.hpHover ? hover : 7); // Re-add to add HP hover
        Sprite bg = SpriteLoader.getSprite(7);
        int id = 0; // poisonType == 0
        if (poisonType == 1)
            id = 1252; // TODO hp orb
        else if (poisonType == 2)
            id = 1251;

        Sprite fg = SpriteLoader.getSprite(id);
        bg.drawSprite(xOffset, 41);
        fg.drawSprite(27 + xOffset, 45);
        int level = client.currentLevels[3];
        int max = client.maximumLevels[3];
        double percent = level / (double) max;
        SpriteLoader.getSprite(14).myHeight = (int) (26 * (1 - percent));
        Client.drawSprite(14, 27 + xOffset, 45);
        if (percent <= .25) {
            SpriteLoader.getSprite(9).drawSprite1(33 + xOffset, 51, 200 + (int) (50 * Math.sin(Client.tick / 7.0)));
        } else {
            Client.drawSprite(9, 33 + xOffset, 51);
        }
        client.smallText.drawCenteredText("" + level, 15 + xOffset, 67, getOrbTextColor((int) (percent * 100)), true);
    }

    public static void drawPrayerOrb(Client client, int xOffset, int yOffset) {
        Sprite bg = SpriteLoader.getSprite(GameFrameInput.hoveringPrayerOrb ? 8 : 7);
        Sprite fg = SpriteLoader.getSprite(GameFrameInput.clickedPrayerOrb ? 2 : 1);
        bg.drawSprite(xOffset, 75 + yOffset);
        fg.drawSprite(27 + xOffset, 79 + yOffset);
        int level = client.currentLevels[5];
        int max = client.maximumLevels[5];
        double percent = level / (double) max;
        SpriteLoader.getSprite(14).myHeight = (int) (26 * (1 - percent));
        Client.drawSprite(14, 27 + xOffset, 79 + yOffset);
        if (percent <= .25) {
            SpriteLoader.getSprite(10).drawSprite1(30 + xOffset, 82 + yOffset, 200 + (int) (50 * Math.sin(Client.tick / 7.0)));
        } else {
            Client.drawSprite(10, 30 + xOffset, 82 + yOffset);
        }
        client.smallText.drawCenteredText(level + "", 15 + xOffset, 101 + yOffset, getOrbTextColor((int) (percent * 100)), true);
    }

    public static void drawRunOrb(Client client, int xOffset, int yOffset) {
        Sprite bg = SpriteLoader.getSprite(GameFrameInput.hoveringEnergyOrb ? 8 : 7);
        Sprite fg = SpriteLoader.getSprite(client.settings[429] == 1 ? 4 : 3);
        bg.drawSprite(10 + xOffset, 107 + yOffset);
        fg.drawSprite(37 + xOffset, 111 + yOffset);
        int level = client.runEnergy;
        double percent = level / (double) 100;
        SpriteLoader.getSprite(14).myHeight = (int) (26 * (1 - percent));
        Client.drawSprite(14, 37 + xOffset, 111 + yOffset);
        if (percent <= .25) {
            SpriteLoader.getSprite(client.settings[429] == 1 ? 12 : 11).drawSprite1(43 + xOffset, 115 + yOffset,
                    200 + (int) (50 * Math.sin(Client.tick / 7.0)));
        } else {
            Client.drawSprite(client.settings[429] == 1 ? 12 : 11, 43 + xOffset, 115 + yOffset);
        }
        // cacheSprite[336].drawSprite(20 + xOffset, 125 + yOffset);
        client.smallText.drawCenteredText(Integer.toString(client.runEnergy), 25 + xOffset, 133 + yOffset, getOrbTextColor((int) (percent * 100)),
                true);
    }
}
