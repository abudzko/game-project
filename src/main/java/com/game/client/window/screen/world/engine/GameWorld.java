package com.game.client.window.screen.world.engine;

import com.game.client.window.screen.world.engine.unit.GameUnit;
import com.game.client.window.screen.world.engine.unit.GameUnitFactory;
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
        addUnits();
    }

    private void addUnits() {
        var random = new Random();
        for (int i = 0; i < 40000; i++) {
            var gameUnit = GameUnitFactory.INSTANCE.createGameUnit();
            var x = random.nextFloat() * (random.nextBoolean() ? 10f : -10f);
            var y = random.nextFloat();
            var z = random.nextFloat() * (random.nextBoolean() ? 10f : -10f);
            gameUnit.getSharedUnitState().setPosition(new Vector3f(x, y, z));
            gameUnitMap.put(gameUnit.getSharedUnitState().getGameUnitId(), gameUnit);
        }
    }

    public GameUnit findById(long id) {
        return getGameUnitMap().get(id);
    }
}
