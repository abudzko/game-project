package com.game.client.engine.unit;

import com.game.client.engine.GameEngine;
import com.game.client.window.Window;
import com.game.client.window.model.GraphicUnitFactory;

import java.util.Map;

public class GameUnitMediator {

    private static final GameUnitDao GAME_UNIT_DAO = GameUnitDao.INSTANCE;
    private static final GraphicUnitFactory GRAPHIC_UNIT_FACTORY = GraphicUnitFactory.INSTANCE;
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
        window.addGraphicUnit(GRAPHIC_UNIT_FACTORY.createSunUnit(GAME_UNIT_DAO.getSun()));
        window.addGraphicUnit(GRAPHIC_UNIT_FACTORY.createGroundUnit(GAME_UNIT_DAO.getGround()));
        window.addGraphicUnit(GRAPHIC_UNIT_FACTORY.createSkydome(GAME_UNIT_DAO.getSkydome()));
    }

    private void addUnits(Map<Long, GameUnit> units) {
        units.forEach((id, unit) -> window.addGraphicUnit(GRAPHIC_UNIT_FACTORY.createGraphicUnit(unit)));
    }

    private void addPlayer(GameUnit player) {
        window.addGraphicUnit(GRAPHIC_UNIT_FACTORY.createPlayerGraphicUnit(player));
    }
}
