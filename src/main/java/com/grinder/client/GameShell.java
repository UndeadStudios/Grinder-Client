package com.grinder.client;

import com.grinder.Configuration;
import com.grinder.client.util.Log;
import com.grinder.model.ChatBox;
import com.grinder.model.Flames;
import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.audio.Audio;
import com.runescape.audio.DevicePcmPlayerProvider;
import com.runescape.cache.OsCache;
import com.runescape.clock.Clock;
import com.runescape.clock.MilliClock;
import com.runescape.clock.NanoClock;
import com.runescape.clock.Time;
import com.runescape.draw.BasicGraphicsBuffer;
import com.runescape.input.KeyHandler;
import com.runescape.input.MouseHandler;
import com.runescape.input.MouseWheel;
import com.runescape.input.MouseWheelHandler;
import com.runescape.task.TaskHandler;
import com.grinder.client.util.Bounds;

import java.applet.Applet;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.net.URL;

/**
 * A customised {@link Applet} that is used to
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 11/12/2019
 */
public abstract class GameShell extends Applet implements Runnable, FocusListener, WindowListener {

    public static TaskHandler taskHandler;
    public static boolean activeFocus;
    public static int fps;
    public static int revision;
    public static int canvasWidth;
    public static int canvasHeight;
    private static Font loadFont;
    public static FontMetrics loadFontMetrics;
    private static Image loadImageBar;
    public static Clock clock;
    private static GameShell gameShell;
    private static int threadCount;
    public static int gameProcessCycleCount;
    public static final int preferredDelay;
    public static int minimumDelay;
    private static long stopTimeMillis;
    static long garbageCollectorLastCollectionTime;
    static long garbageCollectorLastCheckTimeMs;
    private static final long[] renderTimeStamps;
    private static final long[] gameTimeStamps;
    private static int renderTimeIndex;
    private static int gameTimeIndex;
    private static int __ba_aw;

    private static volatile boolean focused;
    private static boolean killed;

    static {
        gameShell = null;
        threadCount = 0;
        stopTimeMillis = 0L;
        killed = false;
        preferredDelay = 20;
        minimumDelay = 1;
        fps = 0;
        renderTimeStamps = new long[32];
        gameTimeStamps = new long[32];
        __ba_aw = 500;
        focused = true;
        garbageCollectorLastCollectionTime = -1L;
        garbageCollectorLastCheckTimeMs = -1L;
    }

    private final EventQueue eventQueue;
    public Canvas canvas;

    /**
     * This is only used when {@link Configuration#DOUBLE_BUFFERING} is {@code true}.
     */
    public Graphics drawGraphics;

    public int contentWidth;
    public int contentHeight;
    private Frame frame;
    private MouseWheelHandler mouseWheelHandler;
    private Clipboard clipboard;
    private volatile long canvasAliveTime;
    private volatile boolean focusing = true;
    public volatile boolean canvasInvalidated;
    private boolean canvasResizeNextFrame;
    private boolean error;
    private int canvasX;
    private int canvasY;
    private int contentWidth0;
    private int contentHeight0;
    private int maxCanvasWidth;
    private int maxCanvasHeight;

