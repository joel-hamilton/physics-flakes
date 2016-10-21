package com.hamilton.joel.physicsflakes;

/**
 * Created by joel on 29/09/15.
 */
public class Flake {
    public float x;
    public float y;
    public float size;
    public double distanceFromTouch = 1;
    public double velocity;
    public double launchAngle;
    public boolean toFling;

    public Flake(float x, float y, float size) {
        this.x = x;
        this.y = y;
        this.size = size;
  }
}
