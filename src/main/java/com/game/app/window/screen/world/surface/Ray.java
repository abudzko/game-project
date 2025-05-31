package com.game.app.window.screen.world.surface;

import lombok.Builder;
import lombok.Getter;
import org.joml.Vector3f;

@Getter
@Builder
public class Ray {
    private final Vector3f startPoint;
    private final Vector3f directionPoint;

    public Ray(Vector3f startPoint, Vector3f directionPoint) {
        this.startPoint = startPoint;
        this.directionPoint = directionPoint;
    }
}
