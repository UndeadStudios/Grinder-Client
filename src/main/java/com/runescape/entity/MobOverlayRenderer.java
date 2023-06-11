package com.runescape.entity;

import com.grinder.Configuration;
import com.grinder.client.ClientCompanion;
import com.grinder.client.util.Log;
import com.grinder.model.ChatBox;
import com.grinder.model.Emojis;
import com.grinder.model.GameFrame;
import com.runescape.cache.graphics.sprite.SpriteCompanion;
import com.runescape.Client;
import com.runescape.cache.def.NpcDefinition;
import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.cache.graphics.sprite.SpriteLoader;
import com.runescape.draw.Rasterizer2D;

import java.util.Objects;

import static com.runescape.Client.localPlayer;

public class MobOverlayRenderer {
    
    private final int maxTextAboveHeadStrings;
    private final int[] textAboveHeadSpriteX;
    private final int[] textAboveHeadSpriteY;
    private final int[] textAboveHeadHeightOffset;
    private final int[] textAboveHeadWidth;
    private final int[] textAboveHeadColour;
    private final int[] textAboveHeaderEffect;
    private final int[] textAboveHeadVisibleCycle;
    private final String[] textAboveHeadMessage;

    private int hintIconDrawType;
    private int hintIconPlayerId;
    private int hintIconNpcId;
    private int hintIconX;
    private int hintIconY;
    private int hintIconLocationArrowHeight;
    private int hintIconLocationArrowRelX;
    private int hintIconLocationArrowRelY;
    
    public MobOverlayRenderer(){
        maxTextAboveHeadStrings = 50;
        textAboveHeadSpriteX = new int[maxTextAboveHeadStrings];
        textAboveHeadSpriteY = new int[maxTextAboveHeadStrings];
        textAboveHeadHeightOffset = new int[maxTextAboveHeadStrings];
        textAboveHeadWidth = new int[maxTextAboveHeadStrings];
        textAboveHeadColour = new int[maxTextAboveHeadStrings];
        textAboveHeaderEffect = new int[maxTextAboveHeadStrings];
        textAboveHeadVisibleCycle = new int[maxTextAboveHeadStrings];
        textAboveHeadMessage = new String[maxTextAboveHeadStrings];
    }
    
    public void onLogin(){
        hintIconDrawType = 0;
    }

    public int handleTextAboveHead(Client client, int textAboveHeadCount, int j, Object obj) {
        
        final int Players_count = client.Players_count;
        final int publicChatMode = ChatBox.publicChatMode;
        
        if (((Mob) (obj)).spokenText != null && (j >= Players_count || publicChatMode == 0 || publicChatMode == 3
                || publicChatMode == 1 && client.isFriendOrSelf(((Player) obj).name))) {
            client.setDrawOffset(((Mob) (obj)), ((Mob) (obj)).defaultHeight);
            final int drawX = client.spriteDrawX;
            final int drawY = client.spriteDrawY;
            if (drawX > -1 && textAboveHeadCount < maxTextAboveHeadStrings) {
                textAboveHeadWidth[textAboveHeadCount] = client.boldText.getTextLength(((Mob) (obj)).spokenText) / 2;
                textAboveHeadHeightOffset[textAboveHeadCount] = client.boldText.verticalSpace;
                textAboveHeadSpriteX[textAboveHeadCount] = drawX;
                textAboveHeadSpriteY[textAboveHeadCount] = drawY;
                textAboveHeadColour[textAboveHeadCount] = ((Mob) (obj)).textColour;
                textAboveHeaderEffect[textAboveHeadCount] = ((Mob) (obj)).textEffect;
                textAboveHeadVisibleCycle[textAboveHeadCount] = ((Mob) (obj)).textCycle;
                textAboveHeadMessage[textAboveHeadCount] = ((Mob) (obj)).spokenText;
                textAboveHeadCount++;
                if (client.textAboveHeadEffectState == 1) {
                    if (((Mob) (obj)).textEffect >= 1 && ((Mob) (obj)).textEffect <= 3) {
                        textAboveHeadHeightOffset[textAboveHeadCount] += 10;
                        textAboveHeadSpriteY[textAboveHeadCount] += 5;
                    }
                    if (((Mob) (obj)).textEffect == 4)
                        textAboveHeadWidth[textAboveHeadCount] = 60;
                    if (((Mob) (obj)).textEffect == 5)
                        textAboveHeadHeightOffset[textAboveHeadCount] += 5;
                }
            }
        }
        return textAboveHeadCount;
    }

