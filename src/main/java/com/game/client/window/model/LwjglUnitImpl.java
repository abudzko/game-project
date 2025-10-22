package com.game.client.window.model;

import com.game.client.window.lwjgl.program.Light;
import com.game.client.window.lwjgl.program.LwjglUnit;
import lombok.Getter;
import org.joml.Matrix4f;

@Getter
public class LwjglUnitImpl implements LwjglUnit {
    private final int vaoId;
    private final int textureId;
    private final GraphicUnit graphicUnit;

    public LwjglUnitImpl(
            int vaoId,
            int textureId,
            GraphicUnit graphicUnit
    ) {
        this.vaoId = vaoId;
        this.textureId = textureId;
        this.graphicUnit = graphicUnit;
    }

    @Override
    public int getIndexCount() {
        return getGraphicUnit().getModel().indexesCount();
    }

    @Override
    public boolean useShading() {
        return graphicUnit.isUseShading() && !isLight();
    }

    @Override
    public Light getLight() {
        return getGraphicUnit().getSharedUnitState().getLight();
    }

    private GraphicUnit getGraphicUnit() {
        return graphicUnit;
    }

    @Override
    public Matrix4f getWorldMatrix() {
        return getGraphicUnit().getSharedUnitState().getWorldMatrix();
    }
}
