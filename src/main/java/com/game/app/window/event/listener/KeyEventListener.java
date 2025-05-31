package com.game.app.window.event.listener;

import com.game.app.window.event.key.KeyEvent;

public interface KeyEventListener extends EventListener {
    default void event(KeyEvent keyEvent) {
    }
}
