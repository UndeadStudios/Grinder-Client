package com.grinder.model;

import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.cache.graphics.RSFont;
import com.runescape.entity.GameObject;
import com.runescape.scene.object.tile.Tile;

import java.util.Arrays;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 20/12/2019
 */
public class DebugTile {

    public static void debugTile(Client client){
        final int plane = client.plane;
        final int localX = Client.localPlayer.getLocalX();
        final int localY = Client.localPlayer.getLocalY();
        final Tile tile = client.scene.getTile(plane, localX, localY);

        int drawX = (ClientUI.frameMode == Client.ScreenMode.FIXED
                ? 428
                : ClientUI.frameWidth - 265) - 100;
        int drawY = 10;

        if(tile == null){
            draw(client.newRegularFont,"Tile is null", drawX, drawY);
            return;
        }

        final int height = client.scene.tileHeights[plane][localX][localY];
        final int unknown = client.scene.renderTileFlags[plane][localX][localY];
        final int flag = client.tileFlags[plane][localX][localY] & 4;
        final int underlay = Client.underlays[plane][localX][localY];
        final int yOffset = 15;

        draw(client.newRegularFont,"["+localX+"]["+localY+"]:", drawX, drawY);
        drawY += yOffset;
        draw(client.newRegularFont, "underlay: "+underlay, drawX, drawY);
        drawY += yOffset;
        draw(client.newRegularFont,"flag: "+flag, drawX, drawY);
        drawY += yOffset;
        draw(client.newRegularFont,"height: "+height, drawX, drawY);
        drawY += yOffset;
        draw(client.newRegularFont,"unknown: "+unknown, drawX, drawY);
        drawY += yOffset;
        draw(client.newRegularFont,"mySimpleTile: "+tile.paint, drawX, drawY);
        drawY += yOffset;
        draw(client.newRegularFont,"myShapedTile: "+tile.model, drawX, drawY);
        drawY += yOffset;
        draw(client.newRegularFont,"wallObject: "+tile.boundaryObject, drawX, drawY);
        drawY += yOffset;
        draw(client.newRegularFont,"wallDecoration: "+tile.wallDecoration, drawX, drawY);
        drawY += yOffset;
        draw(client.newRegularFont,"groundDecoration: "+tile.floorDecoration, drawX, drawY);
        drawY += yOffset;
        draw(client.newRegularFont,"groundItemTile: "+tile.itemLayer, drawX, drawY);
        drawY += yOffset;
        draw(client.newRegularFont,"tiledObjectMasks: "+ Arrays.toString(tile.gameObjectEdgeMasks), drawX, drawY);
        drawY += yOffset;
        draw(client.newRegularFont,"gameObjectIndex: "+tile.gameObjectsCount, drawX, drawY);
        drawY += yOffset;
        if(tile.gameObjects != null) {
            for (GameObject gameObject : tile.gameObjects) {
                if(gameObject == null)
                    continue;
                draw(client.newRegularFont, "     ID = "+gameObject.getKey(), drawX, drawY);
                drawY += yOffset;
            }
        }

        draw(client.newRegularFont,"totalTiledObjectMask: "+tile.gameObjectsEdgeMask, drawX, drawY);
        drawY += yOffset;
        draw(client.newRegularFont,"logicHeight: "+tile.minPlane, drawX, drawY);
        drawY += yOffset;
        draw(client.newRegularFont,"displayTop: "+tile.drawPrimary, drawX, drawY);
        drawY += yOffset;
        draw(client.newRegularFont,"displayTop2: "+tile.drawSecondary, drawX, drawY);
        drawY += yOffset;
        draw(client.newRegularFont,"aBoolean1324: "+tile.drawGameObjects, drawX, drawY);
        drawY += yOffset;
        draw(client.newRegularFont,"someTileMask: "+tile.drawGameObjectEdges, drawX, drawY);
        drawY += yOffset;
        draw(client.newRegularFont,"anInt1326: "+tile.anInt1326, drawX, drawY);
        drawY += yOffset;
        draw(client.newRegularFont,"anInt1327: "+tile.anInt1327, drawX, drawY);
        drawY += yOffset;
        draw(client.newRegularFont,"orientationRelativeToCamera: "+tile.orientationRelativeToCamera, drawX, drawY);
    }

    private static void draw(RSFont font, String text, int x, int y){
        font.drawBasicString(text, x, y, 0xFF8C00);
    }
}
