package com.game.client.window.lwjgl.program;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;

public class TextureUnits {
    public static final int SHADOW_UNIT = GL_TEXTURE2;
    public static final int SHADOW_MAP_INDEX = SHADOW_UNIT - GL_TEXTURE0;
    public static final int DEPTH_MAP_SIZE = 2048;
}
