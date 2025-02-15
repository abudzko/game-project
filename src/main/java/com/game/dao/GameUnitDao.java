package com.game.dao;

import com.game.lwjgl.texture.TextureProperties;
import com.game.model.GraphicUnit;
import com.game.model.Light;
import com.game.model.obj.ObjModel;
import com.game.model.obj.ObjModelProperties;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameUnitDao {
    protected static final Random RANDOM = new Random();
    private static final long MAIN_UNIT_ID = 0;
    private static final TextureCache textureCache = new TextureCache();
    private static final ObjCache objectCache = new ObjCache();
    private static final GraphicUnit GROUND_UNIT = createGroundUnit();
    private final List<GraphicUnit> UNITS = createUnits();
    private final GraphicUnit MAIN_UNIT = createMainUnit();

    private static List<GraphicUnit> createUnits() {
        var units = new ArrayList<GraphicUnit>();
        return units;
    }

    public static GraphicUnit createCubeGraphicUnit(Vector3f position) {
        return new GraphicUnit(
                RANDOM.nextLong(),
                position,
                new Vector3f(0f, 0f, 0f),
                1f,
                ObjModel.createObjectModel(
                        ObjModelProperties.create()
                                .setObjectSource(objectCache.getObject("/obj/cube.obj"))
                                .setTextureProperties(
                                        TextureProperties
                                                .create().setTextureProperties(textureCache.getTexture("/texture/any.png")))

                ));
    }

    public static GraphicUnit createSmallCircleGraphicUnit(Vector3f position) {
        return new GraphicUnit(
                RANDOM.nextLong(),
                position,
                new Vector3f(0f, 0f, 0f),
                1f,
                ObjModel.createObjectModel(
                        ObjModelProperties.create()
                                .setObjectSource(objectCache.getObject("/obj/smallSphere.obj"))
                                .setTextureProperties(
                                        TextureProperties
                                                .create().setTextureProperties(textureCache.getTexture("/texture/yellow.png")))

                ));
    }

    private static GraphicUnit createMainUnit() {
        return new GraphicUnit(
                MAIN_UNIT_ID,
                new Vector3f(0f, 0.0f, 0f),
                new Vector3f(0f, 0f, 0f),
                1f,
                ObjModel.createObjectModel(
                        ObjModelProperties.create()
                                .setObjectSource(objectCache.getObject("/obj/cube.obj"))
                                .setTextureProperties(
                                        TextureProperties
                                                .create().setTextureProperties(textureCache.getTexture("/texture/any.png")))

                ));
    }

    private static GraphicUnit createGroundUnit() {
        return new GraphicUnit(
                1,
                new Vector3f(0f, 0.0f, 0f),
                new Vector3f(0f, 0f, 0f),
                1f,
                ObjModel.createObjectModel(
                        ObjModelProperties.create()
                                .setObjectSource(objectCache.getObject("/obj/map3.obj"))
                                .setTextureProperties(
                                        TextureProperties
                                                .create().setTextureProperties(textureCache.getTexture("/texture/dark-green.png")))

                ));
    }

    public List<GraphicUnit> getUnits() {
        return UNITS;
    }

    public GraphicUnit getMainUnit() {
        return MAIN_UNIT;
    }

    public GraphicUnit getGroundUnit() {
        return GROUND_UNIT;
    }

    public GraphicUnit createGameUnit() {
        long id = RANDOM.nextLong();
        return new GraphicUnit(
                id,
                new Vector3f(0f, 0.5f, 0f),
                new Vector3f(0f, 0f, 0f),
                1f,
                ObjModel.createObjectModel(
                        ObjModelProperties.create()
                                .setObjectSource(objectCache.getObject("/obj/tor1.obj"))
                                .setTextureProperties(
                                        TextureProperties
                                                .create().setTextureProperties(textureCache.getTexture("/texture/any.png")))

                ));

    }

    public GraphicUnit createSunGameUnit() {
        long id = RANDOM.nextLong();
        var light = new Light();
        return new GraphicUnit(
                id,
                light.getLightPosition(),
                new Vector3f(0f, 0f, 0f),
                1f,
                ObjModel.createObjectModel(
                        ObjModelProperties.create()
                                .setObjectSource(objectCache.getObject("/obj/sun.obj"))
                                .setTextureProperties(
                                        TextureProperties
                                                .create().setTextureProperties(textureCache.getTexture("/texture/sun.png")))
                                .setLight(light)

                ));
    }
}
