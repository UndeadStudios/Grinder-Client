package com.runescape.scene.object.tile;

import com.runescape.collection.Node;
import com.runescape.entity.GameObject;
import com.runescape.entity.ItemLayer;
import com.runescape.scene.object.FloorDecoration;
import com.runescape.scene.object.WallDecoration;
import com.runescape.scene.object.BoundaryObject;

public final class Tile extends Node {
    public final int x;
    public final int y;
    public final int originalPlane;
    public final GameObject[] gameObjects;
    public final int[] gameObjectEdgeMasks;
    public int plane;
    public SceneTilePaint paint;
    public SceneTileModel model;
    public BoundaryObject boundaryObject;
    public WallDecoration wallDecoration;
    public FloorDecoration floorDecoration;
    public ItemLayer itemLayer;
    public int gameObjectsCount;
    public int gameObjectsEdgeMask;
    public int minPlane;
    public boolean drawPrimary;
    public boolean drawSecondary;
    public boolean drawGameObjects;
    public int drawGameObjectEdges;
    public int anInt1326;
    public int anInt1327;
    public int orientationRelativeToCamera;
    public Tile linkedBelowTile;

    public Tile(int i, int j, int k) {
        gameObjects = new GameObject[5];
        gameObjectEdgeMasks = new int[5];
        originalPlane = plane = i;
        x = j;
        y = k;
    }

}
