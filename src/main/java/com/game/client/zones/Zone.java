package com.game.client.zones;

public class Zone {
    Integer z;
    Integer x;
    public Integer vertexCount;
    public float[][] topEdge;
    public float[][] rightEdge;
    public float[][] bottomEdge;
    public float[][] leftEdge;
    public float[][][] heightMap;

    public Zone(int vertexCount, int z, int x) {
        this.z = z;
        this.x = x;
        this.vertexCount = vertexCount;
    }
}
