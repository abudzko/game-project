package com.game.client.window.event.listener;

import com.game.client.window.event.cursor.CursorPositionEvent;

public interface CursorPositionEventListener extends EventListener {
    default void event(CursorPositionEvent cursorPositionEvent) {
    }
}
