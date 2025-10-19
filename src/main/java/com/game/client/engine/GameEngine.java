package com.game.client.engine;

import com.game.client.engine.unit.GameUnitDao;
import com.game.client.utils.log.LogUtil;
import lombok.SneakyThrows;

public class GameEngine {
    public static final GameEngine INSTANCE = new GameEngine();
    private static final GameUnitDao GAME_UNIT_DAO = GameUnitDao.INSTANCE;

    private final long stepMs = 100;

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
            GAME_UNIT_DAO.getPlayer().step();
            GAME_UNIT_DAO.getUnits().forEach((id, unit) -> unit.step());
            var end = System.currentTimeMillis();
            var diff = end - start;
            if (diff < stepMs) {
                Thread.sleep(stepMs - diff);
            } else {
                LogUtil.logDebug("Step taken " + diff + "ms");
            }
        }
    }
}
