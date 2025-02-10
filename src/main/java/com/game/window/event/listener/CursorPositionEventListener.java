package com.game.window.event.listener;

import com.game.window.event.cursor.CursorPositionEvent;

public interface CursorPositionEventListener extends EventListener {
    default void event(CursorPositionEvent cursorPositionEvent) {
    }
}
