package com.grinder;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import com.grinder.ui.util.SwingUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * This is a custom Splash Screen and does not use Java's SplashScreen class. This has helper methods to update the
 * status while loading GrinderScape. All public methods run non-blocking passed to the swing thread.
 *
 * @author Jeremy Plsek <github.com/jplsek>
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 11/12/2019
 */
public class GrinderScapeSplashScreen {

//    private final GrinderScapeProperties properties = new GrinderScapeProperties();
    private final JPanel panel = new JPanel();
    private JFrame frame;
    private JLabel messageLabel, subMessageLabel;
    private JProgressBar progressBar = new JProgressBar();

    /**
     * This is not done in the constructor in order to avoid processing in case the user chooses to not load
     * the splash screen.
     *
     * @param estimatedSteps steps until completion, used for the progress bar
     */
    private void initLayout(final int estimatedSteps){
        SwingUtil.setupDefaults();

        frame = new JFrame("GrinderScape Loading");
        messageLabel = new JLabel("Loading...");
        subMessageLabel = new JLabel();
        progressBar.setUI(new BasicProgressBarUI());
        progressBar.setMinimum(0);
        progressBar.setMaximum(estimatedSteps);

        // frame setup
        frame.setSize(220, 290);
        frame.setLocationRelativeTo(null);
        frame.setUndecorated(true);

        panel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        final GridBagLayout layout = new GridBagLayout();
        layout.columnWeights = new double[]{1};
        layout.rowWeights = new double[]{1, 0, 0, 1, 0, 1};
        panel.setLayout(layout);

        synchronized (ImageIO.class) {
            try {
                final BufferedImage logo = ImageIO.read(GrinderScapeSplashScreen.class.getResourceAsStream("icon.png"));
                frame.setIconImage(logo);

                final BufferedImage logoTransparent = ImageIO.read(GrinderScapeSplashScreen.class.getResourceAsStream("grinder_splash.png"));
                final GridBagConstraints logoConstraints = new GridBagConstraints();
                logoConstraints.anchor = GridBagConstraints.SOUTH;
                panel.add(new JLabel(new ImageIcon(logoTransparent.getScaledInstance(96, 96, Image.SCALE_SMOOTH))), logoConstraints);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final JLabel title = new JLabel("GrinderScape");
        final GridBagConstraints titleConstraints = new GridBagConstraints();
        titleConstraints.gridy = 1;
        panel.add(title, titleConstraints);

        final JLabel version = new JLabel("GrinderScape Version : 1");
        version.setForeground(Color.GREEN);
        version.setFont(FontManager.getRunescapeSmallFont());
        version.setForeground(version.getForeground().darker());
        final GridBagConstraints versionConstraints = new GridBagConstraints();
        versionConstraints.gridy = 2;
        panel.add(version, versionConstraints);

        final GridBagConstraints progressConstraints = new GridBagConstraints();
        progressConstraints.fill = GridBagConstraints.HORIZONTAL;
        progressConstraints.anchor = GridBagConstraints.SOUTH;
        progressConstraints.gridy = 4;
        panel.add(progressBar, progressConstraints);

        // main message
        messageLabel.setFont(FontManager.getRunescapeSmallFont());
        final GridBagConstraints messageConstraints = new GridBagConstraints();
        messageConstraints.gridy = 5;
        panel.add(messageLabel, messageConstraints);

        // alternate message
        final GridBagConstraints subMessageConstraints = new GridBagConstraints();
        subMessageLabel.setForeground(subMessageLabel.getForeground().darker());
        subMessageLabel.setFont(FontManager.getRunescapeSmallFont());
        subMessageConstraints.gridy = 6;
        panel.add(subMessageLabel, subMessageConstraints);

        frame.setContentPane(panel);
    }
    private boolean notActive()
    {
        return frame == null || !frame.isDisplayable();
    }

    /**
     * Close/dispose of the splash screen
     */
    public void close()
    {
        SwingUtilities.invokeLater(() ->
        {
            if (notActive())
            {
                return;
            }

            frame.dispose();
        });
    }

    /**
     * Set the splash screen to be visible.
     *
     * @param estimatedSteps steps until completion, used for the progress bar
     */
    public void open(final int estimatedSteps)
    {
        SwingUtilities.invokeLater(() ->
        {
            initLayout(estimatedSteps);
            frame.setVisible(true);
        });
    }

    public void setMessage(final String message)
    {
        SwingUtilities.invokeLater(() ->
        {
            if (notActive())
            {
                return;
            }
            messageLabel.setText(message);
        });
    }

    public void setSubMessage(final String subMessage)
    {
        SwingUtilities.invokeLater(() ->
        {
            if (notActive())
            {
                return;
            }
            subMessageLabel.setText(subMessage);
        });
    }

    public void setProgress(int currentProgress, int progressGoal)
    {
        SwingUtilities.invokeLater(() ->
        {
            if (notActive())
            {
                return;
            }
            if (progressGoal != progressBar.getMaximum())
            {
                panel.remove(progressBar);
                panel.validate();
                final GridBagConstraints progressConstraints = new GridBagConstraints();
                progressConstraints.fill = GridBagConstraints.HORIZONTAL;
                progressConstraints.anchor = GridBagConstraints.SOUTH;
                progressConstraints.gridy = 4;
                panel.add(progressBar, progressConstraints);
                panel.validate();
            }
            progressBar.setMaximum(progressGoal);
            progressBar.setValue(currentProgress);
        });
    }

    public static void main(String[] args) {
        GrinderScape.PROFILES_DIR.mkdirs();
        new GrinderScapeSplashScreen().open(20);
    }
}
