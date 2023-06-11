package com.grinder.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 09/12/2019
 */
public class SnowOverlay {

    private final List<SnowFlock> snowFlocks;
    private final Timer timer;
    private float angle = 0;

    SnowOverlay(int particleCount) {
        snowFlocks = new ArrayList<>(particleCount);
        for(int i = 0; i < particleCount; i++){
            snowFlocks.add(new SnowFlock(
                    (int) (Math.random() * ClientUI.frameWidth),
                    (int) (Math.random() * ClientUI.frameHeight),
                    (int) (Math.random() * 11 + 1),
                    (int) (Math.random() * particleCount)
            ));
        }
        timer = new Timer(40, a -> resetParticles());

    }

    public void paintComponent(Graphics g) {

        for(SnowFlock flock : snowFlocks){
            flock.x++;
            flock.y++;
            g.fillOval(flock.x, flock.y, flock.radius, flock.density);
        }

        if (angle == 0.0) {
            timer.start();
            angle = 0.01f;
        }
//        Toolkit.getDefaultToolkit().sync();
    }


    public void resetParticles(){
        angle += 0.01;
        for (int i = 0; i < snowFlocks.size(); i++) {
            SnowFlock flock = snowFlocks.get(i);
            flock.x += Math.sin(angle) * 2;
            flock.y += Math.cos(angle + flock.density) + 1 + flock.radius / 2;
            if (flock.x > ClientUI.frameWidth + 5 || flock.x < ClientUI.frameWidth - 5 || flock.y > ClientUI.frameHeight) {
                if(i % 3 > 0){
                    flock.x = (int) (Math.random() * ClientUI.frameWidth);
                    flock.y = -10;
                } else {
                    flock.x = Math.sin(angle) > 0 ? -5 : ClientUI.frameWidth + 5;
                    flock.y = (int) (Math.random() * ClientUI.frameHeight);
                }
            }
        }
    }

    static class SnowFlock {

        private int x;
        private int y;
        private final int radius;
        private final int density;

        SnowFlock(int x, int y, int radius, int density) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.density = density;
        }
    }

}
