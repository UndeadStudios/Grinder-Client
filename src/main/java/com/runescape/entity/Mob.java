package com.runescape.entity;

import com.runescape.Client;
import com.runescape.audio.Audio;
import com.runescape.cache.anim.Animation;
import com.runescape.cache.anim.Graphic;

public class Mob extends Renderable {

    public int index = -1;
    public int x;
    public int y;
    public int defaultHeight;
    public int direction;
    public int targetIndex;

    public int sequenceDelay;
    public int faceDegrees;
    public int __cm;
    int spotAnimationFrame;
    public int currentAnimationLoopCount;
    public int currentAnimationNextFrameIndex;

    public int idleSequence;
    public int idleAnimationNextFrameIndex;
    public int turnLeftSequence;
    public int turnRightSequence;

    public int sequence;
    public int sequenceFrame;
    public int sequenceFrameCycle;
    public int movementSequence;
    public int movementFrame;
    public int walkSequence;
    public int walkTurnSequence;
    public int walkTurnLeftSequence;
    public int walkTurnRightSequence;

    public boolean animationStretches;

    public int spotAnimation;
    public int graphicDelay;
    public int heightOffset;
    public int nextSpotAnimFrame;
    public int nextGraphicsAnimationFrame;

    public int textColour;
    public int textEffect;

    public int __cq;
    public int degreesToTurn;
    public int runSequence;
    public int field1152 = -1;
    public int field1153 = -1;
    public int field1154 = -1;
    public int field1155 = -1;
    public int field1200 = -1;
    public int field1212 = -1;
    public int field1158 = -1;
    public String spokenText;

    public int orientation;

    public int movementFrameCycle;
    public int spotAnimationFrameCycle;
    public int pathLength;
    public int loopCycleStatus;
    public int currentHealth;
    public int maxHealth;
    public int textCycle;
    public int npcCycle;
    public int faceX;
    public int faceY;
    public int size;

    public int __ch;
    public int initialX;
    public int destinationX;
    public int initialY;
    public int destinationY;
    public int startForceMovement;
    public int endForceMovement;

    public int orientationScene;

    public final int[] pathX;
    public final int[] pathY;
    public final int[] hitDamages;
    public final int[] hitMarkTypes;
    public final int[] hitsLoopCycle;
    public final boolean[] pathTraversed;
    private int field1181;

    public Mob() {
        pathX = new int[10];
        pathY = new int[10];
        targetIndex = -1;
        degreesToTurn = 32;
        runSequence = -1;
        defaultHeight = 200;
        idleSequence = -1;
        hitDamages = new int[4];
        hitMarkTypes = new int[4];
        hitsLoopCycle = new int[4];
        setMovementSequence(-1);
        spotAnimation = -1;
        setSequence(-1);
        loopCycleStatus = -1000;
        textCycle = 100;
        size = 1;
        animationStretches = false;
        pathTraversed = new boolean[10];
        walkSequence = -1;
        walkTurnSequence = -1;
        walkTurnLeftSequence = -1;
        walkTurnRightSequence = -1;
        turnLeftSequence = -1;
        turnRightSequence = -1;
        faceDegrees = -1;
    }

    public final void setPos(int x, int y, boolean flag) {
        if (getSequence() != -1 && Animation.getSequenceDefinition(getSequence()).priority == 1)
            setSequence(-1);

        if (!flag) {
            int dx = x - pathX[0];
            int dy = y - pathY[0];
            if (dx >= -8 && dx <= 8 && dy >= -8 && dy <= 8) {
                if (pathLength < 9)
                    pathLength++;
                for (int i1 = pathLength; i1 > 0; i1--) {
                    pathX[i1] = pathX[i1 - 1];
                    pathY[i1] = pathY[i1 - 1];
                    pathTraversed[i1] = pathTraversed[i1 - 1];
                }

                pathX[0] = x;
                pathY[0] = y;
                pathTraversed[0] = false;
                return;
            }
        }
        pathLength = 0;
        __ch = 0;
        __cq = 0;
        pathX[0] = x;
        pathY[0] = y;
        this.x = pathX[0] * 128 + size * 64;
        this.y = pathY[0] * 128 + size * 64;
    }

