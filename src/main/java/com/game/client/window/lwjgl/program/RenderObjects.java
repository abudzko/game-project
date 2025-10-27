package com.game.client.window.lwjgl.program;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RenderObjects {
    private Collection<LwjglUnit> lwjglUnits;
    private Map<Integer, List<LwjglUnit>> vaoIdLwjglUnitMap;
    private Vector3f cameraPosition;
    private Matrix4f cameraViewMatrix;
    private Matrix4f projectionMatrix;
}
