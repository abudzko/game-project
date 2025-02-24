package com.game.engine.unit;

import com.game.dao.GraphicUnitDao;
import com.game.engine.GameEngine;
import com.game.window.Window;

import java.util.Map;

public class GameUnitMediator {

    private static final GameUnitDao GAME_UNIT_DAO = GameUnitDao.INSTANCE;
    private static final GraphicUnitDao GRAPHIC_UNIT_DAO = GraphicUnitDao.INSTANCE;
    private final Window window;
    private final GameEngine gameEngine;

    public GameUnitMediator(Window window, GameEngine gameEngine) {
        this.window = window;
        this.gameEngine = gameEngine;
    }

    public void init() {
        addWorld();
        addPlayer(GAME_UNIT_DAO.getPlayer());
        addUnits(GAME_UNIT_DAO.getUnits());
        gameEngine.start();
    }

    private void addWorld() {
        window.addGraphicUnit(GRAPHIC_UNIT_DAO.createSunUnit(GAME_UNIT_DAO.getSun()));
        window.addGraphicUnit(GRAPHIC_UNIT_DAO.createGroundUnit(GAME_UNIT_DAO.getGround()));
    }

    private void addUnits(Map<Long, GameUnit> units) {
        units.forEach((id, unit) -> window.addGraphicUnit(GRAPHIC_UNIT_DAO.createGraphicUnit(unit)));
    }

    private void addPlayer(GameUnit player) {
        window.addGraphicUnit(GRAPHIC_UNIT_DAO.createPlayerGraphicUnit(player));
    }
}
