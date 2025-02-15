package com.game.lwjgl.texture;

import java.io.InputStream;

public class TextureProperties {
    private InputStream textureImageInputStream;
    private String textureImagePath;
    private float[] textureVertices;

    public static TextureProperties create() {
        return new TextureProperties();
    }

    public InputStream getTextureImage() {
        return textureImageInputStream;
    }

    public TextureProperties setTextureImageInputStream(InputStream textureImageInputStream) {
        this.textureImageInputStream = textureImageInputStream;
        return this;
    }

    public TextureProperties setTextureProperties(TextureProperties textureProperties) {
        this.textureImageInputStream = textureProperties.textureImageInputStream;
        this.textureImagePath = textureProperties.getTextureImagePath();
        this.textureVertices = textureProperties.getTextureVertices();
        return this;
    }

    public String getTextureImagePath() {
        return textureImagePath;
    }

    public TextureProperties setImagePath(String imagePath) {
        this.textureImagePath = imagePath;
        return this;
    }

    public float[] getTextureVertices() {
        return textureVertices;
    }

    public TextureProperties setTextureVertices(float[] textureVertices) {
        this.textureVertices = textureVertices;
        return this;
    }
}
