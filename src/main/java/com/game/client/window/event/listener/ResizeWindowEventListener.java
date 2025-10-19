package com.game.client.window.event.listener;

import com.game.client.window.event.resize.ResizeWindowEvent;

public interface ResizeWindowEventListener extends EventListener {
    default void event(ResizeWindowEvent resizeWindowEvent) {
    }
}
