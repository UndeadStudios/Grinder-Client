package com.runescape;

import com.grinder.*;
import com.grinder.client.*;
import com.grinder.client.util.Debug;
import com.grinder.client.util.MenuOpcodes;
import com.grinder.model.*;
import com.grinder.model.Emojis.Emoji;
import com.grinder.net.CacheDownloader;
import com.grinder.net.Resource;
import com.grinder.net.ResourceProvider;
import com.grinder.ui.util.NpcUpdateSnapshot;
import com.grinder.ui.SnapshotCache;
import com.runescape.cache.anim.RSFrame317;
import com.runescape.cache.def.*;
import com.runescape.cache.graphics.sprite.SpriteCompanion;
import com.grinder.ui.ClientUI;
import com.runescape.audio.Audio;
import com.runescape.cache.*;
import com.runescape.cache.FileStore.Store;
import com.runescape.cache.anim.Animation;
import com.runescape.cache.anim.Graphic;
import com.runescape.cache.config.VariableBits;
import com.runescape.cache.config.VariablePlayer;
import com.runescape.cache.definition.OSObjectDefinition;
import com.runescape.cache.graphics.*;
import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.cache.graphics.sprite.SpriteLoader;
import com.runescape.cache.graphics.widget.*;
import com.runescape.clock.Time;
import com.runescape.collection.NodeDeque;
import com.runescape.draw.GraphicsBuffer;
import com.runescape.draw.ProducingGraphicsBuffer;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.*;
import com.runescape.entity.Player.Rights;
import com.runescape.entity.model.IdentityKit;
import com.runescape.entity.model.Model;
import com.runescape.entity.model.particles.Particle;
import com.runescape.input.*;
import com.runescape.io.Buffer;
import com.runescape.io.jaggrab.JagGrab;
import com.runescape.io.packets.outgoing.PacketBuilder;
import com.runescape.io.packets.outgoing.PacketType;
import com.runescape.io.packets.outgoing.impl.*;
import com.runescape.net.IsaacCipher;
import com.runescape.net.NetSocket;
import com.runescape.scene.*;
import com.runescape.scene.Projectile;
import com.runescape.scene.object.*;
import com.runescape.sign.SignLink;
import com.runescape.util.*;
import com.grinder.client.util.Log;
import net.runelite.api.CollisionData;
import net.runelite.api.GameState;
import net.runelite.api.Scene;

import javax.imageio.ImageIO;
import java.applet.AppletContext;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.grinder.client.ClientCompanion.aBoolean831;

public class Client extends GameShell {

    private static final long serialVersionUID = 5707517957054703648L;
    public static final int MAX_CAMERA_ZOOM = 600;
    public static final int GAME_ASSETS_LOADED = 2;
    public static byte[][][] underlays;
    public static byte[][][] overlays;
    public static int map_objects_count;
    public static int canvasBottomX;
    static int map_loading_state;
    static int __client_fe;

    public boolean reloadRegion;
    private final Buffer textStream;
    private final PathFinder pathFinder = new PathFinder();

    @Override
    protected void setUp() {
        setUpKeyboard();
        setUpMouse();
        mouseWheel = mouseWheel();
        setUpClipboard();
        setMaxCanvasSize();
        startUp();
    }

    @Override
    protected synchronized void methodDraw(boolean focus) {

        Audio.methodDrawMusicTrack();

        if (loggedIn && timeSinceResize != 0L && Time.currentTimeMillis() > timeSinceResize) {
            Client.timeSinceResize = 0L;
            setMaxCanvasSize();
        }

        processDrawing();


    }

    @Override
    protected final void methodCycle() {

        tick++;

        Js5Cache.doCycleJs5();
        OsCache.loadStoreActions();
        Audio.methodCycleMidiPlayers();

        synchronized (KeyHandler.instance) {
            ++KeyHandler.idleCycles;
            KeyHandler.typedKeyCharIndex = KeyHandler.__an_cc;
            KeyHandler.__an_cl = 0;
            int var1;
            if (KeyHandler.releasedKeyIndex < 0) {
                for (var1 = 0; var1 < 112; ++var1) {
                    KeyHandler.pressedKeys[var1] = false;
                }

                KeyHandler.releasedKeyIndex = KeyHandler.__an_ch;
            } else {
                while (KeyHandler.releasedKeyIndex != KeyHandler.__an_ch) {
                    var1 = KeyHandler.releasedKeyCodes[KeyHandler.__an_ch];
                    KeyHandler.__an_ch = KeyHandler.__an_ch + 1 & 127;
                    if (var1 < 0) {
                        KeyHandler.pressedKeys[~var1] = false;
                    } else {
                        if (!KeyHandler.pressedKeys[var1] && KeyHandler.__an_cl < KeyHandler.__an_cp.length - 1) {
                            KeyHandler.__an_cp[++KeyHandler.__an_cl - 1] = var1;
                        }
                        KeyHandler.pressedKeys[var1] = true;
                    }
                }
            }

            if (KeyHandler.__an_cl > 0) {
                KeyHandler.idleCycles = 0;
            }

            KeyHandler.__an_cc = KeyHandler.__an_cz;
        }

        synchronized (MouseHandler.instance) {
            MouseHandler.currentButton = MouseHandler.currentButton0;
            MouseHandler.x = MouseHandler.x0;
            MouseHandler.y = MouseHandler.y0;
            MouseHandler.millis = MouseHandler.millis0;
            MouseHandler.lastButton = MouseHandler.lastButton0;
            MouseHandler.lastClickedX = MouseHandler.lastClickedX0;
            MouseHandler.lastClickedY = MouseHandler.lastClickedY0;
            MouseHandler.lastPressedX = MouseHandler.lastPressedX0;
            MouseHandler.lastPressedY = MouseHandler.lastPressedY0;
            MouseHandler.lastPressedTimeMillis = MouseHandler.lastPressedTimeMillis0;
            MouseHandler.lastButton0 = 0;
            MouseHandler.mouseWheelDown = MouseHandler.mouseWheelDown0;
            MouseHandler.mouseWheelDragX = MouseHandler.mouseWheelDragX0;
            MouseHandler.mouseWheelDragY = MouseHandler.mouseWheelDragY0;
            MouseHandler.mouseWheelDragX0 = 0;
            MouseHandler.mouseWheelDragY0 = 0;
        }

        if (mouseWheel != null) {
            mouseWheelRotation = mouseWheel.useRotation();
            if (mouseWheelRotation != 0) {
                handleInterfaceScrolling(mouseWheelRotation);

                if (insideArea(0, 512, 165, Client.instance.getChatBoxHeight() - ChatBox.chatScrollHeight, ClientUI.frameHeight - 25)) {
                    handleChatBoxScrolling(mouseWheelRotation);
                } else if (Client.loggedIn) {
                    handleZooming(mouseWheelRotation);
                }
            }
        }

        processGameLoop();
    }

    @Override
    protected void onResize() {
        timeSinceResize = Time.currentTimeMillis() + 500L;
        refreshFrameSize();

    }

    @Override
    protected void onKill() {

        Log.info("Killing client :)");

        mouseWheel = null;

        if (Audio.pcmPlayer0 != null)
            Audio.pcmPlayer0.shutdown();

        if (Audio.pcmPlayer1 != null)
            Audio.pcmPlayer1.shutdown();

        if (NetCache.socket != null)
            NetCache.socket.close();

        synchronized (IndexStoreActionHandler.IndexStoreActionHandler_lock) {
            if (IndexStoreActionHandler.sleepsTillRestart != 0) {
                IndexStoreActionHandler.sleepsTillRestart = 1;

                try {
                    IndexStoreActionHandler.IndexStoreActionHandler_lock.wait();
                } catch (InterruptedException e) {
                    Log.error("Could not close index store handlerl", e);
                }
            }

        }
        try {
            OsCache.dat2File.close();

            for (int i = 0; i < OsCache.idxCount; ++i)
                OsCache.idxFiles[i].close();

            OsCache.idx255File.close();

            if (OsCache.randomDat != null)
                OsCache.randomDat.close();

        } catch (Exception e) {
            Log.error("Could not close cache files on kill", e);
        }
    }

    public static Client instance;
    private static MouseWheel mouseWheel;
    public ScreenFadeManager screenFadeManager;
    public static GraphicsBuffer chatboxImageProducer;
    public static GraphicsBuffer loginBoxImageProducer;
    public static GraphicsBuffer tabImageProducer;
    public GraphicsBuffer chatSettingImageProducer;
    public GraphicsBuffer minimapImageProducer;
    public GraphicsBuffer leftFrame;
    public GraphicsBuffer topFrame;
    public final FileStore[] indices;

    public NodeDeque spawns;
    public NodeDeque[][][] groundItems;
    public NodeDeque projectiles;
    public NodeDeque incompleteAnimables;

    public GameFont smallText;
    public GameFont regularText;
    public GameFont boldText;
    public GameFont fancyFont;
    public GameFont fancyFontLarge;
    public RSFont newSmallFont, newRegularFont, newBoldFont;
    public RSFont newFancyFont, newFancyFontLarge;

    public FileArchive titleArchive;

    public static int loginState;
    public static int loginExceptionCount;
    public static int loginFailCount;
    public static int gameState;
    public static boolean isLoading;
    private static int mouseWheelRotation;
    private static int soundCycleCounter;
    public static int viewportZoom;
    public static int viewportWidth;
    public static int viewportHeight;
    public static int viewportOffsetX;
    public static int viewportOffsetY;

    private static long timeSinceResize;
    public static int cameraZoom = 600;
    public static Player localPlayer;
    public static boolean loggedIn;
    public static int tick;

    public final int[] currentExp;
    public final int[] currentLevels;

    public final int[] maximumLevels;
    public final Widget chatBoxWidget;
    private final int[] chatRights;
    public final String[] chatTitles;
    public final int MAX_ENTITY_COUNT;
    private final int LOCAL_PLAYER_ID;
    public final int[] quake4PiOverPeriods;
    public final int[] chatTypes;
    public final String[] chatNames;
    public final String[] chatMessages;
    public final int[] anIntArray968;
    public final MobOverlayRenderer mobOverlayRenderer;

    public final int[] characterDesignColours;
    public final boolean aBoolean994;
    public final int[] quakeTimes;
    public final int[] defaultSettings;
    public final int[] minimapLeft;
    public final int[] anIntArray1057;
    public final int scrollBarFillColor;
    public final int[] anIntArray1065;
    public final String[] playerOptions;
    public final boolean[] playerOptionsHighPriority;
    public final int[][][] instanceChunkTemplates;
    public final int[] objectGroups = {
            0, 0, 0, 0,
            1, 1, 1, 1, 1,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            3};
    public final Sprite[] modIcons;
    public final int[] minimapLineWidth;
    public final int[] privateMessageIds;
    public final Set<Long> clanMembers = new HashSet<>();
    public boolean exitRequested = false;
    public final AtomicBoolean captchaRequired = new AtomicBoolean();
    public int dropdownInversionFlag;
    public int chatTypeView;
    public int autoCastId = 0;
    public int cameraX;
    public int cameraY;
    public int cameraZ;
    public int cameraRotation;
    public int localPlayerIndex;
    public String inputString;
    public int spriteDrawX;
    public int spriteDrawY;
    public int[] settings;
    public int openWalkableInterface;

    public ResourceProvider resourceProvider;
    public int currentRegionX;
    public int currentRegionY;

    public int backDialogueId;
    public int fullscreenInterfaceID;
    public int previousChildWidgetId2;// 377
    public int previousChildWidgetId;// 377
    public int childWidgetId;// 377
    public int previousBackDialogueChildWidgetId;// 377
    private int chatBoxUpdateTick;// 377
    public String enter_name_title = "Enter name:";
    public int myCrown;
    public boolean spinnerSpinning;
    private boolean spacePressed;
    /* Wheel of Fortune */
    public final int[][] emptyClipData;
    public Mob currentInteract;
    public String enter_amount_title = "Enter amount:";
    public int setChannel;
    @SuppressWarnings("unused")
    private int currentTrackLoop;
    public String objectMaps = "", floorMaps = "";
    public int specialAttack = 0;
    /**
     * The current skill being practised.
     */
    public int currentSkill = -1;
    /**
     * The player's total exp
     */
    public long totalExp;
    public int specialEnabled = 0;
    public boolean newsflash;

    public long loadingStartTime;

    public Npc[] npcs;
    public int npcCount;
    public int[] npcIndices;
    static int npcCount2;
    static int[] npcIndices2;

    static int[] npcDefaultOrientations;
    private int removedMobCount;
    private int[] removedMobs;
    public int lastOpcode;
    public int secondLastOpcode;
    public int thirdLastOpcode;
    public String clickToContinueString;
    private Buffer loginBuffer;

    public int cameraPitch;
    public int cameraYaw;
    public int clientRights;
    public int weight;
    public String reportAbuseInput;
    public boolean menuOpen;
    public int childWidgetHoverType;
    private Buffer[] playerSynchronizationBuffers;
    public Player[] players;
    public int Players_count;
    public int[] Players_indices;
    private int mobsAwaitingUpdateCount;
    private int[] mobsAwaitingUpdate;

    private int anInt913;
    public int crossX;
    public int crossY;
    public int crossIndex;
    public int crossType;
    public static int plane;
    private int[][] tileLastDrawnActor;

    public int tickDelta;
    public SceneGraph scene;
    public long encodedPrivateMessageRecipientName;
    public String privateMessageRecipientName;
    private boolean aBoolean954;
    public boolean draggingScrollBar;
    public int anInt984;
    private int lastKnownPlane;
    public int dragItemDelay;
    private IsaacCipher decryption;
    public String amountOrNameInput;
    public int daysSinceLastLogin;
    public int packetSize;
    public int opcode;
    private int ping_packet_counter;
    private int mouseIdleGameTicks;
    private int loadingStage;
    public int previousChildWidgetHoverType;
    public boolean aBoolean1031;
    public int baseX;
    public int baseY;
    public int previousAbsoluteX;
    public int previousAbsoluteY;
    public int previousBackDialogueChildWidgetHoverType;
    public int dialogueId;
    public boolean maleCharacter;
    public int previousChildWidgetHoverType2;
    public int flashingSidebarId;
    public int multicombat;
    public int friendsListAction;
    public int mouseInvInterfaceIndex;
    public int lastActiveInvInterface;
    public boolean receivedPlayerUpdatePacket;
    public Buffer incoming;
    public int anInt1084;
    public int anInt1085;
    public int itemDragType;
    public int previousClickMouseX;
    public int previousClickMouseY;
    public int[] menuArguments1;
    public int[] menuArguments2;
    public int[] menuOpcodes;
    public long[] menuArguments0;
    public int x;
    public int y;
    public int height;
    public int speed;
    public int angle;
    public int systemUpdateTime;
    public int membersInt;
    public String inputPromptTitle;

    public int cameraX2;
    public int cameraY2;

    public int menuOptionsCount;
    public int spellSelected;
    public int selectedSpellId;
    public int spellUsableOn;
    public String spellTooltip;
    public boolean inTutorialIsland;
    public int runEnergy;
    public int poisonStatus = 0;
    public boolean continuedDialogue;
    public int unreadMessages;
    public boolean canMute;
    public boolean isInInstance;
    public boolean isCameraLocked;

    public int daysSinceRecovChange;
    private NetSocket socketStream;
    private IsaacCipher cipher;
    public int privateMessageCount;
    public int minimapZoom;
    public String myUsername;
    public String myPassword;
    public boolean showClanOptions;
    public final int reportAbuseInterfaceID;

    public byte[][] regionLandArchives;
    public int cameraPitchTarget;
    public int minimapOrientation;
    private int xAxisRotateSpeed;
    private int yAxisRotateSpeed;
    public int overlayInterfaceId;
    private Buffer outBuffer;
    public int anInt1193;
    public int splitPrivateChat;
    public String[] menuActions;
    public int minimapRotation;
    public String promptInput;
    public static int[][][] Tiles_heights;
    private long serverSeed;

    public int inputDialogState;
    public CollisionMap[] collisionMaps;
    public int[] regions;
    public int[] regionLandArchiveIds;
    public int[] regionMapArchiveIds;
    public boolean mouseOutOfDragZone;
    public int atInventoryLoopCycle;
    public int atInventoryInterface;
    public int atInventoryIndex;
    public int atInventoryItemId;
    public int atInventoryInterfaceType;
    public byte[][] regionMapArchives;
    public int textAboveHeadEffectState;
    public int onTutorialIsland;
    public int mouseButtonSetting;
    public boolean welcomeScreenRaised;
    public boolean messagePromptRaised;
    public byte[][][] tileFlags;
    public int destinationX;
    public int destinationY;
    public Image worldMapMarker;
    public int markerAngle;
    public long lastMarkerRotation;
    public int viewportDrawCount;
    public int localX;
    public int localY;

    public int itemSelected;
    public int usedItemInventorySlot;
    public int usedItemInventoryInterfaceId;
    public int usedItemId;
    public String selectedItemName;
    public int gameChatMode; // placeholder for when we add game chat modes

    public float spinSpeed;
    public final ArrayList<Particle> particleAddScreenList;
    public final ArrayList<Particle> particleRemoveScreenList;

    public Client() {
        instance = this;
        outBuffer = new Buffer(new byte[5000]);
        textStream = new Buffer(new byte[5000]);
        fullscreenInterfaceID = -1;
        chatRights = new int[500];
        chatTitles = new String[500];
        chatTypeView = 0;
        ChatBox.clanChatMode = 0;
        ChatBox.channelButtonPos2 = -1;
        ChatBox.channelButtonPos1 = 0;
        emptyClipData = new int[103][103];
        PlayerRelations.friendsNodeIDs = new int[200];
        npcDefaultOrientations = new int[]{768, 1024, 1280, 512, 1536, 256, 0, 1792};
        groundItems = new NodeDeque[4][104][104];
        aBoolean831 = false;
        npcs = new Npc[32768];
        npcIndices = new int[32768];
        npcCount2 = 0;
        npcIndices2 = new int[250];
        removedMobs = new int[1000];
        loginBuffer = new Buffer(new byte[5000]);
        ClientCompanion.openInterfaceId = -1;
        ClientCompanion.openInterfaceId2 = -1;
        ClientCompanion.interfaceInputSelected = -1;
        currentExp = new int[SkillConstants.SKILL_COUNT];
        reportAbuseInput = "";
        localPlayerIndex = -1;
        menuOpen = false;
        inputString = "";
        MAX_ENTITY_COUNT = 2048; // TODO <- bit large? hmm
        LOCAL_PLAYER_ID = 2047;
        players = new Player[MAX_ENTITY_COUNT];
        Players_indices = new int[MAX_ENTITY_COUNT];
        mobsAwaitingUpdate = new int[MAX_ENTITY_COUNT];
        playerSynchronizationBuffers = new Buffer[MAX_ENTITY_COUNT];
        Rasterizer3D.lastTexturePalettePixels = new byte[16384];
        currentLevels = new int[SkillConstants.SKILL_COUNT];
        PlayerRelations.ignoreListAsLongs = new long[100];
        ClientCompanion.loadingError = false;
        quake4PiOverPeriods = new int[5];
        tileLastDrawnActor = new int[104][104];
        chatTypes = new int[500];
        chatNames = new String[500];
        chatMessages = new String[500];
        SpriteCompanion.sideIcons = new Sprite[15];
        aBoolean954 = true;
        PlayerRelations.friendsListAsLongs = new long[200];
        spriteDrawX = -1;
        spriteDrawY = -1;
        anIntArray968 = new int[33];
        indices = new FileStore[SignLink.CACHE_INDEX_LENGTH];
        settings = new int[12000];
        draggingScrollBar = false;
        lastKnownPlane = -1;
        SpriteCompanion.hitMarks = new Sprite[20];
        characterDesignColours = new int[5];
        aBoolean994 = false;
        amountOrNameInput = "";
        projectiles = new NodeDeque();
        openWalkableInterface = -1;
        quakeTimes = new int[5];
        mobOverlayRenderer = new MobOverlayRenderer();
        aBoolean1031 = false;
        SpriteCompanion.mapFunctions = new Sprite[115];
        dialogueId = -1;
        maximumLevels = new int[SkillConstants.SKILL_COUNT];
        defaultSettings = new int[12000];
        maleCharacter = true;
        minimapLeft = new int[152];
        minimapLineWidth = new int[152];
        flashingSidebarId = -1;
        incompleteAnimables = new NodeDeque();
        anIntArray1057 = new int[33];
        chatBoxWidget = new Widget();
        IndexedImageCompanion.mapScenes = new IndexedImage[100];
        scrollBarFillColor = 0x4d4233;
        anIntArray1065 = new int[7];
        GameFrame.mapIconXs = new int[1000];
        GameFrame.mapIconYs = new int[1000];
        receivedPlayerUpdatePacket = false;
        PlayerRelations.friendsList = new String[200];
        incoming = new Buffer(new byte[5000]);
        menuArguments1 = new int[500];
        menuArguments2 = new int[500];
        menuOpcodes = new int[500];
        menuArguments0 = new long[500];
        SpriteCompanion.headIcons = new Sprite[20];
        SpriteCompanion.skullIcons = new Sprite[20];
        SpriteCompanion.headIconsHint = new Sprite[20];
        SpriteCompanion.autoBackgroundSprites = new Sprite[20];
        SpriteCompanion.autoBackgroundSprites2 = new Sprite[20];
        ClientCompanion.tabAreaAltered = false;
        inputPromptTitle = "";
        playerOptions = new String[ClientCompanion.PLAYER_OPTION_COUNT];
        playerOptionsHighPriority = new boolean[ClientCompanion.PLAYER_OPTION_COUNT];
        instanceChunkTemplates = new int[4][13][13];
        SpriteCompanion.mapIcons = new Sprite[1000];
        inTutorialIsland = false;
        continuedDialogue = false;
        SpriteCompanion.crosses = new Sprite[8];
        loggedIn = false;
        isLoading = true;
        canMute = false;
        isInInstance = false;
        isCameraLocked = false;
        myUsername = "";
        myPassword = "";
        ClientCompanion.genericLoadingError = false;
        reportAbuseInterfaceID = -1;
        spawns = new NodeDeque();
        cameraPitchTarget = 128;
        overlayInterfaceId = -1;
        menuActions = new String[500];
        ChatBox.chatScrollMax = ChatBox.chatScrollHeight;
        promptInput = "";
        modIcons = new Sprite[50];
        ClientCompanion.tabId = 3;
        ChatBox.setUpdateChatbox(false);
        collisionMaps = new CollisionMap[4];
        privateMessageIds = new int[100];
        mouseOutOfDragZone = false;
        ClientCompanion.rsAlreadyLoaded = false;
        welcomeScreenRaised = false;
        messagePromptRaised = false;
        LoginScreen.firstLoginMessage = "";
        LoginScreen.secondLoginMessage = "";
        backDialogueId = -1;
        particleAddScreenList = new ArrayList<>(10000);
        particleRemoveScreenList = new ArrayList<>();
    }

    public static void setHighMem() {
        SceneGraph.lowMem = false;
        Rasterizer3D.lowMem = false;
        ClientCompanion.lowMemory = false;
        MapRegion.isLowDetail = false;
        OSObjectDefinition.ObjectDefinition_isLowDetail = false;
        ObjectDefinition.ObjectDefinition_isLowDetail = false;
    }

    /**
     * Initializes the client for startup
     */
    private void initialize() {
        try {
            ClientCompanion.nodeID = 10;
            ClientCompanion.portOffset = 0;
            setHighMem();
            ClientCompanion.isMembers = true;
            SignLink.storeid = 32;
            SignLink.startpriv(InetAddress.getLocalHost());
        } catch (Exception exception) {
            Log.error("Failed to initialise client!", exception);
        }
    }

    public static GraphicsBuffer constructGraphicsBuffer(int x, int y) {
        return constructGraphicsBuffer(x, y, false);
    }

    public static GraphicsBuffer constructGraphicsBuffer(int x, int y, boolean depthBuffering) {

        GraphicsBuffer graphicClass;
        try {
            Class var_class = Class.forName("com.runescape.draw.BasicGraphicsBuffer");
            GraphicsBuffer instance = (GraphicsBuffer) var_class.newInstance();
            instance.init(Client.instance.canvas, x, y, false, depthBuffering);
            graphicClass = instance;
        } catch (Throwable throwable) {
            ProducingGraphicsBuffer graphicInstance = new ProducingGraphicsBuffer();
            graphicInstance.init(instance.canvas, x, y, false, depthBuffering);
            return graphicInstance;
        }
        return graphicClass;
    }

    public void setMaxCanvasSize() {
        if (ClientUI.frameMode == Client.ScreenMode.FIXED) {
            instance.setMaxCanvasSize(765, 503);
        } else
            instance.setMaxCanvasSize(7680, 2160);
    }


    public void drawLoadingMessages() {
        int width = regularText.getTextWidth("Loading - please wait.");
        int height = 25;
        Rasterizer2D.drawBox(1, 1, width + 6, height, 0);
        Rasterizer2D.drawBox(1, 1, width + 6, 1, 0xffffff);
        Rasterizer2D.drawBox(1, 1, 1, height, 0xffffff);
        Rasterizer2D.drawBox(1, height, width + 6, 1, 0xffffff);
        Rasterizer2D.drawBox(width + 6, 1, 1, height, 0xffffff);
        regularText.drawText(0xffffff, "Loading - please wait.", 18, width / 2 + 5);
    }

    private void drawLoadingMessage(String text) {
        int width = regularText.getTextWidth(text);
        int height = 25;
        Rasterizer2D.drawBox(1, 1, width + 6, height, 0);
        Rasterizer2D.drawBox(1, 1, width + 6, 1, 0xffffff);
        Rasterizer2D.drawBox(1, 1, 1, height, 0xffffff);
        Rasterizer2D.drawBox(1, height, width + 6, 1, 0xffffff);
        Rasterizer2D.drawBox(width + 6, 1, 1, height, 0xffffff);
        regularText.drawText(0xffffff, text, 18, width / 2 + 5);
    }

    public boolean menuHasAddFriend(int j) {
        if (j < 0)
            return false;
        int k = menuOpcodes[j];
        if (k >= 2000)
            k -= 2000;
        return k == 337;
    }

    private boolean menuIsBankTabButton(int j) {
        if (j < 0)
            return false;
        int k = menuArguments2[j];
        return Bank.isBankTabButton(k, false);
    }

    public boolean chatStateCheck() {
        return messagePromptRaised || inputDialogState != 0 || clickToContinueString != null || backDialogueId != -1
                || dialogueId != -1 || isSearchOpen();
    }

    public boolean isChatBoxResizable() {
        return ClientUI.frameMode != ScreenMode.FIXED && ChatBox.changeChatArea && !chatStateCheck() && ClientCompanion.openInterfaceId == -1;
    }

    public int getChatBoxHeight() {
        return isChatBoxResizable() ? ChatBox.chatBoxHeight : ChatBox.chatScrollHeight;
    }

    public int resizeChatOffset() {
        return !displayChatComponents() ? 0 : getChatBoxHeight() - ChatBox.chatScrollHeight;
    }

    public int getResizeChatButtonX() {
        return 255;
    }

    public int getResizeChatButtonY() {
        return ClientUI.frameHeight - 165 - 6 - resizeChatOffset();
    }

    public static void drawSprite(int spriteId, int xOffset, int yOffset) {
        Sprite sprite = SpriteLoader.getSprite(spriteId);
        if (sprite != null)
            sprite.drawSprite(xOffset, yOffset);
        else
            System.err.println("Could not draw sprite[" + spriteId + "] (sprite is null)!");
    }

    public static void drawAdvancedSprite(int spriteId, int xOffset, int yOffset, int alpha) {
        Sprite sprite = SpriteLoader.getSprite(spriteId);
        if (sprite != null)
            sprite.drawAdvancedSprite(xOffset, yOffset, alpha);
        else
            System.err.println("Could not draw sprite[" + spriteId + "] (sprite is null)!");
    }

    public static int getSpriteHeight(int spriteId) {
        Sprite sprite = SpriteLoader.getSprite(spriteId);
        if (sprite != null)
            return sprite.myHeight;
        else
            return 0;
    }

    public static int getSpriteWidth(int spriteId) {
        Sprite sprite = SpriteLoader.getSprite(spriteId);
        if (sprite != null)
            return sprite.myWidth;
        else
            return 0;
    }

    public int getModIcon(int rights) {
        return getModIcon(rights, 0);
    }

    public int getModIcon(int myPrivilege, int myCrown) {
        int crown = myCrown > 0 ? myCrown : myPrivilege;
        return crown - 1;
    }

    public int parseRights(String chatName, int end) {
        try {
            return Integer.parseInt(chatName.substring(3, end));
        } catch (Exception e) {
            return 0;
        }
    }

    public int getClientRights() {
        return clientRights;
    }

    public boolean isStaff() {
        return clientRights >= Rights.SERVER_SUPPORTER.ordinal() && clientRights <= Rights.DEVELOPER.ordinal();
    }


    public boolean isHighStaff() {
        return clientRights >= Rights.CO_OWNER.ordinal() && clientRights <= Rights.DEVELOPER.ordinal();
    }

    public boolean isAdmin() {
        return clientRights >= Rights.ADMINISTRATOR.ordinal() && clientRights <= Rights.DEVELOPER.ordinal();
    }

    public boolean isMember() {
        return (clientRights >= Rights.BRONZE_MEMBER.ordinal() && clientRights <= Rights.DIAMOND_MEMBER.ordinal()) || Integer.parseInt(Widget.interfaceCache[31414].defaultText.substring(1)) >= 9;
    }

    public Socket createSocket(int port) throws IOException {
        return new Socket(InetAddress.getByName(Configuration.connected_world.getAddress()), port);
    }

    public void processInterfaceClick() {
        /*
         * Mouse press
         */
        if (MouseHandler.lastButton == 1) {
            ClientCompanion.interfaceInputSelected = -1;
            handleInterfaceClick();
        }

        /*
         * Right click
         */
        if (MouseHandler.lastButton == 2) {
            ClientCompanion.interfaceInputSelected = -1;
        }

        /*
         * Mouse release
         */
        if (MouseHandler.currentButton == 0) {
            ColorPicker.setSelected(null, -1, -1, -1, -1);
            if (Slider.getSelectedSlider() != null) {
                Slider.setSelected(null, -1, -1);
                playSound(2266);
            }
        }

        /*
         * Handle dragging
         */
        if (ColorPicker.getSelectedPickerType() != null) {
            ColorPicker.handleDragging();
        } else if (Slider.getSelectedSlider() != null) {
            Slider.handleDragging();
        }
    }

