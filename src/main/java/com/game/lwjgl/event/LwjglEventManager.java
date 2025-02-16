package com.game.lwjgl.event;

import com.game.lwjgl.annotation.LwjglMainThread;
import com.game.utils.BufferUtils;
import com.game.utils.log.LogUtil;
import com.game.window.event.cursor.CursorPositionEvent;
import com.game.window.event.key.KeyEvent;
import com.game.window.event.listener.WindowEventListener;
import com.game.window.event.mouse.MouseButtonEvent;
import com.game.window.event.resize.ResizeWindowEvent;
import com.game.window.event.scroll.ScrollEvent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LwjglEventManager {
    private final Long windowId;
    private final List<WindowEventListener> eventListeners = new CopyOnWriteArrayList<>();

    public LwjglEventManager(Long windowId) {
        this.windowId = windowId;
    }

    @LwjglMainThread
    public void configureEventCallbacks() {
        GLFW.glfwSetKeyCallback(windowId, new GLFWKeyCallback() {
            @Override
            public void invoke(long windowId, int key, int scanCode, int action, int mods) {
                try {
                    var keyEvent = EventFactory.buildKeyEvent(key, scanCode, action, mods);
                    processKeyEvent(keyEvent);
                } catch (Exception e) {
                    LogUtil.logError(e.getMessage(), e);
                }
            }
        });

        var cursorX = BufferUtils.createByteBuffer(new byte[8]);
        var cursorY = BufferUtils.createByteBuffer(new byte[8]);
        GLFW.glfwSetMouseButtonCallback(windowId, new GLFWMouseButtonCallback() {

            @Override
            public void invoke(long windowId, int button, int action, int mods) {
                try {
                    GLFW.glfwGetCursorPos(windowId, cursorX.asDoubleBuffer(), cursorY.asDoubleBuffer());
                    var x = cursorX.getDouble();
                    var y = cursorY.getDouble();

                    var mouseEvent = EventFactory.buildMouseEvent(button, action, mods, x, y);
                    processMouseEvent(mouseEvent);
                } catch (Exception e) {
                    LogUtil.logError(e.getMessage(), e);
                } finally {
                    cursorX.flip();
                    cursorY.flip();
                }
            }
        });

        GLFW.glfwSetScrollCallback(windowId, new GLFWScrollCallback() {
            @Override
            public void invoke(long windowId, double offsetX, double offsetY) {
                try {
                    var scrollEvent = EventFactory.buildScrollEvent(offsetX, offsetY);
                    processScrollEvent(scrollEvent);
                } catch (Exception e) {
                    LogUtil.logError(e.getMessage(), e);
                }
            }
        });

        GLFW.glfwSetCursorPosCallback(windowId, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long windowId, double x, double y) {
                try {
                    var cursorPositionEvent = EventFactory.buildCursorPositionEvent(x, y);
                    processCursorPositionEvent(cursorPositionEvent);
                } catch (Exception e) {
                    LogUtil.logError(e.getMessage(), e);
                }
            }
        });

        GLFW.glfwSetCursorEnterCallback(windowId, new GLFWCursorEnterCallback() {
            @Override
            public void invoke(long window, boolean entered) {
            }
        });

        GLFW.glfwSetWindowSizeCallback(windowId, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long windowId, int newWidth, int newHeight) {
                try {
                    var windowResizeEvent = EventFactory.buildWindowResizeEvent(newWidth, newHeight);
                    processWindowResizeEvent(windowResizeEvent);
                } catch (Exception e) {
                    LogUtil.logError(e.getMessage(), e);
                }
            }
        });
    }

    public void addEventListener(WindowEventListener eventListener) {
        getEventListeners().add(eventListener);
    }

    public void processPendingEvents() {
        GLFW.glfwPollEvents();
    }

    private void processCursorPositionEvent(CursorPositionEvent cursorPositionEvent) {
        getEventListeners().forEach(listener -> listener.event(cursorPositionEvent));
    }

    private void processKeyEvent(KeyEvent keyEvent) {
        getEventListeners().forEach(listener -> listener.event(keyEvent));
    }

    private void processScrollEvent(ScrollEvent scrollEvent) {
        getEventListeners().forEach(listener -> listener.event(scrollEvent));
    }

    private void processWindowResizeEvent(ResizeWindowEvent resizeWindowEvent) {
        getEventListeners().forEach(listener -> listener.event(resizeWindowEvent));
    }

    private void processMouseEvent(MouseButtonEvent mouseButtonEvent) {
        getEventListeners().forEach(listener -> listener.event(mouseButtonEvent));
    }

    private List<WindowEventListener> getEventListeners() {
        return eventListeners;
    }
}
