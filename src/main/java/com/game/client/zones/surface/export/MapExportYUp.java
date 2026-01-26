package com.game.client.zones.surface.export;

import com.game.client.zones.Zone;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Export heightMap в obj формат
 * heightMap - поверхность сгенерированная с Perlin Noise
 */
public class MapExportYUp {

    private final int vertexCount;
    private final float[][][] heightMap;
    private final float[][] topEdge;
    private final float[][] rightEdge;
    private final float[][] bottomEdge;
    private final float[][] leftEdge;
    private final float zMulti = -1f;

    public MapExportYUp(Zone zone) {
        this.topEdge = zone.topEdge;
        this.rightEdge = zone.rightEdge;
        this.bottomEdge = zone.bottomEdge;
        this.leftEdge = zone.leftEdge;
        this.vertexCount = zone.vertexCount;
        this.heightMap = copyHeightMap(zone.heightMap);
    }

    private static float[][][] copyHeightMap(float[][][] heightMap) {
        var length = heightMap.length;
        var copy = new float[length][length][3];
        for (int i = 0; i < copy.length; i++) {
            var ar = heightMap[i];
            for (int j = 0; j < ar.length; j++) {
                copy[i][j][0] = heightMap[i][j][0];
                copy[i][j][1] = heightMap[i][j][1];
                copy[i][j][2] = heightMap[i][j][2];
            }
        }
        return copy;
    }