    public void drawTextAboveHead(Client client, int textAboveHeadCount) {
        for (int defaultText = 0; defaultText < textAboveHeadCount; defaultText++) {
            int k1 = textAboveHeadSpriteX[defaultText];
            int l1 = textAboveHeadSpriteY[defaultText];
            int j2 = textAboveHeadWidth[defaultText];
            int k2 = textAboveHeadHeightOffset[defaultText];
            boolean flag = true;
            while (flag) {
                flag = false;
                for (int l2 = 0; l2 < defaultText; l2++)
                    if (l1 + 2 > textAboveHeadSpriteY[l2] - textAboveHeadHeightOffset[l2] && l1 - k2 < textAboveHeadSpriteY[l2] + 2
                            && k1 - j2 < textAboveHeadSpriteX[l2] + textAboveHeadWidth[l2]
                            && k1 + j2 > textAboveHeadSpriteX[l2] - textAboveHeadWidth[l2]
                            && textAboveHeadSpriteY[l2] - textAboveHeadHeightOffset[l2] < l1) {
                        l1 = textAboveHeadSpriteY[l2] - textAboveHeadHeightOffset[l2];
                        flag = true;
                    }

            }
            final int spriteDrawX = client.spriteDrawX = textAboveHeadSpriteX[defaultText];
            final int spriteDrawY = client.spriteDrawY = textAboveHeadSpriteY[defaultText] = l1;
            String s = textAboveHeadMessage[defaultText];
            if (Configuration.enableEmoticons) {
                s = Emojis.Emoji.handleSyntax(s);
            }
            String formatted = s;
            int colorCode = 0;
            if (formatted.startsWith("Yellow:")) {
                formatted = formatted.substring(7);
            } else if (formatted.startsWith("Red:")) {
                colorCode = 1;
                formatted = formatted.substring(4);
            } else if (formatted.startsWith("Green:")) {
                colorCode = 2;
                formatted = formatted.substring(6);
            } else if (formatted.startsWith("Cyan:")) {
                colorCode = 3;
                formatted = formatted.substring(5);
            } else if (formatted.startsWith("Purple:")) {
                colorCode = 4;
                formatted = formatted.substring(7);
            } else if (formatted.startsWith("White:")) {
                colorCode = 5;
                formatted = formatted.substring(6);
            } else if (formatted.startsWith("Flash1:")) {
                colorCode = 6;
                formatted = formatted.substring(7);
            } else if (formatted.startsWith("Flash2:")) {
                colorCode = 7;
                formatted = formatted.substring(7);
            } else if (formatted.startsWith("Flash3:")) {
                colorCode = 8;
                formatted = formatted.substring(7);
            } else if (formatted.startsWith("Glow1:")) {
                colorCode = 9;
                formatted = formatted.substring(6);
            } else if (formatted.startsWith("Glow2:")) {
                colorCode = 10;
                formatted = formatted.substring(6);
            } else if (formatted.startsWith("Glow3:")) {
                colorCode = 11;
                formatted = formatted.substring(6);
            }
            int effectCode = 0;
            if (formatted.startsWith("wave:")) {
                effectCode = 1;
                formatted = formatted.substring(5);
            } else if (formatted.startsWith("wave2:")) {
                effectCode = 2;
                formatted = formatted.substring(6);
            } else if (formatted.startsWith("shake:")) {
                effectCode = 3;
                formatted = formatted.substring(6);
            } else if (formatted.startsWith("scroll:")) {
                effectCode = 4;
                formatted = formatted.substring(7);
            } else if (formatted.startsWith("slide:")) {
                effectCode = 5;
                formatted = formatted.substring(6);
            }
            s = formatted;
            s = s.substring(0, 1).toUpperCase() + s.substring(1);
            int color = colorCode;
            int effect = effectCode;
            if (color > 0)
                textAboveHeadColour[defaultText] = color;
            if(effect > 0) {
                client.textAboveHeadEffectState = 1;
                textAboveHeaderEffect[defaultText] = effect;
            }
            if (client.textAboveHeadEffectState == 1) {
                final int viewportDrawCount = client.viewportDrawCount;
                int i3 = 0xffff00;
                if (textAboveHeadColour[defaultText] < 6)
                    i3 = ClientCompanion.TEXT_EFFECT_COLOURS[textAboveHeadColour[defaultText]];
                if (textAboveHeadColour[defaultText] == 6)
                    i3 = viewportDrawCount % 20 >= 10 ? 0xffff00 : 0xff0000;
                if (textAboveHeadColour[defaultText] == 7)
                    i3 = viewportDrawCount % 20 >= 10 ? 65535 : 255;
                if (textAboveHeadColour[defaultText] == 8)
                    i3 = viewportDrawCount % 20 >= 10 ? 0x80ff80 : 45056;
                if (textAboveHeadColour[defaultText] == 9) {
                    int j3 = 150 - textAboveHeadVisibleCycle[defaultText];
                    if (j3 < 50)
                        i3 = 0xff0000 + 1280 * j3;
                    else if (j3 < 100)
                        i3 = 0xffff00 - 0x50000 * (j3 - 50);
                    else if (j3 < 150)
                        i3 = 65280 + 5 * (j3 - 100);
                }
                if (textAboveHeadColour[defaultText] == 10) {
                    int k3 = 150 - textAboveHeadVisibleCycle[defaultText];
                    if (k3 < 50)
                        i3 = 0xff0000 + 5 * k3;
                    else if (k3 < 100)
                        i3 = 0xff00ff - 0x50000 * (k3 - 50);
                    else if (k3 < 150)
                        i3 = (255 + 0x50000 * (k3 - 100)) - 5 * (k3 - 100);
                }
                if (textAboveHeadColour[defaultText] == 11) {
                    int l3 = 150 - textAboveHeadVisibleCycle[defaultText];
                    if (l3 < 50)
                        i3 = 0xffffff - 0x50005 * l3;
                    else if (l3 < 100)
                        i3 = 65280 + 0x50005 * (l3 - 50);
                    else if (l3 < 150)
                        i3 = 0xffffff - 0x50000 * (l3 - 100);
                }
                if (textAboveHeaderEffect[defaultText] == 0) {
                    client.newBoldFont.drawCenteredString(formatted, spriteDrawX, spriteDrawY, i3, 0);
                    /*boldText.drawText(0, s, spriteDrawY + 1, spriteDrawX);
                    boldText.drawText(i3, s, spriteDrawY, spriteDrawX);*/
                }
                if (textAboveHeaderEffect[defaultText] == 1) {
                    // TODO: EMOJIS
                    client.boldText.wave(0, s, spriteDrawX, viewportDrawCount, spriteDrawY + 1);
                    client.boldText.wave(i3, s, spriteDrawX, viewportDrawCount, spriteDrawY);
                }
                if (textAboveHeaderEffect[defaultText] == 2) {
                    // TODO: EMOJIS
                    client.boldText.wave2(client.spriteDrawX, s, viewportDrawCount, spriteDrawY + 1, 0);
                    client.boldText.wave2(client.spriteDrawX, s, viewportDrawCount, spriteDrawY, i3);
                }
                if (textAboveHeaderEffect[defaultText] == 3) {
                    // TODO: EMOJIS
                    client.boldText.shake(150 - textAboveHeadVisibleCycle[defaultText], s, viewportDrawCount, spriteDrawY + 1, spriteDrawX, 0);
                    client.boldText.shake(150 - textAboveHeadVisibleCycle[defaultText], s, viewportDrawCount, spriteDrawY, spriteDrawX, i3);
                }
                if (textAboveHeaderEffect[defaultText] == 4) {
                    int i4 =  client.newBoldFont.getTextWidth(formatted);
                    //int i4 = boldText.method384(s);
                    int k4 = ((150 - textAboveHeadVisibleCycle[defaultText]) * (i4 + 100)) / 150;
                    Rasterizer2D.Rasterizer2D_setClip(client.spriteDrawX - 50, 334, spriteDrawX + 50, 0);
                    client.newBoldFont.drawBasicString(formatted, (client.spriteDrawX + 50) - k4, spriteDrawY, i3, 0);
                    /*boldText.render(0, s, spriteDrawY + 1, (client.spriteDrawX + 50) - k4);
                    boldText.render(i3, s, spriteDrawY, (client.spriteDrawX + 50) - k4);*/
                    Rasterizer2D.Rasterizer2D_resetClip();
                }
                if (textAboveHeaderEffect[defaultText] == 5) {
                    int j4 = 150 - textAboveHeadVisibleCycle[defaultText];
                    int l4 = 0;
                    if (j4 < 25)
                        l4 = j4 - 25;
                    else if (j4 > 125)
                        l4 = j4 - 125;
                    Rasterizer2D.Rasterizer2D_setClip(0, spriteDrawY + 5, 512, spriteDrawY -  client.boldText.verticalSpace - 1);
                    client.newBoldFont.drawCenteredString(formatted, spriteDrawX, spriteDrawY + l4, i3, 0);
                    Rasterizer2D.Rasterizer2D_resetClip();
                }
            } else {
                client.newBoldFont.drawCenteredString(formatted, spriteDrawX, spriteDrawY, 0xffff00, 0);
            }
        }
    }