    protected GameShell() {
        error = false;
        canvasX = canvasY = 0;
        canvasResizeNextFrame = false;
        canvasInvalidated = false;
        canvasAliveTime = 0L;
        EventQueue eventQueue = null;
        try {
            eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.eventQueue = eventQueue;

        Audio.pcmPlayerProvider = new DevicePcmPlayerProvider();
    }

    public static void updateGameState(int state) {
        if (state != Client.gameState) {

            if (Client.gameState == 0 && Client.instance != null)
                Client.instance.clearInitial();

            if (state == 20 || state == 40 || state == 45) {
                Client.loginState = 0;
                Client.loginFailCount = 0;
                Client.loginExceptionCount = 0;
            }

            if(state != 5 && state != 10) {
                if(state == 20) {
                   int loginIndex = Client.gameState == 11?4:0;
                    if(!Client.loggedIn){
                        if(Audio.musicNotMuted())
                            Audio.requestMusicTrack3(2, OsCache.indexCache6, "autumn voyage", "", 255, true);
                        else
                            Audio.resetMusicTrack(2);
                    }
                } else if(state == 11) {
                    int loginIndex = 4;
                    if(!Client.loggedIn){
                        if(Audio.musicNotMuted())
                            Audio.requestMusicTrack3(2, OsCache.indexCache6, "autumn voyage", "", 255, true);
                        else
                            Audio.resetMusicTrack(2);
                    }
                } else {
                    if(Client.loggedIn){
                        Audio.resetMusicTrack(2);
                    }
                }
            } else {
                if(!Client.loggedIn){
                    if(Audio.musicNotMuted())
                        Audio.requestMusicTrack3(2, OsCache.indexCache6, "autumn voyage", "", 255, true);
                    else
                        Audio.resetMusicTrack(2);
                }
            }
            Client.gameState = state;
        }
    }

    protected void clearInitial() {
        loadImageBar = null;
        loadFont = null;
        loadFontMetrics = null;
    }

    public static void resetClock() {
        clock.mark();
        int i;
        for (i = 0; i < 32; ++i) GameShell.renderTimeStamps[i] = 0L;
        for (i = 0; i < 32; ++i) GameShell.gameTimeStamps[i] = 0L;
        GameShell.gameProcessCycleCount = 0;
    }

    @Override
    public final void start() {
        if (this == gameShell && !killed) {
            stopTimeMillis = 0L;
        }
    }

    @Override
    public final synchronized void paint(Graphics graphics) {
        if (this == gameShell && !killed) {
            focusing = true;
            if (Time.currentTimeMillis() - canvasAliveTime > 1000L) {
                final Rectangle clipBounds = graphics.getClipBounds();
                if (clipBounds == null || clipBounds.width >= canvasWidth && clipBounds.height >= canvasHeight)
                    if(ClientUI.frameMode != Client.ScreenMode.FIXED) {
                        canvasInvalidated = true;
                    } else
                        ChatBox.setUpdateChatbox(true);

            }
        }
    }

    @Override
    public final void stop() {
        if (this == gameShell && !killed) {
            stopTimeMillis = Time.currentTimeMillis() + 4000L;
        }
    }

    @Override
    public final void update(Graphics graphics) {
        paint(graphics);
    }

    @Override
    public final void destroy() {
        if (this == gameShell && !killed) {
            stopTimeMillis = Time.currentTimeMillis();
            Time.sleep(5000L);
            kill();
        }
    }

    @Override
    public final void focusGained(FocusEvent var1) {
        focused = true;
        focusing = true;
    }

    @Override
    public final void focusLost(FocusEvent var1) {
        focused = false;
    }

    @Override
    public final void windowOpened(WindowEvent var1) {
    }

    @Override
    public final void windowClosing(WindowEvent var1) {
        destroy();
    }

    @Override
    public final void windowClosed(WindowEvent var1) {
    }

    @Override
    public final void windowIconified(WindowEvent var1) {
        System.out.println("Iconified");
    }

    @Override
    public final void windowDeiconified(WindowEvent var1) {
        System.out.println("Deiconified");
    }

    @Override
    public final void windowActivated(WindowEvent var1) {
    }

    @Override
    public final void windowDeactivated(WindowEvent var1) {
    }

    private synchronized void kill() {
        if (!killed) {
            killed = true;
            try {
                canvas.removeFocusListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                onKill();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (frame != null) {
                try {
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (taskHandler != null) {
                try {
                    taskHandler.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected abstract void onKill();

    @Override
    public void run() {
        try {
            if (TaskHandler.javaVendor != null) {

                final String vendor = TaskHandler.javaVendor.toLowerCase();

                if (vendor.contains("sun") || vendor.contains("apple")) {

                    final String version = TaskHandler.javaVersion;

                    if (version.equals("1.1") || version.startsWith("1.1.") || version.equals("1.2") || version.startsWith("1.2.") || version.equals("1.3") || version.startsWith("1.3.") || version.equals("1.4") || version.startsWith("1.4.") || version.equals("1.5") || version.startsWith("1.5.") || version.equals("1.6.0") || version.startsWith("1.6.") || version.equals("1.7.0") || version.startsWith("1.7")) {
                        this.error("wrongjava");
                        return;
                    }
                    minimumDelay = 5;
                }
            }

            setFocusCycleRoot(true);
            addCanvas();
            setUp();

            try {
                clock = new NanoClock();
            } catch (Throwable throwable) {
                clock = new MilliClock();
                Log.error("Failed to load nano clock, using milli clock instead", throwable);
            }

            while (0L == stopTimeMillis || Time.currentTimeMillis() < stopTimeMillis) {

                gameProcessCycleCount = clock.wait(preferredDelay, minimumDelay);

                for (int i = 0; i < gameProcessCycleCount; ++i)
                    processGame();

                processDrawing();
                post(canvas);
            }
        } catch (Exception e) {
            Log.error("CRASHED, state = "+Client.gameState, e);
            e.printStackTrace();
            error("crash");
        }
        kill();
    }

    protected void error(String var1) {
        if (!error) {
            error = true;
            System.out.println("error_game_" + var1);

            try {
                getAppletContext().showDocument(new URL(getCodeBase(), "error_game_" + var1 + ".ws"), "_self");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void addCanvas() {

        final Container container = container();

        if (canvas != null) {
            canvas.removeFocusListener(this);
            container.remove(canvas);
        }

        canvasWidth = Math.max(container.getWidth(), contentWidth0);
        canvasHeight = Math.max(container.getHeight(), contentHeight0);
        Insets insets;
        if (frame != null) {
            insets = frame.getInsets();
            canvasWidth -= insets.right + insets.left;
            canvasHeight -= insets.top + insets.bottom;
        }

        canvas = new GameCanvas(this);
        container.setBackground(Color.BLACK);
        container.setLayout(null);
        container.add(canvas);

        canvas.setSize(canvasWidth, canvasHeight);
        canvas.setVisible(true);
        canvas.setBackground(Color.BLACK);

//        System.out.println("Added canvas ["+canvasWidth+", "+canvasHeight+"] ");

        if (container == frame) {
            insets = frame.getInsets();
            canvas.setLocation(insets.left + canvasX, insets.top + canvasY);
        } else {
            canvas.setLocation(canvasX, canvasY);
        }

        canvas.addFocusListener(this);
        canvas.requestFocus();
        focusing = true;

        if (Flames.flameBuffer != null && canvasWidth == Flames.flameBuffer.width && canvasHeight == Flames.flameBuffer.height) {
            ((BasicGraphicsBuffer) Flames.flameBuffer).setComponent(this.canvas);
//            ((BasicGraphicsBuffer) Flames.flameBuffer).drawOnComponent(0, 0);
        } else {
            Flames.flameBuffer = new BasicGraphicsBuffer();
            Flames.flameBuffer.init(canvas, canvasWidth, canvasHeight, false, false);
        }

//        fullGameScreen = Client.constructGraphicsBuffer(canvasWidth, canvasHeight, true);
        canvasInvalidated = false;
        canvasAliveTime = Time.currentTimeMillis();
    }

    protected abstract void setUp();

    private void processGame() {
        long time = Time.currentTimeMillis();
        gameTimeStamps[gameTimeIndex] = time;
        gameTimeIndex = gameTimeIndex + 1 & 31;
        synchronized (this) {
            activeFocus = focused;
        }
        methodCycle();
    }

    void processDrawing() {
        final Container container = container();

        long var2 = Time.currentTimeMillis();
        long var4 = renderTimeStamps[renderTimeIndex];

        renderTimeStamps[renderTimeIndex] = var2;
        renderTimeIndex = renderTimeIndex + 1 & 31;
        if (var4 != 0L && var2 > var4) {
            int var6 = (int) (var2 - var4);
            fps = ((var6 >> 1) + 32000) / var6;
        }

        if (++__ba_aw - 1 > 50) {
            __ba_aw -= 50;
            focusing = true;
            canvas.setSize(canvasWidth, canvasHeight);
            canvas.setVisible(true);
            if (container == frame) {
                final Insets insets = frame.getInsets();
                canvas.setLocation(insets.left + canvasX, canvasY + insets.top);
            } else {
                canvas.setLocation(canvasX, canvasY);
            }
            if(ClientUI.frameMode == Client.ScreenMode.FIXED)
                ChatBox.setUpdateChatbox(true);
        }

        if (canvasInvalidated)
            replaceCanvas();

        checkCanvasSize();

        if(!Configuration.DOUBLE_BUFFERING){

            methodDraw(focusing);

            if(focusing)
                clearBackground();

        } else {

            BufferStrategy strategy = canvas.getBufferStrategy();

            if (strategy == null) {
                replaceCanvas();
                strategy = canvas.getBufferStrategy();
                if(strategy == null)
                    return;
            }

            // Render single frame
            do {
                // The following loop ensures that the contents of the drawing buffer
                // are consistent in case the underlying surface was recreated
                do {
                    Graphics draw = strategy.getDrawGraphics();
                    drawGraphics = draw;

                    methodDraw(focusing);

                    if (focusing)
                        clearBackground();

                    draw.dispose();

                    // Repeat the rendering if the drawing buffer contents
                    // were restored
                } while (strategy.contentsRestored());

                // Display the buffer
                strategy.show();

                // Repeat the rendering if the drawing buffer was lost
            } while (strategy.contentsLost());
        }

        focusing = false;
    }

    private void post(Object object) {
        if (eventQueue != null) {
            for (int i = 0; i < 50 && eventQueue.peekEvent() != null; i++) {
                Time.sleep(1L);
            }
            if (object != null)
                eventQueue.postEvent(new ActionEvent(object, 1001, "dummy"));
        }
    }

    private Container container() {
        return frame != null ? frame : this;
    }

    protected abstract void methodCycle();

    public void replaceCanvas() {

        canvas.removeKeyListener(KeyHandler.instance);
        canvas.removeFocusListener(KeyHandler.instance);
        KeyHandler.releasedKeyIndex = -1;

        canvas.removeMouseListener(MouseHandler.instance);
        canvas.removeMouseMotionListener(MouseHandler.instance);
        canvas.removeFocusListener(MouseHandler.instance);
        MouseHandler.currentButton0 = 0;

        if (mouseWheelHandler != null)
            mouseWheelHandler.removeFrom(canvas);

        addCanvas();
        canvas.setFocusTraversalKeysEnabled(false);
        canvas.addKeyListener(KeyHandler.instance);
        canvas.addFocusListener(KeyHandler.instance);
        canvas.addMouseListener(MouseHandler.instance);
        canvas.addMouseMotionListener(MouseHandler.instance);
        canvas.addFocusListener(MouseHandler.instance);

        if (mouseWheelHandler != null)
            mouseWheelHandler.addTo(canvas);

        notifySizeChange();
    }

    public void checkCanvasSize() {
        Bounds bounds = getFrameContentBounds();
        if (bounds.getWidth() != contentWidth || contentHeight != bounds.getHeight() || canvasResizeNextFrame) {
            resizeCanvas();
            canvasResizeNextFrame = false;
           // System.out.println("content size: "+contentWidth+", "+contentHeight);
        }
    }

    protected abstract void methodDraw(boolean focus);

    private void clearBackground() {
        int canvasX = this.canvasX;
        int canvasY = this.canvasY;
        int marginX = contentWidth - canvasWidth - canvasX;
        int marginY = contentHeight - canvasHeight - canvasY;
        if (canvasX > 0 || marginX > 0 || canvasY > 0 || marginY > 0) {
            try {
                final Container container = container();
                int offsetX = 0;
                int offsetY = 0;
                if (container == frame) {
                    Insets insets = frame.getInsets();
                    offsetX = insets.left;
                    offsetY = insets.top;
                }

                final Graphics graphics = container.getGraphics();
                graphics.setColor(Color.black);
                if (canvasX > 0)
                    graphics.fillRect(offsetX, offsetY, canvasX, contentHeight);
                if (canvasY > 0)
                    graphics.fillRect(offsetX, offsetY, contentWidth, canvasY);
                if (marginX > 0)
                    graphics.fillRect(offsetX + contentWidth - marginX, offsetY, marginX, contentHeight);
                if (marginY > 0) {
                    graphics.fillRect(offsetX, offsetY + contentHeight - marginY, contentWidth, marginY);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void notifySizeChange() {
        canvasResizeNextFrame = true;
    }

    private Bounds getFrameContentBounds() {
        Container var1 = container();
        int width = Math.max(var1.getWidth(), contentWidth0);
        int height = Math.max(var1.getHeight(), contentHeight0);

        if (frame != null) {
            Insets var4 = frame.getInsets();
            width -= var4.right + var4.left;
            height -= var4.bottom + var4.top;
        }

        return new Bounds(width, height);
    }

    public void resizeCanvas() {
        final Container container = container();
        final Bounds bounds = getFrameContentBounds();
        contentWidth = Math.max(bounds.getWidth(), contentWidth0);
        contentHeight = Math.max(bounds.getHeight(), contentHeight0);
        if (contentWidth <= 0)
            contentWidth = 1;
        if (contentHeight <= 0)
            contentHeight = 1;
        canvasWidth = Math.min(contentWidth, maxCanvasWidth);
        canvasHeight = Math.min(contentHeight, maxCanvasHeight);
        canvasX = (contentWidth - canvasWidth) / 2;
        canvasY = 0;
        canvas.setSize(canvasWidth, canvasHeight);
        Flames.flameBuffer = new BasicGraphicsBuffer();
        Flames.flameBuffer.init(canvas, canvasWidth, canvasHeight, false, false);
//        System.out.println("Resized canvas ["+canvasWidth+", "+canvasHeight+"] bounds = "+bounds+", "+maxCanvasWidth+", "+maxCanvasHeight);
//            Client.gameScreenImageProducer = Client.constructGraphicsBuffer(canvasWidth, canvasHeight, true);
        if (container == frame) {
            final Insets insets = frame.getInsets();
            canvas.setLocation(canvasX + insets.left, insets.top + canvasY);
        } else
            canvas.setLocation(canvasX, canvasY);

        focusing = true;
        onResize();
        if(ClientUI.frameMode != Client.ScreenMode.FIXED)
            ClientCompanion.gameScreenImageProducer = Client.constructGraphicsBuffer(
                    ClientUI.frameWidth,
                    ClientUI.frameHeight,
                    true);
    }

    protected abstract void onResize();

    public final void drawInitial(int loadAmount, String text, boolean clearCanvas) {
        try {
            Graphics canvasGraphics = canvas.getGraphics();
            if (loadFont == null) {
                loadFont = new java.awt.Font("Helvetica", Font.BOLD, 13);
                loadFontMetrics = this.canvas.getFontMetrics(loadFont);
            }

            if (clearCanvas) {
                canvasGraphics.setColor(Color.black);
                canvasGraphics.fillRect(0, 0, canvasWidth, canvasHeight);
            }

            final Color loadBarFillColor = new Color(140, 17, 17);

            try {
                if (loadImageBar == null) {
                    loadImageBar = this.canvas.createImage(304, 34);
                }
                final Graphics loadBarGraphics = loadImageBar.getGraphics();
                loadBarGraphics.setColor(loadBarFillColor);
                loadBarGraphics.drawRect(0, 0, 303, 33);
                loadBarGraphics.fillRect(2, 2, loadAmount * 3, 30);
                loadBarGraphics.setColor(Color.black);
                loadBarGraphics.drawRect(1, 1, 301, 31);
                loadBarGraphics.fillRect(loadAmount * 3 + 2, 2, 300 - loadAmount * 3, 30);
                loadBarGraphics.setFont(loadFont);
                loadBarGraphics.setColor(Color.white);
                loadBarGraphics.drawString(text, (304 - loadFontMetrics.stringWidth(text)) / 2, 22);
                canvasGraphics.drawImage(loadImageBar, canvasWidth / 2 - 152, canvasHeight / 2 - 18, null);
            } catch (Exception e) {
                int loadBarX = canvasWidth / 2 - 152;
                int loadBarY = canvasHeight / 2 - 18;
                canvasGraphics.setColor(loadBarFillColor);
                canvasGraphics.drawRect(loadBarX, loadBarY, 303, 33);
                canvasGraphics.fillRect(loadBarX + 2, loadBarY + 2, loadAmount * 3, 30);
                canvasGraphics.setColor(Color.black);
                canvasGraphics.drawRect(loadBarX + 1, loadBarY + 1, 301, 31);
                canvasGraphics.fillRect(loadAmount * 3 + loadBarX + 2, loadBarY + 2, 300 - loadAmount * 3, 30);
                canvasGraphics.setFont(loadFont);
                canvasGraphics.setColor(Color.white);
                canvasGraphics.drawString(text, loadBarX + (304 - loadFontMetrics.stringWidth(text)) / 2, loadBarY + 22);
            }
        } catch (Exception var10) {
            this.canvas.repaint();
        }
    }

    protected final void startThread(int width, int height, int rev) {
        try {
            if (gameShell != null) {
                ++threadCount;
                if (threadCount >= 3) {
                    error("alreadyloaded");
                    return;
                }

                getAppletContext().showDocument(getDocumentBase(), "_self");
                return;
            }

            gameShell = this;
            canvasWidth = width;
            canvasHeight = height;
            revision = rev;
            if (taskHandler == null)
                taskHandler = new TaskHandler();
            taskHandler.newThreadTask(this, 1);
        } catch (Exception e) {
            Log.error("CRASH", e);
            error("crash");
        }
    }

    protected void setUpClipboard() {
        clipboard = getToolkit().getSystemClipboard();
    }

    void clipboardSetString(String string) {
        clipboard.setContents(new StringSelection(string), null);
    }

    protected final void setUpMouse() {
        canvas.addMouseListener(MouseHandler.instance);
        canvas.addMouseMotionListener(MouseHandler.instance);
        canvas.addFocusListener(MouseHandler.instance);
    }

    protected final void setUpKeyboard() {
        if (TaskHandler.javaVendor.toLowerCase().contains("microsoft")) {
            KeyHandler.keyCodes[186] = 57;
            KeyHandler.keyCodes[187] = 27;
            KeyHandler.keyCodes[188] = 71;
            KeyHandler.keyCodes[189] = 26;
            KeyHandler.keyCodes[190] = 72;
            KeyHandler.keyCodes[191] = 73;
            KeyHandler.keyCodes[192] = 58;
            KeyHandler.keyCodes[219] = 42;
            KeyHandler.keyCodes[220] = 74;
            KeyHandler.keyCodes[221] = 43;
            KeyHandler.keyCodes[222] = 59;
            KeyHandler.keyCodes[223] = 28;
        } else {
            KeyHandler.keyCodes[44] = 71;
            KeyHandler.keyCodes[45] = 26;
            KeyHandler.keyCodes[46] = 72;
            KeyHandler.keyCodes[47] = 73;
            KeyHandler.keyCodes[59] = 57;
            KeyHandler.keyCodes[61] = 27;
            KeyHandler.keyCodes[91] = 42;
            KeyHandler.keyCodes[92] = 74;
            KeyHandler.keyCodes[93] = 43;
            KeyHandler.keyCodes[192] = 28;
            KeyHandler.keyCodes[222] = 58;
            KeyHandler.keyCodes[520] = 59;
        }
        canvas.setFocusTraversalKeysEnabled(false);
        canvas.addKeyListener(KeyHandler.instance);
        canvas.addFocusListener(KeyHandler.instance);
    }

    public final void setMaxCanvasSize(int width, int height) {
        if (maxCanvasWidth != width || height != maxCanvasHeight)
            notifySizeChange();
        maxCanvasWidth = width;
        maxCanvasHeight = height;
    }

    protected MouseWheel mouseWheel() {
        if (mouseWheelHandler == null) {
            mouseWheelHandler = new MouseWheelHandler();
            mouseWheelHandler.addTo(canvas);
        }
        return mouseWheelHandler;
    }

    protected final boolean hasFrame() {
        return this.frame != null;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    public Graphics getGraphics() {
        return Configuration.DOUBLE_BUFFERING ? drawGraphics : super.getGraphics();
    }
}
