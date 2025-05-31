package com.game.app.window.model.obj;

import com.game.app.window.lwjgl.texture.TextureProperties;
import com.game.app.window.model.Light;

public class ObjModelProperties {
    private String objPath;
    private String objectSource;
    private TextureProperties textureProperties;
    private Light light;
    private boolean useShading = true;

    public static ObjModelProperties create() {
        return new ObjModelProperties();
    }

    public TextureProperties getTextureProperties() {
        return textureProperties;
    }

    public ObjModelProperties setTextureProperties(TextureProperties textureProperties) {
        this.textureProperties = textureProperties;
        return this;
    }

    public String getObjectSource() {
        return objectSource;
    }

    public ObjModelProperties setObjectSource(String objectSource) {
        this.objectSource = objectSource;
        return this;
    }


    public String getObjPath() {
        return objPath;
    }

    public ObjModelProperties setObjPath(String objPath) {
        this.objPath = objPath;
        return this;
    }

    public Light getLight() {
        return light;
    }

    public ObjModelProperties setLight(Light light) {
        this.light = light;
        this.useShading = false;
        return this;
    }

    public boolean isUseShading() {
        return useShading;
    }

    public ObjModelProperties setUseShading(boolean value) {
        useShading = value;
        return this;
    }
}
