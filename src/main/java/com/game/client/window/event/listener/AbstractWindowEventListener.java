package com.game.client.window.event.listener;

import com.game.client.window.lwjgl.event.LwjglEventManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Cascade delegation of Lwjgl events to child event listeners<br>
 * Window is a root listener of events from Lwjgl<br>
 * <ul>
 * <li>Window: {@link AbstractWindowEventListener#listenLwjglEvents(WindowEventListener)}</li>
 * <li>Screen: {@link AbstractWindowEventListener#addEventListener(WindowEventListener)}</li>
 * <li>CameraEventHandler: {@link AbstractWindowEventListener#addEventListener(WindowEventListener)}</li>
 * </ul>
 */
public abstract class AbstractWindowEventListener implements WindowEventListener {
    protected final List<WindowEventListener> eventListeners = new CopyOnWriteArrayList<>();
    protected LwjglEventManager lwjglEventManager;

    @Override
    public List<WindowEventListener> getEventListeners() {
        return eventListeners;
    }

    public void addEventListener(WindowEventListener windowEventListener) {
        getEventListeners().add(windowEventListener);
    }

    public void listenLwjglEvents(WindowEventListener eventListener) {
        getLwjglEventManager().addEventListener(eventListener);
    }

    public void processPendingEvents() {
        getLwjglEventManager().processPendingEvents();
    }

    protected LwjglEventManager getLwjglEventManager() {
        if (lwjglEventManager == null) {
            throw new IllegalStateException("Window lwjglEventManager is not initialized");
        }
        return lwjglEventManager;
    }
}
