package com.game.client.zones;

import com.game.client.zones.surface.export.MapConfig;
import com.game.client.zones.surface.export.MapConfigExporter;
import com.game.client.zones.surface.export.ObjExportYUp;
import com.game.client.zones.surface.perlinnoise.PerlinNoiseSurface3D;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

public class ZoneGenerator {

    public final MapConfig mapConfig = new MapConfig();
    private final Path resourcesPath = Path.of("src/main/resources");
    private final Path outputPath = resourcesPath.resolve("obj/units/zone");
    private final Path texturePath = resourcesPath.resolve("obj/units/ground/texture.png");
    private float[][][] heightMap;

    public static void main(String[] args) {
        try {
            new ZoneGenerator().generate();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static float[][][] createHeightMap(MapConfig mapConfig) {
        int xMapSize = mapConfig.mapColumnCount * (mapConfig.zoneSize) + 1;
        int yMapSize = mapConfig.mapRowCount * (mapConfig.zoneSize) + 1;
        var surface = new PerlinNoiseSurface3D(xMapSize, yMapSize);
        surface.generateSurface();
        return surface.getHeightMap();
    }

    private static float[] copy(float[] source) {
        var copy = new float[source.length];
        System.arraycopy(source, 0, copy, 0, source.length);
        return copy;
    }

    private static String zoneId(Zone zone) {
        return zoneId(zone.z, zone.x);
    }

    private static String zoneId(Integer z, Integer x) {
        return z + "_" + x;
    }

    public void generate() throws IOException {
        var path = outputPath;
        Files.createDirectories(path);

        heightMap = createHeightMap(mapConfig);
        var zones = splitByZones(heightMap);

        zones.forEach((id, zone) -> {
            fillEdgesFromNeighbours(zone, zones);
        });
        log(zones);

        zones.forEach((zoneId, zone) -> {
            saveZone(path, zone);
            System.out.println("Saved zone " + zoneId(zone));
        });
        new MapConfigExporter().export(mapConfig, path);
        System.out.println(ZoneGenerator.class.getSimpleName() + " generated " + zones.size() + " zones");
    }

    private void log(HashMap<String, Zone> zones) {
        zones.forEach((id, zone) -> {
            System.out.println("\nZone: " + zoneId(zone));
            var sb = new StringBuilder();
            sb.append("left border:\n");
            for (int i = 0; i < zone.vertexCount; i++) {
                float[] v = zone.heightMap[0][i];
                append(sb, v);
            }
            sb.append("\nleft edge:\n");
            for (int i = 0; i < zone.leftEdge.length; i++) {
                append(sb, zone.leftEdge[i]);
            }
            // right
            sb.append("\nright border:\n");
            for (int i = 0; i < zone.vertexCount; i++) {
                float[] v = zone.heightMap[zone.vertexCount-1][i];
                append(sb, v);
            }
            sb.append("\nright edge:\n");
            for (int i = 0; i < zone.rightEdge.length; i++) {
                append(sb, zone.rightEdge[i]);
            }
            System.out.println(sb);
        });
    }

    private static void append(StringBuilder sb, float[] v) {
        sb.append(v[0]).append(",").append(v[1]).append(",").append(v[2]).append("; ");
    }

    private HashMap<String, Zone> splitByZones(float[][][] heightMap) {
        var zones = new HashMap<String, Zone>();
        for (int mapRow = 0; mapRow < mapConfig.mapRowCount; mapRow++) {
            for (int mapColumn = 0; mapColumn < mapConfig.mapColumnCount; mapColumn++) {
                int vertexCount = mapConfig.vertexCount;
                int zoneSize = mapConfig.zoneSize;
                var zone = new Zone(vertexCount, mapRow, mapColumn);
                zones.put(zoneId(zone), zone);
                var zoneHeightMap = new float[vertexCount][vertexCount][3];
                for (int x = 0; x < vertexCount; x++) {
                    for (int y = 0; y < vertexCount; y++) {
                        var vertex = heightMap[mapColumn * zoneSize + x][mapRow * zoneSize + y];
                        float[] zoneVertex = zoneHeightMap[x][y];
                        zoneVertex[0] = vertex[0];
                        zoneVertex[1] = vertex[1];
                        zoneVertex[2] = vertex[2];
                    }
                }
                zone.heightMap = zoneHeightMap;
                System.out.println("Created zone " + zoneId(zone));
            }
        }
        return zones;
    }

    private void fillEdgesFromNeighbours(Zone zone, HashMap<String, Zone> zones) {
        var z = zone.z;
        var x = zone.x;
        var leftZone = zones.get(zoneId(z, x - 1));
        var rightZone = zones.get(zoneId(z, x + 1));
        var topZone = zones.get(zoneId(z + 1, x));
        var bottomZone = zones.get(zoneId(z - 1, x));

        int vertexCount = mapConfig.vertexCount;
        zone.leftEdge = new float[vertexCount][3];
        if (leftZone != null) {
            for (int i = 0; i < vertexCount; i++) {
                zone.leftEdge[i] = copy(leftZone.heightMap[vertexCount - 2][i]);
            }
        } else {
            for (int i = 0; i < vertexCount; i++) {
                zone.leftEdge[i] = copy(zone.heightMap[0][i]);
            }
        }

        zone.rightEdge = new float[vertexCount][3];
        if (rightZone != null) {
            for (int i = 0; i < vertexCount; i++) {
                zone.rightEdge[i] = copy(rightZone.heightMap[1][i]);
            }
        } else {
            for (int i = 0; i < vertexCount; i++) {
                zone.rightEdge[i] = copy(zone.heightMap[vertexCount - 1][i]);
            }
        }

        zone.topEdge = new float[vertexCount][3];
        if (topZone != null) {
            for (int i = 0; i < vertexCount; i++) {
                zone.topEdge[i] = copy(topZone.heightMap[i][1]);
            }
        } else {
            for (int i = 0; i < vertexCount; i++) {
                zone.topEdge[i] = copy(zone.heightMap[i][vertexCount - 1]);
            }
        }

        zone.bottomEdge = new float[vertexCount][3];
        if (bottomZone != null) {
            for (int i = 0; i < vertexCount; i++) {
                zone.bottomEdge[i] = copy(bottomZone.heightMap[i][vertexCount-2]);
            }
        } else {
            for (int i = 0; i < vertexCount; i++) {
                zone.bottomEdge[i] = copy(zone.heightMap[i][0]);
            }
        }
    }

    private void saveZone(Path path, Zone zone) {
        var z = zone.z;
        var x = zone.x;
        var zoneId = zoneId(z, x);
        var currentZonePath = path.resolve(zoneId);
        try {
            Files.createDirectories(currentZonePath);
            Files.copy(
                    texturePath,
                    currentZonePath.resolve("texture.png"),
                    StandardCopyOption.REPLACE_EXISTING
            );

            var fileName = currentZonePath.resolve("model.obj").toString();
            var exporter = new ObjExportYUp(zone);

            exporter.exportToOBJ_YUp(
                    fileName,
                    mapConfig.xzScale,
                    mapConfig.yScale
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
