package com.game.app.window.event.resize;

import lombok.Getter;

@Getter
public class ResizeWindowEvent {

    private final int newWidth;
    private final int newHeight;

    public ResizeWindowEvent(int newWidth, int newHeight) {
        this.newWidth = newWidth;
        this.newHeight = newHeight;
    }
}
