package com.hamilton.joel.wallpaper;

/**
 * Created by joel on 29/09/15.
 */
public class Flake {
    public float x;
    public float y;
    public float size;
    public double distanceFromTouch = 1;
    public double velocity; //TODO set this with ontouchlistener, recalculate with each redraw
    public double launchAngle; //TODO
    public boolean toFling;

    public Flake(float x, float y, float size) {
        this.x = x;
        this.y = y;
        this.size = size;
  }
}