    public void drawHitMarks(Client client, Mob entity) {
        if (!Configuration.hitmarks554) {
            draw554Hitmarks(client, entity);
        } else {
            for (int j2 = 0; j2 < 4; j2++) {
                if (entity.hitsLoopCycle[j2] > Client.tick) {
                    client.setDrawOffset(entity, entity.defaultHeight / 2);
                    if (client.spriteDrawX > -1) {
                        if (j2 == 0 && entity.hitDamages[j2] > 99)
                            entity.hitMarkTypes[j2] = 3;
                        else if (j2 == 1 && entity.hitDamages[j2] > 99)
                            entity.hitMarkTypes[j2] = 3;
                        else if (j2 == 2 && entity.hitDamages[j2] > 99)
                            entity.hitMarkTypes[j2] = 3;
                        else if (j2 == 3 && entity.hitDamages[j2] > 99)
                            entity.hitMarkTypes[j2] = 3;
                        if (j2 == 1) {
                            client.spriteDrawY -= 20;
                        }
                        if (j2 == 2) {
                            client.spriteDrawX -= (entity.hitDamages[j2] > 99 ? 30 : 20);
                            client.spriteDrawY -= 10;
                        }
                        if (j2 == 3) {
                            client.spriteDrawX += (entity.hitDamages[j2] > 99 ? 30 : 20);
                            client.spriteDrawY -= 10;
                        }
                        if (entity.hitMarkTypes[j2] == 3) {
                            client.spriteDrawX -= 8;
                        }
                        SpriteLoader.getSprite(ClientCompanion.HITMARKS_562[entity.hitMarkTypes[j2]])
                                .draw24BitSprite(client.spriteDrawX - 12, client.spriteDrawY - 12);
                        client.smallText.drawText(0xffffff, String.valueOf(entity.hitDamages[j2]),
                                client.spriteDrawY + 3,
                                (entity.hitMarkTypes[j2] == 3 ? client.spriteDrawX + 7 : client.spriteDrawX - 1));
                    }
                }
            }
        }
    }

