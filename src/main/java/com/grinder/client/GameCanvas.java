package com.grinder.client;

import com.grinder.Configuration;
import net.runelite.client.util.OSType;

import java.awt.*;

/**
 * A <code>GameCanvas</code> components represents a {@link Canvas} that
 * forwards update and paint calls to the {@link #component}.
 *
 * For unix systems we use a {@link java.awt.image.BufferStrategy} implementation
 * to improve overall performance and smoothness of the rendering process.
 *
 * The {@link java.awt.image.BufferStrategy} implementation caused screen tearing and
 * other issues on windows machine. Preferably these issues are fixed and this becomes
 * the default way of rendering.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 11/12/2019
 */
public class GameCanvas extends Canvas {

    private final Component component;

    /**
     * Construct a new GameCanvas.
     *
     * @param component the {@link Component} paint and update calls are being delegated to.
     */
    protected GameCanvas(Component component) {
        this.component = component;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if(Configuration.DOUBLE_BUFFERING)
            createBufferStrategy(2);
    }

    @Override
    public final void update(Graphics graphics) {
        component.update(graphics);
    }

    @Override
    public final void paint(Graphics graphics) {
        component.paint(graphics);
    }

    @Override
    public Graphics getGraphics() {

        if(!Configuration.DOUBLE_BUFFERING || component.getGraphics() == null)
            return super.getGraphics();

        return component.getGraphics();
    }
}
