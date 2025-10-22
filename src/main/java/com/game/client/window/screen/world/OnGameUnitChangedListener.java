package com.game.client.window.screen.world;

import com.game.client.window.screen.world.engine.unit.GameUnit;

public interface OnGameUnitChangedListener {
    void processChanges(GameUnit gameUnit);
}
