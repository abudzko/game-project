package com.game.app.window.model.obj;

import com.game.app.window.model.texture.Texture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public interface Model {

    int POINT_PER_VERTEX_3D = 3;

    /**
     * Vertices of 3D model [x0, y0, z0, x1, y1, z1, ...]<br>
     * Where x0, y0, z0 is single vertex
     */
    FloatBuffer verticesBuffer();

    float[] getVertices();

    /**
     * Normals are used in lighting
     * Normals consist of three coordinates [x0, y0, z0, x1, y1, z1, ...]<br>
     * Where x0, y0, z0 is single normal
     */
    FloatBuffer normalsBuffer();

    /**
     * Indexes of vertices forming triangles<br>
     * Triangle consists of three vertexes
     * Each index represents a single vertex<br>
     * Vertex consist of three coordinates: x, y, z<br>
     * Index is used to find tuple of vertex(x0, y0, z0, normal(x1, y1, z1) and texture(x2, y2)
     * in three arrays vertices[], normals[], textures[]
     */
    IntBuffer indexesBuffer();

    int[] getIndexes();

    int indexesCount();

    Texture modelTexture();

    default int getPointPerVertex3d() {
        return POINT_PER_VERTEX_3D;
    }

    FloatBuffer textureVertices();
}
