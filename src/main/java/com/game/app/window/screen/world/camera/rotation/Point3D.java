package com.game.app.window.screen.world.camera.rotation;

import lombok.Getter;

@Getter
public class Point3D {
    private double x, y, z;

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", x, y, z);
    }

}
