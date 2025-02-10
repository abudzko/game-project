package com.game.event;

import com.game.event.engine.EngineEventListener;
import com.game.window.event.listener.WindowEventListener;

import java.util.List;

public class EventRouter implements WindowEventListener, EngineEventListener {

    @Override
    public List<WindowEventListener> getEventChildListeners() {
        throw new UnsupportedOperationException();
    }
}
