package com.runescape.scene;

import com.grinder.Configuration;
import com.runescape.cache.anim.Animation;
import com.runescape.cache.anim.Graphic;
import com.runescape.entity.Renderable;
import com.runescape.entity.model.Model;

public final class AnimableObject extends Renderable {

    public final int id;
    public final int anInt1560;
    public final int anInt1561;
    public final int anInt1562;
    public final int anInt1563;
    public final int cycleStart;
    public Animation sequenceDefinition;
    public boolean isFinished;
    private int frame;
    private int anInt1570;
    private int nextAnimFrameId;
    public int frameCycle;

    public AnimableObject(int i, int j, int l, int i1, int j1, int k1, int l1) {
        frameCycle = 0;
        isFinished = false;
        id = i1;
        anInt1560 = i;
        anInt1561 = l1;
        anInt1562 = k1;
        anInt1563 = j1;
        cycleStart = j + l;
        int var8 = Graphic.getSpotAnimationDefinition(id).sequence;
        if (var8 != -1) {
            isFinished = false;
            sequenceDefinition = Animation.getSequenceDefinition(var8);
        } else {
            isFinished = true;
        }
    }

    public Model getRotatedModel() {
        Graphic var1 = Graphic.getSpotAnimationDefinition(id);
        Model var2;
        if (!isFinished) {
            var2 = var1.getModel(frame);
        } else {
            var2 = var1.getModel(-1);
        }
        return var2 == null ? null : var2;
    }

    public void advance(int var1) {
        if (!this.isFinished) {
            this.frameCycle += var1;
            if (!this.sequenceDefinition.isCachedModelIdSet()) {
                while(this.frameCycle > this.sequenceDefinition.frameLengths[this.frame]) {
                    this.frameCycle -= this.sequenceDefinition.frameLengths[this.frame];
                    ++this.frame;
                    if (this.frame >= this.sequenceDefinition.frameIds.length) {
                        this.isFinished = true;
                        break;
                    }
                }
            } else {
                this.frame += var1;
                if (this.frame >= this.sequenceDefinition.duration()) {
                    this.isFinished = true;
                }
            }

        }
    }
}