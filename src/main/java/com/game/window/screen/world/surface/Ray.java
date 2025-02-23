package com.game.window.screen.world.surface;

import org.joml.Vector3f;

public class Ray {
    private final Vector3f startPoint;
    private final Vector3f directionPoint;

    public Ray(Vector3f startPoint, Vector3f directionPoint) {
        this.startPoint = startPoint;
        this.directionPoint = directionPoint;
    }

    public Vector3f getStartPoint() {
        return startPoint;
    }

    public Vector3f getDirectionPoint() {
        return directionPoint;
    }
}
