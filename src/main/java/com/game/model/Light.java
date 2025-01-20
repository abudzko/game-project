package com.game.model;

import org.joml.Vector3f;

public class Light {
    private Vector3f lightPosition = new Vector3f(0.0f, 3.0f, 0.0f);
    // TODO it is constant and it is a white color
    private Vector3f lightColor = new Vector3f(1.0f, 1.0f, 1.0f);

    public Vector3f getLightPosition() {
        return lightPosition;
    }

    public Light setLightPosition(Vector3f lightPosition) {
        this.lightPosition = lightPosition;
        return this;
    }

    public Vector3f getLightColor() {
        return lightColor;
    }

    public Light setLightColor(Vector3f lightColor) {
        this.lightColor = lightColor;
        return this;
    }
}
