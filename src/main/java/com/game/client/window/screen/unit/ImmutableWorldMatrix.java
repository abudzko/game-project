package com.game.client.window.screen.unit;

import lombok.Builder;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@Builder
public class ImmutableWorldMatrix {
    private final Vector3f position;
    private final Matrix4f worldMatrix;

    public Matrix4f getWorldMatrix() {
        return new Matrix4f(worldMatrix);
    }

    public Vector3f getPosition() {
        return new Vector3f(position);
    }
}