    public int getLocalX() {
        return (x - (size * 64)) / 128;
    }

    public int getLocalY() {
        return (y - (size * 64)) / 128;
    }

    public final void resetPath() {
        pathLength = 0;
        __ch = 0;
    }

    public final void updateHitData(int hitType, int hitDamage, int currentTime) {
        for (int hitPtr = 0; hitPtr < 4; hitPtr++)
            if (hitsLoopCycle[hitPtr] <= currentTime) {
                hitDamages[hitPtr] = hitDamage;
                hitMarkTypes[hitPtr] = hitType;
                hitsLoopCycle[hitPtr] = currentTime + 70;
                return;
            }
    }

    public void nextPreForcedStep() {
        int remaining = startForceMovement - Client.tick;
        int tempX = initialX * 128 + size * 64;
        int tempY = initialY * 128 + size * 64;
        x += (tempX - x) / remaining;
        y += (tempY - y) / remaining;

        __cq = 0;

        if (direction == 0) {
            orientation = 1024;
        }

        if (direction == 1) {
            orientation = 1536;
        }

        if (direction == 2) {
            orientation = 0;
        }

        if (direction == 3) {
            orientation = 512;
        }
    }

    public void nextForcedMovementStep() {
        boolean var1 = endForceMovement == Client.tick || getSequence() == -1 || sequenceDelay != 0;
        if (!var1) {
            Animation var2 = Animation.getSequenceDefinition(getSequence());
            if (var2 != null && !var2.isCachedModelIdSet()) {
                var1 = sequenceFrameCycle + 1 > var2.frameLengths[sequenceFrame];
            } else {
                var1 = true;
            }
        }

        if (var1) {
            int remaining = endForceMovement - startForceMovement;
            int elapsed = Client.tick - startForceMovement;
            int initialX = this.initialX * 128 + size * 64;
            int initialY = this.initialY * 128 + size * 64;
            int endX = destinationX * 128 + size * 64;
            int endY = destinationY * 128 + size * 64;
            x = (initialX * (remaining - elapsed) + endX * elapsed) / remaining;
            y = (initialY * (remaining - elapsed) + endY * elapsed) / remaining;
        }

        __cq = 0;

        if (direction == 0) {
            orientation = 1024;
        }

        if (direction == 1) {
            orientation = 1536;
        }

        if (direction == 2) {
            orientation = 0;
        }

        if (direction == 3) {
            orientation = 512;
        }

        orientationScene = orientation;
    }

