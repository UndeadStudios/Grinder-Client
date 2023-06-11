package com.runescape.scene;

import com.runescape.input.KeyHandler;
import com.grinder.client.util.Log;
import com.runescape.Client;
import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.PacketBuilder;
import com.runescape.io.packets.outgoing.impl.UpdatePlane;

public class PathFinder {

    private final static byte MAP_LENGTH_X = 104;
    private final static byte MAP_LENGTH_Y = 104;

    private final static int DEFAULT_PATH_ORIENTATION = 0;
    private final static int DEFAULT_PATH_COST = 99_999_999;
    private final static int START_PATH_COST = 99;
    public static final int WEST = 2;
    public static final int EAST = 8;
    public static final int SOUTH = 1;
    public static final int NORTH = 4;
    public static final int SOUTH_WEST = 3;
    public static final int SOUTH_EAST = 9;
    public static final int NORTH_WEST = 6;
    public static final int NORTH_EAST = 12;

    private final int[][] pathCost = new int[MAP_LENGTH_X][MAP_LENGTH_Y];
    private final int[][] pathOrientation = new int[MAP_LENGTH_X][MAP_LENGTH_Y];
    private final int[] bigX = new int[4000];
    private final int[] bigY = new int[4000];

    public static int SCREEN_WALK = 0;
    public static int MINIMAP_WALK = 1;
    public static int OBJECT_WALK = 2;

    public boolean doWalkTo(Client client,
                            int type,
                            int endX,
                            int endY,
                            boolean isMapWalk)
    {

        final int plane = client.getPlane();


        client.destinationX = endX;
        client.destinationY = endY;

        client.sendPacket(new UpdatePlane(plane).create());

        if (isMapWalk) {
            final int keyArr = KeyHandler.pressedKeys[82] ? 1 : 0;
            //System.out.println("pressed shift ? " + KeyHandler.pressedKeys[81]);
            OutgoingPacket movementPacket = () -> {
                int opcode = 164;
                if (type == 1) {
                    opcode = 248;
                } else if (type == 2) {
                    opcode = 98;
                }
                PacketBuilder buf = new PacketBuilder(opcode);
                // buf.putByte(k5 + k5 + 3);
                buf.putByte(KeyHandler.pressedKeys[81] ? 1 : 0);
                buf.putSignedWordBigEndian(endX + client.getBaseX());
                buf.putUnsignedWordBigEndian(endY + client.getBaseY());
                buf.putByteC(keyArr);
                return buf;
            };

            client.sendPacket(movementPacket.create());
        }
        return true;
    }

}
