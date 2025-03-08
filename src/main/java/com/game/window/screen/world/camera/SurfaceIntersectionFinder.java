package com.game.window.screen.world.camera;

import com.game.window.screen.world.surface.Intersection;
import com.game.window.screen.world.surface.StaticDynamicSurface;
import lombok.Builder;

import java.util.Optional;

@Builder
public class SurfaceIntersectionFinder {

    private final StaticDynamicSurface staticDynamicSurface;
    private final CameraState cameraState;

    public Intersection findIntersection(double x, double y) {
        return staticDynamicSurface.findIntersection(getCameraState().getRay(x, y));
    }

    /**
     * Find the point on surface what is under or above of camera
     */
    public Intersection findCameraIntersection() {
        return Optional.ofNullable(staticDynamicSurface.findIntersection(getCameraState().getRayFromCamera(-1))).orElseGet(
                () -> staticDynamicSurface.findIntersection(getCameraState().getRayFromCamera(1))
        );
    }

    private CameraState getCameraState() {
        return cameraState;
    }
}
