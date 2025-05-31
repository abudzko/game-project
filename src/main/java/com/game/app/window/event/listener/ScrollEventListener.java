package com.game.app.window.event.listener;

import com.game.app.window.event.scroll.ScrollEvent;

public interface ScrollEventListener extends EventListener {
    default void event(ScrollEvent scrollEvent) {
    }
}
