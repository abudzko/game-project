package com.game.client.window.event.listener;

import com.game.client.window.event.key.KeyEvent;

public interface KeyEventListener extends EventListener {
    default void event(KeyEvent keyEvent) {
    }
}
