package com.grinder;

import com.grinder.client.GameCanvas;
import com.grinder.ui.UIPanel;
import com.grinder.ui.util.SwingUtil;
import net.runelite.client.ui.ContainableFrame;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ComponentListener;

import static com.grinder.ui.ClientUI.frameHeight;
import static com.grinder.ui.ClientUI.frameWidth;

public class TestFrame {

    public static void main(String[] args) {
        Toolkit.getDefaultToolkit().setDynamicLayout(true);
        System.setProperty("sun.awt.noerasebackground", "true");//OSType.getOSType() == OSType.Windows ? "false" : "true");
        System.setProperty("sun.awt.erasebackgroundonresize", "true");
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

        SwingUtil.setupDefaults();
        Dimension expectedDimension = new Dimension(frameWidth, frameHeight);
        final Applet applet = new Applet(){
            @Override
            public final void paint(Graphics graphics){
                graphics.setColor(Color.BLACK);
                graphics.fillRect(0, 0, getWidth(), getHeight());
            }
            @Override
            public void init(){
                this.setLayout(new BorderLayout());
                final Canvas canvas = new GameCanvas(this){
                };
                canvas.setBackground(Color.CYAN);
                setSize(expectedDimension);
                add("Center", canvas);
                final Graphics graphics = getGraphics();
                //System.out.println(graphics);
            }
        };

        new ContainableFrame() {
            {

                setBackground(Color.BLACK);
                setLocationRelativeTo(getOwner());
                setResizable(true);
                setLayout(new BorderLayout());
                SwingUtil.makeDraggable(this, false);

                getRootPane().setBackground(Color.RED);
                getLayeredPane().setBackground(Color.BLUE);
                getContentPane().setBackground(Color.PINK);

                setSize(expectedDimension);


                //hmm
                final UIPanel panel = new UIPanel(applet);
                panel.setBackground(Color.BLACK);
                add(panel);
                //getContentPane().add(panel, BorderLayout.CENTER);


                pack();
                revalidate();

                setVisible(true);

                final ComponentListener[] componentListeners = getComponentListeners();
               // for(ComponentListener listener: componentListeners){
               //     System.out.println(listener);
               // }
                //  panel.setBackground(Color.BLUE)
                //panel.repaint();
            }
        };

    }

}
