package com.game.client.window.model.obj;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.client.window.model.obj.resources.ObjectResources;
import com.game.client.window.model.obj.resources.Resources;
import com.game.client.window.model.obj.resources.TextureResources;
import com.game.client.window.model.obj.zone.ZoneConfig;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectModels {
    private static final String RESOURCES_PATH = "/obj";
    private static final String ZONE_CONFIG_PATH = RESOURCES_PATH + "/units/zone/conf.json";
    private static final ObjectResources OBJECT_RESOURCES = new ObjectResources();
    private static final Resources RESOURCES = new Resources();
    private static final TextureResources TEXTURE_RESOURCES = new TextureResources();
    private static final Map<String, Model> MODEL_CACHE = new ConcurrentHashMap<>();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static ZoneConfig ZONE_CONFIG;

    public static ZoneConfig getZoneConfig() {
        try {
            if (ZONE_CONFIG == null) {
                ZONE_CONFIG = OBJECT_MAPPER.readValue(RESOURCES.getResource(ZONE_CONFIG_PATH), ZoneConfig.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ZONE_CONFIG;
    }

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
