package com.game.lwjgl.event;

import com.game.window.event.cursor.CursorPositionEvent;
import com.game.window.event.key.KeyActionType;
import com.game.window.event.key.KeyEvent;
import com.game.window.event.key.Keys;
import com.game.window.event.mouse.MouseButton;
import com.game.window.event.mouse.MouseButtonAction;
import com.game.window.event.mouse.MouseButtonEvent;
import com.game.window.event.resize.ResizeWindowEvent;
import com.game.window.event.scroll.ScrollEvent;
import com.game.utils.log.LogUtil;
import org.lwjgl.glfw.GLFW;

public class EventFactory {

    public static KeyEvent buildKeyEvent(int key, int scanCode, int action, int mods) {
        var k = key(key);
        var actionType = keyActionType(action);
        KeyEvent keyEvent = new KeyEvent(k, actionType, key, scanCode, action, mods);
        return keyEvent;
    }

    private static KeyActionType keyActionType(int action) {
        switch (action) {
            case 0:
                return KeyActionType.RELEASED;
            case 1:
                return KeyActionType.PRESSED;
            case 2:
                return KeyActionType.REPEAT;
            default:
                throw new RuntimeException(String.format("Unknown key action: %s", action));
        }
    }

    private static Keys key(int key) {
        Keys k;
        switch (key) {
            case GLFW.GLFW_KEY_UP:
                k = Keys.KEY_UP;
                break;
            case GLFW.GLFW_KEY_DOWN:
                k = Keys.KEY_DOWN;
                break;
            case GLFW.GLFW_KEY_RIGHT:
                k = Keys.KEY_RIGHT;
                break;
            case GLFW.GLFW_KEY_LEFT:
                k = Keys.KEY_LEFT;
                break;
            case GLFW.GLFW_KEY_W:
                k = Keys.KEY_W;
                break;
            case GLFW.GLFW_KEY_S:
                k = Keys.KEY_S;
                break;
            case GLFW.GLFW_KEY_A:
                k = Keys.KEY_A;
                break;
            case GLFW.GLFW_KEY_D:
                k = Keys.KEY_D;
                break;
            case GLFW.GLFW_KEY_ESCAPE:
                k = Keys.KEY_ESCAPE;
                break;
            default:
                k = Keys.KEY_UNSUPPORTED;
                LogUtil.log(String.format("Pressed %s", key));
                break;
        }
        return k;
    }

    public static MouseButtonEvent buildMouseEvent(int button, int action, int mods, double x, double y) {
        MouseButtonEvent mouseButtonEvent = new MouseButtonEvent(mouseButton(button), mouseButtonAction(action), mods, x, y);
        return mouseButtonEvent;
    }

    private static MouseButtonAction mouseButtonAction(int action) {
        switch (action) {
            case 0:
                return MouseButtonAction.RELEASED;
            case 1:
                return MouseButtonAction.PRESSED;
            default:
                throw new RuntimeException(String.format("Unknown mouse button action: %s", action));
        }
    }

    private static MouseButton mouseButton(int button) {
        switch (button) {
            case 0:
                return MouseButton.LEFT;
            case 1:
                return MouseButton.RIGHT;
            case 2:
                return MouseButton.WHEEL;
            default:
                throw new RuntimeException(String.format("Unknown mouse button: %s", button));
        }
    }

    public static ScrollEvent buildScrollEvent(double offsetX, double offsetY) {
        ScrollEvent scrollEvent = new ScrollEvent(offsetX, offsetY);
        return scrollEvent;
    }

    public static ResizeWindowEvent buildWindowResizeEvent(int newWidth, int newHeight) {
        ResizeWindowEvent resizeWindowEvent = new ResizeWindowEvent(newWidth, newHeight);
        return resizeWindowEvent;
    }

    public static CursorPositionEvent buildCursorPositionEvent(double x, double y) {
        return new CursorPositionEvent(x, y);
    }
}
