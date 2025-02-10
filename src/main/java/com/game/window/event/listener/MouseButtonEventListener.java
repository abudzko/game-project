package com.game.window.event.listener;

import com.game.window.event.mouse.MouseButtonEvent;

public interface MouseButtonEventListener extends EventListener {
    default void event(MouseButtonEvent mouseButtonEvent) {
    }
}
