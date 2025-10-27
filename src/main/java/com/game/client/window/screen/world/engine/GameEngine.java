package com.game.client.window.screen.world.engine;

import com.game.client.utils.log.LogUtil;
import com.game.client.window.screen.world.engine.action.MoveAction;
import com.game.client.window.screen.world.engine.unit.GameUnit;
import lombok.Getter;
import lombok.SneakyThrows;

public class GameEngine {
    private final long stepMs =50;
    @Getter
    private final GameWorld gameWorld;

    public GameEngine() {
        this.gameWorld = new GameWorld();
    }

    public void start() {
        var thread = new Thread(
                () -> {
                    try {
                        loop();
                    } catch (Exception e) {
                        LogUtil.logError(e.getMessage(), e);
                    }
                });
        thread.setDaemon(true);
        thread.start();
    }

    @SneakyThrows
    private void loop() {
        while (true) {
            var start = System.currentTimeMillis();
            gameWorld.getGameUnitMap().forEach((id, unit) -> {
                step(unit);
            });
            var end = System.currentTimeMillis();
            var diff = end - start;
            LogUtil.logDebug("engine loop " + diff + " ms");
            if (diff < stepMs) {
                Thread.sleep(stepMs - diff);
            } else {
                LogUtil.logDebug("Step taken " + diff + "ms");
            }
        }
    }

    private void step(GameUnit unit) {
        unit.step();
    }

    public void handleMoveAction(MoveAction moveAction) {
        var player = gameWorld.findById(gameWorld.getPlayer().getSharedUnitState().getGameUnitId());
        if (player != null) {
            player.getSharedUnitState().setGameUnitAction(moveAction);
        }
        LogUtil.logDebug(String.format("Move %s to %s", moveAction.getGameUnitId(), moveAction.getTargetPosition()));
    }
}
