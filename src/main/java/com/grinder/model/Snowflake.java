package com.grinder.model;

public class Snowflake {

    private int x;
    private int y;
    private int radius;
    private int opacity;
    private int moveX;
    private int moveY;

    Snowflake() {
        this.x = randomize(Snowfall.LOGIN_WIDTH);
        this.y = randomize(Snowfall.LOGIN_HEIGHT * -1);
        this.radius = randomize(1, 4);
        this.opacity = randomize(80, 200);
        this.moveX = randomize(-1, 1);
        this.moveY = randomize(1, 3);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getOpacity() {
        return opacity;
    }

    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }

    public void incrementX() {
        this.x += moveX;
    }

    public void incrementY() {
        this.y += moveY;
    }

    public static int randomize(double max){
        return (int) (Math.random() * (max + 1));
    }

    public static int randomize(double min, double max){
        return (int) ((Math.random() * ((max-min) + 1)) + min);
    }
}
