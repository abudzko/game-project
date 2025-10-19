package com.game.client.window.lwjgl.program;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

@Getter
@Setter
@Builder
public class Light {
    private Vector3f lightPosition;
    private Vector3f lightColor;
}
