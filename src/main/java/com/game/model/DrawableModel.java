package com.game.model;

import org.joml.Matrix4f;

public class DrawableModel {
    private final int vaoId;
    private final GraphicUnit graphicUnit;

    public DrawableModel(
            int vaoId,
            GraphicUnit graphicUnit
    ) {
        this.vaoId = vaoId;
        this.graphicUnit = graphicUnit;
        updateWorldMatrix();
    }

    public void updateWorldMatrix() {
        getGraphicUnit().updateWorldMatrix();
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getIndexCount() {
        return getGraphicUnit().getModel().geIndexCount();
    }

    public int getTextureId() {
        return getGraphicUnit().getModel().modelTexture().textureId();
    }

    public boolean isLight() {
        return getGraphicUnit().getModel().isLight();
    }

    public Light getLight() {
        return getGraphicUnit().getModel().getLight();
    }

    private GraphicUnit getGraphicUnit() {
        return graphicUnit;
    }

    public Matrix4f getWorldMatrix() {
        return getGraphicUnit().getWorldMatrix();
    }
}
