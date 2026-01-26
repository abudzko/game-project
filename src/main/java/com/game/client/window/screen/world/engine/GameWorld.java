package com.game.client.window.screen.world.engine;

import com.game.client.window.model.obj.ObjectModels;
import com.game.client.window.model.obj.zone.ZoneConfig;
import com.game.client.window.screen.world.engine.action.SkydomeFollowAction;
import com.game.client.window.screen.world.engine.unit.GameUnit;
import com.game.client.window.screen.world.engine.unit.GameUnitFactory;
import com.game.client.window.screen.world.engine.unit.GameUnitType;
import lombok.Getter;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
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
    private final GameUnit skydome;
    private final GameUnit sun;
    private ZoneConfig zoneConfig;

    public GameWorld() {
        zoneConfig = ObjectModels.getZoneConfig();
        var zonesGameUnit = createZones().stream().collect(Collectors.toMap(
                gameUnit -> gameUnit.getSharedUnitState().getGameUnitId(),
                Function.identity()
        ));
        sun = GameUnitFactory.INSTANCE.createSun();
        player = GameUnitFactory.INSTANCE.createPlayer();
        skydome = createSkydome();
        var gameUnits = Stream.of(sun, skydome, player)
                .collect(Collectors.toMap(
                        gameUnit -> gameUnit.getSharedUnitState().getGameUnitId(),
                        Function.identity()
                ));
        gameUnitMap.putAll(zonesGameUnit);
        gameUnitMap.putAll(gameUnits);
        addTrees();
    }

    private GameUnit createSkydome() {
        var sd = GameUnitFactory.INSTANCE.createSkydome();
        var scale = 3 * zoneConfig.zoneSize * getZoneConfig().xzScale;
        sd.getSharedUnitState().setScale(scale);
        sd.getSharedUnitState().setGameUnitAction(SkydomeFollowAction.builder()
                .skydome(sd)
                .player(player)
                .build());
        return sd;
    }

    private List<GameUnit> createZones() {
        var zones = new ArrayList<GameUnit>();
        for (int z = 0; z < zoneConfig.mapRowCount; z++) {
            for (int x = 0; x < zoneConfig.mapColumnCount; x++) {
                String key = GameUnitType.unit("zone." + z + "_" + x);
                var gameUnit = GameUnitFactory.INSTANCE.createZone(key);
                zones.add(gameUnit);
            }
        }
        return zones;
    }

    private void addTrees() {
        var random = new Random();
        float scale = zoneConfig.mapColumnCount * zoneConfig.zoneSize * getZoneConfig().xzScale;
        for (int i = 0; i < 200; i++) {
            String type = random.nextBoolean() ? GameUnitType.TREE_THIJA : GameUnitType.TREE_SPRUCE;
            var gameUnit = GameUnitFactory.INSTANCE.createGameUnit(type);
            var x = random.nextFloat() * scale;
            var y = 0;
            var z = -random.nextFloat() * scale;
            var sharedUnitState = gameUnit.getSharedUnitState();
            sharedUnitState.setPosition(new Vector3f(x, y, z));
            var treeScale = sharedUnitState.getScale() * (random.nextFloat() * .8f + .2f);
            sharedUnitState.setScale(treeScale);
            gameUnitMap.put(sharedUnitState.getGameUnitId(), gameUnit);
        }
    }
}