    public void nextStep() {
        movementSequence = (idleSequence);

        if (pathLength == 0) {
            __cq = 0;
        } else {
            label310:
            {
                if (sequence != -1 && sequenceDelay == 0) {
                    Animation animation = Animation.getSequenceDefinition(sequence);
                    if (__ch > 0 && animation.animatingPrecedence == 0) {
                        ++__cq;
                        break label310;
                    }

                    if (__ch <= 0 && animation.priority == 0) {
                        ++__cq;
                        break label310;
                    }
                }
                int tempX = x;
                int tempY = y;
                int nextX = pathX[pathLength - 1] * 128 + size * 64;
                int nextY = pathY[pathLength - 1] * 128 + size * 64;

                if (tempX < nextX) {
                    if (tempY < nextY) orientation = 1280;
                    else if (tempY > nextY) orientation = 1792;
                    else orientation = 1536;
                } else if (tempX > nextX) {
                    if (tempY < nextY) orientation = 768;
                    else if (tempY > nextY) orientation = 256;
                    else orientation = 512;
                } else if (tempY < nextY)
                    orientation = 1024;
                else if (tempY > nextY)
                    orientation = 0;

                boolean traversed = pathTraversed[pathLength - 1];
                int rotation;
                if (nextX - tempX <= 256 && nextX - tempX >= -256 && nextY - tempY <= 256 && nextY - tempY >= -256) {
                    rotation = orientation - orientationScene & 2047;
                    if (rotation > 1024) {
                        rotation -= 2048;
                    }
                    int sequence = walkTurnSequence;
                    if (rotation >= -256 && rotation <= 256) {
                        sequence = walkSequence;
                    } else if (rotation >= 256 && rotation < 768) {
                        sequence = walkTurnRightSequence;
                    } else if (rotation >= -768 && rotation <= -256) {
                        sequence = walkTurnLeftSequence;
                    }
                    if (sequence == -1)
                        sequence = walkSequence;
                    movementSequence = sequence;
                    int int_6 = 4;
                    boolean click = true;

                    if (this instanceof Npc) {
                        click = ((Npc) this).desc.clickable;
                    }

                    if (click) {
                        if (orientationScene != orientation && targetIndex == -1 && degreesToTurn != 0)
                            int_6 = 2;
                        if (pathLength > 2) int_6 = 6;
                        if (pathLength > 3) int_6 = 8;
                    } else {
                        if (pathLength > 1) int_6 = 6;
                        if (pathLength > 2) int_6 = 8;
                    }

                    if (__cq > 0 && pathLength > 1) {
                        int_6 = 8;
                        --__cq;
                    }

                    if (traversed) {
                        int_6 <<= 1;
                    }
                    if (int_6 >= 8) {
                        if (movementSequence == walkSequence && runSequence != -1) {
                            movementSequence = runSequence;
                        } else if (walkTurnSequence == movementSequence && field1152 != -1) {
                            movementSequence = field1152;
                        } else if (movementSequence == walkTurnLeftSequence && field1153 != -1) {
                            movementSequence = field1153;
                        } else if (walkTurnRightSequence == movementSequence && field1154 != -1) {
                            movementSequence = field1154;
                        }
                    } else if (int_6 <= 1) {
                        if (walkSequence == movementSequence && field1155 != -1) {
                            movementSequence = field1155;
                        } else if (walkTurnSequence == movementSequence && field1200 != -1) {
                            movementSequence = field1200;
                        } else if (walkTurnLeftSequence == movementSequence && field1212 != -1) {
                            movementSequence = field1212;
                        } else if (walkTurnRightSequence == movementSequence && field1158 != -1) {
                            movementSequence = field1158;
                        }
                    }
//                    if(this instanceof Player)
//                        System.out.println(int_6+" -> ["+tempX+", "+nextX+"]["+tempY+", "+nextY+"]");
                    if (tempX != nextX || tempY != nextY) {
                        if (tempX < nextX) {
                            x += int_6;
                            if (x > nextX) {
                                x = nextX;
                            }
                        } else if (tempX > nextX) {
                            x -= int_6;
                            if (x < nextX) {
                                x = nextX;
                            }
                        }

                        if (tempY < nextY) {
                            y += int_6;
                            if (y > nextY) {
                                y = nextY;
                            }
                        } else if (tempY > nextY) {
                            y -= int_6;
                            if (y < nextY) {
                                y = nextY;
                            }
                        }
                    }
                    if (nextX == x && nextY == y) {
                        --pathLength;
                        if (__ch > 0) {
                            --__ch;
                        }
                    }
                } else {
                    x = nextX;
                    y = nextY;
                    --pathLength;
                    if (__ch > 0) {
                        --__ch;
                    }
                }
            }
        }
    }

    public void method1196(final int var1, final int var2) {
        pathLength = 0;
        __ch = 0;
        __cq = 0;
        pathX[0] = var1;
        pathY[0] = var2;
        final int var3 = this.size;
        x = pathX[0] * 128 + var3 * 64;
        y = pathY[0] * 128 + var3 * 64;
    }

