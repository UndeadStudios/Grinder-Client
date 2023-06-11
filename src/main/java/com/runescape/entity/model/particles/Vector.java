package com.runescape.entity.model.particles;

public class Vector {

    public static final Vector ZERO = new Vector(0, 0, 0);
    private int x;
    private int y;
    private int z;

    public Vector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Vector subtract(Vector other) {
        return new Vector(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Vector divide(float scalar) {
        return new Vector((int)(this.x / scalar), (int)(this.y / scalar), (int)(this.z / scalar));
    }

    public Vector addLocal(Vector other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }

    public Vector mix(Vector other, float x, float y, float z) {
        return new Vector(this.x + (int)(other.x * x), this.y + (int)(other.y * y), this.z + (int)(other.z * z));
    }

    @Override
    public Vector clone() {
        return new Vector(this.x, this.y, this.z);
    }
    public int randomWithRange(int min, int max)
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }
    @Override
    public String toString() {
        return "Vector{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}

