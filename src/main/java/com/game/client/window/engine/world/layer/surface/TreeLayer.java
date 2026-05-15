package com.game.client.window.engine.world.layer.surface;

import com.game.client.utils.log.LogUtil;
import com.game.client.window.engine.unit.GameUnit;
import com.game.client.window.engine.unit.GameUnitFactory;
import com.game.client.window.engine.unit.GameUnitType;
import com.game.client.window.screen.world.surface.Intersection;
import com.game.client.window.screen.world.surface.Ray;
import com.game.client.window.screen.world.surface.StaticDynamicSurface;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class TreeLayer {

    private static final Random RANDOM = new Random();
    private final float scale;
    private final StaticDynamicSurface surface;

    public TreeLayer(float scale, StaticDynamicSurface surface) {
        this.scale = scale;
        this.surface = surface;
    }

    private GameUnit generateGameUnit(float scale) {
        var position = generatePosition(scale);
        String type = RANDOM.nextBoolean() ? GameUnitType.TREE_THIJA : GameUnitType.TREE_SPRUCE;
        var gameUnit = GameUnitFactory.INSTANCE.createGameUnit(type);
        var sharedUnitState = gameUnit.getSharedUnitState();
        sharedUnitState.updateWorldMatrix(position);
        var treeScale = sharedUnitState.getScale() * (RANDOM.nextFloat() * .8f + .2f);
        sharedUnitState.setScale(treeScale);
        return gameUnit;
    }

    private Vector3f generatePosition(float scale) {
        var x = RANDOM.nextFloat() * scale;
        var y = 10;
        var z = -RANDOM.nextFloat() * scale;
        var start = new Vector3f(x, y, z);
        var direction = new Vector3f(0, -1, 0);
        var ray = new Ray(start, direction);
        return Optional.ofNullable(surface.findIntersection(ray)).map(Intersection::getPoint).orElseThrow();
    }

    public Map<Long, GameUnit> appendTrees() {
        var start = System.currentTimeMillis();
        var gameUnitMap = new HashMap<Long, GameUnit>();
        for (int i = 0; i < 400; i++) {
            var gameUnit = generateGameUnit(scale);
            gameUnitMap.put(gameUnit.getSharedUnitState().getGameUnitId(), gameUnit);
        }
        var end = System.currentTimeMillis();
        var diff = end - start;
        LogUtil.logDebug(getClass().getSimpleName() + ": " + diff + " ms");
        return gameUnitMap;
    }
}
