package com.game.client.window.model;

import com.game.client.window.lwjgl.program.Light;
import com.game.client.window.screen.world.engine.action.GameUnitAction;
import com.game.client.window.screen.world.engine.unit.ChangeType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Shared between {@link com.game.client.window.screen.world.engine.unit.GameUnit} and {@link GraphicUnit}
 * <p>
 * Required to reflect and sync changes between these classes
 */
@Getter
@Setter
@Builder
public class SharedUnitState {
    private long gameUnitId;
    @Builder.Default
    private Vector3f position = new Vector3f(0, 0, 0);
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
    /**
     * Units which can be selected
     */
    private Light light;
    private GameUnitAction gameUnitAction;
    private Queue<ChangeType> changeQueue;

    public void updateWorldMatrix() {
        var matrix4f = new Matrix4f();
        matrix4f.translate(getPosition())
                .rotateX((float) Math.toRadians(getRotation().x))
                .rotateY((float) Math.toRadians(getRotation().y))
                .rotateZ((float) Math.toRadians(getRotation().z))
                .scale(getScale());
        this.worldMatrix = matrix4f;
    }

    public void addChange(ChangeType changeType) {
        if (changeQueue == null) {
            changeQueue = new LinkedList<>();
        }
        changeQueue.add(changeType);
    }
}