    private void draw554Hitmarks(Client client, Object obj) {
        for (int j1 = 0; j1 < 4; j1++) {
            if (((Mob) (obj)).hitsLoopCycle[j1] > Client.tick) {
                client.setDrawOffset(((Mob) (obj)), ((Mob) (obj)).defaultHeight / 2);
                if (client.spriteDrawX > -1) {
                    if (j1 == 1)
                        client.spriteDrawY -= 20;
                    if (j1 == 2) {
                        client.spriteDrawX -= 15;
                        client.spriteDrawY -= 10;
                    }
                    if (j1 == 3) {
                        client.spriteDrawX += 15;
                        client.spriteDrawY -= 10;
                    }
                    SpriteCompanion.hitMarks[((Mob) (obj)).hitMarkTypes[j1]].drawSprite(client.spriteDrawX - 12, client.spriteDrawY - 12);

                    client.smallText.drawText(0,
                            Configuration.tenXHp ? String.valueOf(((Mob) (obj)).hitDamages[j1] * 10)
                                    : String.valueOf(((Mob) (obj)).hitDamages[j1]),
                            client.spriteDrawY + 4, client.spriteDrawX);

                    client.smallText.drawText(0xffffff,
                            Configuration.tenXHp ? String.valueOf(((Mob) (obj)).hitDamages[j1] * 10)
                                    : String.valueOf(((Mob) (obj)).hitDamages[j1]),
                            client.spriteDrawY + 3, client.spriteDrawX - 1);
                }
            }
        }
    }

