package com.game.model.texture;

import com.game.lwjgl.texture.PngTexture;
import com.game.utils.BufferUtils;

import java.nio.FloatBuffer;

public class ObjTexture implements Texture {

    private final PngTexture texture;
    private final float[] textureVertices;

    public ObjTexture(String imagePath, float[] textureVertices) {
        this.texture = new PngTexture(imagePath);
        this.textureVertices = textureVertices;
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
