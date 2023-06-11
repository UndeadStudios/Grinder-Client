package com.grinder.ui.util;

import com.bulenkov.darcula.DarculaLaf;
import com.bulenkov.iconloader.util.JBUI;
import com.grinder.ui.ClientUI;
import com.grinder.client.util.Log;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.basic.BasicLookAndFeel;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-17
 */
public class SwingUtil {

    /**
     * Sets some sensible defaults for swing.
     * IMPORTANT! Needs to be called before main frame creation
     */
    public static void setupDefaults() {
        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            BasicLookAndFeel darculaLookAndFeel = new DarculaLaf();
            UIManager.setLookAndFeel(darculaLookAndFeel);
        } catch (Throwable e) {
            System.err.printf("Unable to set dracula LAF theme, reverting back to default.%n");
            setDefaultLAFTheme();
        }
//        try {
//            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            // If Nimbus is not available, you can set the GUI to another look and feel.
//        }
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
    }

    private static void setDefaultLAFTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTraversableKeys(final ClientUI frame) {

        final Set<AWTKeyStroke> forwardKeys = frame.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        final Set<AWTKeyStroke> newForwardKeys = new HashSet<>(forwardKeys);

        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));

        frame.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newForwardKeys);
        frame.setFocusTraversalKeysEnabled(false);
    }


    /**
     * Add graceful exit callback.
     *
     * @param frame           the frame
     * @param callback        the callback
     * @param confirmRequired the confirm required
     */
    public static void addGracefulExitCallback(final JFrame frame, final Runnable callback, final Callable<Boolean> confirmRequired) {
        frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                int result = JOptionPane.OK_OPTION;

                try {
                    if (confirmRequired.call()) {
                        result = JOptionPane.showConfirmDialog(
                                frame,
                                "Are you sure you want to exit?", "Exit",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (result == JOptionPane.OK_OPTION) {
                    callback.run();
                    System.exit(0);
                }
            }
        });
    }

    public static JButton createButton(String name, Action action) {
        Icon icon = (Icon) UIManager.get(name);

        JButton button = new JButton(icon) {
            boolean mouseOverButton = false;

            {
                enableEvents(AWTEvent.MOUSE_EVENT_MASK);
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        mouseOverButton = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        mouseOverButton = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                IconUtil.paintInCenterOf(this, g, mouseOverButton ? icon : icon);
            }
        };
        button.putClientProperty("paintActive", Boolean.TRUE);
        button.putClientProperty(AccessibleContext.ACCESSIBLE_NAME_PROPERTY, name);
        button.setBorder(JBUI.Borders.empty());
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setOpaque(false);
        button.setAction(action);
        return button;
    }

    /**
     * Create tray icon.
     *
     * @param icon  the icon
     * @param title the title
     * @param frame the frame
     * @return the tray icon
     */
    public static TrayIcon createTrayIcon(final Image icon, final String title, final Frame frame) {
        if (!SystemTray.isSupported()) {
            return null;
        }

        final SystemTray systemTray = SystemTray.getSystemTray();
        final TrayIcon trayIcon = new TrayIcon(icon, title);
        trayIcon.setImageAutoSize(true);

        try {
            systemTray.add(trayIcon);
        } catch (AWTException ex) {
            Log.getLogger().warn("Unable to add system tray icon ", ex);
            return trayIcon;
        }

        // Bring to front when tray icon is clicked
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.setVisible(true);
                frame.setState(Frame.NORMAL); // Restore
            }
        });

        return trayIcon;
    }

    public static void makeDraggable(final Component obj, final boolean info) {

        MouseInputAdapter d = new MouseInputAdapter() {
            int x, X, y, Y;

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    x = e.getXOnScreen();
                    X = obj.getLocation().x;
                    y = e.getYOnScreen();
                    Y = obj.getLocation().y;
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    obj.setLocation(X + (e.getXOnScreen() - x), Y + (e.getYOnScreen() - y));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (info && SwingUtilities.isLeftMouseButton(e)) {
                    System.err.println(obj.toString().substring(0, obj.toString().indexOf("[")) + " (" + obj.getLocation().x + "," + obj.getLocation().y + ")");
                }
            }
        };
        obj.addMouseListener(d);
        obj.addMouseMotionListener(d);
    }
}
