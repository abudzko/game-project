package com.game.model.texture;

import com.game.lwjgl.texture.PngTexture;
import com.game.lwjgl.texture.TextureProperties;
import com.game.utils.BufferUtils;

import java.nio.FloatBuffer;

public class ObjTexture implements Texture {

    private final PngTexture texture;
    private final float[] textureVertices;

    private ObjTexture(TextureProperties properties) {
        this.texture = PngTexture.createPngTexture(properties);
        this.textureVertices = properties.getTextureVertices();
    }

    public static ObjTexture createObjTexture(TextureProperties properties) {
        return new ObjTexture(properties);
    }

    @Override
    public int textureId() {
        return texture.getTextureId();
    }


    @Override
    public FloatBuffer textureVertices() {
        return BufferUtils.createFloatBuffer4f(textureVertices);
    }
}
