package com.game.app.window.model;

import com.game.app.window.model.obj.ObjModel;
import com.game.app.window.model.obj.ObjModelParameters;
import com.game.app.window.resources.ObjectResources;
import com.game.app.window.resources.TextureResources;

public class ModelFactory {
    private static final String RESOURCES_PATH = "/obj";
    private static final ObjectResources OBJECT_RESOURCES = new ObjectResources();
    private static final TextureResources TEXTURE_RESOURCES = new TextureResources();

    public ObjModel createModel(String modelKey) {
        //obj/unit/sky
        // units/sky = units.sky
        var objSource = OBJECT_RESOURCES.getObjectSource(buildModelPath(modelKey) + '/' + "model.obj");
        var textureSource = TEXTURE_RESOURCES.getTextureSource(buildModelPath(modelKey) + '/' + "texture.png");
        return ObjModel.createObjectModel(ObjModelParameters.builder()
                        .modelKey(modelKey)
                        .objectSource(objSource)
                        .textureSource(textureSource)
                .build());
    }

    private String buildModelPath(String modelKey) {
        return RESOURCES_PATH + "/" + modelKey.replace('.', '/');
    }
}
