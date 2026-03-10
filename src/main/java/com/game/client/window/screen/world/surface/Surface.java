package com.game.client.window.screen.world.surface;

import com.game.client.utils.log.LogUtil;
import com.game.client.window.screen.unit.ScreenUnit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Surface {
    private final Map<Long, ScreenUnit> screenUnitMap = new ConcurrentHashMap<>();
    private BVHNode bvhRoot;

    public void build() {
        var start = System.currentTimeMillis();
        var triangleBuilder = new TrianglesBuilder();
        var triangles = screenUnitMap.values().stream()
                .flatMap(screenUnit -> triangleBuilder.toTriangles(screenUnit).stream())
                .collect(Collectors.toList());

        LogUtil.logDebug("toTriangles: count " + triangles.size() + " " + (System.currentTimeMillis() - start) + "ms");
        start = System.currentTimeMillis();
        bvhRoot = new BVHNode(triangles);
        LogUtil.logDebug("new BVHNode: " + (System.currentTimeMillis() - start) + "ms");
    }

    public void addScreenUnit(ScreenUnit screenUnit) {
        var unit = this.screenUnitMap.get(screenUnit.getSharedUnitState().getGameUnitId());
        if (unit == null) {
            screenUnitMap.put(screenUnit.getSharedUnitState().getGameUnitId(), screenUnit);
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
