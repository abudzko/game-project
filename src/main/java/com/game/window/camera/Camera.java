package com.game.window.camera;

public class Camera {
    private final CameraState cameraState;

    public Camera(CameraState cameraState) {
        this.cameraState = cameraState;
    }

    public CameraState getCameraState() {
        return cameraState;
    }
}
