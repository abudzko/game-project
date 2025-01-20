package com.game.window;

import com.game.event.window.listener.AbstractWindowEventListener;
import com.game.event.window.mouse.MouseButtonEvent;
import com.game.event.window.resize.ResizeWindowEvent;
import com.game.lwjgl.annotation.LwjglMainThread;
import com.game.lwjgl.event.WindowEvents;
import com.game.model.GameUnit;
import com.game.utils.log.LogUtil;
import com.game.window.screen.world.WorldScreen;
import com.game.window.screen.world.WorldScreenState;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;

import java.util.concurrent.CountDownLatch;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window extends AbstractWindowEventListener {
    private final WindowState windowState;
    private final long windowId;
    private WorldScreen worldScreen;
    private boolean windowSizeChanged;

    public Window() {
        this.windowState = new WindowState();
        this.windowId = createWindow();
    }

    private long createWindow() {
        var monitorId = GLFW.glfwGetPrimaryMonitor();
        var videoMode = GLFW.glfwGetVideoMode(monitorId);
        monitorId = NULL;
        assert videoMode != null;

        var id = GLFW.glfwCreateWindow(
                windowState.getWidth(),
                windowState.getHeight(),
                windowState.getName(),
                monitorId,
                NULL
        );

        if (id == NULL) {
            GLFW.glfwTerminate();
            throw new IllegalStateException("Failed to create the GLFW window");
        }
        LogUtil.log("Window id = " + id);

        windowEvents = new WindowEvents(id);
        windowEvents.configureEventCallbacks();
        registerWindowEventListener(this);
        return id;
    }

    public void start() throws InterruptedException {
        var countDownLaunch = new CountDownLatch(1);
        var windowRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    init();
                    createWorldScreen();
                    countDownLaunch.countDown();
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
        countDownLaunch.await();
        show();
    }

    private void createWorldScreen() {
        var worldScreenState = new WorldScreenState();
        worldScreenState.setWidth(windowState.getWidth());
        worldScreenState.setHeight(windowState.getHeight());
        worldScreen = new WorldScreen(worldScreenState);
        subscribeOnWindowEvents(worldScreen);
    }

    private void init() {
        GLFW.glfwMakeContextCurrent(windowId);
        GL.createCapabilities();

        GLFW.glfwSwapInterval(windowState.getSwapInterval());
    }

    public void render() {
        worldScreen.render();
        GLFW.glfwSwapBuffers(getWindowId());
        if (windowSizeChanged) {
            GL30.glViewport(0, 0, windowState.getWidth(), windowState.getHeight());
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
        windowState.setWidth(resizeWindowEvent.getNewWidth());
        windowState.setHeight(resizeWindowEvent.getNewHeight());
        windowSizeChanged = true;
    }

    // TODO ??
    public Vector3f getWorldCoordinates(MouseButtonEvent mouseButtonEvent) {
        return worldScreen.getWorldCoordinates(mouseButtonEvent);
    }
    @Override
    public void event(ResizeWindowEvent event) {
        super.event(event);
        windowSizeChanged(event);
    }

    public void addGameUnit(GameUnit gameUnit) {
        worldScreen.addGameUnit(gameUnit);
    }

    public void updateGameUnit(GameUnit gameUnit) {
        worldScreen.updateGameUnit(gameUnit);
    }

    public void deleteGameUnit(GameUnit gameUnit) {
        worldScreen.deleteGameUnit(gameUnit);
    }
}
