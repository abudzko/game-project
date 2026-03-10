package com.game.client.window.engine.action;

import com.game.client.window.engine.unit.GameUnit;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SkydomeFollowAction implements GameUnitAction {

    protected static final int DIFF = 20;
    private final GameUnit skydome;
    private final GameUnit player;

    @Override
    public boolean act(GameUnit gameUnit) {
        var playerPosition = player.getSharedUnitState().getPosition();
        var skydomePosition = skydome.getSharedUnitState().getPosition();
        if (Math.abs(playerPosition.z - skydomePosition.z) > DIFF
                || Math.abs(playerPosition.x - skydomePosition.x) > DIFF
                || Math.abs(playerPosition.y - skydomePosition.y) > DIFF
        ) {
            skydomePosition.set(playerPosition);
            skydome.getSharedUnitState().updateWorldMatrix();
        }
        return true;
    }
}
