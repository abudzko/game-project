package com.game.window.event.listener;

import com.game.window.event.scroll.ScrollEvent;

public interface ScrollEventListener extends EventListener {
    default void event(ScrollEvent scrollEvent) {
    }
}
