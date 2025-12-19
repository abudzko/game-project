package com.game.client.window.screen.world.engine;

import com.game.client.window.screen.world.engine.unit.GameUnit;
import com.game.client.window.screen.world.engine.unit.GameUnitFactory;
import com.game.client.window.screen.world.engine.unit.GameUnitType;
import lombok.Getter;
import org.joml.Vector3f;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class GameWorld {
    private final Map<Long, GameUnit> gameUnitMap = new ConcurrentHashMap<>();
    private final GameUnit player;
    private final GameUnit ground;
    private final GameUnit skydome;
    private final GameUnit sun;

    public GameWorld() {
        sun = GameUnitFactory.INSTANCE.createSun();
        ground = GameUnitFactory.INSTANCE.createGround();
        skydome = GameUnitFactory.INSTANCE.createSkydome();
        player = GameUnitFactory.INSTANCE.createPlayer();
        var gameUnits = Stream.of(sun, ground, skydome, player)
                .collect(Collectors.toMap(
                        gameUnit -> gameUnit.getSharedUnitState().getGameUnitId(),
                        Function.identity()
                ));
        gameUnitMap.putAll(gameUnits);
        addTrees();
    }

    private void addTrees() {
        var random = new Random();
        for (int i = 0; i < 1700; i++) {
            String type = random.nextBoolean() ? GameUnitType.TREE_THIJA : GameUnitType.TREE_SPRUCE;
            var gameUnit = GameUnitFactory.INSTANCE.createGameUnit(type);
            var x = random.nextFloat() * (random.nextBoolean() ? 10f : -10f);
            var y = 0;
            var z = random.nextFloat() * (random.nextBoolean() ? 10f : -10f);
            var sharedUnitState = gameUnit.getSharedUnitState();
            sharedUnitState.setPosition(new Vector3f(x, y, z));
            var scale = sharedUnitState.getScale() * (random.nextFloat() * .8f + .2f);
            sharedUnitState.setScale(scale);
            gameUnitMap.put(sharedUnitState.getGameUnitId(), gameUnit);
        }

    }

    public GameUnit findById(long id) {
        return getGameUnitMap().get(id);
    }
}
