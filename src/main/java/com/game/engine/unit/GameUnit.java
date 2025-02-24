package com.game.engine.unit;

import lombok.Builder;
import lombok.Getter;
import org.joml.Vector3f;

@Builder
@Getter
public class GameUnit {
    private final long id;
    private final Vector3f position;
    private final boolean dynamic;

    public void step() {

    }
}