    public void drawNPCHeadIcons(Client client, int npcIndex, Object entity) {
        Npc npc = ((Npc) entity);
        if (npc.getHeadIcon() >= 0 && npc.getHeadIcon() < SpriteCompanion.headIcons.length) {
            client.setDrawOffset(((Mob) (entity)), ((Mob) (entity)).defaultHeight + 15);
            if (client.spriteDrawX > -1)
                SpriteLoader.getSprite(SpriteCompanion.headIcons, npc.getHeadIcon()).drawSprite(client.spriteDrawX - 12, client.spriteDrawY - 30);
        }
        if (hintIconDrawType == 1 && hintIconNpcId == npcIndex && Client.tick % 20 < 10) {
            client.setDrawOffset(((Mob) (entity)), ((Mob) (entity)).defaultHeight + 15);
            if (client.spriteDrawX > -1)
                SpriteLoader.getSprite(SpriteCompanion.headIconsHint, 0).drawSprite(client.spriteDrawX - 12, client.spriteDrawY - 28);
        }
    }
    
    public void drawHeadIcon(Client client) {
        if (hintIconDrawType != 2)
            return;
        
        final int x = (hintIconX - client.baseX << 7) + hintIconLocationArrowRelX;
        final int y = (hintIconY - client.baseY << 7) + hintIconLocationArrowRelY;

        client.calcEntityScreenPos(x, hintIconLocationArrowHeight * 2, y);
        if (client.spriteDrawX > -1 && Client.tick % 20 < 10) {
            SpriteLoader.getSprite(SpriteCompanion.headIconsHint, 0)
                    .drawSprite(client.spriteDrawX - 12, client.spriteDrawY - 28);
        }
    }

    public void drawTextAboveNPCHead(Client client, Npc entity, NpcDefinition npcDefinition, int offset) {
        if (Configuration.namesAboveHeads) {
            drawSmallTextAboveHead(client, entity, npcDefinition.name, offset);
            offset += 20;
        }
        if (entity.debugMessages != null) {
            if (localPlayer.isDeveloper()) {
                for (String debugMessage : entity.debugMessages) {
                    drawTextAboveHead(client, entity, debugMessage, offset);
                    offset += 35;
                }
            }
        }
    }

    private void drawSmallTextAboveHead(Client client, Mob mob, String text, int offset) {
        client.setDrawOffset(mob, mob.defaultHeight + offset);
        client.smallText.drawText(0x0099FF, text, client.spriteDrawY - 5, client.spriteDrawX);
    }