    public void exportToOBJ_YUp(String filename, float scaleXZ, float scaleY) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            exportToOBJ_YUp(writer, scaleXZ, scaleY);
        }
    }

    private void exportToOBJ_YUp(BufferedWriter writer, float scaleXZ, float scaleY) throws IOException {
        DecimalFormat df = new DecimalFormat("0.0000",
                DecimalFormatSymbols.getInstance(Locale.US));

        writer.write("# Generated 3D Surface from Perlin Noise (Y-Up)\n");
        writer.write("# Size: " + vertexCount + "x" + vertexCount + "\n");
        writer.write("# Boundary-aware generation\n");
        writer.write("# Coordinate system: X-Right, Y-Up, Z-Forward\n\n");

        // Записываем вершины (vertices) в Y-Up системе
        writer.write("# Vertices (Y-Up)\n");
        for (int z = 0; z < vertexCount; z++) {
            for (int x = 0; x < vertexCount; x++) {
                // Преобразуем координаты для Y-Up:
                // X (ось вправо) = x * scale
                // Y (ось вверх) = высота * scale
                // Z (ось вперед, на нас) = z * scale
                float vx = heightMap[x][z][0] * scaleXZ;
                float vy = heightMap[x][z][2] * scaleY; // Y - высота
                float vz = heightMap[x][z][1] * scaleXZ*zMulti;
                heightMap[x][z][0] = vx;
                heightMap[x][z][1] = vy;
                heightMap[x][z][2] = vz;

                writer.write("v " + df.format(vx) + " " +
                        df.format(vy) + " " +
                        df.format(vz) + "\n");
            }
        }

        edgesToYUp(scaleXZ, scaleY);

        // Записываем нормали (normals) - пересчитываем для Y-Up
        writer.write("\n# Vertex normals (Y-Up)\n");
        float[][][] normals = calculateNormalsYUp();
        for (int z = 0; z < vertexCount; z++) {
            for (int x = 0; x < vertexCount; x++) {
                writer.write("vn " + df.format(normals[x][z][0]) + " " +
                        df.format(normals[x][z][1]) + " " +
                        df.format(normals[x][z][2]) + "\n");
            }
        }

        // Записываем текстурные координаты
        writer.write("\n# Texture coordinates\n");
        for (int z = 0; z < vertexCount; z++) {
            for (int x = 0; x < vertexCount; x++) {
                float u = x / (float) (vertexCount - 1);
                float v = z / (float) (vertexCount - 1);
                writer.write("vt " + df.format(u) + " " + df.format(v) + "\n");
            }
        }

        // Записываем грани (faces) как треугольники
        // Важно: порядок вершин должен быть против часовой стрелки для правильных нормалей
        writer.write("\n# Faces (counter-clockwise winding for Y-Up)\n");
        writer.write("g SurfaceMesh\n");
        writer.write("s 1\n");

        // Генерируем треугольники для квадратной сетки
        for (int z = 0; z < vertexCount - 1; z++) {
            for (int x = 0; x < vertexCount - 1; x++) {
                // Индексы вершин для текущего квадрата (1-based в OBJ)
                int v1 = z * vertexCount + x + 1;
                int v2 = z * vertexCount + (x + 1) + 1;
                int v3 = (z + 1) * vertexCount + x + 1;
                int v4 = (z + 1) * vertexCount + (x + 1) + 1;

                // Текстурные координаты (совпадают с индексами вершин)
                int t1 = v1, t2 = v2, t3 = v3, t4 = v4;
                // Нормали (совпадают с индексами вершин)
                int n1 = v1, n2 = v2, n3 = v3, n4 = v4;

                // Два треугольника на квадрат
                // Для Y-Up: порядок против часовой стрелки, если смотреть снаружи
                // Треугольник 1: v1 - v3 - v2
                writer.write("f " + v1 + "/" + t1 + "/" + n1 + " " +
                        v3 + "/" + t3 + "/" + n3 + " " +
                        v2 + "/" + t2 + "/" + n2 + "\n");

                // Треугольник 2: v2 - v3 - v4
                writer.write("f " + v2 + "/" + t2 + "/" + n2 + " " +
                        v3 + "/" + t3 + "/" + n3 + " " +
                        v4 + "/" + t4 + "/" + n4 + "\n");
            }
        }

        writer.write("\n# End of file\n");
    }

    private void edgesToYUp(float scaleXZ, float scaleY) {
        for (int i = 0; i < leftEdge.length; i++) {
            edgeToYUp(leftEdge, i, scaleXZ, scaleY);
            edgeToYUp(rightEdge, i, scaleXZ, scaleY);
            edgeToYUp(bottomEdge, i, scaleXZ, scaleY);
            edgeToYUp(topEdge, i, scaleXZ, scaleY);
        }
    }

    private void edgeToYUp(float[][] edge, int i, float scaleXZ, float scaleY) {
        var x = edge[i][0] * scaleXZ;
        var z = edge[i][1] * scaleXZ * zMulti;
        var y = edge[i][2] * scaleY;
        edge[i] = new float[]{x, y, z};
    }

    // Вычисление нормалей для Y-Up системы
    private float[][][] calculateNormalsYUp() {
        float[][][] normals = new float[vertexCount][vertexCount][3];

        // Вычисляем нормали для каждой вершины
        for (int z = 0; z < vertexCount; z++) {
            for (int x = 0; x < vertexCount; x++) {
                float[] normal = new float[3];

                // Получаем соседние вершины
                float[] left = (x > 0) ? heightMap[x - 1][z] : leftEdge[z];
                float[] right = (x < vertexCount - 1) ? heightMap[x + 1][z] : rightEdge[z];
                float[] bottom = (z > 0) ? heightMap[x][z - 1] : bottomEdge[x];
                float[] top = (z < vertexCount - 1) ? heightMap[x][z + 1] : topEdge[x];

                // Векторы к соседям
                float[] dx = {right[0] - left[0], right[1] - left[1], right[2] - left[2]};
                float[] dz = {top[0] - bottom[0], top[1] - bottom[1], top[2] - bottom[2]};

                // Векторное произведение dz × dx для получения нормали (Y-Up)
                normal[0] = dz[1] * dx[2] - dz[2] * dx[1];
                normal[1] = dz[2] * dx[0] - dz[0] * dx[2];
                normal[2] = dz[0] * dx[1] - dz[1] * dx[0];

                // Нормализуем
                float length = (float) Math.sqrt(normal[0] * normal[0] + normal[1] * normal[1] + normal[2] * normal[2]);
                if (length > 0.00001f) {
                    normal[0] /= length;
                    normal[1] /= length;
                    normal[2] /= length;
                }

                // Если нормаль направлена вниз, инвертируем ее
                if (normal[1] < 0) {
                    normal[0] = -normal[0];
                    normal[1] = -normal[1];
                    normal[2] = -normal[2];
                }

                normals[x][z] = normal;
            }
        }

        return normals;
    }
}
