package com.game.client.window.model;

import com.game.client.utils.log.LogUtil;
import com.game.client.window.model.obj.ObjectModels;
import com.game.client.window.screen.world.engine.unit.GameUnit;

public class GraphicUnitFactory {

    private static final ObjectModels objectModels = new ObjectModels();

    public static GraphicUnit createPlayerGraphicUnit(GameUnit gameUnit) {
        return createGraphicUnit(gameUnit, "units.small_sphere");
    }

    public static GraphicUnit createGroundGraphicUnit(GameUnit gameUnit) {
        var start = System.currentTimeMillis();
        var groundUnit = GraphicUnit.builder()
                .sharedUnitState(gameUnit.getSharedUnitState())
                .model(objectModels.getModel("units.ground"))
                .build();
        LogUtil.logDebug("createGroundUnit: " + (System.currentTimeMillis() - start) + "ms");
        return groundUnit;
    }

    public static GraphicUnit createSkydomeGraphicUnit(GameUnit gameUnit) {
        return GraphicUnit.builder()
                .sharedUnitState(gameUnit.getSharedUnitState())
                .isSurface(false)
                .useShading(false)
                .model(objectModels.getModel("units.skydome"))
                .build();
    }

    public static GraphicUnit createSunGraphicUnit(GameUnit gameUnit) {
        return GraphicUnit.builder()
                .sharedUnitState(gameUnit.getSharedUnitState())
                .isSurface(false)
                .model(objectModels.getModel("units.sun"))
                .build();
    }

    private static GraphicUnit createGraphicUnit(GameUnit gameUnit, String modelKey) {
        return GraphicUnit.builder()
                .sharedUnitState(gameUnit.getSharedUnitState())
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
