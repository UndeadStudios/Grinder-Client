package com.runescape.cache.anim;

import com.runescape.cache.Frames;
import com.runescape.cache.Js5;
import com.runescape.cache.Skeleton;
import com.runescape.cache.anim.animaya.class134;
import com.runescape.collection.DualNode;
import com.runescape.collection.EvictingDualNodeHashTable;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class Animation extends DualNode {

    public static EvictingDualNodeHashTable cached = new EvictingDualNodeHashTable(64);
    public static EvictingDualNodeHashTable SequenceDefinition_cachedModel = new EvictingDualNodeHashTable(100);
    public static int anInt367;
    public int frameCount;
    public int frameIds[];
    public int chatFrameIds[];
    public int[] frameLengths;
    public int interleaveOrder[];
    public boolean stretches;
    public int forcedPriority;
    public int shield;
    public int weapon;
    public int maximumLoops;
    public boolean field2272;
    public int animatingPrecedence;
    public int priority;
    public int replayMode;
    public int[] soundsEffects;
    public int cachedModelId;
    public Map params;
    public int firstFrame;
    public int lastFrame;
    public boolean[] field2267;

    static boolean field2270 = false;

    private Animation() {
        cachedModelId = -1;
        firstFrame = 0;
        lastFrame = 0;
        frameCount = -1;
        stretches = false;
        forcedPriority = 5;
        shield = -1; //Removes shield
        weapon = -1; //Removes weapon
        maximumLoops = 99;
        animatingPrecedence = -1; //Stops character from moving
        priority = -1;
        replayMode = 2;
    }

    public static Animation getSequenceDefinition(int sequence) {
        Animation animation = (Animation) cached.get(sequence);

        if (animation == null) {
            byte[] data = Js5.configs.takeRecord(12, sequence);
            animation = new Animation();
            if (data != null) {
                animation.read(new Buffer(data));
            }
            animation.post(sequence);
            cached.put(animation, sequence);
        }

        return animation;
    }

    private void post(int sequence) {
        if(this.animatingPrecedence == -1) {
            if(this.interleaveOrder != null) {
                this.animatingPrecedence = 2;
            } else {
                this.animatingPrecedence = 0;
            }
        }

        if(this.priority == -1) {
            if(this.interleaveOrder != null) {
                this.priority = 2;
            } else {
                this.priority = 0;
            }
        }

        switch (sequence) {
            case 791:
            case 2140:
            case 4409:
            case 5054:
            case 741:
            case 2589:
            case 2591:
            case 769:
            case 1604:
            case 2586:
            case 3067:
                weapon = shield = 0;
                break;
            case 832:
                shield = 0;
                break;
        }
    }

    public boolean isCachedModelIdSet() {
        return cachedModelId >= 0;
    }

    public int duration() {
        return lastFrame - firstFrame;
    }
    
    private void read(Buffer buffer) {
        while (true) {
            int opcode = buffer.getUnsignedByte();
            
            if (opcode == 0) {
                return;
            }
            
            readNext(buffer, opcode);
        }
    }

    private void readNext(Buffer buffer, int opcode) {
        int var3;
        int var4;
        if(opcode == 1) {
            var3 = buffer.readUnsignedShort();
            this.frameLengths = new int[var3];

            for(var4 = 0; var4 < var3; ++var4) {
                this.frameLengths[var4] = buffer.readUnsignedShort();
            }

            this.frameIds = new int[var3];

            for(var4 = 0; var4 < var3; ++var4) {
                this.frameIds[var4] = buffer.readUnsignedShort();
            }

            for(var4 = 0; var4 < var3; ++var4) {
                this.frameIds[var4] += buffer.readUnsignedShort() << 16;
            }
        } else if(opcode == 2) {
            this.frameCount = buffer.readUnsignedShort();
        } else if(opcode == 3) {
            var3 = buffer.getUnsignedByte();
            this.interleaveOrder = new int[var3 + 1];

            for(var4 = 0; var4 < var3; ++var4) {
                this.interleaveOrder[var4] = buffer.getUnsignedByte();
            }

            this.interleaveOrder[var3] = 9999999;
        } else if(opcode == 4) {
            this.stretches = true;
        } else if(opcode == 5) {
            this.forcedPriority = buffer.getUnsignedByte();
        } else if(opcode == 6) {
            this.shield = buffer.readUnsignedShort();
        } else if(opcode == 7) {
            this.weapon = buffer.readUnsignedShort();
        } else if(opcode == 8) {
            this.maximumLoops = buffer.getUnsignedByte();
            this.field2272 = true;
        } else if(opcode == 9) {
            this.animatingPrecedence = buffer.getUnsignedByte();
        } else if(opcode == 10) {
            this.priority = buffer.getUnsignedByte();
        } else if(opcode == 11) {
            this.replayMode = buffer.getUnsignedByte();
        } else if(opcode == 12) {
            var3 = buffer.getUnsignedByte();
            this.chatFrameIds = new int[var3];

            for(var4 = 0; var4 < var3; ++var4) {
                this.chatFrameIds[var4] = buffer.readUnsignedShort();
            }

            for(var4 = 0; var4 < var3; ++var4) {
                this.chatFrameIds[var4] += buffer.readUnsignedShort() << 16;
            }
        } else if(opcode == 13) {
            var3 = buffer.getUnsignedByte();
            this.soundsEffects = new int[var3];

            for(var4 = 0; var4 < var3; ++var4) {
                this.soundsEffects[var4] = buffer.readMedium();
            }
        } else if (opcode == 14) {
            cachedModelId = buffer.readInt();
        } else if (opcode == 15) {
            var3 = buffer.readUnsignedShort();
            params = new HashMap();

            for (var4 = 0; var4 < var3; ++var4) {
                int var5 = buffer.readUnsignedShort();
                int var6 = buffer.readMedium();
                this.params.put(var5, var6);
            }
        } else if (opcode == 16) {
            firstFrame = buffer.readUnsignedShort();
            lastFrame = buffer.readUnsignedShort();
        } else if (opcode == 17) {
            this.field2267 = new boolean[256];

            for(var3 = 0; var3 < this.field2267.length; ++var3) {
                this.field2267[var3] = false;
            }

            var3 = buffer.getUnsignedByte();

            for(var4 = 0; var4 < var3; ++var4) {
                this.field2267[buffer.getUnsignedByte()] = true;
            }
        }
    }

    @Override
    public String toString() {
        return "Animation{" +
                "frameCount=" + frameCount +
                ", primaryFrames=" + Arrays.toString(frameIds) +
                ", secondaryFrames=" + Arrays.toString(chatFrameIds) +
                ", durations=" + Arrays.toString(frameLengths) +
                ", interleaveOrder=" + Arrays.toString(interleaveOrder) +
                ", stretches=" + stretches +
                ", forcedPriority=" + forcedPriority +
                ", playerOffhand=" + shield +
                ", playerMainhand=" + weapon +
                ", maximumLoops=" + maximumLoops +
                ", animatingPrecedence=" + animatingPrecedence +
                ", priority=" + priority +
                ", replayMode=" + replayMode +
                '}';
    }

    public Model applyTransformations(Model var1, int var2, Animation var3, int var4) {
        if (field2270 && !this.isCachedModelIdSet() && !var3.isCachedModelIdSet()) {
            return this.method4006(var1, var2, var3, var4);
        } else {
            Model var5 = var1.toSharedSequenceModel(false);
            boolean var6 = false;
            Frames var7 = null;
            Skeleton var8 = null;
            class134 var9;
            if (this.isCachedModelIdSet()) {
                var9 = this.method4016();
                if (var9 == null) {
                    return var5;
                }

                if (var3.isCachedModelIdSet() && field2267 == null) {
                    var5.method4603(var9, var2);
                    return var5;
                }

                var8 = var9.field1570;
                var5.method4639(var8, var9, var2, field2267, false, !var3.isCachedModelIdSet());
            } else {
                var2 = this.frameIds[var2];
                var7 = Frames.getFrames(var2 >> 16);
                var2 &= 65535;
                if (var7 == null) {
                    return var3.transformActorModel(var1, var4);
                }

                if (!var3.isCachedModelIdSet() && (this.interleaveOrder == null || var4 == -1)) {
                    var5.animate(var7, var2);
                    return var5;
                }

                if (this.interleaveOrder == null || var4 == -1) {
                    var5.animate(var7, var2);
                    return var5;
                }

                var6 = var3.isCachedModelIdSet();
                if (!var6) {
                    var5.method4617(var7, var2, this.interleaveOrder, false);
                }
            }

            if (var3.isCachedModelIdSet()) {
                var9 = var3.method4016();
                if (var9 == null) {
                    return var5;
                }

                if (var8 == null) {
                    var8 = var9.field1570;
                }

                var5.method4639(var8, var9, var4, this.field2267, true, true);
            } else {
                var4 = var3.frameIds[var4];
                Frames var10 = Frames.getFrames(var4 >> 16);
                var4 &= 65535;
                if (var10 == null) {
                    return this.transformActorModel(var1, var2);
                }

                var5.method4617(var10, var4, this.interleaveOrder, true);
            }

            if (var6 && var7 != null) {
                var5.method4617(var7, var2, this.interleaveOrder, false);
            }

            var5.resetBounds();
            return var5;
        }
    }


    Model method4006(Model var1, int var2, Animation var3, int var4) {
        var2 = this.frameIds[var2];
        Frames var5 = Frames.getFrames(var2 >> 16);
        var2 &= 65535;
        if (var5 == null) {
            return var3.transformActorModel(var1, var4);
        } else {
            var4 = var3.frameIds[var4];
            Frames var6 = Frames.getFrames(var4 >> 16);
            var4 &= 65535;
            Model var7;
            if (var6 == null) {
                var7 = var1.toSharedSequenceModel(!var5.hasAlphaTransform(var2));
                var7.animate(var5, var2);
                return var7;
            } else {
                var7 = var1.toSharedSequenceModel(!var5.hasAlphaTransform(var2) & !var6.hasAlphaTransform(var4));
                var7.animate2(var5, var2, var6, var4, this.interleaveOrder);
                return var7;
            }
        }
    }

    Model transformSpotAnimationModel(Model var1, int var2) {
        Model var4;
        if (!this.isCachedModelIdSet()) {
            var2 = this.frameIds[var2];
            Frames var5 = Frames.getFrames(var2 >> 16);
            var2 &= 65535;
            if (var5 == null) {
                return var1.toSharedSpotAnimationModel(true);
            } else {
                var4 = var1.toSharedSpotAnimationModel(!var5.hasAlphaTransform(var2));
                var4.animate(var5, var2);
                return var4;
            }
        } else {
            class134 var3 = method3458(this.cachedModelId);
            if (var3 == null) {
                return var1.toSharedSpotAnimationModel(true);
            } else {
                var4 = var1.toSharedSpotAnimationModel(!var3.method3043());
                var4.method4603(var3, var2);
                return var4;
            }
        }
    }

    public Model transformActorModel(Model var1, int var2) {
        Model var4;
        if (!this.isCachedModelIdSet()) {
            var2 = this.frameIds[var2];
            Frames var5 = Frames.getFrames(var2 >> 16);
            var2 &= 65535;
            if (var5 == null) {
                return var1.toSharedSequenceModel(true);
            } else {
                var4 = var1.toSharedSequenceModel(!var5.hasAlphaTransform(var2));
                var4.animate(var5, var2);
                return var4;
            }
        } else {
            class134 var3 = method3458(this.cachedModelId);
            if (var3 == null) {
                return var1.toSharedSequenceModel(true);
            } else {
                var4 = var1.toSharedSequenceModel(!var3.method3043());
                var4.method4603(var3, var2);
                return var4;
            }
        }
    }

    public    Model transformObjectModel(Model var1, int var2, int var3) {
        Model var5;
        if (!this.isCachedModelIdSet()) {
            var2 = this.frameIds[var2];
            Frames var6 = Frames.getFrames(var2 >> 16);
            var2 &= 65535;
            if (var6 == null) {
                return var1.toSharedSequenceModel(true);
            } else {
                var5 = var1.toSharedSequenceModel(!var6.hasAlphaTransform(var2));
                var3 &= 3;
                if (var3 == 1) {
                    var5.rotateY270Ccw();
                } else if (var3 == 2) {
                    var5.rotateY180();
                } else if (var3 == 3) {
                    var5.rotateY90Ccw();
                }

                var5.animate(var6, var2);
                if (var3 == 1) {
                    var5.rotateY90Ccw();
                } else if (var3 == 2) {
                    var5.rotateY180();
                } else if (var3 == 3) {
                    var5.rotateY270Ccw();
                }

                return var5;
            }
        } else {
            class134 var4 = method3458(this.cachedModelId);
            if (var4 == null) {
                return var1.toSharedSequenceModel(true);
            } else {
                var5 = var1.toSharedSequenceModel(!var4.method3043());
                var3 &= 3;
                if (var3 == 1) {
                    var5.rotateY270Ccw();
                } else if (var3 == 2) {
                    var5.rotateY180();
                } else if (var3 == 3) {
                    var5.rotateY90Ccw();
                }

                var5.method4603(var4, var2);
                if (var3 == 1) {
                    var5.rotateY90Ccw();
                } else if (var3 == 2) {
                    var5.rotateY180();
                } else if (var3 == 3) {
                    var5.rotateY270Ccw();
                }

                return var5;
            }
        }
    }

    public Model transformWidgetModel(Model var1, int var2) {
        if (!this.isCachedModelIdSet()) {
            int var3 = this.frameIds[var2];
            Frames var4 = Frames.getFrames(var3 >> 16);
            var3 &= 65535;
            if (var4 == null) {
                return var1.toSharedSequenceModel(true);
            } else {
                Frames var5 = null;
                int var6 = 0;
                if (this.chatFrameIds != null && var2 < this.chatFrameIds.length) {
                    var6 = this.chatFrameIds[var2];
                    var5 = Frames.getFrames(var6 >> 16);
                    var6 &= 65535;
                }

                Model var7;
                if (var5 != null && var6 != 65535) {
                    var7 = var1.toSharedSequenceModel(!var4.hasAlphaTransform(var3) & !var5.hasAlphaTransform(var6));
                    var7.animate(var4, var3);
                    var7.animate(var5, var6);
                    return var7;
                } else {
                    var7 = var1.toSharedSequenceModel(!var4.hasAlphaTransform(var3));
                    var7.animate(var4, var3);
                    return var7;
                }
            }
        } else {
            return this.transformActorModel(var1, var2);
        }
    }

    public int method4015() {
        return this.lastFrame - this.firstFrame;
    }

    class134 method4016() {
        return this.isCachedModelIdSet() ? method3458(this.cachedModelId) : null;
    }

    static class134 method3458(int var0) {
        class134 var3 = (class134)SequenceDefinition_cachedModel.get((long)var0);
        class134 var2;
        class134 var4;
        boolean var7;
        byte[] var8;
        int var9;
        byte[] var10;
        if (var3 != null) {
            var2 = var3;
        } else {
            var7 = true;
            var8 = Js5.skins.getRecord(var0 >> 16 & '\uffff', var0 & '\uffff');
            if (var8 == null) {
                var7 = false;
                var4 = null;
            } else {
                var9 = (var8[1] & 255) << 8 | var8[2] & 255;
                var10 = Js5.skeletons.getRecord(var9, 0);
                if (var10 == null) {
                    var7 = false;
                }

                if (!var7) {
                    var4 = null;
                } else {
                    try {
                        var4 = new class134(var0, false);
                    } catch (Exception var13) {
                        var4 = null;
                    }
                }
            }

            if (var4 != null) {
                SequenceDefinition_cachedModel.put(var4, (long)var0);
            }

            var2 = var4;
        }

        int var1;
        if (var2 == null) {
            var1 = 2;
        } else {
            var1 = var2.method3044() ? 0 : 1;
        }

        if (var1 != 0) {
            return null;
        } else {
            var3 = (class134)SequenceDefinition_cachedModel.get((long)var0);
            if (var3 != null) {
                var2 = var3;
            } else {
                var7 = true;
                var8 = Js5.skins.getRecord(var0 >> 16 & '\uffff', var0 & '\uffff');
                if (var8 == null) {
                    var7 = false;
                    var4 = null;
                } else {
                    var9 = (var8[1] & 255) << 8 | var8[2] & 255;
                    var10 = Js5.skeletons.getRecord(var9, 0);
                    if (var10 == null) {
                        var7 = false;
                    }

                    if (!var7) {
                        var4 = null;
                    } else {
                        try {
                            var4 = new class134(var0, false);
                        } catch (Exception var12) {
                            var4 = null;
                        }
                    }
                }

                if (var4 != null) {
                    Animation.SequenceDefinition_cachedModel.put(var4, (long)var0);
                }

                var2 = var4;
            }

            return var2;
        }
    }
}