    private void handleInterfaceClick() {
        int offsetX;
        int offsetY;
        int tabInterfaceID = ClientCompanion.tabInterfaceIDs[ClientCompanion.tabId];
        int x = (ClientUI.frameWidth / 2) - 240;
        int y = (ClientUI.frameHeight / 2) - 167;

        if (tabInterfaceID != -1) {
            Widget tab = Widget.interfaceCache[tabInterfaceID];

            if (tab != null) {
                offsetX = ClientUI.frameMode == ScreenMode.FIXED ? 516 + 31 : ClientUI.frameWidth - 215;
                offsetY = ClientUI.frameMode == ScreenMode.FIXED ? 168 + 37 : ClientUI.frameHeight - 299;
                if (displaySideStonesStacked()) {
                    offsetX = ClientUI.frameWidth - 197;
                    offsetY = ClientUI.frameWidth >= 1000 ? ClientUI.frameHeight - 303 : ClientUI.frameHeight - 340;
                }
                handleClick(tab, offsetX, offsetY);
            }
        }

        if (ClientCompanion.openInterfaceId != -1) {
            int w = 512, h = 334;
            int count = displaySideStonesStacked() ? 3 : 4;
            if (ClientUI.frameMode != Client.ScreenMode.FIXED) {
                if (Widget.interfaceCache[ClientCompanion.openInterfaceId].width > w || Widget.interfaceCache[ClientCompanion.openInterfaceId].height > h) {
                    x = ClientUtil.getLargeResizableInterfaceOffsetLeftX();
                    y = ClientUtil.getLargeResizableInterfaceOffsetTopY();
                }
                for (int i = 0; i < count; i++) {
                    if (x + w > (ClientUI.frameWidth - 225)) {
                        x = x - 30;
                        if (x < 0) {
                            x = 0;
                        }
                    }
                    if (y + h > (ClientUI.frameHeight - 182)) {
                        y = y - 30;
                        if (y < 0) {
                            y = 0;
                        }
                    }
                }
            }

            Widget tab = Widget.interfaceCache[ClientCompanion.openInterfaceId];

            if (tab != null) {
                offsetX = ClientUI.frameMode == Client.ScreenMode.FIXED ? 4 : x;
                offsetY = ClientUI.frameMode == Client.ScreenMode.FIXED ? 4 : y;

                handleClick(tab, offsetX, offsetY);
            }
        }

        if (ClientCompanion.openInterfaceId2 != -1) {
            int w = 512, h = 334;
            x = ClientUI.frameMode == ScreenMode.FIXED ? 0 : (ClientUI.frameWidth / 2) - 256;
            y = ClientUI.frameMode == ScreenMode.FIXED ? 0 : (ClientUI.frameHeight / 2) - 167;
            int count = displaySideStonesStacked() ? 3 : 4;
            if (ClientUI.frameMode != ScreenMode.FIXED) {
                for (int i = 0; i < count; i++) {
                    if (x + w > (ClientUI.frameWidth - 225)) {
                        x = x - 30;
                        if (x < 0) {
                            x = 0;
                        }
                    }
                    if (y + h > (ClientUI.frameHeight - 182)) {
                        y = y - 30;
                        if (y < 0) {
                            y = 0;
                        }
                    }
                }
            }

            Widget tab = Widget.interfaceCache[ClientCompanion.openInterfaceId2];

            if (tab != null) {
                offsetX = ClientUI.frameMode == Client.ScreenMode.FIXED ? 4 : x;
                offsetY = ClientUI.frameMode == Client.ScreenMode.FIXED ? 4 : y;

                handleClick(tab, offsetX, offsetY);
            }
        }
    }

    private void handleClick(Widget widget, int offsetX, int offsetY) {
        if (widget.children == null) {
            return;
        }

        int positionX;
        int positionY;

        for (int index = 0; index < widget.children.length; index++) {
            Widget child = Widget.interfaceCache[widget.children[index]];

            if (child == null)
                continue;

            positionX = widget.childX[index] + child.horizontalOffset;
            positionY = widget.childY[index] + child.verticalOffset;

            if (MouseHandler.x > offsetX + positionX && MouseHandler.y > offsetY + positionY && MouseHandler.x < offsetX + positionX + child.width && MouseHandler.y < offsetY + positionY + child.height) {
                // Process color choosers
                if (child.atActionType == Widget.OPTION_COLOR_PICKER) {
                    int sbWidth = child.width - 31; // including outline
                    int hueWidth = 21; // including outline
                    if (MouseHandler.x >= offsetX + positionX + 1 && MouseHandler.y >= offsetY + positionY + 1
                            && MouseHandler.x < offsetX + positionX + 1 + sbWidth - 2
                            && MouseHandler.y < offsetY + positionY + 1 + child.height - 2) { // Saturation/Brightness chooser
                        ColorPicker.setSelected(ColorPicker.PickerType.SATURATION_BRIGHTNESS, offsetX + positionX + 1, offsetY + positionY + 1, MouseHandler.x, MouseHandler.y);
                        return;
                    } else if (MouseHandler.x >= offsetX + positionX + 1 + sbWidth - 2 + 10 + 1 && MouseHandler.y >= offsetY + positionY + 1
                            && MouseHandler.x < offsetX + positionX + 1 + sbWidth - 2 + 10 + 1 + hueWidth - 2
                            && MouseHandler.y < offsetY + positionY + 1 + child.height - 2) { // Hue chooser
                        ColorPicker.setSelected(ColorPicker.PickerType.HUE, offsetX + positionX + 1, offsetY + positionY + 1, MouseHandler.x, MouseHandler.y);
                        return;
                    }
                }

                // Process inputs
                if (child.atActionType == Widget.OPTION_INPUT) {
                    if (child.id == YellCustomizer.INPUT_ID && !isHighStaff() && !isMember()) {
                        return;
                    }
                    ClientCompanion.interfaceInputSelected = child.id;
                    return;
                }

                // Process sliders
                if (child.type == Widget.TYPE_SLIDER) {
                    Slider.setSelected(child, offsetX + positionX, MouseHandler.x);
                    return;
                }
            }

            if (child.children != null) {
                handleClick(child, offsetX + widget.childX[index] + child.horizontalOffset, offsetY + widget.childY[index] + child.verticalOffset);
            }
        }
    }

