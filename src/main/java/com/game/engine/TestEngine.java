package com.game.engine;

import com.game.dao.GameUnitDao;
import com.game.window.event.key.KeyEvent;
import com.game.window.event.listener.WindowEventListener;
import com.game.window.event.mouse.MouseButton;
import com.game.window.event.mouse.MouseButtonAction;
import com.game.window.event.mouse.MouseButtonEvent;
import com.game.model.GraphicUnit;
import com.game.utils.log.LogUtil;
import com.game.window.Window;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * TODO screen??
 */
public class TestEngine implements Runnable, WindowEventListener {

    private static final Random RANDOM = new Random();
    private final GameUnitDao gameUnitDao = new GameUnitDao();
    private final Window window;
    private final float moveStep = 0.01f;
    private final Queue<GraphicUnit> tmpUnits = new ConcurrentLinkedQueue<>();
    private volatile boolean isRunning = false;
    private GraphicUnit selectedUnit;

    public TestEngine(Window window) {
        this.window = window;
    }

    private static float rotation(float value) {
        return value + 5 * RANDOM.nextFloat();
    }

    @Override
    public void run() {
        window.addRootEventListener(this);
        isRunning = true;
            var mainUnit = gameUnitDao.getMainUnit();
            selectedUnit = mainUnit;
            window.addGameUnit(mainUnit);
            animate(mainUnit);
            gameUnitDao.getUnits().forEach(window::addGameUnit);
            addSun();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LogUtil.logError(e.getMessage(), e);
        }
    }

    private void addSun() {
        var sun = gameUnitDao.createSunGameUnit();
        window.addGameUnit(sun);
        animateSun(sun);
    }

    private void animate(GraphicUnit graphicUnit) {
        Thread thread = new Thread(() -> {
            while (isRunning) {
                try {
                    graphicUnit.getRotation().y = rotation(graphicUnit.getRotation().y);
                    window.updateGameUnit(graphicUnit);
                    Thread.sleep(100);
                } catch (Exception e) {
                    LogUtil.logError("animate failed", e);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void animateSun(GraphicUnit sun) {
        Thread thread = new Thread(() -> {
            var direction = 1;
            var b = 7;
            var angleDegree = 0.0;
            var radius = 5f;
            while (isRunning) {
                try {
                    var x = sun.getPosition().x;
                    var z = sun.getPosition().z;
                    if ((x > b || x < -b) && (z > b || z < -b)) {
                        direction = -direction;
                    }
                    var deltaX = (float) (radius * Math.cos(Math.toRadians(angleDegree)));
                    var deltaZ = (float) (radius * Math.sin(Math.toRadians(angleDegree)));
                    sun.getPosition().x = deltaX;
                    sun.getPosition().z = deltaZ;
                    window.updateGameUnit(sun);
                    Thread.sleep(10);
                    radius += (moveStep * 0.5f * direction);
                    angleDegree += 0.5;
                } catch (Exception e) {
                    LogUtil.logError("animate failed", e);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void addRandomUnits() {
        var units = new ArrayList<GraphicUnit>();
        for (int i = 0; i < 100; i++) {
            var gameUnit = gameUnitDao.createGameUnit();
            units.add(gameUnit);
            window.addGameUnit(gameUnit);
        }
        Thread thread = new Thread(() -> {
            final var path = new float[]{0};
            final var xDirections = new int[units.size()];
            final var yDirections = new int[units.size()];
            final var zDirections = new int[units.size()];
            for (int i = 0; i < xDirections.length; i++) {
                xDirections[i] = (RANDOM.nextBoolean() ? 1 : -1);
                yDirections[i] = (RANDOM.nextBoolean() ? 1 : -1);
                zDirections[i] = (RANDOM.nextBoolean() ? 1 : -1);
            }
            while (isRunning) {
                for (int i = 0; i < units.size(); i++) {
                    var unit = units.get(i);
                    var xd = xDirections[i];
                    var yd = yDirections[i];
                    var zd = zDirections[i];
                    unit.getPosition().x = resolvePosition(unit.getPosition().x, xd);
                    unit.getPosition().y = resolveYPosition(unit.getPosition().y, yd);
                    unit.getPosition().z = resolvePosition(unit.getPosition().z, zd);
                    unit.getRotation().x = rotation(unit.getRotation().x);
                    window.updateGameUnit(unit);
                }
                path[0] += moveStep;
                if (path[0] >= .2) {
                    path[0] = 0;
                    for (int i = 0; i < xDirections.length; i++) {
                        xDirections[i] = (RANDOM.nextBoolean() ? 1 : -1);
                        yDirections[i] = (RANDOM.nextBoolean() ? 1 : -1);
                        zDirections[i] = (RANDOM.nextBoolean() ? 1 : -1);
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private float resolvePosition(float pos, int direction) {
        return pos >= 10 || pos <= -10 ? 0 : pos + moveStep * direction;
    }

    private float resolveYPosition(float pos, int direction) {
        return pos >= 5 || pos <= 0 ? 1 : pos + moveStep * direction;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    @Override
    public void event(KeyEvent keyEvent) {
        handleKeyEventForSelectedGameUnit(keyEvent);
    }

    private void handleKeyEventForSelectedGameUnit(KeyEvent keyEvent) {
        switch (keyEvent.getKeyActionType()) {
            case PRESSED:
                switch (keyEvent.getKey()) {
                    case KEY_W:
                        moveZ(-moveStep);
                        break;
                    case KEY_S:
                        moveZ(moveStep);
                        break;
                    case KEY_A:
                        moveX(-moveStep);
                        break;
                    case KEY_D:
                        moveX(moveStep);
                        break;
                    case KEY_ESCAPE:
                        GLFW.glfwSetWindowShouldClose(window.getWindowId(), true);
                        LogUtil.log(String.format("Close window %s", window.getWindowId()));
                        break;
                    default:
                        LogUtil.log(String.format("Pressed %s", keyEvent.getKeyDeprecated()));
                        break;
                }
                break;
            case REPEAT:
                switch (keyEvent.getKey()) {
                    case KEY_W:
                        moveZ(-moveStep);
                        break;
                    case KEY_S:
                        moveZ(moveStep);
                        break;
                    case KEY_A:
                        moveX(-moveStep);
                        break;
                    case KEY_D:
                        moveX(moveStep);
                        break;
                    default:
                        break;
                }
                break;
            case RELEASED:
                break;
            default:
                break;
        }
    }

    private void moveX(float stepX) {
        var localSelectedUnit = selectedUnit;
        if (localSelectedUnit != null) {
            System.out.println("X: " + localSelectedUnit.getPosition().x);
            localSelectedUnit.getPosition().x += stepX;
            window.updateGameUnit(localSelectedUnit);
        }
    }

    private void moveZ(float stepZ) {
        var localSelectedUnit = selectedUnit;
        if (localSelectedUnit != null) {
            System.out.println("Z: " + localSelectedUnit.getPosition().z);
            localSelectedUnit.getPosition().z += stepZ;
            window.updateGameUnit(localSelectedUnit);
        }
    }

    @Override
    public void event(MouseButtonEvent mouseButtonEvent) {
        if (MouseButtonAction.PRESSED.equals(mouseButtonEvent.getAction())
                && MouseButton.LEFT.equals(mouseButtonEvent.getButton())) {
            Vector3f worldCoordinates = window.getWorldCoordinates(mouseButtonEvent);
            var tmpGameUnit = GameUnitDao.createCudeGameUnit(worldCoordinates);
            window.addGameUnit(tmpGameUnit);
            tmpUnits.add(tmpGameUnit);
            if (tmpUnits.size() > 10) {
                var last = tmpUnits.remove();
                window.deleteGameUnit(last);
            }
        }
    }
}
