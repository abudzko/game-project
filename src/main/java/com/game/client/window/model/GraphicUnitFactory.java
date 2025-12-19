package com.game.client.window.model;

import com.game.client.utils.log.LogUtil;
import com.game.client.window.model.obj.ObjectModels;
import com.game.client.window.screen.world.engine.unit.GameUnit;

public class GraphicUnitFactory {

    private static final ObjectModels objectModels = new ObjectModels();

    public static GraphicUnit createGraphicUnit(GameUnit gameUnit) {
        var start = System.currentTimeMillis();
        var graphicUnit = GraphicUnit.builder()
                .sharedUnitState(gameUnit.getSharedUnitState())
                .isSurface(gameUnit.isSurface())
                .useShading(gameUnit.isUseShading())
                .model(objectModels.getModel(gameUnit.getModelKey()))
                .build();
        LogUtil.logDebug(gameUnit.getModelKey() + (System.currentTimeMillis() - start) + "ms");
        return graphicUnit;
    }
}
