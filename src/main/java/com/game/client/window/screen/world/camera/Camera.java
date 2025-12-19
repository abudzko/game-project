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

    private Camera(
            CameraState cameraState,
            SurfaceIntersectionFinder surfaceIntersectionFinder,
            PlayerEventHandler playerEventHandler) {
        this.cameraState = cameraState;
        this.surfaceIntersectionFinder = surfaceIntersectionFinder;
        addEventChildListener(playerEventHandler);
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
                new PlayerEventHandler(cameraState, surfaceIntersectionFinder)
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

    public void follow(Vector3f position) {
        var dx = position.x - cameraState.getCenterX();
        var dy = position.y - cameraState.getCenterY();
        var dz = position.z - cameraState.getCenterZ();

        cameraState.setMoveDirectionX(dx);
        cameraState.setMoveDirectionY(dy);
        cameraState.setMoveDirectionZ(dz);

        float eyeX = cameraState.getEyeX() + dx;
        float eyeY = cameraState.getEyeY() + dy;
        float eyeZ = cameraState.getEyeZ() + dz;
        var cameraPosition = new Vector3f(eyeX, eyeY, eyeZ);
        cameraPosition = CameraUtils.resolveCameraPositionIfUnderSurface(cameraPosition, surfaceIntersectionFinder, cameraState);
        cameraState.setCenterX(position.x);
        cameraState.setCenterY(position.y);
        cameraState.setCenterZ(position.z);

        cameraState.setEyeX(cameraPosition.x);
        cameraState.setEyeY(cameraPosition.y);
        cameraState.setEyeZ(cameraPosition.z);

        cameraState.look();
    }

    public Matrix4f createProjectionMatrix() {
        return cameraState.createProjectionMatrix();
    }

    public Intersection findIntersection(double x, double y) {
        return surfaceIntersectionFinder.findIntersection(x, y);
    }

    public Intersection findIntersection(Vector3f position) {
        return surfaceIntersectionFinder.findIntersection(position);
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
