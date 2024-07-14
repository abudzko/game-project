package com.game.window.events;

public class ScrollEvent {

    private final double offsetX;
    private final double offsetY;

    public ScrollEvent(double offsetX, double offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }
}
