package com.runescape.scene;

import com.runescape.cache.ModelData;
import com.runescape.collection.NodeDeque;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.GameObject;
import com.runescape.entity.ItemLayer;
import com.runescape.entity.Renderable;
import com.runescape.entity.model.Model;
import com.runescape.scene.object.FloorDecoration;
import com.runescape.scene.object.WallDecoration;
import com.runescape.scene.object.BoundaryObject;
import com.runescape.scene.object.tile.SceneTileModel;
import com.runescape.scene.object.tile.SceneTilePaint;
import com.runescape.scene.object.tile.Tile;
import net.runelite.api.Scene;

import java.util.Arrays;

public final class SceneGraph implements Scene {

    public static int TILE_DRAW_DISTANCE = 25;

    private static final int[] anIntArray463 = {53, -53, -53, 53};
    private static final int[] anIntArray464 = {-53, -53, 53, 53};
    private static final int[] anIntArray465 = {-45, 45, 45, -45};
    private static final int[] anIntArray466 = {45, 45, -45, -45};
    private static final int Scene_planesCount;
    private static final Occluder[] Scene_currentOccluders = new Occluder[500];
    private static final int[] anIntArray478 = {19, 55, 38, 155, 255, 110, 137, 205, 76};
    private static final int[] anIntArray479 = {160, 192, 80, 96, 0, 144, 80, 48, 160};
    private static final int[] anIntArray480 = {76, 8, 137, 4, 0, 1, 38, 2, 19};
    private static final int[] anIntArray481 = {0, 0, 2, 0, 0, 2, 1, 1, 0};
    private static final int[] anIntArray482 = {2, 0, 0, 2, 0, 0, 0, 4, 4};
    private static final int[] anIntArray483 = {0, 4, 4, 8, 0, 0, 8, 0, 0};
    private static final int[] anIntArray484 = {1, 1, 0, 0, 0, 8, 0, 0, 8};
    private static final int[] TEXTURE_COLORS = {41, 39248, 41, 4643, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 43086,
            41, 41, 41, 41, 41, 41, 41, 8602, 41, 28992, 41, 41, 41, 41, 41, 5056, 41, 41, 41, 7079, 41, 41, 41, 41, 41,
            41, 41, 41, 41, 41, 3131, 41, 41, 41};
    public static boolean lowMem = true;
    public static int Scene_selectedX = -1;
    public static int Scene_selectedY = -1;
    public static final int DEFAULT_VIEW_DISTANCE = 9;
    public static int viewDistance = DEFAULT_VIEW_DISTANCE;
    private static int tileUpdateCount;
    private static int Scene_plane;
    private static int Scene_drawnCount;
    private static int Scene_cameraXTileMin;
    private static int Scene_cameraXTileMax;
    private static int Scene_cameraYTileMin;
    private static int Scene_cameraYTileMax;
    private static int Scene_cameraXTile;
    private static int Scene_cameraYTile;
    private static int Scene_cameraX;
    private static int Scene_cameraZ;
    private static int Scene_cameraY;
    public static int Scene_cameraPitchSine;
    public static int Scene_cameraPitchCosine;
    public static int Scene_cameraYawSine;
    public static int Scene_cameraYawCosine;
    private static GameObject[] gameObjects = new GameObject[100];
    private static boolean checkClick;
    private static int Scene_selectedScreenX;
    private static int Scene_selectedScreenY;
    private static int Scene_selectedPlane;
    private static int[] Scene_planeOccluderCounts;
    private static Occluder[][] Scene_planeOccluders;
    private static int Scene_currentOccludersCount;
    private static NodeDeque Scene_tilesDeque = new NodeDeque();
    private static boolean[][][][] visibilityMap = new boolean[8][32][(TILE_DRAW_DISTANCE * 2) + 3][(TILE_DRAW_DISTANCE * 2) + 3];
    private static boolean[][] visibleTiles;
    private static int Scene_viewportXCenter;
    private static int Scene_viewportYCenter;
    private static int Scene_viewportXMin;
    private static int Scene_viewportYMin;
    private static int Scene_viewportXMax;
    private static int Scene_viewportYMax;

    static {
        Scene_planesCount = 4;
        Scene_planeOccluderCounts = new int[Scene_planesCount];
        Scene_planeOccluders = new Occluder[Scene_planesCount][500];
    }

