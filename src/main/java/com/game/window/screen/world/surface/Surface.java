package com.game.window.screen.world.surface;

import com.game.model.GraphicUnit;
import com.game.utils.log.LogUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Surface {
    private final Map<Long, GraphicUnit> graphicUnitMap = new ConcurrentHashMap<>();
    private BVHNode bvhRoot;

    public void build() {
        var start = System.currentTimeMillis();
        var triangleBuilder = new TrianglesBuilder();
        var triangles = graphicUnitMap.values().stream()
                .flatMap(graphicUnit -> triangleBuilder.toTriangles(graphicUnit).stream())
                .collect(Collectors.toList());

        LogUtil.logDebug("toTriangles: " + (System.currentTimeMillis() - start) + "ms");
        start = System.currentTimeMillis();
        bvhRoot = new BVHNode(triangles);
        LogUtil.logDebug("new BVHNode: " + (System.currentTimeMillis() - start) + "ms");
    }

    public void addGraphicUnit(GraphicUnit graphicUnit) {
        var unit = this.graphicUnitMap.get(graphicUnit.getId());
        if (unit == null) {
            graphicUnitMap.put(graphicUnit.getId(), graphicUnit);
        }
    }

    public Intersection findIntersection(Ray ray) {
        var start = System.currentTimeMillis();
        if (bvhRoot == null) {
            return null;
        }
        var intersection = bvhRoot.findIntersection(ray);
//        LogUtil.logDebug("findIntersection: " + (System.currentTimeMillis() - start) + "ms");
        return intersection;
    }
}
