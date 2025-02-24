package com.game.dao;

import com.game.engine.unit.GameUnit;
import com.game.lwjgl.texture.TextureProperties;
import com.game.model.GraphicUnit;
import com.game.model.Light;
import com.game.model.obj.ObjModel;
import com.game.model.obj.ObjModelProperties;
import com.game.utils.log.LogUtil;
import org.joml.Vector3f;

public class GraphicUnitDao {
    public static final GraphicUnitDao INSTANCE = new GraphicUnitDao();
    private static final TextureCache textureCache = new TextureCache();
    private static final ObjCache objectCache = new ObjCache();

    private static String getObjectSource(String objPath) {
        return objectCache.getObject(objPath);
    }

    private static TextureProperties createTextureProperties(String imagePath) {
        return TextureProperties.create().setTextureProperties(textureCache.getTexture(imagePath));
    }

    private static ObjModelProperties createObjModelProperties(String objPath, String imagePath) {
        return ObjModelProperties.create()
                .setObjectSource(getObjectSource(objPath))
                .setTextureProperties(createTextureProperties(imagePath));
    }

    public GraphicUnit createGraphicUnit(GameUnit gameUnit) {
        return GraphicUnit.builder()
                .id(gameUnit.getId())
                .dynamic(gameUnit.isDynamic())
                .position(gameUnit.getPosition())
                .rotation(new Vector3f(0f, 0f, 0f))
                .scale(1f)
                .model(ObjModel.createObjectModel(createObjModelProperties("/obj/smallSphere.obj", "/texture/yellow.png"))).build();
    }

    public GraphicUnit createGroundUnit(GameUnit gameUnit) {
        var start = System.currentTimeMillis();
        var groundUnit = GraphicUnit.builder()
                .id(gameUnit.getId())
                .dynamic(gameUnit.isDynamic())
                .position(gameUnit.getPosition())
                .rotation(new Vector3f(0f, 0f, 0f))
                .scale(1f)
                .model(ObjModel.createObjectModel(createObjModelProperties("/obj/map2.obj", "/texture/dark-green.png")))
                .build();
        LogUtil.logDebug("createGroundUnit: " + (System.currentTimeMillis() - start) + "ms");
        return groundUnit;
    }

    public GraphicUnit createPlayerGraphicUnit(GameUnit gameUnit) {
        return GraphicUnit.builder()
                .id(gameUnit.getId())
                .dynamic(gameUnit.isDynamic())
                .position(gameUnit.getPosition())
                .rotation(new Vector3f(0f, 0f, 0f))
                .scale(1f)
                .model(ObjModel.createObjectModel(createObjModelProperties("/obj/smallSphere.obj", "/texture/blue.png")))
                .build();
    }

    public GraphicUnit createSunUnit(GameUnit gameUnit) {
        var light = new Light();
        return GraphicUnit.builder()
                .id(gameUnit.getId())
                .dynamic(gameUnit.isDynamic())
                .position(light.getLightPosition())
                .rotation(new Vector3f(0f, 0f, 0f))
                .scale(1f)
                .model(ObjModel.createObjectModel(
                        createObjModelProperties(
                                "/obj/sun.obj",
                                "/texture/sun.png"
                        ).setLight(light)))
                .build();
    }
}
