package com.runescape.entity.model;

import com.grinder.client.ClientCompanion;
import com.runescape.Client;
import com.runescape.cache.FileArchive;
import com.runescape.cache.Js5;
import com.runescape.cache.ModelData;
import com.runescape.cache.OsCache;
import com.runescape.cache.graphics.widget.Widget;
import com.runescape.collection.DualNode;
import com.runescape.collection.EvictingDualNodeHashTable;
import com.runescape.io.Buffer;

public final class IdentityKit extends DualNode {

    private static boolean JS5 = false; // need server side adjustments

    public static int length;

    static EvictingDualNodeHashTable KitDefinition_cached = new EvictingDualNodeHashTable(64);

    public static IdentityKit kits[];
    private int[] recolorFrom;
    private int[] recolorTo;
    private short[] retextureFrom;
    private short[] retextureTo;
    private final int[] headModels = {-1, -1, -1, -1, -1};
    public int bodyPartId = -1;
    public boolean validStyle;
    private int[] bodyModels;

    public static void init(FileArchive archive) {
        if (JS5) {
            return;
        }

        Buffer buffer = new Buffer(archive.readFile("idk.dat"));

        length = buffer.readUShort();
        if (kits == null) {
            kits = new IdentityKit[length];
        }

        for (int id = 0; id < length; id++) {

            if (kits[id] == null) {
                kits[id] = new IdentityKit();
            }

            IdentityKit kit = kits[id];

            kit.decode(buffer);
            kit.recolorFrom = new int[]{55232}; // ??
            kit.recolorTo = new int[]{6798};
        }

    }

    private void decode(Buffer buffer) {
        while (true) {
            int var3;
            int var4;
            int opcode = buffer.readUnsignedByte();
            if (opcode == 0) {
                return;
            } else if (opcode == 1) {
                bodyPartId = buffer.readUnsignedByte();
            } else if (opcode == 2) {
                int count = buffer.readUnsignedByte();
                bodyModels = new int[count];
                for (int part = 0; part < count; part++) {
                    bodyModels[part] = buffer.readUShort();
                }
            } else if (opcode == 3) {
                validStyle = true;
            } else if (!JS5 && opcode >= 40 && opcode < 50) {
                if (recolorFrom == null)
                    recolorFrom = new int[6];
                recolorFrom[opcode - 40] = buffer.readUShort();
            } else if (!JS5 && opcode >= 50 && opcode < 60) {
                if (recolorTo == null)
                    this.recolorTo = new int[6];
                recolorTo[opcode - 50] = buffer.readUShort();
            } else if (JS5 && opcode == 40) {
                var3 = buffer.readUnsignedByte();
                this.recolorFrom = new int[var3];
                this.recolorTo = new int[var3];

                for(var4 = 0; var4 < var3; ++var4) {
                    this.recolorFrom[var4] = (short)buffer.readUnsignedShort();
                    this.recolorTo[var4] = (short)buffer.readUnsignedShort();
                }
            } else if (JS5 && opcode == 41) {
                var3 = buffer.readUnsignedByte();
                this.retextureFrom = new short[var3];
                this.retextureTo = new short[var3];

                for(var4 = 0; var4 < var3; ++var4) {
                    this.retextureFrom[var4] = (short)buffer.readUnsignedShort();
                    this.retextureTo[var4] = (short)buffer.readUnsignedShort();
                }
            } else if (opcode >= 60 && opcode < 70) {
                headModels[opcode - 60] = buffer.readUShort();
            } else {
                System.out.println("Error unrecognised config code: " + opcode);
            }
        }
    }

    public static IdentityKit KitDefinition_get(int var0) {
        if (!JS5) {
            return kits[var0];
        }
        IdentityKit var1 = (IdentityKit)KitDefinition_cached.get((long)var0);
        if (var1 != null) {
            return var1;
        } else {
            byte[] var2 = OsCache.indexCache2.takeRecord(3, var0);
            var1 = new IdentityKit();
            if (var2 != null) {
                var1.decode(new Buffer(var2));
            }

            KitDefinition_cached.put(var1, (long)var0);
            return var1;
        }
    }

    public boolean ready() {
        if (this.bodyModels == null) {
            return true;
        } else {
            boolean var1 = true;

            for(int var2 = 0; var2 < this.bodyModels.length; ++var2) {
                if (!OsCache.indexCache7.tryLoadRecord(this.bodyModels[var2], 0)) {
                    var1 = false;
                }
            }

            return var1;
        }
    }

