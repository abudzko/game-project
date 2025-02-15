package com.game.window;

import com.game.lwjgl.annotation.LwjglMainThread;
import com.game.lwjgl.event.LwjglEventManager;
import com.game.model.GraphicUnit;
import com.game.utils.log.LogUtil;
import com.game.window.camera.world.surface.Ray;
import com.game.window.event.listener.AbstractWindowEventListener;
import com.game.window.event.mouse.MouseButtonEvent;
import com.game.window.event.resize.ResizeWindowEvent;
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

    public Window(WindowState windowState) {
        this.windowState = windowState;
        this.windowId = initWindow();
        configureEventManager();
    }

    public static Window createWindow() {
        var windowState = new WindowState();
        return new Window(windowState);
    }

    private long initWindow() {
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
        return id;
    }

    private void configureEventManager() {
        eventManager = new LwjglEventManager(getWindowId());
        getEventManager().configureEventCallbacks();
        addRootEventListener(this);
    }

    public void start() throws InterruptedException {
        var countDownLatch = new CountDownLatch(1);
        var windowRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    init();
                    createWorldScreen();
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

    private void createWorldScreen() {
        var worldScreenState = new WorldScreenState();
        worldScreenState.setWidth(windowState.getWidth());
        worldScreenState.setHeight(windowState.getHeight());
        worldScreen = new WorldScreen(worldScreenState);
        addEventChildListener(worldScreen);
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

    // TODO ??
    public Ray getRay(MouseButtonEvent mouseButtonEvent) {
        return worldScreen.getRay(mouseButtonEvent);
    }

    @Override
    public void event(ResizeWindowEvent event) {
        super.event(event);
        windowSizeChanged(event);
    }

    public void addGameUnit(GraphicUnit graphicUnit) {
        worldScreen.addGameUnit(graphicUnit);
    }

    public void updateGameUnit(GraphicUnit graphicUnit) {
        worldScreen.updateGameUnit(graphicUnit);
    }

    public void deleteGameUnit(GraphicUnit graphicUnit) {
        worldScreen.deleteGameUnit(graphicUnit);
    }
}
