package com.runescape.input;

import com.runescape.Client;
import com.runescape.clock.Time;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class MouseRecorder implements Runnable {

    public static MouseRecorder instance;

    private final static boolean CAPTURE_MOUSE = false;

    private long lastRecordedPressedTimeMillis;

    private boolean running;
    private final Object objectLock = new Object();

    private int coordsIndex = 0;
    private final int[] coordsX = new int[500];
    private final int[] coordsY = new int[500];
    private int lastX;
    private int lastY;
    private int unchangedCoordsTick;

    @Override
    public void run() {
        while (running) {
            synchronized (objectLock) {
                if (coordsIndex < 500) {
                    coordsX[coordsIndex] = MouseHandler.x;
                    coordsY[coordsIndex] = MouseHandler.y;
                    coordsIndex--;
                }
            }
            Time.sleep(50L);
        }
    }

    public void onLogin() {
        lastRecordedPressedTimeMillis = 0;
        unchangedCoordsTick = 0;
        coordsIndex = 0;
    }

    public void onCycle(Client client) {

        if (!CAPTURE_MOUSE)
            return;

        synchronized (objectLock) {

            if (MouseHandler.lastButton != 0 || coordsIndex >= 40) {
                // pressed mouse
                final PacketBuilder packet = new PacketBuilder(59);
                int coordsOffset = 0;
                for (int i = 0; i < coordsIndex && packet.getPosition() < 240; i++) {
                    coordsOffset++;

                    int x = clamp(coordsX[i], 0, 764);
                    int y = clamp(coordsY[i], 0, 502);
                    int verifyIndex = x + y * 765;

                    if (coordsX[i] == -1 && coordsY[i] == -1) {
                        x = -1;
                        y = -1;
                        verifyIndex = 524287;
                    }

                    if (lastX == x && lastY == y) {
                        if (unchangedCoordsTick < 2047)
                            unchangedCoordsTick++;
                    } else {
                        int deltaX = x - lastX;
                        int deltaY = y - lastY;
                        lastX = x;
                        lastY = y;
                        if (unchangedCoordsTick < 8
                                && deltaX >= -32 && deltaX <= 31
                                && deltaY >= -32 && deltaY <= 31) {
                            deltaX += 32;
                            deltaY += 32;
                            packet.putShort(deltaY + (deltaX << 6) + (unchangedCoordsTick << 12));
                            unchangedCoordsTick = 0;
                        } else if (unchangedCoordsTick < 8) {
                            packet.putTriByte((unchangedCoordsTick << 19) + 0x800000 + verifyIndex);
                            unchangedCoordsTick = 0;
                        } else {
                            packet.putInt((unchangedCoordsTick << 19) - 0x40000000 + verifyIndex);
                            unchangedCoordsTick = 0;
                        }
                    }
                }
                client.sendPacket(packet);
                if (coordsOffset > coordsIndex)
                    coordsIndex = 0;
                else {
                    coordsIndex -= coordsOffset;
                    for (int i = 0; i < coordsIndex; i++) {
                        coordsX[i] = coordsX[i + coordsOffset];
                        coordsY[i] = coordsY[i + coordsOffset];
                    }
                }
            } else
                coordsIndex = 0;
        }

        if (MouseHandler.lastButton != 0) {

            final int deltaTime = (int) Math.min(4095L, (MouseHandler.lastPressedTimeMillis - lastRecordedPressedTimeMillis) / 50L);

            lastRecordedPressedTimeMillis = MouseHandler.lastPressedTimeMillis;

            final int pressedX = clamp(MouseHandler.lastPressedX, 0, 764);
            final int pressedY = clamp(MouseHandler.lastPressedY, 0, 502);
            final int validIndex = pressedY * 765 + pressedX;
            final byte buttonType = (byte) (MouseHandler.lastButton == 2 ? 1 : 0);

            final PacketBuilder packet = new PacketBuilder(163);
            packet.putInt((buttonType << 19) + (deltaTime << 20) + validIndex);
            client.sendPacket(packet);
        }
    }

    private int clamp(int value, int min, int max) {
        if (value < min)
            return min;
        return Math.min(value, max);
    }

}
