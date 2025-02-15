package com.game.window.camera.world.surface;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Surface {
    private final List<Triangle> triangles = new ArrayList<>();
    private BVHNode bvhRoot;

    public void build() {
        bvhRoot = new BVHNode(triangles);
    }

    public void addTriangles(List<Triangle> triangles) {
        this.triangles.addAll(triangles);
    }

    public Vector3f findIntersection(Ray ray) {
        return bvhRoot.findIntersection(ray);
    }
}
