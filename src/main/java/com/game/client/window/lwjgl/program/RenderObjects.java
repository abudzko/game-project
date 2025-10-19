package com.game.client.window.lwjgl.program;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Collection;

@Getter
@Setter
public class RenderObjects {
    private Collection<LwjglUnit> lwjglUnits;
    private Vector3f cameraPosition;
    private Matrix4f cameraViewMatrix;
    private Matrix4f projectionMatrix;
}
