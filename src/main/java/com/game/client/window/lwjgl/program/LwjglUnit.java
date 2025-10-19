package com.game.client.window.lwjgl.program;

import org.joml.Matrix4f;

public interface LwjglUnit {

    int getVaoId();

    int getTextureId();

    /**
     * @return count of vertex indexes for rendering(count of vertexes to render)
     */
    int getIndexCount();

    /**
     * @return {@code true} if shading should be applied to this units
     */
    boolean useShading();

    /**
     * @return {@link Light} if current unit is source of light
     */
    Light getLight();

    /**
     * @return {@code true} if current unit is source of light
     */
    default boolean isLight() {
        return getLight() != null;
    }

    /**
     * @return matrix for transformation unit coordinates to world ones
     */
    Matrix4f getWorldMatrix();
}
