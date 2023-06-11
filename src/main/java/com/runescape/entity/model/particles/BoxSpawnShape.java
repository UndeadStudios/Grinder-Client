package com.runescape.entity.model.particles;

import java.util.Random;

public class BoxSpawnShape implements SpawnShape {
    private Vector center;
    private Vector radius;

    public BoxSpawnShape(Vector center, Vector radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public Vector getPoint(Random r) {
        return center.mix(radius, (r.nextFloat() - 0.5f) * 2f, (r.nextFloat() - 0.5f) * 2f, (r.nextFloat() - 0.5f) * 2f);
    }
}

