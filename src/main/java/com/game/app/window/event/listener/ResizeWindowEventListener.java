package com.game.app.window.event.listener;

import com.game.app.window.event.resize.ResizeWindowEvent;

public interface ResizeWindowEventListener extends EventListener {
    default void event(ResizeWindowEvent resizeWindowEvent) {
    }
}
