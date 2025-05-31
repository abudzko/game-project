package com.game.app.window.event.cursor;

public class CursorPositionEvent {
    private final double x;
    private final double y;

    public CursorPositionEvent(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
