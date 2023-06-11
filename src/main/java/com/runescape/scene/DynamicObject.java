package com.runescape.scene;

import com.runescape.Client;
import com.runescape.cache.anim.Animation;
import com.runescape.cache.config.VariableBits;
import com.runescape.cache.def.ObjectDefinition;
import com.runescape.cache.definition.OSObjectDefinition;
import com.runescape.entity.Renderable;
import com.runescape.entity.model.Model;
import com.grinder.client.ClientCompanion;

public final class DynamicObject extends Renderable {

    public static Client clientInstance;
    private int[] OLD_transforms;
    private int OLD_transformConfigId;
    private int OLD_transformVarbit;
    private final int anInt1603;
    private final int anInt1604;
    private final int anInt1605;
    private final int anInt1606;
    private final int id;
    private final int type;
    private final int orientation;
    private int frame;
    private Animation sequenceDefinition;
    private int cycleStart;
    private final int x, y;

    public DynamicObject(int id, int orientation, int type, int x, int y, int l, int i1, int j1, int k1, int animationID, boolean flag) {
        this.id = id;
        this.type = type;
        this.orientation = orientation;
        this.x = x;
        this.y = y;
        anInt1603 = j1;
        anInt1604 = l;
        anInt1605 = i1;
        anInt1606 = k1;
        if (animationID != -1) {
            sequenceDefinition = Animation.getSequenceDefinition(animationID);
            frame = 0;
            cycleStart = Client.tick;
//                if(this.sequenceDefinition.__t == 0 && var9 != null && var9 instanceof DynamicObject) {
//                    DynamicObject var10 = (DynamicObject)var9;
//                    if(this.sequenceDefinition == var10.sequenceDefinition) {
//                        this.frame = var10.frame;
//                        this.cycleStart = var10.cycleStart;
//                        return;
//                    }
//                }
            if (flag && sequenceDefinition.frameCount != -1) {
                if (!sequenceDefinition.isCachedModelIdSet()) {
                    frame = (int) (Math.random() * (double) sequenceDefinition.frameIds.length);
                    cycleStart -= (int) (Math.random() * (double) sequenceDefinition.frameLengths[frame]);
                } else {
                    frame = (int)(Math.random() * (double)this.sequenceDefinition.duration());
                }
            }
        }
    }


    public static int get_object_x(long id) {
        if(OSObjectDefinition.USE_OSRS)
            return ViewportMouse.getX(id);
        else
            return (int) id & 0x7f;
    }

    public static int get_object_y(long id) {
        if(OSObjectDefinition.USE_OSRS)
            return ViewportMouse.getY(id);
        else
            return (int) (id >> 7) & 0x7f;
    }

    public static int get_object_opcode(long id) {
        if(OSObjectDefinition.USE_OSRS)
            return ViewportMouse.getOpcode(id);
        else
            return (int) id >> 29 & 0x3;
    }

    public static int get_object_key(long id) {
        if(OSObjectDefinition.USE_OSRS)
            return (int)(id >>> 17 & 0xffffffffL);
        else
            return (int) (id >> 32) & 0x7fffffff;
    }
    public static int get_object_index(long tag) {
        return (int) (tag >> 14 & 0x7fff);
    }

    public static int get_object_type(long id) {
        return (int) id >> 14 & 0x1f;
    }

    public static int get_object_orientation(long id) {
        return (int) id >> 20 & 0x3;
    }

    public Model getRotatedModel() {
        int var2;
        if (sequenceDefinition != null) {
            int var1 = Client.tick - cycleStart;
            if (var1 > 100 && sequenceDefinition.frameCount > 0) {
                var1 = 100;
            }
            if (sequenceDefinition.isCachedModelIdSet()) {
                var2 = this.sequenceDefinition.duration(); // L: 68
                this.frame += var1;
                var1 = 0;
                if (this.frame >= var2) {
                    this.frame = var2 - this.sequenceDefinition.frameCount;
                    if (this.frame < 0 || this.frame > var2) {
                        this.sequenceDefinition = null;
                    }
                }
            } else {
                label78: {
                    do {
                        do {
                            if (var1 <= this.sequenceDefinition.frameLengths[this.frame]) { // L: 55
                                break label78;
                            }

                            var1 -= this.sequenceDefinition.frameLengths[this.frame]; // L: 56
                            ++this.frame;
                        } while(this.frame < this.sequenceDefinition.frameIds.length); // L: 58

                        this.frame -= this.sequenceDefinition.frameCount;
                    } while(this.frame >= 0 && this.frame < this.sequenceDefinition.frameIds.length); // L: 60

                    this.sequenceDefinition = null;
                }
            }
            cycleStart = Client.tick - var1;
        }

        ObjectDefinition var12 = ObjectDefinition.lookup(id);

        if (var12.transforms != null) {
            var12 = var12.transform();
        }

        if (var12 == null) {
            return null;
        } else {
            int var3;
            if (this.orientation != 1 && this.orientation != 3) {
                var2 = var12.sizeX;
                var3 = var12.sizeY;
            } else {
                var2 = var12.sizeY;
                var3 = var12.sizeX;
            }

            int var4 = (var2 >> 1) + this.x;
            int var5 = (var2 + 1 >> 1) + this.x;
            int var6 = (var3 >> 1) + this.y;
            int var7 = (var3 + 1 >> 1) + this.y;
            int[][] var8 = Client.Tiles_heights[/*this.plane*/Client.plane]; // TODO: correct the plane
            int var9 = var8[var5][var7] + var8[var4][var7] + var8[var4][var6] + var8[var5][var6] >> 2;
            int var10 = (this.x << 7) + (var2 << 6);
            int var11 = (this.y << 7) + (var3 << 6);
            return var12.getModelDynamic(this.type, this.orientation, var8, var10, var9, var11, this.sequenceDefinition, this.frame);
        }
    }
}