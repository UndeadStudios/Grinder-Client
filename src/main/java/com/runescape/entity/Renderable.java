package com.runescape.entity;

import com.runescape.cache.VertexNormal;
import com.runescape.collection.DualNode;
import com.runescape.entity.model.Model;

public class Renderable extends DualNode {

    public int height;

    public Renderable() {
        height = 1000;
    }

    public void renderDraw(int i, int j, int k, int l, int i1, int j1, int k1, int l1, long uid) {
        Model model = getRotatedModel();
        if (model != null) {
            height = model.height;
            model.renderDraw(i, j, k, l, i1, j1, k1, l1, uid);
        }
    }

    public Model getRotatedModel() {
        return null;
    }
}