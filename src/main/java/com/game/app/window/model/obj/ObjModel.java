package com.game.app.window.model.obj;

import com.game.app.window.model.texture.PngTexture;
import com.game.app.window.model.texture.Texture;
import com.game.utils.BufferUtils;
import com.game.utils.log.LogUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents Blender .obj file
 * Model should have Triangulate (Beauty-Beauty) modifier
 * When model is exported check-boxes Apply-Modifier and Triangulate-Mesh should be checked
 */
public class ObjModel implements Model {
    private static final String LF = "\n";
    private final byte[] objectSource;
    private final ObjModelParameters objModelParameters;
    private float[] vertices;
    private float[] normals;
    private float[] textures;
    private int[] indexes;
    private Texture texture;

    private ObjModel(ObjModelParameters objModelParameters) {
        this.objectSource = objModelParameters.getObjectSource();
        this.objModelParameters = objModelParameters;
        parseObj();
        modelTexture();
    }

    public static ObjModel createObjectModel(ObjModelParameters properties) {
        return new ObjModel(properties);
    }

    /**
     * @param line   = "f 1/2/2 2/3/2 3/32/2"
     * @param result size is 9 - index, texture, normal
     */
    private static void parseFLine(String line, int[] result) {
        int j = 0;
        int start = 2;
        int radix = 10;
        for (int i = start; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '/' || ch == ' ') {
                result[j] = Integer.parseInt(line, start, i, radix);
                start = i + 1;
                j++;
            }
        }
        result[j] = Integer.parseInt(line, start, line.length(), radix);
    }

    /**
     * @param line   = "v 0.2 0.3 1.3"
     * @param result size is 3 - x, y, z
     */
    private static void parseVertexLine(String line, float[] result) {
        int j = 0;
        int start = line.indexOf(' ') + 1;
        for (int i = start; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == ' ') {
                result[j] = Float.parseFloat(line.substring(start, i));
                start = i + 1;
                j++;
            }
        }
        result[j] = Float.parseFloat(line.substring(start));
    }

    /**
     * @param line   = "v 0.2 0.3 1.3"
     * @param result size is 2 - x, y
     */
    private static void parseTextureLine(String line, float[] result) {
        int j = 0;
        int start = line.indexOf(' ') + 1;
        for (int i = start; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == ' ') {
                result[j] = Float.parseFloat(line.substring(start, i));
                start = i + 1;
                j++;
            }
        }
        result[j] = Float.parseFloat(line.substring(start));
    }

    private void parseObj() {
        var verticesMap = new HashMap<Integer, Vertex>();
        var normalsMap = new HashMap<Integer, VertexNormal>();
        var textureMap = new HashMap<Integer, TextureVertex>();

        var indexTupleList = new ArrayList<IndexTuple>(32);
        int verticesIndex = 1;
        int vertexNormalsIndex = 1;
        int textureVerticesIndex = 1;

        var start = System.currentTimeMillis();
        var lines = new String(objectSource, StandardCharsets.UTF_8).split(LF);
        LogUtil.logDebug("objectSource split: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        var int9Result = new int[9];
        var float3Result = new float[3];
        var float2Result = new float[2];
        for (var line : lines) {
            var type = line.substring(0, line.indexOf(' '));
            switch (type) {
                // Vertices
                case "v":
                    var vertex = new Vertex();
                    parseVertexLine(line, float3Result);
                    vertex.x = float3Result[0];
                    vertex.y = float3Result[1];
                    vertex.z = float3Result[2];
                    verticesMap.put(verticesIndex++, vertex);
                    break;
                // Texture vertices
                case "vt":
                    var textureVertex = new TextureVertex();
                    parseTextureLine(line, float2Result);
                    textureVertex.x = float2Result[0];
                    textureVertex.y = float2Result[1];
                    textureMap.put(textureVerticesIndex++, textureVertex);
                    break;
                // Normals
                case "vn":
                    var vertexNormal = new VertexNormal();
                    parseVertexLine(line, float3Result);
                    vertexNormal.x = float3Result[0];
                    vertexNormal.y = float3Result[1];
                    vertexNormal.z = float3Result[2];
                    normalsMap.put(vertexNormalsIndex++, vertexNormal);
                    break;
                // Faces
                case "f":
                    parseFLine(line, int9Result);
                    for (int i = 0; i < int9Result.length; i++) {
                        var indexTuple = new IndexTuple();
                        indexTuple.vertexIndex = int9Result[i];
                        indexTuple.textureIndex = int9Result[++i];
                        indexTuple.normalIndex = int9Result[++i];
                        indexTupleList.add(indexTuple);
                    }
                    break;
                default:
                    break;
            }
        }
        LogUtil.logDebug("for: " + (System.currentTimeMillis() - start) + "ms");

        int uniqueCount = new HashSet<>(indexTupleList).size();
        vertices = new float[uniqueCount * 3];
        normals = new float[uniqueCount * 3];
        textures = new float[uniqueCount * 2];
        indexes = new int[indexTupleList.size()];

        start = System.currentTimeMillis();
        var indexTuplesMap = new HashMap<IndexTuple, Integer>();
        var vi = 0;
        var ni = 0;
        var ti = 0;
        var indx = 0;
        var i = 0;
        for (var indexTuple : indexTupleList) {
            var existedIndex = indexTuplesMap.get(indexTuple);
            if (existedIndex == null) {
                indexTuplesMap.put(indexTuple, indx);
                indexes[i] = indx++;

                var vertex = verticesMap.get(indexTuple.vertexIndex);
                addVertex(vertices, vertex, vi);

                var normal = normalsMap.get(indexTuple.normalIndex);
                addNormal(normals, normal, ni);

                var texture = textureMap.get(indexTuple.textureIndex);
                addTexture(textures, texture, ti);

                ti += 2;
                vi += 3;
                ni += 3;
            } else {
                indexes[i] = existedIndex;
            }
            i++;
        }
        LogUtil.logDebug("vertices: " + (System.currentTimeMillis() - start) + "ms");
    }

    private void addVertex(float[] arr, Vertex v, int index) {
        arr[index++] = v.x;
        arr[index++] = v.y;
        arr[index] = v.z;
    }

    private void addNormal(float[] arr, VertexNormal vn, int index) {
        arr[index++] = vn.x;
        arr[index++] = vn.y;
        arr[index] = vn.z;
    }

    private void addTexture(float[] arr, TextureVertex textureVertex, int index) {
        arr[index++] = textureVertex.x;
        arr[index] = textureVertex.y;
    }

    @Override
    public String modelKey() {
        return objModelParameters.getModelKey();
    }

    @Override
    public FloatBuffer verticesBuffer() {
        if (vertices == null) {
            parseObj();
        }
        return BufferUtils.createFloatBuffer4f(vertices);
    }

    public float[] getVertices() {
        if (vertices == null) {
            parseObj();
        }
        return vertices;
    }

    @Override
    public FloatBuffer normalsBuffer() {
        if (normals == null) {
            parseObj();
        }
        return BufferUtils.createFloatBuffer4f(normals);
    }

    @Override
    public IntBuffer indexesBuffer() {
        if (indexes == null) {
            parseObj();
        }
        return BufferUtils.createIntBuffer(indexes);
    }

    @Override
    public int[] getIndexes() {
        if (indexes == null) {
            parseObj();
        }
        return indexes;
    }

    @Override
    public int indexesCount() {
        if (indexes == null) {
            parseObj();
        }
        return indexes.length;
    }

    @Override
    public Texture modelTexture() {
        if (textures == null) {
            parseObj();
        }
        if (texture == null) {
            texture = PngTexture.createPngTexture(objModelParameters);
        }
        return texture;
    }

    @Override
    public FloatBuffer textureVertices() {
        if (textures == null) {
            parseObj();
        }
        return BufferUtils.createFloatBuffer4f(textures);
    }

    private static class Vertex {
        private float x;
        private float y;
        private float z;
    }

    private static class TextureVertex {
        private float x;
        private float y;
    }

    private static class VertexNormal {
        private float x;
        private float y;
        private float z;
    }

    private static class IndexTuple {
        private Integer vertexIndex;
        private Integer textureIndex;
        private Integer normalIndex;

        @Override
        public int hashCode() {
            return Objects.hash(vertexIndex, textureIndex, normalIndex);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof IndexTuple)) {
                return false;
            }
            var tuple = (IndexTuple) obj;
            return Objects.equals(vertexIndex, tuple.vertexIndex)
                    && Objects.equals(textureIndex, tuple.textureIndex)
                    && Objects.equals(normalIndex, tuple.normalIndex);
        }
    }
}
