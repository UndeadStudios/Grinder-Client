package com.grinder.model;

import com.grinder.Configuration;
import com.grinder.client.ClientCompanion;
import com.grinder.client.ClientUtil;
import com.grinder.client.GameShell;
import com.grinder.net.ResourceProvider;
import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.audio.Audio;
import com.runescape.cache.anim.Animation;
import com.runescape.cache.def.NpcDefinition;
import com.runescape.cache.graphics.GameFont;
import com.runescape.cache.graphics.IndexedImage;
import com.runescape.cache.graphics.PetSystem;
import com.runescape.cache.graphics.sprite.SpriteCompanion;
import com.runescape.cache.graphics.sprite.SpriteLoader;
import com.runescape.cache.graphics.widget.Widget;
import com.runescape.clock.Clock;
import com.runescape.clock.MilliClock;
import com.runescape.clock.NanoClock;
import com.runescape.clock.Time;
import com.runescape.draw.Rasterizer2D;
import com.runescape.entity.PlayerRelations;
import com.runescape.entity.model.IdentityKit;
import com.runescape.entity.model.Model;
import com.runescape.input.KeyHandler;
import com.runescape.input.MouseHandler;
import com.runescape.sign.SignLink;
import com.runescape.util.MiscUtils;
import com.runescape.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;

/**
 * @version 1.0
 * @since 12/12/2019
 */
public class LoginScreen {

    public static final long FLASH_DURATION = 400L;
    public static final long FLASH_INTERVAL = 800L;

    public static String firstLoginMessage;
    public static String secondLoginMessage;
    public static int loginScreenCursorPos;
    public static String loadingText;
    public static int loadingPercentage;
    public static int loginScreenState;
    public static int loginFailures;
    static boolean rememberUsername = true;
    static boolean rememberPassword = false;
    private static boolean rememberUsernameHover;
    private static boolean rememberPasswordHover;
    private static boolean forgottenPasswordHover;
    private static Thread renderThread;
    static boolean stopRenderThread;
    public static long lastFlash = Time.currentTimeMillis();
    private static boolean connecting = false;

