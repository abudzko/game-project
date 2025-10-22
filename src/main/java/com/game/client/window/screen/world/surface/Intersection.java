package com.game.client.window.screen.world.surface;

import lombok.Builder;
import lombok.Getter;
import org.joml.Vector3f;

@Getter
@Builder
public class Intersection {
    private final long gameUnitId;
    private final Vector3f point;
}
