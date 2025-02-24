package com.game;

import com.game.engine.GameEngine;
import com.game.engine.unit.GameUnitMediator;
import com.game.window.WindowContainer;

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
