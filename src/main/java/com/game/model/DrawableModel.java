package com.game.model;

import org.joml.Matrix4f;

public class DrawableModel {
    private final int vaoId;
    private final GraphicUnit graphicUnit;
    private volatile Matrix4f worldMatrix;

    public DrawableModel(
            int vaoId,
            GraphicUnit graphicUnit
    ) {
        this.vaoId = vaoId;
        this.graphicUnit = graphicUnit;
        updateWorldMatrix();
    }

    public void updateWorldMatrix() {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.translate(getGraphicUnit().getPosition())
                .rotateX((float) Math.toRadians(getGraphicUnit().getRotation().x))
                .rotateY((float) Math.toRadians(getGraphicUnit().getRotation().y))
                .rotateZ((float) Math.toRadians(getGraphicUnit().getRotation().z))
                .scale(getGraphicUnit().getScale());
        this.worldMatrix = matrix4f;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVerticesCount() {
        return getGraphicUnit().getModel().getVerticesCount();
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
        return worldMatrix;
    }
}