    public static void drawLoadingText(Client client, int loadAmount, String s) {
        loadingPercentage = loadAmount;
        loadingText = s;
        if (Client.loginBoxImageProducer == null) {
            setupLoginScreen(client);
        }
        Flames.flameBuffer.initDrawingArea();
        if (renderThread == null) {
            renderThread = new Thread(() -> {

                Clock clock;
                try {
                    clock = new NanoClock();
                } catch (Throwable throwable) {
                    clock = new MilliClock();
                    throwable.printStackTrace();
                }

                while (!stopRenderThread) {

                    clock.wait(GameShell.preferredDelay, GameShell.minimumDelay);

                    client.checkCanvasSize();
                    if (client.canvasInvalidated)
                        client.replaceCanvas();

                    int loadBarX = ClientUI.canvasWidth / 2 - 152;
                    int loadBarY = ClientUI.canvasHeight / 2 - 18;

                    drawBackground();
                    Rasterizer2D.drawBoxOutline(loadBarX, loadBarY, 303, 33, 0x8c1111);
                    Rasterizer2D.drawBox(loadBarX + 2, loadBarY + 2, loadingPercentage * 3, 30, 0x8c1111);
                    Rasterizer2D.drawBoxOutline(loadBarX + 1, loadBarY + 1, 301, 31, 0);
                    Rasterizer2D.drawBox(loadingPercentage * 3 + loadBarX + 2, loadBarY + 2, 300 - loadingPercentage * 3, 30, 0);

                    if (client.boldText != null)
                        client.boldText.drawText(0xffffff, loadingText, loadBarY + 2 + 19, ClientUI.canvasWidth / 2);

                    Flames.flameBuffer.drawGraphics(client.canvas.getGraphics(), 0, 0);

                    try {
                        SwingUtilities.invokeAndWait(client::repaint);
                    } catch (InterruptedException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

            });
            renderThread.start();
        }
        if (client.titleArchive == null) {
            client.drawInitial(loadAmount, s, false);
        }
    }

    private static void setupLoginScreen(Client client) {
        Client.chatboxImageProducer = null;
        client.minimapImageProducer = null;
        Client.tabImageProducer = null;
        ClientCompanion.gameScreenImageProducer = null;
        client.chatSettingImageProducer = null;
        Rasterizer2D.clear();
        Client.loginBoxImageProducer = Client.constructGraphicsBuffer(ClientUI.frameWidth, ClientUI.frameHeight);
        Rasterizer2D.clear();
        client.welcomeScreenRaised = true;
    }

    public static void drawLoginScreen(Client client, boolean connecting) {

        if (!stopRenderThread) {
            stopRenderThread = true;
        }
        if (Client.loginBoxImageProducer == null) {
            setupLoginScreen(client);
            client.loadTitleScreen();
        }
        LoginScreen.connecting = connecting;

        drawBackground();

        int x = 202;
        int y = 171;
        IndexedImageCompanion.titleBoxIndexedImage.draw(x, y);
        char c = '\u0168';
        char c1 = '\310';
        if (loginScreenState == 0)
            drawLoginScreenStart(client.boldText, client.resourceProvider, client.smallText, IndexedImageCompanion.titleButtonIndexedImage, x, y, c, c1);
        else if (loginScreenState == 2)
            drawLoginScreenLoaded(client, connecting, x, y, c, c1);
        else if (loginScreenState == 3)
            drawLoginScreenNewAccount(x, y, c, c1, client.boldText, IndexedImageCompanion.titleButtonIndexedImage);
        loginScreenAccessories();

        Flames.flameBuffer.drawGraphics(Client.instance.canvas.getGraphics(), 0, 0);

        Toolkit.getDefaultToolkit().sync();
    }

    private static void drawBackground() {
//        Rasterizer2D.clear();
        Flames.flameBuffer.initDrawingArea();
        ClientUtil.drawBackground();

/*        if (TitleScreen.isInitialised) {
            try {
                Flames.calculate();
                Flames.draw();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        if (Configuration.snowLoginScreen) {
            final long currentTime = Time.currentTimeMillis();
            final long timePassed = currentTime - lastFlash;
            final boolean drawFlash = timePassed > 10;
            Snowfall.draw(drawFlash);
        }
    }

    private static void drawLoginScreenStart(GameFont boldText, ResourceProvider resourceProvider, GameFont smallText, IndexedImage titleButtonIndexedImage, int x, int y, char c, char c1) {
        int i = c1 / 2 + 80;
        smallText.drawCenteredText(resourceProvider.loadingMessage, c / 2 + x, i + y, 0x75a9a9, true);
        i = c1 / 2 - 20;
        boldText.drawCenteredText("Welcome to " + Configuration.CLIENT_NAME, c / 2 + x, i + y, 0xffff00, true);

        int l = c / 2 - 80;
        int k1 = c1 / 2 + 20;
        titleButtonIndexedImage.draw(l - 73 + x, k1 - 20 + y);
        boldText.drawCenteredText("New User", l + x, k1 + 5 + y, 0xffffff, true);
        l = c / 2 + 80;
        titleButtonIndexedImage.draw(l - 73 + x, k1 - 20 + y);
        boldText.drawCenteredText("Existing User", l + x, k1 + 5 + y, 0xffffff, true);
    }

    private static void drawLoginScreenLoaded(Client client, boolean connecting, int x, int y, char c, char c1) {
        int j = c1 / 2 - 45;
        if (firstLoginMessage.length() > 0) {
            client.boldText.drawCenteredText(firstLoginMessage, c / 2 + x, j - 15 + y, 0xffff00, true);
            client.boldText.drawCenteredText(secondLoginMessage, c / 2 + x, j + y, 0xffff00, true);
        } else {
            client.boldText.drawCenteredText(secondLoginMessage, c / 2 + x, j - 7 + y, 0xffff00, true);
        }
        j += 25;

        if (Configuration.SHOW_DEVELOPER_MENU)
            client.boldText.drawCenteredText("Connected to "+Configuration.connected_world.getName(), c / 2 + x, j - 100 + y, 0xffff00, true);

        final long currentTime = Time.currentTimeMillis();
        final long timePassed = currentTime - lastFlash;
        final boolean drawFlash = timePassed > FLASH_INTERVAL;
        final boolean resetFlash = timePassed > FLASH_DURATION + FLASH_INTERVAL;

        if(resetFlash)
            lastFlash = currentTime;

        client.boldText.drawTextWithPotentialShadow("Login: " + client.myUsername + ((loginScreenCursorPos == 0) && (drawFlash) ? "@yel@|" : ""), c / 2 - 90 + x, j + y, 0xffffff, true
        );
        j += 15;
        client.boldText.drawTextWithPotentialShadow("Password: " + StringUtils.passwordAsterisks(client.myPassword)
                + ((loginScreenCursorPos == 1) && (drawFlash) ? "@yel@|" : ""), c / 2 - 90 + x, j + y, 0xffffff, true
        );

        // Remember me
        rememberUsernameHover = client.mouseInRegion(269, 284, 279, 292);
        if (rememberUsername) {
            SpriteLoader.getSprite(rememberUsernameHover, 346, 348).drawAdvancedSprite(67 + x, 108 + y);
        } else {
            SpriteLoader.getSprite(rememberUsernameHover, 345, 347).drawAdvancedSprite(67 + x, 108 + y);
        }
        client.smallText.drawCenteredText("Remember username", 136 + x, 120 + y, 0xffff00, true);

        // Hide username
        rememberPasswordHover = client.mouseInRegion(410, 425, 279, 292);
        if (rememberPassword) {
            SpriteLoader.getSprite(rememberPasswordHover, 346, 348).drawAdvancedSprite(208 + x, 108 + y);
        } else {
            SpriteLoader.getSprite(rememberPasswordHover, 345, 347).drawAdvancedSprite(208 + x, 108 + y);
        }
        client.smallText.drawCenteredText("Remember password", 276 + x, 120 + y, 0xffff00, true);

        forgottenPasswordHover = client.mouseInRegion(288, 471, 346, 357);
        client.smallText.drawCenteredText("Forgotten your password? @whi@Click here.", 178 + x, 186 + y, 0xffff00, true);

        if (!connecting) {
            int i1 = c / 2 - 80;
            int l1 = c1 / 2 + 50;
            IndexedImageCompanion.titleButtonIndexedImage.draw(i1 - 73 + x, l1 - 20 + y);
            client.boldText.drawCenteredText("Login", i1 + x, l1 + 5 + y, 0xffffff, true);
            i1 = c / 2 + 80;
            IndexedImageCompanion.titleButtonIndexedImage.draw(i1 - 73 + x, l1 - 20 + y);
            client.boldText.drawCenteredText("Cancel", i1 + x, l1 + 5 + y, 0xffffff, true);
        }
    }

    private static void drawLoginScreenNewAccount(int x, int y, char c, char c1, GameFont font, IndexedImage titleButton) {
        font.drawCenteredText("Create a new account", c / 2 + x, c1 / 2 - 60 + y, 0xffff00, true);
        int k = c1 / 2 - 35;
        font.drawCenteredText("You can login with an username of your", c / 2 + x, k + y, 0xffffff, true);
        k += 15;
        font.drawCenteredText("choice. It's completely free and there's", c / 2 + x, k + y, 0xffffff, true);
        k += 15;
        font.drawCenteredText("no registration needed. For more info or", c / 2 + x, k + y, 0xffffff, true);
        k += 15;
        font.drawCenteredText("questions visit www.Grinderscape.org", c / 2 + x, k + y, 0xffffff, true);
        k += 15;
        Rasterizer2D.drawHorizontalLine(k + 29 + x, c / 2 - 68 + y, 157, 0xffffff);

        int j1 = c / 2;
        int i2 = c1 / 2 + 50;
        titleButton.draw(j1 - 73 + x, i2 - 20 + y);
        font.drawCenteredText("Cancel", j1 + x, i2 + 5 + y, 0xffffff, true);
    }

    public static void loginScreenAccessories() {
        if (Audio.musicNotMuted()) {
            SpriteLoader.getSprite(58).drawSprite(158 + 562, 196 + 265);
        } else {
            SpriteLoader.getSprite(59).drawSprite(158 + 562, 196 + 265);
        }
    }

    public static void processLoginScreenInput(Client client) {
        ++Flames.tick;
        if (loginScreenState == 0) {
            if (MouseHandler.lastButton == 1 && MouseHandler.lastPressedX >= 722 && MouseHandler.lastPressedX <= 751 && MouseHandler.lastPressedY >= 463 && MouseHandler.lastPressedY <= 493) {
                toggleLoginMusicMute(client);
            }
            int i = GameShell.canvasWidth / 2 + 80;
            int l = GameShell.canvasHeight / 2 + 20;
            l += 20;
            if (MouseHandler.lastButton == 1) {
                if (MouseHandler.lastPressedX >= i - 75 && MouseHandler.lastPressedX <= i + 75 && MouseHandler.lastPressedY >= l - 20
                        && MouseHandler.lastPressedY <= l + 20) {
                    firstLoginMessage = "Enter your username & password. New user?";
                    secondLoginMessage = "enter any untaken username to create an account.";
                    loginScreenState = 2;
                    loginScreenCursorPos = rememberUsername && client.myUsername.length() > 0 ? 1 : 0;
                } else if (client.mouseInRegion(229, 375, 271, 312)) {
                    loginScreenState = 3;
                    loginScreenCursorPos = 0;
                }
            }
            // Press enter to skip login screen
            do {
                int key = KeyHandler.readChar();
                if (key == -1)
                    break;
                if (key == KeyEvent.VK_ENTER) {
                    firstLoginMessage = "Enter your username & password. New user?";
                    secondLoginMessage = "enter any untaken username to create an account.";
                    loginScreenState = 2;
                    loginScreenCursorPos = rememberUsername && client.myUsername.length() > 0 ? 1 : 0;
                }
            } while (true);

        } else if (loginScreenState == 2) {

            if (MouseHandler.lastButton == 1) {
                if (MouseHandler.lastPressedX >= 722 && MouseHandler.lastPressedX <= 753 && MouseHandler.lastPressedY >= 463
                        && MouseHandler.lastPressedY <= 493) {
                    toggleLoginMusicMute(client);
                } else if (rememberUsernameHover) {
                    rememberUsername = !rememberUsername;
                    PlayerSettings.savePlayerData(client);
                } else if (rememberPasswordHover) {
                    rememberPassword = !rememberPassword;
                    PlayerSettings.savePlayerData(client);
                } else if (forgottenPasswordHover) {
                    MiscUtils.launchURL("https://discord.com/channels/358664434324865024/992202052434403378");
                }
            }
            int j = GameShell.canvasHeight / 2 - 45;
            j += 25;
            j += 25;
            if (MouseHandler.lastButton == 1 && MouseHandler.lastPressedY >= j - 15 && MouseHandler.lastPressedY < j)
                loginScreenCursorPos = 0;
            j += 15;
            if (MouseHandler.lastButton == 1 && MouseHandler.lastPressedY >= j - 15 && MouseHandler.lastPressedY < j)
                loginScreenCursorPos = 1;
            j += 15;
            int i1 = GameShell.canvasWidth / 2 - 80;
            int k1 = GameShell.canvasHeight / 2 + 50;
            k1 += 20;
            if (MouseHandler.lastButton == 1 && MouseHandler.lastPressedX >= i1 - 75 && MouseHandler.lastPressedX <= i1 + 75
                    && MouseHandler.lastPressedY >= k1 - 20 && MouseHandler.lastPressedY <= k1 + 20) {

                tryLogin(client);
                PlayerSettings.savePlayerData(client);
                if (Client.loggedIn)
                    return;
            }
            i1 = GameShell.canvasWidth / 2 + 80;
            if (MouseHandler.lastButton == 1 && MouseHandler.lastPressedX >= i1 - 75 && MouseHandler.lastPressedX <= i1 + 75
                    && MouseHandler.lastPressedY >= k1 - 20 && MouseHandler.lastPressedY <= k1 + 20) {
                loginScreenState = 0;
            }
            do {
                int l1 = KeyHandler.readChar();
                if (l1 == -1)
                    break;
                boolean flag1 = false;
                for (int i2 = 0; i2 < ClientCompanion.validUserPassChars.length(); i2++) {
                    if (l1 != ClientCompanion.validUserPassChars.charAt(i2))
                        continue;
                    flag1 = true;
                    break;
                }

                if (loginScreenCursorPos == 0) {
                    if (l1 == 8 && client.myUsername.length() > 0)
                        client.myUsername = client.myUsername.substring(0, client.myUsername.length() - 1);
                    if (l1 == 9 || l1 == 10 || l1 == 13)
                        loginScreenCursorPos = 1;
                    if (flag1)
                        client.myUsername += (char) l1;
                    if (client.myUsername.length() > 12)
                        client.myUsername = client.myUsername.substring(0, 12);
                    if (client.myUsername.length() > 0) {
                        client.myUsername = StringUtils.formatText(StringUtils.capitalize(client.myUsername));
                    }
                } else if (loginScreenCursorPos == 1) {
                    if (l1 == 8 && client.myPassword.length() > 0)
                        client.myPassword = client.myPassword.substring(0, client.myPassword.length() - 1);
                    if (l1 == 9) {
                        loginScreenCursorPos = 0;
                    } else if (l1 == 10 || l1 == 13) {
                        tryLogin(client);
                        return;
                    }
                    if (flag1)
                        client.myPassword += (char) l1;
                    if (client.myPassword.length() > 15)
                        client.myPassword = client.myPassword.substring(0, 15);
                }
            } while (true);
        } else if (loginScreenState == 3) {
            int k = GameShell.canvasWidth / 2;
            int j1 = GameShell.canvasHeight / 2 + 50;

            j1 += 20;

            if (MouseHandler.lastButton == 1 && MouseHandler.lastPressedX >= k - 75 && MouseHandler.lastPressedX <= k + 75 && MouseHandler.lastPressedY >= j1 - 20 && MouseHandler.lastPressedY <= j1 + 20) {
                loginScreenState = 0;
            }
            if (MouseHandler.lastButton == 1 && MouseHandler.lastClickedX >= k - 27 && MouseHandler.lastClickedX <= k + 130 && MouseHandler.lastClickedY >= j1 - 50 && MouseHandler.lastClickedY <= j1 - 38) {
                MiscUtils.launchURL("https://grinderscape.org");
            }
        }
    }

    private static void tryLogin(Client client) {
        if (!connecting) {
            loginFailures = 0;
            client.login(client.myUsername, client.myPassword, false);
        }
    }

    private static void toggleLoginMusicMute(Client client) {
        client.toggleMusicMute();
        client.playSound(2266);
        Configuration.loginMusic = !Configuration.loginMusic;
        PlayerSettings.savePlayerData(client);
    }

    /**
     * interface_handle_auto_content
     */
    public static void drawFriendsListOrWelcomeScreen(Client client, Widget widget) {
        int index = widget.contentType;
        if (index >= 1 && index <= 100 || index >= 701 && index <= 800) {
            if (index == 1 && PlayerRelations.friendServerStatus == 0) {
                widget.setDefaultText("Loading friend list");
                widget.atActionType = 0;
                return;
            }
            if (index == 1 && PlayerRelations.friendServerStatus == 1) {
                widget.setDefaultText("Connecting to friendserver");
                widget.atActionType = 0;
                return;
            }
            if (index == 2 && PlayerRelations.friendServerStatus != 2) {
                widget.setDefaultText("Please wait...");
                widget.atActionType = 0;
                return;
            }
            int k = PlayerRelations.friendsCount;
            if (PlayerRelations.friendServerStatus != 2)
                k = 0;
            if (index > 700)
                index -= 601;
            else
                index--;
            if (index >= k) {
                widget.setDefaultText("");
                widget.atActionType = 0;
                return;
            } else {
                widget.setDefaultText(PlayerRelations.friendsList[index]);
                widget.atActionType = 1;
                return;
            }
        }
        if (index >= 101 && index <= 200 || index >= 801 && index <= 900) {
            int l = PlayerRelations.friendsCount;
            if (PlayerRelations.friendServerStatus != 2)
                l = 0;
            if (index > 800)
                index -= 701;
            else
                index -= 101;
            if (index >= l) {
                widget.setDefaultText("");
                widget.atActionType = 0;
                return;
            }
            if (PlayerRelations.friendsNodeIDs[index] == 0)
                widget.setDefaultText("@red@Offline");
            else if (PlayerRelations.friendsNodeIDs[index] == ClientCompanion.nodeID)
                widget.setDefaultText("@gre@Online")/*
                 * + (friendsNodeIDs[j] - 9)
                 */;
            else
                widget.setDefaultText("@red@Offline")/*
                 * + (friendsNodeIDs[j] - 9)
                 */;
            widget.atActionType = 1;
            return;
        }

        if (index == 203) {
            int i1 = PlayerRelations.friendsCount;
            if (PlayerRelations.friendServerStatus != 2)
                i1 = 0;
            widget.scrollMax = i1 * 15 + 20;
            if (widget.scrollMax <= widget.height)
                widget.scrollMax = widget.height;
            return;
        }
        if (index >= 401 && index <= 500) {
            if ((index -= 401) == 0 && PlayerRelations.friendServerStatus == 0) {
                widget.setDefaultText("Loading ignore list");
                widget.atActionType = 0;
                return;
            }
            if (index == 1 && PlayerRelations.friendServerStatus == 0) {
                widget.setDefaultText("Please wait...");
                widget.atActionType = 0;
                return;
            }
            int j1 = PlayerRelations.ignoreCount;
            if (PlayerRelations.friendServerStatus == 0)
                j1 = 0;
            if (index >= j1) {
                widget.setDefaultText("");
                widget.atActionType = 0;
                return;
            } else {
                widget.setDefaultText(StringUtils.formatText(StringUtils.decodeBase37(PlayerRelations.ignoreListAsLongs[index])));
                widget.atActionType = 1;
                return;
            }
        }
        if (index == 503) {
            widget.scrollMax = PlayerRelations.ignoreCount * 15 + 20;
            if (widget.scrollMax <= widget.height)
                widget.scrollMax = widget.height;
            return;
        }
        if (index == 327) {
            if (IdentityKit.drawCharacterDesignModel(client, widget))
                return;
            //}
            return;
        }
        if (index == 3291) {
            // PetSystem petDef = new PetSystem(NpcDefinition.lookup(239));
            if (PetSystem.petSelected == -1) {
                return;
            }
            NpcDefinition def = NpcDefinition.lookup(PetSystem.petSelected);
            if (def == null) {
                return;
            }
            PetSystem petDef = new PetSystem(def);
            widget.modelXAngle = 50;
            widget.modelYAngle = (int) (Client.tick / 200D * 1024D) & 2047;
            Model model;
            final Model[] parts = new Model[petDef.getModelArrayLength()];
            for (int i = 0; i < petDef.getModelArrayLength(); i++) {
                parts[i] = Model.getModel(petDef.getModelArray()[i]);
            }
            if (parts.length == 1) {
                model = parts[0];
            } else {
                model = new Model(parts, parts.length);
            }
            if (model == null) {
                return;
            }
            model.skin();
            if (petDef.getPetSelected() != 4315) {
                model.applyTransform(Animation.getSequenceDefinition(petDef.getAnimation()).frameIds[PetSystem.animationFrame]);
            }
            model.light(64, 850, -30, -50, -30, true);
            widget.defaultMediaType = 5;
            widget.defaultMedia = 0;
            widget.modelZoom = def.dropTableZoom;
            Widget.method208(client.aBoolean994, model);
            return;
        }
        if (index == 328) {
            /*
              Equipment screen interface character model
             */
            int verticleTilt = 150;
            int animationSpeed = (int) (Math.sin((double) Client.tick / 40D) * 256D) & 0x7ff;
            widget.modelXAngle = verticleTilt;
            widget.modelYAngle = animationSpeed;
            widget.defaultMediaType = 5;
            widget.defaultMedia = 0;
            Widget.method208(client.aBoolean994, Client.localPlayer.getAnimatedModel());
            return;
        }
        if (index == 329) {
            /*
              Item model on Interface`
             */
            if (widget.defaultMedia == -1)
                return;
            Model model = Model.getModel(widget.defaultMedia);
            Widget.method208(client.aBoolean994, model); // Updates model on interface
            return;
        }
        if (index == 330) {
            /*
              Custom dummy character model on interface, currently only used for Ironman Setup interface
             */
            int verticleTilt = 150;
            int horizontalSwivel = 150;
            widget.modelXAngle = verticleTilt;
            widget.modelYAngle = horizontalSwivel;
            widget.defaultMediaType = 5;
            widget.defaultMedia = 0;
            Widget.method208(client.aBoolean994, ClientCompanion.dummyPlayer.getAnimatedModel());
            return;
        }
        if (index == 331) {
            /*
              Custom character model on interface, currently only used for Item Color Customizer Interface
             */
            int verticleTilt = 150;
            int animationSpeed = (int) (Math.sin((double) Client.tick / 40D) * 256D) & 0x7ff;
            widget.modelXAngle = verticleTilt;
            widget.modelYAngle = animationSpeed;
            widget.defaultMediaType = 5;
            widget.defaultMedia = 0;
            Widget.method208(client.aBoolean994, ClientCompanion.dummyPlayer.getAnimatedModel());
            return;
        }
        if (index == 324) {
            if (SpriteCompanion.aClass30_Sub2_Sub1_Sub1_931 == null) {
                SpriteCompanion.aClass30_Sub2_Sub1_Sub1_931 = widget.disabledSprite;
                SpriteCompanion.aClass30_Sub2_Sub1_Sub1_932 = widget.enabledSprite;
            }
            if (client.maleCharacter) {
                widget.disabledSprite = SpriteCompanion.aClass30_Sub2_Sub1_Sub1_932;
            } else {
                widget.disabledSprite = SpriteCompanion.aClass30_Sub2_Sub1_Sub1_931;
            }
            return;
        }
        if (index == 325) {
            if (SpriteCompanion.aClass30_Sub2_Sub1_Sub1_931 == null) {
                SpriteCompanion.aClass30_Sub2_Sub1_Sub1_931 = widget.disabledSprite;
                SpriteCompanion.aClass30_Sub2_Sub1_Sub1_932 = widget.enabledSprite;
            }
            if (client.maleCharacter) {
                widget.disabledSprite = SpriteCompanion.aClass30_Sub2_Sub1_Sub1_931;
            } else {
                widget.disabledSprite = SpriteCompanion.aClass30_Sub2_Sub1_Sub1_932;
            }
            return;
        }
        if (index == 600) {
            widget.setDefaultText(client.reportAbuseInput);
            if (Client.tick % 20 < 10) {
                widget.setDefaultText(widget.getDefaultText() + "|");
                return;
            } else {
                widget.setDefaultText(widget.getDefaultText() + " ");
                return;
            }
        }
        if (index == 613)
            if (client.clientRights >= 1) {
                if (client.canMute) {
                    widget.textColor = 0xff0000;
                    widget.setDefaultText("Moderator option: Mute player for 48 hours: <ON>");
                } else {
                    widget.textColor = 0xffffff;
                    widget.setDefaultText("Moderator option: Mute player for 48 hours: <OFF>");
                }
            } else {
                widget.setDefaultText("");
            }
        if (index == 650 || index == 655)
            if (client.anInt1193 != 0) {
                String s;
                if (client.daysSinceLastLogin == 0)
                    s = "earlier today";
                else if (client.daysSinceLastLogin == 1)
                    s = "yesterday";
                else
                    s = client.daysSinceLastLogin + " days ago";
                widget.setDefaultText("You last logged in " + s + " from: " + SignLink.dns);
            } else {
                widget.setDefaultText("");
            }
        if (index == 651) {
            if (client.unreadMessages == 0) {
                widget.setDefaultText("0 unread messages");
                widget.textColor = 0xffff00;
            }
            if (client.unreadMessages == 1) {
                widget.setDefaultText("1 unread defaultText");
                widget.textColor = 65280;
            }
            if (client.unreadMessages > 1) {
                widget.setDefaultText(client.unreadMessages + " unread messages");
                widget.textColor = 65280;
            }
        }
        if (index == 652)
            if (client.daysSinceRecovChange == 201) {
                if (client.membersInt == 1)
                    widget.setDefaultText("@yel@This is a non-members world: @whi@Since you are a member we");
                else
                    widget.setDefaultText("");
            } else if (client.daysSinceRecovChange == 200) {
                widget.setDefaultText("You have not yet set any password recovery questions.");
            } else {
                String s1;
                if (client.daysSinceRecovChange == 0)
                    s1 = "Earlier today";
                else if (client.daysSinceRecovChange == 1)
                    s1 = "Yesterday";
                else
                    s1 = client.daysSinceRecovChange + " days ago";
                widget.setDefaultText(s1 + " you changed your recovery questions");
            }
        if (index == 653)
            if (client.daysSinceRecovChange == 201) {
                if (client.membersInt == 1)
                    widget.setDefaultText("@whi@recommend you use a members world instead. You may use");
                else
                    widget.setDefaultText("");
            } else if (client.daysSinceRecovChange == 200)
                widget.setDefaultText("We strongly recommend you do so now to secure your account.");
            else
                widget.setDefaultText("If you do not remember making this change then cancel it immediately");
        if (index == 654) {
            if (client.daysSinceRecovChange == 201)
                if (client.membersInt == 1) {
                    widget.setDefaultText("@whi@this world but member benefits are unavailable whilst here.");
                    return;
                } else {
                    widget.setDefaultText("");
                    return;
                }
            if (client.daysSinceRecovChange == 200) {
                widget.setDefaultText("Do this from the 'account management' area on our front webpage");
                return;
            }
            widget.setDefaultText("Do this from the 'account management' area on our front webpage");
        }
    }

    public static void drawWelcomeScreen(Client client) {
        client.welcomeScreenRaised = false;
        if (ClientUI.frameMode == Client.ScreenMode.FIXED) {
            client.topFrame.drawGraphics(client.canvas.getGraphics(), 0, 0);
            client.leftFrame.drawGraphics(client.canvas.getGraphics(), 0, 4);
        }
        ChatBox.setUpdateChatbox(true);
        ClientCompanion.tabAreaAltered = true;
        if (client.getLoadingStage() != 2) {
            if (ClientUI.frameMode == Client.ScreenMode.FIXED) {
                ClientCompanion.gameScreenImageProducer.drawGraphics(client.canvas.getGraphics(), 0, 0
                );
                client.minimapImageProducer.drawGraphics(client.canvas.getGraphics(), 516, 0);
            }
        }
    }
}
