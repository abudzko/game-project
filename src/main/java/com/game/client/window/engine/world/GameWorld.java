package com.game.client.window.engine.world;

import com.game.client.window.engine.action.SkydomeFollowAction;
import com.game.client.window.engine.unit.GameUnit;
import com.game.client.window.engine.unit.GameUnitFactory;
import com.game.client.window.engine.unit.GameUnitType;
import com.game.client.window.engine.world.layer.surface.TreeLayer;
import com.game.client.window.model.obj.ObjectModels;
import com.game.client.window.model.obj.zone.ZoneConfig;
import com.game.client.window.screen.world.surface.StaticDynamicSurface;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class GameWorld {
    private final StaticDynamicSurface surface = StaticDynamicSurface.INSTANCE;
    private final ConcurrentHashMap<Long, GameUnit> gameUnitMap = new ConcurrentHashMap<>();
    private final com.game.client.window.engine.unit.GameUnit player;
    private final com.game.client.window.engine.unit.GameUnit skydome;
    private final com.game.client.window.engine.unit.GameUnit sun;
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
        add(zonesGameUnit);
        add(gameUnits);
        addTrees();
    }

    private com.game.client.window.engine.unit.GameUnit createSkydome() {
        var scale = 3 * zoneConfig.zoneSize * getZoneConfig().xzScale;
        var sd = GameUnitFactory.INSTANCE.createSkydome(scale);
        sd.getSharedUnitState().setGameUnitAction(SkydomeFollowAction.builder()
                .skydome(sd)
                .player(player)
                .build());
        return sd;
    }

    private List<com.game.client.window.engine.unit.GameUnit> createZones() {
        var zones = new ArrayList<com.game.client.window.engine.unit.GameUnit>();
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
        float scale = zoneConfig.mapColumnCount * zoneConfig.zoneSize * getZoneConfig().xzScale;
        add(new TreeLayer(scale, surface).appendTrees());
    }

    private void add(Map<Long, GameUnit> gameUnitMap) {
        this.gameUnitMap.putAll(gameUnitMap);
        gameUnitMap.forEach((id, gameUnit) -> {
            addGameUnitToSurfaces(gameUnit);
        });
    }

    private void addGameUnitToSurfaces(GameUnit gameUnit) {
        if (gameUnit.isSurface()) {
            if (gameUnit.getSharedUnitState().isDynamic()) {
                surface.addDynamicGameUnit(gameUnit);
            } else {
                surface.addStaticGameUnit(gameUnit);
                surface.buildStaticSurface();
            }
        }
    }
}
