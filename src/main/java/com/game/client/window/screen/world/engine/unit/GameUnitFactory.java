package com.game.client.window.screen.world.engine.unit;

import com.game.client.window.lwjgl.program.Light;
import com.game.client.window.model.SharedUnitState;
import lombok.Getter;
import org.joml.Vector3f;

import java.util.concurrent.atomic.AtomicLong;

@Getter
public class GameUnitFactory {
    public static final GameUnitFactory INSTANCE = new GameUnitFactory();
    private static final AtomicLong idGenerator = new AtomicLong();

    private static SharedUnitState sharedUnitState() {
        return SharedUnitState.builder().gameUnitId(idGenerator.incrementAndGet()).build();
    }

    public GameUnit createPlayer() {
        var sharedUnitState = sharedUnitState();
        sharedUnitState.setPosition(new Vector3f(0f, .2f, 0f));
        sharedUnitState.setDynamic(true);
        return GameUnit.builder()
                .sharedUnitState(sharedUnitState)
                .modelKey(GameUnitType.PLAYER)
                .build();
    }

    public GameUnit createSun() {
        var sharedUnitState = sharedUnitState();
        sharedUnitState.setPosition(new Vector3f(50.0f, 50.0f, 0.0f));
        var light = Light.builder()
                .lightColor(new Vector3f(1.0f, 1.0f, 1.0f))
                .lightPosition(sharedUnitState.getPosition())
                .build();
        sharedUnitState.setLight(light);
        return GameUnit.builder()
                .sharedUnitState(sharedUnitState)
                .modelKey(GameUnitType.SUN)
                .build();
    }

    public GameUnit createSkydome() {
        var sharedUnitState = sharedUnitState();
        return GameUnit.builder()
                .sharedUnitState(sharedUnitState)
                .modelKey(GameUnitType.SKYDOME)
                .isSurface(false)
                .useShading(false)
                .build();
    }

    public GameUnit createGround() {
        var sharedUnitState = sharedUnitState();
        return GameUnit.builder()
                .sharedUnitState(sharedUnitState)
                .modelKey(GameUnitType.GEN_GROUND)
                .build();
    }

    public GameUnit createZone(String key) {
        var sharedUnitState = sharedUnitState();
        return GameUnit.builder()
                .sharedUnitState(sharedUnitState)
                .modelKey(key)
                .build();
    }

    public GameUnit createGameUnit(String modelKey) {
        var sharedUnitState = sharedUnitState();
        return GameUnit.builder()
                .sharedUnitState(sharedUnitState)
                .modelKey(modelKey)
                .isSurface(false)
                .useShading(true)
                .build();
    }
}
