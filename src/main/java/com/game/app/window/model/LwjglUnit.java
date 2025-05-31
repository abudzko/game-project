package com.game.app.window.model;

import lombok.Getter;
import org.joml.Matrix4f;

public class LwjglUnit {
    @Getter
    private final int vaoId;
    private final GraphicUnit graphicUnit;

    public LwjglUnit(
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

    public int getIndexCount() {
        return getGraphicUnit().getModel().indexesCount();
    }

    public int getTextureId() {
        return getGraphicUnit().getModel().modelTexture().textureId();
    }

    public boolean isLight() {
        return getGraphicUnit().getModel().isLight();
    }

    public boolean useShading() {
        return getGraphicUnit().getModel().useShading();
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
