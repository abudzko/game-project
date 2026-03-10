package com.game.client.window.screen.unit;

import com.game.client.utils.log.LogUtil;
import com.game.client.window.engine.unit.GameUnit;
import com.game.client.window.model.obj.ObjectModels;

public class ScreenUnitFactory {

    private static final ObjectModels objectModels = new ObjectModels();

    public static ScreenUnit createScreenUnit(GameUnit gameUnit) {
        var start = System.currentTimeMillis();
        var screenUnit = ScreenUnit.builder()
                .sharedUnitState(gameUnit.getSharedUnitState())
                .isSurface(gameUnit.isSurface())
                .useShading(gameUnit.isUseShading())
                .model(objectModels.getModel(gameUnit.getModelKey()))
                .build();
        LogUtil.logDebug(gameUnit.getModelKey() + (System.currentTimeMillis() - start) + "ms");
        return screenUnit;
    }
}
