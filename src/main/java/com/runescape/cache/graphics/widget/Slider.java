package com.runescape.cache.graphics.widget;

import com.grinder.Configuration;
import com.grinder.model.PlayerSettings;
import com.runescape.Client;
import com.runescape.audio.Audio;
import com.runescape.cache.def.ItemDefinition;
import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.draw.Rasterizer3D;
import com.runescape.util.MiscUtils;
import com.grinder.client.ClientCompanion;

public class Slider {

    /**
     * The inset (in pixels) to start/end the actual slider region on the slider sprite (makes edges of slider not part of dragging region)
     */
    private static final int SLIDER_INSET = 13;

    public final static int ZOOM = 1;
    public final static int BRIGHTNESS = 2;
    public final static int MUSIC = 3;
    public final static int SOUND = 4;
    public final static int AMBIENT_SOUND = 5;

    private final double minValue, maxValue, length;
    private final Sprite[] images = new Sprite[2];
    private int position = 86;

    public double getValue() {
        return value;
    }

    private double value;

    private static Widget selectedSlider;
    private static int selectedDrawX = -1, selectedClickX = -1;

    public static Widget getSelectedSlider() {
        return selectedSlider;
    }

    public static void setSelectedSlider(Widget widget) {
        selectedSlider = widget;
    }

    public static int getSelectedDrawX() {
        return selectedDrawX;
    }

    public static void setSelectedDrawX(int drawX) {
        selectedDrawX = drawX;
    }
    public static int getSelectedClickX() {
        return selectedClickX;
    }

    public static void setSelectedClickX(int clickX) {
        selectedClickX = clickX;
    }

    public static void setSelected(Widget widget, int drawX, int clickX) {
        setSelectedSlider(widget);
        setSelectedDrawX(drawX);
        setSelectedClickX(clickX);
    }

    public Slider(Sprite icon, Sprite background, double minimumValue, double maximumValue) {
        this.images[0] = icon;
        this.images[1] = background;
        this.minValue = this.value = minimumValue;
        this.maxValue = maximumValue;
        this.length = this.images[1].myWidth - (SLIDER_INSET * 2);
    }

    public static void handleDragging() {
        Widget widget = selectedSlider;
        Slider slider = widget.slider;
        int dragX = Client.instance.getMouseX() - getSelectedClickX();
        int positionX = getSelectedClickX() - getSelectedDrawX();

        slider.position = MiscUtils.ensureRange(positionX + dragX - SLIDER_INSET, 0, (int) slider.length);

        double val = slider.minValue + (slider.position / slider.length) * (slider.maxValue - slider.minValue);
        if (val < slider.minValue) {
            val = slider.minValue;
        } else if (val > slider.maxValue) {
            val = slider.maxValue;
        }
        double oldVal = slider.value;
        slider.value = val;

        if (val != oldVal) {
            handleContent(widget);
        }
    }

    public void draw(int x, int y) {
        images[1].drawSprite(x, y);
        images[0].drawSprite(x + SLIDER_INSET - (images[0].myWidth / 2) + position, y - images[0].myHeight / 2 + images[1].myHeight / 2);
    }

    private static void handleContent(Widget widget) {
        int contentType = widget.contentType;
        Slider slider = widget.slider;
        switch (contentType) {
            case ZOOM:
                Client.cameraZoom = (int) (slider.minValue + slider.maxValue - slider.value);
                break;
            case BRIGHTNESS:
                ClientCompanion.brightnessState = slider.minValue + slider.maxValue - slider.value;
                Rasterizer3D.changeBrightness(slider.minValue + slider.maxValue - slider.value);
                ItemDefinition.sprites.clear();
                break;
            case MUSIC:
                int musicVolume = Math.max((int) (slider.value - (slider.maxValue - 255D)), 0);
                Configuration.gameMusicVolume = musicVolume;
                Audio.updateMusicTrackVolumeDirect(musicVolume);
                break;
            case SOUND:
                int soundVolume = Math.max((int) (slider.value - (slider.maxValue - 127D)), 0);
                Audio.updateSoundEffectDirect(soundVolume);
                break;
            case AMBIENT_SOUND:
                int ambientSoundVolume = Math.max((int) (slider.value - (slider.maxValue - 127D)), 0);
                Audio.updateObjectSoundVolumeDirect(ambientSoundVolume);
                break;
        }
        PlayerSettings.savePlayerData(Client.instance);
    }

    public double getPercentage() {
        return ((position / length) * 100);
    }

    public void setValue(double value) {
        if (value < minValue) {
            value = minValue;
        } else if (value > maxValue) {
            value = maxValue;
        }

        this.value = value;
        double shift = 1 - ((value - minValue) / (maxValue - minValue));

        position = (int) (length * shift);
    }

}