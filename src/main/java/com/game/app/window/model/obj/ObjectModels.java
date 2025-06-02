package com.game.app.window.model.obj;

import com.game.app.window.model.obj.resources.ObjectResources;
import com.game.app.window.model.obj.resources.TextureResources;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectModels {
    private static final String RESOURCES_PATH = "/obj";
    private static final ObjectResources OBJECT_RESOURCES = new ObjectResources();
    private static final TextureResources TEXTURE_RESOURCES = new TextureResources();
    private static final Map<String, Model> MODEL_CACHE = new ConcurrentHashMap<>();

    public Model getModel(String modelKey) {
        return MODEL_CACHE.computeIfAbsent(modelKey, this::createModel);
    }

    private ObjModel createModel(String modelKey) {
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
