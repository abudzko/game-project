package com.game.model;

import com.game.model.obj.Model;
import org.joml.Vector3f;

/**
 * Represents the game unit:
 * - positions
 * - rotation
 * - scale
 */
public class GraphicUnit {
    private final long id;
    private final Vector3f position;
    /**
     * Angles are measured in degrees
     */
    private final Vector3f rotation;
    private final float scale;

    private final Model model;

    public GraphicUnit(
            long id,
            Vector3f position,
            Vector3f rotation,
            float scale,
            Model model
    ) {
        this.id = id;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.model = model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    public long getId() {
        return id;
    }

    public Model getModel() {
        return model;
    }
}
