package com.grinder.model;

import com.grinder.ui.ClientUI;
import com.runescape.Client;
import com.runescape.cache.graphics.widget.Widget;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 20/12/2019
 */
public class Snow {

    public static int interfaceWidth = 512;
    public static int interfaceHeight = 334;
    public static int childWith = 32;
    public static int childHeight = 32;
    public static int modelRotation1 = 0;
    public static int modelRotation2 = 0;
    public static int modelZoom = 200;
    public static boolean underRoof = false;
    public static boolean disabledSnow = false;
    private static final int[] snowyUnderlays = new int[] {42, 62, 48, 49, 96, 63, 64, 91, 93, 50, 99};

    public static void processSnowInterface(Client client) {

        final int localX = Client.localPlayer.getLocalX();
        final int localY = Client.localPlayer.getLocalY();

        //if(client.tileFlags[client.plane][localX][localY] == 4) // Stan tile system
        //   return;

        int absX = localX + client.baseX;
        int absY = localY + client.baseY;
        int regionX = client.baseX >> 6;
        int regionY = client.baseY >> 6;
        int regionId = (regionX * 256) + regionY;
        int height = Client.plane;
        boolean inEdgeShops = false;
        boolean excludedRegion = false;

        if (disabledSnow) {
            return;
        }
        if (absX >= 3076 && absX <= 3101 && absY >= 3507 && absY <= 3513 // Shops area
       // || absX >= 3034 && absX <= 3055 && absY >= 3371 && absY <= 3387 // Dice area
       // || absX >= 3086 && absX <= 3094 && absY >= 3506 && absY <= 3516 // Shops 2
       // || absX >= 3097 && absX <= 3106 && absY >= 3512 && absY <= 3517 // Shops 3
       // || absX >= 3107 && absX <= 3112 && absY >= 3497 && absY <= 3508 // Shops 4
        || absX >= 3091 && absX <= 3101 && absY >= 3483 && absY <= 3499 // Bank 1
       // || absX >= 3101 && absX <= 3104 && absY >= 3483 && absY <= 3493 // Bank 2
        ) {
            inEdgeShops = true;
        }
        if (regionId == 12102 || regionId == 10055 || regionId == 13898 ||
                regionId == 13643 || regionId == 13898 || regionId == 14954
                || regionId == 13646 || regionId == 13645 || regionId == 13644
                || regionId == 13404 || regionId == 13659 || regionId == 13915
                || regionId == 13914 || regionId == 13658 || regionId == 13659
                || regionId == 13134 || regionId == 13135 || regionId == 13133
                || regionId == 11343 || regionId == 11605 || regionId == 10058
                || regionId == 9547 || regionId == 10314) {
            excludedRegion = true;
        }

        if(Client.underlays == null || Client.overlays == null)
            return;

        if (underRoof)
            return;

        if (absY > 4160 || height > 0 || inEdgeShops) {
           if (!excludedRegion)
               return;
        }

        final int underlay = Client.underlays[Client.plane][localX][localY];
        boolean contains = false;
        for (int excluded : snowyUnderlays){
            if (excluded == underlay){
                contains = true;
                break;
            }
        }
        if (!contains)
            return;

        int snowInterfaceId = 11877;
        client.processWidgetAnimations(client.tickDelta, snowInterfaceId);

        Widget rsinterface = Widget.interfaceCache[snowInterfaceId];

        if (ClientUI.frameMode == Client.ScreenMode.FIXED) {
            client.drawInterface(0, 0, rsinterface, 0, 4, 4);
        } else {

            int repeatHorizontal = (int) Math.ceil((double) ClientUI.frameWidth / interfaceWidth);
            int repeatVertical = (int) Math.ceil((double) ClientUI.frameHeight / interfaceHeight);
            Widget.interfaceCache[11878].width = childWith;
            Widget.interfaceCache[11878].height = childHeight;
            Widget.interfaceCache[11878].modelZoom = modelZoom;
            Widget.interfaceCache[11878].modelYAngle = 178;
            for (int h = 0; h < repeatHorizontal; h++) {
                for (int v = 0; v < repeatVertical; v++) {
                    client.drawInterface(0,
                            h * interfaceWidth,
                            rsinterface,
                            v * interfaceHeight,
                            h * interfaceWidth,
                            v * interfaceHeight);
                }
            }
        }
    }
}
