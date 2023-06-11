package com.runescape.entity;

import com.runescape.Client;
import com.runescape.cache.anim.Animation;
import com.runescape.cache.anim.Graphic;
import com.runescape.cache.anim.RSFrame317;
import com.runescape.cache.def.NpcDefinition;
import com.runescape.cache.def.SpotAnimationDefinition;
import com.runescape.entity.model.Model;

public final class Npc extends Mob {

    public NpcDefinition desc;
    public int headIcon = -1;
    public int ownerIndex = -1;

    public String[] debugMessages = null;

    public boolean showActions() {
        if (ownerIndex == -1) {
            return true;
        }
        return (Client.instance.localPlayerIndex == ownerIndex);
    }

    public int getHeadIcon() {
        if (headIcon == -1) {
            if (desc != null) {
                return desc.headIcon;
            }
        }
        return headIcon;
    }

    public Model getRotatedModel() {
        if (desc == null) {
            return null;
        } else {
            Animation var1 = super.sequence != -1 && super.sequenceDelay == 0? Animation.getSequenceDefinition(super.sequence):null;
            Animation var2 = super.movementSequence != -1 && (super.movementSequence != super.idleSequence || var1 == null)? Animation.getSequenceDefinition(super.movementSequence):null;

            Model var3 = desc.getModel(var1, super.sequenceFrame, var2, super.movementFrame);
            if(var3 == null) {
                return null;
            } else {
                var3.calculateBoundsCylinder();
                super.defaultHeight = var3.height;
                if(desc.isWildernessBoss){
                    int healthDifference = maxHealth-currentHealth;
                    int percentage = (int) (healthDifference / (maxHealth/100.0));
                    int transparency = Math.max(0, Math.min(230, (int) (255*(percentage/100.0))));
                    var3.transparency((byte) transparency);
                }
                // int var4 = var3.indicesCount;
                if(super.spotAnimation != -1 && super.spotAnimationFrame != -1) {
                    Model var5 = Graphic.getSpotAnimationDefinition(super.spotAnimation).getModel(super.spotAnimationFrame);
                    if(var5 != null) {
                        var5.translate(0, -super.heightOffset, 0);
                        Model[] var6 = new Model[]{var3, var5};
                        var3 = new Model(var6, 2);
                    }
                }
                if(this.desc.size == 1) {
                    var3.isSingleTile = true;
                }

                /*if (super.field1206 != 0 && Client.cycle >= super.field1201 && Client.cycle < super.field1159) {
                    var3.overrideHue = super.field1203;
                    var3.overrideSaturation = super.field1185;
                    var3.overrideLuminance = super.field1165;
                    var3.overrideAmount = super.field1206;
                    var3.field2725 = (short)var4;
                } else {
                    var3.overrideAmount = 0;
                }*/
                return var3;
            }
        }
    }

    public boolean isVisible() {
        return desc != null;
    }
}
