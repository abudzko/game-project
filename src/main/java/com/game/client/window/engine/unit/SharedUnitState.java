package com.game.client.window.engine.unit;

import com.game.client.window.engine.action.GameUnitAction;
import com.game.client.window.lwjgl.program.Light;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Shared between {@link com.game.client.window.engine.unit.GameUnit} and {@link GameUnit}
 * <p>
 * Required to reflect and sync changes between these classes
 */
@Getter
@Setter
@Builder
public class SharedUnitState {
    private long gameUnitId;
    @Builder.Default
    private volatile ImmutableWorldMatrix immutableWorldMatrix = ImmutableWorldMatrix.builder().position(new Vector3f(0, 0, 0)).build();
    private volatile ImmutableWorldMatrix lwjglWorldMatrix;

    public Vector3f getPosition() {
        return immutableWorldMatrix.getPosition();
    }

    public Matrix4f getWorldMatrix() {
        return immutableWorldMatrix.getWorldMatrix();
    }

    /**
     * Set new position but to sync with worldMatrix use {@link SharedUnitState#updateWorldMatrix(Vector3f)}
     */
    public void updatePosition(Vector3f position) {
        this.immutableWorldMatrix = ImmutableWorldMatrix.builder().position(position).worldMatrix(getWorldMatrix()).build();
    }

    /**
     * Angles are measured in degrees
     */
    @Builder.Default
    private Vector3f rotation = new Vector3f(0, 0, 0);
    @Builder.Default
    private float scale = 1;
    /**
     * Units which frequently change their position
     */
    private boolean dynamic;
    private Light light;
    private GameUnitAction gameUnitAction;

    /**
     * Save position and rebuild worldMatrix
     */
    public void updateWorldMatrix(Vector3f position) {
        var matrix4f = new Matrix4f();
        matrix4f.translate(position)
                .rotateX((float) Math.toRadians(getRotation().x))
                .rotateY((float) Math.toRadians(getRotation().y))
                .rotateZ((float) Math.toRadians(getRotation().z))
                .scale(getScale());
        this.immutableWorldMatrix = ImmutableWorldMatrix.builder().position(position).worldMatrix(matrix4f).build();
    }

    /**
     * Called in render loop when we have to pass word matrix in lwjgl
     */
    public void prepareLwjglWorldMatrix() {
        this.lwjglWorldMatrix = getImmutableWorldMatrix();
    }
}
