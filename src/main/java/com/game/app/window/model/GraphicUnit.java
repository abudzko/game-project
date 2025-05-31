package com.game.app.window.model;

import com.game.app.window.model.obj.Model;
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
    private long id;
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
    private boolean isSurface;

    public void updateWorldMatrix() {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.translate(getPosition())
                .rotateX((float) Math.toRadians(getRotation().x))
                .rotateY((float) Math.toRadians(getRotation().y))
                .rotateZ((float) Math.toRadians(getRotation().z))
                .scale(getScale());
        this.worldMatrix = matrix4f;
    }

    public static class GraphicUnitBuilder {
        private boolean isSurface = true;

        public GraphicUnit build() {
            var graphicUnit = new GraphicUnit(id, position, rotation, scale, null, model, dynamic, isSurface);
            graphicUnit.updateWorldMatrix();
            return graphicUnit;
        }
    }
}
