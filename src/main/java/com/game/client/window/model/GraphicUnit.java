package com.game.client.window.model;

import com.game.client.window.lwjgl.program.Light;
import com.game.client.window.model.obj.Model;
import lombok.Builder;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Represents the game unit:
 * - positions
 * - rotation
 * - scale
 */
@Getter
@Builder
public class GraphicUnit {
    private long gameUnitId;
    private Vector3f position;
    /**
     * Angles are measured in degrees
     */
    private Vector3f rotation;
    private float scale;
    private volatile Matrix4f worldMatrix;
    private Model model;
    /**
     * Units which frequently change their position
     */
    private boolean dynamic;
    /**
     * Units which can be selected
     */
    @Builder.Default
    private boolean isSurface = true;
    private Light light;
    /**
     * Some units should not have shadows, for ex. the sky
     */
    @Builder.Default
    private boolean useShading = true;

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
