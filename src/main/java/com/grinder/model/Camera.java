package com.grinder.model;

import com.runescape.Client;
import com.runescape.entity.model.Model;
import com.runescape.scene.SceneGraph;

public class Camera {

    public static final int[] quakeAmplitudes = new int[5];
    public static final int[] quakeMagnitudes = new int[5];
    public static final boolean[] quakeDirectionActive = new boolean[5];
    public static int cinematicCamXViewpointLoc;
    public static int cinematicCamYViewpointLoc;
    public static int cinematicCamZViewpointLoc;
    public static int constCinematicCamRotationSpeed;
    public static int varCinematicCamRotationSpeedPromille;
    public static int oculusOrbFocalPointX;
    public static int oculusOrbFocalPointY;

    public static void handleAutomaticCameraMovement(Client client, boolean fixedMode) {
        int pitch = client.cameraPitchTarget;
        if (client.anInt984 / 256 > pitch)
            pitch = client.anInt984 / 256;
        if (quakeDirectionActive[4] && quakeAmplitudes[4] + 128 > pitch)
            pitch = quakeAmplitudes[4] + 128;
        int k = client.minimapOrientation + client.cameraRotation & 0x7ff;
        boolean modifiedViewDistance = SceneGraph.viewDistance != SceneGraph.DEFAULT_VIEW_DISTANCE;
        setCameraPos(
                client, Client.cameraZoom + pitch * (!modifiedViewDistance && !fixedMode ? 2 : SceneGraph.viewDistance == 10 ? 5 : 3),
                pitch,
                oculusOrbFocalPointX,
                client.getTileHeight(Client.plane, Client.localPlayer.y, Client.localPlayer.x) - 50,
                k,
                oculusOrbFocalPointY);
    }

    public static void handleCameraShaking(Client client) {
        for (int i2 = 0; i2 < 5; i2++) {
            if (quakeDirectionActive[i2]) {
                int j2 = (int) ((Math.random() * (double) (quakeMagnitudes[i2] * 2 + 1) - (double) quakeMagnitudes[i2])
                        + Math.sin((double) client.quakeTimes[i2] * ((double) client.quake4PiOverPeriods[i2] / 100D))
                        * (double) quakeAmplitudes[i2]);
                if (i2 == 0)
                    client.cameraX += j2;
                if (i2 == 1)
                    client.cameraY += j2;
                if (i2 == 2)
                    client.cameraZ += j2;
                if (i2 == 3)
                    client.cameraYaw = client.cameraYaw + j2 & 0x7ff;
                if (i2 == 4) {
                    client.cameraPitch += j2;
                    if (client.cameraPitch < 128)
                        client.cameraPitch = 128;
                    if (client.cameraPitch > 383)
                        client.cameraPitch = 383;
                }
            }
        }
    }

    public static void setCameraPos(Client client, int j, int yCurve, int x, int z, int xCurve, int y) {
        //x += getCameraOffsetX();
        int l1 = 2048 - yCurve & 0x7ff;
        int i2 = 2048 - xCurve & 0x7ff;

        int j2 = 0;
        int k2 = 0;
        int l2 = j;
        if (l1 != 0) {
            int i3 = Model.SINE[l1];
            int k3 = Model.COSINE[l1];
            int i4 = k2 * k3 - l2 * i3 >> 16;
            l2 = k2 * i3 + l2 * k3 >> 16;
            k2 = i4;
        }
        if (i2 != 0) {
            int j3 = Model.SINE[i2];
            int l3 = Model.COSINE[i2];
            int j4 = l2 * j3 + j2 * l3 >> 16;
            l2 = l2 * l3 - j2 * j3 >> 16;
            j2 = j4;
        }
        client.cameraX = x - j2;
        client.cameraY = z - k2;
        client.cameraZ = y - l2;
        client.cameraPitch = yCurve;
        client.cameraYaw = xCurve;
    }
}
