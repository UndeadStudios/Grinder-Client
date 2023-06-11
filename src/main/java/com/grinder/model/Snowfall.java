package com.grinder.model;

import com.runescape.draw.Rasterizer2D;

import java.awt.*;

public class Snowfall {

    static final int LOGIN_WIDTH = 765;
    static final int LOGIN_HEIGHT = 503;

    private static final Snowflake[] snowflakes;

    public static void draw(boolean increment) {
        Graphics2D graphics2D = Rasterizer2D.createEllipseGraphics();
        for (int i = 0; i < snowflakes.length; i++) {
            Snowflake snowflake = snowflakes[i];
            if(increment) {
                snowflake.incrementX();
                snowflake.incrementY();
            }

            if ((snowflake.getY() > LOGIN_HEIGHT) || (snowflake.getX() > LOGIN_WIDTH) || (snowflake.getX() + (snowflake.getRadius() * 2) < 0)) {
                snowflakes[i] = new Snowflake();
            } else if (snowflake.getY() + (snowflake.getRadius() * 2) > 0) {
                Rasterizer2D.drawEllipse(graphics2D, snowflake.getX(), snowflake.getY(), snowflake.getRadius() * 2, snowflake.getRadius() * 2, 0, 0xffffff, snowflake.getOpacity(), true);
            }
        }
    }

    /**
     * Deletes a snowflake that gets clicked on
     * Put this in processLoginScreenInput() if it is ever used
     */
    /*public static void handleClick() {
        for (int i = 0; i < snowflakes.length; i++) {
            Snowflake snowflake = snowflakes[i];
            Client client = Client.instance;
            if (client.getClickMode3() == 1
                    && client.getMouseX() >= snowflake.getX() && client.getMouseX() < snowflake.getX() + (snowflake.getRadius() * 2)
                    && client.getMouseY() >= snowflake.getY() && client.getMouseY() < snowflake.getY() + (snowflake.getRadius() * 2)) {
                snowflakes[i] = new Snowflake();
            }
        }
    }*/

    static {
        snowflakes = new Snowflake[250];
        for (int i = 0; i < snowflakes.length; i++)
            snowflakes[i] = new Snowflake();
    }

}
