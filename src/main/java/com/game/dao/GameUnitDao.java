package com.game.dao;

import com.game.model.GameUnit;
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
    private final List<GameUnit> UNITS = createUnits();
    private final GameUnit MAIN_UNIT = createMainUnit();

    private static List<GameUnit> createUnits() {
        var gameUnits = new ArrayList<GameUnit>();
        var groundUnit = createGroundUnit();
        gameUnits.add(groundUnit);
        return gameUnits;
    }

    public static GameUnit createCudeGameUnit(Vector3f position) {
        return new GameUnit(
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

    private static GameUnit createMainUnit() {
        return new GameUnit(
                MAIN_UNIT_ID,
                new Vector3f(0f, 0.5f, 0f),
                new Vector3f(0f, 0f, 0f),
                0.2f,
                new ObjModel(
                        ObjModelProperties.create(
                                "src/main/resources/obj/monkey2.obj",
                                "/texture/red.png"
                        )
                ));
    }

    private static GameUnit createGroundUnit() {
        return new GameUnit(
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

    public List<GameUnit> getUnits() {
        return UNITS;
    }

    public GameUnit getMainUnit() {
        return MAIN_UNIT;
    }

    public GameUnit createGameUnit() {
        long id = RANDOM.nextLong();
        return new GameUnit(
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

    public GameUnit createSunGameUnit() {
        long id = RANDOM.nextLong();
        var light = new Light();
        return new GameUnit(
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
