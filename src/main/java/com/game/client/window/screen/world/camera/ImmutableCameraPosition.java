package com.game.client.window.screen.world.camera;

import lombok.Builder;
import lombok.Getter;
import org.joml.Vector3f;

@Builder
@Getter
public class ImmutableCameraPosition {
    private final Vector3f centerPosition;
    private final Vector3f eyePosition;
}
