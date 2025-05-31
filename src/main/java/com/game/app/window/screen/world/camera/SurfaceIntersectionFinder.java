package com.game.app.window.screen.world.camera;

import com.game.app.window.screen.world.surface.Intersection;
import com.game.app.window.screen.world.surface.Ray;
import com.game.app.window.screen.world.surface.StaticDynamicSurface;
import lombok.Builder;
import org.joml.Vector3f;

import java.util.Optional;

@Builder
public class SurfaceIntersectionFinder {

    private final StaticDynamicSurface staticDynamicSurface;
    private final CameraState cameraState;

    public Intersection findIntersection(double x, double y) {
        return staticDynamicSurface.findIntersection(getCameraState().getRay(x, y));
    }

    /**
     * Find the point on surface what is under or above of position
     */
    public Intersection findIntersection(Vector3f position) {
        return Optional.ofNullable(staticDynamicSurface.findIntersection(getRayFromPosition(position, -1)))
                .orElseGet(
                        () -> staticDynamicSurface.findIntersection(getRayFromPosition(position, 1))
                );
    }

    public Ray getRayFromPosition(Vector3f position, int y) {
        var direction = new Vector3f(0, y, 0);
        return Ray.builder().startPoint(position).directionPoint(direction).build();
    }

    private CameraState getCameraState() {
        return cameraState;
    }
}
