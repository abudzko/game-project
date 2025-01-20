package com.game.model.obj;

import com.game.model.Light;

public class ObjModelProperties {
    private String objPath;
    private String texturePath;

    private Light light;

    public static ObjModelProperties create(String objPath, String texturePath) {
        return new ObjModelProperties().setObjPath(objPath).setTexturePath(texturePath);
    }

    public String getObjPath() {
        return objPath;
    }

    public ObjModelProperties setObjPath(String objPath) {
        this.objPath = objPath;
        return this;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public ObjModelProperties setTexturePath(String texturePath) {
        this.texturePath = texturePath;
        return this;
    }

    public Light getLight() {
        return light;
    }

    public ObjModelProperties setLight(Light light) {
        this.light = light;
        return this;
    }
}
