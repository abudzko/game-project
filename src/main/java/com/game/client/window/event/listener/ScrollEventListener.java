package com.game.client.window.event.listener;

import com.game.client.window.event.scroll.ScrollEvent;

public interface ScrollEventListener extends EventListener {
    default void event(ScrollEvent scrollEvent) {
    }
}
