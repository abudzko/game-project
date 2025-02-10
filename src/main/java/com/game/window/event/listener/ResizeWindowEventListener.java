package com.game.window.event.listener;

import com.game.window.event.resize.ResizeWindowEvent;

public interface ResizeWindowEventListener extends EventListener {
    default void event(ResizeWindowEvent resizeWindowEvent) {
    }
}
