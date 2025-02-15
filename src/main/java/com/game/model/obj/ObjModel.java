package com.game.model.obj;

import com.game.lwjgl.texture.TextureProperties;
import com.game.model.Light;
import com.game.model.texture.ObjTexture;
import com.game.model.texture.Texture;
import com.game.utils.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents Blender .obj file
 * Model should have Triangulate (Beauty-Beauty) modifier
 * When model is exported check-boxes Apply-Modifier and Triangulate-Mesh should be checked
 */
public class ObjModel implements Model {
    private static final String LF = "\n";
    private static final String WHITE_SPACE = " ";
    private static final String SLASH = "/";
    private final String objectSource;
    private final Light light;
    private final TextureProperties textureProperties;
    private float[] vertices;
    private float[] normals;
    private float[] textures;
    private int[] indexes;
    private Texture texture;

    private ObjModel(ObjModelProperties properties) {
        this.objectSource = properties.getObjectSource();
        this.textureProperties = properties.getTextureProperties();
        this.light = properties.getLight();
        parseObj();
        modelTexture();
    }

    public static ObjModel createObjectModel(ObjModelProperties properties) {
        return new ObjModel(properties);
    }

    private void parseObj() {
        var verticesMap = new HashMap<Integer, Vertex>();
        var normalsMap = new HashMap<Integer, VertexNormal>();
        var textureMap = new HashMap<Integer, TextureVertex>();

        var indexTupleList = new ArrayList<IndexTuple>();
        int verticesIndex = 1;
        int vertexNormalsIndex = 1;
        int textureVerticesIndex = 1;

        var lines = objectSource.split(LF);
        for (var line : lines) {
            var elements = line.split(WHITE_SPACE);
            var elementIndex = 0;
            var type = elements[elementIndex++];
            switch (type) {
                // Vertices
                case "v":
                    var vertex = new Vertex();
                    vertex.x = elements[elementIndex++];
                    vertex.y = elements[elementIndex++];
                    vertex.z = elements[elementIndex];
                    verticesMap.put(verticesIndex++, vertex);
                    break;
                // Texture vertices
                case "vt":
                    var textureVertex = new TextureVertex();
                    textureVertex.x = elements[elementIndex++];
                    textureVertex.y = elements[elementIndex];
                    textureMap.put(textureVerticesIndex++, textureVertex);
                    break;
                // Normals
                case "vn":
                    var vertexNormal = new VertexNormal();
                    vertexNormal.x = elements[elementIndex++];
                    vertexNormal.y = elements[elementIndex++];
                    vertexNormal.z = elements[elementIndex];
                    normalsMap.put(vertexNormalsIndex++, vertexNormal);
                    break;
                // Faces
                case "f":
                    var vertexIndexList = extractIndexes(elements, 0);
                    var textureIndexList = extractIndexes(elements, 1);
                    var normalIndexList = extractIndexes(elements, 2);

                    for (int i = 0; i < 3; i++) {
                        var indexPair = new IndexTuple();
                        indexPair.vertexIndex = vertexIndexList.get(i);
                        indexPair.normalIndex = normalIndexList.get(i);
                        indexPair.textureIndex = textureIndexList.get(i);
                        indexTupleList.add(indexPair);
                    }
                    break;
                default:
                    break;
            }
        }

        int uniqueCount = new HashSet<>(indexTupleList).size();
        vertices = new float[uniqueCount * 3];
        normals = new float[uniqueCount * 3];
        textures = new float[uniqueCount * 2];
        indexes = new int[indexTupleList.size()];

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
    }

    /**
     * @param elements : f 21/34/1 10/17/2 2/4/3
     * @param j        0 or 1 or 2
     * @return if 0 - 21 10 2, if 1 - 34 17 4, if 2 - 1 2 3
     */
    private List<Integer> extractIndexes(String[] elements, int j) {
        var indexes = new ArrayList<Integer>();
        for (int i = 1; i <= 3; i++) {
            indexes.add(extractIndexes(elements[i]).get(j));
        }
        return indexes;
    }

    private List<Integer> extractIndexes(String element) {
        return Arrays.stream(element.split(SLASH)).map(Integer::valueOf).collect(Collectors.toList());
    }

    private void addVertex(float[] arr, Vertex v, int index) {
        arr[index++] = Float.parseFloat(v.x);
        arr[index++] = Float.parseFloat(v.y);
        arr[index] = Float.parseFloat(v.z);
    }

    private void addNormal(float[] arr, VertexNormal vn, int index) {
        arr[index++] = Float.parseFloat(vn.x);
        arr[index++] = Float.parseFloat(vn.y);
        arr[index] = Float.parseFloat(vn.z);
    }

    private void addTexture(float[] arr, TextureVertex textureVertex, int index) {
        arr[index++] = Float.parseFloat(textureVertex.x);
        arr[index] = Float.parseFloat(textureVertex.y);
    }

    @Override
    public FloatBuffer vertices() {
        if (vertices == null) {
            parseObj();
        }
        return BufferUtils.createFloatBuffer4f(vertices);
    }

    @Override
    public FloatBuffer normals() {
        if (normals == null) {
            parseObj();
        }
        return BufferUtils.createFloatBuffer4f(normals);
    }

    @Override
    public IntBuffer indexes() {
        if (indexes == null) {
            parseObj();
        }
        return BufferUtils.createIntBuffer(indexes);
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
            texture = ObjTexture.createObjTexture(textureProperties.setTextureVertices(vertices));
        }
        return texture;
    }

    @Override
    public Light getLight() {
        return light;
    }

    private static class Vertex {
        private String x;
        private String y;
        private String z;
    }

    private static class TextureVertex {
        private String x;
        private String y;
    }

    private static class VertexNormal {
        private String x;
        private String y;
        private String z;
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
