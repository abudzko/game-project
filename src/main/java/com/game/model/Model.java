package com.game.model;

import com.game.model.texture.ModelTexture;

import java.nio.FloatBuffer;

public interface Model {
    /**
     * Vertices of 3D model [x0, y0, z0, x1, y1, z1, ...]<br>
     * Where x0, y0, z0 is single vertex
     */
    float[] vertices();

    /**
     * Indexes of vertices forming triangles<br>
     * Triangle consists of three indexes
     * Each index represents a single vertex<br>
     * Vertex consist of three coordinates: x, y, z<br>
     */
    int[] indexes();

    /**
     * Vertices grouped in triangles. Vertex consist of three float coordinates x, y, z<br>
     * Triangle consists of three vertices
     */
    FloatBuffer triangleVertices();

    default FloatBuffer triangleVertexNormals() {
        return null;
    }

    ModelTexture modelTexture();

    default boolean isLight() {
        return getLight() != null;
    }

    default Light getLight() {
        return null;
    }
}
