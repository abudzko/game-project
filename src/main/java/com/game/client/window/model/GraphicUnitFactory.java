package com.game.client.window.model;

import com.game.client.engine.unit.GameUnit;
import com.game.client.utils.log.LogUtil;
import com.game.client.window.lwjgl.program.Light;
import com.game.client.window.model.obj.ObjectModels;
import org.joml.Vector3f;

public class GraphicUnitFactory {

    public static final GraphicUnitFactory INSTANCE = new GraphicUnitFactory();
    private final ObjectModels objectModels = new ObjectModels();

    public GraphicUnit createPlayerGraphicUnit(GameUnit gameUnit) {
        return createGraphicUnit(gameUnit, "units.small_sphere");
    }

    public GraphicUnit createGraphicUnit(GameUnit gameUnit) {
        var modelKey = "units.small_sphere";
        return createGraphicUnit(gameUnit, modelKey);
    }

    public GraphicUnit createGraphicUnit2(GameUnit gameUnit) {
        var modelKey = "units.small_sphere_yellow";
        return createGraphicUnit(gameUnit, modelKey);
    }

    public GraphicUnit createGroundUnit(GameUnit gameUnit) {
        var start = System.currentTimeMillis();
        var groundUnit = GraphicUnit.builder()
                .id(gameUnit.getId())
                .dynamic(gameUnit.isDynamic())
                .position(gameUnit.getPosition())
                .rotation(new Vector3f(0f, 0f, 0f))
                .scale(1f)
                .model(objectModels.getModel("units.ground"))
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
                .model(objectModels.getModel("units.skydome"))
                .build();
    }

    public GraphicUnit createSunUnit(GameUnit gameUnit) {
        var light = Light.builder()
                .lightPosition(new Vector3f(0.0f, 100.0f, 0.0f))
                .lightColor(new Vector3f(1.0f, 1.0f, 1.0f))
                .build();
        return GraphicUnit.builder()
                .id(gameUnit.getId())
                .dynamic(gameUnit.isDynamic())
                .isSurface(false)
                .position(light.getLightPosition())
                .rotation(new Vector3f(0f, 0f, 0f))
                .scale(1f)
                .light(light)
                .model(objectModels.getModel("units.sun"))
                .build();
    }

    private GraphicUnit createGraphicUnit(GameUnit gameUnit, String modelKey) {
        return GraphicUnit.builder()
                .id(gameUnit.getId())
                .dynamic(gameUnit.isDynamic())
                .position(gameUnit.getPosition())
                .rotation(new Vector3f(0f, 0f, 0f))
                .scale(1f)
                .model(objectModels.getModel(modelKey))
                .build();
    }
}
