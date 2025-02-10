package com.game.window.event.listener;

import com.game.window.event.cursor.CursorPositionEvent;
import com.game.window.event.key.KeyEvent;
import com.game.window.event.mouse.MouseButtonEvent;
import com.game.window.event.resize.ResizeWindowEvent;
import com.game.window.event.scroll.ScrollEvent;

import java.util.Collections;
import java.util.List;

public interface WindowEventListener extends
        CursorPositionEventListener,
        KeyEventListener,
        MouseButtonEventListener,
        ResizeWindowEventListener,
        ScrollEventListener {

    default List<WindowEventListener> getEventChildListeners() {
        return Collections.emptyList();
    }

    @Override
    default void event(CursorPositionEvent event) {
        getEventChildListeners().forEach(listener -> listener.event(event));
    }

    @Override
    default void event(KeyEvent event) {
        getEventChildListeners().forEach(listener -> listener.event(event));
    }

    @Override
    default void event(MouseButtonEvent event) {
        getEventChildListeners().forEach(listener -> listener.event(event));
    }

    @Override
    default void event(ResizeWindowEvent event) {
        getEventChildListeners().forEach(listener -> listener.event(event));
    }

    @Override
    default void event(ScrollEvent event) {
        getEventChildListeners().forEach(listener -> listener.event(event));
    }
}
