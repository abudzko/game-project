package com.game.app.window.model.obj;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ObjModelParameters {
    private String modelKey;
    private byte[] objectSource;
    private byte[] textureSource;
}
