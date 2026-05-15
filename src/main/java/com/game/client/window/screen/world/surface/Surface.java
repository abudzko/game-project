package com.game.client.window.screen.world.surface;

import com.game.client.utils.log.LogUtil;
import com.game.client.window.engine.unit.GameUnit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Surface {
    private final Map<Long, GameUnit> gameUnitMap = new ConcurrentHashMap<>();
    private BVHNode bvhRoot;

    public void build() {
        var start = System.currentTimeMillis();
        var triangleBuilder = new TrianglesBuilder();
        var triangles = gameUnitMap.values().stream()
                .flatMap(gameUnit -> triangleBuilder.toTriangles(gameUnit).stream())
                .collect(Collectors.toList());

        LogUtil.logDebug("toTriangles: count " + triangles.size() + " " + (System.currentTimeMillis() - start) + "ms");
        start = System.currentTimeMillis();
        bvhRoot = new BVHNode(triangles);
        LogUtil.logDebug("new BVHNode: " + (System.currentTimeMillis() - start) + "ms");
    }

    public void addGameUnit(GameUnit gameUnit) {
        var unit = this.gameUnitMap.get(gameUnit.getSharedUnitState().getGameUnitId());
        if (unit == null) {
            gameUnitMap.put(gameUnit.getSharedUnitState().getGameUnitId(), gameUnit);
        }
    }

    public Intersection findIntersection(Ray ray) {
        var start = System.currentTimeMillis();
        if (bvhRoot == null) {
            return null;
        }
        var intersection = bvhRoot.findIntersection(ray);
        LogUtil.logDebug(false, "findIntersection: " + (System.currentTimeMillis() - start) + "ms");
        return intersection;
    }
}
