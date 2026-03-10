package com.game.client.window;

import com.game.client.utils.log.LogUtil;
import com.game.client.window.event.key.KeyEvent;
import com.game.client.window.event.listener.AbstractWindowEventListener;
import com.game.client.window.event.resize.ResizeWindowEvent;
import com.game.client.window.lwjgl.annotation.LwjglMainThread;
import com.game.client.window.lwjgl.event.LwjglEventManager;
import com.game.client.window.screen.world.Screen;
import com.game.client.window.screen.world.WorldScreen;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window extends AbstractWindowEventListener {
    private final WindowConfig windowConfig;
    private final long windowId;
    private List<Screen> screens = new ArrayList<>();
    private boolean windowSizeChanged;

    private Window() {
        this.windowConfig = new WindowConfig();
        this.windowId = initWindow();
        configureEventManager();
    }

    public static Window createWindow() {
        return new Window();
    }

    private long initWindow() {
        var monitorId = GLFW.glfwGetPrimaryMonitor();
        var videoMode = GLFW.glfwGetVideoMode(monitorId);
        monitorId = NULL;
        assert videoMode != null;

        var id = GLFW.glfwCreateWindow(
                windowConfig.getWidth(),
                windowConfig.getHeight(),
                windowConfig.getName(),
                monitorId,
                NULL
        );

        if (id == NULL) {
            GLFW.glfwTerminate();
            throw new IllegalStateException("Failed to create the GLFW window");
        }
        LogUtil.logInfo("Window id = " + id);
        return id;
    }

    private void configureEventManager() {
        lwjglEventManager = new LwjglEventManager(getWindowId());
        getLwjglEventManager().configureEventCallbacks();
        listenLwjglEvents(this);
    }

    public void start() throws InterruptedException {
        var countDownLatch = new CountDownLatch(1);
        var windowRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    init();
                    createScreens();
                    countDownLatch.countDown();
                    while (!shouldBeClosed()) {
                        render();
                    }
                } catch (RuntimeException e) {
                    LogUtil.logError(String.format("Error happened inside window [%s] thread", windowId), e);
                } finally {
                    destroy();
                }
            }
        };

        var windowThread = new Thread(windowRunnable);
        windowThread.start();
        // Wait while window will be initialized
        countDownLatch.await();
        show();
    }

    private void createScreens() {
        var worldScreen = WorldScreen.create();
        screens.add(worldScreen);
        addEventListener(worldScreen);
    }

    private void init() {
        GLFW.glfwMakeContextCurrent(windowId);
        GL.createCapabilities();
        GLFW.glfwSwapInterval(windowConfig.getSwapInterval());
    }

    public void render() {
        screens.forEach(Screen::render);
        GLFW.glfwSwapBuffers(getWindowId());
        if (windowSizeChanged) {
            GL30.glViewport(0, 0, windowConfig.getWidth(), windowConfig.getHeight());
            windowSizeChanged = false;
        }
    }

    public long getWindowId() {
        return windowId;
    }

    public void destroy() {
        GLFW.glfwDestroyWindow(windowId);
    }

    @LwjglMainThread
    public void show() {
        GLFW.glfwShowWindow(windowId);
    }

    public void hide() {
        GLFW.glfwHideWindow(windowId);
    }

    public boolean shouldBeClosed() {
        return glfwWindowShouldClose(windowId);
    }

    private void windowSizeChanged(ResizeWindowEvent resizeWindowEvent) {
        windowConfig.setWidth(resizeWindowEvent.getNewWidth());
        windowConfig.setHeight(resizeWindowEvent.getNewHeight());
        windowSizeChanged = true;
    }

    @Override
    public void event(ResizeWindowEvent event) {
        super.event(event);
        windowSizeChanged(event);
    }

    @Override
    public void event(KeyEvent keyEvent) {
        super.event(keyEvent);
        switch (keyEvent.getKeyActionType()) {
            case PRESSED:
                switch (keyEvent.getKey()) {
                    case KEY_ESCAPE:
                        GLFW.glfwSetWindowShouldClose(getWindowId(), true);
                        LogUtil.logDebug(String.format("Close window %s", getWindowId()));
                        break;
                    default:
                        break;
                }
                break;
            case REPEAT:
            case RELEASED:
            default:
                break;
        }
    }
}
