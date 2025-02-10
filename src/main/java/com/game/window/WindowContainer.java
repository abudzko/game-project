package com.game.window;

import com.game.lwjgl.annotation.LwjglMainThread;
import com.game.utils.log.LogUtil;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;

/**
 * Create every GLFW window on the "main" thread
 * Invoke glfwWaitEvents/glfwPollEvents in the "main" thread
 * Create any number of windows with their own render context threads
 * GLFW.glfwMakeContextCurrent(window/windowId) makes the OpenGL or OpenGL ES context of the specified window current on the calling thread
 * GL.createCapabilities() creates a new GLCapabilities instance for the OpenGL context that is current in the current thread and lets LWJGL initialize itself for OpenGL.
 */
public class WindowContainer {
    private final Map<Long, Window> windows = new ConcurrentHashMap<>();
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private boolean run = true;
    private final Thread MAIN_THREAD = new Thread(createTask());

    public void startWindows() throws InterruptedException {
        MAIN_THREAD.start();
        countDownLatch.await();
    }

    private Runnable createTask() {
        return new Runnable() {
            @LwjglMainThread
            @Override
            public void run() {
                try {
                    createWindows();
                    windows.forEach((id, window) -> {
                        try {
                            window.start();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    countDownLatch.countDown();
                    processWindowsEventsLoop();
                } catch (Exception e) {
                    LogUtil.logError(e.getMessage(), e);
                }
            }
        };
    }

    /**
     * In case of several windows 'glfwPollEvents' should be called from single thread
     */
    private void processWindowsEventsLoop() throws InterruptedException {
        Collection<Window> windowList = windows.values();
        while (run) {
            windowList.forEach(Window::processPendingEvents);
            Thread.sleep(20);
            if (windowList.stream().allMatch(Window::shouldBeClosed)) {
                run = false;
            }
        }
    }

    public Map<Long, Window> getWindows() {
        return windows;
    }

    private void createWindows() throws InterruptedException {
        iniLwjgl();
        createWindow();
    }

    private void createWindow() {
        var window = Window.createWindow();
        windows.put(window.getWindowId(), window);
    }

    private void iniLwjgl() {
        // Set an error callback. The default implementation will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        setWindowHints();
    }

    private void setWindowHints() {
        GLFW.glfwDefaultWindowHints(); // optional, the current window hints are already the default
        GLFW.glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        GLFW.glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
    }
}
