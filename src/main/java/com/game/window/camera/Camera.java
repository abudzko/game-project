package com.game.window.camera;

import com.game.window.event.listener.AbstractWindowEventListener;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Optional;

public class Camera extends AbstractWindowEventListener {
    private final CameraState cameraState;

    Camera(CameraState cameraState, CameraEventHandler cameraEventHandler) {
        this.cameraState = cameraState;
        addEventChildListener(cameraEventHandler);
    }

    public static Camera createCamera() {
        var cameraState = new CameraState();
        return new Camera(cameraState, new CameraEventHandler(cameraState));
    }

    public void setCameraViewMatrixChanged(boolean changed) {
        getCameraState().setCameraViewMatrixChanged(changed);
    }

    public Matrix4f getCameraViewMatrixCopy() {
        return new Matrix4f(getCameraState().getCameraViewMatrixCopy());
    }

    public Optional<Matrix4f> getCameraViewMatrixCopyIfChanged() {
        if (getCameraState().isCameraViewMatrixChanged()) {
            return Optional.of(getCameraViewMatrixCopy());
        }
        return Optional.empty();
    }

    public Vector3f getCameraPosition() {
        return new Vector3f(getCameraState().eyeX, getCameraState().eyeY, getCameraState().eyeZ);
    }

    public Matrix4f createProjectionMatrix(float aspectRatio) {
        var projectionMatrix = new Matrix4f();
        projectionMatrix.perspective(
                getCameraState().getFov(),
                aspectRatio,
                getCameraState().getzNear(),
                getCameraState().getzFar()
        );
        return projectionMatrix;
    }

    private CameraState getCameraState() {
        return cameraState;
    }
}
