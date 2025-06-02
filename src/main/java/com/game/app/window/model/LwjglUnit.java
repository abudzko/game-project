package com.game.app.window.model;

import lombok.Getter;
import org.joml.Matrix4f;

@Getter
public class LwjglUnit {
    private final int vaoId;
    private final int textureId;
    private final GraphicUnit graphicUnit;

    public LwjglUnit(
            int vaoId,
            int textureId,
            GraphicUnit graphicUnit
    ) {
        this.vaoId = vaoId;
        this.textureId = textureId;
        this.graphicUnit = graphicUnit;
        updateWorldMatrix();
    }

    public void updateWorldMatrix() {
        getGraphicUnit().updateWorldMatrix();
    }

    public int getIndexCount() {
        return getGraphicUnit().getModel().indexesCount();
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
