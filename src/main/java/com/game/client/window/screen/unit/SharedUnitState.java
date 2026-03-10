package com.game.client.window.screen.unit;

import com.game.client.window.engine.action.GameUnitAction;
import com.game.client.window.lwjgl.program.Light;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Shared between {@link com.game.client.window.engine.unit.GameUnit} and {@link ScreenUnit}
 * <p>
 * Required to reflect and sync changes between these classes
 */
@Getter
@Setter
@Builder
public class SharedUnitState {
    private long gameUnitId;
    @Builder.Default
    private volatile Vector3f position = new Vector3f(0, 0, 0);
    /**
     * Angles are measured in degrees
     */
    @Builder.Default
    private Vector3f rotation = new Vector3f(0, 0, 0);
    @Builder.Default
    private float scale = 1;
    private volatile Matrix4f worldMatrix;
    /**
     * Units which frequently change their position
     */
    private boolean dynamic;
    private Light light;
    private GameUnitAction gameUnitAction;

    public void updateWorldMatrix() {
        var matrix4f = new Matrix4f();
        matrix4f.translate(getPosition())
                .rotateX((float) Math.toRadians(getRotation().x))
                .rotateY((float) Math.toRadians(getRotation().y))
                .rotateZ((float) Math.toRadians(getRotation().z))
                .scale(getScale());
        this.worldMatrix = matrix4f;
    }
}
