package com.runescape.scene;

import com.runescape.cache.anim.Graphic;
import com.runescape.cache.anim.RSFrame317;
import com.runescape.entity.Renderable;
import com.runescape.entity.model.Model;

public final class Projectile extends Renderable {

    public int endX, endY;

    public final int startCycle;
    public final int stopCycle;
    public final int endHeight;
    public final int target;
    public final int projectileZ;
    private final int projectileX;
    private final int projectileY;
    private final int startHeight;
    private final int initialSlope;
    private final int initialDistance;
    private final Graphic projectileGFX;
    public double xPos;
    public double yPos;
    public double cnterHeight;
    public int turnValue;
    private double xIncrement;
    private double yIncrement;
    private double diagonalIncrement;
    private double heightIncrement;
    private double heightOffset;
    private boolean started;
    private int gfxStage;
    private int gfxTickOfCurrentStage;
    private int tiltAngle;

    public Projectile(int initialSlope, int endHeight, int creationCycle, int destructionCycle, int initialDistance, int startZ, int startHeight, int y, int x, int target, int gfxMoving) {
        projectileGFX = Graphic.getSpotAnimationDefinition(gfxMoving);
        projectileZ = startZ;
        projectileX = x;
        projectileY = y;
        this.startHeight = startHeight;
        startCycle = creationCycle;
        stopCycle = destructionCycle;
        this.initialSlope = initialSlope;
        this.initialDistance = initialDistance;
        this.target = target;
        this.endHeight = endHeight;
        started = false;
    }

    public void calculateIncrements(int currentCycle, int targetY, int targetCenterHeight, int targetX) {
        if (!started) {
            double xToGo = targetX - projectileX;
            double yToGo = targetY - projectileY;
            double distanceToGo = Math.sqrt(xToGo * xToGo + yToGo * yToGo);
            xPos = (double) projectileX + (xToGo * (double) initialDistance) / distanceToGo;
            yPos = (double) projectileY + (yToGo * (double) initialDistance) / distanceToGo;
            cnterHeight = startHeight;
        }
        double cyclesLeft = (stopCycle + 1) - currentCycle;
        xIncrement = ((double) targetX - xPos) / cyclesLeft;
        yIncrement = ((double) targetY - yPos) / cyclesLeft;
        diagonalIncrement = Math.sqrt(xIncrement * xIncrement + yIncrement * yIncrement);
        if (!started) {
            heightIncrement = -diagonalIncrement * Math.tan((double) initialSlope * 0.02454369D);
        }
        heightOffset = (2D * ((double) targetCenterHeight - cnterHeight - heightIncrement * cyclesLeft)) / (cyclesLeft * cyclesLeft);
    }

    public void lockOnPosition(int endX, int endY) {
        this.endX = endX;
        this.endY = endY;
    }

    public Model getRotatedModel() {
        Model modelGfx = projectileGFX.getModel(gfxTickOfCurrentStage);
        if (modelGfx == null) {
            return null;
        }
        modelGfx.leanOverX(tiltAngle);
        return modelGfx;
    }

    public void progressCycles(int cyclesMissed) {
        started = true;
        xPos += xIncrement * (double) cyclesMissed;
        yPos += yIncrement * (double) cyclesMissed;
        cnterHeight += heightIncrement * (double) cyclesMissed + 0.5D * heightOffset * (double) cyclesMissed * (double) cyclesMissed;
        heightIncrement += heightOffset * (double) cyclesMissed;
        //noinspection SuspiciousNameCombination
        turnValue = (int) (Math.atan2(xIncrement, yIncrement) * 325.94900000000001D) + 1024 & 0x7ff;
        tiltAngle = (int) (Math.atan2(heightIncrement, diagonalIncrement) * 325.94900000000001D) & 0x7ff;
        if (projectileGFX.animationSequence != null) {
            for (gfxTickOfCurrentStage += cyclesMissed; gfxTickOfCurrentStage > projectileGFX.animationSequence.duration(); ) {
                gfxTickOfCurrentStage -= projectileGFX.animationSequence.duration() + 1;
                gfxStage++;
                if (gfxStage >= projectileGFX.animationSequence.frameCount) {
                    gfxStage = 0;
                }
            }
        }
    }
}