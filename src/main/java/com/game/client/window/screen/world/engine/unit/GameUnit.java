package com.game.client.window.screen.world.engine.unit;

import com.game.client.window.model.SharedUnitState;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;

@Builder
@Getter
@Setter
public class GameUnit {
    protected static final Random RANDOM = new Random();
    private SharedUnitState sharedUnitState;

    public void step() {
        var action = sharedUnitState.getGameUnitAction();
        if (action != null) {
            if (!action.act(this)) {
                sharedUnitState.setGameUnitAction(null);
            }
        }
        if (sharedUnitState.getGameUnitType() == GameUnitType.OTHER) {
            var random = RANDOM;
            float step = 0.01f;
            var dx = random.nextBoolean() ? step : -step;
            var dy = random.nextBoolean() ? step : -step;
            var dz = random.nextBoolean() ? step : -step;
            var currentPosition = sharedUnitState.getPosition();
            currentPosition.x += dx;
            currentPosition.y += dy;
            currentPosition.z += dz;

            sharedUnitState.updateWorldMatrix();
        }
    }
}
