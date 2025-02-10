package com.game.window.event.listener;

import com.game.window.event.key.KeyEvent;

public interface KeyEventListener extends EventListener {
    default void event(KeyEvent keyEvent) {
    }
}
