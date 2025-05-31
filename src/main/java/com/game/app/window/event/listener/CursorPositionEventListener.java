package com.game.app.window.event.listener;

import com.game.app.window.event.cursor.CursorPositionEvent;

public interface CursorPositionEventListener extends EventListener {
    default void event(CursorPositionEvent cursorPositionEvent) {
    }
}
