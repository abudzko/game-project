package com.game.client.window.event.listener;

import com.game.client.window.event.mouse.MouseButtonEvent;

public interface MouseButtonEventListener extends EventListener {
    default void event(MouseButtonEvent mouseButtonEvent) {
    }
}
