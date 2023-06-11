package com.runescape.scene.object.tile;

public final class SceneTilePaint {

    public final int swColor;
    public final int seColor;
    public final int neColor;
    public final int nwColor;
    public final int texture;
    public final boolean isFlat;
    public final int rgb;

    public SceneTilePaint(int swColor, int seColor, int neColor, int nwColor, int texture, int rgb, boolean isFlat) {
        this.swColor = swColor;
        this.seColor = seColor;
        this.neColor = neColor;
        this.nwColor = nwColor;
        this.texture = texture;
        this.rgb = rgb;
        this.isFlat = isFlat;
    }

}
