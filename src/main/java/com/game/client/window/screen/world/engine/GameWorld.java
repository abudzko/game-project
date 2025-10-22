package com.game.client.window.screen.world.engine;

import com.game.client.window.screen.world.engine.unit.GameUnit;
import com.game.client.window.screen.world.engine.unit.GameUnitFactory;
import lombok.Getter;

import java.util.Map;
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
        var gameUnits = Stream.of(sun, ground, skydome, player).collect(Collectors.toMap(GameUnit::getId, Function.identity()));
        gameUnitMap.putAll(gameUnits);
    }

    public GameUnit findById(long id) {
        return getGameUnitMap().get(id);
    }
}
