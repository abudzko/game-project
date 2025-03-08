package com.game.window.screen.world.camera;

import com.game.window.screen.world.CameraToWorldConverter;
import com.game.window.screen.world.surface.Ray;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@Getter
@Setter
public class CameraState {
    private final float fov = (float) Math.toRadians(60f);
    private float centerY = 0;
    private float centerX = 0;
    private float centerZ = 0;
    private volatile float eyeX = 0;
    private volatile float eyeY = 1;
    private volatile float eyeZ = 1;
    private float upX = 0;
    private float upY = 1f;
    private float upZ = 0f;
    private boolean isRightMousePressed = false;
    private boolean isRightMouseReleased = true;
    private float cursorPositionX;
    private float cursorPositionY;
    private float previousCursorPositionX;
    private float previousCursorPositionY;
    private float zNear = 0.05f;
    private float zFar = 120.f;
    private float moveStep = 0.2f;
    private Matrix4f cameraViewMatrix;
    private Matrix4f projectionMatrix;
    private boolean cameraViewMatrixChanged = false;
    private Vector3f groundPosition;

    private int cameraWidth;
    private int cameraHeight;

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
        setCameraViewMatrixChanged(true);
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
        return Ray.builder().startPoint(getCameraPosition()).directionPoint(directionPoint).build();
    }

    public Ray getRayFromCamera(int y) {
        var startPoint = getCameraPosition();
        var direction = new Vector3f(0, y, 0);
        return Ray.builder().startPoint(startPoint).directionPoint(direction).build();
    }

    public Vector3f getCameraPosition() {
        return new Vector3f(getEyeX(), getEyeY(), getEyeZ());
    }

    public Vector3f getCenterPosition() {
        return new Vector3f(getCenterX(), getCenterY(), getCenterZ());
    }
}
