package com.game.model;

import org.joml.Matrix4f;

import java.nio.FloatBuffer;

public class DrawableModel {
    private final int vaoId;

    private final FloatBuffer vertices;
    private final GameUnit gameUnit;
    private volatile Matrix4f worldMatrix;

    public DrawableModel(
            GameUnit gameUnit,
            int vaoId,
            FloatBuffer vertices
    ) {
        this.gameUnit = gameUnit;
        this.vaoId = vaoId;
        this.vertices = vertices;
        updateWorldMatrix();
    }

    public void updateWorldMatrix() {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.translate(gameUnit.getPosition())
                .rotateX((float) Math.toRadians(gameUnit.getRotation().x))
                .rotateY((float) Math.toRadians(gameUnit.getRotation().y))
                .rotateZ((float) Math.toRadians(gameUnit.getRotation().z))
                .scale(gameUnit.getScale());
        this.worldMatrix = matrix4f;
    }

    public int getVaoId() {
        return vaoId;
    }

    public FloatBuffer getVertices() {
        return vertices;
    }

    public int getTextureId() {
        return gameUnit.getModel().modelTexture().textureId();
    }

    public boolean isLight() {
        return gameUnit.getModel().isLight();
    }

    public Light getLight() {
        return gameUnit.getModel().getLight();
    }

    public Matrix4f getWorldMatrix() {
        return worldMatrix;
    }
}
