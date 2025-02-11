package com.game.dao;

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
    private final List<GraphicUnit> UNITS = createUnits();
    private final GraphicUnit MAIN_UNIT = createMainUnit();

    private static List<GraphicUnit> createUnits() {
        var gameUnits = new ArrayList<GraphicUnit>();
        var groundUnit = createGroundUnit();
        gameUnits.add(groundUnit);
        return gameUnits;
    }

    public static GraphicUnit createCudeGameUnit(Vector3f position) {
        return new GraphicUnit(
                RANDOM.nextLong(),
                position,
                new Vector3f(0f, 0f, 0f),
                0.1f,
                new ObjModel(
                        ObjModelProperties.create(
                                "src/main/resources/obj/cube1.obj",
                                "/texture/any.png"
                        )
                ));
    }

    private static GraphicUnit createMainUnit() {
        return new GraphicUnit(
                MAIN_UNIT_ID,
                new Vector3f(0f, 0.5f, 0f),
                new Vector3f(0f, 0f, 0f),
                0.2f,
                new ObjModel(
                        ObjModelProperties.create(
                                "src/main/resources/obj/monkey2.obj",
                                "/texture/any.png"
                        )
                ));
    }

    private static GraphicUnit createGroundUnit() {
        return new GraphicUnit(
                1,
                new Vector3f(0f, 0.0f, 0f),
                new Vector3f(0f, 0f, 0f),
                1f,
                new ObjModel(
                        ObjModelProperties.create(
                                "src/main/resources/obj/map3.obj",
                                "/texture/dark-green.png"
                        )
                ));
    }

    public List<GraphicUnit> getUnits() {
        return UNITS;
    }

    public GraphicUnit getMainUnit() {
        return MAIN_UNIT;
    }

    public GraphicUnit createGameUnit() {
        long id = RANDOM.nextLong();
        return new GraphicUnit(
                id,
                new Vector3f(0f, 0.5f, 0f),
                new Vector3f(0f, 0f, 0f),
                0.1f,
                new ObjModel(
                        ObjModelProperties.create(
                                "src/main/resources/obj/tor1.obj",
                                "/texture/any.png"
                        )
                ));

    }

    public GraphicUnit createSunGameUnit() {
        long id = RANDOM.nextLong();
        var light = new Light();
        return new GraphicUnit(
                id,
                light.getLightPosition(),
                new Vector3f(0f, 0f, 0f),
                0.1f,
                new ObjModel(
                        new ObjModelProperties()
                                .setObjPath("src/main/resources/obj/sun.obj")
                                .setTexturePath("/texture/sun.png")
                                .setLight(light)
                ));
    }
}
