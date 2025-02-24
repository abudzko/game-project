package com.game.window.screen.world.surface;

import lombok.Builder;
import lombok.Getter;
import org.joml.Vector3f;

@Getter
@Builder
public class Intersection {
    private final long unitId;
    private final Vector3f point;
}
