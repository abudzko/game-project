package com.game.client.window.screen.world;

import com.game.client.utils.log.LogUtil;
import com.game.client.window.event.key.KeyEvent;
import com.game.client.window.event.listener.AbstractWindowEventListener;
import com.game.client.window.event.mouse.MouseButton;
import com.game.client.window.event.mouse.MouseButtonAction;
import com.game.client.window.event.mouse.MouseButtonEvent;
import com.game.client.window.model.GraphicUnit;
import org.apache.commons.math3.util.Precision;
import org.joml.Vector3f;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Deprecated
public class WorldScreenEventHandler extends AbstractWindowEventListener {

    private final float moveStep = 0.01f;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4,
            runnable -> {
                var t = Executors.defaultThreadFactory().newThread(runnable);
                t.setDaemon(true);
                return t;
            });

    public static WorldScreenEventHandler create() {
        return new WorldScreenEventHandler();
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
                    runTask(mouseButtonEvent);
                } catch (Exception e) {
                    LogUtil.logError(e.getMessage(), e);
                }
            };
            executorService.submit(runnable);
        }
    }

    private void runTask(MouseButtonEvent mouseButtonEvent) {
//        var ray = window.getRay(mouseButtonEvent);
//        Optional.ofNullable(surface.findIntersection(ray))
//                .ifPresentOrElse(
//                        point -> {
//                            LogUtil.logDebug("Intersection point: " + toStr(point));
//                            var tmpGraphicUnit = GraphicUnitDao.createSmallCircleGraphicUnit(point);
//                            addUnit(tmpGraphicUnit);
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
