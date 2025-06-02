package com.game.app.window.lwjgl.program;

import com.game.app.window.model.LwjglUnit;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@Getter
@Setter
public class RenderObjects {
    private Iterable<LwjglUnit> lwjglUnits;
    private Vector3f cameraPosition;
    private Matrix4f cameraViewMatrix;
    private Matrix4f projectionMatrix;
}
