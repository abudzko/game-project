package com.game.client.zones.surface.export;

public class MapConfig {
    // -Oz
    public int mapRowCount = 4;
    // Ox
    public int mapColumnCount = 4;
    // 10x10 zone will require 11(vertexes)x11(vertexes)
    public int zoneSize = 40;
    public int vertexCount = zoneSize + 1;
    public float xzScale = 2f;
    public float yScale = 2f;
}