    public final void runInDir(int direction) {
        int x = pathX[0];
        int y = pathY[0];
        if (direction == 0) {
            x -= 2;
            y -= 2;
        } else if (direction == 1) {
            --x;
            y -= 2;
        } else if (direction == 2) {
            y -= 2;
        } else if (direction == 3) {
            ++x;
            y -= 2;
        } else if (direction == 4) {
            x += 2;
            y -= 2;
        } else if (direction == 5) {
            x -= 2;
            --y;
        } else if (direction == 6) {
            x += 2;
            --y;
        } else if (direction == 7) {
            x -= 2;
        } else if (direction == 8) {
            x += 2;
        } else if (direction == 9) {
            x -= 2;
            ++y;
        } else if (direction == 10) {
            x += 2;
            ++y;
        } else if (direction == 11) {
            x -= 2;
            y += 2;
        } else if (direction == 12) {
            --x;
            y += 2;
        } else if (direction == 13) {
            y += 2;
        } else if (direction == 14) {
            ++x;
            y += 2;
        } else if (direction == 15) {
            x += 2;
            y += 2;
        }
//        if (this instanceof Player)
//            System.out.println("Sequencing running dir " + direction + " -> new pos = " + x + " y " + y);
        sequencePath(x, y, true);
    }

    public final void walkInDir(int direction, boolean run) {

        int x = pathX[0];
        int y = pathY[0];

        if (direction == 0) {
            --x;
            --y;
        } else if (direction == 1) {
            --y;
        } else if (direction == 2) {
            ++x;
            --y;
        } else if (direction == 3) {
            --x;
        } else if (direction == 4) {
            ++x;
        } else if (direction == 5) {
            --x;
            ++y;
        } else if (direction == 6) {
            ++y;
        } else if (direction == 7) {
            ++x;
            ++y;
        }
//        if (this instanceof Player)
//            System.out.println("Sequencing walking dir " + direction + " -> new pos = " + x + " y " + y);
        sequencePath(x, y, run);
    }

    final void sequencePath(final int x, final int y, final boolean traversed) {

        if (getSequence() != -1 && Animation.getSequenceDefinition(getSequence()).priority == 1)
            setSequence(-1);

        if (pathLength < 9) {
            ++pathLength;
        }

        for (int var4 = pathLength; var4 > 0; --var4) {
            pathX[var4] = pathX[var4 - 1];
            pathY[var4] = pathY[var4 - 1];
            pathTraversed[var4] = pathTraversed[var4 - 1];
        }

        pathX[0] = x;
        pathY[0] = y;
        pathTraversed[0] = traversed;
    }

