package com.game.client.window.lwjgl.program;

import com.game.client.window.screen.unit.ScreenUnit;
import lombok.Getter;
import org.joml.Matrix4f;

@Getter
public class LwjglUnitImpl implements LwjglUnit {
    private final int vaoId;
    private final int textureId;
    private final ScreenUnit screenUnit;

    public LwjglUnitImpl(
            int vaoId,
            int textureId,
            ScreenUnit screenUnit
    ) {
        this.vaoId = vaoId;
        this.textureId = textureId;
        this.screenUnit = screenUnit;
    }

    @Override
    public int getIndexCount() {
        return getScreenUnit().getModel().indexesCount();
    }

    @Override
    public boolean useShading() {
        return screenUnit.isUseShading() && !isLight();
    }

    @Override
    public Light getLight() {
        return getScreenUnit().getSharedUnitState().getLight();
    }

    private ScreenUnit getScreenUnit() {
        return screenUnit;
    }

    @Override
    public Matrix4f getWorldMatrix() {
        return getScreenUnit().getSharedUnitState().getLwjglWorldMatrix().getWorldMatrix();
    }

    @Override
    public void prepareWorldMatrix() {
        getScreenUnit().getSharedUnitState().prepareLwjglWorldMatrix();
    }
}