    private void drawTextAboveHead(Client client, Mob mob, String text, int offset) {
        client.setDrawOffset(mob, mob.defaultHeight + offset);
        client.regularText.drawText(0x0099FF, text, client.spriteDrawY - 5, client.spriteDrawX);
    }
    public void drawPlayerHeadIcons(Client client, int totalEntityCount, Mob entity) {

        int overHeadDrawOffset = 0;
        int l = 30;

        final Player player = (Player) entity;

        if(player.isHidden)
            return;

        if (player.headIcon >= 0) {
            client.setDrawOffset(player, player.defaultHeight + 15);
            if (client.spriteDrawX > -1) {
                if (player.skullIcon < 2) {
                    SpriteLoader.getSprite(SpriteCompanion.skullIcons, player.skullIcon).drawSprite(client.spriteDrawX - 12, client.spriteDrawY - l);
                    l += 25;
                    if (Configuration.hpAboveHeads && Configuration.namesAboveHeads) overHeadDrawOffset -= 25;
                    else if (Configuration.namesAboveHeads) overHeadDrawOffset -= 23;
                    else if (Configuration.hpAboveHeads) overHeadDrawOffset -= 33;
                }
                if (player.headIcon < 13) {
                    SpriteLoader.getSprite(SpriteCompanion.headIcons, player.headIcon).drawSprite(client.spriteDrawX - 12, client.spriteDrawY - l - 3);
                    l += 21;
                    overHeadDrawOffset -= 5;
                    if (Configuration.hpAboveHeads && Configuration.namesAboveHeads)
                        overHeadDrawOffset -= 25;
                    else if (Configuration.namesAboveHeads) overHeadDrawOffset -= 26;
                    else if (Configuration.hpAboveHeads) overHeadDrawOffset -= 33;
                }
            }
        }
        if (totalEntityCount >= 0 && hintIconDrawType == 10 && hintIconPlayerId == client.Players_indices[totalEntityCount]) {
            overHeadDrawOffset = drawHeadHintIcons(client, overHeadDrawOffset, l, player);
        }
        if (Configuration.hpAboveHeads) {
            if(Configuration.namesAboveHeads)
                drawHitPointAboveHead(client, player, overHeadDrawOffset, 29);
            else
                drawHitPointAboveHead(client, player, overHeadDrawOffset, 5);
        }
        if (Configuration.namesAboveHeads) {
            drawPlayerNamesAboveHead(client, overHeadDrawOffset, player);
        }
    }


    private int drawHeadHintIcons(Client client, int heightOffset, int l, Player player) {
        client.setDrawOffset(player, player.defaultHeight + 15);
        if (client.spriteDrawX > -1) {
            l += 13;
            heightOffset -= 17;
            SpriteLoader.getSprite(SpriteCompanion.headIconsHint, player.hintIcon).drawSprite(client.spriteDrawX - 12, client.spriteDrawY - l);
        }
        return heightOffset;
    }

    private void drawHitPointAboveHead(Client client, Mob mob, int heightOffset, int i) {
        client.newSmallFont.drawCenteredString(mob.currentHealth + "/" + mob.maxHealth,
                client.spriteDrawX, client.spriteDrawY - i + heightOffset, 0xff0000, 0);
    }

    private void drawPlayerNamesAboveHead(Client client, int text_over_head_offset, Player player) {
        client.setDrawOffset(player, player.defaultHeight + 15);
        int col = 0xff0000;
        if (Objects.equals(player.clanName, localPlayer.clanName))
            col = 0x00ff00;
        client.smallText.drawText(col, player.name, client.spriteDrawY - 15 + text_over_head_offset, client.spriteDrawX);
    }