    public void updateAnimation() {

        try {
            if (getMovementSequence() > 13798) {
                setMovementSequence(-1);
            }
            animationStretches = false;

            if (movementSequence != -1) {
                Animation animation = Animation.getSequenceDefinition(movementSequence);

                if (animation != null) {
                    if (!animation.isCachedModelIdSet() && animation.frameIds != null) {
                        movementFrameCycle++;
                        if (movementFrame < animation.frameIds.length && movementFrameCycle > animation.frameLengths[movementFrame]) {
                            movementFrameCycle = 1;
                            movementFrame++;
                            Audio.handleSoundEffects(animation, movementFrame, x, y);
                        }

                        if (movementFrame >= animation.frameIds.length) {
                            movementFrameCycle = 0;
                            movementFrame = 0;
                            Audio.handleSoundEffects(animation, movementFrame, x, y);
                        }
                    } else if (animation.isCachedModelIdSet()) {
                        movementFrame++;
                        int var2 = animation.method4015();
                        if (movementFrame < var2) {
                            Audio.handleSoundEffects(animation, movementFrame, x, y);
                        } else {
                            if (animation.frameCount > 0) {
                                movementFrame -= animation.frameCount;
                                if (animation.field2272) {
                                    ++field1181;
                                }

                                if (movementFrame < 0 || movementFrame >= var2 || animation.field2272 && field1181 >= animation.maximumLoops) {
                                    movementFrame = 0;
                                    movementFrameCycle = 0;
                                    field1181 = 0;
                                }
                            } else {
                                movementFrameCycle = 0;
                                movementFrame = 0;
                            }

                            Audio.handleSoundEffects(animation, movementFrame, x, y);
                        }
                    } else {
                        movementSequence = -1;
                    }
                } else {
                    movementSequence = -1;
                }
            }
            if (spotAnimation != -1 && Client.tick >= graphicDelay) {

                if (spotAnimationFrame < 0)
                    spotAnimationFrame = 0;

                Animation gfxAnimation = Graphic.getSpotAnimationDefinition(spotAnimation).animationSequence;

                if (gfxAnimation != null && gfxAnimation.frameIds != null) {

                    ++spotAnimationFrameCycle;

                    if (spotAnimationFrame < gfxAnimation.frameIds.length && spotAnimationFrameCycle > gfxAnimation.frameLengths[getSpotAnimationFrame()]) {
                        spotAnimationFrameCycle = 1;
                        ++spotAnimationFrame;
                        Audio.handleSoundEffects(gfxAnimation, spotAnimationFrame, x, y);
                    }
                    if (spotAnimationFrame >= gfxAnimation.frameIds.length) {
                        spotAnimation = -1;
                    }
                } else
                    spotAnimation = -1;
            }
            if (sequence != -1 && sequenceDelay <= 1) {
                Animation animation_2 = Animation.getSequenceDefinition(getSequence());
                if (animation_2 != null) {
                    if (animation_2.animatingPrecedence == 1 && __ch > 0 && startForceMovement <= Client.tick && endForceMovement < Client.tick) {
                        sequenceDelay = 1;
                        return;
                    }
                }
            }
            if (sequence != -1 && sequenceDelay == 0) {
                Animation animation_3 = Animation.getSequenceDefinition(sequence);
                if (animation_3 != null) {
                    if (!animation_3.isCachedModelIdSet() && animation_3.frameIds != null) {
                        ++sequenceFrameCycle;
                        if (sequenceFrame < animation_3.frameIds.length && sequenceFrameCycle > animation_3.frameLengths[sequenceFrame]) {
                            sequenceFrameCycle = 1;
                            ++sequenceFrame;
                            Audio.handleSoundEffects(animation_3, sequenceFrame, x, y);
                        }
                        if (sequenceFrame >= animation_3.frameIds.length) {
                            sequenceFrame -= animation_3.frameCount;
                            ++currentAnimationLoopCount;
                            if (currentAnimationLoopCount >= animation_3.maximumLoops) {
                                sequence = -1;
                            } else if (sequenceFrame >= 0 && sequenceFrame < animation_3.frameIds.length) {
                                Audio.handleSoundEffects(animation_3, sequenceFrame, x, y);
                            } else {
                                sequence = -1;
                            }
                        }
                        animationStretches = animation_3.stretches;
                    } else if (animation_3.isCachedModelIdSet()) {
                        ++sequenceFrame;
                        int var2 = animation_3.method4015();
                        if (sequenceFrame < var2) {
                            Audio.handleSoundEffects(animation_3, sequenceFrame, x, y);
                        } else {
                            sequenceFrame -= animation_3.frameCount;
                            ++currentAnimationLoopCount;
                            if (currentAnimationLoopCount >= animation_3.maximumLoops) {
                                sequence = -1;
                            } else if (sequenceFrame >= 0 && sequenceFrame < var2) {
                                Audio.handleSoundEffects(animation_3, sequenceFrame, x, y);
                            } else {
                                sequence = -1;
                            }
                        }
                    } else {
                        sequence = -1;
                    }
                } else {
                    sequence = -1;
                }
            }
            if (sequenceDelay > 0)
                sequenceDelay--;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isVisible() {
        return false;
    }

    public int getSpotAnimationFrame() {
        return spotAnimationFrame;
    }

    public void setSpotAnimationFrame(int spotAnimationFrame) {
//        if(this instanceof Player)
//            System.out.println("currentAnimationId = " + currentAnimationId);
        this.spotAnimationFrame = spotAnimationFrame;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
//        if(this instanceof Player)
//            System.out.println("emoteAnimation = " + emoteAnimation);
        this.sequence = sequence;
    }

    public int getMovementSequence() {
        return movementSequence;
    }

    public void setMovementSequence(int movementSequence) {
//        System.out.println("movementAnimationId = " + movementAnimationId);
        this.movementSequence = movementSequence;
    }
}
