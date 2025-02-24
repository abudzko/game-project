package com.game.window.screen.world.camera;

import org.joml.Matrix4f;

class CameraState {

    float centerY = 0;
    float eyeX = 0;
    float eyeY = 1;
    float eyeZ = 1;
    float centerX = 0;
    float centerZ = 0;
    float upX = 0;
    float upY = 1f;
    float upZ = 0f;
    //+Oz
    float angle = 0f;
    boolean isRightMousePressed = false;
    boolean isRightMouseReleased = true;
    float cursorPositionX;
    float cursorPositionY;
    float previousCursorPositionX;
    float previousCursorPositionY;
    private final float fov = (float) Math.toRadians(60f);
    private final float zNear = 0.05f;
    private final float zFar = 120.f;
    private final float moveStep = 0.1f;
    private Matrix4f cameraViewMatrix;
    private boolean cameraViewMatrixChanged = false;

    public boolean isCameraViewMatrixChanged() {
        return cameraViewMatrixChanged;
    }

    public void setCameraViewMatrixChanged(boolean cameraViewMatrixChanged) {
        this.cameraViewMatrixChanged = cameraViewMatrixChanged;
    }

    public float getMoveStep() {
        return moveStep;
    }

    public float getFov() {
        return fov;
    }

    public float getzNear() {
        return zNear;
    }

    public float getzFar() {
        return zFar;
    }

    public Matrix4f getCameraViewMatrixCopy() {
        return new Matrix4f(cameraViewMatrix);
    }

    public void setCameraViewMatrix(Matrix4f cameraViewMatrix) {
        this.cameraViewMatrix = cameraViewMatrix;
        setCameraViewMatrixChanged(true);
    }
}
