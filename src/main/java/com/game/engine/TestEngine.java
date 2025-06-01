package com.game.engine;

import com.game.app.window.Window;
import com.game.app.window.event.key.KeyEvent;
import com.game.app.window.event.listener.WindowEventListener;
import com.game.app.window.event.mouse.MouseButtonEvent;
import com.game.app.window.model.GraphicUnit;
import com.game.app.window.model.GraphicUnitFactory;
import com.game.app.window.screen.world.surface.StaticDynamicSurface;
import com.game.utils.log.LogUtil;
import org.apache.commons.math3.util.Precision;
import org.joml.Vector3f;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TODO screen??
 */
public class TestEngine implements Runnable, WindowEventListener {

    private static final Random RANDOM = new Random();
    private final GraphicUnitFactory graphicUnitFactory = GraphicUnitFactory.INSTANCE;
    private final StaticDynamicSurface surface = StaticDynamicSurface.create();
    private final Window window;
    private final float moveStep = 0.01f;
//    private final Queue<GraphicUnit> tmpUnits = new ConcurrentLinkedQueue<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(4,
            runnable -> {
                var t = Executors.defaultThreadFactory().newThread(runnable);
                t.setDaemon(true);
                return t;
            });
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
        window.addEventChildListener(this);
        isRunning = true;
//        var mainUnit = graphicUnitDao.getPlayerMainUnit();
//        var groundUnit = graphicUnitDao.getGroundUnit();
//        selectedUnit = mainUnit;
//        window.addGraphicUnit(mainUnit);
//
//        surface.addDynamicGraphicUnit(mainUnit);
//        animate(mainUnit);
//        window.addGraphicUnit(groundUnit);
//        graphicUnitDao.getUnits().forEach(window::addGraphicUnit);
//
//        addSun();
//        surface.addStaticGraphicUnit(groundUnit);
//
//        surface.buildStaticSurface();
//        surface.buildDynamicSurface();
    }

    private void addSun() {
//        var sun = graphicUnitDao.createSunUnit();
//        window.addGraphicUnit(sun);
//        animateSun(sun);
    }

    private void animate(GraphicUnit graphicUnit) {
        Thread thread = new Thread(() -> {
            while (isRunning) {
                try {
                    graphicUnit.getRotation().y = rotation(graphicUnit.getRotation().y);
                    graphicUnit.updateWorldMatrix();
//                    surface.buildDynamicSurface();
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
            var direction = 1f;
            var b = 7;
            var angleDegree = 0.0;
            var radius = RANDOM.nextInt(5) + 1f;
            while (isRunning) {
                try {
                    var x = sun.getPosition().x;
                    var z = sun.getPosition().z;
                    if ((x > b || x < -b) && (z > b || z < -b)) {
                        direction = -1f * direction;
                    }
                    var deltaX = (float) (radius * Math.cos(Math.toRadians(angleDegree)));
                    var deltaZ = (float) (radius * Math.sin(Math.toRadians(angleDegree)));
                    sun.getPosition().x = deltaX;
                    sun.getPosition().z = deltaZ;
                    sun.updateWorldMatrix();
                    Thread.sleep(RANDOM.nextInt(5) + 5);
                    radius += (direction * RANDOM.nextFloat() * moveStep);
                    angleDegree += 0.5;
                } catch (Exception e) {
                    LogUtil.logError("animate failed", e);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void event(KeyEvent keyEvent) {
        Runnable runnable = () -> {
            try {
                handleKeyEventForSelectedGameUnit(keyEvent);
            } catch (Exception e) {
                LogUtil.logError(e.getMessage(), e);
            }
        };
        executorService.submit(runnable);
    }

    private void handleKeyEventForSelectedGameUnit(KeyEvent keyEvent) {
//        switch (keyEvent.getKeyActionType()) {
//            case PRESSED:
//                switch (keyEvent.getKey()) {
//                    case KEY_W:
//                        moveZ(-moveStep);
//                        break;
//                    case KEY_S:
//                        moveZ(moveStep);
//                        break;
//                    case KEY_A:
//                        moveX(-moveStep);
//                        break;
//                    case KEY_D:
//                        moveX(moveStep);
//                        break;
//                    case KEY_ESCAPE:
//                        GLFW.glfwSetWindowShouldClose(window.getWindowId(), true);
//                        LogUtil.logDebug(String.format("Close window %s", window.getWindowId()));
//                        break;
//                    default:
////                        LogUtil.logDebug(String.format("Pressed %s", keyEvent.getKeyDeprecated()));
//                        break;
//                }
//                break;
//            case REPEAT:
//                switch (keyEvent.getKey()) {
//                    case KEY_W:
//                        moveZ(-moveStep);
//                        break;
//                    case KEY_S:
//                        moveZ(moveStep);
//                        break;
//                    case KEY_A:
//                        moveX(-moveStep);
//                        break;
//                    case KEY_D:
//                        moveX(moveStep);
//                        break;
//                    default:
//                        break;
//                }
//                break;
//            case RELEASED:
//                break;
//            default:
//                break;
//        }
    }

    private void moveX(float stepX) {
        var localSelectedUnit = selectedUnit;
        if (localSelectedUnit != null) {
//            System.out.println("X: " + localSelectedUnit.getPosition().x);
            localSelectedUnit.getPosition().x += stepX;
            localSelectedUnit.updateWorldMatrix();
            surface.buildDynamicSurface();
        }
    }

    private void moveZ(float stepZ) {
        var localSelectedUnit = selectedUnit;
        if (localSelectedUnit != null) {
            System.out.println("Z: " + localSelectedUnit.getPosition().z);
            localSelectedUnit.getPosition().z += stepZ;
            localSelectedUnit.updateWorldMatrix();
            surface.buildDynamicSurface();
        }
    }

    @Override
    public void event(MouseButtonEvent mouseButtonEvent) {
//        if (MouseButtonAction.PRESSED.equals(mouseButtonEvent.getAction())
//                && MouseButton.LEFT.equals(mouseButtonEvent.getButton())) {
//            Runnable runnable = () -> {
//                try {
//                    runTask(mouseButtonEvent);
//                } catch (Exception e) {
//                    LogUtil.logError(e.getMessage(), e);
//                }
//            };
//            executorService.submit(runnable);
//        }
    }

    private void runTask(MouseButtonEvent mouseButtonEvent) {
//        var ray = window.getRay(mouseButtonEvent);
//        Optional.ofNullable(surface.findIntersection(ray))
//                .ifPresentOrElse(
//                        point -> {
//                            LogUtil.logDebug("Intersection point: " + toStr(point));
////                            var tmpGraphicUnit = GraphicUnitDao.createSmallCircleGraphicUnit(point);
////                            addUnit(tmpGraphicUnit);
//                        },
//                        () -> LogUtil.logDebug("No intersection")
//                );
    }

    private String toStr(Vector3f point) {
        int scale = 3;
        return String.format("%s %s %s",
                Precision.round(point.x, scale),
                Precision.round(point.y, scale),
                Precision.round(point.z, scale)
        );
    }

    private void addUnit(GraphicUnit tmpGraphicUnit) {
        window.addGraphicUnit(tmpGraphicUnit);
//        tmpUnits.add(tmpGraphicUnit);

        surface.addDynamicGraphicUnit(tmpGraphicUnit);
        surface.buildDynamicSurface();
//        if (tmpUnits.size() > 10) {
//            var last = tmpUnits.remove();
//            window.deleteGameUnit(last);
//        }
    }
}
