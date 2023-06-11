package com.runescape.scene;

import com.grinder.net.ResourceProvider;
import com.runescape.Client;
import com.runescape.cache.def.*;
import com.runescape.cache.definition.OSObjectDefinition;
import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.Renderable;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;
import com.runescape.scene.object.ObjectSound;
import com.runescape.util.ChunkUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class MapRegion {

    public static final int BRIDGE_TILE = 2;
    private static final Set<Integer> OLD_REGIONS = new HashSet<>(Arrays.asList(
            13117, 6722, 10039, 10388, 8521, 12848, 10568, 7994, 8250, 7993, 8249, 10314, 12953, 12342, 12343,
            12085, 12341, 12086, 10056, 11928, 10057, 11569, 10044, 14235, 11837, 12861, 11679, 13357, 11343, 14638,
            13209, 10537, 11304, 11816, 11328, 12616, 10558, 9275, 9886, 13133, 13641, 13642, 9512, 6965, 9000, 6966,
            8488, 9005, 5168, 5166, 5162, 5423, 4912, 4911, 5422, 5167, 11842, 4910, 5424, 6223, 11317, 12701, 13886
    ));
    private static final int COSINE_VERTICES[] = {1, 0, -1, 0};
    private static final int anIntArray140[] = {16, 32, 64, 128};
    private static final int SINE_VERTICIES[] = {0, -1, 0, 1};
    private static final int anIntArray152[] = {1, 2, 4, 8};
    private static int rndHue = (int)(Math.random() * 17.0) - 8;
    private static int rndLightness = (int)(Math.random() * 33.0) - 16;
    private static final int BLOCKED_TILE = 1;
    private static final int FORCE_LOWEST_PLANE = 8;
    public static int lastPlane;
    public static int Tiles_minPlane = 99;
    public static boolean isLowDetail = true;
    private final int[] hues;
    private final int[] saturations;
    private final int[] luminances;
    private final int[] chromas;
    private final int[] anIntArray128;
    private static int[][][] tileHeights;
    public final byte[][][] overlays;
    private final byte[][][] shading;
    private final int[][][] anIntArrayArrayArray135;
    private final byte[][][] overlayTypes;
    private final int[][] tileLighting;
    public final byte[][][] underlays;
    private final int regionSizeX;
    private final int regionSizeY;
    private final byte[][][] overlayOrientations;
    private final byte[][][] Tiles_renderFlags;

    public MapRegion(byte[][][] fileFlags, int[][][] tileHeights) {
        Tiles_minPlane = 99;
        regionSizeX = 104;
        regionSizeY = 104;
        MapRegion.tileHeights = tileHeights;
        this.Tiles_renderFlags = fileFlags;
        underlays = new byte[4][regionSizeX][regionSizeY];
        overlays = new byte[4][regionSizeX][regionSizeY];
        overlayTypes = new byte[4][regionSizeX][regionSizeY];
        overlayOrientations = new byte[4][regionSizeX][regionSizeY];
        anIntArrayArrayArray135 = new int[4][regionSizeX + 1][regionSizeY + 1];
        shading = new byte[4][regionSizeX + 1][regionSizeY + 1];
        tileLighting = new int[regionSizeX + 1][regionSizeY + 1];
        hues = new int[regionSizeY];
        saturations = new int[regionSizeY];
        luminances = new int[regionSizeY];
        chromas = new int[regionSizeY];
        anIntArray128 = new int[regionSizeY];
    }

    private static int calculateNoise(int x, int y) {
        int k = x + y * 57;
        k = k << 13 ^ k;
        int l = k * (k * k * 15731 + 0xc0ae5) + 0x5208dd0d & 0x7fffffff;
        return l >> 19 & 0xff;
    }

    private static int calculateVertexHeight(int i, int j) {
        int mapHeight = (interpolatedNoise(i + 45365, j + 0x16713, 4) - 128) + (interpolatedNoise(i + 10294, j + 37821, 2) - 128 >> 1) + (interpolatedNoise(i, j, 1) - 128 >> 2);
        mapHeight = (int) ((double) mapHeight * 0.29999999999999999D) + 35;
        if (mapHeight < 10) {
            mapHeight = 10;
        } else if (mapHeight > 60) {
            mapHeight = 60;
        }
        return mapHeight;
    }

    public static void passiveRequestGameObjectModels(Buffer buffer, ResourceProvider resourceProvider) {
        label0:
        {
            int gameObjectId = -1;
            do {
                int gameObjectIdOffset = buffer.readUnsignedIntSmartShortCompat();

                if (gameObjectIdOffset == 0) {
                    break label0;
                }
                gameObjectId += gameObjectIdOffset;

                ObjectDefinition objectDefinition = ObjectDefinition.lookup(gameObjectId);
                objectDefinition.needsModelFiles();
                do {
                    int terminate = buffer.readUnsignedShortSmart();

                    if (terminate == 0) {
                        break;
                    }
                    buffer.readUnsignedByte();
                } while (true);
            } while (true);
        }
    }

    private static int interpolatedNoise(int x, int y, int frequencyReciprocal) {
        int l = x / frequencyReciprocal;
        int i1 = x & frequencyReciprocal - 1;
        int j1 = y / frequencyReciprocal;
        int k1 = y & frequencyReciprocal - 1;
        int l1 = smoothNoise(l, j1);
        int i2 = smoothNoise(l + 1, j1);
        int j2 = smoothNoise(l, j1 + 1);
        int k2 = smoothNoise(l + 1, j1 + 1);
        int l2 = interpolate(l1, i2, i1, frequencyReciprocal);
        int i3 = interpolate(j2, k2, i1, frequencyReciprocal);
        return interpolate(l2, i3, k1, frequencyReciprocal);
    }

    public static boolean modelReady(int objectId, int type) {

        if(type == 11)
            type = 10;
        if(type >= 5 && type <= 8)
            type = 4;

        if(OSObjectDefinition.USE_OSRS){
            OSObjectDefinition osObjectDefinition = OSObjectDefinition.lookup(objectId);
            return osObjectDefinition.__u_421(type);
        }

        ObjectDefinition class46 = ObjectDefinition.lookup(objectId);
        return class46.modelsReady(type);
    }

    private static int interpolate(int a, int b, int angle, int frequencyReciprocal) {
        int cosine = 0x10000 - Rasterizer3D.Rasterizer3D_cosine[(angle * 1024) / frequencyReciprocal] >> 1;
        return (a * (0x10000 - cosine) >> 16) + (b * cosine >> 16);
    }

    private static int smoothNoise(int x, int y) {
        int corners = calculateNoise(x - 1, y - 1) + calculateNoise(x + 1, y - 1) + calculateNoise(x - 1, y + 1) + calculateNoise(x + 1, y + 1);
        int sides = calculateNoise(x - 1, y) + calculateNoise(x + 1, y) + calculateNoise(x, y - 1) + calculateNoise(x, y + 1);
        int center = calculateNoise(x, y);
        return corners / 16 + sides / 8 + center / 4;
    }

    private static int method187(int i, int j) {
        if (i == -1)
            return Rasterizer3D.MAX_HSL_COLOR_VALUE;
        j = (j * (i & 0x7f)) / 128;
        if (j < 2)
            j = 2;
        else if (j > 126)
            j = 126;
        return (i & 0xff80) + j;
    }

    public static void placeObject(int x, int y, int plane, int z, int orientation, int type, int id, int[][][] heights, SceneGraph scene, CollisionMap collisionMap) {
        int center = heights[plane][x][y];
        int east = heights[plane][x + 1][y];
        int northEast = heights[plane][x + 1][y + 1];
        int north = heights[plane][x][y + 1];
        int mean = center + east + northEast + north >> 2;


        long tag;
        int config;
        final Object reference;
        final ObjectSound objectSound;
        final int animationId;
        final int decorDisplacement;
        final int[] transforms;
        final boolean isInteractive;
        final boolean obstructsGround;
        final boolean blockTile;
        final boolean solid;
        final boolean clipped;
        final boolean modelClipped;
        final boolean impenetrable;
        final boolean clipType_contouredGround;

        int sizeX, sizeY;

        if(OSObjectDefinition.USE_OSRS){
            OSObjectDefinition definition = OSObjectDefinition.lookup(id);
            reference = definition;
            animationId = definition.animationId;
            decorDisplacement = definition.decorDisplacement;
            transforms = definition.transforms;
            isInteractive = !(definition.interactiveState != 0 || definition.interactType == 1);
            obstructsGround = definition.obstructsGround;
            blockTile = definition.interactType == 1;
            impenetrable = definition.impenetrable;
            clipType_contouredGround = false;
            clipped = definition.clipped;
            modelClipped = definition.modelClipped;
            solid = definition.interactType != 0;
            sizeX = definition.sizeX;
            sizeY = definition.sizeY;

            tag = ViewportMouse.toTag(x, y, 2, definition.interactiveState == 0, id);
            config = type + (orientation << 6);

            if(definition.supportItems == 1) {
                config += 256;
            }
            int var23;
            int var24;
        } else {
            ObjectDefinition definition = ObjectDefinition.lookup(id);
            reference = definition;
            animationId = definition.animationId;
            decorDisplacement = definition.decorDisplacement;
            transforms = definition.transforms;
            isInteractive = definition.isInteractive;
            obstructsGround = definition.obstructsGround;
            impenetrable = definition.impenetrable;
            blockTile = definition.solid && isInteractive;
            modelClipped = definition.modelClipped;
            clipped = definition.clipped;
            sizeX = definition.sizeX;
            sizeY = definition.sizeY;
            solid = definition.solid;
            tag = orientation << 20 | type << 14 | (y << 7 | x) + 0x40000000;
            if (!definition.isInteractive)
                tag |= ~0x7fffffffffffffffL;
            if(definition.supportItems == 1)
                tag |= 0x400000L;
            tag |= (long) id << 32;
            config = ((orientation << 6) + type);
            if(definition.supportItems == 1) {
                config += 256;
            }
        }

        int var17 = (x << 7) + (sizeX << 6);
        int var18 = (y << 7) + (sizeY << 6);
        int[][] tilesHeight = tileHeights[z];

        if (type == 22) {
            Object obj;
            if (animationId == -1 && transforms == null)
                obj = getObjectModel(22, orientation, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj = new DynamicObject(id, orientation, 22, x, y, east, northEast, center, north, animationId, true);
            scene.newFloorDecoration(x, y, z, mean, config, ((Renderable) (obj)), tag);
            if (solid && isInteractive)
                collisionMap.block(x, y);
            return;
        }
        if (type == 10 || type == 11) {
            Object obj1;
            if (animationId == -1 && transforms == null)
                obj1 =  getObjectModel(10, orientation, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj1 = new DynamicObject(id, orientation, 10, x, y, east, northEast, center, north, animationId, true);
            if (obj1 != null) {
                int k4;
                int i5;
                if (orientation == 1 || orientation == 3) {
                    k4 = sizeY;
                    i5 = sizeX;
                } else {
                    k4 = sizeX;
                    i5 = sizeY;
                }
                scene.addTiledObject(tag, config, mean, i5, ((Renderable) (obj1)), k4, z, type == 11 ? 256 : 0, y, x);
            }
            if (solid)
                collisionMap.method212(impenetrable, sizeX, sizeY, x, y, orientation);
            return;
        }
        if (type >= 12) {
            Object obj2;
            if (animationId == -1 && transforms == null)
                obj2 =  getObjectModel(type, orientation, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj2 = new DynamicObject(id, orientation, type, x, y, east, northEast, center, north, animationId, true);
            scene.addTiledObject(tag, config, mean, 1, ((Renderable) (obj2)), 1, z, 0, y, x);
            if (solid)
                collisionMap.method212(impenetrable, sizeX, sizeY, x, y, orientation);
            return;
        }
        if (type == 0) {
            Object obj3;
            if (animationId == -1 && transforms == null)
                obj3 =  getObjectModel(0, orientation, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj3 = new DynamicObject(id, orientation, 0, x, y, east, northEast, center, north, animationId, true);
            scene.addWallObject(anIntArray152[orientation], ((Renderable) (obj3)), tag, y, config, x, null, mean, 0, z);
            if (solid)
                collisionMap.method211(y, orientation, x, type, impenetrable);
            return;
        }
        if (type == 1) {
            Object obj4;
            if (animationId == -1 && transforms == null)
                obj4 =  getObjectModel(1, orientation, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj4 = new DynamicObject(id, orientation, 1, x, y, east, northEast, center, north, animationId, true);
            scene.addWallObject(anIntArray140[orientation], ((Renderable) (obj4)), tag, y, config, x, null, mean, 0, z);
            if (solid)
                collisionMap.method211(y, orientation, x, type, impenetrable);
            return;
        }
        if (type == 2) {
            int orientation2 = orientation + 1 & 3;
            Object obj11;
            Object obj12;
            if (animationId == -1 && transforms == null) {
                obj11 =  getObjectModel(2, 4 + orientation, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
                obj12 =  getObjectModel(2, orientation2, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            } else {
                obj11 = new DynamicObject(id, 4 + orientation, 2, x, y, east, northEast, center, north, animationId, true);
                obj12 = new DynamicObject(id, orientation2, 2, x, y, east, northEast, center, north, animationId, true);
            }
            scene.addWallObject(anIntArray152[orientation], ((Renderable) (obj11)), tag, y, config, x, ((Renderable) (obj12)), mean, anIntArray152[orientation2], z);
            if (solid)
                collisionMap.method211(y, orientation, x, type, impenetrable);
            return;
        }
        if (type == 3) {
            Object obj5;
            if (animationId == -1 && transforms == null)
                obj5 =  getObjectModel(3, orientation, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj5 = new DynamicObject(id, orientation, 3, x, y, east, northEast, center, north, animationId, true);
            scene.addWallObject(anIntArray140[orientation], ((Renderable) (obj5)), tag, y, config, x, null, mean, 0, z);
            if (solid)
                collisionMap.method211(y, orientation, x, type, impenetrable);
            return;
        }
        if (type == 9) {
            Object obj6;
            if (animationId == -1 && transforms == null)
                obj6 = getObjectModel(type, orientation, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj6 = new DynamicObject(id, orientation, type, x, y, east, northEast, center, north, animationId, true);
            scene.addTiledObject(tag, config, mean, 1, ((Renderable) (obj6)), 1, z, 0, y, x);
            if (solid)
                collisionMap.method212(impenetrable, sizeX, sizeY, x, y, orientation);
            return;
        }
        if (type == 4) {
            Object obj7;
            if (animationId == -1 && transforms == null)
                obj7 = getObjectModel(4, 0, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj7 = new DynamicObject(id, 0, 4, x, y, east, northEast, center, north, animationId, true);
            scene.addWallDecoration(tag, y, orientation * 512, z, 0, mean, ((Renderable) (obj7)), x, config, 0, anIntArray152[orientation]);
            return;
        }
        if (type == 5) {
            int wallDecorDisplacement = 16;
            long wallObjectTag = scene.getWallObjectUid(z, x, y);

            if (wallObjectTag > 0) {
                int wallObjectId = DynamicObject.get_object_key(wallObjectTag);
                if(OSObjectDefinition.USE_OSRS){
                    wallDecorDisplacement = OSObjectDefinition.lookup(wallObjectId).decorDisplacement;
                } else
                    wallDecorDisplacement = ObjectDefinition.lookup(wallObjectId).decorDisplacement;
            }
            Object obj13;
            if (animationId == -1 && transforms == null)
                obj13 = getObjectModel(4, 0, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj13 = new DynamicObject(id, 0, 4, x, y, east, northEast, center, north, animationId, true);
            scene.addWallDecoration(tag, y, orientation * 512, z, COSINE_VERTICES[orientation] * wallDecorDisplacement, mean, ((Renderable) (obj13)), x, config, SINE_VERTICIES[orientation] * wallDecorDisplacement, anIntArray152[orientation]);
            return;
        }
        if (type == 6) {
            Object obj8;
            if (animationId == -1 && transforms == null)
                obj8 = getObjectModel(4, 0, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj8 = new DynamicObject(id, 0, 4, x, y, east, northEast, center, north, animationId, true);
            scene.addWallDecoration(tag, y, orientation, z, 0, mean, ((Renderable) (obj8)), x, config, 0, 256);
            return;
        }
        if (type == 7) {
            Object obj9;
            if (animationId == -1 && transforms == null)
                obj9 = getObjectModel(4, 0, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj9 = new DynamicObject(id, 0, 4, x, y, east, northEast, center, north, animationId, true);
            scene.addWallDecoration(tag, y, orientation, z, 0, mean, ((Renderable) (obj9)), x, config, 0, 512);
            return;
        }
        if (type == 8) {
            Object obj10;
            if (animationId == -1 && transforms == null)
                obj10 = getObjectModel(4, 0, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj10 = new DynamicObject(id, 0, 4, x, y, east, northEast, center, north, animationId, true);
            scene.addWallDecoration(tag, y, orientation, z, 0, mean, ((Renderable) (obj10)), x, config, 0, 768);
        }
    }
    public static boolean readMapObjects(byte[] data, int offsetX, int offsetY){
        if(OSObjectDefinition.USE_OSRS)
            return OS_readMapObjects(data, offsetX, offsetY);
        else
            return OLD_readMapObjects(data, offsetX, offsetY);
    }

    public static boolean OLD_readMapObjects(byte[] data, int offsetX, int offsetY) // xxx bad
    // method,
    // decompiled
    // with JODE
    {
        boolean bool = true;
        Buffer stream = new Buffer(data);
        int i_252_ = -1;
        for (; ; ) {
            int i_253_ = stream.readUnsignedIntSmartShortCompat();
            if (i_253_ == 0)
                break;
            i_252_ += i_253_;
            int i_254_ = 0;
            boolean bool_255_ = false;
            for (; ; ) {
                if (bool_255_) {
                    int i_256_ = stream.readUnsignedShortSmart();
                    if (i_256_ == 0)
                        break;
                    stream.readUnsignedByte();
                } else {
                    int i_257_ = stream.readUnsignedShortSmart();
                    if (i_257_ == 0)
                        break;
                    i_254_ += i_257_ - 1;
                    int i_258_ = i_254_ & 0x3f;
                    int i_259_ = i_254_ >> 6 & 0x3f;
                    int i_260_ = stream.readUnsignedByte() >> 2;
                    int i_261_ = i_259_ + offsetX;
                    int i_262_ = i_258_ + offsetY;
                    if (i_261_ > 0 && i_262_ > 0 && i_261_ < 103 && i_262_ < 103) {

                        ObjectDefinition class46 = ObjectDefinition.lookup(i_252_);
                        if (i_260_ != 22 || !isLowDetail || class46.isInteractive || class46.obstructsGround) {
                            bool &= class46.needsModelFiles();
                            bool_255_ = true;
                        }
                    }
                }
            }
        }
        return bool;
    }
    static boolean OS_readMapObjects(byte[] data, int offsetX, int offsetY) {
        boolean failed = true;
        Buffer buffer = new Buffer(data);
        int objectId = -1;

        label69:
        while(true) {
            int idOffset = buffer.readUnsignedIntSmartShortCompat();
            if(idOffset == 0) {
                return failed;
            }

            objectId += idOffset;
            int coordinates = 0;
            boolean loaded = false;

            while(true) {
                int packed;
                while(!loaded) {
                    packed = buffer.readUnsignedShortSmart();
                    if(packed == 0) {
                        continue label69;
                    }

                    coordinates += packed - 1;
                    int baseY = coordinates & 63;
                    int baseX = coordinates >> 6 & 63;
                    int type = buffer.getUnsignedByte() >> 2;
                    int x = baseX + offsetX;
                    int y = baseY + offsetY;
                    if(x > 0 && y > 0 && x < 103 && y < 103) {
                        OSObjectDefinition definition = OSObjectDefinition.lookup(objectId);
                        if(type != 22 || !isLowDetail || definition.interactiveState != 0 || definition.interactType == 1 || definition.obstructsGround) {
                            if(!definition.__g_422()) {
                                ++Client.map_objects_count;
                                failed = false;
                            }

                            loaded = true;
                        }
                    }
                }

                packed = buffer.readUnsignedShortSmart();
                if(packed == 0) {
                    break;
                }

                buffer.getUnsignedByte();
            }
        }
    }
    public final void createRegionScene(CollisionMap maps[], SceneGraph scene) {
        try {

            for (int z = 0; z < 4; z++) {
                for (int x = 0; x < 104; x++) {
                    for (int y = 0; y < 104; y++)
                        if ((Tiles_renderFlags[z][x][y] & BLOCKED_TILE) == 1) {
                            int plane = z;
                            if ((Tiles_renderFlags[1][x][y] & BRIDGE_TILE) == 2)
                                plane--;
                            if (plane >= 0)
                                maps[plane].block(x, y);
                        }

                }

            }
            for (int z = 0; z < 4; z++) {
                byte shading[][] = this.shading[z];
                byte byte0 = 96;
                char diffusion = '\u0300';
                byte lightX = -50;
                byte lightY = -10;
                byte lightZ = -50;
                int light = (int) Math.sqrt(lightX * lightX + lightY * lightY + lightZ * lightZ);
                int l3 = diffusion * light >> 8;
                for (int j4 = 1; j4 < regionSizeY - 1; j4++) {
                    for (int j5 = 1; j5 < regionSizeX - 1; j5++) {
                        int k6 = tileHeights[z][j5 + 1][j4] - tileHeights[z][j5 - 1][j4];
                        int l7 = tileHeights[z][j5][j4 + 1] - tileHeights[z][j5][j4 - 1];
                        int j9 = (int) Math.sqrt(k6 * k6 + 0x10000 + l7 * l7);
                        int k12 = (k6 << 8) / j9;
                        int l13 = 0x10000 / j9;
                        int j15 = (l7 << 8) / j9;
                        int j16 = byte0 + (lightX * k12 + lightY * l13 + lightZ * j15) / l3;
                        int j17 = (shading[j5 - 1][j4] >> 2) + (shading[j5 + 1][j4] >> 3) + (shading[j5][j4 - 1] >> 2) + (shading[j5][j4 + 1] >> 3) + (shading[j5][j4] >> 1);
                        tileLighting[j5][j4] = j16 - j17;
                    }

                }

                for (int k5 = 0; k5 < regionSizeY; k5++) {
                    hues[k5] = 0;
                    saturations[k5] = 0;
                    luminances[k5] = 0;
                    chromas[k5] = 0;
                    anIntArray128[k5] = 0;
                }

                for (int l6 = -5; l6 < regionSizeX + 5; l6++) {
                    for (int i8 = 0; i8 < regionSizeY; i8++) {
                        int k9 = l6 + 5;
                        if (k9 >= 0 && k9 < regionSizeX) {
                            int l12 = underlays[z][k9][i8] & 0xff;
                            if (l12 > 0) {
                                FloorUnderlayDefinition flo = FloorUnderlayDefinition.get(l12 - 1);
                                hues[i8] += flo.hue;
                                saturations[i8] += flo.saturation;
                                luminances[i8] += flo.lightness;
                                chromas[i8] += flo.hueMultiplier;
                                anIntArray128[i8]++;
                            }
                        }
                        int i13 = l6 - 5;
                        if (i13 >= 0 && i13 < regionSizeX) {
                            int i14 = underlays[z][i13][i8] & 0xff;
                            if (i14 > 0) {
                                FloorUnderlayDefinition flo_1 = FloorUnderlayDefinition.get(i14 - 1);
                                hues[i8] -= flo_1.hue;
                                saturations[i8] -= flo_1.saturation;
                                luminances[i8] -= flo_1.lightness;
                                chromas[i8] -= flo_1.hueMultiplier;
                                anIntArray128[i8]--;
                            }
                        }
                    }

                    if (l6 >= 1 && l6 < regionSizeX - 1) {
                        int l9 = 0;
                        int j13 = 0;
                        int j14 = 0;
                        int k15 = 0;
                        int k16 = 0;
                        for (int k17 = -5; k17 < regionSizeY + 5; k17++) {
                            int j18 = k17 + 5;
                            if (j18 >= 0 && j18 < regionSizeY) {
                                l9 += hues[j18];
                                j13 += saturations[j18];
                                j14 += luminances[j18];
                                k15 += chromas[j18];
                                k16 += anIntArray128[j18];
                            }
                            int k18 = k17 - 5;
                            if (k18 >= 0 && k18 < regionSizeY) {
                                l9 -= hues[k18];
                                j13 -= saturations[k18];
                                j14 -= luminances[k18];
                                k15 -= chromas[k18];
                                k16 -= anIntArray128[k18];
                            }
                            if (k17 >= 1 && k17 < regionSizeY - 1 && (!isLowDetail || (Tiles_renderFlags[0][l6][k17] & 2) != 0 || (Tiles_renderFlags[z][l6][k17] & 0x10) == 0 && getCollisionPlane(k17, z, l6) == lastPlane)) {
                                if (z < Tiles_minPlane)
                                    Tiles_minPlane = z;
                                int l18 = underlays[z][l6][k17] & 0xff;
                                int overlayId = overlays[z][l6][k17] & 0xff;
                                if (l18 > 0 || overlayId > 0) {
                                    int j19 = tileHeights[z][l6][k17];
                                    int k19 = tileHeights[z][l6 + 1][k17];
                                    int l19 = tileHeights[z][l6 + 1][k17 + 1];
                                    int i20 = tileHeights[z][l6][k17 + 1];
                                    int j20 = tileLighting[l6][k17];
                                    int k20 = tileLighting[l6 + 1][k17];
                                    int l20 = tileLighting[l6 + 1][k17 + 1];
                                    int i21 = tileLighting[l6][k17 + 1];
                                    int j21 = -1;
                                    int k21 = -1;
                                    if (l18 > 0) {
                                        int l21 = (l9 * 256) / k15;
                                        int j22 = j13 / k16;
                                        int l22 = j14 / k16;
                                        j21 = encode(l21, j22, l22);

                                        if (l22 < 0)
                                            l22 = 0;
                                        else if (l22 > 255)
                                            l22 = 255;

                                        k21 = encode(l21, j22, l22);
                                    }
                                    if (z > 0) {
                                        boolean flag = true;
                                        if (l18 == 0 && overlayTypes[z][l6][k17] != 0)
                                            flag = false;
                                        if (overlayId > 0 && !FloorOverlayDefinition.get(overlayId - 1).hideUnderlay)
                                            flag = false;
                                        if (flag && j19 == k19 && j19 == l19 && j19 == i20)
                                            anIntArrayArrayArray135[z][l6][k17] |= 0x924;
                                    }
                                    int i22 = 0;
                                    if (j21 != -1)
                                        i22 = Rasterizer3D.hslToRgb[method187(k21, 96)];
                                    if (overlayId == 0) {
                                        scene.addTile(z, l6, k17, 0, 0, -1, j19, k19, l19, i20, method187(j21, j20), method187(j21, k20), method187(j21, l20), method187(j21, i21), 0, 0, 0, 0, i22, 0);
                                    } else {

                                        int k22 = overlayTypes[z][l6][k17] + 1;
                                        byte byte4 = overlayOrientations[z][l6][k17];
                                        FloorOverlayDefinition overlay_flo = FloorOverlayDefinition.get(overlayId - 1);
                                        int textureId = overlay_flo.texture;
                                        int j23;
                                        int minimapColor;
                                        int var40;
                                        int var41;
                                        if (textureId >= 0) {
                                            minimapColor = Rasterizer3D.Rasterizer3D_textureLoader.getAverageTextureRGB(textureId);
                                            j23 = -1;
                                        } else if (overlay_flo.primaryRgb == 16711935) {
                                            j23 = -2;
                                            textureId = -1;
                                            minimapColor = -2;
                                        } else {
                                            j23 = hslToRgb(overlay_flo.hue, overlay_flo.saturation, overlay_flo.lightness);
                                            var40 = overlay_flo.hue + rndHue & 255;
                                            var41 = overlay_flo.lightness + rndLightness;
                                            if (var41 < 0) {
                                                var41 = 0;
                                            } else if (var41 > 255) {
                                                var41 = 255;
                                            }

                                            minimapColor = hslToRgb(var40, overlay_flo.saturation, var41);
                                        }

                                        var40 = 0;
                                        if (minimapColor != -2) {
                                            var40 = Rasterizer3D.hslToRgb[checkedLight(minimapColor, 96)];
                                        }

                                        if (overlay_flo.secondaryRgb != -1) {
                                            var41 = overlay_flo.secondaryHue + rndHue & 255;
                                            int var42 = overlay_flo.secondaryLightness + rndLightness;
                                            if (var42 < 0) {
                                                var42 = 0;
                                            } else if (var42 > 255) {
                                                var42 = 255;
                                            }

                                            minimapColor = hslToRgb(var41, overlay_flo.secondarySaturation, var42);
                                            var40 = Rasterizer3D.hslToRgb[checkedLight(minimapColor, 96)];
                                        }

                                        scene.addTile(z, l6, k17, k22, byte4, textureId, j19, k19, l19, i20, method187(j21, j20), method187(j21, k20), method187(j21, l20), method187(j21, i21), checkedLight(j23, j20), checkedLight(j23, k20), checkedLight(j23, l20), checkedLight(j23, i21), i22, var40);
                                    }
                                }
                            }
                        }

                    }
                }

                for (int j8 = 1; j8 < regionSizeY - 1; j8++) {
                    for (int i10 = 1; i10 < regionSizeX - 1; i10++)
                        scene.setTileMinPlane(z, i10, j8, getCollisionPlane(j8, z, i10));

                }
            }

            scene.shadeModels(-50, -10, -50);
            for (int j1 = 0; j1 < regionSizeX; j1++) {
                for (int l1 = 0; l1 < regionSizeY; l1++)
                    if ((Tiles_renderFlags[1][j1][l1] & 2) == 2)
                        scene.setLinkBelow(l1, j1);

            }

            int i2 = 1;
            int j2 = 2;
            int k2 = 4;
            for (int l2 = 0; l2 < 4; l2++) {
                if (l2 > 0) {
                    i2 <<= 3;
                    j2 <<= 3;
                    k2 <<= 3;
                }
                for (int i3 = 0; i3 <= l2; i3++) {
                    for (int k3 = 0; k3 <= regionSizeY; k3++) {
                        for (int i4 = 0; i4 <= regionSizeX; i4++) {
                            if ((anIntArrayArrayArray135[i3][i4][k3] & i2) != 0) {
                                int k4 = k3;
                                int l5 = k3;
                                int i7 = i3;
                                int k8 = i3;
                                for (; k4 > 0 && (anIntArrayArrayArray135[i3][i4][k4 - 1] & i2) != 0; k4--)
                                    ;
                                for (; l5 < regionSizeY && (anIntArrayArrayArray135[i3][i4][l5 + 1] & i2) != 0; l5++)
                                    ;
                                label0:
                                for (; i7 > 0; i7--) {
                                    for (int j10 = k4; j10 <= l5; j10++)
                                        if ((anIntArrayArrayArray135[i7 - 1][i4][j10] & i2) == 0)
                                            break label0;

                                }

                                label1:
                                for (; k8 < l2; k8++) {
                                    for (int k10 = k4; k10 <= l5; k10++)
                                        if ((anIntArrayArrayArray135[k8 + 1][i4][k10] & i2) == 0)
                                            break label1;

                                }

                                int l10 = ((k8 + 1) - i7) * ((l5 - k4) + 1);
                                if (l10 >= 8) {
                                    char c1 = '\360';
                                    int k14 = tileHeights[k8][i4][k4] - c1;
                                    int l15 = tileHeights[i7][i4][k4];
                                    SceneGraph.Scene_addOccluder(l2, i4 * 128, l15, i4 * 128, l5 * 128 + 128, k14, k4 * 128, 1);
                                    for (int l16 = i7; l16 <= k8; l16++) {
                                        for (int l17 = k4; l17 <= l5; l17++)
                                            anIntArrayArrayArray135[l16][i4][l17] &= ~i2;

                                    }

                                }
                            }
                            if ((anIntArrayArrayArray135[i3][i4][k3] & j2) != 0) {
                                int l4 = i4;
                                int i6 = i4;
                                int j7 = i3;
                                int l8 = i3;
                                for (; l4 > 0 && (anIntArrayArrayArray135[i3][l4 - 1][k3] & j2) != 0; l4--)
                                    ;
                                for (; i6 < regionSizeX && (anIntArrayArrayArray135[i3][i6 + 1][k3] & j2) != 0; i6++)
                                    ;
                                label2:
                                for (; j7 > 0; j7--) {
                                    for (int i11 = l4; i11 <= i6; i11++)
                                        if ((anIntArrayArrayArray135[j7 - 1][i11][k3] & j2) == 0)
                                            break label2;

                                }

                                label3:
                                for (; l8 < l2; l8++) {
                                    for (int j11 = l4; j11 <= i6; j11++)
                                        if ((anIntArrayArrayArray135[l8 + 1][j11][k3] & j2) == 0)
                                            break label3;

                                }

                                int k11 = ((l8 + 1) - j7) * ((i6 - l4) + 1);
                                if (k11 >= 8) {
                                    char c2 = '\360';
                                    int l14 = tileHeights[l8][l4][k3] - c2;
                                    int i16 = tileHeights[j7][l4][k3];
                                    SceneGraph.Scene_addOccluder(l2, l4 * 128, i16, i6 * 128 + 128, k3 * 128, l14, k3 * 128, 2);
                                    for (int i17 = j7; i17 <= l8; i17++) {
                                        for (int i18 = l4; i18 <= i6; i18++)
                                            anIntArrayArrayArray135[i17][i18][k3] &= ~j2;

                                    }

                                }
                            }
                            if ((anIntArrayArrayArray135[i3][i4][k3] & k2) != 0) {
                                int i5 = i4;
                                int j6 = i4;
                                int k7 = k3;
                                int i9 = k3;
                                for (; k7 > 0 && (anIntArrayArrayArray135[i3][i4][k7 - 1] & k2) != 0; k7--)
                                    ;
                                for (; i9 < regionSizeY && (anIntArrayArrayArray135[i3][i4][i9 + 1] & k2) != 0; i9++)
                                    ;
                                label4:
                                for (; i5 > 0; i5--) {
                                    for (int l11 = k7; l11 <= i9; l11++)
                                        if ((anIntArrayArrayArray135[i3][i5 - 1][l11] & k2) == 0)
                                            break label4;

                                }

                                label5:
                                for (; j6 < regionSizeX; j6++) {
                                    for (int i12 = k7; i12 <= i9; i12++)
                                        if ((anIntArrayArrayArray135[i3][j6 + 1][i12] & k2) == 0)
                                            break label5;

                                }

                                if (((j6 - i5) + 1) * ((i9 - k7) + 1) >= 4) {
                                    int j12 = tileHeights[i3][i5][k7];
                                    SceneGraph.Scene_addOccluder(l2, i5 * 128, j12, j6 * 128 + 128, i9 * 128 + 128, j12, k7 * 128, 4);
                                    for (int k13 = i5; k13 <= j6; k13++) {
                                        for (int i15 = k7; i15 <= i9; i15++)
                                            anIntArrayArrayArray135[i3][k13][i15] &= ~k2;

                                    }

                                }
                            }
                        }

                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static final int hslToRgb(int var0, int var1, int var2) {
        if (var2 > 179) {
            var1 /= 2;
        }

        if (var2 > 192) {
            var1 /= 2;
        }

        if (var2 > 217) {
            var1 /= 2;
        }

        if (var2 > 243) {
            var1 /= 2;
        }

        int var3 = (var1 / 32 << 7) + (var0 / 4 << 10) + var2 / 2;
        return var3;
    }

    public final void initiateVertexHeights(int yOffset, int yLength, int xLength, int xOffset) {
        for (int y = yOffset; y <= yOffset + yLength; y++) {
            for (int x = xOffset; x <= xOffset + xLength; x++) {
                if (x >= 0 && x < regionSizeX && y >= 0 && y < regionSizeY) {
                    shading[0][x][y] = 127;
                    if (x == xOffset && x > 0) {
                        tileHeights[0][x][y] = tileHeights[0][x - 1][y];
                    }
                    if (x == xOffset + xLength && x < regionSizeX - 1) {
                        tileHeights[0][x][y] = tileHeights[0][x + 1][y];
                    }
                    if (y == yOffset && y > 0) {
                        tileHeights[0][x][y] = tileHeights[0][x][y - 1];
                    }
                    if (y == yOffset + yLength && y < regionSizeY - 1) {
                        tileHeights[0][x][y] = tileHeights[0][x][y + 1];
                    }
                }
            }
        }
    }

    private void placeObjectInScene(int z, int x, int y, int id, int orientation, int type, SceneGraph scene, CollisionMap collisionMap) {
        if (isLowDetail && (Tiles_renderFlags[0][x][y] & BRIDGE_TILE) == 0) {
            if ((Tiles_renderFlags[z][x][y] & 0x10) != 0) {
                return;
            }

            if (getCollisionPlane(y, z, x) != lastPlane) {
                return;
            }
        }

        if (z < Tiles_minPlane)
            Tiles_minPlane = z;

        int sizeY2;
        int sizeX2;
        if(OSObjectDefinition.USE_OSRS){
            OSObjectDefinition definition = OSObjectDefinition.lookup(id);
            sizeX2  = definition.sizeX;
            sizeY2 = definition.sizeY;
        } else {
            ObjectDefinition definition = ObjectDefinition.lookup(id);
            sizeX2 = definition.sizeX;
            sizeY2 = definition.sizeY;
//            System.out.println("loading id "+id+" ["+definition.name+"] in osrs = "+OSObjectDefinition.lookup(id).name);
        }
        if (!(orientation != 1 && orientation != 3)) {
            int copyX = sizeX2;
            sizeX2 = sizeY2;
            sizeY2 = copyX;
        }

        int editX;
        int editX2;
        if (x + sizeX2 <= 104) {
            editX = x + (sizeX2 >> 1);
            editX2 = x + (1 + sizeX2 >> 1);
        } else {
            editX = x;
            editX2 = 1 + x;
        }

        int editY;
        int editY2;
        if (sizeY2 + y <= 104) {
            editY = (sizeY2 >> 1) + y;
            editY2 = y + (1 + sizeY2 >> 1);
        } else {
            editY = y;
            editY2 = 1 + y;
        }

        int[][] tilesHeight = tileHeights[z];
        int center = tilesHeight[editX][editY];
        int east = tilesHeight[editX2][editY];
        int northEast = tilesHeight[editX2][editY2];
        int north = tilesHeight[editX][editY2];
        int mean = center + east + northEast + north >> 2;
        int var17 = (x << 7) + (sizeX2 << 6);
        int var18 = (y << 7) + (sizeY2 << 6);
        long tag;
        int config;
        final Object reference;
        final ObjectSound objectSound;
        final int animationId;
        final int decorDisplacement;
        final int[] transforms;
        final boolean isInteractive;
        final boolean obstructsGround;
        final boolean blockTile;
        final boolean solid;
        final boolean clipped;
        final boolean modelClipped;
        final boolean impenetrable;

        int sizeX, sizeY;


        if(OSObjectDefinition.USE_OSRS){
            OSObjectDefinition definition = OSObjectDefinition.lookup(id);
            reference = definition;
            animationId = definition.animationId;
            decorDisplacement = definition.decorDisplacement;
            transforms = definition.transforms;
            isInteractive = !(definition.interactiveState != 0 || definition.interactType == 1);
            obstructsGround = definition.obstructsGround;
            blockTile = definition.interactType == 1;
            impenetrable = definition.impenetrable;
            clipped = definition.clipped;
            modelClipped = definition.modelClipped;
            solid = definition.interactType != 0;
            sizeX = definition.sizeX;
            sizeY = definition.sizeY;

            tag = ViewportMouse.toTag(x, y, 2, definition.interactiveState == 0, id);
            config = type + (orientation << 6);

            if(definition.supportItems == 1) {
                config += 256;
            }
            int var23;
            int var24;
            if(definition.hasSound()){
                final ObjectSound sound = new ObjectSound();
                sound.objectId = definition.id;
                sound.plane = z;
                sound.west = x * 128;
                sound.south = y * 128;
                var23 = definition.sizeX;
                var24 = definition.sizeY;
                if(orientation == 1 || orientation == 3) {
                    var23 = definition.sizeX;
                    var24 = definition.sizeY;
                }
                sound.east = (var23 + x) * 128;
                sound.north = (var24 + y) * 128;
                sound.soundEffectId = definition.ambientSoundId;
                sound.minimumDistance = definition.int4 * 128;
                sound.frequency1 = definition.int5;
                sound.frequency2 = definition.int6;

                sound.soundEffectIds = definition.someSoundStuff;
                if(definition.transforms != null) {
                    sound.OS_obj = definition;
                    sound.set();
                }

                ObjectSound.objectSounds.addFirst(sound);
                if(sound.soundEffectIds != null) {
//                System.out.println("["+id+"]: adding sound "+sound.soundEffectId+", "+sound.minimumDistance +", -> "+definition.int4+", "+definition.int5+", "+definition.int6+", "+ Arrays.toString(definition.someSoundStuff));
                    sound.timer = sound.frequency1 + (int)(Math.random() * (double)(sound.frequency2 - sound.frequency1));
                }
            }
        } else {
            ObjectDefinition definition = ObjectDefinition.lookup(id);
            reference = definition;
            animationId = definition.animationId;
            decorDisplacement = definition.decorDisplacement;
            transforms = definition.transforms;
            isInteractive = definition.isInteractive;
            obstructsGround = definition.obstructsGround;
            impenetrable = definition.impenetrable;
            blockTile = definition.solid && isInteractive;
            modelClipped = definition.modelClipped;
            clipped = definition.clipped;
            sizeX = definition.sizeX;
            sizeY = definition.sizeY;
            solid = definition.solid;
            tag = orientation << 20 | type << 14 | (y << 7 | x) + 0x40000000;
            if (!definition.isInteractive)
                tag |= ~0x7fffffffffffffffL;
            if(definition.supportItems == 1) {
                tag |= 0x400000L;
            }
            tag |= (long) id << 32;
            config = ((orientation << 6) + type);
            if(definition.supportItems == 1) {
                config += 256;
            }

            final ObjectSoundDefinition definition1 = ObjectSoundDefinition.getSound(definition);
            if(definition1 != null){
                objectSound = definition1.toSound(x, y, z, orientation, definition);
                ObjectSound.objectSounds.addFirst(objectSound);
                if(objectSound.soundEffectIds != null) {
                    objectSound.timer = objectSound.frequency1 + (int)(Math.random() * (double)(objectSound.frequency2 - objectSound.frequency1));
                }
            }
        }


        if (type == 22) {
            if (isLowDetail && !isInteractive && !obstructsGround)
                return;
            Object obj;
            if (animationId == -1 && transforms == null) {
                obj = getObjectModel(22, orientation, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            } else
                obj = new DynamicObject(id, orientation, 22, x, y, east, northEast, center, north, animationId, true);

            scene.newFloorDecoration(x, y, z, mean, config, ((Renderable) (obj)), tag);
            if (blockTile && collisionMap != null)
                collisionMap.block(x, y);
            return;
        }
        if (type == 10 || type == 11) {
            Object obj1;
            if (animationId == -1 && transforms == null)
                obj1 = getObjectModel(10, orientation, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj1 = new DynamicObject(id, orientation, 10, x, y, east, northEast, center, north, animationId, true);
            int j4;
            int l4;
            if (orientation == 1 || orientation == 3) {
                j4 = sizeY;
                l4 = sizeX;
            } else {
                j4 = sizeX;
                l4 = sizeY;
            }
            if (obj1 != null && scene.addTiledObject(tag, config, mean, l4, ((Renderable) (obj1)), j4, z, type == 11 ? 256 : 0, y, x) && clipped) {
                int l5 = 15;
                if (obj1 instanceof Model) {
                    l5 = ((Model)obj1).method4625() / 4;
                    if (l5 > 30)
                        l5 = 30;
                }

                for (int j5 = 0; j5 <= j4; j5++) {
                    for (int k5 = 0; k5 <= l4; k5++) {
                        if (l5 > shading[z][x + j5][y + k5])
                            shading[z][x + j5][y + k5] = (byte) l5;
                    }

                }
            }
            if (solid && collisionMap != null)
                collisionMap.method212(impenetrable, sizeX, sizeY, x, y, orientation);
            return;
        }
        if (type >= 12) {
            Object obj2;
            if (animationId == -1 && transforms == null)
                obj2 = getObjectModel(type, orientation, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj2 = new DynamicObject(id, orientation, type, x, y, east, northEast, center, north, animationId, true);
            scene.addTiledObject(tag, config, mean, 1, ((Renderable) (obj2)), 1, z, 0, y, x);
            if (type >= 12 && type <= 17 && type != 13 && z > 0)
                anIntArrayArrayArray135[z][x][y] |= 0x924;
            if (solid && collisionMap != null)
                collisionMap.method212(impenetrable, sizeX, sizeY, x, y, orientation);
            return;
        }
        if (type == 0) {
            Object obj3;
            if (animationId == -1 && transforms == null)
                obj3 = getObjectModel(0, orientation, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj3 = new DynamicObject(id, orientation, 0, x, y, east, northEast, center, north, animationId, true);
            scene.addWallObject(anIntArray152[orientation], ((Renderable) (obj3)), tag, y, config, x, null, mean, 0, z);
            if (orientation == 0) {
                if (clipped) {
                    shading[z][x][y] = 50;
                    shading[z][x][y + 1] = 50;
                }
                if (modelClipped)
                    anIntArrayArrayArray135[z][x][y] |= 0x249;
            } else if (orientation == 1) {
                if (clipped) {
                    shading[z][x][y + 1] = 50;
                    shading[z][x + 1][y + 1] = 50;
                }
                if (modelClipped)
                    anIntArrayArrayArray135[z][x][y + 1] |= 0x492;
            } else if (orientation == 2) {
                if (clipped) {
                    shading[z][x + 1][y] = 50;
                    shading[z][x + 1][y + 1] = 50;
                }
                if (modelClipped)
                    anIntArrayArrayArray135[z][x + 1][y] |= 0x249;
            } else if (orientation == 3) {
                if (clipped) {
                    shading[z][x][y] = 50;
                    shading[z][x + 1][y] = 50;
                }
                if (modelClipped)
                    anIntArrayArrayArray135[z][x][y] |= 0x492;
            }
            if (solid && collisionMap != null)
                collisionMap.method211(y, orientation, x, type, impenetrable);
            if (decorDisplacement != 16)
                scene.method290(y, decorDisplacement, x, z);
            return;
        }
        if (type == 1) {
            Object obj4;
            if (animationId == -1 && transforms == null)
                obj4 = getObjectModel(1, orientation, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj4 = new DynamicObject(id, orientation, 1, x, y, east, northEast, center, north, animationId, true);
            scene.addWallObject(anIntArray140[orientation], ((Renderable) (obj4)), tag, y, config, x, null, mean, 0, z);
            if (clipped)
                if (orientation == 0)
                    shading[z][x][y + 1] = 50;
                else if (orientation == 1)
                    shading[z][x + 1][y + 1] = 50;
                else if (orientation == 2)
                    shading[z][x + 1][y] = 50;
                else if (orientation == 3)
                    shading[z][x][y] = 50;
            if (solid && collisionMap != null)
                collisionMap.method211(y, orientation, x, type, impenetrable);
            return;
        }
        if (type == 2) {
            int orientation2 = orientation + 1 & 3;
            Object obj11;
            Object obj12;
            if (animationId == -1 && transforms == null) {
                obj11 = getObjectModel(2, 4 + orientation, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
                obj12 = getObjectModel(2, orientation2, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            } else {
                obj11 = new DynamicObject(id, 4 + orientation, 2, x, y, east, northEast, center, north, animationId, true);
                obj12 = new DynamicObject(id, orientation2, 2, x, y, east, northEast, center, north, animationId, true);
            }
            scene.addWallObject(anIntArray152[orientation], ((Renderable) (obj11)), tag, y, config, x, ((Renderable) (obj12)), mean, anIntArray152[orientation2], z);
            if (modelClipped)
                if (orientation == 0) {
                    anIntArrayArrayArray135[z][x][y] |= 0x249;
                    anIntArrayArrayArray135[z][x][y + 1] |= 0x492;
                } else if (orientation == 1) {
                    anIntArrayArrayArray135[z][x][y + 1] |= 0x492;
                    anIntArrayArrayArray135[z][x + 1][y] |= 0x249;
                } else if (orientation == 2) {
                    anIntArrayArrayArray135[z][x + 1][y] |= 0x249;
                    anIntArrayArrayArray135[z][x][y] |= 0x492;
                } else if (orientation == 3) {
                    anIntArrayArrayArray135[z][x][y] |= 0x492;
                    anIntArrayArrayArray135[z][x][y] |= 0x249;
                }
            if (solid && collisionMap != null)
                collisionMap.method211(y, orientation, x, type, impenetrable);
            if (decorDisplacement != 16)
                scene.method290(y, decorDisplacement, x, z);
            return;
        }
        if (type == 3) {
            Object obj5;
            if (animationId == -1 && transforms == null)
                obj5 = getObjectModel(3, orientation, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj5 = new DynamicObject(id, orientation, 3, x, y, east, northEast, center, north, animationId, true);
            scene.addWallObject(anIntArray140[orientation], ((Renderable) (obj5)), tag, y, config, x, null, mean, 0, z);
            if (clipped)
                if (orientation == 0)
                    shading[z][x][y + 1] = 50;
                else if (orientation == 1)
                    shading[z][x + 1][y + 1] = 50;
                else if (orientation == 2)
                    shading[z][x + 1][y] = 50;
                else if (orientation == 3)
                    shading[z][x][y] = 50;
            if (solid && collisionMap != null)
                collisionMap.method211(y, orientation, x, type, impenetrable);
            return;
        }
        if (type == 9) {
            Object obj6;
            if (animationId == -1 && transforms == null)
                obj6 = getObjectModel(type, orientation, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj6 = new DynamicObject(id, orientation, type, x, y, east, northEast, center, north, animationId, true);
            scene.addTiledObject(tag, config, mean, 1, ((Renderable) (obj6)), 1, z, 0, y, x);
            if (solid && collisionMap != null)
                collisionMap.method212(impenetrable, sizeX, sizeY, x, y, orientation);
            return;
        }
        if (type == 4) {
            Object obj7;
            if (animationId == -1 && transforms == null)
                obj7 = getObjectModel(4, 0, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj7 = new DynamicObject(id, 0, 4, x, y, east, northEast, center, north, animationId, true);
            scene.addWallDecoration(tag, y, orientation * 512, z, 0, mean, ((Renderable) (obj7)), x, config, 0, anIntArray152[orientation]);
            return;
        }
        if (type == 5) {
            int wallDecorDisplacement = 16;
            long wallObjectTag = scene.getWallObjectUid(z, x, y);
            
            if (wallObjectTag > 0) {
                int wallObjectId = DynamicObject.get_object_key(wallObjectTag);
                if(OSObjectDefinition.USE_OSRS){
                    wallDecorDisplacement = OSObjectDefinition.lookup(wallObjectId).decorDisplacement;
                } else
                    wallDecorDisplacement = ObjectDefinition.lookup(wallObjectId).decorDisplacement;
            }
            Object obj13;
            if (animationId == -1 && transforms == null)
                obj13 = getObjectModel(4, 0, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj13 = new DynamicObject(id, 0, 4, x, y, east, northEast, center, north, animationId, true);
            scene.addWallDecoration(tag, y, orientation * 512, z, COSINE_VERTICES[orientation] * wallDecorDisplacement, mean, ((Renderable) (obj13)), x, config, SINE_VERTICIES[orientation] * wallDecorDisplacement, anIntArray152[orientation]);
            return;
        }
        if (type == 6) {
            Object obj8;
            if (animationId == -1 && transforms == null)
                obj8 = getObjectModel(4, 0, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj8 = new DynamicObject(id, 0, 4, x, y, east, northEast, center, north, animationId, true);
            scene.addWallDecoration(tag, y, orientation, z, 0, mean, ((Renderable) (obj8)), x, config, 0, 256);
            return;
        }
        if (type == 7) {
            Object obj9;
            if (animationId == -1 && transforms == null)
                obj9 = getObjectModel(4, 0, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj9 = new DynamicObject(id, 0, 4, x, y, east, northEast, center, north, animationId, true);
            scene.addWallDecoration(tag, y, orientation, z, 0, mean, ((Renderable) (obj9)), x, config, 0, 512);
            return;
        }
        if (type == 8) {
            Object obj10;
            if (animationId == -1 && transforms == null)
                obj10 = getObjectModel(4, 0, tilesHeight, center, east, northEast, north, mean, var17, var18, reference);
            else
                obj10 = new DynamicObject(id, 0, 4, x, y, east, northEast, center, north, animationId, true);
            scene.addWallDecoration(tag, y, orientation, z, 0, mean, ((Renderable) (obj10)), x, config, 0, 768);
        }
    }

    private static Renderable getObjectModel(int type, int orientation, int[][] tilesHeight, int center, int east, int northEast, int north, int mean, int var17, int var18, Object reference) {
        Renderable obj;
        if(reference instanceof OSObjectDefinition){
//            System.out.println("fetching renderable for "+type);
            final int id = ((OSObjectDefinition) reference).id;
//            obj = ObjectDefinition.lookup(id).modelAt(type, orientation, center, east, northEast, north, -1);
            obj = ((OSObjectDefinition) reference).modelAt(type, orientation, tilesHeight, var17, mean, var18);
        } else
            obj = ((ObjectDefinition) reference).getEntity(type, orientation, tilesHeight, var17, mean, var18);
        return obj;
    }

    /**
     * Encodes the hue, saturation, and luminance into a colour value.
     *
     * @param hue        The hue.
     * @param saturation The saturation.
     * @param luminance  The luminance.
     * @return The colour.
     */
    private int encode(int hue, int saturation, int luminance) {
        if (luminance > 179)
            saturation /= 2;
        if (luminance > 192)
            saturation /= 2;
        if (luminance > 217)
            saturation /= 2;
        if (luminance > 243)
            saturation /= 2;
        return (hue / 4 << 10) + (saturation / 32 << 7) + luminance / 2;
    }

    public final void loadMapChunk(int regionId, int i, int j, CollisionMap clips[], int l, int i1, byte abyte0[], int j1, int k1, int l1) {
        for (int i2 = 0; i2 < 8; i2++) { //Add clipping
            for (int j2 = 0; j2 < 8; j2++)
                if (l + i2 > 0 && l + i2 < 103 && l1 + j2 > 0 && l1 + j2 < 103)
                    clips[k1].clipData[l + i2][l1 + j2] &= 0xfeffffff;

        }

        Buffer stream = new Buffer(abyte0);
        for (int l2 = 0; l2 < 4; l2++) {
            for (int i3 = 0; i3 < 64; i3++) {
                for (int j3 = 0; j3 < 64; j3++)
                    if (l2 == i && i3 >= i1 && i3 < i1 + 8 && j3 >= j1 && j3 < j1 + 8)
                        readTile(regionId, l1 + ChunkUtil.getRotatedMapChunkY(j3 & 7, j, i3 & 7), 0, stream, l + ChunkUtil.getRotatedMapChunkX(j, j3 & 7, i3 & 7), k1, j, 0);
                    else
                        readTile(regionId, -1, 0, stream, -1, 0, 0, 0);

            }

        }

    }

    public final void method180(int regionId, byte abyte0[], int i, int j, int k, int l, CollisionMap aclass11[]) {
        for (int i1 = 0; i1 < 4; i1++) {
            for (int j1 = 0; j1 < 64; j1++) {
                for (int k1 = 0; k1 < 64; k1++)
                    if (j + j1 > 0 && j + j1 < 103 && i + k1 > 0 && i + k1 < 103)
                        aclass11[i1].clipData[j + j1][i + k1] &= 0xfeffffff;
            }

        }

        Buffer stream = new Buffer(abyte0);
        for (int l1 = 0; l1 < 4; l1++) {
            for (int i2 = 0; i2 < 64; i2++) {
                for (int j2 = 0; j2 < 64; j2++)
                    readTile(regionId, j2 + i, l, stream, i2 + j, l1, 0, k);

            }

        }
    }

    private void readTile(int regionId, int y, int j, Buffer stream, int x, int z, int i1, int k1) {
        boolean old = OLD_REGIONS.contains(regionId);
        try {
            if (x >= 0 && x < 104 && y >= 0 && y < 104) {
                Tiles_renderFlags[z][x][y] = 0;
                do {
                    int attribute = old ? stream.readUnsignedByte() : stream.readUnsignedShort();
                    if (attribute == 0) {
                        if (z == 0) {
                            tileHeights[0][x][y] = -calculateVertexHeight(0xe3b7b + x + k1, 0x87cce + y + j) * 8;
                            return;
                        } else {
                            tileHeights[z][x][y] = tileHeights[z - 1][x][y] - 240;
                            return;
                        }
                    }
                    if (attribute == 1) {
                        int height = stream.readUnsignedByte();
                        if (height == 1)
                            height = 0;
                        if (z == 0) {
                            tileHeights[0][x][y] = -height * 8;
                            return;
                        } else {
                            tileHeights[z][x][y] = tileHeights[z - 1][x][y] - height * 8;
                            return;
                        }
                    }
                    if (attribute <= 49) {
                        overlays[z][x][y] = (byte) (old ? stream.readSignedByte() : stream.readShort());
                        overlayTypes[z][x][y] = (byte) ((attribute - 2) / 4);
                        overlayOrientations[z][x][y] = (byte) (attribute - 2 & 3);
                    } else if (attribute <= 81)
                        Tiles_renderFlags[z][x][y] = (byte) (attribute - 49);
                    else
                        underlays[z][x][y] = (byte) (attribute - 81);
                } while (true);
            }
            do {
                int i2 = old ? stream.readUnsignedByte() : stream.readUnsignedShort();
                if (i2 == 0)
                    break;
                if (i2 == 1) {
                    stream.readUnsignedByte();
                    return;
                }
                if (i2 <= 49) {
                    if (old) {
                        stream.readUnsignedByte();
                    } else {
                        stream.readShort();
                    }
                }
            } while (true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the plane that actually contains the collision flag, to adjust for objects such as bridges. TODO better
     * name
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param z The z coordinate.
     * @return The correct z coordinate.
     */
    private int getCollisionPlane(int y, int z, int x) {
        if ((Tiles_renderFlags[z][x][y] & FORCE_LOWEST_PLANE) != 0) {
            return 0;
        }
        if (z > 0 && (Tiles_renderFlags[1][x][y] & BRIDGE_TILE) != 0) {
            return z - 1;
        } else {
            return z;
        }
    }

    public static final int[] CONSTRUCTION_HOTSPOTS = new int[] {26296, 26869, 26865, 26866, 26867, 26861, 26833, 26839, 26845, 26851, 26867, 26857,15314,15313,15361,15362,15363,15366,15367,15364,15365,29119,4515,4517,4516,4519,4520,4518,4521,4521,4523,4524,4524,4524,4524,4524,4524,4524,4524,15402,15405,15401,15398,15404,15403,15400,15400,15399,15302,15302,15302,15302,15302,15302,15304,15303,15303,15301,15300,15300,15300,15300,15299,15299,15299,15299,15298,15443,15445,15447,15446,15444,15441,15439,15448,15450,15266,15265,15264,15263,15263,15263,15263,15263,15263,15263,15263,15267,15262,15260,15261,15268,15379,15378,15377,15386,15383,15382,15384,15385,15380,15381,15379,15378,15377,15386,15383,15382,15384,34255,15380,15381,15346,15344,15345,15343,15342,15296,15297,15297,15294,15293,15292,15291,15290,15289,15288,15287,15286,15282,15281,15280,15279,15278,15277,15397,15396,15395,15393,15392,15394,15390,15389,15388,15387,15397,15396,15395,15393,15392,15394,15390,15389,15388,15387,44909,44910,44911,44908,15423,15423,15423,15423,15420,48662,15422,15421,15425,15425,15424,18813,18814,18812,18815,18811,18810,15275,15275,15271,15271,15276,15270,15269,13733,13733,13733,13733,13733,13733,15270,15274,15273,15406,15407,15408,15409,15368,15375,15375,15375,15375,15376,15376,15376,15376,15373,15373,15374,15374,15370,15371,15372,15369,29119,15426,15426,15435,15438,15434,15434,15427,15427,15427,15427,15436,15436,15436,15436,15436,15436,15437,15437,15437,15437,15437,15437,15350,15348,15347,15351,15349,15353,15352,15354,15356,15331,15331,15331,15331,15355,15355,15355,15355,15330,15330,15330,15330,15331,15331,15323,15325,15325,15324,15324,15329,15328,15326,15327,15325,15325,15324,15324,15330,15330,15330,15330,15331,15331,15323,34138,15330,15330,34138,34138,15330,34138,15330,15331,15331,15337,15336,15380,39230,39231,36692,39229,36676,34138,15330,15330,34138,34138,15330,34138,15330,15331,15331,36675,36672,36672,36675,36672,36675,36675,36672,15331,15331,15330,15330,15257,15256,15259,15259,15327,15326,29143,29141,29140,29142,29145,29144,29138,29139,29136,29137,29122,29120,29121,29130,29131,29132,29133};
    public static final int[] CONSTRUCTION_ROTATED_SIZES = new int[] {4523};

    public final void readObjectMap(CollisionMap collisionMaps[], SceneGraph worldController, int plane, int baseX, int k, int z, byte abyte0[], int i1, int rotation, int baseY) {

        label0:
        {
            Buffer stream = new Buffer(abyte0);
            int id = -1;
            do {
                int i2 = stream.readUnsignedIntSmartShortCompat();
                if (i2 == 0)
                    break label0;

                id += i2;
                int j2 = 0;

                final int ID = id;

                boolean hotspotFlag = Arrays.stream(CONSTRUCTION_HOTSPOTS).anyMatch(obj -> obj == ID);
                boolean rotatedSizes = Arrays.stream(CONSTRUCTION_ROTATED_SIZES).anyMatch(obj -> obj == ID);

                do {
                    int k2 = stream.readUnsignedShortSmart();
                    if (k2 == 0)
                        break;
                    j2 += k2 - 1;
                    int originalYInRegion = j2 & 0x3f;
                    int originalXInRegion = j2 >> 6 & 0x3f;
                    int j3 = j2 >> 12;
                    int k3 = stream.readUnsignedByte();

                    if(hotspotFlag && Client.instance.settings[950] != 1) {
                        continue;
                    }

                    int type = k3 >> 2;
                    int i4 = k3 & 3;
                    if (j3 == plane && originalXInRegion >= i1 && originalXInRegion < i1 + 8 && originalYInRegion >= k && originalYInRegion < k + 8) {

                        int sizeX;
                        int sizeY;

                        if(OSObjectDefinition.USE_OSRS){
                            OSObjectDefinition definition = OSObjectDefinition.lookup(id);
                            sizeX = definition.sizeX;
                            sizeY = definition.sizeY;
                        } else {
                            ObjectDefinition definition = ObjectDefinition.lookup(id);
                            sizeX = definition.sizeX;
                            sizeY = definition.sizeY;
                        }

                        if (rotatedSizes) {
                            int temp = sizeX;
                            sizeX = sizeY;
                            sizeY = temp;
                        }

                        int x = baseX + ChunkUtil.calculateXAfterRotation(originalXInRegion & 7, originalYInRegion & 7, sizeX, sizeY, rotation);
                        int y = baseY + ChunkUtil.calculateYAfterRotation(originalXInRegion & 7, originalYInRegion & 7, sizeX, sizeY, rotation);
                        if (x > 0 && y > 0 && x < 103 && y < 103) {
                            int l4 = j3;
                            if ((Tiles_renderFlags[1][x][y] & 2) == 2)
                                l4--;
                            CollisionMap class11 = null;
                            if (l4 >= 0)
                                class11 = collisionMaps[l4];
                            System.out.println(String.format("id %d, originalXInRegion %d, originalYInRegion %d, sizeX %d, sizeY %d, rotation %d, x %d, y %d", id, originalXInRegion, originalYInRegion, sizeX, sizeY, rotation, x, y));
                            placeObjectInScene(z, x, y, id, i4 + rotation & 3, type, worldController, class11);
                        }
                    }
                } while (true);
            } while (true);
        }
    }

    private int checkedLight(int color, int light) {
        if (color == -2)
            return Rasterizer3D.MAX_HSL_COLOR_VALUE;
        if (color == -1) {
            if (light < 0)
                light = 0;
            else if (light > 127)
                light = 127;
            light = 127 - light;
            return light;
        }
        light = (light * (color & 0x7f)) / 128;
        if (light < 2)
            light = 2;
        else if (light > 126)
            light = 126;
        return (color & 0xff80) + light;
    }

    public final void loadObjectsInScene(int i, CollisionMap[] collisionMaps, int j, SceneGraph scene, byte[] data) {
        label0:
        {
            Buffer stream = new Buffer(data);
            int l = -1;
            do {
                int i1 = stream.readUnsignedIntSmartShortCompat();
                if (i1 == 0)
                    break label0;
                l += i1;
                int j1 = 0;
                do {
                    int k1 = stream.readUnsignedShortSmart();
                    if (k1 == 0)
                        break;
                    j1 += k1 - 1;
                    int l1 = j1 & 0x3f;
                    int i2 = j1 >> 6 & 0x3f;
                    int j2 = j1 >> 12;
                    int k2 = stream.readUnsignedByte();
                    int l2 = k2 >> 2;
                    int i3 = k2 & 3;
                    int j3 = i2 + i;
                    int k3 = l1 + j;
                    if (j3 > 0 && k3 > 0 && j3 < 103 && k3 < 103 && j2 >= 0 && j2 < 4) {
                        int l3 = j2;
                        if ((Tiles_renderFlags[1][j3][k3] & 2) == 2)
                            l3--;
                        CollisionMap class11 = null;
                        if (l3 >= 0)
                            class11 = collisionMaps[l3];
                        try {
                            placeObjectInScene(j2, j3, k3, l, i3, l2, scene, class11);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } while (true);
            } while (true);
        }
    }
}
