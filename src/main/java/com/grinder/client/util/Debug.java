package com.grinder.client.util;

import com.runescape.Client;
import com.runescape.cache.NetCache;
import com.runescape.cache.graphics.GameFont;
import com.runescape.draw.Rasterizer2D;

import java.io.IOException;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 14/03/2020
 */
public class Debug {

    public static void draw(Client client, GameFont font) {

        Rasterizer2D.drawTransparentBox(0, 0,200, 500, 0, 200);

        int x = 150;
        int y = 15;
        try {
            font.drawText(0xffffff, "Stream available: "+client.getSocketStream().available(), y, x);
        } catch (IOException e) {
            font.drawText(0xffffff, "Stream error: "+e.getLocalizedMessage(), y, x);
            e.printStackTrace();
        }
        y+=15;
        font.drawText(0xffffff, "Last packets: "+client.getLastOpcode() +", "+client.getSecondLastOpcode()+", "+client.getThirdLastOpcode(), y, x);
        y+=15;
        font.drawText(0xffffff, "NetCache last read: "+ NetCache.totalTimeMillisPassed, y, x);
        y+=15;
        font.drawText(0xffffff, "NetCache pending read: "
                +NetCache.pendingResponsesCount+", "
                +NetCache.pendingPriorityResponsesCount, y, x);
        y+=15;
        font.drawText(0xffffff, "NetCache pending write: "
                +NetCache.pendingWritesCount+", "
                +NetCache.pendingPriorityWritesCount, y, x);
        y+=15;
        font.drawText(0xffffff, "NetCache expected: "+ NetCache.responseExpectedBytes, y, x);
        y+=15;
        font.drawText(0xffffff, "NetCache ioExceptions: "+ NetCache.ioExceptions, y, x);
        y+=15;
        font.drawText(0xffffff, "NetCache crc mismatches: "+ NetCache.crcMismatches, y, x);
        y+=15;
        font.drawText(0xffffff, "NetCache ?: "+ NetCache.randomValue, y, x);
    }
}
