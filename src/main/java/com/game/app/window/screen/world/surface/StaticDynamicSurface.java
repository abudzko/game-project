package com.game.app.window.screen.world.surface;

import com.game.app.window.model.GraphicUnit;

import java.util.Optional;

public class StaticDynamicSurface {
    private final Surface staticSurface;
    private final Surface dynamicSurface;

    public StaticDynamicSurface(Surface staticSurface, Surface dynamicSurface) {
        this.staticSurface = staticSurface;
        this.dynamicSurface = dynamicSurface;
    }

    public static StaticDynamicSurface create() {
        var staticSurface = new Surface();
        var dynamicSurface = new Surface();
        return new StaticDynamicSurface(staticSurface, dynamicSurface);
    }

    public void addDynamicGraphicUnit(GraphicUnit graphicUnit) {
        dynamicSurface.addGraphicUnit(graphicUnit);
    }

    public void addStaticGraphicUnit(GraphicUnit graphicUnit) {
        staticSurface.addGraphicUnit(graphicUnit);
    }

    public Intersection findIntersection(Ray ray) {
        return Optional.ofNullable(dynamicSurface.findIntersection(ray))
                .orElseGet(() -> staticSurface.findIntersection(ray));
    }

    public void buildStaticSurface() {
        var start = System.currentTimeMillis();
        staticSurface.build();
//        LogUtil.logDebug("buildStaticSurface: " + (System.currentTimeMillis() - start) + "ms");
    }

    public void buildDynamicSurface() {
        var start = System.currentTimeMillis();
        dynamicSurface.build();
//        LogUtil.logDebug("buildDynamicSurface: " + (System.currentTimeMillis() - start) + "ms");
    }
}
