package com.game.client.window.lwjgl.program;

import com.game.client.window.engine.unit.GameUnit;
import lombok.Getter;
import org.joml.Matrix4f;

@Getter
public class LwjglUnitImpl implements LwjglUnit {
    private final int vaoId;
    private final int textureId;
    private final GameUnit gameUnit;

    public LwjglUnitImpl(
            int vaoId,
            int textureId,
            GameUnit gameUnit
    ) {
        this.vaoId = vaoId;
        this.textureId = textureId;
        this.gameUnit = gameUnit;
    }

    @Override
    public int getIndexCount() {
        return getGameUnit().getModel().indexesCount();
    }

    @Override
    public boolean useShading() {
        return gameUnit.isUseShading() && !isLight();
    }

    @Override
    public Light getLight() {
        return getGameUnit().getSharedUnitState().getLight();
    }

    private GameUnit getGameUnit() {
        return gameUnit;
    }

    @Override
    public Matrix4f getWorldMatrix() {
        return getGameUnit().getSharedUnitState().getLwjglWorldMatrix().getWorldMatrix();
    }

    @Override
    public void prepareWorldMatrix() {
        getGameUnit().getSharedUnitState().prepareLwjglWorldMatrix();
    }
}
