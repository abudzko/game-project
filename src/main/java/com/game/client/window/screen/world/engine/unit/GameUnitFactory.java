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
        sharedUnitState.setGameUnitType(GameUnitType.PLAYER);
        sharedUnitState.setDynamic(true);
        sharedUnitState.setScale(0.1f);
        return GameUnit.builder()
                .sharedUnitState(sharedUnitState)
                .build();
    }

    public GameUnit createSun() {
        var sharedUnitState = sharedUnitState();
        sharedUnitState.setGameUnitType(GameUnitType.SUN);
        sharedUnitState.setPosition(new Vector3f(10.0f, 100.0f, 0.0f));
        var light = Light.builder()
                .lightColor(new Vector3f(1.0f, 1.0f, 1.0f))
                .lightPosition(sharedUnitState.getPosition())
                .build();
        sharedUnitState.setLight(light);
        return GameUnit.builder().sharedUnitState(sharedUnitState).build();
    }

    public GameUnit createSkydome() {
        var sharedUnitState = sharedUnitState();
        sharedUnitState.setGameUnitType(GameUnitType.SKYDOME);
        return GameUnit.builder().sharedUnitState(sharedUnitState).build();
    }

    public GameUnit createGround() {
        var sharedUnitState = sharedUnitState();
        sharedUnitState.setScale(1);
        sharedUnitState.setGameUnitType(GameUnitType.GROUND);
        return GameUnit.builder().sharedUnitState(sharedUnitState).build();
    }

    public GameUnit createGameUnit() {
        var sharedUnitState = sharedUnitState();
        sharedUnitState.setScale(0.05f);
        sharedUnitState.setDynamic(true);
        return GameUnit.builder()
                .sharedUnitState(sharedUnitState)
                .build();
    }
}