    private final int planes;
    private final int xSize;
    private final int ySize;
    public final int[][][] tileHeights;
    private final Tile[][][] tiles;
    private final GameObject[] tempGameObjects;
    public final int[][][] renderTileFlags;
    private final int[] anIntArray486;
    private final int[] anIntArray487;
    private final int[][] tileShape2D = {new int[16], {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1}, {1, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1}, {0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0}, {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1},
            {1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1}};
    private final int[][] tileRotation2D = {{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
            {12, 8, 4, 0, 13, 9, 5, 1, 14, 10, 6, 2, 15, 11, 7, 3},
            {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0},
            {3, 7, 11, 15, 2, 6, 10, 14, 1, 5, 9, 13, 0, 4, 8, 12}};
    private int minPlane;
    private int tempGameObjectsCount;

    public static void rebuildVisibilityMap(){
        visibilityMap = new boolean[8][32][(TILE_DRAW_DISTANCE * 2) + 3][(TILE_DRAW_DISTANCE * 2) + 3];
    }

    public SceneGraph(int[][][] tileHeights) {
        int yLocSize = 104;// was parameter
        int xLocSize = 104;// was parameter
        int zLocSize = 4;// was parameter
        tempGameObjects = new GameObject[5000];
        anIntArray486 = new int[10000];
        anIntArray487 = new int[10000];
        planes = zLocSize;
        xSize = xLocSize;
        ySize = yLocSize;
        tiles = new Tile[zLocSize][xLocSize][yLocSize];
        renderTileFlags = new int[zLocSize][xLocSize + 1][yLocSize + 1];
        this.tileHeights = tileHeights;
        clear();
    }

    /**
     * The class destructor.
     */
    public static void destructor() {
        gameObjects = null;
        Scene_planeOccluderCounts = null;
        Scene_planeOccluders = null;
        Scene_tilesDeque = null;
        visibilityMap = null;
        visibleTiles = null;
    }

    public static void Scene_addOccluder(int z, int lowestX, int lowestZ, int highestX, int highestY, int highestZ, int lowestY, int searchMask) {
        Occluder occluder = new Occluder();
        occluder.minTileX = lowestX / 128;
        occluder.maxTileX = highestX / 128;
        occluder.minTileY = lowestY / 128;
        occluder.maxTileY = highestY / 128;
        occluder.type = searchMask;
        occluder.minX = lowestX;
        occluder.maxX = highestX;
        occluder.minY = lowestY;
        occluder.maxY = highestY;
        occluder.maxZ = highestZ;
        occluder.minZ = lowestZ;
        Scene_planeOccluders[z][Scene_planeOccluderCounts[z]++] = occluder;
    }

    public static void Scene_buildVisiblityMap(int[] var0, int var1, int var2, int width, int height) {
        Scene_viewportXMin = 0;
        Scene_viewportYMin = 0;
        Scene_viewportXMax = width;
        Scene_viewportYMax = height;
        Scene_viewportXCenter = width / 2;
        Scene_viewportYCenter = height / 2;
        boolean[][][][] flags = new boolean[9][32][(TILE_DRAW_DISTANCE * 2) + 3][(TILE_DRAW_DISTANCE * 2) + 3];

        int var6;
        int var7;
        int var8;
        int var9;
        int var11;
        int var12;
        for(var6 = 128; var6 <= 383; var6 += 32) {
            for(var7 = 0; var7 < 2048; var7 += 64) {
                Scene_cameraPitchSine = Rasterizer3D.Rasterizer3D_sine[var6];
                Scene_cameraPitchCosine = Rasterizer3D.Rasterizer3D_cosine[var6];
                Scene_cameraYawSine = Rasterizer3D.Rasterizer3D_sine[var7];
                Scene_cameraYawCosine = Rasterizer3D.Rasterizer3D_cosine[var7];
                var8 = (var6 - 128) / 32;
                var9 = var7 / 64;

                for(int var10 = -26; var10 < 26; ++var10) {
                    for(var11 = -26; var11 < 26; ++var11) {
                        var12 = var10 * 128;
                        int var13 = var11 * 128;
                        boolean var14 = false;

                        for(int var15 = -var1; var15 <= var2; var15 += 128) {
                            if(method4422(var12, var0[var8] + var15, var13)) {
                                var14 = true;
                                break;
                            }
                        }

                        flags[var8][var9][var10 + 1 + 25][var11 + 1 + 25] = var14;
                    }
                }
            }
        }

        for(var6 = 0; var6 < 8; ++var6) {
            for(var7 = 0; var7 < 32; ++var7) {
                for(var8 = -25; var8 < 25; ++var8) {
                    for(var9 = -25; var9 < 25; ++var9) {
                        boolean var16 = false;

                        label76:
                        for(var11 = -1; var11 <= 1; ++var11) {
                            for(var12 = -1; var12 <= 1; ++var12) {
                                if(flags[var6][var7][var8 + var11 + 1 + 25][var9 + var12 + 1 + 25]) {
                                    var16 = true;
                                    break label76;
                                }

                                if(flags[var6][(var7 + 1) % 31][var8 + var11 + 1 + 25][var9 + var12 + 1 + 25]) {
                                    var16 = true;
                                    break label76;
                                }

                                if(flags[var6 + 1][var7][var8 + var11 + 1 + 25][var9 + var12 + 1 + 25]) {
                                    var16 = true;
                                    break label76;
                                }

                                if(flags[var6 + 1][(var7 + 1) % 31][var8 + var11 + 1 + 25][var9 + var12 + 1 + 25]) {
                                    var16 = true;
                                    break label76;
                                }
                            }
                        }

                        visibilityMap[var6][var7][var8 + 25][var9 + 25] = var16;
                    }
                }
            }
        }
    }

    public static void setupViewport(int i, int j, int viewportWidth, int viewportHeight, int[] ai) {
        Scene_viewportXMin = 0;
        Scene_viewportYMin = 0;
        Scene_viewportXMax = viewportWidth;
        Scene_viewportYMax = viewportHeight;
        Scene_viewportXCenter = viewportWidth / 2;
        Scene_viewportYCenter = viewportHeight / 2;
        boolean[][][][] flags = new boolean[9][32][(TILE_DRAW_DISTANCE * 2) + 3][(TILE_DRAW_DISTANCE * 2) + 3];
        for (int zAngle = 128; zAngle <= 384; zAngle += 32) {
            for (int xyAngle = 0; xyAngle < 2048; xyAngle += 64) {
                Scene_cameraPitchSine = Model.SINE[zAngle];
                Scene_cameraPitchCosine = Model.COSINE[zAngle];
                Scene_cameraYawSine = Model.SINE[xyAngle];
                Scene_cameraYawCosine = Model.COSINE[xyAngle];
                int angularZSegment = (zAngle - 128) / 32;
                int angularXYSegment = xyAngle / 64;
                for (int xRelativeToCamera = -26; xRelativeToCamera <= 26; xRelativeToCamera++) {
                    for (int yRelativeToCamera = -26; yRelativeToCamera <= 26; yRelativeToCamera++) {
                        int xRelativeToCameraPos = xRelativeToCamera * 128;
                        int yRelativeToCameraPos = yRelativeToCamera * 128;
                        boolean flag2 = false;
                        for (int k4 = -i; k4 <= j; k4 += 128) {
                            if (!method4422(ai[angularZSegment] + k4, yRelativeToCameraPos, xRelativeToCameraPos))
                                continue;
                            flag2 = true;
                            break;
                        }
                        flags[angularZSegment][angularXYSegment][xRelativeToCamera + TILE_DRAW_DISTANCE + 1][yRelativeToCamera + TILE_DRAW_DISTANCE + 1] = flag2;
                    }
                }
            }
        }

        for (int angularZSegment = 0; angularZSegment < 8; angularZSegment++) {
            for (int angularXYSegment = 0; angularXYSegment < 32; angularXYSegment++) {
                for (int xRelativeToCamera = -TILE_DRAW_DISTANCE; xRelativeToCamera < TILE_DRAW_DISTANCE; xRelativeToCamera++) {
                    for (int yRelativeToCamera = -TILE_DRAW_DISTANCE; yRelativeToCamera < TILE_DRAW_DISTANCE; yRelativeToCamera++) {
                        boolean flag1 = false;
                        label0:
                        for (int l3 = -1; l3 <= 1; l3++) {
                            for (int j4 = -1; j4 <= 1; j4++) {
                                if (flags[angularZSegment][angularXYSegment][xRelativeToCamera + l3 + TILE_DRAW_DISTANCE + 1][yRelativeToCamera + j4 + TILE_DRAW_DISTANCE + 1])
                                    flag1 = true;
                                else if (flags[angularZSegment][(angularXYSegment + 1) % 31][xRelativeToCamera + l3 + TILE_DRAW_DISTANCE + 1][yRelativeToCamera + j4 + TILE_DRAW_DISTANCE + 1])
                                    flag1 = true;
                                else if (flags[angularZSegment + 1][angularXYSegment][xRelativeToCamera + l3 + TILE_DRAW_DISTANCE + 1][yRelativeToCamera + j4 + TILE_DRAW_DISTANCE + 1]) {
                                    flag1 = true;
                                } else {
                                    if (!flags[angularZSegment + 1][(angularXYSegment + 1) % 31][xRelativeToCamera + l3 + TILE_DRAW_DISTANCE + 1][yRelativeToCamera + j4 + TILE_DRAW_DISTANCE + 1])
                                        continue;
                                    flag1 = true;
                                }
                                break label0;
                            }
                        }
                        visibilityMap[angularZSegment][angularXYSegment][xRelativeToCamera + TILE_DRAW_DISTANCE][yRelativeToCamera + TILE_DRAW_DISTANCE] = flag1;
                    }
                }
            }
        }
    }

    private static boolean method4422(int i, int j, int k) {
        int l = j * Scene_cameraYawSine + k * Scene_cameraYawCosine >> 16;
        int i1 = j * Scene_cameraYawCosine - k * Scene_cameraYawSine >> 16;
        int j1 = i * Scene_cameraPitchSine + i1 * Scene_cameraPitchCosine >> 16;
        int k1 = i * Scene_cameraPitchCosine - i1 * Scene_cameraPitchSine >> 16;
        if (j1 < Rasterizer3D.MIN_DEPTH || j1 > Model.MODEL_DRAW_DISTANCE)
            return false;
        int l1 = Scene_viewportXCenter + (l << viewDistance) / j1;
        int i2 = Scene_viewportYCenter + (k1 << viewDistance) / j1;
        return l1 >= Scene_viewportXMin && l1 <= Scene_viewportXMax && i2 >= Scene_viewportYMin && i2 <= Scene_viewportYMax;
    }

    public void clear() {
        for (int zLoc = 0; zLoc < planes; zLoc++)
            for (int xLoc = 0; xLoc < xSize; xLoc++)
                for (int yLoc = 0; yLoc < ySize; yLoc++)
                    tiles[zLoc][xLoc][yLoc] = null;
        for (int plane = 0; plane < Scene_planesCount; plane++) {
            for (int j1 = 0; j1 < Scene_planeOccluderCounts[plane]; j1++)
                Scene_planeOccluders[plane][j1] = null;
            Scene_planeOccluderCounts[plane] = 0;
        }

        for (int i = 0; i < tempGameObjectsCount; i++)
            tempGameObjects[i] = null;
        tempGameObjectsCount = 0;
        Arrays.fill(gameObjects, null);
    }

    public void method275(int zLoc) {
        minPlane = zLoc;
        for (int xLoc = 0; xLoc < xSize; xLoc++) {
            for (int yLoc = 0; yLoc < ySize; yLoc++)
                if (tiles[zLoc][xLoc][yLoc] == null)
                    tiles[zLoc][xLoc][yLoc] = new Tile(zLoc, xLoc, yLoc);
        }
    }

    public void setLinkBelow(int yLoc, int xLoc) {
        Tile tileFirstFloor = tiles[0][xLoc][yLoc];
        for (int zLoc = 0; zLoc < 3; zLoc++) {
            Tile tile = tiles[zLoc][xLoc][yLoc] = tiles[zLoc + 1][xLoc][yLoc];
            if (tile != null) {
                tile.plane--;
                for (int j1 = 0; j1 < tile.gameObjectsCount; j1++) {
                    GameObject gameObject = tile.gameObjects[j1];
                    if (gameObject.getOpcode() == 2 && gameObject.startX == xLoc && gameObject.startY == yLoc)
                        gameObject.plane--;
                }
            }
        }
        if (tiles[0][xLoc][yLoc] == null)
            tiles[0][xLoc][yLoc] = new Tile(0, xLoc, yLoc);
        tiles[0][xLoc][yLoc].linkedBelowTile = tileFirstFloor;
        tiles[3][xLoc][yLoc] = null;
    }

    public void setTileMinPlane(int zLoc, int xLoc, int yLoc, int logicHeight) {
        Tile tile = tiles[zLoc][xLoc][yLoc];
        if (tile != null)
            tiles[zLoc][xLoc][yLoc].minPlane = logicHeight;
    }

    public void addTile(int zLoc, int xLoc, int yLoc, int shape, int i1, int j1, int k1, int l1, int i2, int j2, int k2, int l2, int i3, int j3, int k3, int l3, int i4, int j4, int k4, int l4) {
        if (shape == 0) {
            SceneTilePaint simpleTile = new SceneTilePaint(k2, l2, i3, j3, -1, k4, false);
            for (int lowerZLoc = zLoc; lowerZLoc >= 0; lowerZLoc--)
                if (tiles[lowerZLoc][xLoc][yLoc] == null)
                    tiles[lowerZLoc][xLoc][yLoc] = new Tile(lowerZLoc, xLoc, yLoc);

            tiles[zLoc][xLoc][yLoc].paint = simpleTile;
        } else if (shape == 1) {
            SceneTilePaint simpleTile = new SceneTilePaint(k3, l3, i4, j4, j1, l4, k1 == l1 && k1 == i2 && k1 == j2);
            for (int lowerZLoc = zLoc; lowerZLoc >= 0; lowerZLoc--)
                if (tiles[lowerZLoc][xLoc][yLoc] == null)
                    tiles[lowerZLoc][xLoc][yLoc] = new Tile(lowerZLoc, xLoc, yLoc);

            tiles[zLoc][xLoc][yLoc].paint = simpleTile;
        } else {
            SceneTileModel shapedTile = new SceneTileModel(yLoc, k3, j3, i2, j1, i4, i1, k2, k4, i3, j2, l1, k1, shape, j4, l3, l2, xLoc, l4);
            for (int k5 = zLoc; k5 >= 0; k5--)
                if (tiles[k5][xLoc][yLoc] == null)
                    tiles[k5][xLoc][yLoc] = new Tile(k5, xLoc, yLoc);

            tiles[zLoc][xLoc][yLoc].model = shapedTile;
        }
    }

    public void newFloorDecoration(int xLoc, int yLoc, int zLoc, int zPos, int flags, Renderable renderable, long uid) {
        if (renderable == null)
            return;
        FloorDecoration floorDecoration = new FloorDecoration();
        floorDecoration.renderable = renderable;
        floorDecoration.x = xLoc * 128 + 64;
        floorDecoration.y = yLoc * 128 + 64;
        floorDecoration.z = zPos;
        floorDecoration.tag = uid;
        floorDecoration.flags = flags;
        if (tiles[zLoc][xLoc][yLoc] == null)
            tiles[zLoc][xLoc][yLoc] = new Tile(zLoc, xLoc, yLoc);
        tiles[zLoc][xLoc][yLoc].floorDecoration = floorDecoration;
    }

    public void newGroundItemPile(int xLoc, long uid, Renderable firstNode, int zPos, Renderable secondNode, Renderable thirdNode, int zLoc, int yLoc) {
        ItemLayer groundItemTile = new ItemLayer();
        groundItemTile.first = thirdNode;
        groundItemTile.x = xLoc * 128 + 64;
        groundItemTile.y = yLoc * 128 + 64;
        groundItemTile.z = zPos;
        groundItemTile.tag = uid;
        groundItemTile.second = firstNode;
        groundItemTile.third = secondNode;
        int largestItemDropHeight = 0;
        Tile parentTile = tiles[zLoc][xLoc][yLoc];
        if (parentTile != null) {
            for (int i = 0; i < parentTile.gameObjectsCount; i++)
                if ((parentTile.gameObjects[i].flags & 256) == 256 && parentTile.gameObjects[i].renderable instanceof Model) {
                    Model var14 = (Model) parentTile.gameObjects[i].renderable;
                    var14.calculateBoundsCylinder();
                    if (var14.height > largestItemDropHeight)
                        largestItemDropHeight = var14.height;
                }

        }
        groundItemTile.height = largestItemDropHeight;
        if (tiles[zLoc][xLoc][yLoc] == null)
            tiles[zLoc][xLoc][yLoc] = new Tile(zLoc, xLoc, yLoc);
        tiles[zLoc][xLoc][yLoc].itemLayer = groundItemTile;
    }

    public void addWallObject(int orientation1, Renderable renderable1, long uid, int yLoc, int objectFaceType, int xLoc, Renderable renderable2, int zPos, int orientation2, int zLoc) {
        if (renderable1 == null && renderable2 == null)
            return;
        BoundaryObject boundaryObject = new BoundaryObject();
        boundaryObject.tag = uid;
        boundaryObject.flags = objectFaceType;
        boundaryObject.x = xLoc * 128 + 64;
        boundaryObject.y = yLoc * 128 + 64;
        boundaryObject.z = zPos;
        boundaryObject.renderable1 = renderable1;
        boundaryObject.renderable2 = renderable2;
        boundaryObject.orientationA = orientation1;
        boundaryObject.orientationB = orientation2;
        for (int z = zLoc; z >= 0; z--)
            if (tiles[z][xLoc][yLoc] == null)
                tiles[z][xLoc][yLoc] = new Tile(z, xLoc, yLoc);

        tiles[zLoc][xLoc][yLoc].boundaryObject = boundaryObject;
    }

    public void addWallDecoration(long uid, int yLoc, int orientation2, int zLoc, int xOffset, int zPos, Renderable renderable, int xLoc, int objectRotationType, int yOffset, int orientation) {
        if (renderable == null)
            return;

        WallDecoration wallDecoration = new WallDecoration();
        wallDecoration.tag = uid;
        wallDecoration.mask = objectRotationType;
        wallDecoration.x = xLoc * 128 + 64 + xOffset;
        wallDecoration.y = yLoc * 128 + 64 + yOffset;
        wallDecoration.z = zPos;
        wallDecoration.renderable1 = renderable;
        wallDecoration.orientation = orientation;
        wallDecoration.orientation2 = orientation2;

        for (int z = zLoc; z >= 0; z--)
            if (tiles[z][xLoc][yLoc] == null)
                tiles[z][xLoc][yLoc] = new Tile(z, xLoc, yLoc);
        tiles[zLoc][xLoc][yLoc].wallDecoration = wallDecoration;
    }

    public boolean addTiledObject(long uid, int objectRotationType, int tileHeight, int sizeY, Renderable renderable, int sizeX, int zLoc, int turnValue, int yLoc, int xLoc) {
        if (renderable == null) {
            return true;
        } else {
            int xPos = xLoc * 128 + 64 * sizeX;
            int yPos = yLoc * 128 + 64 * sizeY;
            return addAnimableC(zLoc, xLoc, yLoc, sizeX, sizeY, xPos, yPos, tileHeight, renderable, turnValue, false, uid, objectRotationType);
        }
    }

    public boolean addAnimableA(int zLoc, int turnValue, int k, long uid, int yPos, int halfSizePos, int xPos, Renderable animable, boolean flag) {
        if (animable == null)
            return true;
        int startXLoc = xPos - halfSizePos;
        int startYLoc = yPos - halfSizePos;
        int endXLoc = xPos + halfSizePos;
        int endYLoc = yPos + halfSizePos;
        if (flag) {
            if (turnValue > 640 && turnValue < 1408)
                endYLoc += 128;
            if (turnValue > 1152 && turnValue < 1920)
                endXLoc += 128;
            if (turnValue > 1664 || turnValue < 384)
                startYLoc -= 128;
            if (turnValue > 128 && turnValue < 896)
                startXLoc -= 128;
        }
        startXLoc /= 128;
        startYLoc /= 128;
        endXLoc /= 128;
        endYLoc /= 128;
        return addAnimableC(zLoc, startXLoc, startYLoc, (endXLoc - startXLoc) + 1, (endYLoc - startYLoc) + 1, xPos, yPos, k, animable, turnValue, true, uid, (byte) 0);
    }

    public boolean addToScenePlayerAsObject(int zLoc, int playerYPos, Renderable playerAsObject, int playerTurnValue, int objectEndYLoc, int playerXPos, int playerHeight, int objectStartXLoc, int objectEndXLoc, long uid, int objectStartYLoc) {
        return playerAsObject == null || addAnimableC(zLoc, objectStartXLoc, objectStartYLoc, (objectEndXLoc - objectStartXLoc) + 1, (objectEndYLoc - objectStartYLoc) + 1, playerXPos, playerYPos, playerHeight, playerAsObject, playerTurnValue, true, uid, (byte) 0);
    }

    private boolean addAnimableC(int zLoc, int xLoc, int yLoc, int sizeX, int sizeY, int xPos, int yPos, int tileHeight, Renderable renderable, int turnValue, boolean isDynamic, long uid, int objectRotationType) {
        for (int x = xLoc; x < xLoc + sizeX; x++) {
            for (int y = yLoc; y < yLoc + sizeY; y++) {
                if (x < 0 || y < 0 || x >= xSize || y >= ySize)
                    return false;
                Tile tile = tiles[zLoc][x][y];
                if (tile != null && tile.gameObjectsCount >= 5)
                    return false;
            }

        }

        GameObject gameObject = new GameObject();
        gameObject.tag = uid;
        gameObject.flags = objectRotationType;
        gameObject.plane = zLoc;
        gameObject.centerX = xPos;
        gameObject.centerY = yPos;
        gameObject.z = tileHeight;
        gameObject.renderable = renderable;
        gameObject.orientation = turnValue;
        gameObject.startX = xLoc;
        gameObject.startY = yLoc;
        gameObject.endX = (xLoc + sizeX) - 1;
        gameObject.endY = (yLoc + sizeY) - 1;
        for (int x = xLoc; x < xLoc + sizeX; x++) {
            for (int y = yLoc; y < yLoc + sizeY; y++) {
                int mask = 0;
                if (x > xLoc)
                    mask++;
                if (x < (xLoc + sizeX) - 1)
                    mask += 4;
                if (y > yLoc)
                    mask += 8;
                if (y < (yLoc + sizeY) - 1)
                    mask += 2;
                for (int z = zLoc; z >= 0; z--)
                    if (tiles[z][x][y] == null)
                        tiles[z][x][y] = new Tile(z, x, y);

                Tile tile = tiles[zLoc][x][y];
                tile.gameObjects[tile.gameObjectsCount] = gameObject;
                tile.gameObjectEdgeMasks[tile.gameObjectsCount] = mask;
                tile.gameObjectsEdgeMask |= mask;
                tile.gameObjectsCount++;
            }

        }

        if (isDynamic) {
            tempGameObjects[tempGameObjectsCount++] = gameObject;
        }

        return true;
    }

    public void clearGameObjectCache() {
        for (int i = 0; i < tempGameObjectsCount; i++) {
            GameObject object5 = tempGameObjects[i];
            removeGameObject(object5);
            tempGameObjects[i] = null;
        }

        tempGameObjectsCount = 0;
    }

    private void removeGameObject(GameObject gameObject) {
        for (int x = gameObject.startX; x <= gameObject.endX; x++) {
            for (int y = gameObject.startY; y <= gameObject.endY; y++) {
                Tile tile = tiles[gameObject.plane][x][y];
                if (tile != null) {
                    for (int i = 0; i < tile.gameObjectsCount; i++) {
                        if (tile.gameObjects[i] != gameObject)
                            continue;
                        tile.gameObjectsCount--;
                        for (int i1 = i; i1 < tile.gameObjectsCount; i1++) {
                            tile.gameObjects[i1] = tile.gameObjects[i1 + 1];
                            tile.gameObjectEdgeMasks[i1] = tile.gameObjectEdgeMasks[i1 + 1];
                        }

                        tile.gameObjects[tile.gameObjectsCount] = null;
                        break;
                    }

                    tile.gameObjectsEdgeMask = 0;
                    for (int i = 0; i < tile.gameObjectsCount; i++)
                        tile.gameObjectsEdgeMask |= tile.gameObjectEdgeMasks[i];
                }
            }
        }
    }

    public void method290(int yLoc, int k, int xLoc, int zLoc) { //TODO scale position?
        Tile tile = tiles[zLoc][xLoc][yLoc];
        if (tile == null)
            return;
        WallDecoration wallDecoration = tile.wallDecoration;
        if (wallDecoration != null) {
            int xPos = xLoc * 128 + 64;
            int yPos = yLoc * 128 + 64;
            wallDecoration.x = xPos + ((wallDecoration.x - xPos) * k) / 16;
            wallDecoration.y = yPos + ((wallDecoration.y - yPos) * k) / 16;
        }
    }

    public void removeWallObject(int xLoc, int zLoc, int yLoc) {
        Tile tile = tiles[zLoc][xLoc][yLoc];
        if (tile != null)
            tile.boundaryObject = null;
    }

    public void removeWallDecoration(int yLoc, int zLoc, int xLoc) {
        Tile tile = tiles[zLoc][xLoc][yLoc];
        if (tile != null)
            tile.wallDecoration = null;
    }

    public void removeObjectsOnTile(int zLoc, int xLoc, int yLoc) {
        Tile tile = tiles[zLoc][xLoc][yLoc];
        if (tile == null)
            return;
        for (int j1 = 0; j1 < tile.gameObjectsCount; j1++) {
            GameObject gameObject = tile.gameObjects[j1];
            if ((gameObject.getOpcode()) == 2 && gameObject.startX == xLoc && gameObject.startY == yLoc) {
                removeGameObject(gameObject);
                return;
            }
        }

    }

    public void removeGroundDecoration(int zLoc, int yLoc, int xLoc) {
        Tile tile = tiles[zLoc][xLoc][yLoc];
        if (tile == null)
            return;
        tile.floorDecoration = null;
    }

    public void removeGroundItemPile(int zLoc, int xLoc, int yLoc) {
        Tile tile = tiles[zLoc][xLoc][yLoc];
        if (tile != null)
            tile.itemLayer = null;
    }

    public BoundaryObject getWallObject(int zLoc, int xLoc, int yLoc) {
        Tile tile = tiles[zLoc][xLoc][yLoc];
        if (tile == null)
            return null;
        else
            return tile.boundaryObject;
    }

    public WallDecoration getWallDecoration(int xLoc, int yLoc, int zLoc) {
        Tile tile = tiles[zLoc][xLoc][yLoc];
        if (tile == null)
            return null;
        else
            return tile.wallDecoration;
    }

    public GameObject getObjectOnTile(int xLoc, int yLoc, int zLoc) {
        Tile tile = tiles[zLoc][xLoc][yLoc];
        if (tile == null)
            return null;
        for (int i = 0; i < tile.gameObjectsCount; i++) {
            GameObject gameObject = tile.gameObjects[i];
            if ((gameObject.getOpcode()) == 2 && gameObject.startX == xLoc && gameObject.startY == yLoc)
                return gameObject;
        }
        return null;
    }

    public FloorDecoration getGroundDecoration(int yLoc, int xLoc, int zLoc) {
        Tile tile = tiles[zLoc][xLoc][yLoc];
        if (tile == null || tile.floorDecoration == null)
            return null;
        else
            return tile.floorDecoration;
    }

    public long getWallObjectUid(int zLoc, int xLoc, int yLoc) {
        Tile tile = tiles[zLoc][xLoc][yLoc];
        if (tile == null || tile.boundaryObject == null)
            return 0L;
        else
            return tile.boundaryObject.tag;
    }

    public long getWallDecorationUid(int zLoc, int xLoc, int yLoc) {
        Tile tile = tiles[zLoc][xLoc][yLoc];
        if (tile == null || tile.wallDecoration == null)
            return 0L;
        else
            return tile.wallDecoration.tag;
    }

    public long getGameObjectUid(int zLoc, int xLoc, int yLoc) {
        Tile tile = tiles[zLoc][xLoc][yLoc];
        if (tile == null)
            return 0L;
        for (int i = 0; i < tile.gameObjectsCount; i++) {
            GameObject gameObject = tile.gameObjects[i];
            if (DynamicObject.get_object_opcode(gameObject.tag) == 2 && gameObject.startX == xLoc && gameObject.startY == yLoc)
                return gameObject.tag;
        }
        return 0L;
    }

    public long getGroundDecorationUid(int zLoc, int xLoc, int yLoc) {
        Tile tile = tiles[zLoc][xLoc][yLoc];
        if (tile == null || tile.floorDecoration == null)
            return 0L;
        else
            return tile.floorDecoration.tag;
    }

    /**
     * Retrieves the mask of the object with the given uid at the given location.
     * -1 if there's no object.
     *
     * @param zLoc The zLoc.
     * @param xLoc The xLoc.
     * @param yLoc The yLoc.
     * @param tag  The object's Uid.
     * @return The mask, which is comprised out of the rotation (shifted 6 to the left) and the type (which has a maximum value of 22).
     */
    public int getObjectFlags(int zLoc, int xLoc, int yLoc, long tag) {
        final Tile tile = tiles[zLoc][xLoc][yLoc];
        if (tile == null)
            return -1;
        else if (tile.boundaryObject != null && tile.boundaryObject.tag == tag)
            return tile.boundaryObject.flags & 0xff;
        else if (tile.wallDecoration != null && tile.wallDecoration.tag == tag)
            return tile.wallDecoration.mask & 0xff;
        else if (tile.floorDecoration != null && tile.floorDecoration.tag == tag)
            return tile.floorDecoration.flags & 0xff;
        else {
            for (int i = 0; i < tile.gameObjectsCount; i++)
                if (tile.gameObjects[i].tag == tag)
                    return tile.gameObjects[i].flags & 0xff;
        }
        return -1;
    }
    
    public void shadeModels(int var1, int var2, int var3) {
        for (int zLoc = 0; zLoc < planes; zLoc++) {
            for (int xLoc = 0; xLoc < xSize; xLoc++) {
                for (int yLoc = 0; yLoc < ySize; yLoc++) {
                    Tile tile = tiles[zLoc][xLoc][yLoc];
                    if (tile != null) {
                        BoundaryObject var8 = tile.boundaryObject;
                        ModelData var10;
                        if (var8 != null && var8.renderable1 instanceof ModelData) {
                            ModelData var9 = (ModelData)var8.renderable1;
                            this.method4419(var9, zLoc, xLoc, yLoc, 1, 1);
                            if (var8.renderable2 instanceof ModelData) {
                                var10 = (ModelData)var8.renderable2;
                                this.method4419(var10, zLoc, xLoc, yLoc, 1, 1);
                                ModelData.method4201(var9, var10, 0, 0, 0, false);
                                var8.renderable2 = var10.toModel(var10.ambient, var10.contrast, var1, var2, var3);
                            }

                            var8.renderable1 = var9.toModel(var9.ambient, var9.contrast, var1, var2, var3);
                        }
                        for (int k2 = 0; k2 < tile.gameObjectsCount; k2++) {
                            GameObject var14 = tile.gameObjects[k2];
                            if (var14 != null && var14.renderable instanceof ModelData) {
                                ModelData var11 = (ModelData) var14.renderable;
                                this.method4419(var11, zLoc, xLoc, yLoc, var14.endX - var14.startX + 1, var14.endY - var14.startY + 1);
                                var14.renderable = var11.toModel(var11.ambient, var11.contrast, var1, var2, var3);
                            }
                        }

                        FloorDecoration var13 = tile.floorDecoration;
                        if (var13 != null && var13.renderable instanceof ModelData) {
                            var10 = (ModelData)var13.renderable;
                            method306GroundDecorationOnly(xLoc, zLoc, var10, yLoc);
                            var13.renderable = var10.toModel(var10.ambient, var10.contrast, var1, var2, var3);
                        }
                    }
                }
            }
        }
    }

    private void method306GroundDecorationOnly(int modelXLoc, int modelZLoc, ModelData var1, int modelYLoc) {
        ModelData var6;
        if (modelXLoc < xSize) {
            Tile tile = tiles[modelZLoc][modelXLoc + 1][modelYLoc];
            if (tile != null && tile.floorDecoration != null && tile.floorDecoration.renderable instanceof ModelData) {
                var6 = (ModelData)tile.floorDecoration.renderable;
                ModelData.method4201(var1, var6, 128, 0, 0, true);
            }
        }
        if (modelYLoc < xSize) {
            Tile tile = tiles[modelZLoc][modelXLoc][modelYLoc + 1];
            if (tile != null && tile.floorDecoration != null && tile.floorDecoration.renderable instanceof ModelData) {
                var6 = (ModelData)tile.floorDecoration.renderable;
                ModelData.method4201(var1, var6, 0, 0, 128, true);
            }
        }
        if (modelXLoc < xSize && modelYLoc < ySize) {
            Tile tile = tiles[modelZLoc][modelXLoc + 1][modelYLoc + 1];
            if (tile != null && tile.floorDecoration != null && tile.floorDecoration.renderable instanceof ModelData) {
                var6 = (ModelData)tile.floorDecoration.renderable;
                ModelData.method4201(var1, var6, 128, 0, 128, true);
            }
        }
        if (modelXLoc < xSize && modelYLoc > 0) {
            Tile tile = tiles[modelZLoc][modelXLoc + 1][modelYLoc - 1];
            if (tile != null && tile.floorDecoration != null && tile.floorDecoration.renderable instanceof ModelData) {
                var6 = (ModelData)tile.floorDecoration.renderable;
                ModelData.method4201(var1, var6, 128, 0, -128, true);
            }
        }
    }

    private void method4419(ModelData var1, int var2, int var3, int var4, int var5, int var6) {
        boolean var7 = true;
        int var8 = var3;
        int var9 = var3 + var5;
        int var10 = var4 - 1;
        int var11 = var4 + var6;

        for(int var12 = var2; var12 <= var2 + 1; ++var12) {
            if (var12 != this.planes) {
                for(int var13 = var8; var13 <= var9; ++var13) {
                    if (var13 >= 0 && var13 < this.xSize) {
                        for(int var14 = var10; var14 <= var11; ++var14) {
                            if (var14 >= 0 && var14 < this.ySize && (!var7 || var13 >= var9 || var14 >= var11 || var14 < var4 && var3 != var13)) {
                                Tile var15 = this.tiles[var12][var13][var14];
                                if (var15 != null) {
                                    int var16 = (this.tileHeights[var12][var13 + 1][var14] + this.tileHeights[var12][var13 + 1][var14 + 1] + this.tileHeights[var12][var13][var14] + this.tileHeights[var12][var13][var14 + 1]) / 4 - (this.tileHeights[var2][var3 + 1][var4] + this.tileHeights[var2][var3][var4] + this.tileHeights[var2][var3 + 1][var4 + 1] + this.tileHeights[var2][var3][var4 + 1]) / 4;
                                    BoundaryObject var17 = var15.boundaryObject;
                                    if (var17 != null) {
                                        ModelData var18;
                                        if (var17.renderable1 instanceof ModelData) {
                                            var18 = (ModelData)var17.renderable1;
                                            ModelData.method4201(var1, var18, (1 - var5) * 64 + (var13 - var3) * 128, var16, (var14 - var4) * 128 + (1 - var6) * 64, var7);
                                        }

                                        if (var17.renderable2 instanceof ModelData) {
                                            var18 = (ModelData)var17.renderable2;
                                            ModelData.method4201(var1, var18, (1 - var5) * 64 + (var13 - var3) * 128, var16, (var14 - var4) * 128 + (1 - var6) * 64, var7);
                                        }
                                    }

                                    for(int var23 = 0; var23 < var15.gameObjectsCount; ++var23) {
                                        GameObject var19 = var15.gameObjects[var23];
                                        if (var19 != null && var19.renderable instanceof ModelData) {
                                            ModelData var20 = (ModelData)var19.renderable;
                                            int var21 = var19.endX - var19.startX + 1;
                                            int var22 = var19.endY - var19.startY + 1;
                                            ModelData.method4201(var1, var20, (var21 - var5) * 64 + (var19.startX - var3) * 128, var16, (var19.startY - var4) * 128 + (var22 - var6) * 64, var7);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                --var8;
                var7 = false;
            }
        }

    }

    public void drawTileOnMinimapSprite(int[] pixels, int drawIndex, int zLoc, int xLoc, int yLoc) {
        int leftOverWidth = 512;// was parameter
        Tile tile = tiles[zLoc][xLoc][yLoc];
        if (tile == null)
            return;
        SceneTilePaint simpleTile = tile.paint;
        if (simpleTile != null) {
            int tileRGB = simpleTile.rgb;
            if (tileRGB == 0)
                return;
            for (int i = 0; i < 4; i++) {
                pixels[drawIndex] = tileRGB;
                pixels[drawIndex + 1] = tileRGB;
                pixels[drawIndex + 2] = tileRGB;
                pixels[drawIndex + 3] = tileRGB;
                drawIndex += leftOverWidth;
            }
            return;
        }
        SceneTileModel shapedTile = tile.model;

        if (shapedTile == null) {
            return;
        }

        int shape = shapedTile.shape;
        int rotation = shapedTile.rotation;
        int underlayRGB = shapedTile.underlayRgb;
        int overlayRGB = shapedTile.overlayRgb;
        int[] shapePoints = tileShape2D[shape];
        int[] shapePointIndices = tileRotation2D[rotation];
        int shapePtr = 0;
        if (underlayRGB != 0) {
            for (int i = 0; i < 4; i++) {
                pixels[drawIndex] = shapePoints[shapePointIndices[shapePtr++]] != 0 ? overlayRGB : underlayRGB;
                pixels[drawIndex + 1] = shapePoints[shapePointIndices[shapePtr++]] != 0 ? overlayRGB : underlayRGB;
                pixels[drawIndex + 2] = shapePoints[shapePointIndices[shapePtr++]] != 0 ? overlayRGB : underlayRGB;
                pixels[drawIndex + 3] = shapePoints[shapePointIndices[shapePtr++]] != 0 ? overlayRGB : underlayRGB;
                drawIndex += leftOverWidth;
            }
            return;
        }
        for (int i = 0; i < 4; i++) {
            if (shapePoints[shapePointIndices[shapePtr++]] != 0)
                pixels[drawIndex] = overlayRGB;
            if (shapePoints[shapePointIndices[shapePtr++]] != 0)
                pixels[drawIndex + 1] = overlayRGB;
            if (shapePoints[shapePointIndices[shapePtr++]] != 0)
                pixels[drawIndex + 2] = overlayRGB;
            if (shapePoints[shapePointIndices[shapePtr++]] != 0)
                pixels[drawIndex + 3] = overlayRGB;
            drawIndex += leftOverWidth;
        }
    }

    /**
     * Clicks on the screen and requests recomputation of the clicked tile.
     *
     * @param clickY The click's Y-coordinate on the applet.
     * @param clickX The click's X-coordinate on the applet.
     */
    public void clickTile(int clickY, int clickX, int clickZ) {
        checkClick = true;
        Scene_selectedScreenX = clickX;
        Scene_selectedScreenY = clickY;
        Scene_selectedPlane = clickZ;
        Scene_selectedX = -1;
        Scene_selectedY = -1;
    }

    /**
     * Renders the terrain.
     * The coordinates use the WorldCoordinate Axes but the modelWorld coordinates.
     *
     * @param cameraXPos The cameraViewpoint's X-coordinate.
     * @param cameraYPos The cameraViewpoint's Y-coordinate.
     * @param camAngleXY The cameraAngle in the XY-plain.
     * @param cameraZPos The cameraViewpoint's X-coordinate.
     * @param planeZ     The plain the camera's looking at.
     * @param camAngleZ  The cameraAngle on the Z-axis.
     */
    public void render(int cameraXPos, int cameraYPos, int camAngleXY, int cameraZPos, int planeZ, int camAngleZ) {
        if (cameraXPos < 0)
            cameraXPos = 0;
        else if (cameraXPos >= xSize * 128)
            cameraXPos = xSize * 128 - 1;
        if (cameraYPos < 0)
            cameraYPos = 0;
        else if (cameraYPos >= ySize * 128)
            cameraYPos = ySize * 128 - 1;
        Scene_drawnCount++;
        Scene_cameraPitchSine = Model.SINE[camAngleZ];
        Scene_cameraPitchCosine = Model.COSINE[camAngleZ];
        Scene_cameraYawSine = Model.SINE[camAngleXY];
        Scene_cameraYawCosine = Model.COSINE[camAngleXY];
        visibleTiles = visibilityMap[(camAngleZ - 128) / 32][camAngleXY / 64];
        Scene_cameraX = cameraXPos;
        Scene_cameraZ = cameraZPos;
        Scene_cameraY = cameraYPos;
        Scene_cameraXTile = cameraXPos / 128;
        Scene_cameraYTile = cameraYPos / 128;
        Scene_plane = planeZ;
        Scene_cameraXTileMin = Scene_cameraXTile - TILE_DRAW_DISTANCE;
        if (Scene_cameraXTileMin < 0)
            Scene_cameraXTileMin = 0;
        Scene_cameraYTileMin = Scene_cameraYTile - TILE_DRAW_DISTANCE;
        if (Scene_cameraYTileMin < 0)
            Scene_cameraYTileMin = 0;
        Scene_cameraXTileMax = Scene_cameraXTile + TILE_DRAW_DISTANCE;
        if (Scene_cameraXTileMax > xSize)
            Scene_cameraXTileMax = xSize;
        Scene_cameraYTileMax = Scene_cameraYTile + TILE_DRAW_DISTANCE;
        if (Scene_cameraYTileMax > ySize)
            Scene_cameraYTileMax = ySize;
        occlude();
        tileUpdateCount = 0;
        for (int zLoc = minPlane; zLoc < planes; zLoc++) {
            Tile[][] planeTiles = tiles[zLoc];
            for (int xLoc = Scene_cameraXTileMin; xLoc < Scene_cameraXTileMax; xLoc++) {
                for (int yLoc = Scene_cameraYTileMin; yLoc < Scene_cameraYTileMax; yLoc++) {
                    Tile tile = planeTiles[xLoc][yLoc];
                    if (tile != null)
                        if (tile.minPlane > planeZ || !visibleTiles[(xLoc - Scene_cameraXTile) + TILE_DRAW_DISTANCE][(yLoc - Scene_cameraYTile) + TILE_DRAW_DISTANCE] && tileHeights[zLoc][xLoc][yLoc] - cameraZPos < 2000) {
                            tile.drawPrimary = false;
                            tile.drawSecondary = false;
                            tile.drawGameObjectEdges = 0;
                        } else {
                            tile.drawPrimary = true;
                            tile.drawSecondary = true;
                            tile.drawGameObjects = tile.gameObjectsCount > 0;
                            tileUpdateCount++;
                        }
                }
            }
        }

        for (int zLoc = minPlane; zLoc < planes; zLoc++) {
            Tile[][] plane = tiles[zLoc];
            for (int dX = -TILE_DRAW_DISTANCE; dX <= 0; dX++) {
                int xLoc1 = Scene_cameraXTile + dX;
                int xLoc2 = Scene_cameraXTile - dX;
                if (xLoc1 >= Scene_cameraXTileMin || xLoc2 < Scene_cameraXTileMax) {
                    for (int dY = -TILE_DRAW_DISTANCE; dY <= 0; dY++) {
                        int yLoc1 = Scene_cameraYTile + dY;
                        int yLoc2 = Scene_cameraYTile - dY;
                        if (xLoc1 >= Scene_cameraXTileMin) {
                            if (yLoc1 >= Scene_cameraYTileMin) {
                                Tile tile = plane[xLoc1][yLoc1];
                                if (tile != null && tile.drawPrimary)
                                    drawTile(tile, true);
                            }
                            if (yLoc2 < Scene_cameraYTileMax) {
                                Tile tile = plane[xLoc1][yLoc2];
                                if (tile != null && tile.drawPrimary)
                                    drawTile(tile, true);
                            }
                        }
                        if (xLoc2 < Scene_cameraXTileMax) {
                            if (yLoc1 >= Scene_cameraYTileMin) {
                                Tile tile = plane[xLoc2][yLoc1];
                                if (tile != null && tile.drawPrimary)
                                    drawTile(tile, true);
                            }
                            if (yLoc2 < Scene_cameraYTileMax) {
                                Tile tile = plane[xLoc2][yLoc2];
                                if (tile != null && tile.drawPrimary)
                                    drawTile(tile, true);
                            }
                        }
                        if (tileUpdateCount == 0) {
                            checkClick = false;
                            return;
                        }
                    }
                }
            }
        }

        for (int zLoc = minPlane; zLoc < planes; zLoc++) {
            Tile[][] plane = tiles[zLoc];
            for (int dX = -TILE_DRAW_DISTANCE; dX <= 0; dX++) {
                int xLoc1 = Scene_cameraXTile + dX;
                int xLoc2 = Scene_cameraXTile - dX;
                if (xLoc1 >= Scene_cameraXTileMin || xLoc2 < Scene_cameraXTileMax) {
                    for (int dY = -TILE_DRAW_DISTANCE; dY <= 0; dY++) {
                        int yLoc1 = Scene_cameraYTile + dY;
                        int yLoc2 = Scene_cameraYTile - dY;
                        if (xLoc1 >= Scene_cameraXTileMin) {
                            if (yLoc1 >= Scene_cameraYTileMin) {
                                Tile tile = plane[xLoc1][yLoc1];
                                if (tile != null && tile.drawPrimary)
                                    drawTile(tile, false);
                            }
                            if (yLoc2 < Scene_cameraYTileMax) {
                                Tile tile = plane[xLoc1][yLoc2];
                                if (tile != null && tile.drawPrimary)
                                    drawTile(tile, false);
                            }
                        }
                        if (xLoc2 < Scene_cameraXTileMax) {
                            if (yLoc1 >= Scene_cameraYTileMin) {
                                Tile tile = plane[xLoc2][yLoc1];
                                if (tile != null && tile.drawPrimary)
                                    drawTile(tile, false);
                            }
                            if (yLoc2 < Scene_cameraYTileMax) {
                                Tile tile = plane[xLoc2][yLoc2];
                                if (tile != null && tile.drawPrimary)
                                    drawTile(tile, false);
                            }
                        }
                        if (tileUpdateCount == 0) {
                            checkClick = false;
                            return;
                        }
                    }

                }
            }

        }

        checkClick = false;
    }

    private void drawTile(Tile tile, boolean flag) {
        Scene_tilesDeque.addFirst(tile);
        do {
            Tile currentTile;
            do {
                currentTile = (Tile) Scene_tilesDeque.removeLast();
                if (currentTile == null)
                    return;
            } while (!currentTile.drawSecondary);
            int tileX = currentTile.x;
            int tileY = currentTile.y;
            int plane = currentTile.plane;
            int l = currentTile.originalPlane;
            Tile[][] tiles = this.tiles[plane];
            if (currentTile.drawPrimary) {
                if (flag) {
                    if (plane > 0) {
                        final Tile belowTile = this.tiles[plane - 1][tileX][tileY];
                        if (belowTile != null && belowTile.drawSecondary)
                            continue;
                    }
                    if (tileX <= Scene_cameraXTile && tileX > Scene_cameraXTileMin) {
                        final Tile westTile = tiles[tileX - 1][tileY];
                        if (westTile != null && westTile.drawSecondary
                                && (westTile.drawPrimary || (currentTile.gameObjectsEdgeMask & 1) == 0))
                            continue;
                    }
                    if (tileX >= Scene_cameraXTile && tileX < Scene_cameraXTileMax - 1) {
                        final Tile eastTile = tiles[tileX + 1][tileY];
                        if (eastTile != null && eastTile.drawSecondary
                                && (eastTile.drawPrimary || (currentTile.gameObjectsEdgeMask & 4) == 0))
                            continue;
                    }
                    if (tileY <= Scene_cameraYTile && tileY > Scene_cameraYTileMin) {
                        final Tile southTile = tiles[tileX][tileY - 1];
                        if (southTile != null && southTile.drawSecondary
                                && (southTile.drawPrimary || (currentTile.gameObjectsEdgeMask & 8) == 0))
                            continue;
                    }
                    if (tileY >= Scene_cameraYTile && tileY < Scene_cameraYTileMax - 1) {
                        final Tile northTile = tiles[tileX][tileY + 1];
                        if (northTile != null && northTile.drawSecondary
                                && (northTile.drawPrimary || (currentTile.gameObjectsEdgeMask & 2) == 0))
                            continue;
                    }
                } else {
                    flag = true;
                }
                currentTile.drawPrimary = false;

                if (currentTile.linkedBelowTile != null) {

                    final Tile linkedBelowTile = currentTile.linkedBelowTile;

                    if (linkedBelowTile.paint != null) {
                        if (!setAndGetRenderTileFlag(0, tileX, tileY))
                            drawTileUnderlay(linkedBelowTile.paint, 0, tileX, tileY, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine, Scene_cameraYawCosine);
                    } else if (linkedBelowTile.model != null && !setAndGetRenderTileFlag(0, tileX, tileY))
                        drawTileOverlay(linkedBelowTile.model, tileX, tileY, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine,
                                Scene_cameraYawCosine);
                    BoundaryObject boundaryObject = linkedBelowTile.boundaryObject;
                    if (boundaryObject != null)
                        boundaryObject.renderable1.renderDraw(0, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine, Scene_cameraYawCosine,
                                boundaryObject.x - Scene_cameraX, boundaryObject.z - Scene_cameraZ, boundaryObject.y - Scene_cameraY,
                                boundaryObject.tag);
                    for (int i2 = 0; i2 < linkedBelowTile.gameObjectsCount; i2++) {
                        GameObject gameObject = linkedBelowTile.gameObjects[i2];
                        if (gameObject != null)
                            gameObject.renderable.renderDraw(gameObject.orientation, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine, Scene_cameraYawCosine, gameObject.centerX - Scene_cameraX, gameObject.z - Scene_cameraZ, gameObject.centerY - Scene_cameraY, gameObject.tag);
                    }

                }
                boolean flag1 = false;
                if (currentTile.paint != null) {
                    if (!setAndGetRenderTileFlag(l, tileX, tileY)) {
                        flag1 = true;
                        drawTileUnderlay(currentTile.paint, l, tileX, tileY, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine, Scene_cameraYawCosine);
                    }
                } else if (currentTile.model != null && !setAndGetRenderTileFlag(l, tileX, tileY)) {
                    flag1 = true;
                    drawTileOverlay(currentTile.model, tileX, tileY, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine,
                            Scene_cameraYawCosine);
                }
                int j1 = 0;
                int j2 = 0;
                BoundaryObject boundaryObject = currentTile.boundaryObject;
                WallDecoration wallDecoration = currentTile.wallDecoration;
                if (boundaryObject != null || wallDecoration != null) {
                    if (Scene_cameraXTile == tileX)
                        j1++;
                    else if (Scene_cameraXTile < tileX)
                        j1 += 2;
                    if (Scene_cameraYTile == tileY)
                        j1 += 3;
                    else if (Scene_cameraYTile > tileY)
                        j1 += 6;
                    j2 = anIntArray478[j1];
                    currentTile.orientationRelativeToCamera = anIntArray480[j1];
                }
                if (boundaryObject != null) {
                    if ((boundaryObject.orientationA & anIntArray479[j1]) != 0) {
                        if (boundaryObject.orientationA == 16) {
                            currentTile.drawGameObjectEdges = 3;
                            currentTile.anInt1326 = anIntArray481[j1];
                            currentTile.anInt1327 = 3 - currentTile.anInt1326;
                        } else if (boundaryObject.orientationA == 32) {
                            currentTile.drawGameObjectEdges = 6;
                            currentTile.anInt1326 = anIntArray482[j1];
                            currentTile.anInt1327 = 6 - currentTile.anInt1326;
                        } else if (boundaryObject.orientationA == 64) {
                            currentTile.drawGameObjectEdges = 12;
                            currentTile.anInt1326 = anIntArray483[j1];
                            currentTile.anInt1327 = 12 - currentTile.anInt1326;
                        } else {
                            currentTile.drawGameObjectEdges = 9;
                            currentTile.anInt1326 = anIntArray484[j1];
                            currentTile.anInt1327 = 9 - currentTile.anInt1326;
                        }
                    } else {
                        currentTile.drawGameObjectEdges = 0;
                    }
                    if ((boundaryObject.orientationA & j2) != 0 && !method321(l, tileX, tileY, boundaryObject.orientationA))
                        boundaryObject.renderable1.renderDraw(0, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine, Scene_cameraYawCosine,
                                boundaryObject.x - Scene_cameraX, boundaryObject.z - Scene_cameraZ,
                                boundaryObject.y - Scene_cameraY, boundaryObject.tag);
                    if ((boundaryObject.orientationB & j2) != 0 && !method321(l, tileX, tileY, boundaryObject.orientationB))
                        boundaryObject.renderable2.renderDraw(0, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine, Scene_cameraYawCosine,
                                boundaryObject.x - Scene_cameraX, boundaryObject.z - Scene_cameraZ,
                                boundaryObject.y - Scene_cameraY, boundaryObject.tag);
                }
                if (wallDecoration != null && !hasVisibleClusterSurrounding(l, tileX, tileY, wallDecoration.renderable1.height))
                    if ((wallDecoration.orientation & j2) != 0)
                        wallDecoration.renderable1.renderDraw(wallDecoration.orientation2, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine,
                                Scene_cameraYawCosine, wallDecoration.x - Scene_cameraX, wallDecoration.z - Scene_cameraZ,
                                wallDecoration.y - Scene_cameraY, wallDecoration.tag);
                    else if ((wallDecoration.orientation & 0x300) != 0) {
                        int j4 = wallDecoration.x - Scene_cameraX;
                        int l5 = wallDecoration.z - Scene_cameraZ;
                        int k6 = wallDecoration.y - Scene_cameraY;
                        int i8 = wallDecoration.orientation2;
                        int k9;
                        if (i8 == 1 || i8 == 2)
                            k9 = -j4;
                        else
                            k9 = j4;
                        int k10;
                        if (i8 == 2 || i8 == 3)
                            k10 = -k6;
                        else
                            k10 = k6;
                        if ((wallDecoration.orientation & 0x100) != 0 && k10 < k9) {
                            int i11 = j4 + anIntArray463[i8];
                            int k11 = k6 + anIntArray464[i8];
                            wallDecoration.renderable1.renderDraw(i8 * 512 + 256, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine,
                                    Scene_cameraYawCosine, i11, l5, k11, wallDecoration.tag);
                        }
                        if ((wallDecoration.orientation & 0x200) != 0 && k10 > k9) {
                            int j11 = j4 + anIntArray465[i8];
                            int l11 = k6 + anIntArray466[i8];
                            wallDecoration.renderable1.renderDraw(i8 * 512 + 1280 & 0x7ff, Scene_cameraPitchSine, Scene_cameraPitchCosine,
                                    Scene_cameraYawSine, Scene_cameraYawCosine, j11, l5, l11, wallDecoration.tag);
                        }
                    }
                if (flag1) {
                    FloorDecoration floorDecoration = currentTile.floorDecoration;
                    if (floorDecoration != null)
                        floorDecoration.renderable.renderDraw(0, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine, Scene_cameraYawCosine,
                                floorDecoration.x - Scene_cameraX, floorDecoration.z - Scene_cameraZ, floorDecoration.y - Scene_cameraY,
                                floorDecoration.tag);
                    ItemLayer groundItemTile = currentTile.itemLayer;
                    if (groundItemTile != null && groundItemTile.height == 0) {
                        if (groundItemTile.second != null)
                            groundItemTile.second.renderDraw(0, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine, Scene_cameraYawCosine,
                                    groundItemTile.x - Scene_cameraX, groundItemTile.z - Scene_cameraZ,
                                    groundItemTile.y - Scene_cameraY, groundItemTile.tag);
                        if (groundItemTile.third != null)
                            groundItemTile.third.renderDraw(0, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine, Scene_cameraYawCosine,
                                    groundItemTile.x - Scene_cameraX, groundItemTile.z - Scene_cameraZ,
                                    groundItemTile.y - Scene_cameraY, groundItemTile.tag);
                        if (groundItemTile.first != null)
                            groundItemTile.first.renderDraw(0, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine, Scene_cameraYawCosine,
                                    groundItemTile.x - Scene_cameraX, groundItemTile.z - Scene_cameraZ,
                                    groundItemTile.y - Scene_cameraY, groundItemTile.tag);
                    }
                }
                int k4 = currentTile.gameObjectsEdgeMask;
                if (k4 != 0) {
                    if (tileX < Scene_cameraXTile && (k4 & 4) != 0) {
                        final Tile eastTile = tiles[tileX + 1][tileY];
                        if (eastTile != null && eastTile.drawSecondary)
                            Scene_tilesDeque.addFirst(eastTile);
                    }
                    if (tileY < Scene_cameraYTile && (k4 & 2) != 0) {
                        final Tile northTile = tiles[tileX][tileY + 1];
                        if (northTile != null && northTile.drawSecondary)
                            Scene_tilesDeque.addFirst(northTile);
                    }
                    if (tileX > Scene_cameraXTile && (k4 & 1) != 0) {
                        final Tile westTile = tiles[tileX - 1][tileY];
                        if (westTile != null && westTile.drawSecondary)
                            Scene_tilesDeque.addFirst(westTile);
                    }
                    if (tileY > Scene_cameraYTile && (k4 & 8) != 0) {
                        final Tile southTile = tiles[tileX][tileY - 1];
                        if (southTile != null && southTile.drawSecondary)
                            Scene_tilesDeque.addFirst(southTile);
                    }
                }
            }
            if (currentTile.drawGameObjectEdges != 0) {
                boolean flag2 = true;
                for (int k1 = 0; k1 < currentTile.gameObjectsCount; k1++) {
                    if (currentTile.gameObjects[k1].lastRenderTick == Scene_drawnCount || (currentTile.gameObjectEdgeMasks[k1]
                            & currentTile.drawGameObjectEdges) != currentTile.anInt1326)
                        continue;
                    flag2 = false;
                    break;
                }

                if (flag2) {
                    BoundaryObject boundaryObject = currentTile.boundaryObject;
                    if (!method321(l, tileX, tileY, boundaryObject.orientationA))
                        boundaryObject.renderable1.renderDraw(0, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine, Scene_cameraYawCosine,
                                boundaryObject.x - Scene_cameraX, boundaryObject.z - Scene_cameraZ,
                                boundaryObject.y - Scene_cameraY, boundaryObject.tag);
                    currentTile.drawGameObjectEdges = 0;
                }
            }
            if (currentTile.drawGameObjects)
                try {
                    int gameObjectsCount = currentTile.gameObjectsCount;
                    currentTile.drawGameObjects = false;
                    int l1 = 0;
                    tileObjectLoop:
                    for (int k2 = 0; k2 < gameObjectsCount; k2++) {

                        final GameObject tileObject = currentTile.gameObjects[k2];

                        if (tileObject.lastRenderTick == Scene_drawnCount)
                            continue;

                        for (int xLoc = tileObject.startX; xLoc <= tileObject.endX; xLoc++) {
                            for (int yLoc = tileObject.startY; yLoc <= tileObject.endY; yLoc++) {
                                final Tile tileTouched = tiles[xLoc][yLoc];
                                if (!tileTouched.drawPrimary) {
                                    if (tileTouched.drawGameObjectEdges == 0)
                                        continue;
                                    int l6 = 0;
                                    if (xLoc > tileObject.startX)
                                        l6++;
                                    if (xLoc < tileObject.endX)
                                        l6 += 4;
                                    if (yLoc > tileObject.startY)
                                        l6 += 8;
                                    if (yLoc < tileObject.endY)
                                        l6 += 2;
                                    if ((l6 & tileTouched.drawGameObjectEdges) != currentTile.anInt1327)
                                        continue;
                                }
                                currentTile.drawGameObjects = true;
                                continue tileObjectLoop;
                            }
                        }

                        gameObjects[l1++] = tileObject;
                        int i5 = Scene_cameraXTile - tileObject.startX;
                        int i6 = tileObject.endX - Scene_cameraXTile;
                        if (i6 > i5)
                            i5 = i6;
                        int i7 = Scene_cameraYTile - tileObject.startY;
                        int j8 = tileObject.endY - Scene_cameraYTile;
                        if (j8 > i7)
                            tileObject.anInt527 = i5 + j8;
                        else
                            tileObject.anInt527 = i5 + i7;
                    }

                    while (l1 > 0) {
                        int i3 = -50;
                        int l3 = -1;
                        for (int j5 = 0; j5 < l1; j5++) {
                            GameObject class28_2 = gameObjects[j5];
                            if (class28_2.lastRenderTick != Scene_drawnCount)
                                if (class28_2.anInt527 > i3) {
                                    i3 = class28_2.anInt527;
                                    l3 = j5;
                                } else if (class28_2.anInt527 == i3) {
                                    int j7 = class28_2.centerX - Scene_cameraX;
                                    int k8 = class28_2.centerY - Scene_cameraY;
                                    int l9 = gameObjects[l3].centerX - Scene_cameraX;
                                    int l10 = gameObjects[l3].centerY - Scene_cameraY;
                                    if (j7 * j7 + k8 * k8 > l9 * l9 + l10 * l10)
                                        l3 = j5;
                                }
                        }

                        if (l3 == -1)
                            break;
                        GameObject gameObject = gameObjects[l3];
                        gameObject.lastRenderTick = Scene_drawnCount;
                        if (!hasVisibleClusterWithin(l, gameObject.startX, gameObject.endX, gameObject.startY,
                                gameObject.endY, gameObject.renderable.height))
                            gameObject.renderable.renderDraw(gameObject.orientation, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine,
                                    Scene_cameraYawCosine, gameObject.centerX - Scene_cameraX, gameObject.z - Scene_cameraZ,
                                    gameObject.centerY - Scene_cameraY, gameObject.tag);
                        for (int k7 = gameObject.startX; k7 <= gameObject.endX; k7++) {
                            for (int l8 = gameObject.startY; l8 <= gameObject.endY; l8++) {
                                Tile class30_sub3_22 = tiles[k7][l8];
                                if (class30_sub3_22.drawGameObjectEdges != 0)
                                    Scene_tilesDeque.addFirst(class30_sub3_22);
                                else if ((k7 != tileX || l8 != tileY) && class30_sub3_22.drawSecondary)
                                    Scene_tilesDeque.addFirst(class30_sub3_22);
                            }

                        }

                    }
                    if (currentTile.drawGameObjects)
                        continue;
                } catch (Exception _ex) {
                    currentTile.drawGameObjects = false;
                }
            if (!currentTile.drawSecondary || currentTile.drawGameObjectEdges != 0)
                continue;
            if (tileX <= Scene_cameraXTile && tileX > Scene_cameraXTileMin) {
                Tile class30_sub3_8 = tiles[tileX - 1][tileY];
                if (class30_sub3_8 != null && class30_sub3_8.drawSecondary)
                    continue;
            }
            if (tileX >= Scene_cameraXTile && tileX < Scene_cameraXTileMax - 1) {
                Tile class30_sub3_9 = tiles[tileX + 1][tileY];
                if (class30_sub3_9 != null && class30_sub3_9.drawSecondary)
                    continue;
            }
            if (tileY <= Scene_cameraYTile && tileY > Scene_cameraYTileMin) {
                Tile class30_sub3_10 = tiles[tileX][tileY - 1];
                if (class30_sub3_10 != null && class30_sub3_10.drawSecondary)
                    continue;
            }
            if (tileY >= Scene_cameraYTile && tileY < Scene_cameraYTileMax - 1) {
                Tile class30_sub3_11 = tiles[tileX][tileY + 1];
                if (class30_sub3_11 != null && class30_sub3_11.drawSecondary)
                    continue;
            }
            currentTile.drawSecondary = false;
            tileUpdateCount--;
            ItemLayer groundItemTile = currentTile.itemLayer;
            if (groundItemTile != null && groundItemTile.height != 0) {
                if (groundItemTile.second != null)
                    groundItemTile.second.renderDraw(0,
                            Scene_cameraPitchSine, Scene_cameraPitchCosine
                            , Scene_cameraYawSine, Scene_cameraYawCosine,
                            groundItemTile.x - Scene_cameraX,
                            groundItemTile.z - Scene_cameraZ - groundItemTile.height,
                            groundItemTile.y - Scene_cameraY,
                            groundItemTile.tag
                    );
                if (groundItemTile.third != null)
                    groundItemTile.third.renderDraw(0, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine, Scene_cameraYawCosine,
                            groundItemTile.x - Scene_cameraX, groundItemTile.z - Scene_cameraZ - groundItemTile.height,
                            groundItemTile.y - Scene_cameraY, groundItemTile.tag);
                if (groundItemTile.first != null)
                    groundItemTile.first.renderDraw(0, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine, Scene_cameraYawCosine,
                            groundItemTile.x - Scene_cameraX, groundItemTile.z - Scene_cameraZ - groundItemTile.height,
                            groundItemTile.y - Scene_cameraY, groundItemTile.tag);
            }
            if (currentTile.orientationRelativeToCamera != 0) {
                WallDecoration wallDecoration = currentTile.wallDecoration;
                if (wallDecoration != null && !hasVisibleClusterSurrounding(l, tileX, tileY, wallDecoration.renderable1.height))
                    if ((wallDecoration.orientation & currentTile.orientationRelativeToCamera) != 0)
                        wallDecoration.renderable1.renderDraw(wallDecoration.orientation2, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine,
                                Scene_cameraYawCosine, wallDecoration.x - Scene_cameraX, wallDecoration.z - Scene_cameraZ,
                                wallDecoration.y - Scene_cameraY, wallDecoration.tag);
                    else if ((wallDecoration.orientation & 0x300) != 0) {
                        int l2 = wallDecoration.x - Scene_cameraX;
                        int j3 = wallDecoration.z - Scene_cameraZ;
                        int i4 = wallDecoration.y - Scene_cameraY;
                        int k5 = wallDecoration.orientation2;
                        int j6;
                        if (k5 == 1 || k5 == 2)
                            j6 = -l2;
                        else
                            j6 = l2;
                        int l7;
                        if (k5 == 2 || k5 == 3)
                            l7 = -i4;
                        else
                            l7 = i4;
                        if ((wallDecoration.orientation & 0x100) != 0 && l7 >= j6) {
                            int i9 = l2 + anIntArray463[k5];
                            int i10 = i4 + anIntArray464[k5];
                            wallDecoration.renderable1.renderDraw(k5 * 512 + 256, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine,
                                    Scene_cameraYawCosine, i9, j3, i10, wallDecoration.tag);
                        }
                        if ((wallDecoration.orientation & 0x200) != 0 && l7 <= j6) {
                            int j9 = l2 + anIntArray465[k5];
                            int j10 = i4 + anIntArray466[k5];
                            wallDecoration.renderable1.renderDraw(k5 * 512 + 1280 & 0x7ff, Scene_cameraPitchSine, Scene_cameraPitchCosine,
                                    Scene_cameraYawSine, Scene_cameraYawCosine, j9, j3, j10, wallDecoration.tag);
                        }
                    }
                BoundaryObject boundaryObject = currentTile.boundaryObject;
                if (boundaryObject != null) {
                    if ((boundaryObject.orientationB & currentTile.orientationRelativeToCamera) != 0
                            && !method321(l, tileX, tileY, boundaryObject.orientationB))
                        boundaryObject.renderable2.renderDraw(0, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine, Scene_cameraYawCosine,
                                boundaryObject.x - Scene_cameraX, boundaryObject.z - Scene_cameraZ,
                                boundaryObject.y - Scene_cameraY, boundaryObject.tag);
                    if ((boundaryObject.orientationA & currentTile.orientationRelativeToCamera) != 0
                            && !method321(l, tileX, tileY, boundaryObject.orientationA))
                        boundaryObject.renderable1.renderDraw(0, Scene_cameraPitchSine, Scene_cameraPitchCosine, Scene_cameraYawSine, Scene_cameraYawCosine,
                                boundaryObject.x - Scene_cameraX, boundaryObject.z - Scene_cameraZ,
                                boundaryObject.y - Scene_cameraY, boundaryObject.tag);
                }
            }
            if (plane < planes - 1) {
                Tile class30_sub3_12 = this.tiles[plane + 1][tileX][tileY];
                if (class30_sub3_12 != null && class30_sub3_12.drawSecondary)
                    Scene_tilesDeque.addFirst(class30_sub3_12);
            }
            if (tileX < Scene_cameraXTile) {
                final Tile tileEast = tiles[tileX + 1][tileY];
                if (tileEast != null && tileEast.drawSecondary)
                    Scene_tilesDeque.addFirst(tileEast);
            }
            if (tileY < Scene_cameraYTile) {
                final Tile tileNorth = tiles[tileX][tileY + 1];
                if (tileNorth != null && tileNorth.drawSecondary)
                    Scene_tilesDeque.addFirst(tileNorth);
            }
            if (tileX > Scene_cameraXTile) {
                final Tile tileWest = tiles[tileX - 1][tileY];
                if (tileWest != null && tileWest.drawSecondary)
                    Scene_tilesDeque.addFirst(tileWest);
            }
            if (tileY > Scene_cameraYTile) {
                final Tile tileSouth = tiles[tileX][tileY - 1];
                if (tileSouth != null && tileSouth.drawSecondary)
                    Scene_tilesDeque.addFirst(tileSouth);
            }
        } while (true);
    }

    /**
     * Draws the argued {@link SceneTilePaint} in the raster.
     *
     * @param var1          the {@link SceneTilePaint} to draw.
     * @param plane         the plane to render the tile in (in range 0..4).
     * @param var7         the x coordinate of the tile.
     * @param var8         the y coordinate of the tile.
     * @param camPitchSine  the camera pitch sine value.
     * @param camPitchCos   the camera pitch cosine value.
     * @param camYawSine    the camera yaw sine value.
     * @param camYawCos     the camera yaw cosine value.
     */
    private void drawTileUnderlay(SceneTilePaint var1, int plane, int var7, int var8, int camPitchSine, int camPitchCos, int camYawSine, int camYawCos) {
        int t_x4;
        int t_x1 = t_x4 = (var7 << 7) - Scene_cameraX;
        int z2;
        int z1 = z2 = (var8 << 7) - Scene_cameraY;
        int t_x3;
        int t_x2 = t_x3 = t_x1 + 128;
        int z4;
        int z3 = z4 = z1 + 128;
        int heightCenter = tileHeights[plane][var7][var8] - Scene_cameraZ;
        int heightEast = tileHeights[plane][var7 + 1][var8] - Scene_cameraZ;
        int heightNorthEast = tileHeights[plane][var7 + 1][var8 + 1] - Scene_cameraZ;
        int heightNorth = tileHeights[plane][var7][var8 + 1] - Scene_cameraZ;
        int l4 = z1 * camYawSine + t_x1 * camYawCos >> 16;
        z1 = z1 * camYawCos - t_x1 * camYawSine >> 16;
        t_x1 = l4;
        l4 = heightCenter * camPitchCos - z1 * camPitchSine >> 16;
        z1 = heightCenter * camPitchSine + z1 * camPitchCos >> 16;
        heightCenter = l4;
        if (z1 < Rasterizer3D.MIN_DEPTH)
            return;
        l4 = z2 * camYawSine + t_x2 * camYawCos >> 16;
        z2 = z2 * camYawCos - t_x2 * camYawSine >> 16;
        t_x2 = l4;
        l4 = heightEast * camPitchCos - z2 * camPitchSine >> 16;
        z2 = heightEast * camPitchSine + z2 * camPitchCos >> 16;
        heightEast = l4;
        if (z2 < Rasterizer3D.MIN_DEPTH)
            return;
        l4 = z3 * camYawSine + t_x3 * camYawCos >> 16;
        z3 = z3 * camYawCos - t_x3 * camYawSine >> 16;
        t_x3 = l4;
        l4 = heightNorthEast * camPitchCos - z3 * camPitchSine >> 16;
        z3 = heightNorthEast * camPitchSine + z3 * camPitchCos >> 16;
        heightNorthEast = l4;
        if (z3 < Rasterizer3D.MIN_DEPTH)
            return;
        l4 = z4 * camYawSine + t_x4 * camYawCos >> 16;
        z4 = z4 * camYawCos - t_x4 * camYawSine >> 16;
        t_x4 = l4;
        l4 = heightNorth * camPitchCos - z4 * camPitchSine >> 16;
        z4 = heightNorth * camPitchSine + z4 * camPitchCos >> 16;
        heightNorth = l4;
        if (z4 < Rasterizer3D.MIN_DEPTH)
            return;
        int var22 = Rasterizer3D.originViewX + (t_x1 << viewDistance) / z1;
        int var23 = Rasterizer3D.originViewY + (heightCenter << viewDistance) / z1;
        int var24 = Rasterizer3D.originViewX + (t_x2 << viewDistance) / z2;
        int var25 = Rasterizer3D.originViewY + (heightEast << viewDistance) / z2;
        int var26 = Rasterizer3D.originViewX + (t_x3 << viewDistance) / z3;
        int var27 = Rasterizer3D.originViewY + (heightNorthEast << viewDistance) / z3;
        int var28 = Rasterizer3D.originViewX + (t_x4 << viewDistance) / z4;
        int var29 = Rasterizer3D.originViewY + (heightNorth << viewDistance) / z4;
        Rasterizer3D.alpha = 0;
        if ((var26 - var28) * (var25 - var29) - (var27 - var29) * (var24 - var28) > 0) {
            Rasterizer3D.triangleIsOutOfBounds = var26 < 0 || var28 < 0 || var24 < 0 || var26 > Rasterizer2D.lastX || var28 > Rasterizer2D.lastX || var24 > Rasterizer2D.lastX;
            if (checkClick && containsBounds(Scene_selectedScreenX, Scene_selectedScreenY, var26, var28, var24, var27, var29, var25)) {
                Scene_selectedX = var7;
                Scene_selectedY = var8;
            }
            if (var1.texture == -1) {
                if (var1.neColor != Rasterizer3D.MAX_HSL_COLOR_VALUE)
                    Rasterizer3D.drawGouraudTriangle(
                            var26, var28, var24,
                            var27, var29, var25,
                            z1, z2, z4,
                            var1.neColor,
                            var1.nwColor,
                            var1.seColor
                    );
            } else if (!lowMem) {
                if (var1.isFlat)
                    Rasterizer3D.drawTexturedTriangle(
                            var26, var28, var24,
                            var27, var29, var25,
                            z1, z2, z4,
                            var1.neColor,
                            var1.nwColor,
                            var1.seColor,
                            t_x1, t_x2, t_x4,
                            heightCenter, heightEast, heightNorth,
                            z1, z2, z4,
                            var1.texture
                    );
                else
                    Rasterizer3D.drawTexturedTriangle(
                            var26, var28, var24,
                            var27, var29, var25,
                            z1, z2, z4,
                            var1.neColor,
                            var1.nwColor,
                            var1.seColor,
                            t_x3, t_x4, t_x2,
                            heightNorthEast, heightNorth, heightEast,
                            z3, z4, z2,
                            var1.texture
                    );
            } else {
                int var30 = Rasterizer3D.Rasterizer3D_textureLoader.getAverageTextureRGB(var1.texture);
                /*Rasterizer3D.drawGouraudTriangle(
                        var26, var28, var24,
                        var27, var29, var25,
                        z1, z2, z4,
                        interpolateColours(var30, var1.getCenterColor()),
                        interpolateColours(var30, var1.getEastColor()),
                        interpolateColours(var30, var1.getNorthColor())
                );*/
                Rasterizer3D.method4270(z1, z2, z4, var23, var25, var29, var22, var24, var28, interpolateColours(var30, var1.neColor), interpolateColours(var30, var1.nwColor), interpolateColours(var30, var1.seColor));
                //Rasterizer3D.method4270(var23, var25, var29, var22, var24, var28, method4431(var30, var1.swColor), method4431(var30, var1.seColor), method4431(var30, var1.nwColor));
            }
        }
        if ((var22 - var24) * (var29 - var25) - (var23 - var25) * (var28 - var24) > 0) {
            Rasterizer3D.triangleIsOutOfBounds = var22 < 0 || var24 < 0 || var28 < 0 || var22 > Rasterizer2D.lastX || var24 > Rasterizer2D.lastX || var28 > Rasterizer2D.lastX;
            if (checkClick && containsBounds(Scene_selectedScreenX, Scene_selectedScreenY, var22, var24, var28, var23, var25, var29)) {
                Scene_selectedX = var7;
                Scene_selectedY = var8;
            }
            if (var1.texture == -1) {
                if (var1.swColor != Rasterizer3D.MAX_HSL_COLOR_VALUE) {
                    Rasterizer3D.drawGouraudTriangle(
                            var22, var24, var28,
                            var23, var25, var29,
                            z1, z2, z4,
                            var1.swColor,
                            var1.seColor,
                            var1.nwColor
                    );
                }
            } else {
                if (!lowMem) {
                    Rasterizer3D.drawTexturedTriangle(
                            var22, var24, var28,
                            var23, var25, var29,
                            z1, z2, z4,
                            var1.swColor,
                            var1.seColor,
                            var1.nwColor,
                            t_x1, t_x2, t_x4,
                            heightCenter, heightEast, heightNorth,
                            z1, z2, z4,
                            var1.texture
                    );
                    return;
                }
                int textureColor = TEXTURE_COLORS[var1.texture];
                Rasterizer3D.drawGouraudTriangle(
                        var22, var24, var28,
                        var23, var25, var29,
                        z1, z2, z4,
                        interpolateColours(textureColor, var1.swColor),
                        interpolateColours(textureColor, var1.seColor),
                        interpolateColours(textureColor, var1.nwColor));
            }
        }
    }

    /**
     * Draws the argued {@link SceneTileModel} in the raster.
     *
     * @param tile          the {@link SceneTileModel} to draw.
     * @param tileX         the x coordinate of the tile.
     * @param tileY         the y coordinate of the tile.
     * @param camPitchSine  the camera pitch sine value.
     * @param camPitchCos   the camera pitch cosine value.
     * @param camYawSine    the camera yaw sine value.
     * @param camYawCos     the camera yaw cosine value.
     */
    private void drawTileOverlay(SceneTileModel tile, int tileX, int tileY, int camPitchSine, int camPitchCos, int camYawSine, int camYawCos) {
        int numberOfTriangles = tile.vertexX.length;
        for (int t = 0; t < numberOfTriangles; t++) {
            int x = tile.vertexX[t] - Scene_cameraX;
            int y = tile.vertexY[t] - Scene_cameraZ;
            int z = tile.vertexZ[t] - Scene_cameraY;
            int k3 = z * camYawSine + x * camYawCos >> 16;
            z = z * camYawCos - x * camYawSine >> 16;
            x = k3;
            k3 = y * camPitchCos - z * camPitchSine >> 16;
            z = y * camPitchSine + z * camPitchCos >> 16;
            y = k3;
            if (z < Rasterizer3D.MIN_DEPTH)
                return;
            if (tile.triangleTextureId != null) {
                SceneTileModel.texture_vertices_x[t] = x;
                SceneTileModel.texture_vertices_y[t] = y;
                SceneTileModel.texture_vertices_z[t] = z;
            }
            SceneTileModel.vertices_x[t] = Rasterizer3D.originViewX + (x << viewDistance) / z;
            SceneTileModel.vertices_y[t] = Rasterizer3D.originViewY + (y << viewDistance) / z;
            SceneTileModel.vertices_z[t] = z;
        }

        Rasterizer3D.alpha = 0;
        numberOfTriangles = tile.faceX.length;
        for (int t = 0; t < numberOfTriangles; t++) {
            int v1 = tile.faceX[t];
            int v2 = tile.faceY[t];
            int v3 = tile.faceZ[t];
            int x1 = SceneTileModel.vertices_x[v1];
            int x2 = SceneTileModel.vertices_x[v2];
            int x3 = SceneTileModel.vertices_x[v3];
            int y1 = SceneTileModel.vertices_y[v1];
            int y2 = SceneTileModel.vertices_y[v2];
            int y3 = SceneTileModel.vertices_y[v3];
            if ((x1 - x2) * (y3 - y2) - (y1 - y2) * (x3 - x2) > 0) {
                int z1 = SceneTileModel.vertices_z[v1];
                int z2 = SceneTileModel.vertices_z[v2];
                int z3 = SceneTileModel.vertices_z[v3];

                Rasterizer3D.triangleIsOutOfBounds =
                        x1 < 0 || x2 < 0 || x3 < 0
                        || x1 > Rasterizer2D.lastX
                        || x2 > Rasterizer2D.lastX
                        || x3 > Rasterizer2D.lastX;

                if (checkClick && containsBounds(Scene_selectedScreenX, Scene_selectedScreenY, x1, x2, x3, y1, y2, y3)) {
                    Scene_selectedX = tileX;
                    Scene_selectedY = tileY;
                }
                //TODO: check this draw call out, drawDepthTriangle is being returned without rendering anything.
                Rasterizer3D.drawDepthTriangle(
                        x1, x2, x3,
                        y1, y2, y3,
                        z1, z2, z3
                );
                if (tile.triangleTextureId == null || tile.triangleTextureId[t] == -1) {
                    if (tile.triangleColorA[t] != Rasterizer3D.MAX_HSL_COLOR_VALUE)
                        Rasterizer3D.drawGouraudTriangle(
                                x1, x2, x3,
                                y1, y2, y3,
                                z1, z2, z3,
                                tile.triangleColorA[t],
                                tile.triangleColorB[t],
                                tile.triangleColorC[t]
                        );
                } else if (!lowMem) {
                    if (tile.isFlat)
                        Rasterizer3D.drawTexturedTriangle(
                                x1, x2, x3,
                                y1, y2, y3,
                                z1, z2, z3,
                                tile.triangleColorA[t],
                                tile.triangleColorB[t],
                                tile.triangleColorC[t],
                                SceneTileModel.texture_vertices_x[0],
                                SceneTileModel.texture_vertices_x[1],
                                SceneTileModel.texture_vertices_x[3],
                                SceneTileModel.texture_vertices_y[0],
                                SceneTileModel.texture_vertices_y[1],
                                SceneTileModel.texture_vertices_y[3],
                                SceneTileModel.texture_vertices_z[0],
                                SceneTileModel.texture_vertices_z[1],
                                SceneTileModel.texture_vertices_z[3],
                                tile.triangleTextureId[t]
                        );
                    else
                        Rasterizer3D.drawTexturedTriangle(
                                x1, x2, x3,
                                y1, y2, y3,
                                z1, z2, z3,
                                tile.triangleColorA[t],
                                tile.triangleColorB[t],
                                tile.triangleColorC[t],
                                SceneTileModel.texture_vertices_x[v1],
                                SceneTileModel.texture_vertices_x[v2],
                                SceneTileModel.texture_vertices_x[v3],
                                SceneTileModel.texture_vertices_y[v1],
                                SceneTileModel.texture_vertices_y[v2],
                                SceneTileModel.texture_vertices_y[v3],
                                SceneTileModel.texture_vertices_z[v1],
                                SceneTileModel.texture_vertices_z[v2],
                                SceneTileModel.texture_vertices_z[v3],
                                tile.triangleTextureId[t]
                        );
                } else {
                    int k5 = TEXTURE_COLORS[tile.triangleTextureId[t]];
                    Rasterizer3D.drawGouraudTriangle(
                            x1, x2, x3,
                            y1, y2, y3,
                            z1, z2, z3,
                            interpolateColours(k5, tile.triangleColorA[t]),
                            interpolateColours(k5, tile.triangleColorB[t]),
                            interpolateColours(k5, tile.triangleColorC[t])
                    );
                }
            }
        }
    }

    private int interpolateColours(int colour1, int colour2) {
        colour2 = 127 - colour2;
        colour2 = (colour2 * (colour1 & 0x7f)) / 160;
        if (colour2 < 2)
            colour2 = 2;
        else if (colour2 > 126)
            colour2 = 126;
        return (colour1 & 0xff80) + colour2;
    }

    /**
     * Checks whether the argued screen coordinates are within the bounds
     * of the argued triangle coordinates.
     *
     * @param screenX the screen x-coordinate.
     * @param screenY the screen y-coordinate.
     * @param x1      the x coordinate of the first vertex
     * @param x2      the x coordinate of the second vertex
     * @param x3      the x coordinate of the third vertex
     * @param y1      the y coordinate of the first vertex
     * @param y2      the y coordinate of the second vertex
     * @param y3      the y coordinate of the third vertex
     *
     * @return {@code true} if the screen coordinates are within the triangle's bounds,
     *          {@code false} if not.
     */
    private boolean containsBounds(int screenX, int screenY, int x1, int x2, int x3, int y1, int y2, int y3) {
        if (screenY < y1 && screenY < y2 && screenY < y3)
            return false;
        if (screenY > y1 && screenY > y2 && screenY > y3)
            return false;
        if (screenX < x1 && screenX < x2 && screenX < x3)
            return false;
        if (screenX > x1 && screenX > x2 && screenX > x3)
            return false;
        int distance1 = (screenY - y1) * (x2 - x1) - (screenX - x1) * (y2 - y1);
        int distance2 = (screenY - y3) * (x1 - x3) - (screenX - x3) * (y1 - y3);
        int distance3 = (screenY - y2) * (x3 - x2) - (screenX - x2) * (y3 - y2);
        return distance1 * distance3 > 0 && distance3 * distance2 > 0;
    }

    private void occlude() {
        int sceneClusterCount = Scene_planeOccluderCounts[Scene_plane];
        Occluder[] occluders = SceneGraph.Scene_planeOccluders[Scene_plane];
        SceneGraph.Scene_currentOccludersCount = 0;
        for (int sceneIndex = 0; sceneIndex < sceneClusterCount; sceneIndex++) {
            Occluder occluder = occluders[sceneIndex];
            if (occluder.type == 1) { //YZ-plane
                int relativeX = (occluder.minTileX - Scene_cameraXTile) + TILE_DRAW_DISTANCE;
                if (relativeX < 0 || relativeX > 50)
                    continue;
                int minRelativeY = (occluder.minTileY - Scene_cameraYTile) + TILE_DRAW_DISTANCE;
                if (minRelativeY < 0)
                    minRelativeY = 0;
                int maxRelativeY = (occluder.maxTileY - Scene_cameraYTile) + TILE_DRAW_DISTANCE;
                if (maxRelativeY > 50)
                    maxRelativeY = 50;
                boolean flag = false;
                while (minRelativeY <= maxRelativeY)
                    if (visibleTiles[relativeX][minRelativeY++]) {
                        flag = true;
                        break;
                    }
                if (!flag)
                    continue;
                int dXPos = Scene_cameraX - occluder.minX;
                if (dXPos > 32) {
                    occluder.cullDirection = 1;
                } else {
                    if (dXPos >= -32)
                        continue;
                    occluder.cullDirection = 2;
                    dXPos = -dXPos;
                }
                occluder.yStartDistanceToCamera = (occluder.minY - Scene_cameraY << 8) / dXPos;
                occluder.yEndDistanceToCamera = (occluder.maxY - Scene_cameraY << 8) / dXPos;
                occluder.zStartDistanceToCamera = (occluder.maxZ - Scene_cameraZ << 8) / dXPos;
                occluder.zEndDistanceToCamera = (occluder.minZ - Scene_cameraZ << 8) / dXPos;
                Scene_currentOccluders[SceneGraph.Scene_currentOccludersCount++] = occluder;
                continue;
            }
            if (occluder.type == 2) { //XZ-plane
                int relativeY = (occluder.minTileY - Scene_cameraYTile) + TILE_DRAW_DISTANCE;
                if (relativeY < 0 || relativeY > 50)
                    continue;
                int minRelativeX = (occluder.minTileX - Scene_cameraXTile) + TILE_DRAW_DISTANCE;
                if (minRelativeX < 0)
                    minRelativeX = 0;
                int maxRelativeX = (occluder.maxTileX - Scene_cameraXTile) + TILE_DRAW_DISTANCE;
                if (maxRelativeX > 50)
                    maxRelativeX = 50;
                boolean flag1 = false;
                while (minRelativeX <= maxRelativeX)
                    if (visibleTiles[minRelativeX++][relativeY]) {
                        flag1 = true;
                        break;
                    }
                if (!flag1)
                    continue;
                int dYPos = Scene_cameraY - occluder.minY;
                if (dYPos > 32) {
                    occluder.cullDirection = 3;
                } else if (dYPos < -32) {
                    occluder.cullDirection = 4;
                    dYPos = -dYPos;
                } else {
                    continue;
                }
                occluder.xStartDistanceToCamera = (occluder.minX - Scene_cameraX << 8) / dYPos;
                occluder.xEndDistanceToCamera = (occluder.maxX - Scene_cameraX << 8) / dYPos;
                occluder.zStartDistanceToCamera = (occluder.maxZ - Scene_cameraZ << 8) / dYPos;
                occluder.zEndDistanceToCamera = (occluder.minZ - Scene_cameraZ << 8) / dYPos;
                Scene_currentOccluders[SceneGraph.Scene_currentOccludersCount++] = occluder;
            } else if (occluder.type == 4) { //XY-plane
                int relativeZ = occluder.maxZ - Scene_cameraZ;
                if (relativeZ > 128) {
                    int minRelativeY = (occluder.minTileY - Scene_cameraYTile) + TILE_DRAW_DISTANCE;
                    if (minRelativeY < 0)
                        minRelativeY = 0;
                    int maxRelativeY = (occluder.maxTileY - Scene_cameraYTile) + TILE_DRAW_DISTANCE;
                    if (maxRelativeY > 50)
                        maxRelativeY = 50;
                    if (minRelativeY <= maxRelativeY) {
                        int minRelativeX = (occluder.minTileX - Scene_cameraXTile) + TILE_DRAW_DISTANCE;
                        if (minRelativeX < 0)
                            minRelativeX = 0;
                        int maxRelativeX = (occluder.maxTileX - Scene_cameraXTile) + TILE_DRAW_DISTANCE;
                        if (maxRelativeX > 50)
                            maxRelativeX = 50;
                        boolean flag2 = false;
                        label0:
                        for (int i4 = minRelativeX; i4 <= maxRelativeX; i4++) {
                            for (int j4 = minRelativeY; j4 <= maxRelativeY; j4++) {
                                if (!visibleTiles[i4][j4])
                                    continue;
                                flag2 = true;
                                break label0;
                            }

                        }

                        if (flag2) {
                            occluder.cullDirection = 5;
                            occluder.xStartDistanceToCamera = (occluder.minX - Scene_cameraX << 8) / relativeZ;
                            occluder.xEndDistanceToCamera = (occluder.maxX - Scene_cameraX << 8) / relativeZ;
                            occluder.yStartDistanceToCamera = (occluder.minY - Scene_cameraY << 8) / relativeZ;
                            occluder.yEndDistanceToCamera = (occluder.maxY - Scene_cameraY << 8) / relativeZ;
                            Scene_currentOccluders[SceneGraph.Scene_currentOccludersCount++] = occluder;
                        }
                    }
                }
            }
        }

    }

    private boolean setAndGetRenderTileFlag(int zLoc, int xLoc, int yLoc) {
        int flag = renderTileFlags[zLoc][xLoc][yLoc];
        if (flag == -Scene_drawnCount)
            return false;
        if (flag == Scene_drawnCount)
            return true;
        int xPos = xLoc << 7;
        int yPos = yLoc << 7;
        if (hasVisibleClusterAt(xPos + 1, tileHeights[zLoc][xLoc][yLoc], yPos + 1)
                && hasVisibleClusterAt((xPos + 128) - 1, tileHeights[zLoc][xLoc + 1][yLoc], yPos + 1)
                && hasVisibleClusterAt((xPos + 128) - 1, tileHeights[zLoc][xLoc + 1][yLoc + 1], (yPos + 128) - 1)
                && hasVisibleClusterAt(xPos + 1, tileHeights[zLoc][xLoc][yLoc + 1], (yPos + 128) - 1)) {
            renderTileFlags[zLoc][xLoc][yLoc] = Scene_drawnCount;
            return true;
        } else {
            renderTileFlags[zLoc][xLoc][yLoc] = -Scene_drawnCount;
            return false;
        }
    }

    private boolean method321(int zLoc, int xLoc, int yLoc, int orientation) {
        if (!setAndGetRenderTileFlag(zLoc, xLoc, yLoc))
            return false;
        int xPos = xLoc << 7;
        int yPos = yLoc << 7;
        int height = tileHeights[zLoc][xLoc][yLoc] - 1;
        int l1 = height - 120;
        int i2 = height - 230;
        int j2 = height - 238;
        if (orientation < 16) {
            if (orientation == 1) {
                if (xPos > Scene_cameraX) {
                    if (!hasVisibleClusterAt(xPos, height, yPos))
                        return false;
                    if (!hasVisibleClusterAt(xPos, height, yPos + 128))
                        return false;
                }
                if (zLoc > 0) {
                    if (!hasVisibleClusterAt(xPos, l1, yPos))
                        return false;
                    if (!hasVisibleClusterAt(xPos, l1, yPos + 128))
                        return false;
                }
                return hasVisibleClusterAt(xPos, i2, yPos) && hasVisibleClusterAt(xPos, i2, yPos + 128);
            }
            if (orientation == 2) {
                if (yPos < Scene_cameraY) {
                    if (!hasVisibleClusterAt(xPos, height, yPos + 128))
                        return false;
                    if (!hasVisibleClusterAt(xPos + 128, height, yPos + 128))
                        return false;
                }
                if (zLoc > 0) {
                    if (!hasVisibleClusterAt(xPos, l1, yPos + 128))
                        return false;
                    if (!hasVisibleClusterAt(xPos + 128, l1, yPos + 128))
                        return false;
                }
                return hasVisibleClusterAt(xPos, i2, yPos + 128) && hasVisibleClusterAt(xPos + 128, i2, yPos + 128);
            }
            if (orientation == 4) {
                if (xPos < Scene_cameraX) {
                    if (!hasVisibleClusterAt(xPos + 128, height, yPos))
                        return false;
                    if (!hasVisibleClusterAt(xPos + 128, height, yPos + 128))
                        return false;
                }
                if (zLoc > 0) {
                    if (!hasVisibleClusterAt(xPos + 128, l1, yPos))
                        return false;
                    if (!hasVisibleClusterAt(xPos + 128, l1, yPos + 128))
                        return false;
                }
                return hasVisibleClusterAt(xPos + 128, i2, yPos) && hasVisibleClusterAt(xPos + 128, i2, yPos + 128);
            }
            if (orientation == 8) {
                if (yPos > Scene_cameraY) {
                    if (!hasVisibleClusterAt(xPos, height, yPos))
                        return false;
                    if (!hasVisibleClusterAt(xPos + 128, height, yPos))
                        return false;
                }
                if (zLoc > 0) {
                    if (!hasVisibleClusterAt(xPos, l1, yPos))
                        return false;
                    if (!hasVisibleClusterAt(xPos + 128, l1, yPos))
                        return false;
                }
                return hasVisibleClusterAt(xPos, i2, yPos) && hasVisibleClusterAt(xPos + 128, i2, yPos);
            }
        }
        if (!hasVisibleClusterAt(xPos + 64, j2, yPos + 64))
            return false;
        if (orientation == 16)
            return hasVisibleClusterAt(xPos, i2, yPos + 128);
        if (orientation == 32)
            return hasVisibleClusterAt(xPos + 128, i2, yPos + 128);
        if (orientation == 64)
            return hasVisibleClusterAt(xPos + 128, i2, yPos);
        if (orientation == 128) {
            return hasVisibleClusterAt(xPos, i2, yPos);
        } else {
            System.out.println("Warning unsupported wall type"); //TODO
            return true;
        }
    }

    private boolean hasVisibleClusterSurrounding(int plane, int xLoc, int yLoc, int heightOffset) {
        if (!setAndGetRenderTileFlag(plane, xLoc, yLoc))
            return false;
        int xPos = xLoc << 7;
        int yPos = yLoc << 7;
        return hasVisibleClusterAt(xPos + 1, tileHeights[plane][xLoc][yLoc] - heightOffset, yPos + 1)
                && hasVisibleClusterAt((xPos + 128) - 1, tileHeights[plane][xLoc + 1][yLoc] - heightOffset, yPos + 1)
                && hasVisibleClusterAt((xPos + 128) - 1, tileHeights[plane][xLoc + 1][yLoc + 1] - heightOffset, (yPos + 128) - 1)
                && hasVisibleClusterAt(xPos + 1, tileHeights[plane][xLoc][yLoc + 1] - heightOffset, (yPos + 128) - 1);
    }

    private boolean hasVisibleClusterWithin(int plane, int startXLoc, int endXLoc, int startYLoc, int endYLoc, int heightOffset) {
        if (startXLoc == endXLoc && startYLoc == endYLoc) {
            if (!setAndGetRenderTileFlag(plane, startXLoc, startYLoc))
                return false;
            int startXPos = startXLoc << 7;
            int startYPos = startYLoc << 7;
            return hasVisibleClusterAt(startXPos + 1, tileHeights[plane][startXLoc][startYLoc] - heightOffset, startYPos + 1)
                    && hasVisibleClusterAt((startXPos + 128) - 1, tileHeights[plane][startXLoc + 1][startYLoc] - heightOffset, startYPos + 1)
                    && hasVisibleClusterAt((startXPos + 128) - 1, tileHeights[plane][startXLoc + 1][startYLoc + 1] - heightOffset, (startYPos + 128) - 1)
                    && hasVisibleClusterAt(startXPos + 1, tileHeights[plane][startXLoc][startYLoc + 1] - heightOffset, (startYPos + 128) - 1);
        }
        for (int xLoc = startXLoc; xLoc <= endXLoc; xLoc++) {
            for (int yLoc = startYLoc; yLoc <= endYLoc; yLoc++)
                if (renderTileFlags[plane][xLoc][yLoc] == -Scene_drawnCount)
                    return false;

        }

        int nee_xPos = (startXLoc << 7) + 1;
        int nee_yPos = (startYLoc << 7) + 2;
        int height = tileHeights[plane][startXLoc][startYLoc] - heightOffset;
        if (!hasVisibleClusterAt(nee_xPos, height, nee_yPos))
            return false;
        int w_xPos = (endXLoc << 7) - 1;
        if (!hasVisibleClusterAt(w_xPos, height, nee_yPos))
            return false;
        int s_yPos = (endYLoc << 7) - 1;
        return hasVisibleClusterAt(nee_xPos, height, s_yPos) && hasVisibleClusterAt(w_xPos, height, s_yPos);
    }

    private boolean hasVisibleClusterAt(int xPos, int z, int yPos) {

        for (int i = 0; i < Scene_currentOccludersCount; i++) {

            final Occluder cluster = Scene_currentOccluders[i];

            if (cluster.cullDirection == 1) {
                int deltaX = cluster.minX - xPos;
                if (deltaX > 0) {
                    int startYVisible = cluster.minY + (cluster.yStartDistanceToCamera * deltaX >> 8);
                    int endYVisible = cluster.maxY + (cluster.yEndDistanceToCamera * deltaX >> 8);
                    int startZVisible = cluster.maxZ + (cluster.zStartDistanceToCamera * deltaX >> 8);
                    int endZVisible = cluster.minZ + (cluster.zEndDistanceToCamera * deltaX >> 8);
                    if (yPos >= startYVisible && yPos <= endYVisible && z >= startZVisible && z <= endZVisible)
                        return true;
                }
            } else if (cluster.cullDirection == 2) {
                int deltaX = xPos - cluster.minX;
                if (deltaX > 0) {
                    int startYVisible = cluster.minY + (cluster.yStartDistanceToCamera * deltaX >> 8);
                    int endYVisible = cluster.maxY + (cluster.yEndDistanceToCamera * deltaX >> 8);
                    int startZVisible = cluster.maxZ + (cluster.zStartDistanceToCamera * deltaX >> 8);
                    int endZVisible = cluster.minZ + (cluster.zEndDistanceToCamera * deltaX >> 8);
                    if (yPos >= startYVisible && yPos <= endYVisible && z >= startZVisible && z <= endZVisible)
                        return true;
                }
            } else if (cluster.cullDirection == 3) {
                int deltaY = cluster.minY - yPos;
                if (deltaY > 0) {
                    int startXVisible = cluster.minX + (cluster.xStartDistanceToCamera * deltaY >> 8);
                    int endXVisible = cluster.maxX + (cluster.xEndDistanceToCamera * deltaY >> 8);
                    int startZVisible = cluster.maxZ + (cluster.zStartDistanceToCamera * deltaY >> 8);
                    int endZVisible = cluster.minZ + (cluster.zEndDistanceToCamera * deltaY >> 8);
                    if (xPos >= startXVisible && xPos <= endXVisible && z >= startZVisible && z <= endZVisible)
                        return true;
                }
            } else if (cluster.cullDirection == 4) {
                int deltaY = yPos - cluster.minY;
                if (deltaY > 0) {
                    int startXVisible = cluster.minX + (cluster.xStartDistanceToCamera * deltaY >> 8);
                    int endXVisible = cluster.maxX + (cluster.xEndDistanceToCamera * deltaY >> 8);
                    int startZVisible = cluster.maxZ + (cluster.zStartDistanceToCamera * deltaY >> 8);
                    int endZVisible = cluster.minZ + (cluster.zEndDistanceToCamera * deltaY >> 8);
                    if (xPos >= startXVisible && xPos <= endXVisible && z >= startZVisible && z <= endZVisible)
                        return true;
                }
            } else if (cluster.cullDirection == 5) {
                int deltaZ = z - cluster.maxZ;
                if (deltaZ > 0) {
                    int startXVisible = cluster.minX + (cluster.xStartDistanceToCamera * deltaZ >> 8);
                    int endXVisible = cluster.maxX + (cluster.xEndDistanceToCamera * deltaZ >> 8);
                    int startYVisible = cluster.minY + (cluster.yStartDistanceToCamera * deltaZ >> 8);
                    int endYVisible = cluster.maxY + (cluster.yEndDistanceToCamera * deltaZ >> 8);
                    if (xPos >= startXVisible && xPos <= endXVisible && yPos >= startYVisible && yPos <= endYVisible)
                        return true;
                }
            }
        }

        return false;
    }

    public Tile getTile(int plane, int localX, int localY) {
        return tiles[plane][localX][localY];
    }

    @Override
    public Tile[][][] getTiles() {
        return tiles;
    }

    @Override
    public int getDrawDistance() {
        return TILE_DRAW_DISTANCE;
    }

    @Override
    public void setDrawDistance(int drawDistance) {
        System.err.println("Unimplemented method setDrawDistance");
    }
}
