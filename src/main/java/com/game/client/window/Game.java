package com.game.client.window;

import com.game.client.engine.GameEngine;
import com.game.client.engine.unit.GameUnitMediator;

public class Game {

    public Game() {
    }

    public void start() {
        var windowContainer = new WindowContainer();
        try {
            windowContainer.startWindows();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
        windowContainer.getWindows().forEach((id, window) -> {
            var gameUnitMediator = new GameUnitMediator(window, GameEngine.INSTANCE);
            gameUnitMediator.init();
        });
    }
}
