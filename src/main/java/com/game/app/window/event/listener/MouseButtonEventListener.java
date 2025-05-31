package com.game.app.window.event.listener;

import com.game.app.window.event.mouse.MouseButtonEvent;

public interface MouseButtonEventListener extends EventListener {
    default void event(MouseButtonEvent mouseButtonEvent) {
    }
}
