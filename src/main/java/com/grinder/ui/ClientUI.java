package com.grinder.ui;

import com.grinder.Configuration;
import com.runescape.cache.graphics.widget.WidgetManager;
import com.grinder.ui.util.SwingUtil;
import com.runescape.Client;
import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.cache.graphics.sprite.SpriteLoader;
import com.runescape.scene.SceneGraph;
import com.grinder.model.CaptchaViewer;
import com.grinder.GrinderScape;
import com.grinder.GrinderScapeSplashScreen;
import net.runelite.client.ui.ContainableFrame;
import net.runelite.client.util.OSType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 09/12/2019
 */
public class ClientUI extends Client implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static Image ICON;

    public static ContainableFrame frame;
    public static Console console;
    public static ScreenMode frameMode = ScreenMode.FIXED;
    public static int frameWidth = 765;
    public static int frameHeight = 503;
    public static int screenAreaWidth = 512;
    public static int screenAreaHeight = 334;
    public static int FRAME_WIDTH_OFFSET;
    public static int FRAME_HEIGHT_OFFSET;
    public static Sprite backgroundSprite;
    public static Sprite logoSprite;
    public static UIPanel panel;
    private TrayIcon trayIcon;

    static {
        try {
            ICON = Toolkit.getDefaultToolkit().getImage(GrinderScape.class.getResource("icon.png"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void launchConsole(){
        if(console != null){
            frame.remove(console);
            console = null;
        } else {
            frameMode(ScreenMode.FIXED, true);
            console = new Console();
            frame.add(console, BorderLayout.SOUTH);
        }
        frame.pack();
//        FRAME_HEIGHT_OFFSET = frame.getHeight() - frameHeight;
    }

    public static void launchCaptcha() {

        if (!System.getProperty("java.version").startsWith("1.8")) {
            System.out.println("Loading captcha in external browser due to java version not supporting JavaFX");
            CaptchaViewer.openInDefaultBrowser();
            return;
        }

        final Sprite backgroundSprite = SpriteLoader.getSprite(798);
        if (backgroundSprite != null) {
            com.grinder.javafx.CaptchaViewerFX.setBackgroundImage(backgroundSprite);
        }
//
        new Thread(() -> {
            final CountDownLatch loaded = new CountDownLatch(1);
            com.grinder.javafx.CaptchaViewerFX.bind(loaded);
        }).start();
    }

    public static void launchDevTool() {

        if (!System.getProperty("java.version").startsWith("1.8")) {
            JOptionPane.showMessageDialog(panel, "Could not load dev tool, requires java 1.8 (for JavaFX)");
            return;
        }

        JFrame window = new JFrame();

        new Thread(() -> com.grinder.javafx.DevToolFX.launchDevTool(window)).start();
    }

    public static void frameMode(ScreenMode screenMode, boolean updateInterface) {
        if (frameMode != screenMode) {
            //System.out.println("switching to "+screenMode+" "+updateInterface);
            frameMode = screenMode;
            if (screenMode == ScreenMode.FIXED) {
                //frame.setResizable(false);
//                frameWidth = 765;
//                frameHeight = 503;
                frameWidth = 765;
                frameHeight = 503;
//                cameraZoom = 600;
                SceneGraph.viewDistance = 9;
                if (updateInterface) {
                    WidgetManager.processMenuActions(instance, 315, 405, -1, 42522, 405, null);
                }
//                frame.setSize(frameWidth, frameHeight);
                Configuration.DOUBLE_BUFFERING = false;
            } else if (screenMode == ScreenMode.RESIZABLE) {
                //frame.setResizable(true);
                frameWidth = frame.getWidth() - FRAME_WIDTH_OFFSET;
                frameHeight = frame.getHeight() - FRAME_HEIGHT_OFFSET;
//                cameraZoom = 850;
//                SceneGraph.viewDistance = 10;
                if (updateInterface) {
                    WidgetManager.processMenuActions(instance, 315, 405, 16, 42523, 405, null);
                }
                Configuration.DOUBLE_BUFFERING = OSType.getOSType() != OSType.Windows;
            }

            rebuildFrameSize(screenMode, frameWidth, frameHeight);
            instance.setMaxCanvasSize();
            setBounds();

            if (ClientUI.frameMode == ScreenMode.FIXED)
                instance.setupGameplayScreen();

        }
    }

    public static void rebuildFrameSize(ScreenMode screenMode, int width, int height) {
        screenAreaWidth = (screenMode == ScreenMode.FIXED) ? 512 : width;
        screenAreaHeight = (screenMode == ScreenMode.FIXED) ? 334 : height;
        frameWidth = width;
        frameHeight = height;
//        instance.rebuildFrame(width, height, screenMode);
//        if(screenMode == ScreenMode.FIXED)
//            frame.pack();
    }

    public static void resizePanel(int width, int height) {
        panel.setSize(width, height);
    }

    /**
     * Initializes the UI components.
     */
    public void open(GrinderScape grinderScape) {

        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    backgroundSprite = new Sprite(ImageIO.read(GrinderScapeSplashScreen.class.getResourceAsStream("background.jpg")));

//            logoSprite = new Sprite(ImageIO.read(GrinderScapeSplashScreen.class.getResourceAsStream("grinder_logo.jpg")));

                    SwingUtil.setupDefaults();
                    SwingUtil.setTraversableKeys(this);

                    frame = new ContainableFrame();
                    frame.setTitle(Configuration.CLIENT_TITLE);
                    frame.setLocationRelativeTo(frame.getOwner());
                    frame.setResizable(true);


                    frame.setIconImage(ICON);
                    frame.setContainedInScreen(false);
                    SwingUtil.makeDraggable(frame, false);
                    SwingUtil.addGracefulExitCallback(frame, grinderScape::shutdown, this::showWarningOnExit);

                    panel = new UIPanel(this);

                    final UIMenuBar menuBar = new UIMenuBar(this);

                    frame.setJMenuBar(menuBar);
                    frame.add(panel);
                    frame.pack();
                    frame.setMinimumSize(frame.getSize());
                    frame.setLocationRelativeTo(null);

                    frame.setVisible(true);
                    frame.toFront();

                    trayIcon = SwingUtil.createTrayIcon(ICON, "GrinderScape", frame);

                    final int widthOffset = frame.getWidth() - frameWidth;
                    final int heightOffset = frame.getHeight() - frameHeight;
                    FRAME_WIDTH_OFFSET = widthOffset;
                    FRAME_HEIGHT_OFFSET = heightOffset;

                    frame.addWindowStateListener(e -> {
                        if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH){

                            if(OSType.getOSType() == OSType.MacOS)
                                FRAME_HEIGHT_OFFSET = 20;

                        } else {
                            FRAME_WIDTH_OFFSET = widthOffset;
                            FRAME_HEIGHT_OFFSET = heightOffset;
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private boolean showWarningOnExit() {
        return UIConstants.SHOW_WARNING_ON_EXIT;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        String cmd = evt.getActionCommand();

        if (cmd != null) {
            UIMenuBar.executeCommand(cmd);
        }
    }

    public TrayIcon getTrayIcon() {
        return trayIcon;
    }

    public boolean isFocused() {
        return frame.isFocused();
    }
}
