package com.game.engine.unit;

import lombok.Getter;
import org.joml.Vector3f;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Getter
public class GameUnitDao {
    private static final AtomicLong idGenerator = new AtomicLong();
    public static final GameUnitDao INSTANCE = new GameUnitDao();
    private final GameUnit player;
    private final GameUnit ground;
    private final GameUnit sun;
    private final Map<Long, GameUnit> units = new ConcurrentHashMap<>();

    private GameUnitDao() {
        ground = createGround();
        sun = createSun();
        player = createPlayer();
    }

    public static GameUnit createUnit(Vector3f position) {
        return GameUnit.builder().id(idGenerator.incrementAndGet()).dynamic(true).position(position).build();
    }

    private GameUnit createPlayer() {
        return GameUnit.builder()
                .id(idGenerator.incrementAndGet())
                .position(new Vector3f(0, 0, 0))
                .dynamic(true)
                .build();
    }

    private GameUnit createSun() {
        return GameUnit.builder().id(idGenerator.incrementAndGet()).position(new Vector3f(0, 0, 0)).build();
    }

    private GameUnit createGround() {
        return GameUnit.builder().id(idGenerator.incrementAndGet()).position(new Vector3f(0, 0, 0)).build();
    }
}
