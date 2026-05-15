package com.game.client.window.engine.unit;

import com.game.client.window.model.obj.Model;
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
    /**
     * Units which can be selected
     */
    @Builder.Default
    private boolean isSurface = true;
    /**
     * Some units should not have shadows, for ex. the sky
     */
    @Builder.Default
    private boolean useShading = true;
    private Model model;

    public void step() {
        var action = sharedUnitState.getGameUnitAction();
        if (action != null) {
            if (!action.act(this)) {
                sharedUnitState.setGameUnitAction(null);
            }
        }
    }
}
