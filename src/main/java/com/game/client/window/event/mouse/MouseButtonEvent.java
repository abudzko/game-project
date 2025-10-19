package com.game.client.window.event.mouse;

public class MouseButtonEvent {
    private final MouseButton button;
    private final MouseButtonAction action;
    private final int mods;
    private final double x;
    private final double y;

    public MouseButtonEvent(MouseButton button, MouseButtonAction action, int mods, double x, double y) {
        this.button = button;
        this.action = action;
        this.mods = mods;
        this.x = x;
        this.y = y;
    }

    public MouseButton getButton() {
        return button;
    }

    public MouseButtonAction getAction() {
        return action;
    }

    public int getMods() {
        return mods;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
