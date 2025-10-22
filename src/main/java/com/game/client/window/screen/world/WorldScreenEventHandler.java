package com.game.client.window.screen.world;

import com.game.client.utils.log.LogUtil;
import com.game.client.window.event.key.KeyEvent;
import com.game.client.window.event.listener.AbstractWindowEventListener;
import com.game.client.window.event.mouse.MouseButton;
import com.game.client.window.event.mouse.MouseButtonAction;
import com.game.client.window.event.mouse.MouseButtonEvent;
import com.game.client.window.model.GraphicUnit;
import com.game.client.window.screen.world.camera.Camera;
import com.game.client.window.screen.world.engine.GameEngine;
import com.game.client.window.screen.world.engine.action.MoveAction;
import org.apache.commons.math3.util.Precision;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorldScreenEventHandler extends AbstractWindowEventListener {

    private final float moveStep = 0.01f;
    private final Camera camera;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4,
            runnable -> {
                var t = Executors.defaultThreadFactory().newThread(runnable);
                t.setDaemon(true);
                return t;
            });
    private final GameEngine gameEngine;

    public WorldScreenEventHandler(Camera camera, GameEngine gameEngine) {
        this.camera = camera;
        this.gameEngine = gameEngine;
    }

    public static WorldScreenEventHandler create(Camera camera, GameEngine gameEngine) {
        return new WorldScreenEventHandler(camera, gameEngine);
    }

    @Override
    public void event(KeyEvent keyEvent) {
        Runnable runnable = () -> {
            try {
                handleKeyEventForPlayer(keyEvent);
            } catch (Exception e) {
                LogUtil.logError(e.getMessage(), e);
            }
        };
        executorService.submit(runnable);
    }

    private void handleKeyEventForPlayer(KeyEvent keyEvent) {
        switch (keyEvent.getKeyActionType()) {
            case PRESSED:
            case REPEAT:
                switch (keyEvent.getKey()) {
                    case KEY_W:
//                        moveZ(-moveStep);
                        break;
                    case KEY_S:
//                        moveZ(moveStep);
                        break;
                    case KEY_A:
//                        moveX(-moveStep);
                        break;
                    case KEY_D:
//                        moveX(moveStep);
                        break;
                    default:
                        break;
                }
                break;
            case RELEASED:
            default:
                break;
        }
    }

    private void moveX(float stepX) {
//        var localSelectedUnit = selectedUnit;
//        if (localSelectedUnit != null) {
////            System.out.println("X: " + localSelectedUnit.getPosition().x);
//            localSelectedUnit.getPosition().x += stepX;
//            localSelectedUnit.updateWorldMatrix();
//            surface.buildDynamicSurface();
//        }
    }

    private void moveZ(float stepZ) {
//        var localSelectedUnit = selectedUnit;
//        if (localSelectedUnit != null) {
//            System.out.println("Z: " + localSelectedUnit.getPosition().z);
//            localSelectedUnit.getPosition().z += stepZ;
//            localSelectedUnit.updateWorldMatrix();
//            surface.buildDynamicSurface();
//        }
    }

    @Override
    public void event(MouseButtonEvent mouseButtonEvent) {
        if (MouseButtonAction.PRESSED.equals(mouseButtonEvent.getAction())
                && MouseButton.LEFT.equals(mouseButtonEvent.getButton())) {
            Runnable runnable = () -> {
                try {
                    moveAction(mouseButtonEvent);
                } catch (Exception e) {
                    LogUtil.logError(e.getMessage(), e);
                }
            };
            executorService.submit(runnable);
        }
    }

    private void moveAction(MouseButtonEvent mouseButtonEvent) {
        Optional.ofNullable(camera.findIntersection(mouseButtonEvent.getX(), mouseButtonEvent.getY()))
                .ifPresentOrElse(
                        intersection -> {
                            LogUtil.logDebug("Intersection intersection: " + toStr(intersection.getPoint()));
                            var moveAction = MoveAction.builder()
                                    .targetPosition(intersection.getPoint())
                                    .gameUnitId(intersection.getGameUnitId())
                                    .camera(camera)
                                    .build();
                            gameEngine.handleMoveAction(moveAction);
                        },
                        () -> LogUtil.logDebug("No intersection")
                );
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
//        window.addGraphicUnit(tmpGraphicUnit);
//        tmpUnits.add(tmpGraphicUnit);

//        surface.addDynamicGraphicUnit(tmpGraphicUnit);
//        surface.buildDynamicSurface();
//        if (tmpUnits.size() > 10) {
//            var last = tmpUnits.remove();
//            window.deleteGameUnit(last);
//        }
    }
}
