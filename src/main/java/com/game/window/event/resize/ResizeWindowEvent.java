package com.game.window.event.resize;

public class ResizeWindowEvent {

    private final int newWidth;
    private final int newHeight;

    public ResizeWindowEvent(int newWidth, int newHeight) {
        this.newWidth = newWidth;
        this.newHeight = newHeight;
    }

    public int getNewWidth() {
        return newWidth;
    }

    public int getNewHeight() {
        return newHeight;
    }
}
