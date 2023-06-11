package com.runescape.cache.graphics.widget.component;

import com.runescape.cache.graphics.widget.Widget;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 07/04/2020
 */
public class ModelComponent extends ChildComponent {

    public ModelComponent(int modelType, int modelId, int modelZoom, int modelRotation1, int modelRotation2, int relativeX, int relativeY) {
        super(relativeX, relativeY);
        type = Widget.TYPE_MODEL;
//        contentType
        height = width = 100;
        this.modelType = defaultMediaType = modelType;
        this.modelId = defaultMedia = modelId;
        this.modelZoom = modelZoom;
        this.modelXAngle = modelRotation1;
        this.modelYAngle = modelRotation2;
    }

    public ModelComponent(int modelType, int modelId, int relativeX, int relativeY) {
        this(modelType, modelId, 1500, 150, 0, relativeX, relativeY);
    }

    public ModelComponent(int relativeX, int relativeY) {
        this(-1, -1, relativeX, relativeY);
    }

    public void setMediaId(int modelId){
        this.modelId = defaultMedia = modelId;
    }

    public void setMediaType(int type){
        this.modelType = defaultMediaType = type;
    }

    public void setAnimation(int animationId){
        this.defaultAnimationId = animationId;
    }
}
