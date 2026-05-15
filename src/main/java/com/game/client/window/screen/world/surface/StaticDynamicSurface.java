package com.game.client.window.screen.world.surface;

import com.game.client.utils.log.LogUtil;
import com.game.client.window.engine.unit.GameUnit;

import java.util.Optional;

public class StaticDynamicSurface {
    public static final StaticDynamicSurface INSTANCE = create();
    private final Surface staticSurface;
    private final Surface dynamicSurface;

    private StaticDynamicSurface(Surface staticSurface, Surface dynamicSurface) {
        this.staticSurface = staticSurface;
        this.dynamicSurface = dynamicSurface;
    }

    private static StaticDynamicSurface create() {
        var staticSurface = new Surface();
        var dynamicSurface = new Surface();
        return new StaticDynamicSurface(staticSurface, dynamicSurface);
    }

    public void addDynamicGameUnit(GameUnit gameUnit) {
        dynamicSurface.addGameUnit(gameUnit);
    }

    public void addStaticGameUnit(GameUnit gameUnit) {
        staticSurface.addGameUnit(gameUnit);
    }

    public Intersection findIntersection(Ray ray) {
        return Optional.ofNullable(dynamicSurface.findIntersection(ray))
                .orElseGet(() -> staticSurface.findIntersection(ray));
    }

    public void buildStaticSurface() {
        var start = System.currentTimeMillis();
        staticSurface.build();
        LogUtil.logDebug(false, "buildStaticSurface: " + (System.currentTimeMillis() - start) + "ms");
    }

    public void buildDynamicSurface() {
        var start = System.currentTimeMillis();
        dynamicSurface.build();
        LogUtil.logDebug(false, "buildDynamicSurface: " + (System.currentTimeMillis() - start) + "ms");
    }
}
