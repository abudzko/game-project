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
        return getGraphicUnit().getModel().modelTexture().getTextureId();
    }

    public boolean isLight() {
        return getGraphicUnit().getLight() != null;
    }

    public boolean useShading() {
        return graphicUnit.isUseShading() && !isLight();
    }

    public Light getLight() {
        return getGraphicUnit().getLight();
    }

    private GraphicUnit getGraphicUnit() {
        return graphicUnit;
    }

    public Matrix4f getWorldMatrix() {
        return getGraphicUnit().getWorldMatrix();
    }
}
