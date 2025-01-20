package com.game.lwjgl.program;

import com.game.model.DrawableModel;
import org.joml.Matrix4f;

public class RenderObjects {
    private Iterable<DrawableModel> models;
    private float[] cameraPosition;
    private Iterable<DrawableModel> deletedModels;
    private Matrix4f cameraViewMatrix;
    private Matrix4f projectionMatrix;

    public Iterable<DrawableModel> getModels() {
        return models;
    }

    public void setModels(Iterable<DrawableModel> models) {
        this.models = models;
    }

    public Iterable<DrawableModel> getDeletedModels() {
        return deletedModels;
    }

    public void setDeletedModels(Iterable<DrawableModel> deletedModels) {
        this.deletedModels = deletedModels;
    }

    public Matrix4f getCameraViewMatrix() {
        return cameraViewMatrix;
    }

    public void setCameraViewMatrix(Matrix4f cameraViewMatrix) {
        this.cameraViewMatrix = cameraViewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public void setProjectionMatrix(Matrix4f projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }

    public float[] getCameraPosition() {
        return cameraPosition;
    }

    public void setCameraPosition(float[] cameraPosition) {
        this.cameraPosition = cameraPosition;
    }
}
