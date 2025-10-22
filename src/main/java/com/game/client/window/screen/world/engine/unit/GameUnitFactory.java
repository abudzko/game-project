package com.game.client.window.screen.world.engine.unit;

import lombok.Getter;
import org.joml.Vector3f;

import java.util.concurrent.atomic.AtomicLong;

@Getter
public class GameUnitFactory {
    public static final GameUnitFactory INSTANCE = new GameUnitFactory();
    private static final AtomicLong idGenerator = new AtomicLong();

    public static GameUnit createUnit(Vector3f position) {
        return GameUnit.builder().id(idGenerator.incrementAndGet()).dynamic(true).position(position).build();
    }

    public GameUnit createPlayer() {
        return GameUnit.builder()
                .id(idGenerator.incrementAndGet())
                .position(new Vector3f(0, 0, 0))
                .dynamic(true)
                .build();
    }

    public GameUnit createSun() {
        return GameUnit.builder().id(idGenerator.incrementAndGet()).position(new Vector3f(0, 0, 0)).build();
    }

    public GameUnit createSkydome() {
        return GameUnit.builder().id(idGenerator.incrementAndGet()).position(new Vector3f(0, 0, 0)).build();
    }

    public GameUnit createGround() {
        return GameUnit.builder().id(idGenerator.incrementAndGet()).position(new Vector3f(0, 0, 0)).build();
    }
}
