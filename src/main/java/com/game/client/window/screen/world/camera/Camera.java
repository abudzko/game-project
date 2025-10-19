package com.game.client.window.screen.world.camera;

import com.game.client.window.event.listener.AbstractWindowEventListener;
import com.game.client.window.event.resize.ResizeWindowEvent;
import com.game.client.window.screen.world.surface.Intersection;
import com.game.client.window.screen.world.surface.StaticDynamicSurface;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Optional;

public class Camera extends AbstractWindowEventListener {
    private final CameraState cameraState;
    private final SurfaceIntersectionFinder surfaceIntersectionFinder;

    private Camera(CameraState cameraState, SurfaceIntersectionFinder surfaceIntersectionFinder, CameraEventHandler cameraEventHandler) {
        this.cameraState = cameraState;
        this.surfaceIntersectionFinder = surfaceIntersectionFinder;
        addEventChildListener(cameraEventHandler);
    }

    public static Camera createCamera(StaticDynamicSurface surface, int width, int height) {
        var cameraState = new CameraState();
        cameraState.setCameraWidth(width);
        cameraState.setCameraHeight(height);
        var surfaceIntersectionFinder = SurfaceIntersectionFinder.builder()
                .staticDynamicSurface(surface)
                .cameraState(cameraState)
                .build();
        return new Camera(
                cameraState,
                surfaceIntersectionFinder,
                new CameraEventHandler(cameraState, surfaceIntersectionFinder)
        );
    }

    public void setCameraViewMatrixChanged(boolean changed) {
        getCameraState().setCameraViewMatrixChanged(changed);
    }

    public Matrix4f getCameraViewMatrixCopy() {
        return getCameraState().getCameraViewMatrixCopy();
    }

    public Optional<Matrix4f> getCameraViewMatrixCopyIfChanged() {
        if (getCameraState().isCameraViewMatrixChanged()) {
            return Optional.of(getCameraViewMatrixCopy());
        }
        return Optional.empty();
    }

    public Vector3f getCameraPosition() {
        return cameraState.getCameraPosition();
    }

    public Matrix4f createProjectionMatrix() {
        return cameraState.createProjectionMatrix();
    }

    public Intersection findIntersection(double x, double y) {
        return surfaceIntersectionFinder.findIntersection(x, y);
    }

    private CameraState getCameraState() {
        return cameraState;
    }

    @Override
    public void event(ResizeWindowEvent event) {
        super.event(event);
        getCameraState().setCameraWidth(event.getNewWidth());
        getCameraState().setCameraHeight(event.getNewHeight());
    }
}
