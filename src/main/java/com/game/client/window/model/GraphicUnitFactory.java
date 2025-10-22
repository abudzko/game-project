package com.game.client.window.model;

import com.game.client.utils.log.LogUtil;
import com.game.client.window.lwjgl.program.Light;
import com.game.client.window.model.obj.ObjectModels;
import com.game.client.window.screen.world.engine.unit.GameUnit;
import org.joml.Vector3f;

public class GraphicUnitFactory {

    private static final ObjectModels objectModels = new ObjectModels();

    public static GraphicUnit createPlayerGraphicUnit(GameUnit gameUnit) {
        return createGraphicUnit(gameUnit, "units.small_sphere");
    }

    public static GraphicUnit createGroundUnit(GameUnit gameUnit) {
        var start = System.currentTimeMillis();
        var groundUnit = GraphicUnit.builder()
                .gameUnitId(gameUnit.getId())
                .dynamic(gameUnit.isDynamic())
                .position(gameUnit.getPosition())
                .rotation(new Vector3f(0f, 0f, 0f))
                .scale(1f)
                .model(objectModels.getModel("units.ground"))
                .build();
        LogUtil.logDebug("createGroundUnit: " + (System.currentTimeMillis() - start) + "ms");
        return groundUnit;
    }

    public static GraphicUnit createSkydome(GameUnit gameUnit) {
        return GraphicUnit.builder()
                .gameUnitId(gameUnit.getId())
                .dynamic(gameUnit.isDynamic())
                .isSurface(false)
                .position(gameUnit.getPosition())
                .rotation(new Vector3f(0f, 0f, 0f))
                .scale(1f)
                .useShading(false)
                .model(objectModels.getModel("units.skydome"))
                .build();
    }

    public static GraphicUnit createSunUnit(GameUnit gameUnit) {
        var light = Light.builder()
                .lightPosition(new Vector3f(10.0f, 100.0f, 0.0f))
                .lightColor(new Vector3f(1.0f, 1.0f, 1.0f))
                .build();
        return GraphicUnit.builder()
                .gameUnitId(gameUnit.getId())
                .dynamic(gameUnit.isDynamic())
                .isSurface(false)
                .position(light.getLightPosition())
                .rotation(new Vector3f(0f, 0f, 0f))
                .scale(1f)
                .light(light)
                .model(objectModels.getModel("units.sun"))
                .build();
    }

    private static GraphicUnit createGraphicUnit(GameUnit gameUnit, String modelKey) {
        return GraphicUnit.builder()
                .gameUnitId(gameUnit.getId())
                .dynamic(gameUnit.isDynamic())
                .position(gameUnit.getPosition())
                .rotation(new Vector3f(0f, 0f, 0f))
                .scale(1f)
                .model(objectModels.getModel(modelKey))
                .build();
    }

    public static GraphicUnit createGraphicUnit(GameUnit gameUnit) {
        var modelKey = "units.small_sphere";
        return createGraphicUnit(gameUnit, modelKey);
    }

    public static GraphicUnit createGraphicUnit2(GameUnit gameUnit) {
        var modelKey = "units.small_sphere_yellow";
        return createGraphicUnit(gameUnit, modelKey);
    }
}
