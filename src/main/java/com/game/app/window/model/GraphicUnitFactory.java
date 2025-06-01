package com.game.app.window.model;

import com.game.engine.unit.GameUnit;
import com.game.utils.log.LogUtil;
import org.joml.Vector3f;

public class GraphicUnitFactory {

    public static final GraphicUnitFactory INSTANCE = new GraphicUnitFactory();
    private final ModelFactory modelFactory = new ModelFactory();

    public GraphicUnit createGraphicUnit(GameUnit gameUnit) {
        return GraphicUnit.builder()
                .id(gameUnit.getId())
                .dynamic(gameUnit.isDynamic())
                .position(gameUnit.getPosition())
                .rotation(new Vector3f(0f, 0f, 0f))
                .scale(1f)
                .model(modelFactory.createModel("units.small_sphere"))
                .build();
    }

    public GraphicUnit createGroundUnit(GameUnit gameUnit) {
        var start = System.currentTimeMillis();
        var groundUnit = GraphicUnit.builder()
                .id(gameUnit.getId())
                .dynamic(gameUnit.isDynamic())
                .position(gameUnit.getPosition())
                .rotation(new Vector3f(0f, 0f, 0f))
                .scale(1f)
                .model(modelFactory.createModel("units.ground"))
                .build();
        LogUtil.logDebug("createGroundUnit: " + (System.currentTimeMillis() - start) + "ms");
        return groundUnit;
    }

    public GraphicUnit createSkydome(GameUnit gameUnit) {
        return GraphicUnit.builder()
                .id(gameUnit.getId())
                .dynamic(gameUnit.isDynamic())
                .isSurface(false)
                .position(gameUnit.getPosition())
                .rotation(new Vector3f(0f, 0f, 0f))
                .scale(1f)
                .useShading(false)
                .model(modelFactory.createModel("units.skydome"))
                .build();
    }

    public GraphicUnit createSunUnit(GameUnit gameUnit) {
        var light = new Light();
        return GraphicUnit.builder()
                .id(gameUnit.getId())
                .dynamic(gameUnit.isDynamic())
                .isSurface(false)
                .position(light.getLightPosition())
                .rotation(new Vector3f(0f, 0f, 0f))
                .scale(1f)
                .light(light)
                .model(modelFactory.createModel("units.sun"))
                .build();
    }


    public GraphicUnit createPlayerGraphicUnit(GameUnit gameUnit) {
        return GraphicUnit.builder()
                .id(gameUnit.getId())
                .dynamic(gameUnit.isDynamic())
                .position(gameUnit.getPosition())
                .rotation(new Vector3f(0f, 0f, 0f))
                .scale(1f)
                .model(modelFactory.createModel("units.small_sphere"))
                .build();
    }
}