    public void drawHitPointsBar(Client client, Object o) {
        if(o instanceof Mob){

            final Mob mob = (Mob) o;

            if(mob.loopCycleStatus > Client.tick){
                final int mobHealth = mob.currentHealth;
                final int mobMaxHealth = Math.max(1, mob.maxHealth);
                final int tileOffset = mob.defaultHeight + 15;

                try {
                    client.setDrawOffset(mob, tileOffset);
                    if (client.spriteDrawX > -1) {
                        int current = Math.min(30, (mobHealth * 30) / mobMaxHealth);
                        int hpPercent = Math.min(56, (mobHealth * 56) / mobMaxHealth);
                        if (!Configuration.hpBar554) {
                            int multiplier = !(mob instanceof Player) && mobMaxHealth >= 250 ? 4 : 1;
                            int max = 30 * multiplier;
                            current *= multiplier;
                            Rasterizer2D.drawBox(client.spriteDrawX - max / 2, client.spriteDrawY - 3, current + 1, 5, 65280);
                            Rasterizer2D.drawBox((client.spriteDrawX - max / 2) + current, client.spriteDrawY - 3, max - current, 5, 0xff0000);
                        } else {
                            SpriteLoader.getSprite(41).drawSprite(client.spriteDrawX - 28, client.spriteDrawY - 3);
                            SpriteCompanion.hpBar = new Sprite(SpriteCompanion.hp, hpPercent, 7);
                            SpriteCompanion.hpBar.drawSprite(client.spriteDrawX - 28, client.spriteDrawY - 3);
                        }
                    }
                } catch (Exception e) {
                    Log.error("Error in drawHitPointsBar for "+mob);
                }
            }
        }
    }

    public void markMinimap(Client client) {
        if (hintIconDrawType != 0 && Client.tick % 20 < 10) {
            if (hintIconDrawType == 1 && hintIconNpcId >= 0 && hintIconNpcId < client.npcs.length) {
                Npc npc = client.npcs[hintIconNpcId];
                if (npc != null) {
                    int mapX = npc.x / 32 - localPlayer.x / 32;
                    int mapY = npc.y / 32 - localPlayer.y / 32;
                    GameFrame.refreshMinimap(client.minimapOrientation, client.minimapRotation, client.minimapZoom, SpriteCompanion.mapMarker, mapY, mapX);
                }
            }
            if (hintIconDrawType == 2) {
                int mapX = ((hintIconX - client.baseX) * 4 + 2) - localPlayer.x / 32;
                int mapY = ((hintIconY - client.baseY) * 4 + 2) - localPlayer.y / 32;
                GameFrame.refreshMinimap(client.minimapOrientation, client.minimapRotation, client.minimapZoom, SpriteCompanion.mapMarker, mapY, mapX);
            }
            if (hintIconDrawType == 10 && hintIconPlayerId >= 0 && hintIconPlayerId < client.players.length) {
                Player player = client.players[hintIconPlayerId];
                if (player != null) {
                    int mapX = player.x / 32 - localPlayer.x / 32;
                    int mapY = player.y / 32 - localPlayer.y / 32;
                    GameFrame.refreshMinimap(client.minimapOrientation, client.minimapRotation, client.minimapZoom, SpriteCompanion.mapMarker, mapY, mapX);
                }
            }
        }
    }

    public void setHintIconDrawType(int hintIconDrawType) {
        this.hintIconDrawType = hintIconDrawType;
    }

    public void setHintIconPlayerId(int hintIconPlayerId) {
        this.hintIconPlayerId = hintIconPlayerId;
    }

    public void setHintIconNpcId(int hintIconNpcId) {
        this.hintIconNpcId = hintIconNpcId;
    }

    public void setHintIconX(int hintIconX) {
        this.hintIconX = hintIconX;
    }

    public void setHintIconY(int hintIconY) {
        this.hintIconY = hintIconY;
    }

    public void setHintIconLocationArrowHeight(int hintIconLocationArrowHeight) {
        this.hintIconLocationArrowHeight = hintIconLocationArrowHeight;
    }

    public void setHintIconLocationArrowRelX(int hintIconLocationArrowRelX) {
        this.hintIconLocationArrowRelX = hintIconLocationArrowRelX;
    }

    public void setHintIconLocationArrowRelY(int hintIconLocationArrowRelY) {
        this.hintIconLocationArrowRelY = hintIconLocationArrowRelY;
    }
}
