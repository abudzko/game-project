package com.game.client.window.screen.world.camera;

import com.game.client.utils.log.LogUtil;
import org.joml.Vector3f;

import java.util.Optional;

public class CameraUtils {

    public static Vector3f resolveCameraPositionIfUnderSurface(
            Vector3f position,
            SurfaceIntersectionFinder surfaceIntersectionFinder,
            CameraState cameraState
    ) {
        return Optional.ofNullable(surfaceIntersectionFinder.findIntersection(position))
                .map(intersection -> {
                    var intersectionPoint = intersection.getPoint();
                    LogUtil.logDebug(false, String.format("resolveCameraPosition intersectionPoint %s, position %s", intersectionPoint, position));
                    intersectionPoint.y = intersectionPoint.y + cameraState.getMoveStep();
                    return intersectionPoint;
                })
                .filter(intersectionPoint -> intersectionPoint.y() > position.y())
                .orElse(position);
    }
}
