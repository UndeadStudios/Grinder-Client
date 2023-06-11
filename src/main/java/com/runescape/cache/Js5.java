package com.runescape.cache;

import java.util.ArrayList;
import java.util.List;

public class Js5 {

    private static List<Runnable> runnables = new ArrayList<>();
    public static AbstractIndexCache skins;
    public static AbstractIndexCache skeletons;
    public static AbstractIndexCache configs;
    public static AbstractIndexCache models;

    public static void whenLoaded(Runnable runnable) {
        runnables.add(runnable);
    }

    public static void executeLoad() {
        runnables.forEach(Runnable::run);
    }

}
