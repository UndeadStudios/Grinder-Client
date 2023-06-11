package com.grinder.model;

import com.runescape.Client;
import com.runescape.cache.def.NpcDefinition;
import com.runescape.cache.graphics.widget.FontType;
import com.runescape.cache.graphics.widget.WidgetAlignment;
import com.runescape.cache.graphics.widget.WidgetBuilder;
import com.runescape.cache.graphics.widget.component.ModelComponent;
import com.runescape.cache.graphics.widget.component.RectangleComponent;
import com.runescape.cache.graphics.widget.component.TextComponent;
import com.runescape.io.Buffer;

import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 07/04/2020
 */
public final class MonsterHuntTrackerManager {

    private final static int WIDGET_ID = 24000;
    private static MonsterHuntTracker activeTracker;
    private static TextComponent timeComponent = new TextComponent(FontType.NORMAL, Color.WHITE, false, true, 10, 3);
    private static TextComponent nameComponent = new TextComponent(FontType.NORMAL, Color.GREEN, false, true, 55, 3);
    private static ModelComponent modelComponent = new ModelComponent(5, 5);
    private static WidgetBuilder builder = new WidgetBuilder(WIDGET_ID, 150, 20, WidgetAlignment.BOTTOM_LEFT);

    static {
        builder.addChild(new RectangleComponent(5, 0, 45, 20, 100, Color.BLACK, true));
//        builder.addChild(new RectangleComponent(45, 0, 200, 20, 150, Color.DARK_GRAY, true));
//        builder.addChild(modelComponent);
        builder.addChild(timeComponent);
        builder.addChild(nameComponent);
        builder.create();
    }

    private static void set(int npcId, long startTime, int duration){
        final long communicationDelay = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime);
        final int finalDuration = Math.toIntExact(Math.max(0, duration - communicationDelay));
        activeTracker = new MonsterHuntTracker(npcId, finalDuration);
        final NpcDefinition definition = NpcDefinition.lookup(npcId);
        if(definition != null){
            modelComponent.setMediaId(definition.modelId[0]);
            modelComponent.setMediaType(1);
            modelComponent.setAnimation(-1);
        }
    }

    public static void parsePacket(Buffer buffer){
        final int npcId = buffer.readShort();
        final long startTime = buffer.readLong();
        final int duration = buffer.readShort();
        set(npcId, startTime, duration);
    }

    public static void updateTracker(){

        if(activeTracker != null){

            if(activeTracker.expired()){
                activeTracker = null;
                return;
            }

            nameComponent.setText("Kill "+activeTracker.getNpcName());
            timeComponent.setText(activeTracker.getTimeLeftAsString());
        }
    }

    public static void drawTracker(Client client){
        if(activeTracker != null) {
            builder.update();
            client.drawInterfaceInScreen(WIDGET_ID);
        }
    }
}
