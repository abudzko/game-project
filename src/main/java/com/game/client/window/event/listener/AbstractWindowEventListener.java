package com.game.client.window.event.listener;

import com.game.client.window.lwjgl.event.LwjglEventManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Cascade delegation of Lwjgl events to child event listeners<br>
 * Window is a root listener of events from Lwjgl<br>
 * <ul>
 * <li>Window: {@link AbstractWindowEventListener#addRootEventListener(WindowEventListener)}</li>
 * <li>Screen: {@link AbstractWindowEventListener#addEventChildListener(WindowEventListener)}</li>
 * <li>CameraEventHandler: {@link AbstractWindowEventListener#addEventChildListener(WindowEventListener)}</li>
 * </ul>
 */
public abstract class AbstractWindowEventListener implements WindowEventListener {
    protected final List<WindowEventListener> eventChildListeners = new CopyOnWriteArrayList<>();
    protected LwjglEventManager eventManager;

    @Override
    public List<WindowEventListener> getEventChildListeners() {
        return eventChildListeners;
    }

    public void addEventChildListener(WindowEventListener windowEventListener) {
        getEventChildListeners().add(windowEventListener);
    }

    public void addRootEventListener(WindowEventListener eventListener) {
        getEventManager().addEventListener(eventListener);
    }

    public void processPendingEvents() {
        getEventManager().processPendingEvents();
    }

    protected LwjglEventManager getEventManager() {
        if (eventManager == null) {
            throw new IllegalStateException("Window event manager is not initialized");
        }
        return eventManager;
    }
}
