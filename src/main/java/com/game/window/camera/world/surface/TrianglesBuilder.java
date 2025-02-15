package com.game.window.camera.world.surface;

import com.game.model.obj.Model;
import com.game.utils.BufferUtils;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class TrianglesBuilder {

    private static Vector3f readVertex(
            float[] verticesArr,
            int[] indexesArr,
            int indexOfIndex
    ) {
        var v1 = verticesArr[indexesArr[indexOfIndex]];
        var v2 = verticesArr[indexesArr[++indexOfIndex]];
        var v3 = verticesArr[indexesArr[++indexOfIndex]];
        return new Vector3f(v1, v2, v3);
    }

    private static float[] toArray(FloatBuffer floatBuffer) {
        var arr = new float[floatBuffer.limit()];
        floatBuffer.get(arr);
        return arr;
    }

    private static int[] toArray(IntBuffer intBuffer) {
        var arr = new int[intBuffer.limit()];
        intBuffer.get(arr);
        return arr;
    }

    private static int indexOfFirstVertex(int[] indexesArr, int i, int pointPerVertex3d) {
        return indexesArr[i] * pointPerVertex3d;
    }

    private static Vector3f readVertex(float[] verticesArr, int indexOfFirst) {
        return new Vector3f(
                verticesArr[indexOfFirst],
                verticesArr[++indexOfFirst],
                verticesArr[++indexOfFirst]
        );
    }

    public List<Triangle> toTriangles(Model model) {
        var triangles = new ArrayList<Triangle>();
        var vertices = model.vertices();
        var verticesArr = toArray(vertices);
        BufferUtils.memFree(vertices);

        var indexes = model.indexes();
        var indexesArr = toArray(indexes);
        BufferUtils.memFree(indexes);

        int pointPerVertex3d = model.getPointPerVertex3d();
        for (int i = 0; i < indexesArr.length; i++) {
            var triangle = new Triangle(
                    readVertex(verticesArr, indexOfFirstVertex(indexesArr, i, pointPerVertex3d)),
                    readVertex(verticesArr, indexOfFirstVertex(indexesArr, ++i, pointPerVertex3d)),
                    readVertex(verticesArr, indexOfFirstVertex(indexesArr, ++i, pointPerVertex3d)));
            triangles.add(triangle);
        }
        return triangles;
    }
}
