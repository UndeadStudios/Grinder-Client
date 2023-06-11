package com.grinder.client.util;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.WriterConfig;
import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.cache.graphics.widget.Widget;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Stan van der Bend for Empyrean at 18/06/2018.
 *
 * @author https://www.rune-server.ee/members/StanDev/
 */
public class InterfaceUtility {

    private final static String SPRITE_DUMP_DIR = "dump/sprites";
    private final static String WIDGET_DUMP_FILE = "dump/interfaces.json";


    private static Map<Widget, List<Widget>> INTERFACES = new HashMap<>();
    private static Map<Widget, List<Sprite>> SPRITES = new HashMap<>();
    private static List<Sprite> allSprites = new ArrayList<>();
    static int duplicates = 0;

    public static void dumpInterfaceInformation(){

        for(Widget widget : Widget.interfaceCache){
            if(Objects.nonNull(widget)) {

                if (widget.parent > 0) {
                    Widget parent = Widget.interfaceCache[widget.parent];
                    if (Objects.nonNull(parent)) {
                        INTERFACES.putIfAbsent(parent, new ArrayList<>());
                        INTERFACES.get(parent).add(widget);
                    }
                } else {
                    INTERFACES.putIfAbsent(widget, new ArrayList<>());
                }
            }
        }

        System.out.println("[InterfaceUtility]: found "+INTERFACES.keySet().size()+" parent interfaces!");

        JsonArray jsonValues = new JsonArray();

        TreeMap<Integer, JsonValue> treeMap = new TreeMap<>();

        for(Widget widget : INTERFACES.keySet()){
            if(Objects.nonNull(widget))
                treeMap.put(widget.id, convert(widget));
        }

        treeMap.forEach((integer, jsonValue) -> jsonValues.add(jsonValue));

        try {
            FileWriter fileWriter = new FileWriter(new File(WIDGET_DUMP_FILE));

            jsonValues.writeTo(fileWriter, WriterConfig.PRETTY_PRINT);

            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private static JsonValue convert(Widget widget){
        JsonObject object = new JsonObject();
        object.add("id", widget.id);
        if(widget.type > 0) object.add("type", widget.type);
        if(widget.atActionType > 0) object.add("action", widget.atActionType);
        if(widget.contentType > 0) object.add("content", widget.contentType);
        if(widget.hoverType > 0) object.add("hover", widget.hoverType);
        if(widget.width > 0) object.add("width", widget.width);
        if(widget.height > 0) object.add("height", widget.height);
        if(widget.tooltip != null) object.add("tooltip", widget.tooltip);
        if(widget.defaultText != null)  object.add("text", widget.defaultText);
        if(widget.children != null){
            JsonArray children = new JsonArray();
            for(int childID : widget.children){
                Optional.ofNullable(Widget.interfaceCache[childID])
                        .map(InterfaceUtility::convert)
                        .ifPresent(children::add);
            }
            object.add("createChildrenBuffer", children);
        }
        return object;
    }

    public static Map<Widget, List<Widget>> getINTERFACES() {
        return INTERFACES;
    }
}
