package com.game.client.window.screen.world.camera;

import com.game.client.window.screen.world.CameraToWorldConverter;
import com.game.client.window.screen.world.surface.Ray;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CameraState {
    @Getter
    private final float fov = (float) Math.toRadians(60f);
    @Setter
    @Getter
    private volatile ImmutableCameraPosition cameraPosition = ImmutableCameraPosition.builder()
            .eyePosition(new Vector3f(0, 5, 1))
            .centerPosition(new Vector3f(0, 2, 0))
            .build();
    private volatile Vector3f upVector = new Vector3f(0, 1, 0);
    // zNear should be less than moveStep to correctly handle camera intersection with surface
    @Getter
    private float zNear = 0.005f;
    @Getter
    private float zFar = 1000.f;
    @Getter
    private float moveStep = 1.5f;
    @Getter
    private float rotationStepDegree = 0.5f;
    private volatile Matrix4f cameraViewMatrix;
    @Getter
    @Setter
    private Matrix4f projectionMatrix;
    @Setter
    @Getter
    private volatile boolean cameraViewMatrixChanged = false;

    @Setter
    @Getter
    private int cameraWidth;
    @Setter
    @Getter
    private int cameraHeight;

    @Setter
    private volatile Vector3f moveDirection = new Vector3f(0, 0, 0);

    public Matrix4f getCameraViewMatrixCopy() {
        return new Matrix4f(cameraViewMatrix);
    }

    public Matrix4f createProjectionMatrix() {
        var projectionMatrix = new Matrix4f();
        projectionMatrix.perspective(
                getFov(),
                getCameraWidth() / (float) getCameraHeight(),
                getZNear(),
                getZFar()
        );
        return projectionMatrix;
    }

    public void setCameraViewMatrix(Matrix4f cameraViewMatrix) {
        this.cameraViewMatrix = cameraViewMatrix;
        cameraViewMatrixChanged = true;
    }

    public Ray getRay(double x, double y) {
        var converter = CameraToWorldConverter.builder()
                .mouseX(x)
                .mouseY(y)
                .projectionMatrix(createProjectionMatrix())
                .viewMatrix(getCameraViewMatrixCopy())
                .width(getCameraWidth())
                .height(getCameraHeight())
                .build();
        var directionPoint = converter.directionPoint();
        return Ray.builder().startPoint(getCameraPosition().getEyePosition()).directionPoint(directionPoint).build();
    }

    public void look() {
        var m = new Matrix4f();
        var cameraPosition = getCameraPosition();
        m.lookAt(cameraPosition.getEyePosition(), cameraPosition.getCenterPosition(), up());
        setCameraViewMatrix(m);
    }

    private Vector3f up() {
        return new Vector3f(upVector);
    }

    public Vector3f moveDirection() {
        return new Vector3f(moveDirection);
    }
}
