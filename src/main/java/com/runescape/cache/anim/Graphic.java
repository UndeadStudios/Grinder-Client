package com.runescape.cache.anim;

import com.runescape.cache.Js5;
import com.runescape.cache.ModelData;
import com.runescape.collection.DualNode;
import com.runescape.collection.EvictingDualNodeHashTable;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;

import java.util.Arrays;

public final class Graphic extends DualNode {

    public static EvictingDualNodeHashTable SpotAnimationDefinition_cached = new EvictingDualNodeHashTable(64);
    public static Graphic cache[];
    public static EvictingDualNodeHashTable SpotAnimationDefinition_cachedModels = new EvictingDualNodeHashTable(30);
    private short[] recolorFrom;
    private short[] recolorTo;
    private short[] retextureFrom;
    private short[] retextureTo;
    public Animation animationSequence;
    public int widthScale;
    public int heightScale;
    public int orientation;
    public int ambient;
    public int contrast;
    private int id;
    private int modelId;
    public int sequence;

    private Graphic() {
        sequence = -1;
        widthScale = 128;
        heightScale = 128;
    }

    public static Graphic getSpotAnimationDefinition(int spotAnim) {
        Graphic graphic = (Graphic) SpotAnimationDefinition_cached.get(spotAnim);

        if (graphic == null) {
            byte[] data = Js5.configs.takeRecord(13, spotAnim);
            graphic = new Graphic();
            graphic.id = spotAnim;
            if (data != null) {
                graphic.decode(new Buffer(data));
            }
            SpotAnimationDefinition_cached.put(graphic, spotAnim);
        }

        return graphic;
    }

    public void decode(Buffer buffer){
        while (true){
            int opcode = buffer.readUnsignedByte();
            if(opcode == 0)
                return;
            decodeNext(buffer, opcode);
        }
    }

    public void decodeNext(Buffer buffer, int opcode) {
        if (opcode == 1) {
            modelId = buffer.readUnsignedShort();
        } else if (opcode == 2) {
            sequence = buffer.readUnsignedShort();
            animationSequence = Animation.getSequenceDefinition(sequence);
        } else if (opcode == 4) {
            widthScale = buffer.readUnsignedShort();
        } else if (opcode == 5) {
            heightScale = buffer.readUnsignedShort();
        } else if (opcode == 6) {
            orientation = buffer.readUnsignedShort();
        } else if (opcode == 7) {
            ambient = buffer.readUnsignedByte();
        } else if (opcode == 8) {
            contrast = buffer.readUnsignedByte();
        } else {
            int index;
            int length;
            if (opcode == 40) {
                length = buffer.readUnsignedByte();
                recolorFrom = new short[length];
                recolorTo = new short[length];
                for (index = 0; index < length; index++) {
                    recolorFrom[index] = (short) buffer.readUnsignedShort();
                    recolorTo[index] = (short) buffer.readUnsignedShort();
                }
            } else if (opcode == 41) {
                length = buffer.readUnsignedByte();
                retextureFrom = new short[length];
                retextureTo = new short[length];
                for (index = 0; index < length; ++index) {
                    retextureFrom[index] = (short) buffer.readUnsignedShort();
                    retextureTo[index] = (short) buffer.readUnsignedShort();
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Graphic{" +
                "originalModelColours=" + Arrays.toString(recolorFrom) +
                ", modifiedModelColours=" + Arrays.toString(recolorTo) +
                ", retextureToFind=" + Arrays.toString(retextureFrom) +
                ", retextureToReplace=" + Arrays.toString(retextureTo) +
                ", animationSequence=" + animationSequence +
                ", widthScale=" + widthScale +
                ", heightScale=" + heightScale +
                ", rotation=" + orientation +
                ", modelBrightness=" + ambient +
                ", modelShadow=" + contrast +
                ", id=" + id +
                ", modelId=" + modelId +
                ", sequence=" + sequence +
                '}';
    }

    public final Model getModel(int var1) {
        Model var2 = (Model)SpotAnimationDefinition_cachedModels.get((long)this.id);
        if (var2 == null) {
            ModelData var3 = ModelData.ModelData_get(Js5.models, modelId, 0);
            if (var3 == null) {
                return null;
            }

            int var4;
            if (this.recolorFrom != null) {
                for(var4 = 0; var4 < this.recolorFrom.length; ++var4) {
                    var3.recolor((short) this.recolorFrom[var4], (short) this.recolorTo[var4]);
                }
            }

            if (this.retextureFrom != null) {
                for(var4 = 0; var4 < this.retextureFrom.length; ++var4) {
                    var3.retexture((short) this.retextureFrom[var4], (short) this.retextureTo[var4]);
                }
            }

            var2 = var3.toModel(this.ambient + 64, this.contrast + 850, -30, -50, -30);
            SpotAnimationDefinition_cachedModels.put(var2, (long)this.id);
        }

        Model var5;
        if (this.sequence != -1 && var1 != -1) {
            var5 = Animation.getSequenceDefinition(this.sequence).transformSpotAnimationModel(var2, var1);
        } else {
            var5 = var2.toSharedSpotAnimationModel(true);
        }

        if (this.widthScale != 128 || this.heightScale != 128) {
            var5.scale(this.widthScale, this.heightScale, this.widthScale);
        }

        if (this.orientation != 0) {
            if (this.orientation == 90) {
                var5.rotateY90Ccw();
            }

            if (this.orientation == 180) {
                var5.rotateY90Ccw();
                var5.rotateY90Ccw();
            }

            if (this.orientation == 270) {
                var5.rotateY90Ccw();
                var5.rotateY90Ccw();
                var5.rotateY90Ccw();
            }
        }

        return var5;
    }

}
