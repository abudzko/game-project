package com.game.client.window.screen.world.engine.unit;

public class GameUnitType {
    public static final String UNITS = "units";
    public static final String PLAYER = unit("player");
    public static final String SUN = unit("sun");
    public static final String SKYDOME = unit("skydome");
    public static final String GROUND = unit("ground");
    public static final String GEN_GROUND = unit("genground");
    public static final String TREE_SPRUCE = unit("tree.spruce");
    public static final String TREE_THIJA = unit("tree.thuja");

    public static String unit(String unit) {
        return UNITS + "." + unit;
    }
}