    public ModelData getModelData() {
        if (this.bodyModels == null) {
            return null;
        } else {
            ModelData[] var1 = new ModelData[this.bodyModels.length];

            for(int var2 = 0; var2 < this.bodyModels.length; ++var2) {
                var1[var2] = ModelData.ModelData_get(OsCache.indexCache7, this.bodyModels[var2], 0);
            }

            ModelData var4;
            if (var1.length == 1) {
                var4 = var1[0];
            } else {
                var4 = new ModelData(var1, var1.length);
            }

            int var3;
            if (this.recolorFrom != null) {
                for(var3 = 0; var3 < this.recolorFrom.length; ++var3) {
                    var4.recolor((short) this.recolorFrom[var3], (short) this.recolorTo[var3]);
                }
            }

            if (this.retextureFrom != null) {
                for(var3 = 0; var3 < this.retextureFrom.length; ++var3) {
                    var4.retexture(this.retextureFrom[var3], this.retextureTo[var3]);
                }
            }

            return var4;
        }
    }

    public boolean headLoaded() {
        boolean var1 = true;

        for(int var2 = 0; var2 < 5; ++var2) {
            if (this.headModels[var2] != -1 && !Js5.models.tryLoadRecord(this.headModels[var2], 0)) {
                var1 = false;
            }
        }

        return var1;
    }

    public ModelData getKitDefinitionModels() {
        ModelData[] var1 = new ModelData[5];
        int var2 = 0;

        for(int var3 = 0; var3 < 5; ++var3) {
            if (this.headModels[var3] != -1) {
                var1[var2++] = ModelData.ModelData_get(Js5.models, this.headModels[var3], 0);
            }
        }

        ModelData var5 = new ModelData(var1, var2);
        int var4;
        if (this.recolorFrom != null) {
            for(var4 = 0; var4 < this.recolorFrom.length; ++var4) {
                var5.recolor((short) this.recolorFrom[var4], (short) this.recolorTo[var4]);
            }
        }

        if (this.retextureFrom != null) {
            for(var4 = 0; var4 < this.retextureFrom.length; ++var4) {
                var5.retexture(this.retextureFrom[var4], this.retextureTo[var4]);
            }
        }

        return var5;
    }

    // KITS


    public static void nullifyIdentityKits() {
        kits = null;
    }

    public static boolean drawCharacterDesignModel(Client client, Widget widget) {
        widget.modelXAngle = 150;
        widget.modelYAngle = (int) (Math.sin((double) Client.tick / 40D) * 256D) & 0x7ff;
        //if (aBoolean1031) {
        for (int k1 = 0; k1 < 7; k1++) {
            int l1 = client.anIntArray1065[k1];
            if (l1 >= 0 && !KitDefinition_get(l1).ready())
                return true;
        }

        client.aBoolean1031 = false;
        ModelData[] data = new ModelData[7];
        int i2 = 0;
        for (int j2 = 0; j2 < 7; j2++) {
            int k2 = client.anIntArray1065[j2];
            if (k2 >= 0)
                data[i2++] = KitDefinition_get(k2).getModelData();
        }

        ModelData var20 = new ModelData(data, 7);

        for (int var13 = 0; var13 < 5; var13++)
            if (client.characterDesignColours[var13] != 0) {
                var20.recolor((short) ClientCompanion.PLAYER_BODY_RECOLOURS[var13][0],
                        (short) ClientCompanion.PLAYER_BODY_RECOLOURS[var13][client.characterDesignColours[var13]]);
                if (var13 == 1)
                    var20.recolor((short) ClientCompanion.anIntArray1204[0], (short) ClientCompanion.anIntArray1204[client.characterDesignColours[var13]]);
            }

        Model model = var20.toModel(64, 850, -30, -50, -30);
        model.calculateBoundsCylinder();
        model.isSingleTile = true;
        widget.defaultMediaType = 5;
        widget.defaultMedia = 0;
        Widget.method208(client.aBoolean994, model);
        return false;
    }

    public static void changeCharacterGender(Client client) {
        client.aBoolean1031 = true;
        for (int j = 0; j < 7; j++) {
            client.anIntArray1065[j] = -1;
            for (int k = 0; k < getTotal(); k++) {
                if ((JS5 && !KitDefinition_get(k).validStyle || !JS5 && KitDefinition_get(k).validStyle) || KitDefinition_get(k).bodyPartId != j + (client.maleCharacter ? 0 : 7))
                    continue;
                client.anIntArray1065[j] = k;
                break;
            }
        }
    }

    public static int getTotal() {
        if (JS5) {
            return Js5.configs.getRecordLength(3);
        }

        return length;
    }

    public static void selectKitBodyPart(Client client, int k, int j1) {
        int i2 = client.anIntArray1065[k];
        if (i2 != -1) {
            do {
                if (j1 == 0 && --i2 < 0)
                    i2 = getTotal() - 1;
                if (j1 == 1 && ++i2 >= getTotal())
                    i2 = 0;
            } while ((JS5 && !KitDefinition_get(i2).validStyle || !JS5 && KitDefinition_get(i2).validStyle)
                    || KitDefinition_get(i2).bodyPartId != k + (client.maleCharacter ? 0 : 7));
            client.anIntArray1065[k] = i2;
            client.aBoolean1031 = true;
        }
    }

}
