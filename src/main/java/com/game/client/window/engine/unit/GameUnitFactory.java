package com.game.client.window.engine.unit;

import com.game.client.window.lwjgl.program.Light;
import com.game.client.window.model.obj.ObjectModels;
import lombok.Getter;
import org.joml.Vector3f;

import java.util.concurrent.atomic.AtomicLong;

@Getter
public class GameUnitFactory {
    public static final GameUnitFactory INSTANCE = new GameUnitFactory();
    private static final ObjectModels objectModels = new ObjectModels();
    private static final AtomicLong idGenerator = new AtomicLong();

    private static SharedUnitState sharedUnitState() {
        var sharedUnitState = SharedUnitState.builder().gameUnitId(idGenerator.incrementAndGet()).build();
        updateWordMatrix(sharedUnitState);
        return sharedUnitState;
    }

    private static void updateWordMatrix(SharedUnitState sharedUnitState) {
        sharedUnitState.updateWorldMatrix(sharedUnitState.getPosition());
    }

    private static GameUnit.GameUnitBuilder createDefaultGameUnitBuilder(String modelKey) {
        return GameUnit.builder()
                .model(objectModels.getModel(modelKey));
    }

    public GameUnit createPlayer() {
        var sharedUnitState = sharedUnitState();
        sharedUnitState.updateWorldMatrix(new Vector3f(100f, .2f, -100f));
        sharedUnitState.setDynamic(true);
        return createDefaultGameUnitBuilder(GameUnitType.PLAYER)
                .sharedUnitState(sharedUnitState)
                .build();
    }

    public GameUnit createSun() {
        var sharedUnitState = sharedUnitState();
        sharedUnitState.updateWorldMatrix(new Vector3f(0.0f, 400.0f, 100.0f));
        var light = Light.builder()
                .lightColor(new Vector3f(1.0f, 1.0f, 1.0f))
                .lightPosition(sharedUnitState.getPosition())
                .build();
        sharedUnitState.setLight(light);
        return createDefaultGameUnitBuilder(GameUnitType.SUN)
                .sharedUnitState(sharedUnitState)
                .build();
    }

    public GameUnit createSkydome(float scale) {
        var sharedUnitState = sharedUnitState();
        sharedUnitState.setScale(scale);
        updateWordMatrix(sharedUnitState);
        return createDefaultGameUnitBuilder(GameUnitType.SKYDOME)
                .sharedUnitState(sharedUnitState)
                .isSurface(false)
                .useShading(false)
                .build();
    }

    public GameUnit createZone(String key) {
        var sharedUnitState = sharedUnitState();
        return createDefaultGameUnitBuilder(key)
                .sharedUnitState(sharedUnitState)
                .build();
    }

    public GameUnit createGameUnit(String modelKey) {
        var sharedUnitState = sharedUnitState();
        return createDefaultGameUnitBuilder(modelKey)
                .sharedUnitState(sharedUnitState)
                .isSurface(false)
                .useShading(true)
                .build();
    }
}
