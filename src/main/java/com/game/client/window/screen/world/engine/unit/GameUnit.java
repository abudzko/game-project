package com.game.client.window.screen.world.engine.unit;

import com.game.client.window.model.SharedUnitState;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GameUnit {
    private SharedUnitState sharedUnitState;

    public void step() {
        var action = sharedUnitState.getGameUnitAction();
        if (action != null) {
            if (!action.act(this)) {
                sharedUnitState.setGameUnitAction(null);
            }
        }
    }
}