    public void loadRegion() {
        try {

            System.out.println("Client.loadRegion");
            Audio.playPcmPlayers();
            lastKnownPlane = -1;
            incompleteAnimables.clear();
            projectiles.clear();
            Rasterizer3D.clearTextureCache();
            unlinkCaches();
            scene.clear();

            for (int i = 0; i < 4; i++)
                collisionMaps[i].clear();
            for (int l = 0; l < 4; l++) {
                for (int k1 = 0; k1 < 104; k1++) {
                    for (int j2 = 0; j2 < 104; j2++)
                        tileFlags[l][k1][j2] = 0;
                }
            }

            Audio.playPcmPlayers();
            Audio.clearObjectSounds();

            MapRegion objectManager = new MapRegion(tileFlags, Tiles_heights);
            int k2 = regionLandArchives.length;
            sendPacket(new BasicPing().create());

            if (!isInInstance) {
                for (int i3 = 0; i3 < k2; i3++) {
                    int i4 = (regions[i3] >> 8) * 64 - baseX;
                    int k5 = (regions[i3] & 0xff) * 64 - baseY;
                    byte[] abyte0 = regionLandArchives[i3];
                    if (abyte0 != null) {
                        Audio.playPcmPlayers();
                        objectManager.method180(regions[i3], abyte0, k5, i4, (currentRegionX - 6) * 8, (currentRegionY - 6) * 8,
                                collisionMaps);
                    }
                }
                for (int j4 = 0; j4 < k2; j4++) {
                    int l5 = (regions[j4] >> 8) * 64 - baseX;
                    int k7 = (regions[j4] & 0xff) * 64 - baseY;
                    byte[] abyte2 = regionLandArchives[j4];
                    if (abyte2 == null && currentRegionY < 800) {
                        Audio.playPcmPlayers();
                        objectManager.initiateVertexHeights(k7, 64, 64, l5);
                    }
                }

                sendPacket(new BasicPing().create());
                for (int i6 = 0; i6 < k2; i6++) {
                    byte[] data = regionMapArchives[i6];
                    if (data != null) {
                        int regionX = (regions[i6] >> 8) * 64 - baseX;
                        int regionY = (regions[i6] & 0xff) * 64 - baseY;
                        Audio.playPcmPlayers();
                        objectManager.loadObjectsInScene(regionX, collisionMaps, regionY, scene, data);
                    }
                }
            } else {
                for (int plane = 0; plane < 4; plane++) {
                    for (int x = 0; x < 13; x++) {
                        for (int y = 0; y < 13; y++) {
                            int chunkBits = instanceChunkTemplates[plane][x][y];
                            if (chunkBits != -1) {
                                int z = chunkBits >> 24 & 3;
                                int rotation = chunkBits >> 1 & 3;
                                int xCoord = chunkBits >> 14 & 0x3ff;
                                int yCoord = chunkBits >> 3 & 0x7ff;
                                int mapRegion = (xCoord / 8 << 8) + yCoord / 8;
                                for (int idx = 0; idx < regions.length; idx++) {
                                    if (regions[idx] != mapRegion || regionLandArchives[idx] == null)
                                        continue;
                                    objectManager.loadMapChunk(regions[idx], z, rotation, collisionMaps, x * 8, (xCoord & 7) * 8,
                                            regionLandArchives[idx], (yCoord & 7) * 8, plane, y * 8);
                                    break;
                                }

                            }
                        }
                    }
                }
                for (int xChunk = 0; xChunk < 13; xChunk++) {
                    for (int yChunk = 0; yChunk < 13; yChunk++) {
                        int tileBits = instanceChunkTemplates[0][xChunk][yChunk];
                        if (tileBits == -1)
                            objectManager.initiateVertexHeights(yChunk * 8, 8, 8, xChunk * 8);
                    }
                }

                sendPacket(new BasicPing().create());
                for (int chunkZ = 0; chunkZ < 4; chunkZ++) {

                    Audio.playPcmPlayers();

                    for (int chunkX = 0; chunkX < 13; chunkX++) {
                        for (int chunkY = 0; chunkY < 13; chunkY++) {
                            int tileBits = instanceChunkTemplates[chunkZ][chunkX][chunkY];
                            if (tileBits != -1) {
                                int plane = tileBits >> 24 & 3;
                                int rotation = tileBits >> 1 & 3;
                                int coordX = tileBits >> 14 & 0x3ff;
                                int coordY = tileBits >> 3 & 0x7ff;
                                int mapRegion = (coordX / 8 << 8) + coordY / 8;
                                for (int idx = 0; idx < regions.length; idx++) {
                                    if (regions[idx] != mapRegion || regionMapArchives[idx] == null)
                                        continue;
                                    objectManager.readObjectMap(collisionMaps, scene, plane, chunkX * 8,
                                            (coordY & 7) * 8, chunkZ, regionMapArchives[idx], (coordX & 7) * 8, rotation,
                                            chunkY * 8);
                                    break;
                                }
                            }
                        }
                    }
                }
                // isInInstance = false;
            }
            sendPacket(new BasicPing().create());
            Audio.playPcmPlayers();
            //loadCustomObjects();

            underlays = objectManager.underlays;
            overlays = objectManager.overlays;
            objectManager.createRegionScene(collisionMaps, scene);
            ClientCompanion.gameScreenImageProducer.initDrawingArea();
            sendPacket(new BasicPing().create());
            int k3 = MapRegion.Tiles_minPlane;

            if (k3 > plane)
                k3 = plane;
            if (ClientCompanion.lowMemory)
                scene.method275(MapRegion.Tiles_minPlane);
            else
                scene.method275(0);
            for (int i5 = 0; i5 < 104; i5++) {
                for (int i7 = 0; i7 < 104; i7++)
                    updateGroundItems(i5, i7);

            }

            ClientCompanion.anInt1051++;
            if (ClientCompanion.anInt1051 > 98) {
                ClientCompanion.anInt1051 = 0;
            }

            clearObjectSpawnRequests();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (OSObjectDefinition.USE_OSRS)
            OSObjectDefinition.baseModels.clear();
        else
            ObjectDefinition.ObjectDefinition_cachedModelData.clear();

        if (loggedIn) {
            sendPacket(new RegionChange().create());
        }
        if (ClientCompanion.lowMemory && SignLink.cache_dat != null) {
            int j = resourceProvider.getVersionCount(0);
            for (int i1 = 0; i1 < j; i1++) {
                int l1 = resourceProvider.getModelIndex(i1);
                if ((l1 & 0x79) == 0)
                    Model.resetModelHeader(i1);
            }

        }

        Rasterizer3D.tryInitiateTextureCache();
        resourceProvider.clearExtras();

        int startRegionX = (currentRegionX - 6) / 8 - 1;
        int endRegionX = (currentRegionX + 6) / 8 + 1;
        int startRegionY = (currentRegionY - 6) / 8 - 1;
        int endRegionY = (currentRegionY + 6) / 8 + 1;
        for (int regionX = startRegionX; regionX <= endRegionX; regionX++) {
            for (int regionY = startRegionY; regionY <= endRegionY; regionY++) {
                if (regionX == startRegionX || regionX == endRegionX || regionY == startRegionY
                        || regionY == endRegionY) {
                    int floorMapId = resourceProvider.resolve(0, regionY, regionX);
                    if (floorMapId != -1) {
                        resourceProvider.loadExtra(floorMapId, 3);
                    }
                    int objectMapId = resourceProvider.resolve(1, regionY, regionX);
                    if (objectMapId != -1) {
                        resourceProvider.loadExtra(objectMapId, 3);
                    }
                }
            }
        }


    }

    private void loadCustomObjects() {
        //addObject(10660, 3085, 3497, 0, 0, 10);
    }

    private void unlinkCaches() {

//        ((TextureProvider)Rasterizer3D.Rasterizer3D_textureLoader).clear();
        SequenceDefinition.SequenceDefinition_cached.clear();
        Frames.frameCache.clear();
        OsCache.clear();
        if (!OSObjectDefinition.USE_OSRS) {
            ObjectDefinition.ObjectDefinition_cachedModelData.clear();
            ObjectDefinition.ObjectDefinition_cachedModels.clear();
        }
        ItemDefinition.ItemDefinition_cachedModels.clear();
        ItemDefinition.sprites.clear();
        Player.PlayerAppearance_cachedModels.clear();
        Graphic.SpotAnimationDefinition_cachedModels.clear();
        FloorOverlayDefinition.FloorOverlayDefinition_cached.clear();
        FloorUnderlayDefinition.FloorUnderlayDefinition_cached.clear();
    }

    private void renderMapScene(int plane) {
        int[] pixels = SpriteCompanion.minimapImage.myPixels;
        int length = pixels.length;

        for (int pixel = 0; pixel < length; pixel++) {
            pixels[pixel] = 0;
        }

        for (int y = 1; y < 103; y++) {
            int i1 = 24628 + (103 - y) * 512 * 4;
            for (int x = 1; x < 103; x++) {
                if ((tileFlags[plane][x][y] & 0x18) == 0)
                    scene.drawTileOnMinimapSprite(pixels, i1, plane, x, y);
                if (plane < 3 && (tileFlags[plane + 1][x][y] & 8) != 0)
                    scene.drawTileOnMinimapSprite(pixels, i1, plane + 1, x, y);
                i1 += 4;
            }

        }

        int j1 = 0xFFFFFF;
        int l1 = 0xEE0000;
        SpriteCompanion.minimapImage.init();

        for (int y = 1; y < 103; y++) {
            for (int x = 1; x < 103; x++) {
                if ((tileFlags[plane][x][y] & 0x18) == 0)
                    drawMapScenes(y, j1, x, l1, plane);
                if (plane < 3 && (tileFlags[plane + 1][x][y] & 8) != 0)
                    drawMapScenes(y, j1, x, l1, plane + 1);
            }

        }

        ClientCompanion.gameScreenImageProducer.initDrawingArea();
        GameFrame.mapIconCount = 0;

        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {

                long i3 = scene.getGroundDecorationUid(plane, x, y);
                if (i3 != 0) {
                    int id = DynamicObject.get_object_key(i3);

                    int function = ObjectDefinition.lookup(id).minimapFunction;

                    if (function >= 0) {
                        int sprite = AreaDefinition.lookup(function).spriteId;
                        if (sprite != -1) {
                            SpriteCompanion.mapIcons[GameFrame.mapIconCount] = AreaDefinition.getImage(sprite);
                            GameFrame.mapIconXs[GameFrame.mapIconCount] = x;
                            GameFrame.mapIconYs[GameFrame.mapIconCount] = y;
                            GameFrame.mapIconCount++;

                        }

                    }
                }
            }

        }

        if (Configuration.dumpMapRegions) {

            File directory = new File("MapImageDumps/");
            if (!directory.exists()) {
                directory.mkdir();
            }
            BufferedImage bufferedimage = new BufferedImage(SpriteCompanion.minimapImage.myWidth, SpriteCompanion.minimapImage.myHeight, 1);
            bufferedimage.setRGB(0, 0, SpriteCompanion.minimapImage.myWidth, SpriteCompanion.minimapImage.myHeight, SpriteCompanion.minimapImage.myPixels, 0,
                    SpriteCompanion.minimapImage.myWidth);
            Graphics2D graphics2d = bufferedimage.createGraphics();
            graphics2d.dispose();
            try {
                File file1 = new File("MapImageDumps/" + (directory.listFiles().length + 1) + ".png");
                ImageIO.write(bufferedimage, "png", file1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateGroundItems(int x, int y) {
        NodeDeque groundItems = this.groundItems[plane][x][y];
        if (groundItems == null) {
            scene.removeGroundItemPile(plane, x, y);
            return;
        }
        int var3 = Integer.MIN_VALUE;

        Item obj = null;
        for (Item item = (Item) groundItems.last(); item != null; item = (Item) groundItems.previous()) {
            ItemDefinition itemDef = ItemDefinition.lookup(item.ID);
            int l = itemDef.value;
            if (itemDef.stackable)
                l *= item.itemCount + 1;
            if (l >= var3) {
                var3 = l;
                obj = item;
            }
        }

        groundItems.addLast(obj);
        Item obj1 = null;
        Renderable obj2 = null;
        for (Item item = (Item) groundItems.last(); item != null; item = (Item) groundItems.previous()) {
            if (item.ID != obj.ID && obj1 == null)
                obj1 = item;
            if (item.ID != obj.ID && item.ID != obj1.ID
                    && obj2 == null)
                obj2 = item;
        }
        long tag;
        if (OSObjectDefinition.USE_OSRS) {
            tag = ViewportMouse.toTag(x, y, 3, false, 0);
        } else {
            tag = x + (y << 7) + 0x60000000;
        }
        scene.newGroundItemPile(x, tag, obj1, getTileHeight(plane, y * 128 + 64, x * 128 + 64),
                obj2, obj, plane, y);
    }

    private boolean prioritizedNpc(Npc npc) {

        // Check if it's being interacted with
        if (localPlayer.targetIndex != -1 && localPlayer.targetIndex < 32768) {
            if (npc.index == localPlayer.targetIndex) {
                return true;
            }
        }

        if (npc.desc == null) {
            return false;
        }

        return npc.desc.priorityRender;
    }

    private void showPrioritizedNPCs() {
        for (int index = 0; index < npcCount; index++) {
            Npc npc = npcs[npcIndices[index]];

            if (prioritizedNpc(npc)) {
                drawNpcs(npc, index, npc.desc.priorityRender);
            }
        }
    }

    private void showOtherNpcs() {
        for (int index = 0; index < npcCount; index++) {
            Npc npc = npcs[npcIndices[index]];
            drawNpcs(npc, index, false);
        }
    }

    private void drawNpcs(Npc npc, int index, boolean priorityRender) {

        if (npc == null || !npc.isVisible() || npc.desc.priorityRender != priorityRender)
            return;
        int x = npc.x >> 7;
        int y = npc.y >> 7;
        if (x < 0 || x >= 104 || y < 0 || y >= 104)
            return;
        if (npc.size == 1 && (npc.x & 0x7f) == 64 && (npc.y & 0x7f) == 64) {
            if (tileLastDrawnActor[x][y] == viewportDrawCount)
                return;
            tileLastDrawnActor[x][y] = viewportDrawCount;
        }

        long tag;
        if (OSObjectDefinition.USE_OSRS) {
            tag = ViewportMouse.toTag(0, 0, 1, !npc.desc.clickable, npcIndices[index]);
        } else {
            tag = 0x20000000L | (long) (npcIndices[index] << 14);
            if (!npc.desc.clickable)
                tag |= ~0x7fffffffffffffffL;
        }
        scene.addAnimableA(plane, npc.orientationScene,
                getTileHeight(plane, npc.y, npc.x), tag, npc.y,
                (npc.size - 1) * 64 + 60, npc.x, npc, npc.animationStretches);
    }

    public void drawHoverBox(int xPos, int yPos, String text) {
        String[] results = text.split("\n");
        int height = (results.length * 16) + 6;
        int width;
        width = regularText.getTextWidth(results[0]) + 6;
        for (int i = 1; i < results.length; i++)
            if (width <= regularText.getTextWidth(results[i]) + 6)
                width = regularText.getTextWidth(results[i]) + 6;
        Rasterizer2D.drawBox(xPos, yPos, width, height, 0xFFFFA0);
        Rasterizer2D.drawBoxOutline(xPos, yPos, width, height, 0);
        yPos += 14;
        for (String result : results) {
            regularText.drawTextWithPotentialShadow(result, xPos + 3, yPos, 0, false);
            yPos += 16;
        }
    }

    public void updateNPCs(Buffer stream, int i, boolean largeScene) {
        int streamOnset = stream.index;
        int npcListOnset = -1;
        int npcBlockOnset = -1;
        try {
            removedMobCount = 0;
            mobsAwaitingUpdateCount = 0;
            updateNpcMovement(stream);
            npcListOnset = stream.index;
            updateNpcList(i, stream, largeScene);
            npcBlockOnset = stream.index;
            updateNpcBlocks(stream);
            for (int k = 0; k < removedMobCount; k++) {
                int l = removedMobs[k];
                if (npcs[l].npcCycle != tick) {
                    npcs[l].desc = null;
                    npcs[l] = null;
                }
            }
        } catch (Exception e) {
            if (isHighStaff()) {
                final NpcUpdateSnapshot snapshot = new NpcUpdateSnapshot(
                        packetSize,
                        streamOnset,
                        npcListOnset,
                        npcBlockOnset,
                        npcCount,
                        removedMobCount,
                        mobsAwaitingUpdateCount
                );
                SnapshotCache.store(snapshot);
            }
            throw e;
        }

        if (stream.index != i) {
            Log.error(myUsername + " size mismatch in getnpcpos - pos:" + stream.index + " psize:" + i);
            throw new RuntimeException("eek");
        }
        for (int i1 = 0; i1 < npcCount; i1++)
            if (npcs[npcIndices[i1]] == null) {
                Log.error(myUsername + " null entry in npc list - pos:" + i1 + " size:" + npcCount);
                throw new RuntimeException("eek");
            }

    }

    public void toggleMusicMute() {
        Audio.changeMusicVolume(this, Audio.musicNotMuted() ? 0 : 255);
    }

    public int getMusicVolume() {
        return SignLink.midiVolume;
    }

    public void updateVarp(int id) {

        if (VariablePlayer.variables == null) {
            return;
        }
        int parameter = VariablePlayer.variables[id].getActionId();

        if (parameter == 0) {
            return;
        }

        int state = settings[id];

        Log.info("new varp[" + id + "] = " + parameter + ", " + state);

        if (parameter == 1) {

            if (state == 1) {
                Rasterizer3D.changeBrightness(0.9);
                PlayerSettings.savePlayerData(this);
            }

            if (state == 2) {
                Rasterizer3D.changeBrightness(0.8);
                PlayerSettings.savePlayerData(this);
            }

            if (state == 3) {
                Rasterizer3D.changeBrightness(0.7);
                PlayerSettings.savePlayerData(this);
            }

            if (state == 4) {
                Rasterizer3D.changeBrightness(0.6);
                PlayerSettings.savePlayerData(this);
            }

            ItemDefinition.sprites.clear();
            welcomeScreenRaised = true;
        }

        if (parameter == 4) {
            if (state == 0) {
                Audio.soundEffectVolume = 127;
            }
            if (state == 1) {
                Audio.soundEffectVolume = 96;
            }
            if (state == 2) {
                Audio.soundEffectVolume = 64;
            }
            if (state == 3) {
                Audio.soundEffectVolume = 32;
            }
            if (state == 4) {
                Audio.soundEffectVolume = 0;
            }
        }

        if (parameter == 5) {
            mouseButtonSetting = state;
        }

        if (parameter == 6) {
            textAboveHeadEffectState = state;
        }

        if (parameter == 8) {
            splitPrivateChat = state;
            ChatBox.setUpdateChatbox(true);
        }

        if (parameter == 9) {
            anInt913 = state;
        }

        if (parameter == 3) {
            Audio.updateMusicTrackVolume(state);
        }
        if (parameter == 4) {
            Audio.updateSoundEffectVolume(state);
        }
        if (parameter == 10) {
            Audio.updateObjectSoundVolume(state);
        }
    }

    private void updateEntities() {
        try {
            int textAboveHeadCount = 0;

            for (int entityIndex = -1; entityIndex < Players_count + npcCount; entityIndex++) {
                Mob entity;

                if (entityIndex == -1)
                    entity = localPlayer;
                else if (entityIndex < Players_count)
                    entity = players[Players_indices[entityIndex]];
                else
                    entity = npcs[npcIndices[entityIndex - Players_count]];

                if (entity == null || !entity.isVisible())
                    continue;

                if (entity instanceof Npc && updateNpc((Npc) entity))
                    continue;

                if (entityIndex < Players_count)
                    mobOverlayRenderer.drawPlayerHeadIcons(this, entityIndex, entity);
                else
                    mobOverlayRenderer.drawNPCHeadIcons(this, npcIndices[entityIndex - Players_count], entity);

                textAboveHeadCount = mobOverlayRenderer.handleTextAboveHead(this, textAboveHeadCount, entityIndex, entity);
                mobOverlayRenderer.drawHitPointsBar(this, entity);
                CombatBox.handleCombatBoxTimers(this, entity);

                mobOverlayRenderer.drawHitMarks(this, entity);
            }
            mobOverlayRenderer.drawTextAboveHead(this, textAboveHeadCount);
        } catch (Exception e) {
            Log.error("Error during entity drawing updates", e);
        }
    }

    private boolean updateNpc(Npc entity) {

        NpcDefinition npcDefinition = entity.desc;
        int offset = 15;

        mobOverlayRenderer.drawTextAboveNPCHead(this, entity, npcDefinition, offset);

        if (npcDefinition.transforms != null)
            npcDefinition = npcDefinition.transform();

        return npcDefinition == null;
    }

    private void processMobChatText() {
        for (int i = -1; i < Players_count; i++) {
            int j;
            if (i == -1)
                j = LOCAL_PLAYER_ID;
            else
                j = Players_indices[i];
            Player player = players[j];
            if (player != null && player.textCycle > 0) {
                player.textCycle--;
                if (player.textCycle == 0)
                    player.spokenText = null;
            }
        }
        for (int k = 0; k < npcCount; k++) {
            int l = npcIndices[k];
            Npc npc = npcs[l];
            if (npc != null && npc.textCycle > 0) {
                npc.textCycle--;
                if (npc.textCycle == 0)
                    npc.spokenText = null;
            }
        }
    }

    private void calculateCameraPosition() {
        int worldX = x * 128 + 64;
        int worldY = y * 128 + 64;
        int height = getTileHeight(plane, worldY, worldX) - this.height;
        if (cameraX < worldX) {
            cameraX += speed + ((worldX - cameraX) * angle) / 1000;
            if (cameraX > worldX)
                cameraX = worldX;
        }
        if (cameraX > worldX) {
            cameraX -= speed + ((cameraX - worldX) * angle) / 1000;
            if (cameraX < worldX)
                cameraX = worldX;
        }
        if (cameraY < height) {
            cameraY += speed + ((height - cameraY) * angle) / 1000;
            if (cameraY > height)
                cameraY = height;
        }
        if (cameraY > height) {
            cameraY -= speed + ((cameraY - height) * angle) / 1000;
            if (cameraY < height)
                cameraY = height;
        }
        if (cameraZ < worldY) {
            cameraZ += speed + ((worldY - cameraZ) * angle) / 1000;
            if (cameraZ > worldY)
                cameraZ = worldY;
        }
        if (cameraZ > worldY) {
            cameraZ -= speed + ((cameraZ - worldY) * angle) / 1000;
            if (cameraZ < worldY)
                cameraZ = worldY;
        }
        worldX = Camera.cinematicCamXViewpointLoc * 128 + 64;
        worldY = Camera.cinematicCamYViewpointLoc * 128 + 64;
        height = getTileHeight(plane, worldY, worldX) - Camera.cinematicCamZViewpointLoc;
        int l = worldX - cameraX;
        int i1 = height - cameraY;
        int j1 = worldY - cameraZ;
        int k1 = (int) Math.sqrt(l * l + j1 * j1);
        int l1 = (int) (Math.atan2(i1, k1) * 325.94900000000001D) & 0x7ff;
        int i2 = (int) (Math.atan2(l, j1) * -325.94900000000001D) & 0x7ff;
        if (l1 < 128)
            l1 = 128;
        if (l1 > 383)
            l1 = 383;
        if (cameraPitch < l1) {
            cameraPitch += Camera.constCinematicCamRotationSpeed
                    + ((l1 - cameraPitch) * Camera.varCinematicCamRotationSpeedPromille) / 1000;
            if (cameraPitch > l1)
                cameraPitch = l1;
        }
        if (cameraPitch > l1) {
            cameraPitch -= Camera.constCinematicCamRotationSpeed
                    + ((cameraPitch - l1) * Camera.varCinematicCamRotationSpeedPromille) / 1000;
            if (cameraPitch < l1)
                cameraPitch = l1;
        }
        int j2 = i2 - cameraYaw;
        if (j2 > 1024)
            j2 -= 2048;
        if (j2 < -1024)
            j2 += 2048;
        if (j2 > 0) {
            cameraYaw += Camera.constCinematicCamRotationSpeed + (j2 * Camera.varCinematicCamRotationSpeedPromille) / 1000;
            cameraYaw &= 0x7ff;
        }
        if (j2 < 0) {
            cameraYaw -= Camera.constCinematicCamRotationSpeed + (-j2 * Camera.varCinematicCamRotationSpeedPromille) / 1000;
            cameraYaw &= 0x7ff;
        }
        int k2 = i2 - cameraYaw;
        if (k2 > 1024)
            k2 -= 2048;
        if (k2 < -1024)
            k2 += 2048;
        if (k2 < 0 && j2 > 0 || k2 > 0 && j2 < 0)
            cameraYaw = i2;
    }

    public void drawMenu(int x, int y) {
        int xPos = GameFrame.menuOffsetX - (x - 4);
        int yPos = (-y + 4) + GameFrame.menuOffsetY;
        int w = GameFrame.menuWidth;
        int h = GameFrame.menuHeight + 1;
        ChatBox.setUpdateChatbox(true);
        ClientCompanion.tabAreaAltered = true;
        int menuColor = 0x5d5447;
        Rasterizer2D.drawBox(xPos, yPos, w, h, menuColor);
        Rasterizer2D.drawBox(xPos + 1, yPos + 1, w - 2, 16, 0);
        Rasterizer2D.drawBoxOutline(xPos + 1, yPos + 18, w - 2, h - 19, 0);
        boldText.render(menuColor, "Choose Option", yPos + 14, xPos + 3);
        int mouseX = MouseHandler.x - (x);
        int mouseY = (-y) + MouseHandler.y;
        for (int i = 0; i < menuOptionsCount; i++) {
            int textY = yPos + 31 + (menuOptionsCount - 1 - i) * 15;
            int textColor = 0xffffff;
            if (mouseX > xPos && mouseX < xPos + w && mouseY > textY - 13 && mouseY < textY + 3) {
                textColor = 0xffff00;
            }

            newBoldFont.drawBasicString(menuActions[i], xPos + 3, textY, textColor, 0, true);
        }
    }

    public void addFriend(long nameHash) {
        if (nameHash == 0L)
            return;
        sendPacket(new AddFriend(nameHash).create());
    }

    public int getTileHeight(int z, int y, int x) {
        int worldX = x >> 7;
        int worldY = y >> 7;
        if (worldX < 0 || worldY < 0 || worldX > 103 || worldY > 103)
            return 0;
        int plane = z;
        if (plane < 3 && (tileFlags[1][worldX][worldY] & 2) == 2)
            plane++;
        int sizeX = x & 0x7f;
        int sizeY = y & 0x7f;
        int i2 = Tiles_heights[plane][worldX][worldY] * (128 - sizeX)
                + Tiles_heights[plane][worldX + 1][worldY] * sizeX >> 7;
        int j2 = Tiles_heights[plane][worldX][worldY + 1] * (128 - sizeX)
                + Tiles_heights[plane][worldX + 1][worldY + 1] * sizeX >> 7;
        return i2 * (128 - sizeY) + j2 * sizeY >> 7;
    }

    public void resetLogout() {
        try {
            if (socketStream != null)
                socketStream.close();
        } catch (Exception ignored) {
        }

        //GrinderScape.discord.update("In Menu..", "");
        LoginScreen.firstLoginMessage = LoginScreen.secondLoginMessage = "";
        EffectTimerManager.effects_list.clear();
        BroadCastManager.broadcast.clear();
        clanMembers.clear();
        socketStream = null;
        loggedIn = false;
        LoginScreen.loginScreenState = 0;
        unlinkCaches();
        scene.clear();
        for (int i = 0; i < 4; i++)
            collisionMaps[i].clear();
        Arrays.fill(chatMessages, null);
        inputString = "";
        System.gc();
        //stopMidi();
        //resetSong();
        //MusicPlayer.method57(10);

        Audio.resetMusicTrack(2);
        Audio.musicTrackId = -1;
        Audio.musicTrackRequestByServer = false;
        Audio.clearObjectSounds();
        if (Configuration.loginMusic) {
            Audio.musicTrackVarpType = 255;
        } else
            Audio.musicTrackVarpType = 0;

        ClientUI.frameMode(ScreenMode.FIXED, false);
        Widget.handleConfigHover(Widget.interfaceCache[SettingsWidget.FIXED_MODE]);

        PlayerSettings.savePlayerData(this);

        CombatBox.combatBoxTimer.stop();
        XpDrops.reset();
        Emojis.menuOpen = false;

        GameShell.updateGameState(10);
    }

    private void updateNpcList(int i, Buffer stream, boolean largeScene) {
        while (stream.bitsRemaining(i) >= 27) {

            int npcIndex = stream.readBits(15);

            if (npcIndex == 32767)
                break;

            boolean addedNpc = false;

            if (npcs[npcIndex] == null) {
                npcs[npcIndex] = new Npc();
                addedNpc = true;
            }

            Npc npc = npcs[npcIndex];
            npcIndices[npcCount++] = npcIndex;
            npc.npcCycle = tick;

            int x;
            if (largeScene) {
                x = stream.readBits(8);
                if (x > 127) x -= 256;
            } else {
                x = stream.readBits(5);
                if (x > 15) x -= 32;
            }

            int requiresBlockUpdate = stream.readBits(1);
            int defaultOrientation = npcDefaultOrientations[stream.readBits(3)];

            if (addedNpc)
                npc.orientation = npc.orientationScene = defaultOrientation;

            if (stream.readBits(1) == 1)
                mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = npcIndex;

            int y;
            if (largeScene) {
                y = stream.readBits(8);
                if (y > 127) y -= 256;
            } else {
                y = stream.readBits(5);
                if (y > 15) y -= 32;
            }

            final int npcId = stream.readBits(Configuration.npcBits);
            npc.desc = NpcDefinition.lookup(npcId);
            npc.size = npc.desc.size;
            npc.degreesToTurn = npc.desc.degreesToTurn;
            if (npc.degreesToTurn == 0)
                npc.orientationScene = 0;
            npc.walkSequence = npc.desc.walkSequence;
            npc.walkTurnSequence = npc.desc.walkTurnSequence;
            npc.walkTurnLeftSequence = npc.desc.walkTurnLeftSequence;
            npc.walkTurnRightSequence = npc.desc.walkTurnRightSequence;
            npc.idleSequence = npc.desc.standAnim;
            npc.turnLeftSequence = npc.desc.turnLeftSequence;
            npc.turnRightSequence = npc.desc.turnRightSequence;
            npc.field1152 = npc.desc.field2004;
            npc.field1153 = npc.desc.field2005;
            npc.field1154 = npc.desc.field2006;
            npc.field1155 = npc.desc.field2007;
            npc.field1200 = npc.desc.field2008;
            npc.field1212 = npc.desc.field2009;
            npc.field1158 = npc.desc.field1989;
            npc.setPos(localPlayer.pathX[0] + x, localPlayer.pathY[0] + y, requiresBlockUpdate == 1);
        }
        stream.disableBitAccess();
    }

    private void processGameLoop() {

        if (ClientCompanion.rsAlreadyLoaded || ClientCompanion.loadingError || ClientCompanion.genericLoadingError)
            return;

        if (gameState == 0) {
            OsCache.processLoading();
            GameShell.resetClock();
        } else if (gameState == 5) {
            LoginScreen.processLoginScreenInput(this);
            OsCache.processLoading();
            GameShell.resetClock();
        } else if (gameState != 10 && gameState != 11) {
            if (gameState == 20) {
                // draw title;
            } else if (gameState == 25) {
                OS_handleMapLoading();
            }
            if (!loggedIn) {
                LoginScreen.processLoginScreenInput(this);
            } else
                mainGameProcessor();
        } else {
            LoginScreen.processLoginScreenInput(this);
        }

        if (gameState == 30) {

        }
        //if (MouseHandler.lastButton == 1) {
           // System.out.println("x: " + MouseHandler.lastClickedX + ", y: " + MouseHandler.lastClickedY);
       // }
        processOnDemandQueue();
    }

    public boolean insideArea(int minX, int maxX, int minY, int yOff, int maxY) {
        return MouseHandler.x > minX
                && MouseHandler.x < maxX
                && MouseHandler.y > ClientUI.frameHeight - minY - (yOff)
                && MouseHandler.y < maxY;
    }

    public static boolean lockModelDrawDistance = false;

    private void handleZooming(int rotation) {

        boolean zoom = ClientUI.frameMode == ScreenMode.FIXED ? (MouseHandler.x < 512) : (MouseHandler.x < ClientUI.frameWidth - 200);
        if (zoom && ClientCompanion.openInterfaceId == -1 && ClientCompanion.openInterfaceId2 == -1) {

            if (ClientUI.frameMode == ScreenMode.RESIZABLE || ClientUI.frameMode == ScreenMode.FULLSCREEN) {
                Client.cameraZoom += rotation * 10;
            } else {
                Client.cameraZoom += rotation * 35;
            }

            int max_zoom_1 = -300;
            if (Client.cameraZoom < max_zoom_1)
                Client.cameraZoom = max_zoom_1;

            if (Client.cameraZoom > MAX_CAMERA_ZOOM) {
                Client.cameraZoom = MAX_CAMERA_ZOOM;
            }
            if (Client.cameraZoom < 0)
                Client.cameraZoom = 0;

            if (fps > 20) {
                if (fps > 30)
                    lockModelDrawDistance = false;
                if (!lockModelDrawDistance) {
                    if (Client.cameraZoom > 1030) Model.MODEL_DRAW_DISTANCE = 3500 + 3 * Client.cameraZoom;
                    else if (Model.MODEL_DRAW_DISTANCE >= 3500) Model.MODEL_DRAW_DISTANCE = 3500;
                }
            } else if (!lockModelDrawDistance) {
                Model.MODEL_DRAW_DISTANCE = 3500;
                lockModelDrawDistance = true;
            }
            int setting = 0;

            if (Client.cameraZoom > 600) setting = 4;
            else if (Client.cameraZoom > 400) setting = 3;
            else if (Client.cameraZoom > 200) setting = 2;
            else if (Client.cameraZoom > 0) setting = 1;


            Client.instance.settings[168] = setting;
            Widget.interfaceCache[SettingsWidget.ZOOM_SLIDER].slider.setValue(Client.cameraZoom);
        }
        ChatBox.setUpdateChatbox(true);
    }

    private void handleChatBoxScrolling(int rotation) {
        int scrollPos = ChatBox.chatBoxScrollPosition;
        scrollPos -= rotation * 30;
        if (scrollPos < 0)
            scrollPos = 0;
        if (scrollPos > ChatBox.chatScrollMax - Client.instance.getChatBoxHeight())
            scrollPos = ChatBox.chatScrollMax - Client.instance.getChatBoxHeight();
        if (ChatBox.chatBoxScrollPosition != scrollPos) {
            ChatBox.chatBoxScrollPosition = scrollPos;
            ChatBox.setUpdateChatbox(true);
        }
    }

    private void handleInterfaceScrolling(int rotation) {
        int offsetX;
        int offsetY;
        int tabInterfaceID = ClientCompanion.tabInterfaceIDs[ClientCompanion.tabId];
        int x = (ClientUI.frameWidth / 2) - 240;
        int y = (ClientUI.frameHeight / 2) - 167;

        if (tabInterfaceID != -1) {
            Widget tab = Widget.interfaceCache[tabInterfaceID];

            if (tab != null) {
                offsetX = ClientUI.frameMode == ScreenMode.FIXED ? ClientUI.frameWidth - 218 : ClientUI.frameWidth - 197;
                offsetY = ClientUI.frameMode == ScreenMode.FIXED ? ClientUI.frameHeight - 298 : ClientUI.frameHeight - (ClientUI.frameWidth >= 1000 ? 37 : 74) - 267;

                handleScrolling(tab, rotation, offsetX, offsetY);
            }
        }

        if (ClientCompanion.openInterfaceId != -1) {
            int w = 512, h = 334;
            int count = Client.instance.displaySideStonesStacked() ? 3 : 4;
            if (ClientUI.frameMode != Client.ScreenMode.FIXED) {
                if (Widget.interfaceCache[ClientCompanion.openInterfaceId].width > w || Widget.interfaceCache[ClientCompanion.openInterfaceId].height > h) {
                    x = ClientUtil.getLargeResizableInterfaceOffsetLeftX();
                    y = ClientUtil.getLargeResizableInterfaceOffsetTopY();
                }
                for (int i = 0; i < count; i++) {
                    if (x + w > (ClientUI.frameWidth - 225)) {
                        x = x - 30;
                        if (x < 0) {
                            x = 0;
                        }
                    }
                    if (y + h > (ClientUI.frameHeight - 182)) {
                        y = y - 30;
                        if (y < 0) {
                            y = 0;
                        }
                    }
                }
            }

            Widget tab = Widget.interfaceCache[ClientCompanion.openInterfaceId];

            if (tab != null) {
                offsetX = ClientUI.frameMode == Client.ScreenMode.FIXED ? 4 : x;
                offsetY = ClientUI.frameMode == Client.ScreenMode.FIXED ? 4 : y;

                handleScrolling(tab, rotation, offsetX, offsetY);
            }
        }

        if (ClientCompanion.openInterfaceId2 != -1) {
            int w = 512, h = 334;
            x = ClientUI.frameMode == ScreenMode.FIXED ? 0 : (ClientUI.frameWidth / 2) - 256;
            y = ClientUI.frameMode == ScreenMode.FIXED ? 0 : (ClientUI.frameHeight / 2) - 167;
            int count = Client.instance.displaySideStonesStacked() ? 3 : 4;
            if (ClientUI.frameMode != ScreenMode.FIXED) {
                for (int i = 0; i < count; i++) {
                    if (x + w > (ClientUI.frameWidth - 225)) {
                        x = x - 30;
                        if (x < 0) {
                            x = 0;
                        }
                    }
                    if (y + h > (ClientUI.frameHeight - 182)) {
                        y = y - 30;
                        if (y < 0) {
                            y = 0;
                        }
                    }
                }
            }

            offsetX = ClientUI.frameMode == Client.ScreenMode.FIXED ? 4 : x;
            offsetY = ClientUI.frameMode == Client.ScreenMode.FIXED ? 4 : y;

            handleScrolling(Widget.interfaceCache[ClientCompanion.openInterfaceId2], rotation, offsetX, offsetY);
        }
    }

    private void handleScrolling(Widget widget, int rotation, int offsetX, int offsetY) {

        if (widget.children == null)
            return;

        //System.out.println("widget = " + widget + ", rotation = " + rotation + ", offsetX = " + offsetX + ", offsetY = " + offsetY);

        int positionX;
        int positionY;
        int scrollAmount = 30;

        for (int index = 0; index < widget.children.length; index++) {
            Widget child = Widget.interfaceCache[widget.children[index]];
            if (child == null)
                continue;
            if (child.scrollMax > child.height) {
                positionX = widget.childX[index] + child.horizontalOffset;
                positionY = widget.childY[index] + child.verticalOffset;

                if ((MouseHandler.x > (offsetX + positionX)) && (MouseHandler.y > (offsetY + positionY)) && (MouseHandler.x < (offsetX + positionX + child.width)) && (MouseHandler.y < (offsetY + positionY + child.height))) {

                    if (rotation < 0) {

                        if (scrollAmount > child.scrollPosition) {
                            scrollAmount = child.scrollPosition;
                        }

                    } else {
                        int currPos = child.scrollPosition + child.height;

                        if (scrollAmount + currPos > child.scrollMax) {
                            scrollAmount = child.scrollMax - currPos;
                        }
                    }

                    if (Client.instance.getItemDragType() != Widget.ITEM_DRAG_TYPE_NONE) {
                        Client.instance.setPreviousClickMouseY(Client.instance.getPreviousClickMouseY() - (rotation * scrollAmount));
                    }

                    child.scrollPosition += rotation * scrollAmount;
                }


            }
            if (child.children != null) {
                handleScrolling(child, rotation, offsetX + widget.childX[index] + child.horizontalOffset, offsetY + widget.childY[index] + child.verticalOffset);
            }
        }
    }

    private void showPrioritizedPlayers() {
        addPlayerToScene(localPlayer, (long) LOCAL_PLAYER_ID << 32, true);

        // Draw the player we're interacting with
        // Interacting includes combat, following, etc.
        int interact = localPlayer.targetIndex - 32768;
        if (interact > 0) {
            Player player = players[interact];
            addPlayerToScene(player, (long) interact << 32, false);
        }
    }

    private void showOtherPlayers() {
        for (int l = 0; l < Players_count; l++) {
            Player player = players[Players_indices[l]];
            long index = (long) Players_indices[l] << 32;

            // Don't draw interacting player as we've already drawn it on top
            int interact_index = (localPlayer.targetIndex - 32768);
            if (interact_index > 0 && index == (long) interact_index << 32) {
                continue;
            }

            addPlayerToScene(player, index, false);
        }
    }

    private void addPlayerToScene(Player player, long tag, boolean flag) {
        if (player == null || !player.isVisible() || player.isHidden) {
            return;
        }
        if (localPlayer.x >> 7 == destinationX && localPlayer.y >> 7 == destinationY)
            destinationX = 0;
        player.isUnanimated = (ClientCompanion.lowMemory && Players_count > 50 || Players_count > 200) && !flag && player.getMovementSequence() == player.idleSequence;
        int j1 = player.x >> 7;
        int k1 = player.y >> 7;
        if (j1 < 0 || j1 >= 104 || k1 < 0 || k1 >= 104) {
            return;
        }

        if (OSObjectDefinition.USE_OSRS)
            tag = ViewportMouse.toTag(0, 0, 0, false, (int) (tag >> 32));

        if (player.playerModel != null && tick >= player.objectModelStart && tick < player.objectModelStop) {
            player.isUnanimated = false;
            player.tileHeight = getTileHeight(plane, player.y, player.x);
            scene.addToScenePlayerAsObject(plane, player.y, player, player.orientationScene,
                    player.objectAnInt1722GreaterYLoc, player.x, player.tileHeight, player.objectAnInt1719LesserXLoc,
                    player.objectAnInt1721GreaterXLoc, tag, player.objectAnInt1720LesserYLoc);
            return;
        }
        if ((player.x & 0x7f) == 64 && (player.y & 0x7f) == 64) {
            if (tileLastDrawnActor[j1][k1] == viewportDrawCount) {
                return;
            }
            tileLastDrawnActor[j1][k1] = viewportDrawCount;
        }
        player.tileHeight = getTileHeight(plane, player.y, player.x);
        scene.addAnimableA(plane, player.orientationScene, player.tileHeight, tag, player.y, 60, player.x, player,
                player.animationStretches);
    }

    public boolean promptUserForInput(Widget widget) {
        int contentType = widget.contentType;
        if (PlayerRelations.friendServerStatus == 2) {
            if (contentType == 201) {
                ChatBox.setUpdateChatbox(true);
                inputDialogState = 0;
                messagePromptRaised = true;
                promptInput = "";
                friendsListAction = 1;
                inputPromptTitle = "Enter name of friend to add to list";
            }
            if (contentType == 202) {
                ChatBox.setUpdateChatbox(true);
                inputDialogState = 0;
                messagePromptRaised = true;
                promptInput = "";
                friendsListAction = 2;
                inputPromptTitle = "Enter name of friend to delete from list";
            }
        }
        if (contentType == 205) {
            mouseIdleGameTicks = 250;
            return true;
        }
        if (contentType == 501) {
            ChatBox.setUpdateChatbox(true);
            inputDialogState = 0;
            messagePromptRaised = true;
            promptInput = "";
            friendsListAction = 4;
            inputPromptTitle = "Enter name of player to add to list";
        }
        if (contentType == 502) {
            ChatBox.setUpdateChatbox(true);
            inputDialogState = 0;
            messagePromptRaised = true;
            promptInput = "";
            friendsListAction = 5;
            inputPromptTitle = "Enter name of player to delete from list";
        }
        if (contentType == 550) {
            ChatBox.setUpdateChatbox(true);
            inputDialogState = 0;
            messagePromptRaised = true;
            promptInput = "";
            friendsListAction = 6;
            inputPromptTitle = "Enter the name of the chat you wish to join";
        }
        if (contentType >= 300 && contentType <= 313) {
            int k = (contentType - 300) / 2;
            int j1 = contentType & 1;
            IdentityKit.selectKitBodyPart(this, k, j1);
        }
        if (contentType >= 314 && contentType <= 323) {
            int l = (contentType - 314) / 2;
            int k1 = contentType & 1;
            int j2 = characterDesignColours[l];
            if (k1 == 0 && --j2 < 0)
                j2 = ClientCompanion.PLAYER_BODY_RECOLOURS[l].length - 1;
            if (k1 == 1 && ++j2 >= ClientCompanion.PLAYER_BODY_RECOLOURS[l].length)
                j2 = 0;
            characterDesignColours[l] = j2;
            aBoolean1031 = true;
        }
        if (contentType == 324 && !maleCharacter) {
            maleCharacter = true;
            IdentityKit.changeCharacterGender(this);
        }
        if (contentType == 325 && maleCharacter) {
            maleCharacter = false;
            IdentityKit.changeCharacterGender(this);
        }
        if (contentType == 326) {
            sendPacket(new ChangeAppearance(maleCharacter, anIntArray1065, characterDesignColours).create());
            return true;
        }

        if (contentType == 613) {
            canMute = !canMute;
        }

        if (contentType >= 601 && contentType <= 612) {
            clearTopInterfaces();
            if (reportAbuseInput.length() > 0) {
            }
        }
        return false;
    }

    private void parsePlayerSynchronizationMask(Buffer stream) {
        for (int count = 0; count < mobsAwaitingUpdateCount; count++) {
            int index = mobsAwaitingUpdate[count];
            Player player = players[index];
            int mask = stream.readUnsignedByte();

            if ((mask & 0x40) != 0) {
                int extra = stream.readUnsignedByte() << 8;
                mask += extra;
//                System.out.println("reading big mask! "+mask+" += "+extra+" ("+(extra >> 8)+")");
            }
            appendPlayerUpdateMask(mask, index, stream, player);
        }
    }

    private void drawMapScenes(int i, int k, int l, int i1, int j1) {
        long k1 = scene.getWallObjectUid(j1, l, i);
        if (k1 != 0) {
            int k2 = DynamicObject.get_object_orientation(k1);
            int i3 = DynamicObject.get_object_type(k1);
            int k3 = k;
            if (k1 > 0)
                k3 = i1;
            int[] ai = SpriteCompanion.minimapImage.myPixels;
            int k4 = 24624 + l * 4 + (103 - i) * 512 * 4;
            int objectId = DynamicObject.get_object_key(k1);
            int mapSceneId;
            int sizeX;
            int sizeY;
            if (OSObjectDefinition.USE_OSRS) {
                OSObjectDefinition osObjectDefinition = OSObjectDefinition.lookup(objectId);
                mapSceneId = osObjectDefinition.mapSceneId;
                sizeX = osObjectDefinition.sizeX;
                sizeY = osObjectDefinition.sizeY;
            } else {
                ObjectDefinition objectDefinition = ObjectDefinition.lookup(objectId);
                mapSceneId = objectDefinition.mapSceneId;
                sizeX = objectDefinition.sizeX;
                sizeY = objectDefinition.sizeY;
            }
            if (mapSceneId != -1) {
                IndexedImage background_2 = IndexedImageCompanion.mapScenes[mapSceneId];
                if (background_2 != null) {
                    int i6 = (sizeX * 4 - background_2.width) / 2;
                    int j6 = (sizeY * 4 - background_2.height) / 2;
                    background_2.draw(48 + l * 4 + i6, 48 + (104 - i - sizeY) * 4 + j6);
                }
            } else {
                if (i3 == 0 || i3 == 2)
                    if (k2 == 0) {
                        ai[k4] = k3;
                        ai[k4 + 512] = k3;
                        ai[k4 + 1024] = k3;
                        ai[k4 + 1536] = k3;
                    } else if (k2 == 1) {
                        ai[k4] = k3;
                        ai[k4 + 1] = k3;
                        ai[k4 + 2] = k3;
                        ai[k4 + 3] = k3;
                    } else if (k2 == 2) {
                        ai[k4 + 3] = k3;
                        ai[k4 + 3 + 512] = k3;
                        ai[k4 + 3 + 1024] = k3;
                        ai[k4 + 3 + 1536] = k3;
                    } else if (k2 == 3) {
                        ai[k4 + 1536] = k3;
                        ai[k4 + 1536 + 1] = k3;
                        ai[k4 + 1536 + 2] = k3;
                        ai[k4 + 1536 + 3] = k3;
                    }
                if (i3 == 3)
                    if (k2 == 0)
                        ai[k4] = k3;
                    else if (k2 == 1)
                        ai[k4 + 3] = k3;
                    else if (k2 == 2)
                        ai[k4 + 3 + 1536] = k3;
                    else if (k2 == 3)
                        ai[k4 + 1536] = k3;
                if (i3 == 2)
                    if (k2 == 3) {
                        ai[k4] = k3;
                        ai[k4 + 512] = k3;
                        ai[k4 + 1024] = k3;
                        ai[k4 + 1536] = k3;
                    } else if (k2 == 0) {
                        ai[k4] = k3;
                        ai[k4 + 1] = k3;
                        ai[k4 + 2] = k3;
                        ai[k4 + 3] = k3;
                    } else if (k2 == 1) {
                        ai[k4 + 3] = k3;
                        ai[k4 + 3 + 512] = k3;
                        ai[k4 + 3 + 1024] = k3;
                        ai[k4 + 3 + 1536] = k3;
                    } else if (k2 == 2) {
                        ai[k4 + 1536] = k3;
                        ai[k4 + 1536 + 1] = k3;
                        ai[k4 + 1536 + 2] = k3;
                        ai[k4 + 1536 + 3] = k3;
                    }
            }
        }
        k1 = scene.getGameObjectUid(j1, l, i);
        if (k1 != 0) {
            int l2 = DynamicObject.get_object_orientation(k1);
            int j3 = DynamicObject.get_object_type(k1);
            int objectId = DynamicObject.get_object_key(k1);
            int mapSceneId;
            int sizeX;
            int sizeY;
            if (OSObjectDefinition.USE_OSRS) {
                OSObjectDefinition osObjectDefinition = OSObjectDefinition.lookup(objectId);
                mapSceneId = osObjectDefinition.mapSceneId;
                sizeX = osObjectDefinition.sizeX;
                sizeY = osObjectDefinition.sizeY;
            } else {
                ObjectDefinition objectDefinition = ObjectDefinition.lookup(objectId);
                mapSceneId = objectDefinition.mapSceneId;
                sizeX = objectDefinition.sizeX;
                sizeY = objectDefinition.sizeY;
            }

            if (mapSceneId != -1) {
                IndexedImage background_1 = IndexedImageCompanion.mapScenes[mapSceneId];
                if (background_1 != null) {
                    int j5 = (sizeX * 4 - background_1.width) / 2;
                    int k5 = (sizeY * 4 - background_1.height) / 2;
                    background_1.draw(48 + l * 4 + j5, 48 + (104 - i - sizeY) * 4 + k5);
                }
            } else if (j3 == 9) {
                int l4 = 0xeeeeee;
                if (k1 > 0)
                    l4 = 0xee0000;
                int[] ai1 = SpriteCompanion.minimapImage.myPixels;
                int l5 = 24624 + l * 4 + (103 - i) * 512 * 4;
                if (l2 == 0 || l2 == 2) {
                    ai1[l5 + 1536] = l4;
                    ai1[l5 + 1024 + 1] = l4;
                    ai1[l5 + 512 + 2] = l4;
                    ai1[l5 + 3] = l4;
                } else {
                    ai1[l5] = l4;
                    ai1[l5 + 512 + 1] = l4;
                    ai1[l5 + 1024 + 2] = l4;
                    ai1[l5 + 1536 + 3] = l4;
                }
            }
        }
        k1 = scene.getGroundDecorationUid(j1, l, i);
        if (k1 != 0) {
            int objectId = DynamicObject.get_object_key(k1);
            int mapSceneId;
            int sizeX;
            int sizeY;
            if (OSObjectDefinition.USE_OSRS) {
                OSObjectDefinition osObjectDefinition = OSObjectDefinition.lookup(objectId);
                mapSceneId = osObjectDefinition.mapSceneId;
                sizeX = osObjectDefinition.sizeX;
                sizeY = osObjectDefinition.sizeY;
            } else {
                ObjectDefinition objectDefinition = ObjectDefinition.lookup(objectId);
                mapSceneId = objectDefinition.mapSceneId;
                sizeX = objectDefinition.sizeX;
                sizeY = objectDefinition.sizeY;
            }

            if (mapSceneId != -1) {
                IndexedImage background = IndexedImageCompanion.mapScenes[mapSceneId];
                if (background != null) {
                    int i4 = (sizeX * 4 - background.width) / 2;
                    int j4 = (sizeY * 4 - background.height) / 2;
                    background.draw(48 + l * 4 + i4, 48 + (104 - i - sizeY) * 4 + j4);
                }
            }
        }
    }

    public void loadTitleScreen() {
        IndexedImageCompanion.titleBoxIndexedImage = new IndexedImage(titleArchive, "titlebox", 0);
        IndexedImageCompanion.titleButtonIndexedImage = new IndexedImage(titleArchive, "titlebutton", 0);

        //Flames.init();
    }

    public boolean hover(int x1, int y1, Sprite drawnSprite) {
        return MouseHandler.x >= x1 && MouseHandler.x <= x1 + drawnSprite.myWidth && MouseHandler.y >= y1
                && MouseHandler.y <= y1 + drawnSprite.myHeight;
    }

    private void loadingStages() {
        if (ClientCompanion.lowMemory && getLoadingStage() == GAME_ASSETS_LOADED && MapRegion.lastPlane != plane || reloadRegion) {
            ClientCompanion.gameScreenImageProducer.initDrawingArea();
            drawLoadingMessages();
            ClientCompanion.gameScreenImageProducer.drawGraphics(canvas.getGraphics(), ClientUI.frameMode == ScreenMode.FIXED ? 4 : 0, ClientUI.frameMode == ScreenMode.FIXED ? 4 : 0
            );
            setLoadingStage(1);
            loadingStartTime = System.currentTimeMillis();
            reloadRegion = false;
        }
        if (getLoadingStage() == 1) {
            int j = getMapLoadingState();
            if (j != 0 && System.currentTimeMillis() - loadingStartTime > 0x57e40L) {
                Log.error(myUsername + " glcfb " + serverSeed + "," + j + "," + ClientCompanion.lowMemory + "," + indices[0]
                        + "," + resourceProvider.remaining() + "," + plane + "," + currentRegionX + ","
                        + currentRegionY);
                loadingStartTime = System.currentTimeMillis();
            }
        }
        if (getLoadingStage() == GAME_ASSETS_LOADED && plane != lastKnownPlane) {
            lastKnownPlane = plane;
            renderMapScene(plane);
        }
    }

    public void OS_handleMapLoading() {
        __client_fe = 0;
        boolean loading = true;
        int regionIndex;
        for (regionIndex = 0; regionIndex < regionLandArchives.length; ++regionIndex) {
            if (regionMapArchiveIds[regionIndex] != -1 && regionLandArchives[regionIndex] == null) {
//                regionLandArchives[regionIndex] = OsCache.indexCache5.
                if (regionLandArchives[regionIndex] == null) {
                    loading = false;
                    ++__client_fe;
                }
            }

            if (regionLandArchiveIds[regionIndex] != -1 && regionMapArchives[regionIndex] == null) {
//               regionMapArchives[regionIndex] = OsCache.indexCache5.takeRecordEncrypted(regionLandArchiveIds[regionIndex], 0, Xteas.getKeys(regionIndex));
                if (regionMapArchives[regionIndex] == null) {
                    loading = false;
                    ++__client_fe;
                }
            }
        }
        System.out.println("loading " + gameState);
        if (!loading) {
            map_loading_state = 1;
        } else {
            map_objects_count = 0;
            loading = true;

            int shared2;
            int shared;
            for (regionIndex = 0; regionIndex < regionLandArchives.length; ++regionIndex) {
                byte[] regionMap = regionMapArchives[regionIndex];
                if (regionMap != null) {
                    shared2 = (regions[regionIndex] >> 8) * 64 - baseX;
                    shared = (regions[regionIndex] & 255) * 64 - baseY;
                    if (isInInstance) {
                        shared2 = 10;
                        shared = 10;
                    }

                    loading &= MapRegion.readMapObjects(regionMap, shared2, shared);
                }
            }
            if (!loading) {
                map_loading_state = 2;
            } else {
                if (map_loading_state != 0) {
                    drawLoadingMessage("Loading - please wait." + "<br>" + " (" + 100 + "%" + ")");
                }
                MapRegion.lastPlane = plane;
                loadRegion();
                sendPacket(new FinalizedRegionChange().create());
                GameShell.updateGameState(30);
                Audio.playPcmPlayers();
                resetClock();
            }
        }
    }

    public int getMapLoadingState() {
        if (!floorMaps.equals("") || !objectMaps.equals("")) {
            floorMaps = "";
            objectMaps = "";
        }

        for (int i = 0; i < regionLandArchives.length; i++) {
            floorMaps += "  " + regionLandArchiveIds[i];
            objectMaps += "  " + regionMapArchiveIds[i];
            if (regionLandArchives[i] == null && regionLandArchiveIds[i] != -1)
                return -1;
            if (regionMapArchives[i] == null && regionMapArchiveIds[i] != -1)
                return -2;
        }
        boolean loaded = true;
//        int regionIndex;
//        for(regionIndex = 0; regionIndex < regionLandArchives.length; ++regionIndex) {
//            if(regionMapArchiveIds[regionIndex] != -1 && regionLandArchives[regionIndex] == null) {
//                regionLandArchives[regionIndex] = OsCache.indexCache5.takeRecord(regionMapArchiveIds[regionIndex], 0);
//                if(regionLandArchives[regionIndex] == null) {
//                    loaded = false;
//                    System.err.println("Could not load region land archive "+regionMapArchiveIds[regionIndex]);
//                }
//            }
//
//            if(regionLandArchiveIds[regionIndex] != -1 && regionMapArchives[regionIndex] == null) {
//               regionMapArchives[regionIndex] = OsCache.indexCache5.takeRecord(regionLandArchiveIds[regionIndex], 0);
//                if(regionMapArchives[regionIndex] == null) {
//                    loaded = false;
//                    System.err.println("Could not load region objects archive "+regionLandArchiveIds[regionIndex]);
//                }
//            }
//        }
//        if(!loaded)
//            return -1;

        boolean flag = true;
        for (int j = 0; j < regionLandArchives.length; j++) {
            byte[] abyte0 = regionMapArchives[j];
            if (abyte0 != null) {
                int k = (regions[j] >> 8) * 64 - baseX;
                int l = (regions[j] & 0xff) * 64 - baseY;
                if (isInInstance) {
                    k = 10;
                    l = 10;
                }
                flag &= MapRegion.readMapObjects(abyte0, k, l);
            }
        }
        if (!flag)
            return -3;
        if (receivedPlayerUpdatePacket) {
            return -4;
        } else {
            setLoadingStage(2);
            MapRegion.lastPlane = plane;
            loadRegion();
            sendPacket(new FinalizedRegionChange().create());
            GameShell.updateGameState(30);
            Audio.playPcmPlayers();
            resetClock();
            return 0;
        }
    }

    private void createProjectiles() {
        for (Projectile projectile = (Projectile) projectiles
                .last(); projectile != null; projectile = (Projectile) projectiles
                .previous())
            if (projectile.projectileZ != plane || tick > projectile.stopCycle)
                projectile.remove();
            else if (tick >= projectile.startCycle) {
                if (projectile.target == 0) {
                    projectile.calculateIncrements(tick, projectile.endY,
                            getTileHeight(projectile.projectileZ, projectile.endY, projectile.endX) - projectile.endHeight,
                            projectile.endX);
                }
                if (projectile.target > 0) {
                    Npc npc = npcs[projectile.target - 1];
                    if (npc != null && npc.x >= 0 && npc.x < 13312 && npc.y >= 0 && npc.y < 13312)
                        projectile.calculateIncrements(tick, npc.y,
                                getTileHeight(projectile.projectileZ, npc.y, npc.x)
                                        - projectile.endHeight,
                                npc.x);
                }
                if (projectile.target < 0) {
                    int j = -projectile.target - 1;
                    Player player;
                    if (j == localPlayerIndex)
                        player = localPlayer;
                    else
                        player = players[j];
                    if (player != null && player.x >= 0 && player.x < 13312 && player.y >= 0 && player.y < 13312)
                        projectile.calculateIncrements(tick, player.y,
                                getTileHeight(projectile.projectileZ, player.y, player.x)
                                        - projectile.endHeight,
                                player.x);
                }
                projectile.progressCycles(tickDelta);
                scene.addAnimableA(plane, projectile.turnValue, (int) projectile.cnterHeight,
                        -1, (int) projectile.yPos, 60, (int) projectile.xPos,
                        projectile, false);
            }

    }

    private void processOnDemandQueue() {
        do {
            Resource resource;
            do {
                resource = resourceProvider.next();
                if (resource == null)
                    return;
                if (resource.dataType == 0) {
                    Model.readModelHeader(resource.buffer, resource.ID);
                    if (backDialogueId != -1)
                        ChatBox.setUpdateChatbox(true);
                }
                if (resource.dataType == 1) {
                    RSFrame317.load(resource.ID, resource.buffer);
                }
                if (resource.dataType == 2 && resource.buffer != null) {
                    System.out.println("Unhandled music id " + resource.ID);
                }
                if (resource.dataType == 3 && ((OSObjectDefinition.USE_OSRS && gameState == 25) || getLoadingStage() == 1)) {
                    for (int i = 0; i < regionLandArchives.length; i++) {
                        if (regionLandArchiveIds[i] == resource.ID) {
                            regionLandArchives[i] = resource.buffer;
                            if (resource.buffer == null)
                                regionLandArchiveIds[i] = -1;
                            break;
                        }
                        if (regionMapArchiveIds[i] != resource.ID)
                            continue;
                        regionMapArchives[i] = resource.buffer;
                        if (resource.buffer == null)
                            regionMapArchiveIds[i] = -1;
                        break;
                    }
                }
                if (resource.dataType == 5)
                    System.out.println("Unhandled sound " + resource.ID + " contact DEV! :)");

            } while (resource.dataType != 93 || !resourceProvider.landscapePresent(resource.ID));

            if (!OSObjectDefinition.USE_OSRS)
                MapRegion.passiveRequestGameObjectModels(new Buffer(resource.buffer), resourceProvider);

        } while (true);
    }

    public void resetAnimation(int i) {
        Widget class9 = Widget.interfaceCache[i];
        if (class9 == null || class9.children == null) {
            return;
        }
        for (int j = 0; j < class9.children.length; j++) {
            if (class9.children[j] == -1)
                break;
            Widget class9_1 = Widget.interfaceCache[class9.children[j]];

            if (class9_1 == null) {
                continue;
            }

            if (class9_1.type == 1) {
                resetAnimation(class9_1.id);
            }
            class9_1.currentFrame = 0;
            class9_1.lastFrameTime = 0;
        }
    }

    private void mainGameProcessor() {


        if (systemUpdateTime > 1) {
            systemUpdateTime--;
        }
        if (mouseIdleGameTicks > 0) {
            mouseIdleGameTicks--;
        }
        for (int j = 0; j < 10; j++) { // 5 -> 100. Packets parsed per cycle
            if (!PacketHandler.parseFrame(this, decryption, socketStream)) {
                break;
            }
        }

        if (!loggedIn) {
            return;
        }

        MouseRecorder.instance.onCycle(this);
        KeyRecorder.instance.onCycle(this);

        if (ClientCompanion.blackJackTimer > 0 && tick % 49 == 0) {
            sendString(Integer.toString(ClientCompanion.blackJackTimer), 52034);
            ClientCompanion.blackJackTimer--;
        }


        if (activeFocus && !aBoolean954)
            aBoolean954 = true;

        if (!activeFocus && aBoolean954)
            aBoolean954 = false;

        Spinner.processSpinner(this);

        if (!OSObjectDefinition.USE_OSRS)
            loadingStages();

        if (gameState != 30)
            return;

        processSpawnedObjects();
        Audio.processSoundEffects();

        if (Configuration.enableParticles)
            ClientCompanion.particleSystem.process();

        processPlayerMovement();
        processNpcMovement();
        processMobChatText();
        tickDelta++;
        if (crossType != 0) {
            crossIndex += 20;
            if (crossIndex >= 400)
                crossType = 0;
        }
        if (atInventoryInterfaceType != 0) {
            atInventoryLoopCycle++;
            if (atInventoryLoopCycle >= 15) {
                if (atInventoryInterfaceType == 2) {
                }
                if (atInventoryInterfaceType == 3)
                    ChatBox.setUpdateChatbox(true);
                atInventoryInterfaceType = 0;
            }
        }
        if (itemDragType != Widget.ITEM_DRAG_TYPE_NONE) {
            dragItemDelay++;
            boolean bankTabButton = Bank.isBankTabButton(anInt1084, true);
            boolean xTooFar = Math.abs(MouseHandler.x - previousClickMouseX) > 5;
            boolean yTooFar = Math.abs(MouseHandler.y - previousClickMouseY) > 5;
            if (xTooFar || yTooFar)
                mouseOutOfDragZone = true;

            if (MouseHandler.currentButton == 0) {
                if (itemDragType == Widget.ITEM_DRAG_TYPE_BACK_DIALOGUE)
                    ChatBox.setUpdateChatbox(true);
                itemDragType = Widget.ITEM_DRAG_TYPE_NONE;
                ClientCompanion.overlayItemIcon = null;
                lastActiveInvInterface = -1;
                if (mouseOutOfDragZone && dragItemDelay >= (bankTabButton ? Configuration.dragValue : 10)) {

                    GameFrameInput.processRightClick(this);
                    int fromSlot = anInt1085;
                    int toSlot = mouseInvInterfaceIndex;
                    int fromInterface = anInt1084;
                    int toInterface = lastActiveInvInterface;
                    // Bank search containers, use the values of our actual containers instead of the search containers
                    if (Bank.showSearchContainers()) {
                        if (Widget.interfaceCache[fromInterface].contentType == 206) {
                            fromSlot = Bank.getActualSlot(fromInterface, fromSlot);
                            fromInterface -= 10;
                        }
                        if (toInterface != -1 && Widget.interfaceCache[toInterface].contentType == 206) {
                            toSlot = Bank.getActualSlot(toInterface, toSlot);
                            toInterface -= 10;
                        }
                    }
                    if (bankTabButton) {
                        // Rearranging bank tabs
                        int tab = fromInterface - 50010;
                        if (toSlot != tab && tab != 0 && toSlot > 0 && toSlot < 10) {
                            sendPacket(new SwitchBankTabSlot(tab, toSlot).create());
                        }
                    } else if (menuIsBankTabButton(menuOptionsCount - 1)) {
                        // Dragging an item to a new bank tab
                        sendPacket(new BankTabCreation(fromInterface, fromSlot, menuArguments2[menuOptionsCount - 1] - 50010).create());
                    } else if (toInterface != fromInterface && toInterface != -1) {
                        if (toSlot != -1) {
                            // Dragging an item to a different container's slot
                            sendPacket(new SwitchItemContainer(fromInterface, toInterface, fromSlot, toSlot).create());
                        } else {
                            // Dragging an item to a different container's region (packet only supports bank tabs currently)
                            sendPacket(new BankTabCreation(fromInterface, fromSlot, toInterface - 50050).create());
                        }
                    } else if (toInterface == fromInterface
                            && toSlot != fromSlot && toSlot != -1) {
                        Widget childInterface = Widget.interfaceCache[fromInterface];
                        int j1 = 0;
                        if (anInt913 == 1 && childInterface.contentType == 206)
                            j1 = 1;
                        if (childInterface.inventoryItemId[toSlot] <= 0)
                            j1 = 0;
                        if (childInterface.replaceItems) {
                            childInterface.inventoryItemId[toSlot] =
                                    childInterface.inventoryItemId[fromSlot];
                            childInterface.inventoryAmounts[toSlot] =
                                    childInterface.inventoryAmounts[fromSlot];
                            childInterface.inventoryItemId[fromSlot] = -1;
                            childInterface.inventoryAmounts[fromSlot] = 0;
                        } else if (j1 == 1) {
                            int i3 = fromSlot;
                            while (i3 != toSlot) {
                                if (i3 > toSlot) {
                                    childInterface.swapInventoryItems(i3, i3 - 1);
                                    i3--;
                                } else {
                                    childInterface.swapInventoryItems(i3, i3 + 1);
                                    i3++;
                                }
                            }
                        } else {
                            childInterface.swapInventoryItems(fromSlot, toSlot);
                        }
                        sendPacket(new SwitchItemSlot(fromInterface, j1, fromSlot, toSlot).create());
                    }
                } else if ((mouseButtonSetting == 1 || menuHasAddFriend(menuOptionsCount - 1)) && menuOptionsCount > 2)
                    GameFrame.determineMenuSize(this);
                else if (menuOptionsCount > 0)
                    processMenuActions(menuOptionsCount - 1);
                atInventoryLoopCycle = 10;
                MouseHandler.lastButton = 0;
            }
        }
        if (SceneGraph.Scene_selectedX != -1) {
            int targetX = SceneGraph.Scene_selectedX;
            int targetY = SceneGraph.Scene_selectedY;
            boolean flag = pathFinder.doWalkTo(this, PathFinder.SCREEN_WALK, targetX, targetY, true);
            SceneGraph.Scene_selectedX = -1;
            if (flag) {
                crossX = MouseHandler.lastPressedX;
                crossY = MouseHandler.lastPressedY;
                crossType = 1;
                crossIndex = 0;
            }
        }
        if (MouseHandler.lastButton == 1 && clickToContinueString != null) {
            clickToContinueString = null;
            ChatBox.setUpdateChatbox(true);
            MouseHandler.lastButton = 0;
        }
        GameFrame.processMenuClick(this);

        if (MouseHandler.lastButton == 1)
            ;

        if (previousBackDialogueChildWidgetId != 0 || previousChildWidgetId2 != 0 || previousChildWidgetId != 0) {
            if (chatBoxUpdateTick < 0 && !menuOpen) {
                chatBoxUpdateTick++;
                if (chatBoxUpdateTick == 0) {
                    if (previousBackDialogueChildWidgetId != 0) {
                        ChatBox.setUpdateChatbox(true);
                    }
                    if (previousChildWidgetId2 != 0) {
                        ChatBox.setUpdateChatbox(true);
                    }
                }
            }
        } else if (chatBoxUpdateTick > 0)
            chatBoxUpdateTick--;

        if (getLoadingStage() == GAME_ASSETS_LOADED) {
            checkForGameUsages();
            if (isCameraLocked)
                calculateCameraPosition();
        }

        for (int i1 = 0; i1 < 5; i1++)
            quakeTimes[i1]++;

        KeyParser.manageTextInputs(this);

        int mouseIdleCycles = MouseHandler.idleCycles++;
        int keyIdleCycles = KeyHandler.idleCycles;

        if (mouseIdleCycles > 15_000 && keyIdleCycles > 15_000) {
            mouseIdleGameTicks = 250;
            MouseHandler.idleCycles = 14_500;
            sendPacket(new PlayerInactive().create());
        }

        if (soundCycleCounter++ > 2250) {
            soundCycleCounter = 0;
            sendPacket(new RandomSoundArea().create());
        }

        if (ping_packet_counter++ > 65)
            sendPacket(new BasicPing().create());

        try {
            if (socketStream != null && outBuffer.index > 0) {
                socketStream.write(outBuffer.array, 0, outBuffer.index);
                outBuffer.index = 0;
            }
        } catch (IOException ioException) {
            dropClient();
            Log.error("Ran into IOException during game processing!", ioException);
        } catch (Exception exception) {
            resetLogout();
            Log.error("Ran into exception during game processing!", exception);
        }
    }

    private void clearObjectSpawnRequests() {
        SpawnedObject spawnedObject = (SpawnedObject) spawns.last();
        for (; spawnedObject != null; spawnedObject = (SpawnedObject) spawns.previous())
            if (spawnedObject.getLongetivity == -1) {
                spawnedObject.delay = 0;
                method89(spawnedObject);
            } else {
                spawnedObject.remove();
            }
    }

    public boolean clickObject(long uid, int toLocalY, int toLocalX) {
        int objectId = DynamicObject.get_object_key(uid);

        int sceneConfig = scene.getObjectFlags(plane, toLocalX, toLocalY, uid);

        if (sceneConfig == -1) {
            return false;
        }

        int type = DynamicObject.get_object_type(uid);

        int rotation = DynamicObject.get_object_orientation(uid);

        if (type == 10 || type == 11 || type == 22) {
            int surroundings;
            int sizeX;
            int sizeY;
            if (OSObjectDefinition.USE_OSRS) {
                OSObjectDefinition osObjectDefinition = OSObjectDefinition.lookup(objectId);
                sizeX = osObjectDefinition.sizeX;
                sizeY = osObjectDefinition.sizeY;
                surroundings = osObjectDefinition.surroundings;
            } else {
                ObjectDefinition objectDefinition = ObjectDefinition.lookup(objectId);
                sizeX = objectDefinition.sizeX;
                sizeY = objectDefinition.sizeY;
                surroundings = objectDefinition.surroundings;
            }
            if (!(rotation == 0 || rotation == 2)) {
                int copyX = sizeX;
                sizeX = sizeY;
                sizeY = copyX;
            }

            if (rotation != 0) {
                surroundings = (surroundings << rotation & 0xf) + (surroundings >> 4 - rotation);
            }

            /*
              Revenant cave pillars walking up to.
             */
            if (objectId == 31561) {
                if (rotation == 1) {
                    if (localPlayer.pathX[0] < toLocalX) {
                        toLocalX -= sizeX;
                    } else {
                        toLocalX += sizeX;
                    }
                } else if (rotation == 0) {
                    if (localPlayer.pathY[0] < toLocalY) {
                        toLocalY -= sizeY;
                    } else {
                        toLocalY += sizeY;
                    }
                }
            } else if (objectId == 30282) { // The Inferno
                if (localPlayer.pathY[0] < toLocalY) {
                    toLocalY -= 7;
                } else {
                    toLocalY += 7;
                }
            }

            pathFinder.doWalkTo(this, PathFinder.OBJECT_WALK, toLocalX, toLocalY, false);
        } else {
            pathFinder.doWalkTo(this, PathFinder.OBJECT_WALK, toLocalX, toLocalY, false);
        }

        crossX = MouseHandler.lastPressedX;
        crossY = MouseHandler.lastPressedY;
        crossType = 2;
        crossIndex = 0;
        return true;
    }

    public void playSound(int id) {
        playSound(id, 127, 0);
    }

    public void playSound(int id, int volume, int delay) {
        Audio.queueSoundEffect(id, volume, delay);
    }

    private void dropClient() {

        if (mouseIdleGameTicks > 0) {
            resetLogout();
            return;
        }
        GameShell.updateGameState(40);
        System.out.println("Client.dropClient");
        ClientUI.frameMode(ScreenMode.FIXED, false);
        Rasterizer2D.drawBoxOutline(2, 2, 229, 39, 0xffffff); // white box
        Rasterizer2D.drawBox(3, 3, 227, 37, 0); // black fill
        regularText.drawText(0, "Connection lost.", 19, 120);
        regularText.drawText(0xffffff, "Connection lost.", 18, 119);
        regularText.drawText(0, "Please wait - attempting to reestablish.", 34, 117);
        regularText.drawText(0xffffff, "Please wait - attempting to reestablish.", 34, 116);
        if (ClientCompanion.gameScreenImageProducer != null) {
            ClientCompanion.gameScreenImageProducer.drawGraphics(canvas.getGraphics(), ClientUI.frameMode == ScreenMode.FIXED ? 4 : 0, ClientUI.frameMode == ScreenMode.FIXED ? 4 : 0);
        }
        GameFrame.minimapState = 0;
        destinationX = 0;
        NetSocket rsSocket = socketStream;
        loggedIn = false;
        BroadCastManager.broadcast.clear();
        LoginScreen.loginFailures = 0;
        login(myUsername, myPassword, true);

        if (!loggedIn)
            resetLogout();
        try {
            rsSocket.close();
        } catch (Exception _ex) {
            _ex.printStackTrace();
        }
    }

    public void processMenuActions(int id) {

        if (id < 0)
            return;

        int arg0 = (int) menuArguments0[id];
        int arg1 = menuArguments1[id];
        int arg2 = menuArguments2[id];
        int opcode = menuOpcodes[id];
        long tag = menuArguments0[id];

        String string = menuActions[id];

        WidgetManager.processMenuActions(this, opcode, arg0, arg1, arg2, tag, string);
    }

    private void refreshFrameSize() {
        if (ClientUI.frameMode == ScreenMode.RESIZABLE) {

            final boolean applet = true;
            final int width = ClientUI.frame.getWidth() - ClientUI.FRAME_WIDTH_OFFSET;
            final int height = ClientUI.frame.getHeight() - ClientUI.FRAME_HEIGHT_OFFSET;
            boolean updateFrame = false;

            if (ClientUI.frameWidth != width) {
                updateFrame = true;
                ClientUI.frameWidth = width;
                ClientUI.screenAreaWidth = ClientUI.frameWidth;
            }

            if (ClientUI.frameHeight != height) {
                updateFrame = true;
                ClientUI.frameHeight = height;
                ClientUI.screenAreaHeight = ClientUI.frameHeight;
            }

            if (updateFrame) {
                ClientUI.rebuildFrameSize(ClientUI.frameMode, ClientUI.frameWidth, ClientUI.frameHeight);
                setBounds();
            }
        } else if (ClientUI.frameMode == ScreenMode.FIXED)
            ChatBox.setUpdateChatbox(true);
    }

    public static void setBounds() {
        Rasterizer3D.reposition(ClientUI.frameWidth, ClientUI.frameHeight);
        ClientCompanion.fullScreenTextureArray = Rasterizer3D.scanOffsets;
        Rasterizer3D.reposition(
                ClientUI.frameMode == ScreenMode.FIXED
                        ? (chatboxImageProducer != null
                        ? chatboxImageProducer.width : 519)
                        : ClientUI.frameWidth,
                ClientUI.frameMode == ScreenMode.FIXED
                        ? (chatboxImageProducer != null
                        ? chatboxImageProducer.height : 165)
                        : ClientUI.frameHeight);
        ClientCompanion.anIntArray1180 = Rasterizer3D.scanOffsets;
        Rasterizer3D.reposition(
                ClientUI.frameMode == ScreenMode.FIXED
                        ? (tabImageProducer != null ? tabImageProducer.width
                        : 249)
                        : ClientUI.frameWidth,
                ClientUI.frameMode == ScreenMode.FIXED ? (tabImageProducer != null
                        ? tabImageProducer.height : 335) : ClientUI.frameHeight);
        ClientCompanion.anIntArray1181 = Rasterizer3D.scanOffsets;
        Rasterizer3D.reposition(ClientUI.screenAreaWidth, ClientUI.screenAreaHeight);
        ClientCompanion.anIntArray1182 = Rasterizer3D.scanOffsets;
        int[] ai = new int[9];
        for (int i8 = 0; i8 < 9; i8++) {
            int k8 = 128 + i8 * 32 + 15;
            int l8 = 600 + k8 * 3;
            int i9 = Rasterizer3D.Rasterizer3D_sine[k8];
            ai[i8] = l8 * i9 >> 16;
        }
        if (ClientUI.frameMode == ScreenMode.RESIZABLE && (ClientUI.frameWidth >= 765) && (ClientUI.frameWidth <= 1025)
                && (ClientUI.frameHeight >= 503) && (ClientUI.frameHeight <= 850)) {
            //SceneGraph.viewDistance = 9;
            cameraZoom = 200;
        } else if (ClientUI.frameMode == ScreenMode.FIXED) {
            cameraZoom = 600;
        } else if (ClientUI.frameMode == ScreenMode.RESIZABLE || ClientUI.frameMode == ScreenMode.FULLSCREEN) {
            //SceneGraph.viewDistance = 10;
            cameraZoom = 200;
        }
        //System.out.println(""+ClientUI.screenAreaWidth+", "+ClientUI.screenAreaHeight+", "+ClientUI.frameWidth+", "+ClientUI.frameHeight);
        SceneGraph.setupViewport(500, 800, ClientUI.screenAreaWidth, ClientUI.screenAreaHeight, ai);
    }


    public void cleanUpForQuit() {
        exitRequested = true;
        SignLink.reporterror = false;
        try {
            if (socketStream != null) {
                socketStream.close();
            }
        } catch (Exception ignored) {
        }
        socketStream = null;
//        stopMidi();
        resourceProvider.disable();
        resourceProvider = null;
        outBuffer = null;
        loginBuffer = null;
        incoming = null;
        regions = null;
        regionLandArchives = null;
        regionMapArchives = null;
        regionLandArchiveIds = null;
        regionMapArchiveIds = null;
        Tiles_heights = null;
        tileFlags = null;
        scene = null;
        collisionMaps = null;
        Rasterizer3D.lastTexturePalettePixels = null;
        tabImageProducer = null;
        leftFrame = null;
        topFrame = null;
        minimapImageProducer = null;
        ClientCompanion.gameScreenImageProducer = null;
        chatboxImageProducer = null;
        chatSettingImageProducer = null;
        /* Null pointers for custom sprites */
        SpriteLoader.clearSprites();
        IndexedImageCompanion.mapBack = null;
        SpriteCompanion.sideIcons = null;
        SpriteCompanion.compass = null;
        SpriteCompanion.hitMarks = null;
        SpriteCompanion.headIcons = null;
        SpriteCompanion.autoBackgroundSprites = null;
        SpriteCompanion.autoBackgroundSprites2 = null;
        SpriteCompanion.skullIcons = null;
        SpriteCompanion.headIconsHint = null;
        SpriteCompanion.crosses = null;
        SpriteCompanion.mapDotItem = null;
        SpriteCompanion.mapDotNPC = null;
        SpriteCompanion.mapDotPlayer = null;
        SpriteCompanion.mapDotFriend = null;
        SpriteCompanion.mapDotTeam = null;
        IndexedImageCompanion.mapScenes = null;
        SpriteCompanion.mapFunctions = null;
        tileLastDrawnActor = null;
        players = null;
        Players_indices = null;
        mobsAwaitingUpdate = null;
        playerSynchronizationBuffers = null;
        removedMobs = null;
        npcs = null;
        npcIndices = null;
        groundItems = null;
        spawns = null;
        projectiles = null;
        incompleteAnimables = null;
        menuArguments1 = null;
        menuArguments2 = null;
        menuOpcodes = null;
        menuArguments0 = null;
        menuActions = null;
        settings = null;
        GameFrame.mapIconXs = null;
        GameFrame.mapIconYs = null;
        SpriteCompanion.mapIcons = null;
        SpriteCompanion.minimapImage = null;
        PlayerRelations.friendsList = null;
        PlayerRelations.friendsListAsLongs = null;
        PlayerRelations.friendsNodeIDs = null;
        loginBoxImageProducer = null;
        SpriteCompanion.multiOverlay = null;
        nullLoader();
        ObjectDefinition.clear();
        NpcDefinition.clear();
        ItemDefinition.clear();
        IdentityKit.nullifyIdentityKits();
        Widget.interfaceCache = null;
        Animation.cached.clear();
        Animation.SequenceDefinition_cachedModel.clear();
        Graphic.SpotAnimationDefinition_cached = null;
        Graphic.SpotAnimationDefinition_cachedModels = null;
        VariablePlayer.variables = null;
        Player.PlayerAppearance_cachedModels.clear();
        Rasterizer3D.clear();
        SceneGraph.destructor();
        Model.clear();
        RSFrame317.clear();
        System.gc();
    }

    private void nullLoader() {
        aBoolean831 = false;
        IndexedImageCompanion.titleBoxIndexedImage = null;
        IndexedImageCompanion.titleButtonIndexedImage = null;
    }

    public Component getGameComponent() {
        if (SignLink.mainapp != null)
            return SignLink.mainapp;
        else
            return this;
    }

    private void drawSplitPrivateChat() {
        if (splitPrivateChat == 0) {
            return;
        }
        GameFont textDrawingArea = regularText;
        int i = 0;
        if (systemUpdateTime != 0) {
            i = 1;
        }
        int xPos = 4;
        for (int j = 0; j < 500; j++) {
            if (chatMessages[j] != null) {
                String msg = chatMessages[j];
                int chatType = chatTypes[j];

                String chatName = chatNames[j];

                byte rights = 0;

                int yPos = 329 - i * 13;
                if (ClientUI.frameMode != ScreenMode.FIXED) {
                    yPos = ClientUI.frameHeight - 170 - i * 13;
                }
                yPos -= getChatBoxHeight() - ChatBox.chatScrollHeight;

                if (BroadCastManager.broadcast.size() > 0) {
                    yPos -= 14 * BroadCastManager.broadcast.size();
                }

                if (ChatBox.showFromSplitPrivateChatMsg(this, chatType, chatName)) {
                    if (Configuration.enableEmoticons) {
                        msg = Emoji.handleSyntax(msg);
                    }
                    int msgs = ChatBox.drawChatMessage(this, rights, "From", null, chatName, msg, xPos, yPos, 0x00ffff, 0x00ffff, 0, true, true).length;
                    i += msgs;
                    if (i >= 5) {
                        return;
                    }
                }
                if (ChatBox.showLoginSplitPrivateChatMsg(ChatBox.privateChatMode, splitPrivateChat, chatType)) {
                    if (Configuration.enableEmoticons) {
                        if (!msg.endsWith(" has logged in."))
                            msg = Emoji.handleSyntax(msg);
                    }
                    textDrawingArea.render(0, msg, yPos, 4);
                    textDrawingArea.render(65535, msg, yPos - 1, 4);
                    if (++i >= 5) {
                        return;
                    }
                }
                if (ChatBox.showToSplitPrivateChatMsg(ChatBox.privateChatMode, splitPrivateChat, chatType)) {
                    if (Configuration.enableEmoticons) {
                        msg = Emoji.handleSyntax(msg);
                    }
                    int msgs = ChatBox.drawChatMessage(this, rights, "To", null, chatName, msg, xPos, yPos, 0x00ffff, 0x00ffff, 0, true, true).length;
                    i += msgs;
                    if (i >= 5) {
                        return;
                    }
                }
            }
        }
    }

    public void sendMessage(String message) {
        sendMessage(message, 0, "", 0, "");
    }

    public void sendMessage(String message, int type, String name) {
        sendMessage(message, type, name, 0, "");
    }

    private void sendMessage(String message, int type, String name, String title) {
        sendMessage(message, type, name, 0, title);
    }

    private void sendMessage(String message, int type, String name, int rights, String title) {

        //System.out.println("Client.sendMessage -> message = " + message + ", type = " + type + ", name = " + name + ", rights = " + rights + ", title = " + title);

        if (type == 0 && dialogueId != -1) {
            clickToContinueString = message;
            MouseHandler.lastButton = 0;
        }

        if (backDialogueId == -1) {
            ChatBox.setUpdateChatbox(true);
        }

        for (int index = 499; index > 0; index--) {
            chatTypes[index] = chatTypes[index - 1];
            chatNames[index] = chatNames[index - 1];
            chatMessages[index] = chatMessages[index - 1];
            chatRights[index] = chatRights[index - 1];
            chatTitles[index] = chatTitles[index - 1];
        }
        chatTypes[0] = type;
        chatNames[0] = name;
        chatMessages[0] = message;
        chatRights[0] = rights;
        chatTitles[0] = title;
    }

    public void setupGameplayScreen() {
        System.out.println("Client.setupGameplayScreen");
        nullLoader();
        loginBoxImageProducer = null;
        chatboxImageProducer = constructGraphicsBuffer(519, 165);// chatback
        minimapImageProducer = constructGraphicsBuffer(249, 168);// mapback
        Rasterizer2D.clear();
        drawSprite(19, 0, 0);
        tabImageProducer = constructGraphicsBuffer(249, 335);// inventory

        Sprite sprite = SpriteLoader.getSprite(855);
        leftFrame = constructGraphicsBuffer(sprite.myWidth, sprite.myHeight);
        sprite.method346(0, 0);
        sprite = SpriteLoader.getSprite(854);
        topFrame = constructGraphicsBuffer(sprite.myWidth, sprite.myHeight);
        sprite.method346(0, 0);
        ClientCompanion.gameScreenImageProducer = constructGraphicsBuffer(512, 334, true);// gamescreen
        Rasterizer2D.clear();
        chatSettingImageProducer = constructGraphicsBuffer(249, 45);
        welcomeScreenRaised = true;
    }

    private boolean reAddGameScreen() {
        return chatboxImageProducer == null;
    }

    /**
     * The login method for the 317 protocol.
     *
     * @param name         The name of the user trying to login.
     * @param password     The password of the user trying to login.
     * @param reconnecting The flag for the user indicating to attempt to reconnect.
     */
    public void login(String name, String password, boolean reconnecting) {
        SignLink.setError(name);
        try {

            if (name.length() < 3) {
                LoginScreen.firstLoginMessage = "";
                LoginScreen.secondLoginMessage = name.length() == 0 ? "Please enter your username." : "Your username is too short.";
                LoginScreen.loginScreenCursorPos = 0;
                return;
            }
            if (password.length() < 3) {
                LoginScreen.firstLoginMessage = "";
                LoginScreen.secondLoginMessage = password.length() == 0 ? "Please enter your password." : "Your password is too short.";
                LoginScreen.loginScreenCursorPos = 0;
                return;
            }

            if (captchaRequired.get()) {
                LoginScreen.firstLoginMessage = "";
                LoginScreen.secondLoginMessage = "Please solve the captcha first";
                LoginScreen.loginScreenCursorPos = 0;
                return;
            }

            if (!reconnecting) {
                LoginScreen.firstLoginMessage = "";
                LoginScreen.secondLoginMessage = "Connecting to server...";
                GameShell.updateGameState(20);
                LoginScreen.drawLoginScreen(this, true);

            }

            socketStream = new NetSocket(createSocket(Configuration.connected_world.getPort() + ClientCompanion.portOffset), taskHandler, 5000);

            outBuffer.index = 0;
            outBuffer.writeByte(14); // REQUEST
            socketStream.write(outBuffer.array, 0, 1);
            int response = socketStream.readUnsignedByte();

            int copy = response;

            if (response == 0) {
                socketStream.read(incoming.array, 0, 8);
                incoming.index = 0;
                serverSeed = incoming.readLong(); // aka server session key
                int[] seed = new int[4];
                seed[0] = (int) (Math.random() * 99999999D);
                seed[1] = (int) (Math.random() * 99999999D);
                seed[2] = (int) (serverSeed >> 32);
                seed[3] = (int) serverSeed;
                outBuffer.index = 0;
                outBuffer.writeByte(10);
                outBuffer.writeInt(seed[0]);
                outBuffer.writeInt(seed[1]);
                outBuffer.writeInt(seed[2]);
                outBuffer.writeInt(seed[3]);
                outBuffer.writeInt(Configuration.UID);

                outBuffer.writeString(ClientUtil.getMacAddress());
                outBuffer.writeString(ClientUtil.getMotherboardSN());
                outBuffer.writeString(ClientUtil.getHDSerialNumber(Folders.getDisk()));

                outBuffer.writeString(name);
                outBuffer.writeString(password);
                outBuffer.encryptRSAContent();

                loginBuffer.index = 0;
                loginBuffer.writeByte(reconnecting ? 18 : 16);
                loginBuffer.writeByte(outBuffer.index + 2); // size of
                // the
                // login block
                loginBuffer.writeByte(255);
                loginBuffer.writeByte(ClientCompanion.lowMemory ? 1 : 0); // low mem or not
                loginBuffer.writeBytes(outBuffer.array, outBuffer.index, 0);
                cipher = new IsaacCipher(seed);
                for (int index = 0; index < 4; index++)
                    seed[index] += 50;

                decryption = new IsaacCipher(seed);
                socketStream.write(loginBuffer.array, 0, loginBuffer.index);
                response = socketStream.readUnsignedByte();
                Log.info("logged in " + name);
            }

            if (response == 1) {
                try {
                    Thread.sleep(2000L);
                } catch (Exception e) {
                    Log.error("reconnect sleep failed", e);
                }
                login(name, password, reconnecting);
                return;
            }
            if (response == 3) {
//                MusicPlayer.stopMusic(false);
                //GrinderScape.discord.setTopLine("Just signed in..");
                PlayerSettings.savePlayerData(this); // Save login info on successful login
                clientRights = socketStream.readUnsignedByte();
                // flagged = socketStream.read() == 1;
                Audio.soundEffectCount = 0;
                MouseHandler.idleCycles = 0;
                Client.isLoading = true;

                currentSkill = -1;
                totalExp = 0L;
                MouseRecorder.instance.onLogin();
                KeyRecorder.instance.onLogin();
                activeFocus = true;
                aBoolean954 = true;
                loggedIn = true;
                outBuffer.index = 0;
                incoming.index = 0;
                opcode = -1;
                lastOpcode = -1;
                secondLastOpcode = -1;
                thirdLastOpcode = -1;
                packetSize = 0;
                systemUpdateTime = 0;
                mouseIdleGameTicks = 0;
                mobOverlayRenderer.onLogin();
                menuOptionsCount = 0;
                menuOpen = false;
                soundCycleCounter = 0;
                for (int index = 0; index < 500; index++)
                    chatMessages[index] = null;
                itemSelected = 0;
                spellSelected = 0;
                setLoadingStage(0);
                GameFrame.setNorth(this);
                GameFrame.minimapState = 0;
                lastKnownPlane = -1;
                destinationX = 0;
                destinationY = 0;
                Players_count = 0;
                npcCount = 0;
                for (int index = 0; index < MAX_ENTITY_COUNT; index++) {
                    players[index] = null;
                    playerSynchronizationBuffers[index] = null;
                }
                for (int index = 0; index < 16384; index++)
                    npcs[index] = null;
                localPlayer = players[LOCAL_PLAYER_ID] = new Player();
                projectiles.clear();
                incompleteAnimables.clear();
                clearRegionalSpawns();
                fullscreenInterfaceID = -1;
                PlayerRelations.friendServerStatus = 0;
                PlayerRelations.friendsCount = 0;
                dialogueId = -1;
                backDialogueId = -1;
                ClientCompanion.openInterfaceId = -1;
                ClientCompanion.openInterfaceId2 = -1;
                ClientCompanion.interfaceInputSelected = -1;
                overlayInterfaceId = -1;
                openWalkableInterface = -1;
                continuedDialogue = false;
                ClientCompanion.tabId = 3;
                inputDialogState = 0;
                menuOpen = false;
                messagePromptRaised = false;
                clickToContinueString = null;
                multicombat = 0;
                flashingSidebarId = -1;
                maleCharacter = true;
                IdentityKit.changeCharacterGender(this);
                for (int index = 0; index < 5; index++)
                    characterDesignColours[index] = 0;
                for (int index = 0; index < 5; index++) {
                    playerOptions[index] = null;
                    playerOptionsHighPriority[index] = false;
                }

                ClientCompanion.bank10ActionCount = 0;
                ClientCompanion.walkActionCount = 0;
                ClientCompanion.playerOptionActionCount = 0;
                ClientCompanion.playerOption2ActionCount = 0;
                ClientCompanion.objectOption5ActionCount = 0;
                ClientCompanion.npcOption2ActionCount = 0;
                ClientCompanion.npcOption3ActionCount = 0;
                ClientCompanion.npcOption4ActionCount = 0;

                Rasterizer3D.changeBrightness(ClientCompanion.brightnessState);
                SettingsWidget.updateSettings();
                ExpCounterSetup.updateSettings();
                for (Spellbooks.Spellbook spellbook : Spellbooks.Spellbook.values()) {
                    Spellbooks.update(spellbook);
                }

                sendConfiguration(171, Configuration.enableChatEffects ? 1 : 0);
                sendConfiguration(287, Configuration.enableSplitPrivate ? 1 : 0);
                sendConfiguration(170, settings[170]);
                sendConfiguration(427, settings[427]);

                GameShell.updateGameState(30);

                Audio.changeMusicVolume(this, Configuration.gameMusicVolume);

//                this.stopMidi();
                setupGameplayScreen();
                ClientCompanion.firstRender = false;
                return;
            }
            if (response == 28) {
                LoginScreen.firstLoginMessage = "Username or password contains illegal";
                LoginScreen.secondLoginMessage = "characters. Try other combinations.";
                return;
            }
            if (response == 30) {
                LoginScreen.firstLoginMessage = "Old client usage detected.";
                LoginScreen.secondLoginMessage = "Please download the latest one.";
                MiscUtils.launchURL("https://www.grinderscape.org/download");
                return;
            }
            if (response == 2) {
                LoginScreen.firstLoginMessage = "";
                LoginScreen.secondLoginMessage = "Invalid username or password.";
                return;
            }
            if (response == 4) {
                LoginScreen.firstLoginMessage = "Your account has been banned.";
                LoginScreen.secondLoginMessage = "";
                return;
            }
            if (response == 22) {
                LoginScreen.firstLoginMessage = "Your computer has been banned.";
                LoginScreen.secondLoginMessage = "";
                return;
            }
            if (response == 27) {
                LoginScreen.firstLoginMessage = "Your host-address has been banned.";
                LoginScreen.secondLoginMessage = "";
                return;
            }
            if (response == 5) {
                LoginScreen.firstLoginMessage = "Your account is already logged in.";
                LoginScreen.secondLoginMessage = "Try again in 60 secs...";
                return;
            }
            if (response == 6) {
                LoginScreen.firstLoginMessage = Configuration.CLIENT_NAME + " is being updated.";
                LoginScreen.secondLoginMessage = "Try again in 60 secs...";
                return;
            }
            if (response == 7) {
                LoginScreen.firstLoginMessage = "The world is currently full.";
                LoginScreen.secondLoginMessage = "";
                return;
            }
            if (response == 8) {
                LoginScreen.firstLoginMessage = "Unable to connect.";
                LoginScreen.secondLoginMessage = "Login server offline.";
                return;
            }
            if (response == 9) {
                LoginScreen.firstLoginMessage = "Login limit exceeded.";
                LoginScreen.secondLoginMessage = "Too many connections from your address.";
                return;
            }
            if (response == 10) {
                LoginScreen.firstLoginMessage = "Unable to connect. Bad session id.";
                LoginScreen.secondLoginMessage = "Try again in 60 secs...";
                return;
            }
            if (response == 11) {
                LoginScreen.firstLoginMessage = "Login server rejected session.";
                LoginScreen.secondLoginMessage = "Try again in 60 secs...";
                return;
            }
            if (response == 12) {
                LoginScreen.firstLoginMessage = "You need a members account to login to this world.";
                LoginScreen.secondLoginMessage = "Please subscribe, or use a different world.";
                return;
            }
            if (response == 13) {
                LoginScreen.firstLoginMessage = "Could not complete login.";
                LoginScreen.secondLoginMessage = "Please try using a different world.";
                return;
            }
            if (response == 14) {
                LoginScreen.firstLoginMessage = "The server is being updated.";
                LoginScreen.secondLoginMessage = "Please wait 1 minute and try again.";
                return;
            }
            if (response == 15) {
                loggedIn = true;
                incoming.index = 0;
                opcode = -1;
                lastOpcode = -1;
                secondLastOpcode = -1;
                thirdLastOpcode = -1;
                packetSize = 0;
                systemUpdateTime = 0;
                menuOptionsCount = 0;
                menuOpen = false;
                loadingStartTime = System.currentTimeMillis();
                return;
            }
            if (response == 16) {
                LoginScreen.firstLoginMessage = "Login attempts exceeded.";
                LoginScreen.secondLoginMessage = "Please wait 1 minute and try again.";
                return;
            }
            if (response == 17) {
                LoginScreen.firstLoginMessage = "You are standing in a members-only area.";
                LoginScreen.secondLoginMessage = "To play on this world move to a free area first";
                return;
            }
            if (response == 20) {
                LoginScreen.firstLoginMessage = "Invalid loginserver requested";
                LoginScreen.secondLoginMessage = "Please try using a different world.";
                return;
            }
            if (response == 35) {
                LoginScreen.firstLoginMessage = "Your account has been locked for safety purposes.";
                LoginScreen.secondLoginMessage = "Please verify your the real account owner on Discord.";
                return;
            }
            if (response == 21) {
                for (int k1 = socketStream.readUnsignedByte(); k1 >= 0; k1--) {
                    LoginScreen.firstLoginMessage = "You have only just left another world";
                    LoginScreen.secondLoginMessage = "Your profile will be transferred in: " + k1 + " seconds";
                    LoginScreen.drawLoginScreen(this, true);
                    try {
                        Thread.sleep(1000L);
                    } catch (Exception e) {
                        Log.error("world switch sleep failed", e);
                    }
                }
                login(name, password, reconnecting);
                return;
            }
            if (response == 69) {
                LoginScreen.firstLoginMessage = "Hey welcome! Please solve the captcha";
                LoginScreen.secondLoginMessage = "to verify you're not a bot :)";
                ClientUI.launchCaptcha();
                return;
            }
            if (response == -1) {
                if (copy == 0) {
                    if (LoginScreen.loginFailures < 2) {
                        try {
                            Thread.sleep(2000L);
                        } catch (Exception e) {
                            Log.error("re-authentication sleep failed", e);
                        }
                        LoginScreen.loginFailures++;
                        login(name, password, reconnecting);
                        return;
                    } else {
                        LoginScreen.firstLoginMessage = "No response from loginserver";
                        LoginScreen.secondLoginMessage = "Please wait 1 minute and try again.";
                        return;
                    }
                } else {
                    LoginScreen.firstLoginMessage = "No response from server";
                    LoginScreen.secondLoginMessage = "Please try using a different world.";
                    return;
                }
            } else {
                LoginScreen.firstLoginMessage = "Unexpected server response";
                LoginScreen.secondLoginMessage = "Please try using a different world.";
                return;
            }
        } catch (IOException _ex) {
            LoginScreen.firstLoginMessage = "";
            Log.error("Login IO failure", _ex);
        } catch (Exception e) {
            Log.error("Error while generating uid. Skipping step.", e);
        }
        LoginScreen.secondLoginMessage = "Error connecting to server.";
    }

    public void clearRegionalSpawns() {
        for (int plane = 0; plane < 4; plane++) {
            for (int x = 0; x < 104; x++) {
                for (int y = 0; y < 104; y++) {
                    groundItems[plane][x][y] = null;
                    updateGroundItems(x, y);
                }
            }
        }
        if (spawns == null) {
            spawns = new NodeDeque();
        }
        for (SpawnedObject object = (SpawnedObject) spawns
                .last(); object != null; object = (SpawnedObject) spawns.previous())
            object.getLongetivity = 0;
    }

    public boolean shiftTeleport() {
        return (KeyHandler.pressedKeys[81] && clientRights >= 4 && clientRights <= 7);
    }

    private void updateNpcBlocks(Buffer stream) {
        for (int i = 0; i < mobsAwaitingUpdateCount; i++) {

            final int localIndex = mobsAwaitingUpdate[i];
            final Npc npc = npcs[localIndex];
            final int mask = stream.readUnsignedShort();

//            System.out.println("mask = "+mask);
            if ((mask & 0x10) != 0) {

                int animationId = stream.readLEUShort();
//                System.out.println("animation id = "+animationId);
                if (animationId == 65535)
                    animationId = -1;
                int i2 = stream.readUnsignedByte();
                if (animationId == npc.getSequence() && animationId != -1) {
                    int l2 = Animation.getSequenceDefinition(animationId).replayMode;
                    if (l2 == 1) {
                        npc.sequenceFrame = 0;
                        npc.sequenceFrameCycle = 0;
                        npc.sequenceDelay = i2;
                        npc.currentAnimationLoopCount = 0;
                    }
                    if (l2 == 2)
                        npc.currentAnimationLoopCount = 0;
                } else if (animationId == -1 || npc.getSequence() == -1
                        || Animation.getSequenceDefinition(animationId).forcedPriority >= Animation.getSequenceDefinition(npc.getSequence()).forcedPriority) {
                    npc.setSequence(animationId);
                    npc.sequenceFrame = 0;
                    npc.sequenceFrameCycle = 0;
                    npc.sequenceDelay = i2;
                    npc.currentAnimationLoopCount = 0;
                    npc.__ch = npc.pathLength;
                }
            }
            if ((mask & 0x80) != 0) {
                npc.spotAnimation = stream.readUShort();
//                System.out.println("spotanim = "+npc.spotAnimation);
                int k1 = stream.readInt();
                npc.heightOffset = k1 >> 16;
                npc.graphicDelay = tick + (k1 & 0xffff);
                npc.setSpotAnimationFrame(0);
                npc.spotAnimationFrameCycle = 0;
                if (npc.graphicDelay > tick)
                    npc.setSpotAnimationFrame(-1);
                if (npc.spotAnimation == 65535)
                    npc.spotAnimation = -1;
            }
            if ((mask & 8) != 0) {
                int damage = stream.readShort();
                int type = stream.readUnsignedByte();
                int hp = stream.readShort();
                int maxHp = stream.readShort();
//                System.out.println("read damage mask "+damage+", "+type+", "+hp+", "+maxHp);
                npc.updateHitData(type, damage, tick);
                npc.loopCycleStatus = tick + 300;
                npc.currentHealth = hp;
                npc.maxHealth = maxHp;
            }
            if ((mask & 0x20) != 0) {
                npc.targetIndex = stream.readUShort();
                if (npc.targetIndex == 65535)
                    npc.targetIndex = -1;
            }
            if ((mask & 1) != 0) {
                npc.spokenText = stream.readString();
                npc.textCycle = 100;
            }
            if ((mask & 0x40) != 0) {
                int damage = stream.readShort();
                int type = stream.readUnsignedByte();
                int hp = stream.readShort();
                int maxHp = stream.readShort();
                npc.updateHitData(type, damage, tick);
                npc.loopCycleStatus = tick + 300;
                npc.currentHealth = hp;
                npc.maxHealth = maxHp;
            }
            if ((mask & 0x2) != 0) {
                npc.headIcon = stream.readUnsignedByte();
                boolean transform = stream.readUnsignedByte() == 1;

                if (transform) {
                    npc.desc = NpcDefinition.lookup(stream.readLEUShortA());
                    npc.size = npc.desc.size;
                    npc.degreesToTurn = npc.desc.degreesToTurn;
                    npc.walkSequence = npc.desc.walkSequence;
                    npc.walkTurnSequence = npc.desc.walkTurnSequence;
                    npc.walkTurnLeftSequence = npc.desc.walkTurnLeftSequence;
                    npc.walkTurnRightSequence = npc.desc.walkTurnRightSequence;
                    npc.idleSequence = npc.desc.standAnim;
                    npc.turnRightSequence = npc.desc.turnRightSequence;
                    npc.turnLeftSequence = npc.desc.turnLeftSequence;
                }
            }
            if ((mask & 4) != 0) {
                int faceX = stream.readShort();
                int faceY = stream.readShort();

                int x = npc.x - (faceX - baseX - baseX) * 64;
                int y = npc.y - (faceY - baseY - baseY) * 64;
//                System.out.println("facing "+faceX+" -> "+x+", "+faceY+" -> "+y);

                if (x != 0 || y != 0) {
                    npc.faceDegrees = (int) (Math.atan2(x, y) * 325.949D) & 2047;
                }
            }
            if ((mask & 0x400) != 0) {
                int initialX = stream.readUByteS();
                int initialY = stream.readUByteS();
                int destinationX = stream.readUByteS();
                int destinationY = stream.readUByteS();
                int startForceMovement = stream.readLEUShortA() + tick;
                int endForceMovement = stream.readUShortA() + tick;
                int animation = stream.readLEUShortA();
                int direction = stream.readUByteS();

                npc.initialX = initialX;
                npc.initialY = initialY;
                npc.destinationX = destinationX;
                npc.destinationY = destinationY;
                npc.startForceMovement = startForceMovement;
                npc.endForceMovement = endForceMovement;
                npc.direction = direction;

                if (animation >= 0) {
                    npc.setSequence(animation);
                    npc.sequenceFrame = 0;
                    npc.sequenceFrameCycle = 0;
                    npc.sequenceDelay = 0;
                    npc.currentAnimationLoopCount = 0;
                    npc.__ch = npc.pathLength;
                }
                npc.resetPath();
            }
        }
    }

    private void method89(SpawnedObject spawnObjectRequest) {
        long id = 0L;
        int j = -1;
        int k = 0;
        int l = 0;
        if (spawnObjectRequest.group == 0)
            id = scene.getWallObjectUid(spawnObjectRequest.plane, spawnObjectRequest.x, spawnObjectRequest.y);
        if (spawnObjectRequest.group == 1)
            id = scene.getWallDecorationUid(spawnObjectRequest.plane, spawnObjectRequest.x, spawnObjectRequest.y);
        if (spawnObjectRequest.group == 2)
            id = scene.getGameObjectUid(spawnObjectRequest.plane, spawnObjectRequest.x, spawnObjectRequest.y);
        if (spawnObjectRequest.group == 3)
            id = scene.getGroundDecorationUid(spawnObjectRequest.plane, spawnObjectRequest.x, spawnObjectRequest.y);
        if (id != 0) {
            j = DynamicObject.get_object_key(id);
            k = DynamicObject.get_object_type(id);
            l = DynamicObject.get_object_orientation(id);
        }
        spawnObjectRequest.getPreviousId = j;
        spawnObjectRequest.previousType = k;
        spawnObjectRequest.previousOrientation = l;
    }

    public static FileArchive mediaArchive;

    private void startUp() {

        if (!Configuration.LOCALHOST)
            CacheDownloader.init(false);

        PlayerSettings.loadPlayerData(this);

        AtomicInteger totalDone = new AtomicInteger(0);
        SpriteLoader.loadSprites(() -> {
            double percentage = ((totalDone.incrementAndGet() * 1D) / (SpriteLoader.cache.length * 1D)) + 0.05;
            LoginScreen.drawLoadingText(this, (int) (3 + (percentage * 7D)),
                    "Loading Sprites (" + totalDone.get() + " / " + SpriteLoader.cache.length + ")");
        });
        SpriteLoader.init();
        LoginScreen.drawLoadingText(this, 20, "Starting up");
        if (SignLink.cache_dat != null) {
            for (int i = 0; i < SignLink.CACHE_INDEX_LENGTH; i++)
                indices[i] = new FileStore(SignLink.cache_dat, SignLink.indices[i], i + 1);
        }

        Js5Cache.worldHost = Configuration.connected_world.getAddress();
        OsCache.init();

        try {

            if (Configuration.JAGCACHED_ENABLED) {
                JagGrab.onStart();
            } else if (!Configuration.LOCALHOST) {
                CacheDownloader.init(false);
            }

            titleArchive = ClientUtil.createArchive(this, JagGrab.TITLE_CRC, "title screen", "title", JagGrab.CRCs[JagGrab.TITLE_CRC], 25);
            smallText = new GameFont(false, "p11_full", titleArchive);
            regularText = new GameFont(false, "p12_full", titleArchive);
            boldText = new GameFont(false, "b12_full", titleArchive);
            fancyFont = new GameFont(true, "q8_full", titleArchive);
            fancyFontLarge = new GameFont(false, "q8_full_large", titleArchive);

            newSmallFont = new RSFont(false, "p11_full", titleArchive);
            newRegularFont = new RSFont(false, "p12_full", titleArchive);
            newBoldFont = new RSFont(false, "b12_full", titleArchive);
            newFancyFont = new RSFont(true, "q8_full", titleArchive);
            newFancyFontLarge = new RSFont(false, "q8_full_large", titleArchive);

            ClientUtil.drawBackground();
            System.gc(); // removed from inside draw logo, added here, was causing fps issues in drawLoginScreen after changing to use a single ImageProducer
            loadTitleScreen();
            FileArchive configArchive = ClientUtil.createArchive(this, JagGrab.CONFIG_CRC, "config", "config", JagGrab.CRCs[JagGrab.CONFIG_CRC], 30);
            FileArchive interfaceArchive = ClientUtil.createArchive(this, JagGrab.INTERFACE_CRC, "interface", "interface", JagGrab.CRCs[JagGrab.INTERFACE_CRC], 35);
            mediaArchive = ClientUtil.createArchive(this, JagGrab.MEDIA_CRC, "2d graphics", "media", JagGrab.CRCs[JagGrab.MEDIA_CRC], 40);
            FileArchive streamLoader_6 = ClientUtil.createArchive(this, JagGrab.UPDATE_CRC, "update list", "versionlist", JagGrab.CRCs[JagGrab.UPDATE_CRC], 60);
            FileArchive textureArchive = ClientUtil.createArchive(this, JagGrab.TEXTURES_CRC, "textures", "textures", JagGrab.CRCs[JagGrab.TEXTURES_CRC], 45);
            FileArchive wordencArchive = ClientUtil.createArchive(this, JagGrab.CHAT_CRC, "chat system", "wordenc", JagGrab.CRCs[JagGrab.CHAT_CRC], 50);

            tileFlags = new byte[4][104][104];
            Tiles_heights = new int[4][105][105];
            scene = new SceneGraph(Tiles_heights);

            for (int j = 0; j < 4; j++)
                collisionMaps[j] = new CollisionMap();

            SpriteCompanion.minimapImage = new Sprite(512, 512);
            LoginScreen.drawLoadingText(this, 60, "Connecting to update server");
            RSFrame317.animationlist = new RSFrame317[3000][0];
            resourceProvider = new ResourceProvider();
            resourceProvider.initialize(streamLoader_6, this);
            Model.init(resourceProvider);

            if (Configuration.repackIndexOne) CacheUtils.repackCacheIndex(this, Store.MODEL);
            if (Configuration.repackIndexTwo) CacheUtils.repackCacheIndex(this, Store.ANIMATION);
            if (Configuration.repackIndexThree) CacheUtils.repackCacheIndex(this, Store.MUSIC);
            if (Configuration.repackIndexFour) CacheUtils.repackCacheIndex(this, Store.MAP);
            if (Configuration.dumpIndexOne) CacheUtils.dumpCacheIndex(this, Store.MODEL);
            if (Configuration.dumpIndexTwo) CacheUtils.dumpCacheIndex(this, Store.ANIMATION);
            if (Configuration.dumpIndexThree) CacheUtils.dumpCacheIndex(this, Store.MUSIC);
            if (Configuration.dumpIndexFour) CacheUtils.dumpCacheIndex(this, Store.MAP);

            LoginScreen.drawLoadingText(this, 80, "Unpacking media");
            unpackMedia(mediaArchive);

            LoginScreen.drawLoadingText(this, 83, "Unpacking textures");
            unpackTextures(textureArchive);

            LoginScreen.drawLoadingText(this, 86, "Unpacking config");
            unpackConfig(configArchive);

            ItemDefinition.isMembers = ClientCompanion.isMembers;

            LoginScreen.drawLoadingText(this, 95, "Unpacking interfaces");
            unpackInterfaces(interfaceArchive, mediaArchive);

            LoginScreen.drawLoadingText(this, 100, "Preparing game engine");
            prepareGameEngine(wordencArchive);

            return;
        } catch (Exception exception) {
            Log.error("loaderror " + LoginScreen.loadingText + " " + LoginScreen.loadingPercentage, exception);
        }
        ClientCompanion.loadingError = true;
    }

    private void prepareGameEngine(FileArchive wordencArchive) {
        for (int j6 = 0; j6 < 33; j6++) {
            int k6 = 999;
            int i7 = 0;
            for (int k7 = 0; k7 < 34; k7++) {
                if (IndexedImageCompanion.mapBack.palettePixels[k7 + j6 * IndexedImageCompanion.mapBack.width] == 0) {
                    if (k6 == 999)
                        k6 = k7;
                    continue;
                }
                if (k6 == 999)
                    continue;
                i7 = k7;
                break;
            }
            anIntArray968[j6] = k6;
            anIntArray1057[j6] = i7 - k6;
        }
        for (int l6 = 1; l6 < 153; l6++) {
            int j7 = 999;
            int l7 = 0;
            for (int j8 = 24; j8 < 177; j8++) {
                if (IndexedImageCompanion.mapBack.palettePixels[j8 + l6 * IndexedImageCompanion.mapBack.width] == 0 && (j8 > 34 || l6 > 34)) {
                    if (j7 == 999) {
                        j7 = j8;
                    }
                    continue;
                }
                if (j7 == 999) {
                    continue;
                }
                l7 = j8;
                break;
            }
            minimapLeft[l6 - 1] = j7 - 24;
            minimapLineWidth[l6 - 1] = l7 - j7;
        }
        setBounds();
        MessageCensor.load(wordencArchive);

        DynamicObject.clientInstance = this;
        ObjectDefinition.clientInstance = this;
        NpcDefinition.clientInstance = this;

        SpriteCompanion.setSkillSprites();
    }

    private void unpackInterfaces(FileArchive interfaceArchive, FileArchive mediaArchive) {
        GameFont[] gameFonts = {smallText, regularText, boldText, fancyFont, fancyFontLarge};
        Widget.load(interfaceArchive, gameFonts, mediaArchive,
                new RSFont[]{newSmallFont, newRegularFont, newBoldFont, newFancyFont, newFancyFontLarge});
    }

    private void unpackConfig(FileArchive configArchive) throws IOException {
        IdentityKit.init(configArchive);
        VariablePlayer.init(configArchive);
        VariableBits.init(configArchive);
        AreaDefinition.init(configArchive);
    }

    private void unpackTextures(FileArchive textureArchive) {
        Rasterizer3D.loadTextures(textureArchive);
        Rasterizer3D.changeBrightness(0.80000000000000004D);
        Rasterizer3D.tryInitiateTextureCache();
    }

    private void unpackMedia(FileArchive mediaArchive) {
        SkillOrbs.init();
        SpriteCompanion.hp = SpriteLoader.getSprite(40);
        worldMapMarker = SpriteLoader.getSprite(571).convertToImage();
        SpriteCompanion.multiOverlay = new Sprite(mediaArchive, "overlay_multiway", 0);
        IndexedImageCompanion.mapBack = new IndexedImage(mediaArchive, "mapback", 0);
        for (int j3 = 0; j3 <= 14; j3++) {
            /*
              Cache sprite icons, didn't want to do any repacking because tom's suite can mess up cache
             */
            if (j3 == 1) {
                SpriteCompanion.sideIcons[j3] = SpriteLoader.getSprite(144);
                continue;
            } else if (j3 == 3) { // inventory
                SpriteCompanion.sideIcons[j3] = SpriteLoader.getSprite(145);
                continue;
            } else if (j3 == 13) { // notes
                SpriteCompanion.sideIcons[j3] = SpriteLoader.getSprite(360);
                continue;
            }
            SpriteCompanion.sideIcons[j3] = new Sprite(mediaArchive, "sideicons", j3);
        }
        SpriteCompanion.compass = new Sprite(mediaArchive, "compass", 0);
        try {
            for (int k3 = 0; k3 < 100; k3++)
                IndexedImageCompanion.mapScenes[k3] = new IndexedImage(mediaArchive, "mapscene", k3);
        } catch (Exception ignored) {
        }
        try {
            for (int l3 = 0; l3 < 115; l3++)
                SpriteCompanion.mapFunctions[l3] = new Sprite(mediaArchive, "mapfunction", l3);
        } catch (Exception ignored) {
        }
        try {
            for (int i4 = 0; i4 < 20; i4++)
                SpriteCompanion.hitMarks[i4] = new Sprite(mediaArchive, "hitmarks", i4);
        } catch (Exception ignored) {
        }
        try {
            for (int h1 = 0; h1 < 6; h1++)
                SpriteCompanion.headIconsHint[h1] = new Sprite(mediaArchive, "headicons_hint", h1);
        } catch (Exception ignored) {
        }
        try {
            for (int j4 = 0; j4 < 18; j4++)
                SpriteCompanion.headIcons[j4] = new Sprite(mediaArchive, "headicons_prayer", j4);
            for (int j45 = 0; j45 < 3; j45++)
                SpriteCompanion.skullIcons[j45] = new Sprite(mediaArchive, "headicons_pk", j45);
        } catch (Exception ignored) {
        }
        try {
            int i = 0;
            SpriteCompanion.autoBackgroundSprites[i++] = new Sprite(mediaArchive, "tradebacking", 0);
            for (int j = 0; j < 4; j++)
                SpriteCompanion.autoBackgroundSprites[i++] = new Sprite(mediaArchive, "steelborder", j);
            for (int j = 0; j < 2; j++)
                SpriteCompanion.autoBackgroundSprites[i++] = new Sprite(mediaArchive, "steelborder2", j);
            for (int j = 2; j < 4; j++)
                SpriteCompanion.autoBackgroundSprites[i++] = new Sprite(mediaArchive, "miscgraphics", j);
        } catch (Exception ignored) {
        }
        try {
            int i = 0;
            SpriteCompanion.autoBackgroundSprites2[i++] = new Sprite(mediaArchive, "miscgraphics3", 3);
            for (int j = 6; j < 13; j++)
                SpriteCompanion.autoBackgroundSprites2[i++] = new Sprite(mediaArchive, "miscgraphics3", j);
        } catch (Exception ignored) {
        }
        SpriteCompanion.mapFlag = new Sprite(mediaArchive, "mapmarker", 0);
        SpriteCompanion.mapMarker = new Sprite(mediaArchive, "mapmarker", 1);
        for (int k4 = 0; k4 < 8; k4++)
            SpriteCompanion.crosses[k4] = new Sprite(mediaArchive, "cross", k4);
        SpriteCompanion.mapDotItem = new Sprite(mediaArchive, "mapdots", 0);
        SpriteCompanion.mapDotNPC = new Sprite(mediaArchive, "mapdots", 1);
        SpriteCompanion.mapDotPlayer = new Sprite(mediaArchive, "mapdots", 2);
        SpriteCompanion.mapDotFriend = new Sprite(mediaArchive, "mapdots", 3);
        SpriteCompanion.mapDotTeam = new Sprite(mediaArchive, "mapdots", 4);
        SpriteCompanion.mapDotClan = new Sprite(mediaArchive, "mapdots", 5);
        SpriteCompanion.scrollBar1 = new Sprite(mediaArchive, "scrollbar", 0);
        SpriteCompanion.scrollBar2 = new Sprite(mediaArchive, "scrollbar", 1);
        SpriteCompanion.scrollBar3 = SpriteLoader.getSprite(848);
        SpriteCompanion.scrollBar4 = SpriteLoader.getSprite(849);

        Rights.loadModIcons();

        Sprite sprite = SpriteLoader.getSprite(855);
        leftFrame = constructGraphicsBuffer(sprite.myWidth, sprite.myHeight);
        sprite.method346(0, 0);
        sprite = SpriteLoader.getSprite(854);
        topFrame = constructGraphicsBuffer(sprite.myWidth, sprite.myHeight);
        sprite.method346(0, 0);

    }

    private void updatePlayerList(Buffer stream, int packetSize) {

        int i = 0;
        while (stream.bitPosition + 10 < packetSize * 8) {

            int index = stream.readBits(11);
            if (index == 2047) {
                break;
            }
//            System.out.println("updatePlayerList -> index["+(i++)+"] = "+index);
            if (players[index] == null) {
                players[index] = new Player();
                if (playerSynchronizationBuffers[index] != null) {
                    players[index].readAppearanceUpdate(playerSynchronizationBuffers[index]);
                }
            }
            Players_indices[Players_count++] = index;
            Player player = players[index];
            player.npcCycle = tick;

            int update = stream.readBits(1);

            if (update == 1)
                mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = index;

            int discardWalkingQueue = stream.readBits(1);
            int y = stream.readBits(5);
            if (y > 15) y -= 32;
            int x = stream.readBits(5);
            if (x > 15) x -= 32;
            player.setPos(localPlayer.pathX[0] + x, localPlayer.pathY[0] + y, discardWalkingQueue == 1);
        }
        stream.disableBitAccess();
    }

    public void processMainScreenClick() {
        if (ClientCompanion.openInterfaceId == 15244) {
            return;
        }
        if (GameFrame.minimapState != 0) {
            return;
        }
        if (GameFrameInput.hoveringSpecialOrb) {
            return;
        }
        if (MouseHandler.lastButton == 1) {
            int i = MouseHandler.lastPressedX - 25 - 547;
            int j = MouseHandler.lastPressedY - 5 - 3;
            if (ClientUI.frameMode != ScreenMode.FIXED) {
                i = MouseHandler.lastPressedX - (ClientUI.frameWidth - 182 + 24);
                j = MouseHandler.lastPressedY - 8;
            }
            if (ClientUtil.inCircle(0, 0, i, j, 76) && mouseMapPosition() && !GameFrameInput.hoveringEnergyOrb && !GameFrameInput.hoveringWorldOrb && !GameFrameInput.hoveringNewsOrb) {
                i -= 73;
                j -= 80;
                int k = minimapOrientation + minimapRotation & 0x7ff;
                int i1 = Rasterizer3D.Rasterizer3D_sine[k];
                int j1 = Rasterizer3D.Rasterizer3D_cosine[k];
                i1 = i1 * (minimapZoom + 256) >> 8;
                j1 = j1 * (minimapZoom + 256) >> 8;
                int k1 = j * i1 + i * j1 >> 11;
                int l1 = j * j1 - i * i1 >> 11;
                int i2 = localPlayer.x + k1 >> 7;
                int j2 = localPlayer.y - l1 >> 7;
                boolean flag1 = pathFinder.doWalkTo(this, PathFinder.MINIMAP_WALK, i2, j2, true);
            }
            ClientCompanion.anInt1117++;
            if (ClientCompanion.anInt1117 > 1151) {
                ClientCompanion.anInt1117 = 0;
            }
        }
        if (MouseHandler.currentButton == 0)
            ChatBox.stopResizingChatArea();
    }

    public URL getCodeBase() {
        final String uri = Configuration.connected_world.getAddress() + ":" + (80 + ClientCompanion.portOffset);
        try {
            return new URL(uri);
        } catch (Exception e) {
            Log.error("Failed to get code base, uri = " + uri, e);
        }
        return null;
    }

    public String getParameter(String s) {
        if (SignLink.mainapp != null)
            return SignLink.mainapp.getParameter(s);
        else
            return super.getParameter(s);
    }

    public AppletContext getAppletContext() {
        if (SignLink.mainapp != null)
            return SignLink.mainapp.getAppletContext();
        else
            return super.getAppletContext();
    }

    @Override
    public void init() {
        initialize();
        startThread(ClientUI.frameWidth, ClientUI.frameHeight, 180);
    }

    private void processNpcMovement() {
        for (int j = 0; j < npcCount; j++) {
            int k = npcIndices[j];
            Npc npc = npcs[k];
            if (npc != null)
                processMovement(npc);
        }
    }

    private void processMovement(Mob mob) {

        if (mob.startForceMovement > tick) {
            mob.nextPreForcedStep();
        } else if (mob.endForceMovement >= tick) {
            mob.nextForcedMovementStep();
        } else {
            mob.nextStep();
        }

        if (mob.x < 128 || mob.y < 128 || mob.x >= 13184 || mob.y >= 13184) {
            mob.setSequence(-1);
            mob.spotAnimation = -1;
            mob.startForceMovement = 0;
            mob.endForceMovement = 0;
            mob.x = mob.pathX[0] * 128 + mob.size * 64;
            mob.y = mob.pathY[0] * 128 + mob.size * 64;
            mob.resetPath();
        }
        if (mob == localPlayer && (mob.x < 1536 || mob.y < 1536 || mob.x >= 11776 || mob.y >= 11776)) {
            mob.setSequence(-1);
            mob.spotAnimation = -1;
            mob.startForceMovement = 0;
            mob.endForceMovement = 0;
            mob.x = mob.pathX[0] * 128 + mob.size * 64;
            mob.y = mob.pathY[0] * 128 + mob.size * 64;
            mob.resetPath();
        }
        if (mob == localPlayer) {
            int localX = (this.currentRegionX * 8) - 48 + localPlayer.pathX[0];
            int localY = (this.currentRegionY * 8) - 48 + localPlayer.pathY[0];

            //GrinderScape.discord.setRegionId(localX, localY);
        }
        appendFocusDestination(mob);
        mob.updateAnimation();
    }

    private void appendFocusDestination(Mob actor) {

        if (actor.degreesToTurn == 0)
            return;

        if (actor.targetIndex != -1) {
            try {
                final Mob target = actor.targetIndex < 32768
                        ? npcs[actor.targetIndex]
                        : actor.targetIndex - 32768 == localPlayerIndex
                        ? players[LOCAL_PLAYER_ID]
                        : players[actor.targetIndex - 32768];
                if (target != null) {
                    final int deltaX = actor.x - target.x;
                    final int deltaY = actor.y - target.y;
                    if (deltaX != 0 || deltaY != 0) {
                        actor.orientation = (int) (Math.atan2(deltaX, deltaY) * 325.949D) & 2047;
                    }
                }
            } catch (Exception e) {
                Log.error("Invalid `targetIndex` {" + actor.targetIndex + "} for actor {" + actor.toString() + "}", e);
            }
        }

        if (actor.faceDegrees != -1 && (actor.pathLength == 0 || actor.__cq > 0)) {
            actor.orientation = actor.faceDegrees;
            actor.faceDegrees = -1;
        }

        int var4 = actor.orientation - actor.orientationScene & 2047;

        if (var4 == 0) {
            actor.__cm = 0;
            return;
        }

        actor.__cm++;
        boolean var6;
        if (var4 > 1024) {
            actor.orientationScene -= actor.degreesToTurn;
            var6 = true;
            if (var4 < actor.degreesToTurn || var4 > 2048 - actor.degreesToTurn) {
                actor.orientationScene = actor.orientation;
                var6 = false;
            }

            if (actor.movementSequence == actor.idleSequence && (actor.__cm > 25 || var6)) {
                if (actor.turnLeftSequence != -1) {
                    actor.movementSequence = actor.turnLeftSequence;
                } else {
                    actor.movementSequence = actor.walkSequence;
                }
            }
        } else {
            actor.orientationScene += actor.degreesToTurn;
            var6 = true;
            if (var4 < actor.degreesToTurn || var4 > 2048 - actor.degreesToTurn) {
                actor.orientationScene = actor.orientation;
                var6 = false;
            }

            if (actor.movementSequence == actor.idleSequence && (actor.__cm > 25 || var6)) {
                if (actor.turnRightSequence != -1) {
                    actor.movementSequence = actor.turnRightSequence;
                } else {
                    actor.movementSequence = actor.walkSequence;
                }
            }
        }

        actor.orientationScene &= 2047;
    }

    private void drawGameScreen(boolean fixedMode) {

        if (reAddGameScreen())
            setupGameplayScreen();

        if (welcomeScreenRaised) {
            LoginScreen.drawWelcomeScreen(this);
            return;
        }

        if (overlayInterfaceId != -1) {
            try {
                processWidgetAnimations(tickDelta, overlayInterfaceId);
            } catch (Exception ex) {
                Log.error("Failed to process widget animations, overlayInterfaceId=" + overlayInterfaceId, ex);
            }
        }

        GameFrame.drawTabArea(this);
        WidgetManager.handleChatArea(this);

        if (fixedMode) {
            topFrame.drawGraphics(canvas.getGraphics(), 0, 0);
            leftFrame.drawGraphics(canvas.getGraphics(), 0, 0);
            if (ChatBox.shouldRepaintChatBox()) {
                ChatBox.drawChatArea(this);
                ChatBox.setUpdateChatbox(false);
            }
        }

        if (getLoadingStage() == GAME_ASSETS_LOADED)
            drawGameWorld(fixedMode);

        if (getLoadingStage() == GAME_ASSETS_LOADED) {
            if (fixedMode) {
                GameFrame.drawMinimap(this);
                minimapImageProducer.drawGraphics(canvas.getGraphics(), 516, 0);
            }
        }

        if (flashingSidebarId != -1)
            ClientCompanion.tabAreaAltered = true;

        if (ClientCompanion.tabAreaAltered) {
            if (flashingSidebarId != -1 && flashingSidebarId == ClientCompanion.tabId)
                flashingSidebarId = -1;
            ClientCompanion.tabAreaAltered = false;
            chatSettingImageProducer.initDrawingArea();
            ClientCompanion.gameScreenImageProducer.initDrawingArea();
        }

        Audio.processObjectSounds(tickDelta);

        tickDelta = 0;
    }

    private void createStationaryGraphics() {
        AnimableObject animableObject = (AnimableObject) incompleteAnimables.last();
        for (; animableObject != null; animableObject = (AnimableObject) incompleteAnimables
                .previous())
            if (animableObject.anInt1560 != plane || animableObject.isFinished)
                animableObject.remove();
            else if (tick >= animableObject.cycleStart) {
                animableObject.advance(tickDelta);
                if (animableObject.isFinished)
                    animableObject.remove();
                else
                    scene.addAnimableA(animableObject.anInt1560, 0, animableObject.anInt1563, -1,
                            animableObject.anInt1562, 60, animableObject.anInt1561,
                            animableObject, false);
            }

    }

    public boolean mouseInGameArea() {
        if (ClientUI.frameMode == ScreenMode.FIXED) {
            return Client.instance.getMouseX() >= 4 && Client.instance.getMouseX() < 4 + gameAreaWidth && Client.instance.getMouseY() >= 4 && Client.instance.getMouseY() < 4 + gameAreaHeight;
        }

        return !mouseInChatArea() && !mouseInMinimapArea() && !mouseInTabArea();
    }

    private static final int VIEWPORT_OFF_X = 4;
    private static final int VIEWPORT_OFF_Y = 4;
    private int FIXED_GAME_WIDTH = 512 + VIEWPORT_OFF_X;
    private int FIXED_GAME_HEIGHT = 334 + VIEWPORT_OFF_Y;
    private int gameAreaWidth = FIXED_GAME_WIDTH, gameAreaHeight = FIXED_GAME_HEIGHT;

    /**
     * Checks if the mouse in in the chat area bounds.
     *
     * @return <code>true</code> if in bounds.
     */
    public boolean mouseInChatArea() {
        if (ClientUI.frameMode == ScreenMode.FIXED) {
            return Client.instance.getMouseX() >= 4 && Client.instance.getMouseX() < 4 + 516 && Client.instance.getMouseY() >= 4 + gameAreaHeight && Client.instance.getMouseY() < 4 + gameAreaHeight + 165;
        }

        return Client.instance.getMouseX() >= 0 && Client.instance.getMouseY() <= 516 && Client.instance.getMouseY() >= ClientUI.frameHeight - 165;
    }

    /**
     * Checks if the mouse in in the minimap area bounds.
     *
     * @return <code>true</code> if in bounds.
     */
    public boolean mouseInMinimapArea() {
        return Client.instance.getMouseX() >= 516 && Client.instance.getMouseY() <= 168;
    }

    /**
     * Checks if the mouse in in the tab area bounds.
     *
     * @return <code>true</code> if in bounds.
     */
    public boolean mouseInTabArea() {
        return Client.instance.getMouseY() >= (ClientUI.frameMode != ScreenMode.FIXED ? ClientUI.frameWidth - 240 : 516)
                && Client.instance.getMouseY() >= (ClientUI.frameMode != ScreenMode.FIXED ? ClientUI.frameHeight - 337 : 168);
    }


    public void drawInterface(int scroll_y, int x, Widget rsInterface, int y, int hoverXOffset, int hoverYOffset) {
        if (rsInterface == null)
            return;
        if (rsInterface.type != 0 || rsInterface.children == null)
            return;
        if (rsInterface.invisible && previousChildWidgetHoverType != rsInterface.id && previousChildWidgetHoverType2 != rsInterface.id
                && previousBackDialogueChildWidgetHoverType != rsInterface.id || rsInterface.hidden) {
            return;
        }
        if (rsInterface.id == 23300) {
            if (!Configuration.bountyHunterInterface) {
                return;
            }
        }

        if (rsInterface.id == 36100) {
            Rasterizer2D.drawTransparentBox(-100, 0, Rasterizer2D.Rasterizer2D_width + 100, Rasterizer2D.Rasterizer2D_height, 0x3e2f1d, 100);
        }

        switch (rsInterface.id) {
            case QuestTab.INTERFACE_ID:
                QuestTab.updateInterface();
                break;
            case Shop.INTERFACE_ID:
                Shop.updateInterface();
                break;
            case PriceChecker.INTERFACE_ID:
                PriceChecker.updateInterface();
                break;
            case ColorPicker.INTERFACE_ID:
                ColorPicker.updateInterface();
                break;
            case YellCustomizer.INTERFACE_ID:
                YellCustomizer.updateInterface();
                break;
            case ItemDropFinder.INTERFACE_ID:
                ItemDropFinder.updateInterface();
                break;
        }

        int clipLeft = Rasterizer2D.Rasterizer2D_xClipStart;
        int clipTop = Rasterizer2D.Rasterizer2D_yClipStart;
        int clipRight = Rasterizer2D.Rasterizer2D_xClipEnd;
        int clipBottom = Rasterizer2D.Rasterizer2D_yClipEnd;

        Rasterizer2D.Rasterizer2D_setClip(x, y + rsInterface.height, x + rsInterface.width, y);
        int childCount = rsInterface.children.length;


        for (int childId = 0; childId < childCount; childId++) {
            int _x = rsInterface.childX[childId] + x;
            int currentY = (rsInterface.childY[childId] + y) - scroll_y;
            Widget childInterface = Widget.interfaceCache[rsInterface.children[childId]];

            if (childInterface == null) {
                continue;
            }

            if (childInterface.invisible && previousChildWidgetHoverType != childInterface.id && previousChildWidgetHoverType2 != childInterface.id
                    && previousBackDialogueChildWidgetHoverType != childInterface.id || childInterface.hidden) {
                continue;
            }

            if (childInterface.id == 27656) {
                long totalExp = 0;
                for (int i = 0; i < 23; i++) {
                    totalExp += currentExp[i];
                }
                childInterface.setDefaultText("Total XP: " + NumberFormat.getInstance().format(totalExp));
            }

            _x += childInterface.horizontalOffset;
            currentY += childInterface.verticalOffset;
            if (childInterface.contentType > 0)
                LoginScreen.drawFriendsListOrWelcomeScreen(this, childInterface);
            /*
             * if (drawBlackBox) { drawBlackBox(_x, currentY); }
             */
            for (int r = 0; r < ClientCompanion.runeChildren.length; r++) {
                if (childInterface.id == ClientCompanion.runeChildren[r]) {
                    childInterface.modelZoom = 775;
                    break;
                }
            }
            int childX = _x;
            int childY = currentY;
            /*
             * Hovering
             */
            boolean childHovered = false;

            boolean hoverGameInterface = ClientCompanion.openInterfaceId != -1 && mouseInGameArea();

            boolean hoverChatInterface = backDialogueId != -1 && mouseInChatArea();

            boolean hoverTabInterface = ClientCompanion.tabInterfaceIDs[ClientCompanion.tabId] != -1 && mouseInTabArea();


            int hoverX = MouseHandler.x;

            int hoverY = MouseHandler.y;

            childX += childInterface.horizontalOffset;
            childY += childInterface.verticalOffset;

            if (!menuOpen && (hoverChatInterface || hoverGameInterface || hoverTabInterface)) {
                boolean inBounds = MouseHandler.x >= x + hoverXOffset && MouseHandler.y >= y + hoverYOffset
                        && MouseHandler.x < x + hoverXOffset + rsInterface.width && MouseHandler.y < y + hoverYOffset + rsInterface.height
                        && MouseHandler.x >= _x + hoverXOffset && MouseHandler.y >= currentY + hoverYOffset
                        && MouseHandler.x < _x + childInterface.width + hoverXOffset && MouseHandler.y < currentY + childInterface.height + hoverYOffset;

                    childHovered = inBounds;

                    if (hoverGameInterface) {
                        if (childInterface.layerId != 0) {
                            boolean relative = Widget.interfaceCache[ClientCompanion.openInterfaceId].parent == childInterface.layerId;

                            childHovered = inBounds && relative;
                        }
                    } else if (hoverTabInterface) {
                        if (childInterface.layerId != 0) {
                            childHovered = inBounds
                                    && Widget.interfaceCache[ClientCompanion.tabInterfaceIDs[ClientCompanion.tabId]].parent == childInterface.layerId;
                        }
                    } else if (hoverChatInterface) {
                        if (childInterface.layerId != 0) {
                            childHovered = inBounds && Widget.interfaceCache[backDialogueId].parent == childInterface.layerId;
                        }
                    }
                }
                if (!menuOpen
                        && MouseHandler.x >= x + hoverXOffset && MouseHandler.y >= y + hoverYOffset
                        && MouseHandler.x < x + hoverXOffset + rsInterface.width && MouseHandler.y < y + hoverYOffset + rsInterface.height
                        && MouseHandler.x >= _x + hoverXOffset && MouseHandler.y >= currentY + hoverYOffset
                        && MouseHandler.x < _x + childInterface.width + hoverXOffset && MouseHandler.y < currentY + childInterface.height + hoverYOffset) {
                    boolean flag = false;

                    if (rsInterface.id == ClientCompanion.openInterfaceId && ClientCompanion.openInterfaceId2 != -1) {
                        flag = true;
                    }

                    if (rsInterface.id == 81020 && ClientCompanion.openInterfaceId != -1) { // achievement completion overlay hovering
                        flag = true;
                    }

                    if (TitleChooser.isComponent(childInterface.id) && childInterface.tooltip != null && childInterface.tooltip.isEmpty()) {
                        flag = true;
                    }

                    if (childInterface.atActionType == Widget.OPTION_RESET_SETTING) {
                        if (childInterface.tooltip == null || childInterface.tooltip.length() == 0
                                || // prevents menu being shown on selected setting (e.g. bank settings menu)
                                (interfaceIsSelected(childInterface) && childInterface.hideTooltipIfSelected)) {
                            flag = true;
                        }
                    }

                    if (!flag) {
                        if ((childInterface.type == Widget.TYPE_HOVER_SPRITE || childInterface.type == Widget.TYPE_HOVER_SPEED_SPRITE || childInterface.type == Widget.TYPE_TRANSPARENT_HOVER_RECTANGLE)
                                && (!interfaceIsSelected(childInterface) || !childInterface.hideTooltipIfSelected)) {
                            childInterface.hovered = true;
                        }
                    }
                }

                if (childInterface.type == Widget.TYPE_CONTAINER) {
                    WidgetManager.handleContainer(this, hoverXOffset, hoverYOffset, _x, currentY, childInterface);
                } else if (childInterface.type == Widget.DYNAMIC_BUTTON) {
                    boolean hover = childHovered;

                    int boxColor = (hover ? 0x3c3329 : 0x534a3e);

                    if (childInterface.isToggled) {
                        boxColor = childInterface.toggleColor;
                    }

                    Rasterizer2D.fillRectangle(childX, childY + 3, childInterface.width - 5, childInterface.height - 3,
                            boxColor,
                            childInterface.isToggled ? childInterface.toggleTransparency : 255);

                    int hoverSpriteOffset = hover ? 8 : 0;

                    int hoverSpriteOffset2 = hover ? 8 : 0;

                    SpriteLoader.getSprite(1268 + hoverSpriteOffset2).repeatY(childX, childY + 9, childInterface.height - 9 * 2);

                    SpriteLoader.getSprite(1269 + hoverSpriteOffset2).repeatX(childX + 9, childY, childInterface.width - 9 * 2);

                    SpriteLoader.getSprite(1270 + hoverSpriteOffset2).repeatY(childX + childInterface.width - 9, childY + 9,
                            childInterface.height - 9 * 2);

                    SpriteLoader.getSprite(1271 + hoverSpriteOffset2).repeatX(childX + 9, childY + childInterface.height - 9,
                            childInterface.width - 9 * 2);

                    SpriteLoader.getSprite(1264 + hoverSpriteOffset).drawSprite(childX, childY);

                    SpriteLoader.getSprite(1265 + hoverSpriteOffset).drawSprite(childX + childInterface.width - 9, childY);

                    SpriteLoader.getSprite(1266 + hoverSpriteOffset).drawSprite(childX, childY + childInterface.height - 9);

                    SpriteLoader.getSprite(1267 + hoverSpriteOffset).drawSprite(childX + childInterface.width - 9,
                            childY + childInterface.height - 9);
                } else if (childInterface.type != Widget.TYPE_MODEL_LIST) {
                    if (childInterface.type == Widget.TYPE_INVENTORY) {
                        try {
                            WidgetManager.handleInventory(this, rsInterface, _x, currentY, childInterface);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (childInterface.type == Widget.TYPE_RECTANGLE) {
                        WidgetManager.handleRectangle(this, _x, currentY, childInterface);
                    } else if (childInterface.type == Widget.TYPE_TEXT) {
                        if (WidgetManager.handleText(this, rsInterface, _x, currentY, childInterface))
                            continue;
                    } else if (childInterface.type == Widget.TYPE_SPRITE) {
                        WidgetManager.handleSprite(this, _x, currentY, childInterface);
                    } else if (childInterface.type == Widget.TYPE_MODEL) {
                        WidgetManager.handleModel(this, _x, currentY, childInterface);
                    } else if (childInterface.type == Widget.TYPE_ITEM_LIST) {
                        WidgetManager.handleItemList(_x, currentY, childInterface);
                    } else if (childInterface.type == Widget.TYPE_OTHER && (previousBackDialogueChildWidgetId == childInterface.id
                            || previousChildWidgetId2 == childInterface.id || previousChildWidgetId == childInterface.id) && chatBoxUpdateTick == 0
                            && !menuOpen) {
                        WidgetManager.handleOther(this, x, rsInterface, y, _x, currentY, childInterface);
                    } else if (childInterface.type == Widget.TYPE_HOVER
                            || childInterface.type == Widget.TYPE_CONFIG_HOVER) {
                        if (WidgetManager.handleHover(_x, currentY, childInterface)) continue;
                    } else if (childInterface.type == Widget.TYPE_CONFIG) {
                        WidgetManager.handleConfig(_x, currentY, childInterface);
                    } else if (childInterface.type == Widget.TYPE_SLIDER) {
                        WidgetManager.handleSlider(_x, currentY, childInterface);
                    } else if (childInterface.type == Widget.TYPE_DROPDOWN) {
                        WidgetManager.handleDropdown(scrollBarFillColor, newSmallFont, _x, currentY, childInterface);
                    } else if (childInterface.type == Widget.TYPE_KEYBINDS_DROPDOWN) {
                        if (WidgetManager.handleKeyBindingDopDown(this, _x, currentY, childInterface))
                            continue;
                    } else if (childInterface.type == Widget.TYPE_ADJUSTABLE_CONFIG) {
                        WidgetManager.handleAdjustableConfig(_x, currentY, childInterface);
                    } else if (childInterface.type == Widget.TYPE_BOX) {
                        WidgetManager.handleBox(_x, currentY, childInterface);
                    } else if (childInterface.type == Widget.TYPE_MAP) {
                        WidgetManager.handleMap(this, _x, currentY);
                    } else if (childInterface.type == Widget.TYPE_PERCENTAGE) {
                        WidgetManager.handlePercentage(newRegularFont, _x, currentY, childInterface);
                    } else if (childInterface.type == Widget.TYPE_WINDOW) {
                        WidgetManager.handleWindow(_x, currentY, childInterface);
                    } else if (childInterface.type == Widget.TYPE_HORIZONTAL_SEPARATOR) {
                        WidgetManager.handleSeparator(_x, currentY, childInterface, childInterface.isModernWindow(), 822, 813);
                    } else if (childInterface.type == Widget.TYPE_VERTICAL_SEPARATOR) {
                        SpriteLoader.getSprite(childInterface.isModernWindow() ? 824 : 816).repeatY(_x, currentY, childInterface.height);
                    } else if (childInterface.type == Widget.TYPE_DARK_BOX) {
                        WidgetManager.handleDarkBox(_x, currentY, childInterface);
                    } else if (childInterface.type == Widget.TYPE_TRADE_BUTTON) {
                        WidgetManager.handleTradeButton(_x, currentY, childInterface);
                    } else if (childInterface.type == Widget.TYPE_HOVER_SPRITE) {
                        WidgetManager.handleHoverSprite(this, _x, currentY, childInterface);
                    } else if (childInterface.type == Widget.TYPE_HOVER_SPEED_SPRITE) {
                        WidgetManager.handleHoverSpeedSprite(this, _x, currentY, childInterface);
                    } else if (childInterface.type == Widget.TYPE_COLOR_BOX) {
                        WidgetManager.handleColorBox(newRegularFont, newSmallFont, _x, currentY, childInterface);
                    } else if (childInterface.type == Widget.TYPE_COLOR_PICKER) {
                        WidgetManager.handleColorPicker(x, rsInterface, y, hoverXOffset, hoverYOffset, _x, currentY, childInterface);
                    } else if (childInterface.type == Widget.TYPE_INPUT) {
                        WidgetManager.handleInput(newSmallFont, _x, currentY, childInterface);
                    } else if (childInterface.type == Widget.TYPE_TRANSPARENT_HOVER_RECTANGLE) {
                        WidgetManager.handleTransparentHoverRectangle(_x, currentY, childInterface);
                    }

                    if (childInterface.hovered)
                        childInterface.hovered = false;
                }
            }

            // Bank dragging item sprite
            if (ClientCompanion.overlayItemIcon != null && rsInterface.id == ClientCompanion.overlayItemInterfaceId) {
                int bindX = ClientCompanion.overlayItemX;
                if (bindX < Rasterizer2D.Rasterizer2D_xClipStart)
                    bindX = Rasterizer2D.Rasterizer2D_xClipStart;
                if (bindX > Rasterizer2D.Rasterizer2D_xClipEnd - 32)
                    bindX = Rasterizer2D.Rasterizer2D_xClipEnd - 32;
                int bindY = ClientCompanion.overlayItemY;
                if (bindY < Rasterizer2D.Rasterizer2D_yClipStart && rsInterface.scrollPosition == 0)
                    bindY = Rasterizer2D.Rasterizer2D_yClipStart;
                if (bindY > Rasterizer2D.Rasterizer2D_yClipEnd - 32)
                    bindY = Rasterizer2D.Rasterizer2D_yClipEnd - 32;

                int opacity = 128;

                int k10 = ClientCompanion.overlayItemAmount;

                if (k10 == 0) {
                    opacity = 95;
                }

                ClientCompanion.overlayItemIcon.drawTransparentSprite(bindX, bindY, opacity);

                if (!ClientCompanion.overlayItemHideAmount && k10 > -1 && (ClientCompanion.overlayItemIcon.maxWidth == 33 || k10 != 1)) {

                    if (k10 == 0) { // Placeholder text
                        newSmallFont.drawBasicString(ClientUtil.intToKOrMil(k10), bindX, bindY + 9, 0xFFFF00, 1, 120);
                    } else {
                        smallText.render(0, ClientUtil.intToKOrMil(k10), bindY + 10, bindX + 1); // Shadow
                        if (k10 >= 1)
                            smallText.render(0xFFFF00, ClientUtil.intToKOrMil(k10), bindY + 9, bindX);
                        if (k10 >= 100000)
                            smallText.render(0xFFFFFF, ClientUtil.intToKOrMil(k10), bindY + 9, bindX);
                        if (k10 >= 10000000)
                            smallText.render(0x00FF80, ClientUtil.intToKOrMil(k10), bindY + 9, bindX);
                    }
                }
            }

            if (ClientCompanion.enableGridOverlay) {
                for (int i : ClientCompanion.tabInterfaceIDs) {
                    if (i == rsInterface.id)
                        return;
                }
                drawGridOverlay();
            }
            Rasterizer2D.Rasterizer2D_setClip(clipLeft, clipBottom, clipRight, clipTop);
        }

        private void appendPlayerUpdateMask ( int mask, int index, Buffer buffer, Player player){
            if ((mask & 0x400) != 0) {
                int initialX = buffer.readUByteS();
                int initialY = buffer.readUByteS();
                int destinationX = buffer.readUByteS();
                int destinationY = buffer.readUByteS();
                int startForceMovement = buffer.readLEUShortA() + tick;
                int endForceMovement = buffer.readUShortA() + tick;
                int animation = buffer.readLEUShortA();
                int direction = buffer.readUByteS();

                player.initialX = initialX;
                player.initialY = initialY;
                player.destinationX = destinationX;
                player.destinationY = destinationY;
                player.startForceMovement = startForceMovement;
                player.endForceMovement = endForceMovement;
                player.direction = direction;

                if (animation >= 0) {
                    player.setSequence(animation);
                    player.sequenceFrame = 0;
                    player.sequenceFrameCycle = 0;
                    player.sequenceDelay = 0;
                    player.currentAnimationLoopCount = 0;
                    player.__ch = player.pathLength;
                }

                player.resetPath();
            }
            if ((mask & 0x100) != 0) {
                player.spotAnimation = buffer.readLEUShort();
                int info = buffer.readInt();
                player.heightOffset = info >> 16;
                player.graphicDelay = tick + (info & 0xffff);
                player.setSpotAnimationFrame(0);
                player.spotAnimationFrameCycle = 0;
                if (player.graphicDelay > tick)
                    player.setSpotAnimationFrame(-1);
                if (player.spotAnimation == 65535)
                    player.spotAnimation = -1;
            }
            if ((mask & 8) != 0) {
                int animation = buffer.readLEUShort();
                if (animation == 65535)
                    animation = -1;
                int delay = buffer.readNegUByte();

                if (animation == player.getSequence() && animation != -1) {
                    int replayMode = Animation.getSequenceDefinition(animation).replayMode;
                    if (replayMode == 1) {
                        player.sequenceFrame = 0;
                        player.sequenceFrameCycle = 0;
                        player.sequenceDelay = delay;
                        player.currentAnimationLoopCount = 0;
                    }
                    if (replayMode == 2)
                        player.currentAnimationLoopCount = 0;
                } else if (animation == -1 || player.getSequence() == -1
                        || Animation.getSequenceDefinition(animation).forcedPriority >= Animation.getSequenceDefinition(player.getSequence()).forcedPriority) {
                    player.setSequence(animation);
                    player.sequenceFrame = 0;
                    player.sequenceFrameCycle = 0;
                    player.sequenceDelay = delay;
                    player.currentAnimationLoopCount = 0;
                    player.__ch = player.pathLength;
                }
            }
            if ((mask & 4) != 0) {
//			System.out.println("	read spoken chat "+buffer.currentPosition);
                player.spokenText = buffer.readString();
                if (player.spokenText.charAt(0) == '~') {
                    player.spokenText = player.spokenText.substring(1);
//                sendMessage(player.spokenText, ChatBox.CHAT_TYPE_PUBLIC, player.name, player.title);
                } else if (player == localPlayer) {
                    //sendMessage(player.spokenText, ChatBox.CHAT_TYPE_PUBLIC, player.name, player.title);
                }

                player.textColour = 0;
                player.textEffect = 0;
                player.textCycle = 150;
            }
            if ((mask & 0x080) != 0) {
//			System.out.println("	read message chat "+buffer.currentPosition);
                int textColorAndEffect = buffer.readUShort();
                int privilege = buffer.readInt();
                int privilegeToShow = buffer.readUnsignedByte();
                int messageLength = buffer.readUnsignedByte();
                int originalOffset = buffer.index;

//            System.out.println("textColorAndEffect = "+textColorAndEffect);
//            System.out.println("privilege = "+privilege);
//            System.out.println("privilegeToShow = "+privilegeToShow);
//            System.out.println("messageLength = "+messageLength);
//            System.out.println("originalOffset = "+originalOffset);

                if (player.name != null && player.visible) {
                    long nameAsLong = StringUtils.encodeBase37(player.name);
                    boolean ignored = false;
                    if (privilege <= 1) {
                        for (int count = 0; count < PlayerRelations.ignoreCount; count++) {
                            if (PlayerRelations.ignoreListAsLongs[count] != nameAsLong)
                                continue;
                            ignored = true;
                            break;
                        }
                    }
                    if (!ignored && onTutorialIsland == 0 && !player.isHidden)
                        try {
                            // text = text.substring(1);
                            textStream.index = 0;
//                        incoming.getBytes2(messageLength, 0, textStream.payload);
                            incoming.copyArray(textStream.array, 0, messageLength);
                            textStream.index = 0;

                            String text = OSTextInput.applyHuffmanEncoding(textStream);//TextInput.readFromStream(messageLength, textStream);
                            text = OSTextInput.capitalize(text);
                            text = OSTextInput.escapeBrackets(text);

                            player.spokenText = text.trim();

                            if (Configuration.enableEmoticons && ((textColorAndEffect & 0xff) < 1 || (textColorAndEffect & 0xff) > 3)) {
                                player.spokenText = Emoji.handleSyntax(text);
                            }

                            player.textColour = textColorAndEffect >> 8;
                            player.rights = privilege;
                            player.rightsToShow = privilegeToShow;
                            player.textEffect = textColorAndEffect & 0xff;
                            player.textCycle = 150;

                            if (privilege > 0 || privilegeToShow + 1 > 0) {
                                sendMessage(text, ChatBox.CHAT_TYPE_MOD_PUBLIC, player.getImages() + player.name, player.title);
                            } else {
                                sendMessage(text, ChatBox.CHAT_TYPE_PUBLIC, player.name, player.title);
                            }

                        } catch (Exception exception) {
                            Log.error("cde2", exception);
                        }
                }

                buffer.index = originalOffset + messageLength;
                // buffer.currentPosition = off + offset;
            }
            if ((mask & 1) != 0) {
//			System.out.println("	read interacting entity "+buffer.currentPosition);
                player.targetIndex = buffer.readLEUShort();
                if (player.targetIndex == 65535)
                    player.targetIndex = -1;
            }

            if ((mask & 0x10) != 0) {
//			System.out.println("	read appearance "+buffer.currentPosition);
                int length = buffer.readNegUByte();
                byte[] data = new byte[length];
                Buffer appearanceBuffer = new Buffer(data);
                buffer.readBytes(length, 0, data);
                playerSynchronizationBuffers[index] = appearanceBuffer;
                player.readAppearanceUpdate(appearanceBuffer);
            }
            if ((mask & 2) != 0) {
//			System.out.println("	read face "+buffer.currentPosition);
                player.faceDegrees = buffer.readShort();
                if (player.pathLength == 0) {
                    player.orientation = player.faceDegrees;
                    player.faceDegrees = -1;
                }
            }
            if ((mask & 0x20) != 0) {
//			System.out.println("	read damage "+buffer.currentPosition);
                int damage = buffer.readShort();
                int type = buffer.readUnsignedByte();
                int hp = buffer.readShort();
                int maxHp = buffer.readShort();
                player.updateHitData(type, damage, tick);
                player.loopCycleStatus = tick + 300;
                player.currentHealth = hp;
                player.maxHealth = maxHp;
            }
            if ((mask & 0x200) != 0) {
//			System.out.println("	read damage 2 "+buffer.currentPosition);
                int damage = buffer.readShort();
                int type = buffer.readUnsignedByte();
                int hp = buffer.readShort();
                int maxHp = buffer.readShort();
                player.updateHitData(type, damage, tick);
                player.loopCycleStatus = tick + 300;
                player.currentHealth = hp;
                player.maxHealth = maxHp;
            }
            if ((mask & 0x800) != 0) {
//			System.out.println("	read colorful "+buffer.currentPosition);
                player.readColorfulItemUpdate(buffer);
            }
        }

        private void checkForGameUsages () {
            try {
                int j = localPlayer.x + cameraX2;
                int k = localPlayer.y + cameraY2;
                if (Camera.oculusOrbFocalPointX - j < -500 || Camera.oculusOrbFocalPointX - j > 500 || Camera.oculusOrbFocalPointY - k < -500 || Camera.oculusOrbFocalPointY - k > 500) {
                    Camera.oculusOrbFocalPointX = j;
                    Camera.oculusOrbFocalPointY = k;
                }
                // Key camera rotation speeds below
                if (Camera.oculusOrbFocalPointX != j)
                    Camera.oculusOrbFocalPointX += (j - Camera.oculusOrbFocalPointX) / 16;
                if (Camera.oculusOrbFocalPointY != k)
                    Camera.oculusOrbFocalPointY += (k - Camera.oculusOrbFocalPointY) / 16;

                if (MouseHandler.mouseWheelDown) {
                    double speedFactor = 4.0;
                    xAxisRotateSpeed += speedFactor * MouseHandler.mouseWheelDragX;
                    yAxisRotateSpeed += speedFactor * -MouseHandler.mouseWheelDragY;
                    MouseHandler.mouseWheelDragX = 0;
                    MouseHandler.mouseWheelDragY = 0;
                }

                if (KeyHandler.pressedKeys[96])
                    xAxisRotateSpeed += (-24 - xAxisRotateSpeed) / 2;
                else if (KeyHandler.pressedKeys[97])
                    xAxisRotateSpeed += (24 - xAxisRotateSpeed) / 2;
                else
                    xAxisRotateSpeed /= 2;

                if (KeyHandler.pressedKeys[98])
                    yAxisRotateSpeed += (12 - yAxisRotateSpeed) / 2;
                else if (KeyHandler.pressedKeys[99])
                    yAxisRotateSpeed += (-12 - yAxisRotateSpeed) / 2;
                else
                    yAxisRotateSpeed /= 2;

                minimapOrientation = minimapOrientation + xAxisRotateSpeed / 2 & 2047;
                cameraPitchTarget += yAxisRotateSpeed / 2;

                if (cameraPitchTarget < 128)
                    cameraPitchTarget = 128;
                if (cameraPitchTarget > 383)
                    cameraPitchTarget = 383;
                int l = Camera.oculusOrbFocalPointX >> 7;
                int i1 = Camera.oculusOrbFocalPointY >> 7;
                int j1 = getTileHeight(plane, Camera.oculusOrbFocalPointY, Camera.oculusOrbFocalPointX);
                int k1 = 0;
                if (l > 3 && i1 > 3 && l < 100 && i1 < 100) {
                    for (int l1 = l - 4; l1 <= l + 4; l1++) {
                        for (int k2 = i1 - 4; k2 <= i1 + 4; k2++) {
                            int l2 = plane;
                            if (l2 < 3 && (tileFlags[1][l1][k2] & 2) == 2)
                                l2++;
                            int i3 = j1 - Tiles_heights[l2][l1][k2];
                            if (i3 > k1)
                                k1 = i3;
                        }
                    }
                }
                ClientCompanion.anInt1005++;
                if (ClientCompanion.anInt1005 > 1512) {
                    ClientCompanion.anInt1005 = 0;
                }
                int j2 = k1 * 192;
                if (j2 > 0x17f00)
                    j2 = 0x17f00;
                if (j2 < 32768)
                    j2 = 32768;
                if (j2 > anInt984) {
                    anInt984 += (j2 - anInt984) / 24;
                    return;
                }
                if (j2 < anInt984) {
                    anInt984 += (j2 - anInt984) / 80;
                }
            } catch (Exception _ex) {
                Log.error("glfc_ex " + localPlayer.x + "," + localPlayer.y + "," + Camera.oculusOrbFocalPointX + "," + Camera.oculusOrbFocalPointY
                        + "," + currentRegionX + "," + currentRegionY + "," + baseX + "," + baseY, _ex);
                throw new RuntimeException("eek");
            }
        }

        private void processDrawing () {

            if (captchaRequired.get()) {
                ClientUI.frame.repaint();
                return;
            }

            if (ClientCompanion.rsAlreadyLoaded || ClientCompanion.loadingError || ClientCompanion.genericLoadingError) {
                ClientUtil.showErrorScreen(this);
                return;
            }
            if (!loggedIn) {
                if (gameState == 0 || gameState == 5) {
                    OsCache.renderLoadingBar();
                } else {
                    LoginScreen.drawLoginScreen(this, false);
//                TitleScreen.draw();
                }
            } else {

                final boolean fixedMode = ClientUI.frameMode == ScreenMode.FIXED;

                drawGameScreen(fixedMode);

                if (getLoadingStage() == GAME_ASSETS_LOADED && ClientCompanion.openInterfaceId == -1 && !ClientCompanion.firstRender) {
                    ClientCompanion.firstRender = true;
                    if (!fixedMode)
                        ClientUI.frameMode(Configuration.loginMode, true);
                }
            }
        }

        public boolean isFriendOrSelf (String s){
            if (s == null)
                return false;
            for (int i = 0; i < PlayerRelations.friendsCount; i++)
                if (s.equalsIgnoreCase(PlayerRelations.friendsList[i]))
                    return true;
            return s.equalsIgnoreCase(localPlayer.name);
        }

        public void changeSoundVolume ( int i){
            SignLink.wavevol = i;
        }

        public int getSoundVolume () {
            return SignLink.wavevol;
        }

        private void draw3dScreen () {
            if (displayChatComponents()) {
                drawSplitPrivateChat();
            }
            if (crossType == 1) {
                int offSet = ClientUI.frameMode == ScreenMode.FIXED ? 4 : 0;
                SpriteLoader.getSprite(SpriteCompanion.crosses, crossIndex / 100).drawSprite(crossX - 8 - offSet, crossY - 8 - offSet);
                ClientCompanion.anInt1142++;
                if (ClientCompanion.anInt1142 > 67) {
                    ClientCompanion.anInt1142 = 0;
                    // sendPacket(new ClearMinimapFlag()); //Not server-sided, flag
                    // is only handled in the client
                }
            }
            if (crossType == 2) {
                int offSet = ClientUI.frameMode == ScreenMode.FIXED ? 4 : 0;
                SpriteLoader.getSprite(SpriteCompanion.crosses, 4 + crossIndex / 100).drawSprite(crossX - 8 - offSet, crossY - 8 - offSet);
            }

            BroadCastManager.drawBroadcast(this);
            EffectTimerManager.drawEffectTimers(this);
            Achievements.drawAchievements(this);
            MonsterHuntTrackerManager.updateTracker();
            MonsterHuntTrackerManager.drawTracker(this);

            if (Configuration.xpCounterOpen) XpDrops.draw();
            if (CombatBox.shouldDrawCombatBox(currentInteract)) CombatBox.drawCombatBox(this, currentInteract);
            if (Configuration.enableSkillOrbs) SkillOrbs.process();
            if (Configuration.snowInterface) Snow.processSnowInterface(this);

            if (openWalkableInterface != -1) {
                try {
                    processWidgetAnimations(tickDelta, openWalkableInterface);
                    Widget rsinterface = Widget.interfaceCache[openWalkableInterface];
                    if (ClientUI.frameMode == ScreenMode.FIXED) {
                        drawInterface(0, 0, rsinterface, 0, 4, 4);
                    } else {
                        if (openWalkableInterface == 18679) { // Black screen when no light source
                            Rasterizer2D.fillRectangle(0, 0, ClientUI.screenAreaWidth, ClientUI.screenAreaHeight, 0x000000);
                        } else {
                            drawInterfaceInScreen(openWalkableInterface);

                        }
                    }
                } catch (Exception e) {
                    Log.error("Failed to process widget animations, openWalkableInterface = " + openWalkableInterface, e);
                }
            }

            if (ClientCompanion.openInterfaceId != -1) {
                if (Widget.interfaceCache[ClientCompanion.openInterfaceId] == null) {
                    ClientCompanion.openInterfaceId = -1;
                } else
                    try {
                        processWidgetAnimations(tickDelta, ClientCompanion.openInterfaceId);

                        if (ClientCompanion.openInterfaceId == Bank.INTERFACE_ID)
                            Bank.updateInterface();

                        drawInterfaceInScreen(ClientCompanion.openInterfaceId);
                    } catch (Exception e) {
                        Log.error("Failed to process widget animations, openInterfaceId = " + ClientCompanion.openInterfaceId, e);
                    }
            }
            if (ClientCompanion.openInterfaceId2 != -1) {
                try {
                    processWidgetAnimations(tickDelta, ClientCompanion.openInterfaceId2);

                    int w = 512, h = 334;
                    int x = ClientUI.frameMode == ScreenMode.FIXED ? 0 : (ClientUI.frameWidth / 2) - 256;
                    int y = ClientUI.frameMode == ScreenMode.FIXED ? 0 : (ClientUI.frameHeight / 2) - 167;
                    int count = displaySideStonesStacked() ? 3 : 4;
                    if (ClientUI.frameMode != ScreenMode.FIXED) {
                        for (int i = 0; i < count; i++) {
                            if (x + w > (ClientUI.frameWidth - 225)) {
                                x = x - 30;
                                if (x < 0) {
                                    x = 0;
                                }
                            }
                            if (y + h > (ClientUI.frameHeight - 182)) {
                                y = y - 30;
                                if (y < 0) {
                                    y = 0;
                                }
                            }
                        }
                    }

                    Rasterizer2D.drawTransparentBox(-100, 0, Rasterizer2D.Rasterizer2D_width + 100, Rasterizer2D.Rasterizer2D_height, 0x3e2f1d, 100);
                    drawInterface(0, x, Widget.interfaceCache[ClientCompanion.openInterfaceId2], y, ClientUI.frameMode == ScreenMode.FIXED ? 4 : 0, ClientUI.frameMode == ScreenMode.FIXED ? 4 : 0);
                } catch (Exception e) {
                    Log.error("Failed to process widget animations, openInterfaceId2 = " + ClientCompanion.openInterfaceId2, e);
                }
            }
            if (!menuOpen) {
                GameFrameInput.processRightClick(this);
                drawTooltip();
            } else if (GameFrame.menuScreenArea == 0) {
                drawMenu(ClientUI.frameMode == ScreenMode.FIXED ? 4 : 0, ClientUI.frameMode == ScreenMode.FIXED ? 4 : 0);
            }

            // Multi sign
            if (multicombat == 1) {
                SpriteCompanion.multiOverlay.drawSprite(ClientUI.frameMode == ScreenMode.FIXED ? 10 : 10,
                        ClientUI.frameHeight - 200);
            }

            final String screenMode = ClientUI.frameMode == ScreenMode.FIXED ? "Fixed" : "Resizable";
//        displayScreenInfo();
            if (Configuration.displayTileDebug) {
                DebugTile.debugTile(this);
            }
            if (Configuration.displayFps) {
                displayFps();
            }
            if (Configuration.debugPerformance) {
                Debug.draw(this, regularText);
            }

            if (Configuration.clientData) {
                int textColour = 0xffff00;
                displayFps();
                regularText.render(textColour, "Client Zoom: " + cameraZoom, 90, 5);
                regularText.render(textColour, "Brightness: " + ClientCompanion.brightnessState, 105, 5);

                regularText.render(textColour, "Mouse X: " + MouseHandler.x + " , Mouse Y: " + MouseHandler.y, 30, 5);
//			regularText.render(textColour, "Coords: " + x + ", " + y, 45, 5);
                regularText.render(textColour, "Cam: " + cameraX + ", " + cameraZ + ", " + cameraY + ", " + cameraYaw + ", " + cameraPitch, 45, 5);
                regularText.render(textColour, "Client Mode: " + screenMode + "", 60, 5);
                regularText.render(textColour, "Client Resolution: " + ClientUI.frameWidth + "x" + ClientUI.frameHeight, 75, 5);
                int localX = this.currentRegionX / 8;
                int localY = this.currentRegionY / 8;
                int id = (localX << 8) + localY;

                int regionIndex = getRegionIndex(id);
                if (regionIndex != -1) {
                    regularText.render(textColour, "Object Maps: " + resourceProvider.getMapFiles()[regionIndex], 130, 5);
                    regularText.render(textColour, "Floor Maps: " + resourceProvider.getLandscapes()[regionIndex], 145, 5);
                }
                regularText.render(textColour, "Region ID: " + id, 160, 5);

            }
            if (systemUpdateTime != 0) {
                int seconds = systemUpdateTime / 50;
                int minutes = seconds / 60;
                int yOffset = ClientUI.frameMode == ScreenMode.FIXED ? 0 : ClientUI.frameHeight - 498;
                seconds %= 60;
                if (seconds < 10)
                    regularText.render(0xffff00, "System update in: " + minutes + ":0" + seconds, 329 + yOffset - resizeChatOffset(), 4);
                else
                    regularText.render(0xffff00, "System update in: " + minutes + ":" + seconds, 329 + yOffset - resizeChatOffset(), 4);
                ClientCompanion.anInt849++;
                if (ClientCompanion.anInt849 > 75) {
                    ClientCompanion.anInt849 = 0;
                }
            }
        }

        public void drawInterfaceInScreen ( int openWalkableInterface){
            int w = 512, h = 334;
            int x = ClientUI.frameMode == ScreenMode.FIXED ? 0 : (ClientUI.frameWidth / 2) - 256;
            int y = ClientUI.frameMode == ScreenMode.FIXED ? 0 : (ClientUI.frameHeight / 2) - 167;
            int count = displaySideStonesStacked() ? 3 : 4;
            if (ClientUI.frameMode != ScreenMode.FIXED) {
                if (Widget.interfaceCache[openWalkableInterface].width > w || Widget.interfaceCache[openWalkableInterface].height > h) {
                    x = ClientUtil.getLargeResizableInterfaceOffsetLeftX();
                    y = ClientUtil.getLargeResizableInterfaceOffsetTopY();
                }
                for (int i = 0; i < count; i++) {
                    if (x + w > (ClientUI.frameWidth - 225)) {
                        x = x - 30;
                        if (x < 0) {
                            x = 0;
                        }
                    }
                    if (y + h > (ClientUI.frameHeight - 182)) {
                        y = y - 30;
                        if (y < 0) {
                            y = 0;
                        }
                    }
                }
            }
            drawInterface(0, x, Widget.interfaceCache[openWalkableInterface], y, ClientUI.frameMode == ScreenMode.FIXED ? 4 : 0, ClientUI.frameMode == ScreenMode.FIXED ? 4 : 0);
        }

        private int getRegionIndex ( int regionId){
            for (int index = 0; index < resourceProvider.getAreas().length; index++) {
                if (resourceProvider.getAreas()[index] == regionId) {
                    return index;
                }
            }
            return -1;
        }

        public void addIgnore ( long name){
            if (name == 0L)
                return;
            sendPacket(new AddIgnore(name).create());
        }

        private void processPlayerMovement () {
            for (int index = -1; index < Players_count; index++) {

                int playerIndex;

                if (index == -1) {
                    playerIndex = LOCAL_PLAYER_ID;
                } else {
                    playerIndex = Players_indices[index];
                }

                Player player = players[playerIndex];

                if (player != null)
                    processMovement(player);
            }
        }

        private void processSpawnedObjects () {
            if (OSObjectDefinition.USE_OSRS || getLoadingStage() == GAME_ASSETS_LOADED) {
                for (SpawnedObject spawnedObject = (SpawnedObject) spawns
                        .last(); spawnedObject != null; spawnedObject = (SpawnedObject) spawns
                        .previous()) {
                    if (spawnedObject.getLongetivity > 0)
                        spawnedObject.getLongetivity--;
                    if (spawnedObject.getLongetivity == 0) {
                        if (spawnedObject.getPreviousId < 0
                                || MapRegion.modelReady(spawnedObject.getPreviousId, spawnedObject.previousType)) {
                            removeObject(spawnedObject.x, spawnedObject.y, spawnedObject.plane, spawnedObject.previousType, spawnedObject.previousOrientation,
                                    spawnedObject.group,
                                    spawnedObject.getPreviousId);
                            spawnedObject.remove();
                        }
                    } else {
                        if (spawnedObject.delay > 0)
                            spawnedObject.delay--;
                        if (spawnedObject.delay == 0 && spawnedObject.x >= 1 && spawnedObject.y >= 1
                                && spawnedObject.x <= 102 && spawnedObject.y <= 102
                                && (spawnedObject.id < 0 || MapRegion.modelReady(spawnedObject.id, spawnedObject.type))) {
                            removeObject(spawnedObject.x, spawnedObject.y, spawnedObject.plane, spawnedObject.type, spawnedObject.orientation,
                                    spawnedObject.group, spawnedObject.id);
                            spawnedObject.delay = -1;
                            if (spawnedObject.id == spawnedObject.getPreviousId && spawnedObject.getPreviousId == -1)
                                spawnedObject.remove();
                            else if (spawnedObject.id == spawnedObject.getPreviousId
                                    && spawnedObject.orientation == spawnedObject.previousOrientation
                                    && spawnedObject.type == spawnedObject.previousType)
                                spawnedObject.remove();
                        }
                    }
                }

            }
        }

        private void updateLocalPlayerMovement (Buffer stream){
            stream.initBitAccess();

            final boolean skip = stream.readBits(1) == 0;

            if (skip)
                return;

            int type = stream.readBits(2);
            if (type == 0) {
                mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = LOCAL_PLAYER_ID;
                return;
            }
            if (type == 1) {

                int direction = stream.readBits(3);

                localPlayer.walkInDir(direction, false);

                int updateRequired = stream.readBits(1);

                if (updateRequired == 1) {
                    mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = LOCAL_PLAYER_ID;
                }
                return;
            }
            if (type == 2) {

                int walkMask = stream.readBits(3);
                localPlayer.walkInDir(walkMask, true);
                int runMask = stream.readBits(3);
                localPlayer.walkInDir(runMask, true);

                int updateRequired = stream.readBits(1);

                if (updateRequired == 1) {
                    mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = LOCAL_PLAYER_ID;
                }
                return;
            }
            if (type == 3) {
                plane = stream.readBits(2);

                // Fix for height changes
                if (lastKnownPlane != plane) {
                    if (OSObjectDefinition.USE_OSRS)
                        updateGameState(25);
                    else
                        setLoadingStage(1);
                }
                lastKnownPlane = plane;

                int teleport = stream.readBits(1);
                int updateRequired = stream.readBits(1);

                if (updateRequired == 1) {
                    mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = LOCAL_PLAYER_ID;
                }

                int y = stream.readBits(7);
                int x = stream.readBits(7);

                localPlayer.setPos(x, y, teleport == 1);
            }
        }

        public boolean processWidgetAnimations ( int tick, int interfaceId){
            boolean redrawRequired = false;
            Widget widget = Widget.interfaceCache[interfaceId];

            if (widget == null || widget.children == null) {
                return false;
            }

            for (int element : widget.children) {
                if (element == -1) {
                    break;
                }

                Widget child = Widget.interfaceCache[element];

                if (child == null) {
                    continue;
                }

                if (child.type == Widget.TYPE_MODEL_LIST) {
                    redrawRequired |= processWidgetAnimations(tick, child.id);
                }

                if (child.type == 6 && (child.defaultAnimationId != -1 || child.secondaryAnimationId != -1)) {
                    boolean updated = interfaceIsSelected(child);

                    int animationId = updated ? child.secondaryAnimationId : child.defaultAnimationId;

                    if (animationId != -1) {
                        Animation animation = Animation.getSequenceDefinition(animationId);
                        for (child.lastFrameTime += tick; child.lastFrameTime > animation.frameLengths[child.currentFrame]; ) {
                            child.lastFrameTime -= animation.frameLengths[child.currentFrame];
                            child.currentFrame++;
                            if (child.currentFrame >= animation.frameIds.length) {
                                child.currentFrame -= animation.frameCount;
                                if (child.currentFrame < 0 || child.currentFrame >= animation.frameIds.length)
                                    child.currentFrame = 0;
                            }
                            redrawRequired = true;
                        }

                    }
                }
            }

            return redrawRequired;
        }

        private int calculateUnlockedMaxRenderPlane () {
            if (!Configuration.enableRoofs && !Snow.disabledSnow)
                return plane;
            int plane = 3;
            if (cameraPitch < 310) {
                int camX = Math.min(103, Math.max(0, cameraX >> 7));
                int camY = Math.min(103, Math.max(0, cameraZ >> 7));
                int tileX = localPlayer.x >> 7;
                int tileY = localPlayer.y >> 7;
                if ((tileFlags[Client.plane][camX][camY] & 4) != 0)
                    plane = Client.plane;

                final int offsetX = Math.max(1, Math.abs(tileX - camX));
                final int offsetY = Math.max(1, Math.abs(tileY - camY));

                if (offsetX > offsetY) {
                    int i2 = (offsetY * 0x10000) / offsetX;
                    int k2 = 32768;
                    while (camX != tileX) {
                        if (camX < tileX)
                            camX++;
                        else camX--;
                        if ((tileFlags[Client.plane][camX][camY] & 4) != 0)
                            plane = Client.plane;
                        k2 += i2;
                        if (k2 >= 0x10000) {
                            k2 -= 0x10000;
                            if (camY < tileY)
                                camY++;
                            else if (camY > tileY)
                                camY--;
                            if ((tileFlags[Client.plane][camX][camY] & 4) != 0)
                                plane = Client.plane;
                        }
                    }
                } else {
                    int j2 = (offsetX * 0x10000) / offsetY;
                    int l2 = 32768;
                    while (camY != tileY) {
                        if (camY < tileY)
                            camY++;
                        else camY--;
                        if ((tileFlags[Client.plane][camX][camY] & 4) != 0)
                            plane = Client.plane;
                        l2 += j2;
                        if (l2 >= 0x10000) {
                            l2 -= 0x10000;
                            if (camX < tileX)
                                camX++;
                            else if (camX > tileX)
                                camX--;
                            if ((tileFlags[Client.plane][camX][camY] & 4) != 0)
                                plane = Client.plane;
                        }
                    }
                }
            }
            if ((tileFlags[Client.plane][localPlayer.x >> 7][localPlayer.y >> 7] & 4) != 0)
                plane = Client.plane;

            Snow.underRoof = (tileFlags[Client.plane][localPlayer.x >> 7][localPlayer.y >> 7] & 4) != 0;
            return plane;
        }

        private int calculateLockedMaxRenderPlane () {
            int orientation = getTileHeight(plane, cameraZ, cameraX);
            if (orientation - cameraY < 800 && (tileFlags[plane][cameraX >> 7][cameraZ >> 7] & 4) != 0)
                return plane;
            else
                return 3;
        }

        public void removeFriend ( long name){
            if (name == 0L)
                return;
            sendPacket(new DeleteFriend(name).create());
        }

        public void removeIgnore ( long name){
            if (name == 0L)
                return;
            sendPacket(new DeleteIgnore(name).create());
        }

        public int executeScript (Widget widget,int id){

            if (widget.valueIndexArray == null || id >= widget.valueIndexArray.length)
                return -2;
            try {
                int[] script = widget.valueIndexArray[id];
                int accumulator = 0;
                int counter = 0;
                int operator = 0;
                do {
                    int instruction = script[counter++];

                    int value = 0;
                    byte next = 0;

                    if (instruction == 0) return accumulator;
                    if (instruction == ClientCompanion.INSTRUCTION_CURRENT_LEVEL)
                        value = currentLevels[script[counter++]];
                    if (instruction == ClientCompanion.INSTRUCTION_MAX_LEVEL) value = maximumLevels[script[counter++]];
                    if (instruction == ClientCompanion.INSTRUCTION_EXPERIENCE) value = currentExp[script[counter++]];

                    if (instruction == ClientCompanion.INSTRUCTION_ITEM_AMOUNT) {

                        Widget other = Widget.interfaceCache[script[counter++]];
                        int item = script[counter++];

                        boolean isRunePouch = other.id == Spellbooks.RUNE_POUCH_CONTAINER_INTERFACE_ID;
                        boolean hasRunePouch = false;

                        if (isRunePouch) {
                            Widget inventory = Widget.interfaceCache[Spellbooks.INVENTORY_CONTAINTER_INTERFACE_ID];
                            for (int slot = 0; slot < inventory.inventoryItemId.length; slot++) {
                                if (inventory.inventoryItemId[slot] == Spellbooks.RUNE_POUCH_ITEM_ID + 1) {
                                    hasRunePouch = true;
                                    break;
                                }
                            }
                        }

                        if (item >= 0 && item < ItemDefinition.getTotalItems()
                                && (!ItemDefinition.lookup(item).is_members_only || ClientCompanion.isMembers)) {
                            for (int slot = 0; slot < other.inventoryItemId.length; slot++) {
                                if (other.inventoryItemId[slot] == item + 1) {
                                    if (!isRunePouch || hasRunePouch)
                                        value += other.inventoryAmounts[slot];
                                }
                            }
                        }
                    }
                    if (instruction == ClientCompanion.INSTRUCTION_CLIENT_SETTING) value = settings[script[counter++]];
                    if (instruction == 6)
                        value = ClientCompanion.SKILL_EXPERIENCE[maximumLevels[script[counter++]] - 1];
                    if (instruction == 7) value = (settings[script[counter++]] * 100) / 46875;
                    if (instruction == ClientCompanion.INSTRUCTION_COMBAT_LEVEL) value = localPlayer.combatLevel;

                    if (instruction == ClientCompanion.INSTRUCTION_TOTAL_LEVEL) {
                        for (int skill = 0; skill < SkillConstants.SKILL_COUNT; skill++)
                            if (SkillConstants.ENABLED_SKILLS[skill])
                                value += maximumLevels[skill];
                    }

                    if (instruction == 10) {
                        Widget other = Widget.interfaceCache[script[counter++]];
                        int item = script[counter++] + 1;
                        if (item >= 0 && item < ItemDefinition.getTotalItems() && ClientCompanion.isMembers) {
                            for (int stored = 0; stored < other.inventoryItemId.length; stored++) {
                                if (other.inventoryItemId[stored] != item)
                                    continue;
                                value = 0x3b9ac9ff;
                                break;
                            }

                        }
                    }

                    if (instruction == 11) value = runEnergy;
                    if (instruction == 12) value = weight;
                    if (instruction == 13) {
                        int bool = settings[script[counter++]];
                        int shift = script[counter++];
                        value = (bool & 1 << shift) == 0 ? 0 : 1;
                    }

                    if (instruction == 14) {
                        int index = script[counter++];
                        VariableBits bits = VariableBits.varbits[index];
                        int setting = bits.getSetting();
                        int low = bits.getLow();
                        int high = bits.getHigh();
                        int mask = ClientCompanion.BIT_MASKS[high - low];
                        value = settings[setting] >> low & mask;
                    }

                    if (instruction == 15) next = 1;
                    if (instruction == 16) next = 2;
                    if (instruction == 17) next = 3;
                    if (instruction == 18) value = (localPlayer.x >> 7) + baseX;
                    if (instruction == 19) value = (localPlayer.y >> 7) + baseY;
                    if (instruction == 20) value = script[counter++];

                    switch (instruction) {
                        case SettingsWidget.MUSIC_SPRITE_INSTRUCTION:
                            value = Audio.musicNotMuted() ? 0 : 4;
                            break;
                        case SettingsWidget.AUDIO_SPRITE_INSTRUCTION:
                            value = Audio.soundEffectVolume > 0 ? 0 : 4;
                            break;
                        case SettingsWidget.AMBIENT_SOUND_SPRITE_INSTRUCTION:
                            value = Audio.areaSoundEffectVolume > 0 ? 0 : 4;
                            break;
                    }

                    if (next == 0) {
                        if (operator == 0) accumulator += value;
                        if (operator == 1) accumulator -= value;
                        if (operator == 2 && value != 0) accumulator /= value;
                        if (operator == 3) accumulator *= value;
                        operator = 0;
                    } else {
                        operator = next;
                    }
                } while (true);
            } catch (Exception _ex) {
                Log.error("Failed to execute script, id = " + id + ", widget.id = " + widget.id, _ex);
                return -1;
            }
        }

        private void drawTooltip () {
            if (menuOptionsCount < 2 && itemSelected == 0 && spellSelected == 0)
                return;
            String s;
            if (itemSelected == 1 && menuOptionsCount < 2)
                s = "Use " + selectedItemName + " with...";
            else if (spellSelected == 1 && menuOptionsCount < 2)
                s = spellTooltip + "...";
            else
                s = menuActions[menuOptionsCount - 1];
            if (menuOptionsCount > 2)
                s = s + "@whi@ / " + (menuOptionsCount - 2) + " more options";
            newBoldFont.drawBasicString(s, 4, 15, 0xFFFFFF, 1);
        }


        public void setDrawOffset (Mob entity,int i){
            calcEntityScreenPos(entity.x, i, entity.y);
        }

        public void calcEntityScreenPos ( int x, int j, int y){

            if (x < 128 || y < 128 || x > 13056 || y > 13056) {
                spriteDrawX = -1;
                spriteDrawY = -1;
                return;
            }
            int i1 = getTileHeight(plane, y, x) - j;
            x -= cameraX;
            i1 -= cameraY;
            y -= cameraZ;
            int j1 = Model.SINE[cameraPitch];
            int k1 = Model.COSINE[cameraPitch];
            int l1 = Model.SINE[cameraYaw];
            int i2 = Model.COSINE[cameraYaw];
            int j2 = y * l1 + x * i2 >> 16;
            y = y * i2 - x * l1 >> 16;
            x = j2;
            j2 = i1 * k1 - y * j1 >> 16;
            y = i1 * j1 + y * k1 >> 16;
            i1 = j2;
            if (y >= 50) {
                spriteDrawX = Rasterizer3D.originViewX + (x << SceneGraph.viewDistance) / y;
                spriteDrawY = Rasterizer3D.originViewY + (i1 << SceneGraph.viewDistance) / y;
            } else {
                spriteDrawX = -1;
                spriteDrawY = -1;
            }
        }

        public void requestSpawnObject ( int longetivity, int id, int orientation, int group, int y, int type,
        int plane,
        int x, int delay){
            SpawnedObject object = null;
            for (SpawnedObject node = (SpawnedObject) spawns.last(); node != null; node = (SpawnedObject) spawns
                    .previous()) {
                if (node.plane != plane || node.x != x || node.y != y || node.group != group)
                    continue;
                object = node;
                break;
            }

            if (object == null) {
                object = new SpawnedObject();
                object.plane = plane;
                object.group = group;
                object.x = x;
                object.y = y;
                method89(object);
                spawns.addFirst(object);
            }
            object.id = id;
            object.type = type;
            object.orientation = orientation;
            object.delay = delay;
            object.getLongetivity = longetivity;
        }

        public boolean interfaceIsSelected (Widget widget){
            if (widget.valueCompareType == null)
                return false;
            for (int i = 0; i < widget.valueCompareType.length; i++) {
                int value = executeScript(widget, i);
                int requiredValue = widget.requiredValues[i];
                if (widget.valueCompareType[i] == ClientCompanion.VALUE_LESS_OR_EQUAL) {
                    if (value >= requiredValue)
                        return false;
                } else if (widget.valueCompareType[i] == ClientCompanion.VALUE_GREATER_OR_EQUAL) {
                    if (value <= requiredValue)
                        return false;
                } else if (widget.valueCompareType[i] == ClientCompanion.VALUE_EQUAL) {
                    if (value == requiredValue)
                        return false;
                } else if (value != requiredValue)
                    return false;
            }

            return true;
        }

        private void updateOtherPlayerMovement (Buffer stream){
            int count = stream.readBits(8);

            if (count < Players_count) {
                for (int index = count; index < Players_count; index++) {
                    removedMobs[removedMobCount++] = Players_indices[index];
                }
            }
            if (count > Players_count) {
                Log.error(myUsername + " Too many players");
                throw new RuntimeException("eek " + count + " > " + Players_count);
            }

//        Log.info("updateOtherPlayerMovement -> count = "+count+", "+Players_count);

            Players_count = 0;
            for (int globalIndex = 0; globalIndex < count; globalIndex++) {
                int index = Players_indices[globalIndex];
                Player player = players[index];
                player.index = index;
                int updateRequired = stream.readBits(1);

                if (updateRequired == 0) {
                    Players_indices[Players_count++] = index;
                    player.npcCycle = tick;
                } else {
                    int movementType = stream.readBits(2);
                    if (movementType == 0) {
                        Players_indices[Players_count++] = index;
                        player.npcCycle = tick;
                        mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = index;
                    } else if (movementType == 1) {
                        Players_indices[Players_count++] = index;
                        player.npcCycle = tick;

                        int direction = stream.readBits(3);

                        player.walkInDir(direction, false);

                        int update = stream.readBits(1);

                        if (update == 1) {
                            mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = index;
                        }
                    } else if (movementType == 2) {
                        Players_indices[Players_count++] = index;
                        player.npcCycle = tick;

                        int walkMask = stream.readBits(3);
                        player.walkInDir(walkMask, true);
                        int runMask = stream.readBits(3);
                        player.walkInDir(runMask, true);

                        int update = stream.readBits(1);
                        if (update == 1) {
                            mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = index;
                        }
                    } else if (movementType == 3) {
                        removedMobs[removedMobCount++] = index;
                    }
                }
            }
        }

        private void updateNpcMovement (Buffer stream){
            stream.initBitAccess();
            int k = stream.readBits(8);
            if (k < npcCount) {
                for (int l = k; l < npcCount; l++)
                    removedMobs[removedMobCount++] = npcIndices[l];

            }
            if (k > npcCount) {
                Log.error(myUsername + " Too many npcs");
                throw new RuntimeException("eek");
            }
            npcCount = 0;
            for (int i1 = 0; i1 < k; i1++) {
                int j1 = npcIndices[i1];
                Npc npc = npcs[j1];
                npc.index = j1;
                int k1 = stream.readBits(1);
                if (k1 == 0) {
                    npcIndices[npcCount++] = j1;
                    npc.npcCycle = tick;
                } else {
                    int l1 = stream.readBits(2);
                    if (l1 == 0) {
                        npcIndices[npcCount++] = j1;
                        npc.npcCycle = tick;
                        mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = j1;
                    } else if (l1 == 1) {
                        npcIndices[npcCount++] = j1;
                        npc.npcCycle = tick;
                        int i2 = stream.readBits(3);
                        npc.walkInDir(i2, false);
                        int k2 = stream.readBits(1);
                        if (k2 == 1)
                            mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = j1;
                    } else if (l1 == 2) {
                        npcIndices[npcCount++] = j1;
                        npc.npcCycle = tick;
                        int walkMask = stream.readBits(3);
                        npc.walkInDir(walkMask, true);
                        int runMask = stream.readBits(3);
                        npc.walkInDir(runMask, true);
                        int i3 = stream.readBits(1);
                        if (i3 == 1)
                            mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = j1;
                    } else if (l1 == 3)
                        removedMobs[removedMobCount++] = j1;
                }
            }

        }

        private void removeObject ( int x, int y, int z, int l, int k, int group, int previousId){
            if (x >= 1 && y >= 1 && x <= 102 && y <= 102) {

                if (ClientCompanion.lowMemory && z != plane) {
                    //System.out.println("Could not remove object["+group+"] at "+x+" "+y+" off diff plane "+z+" != "+plane);
                    return;
                }
                long key = 0L;
                if (group == 0)
                    key = scene.getWallObjectUid(z, x, y);
                if (group == 1)
                    key = scene.getWallDecorationUid(z, x, y);
                if (group == 2)
                    key = scene.getGameObjectUid(z, x, y);
                if (group == 3)
                    key = scene.getGroundDecorationUid(z, x, y);
                if (key != 0) {
                    //System.out.println("Removing object["+group+"]["+key+"] at "+x+" "+y+" off diff plane "+z+" != "+plane);
                    int id = DynamicObject.get_object_key(key);
                    int objectType = DynamicObject.get_object_type(key);
                    int orientation = DynamicObject.get_object_orientation(key);
                    if (group == 0) {
                        scene.removeWallObject(x, z, y);
                        ObjectDefinition objectDef = ObjectDefinition.lookup(id);
                        if (objectDef.solid)
                            collisionMaps[z].removeObject(orientation, objectType, objectDef.impenetrable, x, y);
                    }
                    if (group == 1)
                        scene.removeWallDecoration(y, z, x);
                    if (group == 2) {
                        scene.removeObjectsOnTile(z, x, y);
                        ObjectDefinition objectDef = ObjectDefinition.lookup(id);
                        if (x + objectDef.sizeX > 103 || y + objectDef.sizeX > 103
                                || x + objectDef.sizeY > 103 || y + objectDef.sizeY > 103)
                            return;
                        if (objectDef.solid)
                            collisionMaps[z].removeObject(orientation, objectDef.sizeX, x, y, objectDef.sizeY,
                                    objectDef.impenetrable);
                    }
                    if (group == 3) {
                        scene.removeGroundDecoration(z, y, x);
                        ObjectDefinition objectDef = ObjectDefinition.lookup(id);
                        if (objectDef.solid && objectDef.isInteractive)
                            collisionMaps[z].removeFloorDecoration(y, x);
                    }
                } else {
                    //System.out.println("Could not remove object[" + group + "] at " + x + " " + y + " off diff plane " + z + " != " + plane);
                }
                if (previousId >= 0) {
                    int plane = z;
                    if (plane < 3 && (tileFlags[1][x][y] & 2) == 2)
                        plane++;
                    MapRegion.placeObject(x, y, plane, z, k, l, previousId, Tiles_heights, scene, collisionMaps[z]);
                    ObjectDefinition objectDef = ObjectDefinition.lookup(previousId);
                    if (objectDef != null && objectDef.nonFlatShading) {
                        scene.shadeModels(-50, -10, -50);
                    }
                }
            }
        }

        public void updatePlayers ( int packetSize, Buffer stream){

            removedMobCount = 0;
            mobsAwaitingUpdateCount = 0;
            updateLocalPlayerMovement(stream);
            updateOtherPlayerMovement(stream);
            updatePlayerList(stream, packetSize);

            parsePlayerSynchronizationMask(stream);

            for (int count = 0; count < removedMobCount; count++) {
                int index = removedMobs[count];

                if (players[index].npcCycle != tick) {
                    players[index] = null;
                }
            }

            if (stream.index != packetSize) {
                Log.error("Error packet size mismatch in getplayer pos:" + stream.index + " psize:" + packetSize);
                throw new RuntimeException("eek " + stream.index + " != " + packetSize);
            }
            for (int count = 0; count < Players_count; count++) {
                if (players[Players_indices[count]] == null) {
                    Log.error(myUsername + " null entry in pl list - pos:" + count + " size:" + Players_count);
                    throw new RuntimeException("eek");
                }
            }

        }

        /**
         * Sends a string
         */
        public void sendString (String text,int index){
            if (Widget.interfaceCache[index] == null)
                return;
            Widget.interfaceCache[index].setDefaultText(text);
        }

        /**
         * Sets button configurations on interfaces.
         */
        public void sendConfiguration ( int id, int state){
            defaultSettings[id] = state;
            if (settings[id] != state) {
                settings[id] = state;
                updateVarp(id);
                if (dialogueId != -1)
                    ChatBox.setUpdateChatbox(true);
            }
        }

        /**
         * Displays an interface over the sidebar area.
         */
        public void inventoryOverlay ( int interfaceId, int sideInterfaceId){
            if (backDialogueId != -1) {
                backDialogueId = -1;
                ChatBox.setUpdateChatbox(true);
            }
            if (inputDialogState != 0) {
                inputDialogState = 0;
                ChatBox.setUpdateChatbox(true);
            }
            ClientCompanion.openInterfaceId = interfaceId;
            overlayInterfaceId = sideInterfaceId;
            ClientCompanion.tabAreaAltered = true;
            continuedDialogue = false;
        }

        public void sendPacket (PacketBuilder builder){
            // Make sure we're logged in and that we can encrypt our packet.
            if (!loggedIn || cipher == null) {
                ping_packet_counter = 0;
                soundCycleCounter = 0;
                return;
            }

            final int opcode = builder.getOpcode();
            final int length = builder.getPosition();
            final PacketType type = PacketMetaData.OUTGOING_TYPES[opcode];

            final int bytesInHeader = type ==
                    PacketType.VARIABLE_BYTE ? 2
                    : type == PacketType.VARIABLE_SHORT ? 3
                    : 1;
            final byte[] buffer = new byte[builder.getPosition() + bytesInHeader];

            buffer[0] = (byte) (opcode + cipher.getNextKey());
            if (type == PacketType.VARIABLE_BYTE)
                buffer[1] = (byte) length;
            else if (type == PacketType.VARIABLE_SHORT) {
                buffer[1] = (byte) (length >> 8);
                buffer[2] = (byte) length;
            }
            // Copy rest of the packet data
            for (int i = bytesInHeader; i < buffer.length; i++) {
                buffer[i] = builder.getBuffer()[i - bytesInHeader];
            }

            outBuffer.writeBytes(buffer);
            ping_packet_counter = 0;
        }

        /**
         * Formerly known as {@code moveCameraWithPlayer}
         */
        private void drawGameWorld ( boolean fixedMode){

            viewportDrawCount++;

            int width = fixedMode ? ClientUI.screenAreaWidth : ClientUI.frameWidth;
            int height = fixedMode ? ClientUI.screenAreaHeight : ClientUI.frameHeight;

            showPrioritizedPlayers();
            showPrioritizedNPCs();

            showOtherPlayers();
            showOtherNpcs();

            createProjectiles();
            createStationaryGraphics();

            int offsetX = Client.viewportOffsetX;
            int offsetY = Client.viewportOffsetY;

            if (!isCameraLocked)
                Camera.handleAutomaticCameraMovement(this, fixedMode);

            int maxRenderPlane;
            if (!isCameraLocked)
                maxRenderPlane = calculateUnlockedMaxRenderPlane();
            else
                maxRenderPlane = calculateLockedMaxRenderPlane();
            int originalCameraX = cameraX;
            int originalCameraY = cameraY;
            int originalCameraZ = cameraZ;
            int originalCameraPitch = cameraPitch;
            int originalCameraYaw = cameraYaw;

            Camera.handleCameraShaking(this);

            int textureCount = Rasterizer3D.lastTextureRetrievalCount;
            int mouseX = MouseHandler.x;
            int mouseY = MouseHandler.y;
            if (MouseHandler.lastButton != 0) {
                mouseX = MouseHandler.lastPressedX;
                mouseY = MouseHandler.lastPressedY;
            }

            if (mouseX >= offsetX && mouseX < offsetX + width && mouseY >= offsetY && mouseY < height + offsetY) {
                int var12 = mouseX - offsetX;
                int var13 = mouseY - offsetY;
                ViewportMouse.ViewportMouse_x = var12;
                ViewportMouse.ViewportMouse_y = var13;
                ViewportMouse.ViewportMouse_isInViewport = true;
                ViewportMouse.ViewportMouse_entityCount = 0;
                ViewportMouse.ViewportMouse_false0 = false;
            } else {
                ViewportMouse.ViewportMouse_isInViewport = false;
                ViewportMouse.ViewportMouse_entityCount = 0;
            }
            Model.aBoolean1684 = true;
            Model.objectCount = 0;
            Model.lastMouseX = MouseHandler.x - (fixedMode ? 4 : 0);
            Model.lastMouseY = MouseHandler.y - (fixedMode ? 4 : 0);

            Audio.playPcmPlayers();
            Rasterizer2D.clear();
            Audio.playPcmPlayers();

            int zoom = Rasterizer3D.Rasterizer3D_zoom; // todo: zooming
            Rasterizer3D.Rasterizer3D_zoom = Client.viewportZoom;
            scene.render(cameraX, cameraZ, cameraYaw, cameraY, maxRenderPlane, cameraPitch);
            Rasterizer3D.Rasterizer3D_zoom = zoom;

            Audio.playPcmPlayers();

            scene.clearGameObjectCache();

            if (localPlayer.isDeveloper())
                Audio.renderAreaSoundLabels(this);

            if (Configuration.enableGroundItemNames)
                renderGroundItemNames();

            if (Configuration.enableParticles)
                ClientCompanion.particleSystem.render();

            updateEntities();

            mobOverlayRenderer.drawHeadIcon(this);

            //Rasterizer3D.updateTextures(this, textureCount);
            ((TextureProvider) Rasterizer3D.Rasterizer3D_textureLoader).animate(tickDelta);

            if (screenFadeManager != null && screenFadeManager.draw())
                screenFadeManager = null;

            draw3dScreen();

            ClientCompanion.console.drawConsole();
            ClientCompanion.console.drawConsoleArea();

            if (!fixedMode) {
                ChatBox.drawChatArea(this);
                GameFrame.drawMinimap(this);
                GameFrame.drawTabArea(this);
            }

            ClientCompanion.gameScreenImageProducer
                    .drawGraphics(canvas.getGraphics(), fixedMode ? 4 : 0, fixedMode ? 4 : 0);

            cameraX = originalCameraX;
            cameraY = originalCameraY;
            cameraZ = originalCameraZ;
            cameraPitch = originalCameraPitch;
            cameraYaw = originalCameraYaw;

            if (Client.isLoading) {
                final int pendingActionsCount = NetCache.pendingPriorityWritesCount + NetCache.pendingPriorityResponsesCount;
                if (pendingActionsCount == 0)
                    Client.isLoading = false;
            }

            if (Client.isLoading) {
                Rasterizer2D.Rasterizer2D_fillRectangle(offsetX, offsetY, width, height, 0);
                drawLoadingMessage("Loading - please wait.");
            }
        }

        public void clearTopInterfaces () {
            // close interface
            onCloseInterface();
            if (ClientCompanion.openInterfaceId2 == -1) {
                sendPacket(new CloseInterface().create());
            }

            if (screenFadeManager != null && screenFadeManager.isFixedOpacity())
                screenFadeManager.stop();

            if (overlayInterfaceId != -1) {
                overlayInterfaceId = -1;
                continuedDialogue = false;
                ClientCompanion.tabAreaAltered = true;
            }
            if (backDialogueId != -1) {
                backDialogueId = -1;
                ChatBox.setUpdateChatbox(true);
                continuedDialogue = false;
            }
            if (ClientCompanion.openInterfaceId2 != -1) {
                ClientCompanion.openInterfaceId2 = -1;
            } else {
                ClientCompanion.openInterfaceId = -1;
            }
            ClientCompanion.interfaceInputSelected = -1;
            fullscreenInterfaceID = -1;
        }

        public void addObject ( int objectId, int x, int y, int height, int face, int type){
            int mX = currentRegionX - 6;
            int mY = currentRegionY - 6;
            int x2 = x - mX * 8;
            int y2 = y - mY * 8;
            int i15 = 40 >> 2;
            int l17 = objectGroups[i15];

            if (y2 > 0 && y2 < 103 && x2 > 0 && x2 < 103) {
                requestSpawnObject(-1, objectId, face, l17, y2, type, height, x2, 0);
            }
        }

        private void displayScreenInfo () {
            int textColour = 0xffff00;
            int x = 10;
            int yOnset = 15;
            int y = yOnset;
            Rasterizer2D.drawBox(x / 2, y / 2, 250, 100, 0);
            regularText.render(textColour, "canvas size: " + canvas.getWidth() + ", " + canvas.getHeight() + "   | " + canvasWidth + ", " + canvasHeight, y += yOnset, x);
            regularText.render(textColour, "applet size: " + getWidth() + ", " + getHeight(), y += yOnset, x);
            regularText.render(textColour, "frame offset: " + ClientUI.FRAME_WIDTH_OFFSET + ", " + ClientUI.FRAME_HEIGHT_OFFSET, y += yOnset, x);
            regularText.render(textColour, "frame size: " + ClientUI.frame.getWidth() + ", " + ClientUI.frame.getHeight() + "     | " + ClientUI.frameWidth + ", " + ClientUI.frameHeight, y += yOnset, x);
            regularText.render(textColour, "panel size: " + ClientUI.panel.getWidth() + ", " + ClientUI.panel.getHeight(), y += yOnset, x);
            regularText.render(textColour, "content size: " + contentWidth + ", " + contentHeight, y += yOnset, x);

        }

        private void displayFps () {
            int textColour = 0xffff00;
            int fpsColour = 0xffff00;
            if (fps < 15) {
                fpsColour = 0xff0000;
            }
            int xOffset = ClientUI.frameMode == ScreenMode.FIXED ? 0 : -40;
            regularText.render(fpsColour, "Fps: " + fps, 12, ClientUI.frameMode == ScreenMode.FIXED ? 470 : ClientUI.frameWidth - 265 + xOffset);

            Runtime runtime = Runtime.getRuntime();
            int clientMemory = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024L);
            regularText.render(textColour, "Mem: " + clientMemory + "k", 27,
                    ClientUI.frameMode == ScreenMode.FIXED ? 428 : ClientUI.frameWidth - 265 + xOffset);
            int[][] clipData = collisionMaps[plane].clipData;

            int mask = clipData[localPlayer.getLocalX()][localPlayer.getLocalY()];
            int underlay = underlays[plane][localPlayer.getLocalX()][localPlayer.getLocalY()];
            int overlay = overlays[plane][localPlayer.getLocalX()][localPlayer.getLocalY()] & 0xff;
            regularText.render(0xffff00, "" + localPlayer.getLocalX() + ", " + localPlayer.getLocalY() + " -> ", 42,
                    ClientUI.frameMode == ScreenMode.FIXED ? 428 : ClientUI.frameWidth - 265 + xOffset);
            regularText.render(0xffff00, "U:" + underlay + ", O:" + overlay + ", C:" + mask, 57,
                    ClientUI.frameMode == ScreenMode.FIXED ? 428 : ClientUI.frameWidth - 265 + xOffset);

        }

        /**
         * If toggled, render ground item names and lootbeams
         */
        private void renderGroundItemNames () {
            for (int x = 0; x < 104; x++) {
                for (int y = 0; y < 104; y++) {
                    NodeDeque node = groundItems[plane][x][y];
                    int offset = 12;
                    if (node != null) {
                        for (Item item = (Item) node.first(); item != null; item = (Item) node.next()) {
                            ItemDefinition itemDef = ItemDefinition.lookup(item.ID);
                            calcEntityScreenPos((x << 7) + 64, 64, (y << 7) + 64);
                            // Red if default value is >= 50k || amount >= 100k
                            newSmallFont.drawCenteredString(
                                    (itemDef.value >= 0xC350 || item.itemCount >= 0x186A0 ? "<col=ff0000>" : "<trans=120>")
                                            + itemDef.name
                                            + (item.itemCount > 1 ? "</col> ("
                                            + StringUtils.insertCommasToNumber(item.itemCount + "") + "</col>)"
                                            : ""),
                                    spriteDrawX, spriteDrawY - offset, 0xffffff, 1);
                            offset += 12;
                        }
                    }
                }
            }
        }

        public Item getGroundItem () {
            for (int x2 = 0; x2 < 104; x2++) {
                for (int y2 = 0; y2 < 104; y2++) {
                    NodeDeque node = groundItems[plane][x2][y2];
                    if (node != null) {
                        return (Item) node.first();
                    }
                }
            }
            return null;
        }

        public void menuActionsRow (String action){
            if (menuOpen)
                return;
            menuActions[1] = action;
            menuOpcodes[1] = MenuOpcodes.ITEM_ACTION_5;
            menuOptionsCount = 2;
        }

        /**
         * Used for emptying Coal Bag, Same method as menuActions Row, but allowed variable for opCode
         *
         * @param action
         * @param opCodeId - The Packet ID
         */
        public void gameBagAction (String action,int opCodeId){
            menuActions[1] = action;
            menuOpcodes[1] = opCodeId;
            menuOptionsCount = 2;
        }

        public void placeholdersConfigIntercept ( byte value){
            Widget.interfaceCache[50007].active = value == 1;
        }

        private void drawGridOverlay () {

            for (int i = 0; i < 516; i += 10) {
                if (i < 334) {
                    Rasterizer2D.drawTransparentHorizontalLine(0, i, 516, 0x6699ff, 90);
                }
                Rasterizer2D.drawTransparentVerticalLine(i, 0, 334, 0x6699ff, 90);
            }

            int x = MouseHandler.x - 4 - ((MouseHandler.x - 4) % 10);
            int y = MouseHandler.y - 4 - ((MouseHandler.y - 4) % 10);

            Rasterizer2D.drawTransparentBoxOutline(x, y, 10, 10, 0xffff00, 255);
            newSmallFont.drawCenteredString("(" + (x + 4) + ", " + (y + 4) + ")", x + 4, y - 1, 0xffff00, 0);
        }

        public void handleKeyPressed (KeyEvent keyevent){
            int key = keyevent.getKeyCode();

            if (key == KeyEvent.VK_SPACE) {
                if (backDialogueId != -1 && !spacePressed) {
                    spacePressed = true;
                }
            }

            if (keyevent.isControlDown() && key == KeyEvent.VK_V) { // Paste
                if (ClientCompanion.openInterfaceId == Broadcast.INTERFACE_ID && ClientCompanion.interfaceInputSelected != -1) {
                    BroadCastManager.handleClipboardInBroadcastMenu();
                }
            }
        }

        public void setLoginMessages (String first, String second){
            LoginScreen.firstLoginMessage = first;
            LoginScreen.secondLoginMessage = second;
        }

        public Sprite[] getModIcons () {
            return modIcons;
        }

        public boolean isMenuOpen () {
            return menuOpen;
        }

        public int getInputDialogState () {
            return inputDialogState;
        }

        public void setInputDialogState ( int state){
            inputDialogState = state;
        }

        public int getItemDragType () {
            return itemDragType;
        }

        public int getDragItemDelay () {
            return dragItemDelay;
        }

        public int getAnInt1084 () {
            return anInt1084;
        }

        public int getPreviousClickMouseX () {
            return previousClickMouseX;
        }

        public int getPreviousClickMouseY () {
            return previousClickMouseY;
        }

        public void setPreviousClickMouseY ( int value){
            previousClickMouseY = value;
        }

        public boolean getABoolean1242 () {
            return mouseOutOfDragZone;
        }

        public void setMessagePromptRaised ( boolean raised){
            messagePromptRaised = raised;
        }

        public void setAmountOrNameInput (String text){
            amountOrNameInput = text;
        }

        public int getCurrentSkill () {
            return currentSkill;
        }

        public long getTotalExp () {
            return totalExp;
        }

        public String getMyUsername () {
            return myUsername;
        }

        public boolean displaySideStonesStacked () {
            return ClientUI.frameMode != ScreenMode.FIXED && ClientCompanion.stackSideStones;
        }

        public boolean displayChatComponents () {
            return ClientUI.frameMode == ScreenMode.FIXED || ChatBox.showChatComponents;
        }

        public boolean displayTabComponents () {
            return ClientUI.frameMode == ScreenMode.FIXED || ClientCompanion.showTabComponents;
        }

        public void onCloseInterface () {
            Bank.closeSearch();
            Shop.closeSearch();

            if (ClientCompanion.openInterfaceId == ChangePassword.CHANGEPASSWORD_INTERFACE_ID) {
                ChangePassword.clearInputFields();
                sendString("Password length: 0", 51012);
                sendString("Password strengh: @red@Weak", 51011);
            }

            if (ClientCompanion.openInterfaceId == ItemDropFinder.INTERFACE_ID)
                Widget.interfaceCache[ItemDropFinder.INPUT_ID].setDefaultText("");

            if (ClientCompanion.openInterfaceId != -1)
                resetWidgetDropDowns(ClientCompanion.openInterfaceId);
        }

        private void resetWidgetDropDowns ( int interfaceId){
            Widget widget = Widget.interfaceCache[interfaceId];

            if (widget == null || widget.children == null)
                return;

            for (int i = 0; i < widget.children.length; i++) {
                Widget child = Widget.interfaceCache[widget.children[i]];

                if (child == null) continue;

                if (child.type == Widget.TYPE_DROPDOWN && child.dropdown.isOpen())
                    child.dropdown.setOpen(false);

                if (child.children != null)
                    resetWidgetDropDowns(child.id);
            }
        }

        public void onInputKeyPress () {
            switch (ClientCompanion.openInterfaceId) {
                case Bank.INTERFACE_ID:
                    Bank.setSearchInput(amountOrNameInput);
                    Bank.setSearchContainers();
                    break;
                case Shop.INTERFACE_ID:
                    if (amountOrNameInput.length() <= 9) {
                        Shop.setSearchInput(amountOrNameInput);
                        Shop.setSearchContainer();
                    }
                    break;
            }
        }

        public boolean isSearchOpen () {
            return Bank.isSearching() || Shop.isSearching();
        }

        public void setButtonConfig ( int id, int value){
            defaultSettings[id] = value;
            if (settings[id] != value) {
                settings[id] = value;

                updateVarp(id);
                if (dialogueId != -1)
                    ChatBox.setUpdateChatbox(true);
            }
        }


        public int getClickMode3 () {
            return MouseHandler.lastButton;
        }

        public int getMouseX () {
            return MouseHandler.x;
        }

        public int getMouseY () {
            return MouseHandler.y;
        }

        public boolean mouseInRegion ( int x1, int x2, int y1, int y2){
            return MouseHandler.x >= x1 && MouseHandler.x <= x2 && MouseHandler.y >= y1 && MouseHandler.y <= y2;
        }

        private boolean mouseMapPosition () {
            return MouseHandler.x < ClientUI.frameWidth - 21 || MouseHandler.x > ClientUI.frameWidth || MouseHandler.y < 0 || MouseHandler.y > 21;
        }

        public int getGameCycle () {
            return tick;
        }

        public GameState getGameState () {
            return loggedIn ? GameState.LOGGED_IN : GameState.LOADING;
        }

        public int getKeyboardIdleTicks () {
            return KeyHandler.idleCycles;
        }

        public int getMouseIdleTicks () {
            return MouseHandler.idleCycles;
        }

        public long getMouseLastPressedMillis () {
            return MouseHandler.lastPressedTimeMillis;
        }

        public int getLoadingStage () {
            if (OSObjectDefinition.USE_OSRS) {
                if (gameState == 25)
                    return 1;
                if (gameState == 30)
                    return 2;
            }
            return loadingStage;
        }

        public void setLoadingStage ( int loadingStage){
            System.out.println("Client.setLoadingStage(" + loadingStage + ")");
            this.loadingStage = loadingStage;
        }

        public NetSocket getSocketStream () {
            return socketStream;
        }

        public int getLastOpcode () {
            return lastOpcode;
        }

        public int getSecondLastOpcode () {
            return secondLastOpcode;
        }

        public int getThirdLastOpcode () {
            return thirdLastOpcode;
        }

        public int getBaseX () {
            return baseX;
        }

        public int getBaseY () {
            return baseY;
        }

        public int getPlane () {
            return plane;
        }

        public boolean isInInstancedRegion () {
            return isInInstance;
        }

        public int[][][] getInstanceTemplateChunks () {
            return instanceChunkTemplates;
        }

        public CollisionData[] getCollisionMaps () {
            return collisionMaps;
        }

        public Scene getScene () {
            return scene;
        }

        public int getCameraX2 () {
            return cameraX;
        }

        public int getCameraY2 () {
            return cameraY;
        }

        public int getCameraZ () {
            return cameraZ;
        }

        public int getCameraPitch () {
            return cameraPitch;
        }

        public int getCameraYaw () {
            return cameraYaw;
        }

        public int getViewportWidth () {
            return contentWidth;
        }

        public int getViewportHeight () {
            return contentHeight;
        }

        public int getViewportXOffset () {
            return viewportOffsetX;
        }

        public int getViewportYOffset () {
            return viewportOffsetY;
        }

        public int getScale () {
            return cameraZoom;
        }

        public byte[][][] getTileSettings () {
            return tileFlags;
        }

        public int[][][] getTiles_heights () {
            return Tiles_heights;
        }

        public Npc getLocalNpc ( int npcIndex){
            return npcs[npcIndices[npcIndex]];
        }

        public boolean doWalkTo ( int type, int endY, int endX){
            return pathFinder.doWalkTo(this, type, endX, endY, false);
        }

        public enum ScreenMode {
            FIXED, RESIZABLE, FULLSCREEN
        }
    }