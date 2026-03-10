package com.game.client.window.screen.world;

import com.game.client.utils.log.LogUtil;
import com.game.client.window.engine.GameEngine;
import com.game.client.window.engine.action.MoveAction;
import com.game.client.window.event.key.KeyEvent;
import com.game.client.window.event.listener.AbstractWindowEventListener;
import com.game.client.window.event.mouse.MouseButton;
import com.game.client.window.event.mouse.MouseButtonAction;
import com.game.client.window.event.mouse.MouseButtonEvent;
import com.game.client.window.screen.world.camera.Camera;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.game.client.utils.log.LogUtil.toStr;

/**
 * Handles players events in World Screen
 */
public class PlayerEventHandler extends AbstractWindowEventListener {

    private final Camera camera;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4,
            runnable -> {
                var t = Executors.defaultThreadFactory().newThread(runnable);
                t.setDaemon(true);
                return t;
            });
    private final GameEngine gameEngine;

    public PlayerEventHandler(Camera camera, GameEngine gameEngine) {
        this.camera = camera;
        this.gameEngine = gameEngine;
    }

    public static PlayerEventHandler create(Camera camera, GameEngine gameEngine) {
        return new PlayerEventHandler(camera, gameEngine);
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
                        break;
                    case KEY_S:
                        break;
                    case KEY_A:
                        break;
                    case KEY_D:
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
                            LogUtil.logDebug("Intersection: id=" + intersection.getGameUnitId() + " " + toStr(intersection.getPoint()));
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
}
